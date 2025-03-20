package com.scriptsbundle.nokri.employeer.jobs.models;

public class Nokri_PostJobsPackagesModel {

    private String id;
    private String title;
    private String quantity;
    private String remainign;
    private boolean isChecked;
    private boolean isEditable = true;

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRemainign() {
        return remainign;
    }

    public void setRemainign(String remainign) {
        this.remainign = remainign;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
