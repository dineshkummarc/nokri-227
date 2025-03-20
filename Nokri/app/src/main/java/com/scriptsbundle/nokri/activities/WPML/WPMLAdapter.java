package com.scriptsbundle.nokri.activities.WPML;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WPMLAdapter extends BaseAdapter {
    Context context;
    ArrayList<LangArray> langArray;
    public WPMLAdapter(Context context, ArrayList<LangArray> langArray){
        this.langArray = langArray;
        this.context = context;
    }

    @Override
    public int getCount() {
        return langArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.wpml_list_item,null);
        ImageView flag = view.findViewById(R.id.flag);
        TextView languageName = view.findViewById(R.id.languageName);
        languageName.setText(langArray.get(position).nativeName);
        Picasso.get().load(langArray.get(position).flagUrl).into(flag);
        return view;
    }
}
