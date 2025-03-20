package com.scriptsbundle.nokri.guest.search.models;

public class LocationModel {
    String radiusText;
    String latitudeText;
    String longitudeText;

    public String getRadiusText() {
        return radiusText;
    }

    public void setRadiusText(String radiusText) {
        this.radiusText = radiusText;
    }

    public String getLatitudeText() {
        return latitudeText;
    }

    public void setLatitudeText(String latitudeText) {
        this.latitudeText = latitudeText;
    }

    public String getLongitudeText() {
        return longitudeText;
    }

    public void setLongitudeText(String longitudeText) {
        this.longitudeText = longitudeText;
    }

    public String getGeoLocationText() {
        return geoLocationText;
    }

    public void setGeoLocationText(String geoLocationText) {
        this.geoLocationText = geoLocationText;
    }

    String geoLocationText;
}
