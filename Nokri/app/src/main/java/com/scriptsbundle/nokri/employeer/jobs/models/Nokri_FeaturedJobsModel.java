package com.scriptsbundle.nokri.employeer.jobs.models;

import android.graphics.Bitmap;

/**
 * Created by Glixen Technologies on 05/01/2018.
 */

public class Nokri_FeaturedJobsModel {
    private String jobType;
    private String jobTitle;
    private String jobExpire;
    private String jobExpireDate;
    private Bitmap locationIcon;
    private Bitmap editJobImage;
    private String editJobText;
    private Bitmap deleteJobImage;
    private String deleteJobText;
    private String address;

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

    public String getJobExpire() {
        return jobExpire;
    }

    public void setJobExpire(String jobExpire) {
        this.jobExpire = jobExpire;
    }

    public String getJobExpireDate() {
        return jobExpireDate;
    }

    public void setJobExpireDate(String jobExpireDate) {
        this.jobExpireDate = jobExpireDate;
    }

    public Bitmap getLocationIcon() {
        return locationIcon;
    }

    public void setLocationIcon(Bitmap locationIcon) {
        this.locationIcon = locationIcon;
    }

    public Bitmap getEditJobImage() {
        return editJobImage;
    }

    public void setEditJobImage(Bitmap editJobImage) {
        this.editJobImage = editJobImage;
    }

    public String getEditJobText() {
        return editJobText;
    }

    public void setEditJobText(String editJobText) {
        this.editJobText = editJobText;
    }

    public Bitmap getDeleteJobImage() {
        return deleteJobImage;
    }

    public void setDeleteJobImage(Bitmap deleteJobImage) {
        this.deleteJobImage = deleteJobImage;
    }

    public String getDeleteJobText() {
        return deleteJobText;
    }

    public void setDeleteJobText(String deleteJobText) {
        this.deleteJobText = deleteJobText;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
