package com.scriptsbundle.nokri.guest.blog.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.blog.models.Nokri_BlogGridModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.AnimationUtils;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Glixen Technologies on 08/01/2018.
 */

public class Nokri_BlogGridAdapter extends RecyclerView.Adapter<Nokri_BlogGridAdapter.MyViewHolder>{
    public interface OnItemClickListener {

        void onItemClick(Nokri_BlogGridModel item);

    }
    private final OnItemClickListener listener;
    private Context context;
    private List<Nokri_BlogGridModel>jobList;
    private Nokri_FontManager fontManager;

    private boolean isMultiLine = false;

    public boolean isMultiLine() {
        return isMultiLine;
    }

    public void setMultiLine(boolean multiLine) {
        isMultiLine = multiLine;
    }

    public Nokri_BlogGridAdapter(List<Nokri_BlogGridModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if(isMultiLine)
             itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_blog_grid_multi,parent,false);
        else
         itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_blog_grid,parent,false);

        return new Nokri_BlogGridAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_BlogGridModel model =  jobList.get(position);

        holder.nokri_bind(model,listener);
        holder.headingTextView.setText(model.getHeadingText());
        if(model.isHasImage()) {
            if(!TextUtils.isEmpty(model.getHeaderImage()))
            Picasso.get().load(model.getHeaderImage()).fit().centerCrop().into(holder.headerImageView);
        }
        else
            holder.headerImageView.setVisibility(View.GONE);
        if(model.isHtmlResponse()) {
            holder.webView.setScrollContainer(false);
            holder.webView.getSettings().setJavaScriptEnabled(true);
            holder.webView.loadDataWithBaseURL(null, Nokri_Utils.getBoldBlockQuote(model.getParagraphText()), "text/html", "utf-8", null);
//            holder.paragraphTextView.setVisibility(View.GONE);
        }else{
//        holder.paragraphTextView.setText(model.getParagraphText());
            holder.webView.setVisibility(View.GONE);}
        holder.commentTextView.setText(model.getCommentsText());
        holder.dateTextView.setText(model.getDateText());
        nokri_setParagraphFont(holder);
        AnimationUtils.slideDown(holder.itemView);

    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private void nokri_setParagraphFont(MyViewHolder holder) {

        fontManager.nokri_setMonesrratSemiBioldFont(holder.headingTextView,context.getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(holder.paragraphTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.commentTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.dateTextView,context.getAssets());


    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView headerImageView;
        public TextView dateTextView,commentTextView,headingTextView;
        public WebView webView;
    public MyViewHolder(View itemView) {
        super(itemView);

        headerImageView = itemView.findViewById(R.id.img_header);
        dateTextView = itemView.findViewById(R.id.txt_date);
        commentTextView = itemView.findViewById(R.id.txt_comments);
        headingTextView = itemView.findViewById(R.id.txt_heading);
//        paragraphTextView = itemView.findViewById(R.id.txt_paragraph);
        webView = itemView.findViewById(R.id.webview);
    }

        public void nokri_bind(final Nokri_BlogGridModel model, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
        }
    }
}
