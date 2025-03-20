package com.scriptsbundle.nokri.candidate.resume.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.resume.model.Nokri_ResumeModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.RuntimePermissionHelper;

import com.victorminerva.widget.edittext.AutofitEdittext;

import java.util.List;

/**
 * Created by Glixen Technologies on 13/01/2018.
 */

public class Nokri_YourResumeAdapter extends RecyclerView.Adapter<Nokri_YourResumeAdapter.MyViewHolder>{
    public interface OnItemClickListener {

        void onItemClick(Nokri_ResumeModel item, int flag);

    }
    private final OnItemClickListener listener;
    private List<Nokri_ResumeModel> jobList;
    private Nokri_FontManager fontManager;
    RuntimePermissionHelper runtimePermissionHelper;
    private Context context;
    public Nokri_YourResumeAdapter(List<Nokri_ResumeModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_your_resume,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Nokri_ResumeModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.nameTextView.setText(model.getName());
        holder.srTextView.setText(model.getSrNum());
        holder.viewButton.setText(model.getButtonText());
        holder.editResumeImageView.setText(model.getDeleteButtonText());
        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setOpenSenseFontTextView(holder.nameTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.srTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.viewButton,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.editResumeImageView,context.getAssets());

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
    public TextView srTextView;
    public AutofitEdittext nameTextView;
    public Button viewButton;
    public Button editResumeImageView;
        public MyViewHolder(View itemView) {
        super(itemView);

        srTextView = itemView.findViewById(R.id.txt_sr);
        nameTextView = itemView.findViewById(R.id.txt_name);
        viewButton = itemView.findViewById(R.id.btn_view);
        editResumeImageView = itemView.findViewById(R.id.img_button_edit);
        }

        public void nokri_bind(final Nokri_ResumeModel model, final OnItemClickListener listener) {
            viewButton.setOnClickListener(view -> {
                listener.onItemClick(model,0);

            });
            editResumeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model,1);
                }
            });
        }
    }
}
