package com.scriptsbundle.nokri.guest.blog.models;

/**
 * Created by Glixen Technologies on 09/01/2018.
 */

public class Nokri_CommentsModel {
    private String id;
    private String profilImage;
    private String nameText;
    private String dateText;
    private String commentText;
    private String replyButtonText;
    private boolean isReply;

    public String getReplyButtonText() {
        return replyButtonText;
    }

    public void setReplyButtonText(String replyButtonText) {
        this.replyButtonText = replyButtonText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

    public String getProfilImage() {
        return profilImage;
    }

    public void setProfilImage(String profilImage) {
        this.profilImage = profilImage;
    }

    public String getNameText() {
        return nameText;
    }

    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
