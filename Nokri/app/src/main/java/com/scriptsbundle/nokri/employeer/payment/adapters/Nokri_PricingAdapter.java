package com.scriptsbundle.nokri.employeer.payment.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.payment.models.Nokri_PricingModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;

import java.util.List;

public class Nokri_PricingAdapter extends RecyclerView.Adapter<Nokri_PricingAdapter.MyViewHolder> {
    private List<Nokri_PricingModel.Nokri_PremiumJobs> jobList;
    private Nokri_FontManager fontManager;
    private Context context;

    public Nokri_PricingAdapter(List<Nokri_PricingModel.Nokri_PremiumJobs> jobList, Context context) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;

    }

     @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pricing_table,parent,false);

         return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Nokri_PricingModel.Nokri_PremiumJobs model = jobList.get(position);
            holder.noOfJobsTextView.setText(model.getNumberOfJobs());
            holder.namesTextView.setText(model.getName());

            setFonts(holder);
    }

    private void setFonts(MyViewHolder holder){
        fontManager.nokri_setMonesrratSemiBioldFont(holder.noOfJobsTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.namesTextView,context.getAssets());
    }
    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView noOfJobsTextView,namesTextView;
        public MyViewHolder(View itemView) {
            super(itemView);
            noOfJobsTextView = itemView.findViewById(R.id.txt_no_of_jobs);
            namesTextView = itemView.findViewById(R.id.txt_name);
        }
    }
    }
