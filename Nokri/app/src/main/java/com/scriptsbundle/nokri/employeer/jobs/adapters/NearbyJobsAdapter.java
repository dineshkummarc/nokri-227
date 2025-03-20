package com.scriptsbundle.nokri.employeer.jobs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.models.NearbyJobModel;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NearbyJobsAdapter extends BaseAdapter {
    Context context;
    ArrayList<NearbyJobModel> nearbyJobs;

    public NearbyJobsAdapter(Context context, ArrayList<NearbyJobModel> nearbyJobs) {
        this.context = context;
        this.nearbyJobs = nearbyJobs;
    }

    @Override
    public int getCount() {
        return nearbyJobs.size();
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
        View v = LayoutInflater.from(context).inflate(R.layout.nearboy_job_item,null);
        NearbyJobModel model = nearbyJobs.get(i);
        CircularImageView companyImage = v.findViewById(R.id.companyImage);
        TextView companyName = v.findViewById(R.id.companyName);
        TextView jobName = v.findViewById(R.id.jobName);
        TextView radius = v.findViewById(R.id.radius);

        Picasso.get().load(model.comp_img).into(companyImage);
        companyName.setText(model.comp_name);
        jobName.setText(model.job_title);
        radius.setText(model.distance);

        return v;
    }
}
