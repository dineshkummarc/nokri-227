package com.scriptsbundle.nokri.candidate.jobs.adapters;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuSavedJobsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Glixen Technologies on 13/01/2018.
 */

public class Nokri_SavedJobsAdapter extends RecyclerView.Adapter<Nokri_SavedJobsAdapter.ViewHolder>{

    private List<Nokri_JobsModel> jobList;

    public interface OnItemClickListener {


        void onItemClick(Nokri_JobsModel item);
        void menuItemSelected(Nokri_JobsModel model, MenuItem item, int position);
        void onCompanyClick(Nokri_JobsModel item);
        void onJobClick(Nokri_JobsModel item);
        void onImageClick(Nokri_JobsModel item);

    }
    private final Nokri_SavedJobsAdapter.OnItemClickListener listener;
    private Nokri_FontManager fontManager;
    private Context context;
    public Nokri_SavedJobsAdapter(List<Nokri_JobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_saved_jobs,parent,false);

        return new Nokri_SavedJobsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Nokri_JobsModel model = jobList.get(position);
        holder.nokri_bind(model,listener,position);
        holder.jobTypeTextView.setText(model.getJobType());
        holder.jobTitleTextView.setText(model.getJobTitle());
        holder.jobDescriptionTextView.setText(model.getJobDescription());
        holder.timeRemainingTextView.setText(model.getTimeRemaining());
        nokri_setParagraphFont(holder);
        holder.addressTextView.setText(model.getAddress());
        holder.salaryTextView.setText(model.getSalary());
        holder.paymentTextView.setText(model.getPaymentPeriod());
        //holder.clockImageView.setImageBitmap(model.getTimeIcon());
        //holder.locationImageView.setImageBitmap(model.getLocationIcon());
        //holder.companyImageView.setImageBitmap(model.getCompanyIcon());
        // Picasso.get()(context).load(model.getCompanyLogo()).resize((int) context.getResources().getDimension(R.dimen.liist_item_icon_size),(int) context.getResources().getDimension(R.dimen.liist_item_icon_size)).centerInside().into(holder.companyImageView);
        if(!TextUtils.isEmpty(model.getCompanyLogo()))
        Picasso.get().load(model.getCompanyLogo()).into(holder.companyImageView);
    }

    private void nokri_setParagraphFont(ViewHolder holder){
        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitleTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobTypeTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobDescriptionTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.timeRemainingTextView,context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.salaryTextView,context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.paymentTextView,context.getAssets());
    }
    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView jobTypeTextView,jobTitleTextView,jobDescriptionTextView,timeRemainingTextView,addressTextView,salaryTextView,paymentTextView,menuTextView;
        public ImageView clockImageView,locationImageView;
        public View menuOverlay;
        public CircularImageView companyImageView;
    public ViewHolder(View itemView) {
        super(itemView);
        jobTypeTextView = itemView.findViewById(R.id.txt_job_type);
        Nokri_Utils.setRoundButtonColor(context,jobTypeTextView);
        jobTitleTextView = itemView.findViewById(R.id.txt_job_title);
        jobDescriptionTextView = itemView.findViewById(R.id.txt_job_description);
        timeRemainingTextView = itemView.findViewById(R.id.txt_time_remaining);
        menuTextView = itemView.findViewById(R.id.txt_menu);
        addressTextView = itemView.findViewById(R.id.txt_address);
        salaryTextView = itemView.findViewById(R.id.txt_salary);
        salaryTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        paymentTextView = itemView.findViewById(R.id.txt_payment_period);
        clockImageView = itemView.findViewById(R.id.img_clock);
        locationImageView = itemView.findViewById(R.id.img_location);

        clockImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.clock));
        locationImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.location_icon));
        companyImageView = itemView.findViewById(R.id.img_company_logo);
        menuOverlay = itemView.findViewById(R.id.menu_overlay);
    }
        public void nokri_bind(final Nokri_JobsModel model, final OnItemClickListener listener, final int position){
            jobTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onJobClick(model);
                }
            });
            jobDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCompanyClick(model);
                }
            });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(model);
            }
        });

        companyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClick(model);
            }
        });

            menuOverlay.setOnClickListener(new View.OnClickListener() {
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

                    popup.inflate(R.menu.menu_saved_jobs);

                    Menu menu = popup.getMenu();
                    Nokri_MenuSavedJobsModel savedJobsModel = Nokri_SharedPrefManager.getSavedJobsMenuSettings(context);
                    menu.findItem(R.id.menu_view_job).setTitle(savedJobsModel.getViewJob());
                    menu.findItem(R.id.menu_view_delete_job).setTitle(savedJobsModel.getDeleteJob());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            listener.menuItemSelected(model,item,position);
                            return false;
                        }
                    });
                    popup.show();
                }
            });

    }
}
}
