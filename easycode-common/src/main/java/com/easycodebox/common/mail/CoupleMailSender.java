package com.easycodebox.common.mail;

import com.easycodebox.common.validate.Assert;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.mail.MailException;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 主题、内容作为组合参数
 * @author WangXiaoJin
 */
public class CoupleMailSender {

    private SimpleMailSender simpleMailSender;

    private Map<String, CoupleMail> coupleMailMap;

    private CoupleMailGenerator<Map<String, CoupleMail>> coupleMailGenerator;

    /**
     * 单位：秒。{@link #coupleMailMap}数据的缓存时间，超过指定时间则重新使用{@link #coupleMailGenerator}生成新的数据。
     * <ul>
     *     <li>expire < 0 : 没有过期限制</li>
     *     <li>expire = 0 : 每次使用数据之前都会用{@link #coupleMailGenerator}重新生成数据</li>
     *     <li>expire > 0 : 超过指定时间则{@link #coupleMailMap}数据无效，需要重新生成</li>
     * </ul>
     */
    private int expire = NumberUtils.INTEGER_MINUS_ONE;

    /**
     * {@link #coupleMailMap}生命周期起始时间
     */
    private volatile long lifecycleStartTime;

    private final ReentrantLock lock = new ReentrantLock();

    public CoupleMailSender(SimpleMailSender simpleMailSender) {
        Assert.notNull(simpleMailSender);
        this.simpleMailSender = simpleMailSender;
    }

    @PostConstruct
    public void init() throws Exception {
        if (coupleMailGenerator != null) {
            validateCoupleMailMap();
        }
        if (coupleMailMap == null) {
            coupleMailMap = new HashMap<>();
        }
    }

    private void validateCoupleMailMap() throws GenerateCoupleMailException {
        if (lifecycleStartTime == 0 || expire == NumberUtils.INTEGER_ZERO ||
                expire > 0 && TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lifecycleStartTime) >= expire) {
            lock.lock();
            try {
                if (lifecycleStartTime == 0 || expire == NumberUtils.INTEGER_ZERO ||
                        expire > 0 && TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lifecycleStartTime) >= expire) {
                    if (coupleMailGenerator.isModified()) {
                        coupleMailMap = coupleMailGenerator.generate();
                    }
                    lifecycleStartTime = System.nanoTime();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 获取coupleKey参数对应的CoupleMail
     * @param coupleKey
     * @return CoupleMail
     * @throws IllegalArgumentException 当找不到coupleKey参数对应的CoupleMail则抛此异常
     */
    private CoupleMail obtainCoupleMail(String coupleKey) throws IllegalArgumentException, GenerateCoupleMailException {
        validateCoupleMailMap();
        String errorMsg = "coupleMailMap do not have the key : ({})";
        Assert.notBlank(coupleKey, errorMsg, coupleKey);
        CoupleMail mail = coupleMailMap.get(coupleKey);
        Assert.notNull(mail, errorMsg, coupleKey);
        return mail;
    }

    /**
     * 处理邮件主题模板
     * @param subject
     * @param model
     * @return
     * @throws ParseTemplateException
     */
    private String processSubjectTmpl(String subject, Object model) throws ParseTemplateException {
        return simpleMailSender.getDefaultTemplateProcessor().process(subject, model);
    }

    /**
     * 发送简单的文本邮件
     * @param coupleKey
     * @param to
     * @throws IllegalArgumentException
     * @throws MailException
     * @throws GenerateCoupleMailException
     */
    public void send(String coupleKey, String... to)
            throws IllegalArgumentException, MailException, GenerateCoupleMailException {
        CoupleMail mail = obtainCoupleMail(coupleKey);
        simpleMailSender.send(mail.getSubject(), mail.getContent(), to);
    }

    /**
     * 发送模板邮件 - 邮件内容为文本格式
     * @param coupleKey
     * @param subjectModel 模板占位符替换值，使用{@link SimpleMailSender#getDefaultTemplateProcessor()}处理主题模板
     * @param contentModel 模板占位符替换值
     * @param to    收件人
     * @throws IllegalArgumentException
     * @throws MailException
     * @throws ParseTemplateException
     * @throws GenerateCoupleMailException
     */
    public void sendTmpl(String coupleKey, Object subjectModel, Object contentModel, String... to)
            throws IllegalArgumentException, MailException, ParseTemplateException, GenerateCoupleMailException {
        CoupleMail mail = obtainCoupleMail(coupleKey);
        String subject = processSubjectTmpl(mail.getSubject(), subjectModel);
        simpleMailSender.sendTmpl(subject, mail.getContent(), contentModel, to);
    }

    /**
     * <b>异步</b>发送简单的文本邮件
     * @param coupleKey
     * @param to    收件人
     */
    public void sendAsync(final String coupleKey, String... to) {
        simpleMailSender.sendAsync(new CoupleMailProcessor() {
            @Override
            public CoupleMail process() throws Exception {
                return obtainCoupleMail(coupleKey);
            }
        }, to);
    }

    /**
     * <b>异步</b>发送模板邮件 - 邮件内容为文本格式
     * @param coupleKey
     * @param subjectModel 邮件标题 - 模板占位符替换值，使用{@link SimpleMailSender#getDefaultTemplateProcessor()}
     * @param contentModel 邮件内容 - 模板占位符替换值
     * @param to    收件人
     */
    public void sendTmplAsync(final String coupleKey, final Object subjectModel, Object contentModel, String... to) {
        simpleMailSender.sendTmplAsync(new CoupleMailProcessor() {
            @Override
            public CoupleMail process() throws Exception {
                CoupleMail mail = obtainCoupleMail(coupleKey);
                String subject = processSubjectTmpl(mail.getSubject(), subjectModel);
                return new CoupleMail(subject, mail.getContent());
            }
        }, contentModel, to);
    }

    /**
     * 发送HTML格式的邮件
     * @param coupleKey
     * @param to    收件人
     * @throws IllegalArgumentException
     * @throws MailException
     * @throws MessagingException
     * @throws GenerateCoupleMailException
     */
    public void sendHtml(String coupleKey, String... to)
            throws IllegalArgumentException, MailException, MessagingException, GenerateCoupleMailException {
        CoupleMail mail = obtainCoupleMail(coupleKey);
        simpleMailSender.sendHtml(mail.getSubject(), mail.getContent(), to);
    }

    /**
     * 发送模板邮件 - 邮件内容为HTML格式
     * @param coupleKey
     * @param subjectModel 模板占位符替换值，使用{@link SimpleMailSender#getDefaultTemplateProcessor()}处理主题模板
     * @param contentModel 模板占位符替换值
     * @param to    收件人
     * @throws IllegalArgumentException
     * @throws MailException
     * @throws ParseTemplateException
     * @throws MessagingException
     * @throws GenerateCoupleMailException
     */
    public void sendHtmlTmpl(String coupleKey, Object subjectModel, Object contentModel, String... to)
            throws IllegalArgumentException, MailException, ParseTemplateException, MessagingException, GenerateCoupleMailException {
        CoupleMail mail = obtainCoupleMail(coupleKey);
        String subject = processSubjectTmpl(mail.getSubject(), subjectModel);
        simpleMailSender.sendHtmlTmpl(subject, mail.getContent(), contentModel, to);
    }

    /**
     * <b>异步</b>发送HTML格式的邮件
     * @param coupleKey
     * @param to    收件人
     */
    public void sendHtmlAsync(final String coupleKey, final String... to) {
        simpleMailSender.sendHtmlAsync(new CoupleMailProcessor() {
            @Override
            public CoupleMail process() throws Exception {
                return obtainCoupleMail(coupleKey);
            }
        }, to);
    }

    /**
     * <b>异步</b>发送模板邮件 - 邮件内容为HTML格式
     * @param coupleKey
     * @param subjectModel 模板占位符替换值，使用{@link SimpleMailSender#getDefaultTemplateProcessor()}处理主题模板
     * @param contentModel 模板占位符替换值
     * @param to    收件人
     */
    public void sendHtmlTmplAsync(final String coupleKey, final Object subjectModel, Object contentModel, String... to) {
        simpleMailSender.sendHtmlTmplAsync(new CoupleMailProcessor() {
            @Override
            public CoupleMail process() throws Exception {
                CoupleMail mail = obtainCoupleMail(coupleKey);
                String subject = processSubjectTmpl(mail.getSubject(), subjectModel);
                return new CoupleMail(subject, mail.getContent());
            }
        }, contentModel, to);
    }

    public CoupleMailGenerator<Map<String, CoupleMail>> getCoupleMailGenerator() {
        return coupleMailGenerator;
    }

    public void setCoupleMailGenerator(CoupleMailGenerator<Map<String, CoupleMail>> coupleMailGenerator) {
        this.coupleMailGenerator = coupleMailGenerator;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public SimpleMailSender getSimpleMailSender() {
        return simpleMailSender;
    }

    public void setSimpleMailSender(SimpleMailSender simpleMailSender) {
        this.simpleMailSender = simpleMailSender;
    }

}
