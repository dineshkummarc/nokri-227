package com.scriptsbundle.nokri.candidate.jobs.models;

import android.graphics.Bitmap;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_JobsModel {
    private Bitmap companyIcon;
    private Bitmap timeIcon;
    private Bitmap locationIcon;
    private String jobType;
    private String jobTitle;
    private String jobDescription;
    private String timeRemaining;
    private String address;
    private String salary;
    private String paymentPeriod;
    private String jobId;
    private String companyLogo;
    private String companyId;
    private  boolean showMenu = true;
    private String location;
    private String  Latitude,Longitude;
    private String Distance;
    private boolean isFeatured;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Nokri_JobsModel() {
    }

    public Bitmap getCompanyIcon() {
        return companyIcon;
    }

    public void setCompanyIcon(Bitmap companyIcon) {
        this.companyIcon = companyIcon;
    }

    public Bitmap getTimeIcon() {
        return timeIcon;
    }

    public void setTimeIcon(Bitmap timeIcon) {
        this.timeIcon = timeIcon;
    }

    public Bitmap getLocationIcon() {
        return locationIcon;
    }

    public void setLocationIcon(Bitmap locationIcon) {
        this.locationIcon = locationIcon;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(String timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(String paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    @Override
    public String toString() {
        return "Nokri_JobsModel{" +
                "companyIcon=" + companyIcon +
                ", timeIcon=" + timeIcon +
                ", locationIcon=" + locationIcon +
                ", jobType='" + jobType + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", timeRemaining='" + timeRemaining + '\'' +
                ", address='" + address + '\'' +
                ", salary='" + salary + '\'' +
                ", paymentPeriod='" + paymentPeriod + '\'' +
                '}';
    }

}

