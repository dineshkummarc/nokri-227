package com.scriptsbundle.nokri.employeer.jobs.models;

import com.scriptsbundle.nokri.employeer.jobs.fragments.Questionnaire.QuestionnaireModel;

import java.util.ArrayList;

/**
 * Created by Glixen Technologies on 10/02/2018.
 */

public class Nokri_ResumeReceivedModel {

    private String id;
    private String imageUrl;
    private String downlaodUrl;
    private String name;
    private String jobType;
    private String date;
    private String address;
    private String actionButtonText;
    private String fileName;
    private boolean isJobLinkedin;
    private String listItemText;
    private String coverLetterTitle;
    private String coverLetterText;
    private boolean isCoverLetterAvailable;
    private ArrayList<QuestionnaireModel> questions;

    public String getCoverLetterTitle() {
        return coverLetterTitle;
    }

    public void setCoverLetterTitle(String coverLetterTitle) {
        this.coverLetterTitle = coverLetterTitle;
    }

    public String getCoverLetterText() {
        return coverLetterText;
    }

    public void setCoverLetterText(String coverLetterText) {
        this.coverLetterText = coverLetterText;
    }

    public boolean isCoverLetterAvailable() {
        return isCoverLetterAvailable;
    }

    public void setCoverLetterAvailable(boolean coverLetterAvailable) {
        isCoverLetterAvailable = coverLetterAvailable;
    }

    public boolean isJobLinkedin() {
        return isJobLinkedin;
    }

    public void setJobLinkedin(boolean jobLinkedin) {
        isJobLinkedin = jobLinkedin;
    }

    public String getListItemText() {
        return listItemText;
    }

    public void setListItemText(String listItemText) {
        this.listItemText = listItemText;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDownlaodUrl() {
        return downlaodUrl;
    }

    public void setDownlaodUrl(String downlaodUrl) {
        this.downlaodUrl = downlaodUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getActionButtonText() {
        return actionButtonText;
    }

    public void setActionButtonText(String actionButtonText) {
        this.actionButtonText = actionButtonText;
    }

    @Override
    public String toString() {
        return "Nokri_ResumeReceivedModel{" +
                "id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", downlaodUrl='" + downlaodUrl + '\'' +
                ", name='" + name + '\'' +
                ", jobType='" + jobType + '\'' +
                ", date='" + date + '\'' +
                ", address='" + address + '\'' +
                ", actionButtonText='" + actionButtonText + '\'' +
                ", fileName='" + fileName + '\'' +
                ", isJobLinkedin=" + isJobLinkedin +
                ", listItemText='" + listItemText + '\'' +
                '}';
    }

    public ArrayList<QuestionnaireModel> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuestionnaireModel> questions) {
        this.questions = questions;
    }
}

