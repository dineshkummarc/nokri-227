package com.scriptsbundle.nokri.activities.WPML;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.activities.NokriSplashActivity;
import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;

import com.squareup.picasso.Picasso;

public class WpmlActivity extends AppCompatActivity implements WPMLInterface, View.OnClickListener {
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    LinearLayout selectlanguage;
    ImageView appImage;
    Toolbar toolbar;
    WPMLModel wpmlModel;
    TextView text1,text2,textDescription;
    Button submit, skip;
    TextView languageName;
    Nokri_SharedPrefManager sharedPrefManager = new Nokri_SharedPrefManager();
    String languageCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Nokri_Config.APP_COLOR = Nokri_SharedPrefManager.getAppColor(this);
        setContentView(R.layout.activity_wpml);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_Config.APP_COLOR));
        }


        wpmlModel = sharedPrefManager.getWPMLSettings(this);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(wpmlModel.header2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text22);
        textDescription = findViewById(R.id.text3);
        submit = findViewById(R.id.submit);
        skip = findViewById(R.id.skip);
        languageName = findViewById(R.id.languageName);

        text1.setText(wpmlModel.header1);
        text2.setText(wpmlModel.header2);
        textDescription.setText(wpmlModel.languageDescription);
        submit.setText(wpmlModel.submit);
        skip.setText(wpmlModel.skipText);
        if (wpmlModel.langArray.size()==0){
            if (getIntent().getBooleanExtra("firstTime",false)){
                sharedPrefManager.setFirstTime(false,this);
                Intent i = new Intent(WpmlActivity.this, Nokri_GuestDashboardActivity.class);
                startActivity(i);
                finish();
                return;
            }else{
                finish();
            }
        }

        languageName.setText(wpmlModel.langArray.get(0).nativeName);
        languageCode = wpmlModel.langArray.get(0).langCode;
        skip.setOnClickListener(this);
        submit.setOnClickListener(this);


        if (sharedPrefManager.getFirstTime(this)){
            getSupportActionBar().hide();
        }else{
            skip.setVisibility(View.GONE);
        }

        appImage = findViewById(R.id.appImage);
        selectlanguage = findViewById(R.id.linearLayout2);

//        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
//        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
//            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        selectlanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(WpmlActivity.this);
                bottomSheetFragment.setBottomSheetInstance(bottomSheetFragment);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });
        Picasso.get().load(wpmlModel.wpmlLogo).into(appImage);
        if (sharedPrefManager.getFirstTime(this)){
            sharedPrefManager.setFirstTime(false, WpmlActivity.this);
        }
    }

    @Override
    public void onLanguageSelected(LangArray langArray) {
        languageName.setText(langArray.nativeName);
        languageCode = langArray.langCode;
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
    public void onClick(View v) {
        if (v.getId()==submit.getId()){
            sharedPrefManager.setWPMLLanguage(languageCode,WpmlActivity.this);
            Intent i = new Intent(WpmlActivity.this, NokriSplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }else if (v.getId()==skip.getId()){
            startActivity(new Intent(WpmlActivity.this, Nokri_GuestDashboardActivity.class));
            finish();
        }
    }
}