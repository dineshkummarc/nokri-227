package com.scriptsbundle.nokri.employeer.jobs.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    ArrayList<Nokri_SpinnerModel> list;
    Context context;

    public SpinnerAdapter(Context context,ArrayList<Nokri_SpinnerModel> list){
        this.context = context;

    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
