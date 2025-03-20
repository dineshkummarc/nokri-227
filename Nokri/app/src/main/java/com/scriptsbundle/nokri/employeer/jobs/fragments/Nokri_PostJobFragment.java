package com.scriptsbundle.nokri.employeer.jobs.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidbuts.multispinnerfilter.MultiSpinner;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.scriptsbundle.nokri.RichEditor.AREditor;
import com.scriptsbundle.nokri.RichEditor.styles.toolbar.ARE_ToolbarDefault;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.ARE_ToolItem_Bold;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.ARE_ToolItem_Italic;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.ARE_ToolItem_ListBullet;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.ARE_ToolItem_ListNumber;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.ARE_ToolItem_Underline;
import com.scriptsbundle.nokri.RichEditor.styles.toolitems.IARE_ToolItem;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Questionnaire.QuestionnaireActivity;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Questionnaire.QuestionnaireModel;
import com.scriptsbundle.nokri.employeer.payment.fragments.Nokri_PricingTableFragment;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.MaterialProgressBar;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_PostJobPackagesAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_PostJobsPackagesModel;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import info.hoang8f.android.segmented.SegmentedGroup;
import mabbas007.tagsedittext.TagsEditText;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class Nokri_PostJobFragment extends Fragment implements View.OnFocusChangeListener, AdapterView.OnItemSelectedListener, View.OnClickListener, OnMapReadyCallback, DatePickerDialog.OnDateSetListener, View.OnTouchListener, GoogleMap.OnMyLocationChangeListener, AdapterView.OnItemClickListener, TextWatcher {
    private AREditor jobDetailsEditor;
    private String jobDetails;

    private ImageView boldImageView, italicImageView, underlineImageView, numberBulletsImageView, listBulletsImageView;
    private MultiSpinner multiSpinner;
    private Nokri_FontManager fontManager;
    int expiryLimit;
    private EditText jobTitleEditText, applicatiponDeadlineEditText, latitudeEditText, longitudeEditText, noOfPositionEditText;
    private String title, location, deadline, latitude, longitude, positions;
    private Spinner jobCategorySpinner, subCategorySinner1, subCategorySinner2, subCategorySinner3, subCategorySinner4, jobQualificationSpinner, jobTypeSpinner;
    private String jobCategory = "", subCategory1 = "", subCategory2 = "", subCategory3 = "", subCategory4 = "", jobQualification = "", jobType = "";
    private Spinner salaryTypeSpinner, salaryCurrencySpinner, salaryOfferSpinner, jobExperienceSpinner, jobShiftSpinner, jobLevelSpinner, jobSkillsSpinner, jobExternalSpinner;
    private String salaryType = "", salaryCurrency = "", salaryOffer = "", jobExperience = "", jobShift = "", jobLevel = "", jobSkills = "";
    private String country = "", state = "", city = "", town = "";
    private String jobId;
    private Nokri_SpinnerModel jobCategorySpinnerModel, subCategorySinner1Model1, subCategorySinnerModel2, subCategorySinnerModel3, subCategorySinnerModel4, jobQualificationSpinnerModel, jobTypeSpinnerModel, jobExternalLinkSpinnerModel;
    private Nokri_SpinnerModel salaryTypeSpinnerModel, salaryCurrencySpinnerModel, salaryOfferSpinnerModel, jobExperienceSpinnerModel, jobShiftSpinnerModel, jobLevelSpinnerModel, jobSkillsSpinnerModel;
    private Button publishJobButton, publishJobButton2;
    private TagsEditText tagsEditText;
    private List<String> tags;
    private String extractedTag = "";
    TextView questionsCounter;
    private TextView basicInformationTextView, jobTitleTextView, jobCategotyTextView, jobSubCategoryTextView1, jobSubCategoryTextView2, jobSubCategoryTextView3, jobSubCategoryTextView4, jobDescriptionTextView;
    private TextView jobDetaialTextView, applicationDeadlineTextView, jobQualificaionTextView, jobTypeTextView, salaryTypeTextView, salaryCurrencyTextView, salaryOfferTextView, jobExerienceTextView, jobLevelTextView, jobShiftTextView, jobSkillsTextView, tagsTextView, noOfPositionTextView, whatsAppText;
    private TextView selectTextView, countryTextView, cityTextView, stateTextView, townTextView;
    private TextView locationOnMapTextView, setLocationTextView, latitudeTextView, longitudeTextView;
    TextView jobExternalTextView, externalLinkPlaceholder;
    EditText externalLinkEditText, whatsAppEdiText;
    private TextView boostTextView;
    private RecyclerView addOnRecyclerView;
    private AutoCompleteTextView autoCompleteTextView;
    private PlacesClient placesClient;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();

    private static double LATITUDE = 0, LONGITUDE = 0;
    private GoogleMap googleMap;
    private boolean shouldWait = true;
    private boolean isPaused = false;
    private MapView map;


    private Calendar calendar;

    private JsonArray selectedSkillsId = new JsonArray();
    private JsonArray preniumJobsId = new JsonArray();


    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    TextView questionHeading;

    private Nokri_DialogManager dialogManager;
    private MaterialProgressBar progressBar;
    RadioButton radioButtonYes, radioButtonNo;
    LinearLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;


    private void clearAllPreniumJobs() {
        for (int i = 0; i < preniumJobsId.size(); i++)
            preniumJobsId.remove(i);
    }

    private void setPreniumJobsId() {
        clearAllPreniumJobs();
        for (int i = 0; i < modelList.size(); i++) {
            if (modelList.get(i).isChecked() && modelList.get(i).isEditable())
                preniumJobsId.add(modelList.get(i).getId());
        }
    }

    public static String POST_JOB_CALLING_SOURCE = "";
    public static String POST_JOB_ID = "";

    private ArrayList<Nokri_PostJobsPackagesModel> modelList;

    public static QuestionnaireModel questionnaireModel;
    LinearLayout questionsLayout;


    public static int locationCount = 0;
    public static String countryId = "";
    public static String cityId = "";
    public static String stateId = "";
    public static String townId = "";
    LinearLayout countrySpinnerLayout;
    TextView categoryTitle, categoryValue;
    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();

    public Nokri_PostJobFragment() {

    }

    private void nokri_setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        addOnRecyclerView.setLayoutManager(layoutManager);
        addOnRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Nokri_PostJobPackagesAdapter adapter = new Nokri_PostJobPackagesAdapter(false, modelList, getContext());
        addOnRecyclerView.setAdapter(adapter);

    }


    private void nokri_setupSkillsSpinner() {

        LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();
        for (int i = 0; i < jobSkillsSpinnerModel.getNames().size(); i++) {

            map.put(jobSkillsSpinnerModel.getNames().get(i), false);

        }

        multiSpinner.setItems(map, new MultiSpinnerListener() {

            @Override
            public void onItemsSelected(boolean[] selected) {

                selectedSkillsId = new JsonArray();
                // your operation with code...
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {

                        selectedSkillsId.add(jobSkillsSpinnerModel.getIds().get(i));

                    }
                }
            }
        });

    }


    private void nokri_setupSkillsSpinnerForUpdate(JSONArray jsonArray) {
        LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                map.put(jsonArray.getJSONObject(i).getString("value"), jsonArray.getJSONObject(i).getBoolean("selected"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        multiSpinner.setItems(map, new MultiSpinnerListener() {

            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedSkillsId = new JsonArray();
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {

                        selectedSkillsId.add(jobSkillsSpinnerModel.getIds().get(i));
                    }
                }
            }

        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_post_job, container, false);
        map = view.findViewById(R.id.map_fragment);
        map.onCreate(savedInstanceState);

        map.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        map.getMapAsync(this);


//        nokri_initialize(view);
//        nokri_setupFonts();
//        if(POST_JOB_CALLING_SOURCE.equals("external"))
//            nokri_getUpdatePostJob(POST_JOB_ID);
//        else {
//            nokri_getPostJob();
//            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
//
//            TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
//
//            toolbarTitleTextView.setText(model.getPostJob());
//        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        nokri_initialize();
        nokri_setupFonts();
        if (POST_JOB_CALLING_SOURCE.equals("external"))
            nokri_getUpdatePostJob(POST_JOB_ID);
        else {
            nokri_getPostJob();
            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

            TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

            toolbarTitleTextView.setText(model.getPostJob());
        }

    }

    /*    @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);




        }*/
    private void nokri_setupFonts() {

        fontManager.nokri_setMonesrratSemiBioldFont(basicInformationTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(jobDetaialTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(selectTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(locationOnMapTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(boostTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(jobTitleTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobCategotyTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView1, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView2, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView3, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSubCategoryTextView4, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(whatsAppText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(jobDescriptionTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(applicationDeadlineTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobQualificaionTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobTypeTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(salaryTypeTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(salaryCurrencyTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(salaryOfferTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobExerienceTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobLevelTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobShiftTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobSkillsTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(tagsTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(categoryTitle, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(categoryValue, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(setLocationTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(latitudeTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(longitudeTextView, getActivity().getAssets());


        fontManager.nokri_setOpenSenseFontEditText(jobTitleEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(applicatiponDeadlineEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(latitudeEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(longitudeEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(whatsAppEdiText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(autoCompleteTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(longitudeEditText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontButton(publishJobButton, getActivity().getAssets());
    }

    public static ArrayList<String> questions = new ArrayList<>();

    private void nokri_initialize() {
        fontManager = new Nokri_FontManager();

        mainLayout = getView().findViewById(R.id.container1);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();

        countrySpinnerLayout = getView().findViewById(R.id.country_spinner_layout);
        categoryTitle = getView().findViewById(R.id.category_title);
        categoryValue = getView().findViewById(R.id.category_value);

        radioButtonNo = getView().findViewById(R.id.radioNo);
        radioButtonYes = getView().findViewById(R.id.radioYes);
        questionHeading = getView().findViewById(R.id.question_heading);
        fontManager.nokri_setOpenSenseFontTextView(questionHeading, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(radioButtonYes, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(radioButtonNo, getActivity().getAssets());

        radioButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), QuestionnaireActivity.class);
                startActivity(i);
            }
        });

        SegmentedGroup segmented3 = (SegmentedGroup) getView().findViewById(R.id.segmented2);
//        segmented3.setTintColor(Color.parseColor("#FFD0FF3C"), Color.parseColor("#FF7B07B2"));
        segmented3.setTintColor(Color.parseColor(Nokri_Config.APP_COLOR), Color.parseColor("#ffffff"));


        questionsLayout = getView().findViewById(R.id.question_layout);
        basicInformationTextView = getView().findViewById(R.id.txt_basic_info);
        jobTitleTextView = getView().findViewById(R.id.txt_job_title);
        jobCategotyTextView = getView().findViewById(R.id.txt_category);
        jobSubCategoryTextView1 = getView().findViewById(R.id.txt_sub_category1);
        jobSubCategoryTextView2 = getView().findViewById(R.id.txt_sub_category2);
        jobSubCategoryTextView3 = getView().findViewById(R.id.txt_sub_category3);
        jobSubCategoryTextView4 = getView().findViewById(R.id.txt_sub_category4);
        jobDescriptionTextView = getView().findViewById(R.id.txt_job_description);

        whatsAppText = getView().findViewById(R.id.text_whatsapp_no);


        jobDetaialTextView = getView().findViewById(R.id.txt_job_detail);
        applicationDeadlineTextView = getView().findViewById(R.id.txt_application_deadline);
        jobQualificaionTextView = getView().findViewById(R.id.txt_job_qualification);
        jobTypeTextView = getView().findViewById(R.id.txt_job_type);
        salaryTypeTextView = getView().findViewById(R.id.txt_salary_type);
        salaryCurrencyTextView = getView().findViewById(R.id.txt_salary_currency);
        salaryOfferTextView = getView().findViewById(R.id.txt_salary_offer);
        jobExerienceTextView = getView().findViewById(R.id.txt_job_experience);
        jobLevelTextView = getView().findViewById(R.id.txt_job_level);
        jobShiftTextView = getView().findViewById(R.id.txt_job_shift);
        jobSkillsTextView = getView().findViewById(R.id.txt_job_skills);
        tagsTextView = getView().findViewById(R.id.txt_tags);
        noOfPositionTextView = getView().findViewById(R.id.txt_no_of_pos);

        autoCompleteTextView = getView().findViewById(R.id.autoCompleteTextView);
        externalLinkEditText = getView().findViewById(R.id.edittxt_external_link);
        whatsAppEdiText = getView().findViewById(R.id.edittxt_whatsapp_no);


        selectTextView = getView().findViewById(R.id.txt_select_country);
        countryTextView = getView().findViewById(R.id.txt_country);
        cityTextView = getView().findViewById(R.id.txt_city);
        stateTextView = getView().findViewById(R.id.txt_state);
        townTextView = getView().findViewById(R.id.txt_town);

        locationOnMapTextView = getView().findViewById(R.id.txt_location_on_map);
        setLocationTextView = getView().findViewById(R.id.txt_set_location);
        latitudeTextView = getView().findViewById(R.id.txt_latitude);
        longitudeTextView = getView().findViewById(R.id.txt_longitude);


        boostTextView = getView().findViewById(R.id.txt_boost);

        jobTitleEditText = getView().findViewById(R.id.edittxt_job_title);
        applicatiponDeadlineEditText = getView().findViewById(R.id.edittxt_appication_deadline);
        latitudeEditText = getView().findViewById(R.id.edittxt_latitude);
        longitudeEditText = getView().findViewById(R.id.edittxt_longitude);
        placesClient = Places.createClient(getContext());
        noOfPositionEditText = getView().findViewById(R.id.edittxt_no_of_pos);


        jobDetailsEditor = getView().findViewById(R.id.edittxt_descripton);

        initToolbar();

//        jobDetailsEditor.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
//        jobDetailsEditor.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));


        tagsEditText = getView().findViewById(R.id.edittxt_tags);


        jobExternalTextView = getView().findViewById(R.id.txt_job_link);
        jobExternalSpinner = getView().findViewById(R.id.spinner_job_link);
        externalLinkPlaceholder = getView().findViewById(R.id.text_job_link);

        jobCategorySpinner = getView().findViewById(R.id.spinner_category);
        subCategorySinner1 = getView().findViewById(R.id.spinner_sub_category1);
        subCategorySinner2 = getView().findViewById(R.id.spinner_sub_category2);
        subCategorySinner3 = getView().findViewById(R.id.spinner_sub_category3);
        subCategorySinner4 = getView().findViewById(R.id.spinner_sub_category4);
        jobQualificationSpinner = getView().findViewById(R.id.spinner_job_qualificaion);
        jobTypeSpinner = getView().findViewById(R.id.spinner_job_type);
        salaryTypeSpinner = getView().findViewById(R.id.spinner_salary_type);
        salaryCurrencySpinner = getView().findViewById(R.id.spinner_salary_currency);
        salaryOfferSpinner = getView().findViewById(R.id.spinner_salary_offer);
        jobExperienceSpinner = getView().findViewById(R.id.spinner_job_experience);
        jobShiftSpinner = getView().findViewById(R.id.spinner_job_shift);
        jobLevelSpinner = getView().findViewById(R.id.spinner_job_level);
        jobSkillsSpinner = getView().findViewById(R.id.spinner_job_skills);
        multiSpinner = getView().findViewById(R.id.simpleMultiSpinner);


        questionsCounter = getView().findViewById(R.id.questionsCounter);
        fontManager.nokri_setOpenSenseFontTextView(questionsCounter, getActivity().getAssets());


        progressBar = getView().findViewById(R.id.progress);


        publishJobButton = getView().findViewById(R.id.btn_publish_job);
        publishJobButton2 = getView().findViewById(R.id.btn_publish_job2);

        Nokri_Utils.setRoundButtonColor(getContext(), publishJobButton);
        Nokri_Utils.setRoundButtonColor(getContext(), publishJobButton2);


        boldImageView = getView().findViewById(R.id.img_bold);
        italicImageView = getView().findViewById(R.id.img_italic);
        underlineImageView = getView().findViewById(R.id.img_underline);
        numberBulletsImageView = getView().findViewById(R.id.img_num_bullets);
        listBulletsImageView = getView().findViewById(R.id.img_list_bullets);


        calendar = Calendar.getInstance();

//        boldImageView.setOnClickListener(this);
//        italicImageView.setOnClickListener(this);
//        underlineImageView.setOnClickListener(this);
//        numberBulletsImageView.setOnClickListener(this);
//        listBulletsImageView.setOnClickListener(this);

        publishJobButton.setOnClickListener(this);
        publishJobButton2.setOnClickListener(this);
        applicatiponDeadlineEditText.setOnTouchListener(this);


        applicatiponDeadlineEditText.setOnFocusChangeListener(this);
        latitudeEditText.setOnFocusChangeListener(this);
        longitudeEditText.setOnFocusChangeListener(this);
        autoCompleteTextView.addTextChangedListener(this);
        autoCompleteTextView.setOnItemClickListener(this);
        jobTitleEditText.setOnFocusChangeListener(this);
        noOfPositionEditText.setOnFocusChangeListener(this);


        emptyTextView = getView().findViewById(R.id.txt_empty);
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);


        LinearLayout textarea = getView().findViewById(R.id.textarea);
        final LinearLayout container = getView().findViewById(R.id.container1);
        textarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobDetailsEditor.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(container.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }

        });


        autoCompleteTextView.setOnFocusChangeListener(this);

        addOnRecyclerView = getView().findViewById(R.id.recyclerview);

        addOnRecyclerView.setNestedScrollingEnabled(false);
        modelList = new ArrayList<>();
    }

    private void initToolbar() {
        ARE_ToolbarDefault mToolbar = getView().findViewById(R.id.areToolbar);
        IARE_ToolItem bold = new ARE_ToolItem_Bold();
        IARE_ToolItem italic = new ARE_ToolItem_Italic();
        IARE_ToolItem underline = new ARE_ToolItem_Underline();
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
        mToolbar.addToolbarItem(bold);
        mToolbar.addToolbarItem(italic);
        mToolbar.addToolbarItem(underline);
        mToolbar.addToolbarItem(listNumber);
        mToolbar.addToolbarItem(listBullet);
        jobDetailsEditor.setHideToolbar(true);
        fontManager.nokri_setOpenSenseFontEditText(jobDetailsEditor.getARE(), getActivity().getAssets());
        jobDetailsEditor.getARE().setTextSize((int) getResources().getDimension(R.dimen.richeditor_font_size));

//
        jobDetailsEditor.getARE().setToolbar(mToolbar);
//        jobDetailsEditor.setToolbarAlignment(AREditor.ToolbarAlignment.BOTTOM);

//        setHtml();

    }


    private void setHtml() {
        String html = "<p style=\"text-align: center;\"><strong>New Feature in 0.1.2</strong></p>\n" +
                "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #3366ff;\">In this release, you have a new usage with ARE.</span></p>\n" +
                "<p style=\"text-align: left;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #3366ff;\">AREditText + ARE_Toolbar, you are now able to control the position of the input area and where to put the toolbar at and, what ToolItems you'd like to have in the toolbar. </span></p>\n" +
                "<p style=\"text-align: left;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #3366ff;\">You can not only define the Toolbar (and it's style), you can also add your own ARE_ToolItem with your style into ARE.</span></p>\n" +
                "<p style=\"text-align: left;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #ff00ff;\"><em><strong>Why not give it a try now?</strong></em></span></p>";
        jobDetailsEditor.fromHtml(html);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bold:
//                jobDetailsEditor(RichEditText.BOLD,true);
                break;
            case R.id.img_italic:
//                jobDetailsEditor.setItalic();

                break;
            case R.id.img_underline:
//                jobDetailsEditor.setUnderline();
                break;
            case R.id.img_num_bullets:
//                jobDetailsEditor.setNumbers();
                break;
            case R.id.img_list_bullets:
//                jobDetailsEditor.setBullets();
                break;

            case R.id.btn_publish_job:

                if (nokri_areFieldsEmpty()) {
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    if (jobDetailsEditor.getHtml() == null || jobDetailsEditor.getHtml().toString().trim().isEmpty())
                        getView().findViewById(R.id.line).setBackgroundColor(Color.RED);

                    else
                        getView().findViewById(R.id.line).setBackgroundColor(getResources().getColor(R.color.gray));
                } else if (selectedSkillsId.size() == 0)
                    Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.SELECT_SKILL);

                else {
                    nokri_setValues();
                    nokri_postJob();
                }
                break;

            case R.id.btn_publish_job2:
                if (nokri_areFieldsEmpty()) {
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    if (jobDetailsEditor.getHtml() == null || jobDetailsEditor.getHtml().toString().trim().isEmpty())
                        view.findViewById(R.id.line).setBackgroundColor(Color.RED);

                    else
                        view.findViewById(R.id.line).setBackgroundColor(getResources().getColor(R.color.gray));
                } else if (selectedSkillsId.size() == 0)
                    Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.SELECT_SKILL);

                else {
                    nokri_setValues();
                    nokri_postJob();
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {

            case R.id.edittxt_job_title:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));

                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                noOfPositionEditText.setHintTextColor(getResources().getColor(R.color.grey));
                autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey));
                break;


            case R.id.edittxt_appication_deadline:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));

                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                noOfPositionEditText.setHintTextColor(getResources().getColor(R.color.grey));
                autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey));
                break;

            case R.id.edittxt_latitude:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));

                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                noOfPositionEditText.setHintTextColor(getResources().getColor(R.color.grey));
                autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey));
                break;

            case R.id.edittxt_longitude:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));

                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                noOfPositionEditText.setHintTextColor(getResources().getColor(R.color.grey));
                autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey));
                break;

            case R.id.edittxt_no_of_pos:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));

                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                noOfPositionEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.grey));

                break;
            case R.id.autoCompleteTextView:
                jobTitleEditText.setHintTextColor(getResources().getColor(R.color.grey));
                applicatiponDeadlineEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                latitudeEditText.setHintTextColor(getResources().getColor(R.color.grey));
                noOfPositionEditText.setHintTextColor(getResources().getColor(R.color.grey));
                autoCompleteTextView.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                break;
            default:
                break;


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_category:

                if (jobCategorySpinnerModel != null && jobCategorySpinnerModel.getHasChild().get(position)) {

                    subCategorySinner1.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView1.setVisibility(View.VISIBLE);
                    nokri_getSubFields(jobCategorySpinnerModel.getIds().get(position), "cat1");
                } else {
                    subCategorySinner1.setVisibility(View.GONE);
                    jobSubCategoryTextView1.setVisibility(View.GONE);
                    subCategorySinner2.setVisibility(View.GONE);
                    jobSubCategoryTextView2.setVisibility(View.GONE);
                    subCategorySinner3.setVisibility(View.GONE);
                    jobSubCategoryTextView3.setVisibility(View.GONE);
                    subCategorySinner4.setVisibility(View.GONE);
                    jobSubCategoryTextView4.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_sub_category1:
                if (subCategorySinner1Model1 != null && subCategorySinner1Model1.getHasChild().get(position)) {

                    subCategorySinner2.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView2.setVisibility(View.VISIBLE);
                    nokri_getSubFields(subCategorySinner1Model1.getIds().get(position), "cat2");
                } else {

                    subCategorySinner2.setVisibility(View.GONE);
                    jobSubCategoryTextView2.setVisibility(View.GONE);
                    subCategorySinner3.setVisibility(View.GONE);
                    jobSubCategoryTextView3.setVisibility(View.GONE);
                    subCategorySinner4.setVisibility(View.GONE);
                    jobSubCategoryTextView4.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_sub_category2:
                if (subCategorySinnerModel2 != null && subCategorySinnerModel2.getHasChild().get(position)) {

                    subCategorySinner3.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView3.setVisibility(View.VISIBLE);
                    nokri_getSubFields(subCategorySinnerModel2.getIds().get(position), "cat3");
                } else {

                    subCategorySinner3.setVisibility(View.GONE);
                    jobSubCategoryTextView3.setVisibility(View.GONE);

                    subCategorySinner4.setVisibility(View.GONE);
                    jobSubCategoryTextView4.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_sub_category3:
                if (subCategorySinnerModel3 != null && subCategorySinnerModel3.getHasChild().get(position)) {

                    subCategorySinner4.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView4.setVisibility(View.VISIBLE);
                    nokri_getSubFields(subCategorySinnerModel3.getIds().get(position), "cat4");
                } else {

                    subCategorySinner4.setVisibility(View.GONE);
                    jobSubCategoryTextView4.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_sub_category4:
                break;
            case R.id.spinner_job_qualificaion:
                break;
            case R.id.spinner_job_type:
                break;
            case R.id.spinner_salary_type:
                break;
            case R.id.spinner_salary_currency:
                break;
            case R.id.spinner_salary_offer:
                break;
            case R.id.spinner_job_experience:
                break;
            case R.id.spinner_job_shift:
                break;
            case R.id.spinner_job_level:
                break;
            case R.id.spinner_job_skills:
                break;
            case R.id.spinner_job_link:
                if (jobExternalLinkSpinnerModel.getIds().get(position).equals("2")) {
                    externalLinkPlaceholder.setVisibility(View.VISIBLE);
                    externalLinkPlaceholder.setText(Nokri_SettingsModel.externalLinkHint);
                    externalLinkEditText.setVisibility(View.VISIBLE);
                    externalLinkEditText.setHint(Nokri_SettingsModel.externalLinkHint);
                } else if (jobExternalLinkSpinnerModel.getIds().get(position).equals("3")) {
                    externalLinkPlaceholder.setVisibility(View.VISIBLE);
                    externalLinkPlaceholder.setText(Nokri_SettingsModel.externalEmailHint);
                    externalLinkEditText.setVisibility(View.VISIBLE);
                    externalLinkEditText.setHint(Nokri_SettingsModel.externalEmailHint);
                } else if (jobExternalLinkSpinnerModel.getIds().get(position).equals("4")) {
                    whatsAppEdiText.setVisibility(View.VISIBLE);
                    whatsAppText.setText(Nokri_SettingsModel.whatsAppHint);
                    whatsAppText.setVisibility(View.VISIBLE);
                    whatsAppEdiText.setHint(Nokri_SettingsModel.whatsAppHint);
                } else {
                    externalLinkEditText.setVisibility(View.GONE);
                    externalLinkPlaceholder.setVisibility(View.GONE);
                }
                break;


        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setOnMyLocationChangeListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Nokri_ToastManager.showLongToast(getContext(), "This Feature Requires Permission");
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                latitudeEditText.setText(LATITUDE + "");
                longitudeEditText.setText(LONGITUDE + "");
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    autoCompleteTextView.setText(address);
                    setMapLocation();

                } catch (IOException e) {
                    Nokri_ToastManager.showLongToast(getContext(), "Something Went Wrong");
                } catch (Exception ex) {
                    Nokri_ToastManager.showLongToast(getContext(), "Something Went Wrong");
                }


                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null)
            map.onResume();
        isPaused = false;
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        if (questions != null && questions.size() != 0) {
            if (questions.size() == 1) {
                questionsCounter.setText("1 question added.");
            } else {
                questionsCounter.setText(questions.size() + " questions added.");
            }
        } else {
            questionsCounter.setText("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null)
            map.onPause();
        isPaused = true;
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


    private Nokri_SpinnerModel nokri_populateExternalLinkForUpdate(Spinner spinner, JSONArray jsonArray) {
        int index = 0;
        Nokri_SpinnerModel model = new Nokri_SpinnerModel();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                model.getHasChild().add(jsonObject.getBoolean("has_child"));

                if (jsonObject.getBoolean("selected")) {
                    nokri_setSpinnerSelection(spinner, i);
                    String value = jsonObject.getString("val");
                    externalLinkEditText.setText(value);
                }
                if (POST_JOB_CALLING_SOURCE.equals("external")) {
                    if (jsonObject.getBoolean("selected")) {
                        index = i;

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (getContext() != null && model != null && spinner != null && model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, model.getNames()));
            if (POST_JOB_CALLING_SOURCE.equals("external"))
                nokri_setSpinnerSelection(spinner, index);
        }
        spinner.setOnItemSelectedListener(this);
        return model;
    }


    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray) {
        int index = 0;
        Nokri_SpinnerModel model = new Nokri_SpinnerModel();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                model.getHasChild().add(jsonObject.getBoolean("has_child"));
                if (POST_JOB_CALLING_SOURCE.equals("external")) {
                    if (jsonObject.getBoolean("selected")) {
                        index = i;

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (getContext() != null && model != null && spinner != null && model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, model.getNames()));
            if (POST_JOB_CALLING_SOURCE.equals("external"))
                nokri_setSpinnerSelection(spinner, index);
        }
        spinner.setOnItemSelectedListener(this);
        return model;
    }


    private void nokri_getPostJob() {
        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getPostJob(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getPostJob(Nokri_RequestHeaderManager.addHeaders());
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
                        JSONObject data = new JSONObject(responseObject.body().string());
                        if (data.getBoolean("success")) {
                            JSONObject response = data.getJSONObject("data");
                            jobId = response.getString("job_id");
                            Log.d("tagggggggg", jobId);
                            expiryLimit = response.getJSONObject("expiry_limit").getInt("value");
                            basicInformationTextView.setText(response.getJSONObject("basic_info").getString("key"));
                            jobTitleTextView.setText(response.getJSONObject("job_title").getString("key"));
                            jobTitleEditText.setHint(response.getJSONObject("job_title").getString("key"));
                            jobCategotyTextView.setText(response.getJSONObject("job_category").getString("key"));
                            jobCategorySpinnerModel = nokri_populateSpinner(jobCategorySpinner, response.getJSONObject("job_category").getJSONArray("value"));
                            jobDescriptionTextView.setText(response.getJSONObject("job_description").getString("key"));
                            jobDetailsEditor.getARE().setHint(response.getJSONObject("job_description").getString("key"));
                            applicationDeadlineTextView.setText(response.getJSONObject("job_deadline").getString("key"));
                            jobQualificaionTextView.setText(response.getJSONObject("job_qualifications").getString("key"));
                            jobQualificationSpinnerModel = nokri_populateSpinner(jobQualificationSpinner, response.getJSONObject("job_qualifications").getJSONArray("value"));
                            jobDetaialTextView.setText(response.getJSONObject("job_desc").getString("key"));
                            jobTypeTextView.setText(response.getJSONObject("job_type").getString("key"));
                            jobTypeSpinnerModel = nokri_populateSpinner(jobTypeSpinner, response.getJSONObject("job_type").getJSONArray("value"));
                            salaryTypeTextView.setText(response.getJSONObject("salary_type").getString("key"));
                            salaryTypeSpinnerModel = nokri_populateSpinner(salaryTypeSpinner, response.getJSONObject("salary_type").getJSONArray("value"));
                            salaryCurrencyTextView.setText(response.getJSONObject("salary_currency").getString("key"));
                            salaryCurrencySpinnerModel = nokri_populateSpinner(salaryCurrencySpinner, response.getJSONObject("salary_currency").getJSONArray("value"));
                            salaryOfferTextView.setText(response.getJSONObject("salary_offer").getString("key"));
                            salaryOfferSpinnerModel = nokri_populateSpinner(salaryOfferSpinner, response.getJSONObject("salary_offer").getJSONArray("value"));
                            jobExerienceTextView.setText(response.getJSONObject("job_experience").getString("key"));
                            jobExperienceSpinnerModel = nokri_populateSpinner(jobExperienceSpinner, response.getJSONObject("job_experience").getJSONArray("value"));
                            jobShiftTextView.setText(response.getJSONObject("job_shift").getString("key"));
                            jobShiftSpinnerModel = nokri_populateSpinner(jobShiftSpinner, response.getJSONObject("job_shift").getJSONArray("value"));
                            jobLevelTextView.setText(response.getJSONObject("job_level").getString("key"));
                            jobLevelSpinnerModel = nokri_populateSpinner(jobLevelSpinner, response.getJSONObject("job_level").getJSONArray("value"));
                            noOfPositionTextView.setText(response.getJSONObject("job_no_pos").getString("key"));
                            noOfPositionEditText.setHint(response.getJSONObject("job_no_pos").getString("key"));
                            jobSkillsTextView.setText(response.getJSONObject("job_skills").getString("key"));
                            jobSkillsSpinnerModel = nokri_populateSpinner(jobSkillsSpinner, response.getJSONObject("job_skills").getJSONArray("value"));

                            jobExternalTextView.setText(response.getJSONObject("job_apply_with").getString("key"));
                            jobExternalLinkSpinnerModel = nokri_populateSpinner(jobExternalSpinner, response.getJSONObject("job_apply_with").getJSONArray("value"));
                            jobExternalSpinner.setTag(response.getJSONObject("job_apply_with").getString("field_type_name"));

                            nokri_setupSkillsSpinner();
                            tagsEditText.setText(response.getJSONObject("job_tags").getString("key"));
                            tagsEditText.setHint(response.getJSONObject("job_tags").getString("key"));

                            locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));

                            tagsTextView.setText(response.getJSONObject("job_tags").getString("key"));


                            selectTextView.setText(response.getJSONObject("job_location").getString("key"));
                            locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
                            setLocationTextView.setText(response.getJSONObject("job_loc").getString("key"));


                            categoryTitle.setText(response.getJSONObject("job_country").getString("key"));
                            categoryValue.setText(response.getJSONObject("job_country").getJSONArray("value").
                                    getJSONObject(0).getString("value"));
                            countryId = response.getJSONObject("job_country").getJSONArray("value").
                                    getJSONObject(0).getString("key");

                            JSONArray countriesList = response.getJSONObject("job_country").getJSONArray("value");
                            countrySpinnerLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (countriesList != null && countriesList.length() != 0) {


                                        makeSpinnerList(countriesList);

                                        String jsonList;
                                        jsonList = new Gson().toJson(categorySpinnerList);

                                        if (jsonList != null) {
                                            getActivity().overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
                                            locationCount = 0;
                                            countryId = "";
                                            cityId = "";
                                            stateId = "";
                                            townId = "";
                                            PostJobStep3.countryId = "";
                                            PostJobStep3.cityId = "";
                                            PostJobStep3.stateId = "";
                                            PostJobStep3.townId = "";
                                            Intent intent = new Intent(getActivity(), PostJobSpinnerListActivity.class);
                                            intent.putExtra("list", jsonList);
                                            intent.putExtra("calledFrom", "country");
                                            intent.putExtra("locationCount", locationCount);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivityForResult(intent, 321);
                                        }
                                    }
                                }
                            });
                            autoCompleteTextView.setHint(response.getJSONObject("job_loc").getString("key"));
                            latitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
                            latitudeEditText.setHint(response.getJSONObject("job_lat").getString("key"));
                            longitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
                            try {
                                LATITUDE = Double.parseDouble(response.getJSONObject("job_lat").getString("key"));

                            } catch (NumberFormatException e) {
                                LATITUDE = 0;
                            }
                            try {
                                LONGITUDE = Double.parseDouble(response.getJSONObject("job_long").getString("key"));
                            } catch (NumberFormatException e) {
                                LONGITUDE = 0;
                            }

                            longitudeEditText.setHint(response.getJSONObject("job_long").getString("key"));
                            publishJobButton.setText(response.getJSONObject("job_post_btn").getString("key"));

                            jobSubCategoryTextView1.setText(response.getJSONObject("job_sub_category").getString("key"));
                            jobSubCategoryTextView2.setText(response.getJSONObject("job_sub_category").getString("key"));
                            jobSubCategoryTextView3.setText(response.getJSONObject("job_sub_category").getString("key"));
                            jobSubCategoryTextView4.setText(response.getJSONObject("job_sub_category").getString("key"));
                            // longitudeEditText.setText(response.getJSONObject("job_long").getString("key"));

                            boostTextView.setText(response.getJSONObject("job_boost").getString("key"));

                            JSONArray preniumJobsArray = response.getJSONArray("premium_jobs");

                            if (preniumJobsArray.length() > 0) {

                                for (int i = 0; i < preniumJobsArray.length(); i++) {
                                    JSONObject preniumJob = preniumJobsArray.getJSONObject(i);
                                    Nokri_PostJobsPackagesModel model = new Nokri_PostJobsPackagesModel();
                                    model.setId(preniumJob.getString("field_type_name"));
                                    model.setQuantity(preniumJob.getString("value"));
                                    model.setTitle(preniumJob.getString("key"));
                                    model.setRemainign(preniumJob.getString("fieldname"));
                                    model.setChecked(preniumJob.getBoolean("is_required"));

                                    modelList.add(model);
                                }
                                nokri_setupRecyclerView();
                            } else {
                                getView().findViewById(R.id.prenium_jobs_container).setVisibility(View.GONE);
                                getView().findViewById(R.id.btn_publish_job2).setVisibility(View.VISIBLE);
                            }
                            setMapLocation();

                            questionnaireModel = new QuestionnaireModel();
                            questionnaireModel.isShowQuestionnaireSection = response.getJSONObject("questionnair_sec").getString("questionnaire_switch");

                            if (questionnaireModel.isShowQuestionnaireSection.equals("1")) {
                                questionsLayout.setVisibility(View.VISIBLE);
                                questionnaireModel.heading = response.getJSONObject("questionnair_sec").getString("sec_heading");
                                questionnaireModel.questionsLabel = response.getJSONObject("questionnair_sec").getString("question_lable");
                                questionnaireModel.questionsPlaceholder = response.getJSONObject("questionnair_sec").getString("question_plc");
                                questionnaireModel.buttonYes = response.getJSONObject("questionnair_sec").getString("btn_yes");
                                questionnaireModel.buttonNo = response.getJSONObject("questionnair_sec").getString("btn_no");
                                questionHeading.setText(questionnaireModel.heading);
                            } else
                                questionsLayout.setVisibility(View.GONE);

                        } else {
                            Nokri_ToastManager.showLongToast(getContext(), data.getString("message"));
                            androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                            androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment pricingTableFragment = new Nokri_PricingTableFragment();

                            fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), pricingTableFragment).commit();

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

    boolean titleEditable = true;

    private void nokri_getUpdatePostJob(String id) {
        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        JsonObject params = new JsonObject();
        params.addProperty("is_update", id);
        params.addProperty("job_id", id);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.editPostJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.editPostJob(params, Nokri_RequestHeaderManager.addHeaders());
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
                        JSONObject data = new JSONObject(responseObject.body().string());
                        JSONObject response = data.getJSONObject("data");
                        titleEditable = response.getBoolean("is_restrict");
                        if (titleEditable){

                            jobTitleEditText.setEnabled(false);
                        }
                        TextView toolbarTitleTextvView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextvView.setText(response.getJSONObject("job_page_title").getString("key"));
                        jobId = response.getString("job_id");
                        basicInformationTextView.setText(response.getJSONObject("basic_info").getString("key"));
                        jobTitleTextView.setText(response.getJSONObject("job_title").getString("key"));
                        jobTitleEditText.setHint(response.getJSONObject("job_title").getString("key"));
                        jobTitleEditText.setText(response.getJSONObject("job_title").getString("value"));

                        jobCategotyTextView.setText(response.getJSONObject("job_category").getString("key"));

                        jobCategorySpinnerModel = nokri_populateSpinner(jobCategorySpinner, response.getJSONObject("job_category").getJSONArray("value"));
                        jobDescriptionTextView.setText(response.getJSONObject("job_description").getString("key"));

                        jobDetailsEditor.getARE().setHint(response.getJSONObject("job_description").getString("key"));
                        jobDetailsEditor.getARE().fromHtml(response.getJSONObject("job_description").getString("value"));


                        applicationDeadlineTextView.setText(response.getJSONObject("job_deadline").getString("key"));
                        applicatiponDeadlineEditText.setText(response.getJSONObject("job_deadline").getString("value"));
                        jobQualificaionTextView.setText(response.getJSONObject("job_qualifications").getString("key"));

                        jobQualificationSpinnerModel = nokri_populateSpinner(jobQualificationSpinner, response.getJSONObject("job_qualifications").getJSONArray("value"));
                        jobDetaialTextView.setText(response.getJSONObject("job_desc").getString("key"));
                        jobDetailsEditor.getARE().fromHtml(response.getJSONObject("job_desc").getString("value"));
                        jobTypeTextView.setText(response.getJSONObject("job_type").getString("key"));
                        jobTypeSpinnerModel = nokri_populateSpinner(jobTypeSpinner, response.getJSONObject("job_type").getJSONArray("value"));
                        salaryTypeTextView.setText(response.getJSONObject("salary_type").getString("key"));
                        salaryTypeSpinnerModel = nokri_populateSpinner(salaryTypeSpinner, response.getJSONObject("salary_type").getJSONArray("value"));
                        salaryCurrencyTextView.setText(response.getJSONObject("salary_currency").getString("key"));
                        salaryCurrencySpinnerModel = nokri_populateSpinner(salaryCurrencySpinner, response.getJSONObject("salary_currency").getJSONArray("value"));
                        salaryOfferTextView.setText(response.getJSONObject("salary_offer").getString("key"));
                        salaryOfferSpinnerModel = nokri_populateSpinner(salaryOfferSpinner, response.getJSONObject("salary_offer").getJSONArray("value"));
                        jobExerienceTextView.setText(response.getJSONObject("job_experience").getString("key"));
                        jobExperienceSpinnerModel = nokri_populateSpinner(jobExperienceSpinner, response.getJSONObject("job_experience").getJSONArray("value"));
                        jobShiftTextView.setText(response.getJSONObject("job_shift").getString("key"));
                        jobShiftSpinnerModel = nokri_populateSpinner(jobShiftSpinner, response.getJSONObject("job_shift").getJSONArray("value"));
                        jobLevelTextView.setText(response.getJSONObject("job_level").getString("key"));
                        jobLevelSpinnerModel = nokri_populateSpinner(jobLevelSpinner, response.getJSONObject("job_level").getJSONArray("value"));
                        noOfPositionTextView.setText(response.getJSONObject("job_no_pos").getString("key"));

                        noOfPositionEditText.setHint(response.getJSONObject("job_no_pos").getString("key"));
                        noOfPositionEditText.setText(response.getJSONObject("job_no_pos").getString("value"));

                        jobSkillsTextView.setText(response.getJSONObject("job_skills").getString("key"));
                        jobSkillsSpinnerModel = nokri_populateSpinner(jobSkillsSpinner, response.getJSONObject("job_skills").getJSONArray("value"));

                        jobExternalTextView.setText(response.getJSONObject("job_apply_with").getString("key"));
                        jobExternalLinkSpinnerModel = nokri_populateSpinner(jobExternalSpinner, response.getJSONObject("job_apply_with").getJSONArray("value"));
                        jobExternalSpinner.setTag(response.getJSONObject("job_apply_with").getString("field_type_name"));


                        jobExternalTextView.setText(response.getJSONObject("job_apply_with").getString("key"));
                        jobExternalLinkSpinnerModel = nokri_populateExternalLinkForUpdate(jobExternalSpinner, response.getJSONObject("job_apply_with").getJSONArray("value"));
                        jobExternalSpinner.setTag(response.getJSONObject("job_apply_with").getString("field_type_name"));


                        nokri_setupSkillsSpinnerForUpdate(response.getJSONObject("job_skills").getJSONArray("value"));

                        tagsEditText.setText(response.getJSONObject("job_tags").getString("key"));
                        tagsEditText.setHint(response.getJSONObject("job_tags").getString("key"));
                        tagsEditText.setTags(response.getJSONObject("job_tags").getString("value").split(","));

                        categoryTitle.setText(response.getJSONObject("job_country").getString("key"));
                        categoryValue.setText(response.getJSONObject("job_country").getJSONArray("value").
                                getJSONObject(0).getString("value"));
                        countryId = response.getJSONObject("job_country").getJSONArray("value").
                                getJSONObject(0).getString("key");

                        JSONArray countriesList = response.getJSONObject("job_country").getJSONArray("value");
                        countrySpinnerLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (countriesList != null && countriesList.length() != 0) {


                                    makeSpinnerList(countriesList);

                                    String jsonList;
                                    jsonList = new Gson().toJson(categorySpinnerList);

                                    if (jsonList != null) {
                                        getActivity().overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
                                        locationCount = 0;
                                        countryId = "";
                                        cityId = "";
                                        stateId = "";
                                        townId = "";
                                        PostJobStep3.countryId = "";
                                        PostJobStep3.cityId = "";
                                        PostJobStep3.stateId = "";
                                        PostJobStep3.townId = "";
                                        Intent intent = new Intent(getActivity(), PostJobSpinnerListActivity.class);
                                        intent.putExtra("list", jsonList);
                                        intent.putExtra("calledFrom", "country");
                                        intent.putExtra("locationCount", locationCount);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivityForResult(intent, 321);
                                    }
                                }
                            }
                        });
                        locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));

                        tagsTextView.setText(response.getJSONObject("job_tags").getString("key"));


                        selectTextView.setText(response.getJSONObject("job_location").getString("key"));
                        locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
                        setLocationTextView.setText(response.getJSONObject("job_loc").getString("key"));

                        autoCompleteTextView.setText(response.getJSONObject("job_loc").getString("value"));
                        autoCompleteTextView.setHint(response.getJSONObject("job_loc").getString("key"));

                        latitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
                        latitudeEditText.setHint(response.getJSONObject("job_lat").getString("key"));
                        latitudeEditText.setText(response.getJSONObject("job_lat").getString("value"));

                        longitudeTextView.setText(response.getJSONObject("job_long").getString("key"));
                        longitudeEditText.setHint(response.getJSONObject("job_long").getString("key"));
                        longitudeEditText.setText(response.getJSONObject("job_long").getString("value"));
                        publishJobButton.setText(response.getJSONObject("job_post_btn").getString("key"));

                        jobSubCategoryTextView1.setText(response.getJSONObject("job_sub_category").getString("key"));
                        jobSubCategoryTextView2.setText(response.getJSONObject("job_sub_category").getString("key"));
                        jobSubCategoryTextView3.setText(response.getJSONObject("job_sub_category").getString("key"));
                        jobSubCategoryTextView4.setText(response.getJSONObject("job_sub_category").getString("key"));
                        // longitudeEditText.setText(response.getJSONObject("job_long").getString("key"));

                        try {
                            LATITUDE = Double.parseDouble(response.getJSONObject("job_lat").getString("value"));

                        } catch (NumberFormatException e) {
                            LATITUDE = 0;
                        }
                        try {
                            LONGITUDE = Double.parseDouble(response.getJSONObject("job_long").getString("value"));
                        } catch (NumberFormatException e) {
                            LONGITUDE = 0;
                        }

                        boostTextView.setText(response.getJSONObject("job_boost").getString("key"));

                        JSONArray preniumJobsArray = response.getJSONArray("premium_jobs");

                        if (preniumJobsArray.length() > 0) {

                            for (int i = 0; i < preniumJobsArray.length(); i++) {
                                JSONObject preniumJob = preniumJobsArray.getJSONObject(i);
                                Nokri_PostJobsPackagesModel model = new Nokri_PostJobsPackagesModel();
                                model.setId(preniumJob.getString("field_type_name"));
                                model.setQuantity(preniumJob.getString("value"));
                                model.setTitle(preniumJob.getString("key"));
                                model.setRemainign(preniumJob.getString("fieldname"));
                                model.setChecked(preniumJob.getBoolean("is_required"));
                                if (preniumJob.getBoolean("is_required"))
                                    model.setEditable(false);

                                modelList.add(model);
                            }
                            nokri_setupRecyclerView();
                        } else {
                            getView().findViewById(R.id.prenium_jobs_container).setVisibility(View.GONE);
                            getView().findViewById(R.id.btn_publish_job2).setVisibility(View.VISIBLE);
                        }
                        setMapLocation();
                        questionnaireModel = new QuestionnaireModel();
                        questionnaireModel.isShowQuestionnaireSection = response.getJSONObject("questionnair_sec").getString("questionnaire_switch");

                        if (questionnaireModel.isShowQuestionnaireSection.equals("1")) {
                            questionsLayout.setVisibility(View.VISIBLE);
                            questionnaireModel.heading = response.getJSONObject("questionnair_sec").getString("sec_heading");
                            questionnaireModel.questionsLabel = response.getJSONObject("questionnair_sec").getString("question_lable");
                            questionnaireModel.questionsPlaceholder = response.getJSONObject("questionnair_sec").getString("question_plc");
                            questionHeading.setText(questionnaireModel.heading);
                            JSONArray questionsArray = response.getJSONArray("job_questions");
                            questions.clear();
                            for (int i = 0; i < questionsArray.length(); i++) {
                                questions.add(questionsArray.getString(i));
                            }
                            if (questions.size() != 0) {
                                if (questions.size() == 1) {
                                    questionsCounter.setText(questions.size() + "Question Added");
                                } else {
                                    questionsCounter.setText(questions.size() + "Questions Added");
                                }
                            } else {
                                questionsCounter.setText("");
                            }


                        } else
                            questionsLayout.setVisibility(View.GONE);


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
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    private void makeSpinnerList(JSONArray jsonArray) {
        int index = 0;
        categorySpinnerList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Nokri_SpinnerModel model = new Nokri_SpinnerModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.setName(jsonObject.getString("value"));
                model.setId(jsonObject.getString("key"));
                model.setHasChild(jsonObject.getBoolean("has_child"));
                if (i == 0) {
                    countryId = model.getId();
                }
                categorySpinnerList.add(model);
                {
                    if (jsonObject.getBoolean("selected")) {
                        index = i;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        applicatiponDeadlineEditText.setText(sdf.format(calendar.getTime()));
    }


    private void nokri_getSubFields(String id, final String tag) {

      /*  dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());*/
        progressBar.setVisibility(View.VISIBLE);
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cat_id", id);

        params.add(jsonObject);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

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
                        Log.v("response", responseObject.message());

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
                            case "cat4":
                                subCategorySinnerModel4 = nokri_populateSpinner(subCategorySinner4, response);
                                break;
                        }

                        progressBar.setVisibility(View.GONE);
                        /* dialogManager.hideAlertDialog();*/


                    } catch (JSONException e) {
                        //   dialogManager.showCustom(e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        //    dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                   /*     dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();*/
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();

                    }
                } else {
                  /*  dialogManager.showCustom(responseObject.code()+"");
                    dialogManager.hideAfterDelay();*/
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              /*  dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();*/
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void nokri_postJob() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject jsonObject = new JsonObject();

        setPreniumJobsId();
        if (preniumJobsId.size() > 0)
            jsonObject.add("class_type_value", preniumJobsId);


        if (POST_JOB_CALLING_SOURCE.equals("external")) {
            jsonObject.addProperty("job_id", POST_JOB_ID);
            jsonObject.addProperty("is_update", POST_JOB_ID);
        } else
            jsonObject.addProperty("is_update", jobId);

        if (jobExternalSpinner.getSelectedItemPosition() != 0 && jobExternalSpinner.getSelectedItemPosition() != 1) {
            if (jobExternalSpinner.getSelectedItemPosition() == 2) {
                if (externalLinkEditText.getText().toString().equals("")
                        || !Patterns.WEB_URL.matcher(externalLinkEditText.getText().toString()).matches()) {
                    externalLinkEditText.setError(Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    externalLinkEditText.requestFocus();
                    dialogManager.hideAlertDialog();
                    return;
                } else {
                    jsonObject.addProperty("external_link", externalLinkEditText.getText().toString());
                }
            } else if (jobExternalSpinner.getSelectedItemPosition() == 4) {
                if (whatsAppEdiText.getText().toString().equals("")) {
                    whatsAppEdiText.setError(Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    whatsAppEdiText.requestFocus();
                    dialogManager.hideAlertDialog();
                    return;
                } else {
                    jsonObject.addProperty("whatsapp", whatsAppEdiText.getText().toString());
                }
            }else {
                if (externalLinkEditText.getText().toString().equals("")
                        || !Patterns.EMAIL_ADDRESS.matcher(externalLinkEditText.getText().toString()).matches()) {
                    externalLinkEditText.setError(Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    externalLinkEditText.requestFocus();
                    dialogManager.hideAlertDialog();
                    return;
                } else {
                    jsonObject.addProperty("external_email", externalLinkEditText.getText().toString());
                }
            }

    }


        jsonObject.add("job_skills", selectedSkillsId);
        jsonObject.addProperty("job_description", jobDetails);
        jsonObject.addProperty("job_title", title);
        jsonObject.addProperty("job_address", location);
        jsonObject.addProperty("job_date", deadline);
        jsonObject.addProperty("job_lat", latitude);
        jsonObject.addProperty("job_long", longitude);
        jsonObject.addProperty("job_posts", positions);
        jsonObject.addProperty("job_cat", jobCategory);

        if (!subCategory1.equals(""))
            jsonObject.addProperty("job_cat_second", subCategory1);
        if (!subCategory2.equals(""))
            jsonObject.addProperty("job_cat_third", subCategory2);
        if (!subCategory3.equals(""))
            jsonObject.addProperty("job_cat_forth", subCategory3);
     /*   if(!subCategory4.equals(""))
        jsonObject.addProperty("",subCategory4);*/
        String[] questionsArray = new String[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            questionsArray[i] = questions.get(i);
        }
        String output = "";

        for (int i = 0; i < questionsArray.length; i++) {
            output += "\"" + questionsArray[i] + "\"";
            if (i != questionsArray.length - 1) {
                output += ", ";
            }
        }

        output = "[" + output + "]";

        Gson userGson = new GsonBuilder().create();
        JSONObject params = new JSONObject();

        try {
            params.put("user", output);
        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < questions.size(); i++) {
            jsonArray.add(questions.get(i));
        }

        jsonObject.add("job_qstns", jsonArray);
        jsonObject.addProperty("job_qualif", jobQualification);
        jsonObject.addProperty("job_type", jobType);
        jsonObject.addProperty("job_salary_type", salaryType);
        jsonObject.addProperty("job_currency", salaryCurrency);
        jsonObject.addProperty("job_salary", salaryOffer);
        jsonObject.addProperty("job_shift", jobShift);
        jsonObject.addProperty("job_experience", jobExperience);
        jsonObject.addProperty("job_level", jobLevel);

        jsonObject.addProperty("job_tags", extractedTag);
        jsonObject.addProperty("job_country", countryId);
        if (!cityId.equals(""))
            jsonObject.addProperty("job_cities", cityId);
        if (!stateId.equals(""))
            jsonObject.addProperty("job_states", stateId);
        if (!townId.equals(""))
            jsonObject.addProperty("job_town", townId);

        Log.v("tagggggggg", jsonObject.toString());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postJob(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postJob(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        dialogManager.hideAlertDialog();

                        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                        Nokri_JobDetailFragment.CALLING_SOURCE = "applied";
                        Bundle bundle = new Bundle();
                        bundle.putString("job_id",jobId);
                        jobDetailFragment.setArguments(bundle);
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).commit();


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
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }

    private boolean nokri_areFieldsEmpty() {

        Nokri_Utils.checkEditTextForError(jobTitleEditText);
        Nokri_Utils.checkEditTextForError(applicatiponDeadlineEditText);
        Nokri_Utils.checkEditTextForError(noOfPositionEditText);
        Nokri_Utils.checkEditTextForError(autoCompleteTextView);
        Nokri_Utils.checkEditTextForError(latitudeEditText);
        Nokri_Utils.checkEditTextForError(longitudeEditText);
        Nokri_Utils.checkEditTextForError(tagsEditText);

        if (jobTitleEditText.getText().toString().trim().isEmpty() || applicatiponDeadlineEditText.getText().toString().trim().isEmpty() || noOfPositionEditText.getText().toString().trim().isEmpty() || autoCompleteTextView.getText().toString().trim().isEmpty() || latitudeEditText.getText().toString().trim().isEmpty() || longitudeEditText.getText().toString().trim().isEmpty() || jobDetailsEditor.getHtml() == null || jobDetailsEditor.getHtml().toString().trim().isEmpty() || tagsEditText.getTags().size() == 0) {

            return true;

        } else {

            return false;
        }
    }

    private void nokri_setValues() {
        tags = tagsEditText.getTags();
        extractedTag = "";
        for (int i = 0; i < tags.size(); i++) {

            extractedTag += tags.get(i) + " ";

        }


        jobDetails = jobDetailsEditor.getHtml().toString();
        title = jobTitleEditText.getText().toString();
        location = autoCompleteTextView.getText().toString();
        deadline = applicatiponDeadlineEditText.getText().toString();
        latitude = latitudeEditText.getText().toString();
        longitude = longitudeEditText.getText().toString();
        positions = noOfPositionEditText.getText().toString();
        if (jobCategorySpinner.getAdapter() != null) {
            if (jobCategorySpinnerModel.getIds() != null && jobCategorySpinnerModel.getIds().size() > 0)
                jobCategory = jobCategorySpinnerModel.getIds().get(jobCategorySpinner.getSelectedItemPosition());
        }


        if (subCategorySinner1.getAdapter() != null && subCategorySinner1.getVisibility() == View.VISIBLE) {
            if (subCategorySinner1Model1.getIds() != null && subCategorySinner1Model1.getIds().size() > 0)
                subCategory1 = subCategorySinner1Model1.getIds().get(subCategorySinner1.getSelectedItemPosition());
        } else
            subCategory1 = "";


        if (subCategorySinner2.getAdapter() != null && subCategorySinner2.getVisibility() == View.VISIBLE) {
            if (subCategorySinnerModel2.getIds() != null && subCategorySinnerModel2.getIds().size() > 0)
                subCategory2 = subCategorySinnerModel2.getIds().get(subCategorySinner2.getSelectedItemPosition());
        } else
            subCategory2 = "";


        if (subCategorySinner3.getAdapter() != null && subCategorySinner3.getVisibility() == View.VISIBLE) {
            if (subCategorySinnerModel3.getIds() != null && subCategorySinnerModel3.getIds().size() > 0)
                subCategory3 = subCategorySinnerModel3.getIds().get(subCategorySinner3.getSelectedItemPosition());
        } else
            subCategory3 = "";


        if (subCategorySinner4.getAdapter() != null && subCategorySinner4.getVisibility() == View.VISIBLE) {
            if (subCategorySinnerModel4.getIds() != null && subCategorySinnerModel4.getIds().size() > 0)
                subCategory4 = subCategorySinnerModel4.getIds().get(subCategorySinner4.getSelectedItemPosition());
        } else
            subCategory4 = "";


        if (jobQualificationSpinner.getAdapter() != null) {
            if (jobQualificationSpinnerModel.getIds() != null && jobQualificationSpinnerModel.getIds().size() > 0)
                jobQualification = jobQualificationSpinnerModel.getIds().get(jobQualificationSpinner.getSelectedItemPosition());
        }
        if (jobTypeSpinner.getAdapter() != null) {
            if (jobTypeSpinnerModel.getIds() != null && jobTypeSpinnerModel.getIds().size() > 0)
                jobType = jobTypeSpinnerModel.getIds().get(jobTypeSpinner.getSelectedItemPosition());
        }


        if (salaryTypeSpinner.getAdapter() != null) {
            if (salaryTypeSpinnerModel.getIds() != null && salaryTypeSpinnerModel.getIds().size() > 0)
                salaryType = salaryTypeSpinnerModel.getIds().get(salaryTypeSpinner.getSelectedItemPosition());
        }

        if (salaryCurrencySpinner.getAdapter() != null) {
            if (salaryCurrencySpinnerModel.getIds() != null && salaryCurrencySpinnerModel.getIds().size() > 0)
                salaryCurrency = salaryCurrencySpinnerModel.getIds().get(salaryCurrencySpinner.getSelectedItemPosition());
        }

        if (salaryOfferSpinner.getAdapter() != null) {
            if (salaryOfferSpinnerModel.getIds() != null && salaryOfferSpinnerModel.getIds().size() > 0)
                salaryOffer = salaryOfferSpinnerModel.getIds().get(salaryOfferSpinner.getSelectedItemPosition());
        }

        if (jobExperienceSpinner.getAdapter() != null) {
            if (jobExperienceSpinnerModel.getIds() != null && jobExperienceSpinnerModel.getIds().size() > 0)
                jobExperience = jobExperienceSpinnerModel.getIds().get(jobExperienceSpinner.getSelectedItemPosition());
        }

        if (jobShiftSpinner.getAdapter() != null) {
            if (jobShiftSpinnerModel.getIds() != null && jobShiftSpinnerModel.getIds().size() > 0)
                jobShift = jobShiftSpinnerModel.getIds().get(jobShiftSpinner.getSelectedItemPosition());
        }

        if (jobLevelSpinner.getAdapter() != null) {
            if (jobLevelSpinnerModel.getIds() != null && jobLevelSpinnerModel.getIds().size() > 0)
                jobLevel = jobLevelSpinnerModel.getIds().get(jobLevelSpinner.getSelectedItemPosition());
        }

        if (jobSkillsSpinner.getAdapter() != null) {
            if (jobSkillsSpinnerModel.getIds() != null && jobSkillsSpinnerModel.getIds().size() > 0)
                jobSkills = jobSkillsSpinnerModel.getIds().get(jobSkillsSpinner.getSelectedItemPosition());
        }


    }


    private void nokri_setSpinnerSelection(Spinner spinner, int index) {


        spinner.setSelection(index);
        Log.d("itemzzz", "called" + index + " " + spinner.getAdapter().getItem(index).toString());


    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            DatePickerDialog dialog = new DatePickerDialog(getActivity());
            dialog.setOnDateSetListener(this);
            dialog.onDateChanged(dialog.getDatePicker(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
//            dialog.getDatePicker().setMaxDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expiryLimit));
            dialog.show();
        }
//            new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

        return false;
    }

    @Override
    public void onMyLocationChange(Location location) {

        if (location != null) {
            LATITUDE = location.getLatitude();
            LONGITUDE = location.getLongitude();

        }

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
            latitudeEditText.setText(place.getLatLng().latitude + "");
            longitudeEditText.setText(place.getLatLng().longitude + "");
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
            autoCompleteTextView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                Nokri_SpinnerModel model = (Nokri_SpinnerModel) data.getSerializableExtra("some_key");
                if (data.getStringExtra("country_id") != null)
                    countryId = data.getStringExtra("country_id");
                if (data.getStringExtra("state_id") != null)
                    stateId = data.getStringExtra("state_id");
                if (data.getStringExtra("city_id") != null)
                    cityId = data.getStringExtra("city_id");
                if (data.getStringExtra("town_id") != null)
                    townId = data.getStringExtra("town_id");
                categoryValue.setText(model.getName());
            }
        }
    }

}
