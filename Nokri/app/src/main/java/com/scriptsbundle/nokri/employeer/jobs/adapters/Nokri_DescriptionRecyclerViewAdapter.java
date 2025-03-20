package com.scriptsbundle.nokri.employeer.jobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_DescriptionModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Globals;

import java.util.List;

/**
 * Created by Glixen Technologies on 26/12/2017.
 */

public class Nokri_DescriptionRecyclerViewAdapter extends RecyclerView.Adapter<Nokri_DescriptionRecyclerViewAdapter.MyViewHolder> {


    public interface OnItemClickListener {

        void onItemClick(Nokri_DescriptionModel item);

    }
    private static int count =0;
    private List<Nokri_DescriptionModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;
    private OnItemClickListener listener;

    private int type;
    public Nokri_DescriptionRecyclerViewAdapter(List<Nokri_DescriptionModel> jobList, Context context , int type) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.type = type;
    }

    public Nokri_DescriptionRecyclerViewAdapter(List<Nokri_DescriptionModel> jobList, Context context , int type, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.type = type;
        this.type = type;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = null;
        if(type==0) {
           itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_descrptions, parent, false);


        } else if(type ==1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_personal_info, parent, false);
        }
        else if(type ==3) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_descriptions_no_space, parent, false);
        }
        return new Nokri_DescriptionRecyclerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Nokri_DescriptionModel model = jobList.get(position);

        if(model.getDescription().equals("null") || model.getDescription().trim().isEmpty()){
            holder.itemView.setVisibility(View.GONE);
            holder.descriptionTextView.setVisibility(View.GONE);
            holder.itemView.getLayoutParams().height = 0;
            holder.titleTextView.setVisibility(View.GONE);
            return;
        }

        holder.titleTextView.setText(model.getTitle());
        if(model.getDescription().isEmpty() || model.getDescription() == null)
        {
            holder.descriptionTextView.setText(Nokri_Globals.EDIT_MESSAGE);
            holder.descriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                listener.onItemClick(model);
                }
            });
        }
        else
        holder.descriptionTextView.setText(model.getDescription().trim());
        holder.descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getFieldTypeName().equals("emp_web")){
                    if (!model.getDescription().startsWith("http://") && !model.getDescription().startsWith("https://"))
                        model.setDescription("http://" + model.getDescription());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getDescription()));
                    context.startActivity(browserIntent);
                }
            }
        });
//        AnimationUtils.slideUp(holder.itemView);
        nokri_setParagraphFont(holder);

    }
    private void nokri_setParagraphFont(Nokri_DescriptionRecyclerViewAdapter.MyViewHolder holder){
       if(type == 3)
           fontManager.nokri_setMonesrratSemiBioldFont(holder.titleTextView,context.getAssets());
       else
        fontManager.nokri_setOpenSenseFontTextView(holder.titleTextView,context.getAssets());

        fontManager.nokri_setOpenSenseFontTextView(holder.descriptionTextView,context.getAssets());

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView,descriptionTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.txt_title);
            descriptionTextView = itemView.findViewById(R.id.txt_descroption);

            if(type==0 || type == 3)
        {
            ++count;

        if(count == jobList.size())
        {
            itemView.findViewById(R.id.line1).setVisibility(View.GONE);
            count=0;
        }
        }


        }
        /*public void nokri_bind(final Nokri_DescriptionModel model,final Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
        }*/
}
    public void removeAt(int position) {
        jobList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, jobList.size());
    }
}
