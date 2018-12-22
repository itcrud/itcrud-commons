package com.itcrud.common.mail.javamail;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/10/14 13:15
 * @Modified By:
 * @Project_name: zdydoit
 * @Version 1.0
 */
public class JavaMailSenderUtils {

    private static Logger logger;

    private static String MAIL_HOST;
    private static String MAIL_HOST_PORT;
    private static String MAIL_TRANSPORT_PROTOCOL;
    private static String MAIL_SMTP_AUTH;

    private static String MAIL_NAME;
    private static String MAIL_PWD;

    private static ThreadLocal<MailParams> localMail = new ThreadLocal<>();

    /*初始化参数*/
    static {
        logger = LoggerFactory.getLogger(JavaMailSenderUtils.class);
        URL resource = JavaMailSenderUtils.class
                .getClassLoader().getResource("mail.properties");
        if (resource != null) {
            File f = new File(resource.getFile());
            try (InputStream in = new FileInputStream(f)) {
                Properties prop = new Properties();
                prop.load(in);
                if (StringUtils.isNotBlank(prop.getProperty("mail.host")))
                    MAIL_HOST = prop.getProperty("mail.host");
                if (StringUtils.isNotBlank(prop.getProperty("mail.host.port")))
                    MAIL_HOST_PORT = prop.getProperty("mail.host.port");
                if (StringUtils.isNotBlank(prop.getProperty("mail.transport.protocol")))
                    MAIL_TRANSPORT_PROTOCOL = prop.getProperty("mail.transport.protocol");
                if (StringUtils.isNotBlank(prop.getProperty("mail.smtp.auth")))
                    MAIL_SMTP_AUTH = prop.getProperty("mail.smtp.auth");
                if (StringUtils.isNotBlank(prop.getProperty("mail.name")))
                    MAIL_NAME = prop.getProperty("mail.name");
                if (StringUtils.isNotBlank(prop.getProperty("mail.pwd")))
                    MAIL_PWD = prop.getProperty("mail.pwd");
            } catch (Exception e) {
                logger.warn("*******JavaMailSend builder fail*******");
            }
        }
    }

    /**
     * 发送邮件
     *
     * @param mailParams
     * @param isSimple
     * @throws Exception
     */
    public static void sendMail(MailParams mailParams, Boolean isSimple) throws Exception {
        if (isSimple) sendSimpleMail(mailParams);
        else sendInnerImageOrAttachAndMixedMail(mailParams);
    }

    /**
     * 发送普通邮件
     *
     * @param mailParams
     * @throws Exception
     */
    private static void sendSimpleMail(MailParams mailParams) throws Exception {
        MimeMessage message = prepare(mailParams);
        message.setText(mailParams.getText());
        Transport.send(message);
    }

    /**
     * 发送带图片、附件综合邮件的推送最终方法
     */
    private static void sendInnerImageOrAttachAndMixedMail(MailParams mailParams) throws Exception {
        localMail.set(mailParams);
        MimeMessage message = prepare(mailParams);
        //构建
        MimeMultipart mp = buildBodyPart();
        message.setContent(mp);
        Transport.send(message);
    }

    /**
     * 构建附件和图片部分
     *
     * @return
     */
    private static MimeMultipart buildBodyPart() throws MessagingException {
        MailParams mailParams = localMail.get();
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(mailParams.getText(), "text/html;charset=UTF-8");
        List<MimeBodyPart> mbpList = new ArrayList<>();
        if (mailParams.getInnerImages() != null && mailParams.getInnerImages().size() > 0)
            buildBodyPart(mailParams.getInnerImages(), mbpList, 1);
        if (mailParams.getAttachs() != null && mailParams.getAttachs().size() > 0)
            buildBodyPart(mailParams.getAttachs(), mbpList, 2);
        MimeMultipart mp = new MimeMultipart();
        for (MimeBodyPart mbp : mbpList) {
            mp.addBodyPart(mbp);
        }
        mp.addBodyPart(text);
        if (mailParams.isMixed()) mp.setSubType("mixed");
        return mp;
    }

    /**
     * 构建附件和图片部分
     *
     * @param source
     * @param mbpList
     * @param type
     */
    private static void buildBodyPart
    (Map<String, String> source, List<MimeBodyPart> mbpList, int type) {
        source.forEach((k, v) -> {
            MimeBodyPart mbp = new MimeBodyPart();
            DataHandler dh = new DataHandler(new FileDataSource(v));
            try {
                mbp.setDataHandler(dh);
                if ((type & 1) == 0) {
                    mbp.setFileName(k);
                } else {
                    mbp.setContentID(k);
                }
            } catch (MessagingException e) {
                logger.error("构建内嵌图片失败");
            }
            mbpList.add(mbp);
        });
    }

    private static Session getSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.host", MAIL_HOST);
        properties.setProperty("mail.transport.protocol", MAIL_TRANSPORT_PROTOCOL);
        properties.setProperty("mail.smtp.auth", MAIL_SMTP_AUTH);
        properties.setProperty("mail.host.port", MAIL_HOST_PORT);
        return Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_NAME, MAIL_PWD);
            }
        });
    }

    /**
     * 准备工作
     *
     * @param mailParams
     * @return
     * @throws Exception
     */
    private static MimeMessage prepare(MailParams mailParams) throws Exception {
        localMail.set(mailParams);
        Session session = getSession();
        return buildBaseMsg(session);
    }

    /**
     * 构建邮件基础信息
     *
     * @param session
     * @return
     * @throws Exception
     */
    private static MimeMessage buildBaseMsg(Session session) throws Exception {
        MailParams mailInfo = localMail.get();
        MimeMessage message = new MimeMessage(session);
        message.setSubject(mailInfo.getSubject());
        addReceiver(message);
        return message;
    }

    /**
     * 添加收件人、抄送人、密送人
     *
     * @param message
     * @throws Exception
     */
    private static void addReceiver(MimeMessage message) throws Exception {
        message.setFrom(new InternetAddress(MAIL_NAME));
        addReceiver(MimeMessage.RecipientType.TO, message);
        addReceiver(MimeMessage.RecipientType.CC, message);
        addReceiver(MimeMessage.RecipientType.BCC, message);
    }

    /**
     * 添加收件人、抄送人、密送人
     *
     * @param type
     * @param message
     * @throws Exception
     */
    private static void addReceiver(Message.RecipientType type, MimeMessage message) throws Exception {
        MailParams mailInfo = localMail.get();
        List<String> receivers = new ArrayList<>();
        if (type == MimeMessage.RecipientType.TO) {
            receivers = mailInfo.getTo();
        } else if (type == MimeMessage.RecipientType.CC) {
            receivers = mailInfo.getCc();
        } else if (type == MimeMessage.RecipientType.BCC) {
            receivers = mailInfo.getBcc();
        }
        if (receivers == null || receivers.size() == 0) return;
        InternetAddress[] addressArr = new InternetAddress[receivers.size()];
        for (int i = 0; i < receivers.size(); i++) {
            addressArr[i] = new InternetAddress(receivers.get(i));
        }
        message.setRecipients(type, addressArr);
    }
}
