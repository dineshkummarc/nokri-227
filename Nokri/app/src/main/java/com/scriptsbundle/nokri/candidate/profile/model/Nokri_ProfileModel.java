package com.scriptsbundle.nokri.candidate.profile.model;

import android.graphics.Bitmap;

/**
 * Created by Glixen Technologies on 08/01/2018.
 */

public class Nokri_ProfileModel {
    private Bitmap icon;
    private String title;
    private Bitmap nextIcon;

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getNextIcon() {
        return nextIcon;
    }

    public void setNextIcon(Bitmap nextIcon) {
        this.nextIcon = nextIcon;
    }
}
