package com.easycodebox.common.mail;

import com.easycodebox.common.lang.Strings;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 增加了异步发送邮件、模板解析功能
 * @author WangXiaoJin
 */
public class SimpleMailSender extends JavaMailSenderImpl {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 邮件发送人全字符正则
     * <p>多个邮件地址可以用逗号隔开，所以正则中取出了逗号
     * <p>邮件地址格式：
     * <ul>
     *     <ul>王 &lt;3xxxxx@qq.com&gt;</ul>
     *     <ul>王 &lt;3xxxxx@qq.com&gt;, 李 &lt;111@qq.com&gt;</ul>
     *     <ul>3xxxxx@qq.com</ul>
     *     <ul>3xxxxx@qq.com, 111@qq.com</ul>
     * </ul>
     */
    private final Pattern fullNamePattern = Pattern.compile("([^<>,]+)<([^<>,]+)>");

    /**
     * 模板映射配置的分隔符
     */
    private String templateMappingSeparatorPattern = "[,\n]";

    /**
     * 模板和模板处理器的映射。key：模板，value：处理器名
     */
    private ConcurrentHashMap<String, String> templateMap = new ConcurrentHashMap<>(4);

    /**
     * key：处理器别名，value：处理器
     */
    private ConcurrentHashMap<String, TemplateProcessor> processorMap;

    /**
     * 默认模板处理器
     */
    private TemplateProcessor defaultTemplateProcessor;

    /**
     * 发件人 - 如果只用Session Property的mail.from属性，发送邮件时发件人为空，所以独立出此属性
     */
    private String fromAddress;

    /**
     * 发送邮件时显示的人名
     */
    private String fromPersonal;

    /**
     * executor用于异步执行操作，默认使用{@link SimpleAsyncTaskExecutor}，阈值设置为50
     * <p> 为了使性能最优，请使用{@link ThreadPoolTaskExecutor}线程池
     */
    private AsyncListenableTaskExecutor executor;

    /**
     * 异步操作执行成功后的回调函数
     */
    private ListenableFutureCallback<MimeMessage[]> callback;

    public SimpleMailSender() {
        super();
    }

    @PostConstruct
    public void init() throws Exception {
        if (executor == null) {
            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
            executor.setConcurrencyLimit(50);
            this.executor = executor;
        }
        if (callback == null) {
            callback = new ListenableFutureCallback<MimeMessage[]>() {
                @Override
                public void onFailure(Throwable ex) {
                    log.error("Send mail error.", ex);
                }

                @Override
                public void onSuccess(MimeMessage[] result) {
                    if (result == null || result.length == 0) return;
                    try {
                        List<String> to = new ArrayList<>();
                        for (MimeMessage msg : result) {
                            for (Address address : msg.getRecipients(Message.RecipientType.TO)) {
                                to.add(address.toString());
                            }
                        }
                        log.info("Send mail to {} suc.", ArrayUtils.toString(to));
                    } catch (MessagingException e) {
                        log.error("Get mail TO address error.", e);
                    }
                }
            };
        }
        if (defaultTemplateProcessor == null) {
            defaultTemplateProcessor = new StringTemplateProcessor();
        }
        if (processorMap == null) {
            processorMap = new ConcurrentHashMap<>(4);
        }
    }

    /**
     * 发送简单的文本邮件
     * @param subject   邮件主题
     * @param text  邮件内容
     * @param to    收件人
     * @throws MailException
     */
    public void send(String subject, String text, String... to) throws MailException {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        send(msg);
    }

    /**
     * 发送模板邮件 - 邮件内容为文本格式
     * @param subject   邮件主题
     * @param template  邮件内容
     * @param model 模板占位符替换值，可用值类型根据具体模板处理器而定
     * @param to    收件人
     * @throws MailException
     * @throws ParseTemplateException
     */
    public void sendTmpl(String subject, String template, Object model, String... to) throws MailException, ParseTemplateException {
        String text = processTemplate(template, model);
        send(subject, text, to);
    }

    /**
     * <b>异步</b>发送简单的文本邮件
     * @param subject   邮件主题
     * @param text  邮件内容
     * @param to    收件人
     */
    public void sendAsync(String subject, String text, String... to) {
        asyncLogic(subject, text, null, null, false, false, to);
    }

    /**
     * <b>异步</b>发送简单的文本邮件
     * @param coupleMailProcessor
     * @param to    收件人
     */
    public void sendAsync(CoupleMailProcessor coupleMailProcessor, String... to) {
        asyncLogic(null, null, coupleMailProcessor, null, false, false, to);
    }

    /**
     * <b>异步</b>发送模板邮件 - 邮件内容为文本格式
     * @param subject   邮件主题
     * @param template  邮件模板
     * @param model 模板占位符替换值，可用值类型根据具体模板处理器而定
     * @param to    收件人
     */
    public void sendTmplAsync(String subject, String template, Object model, String... to) {
        asyncLogic(subject, template, null, model, true, false, to);
    }

    /**
     * <b>异步</b>发送模板邮件 - 邮件内容为文本格式
     * @param coupleMailProcessor
     * @param model 模板占位符替换值，可用值类型根据具体模板处理器而定
     * @param to    收件人
     */
    public void sendTmplAsync(CoupleMailProcessor coupleMailProcessor, Object model, String... to) {
        asyncLogic(null, null, coupleMailProcessor, model, true, false, to);
    }

    /**
     * 发送HTML格式的邮件
     * @param subject   邮件主题
     * @param html  邮件内容
     * @param to    收件人
     * @throws MailException
     * @throws MessagingException
     */
    public void sendHtml(String subject, String html, String... to) throws MailException, MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(this.createMimeMessage());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        send(helper.getMimeMessage());
    }

    /**
     * 发送模板邮件 - 邮件内容为HTML格式
     * @param subject   邮件主题
     * @param template  邮件内容
     * @param model 模板占位符替换值，可用值类型根据具体模板处理器而定
     * @param to    收件人
     * @throws MailException
     * @throws ParseTemplateException
     * @throws MessagingException
     */
    public void sendHtmlTmpl(String subject, String template, Object model, String... to)
            throws MailException, ParseTemplateException, MessagingException {
        String html = processTemplate(template, model);
        sendHtml(subject, html, to);
    }

    /**
     * <b>异步</b>发送HTML格式的邮件
     * @param subject   邮件主题
     * @param html  邮件内容
     * @param to    收件人
     */
    public void sendHtmlAsync(String subject, String html, String... to) {
        asyncLogic(subject, html, null, null, false, true, to);
    }

    /**
     * <b>异步</b>发送HTML格式的邮件
     * @param coupleMailProcessor
     * @param to    收件人
     */
    public void sendHtmlAsync(CoupleMailProcessor coupleMailProcessor, String... to) {
        asyncLogic(null, null, coupleMailProcessor, null, false, true, to);
    }

    /**
     * <b>异步</b>发送模板邮件 - 邮件内容为HTML格式
     * @param subject   邮件主题
     * @param template  邮件模板
     * @param model 模板占位符替换值，可用值类型根据具体模板处理器而定
     * @param to    收件人
     */
    public void sendHtmlTmplAsync(String subject, String template, Object model, String... to) {
        asyncLogic(subject, template, null, model, true, true, to);
    }

    /**
     * <b>异步</b>发送模板邮件 - 邮件内容为HTML格式
     * @param coupleMailProcessor
     * @param model 模板占位符替换值，可用值类型根据具体模板处理器而定
     * @param to    收件人
     */
    public void sendHtmlTmplAsync(CoupleMailProcessor coupleMailProcessor, Object model, String... to) {
        asyncLogic(null, null, coupleMailProcessor, model, true, true, to);
    }

    /**
     * 异步发送邮件
     * @param mimeMessages
     */
    public void sendAsync(final MimeMessage... mimeMessages) {
        executor.submitListenable(new Callable<MimeMessage[]>() {
            @Override
            public MimeMessage[] call() throws Exception {
                doSend(mimeMessages, null);
                return mimeMessages;
            }
        }).addCallback(callback);
    }

    /**
     * 检查mimeMessages中的from属性有没有值，没有则设置为 {@link #fromAddress} 值
     * @param mimeMessages
     * @param originalMessages
     * @throws MailException
     */
    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        for (MimeMessage msg : mimeMessages) {
            try {
                Address[] froms = msg.getFrom();
                if (froms == null || froms.length == 0) {
                    msg.setFrom(createFromAddress(null, null));
                }
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Get mail from address error.", e);
            }
        }
        super.doSend(mimeMessages, originalMessages);
    }

    protected void asyncLogic(final String subject, final String text, final CoupleMailProcessor coupleMailProcessor,
                              final Object model, final boolean isTemplate, final boolean isHtml, final String... to) {
        executor.submitListenable(new Callable<MimeMessage[]>() {
            @Override
            public MimeMessage[] call() throws Exception {
                String sbj = subject, content = text;
                if (coupleMailProcessor != null) {
                    CoupleMail coupleMail = coupleMailProcessor.process();
                    sbj = coupleMail.getSubject();
                    content = coupleMail.getContent();
                }
                MimeMessageHelper helper = new MimeMessageHelper(SimpleMailSender.this.createMimeMessage());
                helper.setTo(to);
                helper.setSubject(sbj);
                helper.setText(isTemplate ? processTemplate(content, model) : content, isHtml);
                send(helper.getMimeMessage());
                return new MimeMessage[] {helper.getMimeMessage()};
            }
        }).addCallback(callback);
    }

    /**
     * 处理模板内容
     * @param template
     * @param model
     * @return
     * @throws ParseTemplateException
     * @throws IllegalArgumentException
     */
    protected String processTemplate(String template, Object model) throws ParseTemplateException, IllegalArgumentException {
        if (Strings.isBlank(template)) return template;
        if (templateMap.containsKey(template)) {
            String pros = templateMap.get(template);
            TemplateProcessor processor = processorMap.get(pros);
            if (processor == null) {
                throw new IllegalArgumentException("There is no processor corresponding to " + pros);
            }
            return processor.process(template, model);
        } else {
            //没有配置对应的模板处理器，则使用默认的模板处理器
            return defaultTemplateProcessor.process(template, model);
        }
    }

    /**
     * 创建发送人邮件地址
     * @param addr  邮件地址
     * @param personal  人名
     * @return
     */
    protected Address createFromAddress(String addr, String personal) throws UnsupportedEncodingException {
        return new InternetAddress(addr == null ? getFromAddress() : addr, personal == null ? getFromPersonal() : personal, getDefaultEncoding());
    }

    /**
     * 验证邮件发送人字符窜中是否设置了昵称，即使用了如下格式：
     * <pre>
     *      隔壁老王 &lt;3xxxxx@qq.com&gt;
     * </pre>
     *
     * @param address
     * @return
     */
    protected boolean hasPersonal(String address) {
        return Strings.isNotBlank(address) && fullNamePattern.matcher(address.trim()).matches();
    }

    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * fromAddress不支持多个邮件地址，如果传了多个邮件地址，则默认使用第一个。格式支持：
     * <ul>
     *     <li>3xxxxx@qq.com</li>
     *     <li>隔壁老王 &lt;3xxxxx@qq.com&gt;</li>
     * </ul>
     * @param fromAddress
     */
    public void setFromAddress(String fromAddress) {
        if (Strings.isBlank(fromAddress)) return;
        Matcher matcher = fullNamePattern.matcher(fromAddress.trim());
        if (matcher.find()) {
            this.fromAddress = matcher.group(2).trim();
            this.fromPersonal = matcher.group(1).trim();
        } else {
            this.fromAddress = fromAddress.trim();
        }
    }

    public String getFromPersonal() {
        return fromPersonal;
    }

    public void setFromPersonal(String fromPersonal) {
        this.fromPersonal = fromPersonal;
    }

    public AsyncListenableTaskExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(AsyncListenableTaskExecutor executor) {
        this.executor = executor;
    }

    public ListenableFutureCallback<MimeMessage[]> getCallback() {
        return callback;
    }

    public void setCallback(ListenableFutureCallback<MimeMessage[]> callback) {
        this.callback = callback;
    }

    public TemplateProcessor getDefaultTemplateProcessor() {
        return defaultTemplateProcessor;
    }

    public void setDefaultTemplateProcessor(TemplateProcessor defaultTemplateProcessor) {
        this.defaultTemplateProcessor = defaultTemplateProcessor;
    }

    /**
     * 配置模板和处理器的映射，字符窜格式：
     * <pre>
     *     /templates/order.ftl=freemarker
     *     /templates/cards.vm=velocity
     * </pre>
     * 注：等号后面的freemarker、velocity是通过{@link #processorMap}来决定的
     * @param mapping
     */
    public void setTemplateProcessorMapping(String mapping) {
        if (Strings.isBlank(mapping)) return;
        String[] frags = mapping.split(templateMappingSeparatorPattern);
        for (String frag : frags) {
            if (Strings.isBlank(frag)) continue;
            String[] keyVal = frag.trim().split("\\s*=\\s*");
            if (keyVal.length == 2 && Strings.isNotEmpty(keyVal[0])) {
                templateMap.put(keyVal[0], keyVal[1]);
            }
        }
    }

    public ConcurrentHashMap<String, String> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(Map<String, String> templateMap) {
        if (templateMap instanceof ConcurrentHashMap) {
            this.templateMap = (ConcurrentHashMap<String, String>) templateMap;
        } else if (templateMap != null){
            this.templateMap = new ConcurrentHashMap<>(templateMap);
        }
    }

    public ConcurrentHashMap<String, TemplateProcessor> getProcessorMap() {
        return processorMap;
    }

    public void setProcessorMap(Map<String, TemplateProcessor> processorMap) {
        if (processorMap instanceof ConcurrentHashMap) {
            this.processorMap = (ConcurrentHashMap<String, TemplateProcessor>) processorMap;
        } else if (processorMap != null){
            this.processorMap = new ConcurrentHashMap<>(processorMap);
        }
    }

}
