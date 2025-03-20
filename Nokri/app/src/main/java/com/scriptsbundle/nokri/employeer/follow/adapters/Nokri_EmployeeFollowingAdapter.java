package com.scriptsbundle.nokri.employeer.follow.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scriptsbundle.nokri.employeer.follow.models.Nokri_FollowingModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;

import com.scriptsbundle.nokri.utils.Nokri_Config;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_EmployeeFollowingAdapter extends RecyclerView.Adapter<Nokri_EmployeeFollowingAdapter.MyViewHolder>{
    private List<Nokri_FollowingModel> jobList;
    private final Nokri_EmployeeFollowingAdapter.OnItemClickListener listener;
    private Nokri_FontManager fontManager;
    private Context context;
    public interface OnItemClickListener {

        void onItemClick(Nokri_FollowingModel item);
        void onRemoveClick(Nokri_FollowingModel item);
    }

    public Nokri_EmployeeFollowingAdapter(List<Nokri_FollowingModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_followings,parent,false);

        return new Nokri_EmployeeFollowingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_FollowingModel model = jobList.get(position);
        if(model.isHideUnfollowButton())
            holder.unfollowButton.setVisibility(View.GONE);
        holder.nokri_bind(model,listener);
        holder.companyNameTextView.setText(model.getCompanyName());
        holder.addressTextView.setText(model.getCompanyAddress());
        holder.unfollowButton.setText(model.getUnfollow());
        if (model.isFeatured()){
            holder.featuredView.setVisibility(View.VISIBLE);
        }else{
            holder.featuredView.setVisibility(View.GONE);
        }
       // holder.openPositionTextView.setText(model.getTotalPositons());
        if(!TextUtils.isEmpty(model.getCompanyLogo()))
        Picasso.get().load(model.getCompanyLogo()).fit().centerCrop().into(holder.companyLogoImageView);
        nokri_setParagraphFont(holder);
      
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {

        fontManager.nokri_setOpenSenseFontTextView(holder.companyNameTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
       // fontManager.nokri_setOpenSenseFontTextView(holder.openPositionTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.unfollowButton,context.getAssets());

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView companyNameTextView,addressTextView;//,openPositionTextView;
        public ImageView companyLogoImageView;
        public Button unfollowButton;
        View featuredView;
    public MyViewHolder(View itemView) {
        super(itemView);
    companyNameTextView = itemView.findViewById(R.id.txt_company_name);
    addressTextView = itemView.findViewById(R.id.txt_address);
    companyLogoImageView = itemView.findViewById(R.id.img_company_logo);
    unfollowButton = itemView.findViewById(R.id.btn_unfollow);
    unfollowButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
    featuredView = itemView.findViewById(R.id.featuredView);
   // openPositionTextView = itemView.findViewById(R.id.txt_open_position);

    }
        public void nokri_bind(final Nokri_FollowingModel model, final OnItemClickListener listener){
            unfollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRemoveClick(model);
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
