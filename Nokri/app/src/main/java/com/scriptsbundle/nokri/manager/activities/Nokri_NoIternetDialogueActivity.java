package com.scriptsbundle.nokri.manager.activities;

import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.scriptsbundle.nokri.manager.Nokri_PopupManager;

public class Nokri_NoIternetDialogueActivity extends AppCompatActivity implements Nokri_PopupManager.NoInternetInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Nokri_PopupManager.nokri_showNoInternetAlert(this,this);
    }


    @Override
    public void onButtonClick(DialogInterface dialog) {


           android.os.Process.killProcess(android.os.Process.myPid());

    }

    @Override
    public void onNoClick(DialogInterface dialog) {
       onBackPressed();
    }
}
