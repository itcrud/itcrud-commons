package com.itcrud.common.mail.javamail;


import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/10/14 12:07
 * @Modified By:
 * @Project_name: zdydoit
 * @Version 1.0
 */
public class MailTest {

    public static void main(String[] args) throws Exception {
        sendMsg();
        //send();
    }

    private static void sendMsg() throws MessagingException {
        //构建Session
        Properties properties = new Properties();
        properties.setProperty("mail.host", "smtp.163.com");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.host.port", "25");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("millery88@163.com", "***");
            }
        });
        session.setDebug(true);//打印发送日志

        //构建基础信息
        MimeMessage message = new MimeMessage(session);
        message.setSubject("这是一封测试邮件！");
        //message.setText("测试邮件内容随意");
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("itcrud@aliyun.com"));
        message.setFrom(new InternetAddress("millery88@163.com"));

        //构建内容
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("<html><body><h3>你好，这是一封模板邮件!</h3><img src='cid:avatar'><h3>上面内嵌了一张图片↑↑↑↑</h3></body></html>", "text/html;charset=UTF-8");
        MimeBodyPart bodyPart = new MimeBodyPart();

        //构建内嵌图片
        DataHandler dataHandler = new DataHandler(new FileDataSource("/Users/joker/Downloads/avatar.png"));
        bodyPart.setDataHandler(dataHandler);
        bodyPart.setContentID("avatar");//如果没有这句，表示作为附件发送，加上表示显示在邮件内容的一部分

        //构建附件
        MimeBodyPart attachment = new MimeBodyPart();
        DataHandler attachmentHandler = new DataHandler(new FileDataSource("/Users/joker/Downloads/avatar.png"));
        attachment.setDataHandler(attachmentHandler);
        attachment.addHeader("Content-Type", "UTF-8");//防止中文名乱码
        attachment.setFileName("中文名称.png");//这里名称记得带上后缀名，否则邮件附件下载下来需要手动添加后缀，可能会损坏

        //构建邮件体内容
        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(text);
        mimeMultipart.addBodyPart(bodyPart);
        mimeMultipart.addBodyPart(attachment);
        mimeMultipart.setSubType("mixed");//设置为mixed，别忘了哦
        message.setContent(mimeMultipart);

        //发送邮件
        Transport.send(message);
    }

    private static void send() throws MessagingException {
        //构建Session
        Properties properties = new Properties();
        properties.setProperty("mail.host", "smtp.163.com");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.host.port", "25");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("millery88@163.com", "****");
            }
        });

        session.setDebug(true);
        //构建邮件MimeMessage
        MimeMessage message = new MimeMessage(session);
        message.setSubject("这是一封测试邮件！");
        message.setText("测试邮件内容随意");
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("itcrud@aliyun.com"));
        message.setFrom(new InternetAddress("millery88@163.com"));

        //发送邮件
        Transport.send(message);
    }
}
