package com.scriptsbundle.nokri.candidate.edit.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.models.Nokri_FileModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;

import com.squareup.picasso.Picasso;
import com.victorminerva.widget.edittext.AutofitEdittext;

import java.util.List;

/**
 * Created by Glixen Technologies on 25/01/2018.
 */

public class Nokri_FilePreviewAdapter extends RecyclerView.Adapter<Nokri_FilePreviewAdapter.MyViewHolder>{

    public interface OnItemClickListener {

        void onItemClick(Nokri_FileModel item, int position);

    }


    private List<Nokri_FileModel>jobList;
     private Nokri_FontManager fontManager;
     private Context context;
    private final OnItemClickListener listener;
    private int flag;


    public Nokri_FilePreviewAdapter(List<Nokri_FileModel> jobList, Context context, OnItemClickListener listener, int flag) {
        fontManager = new Nokri_FontManager();
        this.jobList = jobList;
        this.context = context;
        this.listener = listener;
        this.flag = flag;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_file_preview,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_FileModel model = jobList.get(position);
        holder.nokri_bind(model,listener,position);
        holder.fileNameTextView.setText(model.getName());
        holder.deleteButton.setText(model.getButtonText());
        if(flag ==1){
            if(!TextUtils.isEmpty(model.getUrl()))
            Picasso.get().load(model.getUrl()).fit().centerCrop().into(holder.fileImageView);
        }

        if(flag ==0){
            String extension = model.getName().substring(model.getName().lastIndexOf(".")+1);

            switch (extension){
                case "pdf":
                    Picasso.get().load(R.drawable.pdf).into(holder.fileImageView);
                    break;
                case "docx":
                    Picasso.get().load(R.drawable.docx).into(holder.fileImageView);
                    break;
                case "doc":
                    Picasso.get().load(R.drawable.doc).into(holder.fileImageView);
                    break;
                case "txt":
                    Picasso.get().load(R.drawable.txt).into(holder.fileImageView);
                    break;
                    default:
                        Picasso.get().load(R.drawable.file).into(holder.fileImageView);
                        break;
            }
        }

        nokri_setParagraphFont(holder);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {

        fontManager.nokri_setOpenSenseFontTextView(holder.fileNameTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.deleteButton,context.getAssets());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public AutofitEdittext fileNameTextView;
        public ImageView fileImageView;
        public Button deleteButton;
    public MyViewHolder(View itemView) {
        super(itemView);
        fileImageView = itemView.findViewById(R.id.img_file);
        fileNameTextView = itemView.findViewById(R.id.txt_file_name);
        deleteButton = itemView.findViewById(R.id.btn_delete);
    }

        public void nokri_bind(final Nokri_FileModel model, final OnItemClickListener listener, final int position) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model,position);
                }
            });
        }
        }
    }

