package com.scriptsbundle.nokri.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.utils.Nokri_Config;
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

public class Nokri_MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signInButton,signUpButton;
    private Nokri_FontManager fontManager;
    private ImageView logo;

    private Nokri_DialogManager dialogManager;
    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        setContentView(R.layout.activity_nokri_main);
        nokri_initialize();
        nokri_setColor();
        nokri_setUpFonts();
        nokri_getMainActivity();

        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this)); }
    private void nokri_setColor(){

        Nokri_Utils.setBordederButton(this,signUpButton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }
        Nokri_Utils.setRoundButtonColor(this,signInButton);
        signUpButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
    };
    private void nokri_initialize() {
        signInButton = findViewById(R.id.btn_singin);
        signUpButton = findViewById(R.id.btn_signup);
        logo = findViewById(R.id.logo);
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        fontManager = new Nokri_FontManager();

    }
    private void nokri_setUpFonts(){
        fontManager.nokri_setOpenSenseFontButton(signInButton,getAssets());
        fontManager.nokri_setOpenSenseFontButton(signUpButton,getAssets());

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_singin:
                Intent signInIntent = new Intent(Nokri_MainActivity.this,Nokri_SigninActivity.class);
                signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(signInIntent);
                finish();
                break;
            case R.id.btn_signup:
                Intent signUpIntent = new Intent(Nokri_MainActivity.this,Nokri_SignupActivity.class);
                signUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(signUpIntent);
                finish();
                break;

        }
    }
    private void nokri_getMainActivity() {

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall;

        myCall = restService.getMainActivity();
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {


                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                        if (jsonObject.getBoolean("success")) {

                            JSONObject data = jsonObject.getJSONObject("data");
                            if(!TextUtils.isEmpty(data.getString("logo")))
                                Picasso.get().load(data.getString("logo")).into(logo);
                            signInButton.setText(data.getString("signin"));
                            signUpButton.setText(data.getString("signup"));

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
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(Nokri_MainActivity.this,t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Nokri_MainActivity.this, Nokri_GuestDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();

    }
}