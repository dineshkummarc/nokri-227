package com.scriptsbundle.nokri.candidate.profile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.models.CandidateAvailabilityList;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import java.util.ArrayList;
import java.util.Calendar;

public class CandidateAvailabilityAdapter extends BaseAdapter {
    Context context;
    ArrayList<CandidateAvailabilityList.Day> days;
    String closedString;
    public CandidateAvailabilityAdapter(Context context, ArrayList<CandidateAvailabilityList.Day> days, String closedString){
        this.context = context;
        this.days = days;
        this.closedString = closedString;
    }

    @Override
    public int getCount() {
        return days.size();
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
    public View getView(int i, View v, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(R.layout.availability_day_item,null);
        TextView startTime = view.findViewById(R.id.startTime);
        TextView endTime = view.findViewById(R.id.endTime);
        TextView dash = view.findViewById(R.id.dash);
        TextView day = view.findViewById(R.id.day);
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String currentDay = "";
            Toast.makeText(context,String.valueOf(dayOfWeek), Toast.LENGTH_LONG);
        if (dayOfWeek==1){
            currentDay = "Sunday";
        }
        if (dayOfWeek==2){
            currentDay = "Monday";
        }
        if (dayOfWeek==3){
            currentDay = "Tuesday";
        }
        if (dayOfWeek==4){
            currentDay = "Wednesday";
        }
        if (dayOfWeek==5){
            currentDay = "Thursday";
        }
        if (dayOfWeek==6){
            currentDay = "Friday";
        }
        if (dayOfWeek==7){
            currentDay = "Saturday";
        }


        day.setText(days.get(i).getDay_name());
        startTime.setText(days.get(i).getStart_time());
        endTime.setText(days.get(i).getEnd_time());
        if (days.get(i).getClosed()){
            startTime.setText(closedString);
            endTime.setText("");
            dash.setText("");
            startTime.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            endTime.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        if (currentDay.equals(days.get(i).getDay_name())) {
            day.setTextColor(Color.parseColor("#48d17e"));
            if (!days.get(i).getClosed()) {
                startTime.setTextColor(Color.parseColor("#48d17e"));
                endTime.setTextColor(Color.parseColor("#48d17e"));
            }
        }

        return view;
    }
}
