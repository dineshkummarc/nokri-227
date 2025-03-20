package com.scriptsbundle.nokri.employeer.email.model;

/**
 * Created by Glixen Technologies on 13/01/2018.
 */

public class Nokri_EditEmailTemplateModel {
    private  String srNum;
    private  String name;
     private String link;
     private String id;
     private String buttonText;
     private String deleteButtonText;

    public String getDeleteButtonText() {
        return deleteButtonText;
    }

    public void setDeleteButtonText(String deleteButtonText) {
        this.deleteButtonText = deleteButtonText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public  String getSrNum() {
        return srNum;
    }

    public  void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public  String getName() {
        return name;
    }

    public  void setName(String name) {
        this.name = name;
    }
}
