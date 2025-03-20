package com.scriptsbundle.nokri.employeer.dashboard.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.BuildConfig;
import com.scriptsbundle.nokri.activities.Nokri_ImagePreview;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_PortfolioAdapter;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_PortfolioModel;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_DescriptionModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.edit.fragments.Nokri_CompanyEditProfileFragment;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mabbas007.tagsedittext.TagsEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NokriEmployeerDashboardFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private RecyclerView recyclerView1;
    private Nokri_DescriptionRecyclerViewAdapter adapter1;

    private List<Nokri_DescriptionModel> modelList;
    private Nokri_FontManager fontManager;

    private MapView map;
    private TextView nameTextView, addressTextView;
    private TextView yourDashboardTextView, aboutMeTextView, aboutMeDataTextView, locationAndMapTextView, skillsTextView;
    private CircularImageView profileImage;

    private static double LATITUDE, LONGITUDE;

    private TagsEditText tagsEditText;
    private String facebook, twitter, linkedin, googleplus;
    private ImageView facebookImageButton, twitterImageButton, linkedinImageButton, googlePlusImageButton;
    private TextView noSkillsTextView;
    private Nokri_DialogManager dialogManager;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    private TextView portfolioTextView, portfolioGoneTextView, youtubeTextView, youtubeGoneTextView;
    RecyclerView portfolioRecyclerview;
    ArrayList<Nokri_PortfolioModel> potfolionModelList = new ArrayList<>();
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    LinearLayout mapLayout;
    static {
        System.loadLibrary("keys");
    }

    public static native String placeInitialize();
    public NokriEmployeerDashboardFragment() {
        // Required empty public constructor
    }


    private void nokri_initialize() {

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        mapLayout = getView().findViewById(R.id.mapLayout);
        if (!Nokri_Globals.showEmployerMap){
            mapLayout.setVisibility(View.GONE);
        }

        fontManager = new Nokri_FontManager();
        recyclerView1 = getView().findViewById(R.id.recyclerview);
        profileImage = getView().findViewById(R.id.img_profile);
        portfolioTextView = getView().findViewById(R.id.txt_portfolio);
        portfolioGoneTextView = getView().findViewById(R.id.txt_no_porfolio);
        youtubeTextView = getView().findViewById(R.id.txt_youttube);
        youtubeGoneTextView = getView().findViewById(R.id.txt_no_youtube);
        portfolioRecyclerview = getView().findViewById(R.id.recyclerview_portfolio);


        Drawable mDrawable = getActivity().getResources().getDrawable(R.drawable.saa);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.MULTIPLY));


        nameTextView = getView().findViewById(R.id.txt_name);
        addressTextView = getView().findViewById(R.id.txt_address);
        yourDashboardTextView = getView().findViewById(R.id.txt_your_dashboard);
        aboutMeTextView = getView().findViewById(R.id.txt_about_me);
        aboutMeDataTextView = getView().findViewById(R.id.txt_about_me_data);
        locationAndMapTextView = getView().findViewById(R.id.txt_location_and_map);
        skillsTextView = getView().findViewById(R.id.txt_skills);
        noSkillsTextView = getView().findViewById(R.id.txt_no_skills);


        facebookImageButton = getView().findViewById(R.id.img_btn_facebook);
        twitterImageButton = getView().findViewById(R.id.img_btn_twitter);
        linkedinImageButton = getView().findViewById(R.id.img_btn_linkedin);
        googlePlusImageButton = getView().findViewById(R.id.img_btn_goole_plus);


        facebookImageButton.setOnClickListener(this);
        twitterImageButton.setOnClickListener(this);
        linkedinImageButton.setOnClickListener(this);
        googlePlusImageButton.setOnClickListener(this);

        modelList = new ArrayList<>();

        tagsEditText = getView().findViewById(R.id.skills_container);
        tagsEditText.setEnabled(false);

        recyclerView1.setNestedScrollingEnabled(false);
    }

    private void setSkills(JSONArray jsonArray, String skillsEmpty) {
        List<String> tagsList = new ArrayList<>();
        if (jsonArray.length() <= 0) {
            String[] tag = {skillsEmpty};
            tagsEditText.setTags(tag);
            tagsEditText.setVisibility(View.GONE);
            noSkillsTextView.setVisibility(View.VISIBLE);
            noSkillsTextView.setText(skillsEmpty);
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tagObject = jsonArray.getJSONObject(i);
                tagsList.add(tagObject.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String tags[] = new String[tagsList.size()];
        tagsEditText.setTags(tagsList.toArray(tags));
    }

    private void nokri_setupFonts() {


        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(noSkillsTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(aboutMeDataTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(yourDashboardTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(aboutMeTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(locationAndMapTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(skillsTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(youtubeGoneTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(youtubeTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(portfolioGoneTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(portfolioTextView, getActivity().getAssets());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        getEmployeerDashboard();
        Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getDashboard());
        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            checkLoading = true;
            recreate_nokri_getEmployeerDashboard();

        });
    }

    private void recreate_nokri_getEmployeerDashboard() {

        NokriEmployeerDashboardFragment NokriEmployeerDashboardFragment = new NokriEmployeerDashboardFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, NokriEmployeerDashboardFragment);
        transaction.commit();
    }

    private void getEmployeerDashboard() {
        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(getActivity());

        Nokri_Utils.isCallRunning = true;
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());


        Call<ResponseBody> myCall;

        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployeerDashboard(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getEmployeerDashboard(Nokri_RequestHeaderManager.addHeaders());
        }
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
                        String defaultAboutText = null, skillsEmpty = null;
                        //    Log.d("errrrrrrrr",responseObject.body().string());
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                        if (jsonObject.getBoolean("success")) {

                            Nokri_Globals.EDIT_MESSAGE = jsonObject.getString("message");
                            JSONObject data = jsonObject.getJSONObject("data");

                            JSONObject social = data.getJSONObject("social");
                            facebook = social.getString("facebook");
                            twitter = social.getString("twitter");
                            linkedin = social.getString("linkedin");
                            googleplus = social.getString("google_plus");
                            if (facebook.trim().isEmpty())
                                facebookImageButton.setVisibility(View.GONE);
                            if (twitter.trim().isEmpty())
                                twitterImageButton.setVisibility(View.GONE);
                            if (linkedin.trim().isEmpty())
                                linkedinImageButton.setVisibility(View.GONE);
                            if (googleplus.trim().isEmpty())
                                googlePlusImageButton.setVisibility(View.GONE);

                            JSONArray extrasArray = data.getJSONArray("extra");
                            for (int i = 0; i < extrasArray.length(); i++) {
                                JSONObject extra = extrasArray.getJSONObject(i);
                                if (extra.getString("field_type_name").equals("emp_about")) {
                                    defaultAboutText = extra.getString("value");
                                } else if (extra.getString("field_type_name").equals("emp_not_skills")) {
                                    skillsEmpty = extra.getString("value");
                                } else if (extra.getString("field_type_name").equals("emp_skills")) {
                                    skillsTextView.setText(extra.getString("value"));

                                } else if (extra.getString("field_type_name").equals("video_url")) {
                                    youtubeTextView.setText(extra.getString("key").trim());
                                    if (!extra.getBoolean("is_required")) {
                                        youtubeGoneTextView.setVisibility(View.VISIBLE);
                                    } else {
                                        nokri_initYoutube(extra.getString("value"));
                                    }
                                } else if (extra.getString("field_type_name").equals("port_text")) {
                                    portfolioTextView.setText(extra.getString("key"));
                                }

                            }

                            try {

                                JSONArray portfolioArray = data.getJSONArray("gal_images");
                                for (int i = 0; i < portfolioArray.length(); i++) {


                                    JSONObject object = portfolioArray.getJSONObject(i);
                                    Nokri_PortfolioModel model = new Nokri_PortfolioModel();
                                    model.setUrl(object.getString("value"));
                                    potfolionModelList.add(model);

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            nokri_setupPortfolioRecyclerview();

                            JSONArray skillsArray = data.getJSONArray("skills");
                            setSkills(skillsArray, skillsEmpty);

                            JSONArray jsonArray = data.getJSONArray("info");

                            JSONObject dpJsonObject = data.getJSONObject("profile_img");
                            JSONObject coverJsonObject = data.getJSONObject("cvr_img");


                            Log.v("background", coverJsonObject.getString("img"));
                            if (!TextUtils.isEmpty(dpJsonObject.getString("img")))
                                Picasso.get().load(dpJsonObject.getString("img")).into(profileImage);
                            Nokri_SharedPrefManager.saveProfileImage(dpJsonObject.getString("img"), getContext());
                            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                            View headerView = navigationView.getHeaderView(0);
                            CircularImageView dashboardImage = headerView.findViewById(R.id.img_profile);
                            if (!TextUtils.isEmpty(Nokri_SharedPrefManager.getProfileImage(getContext())))
                                Picasso.get().load(Nokri_SharedPrefManager.getProfileImage(getContext())).fit().centerCrop().into(dashboardImage);
                            //Picasso.get()(getContext()).load(coverJsonObject.getString("img")).fit().centerCrop().into(headerImageView);
                            Nokri_SharedPrefManager.saveProfileImage(dpJsonObject.getString("img"), getContext());
                            Nokri_SharedPrefManager.saveCoverImage(coverJsonObject.getString("img"), getContext());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                if (jsonData.getString("field_type_name").equals("emp_name")) {
                                    nameTextView.setText(jsonData.getString("value"));
                                    continue;
                                }


                                if (jsonData.getString("field_type_name").equals("emp_adress")) {
                                    addressTextView.setText(jsonData.getString("value"));
                                    Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                    model.setTitle(jsonData.getString("key"));
                                    model.setDescription(jsonData.getString("value"));
                                    modelList.add(model);
                                    continue;
                                }
                                if (jsonData.getString("field_type_name").equals("about_me")) {
                                    aboutMeTextView.setText(jsonData.getString("key"));
                                    String aboutMeText = Nokri_Utils.stripHtml(jsonData.getString("value")).toString();
                                    if (aboutMeText.isEmpty() || aboutMeText == null)
                                        aboutMeDataTextView.setText(defaultAboutText);
                                    else
                                        aboutMeDataTextView.setText(aboutMeText);
                                    Nokri_SharedPrefManager.saveAbout(jsonData.getString("value"), getContext());
                                    continue;
                                }
                                if (jsonData.getString("field_type_name").equals("loc")) {
                                    locationAndMapTextView.setText(jsonData.getString("key"));
                                    continue;
                                }
                                if (jsonData.getString("field_type_name").equals("emp_lat")) {

                                    try {
                                        LATITUDE = Double.parseDouble(jsonData.getString("value"));
                                        continue;
                                    } catch (NumberFormatException e) {
                                        LATITUDE = 0;
                                        continue;
                                    }
                                }
                                if (jsonData.getString("field_type_name").equals("emp_long")) {
                                    try {
                                        LONGITUDE = Double.parseDouble(jsonData.getString("value"));
                                        continue;
                                    } catch (NumberFormatException e) {
                                        LONGITUDE = 0;
                                        continue;
                                    }
                                }

                                if (jsonData.getString("field_type_name").equals("your_dashbord")) {
                                    yourDashboardTextView.setText(jsonData.getString("key"));
                                    continue;
                                }
                                if (jsonData.getString("key").equals("dp") || jsonData.getString("key").equals("cover"))
                                    continue;
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(jsonData.getString("key"));
                                model.setDescription(jsonData.getString("value"));
                                model.setFieldTypeName(jsonData.getString("field_type_name"));
                                if (jsonData.getString("field_type_name").equals("emp_est"))
                                    Nokri_SharedPrefManager.saveEstablishedSince(jsonData.getString("value"), getContext());
                                if (jsonData.getString("field_type_name").equals("emp_nos"))
                                    Nokri_SharedPrefManager.saveNumberOfEmployees(jsonData.getString("value"), getContext());
                                if (jsonData.getString("field_type_name").equals("emp_rgstr"))
                                    Nokri_SharedPrefManager.saveMemberSince(jsonData.getString("value"), getContext());
                                if (jsonData.getString("field_type_name").equals("emp_head"))
                                    Nokri_SharedPrefManager.saveHeadline(jsonData.getString("value"), getContext());


                                modelList.add(model);
                                dialogManager.hideAlertDialog();
                            }
                            Log.v("array", jsonArray.toString());
                        }

                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(getContext(), e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(getContext(), e.getMessage());
                    }
                    adapter1 = new Nokri_DescriptionRecyclerViewAdapter(modelList, getContext(), 0, new Nokri_DescriptionRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Nokri_DescriptionModel item) {
                            androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                            androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            RelativeLayout placeholder = getActivity().findViewById(R.id.fragment_placeholder);
                            fragmentTransaction.replace(R.id.fragment_placeholder, new Nokri_CompanyEditProfileFragment()).commit();
                        }
                    });

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView1.setLayoutManager(layoutManager);

                    recyclerView1.setItemAnimator(new DefaultItemAnimator());
                    recyclerView1.setAdapter(adapter1);
                    if (map!=null)
                        map.getMapAsync(NokriEmployeerDashboardFragment.this);
                }


                loadingLayout.setVisibility(View.GONE);
                shimmerContainer.setVisibility(View.GONE);
                shimmerContainer.stopShimmer();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_employeer_dashboard, container, false);
        if (Nokri_Globals.showEmployerMap) {
            map = view.findViewById(R.id.map_fragment);
            map.onCreate(savedInstanceState);

            map.onResume();
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        // map.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
     /*   LatLng location = new LatLng(LATITUDE, LONGITUDE);
        map.addMarker(new MarkerOptions().position(location));
        map.setMinZoomPreference(Nokri_Config.MAP_CAM_MIN_ZOOM);
        map.setMaxZoomPreference(Nokri_Config.MAP_CAM_MAX_ZOOM);
        map.moveCamera(CameraUpdateFactory.newLatLng(location));*/
        LatLng location = new LatLng(LATITUDE, LONGITUDE);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.addMarker(new MarkerOptions().position(location));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location).zoom(Nokri_Config.MAP_CAM_MIN_ZOOM).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (map != null)
            map.onResume();
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
        if (map != null)
            map.onLowMemory();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_btn_facebook:
                Nokri_Utils.opeInBrowser(getContext(), facebook);
                break;
            case R.id.img_btn_twitter:
                Nokri_Utils.opeInBrowser(getContext(), twitter);
                break;
            case R.id.img_btn_linkedin:
                Nokri_Utils.opeInBrowser(getContext(), linkedin);
                break;
            case R.id.img_btn_goole_plus:
                Nokri_Utils.opeInBrowser(getContext(), googleplus);
                break;

        }
    }

    private YouTubePlayer YPlayer;

    private void nokri_initYoutube(final String url) {


        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize(placeInitialize(), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.setFullscreen(false);
                    YPlayer.cueVideo(url);
//                    YPlayer.play();

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

            }
        });
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
}
