package com.scriptsbundle.nokri.guest.faq.viewholders;

import android.view.View;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;


/**
 * Created by Glixen Technologies on 09/01/2018.
 */

public class ChildViewHolder extends com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder {
    public TextView childTextView;

    public ChildViewHolder(View itemView) {
        super(itemView);
    childTextView = itemView.findViewById(R.id.txt_child);

    }

    public void setChildTextView(String text){
        childTextView.setText(text);
    }
}
