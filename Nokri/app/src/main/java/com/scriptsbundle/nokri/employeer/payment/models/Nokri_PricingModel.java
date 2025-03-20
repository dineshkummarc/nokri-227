package com.scriptsbundle.nokri.employeer.payment.models;

import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;

import java.util.ArrayList;

public class Nokri_PricingModel {


    private String daysText;
    private String daysValue;
    private String adsText;
    private int adsValue;
    private ArrayList<Nokri_PremiumJobs>preniumJobs;
    private String producId;
    private String productTitle;
    private String productPrice;
    private String productLink;
    private int productQuantity;
    private String productButtonText;
    private String currency;
    private Nokri_InAppPurchaseModel inAppPurchaseModel;
    private ArrayList<Nokri_SpinnerModel> spinnerModel;
    private String buttonText;
    private Boolean isButtonVisible = false;

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public Boolean getButtonVisible() {
        return isButtonVisible;
    }

    public void setButtonVisible(Boolean buttonVisible) {
        isButtonVisible = buttonVisible;
    }

    public ArrayList<Nokri_SpinnerModel> getSpinnerModel() {
        return spinnerModel;
    }

    public void setSpinnerModel(ArrayList<Nokri_SpinnerModel> spinnerModel) {
        this.spinnerModel = spinnerModel;
    }

    public Nokri_InAppPurchaseModel getInAppPurchaseModel() {
        return inAppPurchaseModel;
    }

    public void setInAppPurchaseModel(Nokri_InAppPurchaseModel inAppPurchaseModel) {
        this.inAppPurchaseModel = inAppPurchaseModel;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDaysText() {
        return daysText;
    }

    public void setDaysText(String daysText) {
        this.daysText = daysText;
    }

    public String getDaysValue() {
        return daysValue;
    }

    public void setDaysValue(String daysValue) {
        this.daysValue = daysValue;
    }

    public String getAdsText() {
        return adsText;
    }

    public void setAdsText(String adsText) {
        this.adsText = adsText;
    }

    public int getAdsValue() {
        return adsValue;
    }

    public void setAdsValue(int adsValue) {
        this.adsValue = adsValue;
    }

    public ArrayList<Nokri_PremiumJobs> getPreniumJobs() {
        return preniumJobs;
    }

    public void setPremiumJobs(ArrayList<Nokri_PremiumJobs> preniumJobs) {
        this.preniumJobs = preniumJobs;
    }

    public String getProducId() {
        return producId;
    }

    public void setProducId(String producId) {
        this.producId = producId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductButtonText() {
        return productButtonText;
    }

    public void setProductButtonText(String productButtonText) {
        this.productButtonText = productButtonText;
    }

    public class Nokri_PremiumJobs {
        private String numberOfJobs;
        private String name;

        public String getNumberOfJobs() {
            return numberOfJobs;
        }

        public void setNumberOfJobs(String numberOfJobs) {
            this.numberOfJobs = numberOfJobs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "nokri_PreniumJobs{" +
                    "numberOfJobs='" + numberOfJobs + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Nokri_PricingModel{" +
                "daysText='" + daysText + '\'' +
                ", daysValue='" + daysValue + '\'' +
                ", adsText='" + adsText + '\'' +
                ", adsValue=" + adsValue +
                ", preniumJobs=" + preniumJobs +
                ", producId='" + producId + '\'' +
                ", productTitle='" + productTitle + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productLink='" + productLink + '\'' +
                ", productQuantity=" + productQuantity +
                ", productButtonText=" + productButtonText +
                '}';
    }
}
