package com.scriptsbundle.nokri.candidate.jobs.Activties;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobSpinnerListActivity;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateJobAlert extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    TextView alertName, email, emailFrequency, category, experience, type, categoryValue;
    Spinner emailFrequencySpinner, experienceSpinner, typeSpinner;
    EditText editTextAlertName, editTextEmail;
    Button submit;
    JSONObject response;
    Toolbar toolbar;
    LinearLayout categoryLayout;
    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();
    ArrayList<Nokri_SpinnerModel> models = new ArrayList<>();
    String countryId;
    JsonObject jsonObject;
    Nokri_CandidateDashboardModel candidateDashboardModel;
    TextView tagLine,title;

    Nokri_SettingsModel settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job_alert);

        settings = Nokri_SharedPrefManager.getSettings(this);
        models.clear();
        try {
            response = new JSONObject(getIntent().getStringExtra("jsonObject"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        candidateDashboardModel = Nokri_SharedPrefManager.getCandidateSettings(this);;
        getSupportActionBar().setTitle(candidateDashboardModel.getJobAlerts());


        alertName = findViewById(R.id.txtAlertName);
        email = findViewById(R.id.txt_email);
        emailFrequency = findViewById(R.id.txt_email_frequency);
        category = findViewById(R.id.txt_category);
        categoryValue = findViewById(R.id.txt_category_value);
        experience = findViewById(R.id.txt_experience);
        type = findViewById(R.id.txt_type);
        categoryLayout = findViewById(R.id.category_spinner_layout);
        tagLine = findViewById(R.id.tag_line);
        title = findViewById(R.id.title);
        tagLine.setText(Nokri_Globals.JOB_ALERTS_TAGLINE);
        title.setText(candidateDashboardModel.getJobAlerts());
        emailFrequencySpinner = findViewById(R.id.spinner_email_frequency);
        experienceSpinner = findViewById(R.id.spinner_experience);
        typeSpinner = findViewById(R.id.spinner_type);

        editTextAlertName = findViewById(R.id.edittextAlertName);
        editTextEmail = findViewById(R.id.edittext_email);

        alertName.setText(candidateDashboardModel.getAlertName());
        editTextAlertName.setHint(candidateDashboardModel.getAlertName());
        editTextEmail.setHint(candidateDashboardModel.getAlertEmail());
        email.setText(candidateDashboardModel.getAlertEmail());

        submit = findViewById(R.id.submit);
        submit.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        submit.setText(settings.getFormSubmit());
        submit.setOnClickListener(this);

        try {
            populateSpinners();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void populateSpinners() throws JSONException {
        JSONArray jsonArray = response.getJSONObject("data").getJSONArray("search_fields");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject filterObject = jsonArray.getJSONObject(i);

            if (filterObject.getString("field_type_name").equals("job_type")) {
                type.setText(filterObject.getString("key"));
                String tag = "job_type";
                typeSpinner.setTag(tag);
                nokri_populateSpinner(typeSpinner, filterObject.getJSONArray("value"), tag);
            } else if (filterObject.getString("field_type_name").equals("job_experience")) {
                experience.setText(filterObject.getString("key"));
                String tag = "job_experience";
                experienceSpinner.setTag(tag);
                nokri_populateSpinner(experienceSpinner, filterObject.getJSONArray("value"), tag);
            } else if (filterObject.getString("field_type_name").equals("email_freq")) {
                emailFrequency.setText(filterObject.getString("key"));
                String tag = "email_freq";
                emailFrequencySpinner.setTag(tag);
                nokri_populateSpinner(emailFrequencySpinner, filterObject.getJSONArray("value"), tag);
            } else if (filterObject.getString("field_type_name").equals("job_category")) {
                category.setText(filterObject.getString("key"));

                makeSpinnerList(filterObject.getJSONArray("value"));
                categoryLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(CreateJobAlert.this, PostJobSpinnerListActivity.class);

                        String jsonList;
                        jsonList = new Gson().toJson(categorySpinnerList);
                        i.putExtra("list", jsonList);
                        i.putExtra("calledFrom", "fields");
                        startActivityForResult(i, 321);

                    }
                });
            }
        }

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
                    categoryValue.setText(model.getName());
                    countryId = model.getId();
                }
                categorySpinnerList.add(model);
                {
                    if (jsonObject.getBoolean("selected")) {
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Nokri_SpinnerModel nokri_populateSpinner(Spinner spinner, JSONArray jsonArray, String tag) {

        Nokri_SpinnerModel model = new Nokri_SpinnerModel();
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
        model.setValue(tag);
        if (model.getNames() != null) {

            spinner.setAdapter(new Nokri_SpinnerAdapter(this, R.layout.spinner_item_popup, model.getNames(), true));
            spinner.setOnItemSelectedListener(this);
            models.add(model);
        }

        return model;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                Nokri_SpinnerModel model = (Nokri_SpinnerModel) data.getSerializableExtra("some_key");
                categoryValue.setText(model.getName());
                countryId = model.getId();

            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == submit.getId()) {
            gatherFields();
        }
    }

    private void gatherFields() {
        jsonObject = new JsonObject();

        for (int i = 0; i < models.size(); i++) {
            if (experienceSpinner.getTag().toString().equals(models.get(i).getValue())) {
                jsonObject.addProperty("alert_experience", models.get(i).getIds().get(experienceSpinner.getSelectedItemPosition()));
            }
            if (typeSpinner.getTag().toString().equals(models.get(i).getValue())) {
                jsonObject.addProperty("alert_type", models.get(i).getIds().get(typeSpinner.getSelectedItemPosition()));
            }
            if (emailFrequencySpinner.getTag().toString().equals(models.get(i).getValue())) {
                jsonObject.addProperty("alert_frequency", models.get(i).getIds().get(emailFrequencySpinner.getSelectedItemPosition()));
            }
        }
        jsonObject.addProperty("alert_category", countryId);

        if (checkValidations()) {
            jsonObject.addProperty("alert_name", editTextAlertName.getText().toString());
            jsonObject.addProperty("alert_email", editTextEmail.getText().toString());

            RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

            Nokri_DialogManager manager = new Nokri_DialogManager();
            manager.showAlertDialog(this);
            Call<ResponseBody> myCall;
            if (Nokri_SharedPrefManager.isSocialLogin(CreateJobAlert.this)) {
                myCall = restService.createJobAlert(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.createJobAlert(jsonObject, Nokri_RequestHeaderManager.addHeaders());
            }
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                    if (responseObject.isSuccessful()) {
                        try {
                            JSONObject response = new JSONObject(responseObject.body().string());
                            manager.hideAlertDialog();
                            if (response.getBoolean("success")){
                                Toast.makeText(CreateJobAlert.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CreateJobAlert.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            manager.hideAlertDialog();
                            e.printStackTrace();
                        } catch (IOException e) {
                            manager.hideAlertDialog();
                            e.printStackTrace();
                        }

                    } else {
                        manager.hideAlertDialog();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Nokri_ToastManager.showLongToast(CreateJobAlert.this, t.getMessage());
                    manager.hideAlertDialog();
                }
            });
        } else {
            return;
        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkValidations() {
        if (editTextAlertName.getText().toString().equals("")) {
            editTextAlertName.setError("");
            editTextAlertName.requestFocus();
            return false;
        } if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
            editTextEmail.setError("");
            editTextEmail.requestFocus();
            return false;
        }if (editTextEmail.getText().toString().equals("")) {
            editTextEmail.setError("");
            editTextEmail.requestFocus();
            return false;
        }
        return true;
    }
}
