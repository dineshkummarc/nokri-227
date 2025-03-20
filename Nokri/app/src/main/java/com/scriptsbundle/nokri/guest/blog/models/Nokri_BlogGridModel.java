package com.scriptsbundle.nokri.guest.blog.models;

/**
 * Created by Glixen Technologies on 08/01/2018.
 */

public class Nokri_BlogGridModel {

    private String headerImage;
    private String dateText;
    private String commentsText;
    private String headingText;
    private String paragraphText;
    private String id;
    private boolean hasImage;
    private boolean htmlResponse;

    public boolean isHtmlResponse() {
        return htmlResponse;
    }

    public void setHtmlResponse(boolean htmlResponse) {
        this.htmlResponse = htmlResponse;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getCommentsText() {
        return commentsText;
    }

    public void setCommentsText(String commentsText) {
        this.commentsText = commentsText;
    }

    public String getHeadingText() {
        return headingText;
    }

    public void setHeadingText(String headingText) {
        this.headingText = headingText;
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public void setParagraphText(String paragraphText) {
        this.paragraphText = paragraphText;
    }
}
