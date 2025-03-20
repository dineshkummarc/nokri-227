package com.scriptsbundle.nokri.guest.faq.viewholders;

import android.graphics.Color;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


/**
 * Created by Glixen Technologies on 09/01/2018.
 */

public class ParentViewHolder extends GroupViewHolder {
    public TextView titleTextView;
    public ImageButton arrow;
    private RelativeLayout container;
    public ParentViewHolder(View itemView) {
        super(itemView);
    titleTextView = itemView.findViewById(R.id.txt_title);
    arrow = itemView.findViewById(R.id.img_btn_arrow);
    container = itemView.findViewById(R.id.container);}

    public void setTitle(String title){
    titleTextView.setText(title);
    }
    @Override
    public void expand() {
        animateExpand();
        container.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        titleTextView.setTextColor(Color.parseColor("#ffffff"));
    }

    @Override
    public void collapse() {
        container.setBackground(container.getResources().getDrawable(R.drawable.rectangle_faq));
        titleTextView.setTextColor(Color.parseColor("#333333"));
        animateCollapse();
    }
    private void animateExpand() {

        RotateAnimation rotate = new RotateAnimation(-360, -270, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(-270, -360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
