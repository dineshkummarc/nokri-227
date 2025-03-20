package com.scriptsbundle.nokri.candidate.profile.model;

public class Nokri_ContactModel {
    private String nameHint;
    private String emailHint;
    private String subjectHint;
    private String messageHint;
    private String buttonText;
    private String contactTitleText;
    private String receiverId;
    private String receiverName;
    private String receiverEmail;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getNameHint() {
        return nameHint;
    }

    public void setNameHint(String nameHint) {
        this.nameHint = nameHint;
    }

    public String getEmailHint() {
        return emailHint;
    }

    public void setEmailHint(String emailHint) {
        this.emailHint = emailHint;
    }

    public String getSubjectHint() {
        return subjectHint;
    }

    public void setSubjectHint(String subjectHint) {
        this.subjectHint = subjectHint;
    }

    public String getMessageHint() {
        return messageHint;
    }

    public void setMessageHint(String messageHint) {
        this.messageHint = messageHint;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getContactTitleText() {
        return contactTitleText;
    }

    public void setContactTitleText(String contactTitleText) {
        this.contactTitleText = contactTitleText;
    }
}
