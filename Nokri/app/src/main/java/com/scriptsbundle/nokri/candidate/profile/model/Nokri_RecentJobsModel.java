package com.scriptsbundle.nokri.candidate.profile.model;

import android.graphics.Bitmap;

/**
 * Created by Glixen Technologies on 27/12/2017.
 */

public class Nokri_RecentJobsModel {
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

    public Nokri_RecentJobsModel() {
    }

    public Bitmap getCompanyIcon() {
        return companyIcon;
    }

    public void setCompanyIcon(Bitmap companyIcon) {
        this.companyIcon = companyIcon;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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
        return "Nokri_RecentJobsModel{" +
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
                ", jobId='" + jobId + '\'' +
                ", companyLogo='" + companyLogo + '\'' +
                ", companyId='" + companyId + '\'' +
                '}';
    }
}


