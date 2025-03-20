package com.scriptsbundle.nokri.employeer.jobs.models;

import java.util.ArrayList;

public class ModelsPostJobStep2 {


    public static class SpinnerArrayListStorageModel{
        public String id;
        public boolean isRequired;
        public ArrayList<Nokri_SpinnerModel> spinnerList = new ArrayList<Nokri_SpinnerModel>();
    }


    public static class EdittextStorageModel{
        public String id;
        public boolean isRequired;
        public String value;
        public String selected;
        public String name;
    }

    public static class CheckBoxModel{
        public String id;
        public boolean isRequired;
        public String value;
        public String selected;
        public String name;
    }
}
