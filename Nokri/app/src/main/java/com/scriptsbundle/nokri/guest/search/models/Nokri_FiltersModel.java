package com.scriptsbundle.nokri.guest.search.models;

import java.util.ArrayList;

/**
 * Created by Glixen Technologies on 08/01/2018.
 */

public class Nokri_FiltersModel {
    private String title;
    private ArrayList<String> names;
    private ArrayList<String>ids;



    public Nokri_FiltersModel() {

        names = new ArrayList<>();
        ids = new ArrayList<>();
    }


    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
