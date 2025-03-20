package com.scriptsbundle.nokri.candidate.profile.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scriptsbundle.nokri.candidate.profile.model.Nokri_MyProfileModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;

import java.util.List;

/**
 * Created by Glixen Technologies on 08/01/2018.
 */

public class Nokri_MyProfileAdapter extends RecyclerView.Adapter<Nokri_MyProfileAdapter.MyViewHolder>{
    private List<Nokri_MyProfileModel> jobList;

    private Context context;
    private Nokri_FontManager fontManager;
    private Boolean isEducationDetail = false;
    public interface OnItemClickListener {

        void onItemClick(Nokri_MyProfileModel item);

    }
    public Nokri_MyProfileAdapter(List<Nokri_MyProfileModel> jobList, Context context) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;

    }

    public Nokri_MyProfileAdapter(List<Nokri_MyProfileModel> jobList, Context context, boolean isEducationDetail) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.isEducationDetail = isEducationDetail;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_info,parent,false);

        return new Nokri_MyProfileAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            Nokri_MyProfileModel model = jobList.get(position);

            holder.titleTextView.setText(model.getTitleText().trim());
            holder.dateTextView.setText(model.getDateText().trim());
            holder.paragraphTextView.setText(model.getParagraphText().trim());
           // holder.degreeTitleTextView.setText(model.getDegreeTitle().trim());


        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(model.getDegreeTitle().trim());
        builder.append("\n");
        SpannableString spannableString = new SpannableString(model.getDateText());
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.edit_profile_grey)), 0, model.getDateText().length(), 0);
        builder.append(spannableString);
        holder.degreeTitleTextView.setText(builder);

        if(isEducationDetail){
                holder.degreePercentageTextView.setText(model.getDegreePercentage());
                holder.degreePercentageTitleTextView.setText(model.getPercentageTitle());
                holder.degreeGradeTextView.setText(model.getDegreeGrade());
                holder.degreeGradeTitleTextView.setText(model.getGradeTitle());
            }

        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
    fontManager.nokri_setMonesrratSemiBioldFont(holder.titleTextView,context.getAssets());
    fontManager.nokri_setOpenSenseFontTextView(holder.dateTextView,context.getAssets());
    fontManager.nokri_setOpenSenseFontTextView(holder.paragraphTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.degreeTitleTextView,context.getAssets());


            if(isEducationDetail){





                fontManager.nokri_setOpenSenseFontTextView( holder.degreePercentageTextView,context.getAssets());
                fontManager.nokri_setOpenSenseFontTextView( holder.degreePercentageTitleTextView,context.getAssets());
                fontManager.nokri_setOpenSenseFontTextView(holder.degreeGradeTextView,context.getAssets());
                fontManager.nokri_setOpenSenseFontTextView(holder.degreeGradeTitleTextView,context.getAssets());
            }

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView,dateTextView,paragraphTextView,degreeTitleTextView;
        public TextView degreePercentageTextView,degreePercentageTitleTextView,degreeGradeTextView,degreeGradeTitleTextView;
        public LinearLayout gradeContainer,pecentageConteiner;
        public MyViewHolder(View itemView) {
        super(itemView);
    titleTextView  = itemView.findViewById(R.id.txt_title);
    dateTextView = itemView.findViewById(R.id.txt_date);
    paragraphTextView = itemView.findViewById(R.id.txt_paragraph);
    degreeTitleTextView = itemView.findViewById(R.id.txt_degree_title);
    if(isEducationDetail)
    {

        gradeContainer = itemView.findViewById(R.id.grade_container);
        pecentageConteiner = itemView.findViewById(R.id.percentage_container);
        gradeContainer.setVisibility(View.VISIBLE);
        pecentageConteiner.setVisibility(View.VISIBLE);
        degreePercentageTitleTextView = itemView.findViewById(R.id.txt_percentage_title);
        degreePercentageTextView = itemView.findViewById(R.id.txt_percentage);
        degreeGradeTitleTextView = itemView.findViewById(R.id.txt_grade_title);
        degreeGradeTextView = itemView.findViewById(R.id.txt_grade);
    }


    }


    }
}
