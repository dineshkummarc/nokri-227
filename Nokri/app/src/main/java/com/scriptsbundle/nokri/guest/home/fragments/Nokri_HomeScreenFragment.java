package com.scriptsbundle.nokri.guest.home.fragments;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.jobs.fragments.Nokri_AllJobsFragment;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.scriptsbundle.nokri.custom.DynamicHeightViewPager;
import com.scriptsbundle.nokri.custom.Nokri_ViewPagerAdapterDynamicHeight;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.scriptsbundle.nokri.guest.blog.fragments.Nokri_BlogDetailFragment;
import com.scriptsbundle.nokri.guest.blog.fragments.Nokri_BlogGridFragment;
import com.scriptsbundle.nokri.guest.blog.models.Nokri_BlogGridModel;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.guest.home.adapters.Nokri_SelectJobsAdapter;
import com.scriptsbundle.nokri.guest.home.models.Nokri_SelectJobsModel;
import com.scriptsbundle.nokri.guest.search.models.Nokri_JobSearchModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_HomeScreenFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener, TabLayout.OnTabSelectedListener {


    private DynamicHeightViewPager viewPager;

    private TabLayout tabLayout;

    private Nokri_FontManager fontManager;
    private RecyclerView topRecyclerview;
    private TextView viewJobsTextView;
    private ImageButton searchImageButton;
    private Nokri_SelectJobsAdapter selectJobsAdapter;

    private List<Nokri_SelectJobsModel> selectJobsModelList;
    private TextView findJobsTextView, searchFromTextView, selectTextView, jobsTextView;
    private EditText searchEditText;
    // private LinearLayout gradiendContainer;
    private boolean isCallFromOnStop = false;
    private ImageButton nextImageButton, previousImageButton;
    RecyclerView.LayoutManager horizontolManager;
    private ImageView headerImageview;
    private View left, right;
    private Nokri_GuestDashboardModel guestDashboardModel;
    private Nokri_DialogManager dialogManager;
    public static JSONObject HOME_RESPONSE;
    public static JSONObject LATEST_JOBS_RESPONSE;
    public static JSONObject FEATURED_JOBS_RESPONSE;
    public static JSONObject BLOGS_RESPONSE;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    @Override
    public void onStop() {
        super.onStop();
        isCallFromOnStop = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        } catch (Exception e) {
        }

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.GONE);
        toolbar.findViewById(R.id.createAlert).setVisibility(View.GONE);
        toolbar.findViewById(R.id.collapse).setVisibility(View.VISIBLE);
    }


    public Nokri_HomeScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        nokri_getHeaderData();
        View view = inflater.inflate(R.layout.fragment_nokri_home_screen, container, false);
        sendFirebaseToken();

        return view;
    }
    private void sendFirebaseToken() {
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        String token = Nokri_SharedPrefManager.getFirebaseNotificationToken(getActivity());

        JsonObject params = new JsonObject();
        params.addProperty("api_fcm_token",token);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.sendFirebaseId(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.sendFirebaseId(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()){
                    Nokri_ToastManager.showShortToast(getActivity(),"Something Wrong");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        guestDashboardModel = Nokri_SharedPrefManager.getGuestSettings(getContext());


        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        fontManager = new Nokri_FontManager();

//        getView().findViewById(R.id.line1).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        getView().findViewById(R.id.line2).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        viewPager = getView().findViewById(R.id.viewpager);

        tabLayout = getView().findViewById(R.id.tabs);


        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        findJobsTextView = getView().findViewById(R.id.txt_find_jobs);
        searchFromTextView = getView().findViewById(R.id.txt_search_jobs);
        searchEditText = getView().findViewById(R.id.edittxt_search);
        selectTextView = getView().findViewById(R.id.txt_select);
        jobsTextView = getView().findViewById(R.id.txt_jobs);

        jobsTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));

        viewJobsTextView = getView().findViewById(R.id.txt_view_jobs);
        topRecyclerview = getView().findViewById(R.id.recyclerview_select_jobs);
        topRecyclerview.setNestedScrollingEnabled(false);
        searchImageButton = getView().findViewById(R.id.img_btn_search);
//        searchImageButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        //Nokri_Utils.setXmlDrawable(searchImageButton,getResources().getDrawable(R.drawable.textfield_drawable));
//        searchImageButton.setBackgroundResource(R.drawable.searchdrawableheader);
//         Nokri_Utils.setXmlDrawableSingleLayer(getContext(),searchImageButton,R.drawable.fieldradius);
        //gradiendContainer = getView().findViewById(R.id.graident_header_container);
        Drawable drawable;
        if (Nokri_Globals.IS_RTL_ENABLED)
            drawable = getResources().getDrawable(R.drawable.home_search_rtl).mutate();
        else
            drawable = getResources().getDrawable(R.drawable.bg_home_search_click).mutate();

        drawable.setColorFilter((Color.parseColor(Nokri_Config.APP_COLOR)), PorterDuff.Mode.SRC_ATOP);
        searchImageButton.setBackground(drawable);

        headerImageview = getView().findViewById(R.id.img_header);


        searchImageButton.setOnClickListener(this);

        tabLayout.setupWithViewPager(viewPager);

        nextImageButton = getView().findViewById(R.id.img_btn_next);
        previousImageButton = getView().findViewById(R.id.img_btn_previous);

        right = getView().findViewById(R.id.view_right);
        left = getView().findViewById(R.id.view_left);
        right.setOnClickListener(this);
        left.setOnClickListener(this);
        nextImageButton.setOnClickListener(this);
        previousImageButton.setOnClickListener(this);


        selectJobsModelList = new ArrayList<>();
        nokri_getHeaderData();
        FirebaseMessaging.getInstance().subscribeToTopic(Nokri_Config.TOPIC_GLOBAL);
        nokri_setupFonts();

        String toolbarTitle = "";

        if (Nokri_Globals.IS_RTL_ENABLED) {
            nextImageButton.setPivotX(180);
            previousImageButton.setPivotX(180);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        if (Nokri_SharedPrefManager.isAccountPublic(getContext())) {
            Nokri_GuestDashboardModel model = Nokri_SharedPrefManager.getGuestSettings(getContext());
            toolbarTitle = model.getHome();
        } else if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {

            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
            toolbarTitle = model.getHome();
        } else if (Nokri_SharedPrefManager.isAccountCandidate(getContext())) {

            Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
            toolbarTitle = model.getHome();

        }
        ApplicationInfo ai;
        PackageManager pm = getContext().getPackageManager();
        try {


            ai = pm.getApplicationInfo(getContext().getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");


        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(applicationName);




    }

    private void nokri_setupViewPager() {

        Nokri_FeaturedJobsFragment.CALLED_FROM_DASHBOARD = false;
        Nokri_RecentJobsFragment.CALLED_FROM_DASHBOARD = false;
        if (Nokri_SharedPrefManager.hideBlog(getContext()).equals("1"))
            Nokri_FeaturedJobsFragment.CALLING_SOURCE = "external";
        else {
            Nokri_FeaturedJobsFragment.CALLING_SOURCE = "";
        }
        if (Nokri_SharedPrefManager.hideBlog(getContext()).equals("1"))
            Nokri_RecentJobsFragment.CALLING_SOURCE = "external";
        else {
            Nokri_RecentJobsFragment.CALLING_SOURCE = "";
        }
        Nokri_ViewPagerAdapterDynamicHeight pagerAdapter = new Nokri_ViewPagerAdapterDynamicHeight(getChildFragmentManager());

        Nokri_FeaturedJobsFragment featuredJobsFragment = new Nokri_FeaturedJobsFragment();

        Nokri_RecentJobsFragment recentJobsFragment = new Nokri_RecentJobsFragment();

        if (Nokri_Globals.IS_RTL_ENABLED) {
            pagerAdapter.addFragment(featuredJobsFragment, guestDashboardModel.getTabPrenium());
            pagerAdapter.addFragment(recentJobsFragment, guestDashboardModel.getTabLatest());


        } else {
            pagerAdapter.addFragment(recentJobsFragment, guestDashboardModel.getTabLatest());
            pagerAdapter.addFragment(featuredJobsFragment, guestDashboardModel.getTabPrenium());

        }

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);


        featuredJobsFragment.nokri_setPagerCallback(new Nokri_FeaturedJobsFragment.nokri_pagerCallback() {
            @Override
            public void onJobClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();

                fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
            }

            @Override
            public void onCompanyClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void onIconClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void loadThisFragmentExternally() {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment featuredJobsFragment = new Nokri_FeaturedJobsFragment();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), featuredJobsFragment).addToBackStack(null).commit();
                Nokri_FeaturedJobsFragment.CALLING_SOURCE = "";

            }

            @Override
            public void onBlogItemClicked(Nokri_BlogGridModel blogGridModel) {
                Nokri_BlogGridFragment.BLOG_ID = blogGridModel.getId();
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment editEmailTemplate = new Nokri_BlogDetailFragment();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), editEmailTemplate).addToBackStack(null).commit();
            }

            @Override
            public void onClickBlogLoadMode() {

                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment editEmailTemplate = new Nokri_BlogGridFragment();
                fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), editEmailTemplate).addToBackStack(null).commit();

            }
        });
        recentJobsFragment.nokri_setPagerCallback(new Nokri_RecentJobsFragment.nokri_pagerCallback() {
            @Override
            public void onJobClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
            }

            @Override
            public void onCompanyClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();
            }

            @Override
            public void onIconClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void onBlogItemClicked(Nokri_BlogGridModel blogGridModel) {
                Nokri_BlogGridFragment.BLOG_ID = blogGridModel.getId();
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment editEmailTemplate = new Nokri_BlogDetailFragment();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), editEmailTemplate).addToBackStack(null).commit();
            }

            @Override
            public void onClickBlogLoadMode() {

                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment editEmailTemplate = new Nokri_BlogGridFragment();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), editEmailTemplate).addToBackStack(null).commit();

            }


            @Override
            public void loadThisFragmentExternally() {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment recentJobsFragment = new Nokri_RecentJobsFragment();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), recentJobsFragment).addToBackStack(null).commit();
                Nokri_RecentJobsFragment.CALLING_SOURCE = "";
            }
        });


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_btn_next:
                if (topRecyclerview != null && horizontolManager != null)
                    topRecyclerview.getLayoutManager().scrollToPosition(((LinearLayoutManager) topRecyclerview.getLayoutManager()).findLastVisibleItemPosition() + 1);
                break;
            case R.id.img_btn_previous:
                topRecyclerview.getLayoutManager().scrollToPosition(((LinearLayoutManager) topRecyclerview.getLayoutManager()).findFirstVisibleItemPosition() - 1);
                break;
            case R.id.view_left:
                topRecyclerview.getLayoutManager().scrollToPosition(((LinearLayoutManager) topRecyclerview.getLayoutManager()).findFirstVisibleItemPosition() - 1);

                break;
            case R.id.view_right:
                if (topRecyclerview != null && horizontolManager != null)
                    topRecyclerview.getLayoutManager().scrollToPosition(((LinearLayoutManager) topRecyclerview.getLayoutManager()).findLastVisibleItemPosition() + 1);

                break;
            case R.id.img_btn_search:
                //      adforest_PayPal("",null,"");
                Nokri_JobSearchModel jobSearchModel = new Nokri_JobSearchModel();

                jobSearchModel.setJobCategory("");
                jobSearchModel.setSubCategory1("");
                jobSearchModel.setSubCategory2("");
                jobSearchModel.setSubCategory3("");


                jobSearchModel.setSearchNow(searchEditText.getText().toString());
                jobSearchModel.setJobQualification("");
                jobSearchModel.setJobType("");
                jobSearchModel.setSalaryCurrency("");
                jobSearchModel.setJobShift("");
                jobSearchModel.setJobLevel("");
                jobSearchModel.setJobSkills("");
                Nokri_SharedPrefManager.saveJobSearchModel(jobSearchModel, getContext());

                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment allJobsFragment = new Nokri_AllJobsFragment();
                Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "external";
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), allJobsFragment).addToBackStack(null).commit();
                break;
            default:
                break;
        }

    }

    private void nokri_setupFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(jobsTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(selectTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(findJobsTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(searchFromTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(viewJobsTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(searchEditText, getActivity().getAssets());

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.custom_tab, tabLayout, false);
            TextView tv = relativeLayout.findViewById(R.id.tab_title);
            tv.setPadding(0, 0, 0, 0);

            if (i == 0) {
                tv.setTextColor(Color.WHITE);
                relativeLayout.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                tv.setPadding(0, 0, 0, 0);


                if (Nokri_Globals.IS_RTL_ENABLED) {

                    tv.setText(guestDashboardModel.getTabPrenium());
                } else {
                    tv.setText(guestDashboardModel.getTabLatest());
                    // tv.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                }
            } else if (i == 1) {
                if (Nokri_Globals.IS_RTL_ENABLED) {
                    tv.setText(guestDashboardModel.getTabLatest());
                } else
                    tv.setText(guestDashboardModel.getTabPrenium());
                relativeLayout.setBackgroundColor(getResources().getColor(R.color.light_grey));

            }
            fontManager.nokri_setOpenSenseFontTextView(tv, getActivity().getAssets());
            tabLayout.getTabAt(i).setCustomView(relativeLayout);

        }
    }


    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.edittxt_search:
                if (selected)
                    searchEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                else
                    searchEditText.setHintTextColor(getResources().getColor(R.color.grey));
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        View view = tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        tab.getCustomView().setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        custom.setTextColor(Color.WHITE);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView custom = view.findViewById(R.id.tab_title);
        custom.setTextColor(getResources().getColor(R.color.black));
        tab.getCustomView().setBackgroundColor(getResources().getColor(R.color.light_grey));
        custom.setTextColor(Color.BLACK);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void nokri_getHeaderData() {
        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(getActivity());
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Log.d("opt1","Call made");

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getHomeFull(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getHomeFull(Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.d("opt1","Call Finished");
                        BLOGS_RESPONSE = response.getJSONObject("data").getJSONObject("home_blog");
                        LATEST_JOBS_RESPONSE = response.getJSONObject("data").getJSONObject("all_jobs");
                        FEATURED_JOBS_RESPONSE = response.getJSONObject("data").getJSONObject("premium_jobs");
                        nokri_setupViewPager();
                        tabLayout.setOnTabSelectedListener(Nokri_HomeScreenFragment.this);
                        nokri_setupFonts();
                        JSONObject data = response.getJSONObject("data").getJSONObject("home").getJSONObject("data");
//                        VignetteFilterTransformation transformation = new VignetteFilterTransformation(getActivity());
                        //.transform(new VignetteFilterTransformation(mContext, new PointF(0.5f, 0.5f), new float[] { 0.0f, 0.0f, 0.0f }, 0f, 0.75f))
                        if (!TextUtils.isEmpty(data.getString("img")))
                            Picasso.get().load(data.getString("img")).centerCrop().fit().into(headerImageview);
                        findJobsTextView.setText(data.getString("heading"));
                        searchFromTextView.setText(data.getString("tagline"));
                        searchEditText.setHint(data.getString("placehldr"));
                        String selectCategoryText[] = data.getString("cats_text").split(" ");

                        if (selectCategoryText.length == 1) {
                            selectTextView.setText(selectCategoryText[0]);
                            jobsTextView.setText("");
                        } else if (selectCategoryText.length > 1) {

                            String startText = "";
                            String endText = "";

                            for (int i = 0; i < selectCategoryText.length; i++) {

                                if (i + 1 < selectCategoryText.length) {
                                    startText += selectCategoryText[i] + " ";
                                    selectTextView.setText(startText);
                                }
                                jobsTextView.setText(selectCategoryText[i]);

                            }
                        }

                        JSONArray categoryArray = data.getJSONArray("cat_icons");
                        if (categoryArray.length() <= 0) {
                            left.setVisibility(View.GONE);
                            right.setVisibility(View.GONE);
                            getView().findViewById(R.id.select_jobs_container).setVisibility(View.GONE);
                            getView().findViewById(R.id.top_recyclerview_container);
                        }
                        for (int i = 0; i < categoryArray.length(); i++) {

                            JSONObject category = categoryArray.getJSONObject(i);
                            Nokri_SelectJobsModel model = new Nokri_SelectJobsModel();
                            model.setId(category.getInt("job_category"));
                            model.setLogo(category.getString("img"));
                            model.setJobTitle(category.getString("name"));
                            model.setJobsInclude(category.getString("count"));
                            selectJobsModelList.add(model);
                        }
                        nokri_setupAdapter();


                        Log.d("opt1","Call Finished");
                        dialogManager.hideAfterDelay();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    }

                } else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_setupAdapter() {
        selectJobsAdapter = new Nokri_SelectJobsAdapter(selectJobsModelList, getContext(), new Nokri_SelectJobsAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Nokri_SelectJobsModel item) {
                Nokri_JobSearchModel jobSearchModel = new Nokri_JobSearchModel();
                //     Nokri_ToastManager.showShortToast(getContext(),item.getJobTitle());
                jobSearchModel.setJobCategory(item.getId() + "");
                jobSearchModel.setSubCategory1("");
                jobSearchModel.setSubCategory2("");
                jobSearchModel.setSubCategory3("");


                jobSearchModel.setSearchNow("");
                jobSearchModel.setJobQualification("");
                jobSearchModel.setJobType("");
                jobSearchModel.setSalaryCurrency("");
                jobSearchModel.setJobShift("");
                jobSearchModel.setJobLevel("");
                jobSearchModel.setJobSkills("");
                Nokri_SharedPrefManager.saveJobSearchModel(jobSearchModel, getContext());

                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment allJobsFragment = new Nokri_AllJobsFragment();
                Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "external";
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), allJobsFragment).addToBackStack(null).commit();

            }
        }

        );


        horizontolManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        topRecyclerview.setLayoutManager(horizontolManager);

        topRecyclerview.setItemAnimator(new DefaultItemAnimator());
        topRecyclerview.setAdapter(selectJobsAdapter);

    }


}

