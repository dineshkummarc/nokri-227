package com.scriptsbundle.nokri.employeer.jobs.models;

/**
 * Created by Glixen Technologies on 26/12/2017.
 */

public class Nokri_DescriptionModel {
    private String title;
    private String description;
    private String fieldTypeName = "";

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public Nokri_DescriptionModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Nokri_DescriptionModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
