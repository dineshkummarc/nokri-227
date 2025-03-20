package com.scriptsbundle.nokri.guest.search.models;

/**
 * Created by GlixenTech on 3/13/2018.
 */

public class Nokri_JobSearchModel {
    private String searchNow , jobCategory ,jobQualification ,jobType ,salaryCurrency ,jobShift ,jobLevel ,jobSkills,subCategory1,subCategory2,subCategory3;
    private String location;
     private String  Latitude,Longitude;
    private String Distance;

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }



    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubCategory1() {
        return subCategory1;
    }

    public void setSubCategory1(String subCategory1) {
        this.subCategory1 = subCategory1;
    }

    public String getSubCategory2() {
        return subCategory2;
    }

    public void setSubCategory2(String subCategory2) {
        this.subCategory2 = subCategory2;
    }

    public String getSubCategory3() {
        return subCategory3;
    }

    public void setSubCategory3(String subCategory3) {
        this.subCategory3 = subCategory3;
    }

    public String getSearchNow() {
        return searchNow;
    }

    public void setSearchNow(String searchNow) {
        this.searchNow = searchNow;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getJobQualification() {
        return jobQualification;
    }

    public void setJobQualification(String jobQualification) {
        this.jobQualification = jobQualification;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public String getJobShift() {
        return jobShift;
    }

    public void setJobShift(String jobShift) {
        this.jobShift = jobShift;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getJobSkills() {
        return jobSkills;
    }

    public void setJobSkills(String jobSkills) {
        this.jobSkills = jobSkills;
    }

    @Override
    public String toString() {
        return "Nokri_JobSearchModel{" +
                "searchNow='" + searchNow + '\'' +
                ", jobCategory='" + jobCategory + '\'' +
                ", jobQualification='" + jobQualification + '\'' +
                ", jobType='" + jobType + '\'' +
                ", salaryCurrency='" + salaryCurrency + '\'' +
                ", jobShift='" + jobShift + '\'' +
                ", jobLevel='" + jobLevel + '\'' +
                ", jobSkills='" + jobSkills + '\'' +
                ", subCategory1='" + subCategory1 + '\'' +
                ", subCategory2='" + subCategory2 + '\'' +
                ", subCategory3='" + subCategory3 + '\'' +
                '}';
    }
}
