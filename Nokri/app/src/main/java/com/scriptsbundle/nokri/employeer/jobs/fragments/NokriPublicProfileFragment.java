package com.scriptsbundle.nokri.employeer.jobs.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.BuildConfig;
import com.scriptsbundle.nokri.activities.Nokri_ImagePreview;
import com.scriptsbundle.nokri.activities.Rating;
import com.scriptsbundle.nokri.candidate.edit.models.CandidateAvailabilityList;
import com.scriptsbundle.nokri.candidate.profile.adapter.CandidateAvailabilityAdapter;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_SkillsAdapter;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_MyProfileModel;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_ProgressModel;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_DescriptionModel;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.manager.Nokri_UploadProgressDialolque;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.fragments.Nokri_CandidateEditProfileFragment;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_MyProfileAdapter;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_PortfolioAdapter;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_PortfolioModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.utils.AddRatingBottomSheet;
import com.scriptsbundle.nokri.utils.NonSchrollingListView;
import com.scriptsbundle.nokri.utils.RatingsAdapter;
import com.scriptsbundle.nokri.utils.RatingsBottomSheet;
import com.scriptsbundle.nokri.utils.models.RatingModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
public class NokriPublicProfileFragment extends Fragment implements View.OnClickListener, Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener, View.OnFocusChangeListener, OnMapReadyCallback {

    public static String USER_ID;
    private RecyclerView recyclerView1, recyclerView2, recyclerView3, certificationRecyclerview, portfolioRecyclerview;

    Nokri_DescriptionRecyclerViewAdapter adapter;
    private List<Nokri_DescriptionModel> modelList;
    private List<Nokri_MyProfileModel> modelList2, modelList3, modelList4;
    private TextView aboutMeTextView, aboutMeDataTextView, educationalInfoTextView, workExperienceTextView, nameTextView, jobTextView, skillsTextiew, certificationTextView, portfolioTextView, contactTextView;
    private Nokri_FontManager fontManager;
    private CircularImageView profileImageView;
    private ImageView facebookImageView, twitterImageView, googlePlusImageView, linkedinImageView;
    private List<Nokri_PortfolioModel> potfolionModelList;
    private TextView portfolioGoneTextView;

    private TextView noSkillsTextView;
    private Nokri_DialogManager dialogManager;
    String facebook;
    String twitter;
    String linkedIn;
    String googlePlus;
    private TextView youtubeTextView, youtubeGoneTextView;
    private YouTubePlayer YPlayer;
    private RecyclerView skillsRecyclerView;
    private List<Nokri_ProgressModel> skillsModelList;
    private EditText nameEditText, emailEditText, subjectEditText, messageEditText, buttonEditText;
    private Button messageButton;
    private String receiverId, reciverName, receiverEimail;
    TextView saveResume;
    String resumeDownloadUrl = "";
    LinearLayout layoutResumeDownload, imageContainer;
    ImageView downloadResume;
    String candidateName, fileExtension;//For File Name Purpose
    private DownloadResult serviceResult;
    Nokri_UploadProgressDialolque downloadProgressDialog;
    LinearLayout parentLayout, downloadResumeLayout;

    private MapView map;
    private static double LATITUDE = 0, LONGITUDE = 0;
    private GoogleMap googleMap;


    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    JSONObject ratingSection;
    TextView ratingText;
    RecyclerView ratingList;


    TextView labelCandidateAvailability, status;
    NonSchrollingListView listViewCandidateDays;
    LinearLayout availabilityContainer;
    static {
        System.loadLibrary("keys");
    }

    public static native String placeInitialize();

    @Override
    public void onResume() {
        super.onResume();
        if (map != null)
            map.onResume();

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nokri_initialize();
        nokri_setUpFonts();
        nokri_getProfile();

    }

    public NokriPublicProfileFragment() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_public_profile, container, false);
        this.savedInstanceState = savedInstanceState;
        initializeMap(view);
        return view;
    }

    public void initializeMap(View view) {
        map = view.findViewById(R.id.map_fragment);
        if (Nokri_Globals.showCandidateMap) {
            map.setVisibility(View.VISIBLE);
            map.onCreate(savedInstanceState);

            map.onResume();
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.getMapAsync(this);
            Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        } else {
            map.setVisibility(View.GONE);

        }
    }


    private void nokri_setupDescriptionRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView1.setLayoutManager(layoutManager);

        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_DescriptionRecyclerViewAdapter(modelList, getContext(), 0, this);
        recyclerView1.setAdapter(new Nokri_DescriptionRecyclerViewAdapter(modelList, getContext(), 0, this));

    }

    TextView addRating;

    @SuppressLint("NewApi")
    private void nokri_initialize() {


        availabilityContainer = getView().findViewById(R.id.availabilityContainer);
        viewAllReviews = getView().findViewById(R.id.viewAllReviews);
        ratingList = getView().findViewById(R.id.ratingList);
        addRating = getView().findViewById(R.id.add_rating);
        Nokri_Utils.setRoundButtonColor(getActivity(), addRating);
        ratingText = getView().findViewById(R.id.ratingText);

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingSection != null) {
                    AddRatingBottomSheet bottomSheet = new AddRatingBottomSheet(ratingSection, false);
                    bottomSheet.show(getChildFragmentManager(),
                            "RatingBottomSheet");
                }
            }
        });


        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        fontManager = new Nokri_FontManager();
        serviceResult = new DownloadResult(new Handler(Looper.getMainLooper()));

        saveResume = getView().findViewById(R.id.save_resume);
        Nokri_Utils.setRoundButtonColor(getActivity(), saveResume);
        fontManager.nokri_setOpenSenseFontTextView(saveResume, getActivity().getAssets());
        downloadResume = getView().findViewById(R.id.downloadResume);
        layoutResumeDownload = getView().findViewById(R.id.downloadResumeLayout);
        imageContainer = getView().findViewById(R.id.imageContainer);
        Nokri_Utils.setRoundButtonColor(getActivity(), imageContainer);

        downloadResume.setImageDrawable(getActivity().getDrawable(R.drawable.donwload_icon));
        parentLayout = getView().findViewById(R.id.parentLayout);
        imageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!resumeDownloadUrl.equals("")) {
                    new DownloadFileFromURL().execute(resumeDownloadUrl);
                }
            }
        });
        saveResume.setOnClickListener(this);
        recyclerView1 = getView().findViewById(R.id.recyclerview1);
        recyclerView2 = getView().findViewById(R.id.recyclerview_educational_info);
        recyclerView3 = getView().findViewById(R.id.recyclerview_work_experience);
        skillsRecyclerView = getView().findViewById(R.id.skills_recyclerview);
        certificationRecyclerview = getView().findViewById(R.id.recyclerview_certification);
        portfolioRecyclerview = getView().findViewById(R.id.recyclerview_portfolio);
        youtubeTextView = getView().findViewById(R.id.txt_youttube);
        youtubeGoneTextView = getView().findViewById(R.id.txt_no_youtube);


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
        contactTextView = getView().findViewById(R.id.txt_contact);

        profileImageView = getView().findViewById(R.id.img_logo);
        certificationTextView = getView().findViewById(R.id.txt_certificaton);

        facebookImageView = getView().findViewById(R.id.img_facebook);
        twitterImageView = getView().findViewById(R.id.img_twitter);
        googlePlusImageView = getView().findViewById(R.id.img_gooogle_plus);
        linkedinImageView = getView().findViewById(R.id.img_linkedin);

        noSkillsTextView = getView().findViewById(R.id.txt_no_skills);

        messageButton = getView().findViewById(R.id.btn_message);

        Nokri_Utils.setRoundButtonColor(getContext(), messageButton);

        nameEditText = getView().findViewById(R.id.edittxt_name);
        emailEditText = getView().findViewById(R.id.edittxt_email);
        subjectEditText = getView().findViewById(R.id.edittxt_subject);
        messageEditText = getView().findViewById(R.id.edittxt_message);

        modelList = new ArrayList<>();
        modelList2 = new ArrayList<>();
        modelList3 = new ArrayList<>();
        modelList4 = new ArrayList<>();
        skillsModelList = new ArrayList<>();
        potfolionModelList = new ArrayList<>();

        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);
        subjectEditText.setOnFocusChangeListener(this);
        messageEditText.setOnFocusChangeListener(this);

        facebookImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        googlePlusImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
        messageButton.setOnClickListener(this);
    }

    TextView viewAllReviews;

    private void nokri_getProfile() {
        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        JsonObject params = new JsonObject();
        params.addProperty("user_id", USER_ID);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidatePublicProfile(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCandidatePublicProfile(params, Nokri_RequestHeaderManager.addHeaders());
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
                if (responseObject.isSuccessful()) {

                    try {
                        String aboutEmpty = null, skillsEmpty = null;
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        if (jsonObject.getBoolean("success") == false) {
                            getView().findViewById(R.id.main_container).setVisibility(View.GONE);
                            getView().findViewById(R.id.private_container).setVisibility(View.VISIBLE);
                            TextView messageTextView = getView().findViewById(R.id.txt_message);
                            messageTextView.setText(jsonObject.getString("message"));
                            dialogManager.hideAlertDialog();
                            if (!jsonObject.getString("message").trim().isEmpty()) {
                                Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
                            }
                            return;
                        }

                        JSONArray customFields = null;
                        try {

                            customFields = jsonObject.getJSONObject("data").getJSONArray("custom_fields");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (customFields == null) {
                            customFields = new JSONArray();
                        }

                        ratingSection = jsonObject.getJSONObject("data").getJSONObject("user_reviews");
                        JSONArray reviewArray = ratingSection.getJSONArray("reviews_data");
                        ratingText.setText(ratingSection.getJSONObject("extra").getString("reviews_title"));

                        ArrayList<RatingModel> ratings = new ArrayList<>();
                        for (int i = 0; i < reviewArray.length(); i++) {
                            RatingModel model = new RatingModel();
                            JSONObject jsonObject1 = reviewArray.getJSONObject(i);
                            model.setDate(jsonObject1.getString("_rating_date"));
                            model.setRaterName(jsonObject1.getString("_rating_poster"));
                            model.setRating(jsonObject1.getDouble("_rating_avg"));
                            model.setContainsReply(jsonObject1.getBoolean("has_reply"));
                            model.setAuthorReply(jsonObject1.getString("reply_text"));
                            model.setAuthorName(jsonObject1.getString("reply_heading"));
                            model.setRaterTitle(jsonObject1.getString("_rating_title"));
                            model.setRaterComments(jsonObject1.getString("_rating_description"));
                            model.setCanReply(jsonObject1.getBoolean("can_reply"));
                            model.setUrl(jsonObject1.getString("emp_image"));
                            model.setCommentID(jsonObject1.getString("cid"));
                            ratings.add(model);
                        }

                        if (ratings.size() == 0) {
                            viewAllReviews.setVisibility(View.GONE);
                        }
                        viewAllReviews.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RatingsBottomSheet bottomSheet = new RatingsBottomSheet(USER_ID, false);
                                bottomSheet.show(getChildFragmentManager(),
                                        "AllRatingsBottomSheet");
                            }
                        });

                        RatingsAdapter adapter = new RatingsAdapter(getActivity(), ratings, false);
                        ratingList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        ratingList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                        Nokri_Globals.EDIT_MESSAGE = jsonObject.getString("message");
                        JSONObject data = jsonObject.getJSONObject("data");

                        JSONObject usereConatct = data.getJSONObject("user_contact");
                        contactTextView.setText(usereConatct.getString("receiver_name"));
                        nameEditText.setHint(usereConatct.getString("sender_name"));
                        emailEditText.setHint(usereConatct.getString("sender_email"));
                        subjectEditText.setHint(usereConatct.getString("sender_subject"));
                        messageEditText.setHint(usereConatct.getString("sender_message"));


                        receiverId = usereConatct.getString("receiver_id");
                        reciverName = usereConatct.getString("receiver_name");
                        receiverEimail = usereConatct.getString("receiver_email");

                        messageButton.setText(usereConatct.getString("btn_txt"));
                        JSONObject toolbarExtra = data.getJSONObject("extra");
//
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));
//                        if (toolbarExtra.getJSONObject("0").getString("value").equals("0")){
//                            map.setVisibility(View.GONE);
//                        }

                        JSONObject basicInfo = data.getJSONObject("basic_ifo");
                        JSONArray extrasArray = basicInfo.getJSONArray("extra");
                        for (int i = 0; i < extrasArray.length(); i++) {
                            JSONObject extra = extrasArray.getJSONObject(i);
                            if (extra.getString("field_type_name").equals("cand_about"))
                                aboutEmpty = extra.getString("value");
                            if (extra.getString("field_type_name").equals("cand_skills"))
                                skillsTextiew.setText(extra.getString("key"));
                            skillsEmpty = extra.getString("value");
                            if (extra.getString("field_type_name").equals("map_switch"))
                                if (extra.getString("value").equals("0")) {
                                    map.setVisibility(View.GONE);
                                } else {
                                    map.setVisibility(View.VISIBLE);
                                }

                            Log.v("tagzzzzzz", "inside extras Array");

                        }
                        try {


                            JSONObject scheduledHours = basicInfo.getJSONObject("scheduled_hours");
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
                            if (daysArray.length() == 0) {
                                availabilityContainer.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < daysArray.length(); i++) {
                                    CandidateAvailabilityList.Day object = new CandidateAvailabilityList.Day();
                                    object.setStart_time(daysArray.getJSONObject(i).getString("start_time"));
                                    object.setEnd_time(daysArray.getJSONObject(i).getString("end_time"));
                                    object.setDay_name(daysArray.getJSONObject(i).getString("day_key"));
                                    object.setClosed(daysArray.getJSONObject(i).getBoolean("closed"));
                                    days.add(object);
                                }
                                CandidateAvailabilityAdapter candidateAvailabilityAdapter = new CandidateAvailabilityAdapter(getActivity(), days, extra.getString("cloes_now"));
                                listViewCandidateDays.setAdapter(candidateAvailabilityAdapter);


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Candidate Availability", e.getMessage());
                            availabilityContainer.setVisibility(View.GONE);
                        }

                        //   JSONObject profileImageObject = basicInfo.getJSONObject("profile_img");
                        if (!TextUtils.isEmpty(basicInfo.getString("profile_img")))
                            Picasso.get().load(basicInfo.getString("profile_img")).fit().centerCrop().into(profileImageView);
                        JSONArray skillsArray = basicInfo.getJSONArray("skills");
                        if (skillsArray.length() > 0) {

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
                        } else {
                            noSkillsTextView.setVisibility(View.VISIBLE);
                            noSkillsTextView.setText(skillsEmpty);
                        }

                        JSONObject socialObject = basicInfo.getJSONObject("social");
                        if (socialObject.getBoolean("is_show")) {
                            facebook = socialObject.getString("facebook");
                            twitter = socialObject.getString("twitter");
                            linkedIn = socialObject.getString("linkedin");
                            googlePlus = socialObject.getString("google_plus");

                            if (facebook.trim().isEmpty())
                                facebookImageView.setVisibility(View.GONE);
                            if (twitter.trim().isEmpty())
                                twitterImageView.setVisibility(View.GONE);
                            if (linkedIn.trim().isEmpty())
                                linkedinImageView.setVisibility(View.GONE);
                            if (googlePlus.trim().isEmpty())
                                googlePlusImageView.setVisibility(View.GONE);
                        } else
                            getView().findViewById(R.id.socail_container).setVisibility(View.GONE);
                        JSONArray infoArray = basicInfo.getJSONArray("info");

                        for (int i = 0; i < infoArray.length(); i++) {
                            Log.v("tagzzzzzz", "inside info Array");
                            JSONObject response = infoArray.getJSONObject(i);
                            Log.v("tagzzzzzz", "inside after info Array");
                            if (response.getString("field_type_name").equals("download_resumes")) {
                                if (response.getString("is_show").equals("1")) {
                                    layoutResumeDownload.setVisibility(View.VISIBLE);
                                    resumeDownloadUrl = response.getString("key");

                                } else
                                    layoutResumeDownload.setVisibility(View.GONE);
                            } else if (response.getString("field_type_name").equals("resume_name")) {
                                fileExtension = response.getString("key");
                            } else if (response.getString("field_type_name").equals("cand_lat")) {
                                LATITUDE = Double.parseDouble(response.getString("value"));
                            } else if (response.getString("field_type_name").equals("cand_long")) {
                                LONGITUDE = Double.parseDouble(response.getString("value"));
                            } else if (response.getString("field_type_name").equals("cand_name")) {
                                candidateName = response.getString("value");
                                nameTextView.setText(candidateName);
                            } else if (response.getString("field_type_name").equals("cand_hand"))
                                jobTextView.setText(response.getString("value"));
                            else if (response.getString("field_type_name").equals("about_me")) {
                                aboutMeTextView.setText(response.getString("key"));
                                String text = Nokri_Utils.stripHtml(response.getString("value")).toString();
                                if (text.isEmpty())
                                    aboutMeDataTextView.setText(aboutEmpty);
                                else
                                    aboutMeDataTextView.setText(Nokri_Utils.stripHtml(response.getString("value")));
                            } else if (response.getString("field_type_name").equals("about_me")) {
                                aboutMeTextView.setText(response.getString("key"));
                                String text = Nokri_Utils.stripHtml(response.getString("value")).toString();
                                if (text.isEmpty())
                                    aboutMeDataTextView.setText(aboutEmpty);
                                else
                                    aboutMeDataTextView.setText(Nokri_Utils.stripHtml(response.getString("value")));
                            } else if (!response.getString("field_type_name").equals("your_dashbord") && !response.getString("field_type_name").equals("loc") && !response.getString("field_type_name").equals("cand_long") && !response.getString("field_type_name").equals("cand_lat")) {
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(response.getString("key"));
                                model.setDescription(response.getString("value"));
                                modelList.add(model);
                            }
                        }
                        nokri_setupDescriptionRecyclerView();
                        populateCustomFields(customFields);
                        setMapLocation();
                        //---------------------------------Certfication---------------------------------> Start

                        String notAdded = null;
                        //    Log.v("response",responseObject.body().string());


                        JSONObject certificationsObject = data.getJSONObject("certifications");
                        JSONArray extras = certificationsObject.getJSONArray("extras");
                        for (int i = 0; i < extras.length(); i++) {
                            JSONObject object = extras.getJSONObject(i);
                            if (object.getString("field_type_name").equals("section_name"))
                                certificationTextView.setText(object.getString("value"));

                            if (object.getString("field_type_name").equals("not_added"))
                                notAdded = object.getString("value");
                        }


                        JSONArray dataArray = certificationsObject.getJSONArray("certification");
                        for (int i = 0; i < dataArray.length(); i++) {
                            String degreeStart = null, degreeEnd = null;
                            JSONArray responseArray = dataArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for (int j = 0; j < responseArray.length(); j++) {
                                JSONObject response = responseArray.getJSONObject(j);

                                if (response.getString("field_type_name").equals("certification_name"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if (response.getString("field_type_name").equals("certification_start"))
                                    degreeStart = response.getString("value");
                                else if (response.getString("field_type_name").equals("certification_end"))
                                    degreeEnd = response.getString("value");
                                else if (response.getString("field_type_name").equals("certification_institute"))
                                    model.setTitleText(response.getString("value"));
                                else if (response.getString("field_type_name").equals("certification_desc"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if (j + 1 == responseArray.length()) {
                                    if (degreeStart.isEmpty() || degreeEnd.isEmpty())
                                        model.setDateText(notAdded);
                                    else
                                        model.setDateText(degreeStart + " to " + degreeEnd);
                                    modelList4.add(model);

                                }
                            }

                        }
                        nokri_setupCertificationRecyclerview();

                        //---------------------------------Certfication---------------------------------> End


                        //---------------------------------Education---------------------------------> Start
                        //    Log.v("response",responseObject.body().string());
                        String educationNotAdded = null;
                        JSONObject educationObject = data.getJSONObject("Education");
                        JSONArray educationArray = educationObject.getJSONArray("education");
                        JSONArray educationExtra = educationObject.getJSONArray("extras");


                        for (int i = 0; i < educationExtra.length(); i++) {
                            JSONObject object = educationExtra.getJSONObject(i);
                            if (object.getString("field_type_name").equals("section_name"))
                                educationalInfoTextView.setText(object.getString("value"));

                            if (object.getString("field_type_name").equals("not_added"))
                                educationNotAdded = object.getString("value");
                        }

                        for (int i = 0; i < educationArray.length(); i++) {
                            String degreeStart = null, degreeEnd = null;
                            JSONArray responseArray = educationArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for (int j = 0; j < responseArray.length(); j++) {
                                JSONObject response = responseArray.getJSONObject(j);

                                if (response.getString("field_type_name").equals("degree_name"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if (response.getString("field_type_name").equals("degree_start"))
                                    degreeStart = response.getString("value");
                                else if (response.getString("field_type_name").equals("degree_end"))
                                    degreeEnd = response.getString("value");
                                else if (response.getString("field_type_name").equals("degree_institute"))
                                    model.setTitleText(response.getString("value"));
                                else if (response.getString("field_type_name").equals("degree_detail"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if (j + 1 == responseArray.length()) {
                                    if (degreeStart.isEmpty() || degreeEnd.isEmpty())
                                        model.setDateText(educationNotAdded);
                                    else
                                        model.setDateText(degreeStart + " to " + degreeEnd);
                                    // Log.v("response",model.toString());
                                    modelList2.add(model);

                                }
                            }

                        }
                        nokri_setupEducationRecyclerview();

                        //---------------------------------Education---------------------------------> End


                        //---------------------------------Profession------------------------------------> Start

                        String professionNotAdded = null;
                        //    Log.v("response",responseObject.body().string());


                        JSONObject professionObject = data.getJSONObject("Profession");

                        JSONArray professionArray = professionObject.getJSONArray("profession");
                        JSONArray professionExtra = professionObject.getJSONArray("extras");


                        for (int i = 0; i < professionExtra.length(); i++) {
                            JSONObject object = professionExtra.getJSONObject(i);
                            if (object.getString("field_type_name").equals("section_name"))
                                workExperienceTextView.setText(object.getString("value"));
                            if (object.getString("field_type_name").equals("not_added"))
                                professionNotAdded = object.getString("value");
                        }


                        for (int i = 0; i < professionArray.length(); i++) {
                            String degreeStart = null, degreeEnd = null;
                            JSONArray responseArray = professionArray.getJSONArray(i);
                            Nokri_MyProfileModel model = new Nokri_MyProfileModel();
                            for (int j = 0; j < responseArray.length(); j++) {
                                JSONObject response = responseArray.getJSONObject(j);

                                if (response.getString("field_type_name").equals("project_name"))
                                    model.setDegreeTitle(response.getString("value"));
                                else if (response.getString("field_type_name").equals("project_start"))
                                    degreeStart = response.getString("value");
                                else if (response.getString("field_type_name").equals("project_end"))
                                    degreeEnd = response.getString("value");
                                else if (response.getString("field_type_name").equals("project_role"))
                                    model.setTitleText(response.getString("value"));
                                else if (response.getString("field_type_name").equals("project_desc"))
                                    model.setParagraphText(Nokri_Utils.stripHtml(response.getString("value")).toString());

                                if (j + 1 == responseArray.length()) {
                                    if (degreeStart.isEmpty() || degreeEnd.isEmpty())
                                        model.setDateText(professionNotAdded);
                                    else
                                        model.setDateText(degreeStart + " to " + degreeEnd);
                                    modelList3.add(model);

                                }
                            }

                        }
                        nokri_setupProfesstionRecyclerview();

                        //---------------------------------Profession------------------------------------> End


                        //---------------------------------Portfolio------------------------------------> Start


                        JSONObject portfolioObject = data.getJSONObject("portfolio");
                        JSONArray portfolioArray = portfolioObject.getJSONArray("img");
                        JSONArray portfolioExtra = portfolioObject.getJSONArray("extra");

                        for (int i = 0; i < portfolioExtra.length(); i++) {
                            JSONObject object = portfolioExtra.getJSONObject(i);
                            if (object.getString("field_type_name").equals("section_label")) {
                                portfolioTextView.setText(object.getString("value"));

                            }
                            if (object.getString("field_type_name").equals("not_added")) {
                                portfolioGoneTextView.setText(object.getString("value"));

                            }
                            if (object.getString("field_type_name").equals("video_url")) {
                                youtubeTextView.setText(object.getString("key").trim());
                                if (!object.getBoolean("is_required")) {

                                    youtubeGoneTextView.setVisibility(View.VISIBLE);
                                } else {

                                    nokri_initYoutube(object.getString("value"));
                                }
                            }

                            if (object.getString("field_type_name").equals("no_video_url")) {

                                youtubeGoneTextView.setText(object.getString("value").trim());

                            }
                        }
                        if (portfolioArray.length() <= 0) {
                            portfolioGoneTextView.setVisibility(View.VISIBLE);
                            dialogManager.hideAlertDialog();
                            return;
                        } else
                            portfolioGoneTextView.setVisibility(View.GONE);

                        for (int i = 0; i < portfolioArray.length(); i++) {
                            JSONObject object = portfolioArray.getJSONObject(i);
                            Nokri_PortfolioModel model = new Nokri_PortfolioModel();
                            model.setUrl(object.getString("value"));
                            potfolionModelList.add(model);
                        }
                        nokri_setupPortfolioRecyclerview();

                        //---------------------------------Portfolio------------------------------------> End


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
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    private void populateCustomFields(JSONArray customFields) throws JSONException {
        for (int i = 0; i < customFields.length(); i++) {
            String field_type_name = customFields.getJSONObject(i).getString("field_type_name");
            if (!field_type_name.equals("cand_name") && !field_type_name.equals("cand_rgstr")) {
                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                model.setTitle(customFields.getJSONObject(i).getString("key"));
                model.setDescription(customFields.getJSONObject(i).getString("value"));
                modelList.add(model);
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng location = new LatLng(LATITUDE, LONGITUDE);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(location));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location).zoom(Nokri_Config.MAP_CAM_MIN_ZOOM).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }

    private void setMapLocation() {
        if (googleMap != null) {


            LatLng location = new LatLng(LATITUDE, LONGITUDE);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(location));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location).zoom(Nokri_Config.MAP_CAM_MIN_ZOOM).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }

    }

    public boolean downloadedWithoutException = true;

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Nokri_ToastManager.showShortToast(getActivity(), "Download Started");

        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream

                int i = fileExtension.lastIndexOf('.');
                if (i > 0) {
                    fileExtension = fileExtension.substring(i + 1);
                }
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/Download/" + candidateName + "." + fileExtension);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                downloadedWithoutException = false;
                Nokri_ToastManager.showShortToast(getActivity(), e.getLocalizedMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            if (downloadedWithoutException) {
                Snackbar.make(parentLayout, "Downloaded", Snackbar.LENGTH_LONG)
                        .show();
            }
            downloadedWithoutException = true;

        }

    }


    private void nokri_setUpFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(skillsTextiew, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(educationalInfoTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(workExperienceTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(certificationTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(portfolioTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(contactTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(nameTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(portfolioGoneTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(noSkillsTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(nameEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(subjectEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(messageEditText, getActivity().getAssets());


        fontManager.nokri_setMonesrratSemiBioldFont(youtubeTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(youtubeGoneTextView, getActivity().getAssets());

    }

    private void nokri_setupEducationRecyclerview() {
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setLayoutManager(layoutManager2);

        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(new Nokri_MyProfileAdapter(modelList2, getContext()));
    }

    private void nokri_setupProfesstionRecyclerview() {
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setAdapter(new Nokri_MyProfileAdapter(modelList3, getContext()));
    }

    private void nokri_setupPortfolioRecyclerview() {
        portfolioRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 4));
        portfolioRecyclerview.setItemAnimator(new DefaultItemAnimator());


        portfolioRecyclerview.setAdapter(new Nokri_PortfolioAdapter(potfolionModelList, getContext(), new Nokri_PortfolioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_PortfolioModel item, int position) {
                Intent intent = new Intent(getActivity(), Nokri_ImagePreview.class);
                //  Nokri_ImagePreview.INDEX = position;
                intent.putStringArrayListExtra(Nokri_ImagePreview.EXTRA_NAME, nokri_getImagesList(potfolionModelList, position));


                startActivity(intent);
            }
        }));
    }


    private ArrayList<String> nokri_getImagesList(List<Nokri_PortfolioModel> models, int position) {
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            images.add(models.get(i).getUrl());

        }
        Collections.swap(images, 0, position);
        return images;
    }

    private void nokri_setupCertificationRecyclerview() {
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        certificationRecyclerview.setLayoutManager(layoutManager3);

        certificationRecyclerview.setItemAnimator(new DefaultItemAnimator());
        certificationRecyclerview.setAdapter(new Nokri_MyProfileAdapter(modelList4, getContext()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_facebook:
                Nokri_Utils.opeInBrowser(getContext(), facebook);
                break;
            case R.id.img_twitter:
                Nokri_Utils.opeInBrowser(getContext(), twitter);
                break;
            case R.id.img_gooogle_plus:
                Nokri_Utils.opeInBrowser(getContext(), googlePlus);
                break;
            case R.id.img_linkedin:
                Nokri_Utils.opeInBrowser(getContext(), linkedIn);
                break;
            case R.id.btn_message:
                nokri_sendMessage();
                break;
            case R.id.save_resume:
                saveResume();
                break;
        }
    }


    private void saveResume() {


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("cand_id", USER_ID);


        Log.v("tagzzzzzz", jsonObject.toString());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.saveResume(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.saveResume(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            //    nokri_getLocationAndAddress();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAlertDialog();
                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();

                    }
                } else {
                    Nokri_ToastManager.showLongToast(getContext(), responseObject.message());
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });


    }

    private void nokri_sendMessage() {

        if (!Nokri_Utils.isValidEmail(emailEditText.getText().toString())) {
            emailEditText.setError("!");

            Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.INVALID_EMAIL);

            return;

        }

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("sender_name", nameEditText.getText().toString());
        jsonObject.addProperty("sender_email", emailEditText.getText().toString());
        jsonObject.addProperty("sender_subject", subjectEditText.getText().toString());
        jsonObject.addProperty("sender_message", messageEditText.getText().toString());
        jsonObject.addProperty("receiver_id", receiverId);
        jsonObject.addProperty("receiver_name", reciverName);
        jsonObject.addProperty("receiver_email", receiverEimail);


        Log.v("tagzzzzzz", jsonObject.toString());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postContactUS(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postContactUS(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            //    nokri_getLocationAndAddress();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAfterDelay();
                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();

                    }
                } else {
                    Nokri_ToastManager.showLongToast(getContext(), responseObject.message());
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });


    }

    @Override
    public void onItemClick(Nokri_DescriptionModel item) {
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_placeholder, new Nokri_CandidateEditProfileFragment()).commit();
    }

    private void nokri_initYoutube(final String url) {


        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize(placeInitialize(), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.setFullscreen(false);
                    YPlayer.cueVideo(url);


                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {


            }
        });


    }

    private void nokri_setupSkillsRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        skillsRecyclerView.setLayoutManager(layoutManager);

        skillsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        skillsRecyclerView.setAdapter(new Nokri_SkillsAdapter(skillsModelList, getContext()));

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edittxt_name:
                if (hasFocus) {
                    nameEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }
                break;
            case R.id.edittxt_email:

                if (hasFocus) {
                    emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }

                break;
            case R.id.edittxt_subject:

                if (hasFocus) {
                    subjectEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    messageEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }

                break;
            case R.id.edittxt_message:

                if (hasFocus) {
                    messageEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    subjectEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));

                }

                break;
            default:
                break;


        }
    }

    public class DownloadResult extends ResultReceiver {
        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public DownloadResult(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            switch (resultCode) {
                case 1000:
                    if (resultData.getBoolean("state"))
                        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (map != null)
            map.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (map != null)
            map.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }
}