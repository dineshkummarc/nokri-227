package com.scriptsbundle.nokri.guest.home.adapters;

import android.content.Context;
import android.graphics.Point;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.scriptsbundle.nokri.guest.home.models.Nokri_SelectJobsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.AnimationUtils;

import com.squareup.picasso.Picasso;
import com.victorminerva.widget.edittext.AutofitEdittext;

import java.util.List;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_SelectJobsAdapter extends RecyclerView.Adapter<Nokri_SelectJobsAdapter.MyViewHolder>{

    public interface OnItemClickListener {

        void onItemClick(Nokri_SelectJobsModel item);

    }
    private List<Nokri_SelectJobsModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;
    private OnItemClickListener listener;


    public Nokri_SelectJobsAdapter(List<Nokri_SelectJobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }

    public int getScreenWidth() {

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home_screen_horizontol,parent,false);
itemView.getLayoutParams().width = (int) (getScreenWidth() / 3);
        return new MyViewHolder(itemView);
    }
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_SelectJobsModel model = jobList.get(position);
        holder.nokri_bind(model,listener);

//        setFadeAnimation(holder.itemView);
        AnimationUtils.slideUp(holder.itemView);
        holder.jobTitle.setText(model.getJobTitle());
        holder.jobsInclude.setText(model.getJobsInclude());
        nokri_setParagraphFont(holder);
        if(!TextUtils.isEmpty(model.getLogo()))
        Picasso.get().load(model.getLogo()).into(holder.companyLogo);


    }
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
//    private void setScaleAnimation(View view) {
//        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        anim.setDuration(FADE_DURATION);
//        view.startAnimation(anim);
//    }
    private void nokri_setParagraphFont(MyViewHolder holder) {

        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitle,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobsInclude,context.getAssets());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView companyLogo;
        public AutofitEdittext jobTitle;
        public TextView jobsInclude;
    public MyViewHolder(View itemView) {
        super(itemView);
        companyLogo = itemView.findViewById(R.id.img_job_logo);
        jobTitle = itemView.findViewById(R.id.txt_job_title);
        jobsInclude = itemView.findViewById(R.id.txt_jobs_include);

    }

        public void nokri_bind(final Nokri_SelectJobsModel model, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
        }
}
}
