package com.scriptsbundle.nokri.candidate.edit.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.employeer.jobs.models.ModelsPostJobStep2;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.custom.ProgressRequestBody;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_UploadProgressDialolque;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_PathUtils;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.utils.models.Nokri_EditTextModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_PersonalInfoFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, View.OnTouchListener, ProgressRequestBody.UploadCallbacks, AdapterView.OnItemSelectedListener, Nokri_PopupManager.ConfirmInterface {

    private static final int PICK_IMAGE_PROFILE = 200;
    private TextView nameDataTextView, headlineDataTextView, nameTextView, phoneTextView, emailTextView, headlineTextView, dateTextView, educationTextView, profileImageTextView, aboutTextView, statusTextView, changePasswordTextView, deleteAccountTextView,
            salaryTypeTextView, salaryRangeTextView,salaryCurrencyTextView,genderTextView;

    private EditText nameEditText, phoneEditText, emailEditText, headlineEditText, dateEditText, pofileImaeEditText;
    private RichEditor aboutEditText;
    private Button saveProfileButton;
    private ImageView boldImageView, italicImageView, underlineImageView, numberBulletsImageView, listBulletsImageView;
    private Nokri_FontManager fontManager;
    private CircularImageView profileImageView;
    private Calendar calendar;
    private Spinner educationEditText;
    private ArrayList<String> idList;
    private ArrayList<Nokri_EditTextModel> editTextModels;
    private Nokri_UploadProgressDialolque progressDialolque;

    private LinearLayout textarea;
    private LinearLayout container;

    JSONArray customFields;
    LinearLayout customFieldsLayout;

    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private Nokri_DialogManager dialogManager;

    private Spinner genderSpinner, levelSpinner, typeSpinner, experienceSpinner, statusSpinner,salaryTypeSpinner, salaryRangeSpinner, salaryCurrencySpinner;
    private TextView levelTextView, typeTextView, experienceTextView;
    private ArrayList<String> genderSpinnerIds,levelSpinnerIds, typeSpinnerIds, experienceSpinnerIds, statusSpinnerIds, salaryTypeIds, salaryRangeIds, salaryCurrencyIds;
    private Nokri_PopupManager popupManager;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!mUserSeen && isVisibleToUser) {
            mUserSeen = true;
            onUserFirstSight();
            tryViewCreatedFirstSight();
        }
        onUserVisibleChanged(isVisibleToUser);
    }

    protected void onUserFirstSight() {

    }

    protected void onUserVisibleChanged(boolean visible) {
        if (getContext() != null) {
            if (visible) {


            }

        }
    }

    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }

    protected void onViewCreatedFirstSight(View view) {

    }


    public Nokri_PersonalInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("opt1", getClass().getSimpleName() + " : onActivityCreated");
//        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(getActivity());
        nokri_initialize();
        nokri_setDataFromSharedref();
        try {

            nokri_getCandidatePersonalInfo();
        }catch (Exception e){
            e.printStackTrace();
        }
        nokri_setFonts();
        nokri_setListeners();


    }


    private void nokri_setListeners() {

        pofileImaeEditText.setOnFocusChangeListener(this);
        dateEditText.setOnFocusChangeListener(this);
        nameEditText.setOnFocusChangeListener(this);
        phoneEditText.setOnFocusChangeListener(this);

        headlineEditText.setOnFocusChangeListener(this);
        saveProfileButton.setOnClickListener(this);
        pofileImaeEditText.setOnTouchListener(this);

        dateEditText.setOnTouchListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_personal_info, container, false);
    }

    private void nokri_initialize() {
        editTextModels = new ArrayList<>();
        fontManager = new Nokri_FontManager();
        genderSpinner = getView().findViewById(R.id.spinner_gender);
        genderTextView = getView().findViewById(R.id.txt_gender);
        salaryCurrencySpinner = getView().findViewById(R.id.spinner_salary_currency);
        salaryCurrencyTextView = getView().findViewById(R.id.txt_salary_currency);
        salaryRangeSpinner = getView().findViewById(R.id.spinner_salary_range);
        salaryRangeTextView = getView().findViewById(R.id.txt_salary_range);
        salaryTypeSpinner = getView().findViewById(R.id.spinner_salary_type);
        salaryTypeTextView = getView().findViewById(R.id.txt_salary_type);
        nameDataTextView = getView().findViewById(R.id.txt_name_data);
        headlineDataTextView = getView().findViewById(R.id.txt_headline_data);
        calendar = Calendar.getInstance();
        nameTextView = getView().findViewById(R.id.txt_name);
        phoneTextView = getView().findViewById(R.id.txt_phone);
        saveProfileButton = getView().findViewById(R.id.btn_saveprofile);
        Nokri_Utils.setEditBorderButton(getContext(), saveProfileButton);
        emailTextView = getView().findViewById(R.id.txt_email);
        headlineTextView = getView().findViewById(R.id.txt_headline);
        dateTextView = getView().findViewById(R.id.txt_date);
        educationTextView = getView().findViewById(R.id.txt_education);
        statusTextView = getView().findViewById(R.id.txt_status);
        profileImageTextView = getView().findViewById(R.id.txt_profile_image);

        aboutTextView = getView().findViewById(R.id.txt_about);
        container = getView().findViewById(R.id.container1);
        changePasswordTextView = getView().findViewById(R.id.txt_change_password);
        deleteAccountTextView = getView().findViewById(R.id.txt_delete_account);
        changePasswordTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        if (Nokri_SharedPrefManager.isSocialLogin(getContext()))
            changePasswordTextView.setVisibility(View.GONE);

        customFieldsLayout = getActivity().findViewById(R.id.customFieldsLayout);

        nameEditText = getView().findViewById(R.id.edittxt_name);
        phoneEditText = getView().findViewById(R.id.edittxt_phone);
        emailEditText = getView().findViewById(R.id.edittxt_email);
        headlineEditText = getView().findViewById(R.id.edittxt_headline);
        dateEditText = getView().findViewById(R.id.edittxt_date);

        pofileImaeEditText = getView().findViewById(R.id.edittxt_profile_image);

        aboutEditText = getView().findViewById(R.id.edittxt_descripton);

        textarea = getView().findViewById(R.id.textarea);

        aboutEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.rich_text_size));

        aboutEditText.setBackground(getActivity().getResources().getDrawable(R.drawable.rectangle));
        aboutEditText.setTextColor(Color.RED);
        profileImageView = getView().findViewById(R.id.img_profile);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery(1);
            }
        });

        educationEditText = getView().findViewById(R.id.spinner_education);


        boldImageView = getView().findViewById(R.id.img_bold);
        italicImageView = getView().findViewById(R.id.img_italic);
        underlineImageView = getView().findViewById(R.id.img_underline);
        numberBulletsImageView = getView().findViewById(R.id.img_num_bullets);
        listBulletsImageView = getView().findViewById(R.id.img_list_bullets);


        boldImageView.setOnClickListener(this);
        italicImageView.setOnClickListener(this);
        underlineImageView.setOnClickListener(this);
        numberBulletsImageView.setOnClickListener(this);
        listBulletsImageView.setOnClickListener(this);
        changePasswordTextView.setOnClickListener(this);
        deleteAccountTextView.setOnClickListener(this);

        typeSpinner = getView().findViewById(R.id.spinner_type);
        levelSpinner = getView().findViewById(R.id.spinner_level);
        experienceSpinner = getView().findViewById(R.id.spinner_experience);
        typeTextView = getView().findViewById(R.id.txt_type);
        levelTextView = getView().findViewById(R.id.txt_level);
        experienceTextView = getView().findViewById(R.id.txt_experience);
        statusSpinner = getView().findViewById(R.id.spinner_status);
        typeSpinnerIds = new ArrayList<>();
        levelSpinnerIds = new ArrayList<>();
        experienceSpinnerIds = new ArrayList<>();
        statusSpinnerIds = new ArrayList<>();
        aboutEditText.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        aboutEditText.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));


    }

    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(salaryRangeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(genderTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(salaryTypeTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(salaryCurrencyTextView,getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameDataTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(headlineDataTextView, getActivity().getAssets());


        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(phoneTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(emailTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(headlineTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(dateTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(educationTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(levelTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(typeTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(experienceTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(profileImageTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(statusTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(aboutTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(changePasswordTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(deleteAccountTextView, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(nameEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(phoneEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(headlineEditText, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(dateEditText, getActivity().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(pofileImaeEditText, getActivity().getAssets());

        //

        fontManager.nokri_setOpenSenseFontButton(saveProfileButton, getActivity().getAssets());

    }

    private void nokri_setDataFromSharedref() {

        if (Nokri_SharedPrefManager.getProfileImage(getContext()) != null && !Nokri_SharedPrefManager.getProfileImage(getContext()).trim().isEmpty())
            Picasso.get().load(Nokri_SharedPrefManager.getProfileImage(getContext())).into(profileImageView);
        String name = Nokri_SharedPrefManager.getName(getContext());
        if (name != null) {
            nameDataTextView.setText(name);
            nameEditText.setHint(name);
        }
        String headLine = Nokri_SharedPrefManager.getHeadline(getContext());
        if (headLine != null) {
            headlineEditText.setHint(headLine);
            headlineDataTextView.setText(headLine);
        }

        textarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutEditText.focusEditor();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(container.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }

        });
    }


    private void uploadProfileImageRequest(String absolutePath) {
        progressDialolque = new Nokri_UploadProgressDialolque(getContext());
        progressDialolque.showUploadDialogue();


        File file = new File(absolutePath);
        ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("logo_img", file.getName(), requestBody);
        RestService restService = Nokri_ServiceGenerator.createServiceNoTimeout(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        final Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postUploadProfileImage(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddSocial());
        } else {
            myCall = restService.postUploadProfileImage(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        }
        progressDialolque.setCloseClickListener(new Nokri_UploadProgressDialolque.CloseClickListener() {
            @Override
            public void onCloseClick() {
                myCall.cancel();
            }
        });
        //  Call<ResponseBody> myCall  = restService.postUploadProfileImage(fileToUpload, Nokri_RequestHeaderManager.UploadImageAddHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        if (jsonObject.getBoolean("success")) {
                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
                            JSONObject data = jsonObject.getJSONObject("data");
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("logo_img"), getContext());
                            Log.v("uploaded profile", data.getString("logo_img"));
                            if (!TextUtils.isEmpty(data.getString("logo_img")))
                                Picasso.get().load(data.getString("logo_img")).into(profileImageView);
                               /* if(!jsonObject.getBoolean("data")){
                                    Log.v("Cover Upload","failed");
                                }
                                else
                                {

                                }*/
                            if (!TextUtils.isEmpty(data.getString("logo_img"))) {
                                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                View headerView = navigationView.getHeaderView(0);
                                CircularImageView profileImage = headerView.findViewById(R.id.img_profile);

                                Picasso.get().load(data.getString("logo_img")).fit().centerCrop().into(profileImage);
                            }


                            Log.v("Profile Upload", jsonObject.get("data").toString());

                            progressDialolque.handleSuccessScenerion();
                        } else {
                            Log.v("Profile Upload", jsonObject.get("data").toString());
                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
                            progressDialolque.handleFailedScenerio();
                        }
                    } catch (JSONException e) {
                        progressDialolque.handleFailedScenerio(e.getMessage());
                        Log.v("Profile Upload", e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        progressDialolque.handleFailedScenerio();
                        e.printStackTrace();
                        Log.v("Profile Upload", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                progressDialolque.handleFailedScenerio(t.getMessage());
                Log.v("Profile Upload", t.getMessage());
            }
        });
    }


    private void nokri_getCandidatePersonalInfo() throws JSONException {
        JSONObject response = Nokri_CandidateEditProfileFragment.personalInfo;
        if (response.getBoolean("success")) {

            JSONArray extrasArray = response.getJSONArray("extras");


            customFields = response.getJSONArray("custom_fields");

            setCustomFields(customFields);

            for (int i = 0; i < extrasArray.length(); i++) {
                JSONObject extraObject = extrasArray.getJSONObject(i);
                if (extraObject.getString("field_type_name").equals("section_name")) {
                    nameTextView.setText(extraObject.getString("value") + ":");
                } else if (extraObject.getString("field_type_name").equals("btn_name")) {
                    saveProfileButton.setText(extraObject.getString("value"));
                }
                if (extraObject.getString("field_type_name").equals("change_pasword")) {
                    changePasswordTextView.setText(extraObject.getString("value"));

                }
                if (extraObject.getString("field_type_name").equals("del_acount")) {
                    deleteAccountTextView.setText(extraObject.getString("value"));

                }

            }

            JSONArray dataJsonArray = response.getJSONArray("data");
            for (int i = 0; i < dataJsonArray.length(); i++) {
                Nokri_EditTextModel editTextModel;
                JSONObject jsonObject = dataJsonArray.getJSONObject(i);
                if (jsonObject.getString("field_type_name").equals("cand_name")) {
                    nameEditText.setText(jsonObject.getString("value"));
                    nameTextView.setText(jsonObject.getString("key"));
                    nameEditText.setHint(jsonObject.getString("key"));
                    nameDataTextView.setText(jsonObject.getString("value"));
                    Nokri_SharedPrefManager.saveName(jsonObject.getString("value"), getContext());
                    editTextModel = new Nokri_EditTextModel();
                    editTextModel.setEditText(nameEditText);
                    editTextModel.setRequired(jsonObject);
                } else if (jsonObject.getString("field_type_name").equals("cand_phone")) {
                    phoneEditText.setText(jsonObject.getString("value"));
                    phoneTextView.setText(jsonObject.getString("key"));
                    phoneEditText.setHint(jsonObject.getString("key"));

                    editTextModel = new Nokri_EditTextModel();
                    editTextModel.setEditText(phoneEditText);
                    editTextModel.setRequired(jsonObject);

                } else if (jsonObject.getString("field_type_name").equals("")) {
                    emailEditText.setText(jsonObject.getString("value"));
                    emailTextView.setText(jsonObject.getString("key"));
                    emailEditText.setHint(jsonObject.getString("key"));

                    editTextModel = new Nokri_EditTextModel();
                    editTextModel.setEditText(emailEditText);
                    editTextModel.setRequired(jsonObject);
                } else if (jsonObject.getString("field_type_name").equals("cand_headline")) {
                    headlineEditText.setText(jsonObject.getString("value"));
                    headlineDataTextView.setText(jsonObject.getString("value"));
                    headlineTextView.setText(jsonObject.getString("key"));
                    headlineEditText.setHint(jsonObject.getString("key"));

                    editTextModel = new Nokri_EditTextModel();
                    editTextModel.setEditText(headlineEditText);
                    editTextModel.setRequired(jsonObject);
                } else if (jsonObject.getString("field_type_name").equals("cand_dob")) {
                    dateEditText.setText(jsonObject.getString("value"));
                    dateTextView.setText(jsonObject.getString("key"));
                    dateEditText.setHint(jsonObject.getString("key"));

                    editTextModel = new Nokri_EditTextModel();
                    editTextModel.setEditText(dateEditText);
                    editTextModel.setRequired(jsonObject);

                } else if (jsonObject.getString("field_type_name").equals("cand_dp")) {

                    profileImageTextView.setText(jsonObject.getString("key"));
                    pofileImaeEditText.setHint(jsonObject.getString("key"));

                    editTextModel = new Nokri_EditTextModel();
                    editTextModel.setEditText(pofileImaeEditText);
                    editTextModel.setRequired(jsonObject);
                } else if (jsonObject.getString("field_type_name").equals("cand_cvr")) {


                    editTextModel = new Nokri_EditTextModel();

                    editTextModel.setRequired(jsonObject);
                } else if (jsonObject.getString("field_type_name").equals("cand_last")) {
                    int selectedIndex = 0;
                    educationTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();
                    idList = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        idList.add(object.getString("key"));
                    }
                    educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    educationEditText.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_level")) {
                    int selectedIndex = 0;
                    levelTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();

                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        levelSpinnerIds.add(object.getString("key"));
                    }
                    levelSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    levelSpinner.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_gender")) {
                    genderSpinnerIds = new ArrayList<>();
                    int selectedIndex = 0;
                    genderTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();

                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        genderSpinnerIds.add(object.getString("key"));
                    }
                    genderSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    genderSpinner.setSelection(selectedIndex);
                }  else if (jsonObject.getString("field_type_name").equals("cand_type")) {
                    int selectedIndex = 0;
                    typeTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();

                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        typeSpinnerIds.add(object.getString("key"));
                    }
                    typeSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    typeSpinner.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_experience")) {
                    int selectedIndex = 0;
                    experienceTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();

                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        experienceSpinnerIds.add(object.getString("key"));
                    }
                    experienceSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    experienceSpinner.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_salary_range")) {
                    int selectedIndex = 0;
                    salaryRangeTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();
                    salaryRangeIds = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        salaryRangeIds.add(object.getString("key"));
                    }
                    salaryRangeSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    salaryRangeSpinner.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_salary_curren")) {
                    int selectedIndex = 0;
                    salaryCurrencyTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();
                    salaryCurrencyIds = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        salaryCurrencyIds.add(object.getString("key"));
                    }
                    salaryCurrencySpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    salaryCurrencySpinner.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_salary_type")) {
                    int selectedIndex = 0;
                    salaryTypeTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> educationList = new ArrayList<>();
                    salaryTypeIds = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        educationList.add(object.getString("value"));
                        salaryTypeIds.add(object.getString("key"));
                    }
                    salaryTypeSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, educationList));
                    salaryTypeSpinner.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_prof_stat")) {


                    if (!jsonObject.getBoolean("is_required")) {
                        statusTextView.setVisibility(View.GONE);
                        statusSpinner.setVisibility(View.GONE);
                    }


                    int selectedIndex = 0;
                    statusTextView.setText(jsonObject.getString("key"));
                    JSONArray array = jsonObject.getJSONArray("value");
                    ArrayList<String> statusList = new ArrayList<>();

                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        if (object.getBoolean("selected")) {
                            selectedIndex = j;
                        }
                        statusList.add(object.getString("value"));
                        statusSpinnerIds.add(object.getString("key"));
                    }
                    statusSpinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, statusList));
                    statusSpinner.setSelection(selectedIndex);
                } else if (jsonObject.getString("field_type_name").equals("cand_intro")) {
                    aboutEditText.setHtml(jsonObject.getString("value"));
                    aboutEditText.setPlaceholder(jsonObject.getString("key"));

                    aboutTextView.setText(jsonObject.getString("key"));

                                  /*  editTextModel = new Nokri_EditTextModel();
                                    editTextModel.setEditText(nameEditText);
                                    editTextModel.setRequired(jsonObject);*/

                }
            }
        }

    }

    private void nokri_postPersonalInfo(String name, String phone, String headline, String dob, String lastEducation, String type, String level, String intro, String experience, String status,
                                        String salaryRange,String salaryType,String salaryCurrency, String gender) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonArray params = new JsonArray();
        JsonObject customFields = new JsonObject();
        try {
            if (!gatherCustomFieldsData(customFields)) {
                dialogManager.hideAlertDialog();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cand_salary_range",salaryRange);
        jsonObject.addProperty("cand_salary_type",salaryType);
        jsonObject.addProperty("cand_salary_curren",salaryCurrency);
        jsonObject.addProperty("cand_name", name);
        jsonObject.addProperty("cand_phone", phone);
        jsonObject.addProperty("cand_headline", headline);
        jsonObject.addProperty("cand_dob", dob);
        jsonObject.addProperty("cand_last", lastEducation);
        jsonObject.addProperty("cand_type", type);
        jsonObject.addProperty("cand_level", level);
        jsonObject.addProperty("cand_intro", intro);
        jsonObject.addProperty("cand_experience", experience);
        jsonObject.addProperty("cand_prof_stat", status);
        jsonObject.addProperty("cand_gender", gender);
        jsonObject.add("custom_fields", customFields);
        params.add(jsonObject);


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postCandidatePersonalInfo(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postCandidatePersonalInfo(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        // Call<ResponseBody> myCall = restService.postCandidatePersonalInfo(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
//                            nokri_getCandidatePersonalInfo();
                            Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAfterDelay();
                        }

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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_PROFILE && data != null)
            uploadProfileImageRequest(Nokri_PathUtils.getPath(getContext(), data.getData()));
    }


    private void getDataFromEditText() {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String headline = headlineEditText.getText().toString();
        String dateOfBirth = dateEditText.getText().toString();

        String lastEducation = idList.get(educationEditText.getSelectedItemPosition());
        String type = typeSpinnerIds.get(typeSpinner.getSelectedItemPosition());
        String level = levelSpinnerIds.get(levelSpinner.getSelectedItemPosition());
        String gender = genderSpinnerIds.get(genderSpinner.getSelectedItemPosition());
        String about = aboutEditText.getHtml();
        String salaryRange = salaryRangeIds.get(salaryRangeSpinner.getSelectedItemPosition());
        String salaryCurrency = salaryCurrencyIds.get(salaryCurrencySpinner.getSelectedItemPosition());
        String salaryType = salaryTypeIds.get(salaryTypeSpinner.getSelectedItemPosition());
        String experience = experienceSpinnerIds.get(experienceSpinner.getSelectedItemPosition());
        String status = statusSpinnerIds.get(statusSpinner.getSelectedItemPosition());
        Nokri_Utils.checkEditTextForError(nameEditText);
        Nokri_Utils.checkEditTextForError(phoneEditText);
        Nokri_Utils.checkEditTextForError(headlineEditText);
        Nokri_Utils.checkEditTextForError(dateEditText);

        if (aboutEditText != null && !about.isEmpty()) {

            getView().findViewById(R.id.line).setBackgroundColor(getResources().getColor(R.color.gray));
            if (!name.trim().isEmpty() && !phone.trim().isEmpty() && !headline.trim().isEmpty() && !dateOfBirth.trim().isEmpty() && !lastEducation.trim().isEmpty() && !about.trim().isEmpty() && !status.trim().isEmpty()) {
                nokri_postPersonalInfo(name, phone, headline, dateOfBirth, lastEducation, type, level, about, experience, status,salaryRange,salaryType,salaryCurrency,gender);
            } else {
                Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            }
        } else {
            getView().findViewById(R.id.line).setBackgroundColor(Color.RED);
            Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);


        }

    }


    private void selectImageFromGallery(int flag) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_PROFILE);
    }


    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.edittxt_name:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));


                }
                break;
            case R.id.edittxt_phone:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                }
                break;
            case R.id.edittxt_email:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                }
                break;
            case R.id.edittxt_headline:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                }
                break;
            case R.id.edittxt_date:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));

                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));

                    //new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                break;

            case R.id.edittxt_profile_image:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    phoneEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    headlineEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    dateEditText.setHintTextColor(getResources().getColor(R.color.edit_profile_grey));
                    pofileImaeEditText.setHintTextColor(getResources().getColor(R.color.quantum_grey));

                    //  selectImageFromGallery(1);
                }
                break;


            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bold:
                aboutEditText.setBold();
                break;
            case R.id.img_italic:
                aboutEditText.setItalic();
                break;
            case R.id.img_underline:
                aboutEditText.setUnderline();
                break;
            case R.id.img_num_bullets:
                aboutEditText.setNumbers();
                break;
            case R.id.img_list_bullets:
                aboutEditText.setBullets();
                break;
            case R.id.btn_saveprofile:
                getDataFromEditText();
                break;
            case R.id.txt_change_password:
                nokri_getResetPassword();
                break;
            case R.id.txt_delete_account:
                popupManager = new Nokri_PopupManager(getContext(), this);
                popupManager.nokri_showDeletePopup();

                break;

            default:
                break;
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.edittxt_profile_image:

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    selectImageFromGallery(1);

                break;

            case R.id.edittxt_date:
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
        }

        return false;
    }

    @Override
    public void onProgressUpdate(int percentage) {

        progressDialolque.updateProgress(percentage);
    }

    @Override
    public void onError() {
        progressDialolque.handleFailedScenerio();
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onConfirmClick(Dialog dialog) {

        nokri_deleteAccont();
        dialog.dismiss();
    }

    private void nokri_deleteAccont() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();

        params.addProperty("user_id", Nokri_SharedPrefManager.getId(getContext()));


        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.deleteAccount(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.deleteAccount(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getAppliedJobs(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if (responseObject.isSuccessful()) {
                    try {


                        JSONObject response = new JSONObject(responseObject.body().string());
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        Nokri_SharedPrefManager.invalidate(getContext());
                        Intent intent = new Intent(getActivity(), Nokri_GuestDashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);


                    } catch (IOException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (JSONException e) {

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

                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    private void nokri_getResetPassword() {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());


        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getResetPassword(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getResetPassword(Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                        if (jsonObject.getBoolean("success")) {

                            JSONObject data = jsonObject.getJSONObject("data");

                            showDeleteDialog(data.getString("logo"), data.getString("old_password"), data.getString("new_password"), data.getString("confirm_password"), data.getString("ok"), data.getString("cancel"));


                        }

                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();

                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();

            }
        });
    }

    private void showDeleteDialog(String url, String oldPasswordText, String newPasswordText, String confirmPasswodText, String okButtonText, String cancelButtonText) {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_reset_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText oldPasswordEditText = dialog.findViewById(R.id.edittxt_old_password);
        final EditText newPasswordEditText = dialog.findViewById(R.id.edittxt_new_password);
        final EditText confirmPasswordEditText = dialog.findViewById(R.id.edittxt_confirm_password);
        CircularImageView logo = dialog.findViewById(R.id.logo);

        Picasso.get().load(R.mipmap.ic_launcher).into(logo);
        oldPasswordEditText.setHint(oldPasswordText);
        newPasswordEditText.setHint(newPasswordText);
        confirmPasswordEditText.setHint(confirmPasswodText);


        Button okButton = dialog.findViewById(R.id.btn_ok);
        Button cancelButton = dialog.findViewById(R.id.btn_cancel);
        okButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        cancelButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        okButton.setText(okButtonText);
        cancelButton.setText(cancelButtonText);

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setOpenSenseFontEditText(oldPasswordEditText, getContext().getAssets());

        fontManager.nokri_setOpenSenseFontEditText(newPasswordEditText, getContext().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(confirmPasswordEditText, getContext().getAssets());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                nokri_postResetPassword(oldPassword, newPassword, confirmPassword);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

        //  dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int)getContext().getResources().getDimension(R.dimen.reset_password_height));
        dialogManager.hideAlertDialog();
    }


    private void nokri_postResetPassword(String oldPassword, final String newPassword, String confirmPassword) {

        JsonObject params = new JsonObject();
        params.addProperty("old_password", oldPassword);
        params.addProperty("new_password", newPassword);
        params.addProperty("confirm_password", confirmPassword);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postResetPassword(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postResetPassword(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {
                            Nokri_SharedPrefManager.savePassword(newPassword, getContext());


                        }
                        Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
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
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    // Custom Fields Section


    private void setCustomFields(JSONArray customFields) throws JSONException {
        customFieldsLayout.removeAllViews();
        for (int i = 0; i < customFields.length(); i++) {
            String viewType = customFields.getJSONObject(i).getString("field_type");
            if (viewType.equals("textfield")) {
                String hint = customFields.getJSONObject(i).getString("main_title");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                String value = customFields.getJSONObject(i).getString("value");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow)
                    populateEditText(hint, tag, value);
            } else if (viewType.equals("Number")) {
                String hint = customFields.getJSONObject(i).getString("main_title");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                String value = customFields.getJSONObject(i).getString("value");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow)
                    populateNumberedEditText(hint, tag, value);
            } else if (viewType.equals("checkbox")) {
                String heading = customFields.getJSONObject(i).getString("main_title");
                JSONArray radioList = customFields.getJSONObject(i).getJSONArray("value");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow)
                    populateRadioButton(heading, radioList, tag);
            } else if (viewType.equals("select")) {
                String heading = customFields.getJSONObject(i).getString("main_title");
                JSONArray radioList = customFields.getJSONObject(i).getJSONArray("value");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow)
                    populateSpinner(heading, radioList, tag);
            } else if (viewType.equals("date")) {
                String title = customFields.getJSONObject(i).getString("main_title");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                String idName = customFields.getJSONObject(i).getString("field_type_name");
                String fieldValue = customFields.getJSONObject(i).getString("value");
                if (isShow) {
                    populateDateField(idName, title, fieldValue);
                }
            } else if (viewType.equals("Textarea")) {
                String title = customFields.getJSONObject(i).getString("main_title");
                String hint = customFields.getJSONObject(i).getString("main_title");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                String value = customFields.getJSONObject(i).getString("value");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow) {
                    populateTextArea(title, tag, value);
                }

            }

        }
    }

    private void populateTextArea(String title, String tag, String value) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.register_custom_textarea, null);

        TextView heading = view.findViewById(R.id.heading);
        heading.setText(title);
        RichEditor mEditor = view.findViewById(R.id.edittxt_descripton);


        view.findViewById(R.id.img_bold).

                setOnClickListener(v -> mEditor.setBold());

        view.findViewById(R.id.img_italic).

                setOnClickListener(v -> mEditor.setItalic());

        view.findViewById(R.id.img_underline).

                setOnClickListener(v -> mEditor.setUnderline());


        view.findViewById(R.id.img_num_bullets).

                setOnClickListener(v -> mEditor.setBullets());

        view.findViewById(R.id.img_list_bullets).

                setOnClickListener(v -> mEditor.setNumbers());
        mEditor.setEditorFontColor(getResources().getColor(R.color.edit_profile_grey));
        mEditor.setEditorFontSize((int) getResources().getDimension(R.dimen.richeditor_font_size));
        mEditor.setPlaceholder(title);
        mEditor.setHtml("<text>" + value + "</text>");
        view.setTag(tag);
        customFieldsLayout.addView(view);

    }


    public void populateDateField(String idName, String title, String fieldValue) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_profile_date, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        EditText dateField = view.findViewById(R.id.edittext);
        TextView heading = view.findViewById(R.id.heading);
        heading.setText(title);
        dateField.setHint(title);
        dateField.setText(fieldValue);
        dateField.setTag(idName);
        dateField.setFocusable(false);
        dateField.setFocusableInTouchMode(false);
        dateField.setOnClickListener(v -> adforest_showDate(dateField));
        view.setTag(idName);
        customFieldsLayout.addView(view);
    }

    Calendar myCalendar = Calendar.getInstance();

    private void adforest_showDate(final EditText editText) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
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

    ModelsPostJobStep2.SpinnerArrayListStorageModel model;
    ArrayList<ModelsPostJobStep2.SpinnerArrayListStorageModel> spinnerModelsList = new ArrayList<>();

    private void populateSpinner(String heading, JSONArray jsonArray, String tag) throws JSONException {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_profile_spinner, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        TextView headingView = view.findViewById(R.id.heading);
        headingView.setText(heading);
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayList<Nokri_SpinnerModel> spinnerListData = new ArrayList<>();
        ArrayList<String> spinnerList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Nokri_SpinnerModel model = new Nokri_SpinnerModel();
            try {
                model.setValue(jsonArray.getJSONObject(i).getString("value"));
                try {
                    boolean selectedOption = jsonArray.getJSONObject(i).getBoolean("selected");
                    model.setSelected(selectedOption);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model.setName(jsonArray.getJSONObject(i).getString("name"));
                spinnerList.add(model.getName());
                spinnerListData.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        model = new ModelsPostJobStep2.SpinnerArrayListStorageModel();
        model.id = tag;
        model.spinnerList.addAll(spinnerListData);
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item_popup, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);
        for (int i = 0; i < spinnerListData.size(); i++) {
            if (spinnerListData.get(i).getSelected()) {
                spinner.setSelection(i);
            }
        }
        view.setTag(tag);
        spinnerModelsList.add(model);
        customFieldsLayout.addView(view);

    }

    @SuppressLint("NewApi")
    private void populateRadioButton(String heading, JSONArray radioList, String tag) throws JSONException {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.register_custom_radio, null);
        TextView headingView = view.findViewById(R.id.heading);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        headingView.setText(heading);
        for (int i = 0; i < radioList.length(); i++) {
            String name = radioList.getJSONObject(i).getString("name");
            String tagButton = radioList.getJSONObject(i).getString("value");
            boolean selected = radioList.getJSONObject(i).getBoolean("selected");

            RadioButton radioButton = new RadioButton(getActivity());
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[]{Color.parseColor(Nokri_Config.APP_COLOR)}
            );
            radioButton.setButtonTintList(colorStateList);
            radioButton.setText(name);
            radioButton.setTextColor(Color.parseColor("#808080"));
            radioButton.setTag(tagButton);
            radioGroup.addView(radioButton);
            if (selected)
                radioButton.setChecked(true);
        }
        view.setTag(tag);
        customFieldsLayout.addView(view);
    }

    private void populateNumberedEditText(String hint, String tag, String value) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_profile_edittext, null);
        EditText editText = view.findViewById(R.id.edittext);
        TextView textView = view.findViewById(R.id.heading);
        textView.setText(hint);
        editText.setHint(hint);
        editText.setText(value);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        ;
        view.setTag(tag);
        customFieldsLayout.addView(view);
    }

    private void populateEditText(String hint, String tag, String value) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_profile_edittext, null);
        EditText editText = view.findViewById(R.id.edittext);
        TextView textView = view.findViewById(R.id.heading);
        textView.setText(hint);
        editText.setHint(hint);
        editText.setText(value);
        view.setTag(tag);
        customFieldsLayout.addView(view);
    }


    private boolean gatherCustomFieldsData(JsonObject params) throws JSONException {
        if (customFields != null && customFields.length() != 0) {
            for (int i = 0; i < customFields.length(); i++) {
                String viewType = customFields.getJSONObject(i).getString("field_type");
                boolean required = customFields.getJSONObject(i).getBoolean("is_required");
                if (viewType.equals("checkbox")) {
                    String viewTag = customFields.getJSONObject(i).getString("field_type_name");
                    for (int j = 0; j < customFieldsLayout.getChildCount(); j++) {
                        if (customFieldsLayout.getChildAt(j).getTag().equals(viewTag)) {
                            LinearLayout linearLayout = (LinearLayout) customFieldsLayout.getChildAt(j);
                            RadioGroup radioGroup = (RadioGroup) linearLayout.getChildAt(1);
                            if (radioGroup.getCheckedRadioButtonId() != -1) {
                                RadioButton radioButton = getActivity().findViewById(radioGroup.getCheckedRadioButtonId());
                                params.addProperty(viewTag, (String) radioButton.getTag());
                            } else {
                                if (required) {
                                    Toast.makeText(getActivity(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER, Toast.LENGTH_SHORT).show();
                                    dialogManager.hideAlertDialog();
                                    return false;
                                }
                            }
                        }
                    }
                } else if (viewType.equals("select")) {
                    String viewTag = customFields.getJSONObject(i).getString("field_type_name");
                    for (int j = 0; j < customFieldsLayout.getChildCount(); j++) {
                        if (customFieldsLayout.getChildAt(j).getTag().equals(viewTag)) {
                            LinearLayout linearLayout = (LinearLayout) customFieldsLayout.getChildAt(j);
                            Spinner spinner = (Spinner) linearLayout.getChildAt(1);
                            for (int k = 0; k < spinnerModelsList.size(); k++) {
                                ModelsPostJobStep2.SpinnerArrayListStorageModel model = spinnerModelsList.get(k);
                                if (model.id.equals(viewTag)) {
                                    ArrayList<Nokri_SpinnerModel> spinnerModel = model.spinnerList;
                                    for (int l = 0; l < spinnerModel.size(); l++) {
                                        if (spinnerModel.get(l).getName().equals(spinner.getSelectedItem().toString())) {
                                            params.addProperty(viewTag, spinnerModel.get(l).getValue());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (viewType.equals("textfield")) {
                    String viewTag = customFields.getJSONObject(i).getString("field_type_name");
                    for (int j = 0; j < customFieldsLayout.getChildCount(); j++) {
                        if (customFieldsLayout.getChildAt(j).getTag().equals(viewTag)) {
                            LinearLayout linearLayout = (LinearLayout) customFieldsLayout.getChildAt(j);
                            EditText editText = (EditText) linearLayout.getChildAt(1);
                            if (!editText.getText().toString().equals("")) {
                                params.addProperty(viewTag, editText.getText().toString());
                            } else {
                                if (required) {
                                    editText.setError("");
                                    editText.requestFocus();
                                    return false;
                                }
                            }
                        }
                    }
                } else if (viewType.equals("Number")) {
                    String viewTag = customFields.getJSONObject(i).getString("field_type_name");
                    for (int j = 0; j < customFieldsLayout.getChildCount(); j++) {
                        if (customFieldsLayout.getChildAt(j).getTag().equals(viewTag)) {
                            LinearLayout linearLayout = (LinearLayout) customFieldsLayout.getChildAt(j);
                            EditText editText = (EditText) linearLayout.getChildAt(1);
                            if (!editText.getText().toString().equals("")) {
                                params.addProperty(viewTag, editText.getText().toString());
                            } else {
                                if (required) {
                                    editText.setError("");
                                    editText.requestFocus();
                                    return false;
                                }
                            }
                        }
                    }
                } else if (viewType.equals("date")) {
                    String viewTag = customFields.getJSONObject(i).getString("field_type_name");
                    for (int j = 0; j < customFieldsLayout.getChildCount(); j++) {
                        if (customFieldsLayout.getChildAt(j).getTag().equals(viewTag)) {
                            LinearLayout linearLayout = (LinearLayout) customFieldsLayout.getChildAt(j);
                            EditText editText = (EditText) linearLayout.getChildAt(1);
                            if (!editText.getText().toString().equals("")) {
                                params.addProperty(viewTag, editText.getText().toString());
                            } else {
                                if (required) {
                                    editText.setError("");
                                    editText.requestFocus();
                                    return false;
                                }
                            }
                        }
                    }
                } else if (viewType.equalsIgnoreCase("Textarea")) {
                    String viewTag = customFields.getJSONObject(i).getString("field_type_name");
                    for (int j = 0; j < customFieldsLayout.getChildCount(); j++) {
                        if (customFieldsLayout.getChildAt(j).getTag().equals(viewTag)) {
                            LinearLayout linearLayout = (LinearLayout) customFieldsLayout.getChildAt(j);
                            RichEditor editText = (RichEditor) ((LinearLayout) linearLayout.getChildAt(2)).getChildAt(0);
                            if (editText.getHtml() == null)
                                editText.setHtml("");
                            if (!editText.getHtml().equals("")) {
                                params.addProperty(viewTag, editText.getHtml());
                            } else {
                                if (required) {
                                    Toast.makeText(getActivity(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER, Toast.LENGTH_SHORT).show();
                                    editText.requestFocus();
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}

