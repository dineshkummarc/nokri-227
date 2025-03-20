package com.scriptsbundle.nokri.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.scriptsbundle.nokri.employeer.dashboard.Nokri_EmployerDashboardActivity;
import com.scriptsbundle.nokri.employeer.jobs.models.ModelsPostJobStep2;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.CustomEditText;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_LanguageSupport;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.scriptsbundle.nokri.R;

import com.scriptsbundle.nokri.utils.PasswordStrength;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import jp.wasabeef.richeditor.RichEditor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class Nokri_SignupActivity extends AppCompatActivity implements View.OnFocusChangeListener, BaseToggleSwitch.OnToggleSwitchChangeListener, View.OnClickListener {

    TextView singUpTextView, alreadyTextView, signInTextView, termsTextView;
    Nokri_FontManager fontManager;
    Button facebookButton, googleButton, signupButton;
    EditText nameEditText, emailEditText;
    CustomEditText confirmPasswordEditText, passwordEditText;
    ImageView logoImageView;
    ImageView showPassword, showPasswordConfirm;
    View view;
    ToggleSwitch toogleSwitch;
    AppCompatCheckBox checkBox;
    GoogleSignInClient mGoogleSignInClient;
    static final int GOOLGE_STATUS_CODE = 999;
    ArrayList<String> list = new ArrayList();
    CallbackManager callbackManager;
    LoginManager loginManager;
    String[] accountTypes = new String[2];
    String selectedOption = "";
    Nokri_DialogManager dialogManager;
    String termsUrl;
    LinearLayout customFieldsLayout;
    JSONArray customFields;
    ModelsPostJobStep2.SpinnerArrayListStorageModel model;
    boolean isPasswordStrong = false;
    boolean requiredStrongPassword = false;
    String passwordStrengthText;
    LinearLayout newsLetterLayout;
    AppCompatCheckBox newsCheck;
    TextView textNewsLetter;
    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Nokri_Utils.changeSystemBarColor(this);
        setContentView(R.layout.activity_nokri_signup);
        fontManager = new Nokri_FontManager();

        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));

        singUpTextView = findViewById(R.id.txt_signup);
        alreadyTextView = findViewById(R.id.txt_already);
        signInTextView = findViewById(R.id.txt_signin);
        signInTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        facebookButton = findViewById(R.id.btn_facebook);
        signupButton = findViewById(R.id.btn_singup);
        googleButton = findViewById(R.id.btn_google);
        view = findViewById(R.id.viw1);
        view.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        logoImageView = findViewById(R.id.img_logo);
        customFieldsLayout = findViewById(R.id.customFieldsLayout);


        toogleSwitch = findViewById(R.id.toogle_switch);
        toogleSwitch.setActiveBgColor(Color.parseColor(Nokri_Config.APP_COLOR));
        termsTextView = findViewById(R.id.txt_terms);


        fontManager.nokri_setMonesrratSemiBioldFont(singUpTextView, getAssets());
        fontManager.nokri_setOpenSenseFontTextView(termsTextView, getAssets());
        fontManager.nokri_setOpenSenseFontTextView(alreadyTextView, getAssets());
        fontManager.nokri_setOpenSenseFontTextView(signInTextView, getAssets());
        fontManager.nokri_setOpenSenseFontButton(facebookButton, getAssets());
        fontManager.nokri_setOpenSenseFontButton(googleButton, getAssets());
        fontManager.nokri_setOpenSenseFontButton(signupButton, getAssets());


        nameEditText = findViewById(R.id.edittxt_name);
        emailEditText = findViewById(R.id.edittxt_email);
        passwordEditText = findViewById(R.id.edittxt_password);
        confirmPasswordEditText = findViewById(R.id.edittxt_confirm_password);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePasswordStrengthView(charSequence.toString(), true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePasswordStrengthView(charSequence.toString(), false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        showPassword = findViewById(R.id.showPassword);
        showPasswordConfirm = findViewById(R.id.showPasswordConfirm);
        confirmPasswordEditText.setHint(Nokri_SharedPrefManager.getSettings(this).getConfirmPasswordString());


        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.getInputType() == 129) {
                    fontManager.nokri_setOpenSenseFontEditText(passwordEditText, getAssets());
                    showPassword.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    showPassword.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.gray_very_light), PorterDuff.Mode.SRC_IN));
                    fontManager.nokri_setOpenSenseFontEditText(passwordEditText, getAssets());
                    passwordEditText.setInputType(129);
                }
            }
        });

        showPasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmPasswordEditText.getInputType() == 129) {
                    fontManager.nokri_setOpenSenseFontEditText(confirmPasswordEditText, getAssets());
                    showPasswordConfirm.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    showPasswordConfirm.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.gray_very_light), PorterDuff.Mode.SRC_IN));
                    fontManager.nokri_setOpenSenseFontEditText(confirmPasswordEditText, getAssets());
                    confirmPasswordEditText.setInputType(129);
                }
            }
        });


        checkBox = findViewById(R.id.checkbox);
        newsCheck = findViewById(R.id.newsCheckbox);
        newsLetterLayout = findViewById(R.id.newsLetterLayout);
        textNewsLetter = findViewById(R.id.textNewsLetter);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[]{

                        Color.RED //disabled
                        , Color.parseColor(Nokri_Config.APP_COLOR) //enabled

                }
        );

        checkBox.setSupportButtonTintList(colorStateList);
        newsCheck.setSupportButtonTintList(colorStateList);

        fontManager.nokri_setOpenSenseFontEditText(nameEditText, getAssets());
        fontManager.nokri_setOpenSenseFontEditText(passwordEditText, getAssets());
        fontManager.nokri_setOpenSenseFontEditText(confirmPasswordEditText, getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText, getAssets());


        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        toogleSwitch.setOnToggleSwitchChangeListener(this);

        signupButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        termsTextView.setOnClickListener(this);

        Nokri_Utils.setRoundButtonColor(this, signupButton);
        makeCall();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            // Nokri_ToastManager.showLongToast(Nokri_SigninActivity.this,"called");
//                           Log.d("facebook", String.valueOf(object.getJSONArray("public_profile")));
                            nokri_postSocialSignin(object.getString("email"), "social");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.v("facebook", error.getMessage() + error.getCause());
            }
        });

    }

    private void updatePasswordStrengthView(String password, boolean confirm) {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);


        PasswordStrength str = PasswordStrength.calculateStrength(password);
        Drawable background;
        if (confirm)
            background = findViewById(R.id.passwordLayout).getBackground();
        else
            background = findViewById(R.id.cPasswordLayout).getBackground();

        Context mContext = this;
        GradientDrawable gD = (GradientDrawable) background;
        gD.setStroke(4, str.getColor());

        if (str.getText(this).equals("Strong")) {
            isPasswordStrong = true;
        } else {
            isPasswordStrong = false;
        }
    }

    public void nokri_onClickSignIn(View view) {
        startActivity(new Intent(Nokri_SignupActivity.this, Nokri_SigninActivity.class));
        finish();
    }

    public void nokri_onClickBack(View view) {
        onBackPressed();
    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()) {
            case R.id.edittxt_name:
                if (selected) {

                    nameEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    passwordEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_email:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    passwordEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_password:
                if (selected) {
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    passwordEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                }
                break;
        }
    }

    private void makeCall() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.getRegisterSettings();
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        if (respone.getBoolean("success")) {

                            JSONObject data = respone.getJSONObject("data");
                            Log.d("info", data.getString("bg_color"));
                            //String backgroud = data.getString("bg_color");
                            // Toast.makeText(Nokri_SignupActivity.this,backgroud,Toast.LENGTH_LONG).show();
                            if (!TextUtils.isEmpty(data.getString("logo")))
                                Picasso.get().load(data.getString("logo")).into(logoImageView);

                            // container.setBackgroundColor(Color.parseColor(data.getString("bg_color")));

                            nameEditText.setHint(data.getString("name_placeholder"));
                            emailEditText.setHint(data.getString("email_placeholder"));
                            passwordEditText.setHint(data.getString("password_placeholder"));
                            signupButton.setText(data.getString("form_btn"));
                            facebookButton.setText(data.getString("facebook_btn"));
                            googleButton.setText(data.getString("google_btn"));
                            String loginText = data.getString("login_text");
                            alreadyTextView.setText(loginText.substring(0, loginText.indexOf('?')));
                            signInTextView.setText(loginText.substring(loginText.indexOf('?'), loginText.length()));
                            list.add(data.getString("switch_cand"));
                            list.add(data.getString("switch_emp"));
                            if (data.getString("is_password_strength").equals("1")) {
                                requiredStrongPassword = true;
                                passwordStrengthText = data.getString("password_strength_text");
                            }
                            if (data.getString("is_newsletter").equals("1")){
                                newsLetterLayout.setVisibility(View.VISIBLE);
                                textNewsLetter.setText(data.getString("newsletter-text"));
                            }else{
                                newsLetterLayout.setVisibility(View.GONE);
                            }
                            String onlyAdminCanPost = data.getString("is_admin_post");
                            if (onlyAdminCanPost.equals("1")) {
                                toogleSwitch.setVisibility(View.GONE);
                            } else {
                                toogleSwitch.setVisibility(View.VISIBLE);
                            }
                            termsTextView.setText(data.getString("terms_text"));
                            termsUrl = data.getString("terms_link");
//                            termsTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            toogleSwitch.setLabels(list);
                            try {
                                customFields = data.getJSONArray("custom_fields");
                                if (customFields != null && customFields.length() != 0) {
                                    setCustomFields(customFields);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else {
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                dialogManager.hideAlertDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, t.getMessage());
            }

        });
    }

    private void setCustomFields(JSONArray customFields) throws JSONException {
        for (int i = 0; i < customFields.length(); i++) {
            String viewType = customFields.getJSONObject(i).getString("field_type");
            if (viewType.equals("textfield")) {
                String hint = customFields.getJSONObject(i).getString("main_title");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow)
                    populateEditText(hint, tag);
            } else if (viewType.equals("Number")) {
                String hint = customFields.getJSONObject(i).getString("main_title");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow)
                    populateNumberedEditText(hint, tag);
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
                String fieldValue = customFields.getJSONObject(i).getString("field_val");
                if (isShow) {
                    populateDateField(idName, title, fieldValue);
                }
            } else if (viewType.equals("Textarea")) {
                String title = customFields.getJSONObject(i).getString("main_title");
                String hint = customFields.getJSONObject(i).getString("main_title");
                String tag = customFields.getJSONObject(i).getString("field_type_name");
                boolean isShow;
                try {
                    isShow = customFields.getJSONObject(i).getBoolean("is_show");
                } catch (Exception e) {
                    e.printStackTrace();
                    isShow = true;
                }
                if (isShow) {
                    populateTextArea(title, tag);
                }

            }

        }
    }

    private void populateTextArea(String title, String tag) {
        View view = LayoutInflater.from(this).inflate(R.layout.register_custom_textarea, null);

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
        view.setTag(tag);
        customFieldsLayout.addView(view);

    }


    public void populateDateField(String idName, String title, String fieldValue) {
        View view = LayoutInflater.from(this).inflate(R.layout.register_custom_date, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        params.bottomMargin = 10;
        view.setLayoutParams(params);
        EditText dateField = view.findViewById(R.id.editText);
        dateField.setHint(title);
        dateField.setTag(idName);
        dateField.setFocusable(false);
        dateField.setFocusableInTouchMode(false);
        dateField.setOnClickListener(v -> adforest_showDate(dateField));
        view.setTag(idName);
        customFieldsLayout.addView(view);
    }

    Calendar myCalendar = Calendar.getInstance();

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


    ArrayList<ModelsPostJobStep2.SpinnerArrayListStorageModel> spinnerModelsList = new ArrayList<>();

    private void populateSpinner(String heading, JSONArray jsonArray, String tag) throws JSONException {

        View view = LayoutInflater.from(this).inflate(R.layout.register_custom_spinner, null);
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
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spinner_item, spinnerList);
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
        View view = LayoutInflater.from(this).inflate(R.layout.register_custom_radio, null);
        TextView headingView = view.findViewById(R.id.heading);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        headingView.setText(heading);
        for (int i = 0; i < radioList.length(); i++) {
            String name = radioList.getJSONObject(i).getString("name");
            String tagButton = radioList.getJSONObject(i).getString("value");
            RadioButton radioButton = new RadioButton(this);
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
        }
        view.setTag(tag);
        customFieldsLayout.addView(view);
    }

    private void populateNumberedEditText(String hint, String tag) {
        View view = LayoutInflater.from(this).inflate(R.layout.register_custom_edittext, null);
        EditText editText = view.findViewById(R.id.edittext);
        editText.setHint(hint);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        ;
        view.setTag(tag);
        customFieldsLayout.addView(view);
    }

    private void populateEditText(String hint, String tag) {
        View view = LayoutInflater.from(this).inflate(R.layout.register_custom_edittext, null);
        EditText editText = view.findViewById(R.id.edittext);
        editText.setHint(hint);
        view.setTag(tag);
        customFieldsLayout.addView(view);
    }


    private void nokri_postSignup(String name, final String email, final String password, final int type) {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        JsonObject customFields = new JsonObject();
        try {

            if (!gatherCustomFieldsData(customFields)) {
                dialogManager.hideAlertDialog();
                return;
            }
        } catch (Exception e) {
            dialogManager.hideAlertDialog();
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        params.add("custom_fields", customFields);
        params.addProperty("name", name);
        params.addProperty("email", email);
        params.addProperty("pass", password);
        params.addProperty("type", type);
        if (newsCheck.isChecked()){
            params.addProperty("subscribe_now","on");
        }else{
            params.addProperty("subscribe_now","off");
        }
        if (requiredStrongPassword && !isPasswordStrong) {
            dialogManager.hideAlertDialog();
            dialogManager.showCustom(passwordStrengthText);
            return;
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.postRegister(params);
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                if (responseObj.isSuccessful()) {

                    try {
                        JSONObject respone = new JSONObject(responseObj.body().string());
                        if (respone.getBoolean("success")) {
                            nokri_postSignin(email, password, type);
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));

                        } else {
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));
                            dialogManager.hideAlertDialog();
                        }
                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(Nokri_SignupActivity.this, e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showShortToast(Nokri_SignupActivity.this, e.getMessage());

                        e.printStackTrace();
                    }
                }

                //    Nokri_DialogManager.hideAlertDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("info", call.toString());
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showShortToast(Nokri_SignupActivity.this, t.getMessage());

/*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    },2500);
*/

                Toast.makeText(Nokri_SignupActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }


        });
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
                                RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                                params.addProperty(viewTag, (String) radioButton.getTag());
                            } else {
                                if (required) {
                                    Toast.makeText(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER, Toast.LENGTH_SHORT).show();
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
                            EditText editText = (EditText) linearLayout.getChildAt(0);
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
                            EditText editText = (EditText) linearLayout.getChildAt(0);
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
                            EditText editText = (EditText) linearLayout.getChildAt(0);
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
                                    Toast.makeText(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER, Toast.LENGTH_SHORT).show();
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

    private void nokri_postSignin(String email, final String password, final int type) {


        JsonObject params = new JsonObject();
        params.addProperty("email", email);
        params.addProperty("pass", password);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, password, Nokri_SignupActivity.this);
        Call<ResponseBody> myCall = restService.postLogin(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("response", respone.toString());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");
                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.savePassword(password, Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.saveId(data.getString("id"), Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), Nokri_SignupActivity.this);
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), Nokri_SignupActivity.this);

                            if (type == 0) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (type == 1) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_EmployerDashboardActivity.class));
                                finish();

                            }
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));

                            dialogManager.hideAlertDialog();
                        } else {
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));


                        }
                    } catch (IOException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, e.getMessage());

                        e.printStackTrace();
                    } catch (JSONException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, e.getMessage());

                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, t.getMessage());

            }
        });
    }


    @Override
    public void onToggleSwitchChangeListener(int position, boolean isChecked) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_singup:
                if (checkBox.isChecked()) {
                    String name = null, email = null, phone = null, password = null;
                    int type;
                    if (!nameEditText.getText().toString().trim().isEmpty())
                        name = nameEditText.getText().toString();
                    if (!emailEditText.getText().toString().trim().isEmpty()) {
                        email = emailEditText.getText().toString();
                    }
                    if (!passwordEditText.getText().toString().trim().isEmpty())
                        password = passwordEditText.getText().toString();
                    type = toogleSwitch.getCheckedTogglePosition();

                    Nokri_Utils.checkEditTextForError(nameEditText);
                    Nokri_Utils.checkEditTextForError(emailEditText);
                    Nokri_Utils.checkEditTextForError(passwordEditText);

                    if (name != null && email != null && password != null) {
                        if (!confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                            Toast.makeText(this, Nokri_SharedPrefManager.getSettings(this).getPasswordMismatchString(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (Nokri_Utils.isValidEmail(email))
                            nokri_postSignup(name, email, password, type);
                        else {
                            Nokri_ToastManager.showShortToast(this, Nokri_Globals.INVALID_EMAIL);
                            emailEditText.setError("!");
                        }
                    } else
                        Nokri_ToastManager.showShortToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);


                } else
                    Nokri_ToastManager.showShortToast(this, Nokri_Globals.TERMS_AND_SERVICES);
                break;

            case R.id.btn_google:
                signIn();
                break;
            case R.id.btn_facebook:
                loginManager.logInWithReadPermissions(Nokri_SignupActivity.this, Arrays.asList("email", "public_profile"));
                break;
            case R.id.txt_terms:
                Nokri_Utils.opeInBrowser(Nokri_SignupActivity.this, termsUrl);
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOLGE_STATUS_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOLGE_STATUS_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            nokri_postSocialSignin(account.getEmail(), "social");
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void nokri_postSocialSignin(String email, final String type) {
        Log.d("socail", email + type);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("type", type);
        params.addProperty("email", email);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, email, this);
//        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.postSocialLogin(params, Nokri_RequestHeaderManager.addSocialHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("socail", respone.toString());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");

                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), getBaseContext());
                            Nokri_SharedPrefManager.saveLoginType(type, getBaseContext());
                            Nokri_SharedPrefManager.savePassword("pass", getBaseContext());

                            Nokri_SharedPrefManager.saveId(data.getString("id"), getBaseContext());
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), getBaseContext());
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), getBaseContext());
                            Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), getBaseContext());

                            // Default Sigin in type for testing

//---------------------------------------------------------------------
                            if (data.getString("acount_type").equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (data.getString("acount_type").equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_EmployerDashboardActivity.class));

                                finish();
                            } else {

                                nokri_showAccountTypePopup();
                            }
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));
                            dialogManager.hideAlertDialog();
                        } else {
                            Log.d("socail", responseObject.toString() + "error");
                            dialogManager.showError();
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));
                        }
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
//                        Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        //  Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, t.getMessage());
            }
        });
    }

    private void nokri_showAccountTypePopup() {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.getAccoutTypeSelector(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if (responseObject.isSuccessful()) {
                    try {
                        Log.d("responce", responseObject.toString());

                        JSONObject respone = new JSONObject(responseObject.body().string());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");


                            ArrayList<String> list = new ArrayList();


                            final Dialog dialog = new Dialog(Nokri_SignupActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.account_type_popup);
                            dialog.findViewById(R.id.title_text_container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            final TextView titleTextView = dialog.findViewById(R.id.txt_title);
                            TextView headingTextView = dialog.findViewById(R.id.txt_heading);
                            final ToggleSwitch toggleSwitch = dialog.findViewById(R.id.toogle_switch);
                            toggleSwitch.setActiveBgColor(Color.parseColor(Nokri_Config.APP_COLOR));
                            list.add(data.getString("btn_cand"));
                            list.add(data.getString("btn_emp"));
                            headingTextView.setText(data.getString("desc"));
                            toggleSwitch.setLabels(list);
                            Button okButton = dialog.findViewById(R.id.btn_ok);
                            okButton.setText(data.getString("continue"));
                            Nokri_Utils.setRoundButtonColor(Nokri_SignupActivity.this, okButton);
                            accountTypes[0] = data.getString("btn_cand");
                            accountTypes[1] = data.getString("btn_emp");
                            titleTextView.setText(accountTypes[0]);
                            Nokri_FontManager innerFontManager = new Nokri_FontManager();
                            innerFontManager.nokri_setMonesrratSemiBioldFont(titleTextView, getAssets());
                            innerFontManager.nokri_setOpenSenseFontTextView(headingTextView, getAssets());
                            innerFontManager.nokri_setOpenSenseFontButton(okButton, getAssets());

                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    dialogManager.hideAlertDialog();
                                }
                            });
                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedOption = toggleSwitch.getCheckedTogglePosition() + "";
                                    nokri_postAccountTypePopup(selectedOption);
                                    dialog.dismiss();

                                }
                            });
                            toggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
                                @Override
                                public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                                    switch (position) {
                                        case 0:
                                            titleTextView.setText(accountTypes[position]);
                                            break;
                                        case 1:
                                            titleTextView.setText(accountTypes[position]);
                                            break;

                                    }
                                }
                            });
                            dialog.show();


                        } else {
                            Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, respone.getString("message"));
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                dialogManager.hideAlertDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  dialog.showError();
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, t.getMessage());
            }
        });
    }

    private void nokri_postAccountTypePopup(final String type) {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("user_type", type);
        params.addProperty("user_id", Nokri_SharedPrefManager.getId(this));
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), "password", Nokri_SignupActivity.this);
        Call<ResponseBody> myCall = restService.postAccountTypleSelector(params, Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, @NonNull Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject respone = new JSONObject(responseObject.body().string());
                        Log.d("response", respone.toString());
                        if (respone.getBoolean("success")) {

                            if (type.equals("0")) {
                                Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (type.equals("1")) {
                                Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SignupActivity.this);
                                startActivity(new Intent(Nokri_SignupActivity.this, Nokri_EmployerDashboardActivity.class));

                                finish();
                            }
                            dialogManager.hideAfterDelay();
                        } else {
                            Log.d("response", responseObject.toString() + "error");
                            dialogManager.showCustom(respone.getString("message"));
                            dialogManager.hideAlertDialog();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(Nokri_SignupActivity.this, t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, Nokri_GuestDashboardActivity.class));
        finish();
    }
}
