package com.scriptsbundle.nokri.guest.search.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.custom.MaterialProgressBar;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobSpinnerListActivity;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobStep3;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.search.models.Nokri_CandidateSearchModel;
import com.scriptsbundle.nokri.guest.search.models.Nokri_ShowFilteredCandidatesFragment;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_CandidateSearchFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private TextView countryTextView, cityTextView, stateTextView, townTextView;

    private String country = "", state = "", city = "", town = "";
    private String type = "", experience = "", level = "", skills = "", qualification = "", gender = "", salaryRange = "", salaryType
            ,salaryCurrency = "", location = "";
    private Nokri_SpinnerModel countrySpinnerModel, stateSpinnerModel, citySpinnerModel, townSpinnerModel;
    private TextView toolbarTitleTextView;
    private Spinner typeSpinner, experienceSpinner, levelSpinner, skillsSpinner, qualificationSpinner,genderSpinner,salaryRangeSpinner
            ,salaryTypeSpinner,salaryCurrencySpinner;

    private TextView[] textViews = new TextView[12];
    private Spinner[] spinners = new Spinner[12];
    private Nokri_SpinnerModel[] spinnerModels = new Nokri_SpinnerModel[12];

    private MaterialProgressBar progressBar;
    private Nokri_DialogManager dialogManager;
    private CardView stateContainer, cityContainer, townContainer;
    private TextView typeTextView, experienceTextView, levelTextView, skillsTextView
      , qualificationTextView,genderTextView,salaryRangeTextView
            ,salaryTypeTextView,salaryCurrencyTextView;
    private TextView searchByTitleTextView, footerTextView;
    private EditText searchEditText, edittext_headline;
    private String[] values = new String[4];
    private ImageButton searchImageButton;
    private LinearLayout searchNow;
    private String spinnerTitleText;

    public static int locationCount = 0;
    public static String countryId = "";
    public static String cityId = "";
    public static String stateId = "";
    public static String townId = "";
    LinearLayout countrySpinnerLayout;
    TextView categoryTitle, categoryValue;
    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();
    Nokri_CandidateDashboardModel candidateDashboardModel;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    public Nokri_CandidateSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_candidate_search, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setupFonts();
        nokri_getCandidateSeachFilters();
    }

    private void nokri_initialize() {

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        candidateDashboardModel = Nokri_SharedPrefManager.getCandidateSettings(getActivity());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        toolbarTitleTextView.setText(candidateDashboardModel.getCandidateSearch());

        countryTextView = getView().findViewById(R.id.txt_country);
        cityTextView = getView().findViewById(R.id.txt_city);
        stateTextView = getView().findViewById(R.id.txt_state);
        townTextView = getView().findViewById(R.id.txt_town);
        edittext_headline = getView().findViewById(R.id.edittext_headline);
        countrySpinnerLayout = getView().findViewById(R.id.country_spinner_layout);
        categoryTitle = getView().findViewById(R.id.category_title);
        categoryValue = getView().findViewById(R.id.category_value);

        typeTextView = getView().findViewById(R.id.txt_type);
        experienceTextView = getView().findViewById(R.id.txt_experience);
        levelTextView = getView().findViewById(R.id.txt_level);
        skillsTextView = getView().findViewById(R.id.txt_skills);
        qualificationTextView = getView().findViewById(R.id.txt_qualification);
        genderTextView = getView().findViewById(R.id.txt_gender);
        salaryRangeTextView = getView().findViewById(R.id.txt_range);
        salaryTypeTextView = getView().findViewById(R.id.txt_salary_type);
        salaryCurrencyTextView = getView().findViewById(R.id.txt_currency);

        searchByTitleTextView = getView().findViewById(R.id.txt_search_by_title);
        footerTextView = getView().findViewById(R.id.footer_text);

        progressBar = getView().findViewById(R.id.progress);

        typeSpinner = getView().findViewById(R.id.spinner_type);
        experienceSpinner = getView().findViewById(R.id.spinner_experience);
        levelSpinner = getView().findViewById(R.id.spinner_level);
        skillsSpinner = getView().findViewById(R.id.spinner_skills);
        qualificationSpinner = getView().findViewById(R.id.spinner_qualification);
        genderSpinner = getView().findViewById(R.id.spinner_gender);
        salaryRangeSpinner = getView().findViewById(R.id.spinner_range);
        salaryTypeSpinner = getView().findViewById(R.id.spinner_salary_type);
        salaryCurrencySpinner = getView().findViewById(R.id.spinner_currency);

        toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);


        textViews[0] = typeTextView;
        textViews[1] = experienceTextView;
        textViews[2] = levelTextView;
        textViews[3] = skillsTextView;
        searchEditText = getView().findViewById(R.id.edittxt_search);


        stateContainer = getView().findViewById(R.id.state_container);
        cityContainer = getView().findViewById(R.id.city_container);
        townContainer = getView().findViewById(R.id.town_container);


        searchImageButton = getView().findViewById(R.id.img_btn_search);
        searchImageButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        searchNow = getView().findViewById(R.id.search_now);
        searchNow.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        searchImageButton.setOnClickListener(this);

        searchNow.setOnClickListener(this);


    }

    private void nokri_setupFonts() {

        Nokri_FontManager fontManager = new Nokri_FontManager();

        fontManager.nokri_setOpenSenseFontTextView(typeTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(experienceTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(levelTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(skillsTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(searchByTitleTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(footerTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(categoryValue, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(categoryTitle, getActivity().getAssets());
    }

    private void nokri_getCandidateSeachFilters() {
        dialogManager = new Nokri_DialogManager();

        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateSearchFilters(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCandidateSearchFilters(Nokri_RequestHeaderManager.addHeaders());
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
                        JSONArray extras = response.getJSONArray("extra");

                        for (int i = 0;i<extras.length();i++){
                            JSONObject extraObject = extras.getJSONObject(i);
                            if (extraObject.getString("field_type_name").equals("cand_search_now")) {
                                footerTextView.setText(extraObject.getString("key"));
                            }
                        }

                        JSONArray searchFields = response.getJSONObject("data").getJSONArray("search_fields");
                        spinnerTitleText = searchFields.getJSONObject(3).getString("is_show");
                        for (int i = 0; i < searchFields.length(); i++) {

                            JSONObject filterObject = searchFields.getJSONObject(i);

                            if (i == 1 || i == 0) {
                                if (filterObject.getString("field_type_name").equals("cand_title")) {
                                    String hint = filterObject.getString("value");
                                    searchEditText.setHint(hint);
                                } else if (filterObject.getString("field_type_name").equals("cand_head")) {
                                    String hint = filterObject.getString("key");
                                    edittext_headline.setHint(hint);
                                }
                            } else {
                                if (filterObject.getString("field_type_name").equals("cand_qualification")) {
                                    spinnerModels [i] = nokri_populateSpinner(qualificationSpinner, filterObject.getJSONArray("value"));
                                    qualificationSpinner.setTag("cand_qualification");
                                    qualificationTextView.setText(filterObject.getString("key"));
                                    spinners[i] = qualificationSpinner;
                                } else if (filterObject.getString("field_type_name").equals("cand_experience")) {

                                    spinnerModels [i] = nokri_populateSpinner(experienceSpinner, filterObject.getJSONArray("value"));
                                    experienceSpinner.setTag("cand_experience");
                                    experienceTextView.setText(filterObject.getString("key"));
                                    spinners[i] = experienceSpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_level")) {
                                    spinnerModels [i] = nokri_populateSpinner(levelSpinner, filterObject.getJSONArray("value"));
                                    levelSpinner.setTag("cand_level");
                                    levelTextView.setText(filterObject.getString("key"));
                                    spinners[i] = levelSpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_skills")) {
                                    spinnerModels [i] = nokri_populateSpinner(skillsSpinner, filterObject.getJSONArray("value"));
                                    skillsSpinner.setTag("cand_skills");
                                    skillsTextView.setText(filterObject.getString("key"));
                                    spinners[i] = skillsSpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_salary_range")) {
                                    spinnerModels [i] = nokri_populateSpinner(salaryRangeSpinner, filterObject.getJSONArray("value"));
                                    salaryRangeSpinner.setTag("cand_salary_range");
                                    salaryRangeTextView.setText(filterObject.getString("key"));
                                    spinners[i] = salaryRangeSpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_salary_type")) {
                                    spinnerModels [i] = nokri_populateSpinner(salaryTypeSpinner, filterObject.getJSONArray("value"));
                                    salaryTypeSpinner.setTag("cand_salary_type");
                                    salaryTypeTextView.setText(filterObject.getString("key"));
                                    spinners[i] = salaryTypeSpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_salary_curr")) {
                                    spinnerModels [i] = nokri_populateSpinner(salaryCurrencySpinner, filterObject.getJSONArray("value"));
                                    salaryCurrencySpinner.setTag("cand_salary_curr");
                                    salaryCurrencyTextView.setText(filterObject.getString("key"));
                                    spinners[i] = salaryCurrencySpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_type")) {
                                    spinnerModels [i] = nokri_populateSpinner(typeSpinner, filterObject.getJSONArray("value"));
                                    typeSpinner.setTag("cand_type");
                                    typeTextView.setText(filterObject.getString("key"));
                                    spinners[i] = typeSpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_gender")) {
                                    spinnerModels [i] = nokri_populateSpinner(genderSpinner, filterObject.getJSONArray("value"));
                                    genderSpinner.setTag("cand_gender");
                                    genderTextView.setText(filterObject.getString("key"));
                                    spinners[i] = genderSpinner;
                                }else if (filterObject.getString("field_type_name").equals("cand_location")) {
                                    categoryTitle.setText(filterObject.getString("key"));
                                    categoryValue.setText(filterObject.getJSONArray("value").
                                            getJSONObject(0).getString("value"));
                                    JSONArray countriesList = filterObject.getJSONArray("value");


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
                                }
                            }

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

    private void makeSpinnerList(JSONArray jsonArray) {
        int index = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Nokri_SpinnerModel model = new Nokri_SpinnerModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.setName(jsonObject.getString("value"));
                model.setId(jsonObject.getString("key"));
                model.setHasChild(jsonObject.getBoolean("has_child"));
                if (i == 0) {
                    Toast.makeText(getActivity(), model.getName(), Toast.LENGTH_SHORT).show();
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


    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray) {
        int index = 0;
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
        if (getContext() != null && model != null && spinner != null && model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, model.getNames(), true));
            nokri_setSpinnerSelection(spinner, index);
            spinner.setOnItemSelectedListener(this);
        }
        return model;
    }

    private void nokri_setSpinnerSelection(Spinner spinner, int index) {


        spinner.setSelection(index);
        Log.d("itemzzz", "called" + index + " " + spinner.getAdapter().getItem(index).toString());


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void nokri_PostSearchCandidate(boolean searchOnlyName) {

        nokri_setValues();
        JsonObject params = new JsonObject();
        Nokri_CandidateSearchModel candidateSearchModel = new Nokri_CandidateSearchModel();
        if (searchOnlyName)
            candidateSearchModel.setSearchOnly(true);
        else
            candidateSearchModel.setSearchOnly(false);

        candidateSearchModel.setTitle(searchEditText.getText().toString());
        params.addProperty("cand_location", country);
        String location;
        location = country;
        if (!state.isEmpty())
            location = state;

        if (!city.isEmpty())
            location = city;

        if (!town.isEmpty())
            location = town;

        candidateSearchModel.setLocation(location);
        candidateSearchModel.setType(type);
        candidateSearchModel.setExperience(experience);
        candidateSearchModel.setLevel(level);
        candidateSearchModel.setSkill(skills);
        candidateSearchModel.setGender(gender);
        candidateSearchModel.setSalaryCurrency(salaryCurrency);
        candidateSearchModel.setSalaryRange(salaryRange);
        candidateSearchModel.setSalaryType(salaryType);
        candidateSearchModel.setQualification(qualification);
        if (!edittext_headline.getText().toString().equals(""))
            candidateSearchModel.setHeadline(edittext_headline.getText().toString());

        Nokri_SharedPrefManager.saveCandidateSearchModel(candidateSearchModel, getContext());
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment showFilteredCandidatesFragment = new Nokri_ShowFilteredCandidatesFragment();
        fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), showFilteredCandidatesFragment).addToBackStack(null).commit();


    }

    private void nokri_setValues() {

        for (int i = 0; i < spinners.length; i++) {
            if (spinners[i]!=null){
                if (spinners[i].getTag().equals("cand_gender")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        gender = "";
                    }else{
                        gender = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_experience")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        experience = "";
                    }else{
                        experience = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_qualification")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        qualification = "";
                    }else{
                        qualification = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_level")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        level = "";
                    }else{
                        level = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_salary_range")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        salaryRange = "";
                    }else{
                        salaryRange = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_skills")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        skills = "";
                    }else{
                        skills = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_salary_type")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        salaryType = "";
                    }else{
                        salaryType = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_location")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        location = "";
                    }else{
                        location = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_salary_curr")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        salaryCurrency = "";
                    }else{
                        salaryCurrency = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }else if (spinners[i].getTag().equals("cand_type")){
                    if (spinners[i].getSelectedItemPosition()==0){
                        type = "";
                    }else{
                        type = spinnerModels[i].getIds().get(spinners[i].getSelectedItemPosition());

                    }
                }
            }
        }



        if (!countryId.equals("")){
            country = countryId;
        }if (!stateId.equals("")){
            state = stateId;
        }if (!cityId.equals("")){
            city = cityId;
        }if (!townId.equals("")){
            town = townId;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_search:
                nokri_PostSearchCandidate(true);
                break;
            case R.id.search_now:
                nokri_PostSearchCandidate(false);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                Nokri_SpinnerModel model = (Nokri_SpinnerModel) data.getSerializableExtra("some_key");
                if (data.getStringExtra("country_id")!=null)
                    countryId = data.getStringExtra("country_id");
                if (data.getStringExtra("state_id")!=null)
                    stateId = data.getStringExtra("state_id");
                if (data.getStringExtra("city_id")!=null)
                    cityId = data.getStringExtra("city_id");
                if (data.getStringExtra("town_id")!=null)
                    townId = data.getStringExtra("town_id");
                categoryValue.setText(model.getName());
            }
        }
    }

}
