package com.itcrud.common.mail.springmail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/10/18 15:42
 * @Modified By:
 * @Project_name: zdydoit
 * @Version 1.0
 */
@Component
@Slf4j
public class JavaMailSenderUtils {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private VelocityEngine velocityEngine;

    private static final String REPLACE_PREFIX = "${";

    private static final String REPLACE_SUFFIX = "}";

    /**
     * 字节码的形式
     *
     * @param params
     * @param inlineResource
     */
    public void sendMailForByte(MailParams params, Map<String, byte[]> inlineResource, Map<String, byte[]> attachmentResource) throws Exception {
        Map<String, InputStream> inlines = new HashMap<>();
        if (inlineResource != null && !inlineResource.isEmpty())
            inlineResource.forEach((k, v) -> inlines.put(k, new ByteArrayInputStream(v)));
        Map<String, InputStream> attachments = new HashMap<>();
        if (attachmentResource != null && !attachmentResource.isEmpty())
            attachmentResource.forEach((k, v) -> attachments.put(k, new ByteArrayInputStream(v)));
        sendMailForStream(params, inlines, attachments);
    }

    /**
     * 流的形式
     *
     * @param params
     * @param inlineResource
     * @param attachmentResource
     * @throws Exception
     */
    public void sendMailForStream(MailParams params, Map<String, InputStream> inlineResource, Map<String, InputStream> attachmentResource) throws Exception {
        MimeMessageHelper helper = buidHelper(params);
        Map<String, InputStreamResource> inlines = new HashMap<>();
        if (inlineResource != null && !inlineResource.isEmpty())
            inlineResource.forEach((k, v) -> inlines.put(k, new InputStreamResource(v)));
        Map<String, InputStreamResource> attachments = new HashMap<>();
        if (attachmentResource != null && !attachmentResource.isEmpty())
            attachmentResource.forEach((k, v) -> attachments.put(k, new InputStreamResource(v)));
        sendComplexMail(params, helper, inlines, attachments);
    }

    /**
     * 发送邮件
     */
    public void sendMail(MailParams msg) throws Exception {
        if (StringUtils.isBlank(msg.getFrom()))
            throw new MailSendException("from can not be empty");
        if (msg.getTo() == null || msg.getTo().isEmpty())
            throw new MailSendException("reciever can not be empty");
        if (StringUtils.isBlank(msg.getSubject()))
            throw new MailSendException("subject can not be empty");
        MimeMessageHelper helper = buidHelper(msg);
        if (msg.isSimple()) {
            sendSimpleMail(msg, helper, msg.isHTML());
        } else {
            sendComplexMail(msg, helper, null, null);
        }
    }

    /**
     * 构建MimeMessageHelper
     */
    private MimeMessageHelper buidHelper(MailParams params) throws MessagingException {
        MimeMessageHelper helper;
        MimeMessage mm = sender.createMimeMessage();
        if (params.isSimple()) {
            helper = new MimeMessageHelper(mm);
        } else {
            helper = new MimeMessageHelper(mm, true);
        }
        helper.setSubject(params.getSubject());
        helper.setFrom(params.getFrom());
        helper.setTo(list2array(params.getTo()));
        if (params.getCc() != null && !params.getCc().isEmpty())
            helper.setCc(list2array(params.getCc()));
        if (params.getBcc() != null && !params.getBcc().isEmpty())
            helper.setBcc(list2array(params.getBcc()));
        return helper;
    }

    /**
     * 发送简单邮件
     */
    private void sendSimpleMail(MailParams params, MimeMessageHelper helper, boolean isHTML) throws MailSendException, MessagingException {
        /**
         * 发送简单邮件逻辑
         * 主题，发送人，收件人,发送内容(simpleText)必须不能为空
         * 替换字符集合选填
         */
        //字符替换
        if (StringUtils.isBlank(params.getSimpleText()))
            throw new MailSendException("text can not be empty");
        helper.setText(replaceText(params), isHTML);
        sender.send(helper.getMimeMessage());
    }

    /**
     * 发送复杂邮件
     */
    private void sendComplexMail(MailParams params, MimeMessageHelper helper,
                                 Map<String, InputStreamResource> inLineResource, Map<String, InputStreamResource> attachmentResource) throws MailSendException, MessagingException {
        /**
         * 发送复杂邮件逻辑
         * 主题，发送人，收件人必能为空
         * 发送内容(simpleText或templateLocation)必须有一个有值，都有值取templateLocation
         * replaceMap、inlines、attachments选填
         */
        if (StringUtils.isNotBlank(params.getTemplateLocation())) {
            //template
            VelocityContext context = new VelocityContext();
            if (params.getReplaceMap() != null && !params.getReplaceMap().isEmpty()) {
                params.getReplaceMap().forEach(context::put);
            }
            try (StringWriter writer = new StringWriter()) {
                velocityEngine.mergeTemplate(params.getTemplateLocation(), "UTF-8", context, writer);
                helper.setText(writer.toString(), params.isHTML());
            } catch (Exception e) {
                throw new MailSendException("read mail template fail");
            }
        } else {
            //html
            if (StringUtils.isBlank(params.getSimpleText()))
                throw new MailSendException("mail text can not be empty");
            helper.setText(replaceText(params), params.isHTML());
        }
        if (params.getInlines() != null && !params.getInlines().isEmpty()) {
            for (Map.Entry<String, String> e : params.getInlines().entrySet()) {
                helper.addInline(e.getKey(), new FileDataSource(e.getValue()));
            }
        }
        if (params.getAttachments() != null && !params.getAttachments().isEmpty()) {
            for (Map.Entry<String, String> e : params.getAttachments().entrySet()) {
                helper.addAttachment(e.getKey(), new FileDataSource(e.getValue()));
            }
        }
        if (inLineResource != null && !inLineResource.isEmpty()) {
            for (Map.Entry<String, InputStreamResource> s : inLineResource.entrySet()) {
                helper.addInline(s.getKey(), s.getValue());
            }
        }
        if (attachmentResource != null && !attachmentResource.isEmpty()) {
            for (Map.Entry<String, InputStreamResource> s : attachmentResource.entrySet()) {
                helper.addAttachment(s.getKey(), s.getValue());
            }
        }
        sender.send(helper.getMimeMessage());
    }

    /**
     * 替换简单邮件的占位符
     */
    private String replaceText(MailParams params) {
        String text = params.getSimpleText();
        if (params.getReplaceMap() != null && !params.getReplaceMap().isEmpty()) {
            for (Map.Entry<String, String> e : params.getReplaceMap().entrySet()) {
                text = text.replace(REPLACE_PREFIX
                        + e.getKey() + REPLACE_SUFFIX, e.getValue());
            }
        }
        return text;
    }

    /**
     * list集合转数组
     */
    private String[] list2array(List<String> list) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}
