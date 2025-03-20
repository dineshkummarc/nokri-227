package com.scriptsbundle.nokri.guest.search.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.jobs.Activties.CreateJobAlert;
import com.scriptsbundle.nokri.candidate.jobs.fragments.Nokri_AllJobsFragment;
import com.scriptsbundle.nokri.custom.MaterialProgressBar;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.guest.search.LocationListen;
import com.scriptsbundle.nokri.guest.search.models.LocationModel;
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
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import com.zhouyou.view.seekbar.SignSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_JobSearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, AdapterView.OnItemClickListener{

    private TextView searchByTitleTextView;
    private EditText searchEditText;
    private ImageButton searchImageButton;
    private Nokri_FontManager fontManager;
    private LinearLayout searchNow;
    View view;
    ImageView locationButton;
    private TextView toolbarTitleTextView;
    private RelativeLayout filgersResetContainer;
    private TextView filtersTextView;
    private ImageButton closeImgeButton;
    private Button resetButton;
    List<View> allViewInstanceforCustom = new ArrayList<>();

    private TextView footerTextView;
    //private Nokri_GuestDashboardActivity guestDashboardActivity;


    private Nokri_SpinnerModel countrySpinnerModel, stateSpinnerModel, citySpinnerModel, townSpinnerModel;
    private TextView countryTextView, cityTextView, stateTextView, townTextView;

    private Spinner countrySpinner, stateSpinner, citySpinner, townSpinner;
    private String country = "", state = "", city = "", town = "";
    private MaterialProgressBar progressBar;


    private TextView jobCategoryTextView, jobQualificationTextView, jobTypeTextView, salaryCurrencyTextView, jobShiftTextView, jobLevelTextView, jobSkillsTextView;
    private TextView jobSubCategoryTextView1, jobSubCategoryTextView2, jobSubCategoryTextView3;
    private Spinner jobCategorySpinner, jobQualificationSpinner, jobTypeSpinner, salaryCurrenencySpinner, jobShiftSpinner, jobLevelSpinner, jobSkillsSpinner;
    private Spinner subCategorySinner1, subCategorySinner2, subCategorySinner3;
    private RelativeLayout stateContainer, cityContainer, townContainer;

    private Nokri_SpinnerModel jobCategorySpinnerModel, jobQualificationSpinnerModel, jobTypeSpinenrModel, salaryCurrerencySpinneModel, jobShiftSpinnerModel, jobLevelSpinnerModel, jobSkillsSpinnerModel;
    private Nokri_SpinnerModel subCategorySinner1Model1, subCategorySinnerModel2, subCategorySinnerModel3;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearLayout;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private String jobCategory = "", jobQualification = "", jobType = "", salaryCurrency = "", jobShift = "", jobLevel = "", jobSkills = "";
    private String subCategory1 = "", subCategory2 = "", subCategory3 = "";
    private ArrayList<String> jobTypeKeys = new ArrayList<>();
    private ArrayList<String> jobShiftKeys = new ArrayList<>();
    private String spinnerTitleText;
    private String[] values = new String[7];
    private TextView[] textViews = new TextView[7];
    private Spinner[] spinners = new Spinner[7];
    private Nokri_SpinnerModel[] spinnerModels = new Nokri_SpinnerModel[7];
    private RelativeLayout subCategoryContainer1, subCategoryContainer2, subCategoryContainer3;
    private Nokri_DialogManager dialogManager;

    //Map Related Fields
    TextView textRadius, textLatitude, textLongitude;
    EditText editTextLatitude, editTextLongitude;
    AutoCompleteTextView placesAutoComplete;
    PlacesClient placesClient;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    double LATITUDE = 0;
    double LONGITUDE= 0;
    private GoogleMap googleMap;
    SignSeekBar seekBar;


    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    LocationListen locationListener;
    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
        if (Nokri_SharedPrefManager.getAccountType(getActivity()).equals("candidate")) {
            toolbar.findViewById(R.id.createAlert).setVisibility(View.VISIBLE);
        } else {
            toolbar.findViewById(R.id.createAlert).setVisibility(View.GONE);

        }
        toolbar.findViewById(R.id.collapse).setVisibility(View.GONE);


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }

    public Nokri_JobSearchFragment() {

    }


    @Override
    public void onPause() {
        super.onPause();


        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.GONE);
        toolbar.findViewById(R.id.collapse).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.createAlert).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_job_search, container, false);
        return view;


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        nokri_getJobSearchData();


        String toolbarTitle = "";


        if (Nokri_SharedPrefManager.isAccountPublic(getContext())) {
            Nokri_GuestDashboardModel model = Nokri_SharedPrefManager.getGuestSettings(getContext());
            toolbarTitle = model.getExplore();
        } else if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {

            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
            toolbarTitle = model.getAdvancedSearch();
        } else if (Nokri_SharedPrefManager.isAccountCandidate(getContext())) {

            Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
            toolbarTitle = model.getExplore();

        }


        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(toolbarTitle);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.GONE);
        toolbar.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.createAlert).setVisibility(View.VISIBLE);


    }


    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();


        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        searchByTitleTextView = getView().findViewById(R.id.txt_search_by_title);
        searchEditText = getView().findViewById(R.id.edittxt_search);
        searchImageButton = getView().findViewById(R.id.img_btn_search);
        searchImageButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        footerTextView = getView().findViewById(R.id.footer_text);

        searchNow = getView().findViewById(R.id.search_now);


        //Map Related Data
//        textLatitude = getView().findViewById(R.id.txt_latitude);
//        textLongitude = getView().findViewById(R.id.txt_longitude);
        textRadius = getView().findViewById(R.id.txt_radius);
        editTextLatitude = getView().findViewById(R.id.edittxt_latitude);
        editTextLongitude = getView().findViewById(R.id.edittxt_longitude);
        placesAutoComplete = getView().findViewById(R.id.autoCompleteTextView);
        placesAutoComplete.addTextChangedListener(this);
        placesAutoComplete.setOnItemClickListener(this);
        placesClient = Places.createClient(getContext());
        seekBar = getView().findViewById(R.id.seekBar);
        locationButton = getView().findViewById(R.id.locationButton);
        locationButton.setOnClickListener(this);

        seekBar.getConfigBuilder().trackColor(Color.GRAY)
                .thumbColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .trackColor(Color.GRAY)
                .signColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .secondTrackColor(Color.parseColor(Nokri_Config.APP_COLOR))
                .build();

        LocationModel locationModel = Nokri_SharedPrefManager.getLocationSettings(getActivity());
        placesAutoComplete.setHint(locationModel.getGeoLocationText());
        editTextLatitude.setHint(locationModel.getLatitudeText());
        editTextLongitude.setHint(locationModel.getLongitudeText());
//        textLongitude.setHint(locationModel.getLongitudeText());
//        textLatitude.setHint(locationModel.getLatitudeText());
        textRadius.setText(locationModel.getRadiusText());

        searchNow.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

        jobCategoryTextView = getView().findViewById(R.id.txt_job_caregory);
        jobQualificationTextView = getView().findViewById(R.id.txt_job_qualification);
        jobTypeTextView = getView().findViewById(R.id.txt_job_type);
        salaryCurrencyTextView = getView().findViewById(R.id.txt_salary_currency);
        jobShiftTextView = getView().findViewById(R.id.txt_job_shift);
        jobLevelTextView = getView().findViewById(R.id.txt_job_level);
        jobSkillsTextView = getView().findViewById(R.id.txt_job_skills);
//        horizontalScrollView=getView().findViewById(R.id.horiradio);
        jobSubCategoryTextView1 = getView().findViewById(R.id.txt_sub_category1);
        jobSubCategoryTextView2 = getView().findViewById(R.id.txt_sub_category2);
        jobSubCategoryTextView3 = getView().findViewById(R.id.txt_sub_category3);


        countryTextView = getView().findViewById(R.id.txt_country);
        cityTextView = getView().findViewById(R.id.txt_city);
        stateTextView = getView().findViewById(R.id.txt_state);
        townTextView = getView().findViewById(R.id.txt_town);

        countrySpinner = getView().findViewById(R.id.spinner_country);
        stateSpinner = getView().findViewById(R.id.spinner_state);
        citySpinner = getView().findViewById(R.id.spinner_city);
        townSpinner = getView().findViewById(R.id.spinner_town);
        progressBar = getView().findViewById(R.id.progress);


        stateContainer = getView().findViewById(R.id.state_container);
        cityContainer = getView().findViewById(R.id.city_container);
        townContainer = getView().findViewById(R.id.town_container);

        textViews[0] = jobCategoryTextView;
        textViews[1] = jobQualificationTextView;
        textViews[2] = jobTypeTextView;
        textViews[3] = salaryCurrencyTextView;
        textViews[4] = jobShiftTextView;
        textViews[5] = jobLevelTextView;
        textViews[6] = jobSkillsTextView;

        jobCategorySpinner = getView().findViewById(R.id.spinner_job_category);
        jobQualificationSpinner = getView().findViewById(R.id.spinner_job_qualificaion);
        jobTypeSpinner = getView().findViewById(R.id.spinner_job_type);
        salaryCurrenencySpinner = getView().findViewById(R.id.spinner_salary_currency);
        jobShiftSpinner = getView().findViewById(R.id.spinner_job_shift);
        jobLevelSpinner = getView().findViewById(R.id.spinner_job_level);
        jobSkillsSpinner = getView().findViewById(R.id.spinner_job_skills);


        subCategorySinner1 = getView().findViewById(R.id.spinner_sub_category1);
        subCategorySinner2 = getView().findViewById(R.id.spinner_sub_category2);
        subCategorySinner3 = getView().findViewById(R.id.spinner_sub_category3);


        spinners[0] = jobCategorySpinner;
        spinners[1] = jobQualificationSpinner;
        spinners[2] = jobTypeSpinner;
        spinners[3] = salaryCurrenencySpinner;
        spinners[4] = jobShiftSpinner;
        spinners[5] = jobLevelSpinner;
        spinners[6] = jobSkillsSpinner;


//        radioGroup[0]=jobTypeSpinenrModel;

        spinnerModels[0] = jobCategorySpinnerModel;
        spinnerModels[1] = jobQualificationSpinnerModel;
        spinnerModels[2] = jobTypeSpinenrModel;
        spinnerModels[3] = salaryCurrerencySpinneModel;
        spinnerModels[4] = jobShiftSpinnerModel;
        spinnerModels[5] = jobLevelSpinnerModel;
        spinnerModels[6] = jobSkillsSpinnerModel;


        values[0] = jobCategory;
        values[1] = jobQualification;
        values[2] = jobType;
        values[3] = salaryCurrency;
        values[4] = jobShift;
        values[5] = jobLevel;
        values[6] = jobSkills;


        subCategoryContainer1 = getView().findViewById(R.id.sub_category_container1);
        subCategoryContainer2 = getView().findViewById(R.id.sub_category_container2);
        subCategoryContainer3 = getView().findViewById(R.id.sub_category_container3);


        searchImageButton.setOnClickListener(this);
        searchNow.setOnClickListener(this);

        toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        filgersResetContainer = getActivity().findViewById(R.id.filter_reset_container);
        filtersTextView = getActivity().findViewById(R.id.txt_filters);
        resetButton = getActivity().findViewById(R.id.btn_reset);
        closeImgeButton = getActivity().findViewById(R.id.img_btn_cross);

        //  getActivity().findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);

        closeImgeButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);




        // filgersResetContainer.setVisibility(View.VISIBLE);
    }


    private void nokri_setupFonts() {

        fontManager.nokri_setOpenSenseFontTextView(countryTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(cityTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(stateTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(townTextView, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(textLatitude, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontTextView(textLongitude, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(textRadius, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(footerTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView1, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView2, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView3, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(searchByTitleTextView, getActivity().getAssets());


        fontManager.nokri_setOpenSenseFontEditText(searchEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(placesAutoComplete, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(editTextLatitude, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(editTextLongitude, getActivity().getAssets());

        for (int i = 0; i < textViews.length; i++)
            fontManager.nokri_setOpenSenseFontTextView(textViews[i], getActivity().getAssets());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_btn_search:
                nokri_postSearch();
                break;
            case R.id.search_now:
                nokri_postSearch();
                break;
            case R.id.btn_reset:
                break;
            case R.id.img_btn_cross:
                getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
                filgersResetContainer.setVisibility(View.GONE);
                break;
            case R.id.locationButton:
                locationListener = new LocationListen(getActivity());
                locationListener.requestForUpdates();
                break;

        }

    }

    private void nokri_postSearch() {
        if (toolbarTitleTextView != null)
            toolbarTitleTextView.setText("Search Results");

        nokri_setValues();

        String location;
        location = country;
        if (!state.isEmpty())
            location = state;

        if (!city.isEmpty())
            location = city;

        if (!town.isEmpty())
            location = town;


        Nokri_JobSearchModel model = new Nokri_JobSearchModel();
        model.setSearchNow(searchEditText.getText().toString());
        model.setJobCategory(jobCategory);
        model.setJobQualification(jobQualification);
        model.setJobType(jobType);
        model.setSalaryCurrency(salaryCurrency);
        model.setJobShift(jobShift);
        model.setJobLevel(jobLevel);
        model.setJobSkills(jobSkills);
        model.setSubCategory1(subCategory1);
        model.setSubCategory2(subCategory2);
        model.setSubCategory3(subCategory3);
        model.setLocation(location);
        if (LATITUDE!=0){
            model.setLatitude(String.valueOf(LATITUDE));
            model.setLongitude(String.valueOf(LONGITUDE));
            model.setDistance(String.valueOf(seekBar.getProgress()));
        }else{
            model.setLatitude("");
            model.setLongitude("");
            model.setDistance("");
        }
        Log.d("tesssssssssssssssst", model.toString());
        Nokri_AllJobsFragment.ALL_JOBS_SOURCE = "external";
        Nokri_SharedPrefManager.saveJobSearchModel(model, getContext());
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment allJobsFragment = new Nokri_AllJobsFragment();
        fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), allJobsFragment).addToBackStack(null).commit();

    }


    private void nokri_getJobSearchData() {
        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getFilters(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getFilters(Nokri_RequestHeaderManager.addHeaders());
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
                JsonObject optionsObj = null;
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONArray extraArray = response.getJSONArray("extra");
                        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                        toolbar.findViewById(R.id.createAlert).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getActivity(), CreateJobAlert.class);
                                i.putExtra("jsonObject", response.toString());
                                startActivity(i);
                            }
                        });

                        for (int i = 0; i < extraArray.length(); i++) {
                            JSONObject extra = extraArray.getJSONObject(i);
                            if (extra.getString("field_type_name").equals("job_title")) {
                                searchByTitleTextView.setText(extra.getString("key"));
                                searchEditText.setHint(extra.getString("value"));


                            } else if (extra.getString("field_type_name").equals("job_post_btn")) {
                                footerTextView.setText(extra.getString("key"));
                                searchByTitleTextView.setText(extra.getString("key"));

                            } else if (extra.getString("field_type_name").equals("job_search_cat")) {
                                jobSubCategoryTextView1.setText(extra.getString("key"));
                                jobSubCategoryTextView2.setText(extra.getString("key"));
                                jobSubCategoryTextView3.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("page_title")) {
                                toolbarTitleTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("country")) {

                                countryTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("state")) {

                                stateTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("city")) {

                                cityTextView.setText(extra.getString("key"));
                            } else if (extra.getString("field_type_name").equals("town")) {

                                townTextView.setText(extra.getString("key"));
                            }


                        }

                        JSONObject data = response.getJSONObject("data");
                        JSONArray searchFieldsArray = data.getJSONArray("search_fields");
                        spinnerTitleText = searchFieldsArray.getJSONObject(0).getString("is_show");
                        for (int i = 0; i < searchFieldsArray.length(); i++) {

                            JSONObject filterObject = searchFieldsArray.getJSONObject(i);
                            JSONArray filters = filterObject.getJSONArray("value");
                            if (filterObject.getString("field_type_name").equals("job_location")) {
                                //countryTextView.setText(filterObject.getString("key"));
                                countrySpinnerModel = nokri_populateSpinner(countrySpinner, filterObject.getJSONArray("value"));
                                continue;
                            }

                            if (filterObject.getString("field_type_name").equals("job_type")) {

                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                params.topMargin = 25;
                                params.bottomMargin = 25;
                                params.rightMargin = 25;
                                params.leftMargin = 25;

                                JSONArray jobTypeValueArray = filterObject.getJSONArray("value");
                                radioGroup = getView().findViewById(R.id.radioGroup);
                                radioGroup.setOrientation(RadioGroup.HORIZONTAL);

                                for (int j = 0; j < jobTypeValueArray.length(); j++) {
                                    JSONObject jobTypeJson = jobTypeValueArray.getJSONObject(j);

                                    RadioButton radioButton = new RadioButton(getActivity());
                                    radioButton.setButtonDrawable(null);
                                    radioButton.setTextSize(12);
//                                                radioButton.setPadding(15,15,15,15);
                                    radioButton.setPadding(30, 12, 30, 12);
                                    radioButton.setTextColor(Color.LTGRAY);
                                    radioButton.setBackgroundResource(R.drawable.radiobuttonbg);
//                                    radioButton.setTextColor(ContextCompat.getColorStateList(getContext(), Color.parseColor(Nokri_Config.APP_COLOR)));
                                    radioButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                    radioButton.setText(jobTypeJson.getString("value"));
                                    jobTypeKeys.add(jobTypeJson.getString("key"));
                                    radioGroup.addView(radioButton, params);
                                    radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if (isChecked) {
                                                radioButton.setBackgroundResource(R.drawable.advance_search_selected_radio);

                                                GradientDrawable drawable = (GradientDrawable) radioButton.getBackground();
                                                drawable.setColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                                radioButton.setTextColor(Color.parseColor("#FFFFFF"));
                                            } else {
                                                radioButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                                radioButton.setBackgroundResource(R.drawable.radiobuttonbg);
                                            }
                                        }
                                    });
//                                                radioButton.setLayoutParams(params);

//                                        radioButton.setText(jobTypeJson.getString("key"));

                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                                            int index = radioGroup.indexOfChild(radioButton);
                                            jobType = jobTypeKeys.get(index);
//                                            Nokri_ToastManager.showShortToast(getContext(),jobType);
                                        }
                                    });
                                }

                            }
                            if (filterObject.getString("field_type_name").equals("job_shift")) {
                                RelativeLayout.LayoutParams params22 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                params22.topMargin = 25;
                                params22.bottomMargin = 25;
                                params22.rightMargin = 25;
                                params22.leftMargin = 25;

                                JSONArray jobShiftValueArray = filterObject.getJSONArray("value");
                                radioGroup = getView().findViewById(R.id.radioGroup1);
                                radioGroup.setOrientation(RadioGroup.HORIZONTAL);

                                for (int k = 0; k < jobShiftValueArray.length(); k++) {
                                    JSONObject jobShiftJson = jobShiftValueArray.getJSONObject(k);

                                    RadioButton radioButton1 = new RadioButton(getActivity());
                                    radioButton1.setButtonDrawable(null);
                                    radioButton1.setTextSize(12);
                                    radioButton1.setPadding(30, 12, 30, 12);
                                    radioButton1.setTextColor(Color.LTGRAY);
                                    radioButton1.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                    radioButton1.setBackgroundResource(R.drawable.radiobuttonbg);
                                    radioButton1.setText(jobShiftJson.getString("value"));
                                    radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if (isChecked) {
                                                radioButton1.setTextColor(Color.parseColor("#FFFFFF"));
                                                radioButton1.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                            } else {
                                                radioButton1.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                                                radioButton1.setBackgroundResource(R.drawable.radiobuttonbg);
                                            }
                                        }
                                    });
                                    jobShiftKeys.add(jobShiftJson.getString("key"));
                                    radioGroup.addView(radioButton1, params22);
//                                                radioButton.setLayoutParams(params);

                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                                            int index = radioGroup.indexOfChild(radioButton);
                                            jobShift = jobShiftKeys.get(index);
//                                            Nokri_ToastManager.showShortToast(getContext(),jobShift);
                                        }
                                    });
                                }

                            } else {
//                                dialogManager.showCustom(responseObject.message());
//                                dialogManager.hideAfterDelay();
                            }

                            try {
                                textViews[i].setText(filterObject.getString("key"));
                                spinnerModels[i] = nokri_populateSpinner(spinners[i], filters);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

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

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                Nokri_ToastManager.showShortToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray) {

        Nokri_SpinnerModel model = new Nokri_SpinnerModel();
        model.getNames().add(spinnerTitleText);
        model.getIds().add(spinnerTitleText);
        model.getHasChild().add(false);
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                model.getHasChild().add(jsonObject.getBoolean("has_child"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, model.getNames(), true));
            spinner.setOnItemSelectedListener(this);
        }

        return model;
    }

    private void nokri_setValues() {


        for (int i = 0; i < spinners.length; i++) {
            if (spinners[i].getSelectedItemPosition() == 0)
                values[i] = "";
            else {
                values[i] = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

            }
        }

        jobCategory = values[0];
        jobQualification = values[1];
        //jobType = values[2];
        salaryCurrency = values[3];
//        jobShift = values[4];
        jobLevel = values[5];
        jobSkills = values[6];

        if (subCategorySinner1.getVisibility() == View.VISIBLE) {
            if (subCategorySinner1.getSelectedItemPosition() > 0) {
                subCategory1 = subCategorySinner1Model1.getIds().get(subCategorySinner1.getSelectedItemPosition());
            } else
                subCategory1 = "";
        }

        if (subCategorySinner2.getVisibility() == View.VISIBLE) {
            if (subCategorySinner2.getSelectedItemPosition() > 0) {
                subCategory2 = subCategorySinnerModel2.getIds().get(subCategorySinner2.getSelectedItemPosition());
                subCategory1 = "";
            } else
                subCategory2 = "";
        }
        if (subCategorySinner3.getVisibility() == View.VISIBLE) {
            if (subCategorySinner3.getSelectedItemPosition() > 0) {
                subCategory3 = subCategorySinnerModel3.getIds().get(subCategorySinner3.getSelectedItemPosition());
                subCategory1 = "";
                subCategory2 = "";
            } else
                subCategory3 = "";
        }

        if (countrySpinner.getAdapter() != null) {
            if (countrySpinnerModel.getIds() != null && countrySpinnerModel.getIds().size() > 0 && countrySpinner.getSelectedItemPosition() != 0)
                country = countrySpinnerModel.getIds().get(countrySpinner.getSelectedItemPosition());
        } else
            country = "";

        if (stateSpinner.getAdapter() != null && stateSpinner.getVisibility() == View.VISIBLE && stateSpinner.getSelectedItemPosition() != 0) {
            if (stateSpinnerModel.getIds() != null && stateSpinnerModel.getIds().size() > 0)
                state = stateSpinnerModel.getIds().get(stateSpinner.getSelectedItemPosition());
        } else state = "";

        if (citySpinner.getAdapter() != null && citySpinner.getVisibility() == View.VISIBLE && citySpinner.getSelectedItemPosition() != 0) {
            if (citySpinnerModel.getIds() != null && citySpinnerModel.getIds().size() > 0)
                city = citySpinnerModel.getIds().get(citySpinner.getSelectedItemPosition());
        } else city = "";
        if (townSpinner.getAdapter() != null && townSpinner.getVisibility() == View.VISIBLE && townSpinner.getSelectedItemPosition() != 0) {
            if (townSpinnerModel.getIds() != null && townSpinnerModel.getIds().size() > 0)
                town = townSpinnerModel.getIds().get(townSpinner.getSelectedItemPosition());
        } else town = "";


    }

    private void nokri_getCountryCityState(String id, final String tag) {

     /*   dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());*/
        progressBar.setVisibility(View.VISIBLE);
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id", id);

        params.add(jsonObject);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONArray response = new JSONArray(responseObject.body().string());
//                        Log.v("response", responseObject.message());

                        switch (tag) {
                            case "state":
                                stateSpinnerModel = nokri_populateSpinner(stateSpinner, response);
                                break;
                            case "city":
                                citySpinnerModel = nokri_populateSpinner(citySpinner, response);
                                break;
                            case "town":
                                townSpinnerModel = nokri_populateSpinner(townSpinner, response);
                                break;

                        }


                        //     dialogManager.hideAlertDialog();
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                       /* dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();*/
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    } catch (IOException e) {
                       /* dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();*/
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();

                    }
                } else {
                 /*   dialogManager.showCustom(responseObject.code()+"");
                    dialogManager.hideAfterDelay();*/
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            /*    dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();*/
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = parent.getId();
        if (i == R.id.spinner_country) {
            if (countrySpinnerModel != null && countrySpinnerModel.getHasChild().get(position)) {
                stateContainer.setVisibility(View.VISIBLE);
                stateTextView.setVisibility(View.VISIBLE);
                stateSpinner.setVisibility(View.VISIBLE);
                nokri_getCountryCityState(countrySpinnerModel.getIds().get(position), "state");
            } else {

                stateContainer.setVisibility(View.GONE);
                stateTextView.setVisibility(View.GONE);
                stateSpinner.setVisibility(View.GONE);
                cityContainer.setVisibility(View.GONE);
                citySpinner.setVisibility(View.GONE);
                cityTextView.setVisibility(View.GONE);
                townContainer.setVisibility(View.GONE);
                townSpinner.setVisibility(View.GONE);
                townTextView.setVisibility(View.GONE);

            }
        } else if (i == R.id.spinner_state) {
            if (stateSpinnerModel != null && stateSpinnerModel.getHasChild().get(position)) {
                cityContainer.setVisibility(View.VISIBLE);
                cityTextView.setVisibility(View.VISIBLE);
                citySpinner.setVisibility(View.VISIBLE);
                nokri_getCountryCityState(stateSpinnerModel.getIds().get(position), "city");
            } else {

                cityContainer.setVisibility(View.GONE);
                citySpinner.setVisibility(View.GONE);
                cityTextView.setVisibility(View.GONE);
                townContainer.setVisibility(View.GONE);
                townSpinner.setVisibility(View.GONE);
                townTextView.setVisibility(View.GONE);

            }
        } else if (i == R.id.spinner_city) {
            if (citySpinnerModel != null && citySpinnerModel.getHasChild().get(position)) {
                townContainer.setVisibility(View.VISIBLE);
                townTextView.setVisibility(View.VISIBLE);
                townSpinner.setVisibility(View.VISIBLE);
                nokri_getCountryCityState(citySpinnerModel.getIds().get(position), "town");
            } else {


                townContainer.setVisibility(View.GONE);
                townSpinner.setVisibility(View.GONE);
                townTextView.setVisibility(View.GONE);

            }
        } else if (i == R.id.spinner_town) {
        } else if (i == R.id.spinner_job_category) {
            if (spinnerModels[0] != null && spinnerModels[0].getHasChild().get(position)) {

                subCategorySinner1.setVisibility(View.VISIBLE);
                jobSubCategoryTextView1.setVisibility(View.VISIBLE);
                subCategoryContainer1.setVisibility(View.VISIBLE);
                nokri_getSubFields(spinnerModels[0].getIds().get(position), "cat1");
            } else {

                subCategorySinner1.setVisibility(View.GONE);
                jobSubCategoryTextView1.setVisibility(View.GONE);
                subCategoryContainer1.setVisibility(View.GONE);
                subCategorySinner2.setVisibility(View.GONE);
                jobSubCategoryTextView2.setVisibility(View.GONE);
                subCategoryContainer2.setVisibility(View.GONE);
                subCategorySinner3.setVisibility(View.GONE);
                jobSubCategoryTextView3.setVisibility(View.GONE);
                subCategoryContainer3.setVisibility(View.GONE);
            }
        } else if (i == R.id.spinner_sub_category1) {
            if (subCategorySinner1Model1 != null && subCategorySinner1Model1.getHasChild().get(position)) {
                subCategoryContainer2.setVisibility(View.VISIBLE);
                subCategorySinner2.setVisibility(View.VISIBLE);
                jobSubCategoryTextView2.setVisibility(View.VISIBLE);
                nokri_getSubFields(subCategorySinner1Model1.getIds().get(position), "cat2");
            } else {
                subCategoryContainer2.setVisibility(View.GONE);
                subCategorySinner2.setVisibility(View.GONE);
                jobSubCategoryTextView2.setVisibility(View.GONE);
                subCategorySinner3.setVisibility(View.GONE);
                jobSubCategoryTextView3.setVisibility(View.GONE);
                subCategoryContainer3.setVisibility(View.GONE);
            }
        } else if (i == R.id.spinner_sub_category2) {
            if (subCategorySinnerModel2 != null && subCategorySinnerModel2.getHasChild().get(position)) {

                subCategorySinner3.setVisibility(View.VISIBLE);
                jobSubCategoryTextView3.setVisibility(View.VISIBLE);
                subCategoryContainer3.setVisibility(View.VISIBLE);
                nokri_getSubFields(subCategorySinnerModel2.getIds().get(position), "cat3");
            } else {

                subCategorySinner3.setVisibility(View.GONE);
                jobSubCategoryTextView3.setVisibility(View.GONE);
                subCategoryContainer3.setVisibility(View.GONE);

            }
        }










         /*   case R.id.spinner_sub_category3 :
                if(subCategorySinnerModel3!= null && subCategorySinnerModel3.getHasChild().get(position))
                {


                    nokri_getSubFields(subCategorySinnerModel3.getIds().get(position),"cat4");
                }

                break;*/
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void nokri_getSubFields(String id, final String tag) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cat_id", id);

        params.add(jsonObject);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONArray response = new JSONArray(responseObject.body().string());
//                        Log.v("response", responseObject.message());

                        switch (tag) {
                            case "cat1":
                                subCategorySinner1Model1 = nokri_populateSpinner(subCategorySinner1, response);
                                break;
                            case "cat2":
                                subCategorySinnerModel2 = nokri_populateSpinner(subCategorySinner2, response);
                                break;
                            case "cat3":
                                subCategorySinnerModel3 = nokri_populateSpinner(subCategorySinner3, response);
                                break;

                        }


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
                    dialogManager.showCustom(responseObject.code() + "");
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showShortToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        nokri_manageAutoComplete(s.toString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String placeId = ids.get(position);
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);


        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i("Places", "Place found: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
            LATITUDE = place.getLatLng().latitude;
            LONGITUDE = place.getLatLng().longitude;
            editTextLatitude.setText(place.getLatLng().latitude + "");
            editTextLongitude.setText(place.getLatLng().longitude + "");
            setMapLocation();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();

                Log.e("Places", "Place not found: " + exception.getMessage());
            }
        });

    }


    private void nokri_manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();


        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

            ids.clear();
            places.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                places.add(prediction.getFullText(null).toString());
                ids.add(prediction.getPlaceId());
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getFullText(null).toString());
            }
            String[] data = places.toArray(new String[places.size()]);  // terms is a List<String>

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_dropdown_item_1line, data);
            placesAutoComplete.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }

    private void setMapLocation() {
        if (googleMap != null) {
            googleMap.clear();
            LatLng location = new LatLng(LATITUDE, LONGITUDE);
            googleMap.addMarker(new MarkerOptions().position(location));
            googleMap.setMinZoomPreference(Nokri_Config.MAP_CAM_MIN_ZOOM);
            googleMap.setMaxZoomPreference(Nokri_Config.MAP_CAM_MAX_ZOOM);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String latitude = intent.getStringExtra("latitude");
            String longitude = intent.getStringExtra("longitude");
            if (latitude != null){
                editTextLatitude.setText(latitude);
                editTextLongitude.setText(longitude);
                if (locationListener!=null)
                    locationListener.stopLocationUpdates();
            }
            LATITUDE = Double.parseDouble(latitude);
            LONGITUDE = Double.parseDouble(longitude);

            try {
                getAddressFromCurrentLocation(LATITUDE,LONGITUDE);
            } catch (IOException e) {
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            Log.d("receiver", "Latitude : " + latitude + " Longitude  : " + longitude);
        }
    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void getAddressFromCurrentLocation(double latitude,double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        if (address!=null){
            placesAutoComplete.setText(address);
        }else{
            Toast.makeText(getActivity(), "Couldn't find address", Toast.LENGTH_SHORT).show();
        }
    }
}
