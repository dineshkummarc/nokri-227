package com.scriptsbundle.nokri.candidate.profile.model;

/**
 * Created by Glixen Technologies on 08/01/2018.
 */

public class Nokri_MyProfileModel {
    private String titleText;
    private String dateText;
    private String paragraphText;
    private String degreeTitle;
    private String degreePercentage;
    private String degreeGrade;
    private String percentageTitle;
    private String gradeTitle;

    public String getPercentageTitle() {
        return percentageTitle;
    }

    public void setPercentageTitle(String percentageTitle) {
        this.percentageTitle = percentageTitle;
    }

    public String getGradeTitle() {
        return gradeTitle;
    }

    public void setGradeTitle(String gradeTitle) {
        this.gradeTitle = gradeTitle;
    }

    public String getDegreePercentage() {
        return degreePercentage;
    }

    public void setDegreePercentage(String degreePercentage) {
        this.degreePercentage = degreePercentage;
    }

    public String getDegreeGrade() {
        return degreeGrade;
    }

    public void setDegreeGrade(String degreeGrade) {
        this.degreeGrade = degreeGrade;
    }

    public String getDegreeTitle() {
        return degreeTitle;
    }

    public void setDegreeTitle(String degreeTitle) {
        this.degreeTitle = degreeTitle;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public void setParagraphText(String paragraphText) {
        this.paragraphText = paragraphText;
    }

    @Override
    public String toString() {
        return "Nokri_MyProfileModel{" +
                "titleText='" + titleText + '\'' +
                ", dateText='" + dateText + '\'' +
                ", paragraphText='" + paragraphText + '\'' +
                '}';
    }
}
