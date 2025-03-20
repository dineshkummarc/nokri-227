package com.scriptsbundle.nokri.candidate.profile.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.youtube.player.YouTubePlayer;
import com.mikhaellopez.circularimageview.CircularImageView;

import com.scriptsbundle.nokri.Video.VideoPlayerActivity;
import com.scriptsbundle.nokri.Video.YoutubeActivity;
import com.scriptsbundle.nokri.activities.Nokri_ImagePreview;
import com.scriptsbundle.nokri.activities.Rating;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.edit.models.CandidateAvailabilityList;
import com.scriptsbundle.nokri.candidate.profile.adapter.CandidateAvailabilityAdapter;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_SkillsAdapter;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_ProgressModel;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_DescriptionModel;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.fragments.Nokri_CandidateEditProfileFragment;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_MyProfileAdapter;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_PortfolioAdapter;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_MyProfileModel;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_PortfolioModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.utils.NonSchrollingListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_MyProfileFragment extends Fragment  implements View.OnClickListener,Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener {
    private RecyclerView recyclerView1,recyclerView2,recyclerView3,certificationRecyclerview,portfolioRecyclerview;
    private List<Nokri_DescriptionModel> modelList;
    private List<Nokri_MyProfileModel>modelList2,modelList3,modelList4;
    private TextView aboutMeTextView,aboutMeDataTextView,educationalInfoTextView,workExperienceTextView,nameTextView,jobTextView,skillsTextiew,certificationTextView,portfolioTextView;
    private Nokri_FontManager fontManager;
    private CircularImageView profileImageView;
    private ImageView facebookImageView,twitterImageView,googlePlusImageView,linkedinImageView;
    private List<Nokri_PortfolioModel>potfolionModelList;
    private TextView portfolioGoneTextView;
    Nokri_DescriptionRecyclerViewAdapter adapter;
    private String facebook,twitter,linkedin,googlePlus;
    private TextView noSkillsTextView;
    private Nokri_DialogManager dialogManager;
    private TextView youtubeTextView,youtubeGoneTextView;
    private YouTubePlayer YPlayer;
    private RecyclerView skillsRecyclerView;
    private List<Nokri_ProgressModel>skillsModelList;
    NumberProgressBar profileProgress;
    TextView profileProgressLabel,progressText;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout,progressLayout;
    //    FrameLayout youtubeVideoPlayer;
//    AndExoPlayerView normalVideoPlayer;
    TextView resumeVideoText;
    String videoUrl = "";
    boolean ytResumeType = false;
    ImageView playIcon,playIconPortfolio;
    LinearLayout portfolioVideoContainer;
    TextView labelCandidateAvailability,status;
    NonSchrollingListView listViewCandidateDays;
    LinearLayout availabilityContainer;
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setUpFonts();
        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        toolbarTitleTextView.setText(model.getProfile());
        nokri_getProfile();
        nokri_getEducation();
        nokri_getProfession();
        nokri_getCertification();
        nokri_getPortfolio();
    }

    public Nokri_MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_my_profile, container, false);
    }

    private  void nokri_setupDescriptionRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView1.setLayoutManager(layoutManager);

        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_DescriptionRecyclerViewAdapter(modelList,getContext(),0,this);
        recyclerView1.setAdapter(adapter);

    }
    private void nokri_initialize(){

        fontManager = new Nokri_FontManager();

        availabilityContainer = getView().findViewById(R.id.availabilityContainer);
        portfolioVideoContainer = getView().findViewById(R.id.portfolioVideoContainer);
        playIcon = getView().findViewById(R.id.playIcon);
        playIconPortfolio = getView().findViewById(R.id.playIconPortfolio);
        resumeVideoText = getView().findViewById(R.id.txt_resume);
//        normalVideoPlayer = getView().findViewById(R.id.normalVideoPlayer);
//        youtubeVideoPlayer = getView().findViewById(R.id.youtubeVideoPlayer);
        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        progressLayout = getView().findViewById(R.id.progressLayout);

        profileProgress = getView().findViewById(R.id.profileProgress);
        profileProgressLabel = getView().findViewById(R.id.profileProgressPlaceholder);
        progressText = getView().findViewById(R.id.percentTxt);
        profileProgress.setProgress(0);
        recyclerView1 = getView().findViewById(R.id.recyclerview1);
        recyclerView2 = getView().findViewById(R.id.recyclerview_educational_info);
        recyclerView3 = getView().findViewById(R.id.recyclerview_work_experience);
        skillsRecyclerView = getView().findViewById(R.id.skills_recyclerview);
        certificationRecyclerview = getView().findViewById(R.id.recyclerview_certification);
        portfolioRecyclerview = getView().findViewById(R.id.recyclerview_portfolio);

        recyclerView1.setNestedScrollingEnabled(false);
        recyclerView2.setNestedScrollingEnabled(false);
        recyclerView3.setNestedScrollingEnabled(false);
        skillsRecyclerView.setNestedScrollingEnabled(false);
        certificationRecyclerview.setNestedScrollingEnabled(false);
        portfolioRecyclerview.setNestedScrollingEnabled(false);


        aboutMeTextView = getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView = getView().findViewById(R.id.txt_about_me_data);
        nameTextView = getView().findViewById(R.id.txt_name);
        jobTextView = getView().findViewById(R.id.txt_job);
        skillsTextiew = getView().findViewById(R.id.txt_skills);
        educationalInfoTextView = getView().findViewById(R.id.txt_educational_info);
        workExperienceTextView = getView().findViewById(R.id.txt_work_experence);
        portfolioTextView = getView().findViewById(R.id.txt_portfolio);
        portfolioGoneTextView = getView().findViewById(R.id.txt_no_porfolio);
        youtubeTextView = getView().findViewById(R.id.txt_youttube);
        youtubeGoneTextView = getView().findViewById(R.id.txt_no_youtube);

        noSkillsTextView = getView().findViewById(R.id.txt_no_skills);


        profileImageView = getView().findViewById(R.id.img_logo);
        certificationTextView = getView().findViewById(R.id.txt_certificaton);

        facebookImageView = getView().findViewById(R.id.img_facebook);
        twitterImageView = getView().findViewById(R.id.img_twitter);
        googlePlusImageView = getView().findViewById(R.id.img_gooogle_plus);
        linkedinImageView = getView().findViewById(R.id.img_linkedin);




        modelList = new ArrayList<>();
        modelList2 = new ArrayList<>();
        modelList3 = new ArrayList<>();
        modelList4 = new ArrayList<>();
        skillsModelList = new ArrayList<>();
        potfolionModelList = new ArrayList<>();
        facebookImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        googlePlusImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
    }

    private void nokri_getProfile(){
        dialogManager = new Nokri_DialogManager();

        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateProfile(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateProfile( Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if(responseObject.isSuccessful()){
                    try {
                        String aboutEmpty= null,skillsEmpty = null;
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Nokri_Globals.EDIT_MESSAGE = jsonObject.getString("message");
                        JSONObject data = jsonObject.getJSONObject("data");

                        JSONArray extrasArray = data.getJSONArray("extra");
                        for (int i =0;i<extrasArray.length();i++){
                            JSONObject extra = extrasArray.getJSONObject(i);
                            if(extra.getString("field_type_name").equals("cand_about"))
                                aboutEmpty = extra.getString("value");
                            if(extra.getString("field_type_name").equals("cand_skills"))
                                skillsTextiew.setText(extra.getString("key"));
                                skillsEmpty = extra.getString("value");
                            if(extra.getString("field_type_name").equals("percentage_switch"))
                                if (!extra.getBoolean("value"))
                                    progressLayout.setVisibility(View.GONE);
                                else
                                    progressLayout.setVisibility(View.VISIBLE);
                            if (extra.getString("field_type_name").equals("is_video_upload")){
                                resumeVideoText.setText(extra.getString("key"));
                                if (extra.getString("value").equals("1")){
                                    ytResumeType = false;
                                }else{
                                    ytResumeType = true;
                                }
                            }
                            if (extra.getString("field_type_name").equals("cand_intro_video")){
                                videoUrl = extra.getString("value");
                            }

                        }

                        playIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ytResumeType){
                                    Intent uTube = new Intent(getActivity(), YoutubeActivity.class);
                                    uTube.putExtra("url",videoUrl);
                                    startActivity(uTube);
                                }else{
                                    Intent i = new Intent(getActivity(), VideoPlayerActivity.class);
                                    i.putExtra("videoPath",videoUrl);
                                    startActivity(i);
                                }
                            }
                        });





                        if(!TextUtils.isEmpty(data.getString("profile_img")))
                            Picasso.get().load(data.getString("profile_img")).fit().centerCrop().into(profileImageView);
                        JSONArray skillsArray = data.getJSONArray("skills");

                        if(skillsArray.length()>0) {

                            for (int i = 0; i < skillsArray.length(); i++) {
                                JSONObject obj = skillsArray.getJSONObject(i);
                                Nokri_ProgressModel model = new Nokri_ProgressModel();
                                model.setTitle(obj.getString("name"));

                                try {
                                    model.setProgress(Integer.parseInt(obj.getString("Percent value")));
                                } catch (NumberFormatException ex) {
                                    model.setProgress(0);
                                } finally {
                                    skillsModelList.add(model);
                                }

                            }
                            nokri_setupSkillsRecyclerView();
                        }
                        else
                        {
                            noSkillsTextView.setVisibility(View.VISIBLE);
                            noSkillsTextView.setText(skillsEmpty);
                        }


                        JSONObject socialObject = data.getJSONObject("social");
                        facebook = socialObject.getString("facebook");
                        twitter = socialObject.getString("twitter");
                        linkedin = socialObject.getString("linkedin");
                        googlePlus = socialObject.getString("google_plus");
                        if(facebook.trim().isEmpty())
                            facebookImageView.setVisibility(View.GONE);
                        else
                            facebookImageView.setVisibility(View.VISIBLE);
                        if(twitter.trim().isEmpty())
                            twitterImageView.setVisibility(View.GONE);
                        else
                            twitterImageView.setVisibility(View.VISIBLE);
                        if(linkedin.trim().isEmpty())
                            linkedinImageView.setVisibility(View.GONE);
                        else
                            linkedinImageView.setVisibility(View.VISIBLE);
                        if(googlePlus.trim().isEmpty())
                            googlePlusImageView.setVisibility(View.GONE);
                        else
                            googlePlusImageView.setVisibility(View.VISIBLE);
                        JSONArray infoArray = data.getJSONArray("info");
                        for(int i=0;i<infoArray.length();i++)
                        {       JSONObject response = infoArray.getJSONObject(i);
                            if(response.getString("field_type_name").equals("cand_name"))
                                nameTextView.setText(response.getString("value").trim());
                            else
                            if(response.getString("field_type_name").equals("cand_hand"))
                                jobTextView.setText(response.getString("value").trim());
                            else
                            if(response.getString("field_type_name").equals("about_me"))
                            { aboutMeTextView.setText(response.getString("key").trim());
                                String text = Nokri_Utils.stripHtml(response.getString("value")).toString();
                                if(text.isEmpty())
                                    aboutMeDataTextView.setText(aboutEmpty.trim());
                                else
                                    aboutMeDataTextView.setText(Nokri_Utils.stripHtml(response.getString("value").trim()));}
                            else if(!response.getString("field_type_name").equals("your_dashbord")&&!response.getString("field_type_name").equals("loc")&&!response.getString("field_type_name").equals("cand_long")&&!response.getString("field_type_name").equals("cand_lat") && !response.getString("field_type_name").equals("set_profile"))
                            {
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(response.getString("key"));
                                model.setDescription(response.getString("value"));
                                modelList.add(model);
                            }
                        }
                        nokri_setupDescriptionRecyclerView();
//                        populateCustomFields(customFields);

                        String profileProgressLabelString = data.getJSONObject("cand_scoring").getString("key");
                        String scoringString = data.getJSONObject("cand_scoring").getString("value");

                        try {
                            profileProgressLabel.setText(profileProgressLabelString);
                            progressText.setText(scoringString + "%");
                            profileProgress.setProgress(Integer.parseInt(scoringString));
                            profileProgress.setProgressTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            profileProgress.offsetLeftAndRight(10);
                        }catch (Exception e){
                            profileProgress.setProgress(0);
                            e.printStackTrace();
                        }

                        try {
                            JSONObject scheduledHours = data.getJSONObject("scheduled_hours");
                            JSONObject extra = scheduledHours.getJSONObject("extra");
                            labelCandidateAvailability = getView().findViewById(R.id.txt_cand_availability);
                            status = getView().findViewById(R.id.status);
                            status.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(getActivity(), Rating.class);
                                    startActivity(i);
                                }
                            });
                            listViewCandidateDays = getView().findViewById(R.id.daysList);



                            labelCandidateAvailability.setText(extra.getString("cand_availability"));
                            status.setText(scheduledHours.getString("status"));

                            ArrayList<CandidateAvailabilityList.Day> days = new ArrayList<>();

                            JSONArray daysArray = scheduledHours.getJSONArray("days");
                            if (daysArray.length()==0){
                                availabilityContainer.setVisibility(View.GONE);
                            }else{
                                for (int i=0;i<daysArray.length();i++){
                                    CandidateAvailabilityList.Day object = new CandidateAvailabilityList.Day();
                                    object.setStart_time(daysArray.getJSONObject(i).getString("start_time"));
                                    object.setEnd_time(daysArray.getJSONObject(i).getString("end_time"));
                                    object.setDay_name(daysArray.getJSONObject(i).getString("day_key"));
                                    object.setClosed(daysArray.getJSONObject(i).getBoolean("closed"));
                                    days.add(object);
                                }
                                CandidateAvailabilityAdapter adapter = new CandidateAvailabilityAdapter(getActivity(),days,extra.getString("cloes_now"));
                                listViewCandidateDays.setAdapter(adapter);


                            }
                        }catch (Exception e){
                            Log.e("Scheduled Hours Section", e.getMessage());
                            availabilityContainer.setVisibility(View.GONE);
                        }

                        //  Nokri_DialogManager.hideAlertDialog();
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
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    public void playYoutubeResumeVideo(String url){
//        nokri_initResumeYoutube(url);
    }
    public void playNormalVideo(String url){
//        normalVideoPlayer.setSource(url);
    }
    private void populateCustomFields(JSONArray customFields) throws JSONException {
        for (int i = 0;i<customFields.length();i++){
            String field_type_name = customFields.getJSONObject(i).getString("field_type_name");
            if (!field_type_name.equals("cand_name")&&!field_type_name.equals("cand_rgstr")){
                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                model.setTitle(customFields.getJSONObject(i).getString("key"));
                model.setDescription(customFields.getJSONObject(i).getString("value"));
                modelList.add(model);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void nokri_getEducation(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateEducation(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateEducation( Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = service.getCandidateEducation(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        boolean isEmpty = false;
                        //    Log.v("response",responseObject.body().string());
                        String notAdded = null;

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray dataArray = dataObject.getJSONArray("education");
                        JSONArray extrasArray = dataObject.getJSONArray("extras");


                        for(int i = 0;i<extrasArray.length();i++){
                            JSONObject object = extrasArray.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_name"))
                                educationalInfoTextView.setText(object.getString("value").trim());

                            if(object.getString("field_type_name").equals("not_added"))
                                notAdded = object.getString("value");
                        }

                        for(int i=0;i<dataArray.length();i++)
                        {String degreeStart = null,degreeEnd = null;
                            JSONArray responseArray = dataArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for(int j=0;j<responseArray.length();j++)
                            {
                                JSONObject response = responseArray.getJSONObject(j);

                                if(response.getString("field_type_name").equals("degree_name"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if(response.getString("field_type_name").equals("degree_start"))
                                    degreeStart = response.getString("value");
                                else if(response.getString("field_type_name").equals("degree_end"))
                                    degreeEnd =response.getString("value");
                                else if(response.getString("field_type_name").equals("degree_institute"))
                                    model.setTitleText(response.getString("value"));
                                else if(response.getString("field_type_name").equals("degree_percent")){
                                    model.setDegreePercentage(response.getString("value"));
                                    model.setPercentageTitle(response.getString("key"));

                                }
                                else if(response.getString("field_type_name").equals("degree_grade")) {
                                    model.setDegreeGrade(response.getString("value"));
                                    model.setGradeTitle(response.getString("key"));

                                }



                                else if(response.getString("field_type_name").equals("degree_detail"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if(j+1==responseArray.length())
                                {
                                    if(degreeStart.trim().isEmpty() || degreeEnd.trim().isEmpty()) {
                                        model.setDateText(notAdded);
                                        isEmpty = true;
                                    }
                                    else
                                        model.setDateText(degreeStart+" to "+degreeEnd);
                                    // Log.v("response",model.toString());
                                    modelList2.add(model);

                                }
                            }

                        }
                        nokri_setupEducationRecyclerview(isEmpty);
                        //     Nokri_DialogManager.hideAlertDialog();
                    }
                    catch (JSONException e) {
                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        //  Nokri_DialogManager.showCustom(e.getMessage());
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }
    private void nokri_getProfession(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateProfession(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateProfession( Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = service.getCandidateProfession(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {String notAdded = null;
                        //    Log.v("response",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray extras = dataObject.getJSONArray("extras") ;
                        for(int i =0;i<extras.length();i++)
                        {
                            JSONObject object = extras.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_name"))
                                workExperienceTextView.setText(object.getString("value").trim());
                            if(object.getString("field_type_name").equals("not_added"))
                                notAdded = object.getString("value");
                        }
                        JSONArray dataArray = dataObject.getJSONArray("profession");
                        for(int i=0;i<dataArray.length();i++)
                        {String degreeStart = null,degreeEnd = null;
                            JSONArray responseArray = dataArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for(int j=0;j<responseArray.length();j++)
                            {
                                JSONObject response = responseArray.getJSONObject(j);

                                if(response.getString("field_type_name").equals("project_organization"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if(response.getString("field_type_name").equals("project_start"))
                                    degreeStart = response.getString("value");
                                else if(response.getString("field_type_name").equals("project_end"))
                                    degreeEnd =response.getString("value");
                                else if(response.getString("field_type_name").equals("project_role"))
                                    model.setTitleText(response.getString("value"));
                                else if(response.getString("field_type_name").equals("project_desc"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if(j+1==responseArray.length())
                                {
                                    if(degreeStart.isEmpty() || degreeEnd.isEmpty())
                                        model.setDateText(notAdded);
                                    else
                                        model.setDateText(degreeStart+" to "+degreeEnd);
                                    modelList3.add(model);

                                }
                            }

                        }
                        nokri_setupProfesstionRecyclerview();
                        //    Nokri_DialogManager.hideAlertDialog();
                    }
                    catch (JSONException e) {
                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        //  Nokri_DialogManager.showCustom(e.getMessage());
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_getCertification(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateCerification(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidateCerification( Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateCerification(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {String notAdded = null;
                        //    Log.v("response",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        JSONArray extras = dataObject.getJSONArray("extras");
                        for(int i=0;i<extras.length();i++)
                        {
                            JSONObject object = extras.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_name"))
                                certificationTextView.setText(object.getString("value").trim());

                            if(object.getString("field_type_name").equals("not_added"))
                                notAdded = object.getString("value");
                        }


                        JSONArray dataArray = dataObject.getJSONArray("certification");
                        for(int i=0;i<dataArray.length();i++)
                        {String degreeStart = null,degreeEnd = null;
                            JSONArray responseArray = dataArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for(int j=0;j<responseArray.length();j++)
                            {
                                JSONObject response = responseArray.getJSONObject(j);

                                if(response.getString("field_type_name").equals("certification_name"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if(response.getString("field_type_name").equals("certification_start"))
                                    degreeStart = response.getString("value");
                                else if(response.getString("field_type_name").equals("certification_end"))
                                    degreeEnd =response.getString("value");
                                else if(response.getString("field_type_name").equals("certification_institute"))
                                    model.setTitleText(response.getString("value"));
                                else if(response.getString("field_type_name").equals("certification_desc"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if(j+1==responseArray.length())
                                {
                                    if(degreeStart.isEmpty() || degreeEnd.isEmpty())
                                        model.setDateText(notAdded);
                                    else
                                        model.setDateText(degreeStart+" to "+degreeEnd);
                                    modelList4.add(model);

                                }
                            }

                        }
                        nokri_setupCertificationRecyclerview();
                    }
                    catch (JSONException e) {
                        //  Nokri_DialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {

                        //  Nokri_DialogManager.showCustom(e.getMessage());
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_getPortfolio(){
        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidatePortfolio(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getCandidatePortfolio( Nokri_RequestHeaderManager.addHeaders());
        }
        //   Call<ResponseBody> myCall = service.getCandidatePortfolio(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        //    Log.v("response",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        JSONArray dataArray = dataObject.getJSONArray("img");
                        JSONArray extras = dataObject.getJSONArray("extra");
                        String youtubeGone = "";
                        for(int i =0;i<extras.length();i++){
                            JSONObject object = extras.getJSONObject(i);
                            if(object.getString("field_type_name").equals("section_label"))
                            {   portfolioTextView.setText(object.getString("value").trim());

                            }
                            if(object.getString("field_type_name").equals("not_added"))
                            {   portfolioGoneTextView.setText(object.getString("value").trim());

                            }

                            if(object.getString("field_type_name").equals("video_url"))
                            {   youtubeTextView.setText(object.getString("key").trim());
                                if(!object.getBoolean("is_required")){

                                    youtubeGoneTextView.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    portfolioVideoContainer.setVisibility(View.VISIBLE);
                                    playIconPortfolio.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent uTube = new Intent(getActivity(),YoutubeActivity.class);
                                            try {
                                                uTube.putExtra("url",object.getString("value"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            startActivity(uTube);
                                        }
                                    });
                                }
                            }

                            if(object.getString("field_type_name").equals("no_video_url"))
                            {

                                youtubeGoneTextView.setText(object.getString("value").trim());

                            }

                        }
                        if(dataArray.length()<=0)
                        {
                            portfolioGoneTextView.setVisibility(View.VISIBLE);
                            dialogManager.hideAlertDialog();
                            return;
                        }
                        else
                            portfolioGoneTextView.setVisibility(View.GONE);

                        for(int i=0;i<dataArray.length();i++)
                        {
                            JSONObject object = dataArray.getJSONObject(i);
                            Nokri_PortfolioModel model = new Nokri_PortfolioModel();
                            model.setUrl(object.getString("value"));
                            potfolionModelList.add(model);
                        }
                        nokri_setupPortfolioRecyclerview();
                        dialogManager.hideAlertDialog(); }
                    catch (JSONException e) {
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_setUpFonts(){
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(skillsTextiew,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(educationalInfoTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(workExperienceTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(certificationTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(portfolioTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(youtubeTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(portfolioGoneTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(noSkillsTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(youtubeGoneTextView,getActivity().getAssets());
    }


    private void nokri_setupEducationRecyclerview(boolean isEmpty){
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setLayoutManager(layoutManager2);

        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        if(!isEmpty)
            recyclerView2.setAdapter(new Nokri_MyProfileAdapter(modelList2,getContext(),true));
        else
            recyclerView2.setAdapter(new Nokri_MyProfileAdapter(modelList2,getContext()));
    }
    private void nokri_setupProfesstionRecyclerview(){
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setAdapter(new Nokri_MyProfileAdapter(modelList3,getContext()));
    }
    private void nokri_setupPortfolioRecyclerview(){
        portfolioRecyclerview.setLayoutManager(new GridLayoutManager(getContext(),4));
        portfolioRecyclerview.setItemAnimator(new DefaultItemAnimator());


        portfolioRecyclerview.setAdapter(new Nokri_PortfolioAdapter(potfolionModelList, getContext(), new Nokri_PortfolioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_PortfolioModel item, int position) {
                Intent intent = new Intent(getActivity(), Nokri_ImagePreview.class);
                //  Nokri_ImagePreview.INDEX = position;
                intent.putStringArrayListExtra(Nokri_ImagePreview.EXTRA_NAME, nokri_getImagesList(potfolionModelList,position));


                startActivity(intent);
            }
        }));
    }
    private ArrayList<String> nokri_getImagesList(List<Nokri_PortfolioModel> models, int position){
        ArrayList<String>images = new ArrayList<>();
        for(int i =0;i<models.size();i++) {
            images.add(models.get(i).getUrl());

        }
        Collections.swap(images, 0, position);
        return images;
    }
    private void nokri_setupCertificationRecyclerview(){
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        certificationRecyclerview.setLayoutManager(layoutManager3);

        certificationRecyclerview.setItemAnimator(new DefaultItemAnimator());
        certificationRecyclerview.setAdapter(new Nokri_MyProfileAdapter(modelList4,getContext()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_facebook:
                Nokri_Utils.opeInBrowser(getContext(),facebook);
                break;
            case R.id.img_twitter:
                Nokri_Utils.opeInBrowser(getContext(),twitter);
                break;
            case R.id.img_gooogle_plus:
                Nokri_Utils.opeInBrowser(getContext(),googlePlus);
                break;
            case R.id.img_linkedin:
                Nokri_Utils.opeInBrowser(getContext(),linkedin);
                break;
        }
    }

    @Override
    public void onItemClick(Nokri_DescriptionModel item) {
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder,new Nokri_CandidateEditProfileFragment()).commit();
    }



    private void nokri_setupSkillsRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        skillsRecyclerView.setLayoutManager(layoutManager);

        skillsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        skillsRecyclerView.setAdapter(new Nokri_SkillsAdapter(skillsModelList,getContext()));

    }


}
