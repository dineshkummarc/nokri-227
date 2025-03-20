package com.scriptsbundle.nokri.candidate.profile.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_PortfolioModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Glixen Technologies on 17/01/2018.
 */

public class Nokri_PortfolioAdapter extends RecyclerView.Adapter<Nokri_PortfolioAdapter.MyViewHolder> {


    public interface OnItemClickListener {

        void onItemClick(Nokri_PortfolioModel item, int position);

    }

    private Context context;
    private List<Nokri_PortfolioModel>jobList;
    private final OnItemClickListener listener;



    public Nokri_PortfolioAdapter(List<Nokri_PortfolioModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;

        this.context = context;
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_portfoltio,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_PortfolioModel model = jobList.get(position);
        holder.nokri_bind(model,listener,position);
        if(!TextUtils.isEmpty(model.getUrl()))
        Picasso.get().load(model.getUrl()).fit().centerCrop().into(holder.portfolioImageView);
       // holder.portfolioImageView.setImageResource(R.drawable.person);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView portfolioImageView;
    public MyViewHolder(View itemView) {
        super(itemView);
    portfolioImageView = itemView.findViewById(R.id.img_portfolio);
    }

        public void nokri_bind(final Nokri_PortfolioModel model, final OnItemClickListener listener, final int position) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model,position);
                }
            });
    }
    }
}
