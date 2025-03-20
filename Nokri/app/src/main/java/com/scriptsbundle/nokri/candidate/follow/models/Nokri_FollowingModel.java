package com.scriptsbundle.nokri.candidate.follow.models;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_FollowingModel {
private String companyLogo;
private String companyName;
private String companyAddress;
private String unfollow;
private String companyId;
private String totalPositons;

    public String getTotalPositons() {
        return totalPositons;
    }

    public void setTotalPositons(String totalPositons) {
        this.totalPositons = totalPositons;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Nokri_FollowingModel() {
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getUnfollow() {
        return unfollow;
    }

    public void setUnfollow(String unfollow) {
        this.unfollow = unfollow;
    }

    @Override
    public String toString() {
        return "Nokri_FollowingModel{" +
                "companyLogo=" + companyLogo +
                ", companyName='" + companyName + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", unfollow='" + unfollow + '\'' +
                '}';
    }


}
