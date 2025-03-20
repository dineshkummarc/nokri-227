package com.scriptsbundle.nokri.guest.settings.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.faq.fragments.Nokri_FaqFragment;
import com.scriptsbundle.nokri.guest.home.models.Nokri_RateAppModel;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_SettingsFragment extends Fragment implements View.OnClickListener {
    private Nokri_SettingsModel settingsModel;
    private  String versionName = "";
    private LinearLayout aboutContainer,versionContainer,ratingContainer,shareContainer,feedbackContainer,faqContainer,termsContainer,policyContainer;
    TextView aboutHeadingTextView,aboutDataTextView,appVersionHeadingTextView,appVeriosnDataTextView,feedbackHeadingTextView,feedbackDataTextView,ratingTextView,shareTextView,faqTextView,termsTextView,policyTextView;
    String subjectHint,emailHint,messageHint,submitButton,cancelButton,heading;
    String shareSubject = "";
    public Nokri_SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PackageInfo pinfo = null;
        try {
            pinfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
           // int versionNumber = pinfo.versionCode;
             versionName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "Not Found";
            e.printStackTrace();
        }

        settingsModel = Nokri_SharedPrefManager.getSettings(getContext());



        aboutHeadingTextView = view.findViewById(R.id.txt_about_app);
        appVersionHeadingTextView = view.findViewById(R.id.txt_app_version);
        appVeriosnDataTextView = view.findViewById(R.id.txt_app_version_data);
        feedbackHeadingTextView = view.findViewById(R.id.txt_feedback);
        feedbackDataTextView = view.findViewById(R.id.txt_feedback_data);
        ratingTextView = view.findViewById(R.id.txt_app_rating);
        shareTextView = view.findViewById(R.id.txt_app_share);
        aboutDataTextView = view.findViewById(R.id.txt_about_app_data);
        faqTextView = view.findViewById(R.id.txt_faq);
        faqTextView.setText(Nokri_SharedPrefManager.getCandidateSettings(getContext()).getFaq());
        termsTextView = view.findViewById(R.id.txt_terms);

        policyTextView = view.findViewById(R.id.txt_policy);


        aboutContainer = view.findViewById(R.id.aboutAppContainer);
        versionContainer = view.findViewById(R.id.appVersionContainer);
        ratingContainer = view.findViewById(R.id.appStoreRatingContainer);
        shareContainer = view.findViewById(R.id.shareAppContainer);
        feedbackContainer = view.findViewById(R.id.feedbackContainer);
        faqContainer = view.findViewById(R.id.faqContainer);
        termsContainer = view.findViewById(R.id.termaContainer);
        policyContainer = view.findViewById(R.id.policyContainer);


        if(settingsModel!=null){


                  if(!settingsModel.isAboutEnabled())
        aboutContainer.setVisibility(View.GONE);


        if(!settingsModel.isVersionEnabled())
            versionContainer.setVisibility(View.GONE);

        if(!settingsModel.isRatingEnabled()) {
            ratingContainer.setVisibility(View.GONE);
            view.findViewById(R.id.line).setVisibility(View.GONE);
        }
        if(!settingsModel.isShareEnabled()) {
            shareContainer.setVisibility(View.GONE);
            view.findViewById(R.id.line1).setVisibility(View.GONE);

        }
        if(!settingsModel.isTermsEnabled())
            termsContainer.setVisibility(View.GONE);

        if(!settingsModel.isPrvacyEnabled())
            policyContainer.setVisibility(View.GONE);

        if(!settingsModel.isFeedbackEnabled()){

            feedbackContainer.setVisibility(View.GONE);
        }
        if(!settingsModel.isFaqEnabled())
        {
            faqContainer.setVisibility(View.GONE);
        }

            aboutHeadingTextView.setText(settingsModel.getAboutTitle());
            aboutDataTextView.setText(settingsModel.getAboutText());
            appVersionHeadingTextView.setText(settingsModel.getVersionText());
            ratingTextView.setText(settingsModel.getRatingText());
            shareTextView.setText(settingsModel.getShareText());
            termsTextView.setText(settingsModel.getTermsText());
            policyTextView.setText(settingsModel.getPrivacyText());
            feedbackHeadingTextView.setText(settingsModel.getFeedbackTitle());
            feedbackDataTextView.setText(settingsModel.getFeedbackSubtitle());

            subjectHint = settingsModel.getFeedbackFormTitle();
            emailHint = settingsModel.getFeedbackFormEmail();
            messageHint = settingsModel.getFeedbackFormMessage();
            submitButton = settingsModel.getFormSubmit();
            cancelButton = settingsModel.getFormCancel();
            heading = settingsModel.getFromHeading();
            shareSubject = settingsModel.getShareSubject();
        }

























        aboutContainer.setOnClickListener(this);
        versionContainer.setOnClickListener(this);
        ratingContainer.setOnClickListener(this);
        shareContainer.setOnClickListener(this);
        feedbackContainer.setOnClickListener(this);
        faqContainer.setOnClickListener(this);
        termsContainer.setOnClickListener(this);
        policyContainer.setOnClickListener(this);

        nokri_setupFonts();

        appVeriosnDataTextView.setText(versionName);


        String toolbarTitle = "";
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        if(Nokri_SharedPrefManager.getGuestSettings(getContext())!=null){
            if(Nokri_SharedPrefManager.getGuestSettings(getContext()).getSettings()!=null){
                toolbarTitle = Nokri_SharedPrefManager.getGuestSettings(getContext()).getSettings();
            }
        }
        toolbarTitleTextView.setText(toolbarTitle);


    }


    private void nokri_setupFonts(){

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setMonesrratSemiBioldFont(aboutHeadingTextView,getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(appVersionHeadingTextView,getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(feedbackHeadingTextView,getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(ratingTextView,getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(shareTextView,getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(faqTextView,getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(termsTextView,getContext().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(policyTextView,getContext().getAssets());

        fontManager.nokri_setOpenSenseFontTextView(aboutDataTextView,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(appVeriosnDataTextView,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(feedbackDataTextView,getContext().getAssets());
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.aboutAppContainer:
                break;

            case R.id.appVersionContainer:
                break;

            case R.id.appStoreRatingContainer:
                nokri_showRatingPopup();
                break;

            case R.id.shareAppContainer:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        shareSubject+": https://play.google.com/store/apps/details?id="+getContext().getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.feedbackContainer:
                nokri_showFeedbackpopup();
                break;
            case R.id.faqContainer:

                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_placeholder, new Nokri_FaqFragment()).addToBackStack(null).commit();
                break;
            case R.id.termaContainer:
                Nokri_Utils.opeInBrowser(getContext(),settingsModel.getTermsUrl());
                break;
            case R.id.policyContainer:
                Nokri_Utils.opeInBrowser(getContext(),settingsModel.getPrivacyUrl());
                break;
        }

    }
    private void nokri_showRatingPopup(){

        Nokri_RateAppModel rateAppModel = Nokri_SharedPrefManager.getRateAppModel(getContext()) ;
        RatingDialog ratingDialog = new RatingDialog.Builder(getContext())

                .threshold(3)
                .title(rateAppModel.getTitle())
                .positiveButtonText(rateAppModel.getConfirmButton())
                .negativeButtonText(rateAppModel.getCancelButton())
                .ratingBarColor(R.color.app_blue)
                .playstoreUrl(rateAppModel.getUrl())
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Log.d("Feedback",feedback);
                    }
                }).build();
        ratingDialog.show();
    }


    private void nokri_showFeedbackpopup(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_feedback);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final RelativeLayout headerContainer = dialog.findViewById(R.id.headerContainer);
        headerContainer.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

        TextView headerTextView = dialog.findViewById(R.id.txt_header);
      final  EditText emailEditText = dialog.findViewById(R.id.edittxt_email);
      final  EditText subjectEditText = dialog.findViewById(R.id.edittxt_subject);
      final  EditText feedbackEditText = dialog.findViewById(R.id.edittxt_feedback);

        emailEditText.setHint(emailHint);
        subjectEditText.setHint(subjectHint);
        feedbackEditText.setHint(messageHint);
        headerTextView.setText(heading);

        Button submitButton = dialog.findViewById(R.id.btn_submit);
        Button cancelButton = dialog.findViewById(R.id.btn_cancel);
        submitButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        cancelButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

        submitButton.setText(this.submitButton);
        cancelButton.setText(this.cancelButton);

        Nokri_FontManager fontManager = new Nokri_FontManager();
        fontManager.nokri_setMonesrratSemiBioldFont(headerTextView,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(subjectEditText,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(feedbackEditText,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontButton(submitButton,getContext().getAssets());
        fontManager.nokri_setOpenSenseFontButton(cancelButton,getContext().getAssets());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Nokri_Utils.isValidEmail(emailEditText.getText().toString())) {
                    nokri_postFeedback(emailEditText.getText().toString(), subjectEditText.getText().toString(), feedbackEditText.getText().toString());
                }
                else
                {   emailEditText.setError("!");
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.INVALID_EMAIL);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();


    }



    private void nokri_postFeedback(String email,String subject,String feedback){


      final    Nokri_DialogManager   dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();
             params.addProperty("email",email);
            params.addProperty("subject",subject);
            params.addProperty("message", feedback);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postFeedback(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postFeedback(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getAppliedJobs(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if(responseObject.isSuccessful()){
                    try {

                         JSONObject response = new JSONObject(responseObject.body().string());


                       Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                    dialogManager.hideAlertDialog();
                    } catch (IOException e) {

                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());

                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (JSONException e) {

                        Nokri_ToastManager.showLongToast(getContext(),e.getMessage());
                            dialogManager.hideAfterDelay();

                        e.printStackTrace();

                    }

                }
                else {
                     dialogManager.hideAfterDelay();
                    Nokri_ToastManager.showLongToast(getContext(),responseObject.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                    dialogManager.hideAfterDelay();
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());    }
        });
    }
}
