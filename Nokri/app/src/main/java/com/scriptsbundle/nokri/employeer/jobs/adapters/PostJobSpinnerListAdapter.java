package com.scriptsbundle.nokri.employeer.jobs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;

import java.util.ArrayList;
import java.util.Locale;

public class PostJobSpinnerListAdapter extends BaseAdapter {

    ArrayList<Nokri_SpinnerModel> list;
    Context context;
    ArrayList<Nokri_SpinnerModel> stat = new ArrayList<>();
    public PostJobSpinnerListAdapter(ArrayList<Nokri_SpinnerModel> list, Context context){
        this.list = list;
        this.context = context;

        stat.addAll(list);
    }

    @Override
    public int getCount() {
        return list.size();
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

        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_spinner_list_item,null);
        TextView text = view.findViewById(R.id.text);
        text.setText(list.get(position).getName());
        return view;
    }

    public void filter(String charText ) {
        // TODO Auto-generated method stub
        charText = charText.toLowerCase(Locale.getDefault());
        //charText = charText.replace(" ", "");
        list.clear();
        if (charText.length() == 0) {
            list.addAll(stat);
        }
        else {

            for (Nokri_SpinnerModel a : stat){

                if (a.getName().toLowerCase(Locale.getDefault()).contains(charText)
                        || a.getName().toLowerCase(Locale.getDefault()).contains(charText)){

                    list.add(a);
                }
            }
        }
        notifyDataSetChanged();
    }
}
