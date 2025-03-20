package com.scriptsbundle.nokri.candidate.follow.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.candidate.follow.models.Nokri_FollowingModel;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_FollowingAdapter extends RecyclerView.Adapter<Nokri_FollowingAdapter.MyViewHolder>{
    private List<Nokri_FollowingModel> jobList;
    private final Nokri_FollowingAdapter.OnItemClickListener listener;
    private Nokri_FontManager fontManager;
    private Context context;
    public interface OnItemClickListener {

        void onItemClick(Nokri_FollowingModel item);
        void onDeleteClick(Nokri_FollowingModel item);
    }

    public Nokri_FollowingAdapter(List<Nokri_FollowingModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_followings,parent,false);

        return new Nokri_FollowingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_FollowingModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.companyNameTextView.setText(model.getCompanyName());
        holder.addressTextView.setText(model.getCompanyAddress());
        holder.unfollowButton.setText(model.getUnfollow());
        holder.openPositionTextView.setText(model.getTotalPositons());
        if(!TextUtils.isEmpty(model.getCompanyLogo()))
        Picasso.get().load(model.getCompanyLogo()).fit().centerCrop().into(holder.companyLogoImageView);
        nokri_setParagraphFont(holder);
      
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {

        fontManager.nokri_setOpenSenseFontTextView(holder.companyNameTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.openPositionTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.unfollowButton,context.getAssets());

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView companyNameTextView,addressTextView,openPositionTextView;
        public CircularImageView companyLogoImageView;
        public Button unfollowButton;
    public MyViewHolder(View itemView) {
        super(itemView);
    companyNameTextView = itemView.findViewById(R.id.txt_company_name);
    addressTextView = itemView.findViewById(R.id.txt_address);
    companyLogoImageView = itemView.findViewById(R.id.img_company_logo);
    unfollowButton = itemView.findViewById(R.id.btn_unfollow);
    unfollowButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
    openPositionTextView = itemView.findViewById(R.id.txt_open_position);

    }
        public void nokri_bind(final Nokri_FollowingModel model, final OnItemClickListener listener){
            unfollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteClick(model);
                }
            });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });
    }

    }
}
