package com.scriptsbundle.nokri.employeer.jobs.adapters;

import android.content.Context;

import android.graphics.Color;

import androidx.appcompat.view.ContextThemeWrapper
        ;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_ActiveJobsModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuActiveJobsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_ActiveJobsAdapter extends RecyclerView.Adapter<Nokri_ActiveJobsAdapter.MyViewHolder> {
    public interface OnItemClickListener {

        void onItemClick(Nokri_ActiveJobsModel item);

        void menuItemSelected(Nokri_ActiveJobsModel model, MenuItem item);

        void onInActive(Nokri_ActiveJobsModel model);

        void onDeleteClick(Nokri_ActiveJobsModel modael);

        void onBumpClicked(Nokri_ActiveJobsModel model);

    }

    private final OnItemClickListener listener;
    private List<Nokri_ActiveJobsModel> jobList;
    private Nokri_FontManager fontManager;
    private Context context;

    public Nokri_ActiveJobsAdapter(List<Nokri_ActiveJobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_active_jobs, parent, false);

        return new Nokri_ActiveJobsAdapter.MyViewHolder(itemView);
    }

    private void nokri_setParagraphFont(MyViewHolder holder) {

        fontManager.nokri_setOpenSenseFontTextView(holder.jobTypeTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobExpireTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.expireDateTextView, context.getAssets());

        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView, context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitleTextView, context.getAssets());
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_ActiveJobsModel model = jobList.get(position);
        holder.nokri_bind(model, listener);
        holder.jobTypeTextView.setText(model.getJobType());
        holder.jobTitleTextView.setText(model.getJobTitle());
        holder.jobExpireTextView.setText(model.getJobExpire());
        holder.expireDateTextView.setText(model.getJobExpireDate());

        holder.inactiveButton.setText(model.getInavtiveText());
        holder.locationImageView.setBackground(Nokri_Utils.getColoredXml(context, R.drawable.location_icon));
        holder.addressTextView.setText(model.getAddress());


        nokri_setParagraphFont(holder);
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView jobTypeTextView, jobTitleTextView, jobExpireTextView, expireDateTextView, addressTextView, menuTextView;
        public ImageView locationImageView;
        public Button inactiveButton, bumpUpButton;
        public ImageButton deleteImageButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            jobTypeTextView = itemView.findViewById(R.id.txt_job_type);
            Nokri_Utils.setRoundButtonColor(context, jobTypeTextView);
            jobTitleTextView = itemView.findViewById(R.id.txt_job_title);
            jobExpireTextView = itemView.findViewById(R.id.txt_job_expire);
            expireDateTextView = itemView.findViewById(R.id.txt_job_expire_date);
            expireDateTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            menuTextView = itemView.findViewById(R.id.txt_menu);

            addressTextView = itemView.findViewById(R.id.txt_address);
            locationImageView = itemView.findViewById(R.id.img_location);


            bumpUpButton = itemView.findViewById(R.id.btn_bump);
            bumpUpButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
            inactiveButton = itemView.findViewById(R.id.btn_inactive);
            inactiveButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
            deleteImageButton = itemView.findViewById(R.id.img_btn_delete);


        }

        public void nokri_bind(final Nokri_ActiveJobsModel model, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });


            inactiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onInActive(model);
                }
            });

            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onDeleteClick(model);
                }
            });
            bumpUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBumpClicked(model);
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
                    popup.inflate(R.menu.active_jobs_menu_item);

                    Nokri_MenuActiveJobsModel activeJobsModel = Nokri_SharedPrefManager.getActiveJobMenuSettings(context);

                    Menu menu = popup.getMenu();
                    menu.findItem(R.id.menu_resume_received).setTitle(activeJobsModel.getResumeReceived());
                    menu.findItem(R.id.menu_delete).setTitle(activeJobsModel.getDelete());
                    menu.findItem(R.id.menu_edit).setTitle(activeJobsModel.getEdit());
                    menu.findItem(R.id.menu_view_job).setTitle(activeJobsModel.getViewJob());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            listener.menuItemSelected(model, item);
                            return false;
                        }
                    });
                    popup.show();
                }
            });

        }


    }

}
