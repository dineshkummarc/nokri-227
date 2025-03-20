package com.scriptsbundle.nokri.candidate.jobs.adapters;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.jobs.models.CandidateNotificationModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeInterface;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;

import java.util.ArrayList;

public class CandidateNotificationAdapter extends RecyclerView.Adapter<CandidateNotificationAdapter.ViewHolder> {
    Context context;
    ArrayList<CandidateNotificationModel> arrayList;
    SavedResumeInterface myInterface;
    Nokri_FontManager fontManager = new Nokri_FontManager();

    public CandidateNotificationAdapter(Context context, ArrayList<CandidateNotificationModel> arrayList, SavedResumeInterface myInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.myInterface = myInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.item_candidate_notification, null);
        listItem.setLayoutParams(new RecyclerView.LayoutParams(
                ((RecyclerView) parent).getLayoutManager().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int i) {
        CandidateNotificationModel model = arrayList.get(i);


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.candidate_dp);
        requestOptions.error(R.drawable.candidate_dp);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(model.getImageUrl()).into(v.img_logo);
        v.postingTime.setText(model.getPostingTime());
        v.companyName.setText(model.getCompanyName() + " ");
        v.post.setText(model.getPost());
        v.jobTitle.setText(model.getJobTitle());
        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setOpenSenseFontTextView(v.postingTime, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.companyName, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.post, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.jobTitle, context.getAssets());
//        v.jobExpiryLabel.setText(model.expiryLabel);
//        v.jobExpiryValue.setText(model.expiryValue);
//        v.jobPostDateLabel.setText(model.jobPostDateLabel);
//        v.jobPostDateValue.setText(model.jobPostDateValue);
//        v.jobName.setText(model.name);
//        v.viewResume.setText(model.viewResumeLabel);
//        v.jobStatus.setText(model.jobStatus);
//        v.viewResume.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        v.jobExpiryValue.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        v.jobPostDateValue.setTextColor(Color.parseColor("#76c7f8"));
//        v.jobStatus.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        v.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.onView(model.getJobId());
            }
        });
//
//        fontManager.nokri_setOpenSenseFontTextView(v.jobExpiryLabel,context.getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(v.jobExpiryValue,context.getAssets());
//        fontManager.nokri_setOpenSenseFontTextView( v.jobPostDateLabel,context.getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(v.jobPostDateValue,context.getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(v.jobName,context.getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(v.jobStatus,context.getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(v.viewResume,context.getAssets());
//        Nokri_Utils.setRoundButtonColor(context,v.viewResume);
//        viewHolder.candidateName.setText(model.name);
//
//        viewHolder.btnDeleteResume.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        viewHolder.btnViewProfile.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        viewHolder.btnViewProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myInterface.onView(model.id);
//            }
//        });
//
//        viewHolder.btnDeleteResume.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myInterface.onDelete(model.id,viewHolder.getAdapterPosition());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView companyName;
        public TextView post;
        public TextView jobTitle;
        public TextView postingTime;
        CircularImageView img_logo;

        public ViewHolder(View itemView) {
            super(itemView);
            img_logo = itemView.findViewById(R.id.img_logo);
            companyName = itemView.findViewById(R.id.employer_name);
            post = itemView.findViewById(R.id.post);
            jobTitle = itemView.findViewById(R.id.job_name);
            postingTime = itemView.findViewById(R.id.postingTime);
//            jobPostDateValue = itemView.findViewById(R.id.jobPostDateValue);
//            viewResume = itemView.findViewById(R.id.viewResume);

        }
    }
}
