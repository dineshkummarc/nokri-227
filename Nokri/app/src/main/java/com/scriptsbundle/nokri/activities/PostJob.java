package com.scriptsbundle.nokri.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobSpinnerListActivity;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobStep2;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostJob extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener, View.OnFocusChangeListener {

    Toolbar toolbar;
    LinearLayout progressBar;
    LinearLayout buttonNext;
    private String jobId;
    private TextView applicationDeadlineTextView, basicInformationTextView, jobTitleTextView, jobDescriptionTextView;
    private EditText jobTitleEditText, applicationDeadlineEditText, latitudeEditText, longitudeEditText, noOfPositionEditText;
    private Nokri_SpinnerModel jobCategorySpinnerModel, subCategorySinner1Model1, subCategorySinnerModel2, subCategorySinnerModel3, subCategorySinnerModel4, jobQualificationSpinnerModel, jobTypeSpinnerModel;
    private RichEditor jobDetailsEditor;
    private Spinner jobQualificationSpinner, jobTypeSpinner;
    private Calendar calendar;
    RestService restService;
    String categoryResponseString;
    TextView categoryTitle, categoryValue;
    LinearLayout categorySpinnerLayout;
    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();
    JSONObject mainJsonObject, page3Object;
    String catId;
    String jobIdForUpdate;
    boolean isUpdate;
    TextView nextText;
    Nokri_FontManager fontManager = new Nokri_FontManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }



        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Job");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("isUpdate"))
            jobIdForUpdate = getIntent().getStringExtra("jobId");



        calendar = Calendar.getInstance();
        restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this), this);
        initializeView();
        setupFonts();
        if (jobIdForUpdate!=null){
            getEditPostJob();
        }else{
            getPostJob();
        }


    }

    private void setupFonts() {
        fontManager.nokri_setOpenSenseFontTextView(categoryTitle,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(categoryValue,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobTitleEditText,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(basicInformationTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobTitleTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(jobDescriptionTextView,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(applicationDeadlineEditText,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(applicationDeadlineTextView,getAssets());
    }

    public void initializeView() {

        progressBar = findViewById(R.id.progress);
        buttonNext = findViewById(R.id.buttonNext);
        Nokri_Utils.setRoundButtonColor(this,buttonNext);

        Nokri_Utils.setRoundButtonColor(this, buttonNext);
        basicInformationTextView = findViewById(R.id.txt_basic_info);
        jobTitleTextView = findViewById(R.id.txt_job_title);
        jobDescriptionTextView = findViewById(R.id.txt_job_description);

        nextText = findViewById(R.id.nextText);
        nextText.setText(Nokri_Globals.NEXT_STRING);
        fontManager.nokri_setOpenSenseFontTextView(nextText,getAssets());

        //Category Custom Spinner Views
        categorySpinnerLayout = findViewById(R.id.category_spinner_layout);
        categoryTitle = findViewById(R.id.category_title);
        categoryValue = findViewById(R.id.category_value);
        jobTitleEditText = findViewById(R.id.edittxt_job_title);

        applicationDeadlineTextView = findViewById(R.id.txt_application_deadline);
        applicationDeadlineEditText = findViewById(R.id.editText_application_deadline);

        applicationDeadlineEditText.setOnTouchListener(this);


        applicationDeadlineEditText.setOnFocusChangeListener(this);

        jobDetailsEditor = findViewById(R.id.edittxt_descripton);
        jobDetailsEditor.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        jobDetailsEditor.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));
        buttonNext.setOnClickListener(this);
        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setOpenSenseFontTextView((TextView) (buttonNext.getChildAt(0)), getAssets());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPostJob() {
        progressBar.setVisibility(View.VISIBLE);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(this)) {
            myCall = restService.getPostJob(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getPostJob(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject data = new JSONObject(responseObject.body().string());
                        if (data.getBoolean("success")) {
                            JSONObject response = data.getJSONObject("data");
                            page3Object = response;
                            basicInformationTextView.setText(response.getJSONObject("basic_info").getString("key"));
                            jobId = response.getString("job_id");
                            Log.d("tagggggggg", jobId);
                            jobTitleTextView.setText(response.getJSONObject("job_title").getString("key"));
//                            jobTitleEditText.setHint(response.getJSONObject("job_title").getString("key"));
                            categoryTitle.setText(response.getJSONObject("job_category").getString("key"));
                            JSONArray jsonArray = response.getJSONObject("job_category").getJSONArray("value");
                            categoryValue.setText(jsonArray.getJSONObject(0).getString("value"));
                            getCategoryData(jsonArray.getJSONObject(0).getString("key"),null);

                            categorySpinnerLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (jsonArray != null && jsonArray.length() != 0) {

                                        makeSpinnerList(jsonArray);

                                        String jsonList;
                                        jsonList = new Gson().toJson(categorySpinnerList);

                                        if (jsonList != null) {
                                            overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
                                            Intent intent = new Intent(PostJob.this, PostJobSpinnerListActivity.class);
                                            intent.putExtra("list", jsonList);
                                            intent.putExtra("calledFrom", "categories");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivityForResult(intent, 321);
                                        }
                                    }
                                }
                            });
                            jobDescriptionTextView.setText(response.getJSONObject("job_description").getString("key"));
                            jobDetailsEditor.setPlaceholder(response.getJSONObject("job_description").getString("key"));
                            applicationDeadlineTextView.setText(response.getJSONObject("job_deadline").getString("key"));
                            applicationDeadlineEditText.setHint(response.getJSONObject("job_deadline").getString("key"));

                        } else {
                            Toast.makeText(PostJob.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(PostJob.this, e.getLocalizedMessage());
                        progressBar.setVisibility(View.GONE);
                        finish();
                        e.printStackTrace();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.GONE);
                        finish();
                        e.printStackTrace();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                finish();
            }
        });
    }

    private void getEditPostJob() {
        progressBar.setVisibility(View.VISIBLE);

        JsonObject params = new JsonObject();

        params.addProperty("is_update",jobIdForUpdate);
        params.addProperty("job_id",jobIdForUpdate);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(this)) {
            myCall = restService.editPostJob(params,Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.editPostJob(params,Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject data = new JSONObject(responseObject.body().string());
                        if (data.getBoolean("success")) {
                            JSONObject response = data.getJSONObject("data");
                            Nokri_Globals.POST_JOB_STRING = response.getString("job_post_txt");
                            getSupportActionBar().setTitle(Nokri_Globals.POST_JOB_STRING);
                            page3Object = response;
                            basicInformationTextView.setText(response.getJSONObject("basic_info").getString("key"));
                            jobId = response.getString("job_id");
                            Log.d("tagggggggg", jobId);

                            //Storing Title Value
                            jobTitleTextView.setText(response.getJSONObject("job_title").getString("key"));
                            jobTitleEditText.setText(response.getJSONObject("job_title").getString("value"));

                            //Select Category
                            categoryTitle.setText(response.getJSONObject("job_category").getString("key"));
                            JSONArray jsonArray = response.getJSONObject("job_category").getJSONArray("value");
                            for (int i =0;i<jsonArray.length();i++){
                                boolean selectedCategory = jsonArray.getJSONObject(i).getBoolean("selected");
                                if (selectedCategory){
                                    categoryValue.setText(jsonArray.getJSONObject(i).getString("value"));
                                    catId = jsonArray.getJSONObject(i).getString("key");
                                    getCategoryData(catId,jobIdForUpdate);
                                }
                            }

                            categorySpinnerLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (jsonArray != null && jsonArray.length() != 0) {

                                        makeSpinnerList(jsonArray);

                                        String jsonList;
                                        jsonList = new Gson().toJson(categorySpinnerList);

                                        if (jsonList != null) {
                                            overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
                                            Intent intent = new Intent(PostJob.this, PostJobSpinnerListActivity.class);
                                            intent.putExtra("list", jsonList);
                                            intent.putExtra("calledFrom", "categories");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivityForResult(intent, 321);
                                        }
                                    }
                                }
                            });
                            jobDescriptionTextView.setText(response.getJSONObject("job_desc").getString("key"));
                            jobDetailsEditor.setHtml(response.getJSONObject("job_desc").getString("value"));
                            applicationDeadlineTextView.setText(response.getJSONObject("job_deadline").getString("key"));
                            applicationDeadlineEditText.setText(response.getJSONObject("job_deadline").getString("value"));
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(PostJob.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(PostJob.this, e.getLocalizedMessage());
                        progressBar.setVisibility(View.GONE);
                        finish();
                        e.printStackTrace();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.GONE);
                        finish();
                        e.printStackTrace();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == buttonNext.getId()) {
            if (categoryResponseString!=null){
                if (!categoryResponseString.equals("")) {
                    try {
                        if (!createJsonObject())
                            return;

                        if (jobIdForUpdate!=null){
                            page3Object.put("is_update",true);
                            page3Object.put("updateId",jobIdForUpdate);
                        }else{
                            page3Object.put("is_update",false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(this, PostJobStep2.class);
                    i.putExtra("jsonObjectAsString", categoryResponseString);
                    if (jobIdForUpdate!=null)
                        i.putExtra("job_id", jobIdForUpdate);
                    else
                        i.putExtra("job_id", jobId);
                    i.putExtra("main_object", mainJsonObject.toString());
                    i.putExtra("page3", page3Object.toString());
                    startActivity(i);
                }
            }
        }
    }

    private boolean createJsonObject() throws JSONException {
        mainJsonObject = new JSONObject();
        if (validate()){
            mainJsonObject.put("job_title", jobTitleEditText.getText().toString());
            mainJsonObject.put("job_cat", catId);
            mainJsonObject.put("job_description", jobDetailsEditor.getHtml());
            mainJsonObject.put("is_update", jobId);
            mainJsonObject.put("job_date", applicationDeadlineEditText.getText().toString());
            Log.d("data_info", mainJsonObject.toString());
            return true;
        }

        return false;
    }

    private boolean validate() {
        if (jobTitleEditText.getText().toString().equals("")){
            jobTitleEditText.setError(Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            jobTitleEditText.requestFocus();
            return false;
        }else if (applicationDeadlineEditText.getText().toString().equals("")){
            applicationDeadlineEditText.setError(Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            applicationDeadlineEditText.requestFocus();
            return false;
        }else if (jobDetailsEditor.getHtml()==null){
            Nokri_ToastManager.showShortToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            applicationDeadlineEditText.requestFocus();
            return false;
        }else if (jobDetailsEditor.getHtml()!=null){
            if (jobDetailsEditor.getHtml().equals("")){
                Nokri_ToastManager.showShortToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                applicationDeadlineEditText.requestFocus();
                return false;
            }
        }
        return true;
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


    public void getCategoryData(String catId,String jobId) {
        Call<ResponseBody> myCall;
        JsonObject params = new JsonObject();
        if (jobId!=null){
            params.addProperty("job_id",jobId);
        }
        params.addProperty("cat_id", catId);
        myCall = restService.postGetDynamicFields(params, Nokri_RequestHeaderManager.addHeaders());

        progressBar.setVisibility(View.VISIBLE);
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success")) {
                            categoryResponseString = response.body().string();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            categoryResponseString = "";
                            categoryResponseString = jsonArray.toString();
                            progressBar.setVisibility(View.GONE);
                            } else {
                            Toast.makeText(PostJob.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(PostJob.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PostJob.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
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


    @SuppressLint("NewApi")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            DatePickerDialog dialog = new DatePickerDialog(this);
            dialog.setOnDateSetListener(this);
            dialog.onDateChanged(dialog.getDatePicker(),calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        }


        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                Nokri_SpinnerModel model = (Nokri_SpinnerModel) data.getSerializableExtra("some_key");
                categoryValue.setText(model.getName());
                catId = model.getId();
                getCategoryData(model.getId(),null);
            }
        }
    }
}
