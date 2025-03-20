package com.scriptsbundle.nokri.candidate.profile.fragments;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_ContactModel;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_DescriptionModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.DynamicHeightViewPager;
import com.scriptsbundle.nokri.custom.Nokri_ViewPagerAdapterDynamicHeight;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_RecentJobsModel;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CompanyPublicProfileFragment extends Fragment implements TabLayout.OnTabSelectedListener,View.OnClickListener{
    private Nokri_FontManager fontManager;
    private TextView nameTextView,addressTextView;
    private Button followingButton,messageButton;
    private DynamicHeightViewPager viewPager;
    private TabLayout tabLayout;
    private String title;
    private ImageView facebookImageView,twitterImageView,googlePlusImageView,linkedinImageView;
    private int nextPage=1;
    private boolean hasNextPage;
    private ArrayList<Nokri_RecentJobsModel> modelList;
    public static String COMPANY_ID;
    private String message;
    private CircularImageView profileImage;
    private static final DateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
    private String facebook,twitter,googlePlus,linkedin;
    private String latitude,longitude;
    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private Nokri_EmployeerDashboardModel employeerDashboardModel;
    private Nokri_RecentJobsFragment recentJobsFragment;
    private Nokri_CompanyDetailFragment companyDetailFragment;
    private Nokri_ContactModel contactModel;

    private Nokri_DialogManager dialogManager;
    boolean map_Switch;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!mUserSeen && isVisibleToUser) {
            mUserSeen = true;
            onUserFirstSight();
            tryViewCreatedFirstSight();
        }
        onUserVisibleChanged(isVisibleToUser);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Override this if you want to get savedInstanceState.
        mViewCreated = true;
        tryViewCreatedFirstSight();
    }
    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }


    protected void onViewCreatedFirstSight(View view) {

    }

    protected void onUserFirstSight() {

    }

    protected void onUserVisibleChanged(boolean visible) {
        if(getContext()!=null)
        {
            if(visible){


            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    private ArrayList<Nokri_DescriptionModel> descriptionModelList;

    private String aboutMeTextView,aboutMeDataTextView,yourDashboardTexView,profileDetailTextView;
    private String pleaseLoginAsCandidate;



    public Nokri_CompanyPublicProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nokri_company_public_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        employeerDashboardModel = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
        nokri_initialize();
        nextPage = 1;
        nokri_loadMore(true);
    }




    private void nokri_initialize(){

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        fontManager = new Nokri_FontManager();
        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.tabs);

        if(Nokri_Globals.IS_RTL_ENABLED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

        }


        nameTextView = getView().findViewById(R.id.txt_name);
        addressTextView = getView().findViewById(R.id.txt_address);
        profileImage = getView().findViewById(R.id.img_profile);

        profileImage.setBorderColor(Color.parseColor(Nokri_Config.APP_COLOR));
        profileImage.setShadowColor(Color.parseColor(Nokri_Config.APP_COLOR));

        followingButton = getView().findViewById(R.id.btn_following);
        Nokri_Utils.setRoundButtonColor(getContext(),followingButton);
        if(Nokri_SharedPrefManager.isAccountEmployeer(getContext()))
            followingButton.setVisibility(View.GONE);

        messageButton = getView().findViewById(R.id.btn_message);
        followingButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);

        facebookImageView = getView().findViewById(R.id.img_facebook);
        googlePlusImageView = getView().findViewById(R.id.img_gooogle_plus);
        twitterImageView = getView().findViewById(R.id.img_twitter);
        linkedinImageView = getView().findViewById(R.id.img_linkedin);

        facebookImageView.setOnClickListener(this);
        googlePlusImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);

        modelList = new ArrayList<>();
        descriptionModelList = new ArrayList<>();


        contactModel = new Nokri_ContactModel();

        nokri_setFonts();


    }
    private void nokri_setupViewPager(){
        Nokri_ViewPagerAdapterDynamicHeight pagerAdapter = new Nokri_ViewPagerAdapterDynamicHeight(getChildFragmentManager());
        recentJobsFragment = new Nokri_RecentJobsFragment();

        // Log.d("modelllll",modelList.toString());
        recentJobsFragment.setData(hasNextPage,modelList);
        recentJobsFragment.nokri_setPagerCallback(new Nokri_RecentJobsFragment.nokri_pagerCallback() {
            @Override
            public void onItemClick(Nokri_RecentJobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Log.d("txxxxxxt","company -- >"+model.getCompanyId());

                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();

                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();

            }

            @Override
            public void onLoadMoreClick() {

                nokri_loadMore(false);
            }
        });
        companyDetailFragment = new Nokri_CompanyDetailFragment();
        companyDetailFragment.setData(latitude,longitude,map_Switch,profileDetailTextView,aboutMeTextView,aboutMeDataTextView,descriptionModelList);
        companyDetailFragment.setRatingObject(rating,COMPANY_ID);
        companyDetailFragment.nokri_setContactModel(contactModel);
        if(Nokri_Globals.IS_RTL_ENABLED){
            pagerAdapter.addFragment(companyDetailFragment, employeerDashboardModel.getTabPublicDetails());
            pagerAdapter.addFragment(recentJobsFragment, employeerDashboardModel.getTabPublicOpen());

        }
        else {
            pagerAdapter.addFragment(recentJobsFragment, employeerDashboardModel.getTabPublicOpen());
            pagerAdapter.addFragment(companyDetailFragment, employeerDashboardModel.getTabPublicDetails());
        }
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Nokri_Config.APP_COLOR));
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, tabLayout, false);
            TextView tv= relativeLayout.findViewById(R.id.tab_title);
            if(i==0) {
                if(Nokri_Globals.IS_RTL_ENABLED)
                    tv.setText(employeerDashboardModel.getTabPublicDetails());
                else
                    tv.setText(employeerDashboardModel.getTabPublicOpen());

                tv.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
            }
            else
            if(i==1) {
                if(Nokri_Globals.IS_RTL_ENABLED)
                    tv.setText(employeerDashboardModel.getTabPublicOpen());
                else
                    tv.setText(employeerDashboardModel.getTabPublicDetails());

            }
            fontManager.nokri_setMonesrratSemiBioldFont(tv,getActivity().getAssets());
            tabLayout.getTabAt(i).setCustomView(relativeLayout);

        }

        TextView recentJobsTag = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_view, null);
        TextView companyDetailTag = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab_view, null);
        recentJobsTag.setText(employeerDashboardModel.getTabPublicOpen());
        companyDetailTag.setText(employeerDashboardModel.getTabPublicDetails());

        tabLayout.setOnTabSelectedListener(Nokri_CompanyPublicProfileFragment.this);
        //

        dialogManager.hideAfterDelay(3000);}

    private void nokri_setFonts(){

        fontManager.nokri_setOpenSenseFontTextView(nameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView,getActivity().getAssets());


        fontManager.nokri_setOpenSenseFontButton(followingButton,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(messageButton,getActivity().getAssets());

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_following:
                if(Nokri_SharedPrefManager.isAccountCandidate(getContext()))
                    nokri_followCompany();
                else
                    Nokri_ToastManager.showLongToast(getContext(), pleaseLoginAsCandidate);
                break;
            case R.id.btn_message:
                break;
            case R.id.img_facebook:
                if(facebook!=null && !facebook.isEmpty())
                    Nokri_Utils.opeInBrowser(getContext(),facebook);
                else
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.NO_URL_FOUND);
                break;
            case R.id.img_twitter:
                if(twitter!=null && !twitter.isEmpty())
                    Nokri_Utils.opeInBrowser(getContext(),twitter);
                else
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.NO_URL_FOUND);
                break;
            case R.id.img_linkedin:
                if(linkedin!=null && !linkedin.isEmpty())
                    Nokri_Utils.opeInBrowser(getContext(),linkedin);
                else
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.NO_URL_FOUND);
                break;
            case R.id.img_gooogle_plus:
                if(googlePlus!=null && !googlePlus.isEmpty())
                    Nokri_Utils.opeInBrowser(getContext(),googlePlus);
                else
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.NO_URL_FOUND);
                break;
            default:
                break;
        }
    }



    JSONObject rating;

    protected void nokri_loadMore(final Boolean showAlert) {

        if(showAlert)
        {
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();
        params.addProperty("company_id",COMPANY_ID);

        params.addProperty("page_number",nextPage);

        Call<ResponseBody> myCall;

        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployeerPublicProfile(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getEmployeerPublicProfile(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (showAlert){
                    mainLayout.setVisibility(View.VISIBLE);
                    shimmerContainer.stopShimmer();
                    shimmerContainer.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    Nokri_Utils.isCallRunning = false;
                }
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());


                        try {
                            TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                            toolbarTitleTextView.setText(response.getString("page_title"));
                        }
                        catch (Exception e ){
                            e.printStackTrace();
                        }
                        if(response.getBoolean("success")==false)
                        {
                            getView().findViewById(R.id.main_container).setVisibility(View.GONE);
                            getView().findViewById(R.id.private_container).setVisibility(View.VISIBLE);
                            TextView messageTextView = getView().findViewById(R.id.txt_message);
                            messageTextView.setText(response.getString("message"));
                            dialogManager.hideAlertDialog();
                            return;
                        }


                        Log.d("REsponseeeeeee",response.toString());
                        JSONObject data = response.getJSONObject("data");
                        JSONObject userContact = data.getJSONObject("user_contact");

                        contactModel.setButtonText(userContact.getString("btn_txt"));
                        contactModel.setContactTitleText(userContact.getString("receiver_name"));
                        contactModel.setNameHint(userContact.getString("sender_name"));
                        contactModel.setEmailHint(userContact.getString("sender_email"));
                        contactModel.setSubjectHint(userContact.getString("sender_subject"));
                        contactModel.setMessageHint(userContact.getString("sender_message"));
                        contactModel.setReceiverId(userContact.getString("receiver_id"));
                        contactModel.setReceiverName(userContact.getString("receiver_name"));
                        contactModel.setReceiverEmail(userContact.getString("receiver_email"));
                        JSONArray extras = data.getJSONArray("extra");



                        for(int i =0;i<extras.length();i++) {

                            JSONObject object = extras.getJSONObject(i);
                            if (object.getString("field_type_name").equals("comp_profile"))
                                title = object.getString("key");
                            else if (object.getString("field_type_name").equals("comp_follow"))
                                followingButton.setText(object.getString("key"));
                            else if (object.getString("field_type_name").equals("comp_message"))
                                messageButton.setText(object.getString("key"));
                            else if (object.getString("field_type_name").equals("comp_detail"))
                                profileDetailTextView =  object.getString("key");
                            else if (object.getString("field_type_name").equals("map_switch"))
                                if (object.getString("value").equals("0"))
                                    map_Switch = false;
                                else
                                    map_Switch = true;
                            else if (object.getString("field_type_name").equals("page_title")) {
                                TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                                toolbarTitleTextView.setText(object.getString("key"));

                            }
                            else if (object.getString("field_type_name").equals("login_as"))
                                pleaseLoginAsCandidate =  object.getString("key");

                        }
                        // Log.d("txxxxxxt","company -- >"+data.toString());
                        JSONObject pagination = data.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");



                        message = response.getString("message");

                        rating = data.getJSONObject("user_reviews");

                        JSONArray companiesArray = data.getJSONArray("jobs");

                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_RecentJobsModel model = new Nokri_RecentJobsModel();
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                Log.d("txxxxxxt","company -- >"+object.toString() +" "+COMPANY_ID +" "+nextPage);
                                if(object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_expiry"))
                                    model.setTimeRemaining(object.getString("key")+" "+object.getString("value"));
                             /*   else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));*/
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location")) {
                                    model.setAddress(object.getString("value"));

                                }
                                //  else if (object.getString("field_type_name").equals("company_id")) {
                                model.setCompanyId(COMPANY_ID);

                                //}
                                if(j+1 == dataArray.length())
                                    modelList.add(model);
                            }

                            if(!showAlert)
                            {
                                if(recentJobsFragment!=null) {
                                    recentJobsFragment.setData(hasNextPage, modelList);
                                    recentJobsFragment.setupAdapter();
                                }
                            }
                        }
                        Log.d("Insied ","load mode");
                        //     setupAdapter();


//                        listener.loadData();
                        if(!hasNextPage){

                            //        progressBar.setVisibility(View.GONE);
                        }
                        //progressBar.setVisibility(View.GONE);
                        JSONObject basicInfo = data.getJSONObject("basic_ifo");
                        JSONObject socialObject = basicInfo.getJSONObject("social");


                        if(socialObject.getBoolean("is_show")) {
                            facebook = socialObject.getString("facebook");
                            twitter = socialObject.getString("twitter");
                            linkedin = socialObject.getString("linkedin");
                            googlePlus = socialObject.getString("google_plus");

                            if (facebook.trim().isEmpty())
                                facebookImageView.setVisibility(View.GONE);
                            if (twitter.trim().isEmpty())
                                twitterImageView.setVisibility(View.GONE);
                            if (linkedin.trim().isEmpty())
                                linkedinImageView.setVisibility(View.GONE);
                            if (googlePlus.trim().isEmpty())
                                googlePlusImageView.setVisibility(View.GONE);
                        }
                        else
                            getView().findViewById(R.id.socail_container).setVisibility(View.GONE);

                        JSONObject profileImageObject = basicInfo.getJSONObject("profile_img");
                        if(!TextUtils.isEmpty(profileImageObject.getString("img")))
                            Picasso.get().load(profileImageObject.getString("img")).fit().centerCrop().into(profileImage);

                        JSONArray jsonArray = basicInfo.getJSONArray("info");
                        JSONArray basicInfoExtrasArray = basicInfo.getJSONArray("extra");


                        String defaultAboutText = null;

                        for(int i=0;i<basicInfoExtrasArray.length();i++) {
                            JSONObject extra = basicInfoExtrasArray.getJSONObject(i);
                            if (extra.getString("field_type_name").equals("emp_about")) {
                                defaultAboutText = extra.getString("value");
                                //  break;
                            }
                        }





                        for(int i = 0; i<jsonArray.length();i++){
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            if(jsonData.getString("field_type_name").equals("emp_name"))
                            {
                                nameTextView.setText(jsonData.getString("value"));
                                continue;
                            }


                            if(jsonData.getString("field_type_name").equals("emp_adress")){
                                addressTextView.setText(jsonData.getString("value"));
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(jsonData.getString("key"));
                                model.setDescription(jsonData.getString("value"));
                                descriptionModelList.add(model);
                                continue;
                            }
                            if(jsonData.getString("field_type_name").equals("about_me"))
                            {       aboutMeTextView = jsonData.getString("key");
                                String aboutMeText = Nokri_Utils.stripHtml(jsonData.getString("value")).toString();
                                if(aboutMeText.isEmpty() || aboutMeText == null)
                                    aboutMeDataTextView = defaultAboutText;
                                else
                                    aboutMeDataTextView = aboutMeText;
                                Nokri_SharedPrefManager.saveAbout(jsonData.getString("value"),getContext());
                                continue;
                            }
                            if(jsonData.getString("field_type_name").equals("loc")){

                                continue;
                            }
                            if(jsonData.getString("field_type_name").equals("emp_lat")){
                                latitude = jsonData.getString("value");
                                Log.d("loccccccc m",latitude);
                                continue;

                            }
                            if(jsonData.getString("field_type_name").equals("emp_long")){
                                longitude = jsonData.getString("value");
                                Log.d("loccccccc m",longitude);
                                continue;

                            }

                            if(jsonData.getString("field_type_name").equals("your_dashbord")){
                                yourDashboardTexView = jsonData.getString("key");
                                continue;
                            }
                            if(jsonData.getString("key").equals("dp")||jsonData.getString("key").equals("cover"))
                                continue;
                            Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                            model.setTitle(jsonData.getString("key"));
                            model.setDescription(jsonData.getString("value"));
                            model.setFieldTypeName(jsonData.getString("field_type_name"));
                            if(jsonData.getString("field_type_name").equals("emp_est"))
                                Nokri_SharedPrefManager.saveEstablishedSince(jsonData.getString("value"),getContext());
                            if(jsonData.getString("field_type_name").equals("emp_nos"))
                                Nokri_SharedPrefManager.saveNumberOfEmployees(jsonData.getString("value"),getContext());
                            if(jsonData.getString("field_type_name").equals("emp_rgstr"))
                                Nokri_SharedPrefManager.saveMemberSince(jsonData.getString("value"),getContext());
                            if(jsonData.getString("field_type_name").equals("emp_head"))
                                Nokri_SharedPrefManager.saveHeadline(jsonData.getString("value"),getContext());



                            descriptionModelList.add(model);

                        }

 /*                       if(companyDetailFragment!=null)
                        companyDetailFragment.setData();*/


                        if(showAlert) {
                            nokri_setupViewPager();
                        }

                    } catch (Exception e) {
                        if(showAlert){
                            mainLayout.setVisibility(View.VISIBLE);
                            shimmerContainer.stopShimmer();
                            shimmerContainer.setVisibility(View.GONE);
                            loadingLayout.setVisibility(View.GONE);
                            Nokri_Utils.isCallRunning = false;
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();}
                        e.printStackTrace();
                    }

                }
                else {
                    if(showAlert) {

                        mainLayout.setVisibility(View.VISIBLE);
                        shimmerContainer.stopShimmer();
                        shimmerContainer.setVisibility(View.GONE);
                        loadingLayout.setVisibility(View.GONE);
                        Nokri_Utils.isCallRunning = false;
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
                if (showAlert){
                    mainLayout.setVisibility(View.VISIBLE);
                    shimmerContainer.stopShimmer();
                    shimmerContainer.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    Nokri_Utils.isCallRunning = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    protected void nokri_followCompany() {


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        JsonObject params = new JsonObject();
        params.addProperty("company_id",COMPANY_ID);

        params.addProperty("follow_date",sdf.format(new Date()));

        Call<ResponseBody> myCall;

        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postFollowCompany(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postFollowCompany(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {


                        JSONObject response = new JSONObject(responseObject.body().string());

                        Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                        dialogManager.hideAlertDialog();

                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();

                    }

                }
                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }
    protected void setNextPage(int nextPage){
        this.nextPage = nextPage;
    }
    /*protected boolean hasNextPage(){
        return hasNextPage;
    }
    protected void setHasNextPage(boolean hasNextPage){
        this.hasNextPage = hasNextPage;
    }
    protected int getNextPage(){
        return nextPage;
    }
    protected ArrayList<Nokri_RecentJobsModel>getJobsList(){
        return modelList;
    }*/
    protected String getMessage(){
        return message;
    }


}