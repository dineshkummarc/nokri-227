package com.scriptsbundle.nokri.candidate.profile.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_ProfileModel;

import java.util.List;

/**
 * Created by Glixen Technologies on 08/01/2018.
 */

public class Nokri_ProfileAdapter extends RecyclerView.Adapter<Nokri_ProfileAdapter.MyViewHolder>{
    private final OnItemClickListener listener;
    private List<Nokri_ProfileModel>jobList;
    private Context context;
    private Nokri_FontManager fontManager;
    public interface OnItemClickListener {

        void onItemClick(Nokri_ProfileModel item);

    }
    public Nokri_ProfileAdapter(List<Nokri_ProfileModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_activity_profile,parent,false);

        return new Nokri_ProfileAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_ProfileModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.titleTextView.setText(model.getTitle());
        holder.iconImageView.setImageBitmap(model.getIcon());
//        holder.nextImageView.setImageBitmap(model.getNextIcon());
        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setMonesrratSemiBioldFont(holder.titleTextView,context.getAssets());

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public ImageView iconImageView,nextImageView;
    public MyViewHolder(View itemView) {
        super(itemView);
        iconImageView = itemView.findViewById(R.id.img_icon);
        nextImageView = itemView.findViewById(R.id.img_next);
        titleTextView = itemView.findViewById(R.id.txt_title);
    }

        public void nokri_bind(final Nokri_ProfileModel model, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
        }
    }
}
