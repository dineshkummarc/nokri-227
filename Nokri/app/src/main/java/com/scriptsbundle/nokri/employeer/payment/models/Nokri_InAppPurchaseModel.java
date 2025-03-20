package com.scriptsbundle.nokri.employeer.payment.models;

public class Nokri_InAppPurchaseModel {

    private String productId;
    private String productTitle;
    private String productAndroidInApp;
    private String productPrice;
    private String productPricsOnly;
    private String productPriceSign;
    private String productLink;
    private int productQuantity;
    private String androidPurchaseCode;
    private String message;
    private boolean isInAppOn;
    private String noMarketMessage;
    private String oneTimeMessage;
    private String titleText;
    private String billingError;
    private String secretCode;

    public String getBillingError() {
        return billingError;
    }

    public void setBillingError(String billingError) {
        this.billingError = billingError;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public boolean isInAppOn() {
        return isInAppOn;
    }

    public void setInAppOn(boolean inAppOn) {
        isInAppOn = inAppOn;
    }

    public String getNoMarketMessage() {
        return noMarketMessage;
    }

    public void setNoMarketMessage(String noMarketMessage) {
        this.noMarketMessage = noMarketMessage;
    }

    public String getOneTimeMessage() {
        return oneTimeMessage;
    }

    public void setOneTimeMessage(String oneTimeMessage) {
        this.oneTimeMessage = oneTimeMessage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductAndroidInApp() {
        return productAndroidInApp;
    }

    public void setProductAndroidInApp(String productAndroidInApp) {
        this.productAndroidInApp = productAndroidInApp;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductPricsOnly() {
        return productPricsOnly;
    }

    public void setProductPricsOnly(String productPricsOnly) {
        this.productPricsOnly = productPricsOnly;
    }

    public String getProductPriceSign() {
        return productPriceSign;
    }

    public void setProductPriceSign(String productPriceSign) {
        this.productPriceSign = productPriceSign;
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

    public String getAndroidPurchaseCode() {
        return androidPurchaseCode;
    }

    public void setAndroidPurchaseCode(String androidPurchaseCode) {
        this.androidPurchaseCode = androidPurchaseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
