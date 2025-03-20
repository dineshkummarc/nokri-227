package com.scriptsbundle.nokri.guest.faq.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Glixen Technologies on 09/01/2018.
 */

public class Parent extends ExpandableGroup {
    public Parent(String title, List items) {
        super(title, items);
    }
}
