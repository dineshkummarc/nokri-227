package com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import java.util.ArrayList;

public class SavedResumesAdapter extends RecyclerView.Adapter<SavedResumesAdapter.ViewHolder> {
    Context context;
    ArrayList<SavedResumeModel> arrayList;
    SavedResumeInterface myInterface;
    boolean calledFromMatched = false;
    Nokri_FontManager fontManager = new Nokri_FontManager();

    public SavedResumesAdapter(Context context, ArrayList<SavedResumeModel> arrayList, SavedResumeInterface myInterface) {
        this.context = context;
        this.arrayList = arrayList;
        this.myInterface = myInterface;
    }

    public void setCalledFromMatched(boolean calledFromMatched) {
        this.calledFromMatched = calledFromMatched;
    }

    @NonNull
    @Override
    public SavedResumesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.saved_resume_item, null);
        listItem.setLayoutParams(new RecyclerView.LayoutParams(
                ((RecyclerView) parent).getLayoutManager().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        SavedResumesAdapter.ViewHolder viewHolder = new SavedResumesAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull SavedResumesAdapter.ViewHolder viewHolder, int i) {
        SavedResumeModel model = arrayList.get(i);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.candidate_dp);
        requestOptions.error(R.drawable.candidate_dp);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(model.url).into(viewHolder.candidateImage);

        viewHolder.candidateName.setText(model.name);

        viewHolder.btnViewProfile.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        fontManager.nokri_setOpenSenseFontTextView(viewHolder.btnViewProfile,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(viewHolder.candidateName,context.getAssets());
        Nokri_Utils.setRoundButtonColor(context,viewHolder.btnViewProfile);
        Nokri_Utils.setRoundButtonColor(context,viewHolder.imageContainer);

        viewHolder.btnViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.onView(model.id);
            }
        });

        viewHolder.btnDeleteResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calledFromMatched)
                    if (!model.attachmentUrl.equals(""))
                        myInterface.onDownload(model.attachmentUrl);
                    else
                        myInterface.onDownload("");
                else{
                    myInterface.onDelete(model.id, viewHolder.getAdapterPosition());
                }
            }
        });
        viewHolder.btnDeleteResume.setPadding(5,5,5,5);
        if (calledFromMatched) {
            viewHolder.btnDeleteResume.setImageDrawable(context.getDrawable(R.drawable.donwload_icon));
        }else{
            Nokri_Utils.setRoundButtonColorRed(context,viewHolder.imageContainer);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView candidateImage;
        public TextView candidateName;
        public TextView btnViewProfile;
        public ImageView btnDeleteResume;
        public LinearLayout imageContainer;
        public ViewHolder(View itemView) {
            super(itemView);
            candidateImage = itemView.findViewById(R.id.img_logo);
            candidateName = itemView.findViewById(R.id.candidate_name);
            btnViewProfile = itemView.findViewById(R.id.view_profile);
            btnDeleteResume = itemView.findViewById(R.id.delete_resume);
            imageContainer = itemView.findViewById(R.id.imageContainer);
        }
    }
}
