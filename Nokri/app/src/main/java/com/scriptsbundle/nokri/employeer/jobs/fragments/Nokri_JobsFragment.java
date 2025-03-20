package com.scriptsbundle.nokri.employeer.jobs.fragments;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scriptsbundle.nokri.activities.PostJob;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_ViewPagerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_ActiveJobsModel;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_InactiveJobsModel;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_JobsFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private ViewPager viewPager;

    private TabLayout tabLayout;

    private Nokri_FontManager fontManager;

    private String id;
    private Nokri_EmployeerDashboardModel employeerDashboardModel;

    public Nokri_JobsFragment() {
        // Required empty public constructor
    }
    public interface pagerCallback{
        void onPagerCallback();
    }

    @Override
    public void onResume() {
        super.onResume();

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_jobs, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        employeerDashboardModel = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
        fontManager = new Nokri_FontManager();


        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Nokri_Config.APP_COLOR));
        if(Nokri_Globals.IS_RTL_ENABLED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        nokri_setupViewPager();

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);
        nokri_setupFonts();
        Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getJobs());
    }

    private void nokri_setupFonts() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.custom_tab2, tabLayout, false);
            TextView tv= relativeLayout.findViewById(R.id.tab_title);
            if(i==0) {
                if(Nokri_Globals.IS_RTL_ENABLED)
                    tv.setText(employeerDashboardModel.getJobInactive());
                    else
                tv.setText(employeerDashboardModel.getJobActive());
                tv.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            }
            else
            if(i==1) {
                if(Nokri_Globals.IS_RTL_ENABLED)
                    tv.setText(employeerDashboardModel.getJobActive());
                    else
                tv.setText(employeerDashboardModel.getJobInactive());
            }
            fontManager.nokri_setOpenSenseFontTextView(tv,getActivity().getAssets());
            tabLayout.getTabAt(i).setCustomView(relativeLayout);



        }


    }


    private void nokri_setupViewPager(){
        Nokri_ViewPagerAdapter pagerAdapter = new Nokri_ViewPagerAdapter(getChildFragmentManager());
        Nokri_ActiveJobsFragment activeJobsFragment = new Nokri_ActiveJobsFragment();
        Nokri_InactiveJobsFragment inactiveJobsFragment = new Nokri_InactiveJobsFragment();
        activeJobsFragment.nokri_setListener(new Nokri_ActiveJobsFragment.nokri_pagerCallback() {
            @Override
            public void onCallback(Nokri_ActiveJobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment resumeReceivedFragment = new Nokri_ResumeReceivedFragment();
                Nokri_ResumeReceivedFragment.ID = model.getJobId();
                fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(),resumeReceivedFragment).addToBackStack(null).commit();


            }

            @Override
            public void onViewJobClick(Nokri_ActiveJobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.CALLING_SOURCE = "applied";
                fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();
            }

            @Override
            public void onEditJobClick(Nokri_ActiveJobsModel model) {
                Nokri_SettingsModel settings = new Nokri_SettingsModel();
                if (settings.isCatTempOn()){
                    Intent i = new Intent(getActivity(),PostJob.class);
                    i.putExtra("jobId",model.getJobId());
                    i.putExtra("isUpdate",true);
                    startActivity(i);
                }else{
                    androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment postJobFragment = new Nokri_PostJobFragment();
                    Nokri_PostJobFragment.POST_JOB_CALLING_SOURCE = "external";
                    Nokri_PostJobFragment.POST_JOB_ID = model.getJobId();
                    fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),postJobFragment).addToBackStack(null).commit();
                }

            }

        });

        inactiveJobsFragment.nokri_setListener(new Nokri_InactiveJobsFragment.nokri_pagerCallback() {
            @Override
            public void onCallback(Nokri_InactiveJobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment resumeReceivedFragment = new Nokri_ResumeReceivedFragment();
                Nokri_ResumeReceivedFragment.ID = model.getJobId();

                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),resumeReceivedFragment).addToBackStack(null).commit();

            }

            @Override
            public void onViewJobClick(Nokri_InactiveJobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID="";
                Nokri_JobDetailFragment.CALLING_SOURCE = "applied";
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();
            }

            @Override
            public void onEditJobClick(Nokri_InactiveJobsModel model) {
                Nokri_PostJobFragment.POST_JOB_CALLING_SOURCE = "external";

                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment postJobFragment = new Nokri_PostJobFragment();
                Nokri_PostJobFragment.POST_JOB_CALLING_SOURCE = "external";
                Nokri_PostJobFragment.POST_JOB_ID = model.getJobId();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),postJobFragment).addToBackStack(null).commit();
            }

        });


        if(Nokri_Globals.IS_RTL_ENABLED){
            pagerAdapter.addFragment(inactiveJobsFragment, employeerDashboardModel.getJobInactive());
            pagerAdapter.addFragment(activeJobsFragment, employeerDashboardModel.getJobActive());
        }
        else {
            pagerAdapter.addFragment(activeJobsFragment, employeerDashboardModel.getJobActive());
            pagerAdapter.addFragment(inactiveJobsFragment, employeerDashboardModel.getJobInactive());
        }

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        View view =  tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));

        }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view =  tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }





}
