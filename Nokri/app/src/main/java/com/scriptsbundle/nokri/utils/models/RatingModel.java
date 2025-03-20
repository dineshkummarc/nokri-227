package com.scriptsbundle.nokri.utils.models;

public class RatingModel {
    String url;
    String raterName;
    String raterComments;
    String raterTitle;
    String date;
    String authorName;
    String authorReply;
    double rating;
    boolean containsReply;
    boolean canReply;
    String commentID;
    public String replyLabel;
    public String submit;
    public String cancel;

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getRaterTitle() {
        return raterTitle;
    }

    public void setRaterTitle(String raterTitle) {
        this.raterTitle = raterTitle;
    }

    public boolean isCanReply() {
        return canReply;
    }

    public void setCanReply(boolean canReply) {
        this.canReply = canReply;
    }

    public boolean isContainsReply() {
        return containsReply;
    }

    public void setContainsReply(boolean containsReply) {
        this.containsReply = containsReply;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRaterName() {
        return raterName;
    }

    public void setRaterName(String raterName) {
        this.raterName = raterName;
    }

    public String getRaterComments() {
        return raterComments;
    }

    public void setRaterComments(String raterComments) {
        this.raterComments = raterComments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorReply() {
        return authorReply;
    }

    public void setAuthorReply(String authorReply) {
        this.authorReply = authorReply;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


}
