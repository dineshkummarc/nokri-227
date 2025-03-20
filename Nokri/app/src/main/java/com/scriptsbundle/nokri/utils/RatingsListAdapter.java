package com.scriptsbundle.nokri.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.models.RatingModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RatingsListAdapter extends BaseAdapter {
    Context context;
    ArrayList<RatingModel> ratings;
    boolean calledForList;
    private RatingsAdapter.ItemClickListener mClickListener;
    public RatingsListAdapter(Context context, ArrayList<RatingModel> ratings, boolean calledForList) {
        this.context = context;
        this.ratings = ratings;
        this.calledForList = calledForList;
    }

    @Override
    public int getCount() {
        return ratings.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view1, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_review_item, null);

        RatingModel ratingModel = ratings.get(i);
        CircularImageView imageView;
        TextView raterName;
        TextView writeReply;
        TextView raterComments;
        TextView authorName;
        TextView reply;
        TextView date;
        SimpleRatingBar ratingBar;
        ConstraintLayout authorContainer;


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
        Picasso.get().load(ratingModel.getUrl()).into(imageView);
        raterName.setText(ratingModel.getRaterName());
        raterComments.setText(ratingModel.getRaterComments());
        ratingBar.setRating((float) ratingModel.getRating());
        date.setText(ratingModel.getDate());

        if (ratingModel.isContainsReply()){
            authorContainer.setVisibility(View.VISIBLE);
            authorName.setText(ratingModel.getAuthorName());
            reply.setText(ratingModel.getAuthorReply());
        }else{
            authorContainer.setVisibility(View.GONE);
            if (ratingModel.isCanReply() && calledForList){
                writeReply.setVisibility(View.VISIBLE);
            }
        }
        writeReply.setOnClickListener(new View.OnClickListener() {
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
                            mClickListener.onItemClick(ratingModel,i,editText.getText().toString(),dialog);
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


        return view;
    }

    void setClickListener(RatingsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
