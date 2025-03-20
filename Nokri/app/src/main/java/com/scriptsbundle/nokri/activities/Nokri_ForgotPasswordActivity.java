package com.scriptsbundle.nokri.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.rest.RestService;

import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_LanguageSupport;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nokri_ForgotPasswordActivity extends AppCompatActivity implements View.OnFocusChangeListener,View.OnClickListener{
    private TextView forgotPasswordTextView,alreadyTextView,signInTextView;
    private Button submitButton;
    private Nokri_FontManager fontManager;
    private EditText emailEditText;
    private View view;
    private ImageView logoImageView,ImageViewBack;
    private Nokri_DialogManager dialogManager;
    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_forgot_password);
        fontManager = new Nokri_FontManager();
        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));
        forgotPasswordTextView = findViewById(R.id.txt_forgotpassword);
        alreadyTextView = findViewById(R.id.txt_already);
        signInTextView = findViewById(R.id.txt_signin);
        submitButton = findViewById(R.id.btn_submit);
        logoImageView = findViewById(R.id.img_logo);
        ImageViewBack = findViewById(R.id.img_back);
//        ImageViewBack.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        Nokri_Utils.setRoundButtonColor(this,ImageViewBack);

        emailEditText = findViewById(R.id.edittxt_email);
        view=findViewById(R.id.viw1);
        view.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        fontManager.nokri_setMonesrratSemiBioldFont(forgotPasswordTextView,getAssets());

        fontManager.nokri_setOpenSenseFontTextView(alreadyTextView,getAssets());
        fontManager.nokri_setOpenSenseFontTextView(signInTextView,getAssets());
        fontManager.nokri_setOpenSenseFontButton(submitButton,getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText,getAssets());

        emailEditText.setOnFocusChangeListener(this);
        submitButton.setOnClickListener(this);
        ImageViewBack.setOnClickListener(this::ImageViewBack);
        nokri_getForgotPassword();
        nokri_setColor();
    }
    private void nokri_setColor(){
        signInTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        Nokri_Utils.setRoundButtonColor(this,submitButton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
    };

    private void nokri_getForgotPassword() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.getForgotPassword();
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if (responseObject.isSuccessful()) {
                    try {
                        Log.d("responce", responseObject.toString());

                        JSONObject respone = new JSONObject(responseObject.body().string());
                        if (respone.getBoolean("success")) {
                            JSONObject data = respone.getJSONObject("data");
                            if(!TextUtils.isEmpty(data.getString("logo")))
                            Picasso.get().load(data.getString("logo")).into(logoImageView);

                            emailEditText.setHint(data.getString("email_placeholder"));
                            submitButton.setText(data.getString("submit_text"));
                            forgotPasswordTextView.setText(data.getString("heading"));
                      //      alreadyTextView.setText(data.getString("already"));
                        //    signInTextView.setText(data.getString("signin"));


                        }
                    } catch (JSONException e) {

                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //    dialog.dismiss();
                dialogManager.hideAlertDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //  dialog.showError();
                dialogManager.showError();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 2500);
            }
        });
    }

    public void nokri_onClickSignIn(View view){
        startActivity(new Intent(Nokri_ForgotPasswordActivity.this,Nokri_SignupActivity.class));
        finish();
    }

    public void ImageViewBack(View view){
        ImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

    }

    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()){
            case R.id.edittxt_email:
                if(selected)
                {emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));

                }
                else
                {
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, Nokri_GuestDashboardActivity.class));
        finish();
    }

    private void nokri_postResetPassword(String email){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("email",email);

        Log.d("info sendChangePassword",params.toString());

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall = restService.postForgotPassword(params, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        String responseString = response.body().string();
                        JSONObject responseObject = new JSONObject(responseString);
                        if(responseObject.getBoolean("success"))
                        {


                        }
                        Nokri_ToastManager.showShortToast(Nokri_ForgotPasswordActivity.this,responseObject.getString("message"));
                        dialogManager.hideAlertDialog();


                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
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

    @Override
    public void onClick(View v) {

        if(emailEditText.getText().toString().trim().isEmpty()) {
            Nokri_ToastManager.showShortToast(this, Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
            Nokri_Utils.checkEditTextForError(emailEditText);
        }
        else
            if(!Nokri_Utils.isValidEmail(emailEditText.getText().toString())) {
                Nokri_ToastManager.showShortToast(this, Nokri_Globals.INVALID_EMAIL);
                emailEditText.setError("!");
            }
    else{
                nokri_postResetPassword(emailEditText.getText().toString());
            }


    }
}
