package com.scriptsbundle.nokri;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.models.AttachmentModel;
import com.scriptsbundle.nokri.employeer.jobs.models.ImageOnClickListener;

import com.wonshinhyo.dragrecyclerview.DragAdapter;
import com.wonshinhyo.dragrecyclerview.DragHolder;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;

import java.util.ArrayList;

public class ItemEditImageAdapter extends DragAdapter {

    private ArrayList<AttachmentModel> list;
    ImageOnClickListener imageOnClickListener;
    private Context mContext;


    public ItemEditImageAdapter(Context context, ArrayList<AttachmentModel> data) {
        super(context, data);
        this.list = data;
        this.mContext = context;
    }


    @Override
    public DragRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_grid_image, parent, false), viewType);
    }


    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final DragRecyclerView.ViewHolder hol, @SuppressLint("RecyclerView") final int position) {
        MyViewHolder holder = (MyViewHolder) hol;
        if (getExtension(list.get(position).url).equalsIgnoreCase("pdf")){
            holder.mainImage.setImageDrawable(mContext.getDrawable(R.drawable.pdf));
        }else if (getExtension(list.get(position).url).equalsIgnoreCase("doc")){
            holder.mainImage.setImageDrawable(mContext.getDrawable(R.drawable.doc));
        }else if (getExtension(list.get(position).url).equalsIgnoreCase("docx")){
            holder.mainImage.setImageDrawable(mContext.getDrawable(R.drawable.docx));
        }else{
            Glide.with(mContext).load(list.get(position).url).into(holder.mainImage);
        }
        holder.delAd.setTag(list.get(position).attachmentId);

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOnClickListener.delViewOnClick(v, position);
            }
        };


        holder.delAd.setOnClickListener(listener2);



    }


    public void setOnItemClickListener(ImageOnClickListener onItemClickListener) {
        this.imageOnClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends DragHolder {

        ImageView mainImage, delAd;
        RelativeLayout relativeLayout;

        MyViewHolder(View v, int viewtype) {
            super(v);

            relativeLayout = v.findViewById(R.id.linear_layout_card_view);
            delAd = v.findViewById(R.id.delAdd);
            mainImage = v.findViewById(R.id.imageView);
        }

    }


    public String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }
}