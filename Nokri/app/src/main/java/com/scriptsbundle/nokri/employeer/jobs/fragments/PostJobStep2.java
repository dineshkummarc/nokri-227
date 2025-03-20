package com.scriptsbundle.nokri.employeer.jobs.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.utils.CustomFontCheckBox;
import com.scriptsbundle.nokri.utils.MyArrayAdapter;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.ItemEditImageAdapter;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.ProgressRequestBody;
import com.scriptsbundle.nokri.employeer.jobs.models.AttachmentModel;
import com.scriptsbundle.nokri.employeer.jobs.models.ImageOnClickListener;
import com.scriptsbundle.nokri.employeer.jobs.models.ModelsPostJobStep2;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.RuntimePermissionHelper;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostJobStep2 extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks, RuntimePermissionHelper.permissionInterface, AdapterView.OnItemSelectedListener {
    //Attachment Section Variables;
    private ArrayList<String> filePathsString;
    int filesCount;
    Toolbar toolbar;
    LinearLayout attachmentlinear;
    TextView filepick, imagepick;
    ProgressDialog dialog;
    ItemEditImageAdapter adapter;
    Nokri_FontManager fontManager;
    LinearLayout linearLayoutCustom;
    JSONArray response;
    ArrayList<AttachmentModel> imagesViewList = new ArrayList<>();
    ArrayList<String> filePaths = new ArrayList<>();
    RuntimePermissionHelper runtimePermissionHelper;
    DragRecyclerView imagesGridView;
    ArrayList<ModelsPostJobStep2.SpinnerArrayListStorageModel> spinnerDataStorageList = new ArrayList<>();
    String jobId;
    LinearLayout next,back;
    ArrayList<ModelsPostJobStep2.EdittextStorageModel> edittextMatchList = new ArrayList<>();
    ArrayList<ModelsPostJobStep2.EdittextStorageModel> radioButtonMatchList = new ArrayList<>();
    private ArrayList<String> photoPaths;
    JSONObject customFieldsJsonObject;
    JSONObject mainJsonOject;
    Calendar myCalendar = Calendar.getInstance();
    TextView nextText,backText;

    public PostJobStep2() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job_step2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }

        fontManager = new Nokri_FontManager();
        runtimePermissionHelper = new RuntimePermissionHelper(this, this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Nokri_Globals.POST_JOB_STRING);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        jobId = getIntent().getStringExtra("job_id");
        try {
            mainJsonOject = new JSONObject(getIntent().getStringExtra("main_object"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        runtimePermissionHelper.requestStorageCameraPermission(1);


        linearLayoutCustom = findViewById(R.id.linearLayoutCustom);
        next = findViewById(R.id.buttonNext);
        back = findViewById(R.id.buttonBack);
        nextText = findViewById(R.id.nextText);
        backText = findViewById(R.id.backText);
        fontManager.nokri_setOpenSenseFontTextView(nextText,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(backText,getAssets());

        try {
            response = new JSONArray(getIntent().getStringExtra("jsonObjectAsString"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeView();


        Nokri_Utils.setRoundButtonColor(this,next);
        Nokri_Utils.setRoundButtonColor(this,back);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customFieldsJsonObject = new JSONObject();
                try{
                    getFieldsData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });
    }


    public void getFieldsData() throws JSONException {
        for (int i = 0; i < linearLayoutCustom.getChildCount(); i++) {
            if (linearLayoutCustom.getChildAt(i).getTag().toString().equals("spinner")) {
                LinearLayout linearLayout = (LinearLayout) linearLayoutCustom.getChildAt(i);
                Spinner adapterView = (Spinner) linearLayout.getChildAt(1);
                for (int j = 0; j < spinnerDataStorageList.size(); j++) {

                    if (spinnerDataStorageList.get(j).isRequired && adapterView.getSelectedItemPosition()==0){
                        adapterView.requestFocus();
                        Toast.makeText(PostJobStep2.this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (spinnerDataStorageList.get(j).id.equals(adapterView.getTag())) {
                        ArrayList<Nokri_SpinnerModel> model = spinnerDataStorageList.get(j).spinnerList;
                        for (int k = 0; k < model.size(); k++) {
                            if (model.get(k).getName().equals(adapterView.getSelectedItem().toString())) {
                                Log.i("data_info",adapterView.getTag()+" : "+model.get(k).getValue());
                                customFieldsJsonObject.put(adapterView.getTag().toString(),model.get(k).getValue());
                            }
                        }
                    }
                }
            }else if(linearLayoutCustom.getChildAt(i).getTag().toString().equals("edittext")){
                LinearLayout editTextLayout = (LinearLayout) linearLayoutCustom.getChildAt(i);
                EditText editText = (EditText) editTextLayout.getChildAt(1);
                for (int j = 0;j<edittextMatchList.size();j++){
                    if (editText.getTag().toString().equals(edittextMatchList.get(j).id)){
                        if (edittextMatchList.get(j).isRequired && editText.getText().toString().equals("")){
                            editText.setError("");
                            editText.requestFocus();
                            return;
                        }else{
                            Log.i("data_info",editText.getTag().toString() + " : "+editText.getText().toString());
                            customFieldsJsonObject.put(editText.getTag().toString() ,editText.getText().toString());
                        }
                    }
                }
            }else if(linearLayoutCustom.getChildAt(i).getTag().toString().equals("link")){
                LinearLayout editTextLayout = (LinearLayout) linearLayoutCustom.getChildAt(i);
                EditText editText = (EditText) editTextLayout.getChildAt(1);
                for (int j = 0;j<edittextMatchList.size();j++){
                    if (editText.getTag().toString().equals(edittextMatchList.get(j).id)){
                        if (edittextMatchList.get(j).isRequired && editText.getText().toString().equals("")){
                            editText.setError("");
                            editText.requestFocus();
                            return;
                        }else{
                            Log.i("data_info",editText.getTag().toString() + " : "+editText.getText().toString());
                            customFieldsJsonObject.put(editText.getTag().toString() ,editText.getText().toString());
                        }
                    }
                }
            }else if(linearLayoutCustom.getChildAt(i).getTag().toString().equals("checkBox")){
                LinearLayout linearLayoutCheckBox = (LinearLayout) linearLayoutCustom.getChildAt(i);
                String checkBoxResultString = "";
                String checkBoxSeprator = ",";
                boolean required = false;
                boolean checked = false;
                String id;
                JSONArray jsonArray = new JSONArray();
                TextView textView = (TextView) linearLayoutCheckBox.getChildAt(0);
                for (int k=0;k<radioButtonMatchList.size();k++){
                    if (textView.getTag().equals(radioButtonMatchList.get(k).id)){
                        if (radioButtonMatchList.get(k).isRequired){
                            required = true;
                            break;
                        }
                    }
                }
                if (linearLayoutCheckBox.getChildCount()>1){
                    for (int j = 1;j<linearLayoutCheckBox.getChildCount();j++){
                        CheckBox checkBox = (CheckBox) linearLayoutCheckBox.getChildAt(j);

                        if (checkBox.isChecked()){
                            checked = true;
                            jsonArray.put(checkBox.getTag().toString());
                            if (checkBoxResultString.equals("")){
                                checkBoxResultString = checkBox.getText().toString();

                            }else{
                                checkBoxResultString = checkBoxResultString + checkBoxSeprator + checkBox.getText().toString();
                            }
                        }
                    }
                }
                if (required &&!checked){
                    Nokri_ToastManager.showShortToast(getApplicationContext(),Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                    return;
                }
                Log.i("data_info",linearLayoutCheckBox.getTag() +" : " +  checkBoxResultString);
                customFieldsJsonObject.put(textView.getTag().toString(),jsonArray);
            }else if(linearLayoutCustom.getChildAt(i).getTag().toString().equals("radio")){
                LinearLayout linearLayoutRadio = (LinearLayout) linearLayoutCustom.getChildAt(i);
                String radioButtonString = "";
                if (linearLayoutRadio.getChildCount()>1){
                    for (int j = 1;j<linearLayoutRadio.getChildCount();j++){
                        RadioGroup radioGroup = (RadioGroup) linearLayoutRadio.getChildAt(j);
                        for (int k=0;k<radioButtonMatchList.size();k++){
                            if (radioGroup.getTag().equals(radioButtonMatchList.get(k).id)){
                                if (radioButtonMatchList.get(k).isRequired){
                                    if (radioGroup.getCheckedRadioButtonId()==-1){
                                        Nokri_ToastManager.showShortToast(getApplicationContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
                                        return;
                                    }
                                }
                            }
                        }
                        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                        if (radioButton!=null){
                            if (radioButton.isChecked()){
                                radioButtonString = radioButton.getText().toString();
                            }
                            Log.i("data_info",radioGroup.getTag() +" : " +  radioButtonString);
                            customFieldsJsonObject.put(radioGroup.getTag().toString(),radioButtonString);
                        }
                    }
                }
            }else if(linearLayoutCustom.getChildAt(i).getTag().toString().equals("date")){
                LinearLayout editTextLayout = (LinearLayout) linearLayoutCustom.getChildAt(i);
                EditText editText = (EditText) editTextLayout.getChildAt(1);
                for (int j = 0;j<edittextMatchList.size();j++){
                    if (editText.getTag().toString().equals(edittextMatchList.get(j).id)){
                        if (edittextMatchList.get(j).isRequired && editText.getText().toString().equals("")){
                            editText.setError("");
                            editText.requestFocus();
                            return;
                        }else{
                            Log.i("data_info",editText.getTag().toString() + " : "+editText.getText().toString());
                            customFieldsJsonObject.put(editText.getTag().toString(),editText.getText().toString());
                        }
                    }
                }
            }
        }
        submitQuery();

    }


    public void submitQuery() throws JSONException {
        mainJsonOject.put("custom_fields",customFieldsJsonObject);
        Log.d("data_info", mainJsonOject.toString());
        Intent i = new Intent(PostJobStep2.this,PostJobStep3.class);
        i.putExtra("main_json_object",mainJsonOject.toString());
        i.putExtra("page3",getIntent().getStringExtra("page3"));
        startActivity(i);
    }

    private void adforest_showDate(final EditText editText) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            if (editText != null)
                editText.setText(sdf.format(myCalendar.getTime()));
        }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    public void initializeView() {
        linearLayoutCustom.removeAllViews();
        radioButtonMatchList.clear();
        edittextMatchList.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                if (response.getJSONObject(i).getString("field_type").equals("attachment")) {
//                    showAttachmentSection();
                } else if (response.getJSONObject(i).getString("field_type").equals("select")) {
                    String title = response.getJSONObject(i).getString("title");
                    boolean isRequired = response.getJSONObject(i).getBoolean("is_required");
                    boolean isShow;
                    try{
                        isShow = response.getJSONObject(i).getBoolean("is_show");
                    }catch (Exception e){
                        e.printStackTrace();
                        isShow = true;
                    }
                    String idName = response.getJSONObject(i).getString("field_type_name");
                    JSONArray jsonArray = response.getJSONObject(i).getJSONArray("values");
                    if (isShow) {
                        populateSpinners(jsonArray, idName, title, i, isRequired);
                    }
                }else if (response.getJSONObject(i).getString("field_type").equals("textfield")){
                    String title = response.getJSONObject(i).getString("title");
                    boolean isRequired = response.getJSONObject(i).getBoolean("is_required");
                    boolean isShow;
                    try{
                        isShow = response.getJSONObject(i).getBoolean("is_show");
                    }catch (Exception e){
                        e.printStackTrace();
                        isShow = true;
                    }
                    String value = response.getJSONObject(i).getString("field_val");
                    String idName = response.getJSONObject(i).getString("field_type_name");
                    if (isShow) {
                        populateEdittext(title,idName,isRequired,value);
                    }
                } else if (response.getJSONObject(i).getString("field_type").equals("link")){
                    String title = response.getJSONObject(i).getString("title");
                    boolean isRequired = response.getJSONObject(i).getBoolean("is_required");
                    boolean isShow;
                    try{
                        isShow = response.getJSONObject(i).getBoolean("is_show");
                    }catch (Exception e){
                        e.printStackTrace();
                        isShow = true;
                    }
                    String value = response.getJSONObject(i).getString("field_val");
                    String idName = response.getJSONObject(i).getString("field_type_name");
                    if (isShow) {
                        populateEdittext(title,idName,isRequired,value);
                    }
                } else if (response.getJSONObject(i).getString("field_type").equals("multi_select")){
                    String title = response.getJSONObject(i).getString("title");
                    boolean isRequired = response.getJSONObject(i).getBoolean("is_required");
                    boolean isShow;
                    try{
                        isShow = response.getJSONObject(i).getBoolean("is_show");
                    }catch (Exception e){
                        e.printStackTrace();
                        isShow = true;
                    }
                    String idName = response.getJSONObject(i).getString("field_type_name");
                    JSONArray jsonArray = response.getJSONObject(i).getJSONArray("values");
                    if (isShow) {
                        populateCheckBox(idName,title,jsonArray,isRequired);
                    }
                }else if (response.getJSONObject(i).getString("field_type").equals("checkbox")){
                    String title = response.getJSONObject(i).getString("title");
                    boolean isShow;
                    boolean isRequired = response.getJSONObject(i).getBoolean("is_required");
                    try{
                        isShow = response.getJSONObject(i).getBoolean("is_show");
                    }catch (Exception e){
                        e.printStackTrace();
                        isShow = true;
                    }
                    String idName = response.getJSONObject(i).getString("field_type_name");
                    JSONArray jsonArray = response.getJSONObject(i).getJSONArray("values");
                    if (isShow) {
                        populateRadioButton(idName,title,jsonArray,isRequired);
                    }
                }else if (response.getJSONObject(i).getString("field_type").equals("date")){
                    String title = response.getJSONObject(i).getString("title");
                    boolean isShow;
                    boolean isRequired = response.getJSONObject(i).getBoolean("is_required");
                    try{
                        isShow = response.getJSONObject(i).getBoolean("is_show");
                    }catch (Exception e){
                        e.printStackTrace();
                        isShow = true;
                    }
                    String idName = response.getJSONObject(i).getString("field_type_name");
                    String fieldValue = response.getJSONObject(i).getString("field_val");
                    if (isShow) {
                        populateDateField(idName,title,fieldValue,isRequired);
                    }
                }
            }
//            next = new Button(this);
//
//            linearLayoutCustom.addView(next, linearLayoutCustom.getChildCount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void populateDateField(String idName,String title,String fieldValue,boolean isRequired){
        View view = LayoutInflater.from(this).inflate(R.layout.custom_date_field,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        TextView heading = view.findViewById(R.id.heading);

        fontManager.nokri_setOpenSenseFontTextView(heading,getAssets());
        EditText dateField = view.findViewById(R.id.editText);
        heading.setText(title);
        dateField.setHint(title);
        if (!fieldValue.equals("")){
            dateField.setText(fieldValue);
        }
        dateField.setTag(idName);
        dateField.setFocusable(false);
        dateField.setFocusableInTouchMode(false);
        fontManager.nokri_setOpenSenseFontEditText(dateField,getAssets());
        dateField.setOnClickListener(v -> adforest_showDate(dateField));
        ModelsPostJobStep2.EdittextStorageModel model = new ModelsPostJobStep2.EdittextStorageModel();
        model.id = idName;
        model.isRequired = isRequired;
        model.name = title;
        model.value = dateField.getText().toString();
        edittextMatchList.add(model);
        view.setTag("date");
        linearLayoutCustom.addView(view);
    }

    private void populateRadioButton(String idName, String title, JSONArray jsonArray, boolean isRequired) throws JSONException {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_radio_button,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        TextView heading = view.findViewById(R.id.heading);
        heading.setText(title);
        fontManager.nokri_setOpenSenseFontTextView(heading,getAssets());
        RadioGroup radioGroup = (RadioGroup) ((LinearLayout)view).getChildAt(1);
        for (int i = 0;i<jsonArray.length();i++){
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.topMargin = 3;
            params2.bottomMargin = 3;
            String name = jsonArray.getJSONObject(i).getString("name");
            String value = jsonArray.getJSONObject(i).getString("value");
            Boolean has_sub = jsonArray.getJSONObject(i).getBoolean("has_sub");
            Boolean has_template = jsonArray.getJSONObject(i).getBoolean("has_template");
            boolean selected = jsonArray.getJSONObject(i).getBoolean("selected");
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(value);
            radioButton.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans.ttf"));
            if (selected){
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton,i,params2);
        }
        ModelsPostJobStep2.EdittextStorageModel model = new ModelsPostJobStep2.EdittextStorageModel();
        model.id = idName;
        model.isRequired = isRequired;
        radioButtonMatchList.add(model);
        radioGroup.setTag(idName);
        view.setTag("radio");
        linearLayoutCustom.addView(view);
    }

    public void populateCheckBox(String idName, String title, JSONArray checkBoxJSONOpt, boolean isRequired) throws JSONException {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_checkbox_layout,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        TextView heading = view.findViewById(R.id.heading);
        heading.setText(title);
        heading.setTag(idName);
        fontManager.nokri_setOpenSenseFontTextView(heading,getAssets());
        LinearLayout checkBoxLayout = view.findViewById(R.id.checkboxLayout);
        for (int i = 0;i<checkBoxJSONOpt.length();i++){
            CustomFontCheckBox checkBox = new CustomFontCheckBox(this);
            checkBox.setFocusable(true);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.topMargin = 3;
            params2.bottomMargin = 3;

            String optionString = checkBoxJSONOpt.getJSONObject(i).getString("name");
            String checkId = checkBoxJSONOpt.getJSONObject(i).getString("id");
            boolean isChecked = checkBoxJSONOpt.getJSONObject(i).getBoolean("is_checked");
            checkBox.setTag(checkId);
            if (isChecked){
                checkBox.setChecked(true);
            }
            checkBox.setText(optionString);
            checkBoxLayout.addView(checkBox, i+1,params2);
        }
        checkBoxLayout.setTag(idName);

        ModelsPostJobStep2.EdittextStorageModel model = new ModelsPostJobStep2.EdittextStorageModel();
        model.id = idName;
        model.isRequired = isRequired;
        radioButtonMatchList.add(model);
        checkBoxLayout.setTag(idName);
        view.setTag("checkBox");
        linearLayoutCustom.addView(view);
    }

    public void populateEdittext(String title,String idName, boolean isRequired,String value){
        View view = LayoutInflater.from(this).inflate(R.layout.custom_edittext,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        EditText editText = view.findViewById(R.id.editText);
        TextView heading = view.findViewById(R.id.heading);
        fontManager.nokri_setOpenSenseFontTextView(heading,getAssets());
        heading.setText(title);
        editText.setHint(title);
        if (!value.equals("false"))
            editText.setText(value);
        editText.setTag(idName);
        fontManager.nokri_setOpenSenseFontEditText(editText,getAssets());
        ModelsPostJobStep2.EdittextStorageModel model = new ModelsPostJobStep2.EdittextStorageModel();
        model.id = idName;
        model.isRequired = isRequired;
        model.name = title;
        model.value = editText.getText().toString();
        edittextMatchList.add(model);
        view.setTag("edittext");
        linearLayoutCustom.addView(view);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    filePathsString = new ArrayList<>();
                    filePathsString.addAll(photoPaths);

                    filesCount = filePathsString.size();
                }
                break;
//            case Constant.REQUEST_CODE_PICK_FILE:
//                if (resultCode == RESULT_OK && data != null) {
//                    ArrayList<NormalFile> docPaths = new ArrayList<>();
//                    docPaths.addAll(data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE));
//
//                    filePathsString = new ArrayList<>();
//                    for (int i = 0; i < docPaths.size(); i++) {
//                        filePathsString.add(docPaths.get(i).getPath());
//                    }
//                    filesCount = filePathsString.size();
//                    docPaths.clear();
//                }
        }
        if (filePathsString != null && filePathsString.size() != 0) {
            dialog.setMessage("Uploading...");
            dialog.show();
            for (String d : filePathsString) {
                nokri_uploadResumeRequest(d);
            }

            super.onActivityResult(requestCode, resultCode, data);

        }
    }


    public void showAttachmentSection() {
        View view = LayoutInflater.from(this).inflate(R.layout.attachment_custom_section, null);

        attachmentlinear = view.findViewById(R.id.attachmentlinear);
        filepick = view.findViewById(R.id.selectfile);
        imagepick = view.findViewById(R.id.selectPix);
        attachmentlinear.setVisibility(View.VISIBLE);
        filepick.setVisibility(View.VISIBLE);
        imagepick.setVisibility(View.VISIBLE);
        filepick.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                show_filedocs();
            }
        });
        imagepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFileChooser();
            }
        });

        view.setTag("attach");
        linearLayoutCustom.addView(view);
    }
//        TextView customOptionsName = new TextView(this);
//        customOptionsName.setTextSize(12);
//        customOptionsName.setAllCaps(true);
//        customOptionsName.setPadding(10, 15, 10, 15);
//        customOptionsName.setText("Custom Options List Title");
//        customOptionsName.setFocusable(true);
//        CardView cardView = new CardView(this);
//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.setPadding(5, 15, 5, 15);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.addView(customOptionsName);
//
////        LinearLayout linearLayoutdate = new LinearLayout(this);
////        linearLayoutdate.setOrientation(LinearLayout.HORIZONTAL);
////        TextInputLayout til1 = new TextInputLayout(this);
////        TextInputLayout till = new TextInputLayout(this);
////        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        params3.weight = 1;
////        til1.setHint("Hello");
////        till.setHint("Hello");
////        EditText et = new EditText(this);
////        et.setId(5555);
////        EditText et2 = new EditText(this);
////        et2.setId(6666);
////        til1.setLayoutParams(params3);
////        till.setLayoutParams(params3);
////        et.setTextSize(14);
////        et2.setTextSize(14);
////        et2.setFocusable(true);
////        et.setFocusable(true);
////        Drawable img = getResources().getDrawable(R.drawable.ic_photo_camera_black_24dp);
////        Drawable img2 = getResources().getDrawable(R.drawable.ic_attachment_black_24dp);
////        et.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null);
////        et2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img2, null);
////        til1.addView(et);
////        till.addView(et2);
////
////        linearLayoutdate.addView(til1);
////        linearLayoutdate.addView(till);
////        cardView.setContentPadding(10, 20, 10, 20);
////        et.setClickable(false);
////        et.setFocusable(false);
////        et2.setClickable(false);
////        et2.setFocusable(false);
////        EditText editText = et;
////        EditText editText1 = et2;
////        et.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (v.getId() == 5555) {
//////                    selectImageFromGallery(1);
////                }
////            }
////
////        });
////
////        et2.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (v.getId() == 6666||v.getId()==5555) {
//////                    Intent intent4 = new Intent(PostJobStep2.this, NormalFilePickActivity.class);
//////                    intent4.putExtra(Constant.MAX_NUMBER, 3);
//////                    intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf","txt"});
//////                    startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
////                    showFileChooser();
////                }
////            }
////        });
////
////        linearLayout.addView(cardView);
////        cardView.setBackground(getResources().getDrawable(R.drawable.attachmentaradius));
////        cardView.addView(linearLayoutdate);
//////        cardView.setBackground(getResources().getDrawable(R.drawable.spinner_background))(
////        linearLayoutCustom.addView(linearLayout);
//    }


    private void showFileChooser() {
        FilePickerBuilder.getInstance().setMaxCount(5)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.LibAppTheme)
                .pickPhoto(this);
    }

    private void show_filedocs() {

//        FilePickerBuilder.getInstance().setMaxCount(10)
//                .setSelectedFiles(filePaths)
//                .setActivityTheme(R.style.LibAppTheme)
//                .pickFile(this);
//        Intent intent4 = new Intent(PostJobStep2.this, NormalFilePickActivity.class);
//        intent4.putExtra(Constant.MAX_NUMBER, 3);
//        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf", "txt"});
//        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }

    public void populateSpinners(JSONArray jsonArray, String idName, String title, int id,
                                 boolean isRequired) {
        View view = LayoutInflater.from(this).inflate(R.layout.spinner_dropdown_custom, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        Spinner spinner = view.findViewById(R.id.spinner);

        spinner.setTag(idName);
        TextView heading = view.findViewById(R.id.heading);

        fontManager.nokri_setOpenSenseFontTextView(heading, getAssets());
        heading.setText(title);

        ArrayList<Nokri_SpinnerModel> spinnerListData = new ArrayList<>();
        ArrayList<String> spinnerList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Nokri_SpinnerModel model = new Nokri_SpinnerModel();
            try {
                model.setValue(jsonArray.getJSONObject(i).getString("value"));
                try {
                    boolean selectedOption = jsonArray.getJSONObject(i).getBoolean("selected");
                    model.setSelected(selectedOption);
                }catch (Exception e){
                    e.printStackTrace();
                }
                model.setName(jsonArray.getJSONObject(i).getString("name"));
                spinnerList.add(model.getName());
                spinnerListData.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ModelsPostJobStep2.SpinnerArrayListStorageModel spinnerArrayListStorageModel = new ModelsPostJobStep2.SpinnerArrayListStorageModel();
        spinnerArrayListStorageModel.id = idName;
        spinnerArrayListStorageModel.isRequired = isRequired;
        spinnerArrayListStorageModel.spinnerList = spinnerListData;
        spinnerDataStorageList.add(spinnerArrayListStorageModel);
        final Typeface[] tfavv = new Typeface[1];
        MyArrayAdapter adapter = new MyArrayAdapter(this,android.R.layout.simple_spinner_item,spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);
        for (int i=0;i<spinnerListData.size();i++){
            if (spinnerListData.get(i).getSelected()){
                spinner.setSelection(i);
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != 0) {
//                    for (int j = 0; j < spinnerDataStorageList.size(); j++) {
//                        if (spinnerDataStorageList.get(j).id.equals(adapterView.getTag())) {
//                            Toast.makeText(PostJobStep2.this, String.valueOf(adapterView.getTag() + " : " + adapterView.getSelectedItem().toString()), Toast.LENGTH_SHORT).show();
//                            ArrayList<Nokri_SpinnerModel> model = spinnerDataStorageList.get(j).spinnerList;
//                            for (int k = 0; k < model.size(); k++) {
//                                if (model.get(k).getName().equals(adapterView.getSelectedItem().toString())) {
//                                    Toast.makeText(PostJobStep2.this, model.get(k).getValue(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        view.setTag("spinner");
        linearLayoutCustom.addView(view);
    }


//    private void selectImageFromGallery(int flag) {
//        FilePickerBuilder.getInstance().setMaxCount(5)
//                .setSelectedFiles(filePaths)
//                .setActivityTheme(R.style.LibAppTheme)
//                .showGifs(true)
//                .pickPhoto(this);
//    }


    private void nokri_uploadResumeRequest(String absolutePath) {

        Log.v("Cover Upload", String.valueOf(Uri.parse(absolutePath)));
        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, this);

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("custom_upload", file.getName(), requestBody);
        RestService restService = Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(PostJobStep2.this), Nokri_SharedPrefManager.getPassword(PostJobStep2.this), PostJobStep2.this);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), jobId);

        final Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(PostJobStep2.this)) {
            myCall = restService.postUploadDynamicAttachments(fileToUpload, description, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            myCall = restService.postUploadDynamicAttachments(fileToUpload, description, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }


        // Call<ResponseBody> myCall  = restService.postUploadResume(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                Log.v("Cover Upload", responseObject.message());
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        Log.d("responceuplaodresumeu", jsonObject.toString());
                        if (jsonObject.getBoolean("success")) {
                            // JSONArray dataArray = jsonObject.getJSONArray("data");
                            if (imagesGridView != null) {
                                AttachmentModel model = new AttachmentModel();
                                model.url = absolutePath;
                                model.attachmentId = jsonObject.getString("extras");
                                imagesViewList.add(model);
                                adapter.notifyDataSetChanged();
                            } else {
                                AttachmentModel model = new AttachmentModel();
                                model.url = absolutePath;
                                model.attachmentId = jsonObject.getString("extras");
                                imagesViewList.add(model);
                                setupGridView();
                            }
                            Nokri_ToastManager.showLongToast(PostJobStep2.this, jsonObject.getString("message"));
                            // Log.v("Resume Upload",dataArray.toString());

//                            nokri_getCandidateResumeList();

                        } else {

                            Nokri_ToastManager.showLongToast(PostJobStep2.this, jsonObject.getString("message"));
                            // nokri_getCandidateResumeList();
                        }
                        if (filePathsString.size() != 0) {
                            if (filePathsString.get(filesCount - 1).equals(absolutePath)) {
//                                progressBar.setVisibility(View.GONE);
                                filePathsString.clear();
                                dialog.dismiss();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (filePathsString.size() != 0) {
                            if (filePathsString.get(filesCount - 1).equals(absolutePath)) {
                                filePathsString.clear();
                                dialog.dismiss();
                            }
                        }
                        Toast.makeText(PostJobStep2.this, "Can't upload picture", Toast.LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                dialog.dismiss();
//                progressBar.setVisibility(View.GONE);
                Nokri_ToastManager.showLongToast(PostJobStep2.this, t.getMessage());
            }
        });
    }


    public void setupGridView() {

        imagesGridView = new DragRecyclerView(PostJobStep2.this, null);
        GridLayoutManager MyLayoutManager = new GridLayoutManager(this, 3);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        imagesGridView.setLayoutManager(MyLayoutManager);
        adapter = new ItemEditImageAdapter(PostJobStep2.this, imagesViewList);
        imagesGridView.setAdapter(adapter);
        adapter.setHandleDragEnabled(true);
        adapter.setLongPressDragEnabled(true);
        adapter.setSwipeEnabled(true);

        adapter.setOnItemClickListener(new ImageOnClickListener() {
            @Override
            public void onItemClick(AttachmentModel item) {

            }

            @Override
            public void delViewOnClick(View v, int position) {
//                progressBar.setVisibility(View.VISIBLE);
                dialog.setMessage("Deleting...");
                dialog.show();
                deleteAttachment(imagesViewList.get(position).attachmentId, position);
            }

            @Override
            public void editViewOnClick(View v, int position) {

            }
        });

        linearLayoutCustom.addView(imagesGridView, 1);
    }

    public void deleteAttachment(String attachId, int position) {
        JsonObject params = new JsonObject();
        params.addProperty("job_id", jobId);
        params.addProperty("attach_id", attachId);

        Call<ResponseBody> call;
        RestService restService = Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(PostJobStep2.this), Nokri_SharedPrefManager.getPassword(PostJobStep2.this), PostJobStep2.this);


        if (Nokri_SharedPrefManager.isSocialLogin(PostJobStep2.this)) {
            call = restService.postDeleteAttachment(params, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            call = restService.postDeleteAttachment(params, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
//                        progressBar.setVisibility(View.GONE);
                        if (jsonObject.getBoolean("success")) {
                            imagesViewList.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(PostJobStep2.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
//                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    } catch (IOException e) {
//                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
//                    progressBar.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
    }


    @Override
    public void onProgressUpdate(int percentage) {

//        progressDialolque.updateProgress(percentage);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccessPermission(int code) {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
