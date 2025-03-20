package com.scriptsbundle.nokri.employeer.jobs.adapters;

import android.content.Context;
import androidx.appcompat.view.ContextThemeWrapper
;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_ResumeReceivedModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuResumeReceivedModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Glixen Technologies on 10/02/2018.
 */

public class Nokri_ReceivedResumeAdapter extends RecyclerView.Adapter<Nokri_ReceivedResumeAdapter.MyViewHolder>{
    public interface OnItemClickListener {

        void onItemClick(Nokri_ResumeReceivedModel item);
        void onTakeActionClick(Nokri_ResumeReceivedModel model);
        void onDownloadClick(Nokri_ResumeReceivedModel model);



        void menuItemSelected(Nokri_ResumeReceivedModel model, MenuItem item);
    }
    private final OnItemClickListener listener;
    private List<Nokri_ResumeReceivedModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;
    public Nokri_ReceivedResumeAdapter(List<Nokri_ResumeReceivedModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_received_resume,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_ResumeReceivedModel model = jobList.get(position);
        holder.nokri_bind(model,listener);
        holder.jobTypeTextView.setText(model.getJobType());
        holder.nameTextView.setText(model.getName());
        holder.dateTextView.setText(model.getDate());
        holder.addressTextView.setText(model.getAddress());
        holder.takeActionButton.setText(model.getActionButtonText());
        if(!TextUtils.isEmpty(model.getImageUrl()))
        Picasso.get().load(model.getImageUrl()).into(holder.profileImage);
        nokri_setParagraphFont(holder);
    }
    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setOpenSenseFontTextView(holder.jobTypeTextView,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.nameTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.dateTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.dateTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontButton(holder.takeActionButton,context.getAssets());
    }
    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView jobTypeTextView,nameTextView,dateTextView,addressTextView,menuTextView;
        public ImageView locationImageView;
        public Button takeActionButton,downloadButton;
        public CircularImageView profileImage;
        public MyViewHolder(View itemView) {
            super(itemView);

            jobTypeTextView = itemView.findViewById(R.id.txt_job_type);
            Nokri_Utils.setRoundButtonColor(context,jobTypeTextView);
            nameTextView = itemView.findViewById(R.id.txt_name);
            dateTextView = itemView.findViewById(R.id.txt_date);
            addressTextView = itemView.findViewById(R.id.txt_address);
            locationImageView = itemView.findViewById(R.id.img_location);
            locationImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.location_icon));
            menuTextView = itemView.findViewById(R.id.txt_menu);

            takeActionButton = itemView.findViewById(R.id.btn_take_action);
            downloadButton = itemView.findViewById(R.id.btn_download);
            profileImage = itemView.findViewById(R.id.img_profile);

        }

        public void nokri_bind(final Nokri_ResumeReceivedModel model, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDownloadClick(model);
                }
            });
        takeActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTakeActionClick(model);
            }
        });



            menuTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.PopupMenu);
                    PopupMenu popup = new PopupMenu(ctw, menuTextView);
                    try {
                        Field[] fields = popup.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            if ("mPopup".equals(field.getName())) {
                                field.setAccessible(true);
                                Object menuPopupHelper = field.get(popup);
                                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                                Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                                setForceIcons.invoke(menuPopupHelper, true);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    popup.inflate(R.menu.menu_resume_received);

                    Menu menu = popup.getMenu();

                    Nokri_MenuResumeReceivedModel menuResumeReceivedModel = Nokri_SharedPrefManager.getResumeReceivedMenuSettings(context);
                    menu.findItem(R.id.menu_take_action).setTitle(menuResumeReceivedModel.getTakeAction());
                   if(model.isJobLinkedin())
                       menu.findItem(R.id.menu_download).setTitle(menuResumeReceivedModel.getLinkedin());
                       else
                    menu.findItem(R.id.menu_download).setTitle(menuResumeReceivedModel.getDownload());
                    menu.findItem(R.id.menu_view_profile).setTitle(menuResumeReceivedModel.getViewProfile());
                    menu.findItem(R.id.menu_delete_profile).setTitle(menuResumeReceivedModel.getDelete());

                    if(model.isCoverLetterAvailable()){
                        menu.findItem(R.id.menu_view_cover_letter).setTitle(model.getCoverLetterTitle());
                    }
                    else
                        menu.findItem(R.id.menu_view_cover_letter).setTitle(model.getCoverLetterTitle()).setVisible(false);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            listener.menuItemSelected(model,item);
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }


    }

}
