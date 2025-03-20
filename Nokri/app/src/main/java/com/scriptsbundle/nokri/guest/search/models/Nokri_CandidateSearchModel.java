package com.scriptsbundle.nokri.guest.search.models;

public class Nokri_CandidateSearchModel {

    private String title;
    private String location;
    private String skill;
    private String type;
    private String experience;
    private String level;
    private String candLocation;
    private String gender;
    private String salaryRange;
    private String salaryCurrency;
    private String salaryType;
    private String qualification;
    private String headline;
    private boolean isSearchOnly;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getCandLocation() {
        return candLocation;
    }

    public void setCandLocation(String candLocation) {
        this.candLocation = candLocation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }

    public boolean isSearchOnly() {
        return isSearchOnly;
    }

    public void setSearchOnly(boolean searchOnly) {
        isSearchOnly = searchOnly;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return "Nokri_CandidateSearchModel{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", experience='" + experience + '\'' +
                ", level='" + level + '\'' +
                ", skill='" + skill + '\'' +
                ", isSearchOnly=" + isSearchOnly +
                '}';
    }
}
