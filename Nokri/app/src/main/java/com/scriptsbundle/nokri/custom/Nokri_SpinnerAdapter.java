package com.scriptsbundle.nokri.custom;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.R;

import java.util.List;

/**
 * Created by Glixen Technologies on 22/12/2017.
 */

public class Nokri_SpinnerAdapter extends ArrayAdapter<String> {
    private Nokri_FontManager fontManager;
    private boolean disableFirstIndex = false;
    private boolean shouldTextColorBeWhite;

    public Nokri_SpinnerAdapter(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        fontManager =  new Nokri_FontManager();
        }
    public Nokri_SpinnerAdapter(@NonNull Context context, int resource, List<String> objects, boolean disableFirstIndex) {
        super(context, resource, objects);
        fontManager =  new Nokri_FontManager();
        this.disableFirstIndex = disableFirstIndex;
    }


    public void setTextColorWhire(Boolean shouldTextColorBeWhite){this.shouldTextColorBeWhite = shouldTextColorBeWhite;}

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        if(shouldTextColorBeWhite)
            view.setTextColor(getContext().getResources().getColor(R.color.white));
        fontManager.nokri_setOpenSenseFontTextView(view,getContext().getAssets());
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        fontManager.nokri_setOpenSenseFontTextView(view,getContext().getAssets());
        return view;
    }

    @Override
    public boolean isEnabled(int position) {

    if(disableFirstIndex) {
        if (position == 0)
            return false;
        else return true;
    }
    else
        return true;
           }
}
