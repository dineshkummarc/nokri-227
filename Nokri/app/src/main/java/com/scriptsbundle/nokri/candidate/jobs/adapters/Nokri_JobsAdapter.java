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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuJobModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Glixen Technologies on 04/01/2018.
 */

public class Nokri_JobsAdapter extends RecyclerView.Adapter<Nokri_JobsAdapter.MyViewHolder> {
    private List<Nokri_JobsModel> jobList;
    boolean calledFromJobsApplied = false;

    public interface OnItemClickListener {

        void onItemClick(Nokri_JobsModel item);

        void onCompanyClick(Nokri_JobsModel item);

        void menuItemSelected(Nokri_JobsModel model, MenuItem item);

    }

    public interface OnJobClickListener {
        void onJobClick(Nokri_JobsModel model);
    }

    public interface OnImageClickListener {
        void onImageClick(Nokri_JobsModel model);
    }

    public void setCalledFromJobsApplied(boolean bool){
        this.calledFromJobsApplied = bool;
    }

    private final OnItemClickListener listener;
    private OnJobClickListener onJobClickListener;
    private OnImageClickListener onImageClickListener;
    private Nokri_FontManager fontManager;
    private Context context;

    public void setOnJobClickListener(OnJobClickListener onJobClickListener) {
        this.onJobClickListener = onJobClickListener;
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }


    public Nokri_JobsAdapter(List<Nokri_JobsModel> jobList, Context context, OnItemClickListener listener) {
        this.jobList = jobList;
        fontManager = new Nokri_FontManager();
        this.context = context;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (Nokri_Globals.DESIGN_TYPE == 1)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jobs, parent, false);
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jobs_2, parent, false);

        }


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Nokri_JobsModel model = jobList.get(position);
        holder.nokri_bind(model, listener);
        holder.jobTypeTextView.setText(model.getJobType());
//        Nokri_Utils.setRoundButtonColor(context,holder.jobTypeTextView);
        if (Nokri_Globals.DESIGN_TYPE==1){
            holder.jobTypeTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        holder.jobTitleTextView.setText(model.getJobTitle());
        holder.jobDescriptionTextView.setText(model.getJobDescription());
        holder.timeRemainingTextView.setText(model.getTimeRemaining());
        nokri_setParagraphFont(holder);
        holder.addressTextView.setText(model.getAddress());
        holder.salaryTextView.setText(model.getSalary());


        if (Nokri_Globals.DESIGN_TYPE == 1) {

            holder.paymentTextView.setText(model.getPaymentPeriod());

            if (!model.isShowMenu()) {
                holder.menuTextView.setVisibility(View.GONE);
                holder.menuOverlay.setVisibility(View.GONE);
            }
        }else {
            if (model.getJobType().equals(""))
                holder.featuredView.setVisibility(View.GONE);
            if (model.getSalary().equals(""))
                holder.salaryTextView.setText("N/A");
            holder.featuredText.setText(model.getJobType());
            if (calledFromJobsApplied){
                holder.statusText.setText(model.getStatus());
            }

            try {

                if (!model.isFeatured()){
                    holder.featuredTag.setVisibility(View.GONE);
                }else {
                    holder.featuredTag.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //holder.clockImageView.setImageBitmap(model.getTimeIcon());
        //holder.locationImageView.setImageBitmap(model.getLocationIcon());
        //holder.companyImageView.setImageBitmap(model.getCompanyIcon());
        // Picasso.get()(context).load(model.getCompanyLogo()).resize((int) context.getResources().getDimension(R.dimen.liist_item_icon_size),(int) context.getResources().getDimension(R.dimen.liist_item_icon_size)).centerInside().into(holder.companyImageView);
        if (!TextUtils.isEmpty(model.getCompanyLogo()))
            if (Nokri_Globals.DESIGN_TYPE == 1) {
                Picasso.get().load(model.getCompanyLogo()).into((CircularImageView) holder.companyImageView);
            } else {
                Picasso.get().load(model.getCompanyLogo()).into((ImageView) holder.companyImageView);
            }

    }

    private void nokri_setParagraphFont(MyViewHolder holder) {
        fontManager.nokri_setMonesrratSemiBioldFont(holder.jobTitleTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobTypeTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.addressTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.jobDescriptionTextView, context.getAssets());
        fontManager.nokri_setOpenSenseFontTextView(holder.timeRemainingTextView, context.getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(holder.salaryTextView, context.getAssets());
        if (Nokri_Globals.DESIGN_TYPE == 1)
            fontManager.nokri_setOpenSenseFontTextView(holder.paymentTextView, context.getAssets());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView statusText, featuredText, jobTypeTextView, jobTitleTextView, jobDescriptionTextView, timeRemainingTextView, addressTextView, salaryTextView, paymentTextView, menuTextView;
        public ImageView clockImageView, locationImageView,featuredTag;
        public LinearLayout linearLayout;
        ImageView pointing_icon,img_clock;
        View companyImageView,featuredView, statusView;

        public void MyViewHolder() {
        }

        public View menuOverlay;

        //  public CircularImageView companyImageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            jobTypeTextView = itemView.findViewById(R.id.txt_job_type);
            featuredTag = itemView.findViewById(R.id.featuredTag);
            jobTitleTextView = itemView.findViewById(R.id.txt_job_title);
            linearLayout = itemView.findViewById(R.id.container);
            jobDescriptionTextView = itemView.findViewById(R.id.txt_job_description);
            timeRemainingTextView = itemView.findViewById(R.id.txt_time_remaining);
            addressTextView = itemView.findViewById(R.id.txt_address);
            menuTextView = itemView.findViewById(R.id.txt_menu);
            salaryTextView = itemView.findViewById(R.id.txt_salary);
            salaryTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            paymentTextView = itemView.findViewById(R.id.txt_payment_period);
            clockImageView = itemView.findViewById(R.id.img_clock);
            locationImageView = itemView.findViewById(R.id.img_location);
            companyImageView = itemView.findViewById(R.id.img_company_logo);
            menuOverlay = itemView.findViewById(R.id.menu_overlay);

            if (Nokri_Globals.DESIGN_TYPE==2){
                featuredView = itemView.findViewById(R.id.featuredView);
                statusView = itemView.findViewById(R.id.statusView);
                featuredView.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                statusView.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                statusText = itemView.findViewById(R.id.statusText);
                if (calledFromJobsApplied){
                    statusView.setVisibility(View.VISIBLE);
                    statusText.setVisibility(View.VISIBLE);
                }

                featuredText = itemView.findViewById(R.id.featuredText);
                fontManager.nokri_setOpenSenseFontTextView(featuredText,context.getAssets());
                fontManager.nokri_setOpenSenseFontTextView(statusText,context.getAssets());
                img_clock = itemView.findViewById(R.id.img_clock);
                pointing_icon = itemView.findViewById(R.id.pointing_icon);
                img_clock.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR));
                pointing_icon.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR));
                if (Nokri_Globals.IS_RTL_ENABLED){
                    pointing_icon.setImageDrawable(context.getDrawable(R.drawable.hand_pointing_left));
                    featuredTag.setImageDrawable(context.getDrawable(R.drawable.featured_stars_right));

                }
                locationImageView.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR));
                featuredText.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                statusText.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
//                int color = Color.parseColor("#ae182c"); //The color u want
            }
            //#8224e3

//            clockImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.clock));
//            locationImageView.setBackground(Nokri_Utils.getColoredXml(context,R.drawable.location_icon));

        }

        public void nokri_bind(final Nokri_JobsModel model, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(model);
                }
            });

            jobDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onJobClickListener != null) {
                        onJobClickListener.onJobClick(model);
                    } else {
                        listener.onCompanyClick(model);
                    }
                }
            });
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onJobClickListener != null)
                        onJobClickListener.onJobClick(model);
                }
            });
            companyImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageClickListener != null)
                        onImageClickListener.onImageClick(model);
                }
            });
            if (Nokri_Globals.DESIGN_TYPE == 1) {

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

                        popup.inflate(R.menu.menu_job);

                        Menu menu = popup.getMenu();
                        Nokri_MenuJobModel menuJobModel = Nokri_SharedPrefManager.getJobMenuSettings(context);
                        menu.findItem(R.id.menu_view_job).setTitle(menuJobModel.getViewJob());
                        menu.findItem(R.id.menu_view_company_profile).setTitle(menuJobModel.getViewCompanyProfile());

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
}
