package com.scriptsbundle.nokri.employeer.email.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.email.model.Nokri_EditEmailTemplateModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;

import com.victorminerva.widget.edittext.AutofitEdittext;

import java.util.List;

/**
 * Created by Glixen Technologies on 13/01/2018.
 */

public class Nokri_EditEmailAdapter extends RecyclerView.Adapter<Nokri_EditEmailAdapter.MyViewHolder>{
    public interface OnItemClickListener {

        void onItemClick(Nokri_EditEmailTemplateModel item, int flag);

    }
    private final OnItemClickListener listener;
    private List<Nokri_EditEmailTemplateModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;
    public Nokri_EditEmailAdapter(List<Nokri_EditEmailTemplateModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_edit_email,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_EditEmailTemplateModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.nameTextView.setText(model.getName());
        holder.srTextView.setText(model.getSrNum());
        holder.updateButton.setText(model.getButtonText());
        holder.deleteButton.setText(model.getDeleteButtonText());
        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setOpenSenseFontTextView(holder.nameTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.srTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.updateButton,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.deleteButton,context.getAssets());

    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
    public TextView srTextView;
    public AutofitEdittext nameTextView;
    public Button updateButton;
    public Button deleteButton;
        public MyViewHolder(View itemView) {
        super(itemView);

        srTextView = itemView.findViewById(R.id.txt_sr);
        nameTextView = itemView.findViewById(R.id.txt_name);
        updateButton = itemView.findViewById(R.id.btn_update);
        deleteButton = itemView.findViewById(R.id.btn_delete);
        }

        public void nokri_bind(final Nokri_EditEmailTemplateModel model, final OnItemClickListener listener) {
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model,0);
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model,1);
                }
            });
        }
    }
}
