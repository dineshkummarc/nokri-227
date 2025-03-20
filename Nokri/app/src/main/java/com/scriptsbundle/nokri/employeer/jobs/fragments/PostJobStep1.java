package com.scriptsbundle.nokri.employeer.jobs.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostJobStep1 extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener, View.OnFocusChangeListener {
    ProgressBar progressBar;
    LinearLayout buttonNext;
    private String jobId;
    private TextView applicationDeadlineTextView,basicInformationTextView,jobTitleTextView,jobCategotyTextView,jobSubCategoryTextView1,jobSubCategoryTextView2,jobSubCategoryTextView3,jobSubCategoryTextView4,jobDescriptionTextView;
    private EditText jobTitleEditText,applicationDeadlineEditText,latitudeEditText,longitudeEditText,noOfPositionEditText;
    private Nokri_SpinnerModel jobCategorySpinnerModel,subCategorySinner1Model1,subCategorySinnerModel2,subCategorySinnerModel3,subCategorySinnerModel4,jobQualificationSpinnerModel,jobTypeSpinnerModel;
    private RichEditor jobDetailsEditor;
    private Spinner jobCategorySpinner,subCategorySinner1,subCategorySinner2,subCategorySinner3,subCategorySinner4,jobQualificationSpinner,jobTypeSpinner;
    private Calendar calendar;
    RestService restService;
    String categoryResponseString;
    public PostJobStep1() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_post_job_step1, container, false);
        calendar = Calendar.getInstance();
        restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getActivity()), Nokri_SharedPrefManager.getPassword(getActivity()),getActivity());
        initializeView(view);
        getPostJob();
        Toast.makeText(getActivity(), "On Create View", Toast.LENGTH_SHORT).show();

        return view;
    }

    public void initializeView(View view){
        progressBar = view.findViewById(R.id.progressBar);
        buttonNext = view.findViewById(R.id.buttonNext);
        basicInformationTextView       = view.findViewById(R.id.txt_basic_info);
        jobTitleTextView               = view.findViewById(R.id.txt_job_title);
        jobCategotyTextView            = view.findViewById(R.id.txt_category);
        jobSubCategoryTextView1        = view.findViewById(R.id.txt_sub_category1);
        jobSubCategoryTextView2        = view.findViewById(R.id.txt_sub_category2);
        jobSubCategoryTextView3        = view.findViewById(R.id.txt_sub_category3);
        jobSubCategoryTextView4        = view.findViewById(R.id.txt_sub_category4);
        jobDescriptionTextView         = view.findViewById(R.id.txt_job_description);

        jobCategorySpinner      = view.findViewById(R.id.spinner_category);
        subCategorySinner1      = view.findViewById(R.id.spinner_sub_category1);
        subCategorySinner2      = view.findViewById(R.id.spinner_sub_category2);
        subCategorySinner3      = view.findViewById(R.id.spinner_sub_category3);
        subCategorySinner4      = view.findViewById(R.id.spinner_sub_category4);
        applicationDeadlineTextView    = view.findViewById(R.id.txt_application_deadline);
        applicationDeadlineEditText    = view.findViewById(R.id.editText_application_deadline);

        jobDetailsEditor     = view. findViewById(R.id.edittxt_descripton);
        applicationDeadlineEditText.setOnTouchListener(this);



        applicationDeadlineEditText.setOnFocusChangeListener(this);

        jobDetailsEditor.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        jobDetailsEditor.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));
        buttonNext.setOnClickListener(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toast.makeText(getActivity(), "On Activity Created", Toast.LENGTH_SHORT).show();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });

    }



    private void getPostJob(){
        progressBar.setVisibility(View.VISIBLE);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getPostJob(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getPostJob( Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject data = new JSONObject(responseObject.body().string());
                        if(data.getBoolean("success")) {
                            JSONObject response  = data.getJSONObject("data");
                            basicInformationTextView.setText(response.getJSONObject("basic_info").getString("key"));
                            jobId = response.getString("job_id");
                            Log.d("tagggggggg", jobId);
                            jobTitleTextView.setText(response.getJSONObject("job_title").getString("key"));
//                            jobTitleEditText.setHint(response.getJSONObject("job_title").getString("key"));
                            jobCategotyTextView.setText(response.getJSONObject("job_category").getString("key"));
                            jobCategorySpinnerModel = nokri_populateSpinner(jobCategorySpinner, response.getJSONObject("job_category").getJSONArray("value"));
                            jobDescriptionTextView.setText(response.getJSONObject("job_description").getString("key"));
                            jobDetailsEditor.setPlaceholder(response.getJSONObject("job_description").getString("key"));
                            applicationDeadlineTextView.setText(response.getJSONObject("job_deadline").getString("key"));
                            applicationDeadlineEditText.setHint(response.getJSONObject("job_deadline").getString("key"));
//                            jobQualificaionTextView.setText(response.getJSONObject("job_qualifications").getString("key"));
//                            jobQualificationSpinnerModel = nokri_populateSpinner(jobQualificationSpinner, response.getJSONObject("job_qualifications").getJSONArray("value"));
//                            jobDetaialTextView.setText(response.getJSONObject("job_desc").getString("key"));
//                            jobTypeTextView.setText(response.getJSONObject("job_type").getString("key"));
//                            jobTypeSpinnerModel = nokri_populateSpinner(jobTypeSpinner, response.getJSONObject("job_type").getJSONArray("value"));
//                            salaryTypeTextView.setText(response.getJSONObject("salary_type").getString("key"));
//                            salaryTypeSpinnerModel = nokri_populateSpinner(salaryTypeSpinner, response.getJSONObject("salary_type").getJSONArray("value"));
//                            salaryCurrencyTextView.setText(response.getJSONObject("salary_currency").getString("key"));
//                            salaryCurrencySpinnerModel = nokri_populateSpinner(salaryCurrencySpinner, response.getJSONObject("salary_currency").getJSONArray("value"));
//                            salaryOfferTextView.setText(response.getJSONObject("salary_offer").getString("key"));
//                            salaryOfferSpinnerModel = nokri_populateSpinner(salaryOfferSpinner, response.getJSONObject("salary_offer").getJSONArray("value"));
//                            jobExerienceTextView.setText(response.getJSONObject("job_experience").getString("key"));
//                            jobExperienceSpinnerModel = nokri_populateSpinner(jobExperienceSpinner, response.getJSONObject("job_experience").getJSONArray("value"));
//                            jobShiftTextView.setText(response.getJSONObject("job_shift").getString("key"));
//                            jobShiftSpinnerModel = nokri_populateSpinner(jobShiftSpinner, response.getJSONObject("job_shift").getJSONArray("value"));
//                            jobLevelTextView.setText(response.getJSONObject("job_level").getString("key"));
//                            jobLevelSpinnerModel = nokri_populateSpinner(jobLevelSpinner, response.getJSONObject("job_level").getJSONArray("value"));
//                            noOfPositionTextView.setText(response.getJSONObject("job_no_pos").getString("key"));
//                            noOfPositionEditText.setHint(response.getJSONObject("job_no_pos").getString("key"));
//                            jobSkillsTextView.setText(response.getJSONObject("job_skills").getString("key"));
//                            jobSkillsSpinnerModel = nokri_populateSpinner(jobSkillsSpinner, response.getJSONObject("job_skills").getJSONArray("value"));
//                            nokri_setupSkillsSpinner();
//                            tagsEditText.setText(response.getJSONObject("job_tags").getString("key"));
//                            tagsEditText.setHint(response.getJSONObject("job_tags").getString("key"));
//                            countryTextView.setText(response.getJSONObject("job_country").getString("key"));
//                            countrySpinnerModel = nokri_populateSpinner(countrySpinner, response.getJSONObject("job_country").getJSONArray("value"));
//
//                            stateTextView.setText(response.getJSONObject("job_state").getString("key"));
//                            cityTextView.setText(response.getJSONObject("job_city").getString("key"));
//                            townTextView.setText(response.getJSONObject("job_town").getString("key"));
//                            locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
//
//                            tagsTextView.setText(response.getJSONObject("job_tags").getString("key"));
//
//
//                            selectTextView.setText(response.getJSONObject("job_location").getString("key"));
//                            locationOnMapTextView.setText(response.getJSONObject("job_location_head").getString("key"));
//                            setLocationTextView.setText(response.getJSONObject("job_loc").getString("key"));
//
//
//                            autoCompleteTextView.setHint(response.getJSONObject("job_loc").getString("key"));
//                            latitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
//                            latitudeEditText.setHint(response.getJSONObject("job_lat").getString("key"));
//                            longitudeTextView.setText(response.getJSONObject("job_lat").getString("key"));
//                            try {
//                                LATITUDE = Double.parseDouble(response.getJSONObject("job_lat").getString("key"));
//
//                            } catch (NumberFormatException e) {
//                                LATITUDE = 0;
//                            }
//                            try {
//                                LONGITUDE = Double.parseDouble(response.getJSONObject("job_long").getString("key"));
//                            } catch (NumberFormatException e) {
//                                LONGITUDE = 0;
//                            }
//
//                            longitudeEditText.setHint(response.getJSONObject("job_long").getString("key"));
//                            publishJobButton.setText(response.getJSONObject("job_post_btn").getString("key"));

                            jobSubCategoryTextView1.setText(response.getJSONObject("job_sub_category").getString("key"));
                            jobSubCategoryTextView2.setText(response.getJSONObject("job_sub_category").getString("key"));
                            jobSubCategoryTextView3.setText(response.getJSONObject("job_sub_category").getString("key"));
                            jobSubCategoryTextView4.setText(response.getJSONObject("job_sub_category").getString("key"));
                            // longitudeEditText.setText(response.getJSONObject("job_long").getString("key"));

                       }
                        else {
//                            Nokri_ToastManager.showLongToast(getActivity(), data.getString("message"));
//                            android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
//                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            Fragment pricingTableFragment = new Nokri_PricingTableFragment();
//                            fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), pricingTableFragment).commit();
                            Toast.makeText(getActivity(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(getActivity(),e.getLocalizedMessage());
                        progressBar.setVisibility(View.GONE);
                        getActivity().finish();
                        e.printStackTrace();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.GONE);
                        getActivity().finish();
                        e.printStackTrace();
                    }

                }
                else {
                    progressBar.setVisibility(View.GONE);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==buttonNext.getId()){

        }
    }
    private void nokri_setSpinnerSelection(Spinner spinner, int index){



        spinner.setSelection(index);
        Log.d("itemzzz","called"+ index + " "+spinner.getAdapter().getItem(index).toString());


    }


    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray){
        int index = 0;
        Nokri_SpinnerModel model = new Nokri_SpinnerModel();

        for(int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.getNames().add(jsonObject.getString("value"));
                model.getIds().add(jsonObject.getString("key"));
                model.getHasChild().add(jsonObject.getBoolean("has_child"));
                {
                    if(jsonObject.getBoolean("selected")) {
                        index = i;

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(getContext()!=null && model!=null && spinner!=null && model.getNames()!=null){

            spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,model.getNames()));

        }
        spinner.setOnItemSelectedListener(this);
        return model;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId())
        {
            case R.id.spinner_category :

                if(jobCategorySpinnerModel!= null && jobCategorySpinnerModel.getHasChild().get(position)) {
                    subCategorySinner1.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView1.setVisibility(View.VISIBLE);
                    nokri_getSubFields(jobCategorySpinnerModel.getIds().get(position),"cat1");
                }
                else {
                    getCategoryData(jobCategorySpinnerModel.getIds().get(position));
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
            case R.id.spinner_sub_category1 :
                if(subCategorySinner1Model1!= null && subCategorySinner1Model1.getHasChild().get(position))
                {

                    subCategorySinner2.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView2.setVisibility(View.VISIBLE);
                    nokri_getSubFields(subCategorySinner1Model1.getIds().get(position),"cat2");
                }
                else {

                    getCategoryData(jobCategorySpinnerModel.getIds().get(position));
                    subCategorySinner2.setVisibility(View.GONE);
                    jobSubCategoryTextView2.setVisibility(View.GONE);
                    subCategorySinner3.setVisibility(View.GONE);
                    jobSubCategoryTextView3.setVisibility(View.GONE);
                    subCategorySinner4.setVisibility(View.GONE);
                    jobSubCategoryTextView4.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_sub_category2 :
                if(subCategorySinnerModel2!= null && subCategorySinnerModel2.getHasChild().get(position))
                {

                    subCategorySinner3.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView3.setVisibility(View.VISIBLE);
                    nokri_getSubFields(subCategorySinnerModel2.getIds().get(position),"cat3");
                }
                else {

                    getCategoryData(jobCategorySpinnerModel.getIds().get(position));
                    subCategorySinner3.setVisibility(View.GONE);
                    jobSubCategoryTextView3.setVisibility(View.GONE);

                    subCategorySinner4.setVisibility(View.GONE);
                    jobSubCategoryTextView4.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_sub_category3 :
                if(subCategorySinnerModel3!= null && subCategorySinnerModel3.getHasChild().get(position))
                {

                    subCategorySinner4.setVisibility(View.VISIBLE);
                    jobSubCategoryTextView4.setVisibility(View.VISIBLE);
                    nokri_getSubFields(subCategorySinnerModel3.getIds().get(position),"cat4");
                }
                else {

                    getCategoryData(jobCategorySpinnerModel.getIds().get(position));
                    subCategorySinner4.setVisibility(View.GONE);
                    jobSubCategoryTextView4.setVisibility(View.GONE);
                }
                break;
            case R.id.spinner_sub_category4:
                break;

        }}



    public void getCategoryData(String catId){
        Call<ResponseBody> myCall;
        JsonObject params = new JsonObject();
        params.addProperty("cat_id",catId);
        myCall = restService.postGetDynamicFields(params,Nokri_RequestHeaderManager.addHeaders());


        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        progressBar.setVisibility(View.GONE);
                        categoryResponseString = response.body().string();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void nokri_getSubFields(String id, final String tag){

      /*  dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());*/
        progressBar.setVisibility(View.VISIBLE);
        JsonArray params = new JsonArray();



        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cat_id",id);

        params.add(jsonObject);



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONArray response = new JSONArray(responseObject.body().string());
                        Log.v("response",responseObject.message());

                        switch (tag)
                        {
                            case "cat1":
                                subCategorySinner1Model1 =  nokri_populateSpinner(subCategorySinner1,response);
                                break;
                            case "cat2":
                                subCategorySinnerModel2 =  nokri_populateSpinner(subCategorySinner2,response);
                                break;
                            case "cat3":
                                subCategorySinnerModel3 =  nokri_populateSpinner(subCategorySinner3,response);
                                break;
                            case "cat4":
                                subCategorySinnerModel4 =  nokri_populateSpinner(subCategorySinner4,response);
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
                }
                else {
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

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        applicationDeadlineEditText.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
