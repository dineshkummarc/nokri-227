package com.scriptsbundle.nokri.guest.blog.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;

import com.scriptsbundle.nokri.guest.blog.models.Nokri_CommentsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Glixen Technologies on 09/01/2018.
 */

public class Nokri_BlogDetailAdapter extends RecyclerView.Adapter<Nokri_BlogDetailAdapter.MyViewHolder>{
    public interface OnItemClickListener {

        void onItemClick(Nokri_CommentsModel item);

    }
    private final OnItemClickListener listener;
    private Context context;
    private List<Nokri_CommentsModel>jobList;
    private Nokri_FontManager fontManager;

    public Nokri_BlogDetailAdapter(List<Nokri_CommentsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comments,parent,false);

        return new Nokri_BlogDetailAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_CommentsModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        if(model.isReply()){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(30, 0, 0, 0);
            holder.cardView.requestLayout();

        }
        else {
            holder.replyButton.setVisibility(View.VISIBLE);
            holder.replyButton.setText(model.getReplyButtonText());
        }
        holder.nameTextView.setText(model.getNameText());
        if(!TextUtils.isEmpty(model.getProfilImage()))
        Picasso.get().load(model.getProfilImage()).fit().centerInside().into(holder.profileImageView);

        holder.expTv1.setText(model.getCommentText());
        holder.dateTextView.setText(model.getDateText());
        nokri_setParagraphFont(holder);

    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
    fontManager.nokri_setMonesrratSemiBioldFont(holder.nameTextView,context.getAssets());
    fontManager.nokri_setOpenSenseFontTextView(holder.dateTextView,context.getAssets());

        fontManager.nokri_setOpenSenseFontButton(holder.replyButton,context.getAssets());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
         public CircularImageView profileImageView;
        public TextView nameTextView,dateTextView;


        public com.ms.square.android.expandabletextview.ExpandableTextView expTv1;
        public Button replyButton;

        public RelativeLayout cardView;
        public MyViewHolder(View itemView) {
        super(itemView);
            nameTextView = itemView.findViewById(R.id.txt_name);
            dateTextView = itemView.findViewById(R.id.txt_date);

            profileImageView = itemView.findViewById(R.id.img_profile);
            cardView = itemView.findViewById(R.id.container);
            replyButton = itemView.findViewById(R.id.btn_reply);
            replyButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
            expTv1 = itemView
                    .findViewById(R.id.expand_text_view);

        }


        public void nokri_bind(final Nokri_CommentsModel model, final OnItemClickListener listener) {
            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });
        }
    }
}
