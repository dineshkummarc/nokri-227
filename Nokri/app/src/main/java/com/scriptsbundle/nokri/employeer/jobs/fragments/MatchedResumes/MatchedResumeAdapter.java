package com.scriptsbundle.nokri.employeer.jobs.fragments.MatchedResumes;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeInterface;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import java.util.ArrayList;

public class MatchedResumeAdapter extends RecyclerView.Adapter<MatchedResumeAdapter.ViewHolder>{
    Context context;
    ArrayList<MatchedResumeModel> arrayList;
    SavedResumeInterface myInterface;
    Nokri_FontManager fontManager = new Nokri_FontManager();
    public MatchedResumeAdapter(Context context,ArrayList<MatchedResumeModel> arrayList,SavedResumeInterface myInterface){
        this.context = context;
        this.arrayList = arrayList;
        this.myInterface = myInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.item_matched_resume, null);
        listItem.setLayoutParams(new RecyclerView.LayoutParams(
                ((RecyclerView) parent).getLayoutManager().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int i) {
        MatchedResumeModel model = arrayList.get(i);
        v.jobExpiryLabel.setText(model.expiryLabel);
        v.jobExpiryValue.setText(model.expiryValue);
        v.jobPostDateLabel.setText(model.jobPostDateLabel);
        v.jobPostDateValue.setText(model.jobPostDateValue);
        v.jobName.setText(model.name);
        v.viewResume.setText(model.viewResumeLabel);
        v.jobStatus.setText(model.jobStatus);
        v.viewResume.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        v.jobExpiryValue.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        v.jobPostDateValue.setTextColor(Color.parseColor("#76c7f8"));
        v.jobStatus.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        v.viewResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.onView(model.id);
            }
        });

        fontManager.nokri_setOpenSenseFontTextView(v.jobExpiryLabel,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.jobExpiryValue,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView( v.jobPostDateLabel,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.jobPostDateValue,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.jobName,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.jobStatus,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(v.viewResume,context.getAssets());
        Nokri_Utils.setRoundButtonColor(context,v.viewResume);
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
        public TextView jobStatus;
        public TextView jobName;
        public TextView jobExpiryLabel;
        public TextView jobExpiryValue;
        public TextView jobPostDateLabel;
        public TextView jobPostDateValue;
        public TextView viewResume;
        public ViewHolder(View itemView) {
            super(itemView);
            jobStatus = itemView.findViewById(R.id.jobStatus);
            jobName = itemView.findViewById(R.id.name);
            jobExpiryLabel = itemView.findViewById(R.id.jobExpiryLabel);
            jobExpiryValue = itemView.findViewById(R.id.jobExpiryValue);
            jobPostDateLabel = itemView.findViewById(R.id.jobPostDateLabel);
            jobPostDateValue = itemView.findViewById(R.id.jobPostDateValue);
            viewResume = itemView.findViewById(R.id.viewResume);

        }
    }
}
