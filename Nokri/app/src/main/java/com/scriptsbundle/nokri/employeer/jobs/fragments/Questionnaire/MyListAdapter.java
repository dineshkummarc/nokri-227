package com.scriptsbundle.nokri.employeer.jobs.fragments.Questionnaire;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_PostJobFragment;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

    // RecyclerView recyclerView;
    Context context;
    Nokri_FontManager fontManager = new Nokri_FontManager();
    public MyListAdapter(Context context) {
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.question_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.heading.setText(Nokri_PostJobFragment.questionnaireModel.questionsLabel);
        holder.questionText.setText(Nokri_PostJobFragment.questions.get(position));

        fontManager.nokri_setOpenSenseFontTextView(holder.heading,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.questionText,context.getAssets());
    }


    @Override
    public int getItemCount() {
        return Nokri_PostJobFragment.questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText questionText;
        public TextView heading;
        public RelativeLayout relativeLayout;
        public RelativeLayout viewForeground;
        public RelativeLayout viewBackground;
        public ViewHolder(View itemView) {
            super(itemView);
            this.questionText = itemView.findViewById(R.id.questionText);
            this.heading = itemView.findViewById(R.id.heading);
            this.viewForeground = itemView.findViewById(R.id.view_foreground);
            viewBackground = itemView.findViewById(R.id.view_background);
        }
    }

    public void removeItem(int position) {
        Nokri_PostJobFragment.questions.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

}