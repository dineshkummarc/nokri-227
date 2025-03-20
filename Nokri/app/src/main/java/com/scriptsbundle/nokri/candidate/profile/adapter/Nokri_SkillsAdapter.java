package com.scriptsbundle.nokri.candidate.profile.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_ProgressModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.List;

public class Nokri_SkillsAdapter  extends RecyclerView.Adapter<Nokri_SkillsAdapter.MyViewHolder>{

    private List<Nokri_ProgressModel> jobList;

    private Context context;
    private Nokri_FontManager fontManager;

    public Nokri_SkillsAdapter(List<Nokri_ProgressModel> jobList, Context context) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_skills,parent,false);

        return new Nokri_SkillsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Nokri_ProgressModel model = jobList.get(position);

            holder.title.setText(model.getTitle());

        holder.signSeekBar.getConfigBuilder()
                .progress(model.getProgress())
                .sectionCount(4)
                .trackColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .secondTrackColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .thumbColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .sectionTextColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .thumbTextColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .signBorderColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .autoAdjustSectionMark()
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();
        holder.signSeekBar.setEnabled(false);




    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }
    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setMonesrratSemiBioldFont(holder.title,context.getAssets());

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public SignSeekBar signSeekBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_skill);
            signSeekBar = itemView.findViewById(R.id.seek_bar);

        }
    }

}
