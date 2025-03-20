package com.scriptsbundle.nokri.employeer.payment.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scriptsbundle.nokri.employeer.payment.models.Nokri_PackageDetailModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;

import java.util.List;

public class Nokri_PackageDetailAdapter extends RecyclerView.Adapter<Nokri_PackageDetailAdapter.MyViewHolder>{
    private List<Nokri_PackageDetailModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;
    public Nokri_PackageDetailAdapter(List<Nokri_PackageDetailModel> jobList, Context context) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_packge_detail,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Nokri_PackageDetailModel model = jobList.get(position);

        holder.titleTextView.setText(model.getTitle());
        holder.srTextView.setText(model.getSrNum());
        holder.packgeDetailTextView.setText(model.getPackageDetail());

        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setMonesrratSemiBioldFont( holder.titleTextView,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.srTextView,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont( holder.packgeDetailTextView,context.getAssets());


    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView srTextView,titleTextView,packgeDetailTextView;


        public MyViewHolder(View itemView) {
            super(itemView);

            srTextView = itemView.findViewById(R.id.txt_sr);
            titleTextView = itemView.findViewById(R.id.txt_title);
            packgeDetailTextView = itemView.findViewById(R.id.txt_detail);
        }


        }
    }

