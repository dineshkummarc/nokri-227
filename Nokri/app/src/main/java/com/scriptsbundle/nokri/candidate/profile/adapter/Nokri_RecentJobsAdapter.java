package com.scriptsbundle.nokri.candidate.profile.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_RecentJobsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import java.util.List;

/**
 * Created by Glixen Technologies on 27/12/2017.
 */

public class Nokri_RecentJobsAdapter extends RecyclerView.Adapter<Nokri_RecentJobsAdapter.MyViewHolder> {
    private List<Nokri_RecentJobsModel> jobList;

    public interface OnItemClickListener {

        void onItemClick(Nokri_RecentJobsModel item);

    }
    private final OnItemClickListener listener;
    private Nokri_FontManager fontManager;
    private Context context;
    public Nokri_RecentJobsAdapter(List<Nokri_RecentJobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recent_jobs,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_RecentJobsModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.jobTypeTextView.setText(model.getJobType());
        holder.jobTitleTextView.setText(model.getJobTitle());
        holder.jobDescriptionTextView.setText(model.getJobDescription());
        holder.timeRemainingTextView.setText(model.getTimeRemaining());
        nokri_setParagraphFont(holder);
        holder.addressTextView.setText(model.getAddress());
        holder.salaryTextView.setText(model.getSalary());
        holder.paymentTextView.setText(model.getPaymentPeriod());
        holder.clockImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.clock));
        holder.locationImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.location_icon));
    }
    private void nokri_setParagraphFont(MyViewHolder holder){
        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitleTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobTypeTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobDescriptionTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.timeRemainingTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.salaryTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.paymentTextView,context.getAssets());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView jobTypeTextView,jobTitleTextView,jobDescriptionTextView,timeRemainingTextView,addressTextView,salaryTextView,paymentTextView;
        public ImageView clockImageView,locationImageView;
        public MyViewHolder(View itemView) {
            super(itemView);

        jobTypeTextView = itemView.findViewById(R.id.txt_job_type);
        Nokri_Utils.setRoundButtonColor(context,jobTypeTextView);
        jobTitleTextView = itemView.findViewById(R.id.txt_job_title);
        jobDescriptionTextView = itemView.findViewById(R.id.txt_job_description);
        timeRemainingTextView = itemView.findViewById(R.id.txt_time_remaining);
        addressTextView = itemView.findViewById(R.id.txt_address);
        salaryTextView = itemView.findViewById(R.id.txt_salary);
        salaryTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        paymentTextView = itemView.findViewById(R.id.txt_payment_period);
        clockImageView = itemView.findViewById(R.id.img_clock);
        locationImageView = itemView.findViewById(R.id.img_location);

        }

        public void nokri_bind(final Nokri_RecentJobsModel model, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
        }

    }
}
