package com.scriptsbundle.nokri.employeer.jobs.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Glixen Technologies on 01/03/2018.
 */

public class Nokri_SpinnerModel implements Serializable {
    private ArrayList<String>names;
    private ArrayList<String>ids;
    private ArrayList<Boolean>hasChild;
    private String id;
    private String name;
    private String value;
    private boolean selected;
    private boolean isHasChild;

    public boolean isHasChild() {
        return isHasChild;
    }

    public void setHasChild(boolean hasChild) {
        isHasChild = hasChild;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Nokri_SpinnerModel() {

        names = new ArrayList<>();
        ids = new ArrayList<>();
        hasChild = new ArrayList<>();

    }

    public ArrayList<Boolean> getHasChild() {
        return hasChild;
    }

    public void setHasChild(ArrayList<Boolean> hasChild) {
        this.hasChild = hasChild;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void addNames(String name){
       names.add(name);
    }
    public void addIds(String id){
        ids.add(id);
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
}
