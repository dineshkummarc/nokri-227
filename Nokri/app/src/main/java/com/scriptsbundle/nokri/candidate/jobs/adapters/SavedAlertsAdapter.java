package com.scriptsbundle.nokri.candidate.jobs.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.jobs.models.JobAlertsModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeInterface;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import java.util.ArrayList;

public class SavedAlertsAdapter extends RecyclerView.Adapter<SavedAlertsAdapter.ViewHolder>{
    Context context;
    ArrayList<JobAlertsModel> arrayList;
    SavedResumeInterface myInterface;
    public SavedAlertsAdapter(Context context,ArrayList<JobAlertsModel> arrayList,SavedResumeInterface myInterface){
        this.context = context;
        this.arrayList = arrayList;
        this.myInterface = myInterface;
    }
    @NonNull
    @Override
    public SavedAlertsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.saved_job_alert_item, null);
        listItem.setLayoutParams(new RecyclerView.LayoutParams(
                ((RecyclerView) parent).getLayoutManager().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        SavedAlertsAdapter.ViewHolder viewHolder = new SavedAlertsAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedAlertsAdapter.ViewHolder viewHolder, int i) {
        JobAlertsModel model = arrayList.get(i);
        viewHolder.alertName.setText(model.name);
        viewHolder.category.setText(model.category + "    |    "+model.frequency);
//        viewHolder.emailFrequency.setText(model.frequency);
//

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setMonesrratSemiBioldFont(viewHolder.alertName,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(viewHolder.category,context.getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(viewHolder.emailFrequency,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(viewHolder.remove,context.getAssets());
        viewHolder.remove.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//
        Nokri_SettingsModel model1 = Nokri_SharedPrefManager.getSettings(context);
        viewHolder.remove.setText(model1.getRemoveText());
        Nokri_Utils.setRoundButtonColor(context,viewHolder.remove);
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myInterface.onDelete(model.id,viewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView alertName;
        public TextView category;
        public TextView emailFrequency;
        public TextView remove;
        public ViewHolder(View itemView) {
            super(itemView);
            alertName = itemView.findViewById(R.id.alertName);
            category = itemView.findViewById(R.id.category);
//            emailFrequency = itemView.findViewById(R.id.emailFrequency);
            remove = itemView.findViewById(R.id.remove);

        }
    }
}
