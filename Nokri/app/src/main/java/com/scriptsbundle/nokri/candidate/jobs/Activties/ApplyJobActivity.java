package com.scriptsbundle.nokri.candidate.jobs.Activties;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.FilePicker.LFilePicker;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.custom.ProgressRequestBody;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerConst;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment.names;

public class ApplyJobActivity extends AppCompatActivity implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {

    LinearLayout progress, withoutLoginApplyLayout;
    Button applyBtn;
    Toolbar toolbar;
    EditText coverLetterEditText;
    Spinner resumeSpinner;
    private String JOB_ID;
    private ArrayList<String> docPaths;
    ArrayList<String> questions = new ArrayList<>();
    LinearLayout questionsLayout;
    ArrayList<String> questionAnswers = new ArrayList<>();
    ImageView uploadResume;
    boolean applyWithoutLogin;
    EditText resumeNotLogin, name, email;
    LinearLayout resumeWithLoginLayout, uploadResumeContainer;
    String resumeId, uploadResumeOption;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }

        JOB_ID = getIntent().getStringExtra("job_id");

//        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        applyWithoutLogin = getIntent().getBooleanExtra("applyWithoutLogin", false);

        progress = findViewById(R.id.progress);


        uploadResumeOption = getIntent().getStringExtra("uploadResumeOption");
        uploadResumeContainer = findViewById(R.id.uploadResumeContainer);
        //0 For Optional 1 for mandatory 2 for hide

        progress.setVisibility(View.GONE);

        applyBtn = findViewById(R.id.btn_applynow);
        Nokri_Utils.setRoundButtonColor(this, applyBtn);
        applyBtn.setOnClickListener(this);
        resumeSpinner = findViewById(R.id.spinner_resume);
        resumeNotLogin = findViewById(R.id.resumeNotLogin);
        resumeWithLoginLayout = findViewById(R.id.resumeWithLoginLayout);
        questionsLayout = findViewById(R.id.questionsLayout);
        uploadResume = findViewById(R.id.upload_resume);
        name = findViewById(R.id.edittxt_name);
        email = findViewById(R.id.edittxt_email);
        LinearLayout imageContainer = findViewById(R.id.imageContainer);

        Nokri_Utils.setRoundButtonColor(this, imageContainer);
        uploadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadResume();
            }
        });
        coverLetterEditText = findViewById(R.id.edittxt_cover_letter);

        withoutLoginApplyLayout = findViewById(R.id.withoutLoginApplyLayout);

        if (applyWithoutLogin) {
            withoutLoginApplyLayout.setVisibility(View.VISIBLE);
            resumeNotLogin.setVisibility(View.VISIBLE);
            resumeWithLoginLayout.setVisibility(View.GONE);
        } else {
            resumeWithLoginLayout.setVisibility(View.VISIBLE);
            resumeNotLogin.setVisibility(View.GONE);
        }
        if (uploadResumeOption.equals("3")){
            resumeNotLogin.setVisibility(View.GONE);
            resumeWithLoginLayout.setVisibility(View.GONE);
        }

        resumeNotLogin.setOnClickListener(this);
        nokri_getPopupLabels();
    }


    private void nokri_getPopupLabels() {
        JsonObject params = new JsonObject();
        params.addProperty("job_id", JOB_ID);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        progress.setVisibility(View.VISIBLE);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(ApplyJobActivity.this)) {
            myCall = restService.getApplyJobPopup(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getApplyJobPopup(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        Nokri_JobDetailFragment.ids = new ArrayList<>();
                        names = new ArrayList<>();
                        String headingText = null, coverText = null;
                        String buttonText = null, selectResumeText = null;


                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");
                        JSONArray infoArray = data.getJSONArray("info");
                        for (int i = 0; i < infoArray.length(); i++) {

                            JSONObject infoObject = infoArray.getJSONObject(i);
                            if (infoObject.getString("field_type_name").equals("job_apply")) {
                                headingText = infoObject.getString("key");
                            } else if (infoObject.getString("field_type_name").equals("job_resume")) {
                                selectResumeText = infoObject.getString("key");
                            } else if (infoObject.getString("field_type_name").equals("job_cvr")) {
                                coverText = infoObject.getString("key");
                            } else if (infoObject.getString("field_type_name").equals("job_btn")) {
                                buttonText = infoObject.getString("key");
                            }

                        }


                        questions.clear();
                        JSONArray questionsArray = data.getJSONArray("job_questions");
                        for (int i = 0; i < questionsArray.length(); i++) {
                            questions.add(String.valueOf(questionsArray.get(i)));
                        }

                        if (questions.size() != 0) {
                            populateQuestions();
                        }

                        JSONArray filterArray = data.getJSONArray("resumes");

                        for (int i = 0; i < filterArray.length(); i++) {
                            JSONObject valueObject = filterArray.getJSONObject(i);
                            Nokri_JobDetailFragment.ids.add(valueObject.getString("key"));
                            names.add(valueObject.getString("value"));
                        }

                        if (names.size() == 0 && !applyWithoutLogin) {
                            showUploadResumeDialog();
                        }

                        populateSpinner(selectResumeText);
                        applyBtn.setText(buttonText);
                        coverLetterEditText.setHint(coverText);
                        getSupportActionBar().setTitle(headingText);
                        //   Log.d("Pointz",modelList.toString());

                        progress.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        progress.setVisibility(View.GONE);
                        e.printStackTrace();
                    } catch (IOException e) {
                        progress.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                } else {
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(ApplyJobActivity.this, t.getMessage());
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void populateQuestions() {
        questionsLayout.removeAllViews();
        for (int i = 0; i < questions.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.questionnaire_display_layout, null);
            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10,
                    r.getDisplayMetrics()
            );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, px, 0, 0);
            EditText editText = view.findViewById(R.id.edittext);
            editText.setLayoutParams(params);
            editText.setHint(questions.get(i));
            questionsLayout.addView(view);
        }
    }

    private void showUploadResumeDialog() {
        new AlertDialog.Builder(this).setTitle("Upload Resume")
                .setMessage("It seems like you haven't uploaded any resume yet. Please upload the resume to post the job.")
                .setPositiveButton("Upload Resume", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadResume();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).show();
    }

    private void uploadResume() {
        Intent data = new Intent(Intent.ACTION_GET_CONTENT);
        data.setType("*/*");
        data = Intent.createChooser(data, "Choose a file");
        sActivityResultLauncher.launch(data);
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && data != null) {
//            if (Constant.REQUEST_CODE_PICK_FILE == requestCode) {
////        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
//                docPaths = new ArrayList<>();
//                docPaths.addAll(data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
//                if (docPaths.size() != 0)
//                    if (applyWithoutLogin) {
//                        resumeNotLogin.setText(docPaths.get(0).getName());
//                        uploadResumeWithoutLogin(docPaths.get(0).getPath());
//                    } else {
//                        Log.d("SHittttttttttttttttt", docPaths.get(0).getPath());
//                        nokri_uploadResumeRequest(docPaths.get(0).getUri().getPath());
//                    }
//                else
//                    Nokri_ToastManager.showShortToast(this, "Nothing Selected");
//
//            }else
//            if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
////                docPaths = new ArrayList<>();
////                docPaths.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//                ArrayList<String> docPaths = data.getStringArrayListExtra("paths");
//                if (applyWithoutLogin) {
//                    resumeNotLogin.setText(docPaths.get(0));
//                    uploadResumeWithoutLogin(docPaths.get(0));
//                } else {
//                    Log.d("SHittttttttttttttttt", docPaths.get(0));
//                    nokri_uploadResumeRequest(docPaths.get(0));
//                }
//            }


        //}
    //}
    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
           new ActivityResultCallback<ActivityResult>() {
                @Override
               public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        Log.d("data",data.getData().toString());
                        if (applyWithoutLogin) {
                            resumeNotLogin.setText(uri.getPath());
                            uploadResumeWithoutLogin(uri.getPath());
                        } else {
                            Log.d("!applyWithoutLogin", uri.getPath());
                            nokri_uploadResumeRequest(uri.getPath());
                        }
                    }

               }
           }
   );

    private void nokri_uploadResumeRequest(String absolutePath) {

        progress.setVisibility(View.VISIBLE);

        Log.v("Cover Upload", String.valueOf(Uri.parse(absolutePath)));
        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("my_cv_upload", file.getName(), requestBody);
        RestService restService = Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(ApplyJobActivity.this), Nokri_SharedPrefManager.getPassword(ApplyJobActivity.this), ApplyJobActivity.this);

        final Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(ApplyJobActivity.this)) {
            myCall = restService.postUploadResumeNormal(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            myCall = restService.postUploadResumeNormal(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }

        // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload", responseObject.message());
                if (responseObject.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Log.v("Resume Upload", jsonObject.toString());
                        if (jsonObject.getBoolean("success")) {


                            Nokri_ToastManager.showLongToast(ApplyJobActivity.this, jsonObject.getString("message"));
                            Nokri_ToastManager.showLongToast(ApplyJobActivity.this, "Updating resume list");
                            progress.setVisibility(View.GONE);
                            nokri_getPopupLabels();
                        } else {
                            Nokri_ToastManager.showLongToast(ApplyJobActivity.this, jsonObject.getString("message"));
                            progress.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        progress.setVisibility(View.GONE);

                        e.printStackTrace();
                    } catch (IOException e) {
                        progress.setVisibility(View.GONE);

                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(ApplyJobActivity.this, t.getMessage());
                progress.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    public void populateSpinner(String headingText) {
        TextView heading = findViewById(R.id.txt_select_resume);
        heading.setText(headingText);
        resumeSpinner.setAdapter(new Nokri_SpinnerAdapter(this, R.layout.spinner_item_popup, Nokri_JobDetailFragment.names));
        resumeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (Nokri_JobDetailFragment.ids != null && !Nokri_JobDetailFragment.ids.isEmpty()) {
                    Nokri_JobDetailFragment.ids.get(resumeSpinner.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == applyBtn.getId()) {
            if (Nokri_JobDetailFragment.ids != null && coverLetterEditText != null && !coverLetterEditText.getText().toString().isEmpty()) {

                Intent returnIntent = new Intent();
                if (questions.size() != 0) {
                    if (gatherQuestions()) {
                        returnIntent.putExtra("answersList", questionAnswers);
                    } else {
                        Nokri_ToastManager.showLongToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                        return;
                    }
                }
                if (!applyWithoutLogin) {

                    if (uploadResumeOption.equals("1")){
                        if (resumeSpinner.getSelectedItemPosition()!=0){
                            returnIntent.putExtra("id", Nokri_JobDetailFragment.ids.get(resumeSpinner.getSelectedItemPosition()));
                        }else{
                            Nokri_ToastManager.showLongToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                            return;
                        }
                    }else{
                        returnIntent.putExtra("id", Nokri_JobDetailFragment.ids.get(resumeSpinner.getSelectedItemPosition()));
                    }
                } else {
                    if (uploadResumeOption.equals("1") && resumeId==null){
                        Toast.makeText(this, "Please select the resume", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    returnIntent.putExtra("id", resumeId);
                }
                returnIntent.putExtra("coverLetter", coverLetterEditText.getText().toString());
                if (applyWithoutLogin) {
                    if (validateFields()) {
                        returnIntent.putExtra("email", email.getText().toString());
                        returnIntent.putExtra("name", name.getText().toString());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                } else {
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            } else {
                Nokri_ToastManager.showLongToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            }

        } else if (view.getId() == resumeNotLogin.getId()) {
//            FilePickerBuilder.getInstance()//optional

            new LFilePicker()
                    .withActivity(ApplyJobActivity.this)
                    .withRequestCode(FilePickerConst.REQUEST_CODE_DOC)
                    .withStartPath("/storage/emulated/0")
                    .withFileFilter(new String[]{".pdf",".xlsx", ".xls", ".doc", ".docx", ".ppt", ".pptx", ".txt"})
                    .withMaxNum(1)
                    .start();
            //FOR SD CARD SHOWING
//            new LFilePicker()
//                    .withActivity(ApplyJobActivity.this)
//                    .withRequestCode(FilePickerConst.REQUEST_CODE_DOC)
//                    .withStartPath("/storage")
////                    .withFileFilter(new String[]{".xlsx", ".xls", ".doc", ".docx", ".ppt", ".pptx", ".txt"})
//                    .start();
//
//
//                    .pickFile(this);
//            Intent intent4 = new Intent(this, NormalFilePickActivity.class);
//            intent4.putExtra(Constant.MAX_NUMBER, 1);
//            intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf", "txt"});
//            startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
        }
    }

    public boolean validateFields() {
        String stEmail, stName;
        stEmail = email.getText().toString();
        stName = name.getText().toString();

        if (stName.equals("")) {
            name.setError("");
            name.requestFocus();
            Nokri_ToastManager.showLongToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(stEmail).matches()) {
            email.setError("");
            email.requestFocus();
            Nokri_ToastManager.showLongToast(this, "Please enter a valid email");
            return false;
        }
        if (stEmail.equals("")) {
            email.setError("");
            email.requestFocus();
            Nokri_ToastManager.showLongToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            return false;
        }
        return true;
    }

    private boolean gatherQuestions() {
        questionAnswers.clear();
        for (int i = 0; i < questionsLayout.getChildCount(); i++) {
            EditText editText = (EditText) questionsLayout.getChildAt(i);
            if (editText.getText().toString().equals("")) {
                editText.setError(Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                editText.requestFocus();
                return false;
            } else {
                questionAnswers.add(editText.getText().toString());
            }
        }
        return true;
    }

    @Override
    public void onProgressUpdate(int percentage) {
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    public void uploadResumeWithoutLogin(String absolutePath) {
        progress.setVisibility(View.VISIBLE);

        Log.v("Cover Upload", String.valueOf(Uri.parse(absolutePath)));
        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("my_cv_upload", file.getName(), requestBody);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        final Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(ApplyJobActivity.this)) {
            myCall = restService.uploadResumeWithoutLogin(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            myCall = restService.uploadResumeWithoutLogin(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }

        // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload", responseObject.message());
                if (responseObject.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        if (jsonObject.getBoolean("success")) {
                            resumeId = jsonObject.getString("data");
                        }
                        Log.v("Resume Upload", jsonObject.toString());

                        progress.setVisibility(View.GONE);
                        Nokri_ToastManager.showLongToast(ApplyJobActivity.this, jsonObject.getString("message"));

                    } catch (JSONException e) {
                        progress.setVisibility(View.GONE);

                        e.printStackTrace();
                    } catch (IOException e) {
                        progress.setVisibility(View.GONE);

                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(ApplyJobActivity.this, t.getMessage());
                progress.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
