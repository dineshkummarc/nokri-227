package com.scriptsbundle.nokri.guest.home.models;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_SelectJobsModel {
    private int id;
    private String logo;
    private String jobTitle;
    private String jobsInclude;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Nokri_SelectJobsModel() {
    }


    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobsInclude() {
        return jobsInclude;
    }

    public void setJobsInclude(String jobsInclude) {
        this.jobsInclude = jobsInclude;
    }

    @Override
    public String toString() {
        return "Nokri_SelectJobsModel{" +
                "logo=" + logo +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobsInclude='" + jobsInclude + '\'' +
                '}';
    }
}
