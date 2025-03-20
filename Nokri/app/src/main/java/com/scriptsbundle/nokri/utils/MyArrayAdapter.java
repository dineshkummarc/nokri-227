package com.scriptsbundle.nokri.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<String> {

    Typeface myFont;
    public MyArrayAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId,items);
        myFont = Typeface.createFromAsset(context.getAssets(), "OpenSans.ttf");
    }

    public TextView getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(myFont);
        return v;
    }

    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(myFont);
        return v;
    }

}