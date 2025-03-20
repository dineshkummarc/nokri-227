package com.scriptsbundle.nokri.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.models.RatingModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.MyViewHolder> {
    Context context;
    ArrayList<RatingModel> ratings;
    boolean calledForList;
    private ItemClickListener mClickListener;
        public RatingsAdapter(Context context, ArrayList<RatingModel> ratings, boolean calledForList) {
        this.context = context;
        this.ratings = ratings;
        this.calledForList = calledForList;
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_review_item, parent, false);

        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RatingModel ratingModel = ratings.get(holder.getAdapterPosition());
        Picasso.get().load(ratingModel.getUrl()).into(holder.imageView);
        holder.raterName.setText(ratingModel.getRaterName());
        holder.raterComments.setText(ratingModel.getRaterComments());
        holder.ratingBar.setRating((float) ratingModel.getRating());
        holder.date.setText(ratingModel.getDate());

        for (int i = 0;i<ratings.size();i++){
            if (ratingModel.isContainsReply()){
                holder.authorContainer.setVisibility(View.VISIBLE);
                holder.authorName.setText(ratingModel.getAuthorName());
                holder.reply.setText(ratingModel.getAuthorReply());
            }else{
                holder.authorContainer.setVisibility(View.GONE);
                if (ratingModel.isCanReply() && calledForList){
                    holder.writeReply.setVisibility(View.VISIBLE);
                }
            }
        }

        holder.writeReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                View v = LayoutInflater.from(context).inflate(R.layout.reply_dialog,null);
                TextView label = v.findViewById(R.id.replyLabel);
                TextView editText = v.findViewById(R.id.replyText);
                Button confirm = v.findViewById(R.id.btn_confirm);
                Button cancel = v.findViewById(R.id.btn_close);

                confirm.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                label.setText(ratingModel.replyLabel);
                editText.setHint(ratingModel.replyLabel);
                cancel.setText(ratingModel.cancel);
                confirm.setText(ratingModel.submit);
                builder.setView(v);

                AlertDialog dialog = builder.create();
                dialog.show();
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString().length()!=0){
                            confirm.setEnabled(false);
                            mClickListener.onItemClick(ratingModel,position,editText.getText().toString(),dialog);
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });


    }



    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircularImageView imageView;
        TextView raterName;
        TextView writeReply;
        TextView raterComments;
        TextView authorName;
        TextView reply;
        TextView date;
        SimpleRatingBar ratingBar;
        ConstraintLayout authorContainer;
        public MyViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.circularImageView);
            raterName = view.findViewById(R.id.raterName);
            writeReply = view.findViewById(R.id.writeReply);
            raterComments = view.findViewById(R.id.raterComments);
            Nokri_Utils.setRoundButtonColor(context,writeReply);
            authorName = view.findViewById(R.id.authorName);
            reply = view.findViewById(R.id.reply);
            date = view.findViewById(R.id.date);
            ratingBar = view.findViewById(R.id.rating);
            authorContainer = view.findViewById(R.id.replyContainer);

        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(RatingModel model, int position, String comment,AlertDialog dialog);
    }


}
