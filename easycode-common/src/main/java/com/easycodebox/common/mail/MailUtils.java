package com.easycodebox.common.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

public class MailUtils {

	private static final Logger log = LoggerFactory.getLogger(MailUtils.class);

	public static MailInfo defaultMailInfo() {
		MailInfo info = new MailInfo();
		info.setMailServerHost(BaseConstants.mailHost);
		info.setMailServerPort("25");
		info.setValidate(true);
		info.setUsername(BaseConstants.mailUsername);
		info.setPassword(BaseConstants.mailPassword);
		info.setFromAddress(BaseConstants.mailFrom);
		return info;
	}

	/**
	 * 异步发送文本格式邮件
	 */
	public static void asynSendText(final String fromName, final String addressee,
			final String subject, final String content) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				MailUtils.sendText(fromName, addressee, subject, content);
			}

		}).start();
	}

	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 */
	public static boolean sendText(String fromName, String addressee, String subject,
			String content) {
		final MailInfo mailInfo = defaultMailInfo();
		mailInfo.setToAddress(addressee);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		// 判断是否需要身份认证
		Auth authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new Auth(mailInfo.getUsername(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			String nick = MimeUtility.encodeText(fromName);
			mailMessage.setFrom(new InternetAddress(nick + " <" + from + ">"));
			// 设置邮件消息的发送者
			//mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (Exception e) {
			log.error("send mail error!", e);
		}
		return false;
	}

	/**
	 * 异步发送HTML格式邮件
	 */
	public static void asynSendHtml(final String fromName, final String addressee,
			final String subject, final String content) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				MailUtils.sendHtml(fromName, addressee, subject, content);
			}

		}).start();
	}

	/**
	 * 以HTML格式发送邮件
	 * @param mailInfo 待发送的邮件信息
	 */
	public static boolean sendHtml(String fromName, String addressee, String subject,
			String content) {
		final MailInfo mailInfo = defaultMailInfo();
		mailInfo.setToAddress(addressee);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		// 判断是否需要身份认证
		Auth authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new Auth(mailInfo.getUsername(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			String nick = MimeUtility.encodeText(fromName);
			mailMessage.setFrom(new InternetAddress(nick + " <" + from + ">"));
			// mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (Exception e) {
			log.error("send mail error!", e);
		}
		return false;
	}

	public static void main(String[] args) throws AddressException {
		System.out.println("xx" + " <" + new InternetAddress("rdj2eelogdev@easycodebox.com") + ">");
		String content = "test";
		// sendText("418262405@qq.com", "德赢快点订-订单提醒", content);// 发送文体格式
		asynSendHtml("VV", "381954728@qq.com", "订单提醒", content);// 发送html格式
	}
}
