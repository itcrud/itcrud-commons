package com.itcrud.common.mail.javamail;

import java.util.List;
import java.util.Map;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/10/14 14:42
 * @Modified By:
 * @Project_name: zdydoit
 * @Version 1.0
 */
public class MailParams {
    private String subject;
    private String text;
    private Map<String,String> innerImages;
    private Map<String,String> attachs;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private boolean mixed;

    public boolean isMixed() {
        return mixed;
    }

    public void setMixed(boolean mixed) {
        this.mixed = mixed;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getAttachs() {
        return attachs;
    }

    public void setAttachs(Map<String, String> attachs) {
        this.attachs = attachs;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public Map<String, String> getInnerImages() {
        return innerImages;
    }

    public void setInnerImages(Map<String, String> innerImages) {
        this.innerImages = innerImages;
    }
}
