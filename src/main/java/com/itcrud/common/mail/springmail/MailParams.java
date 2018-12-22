package com.itcrud.common.mail.springmail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/10/18 15:45
 * @Modified By:
 * @Project_name: zdydoit
 * @Version 1.0
 */
public class MailParams {
    /*主题*/
    private String subject;
    /*简单文本信息*/
    private String simpleText;
    /*发送人*/
    public String from;
    /*收件人*/
    public List<String> to;
    /*抄送人*/
    private List<String> cc;
    /*密送人*/
    private List<String> bcc;
    /*模板位置*/
    private String templateLocation;
    /*文字替换集合*/
    private Map<String, String> replaceMap;
    /*图片替换集合*/
    private Map<String, String> inlines;
    /*附件键值对*/
    private Map<String, String> attachments;
    /*是否为简单邮件*/
    private boolean isSimple;
    /*是否为HTML内容*/
    private boolean isHTML;

    public static class Builder {
        private String subject;
        private String simpleText;
        private String from;
        private List<String> to;
        private List<String> cc;
        private List<String> bcc;
        private String templateLocation;
        private Map<String, String> replaceMap;
        private Map<String, String> inlines;
        private Map<String, String> attachments;
        private boolean isSimple;
        private boolean isHTML;

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder simpleText(String simpleText) {
            this.simpleText = simpleText;
            return this;
        }

        public Builder templateLocation(String templateLocation) {
            this.templateLocation = templateLocation;
            return this;
        }

        public Builder inlines(Map<String, String> inlines) {
            this.inlines = inlines;
            return this;
        }

        public Builder attachments(Map<String, String> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder replaceMap(Map<String, String> replaceMap) {
            this.replaceMap = replaceMap;
            return this;
        }

        public Builder addInline(String key, String value) {
            if (inlines == null) inlines = new HashMap<>();
            inlines.put(key, value);
            return this;
        }

        public Builder addAttachment(String key, String value) {
            if (attachments == null) attachments = new HashMap<>();
            attachments.put(key, value);
            return this;
        }

        public Builder addReplace(String key, String value) {
            if (replaceMap == null) replaceMap = new HashMap<>();
            replaceMap.put(key, value);
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(List<String> to) {
            this.to = to;
            return this;
        }

        public Builder addTo(String e) {
            if (to == null) to = new ArrayList<>();
            to.add(e);
            return this;
        }

        public Builder cc(List<String> cc) {
            this.cc = cc;
            return this;
        }

        public Builder addCc(String e) {
            if (cc == null) cc = new ArrayList<>();
            cc.add(e);
            return this;
        }

        public Builder bcc(List<String> bcc) {
            this.bcc = bcc;
            return this;
        }

        public Builder addBcc(String e) {
            if (bcc == null) bcc = new ArrayList<>();
            bcc.add(e);
            return this;
        }

        public Builder isHTML(boolean isHTML) {
            this.isHTML = isHTML;
            return this;
        }

        public Builder isSimple(boolean isSimple) {
            this.isSimple = isSimple;
            return this;
        }

        public MailParams build() {
            MailParams params = new MailParams();
            params.setSubject(this.subject);
            params.setSimpleText(this.simpleText);
            params.setFrom(from);
            params.setTo(to);
            params.setCc(cc);
            params.setBcc(bcc);
            params.setTemplateLocation(this.templateLocation);
            params.setInlines(this.inlines);
            params.setAttachments(this.attachments);
            params.setReplaceMap(this.replaceMap);
            params.setHTML(isHTML);
            params.setSimple(isSimple);
            return params;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSubject() {
        return subject;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public Map<String, String> getReplaceMap() {
        return replaceMap;
    }

    public Map<String, String> getInlines() {
        return inlines;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public boolean isHTML() {
        return isHTML;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setReplaceMap(Map<String, String> replaceMap) {
        this.replaceMap = replaceMap;
    }

    public void setInlines(Map<String, String> inlines) {
        this.inlines = inlines;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    public void setHTML(boolean HTML) {
        isHTML = HTML;
    }
}
