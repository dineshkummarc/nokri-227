package com.scriptsbundle.nokri.employeer.jobs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.fragments.CheckBoxInterface;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_PostJobsPackagesModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import java.util.List;

public class Nokri_PostJobPackagesAdapter extends RecyclerView.Adapter<Nokri_PostJobPackagesAdapter.MyViewHolder> {
    private List<Nokri_PostJobsPackagesModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;
    boolean calledFromCustom;
    CheckBoxInterface checkBoxInterface;

    public Nokri_PostJobPackagesAdapter(boolean calledFromCustom, List<Nokri_PostJobsPackagesModel> jobList, Context context) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.calledFromCustom = calledFromCustom;

    }

    public void setListener(CheckBoxInterface checkBoxInterface) {
        this.checkBoxInterface = checkBoxInterface;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_boost_job_adons, parent, false);

        return new Nokri_PostJobPackagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Nokri_PostJobsPackagesModel model = jobList.get(position);
        holder.titleTextView.setText(model.getTitle());
        holder.quantityTextView.setText(model.getQuantity());
        holder.remainingTextView.setText(model.getRemainign());
        if (!model.isEditable()) {
            holder.checkBox.setEnabled(false);
            holder.checkBox.setChecked(model.isChecked());
            holder.checkBox.setSelected(model.isChecked());
        } else if (model.isEditable()) {
            holder.checkBox.setSelected(model.isChecked());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        model.setChecked(true);
                    else
                        model.setChecked(false);
                }
            });
            holder.checkBox.setChecked(model.isChecked());
        }

        if (calledFromCustom){
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        checkBoxInterface.onCheckClick(position);
                    }else{
                        checkBoxInterface.onCheckUnClick(position);
                    }
                }
            });
        }


        if (position % 2 == 0)
            holder.itemContainer.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
        else
            holder.itemContainer.setBackgroundColor(context.getResources().getColor(R.color.white));

        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setMonesrratSemiBioldFont(holder.titleTextView, context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.quantityTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.remainingTextView, context.getAssets());


    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView, remainingTextView, quantityTextView;
        public AppCompatCheckBox checkBox;
        public RelativeLayout itemContainer;

        @SuppressLint("RestrictedApi")
        public MyViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.txt_title);
            remainingTextView = itemView.findViewById(R.id.txt_remaining);
            quantityTextView = itemView.findViewById(R.id.txt_quantity);
            checkBox = itemView.findViewById(R.id.checkbox);
            itemContainer = itemView.findViewById(R.id.item_container);


            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{

                            new int[]{-android.R.attr.state_enabled}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[]{

                            Color.RED //disabled
                            , Color.parseColor(Nokri_Config.APP_COLOR) //enabled

                    }
            );

//            checkBox.setSupportButtonTintList(colorStateList);
        }
    }
}
