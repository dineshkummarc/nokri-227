package com.scriptsbundle.nokri.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.libraries.places.api.Places;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scriptsbundle.nokri.BuildConfig;
import com.scriptsbundle.nokri.activities.WPML.LangArray;
import com.scriptsbundle.nokri.activities.WPML.WPMLModel;
import com.scriptsbundle.nokri.activities.WPML.WpmlActivity;
import com.scriptsbundle.nokri.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.scriptsbundle.nokri.guest.dashboard.Nokri_GuestDashboardActivity;
import com.scriptsbundle.nokri.guest.search.models.LocationModel;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.employeer.dashboard.Nokri_EmployerDashboardActivity;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.guest.home.models.Nokri_RateAppModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuActiveJobsModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuJobModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuResumeReceivedModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuSavedJobsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.models.Nokri_PopupModel;
import com.scriptsbundle.nokri.manager.models.Nokri_ProgressModel;
import com.scriptsbundle.nokri.manager.notification.FireBaseNotificationModel;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_LanguageSupport;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NokriSplashActivity extends AppCompatActivity implements Nokri_PopupManager.NoInternetInterface {
    private TextView loadingText;
    private static final int PERMISSION_CODE = 100;
    private ImageView logo;
    boolean splashDataArrived = false;
    boolean loginDataArrived = false;
    String signInType;
    String adId;
    boolean firstTime;
    public static String language;
    static {
        System.loadLibrary("keys");
    }

    public static native String placeInitialize();

    //  private LinearLayout adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_splash);

        Nokri_SharedPrefManager nokri_sharedPrefManager = new Nokri_SharedPrefManager();
        firstTime = nokri_sharedPrefManager.getFirstTime(this);
        language = nokri_sharedPrefManager.getWPMLLanguage(this);


        try {
            JsonObject jsonObject = new JsonParser().parse(String.valueOf(getIntent().getExtras().get("data"))).getAsJsonObject();
            adId = jsonObject.get("ad_id").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Nokri_SharedPrefManager.getAppColor(this)));
        }
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            Log.d("versionInfo", version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), placeInitialize());
        }

        //updateViews("ku");
        logo = findViewById(R.id.img_logo);
        Picasso.get().load(R.drawable.logo).into(logo);

        Nokri_Utils.generateKeyhash(this);

        Nokri_Utils.turnSystemFontsOff(this);

        if (Build.VERSION.SDK_INT >= 23) {
            if (nokri_checkAndRequestPermission())
                nokri_performAcions();
        } else
            nokri_performAcions();

        //  PayUBaseActivity sadf;


    }


    private void nokri_performAcions() {

        if (!Nokri_SharedPrefManager.isAccountEmployeer(this) && !Nokri_SharedPrefManager.isAccountCandidate(this))
            Nokri_SharedPrefManager.saveAccountType("public", this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        Nokri_GoogleAnalyticsManager.initialize(this);

        Nokri_GoogleAnalyticsManager.getInstance().get(Nokri_GoogleAnalyticsManager.Target.APP, Nokri_Config.GOOGLE_ANALYTICS_TRACKING_ID);


        loadingText = findViewById(R.id.loading_txt);
//        loadingText.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        new Nokri_FontManager().nokri_setMonesrratSemiBioldFont(loadingText, getAssets());
        nokri_saveAppSettings();
        if (!Nokri_SharedPrefManager.isSocialLogin(NokriSplashActivity.this) && Nokri_SharedPrefManager.getEmail(NokriSplashActivity.this) != null && Nokri_SharedPrefManager.getPassword(NokriSplashActivity.this) != null) {
            nokri_postSignin(Nokri_SharedPrefManager.getEmail(NokriSplashActivity.this), Nokri_SharedPrefManager.getPassword(NokriSplashActivity.this));


        } else if (Nokri_SharedPrefManager.isSocialLogin(NokriSplashActivity.this) && Nokri_SharedPrefManager.getEmail(NokriSplashActivity.this) != null) {
            nokri_postSocialSignin(Nokri_SharedPrefManager.getEmail(NokriSplashActivity.this), "social");
        }
    }


    private void nokri_saveAppSettings() {


        FireBaseNotificationModel fireBaseNotificationModel = Nokri_SharedPrefManager.getFirebaseNotification(this);
        if (fireBaseNotificationModel != null) {

            fireBaseNotificationModel.setTitle("");
            Nokri_SharedPrefManager.saveFireBaseNotification(fireBaseNotificationModel, this);
        }


        //Nokri_DialogManager.showAlertDialog(this);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall;

        myCall = restService.getCandidateSettings(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {


                        String remoteResponse=responseObject.body().string();
                        JSONObject jsonObject = new JSONObject(remoteResponse);

                        if (jsonObject.getBoolean("success")) {

                            JSONObject data = jsonObject.getJSONObject("data");

                            Nokri_SettingsModel settings = new Nokri_SettingsModel();


//                            try {
//                                Nokri_SettingsModel.externalEmailHint = data.getJSONObject("extra").getString("external_email_link");
//                                Nokri_SettingsModel.externalLinkHint = data.getJSONObject("extra").getString("external_link");
//                                Nokri_SettingsModel.whatsAppHint = data.getJSONObject("extra").getString("enter_whatsapp");
//                                settings.setCatTempOn(data.getBoolean("job_form"));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }

                            settings.setPasswordMismatchString(data.getJSONObject("extra").getString("password_mismatch"));
                            settings.setAppliedAlreadyString(data.getJSONObject("extra").getString("have_applied"));
                            settings.setConfirmPasswordString(data.getJSONObject("extra").getString("pass_confirm"));
                            settings.setAboutEnabled(data.getJSONObject("about").getBoolean("about_section"));
                            settings.setAboutText(data.getJSONObject("about").getString("about_details"));
                            settings.setAboutTitle(data.getJSONObject("about").getString("about_title"));
                            settings.setVersionEnabled(data.getJSONObject("version").getBoolean("version_section"));
                            settings.setVersionText(data.getJSONObject("version").getString("version_txt"));
                            settings.setRatingEnabled(data.getJSONObject("rating").getBoolean("rating_section"));
                            settings.setRatingText(data.getJSONObject("rating").getString("rating_txt"));
                            settings.setShareEnabled(data.getJSONObject("share").getBoolean("share_section"));
                            settings.setShareText(data.getJSONObject("share").getString("popup_title"));
                            settings.setShareSubject(data.getJSONObject("share").getString("subject"));
                            settings.setShareUrl(data.getJSONObject("share").getString("url"));
                            settings.setPrvacyEnabled(data.getJSONObject("privacy").getBoolean("privacy_section"));
                            settings.setPrivacyText(data.getJSONObject("privacy").getString("privacy_title"));
                            settings.setPrivacyUrl(data.getJSONObject("privacy").getString("url"));
                            settings.setRemoveText(data.getJSONObject("extra").getString("remove_resume"));
                            settings.setTermsEnabled(data.getJSONObject("terms_n_conditions").getBoolean("terms_section"));
                            settings.setTermsText(data.getJSONObject("terms_n_conditions").getString("terms_title"));
                            settings.setTermsUrl(data.getJSONObject("terms_n_conditions").getString("url"));
                            settings.setFaqEnabled(data.getJSONObject("faqs_section").getBoolean("faq"));
                            settings.setFeedbackEnabled(data.getJSONObject("feedback").getBoolean("is_show"));
                            settings.setFormSubmit(data.getJSONObject("feedback").getJSONObject("form").getString("btn_submit"));
                            settings.setExternalApplyText(data.getJSONObject("extra").getString("ext_confirm_content"));
                            settings.setExternalApplyHeading(data.getJSONObject("extra").getString("ext_confrim_title"));
                            if (data.getJSONObject("feedback").getBoolean("is_show")) {
                                settings.setFeedbackTitle(data.getJSONObject("feedback").getString("title"));
                                settings.setFeedbackSubtitle(data.getJSONObject("feedback").getString("subline"));
                                settings.setFeedbackFormTitle(data.getJSONObject("feedback").getJSONObject("form").getString("title"));
                                settings.setFeedbackFormEmail(data.getJSONObject("feedback").getJSONObject("form").getString("email"));
                                settings.setFeedbackFormMessage(data.getJSONObject("feedback").getJSONObject("form").getString("message"));
                                settings.setFormSubmit(data.getJSONObject("feedback").getJSONObject("form").getString("btn_submit"));
                                settings.setFormCancel(data.getJSONObject("feedback").getJSONObject("form").getString("btn_cancel"));
                                settings.setFromHeading(data.getJSONObject("feedback").getJSONObject("form").getString("header"));
                            }
                            Nokri_SharedPrefManager.saveSettings(settings, NokriSplashActivity.this);


                            Nokri_Config.STRIPE_KEY = data.getJSONObject("extra").getString("stripe_Skey");
                            Nokri_Globals.NEXT_STEP = data.getJSONObject("extra").getString("nxt_step");
                            Nokri_Globals.NEXT_STRING = data.getJSONObject("extra").getString("e_next_txt");
                            Nokri_Globals.BACK_STRING = data.getJSONObject("extra").getString("e_back_txt");
                            String style = data.getJSONObject("extra").getString("app_style_check");

                            if (style.equals("style1")) {
                                Nokri_Globals.DESIGN_TYPE = 1;
                            } else {
                                Nokri_Globals.DESIGN_TYPE = 2;
                            }
                            Nokri_Globals.LOGIN_FIRST = data.getJSONObject("extra").getString("is_login");
                            Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER = data.getJSONObject("extra").getString("all_fields");
                            Nokri_Globals.NO_URL_FOUND = data.getJSONObject("extra").getString("no_url");
                            Nokri_Globals.COMMENT_REQUIRED_TEXT = data.getJSONObject("extra").getString("coment_first");
                            Nokri_Globals.REQUIRED_REPY_TEXT = data.getJSONObject("extra").getString("rply_first");
                            Nokri_Globals.APP_NOT_FOUNT = data.getJSONObject("extra").getString("no_app");
                            Nokri_Globals.EXIT_TEXT = data.getJSONObject("extra").getString("exit");
                            Nokri_Globals.INVALID_EMAIL = data.getJSONObject("extra").getString("valid_email");
                            Nokri_Globals.TERMS_AND_SERVICES = data.getJSONObject("extra").getString("agree_term");
                            Nokri_Globals.ON_BACK_EXIT_TEXT = data.getJSONObject("extra").getString("click_back");
                            Nokri_Globals.SELECT_VALID_SKILL = data.getJSONObject("extra").getString("valid_skill");
                            Nokri_Globals.SELECT_SKILL = data.getJSONObject("extra").getString("select_one");
                            Nokri_Globals.APP_NOT_INSTALLED = data.getJSONObject("extra").getString("not_instal");
                            Nokri_Globals.INVALID_URL = data.getJSONObject("extra").getString("invalid_url");
                            Nokri_Globals.JOB_SEARCH_PLACEHOLER = data.getJSONObject("extra").getString("place_hldr");
//                            Nokri_Globals.JOB_ALERTS_TAGLINE = data.getJSONObject("extra").getString("job_alerts_tagline");
                            Nokri_Globals.showEmployerMap = data.getJSONObject("extra").getBoolean("emp_map_switch");
                            Nokri_Globals.showCandidateMap = data.getJSONObject("extra").getBoolean("cand_map_switch");


                            JSONObject tabs = data.getJSONObject("tabs");
                            JSONObject employeerTabs = data.getJSONObject("emp_tabs");
                            JSONObject guestTabs = data.getJSONObject("guest_tabs");

                            Nokri_Globals.PLEASE_WAIT_TEXT = tabs.getString("loading");

                            Nokri_CandidateDashboardModel model = new Nokri_CandidateDashboardModel();
                            model.setDashboard(tabs.getString("dashboard"));
                            model.setEdit(tabs.getString("edit"));
                            model.setProfile(tabs.getString("profile"));
                            model.setResume(tabs.getString("resume"));
                            model.setApplied(tabs.getString("apllied"));
                            model.setWpmlMenuText(data.getString("wpml_menu_text"));
                            model.setSaved(tabs.getString("saved"));
                            model.setJobs(tabs.getString("jobs"));
                            model.setCompany(tabs.getString("company"));
                            model.setExplore(tabs.getString("search"));
//                            model.setJobAlerts(data.getJSONObject("extra").getString("job_alerts_btn"));
                            model.setBlog(tabs.getString("blog"));
                            model.setLogout(tabs.getString("logout"));
                            model.setExit(tabs.getString("exit"));
                            model.setHome(tabs.getString("home"));
//                            model.setAlertName(data.getJSONObject("extra").getString("alert_name"));
//                            model.setAlertEmail(data.getJSONObject("extra").getString("alert_email"));
                            model.setEmployerSearch(data.getJSONObject("extra").getString("emp_search_txt"));
                            model.setFaq(employeerTabs.getString("faq"));
                            model.setPackageDetails(employeerTabs.getString("pkg_detail"));
                            //Maps Placeholder Data
                            LocationModel locationModel = new LocationModel();
                            locationModel.setLatitudeText(data.getJSONObject("extra").getString("lat"));
                            locationModel.setLongitudeText(data.getJSONObject("extra").getString("long"));
                            locationModel.setRadiusText(data.getJSONObject("extra").getString("radius"));
                            locationModel.setGeoLocationText(data.getJSONObject("extra").getString("geo_location"));
                            Nokri_SharedPrefManager.saveLocationSettings(locationModel,NokriSplashActivity.this);


                            JSONObject candidateEditTabs = data.getJSONObject("cand_tabs");
                            model.setCandidateNotification(candidateEditTabs.getString("cand_notify"));
                            //model.setSettings("set");
                            model.setSettings(data.getJSONObject("extra").getString("settings_txt"));
                            model.setTabPersonal(candidateEditTabs.getString("personal"));
                            model.setTabResume(candidateEditTabs.getString("resumes"));
                            model.setTabEducation(candidateEditTabs.getString("education"));
                            model.setTabProfessional(candidateEditTabs.getString("experience"));
                            model.setTabCertification(candidateEditTabs.getString("certification"));
                            model.setBuyPackages(employeerTabs.getString("buy_package"));

                            model.setTabSkills(candidateEditTabs.getString("skills"));
                            model.setTabPortfolio(candidateEditTabs.getString("portfolio"));
                            model.setTabSocial(candidateEditTabs.getString("socail"));
                            model.setTabLocation(candidateEditTabs.getString("loca"));
                            model.setCandidateSearch(candidateEditTabs.getString("cand_search"));
                            model.setJobsForYou(candidateEditTabs.getString("job_for"));

                            Nokri_SharedPrefManager.saveCandidateSettings(model, NokriSplashActivity.this);

                            Nokri_EmployeerDashboardModel employeerDashboardModel = new Nokri_EmployeerDashboardModel();
                            employeerDashboardModel.setDashboard(employeerTabs.getString("dashboard"));
                            employeerDashboardModel.setProfile(employeerTabs.getString("profile"));
                            employeerDashboardModel.setTemplates(employeerTabs.getString("templates"));
                            employeerDashboardModel.setJobs(employeerTabs.getString("jobs"));
                            employeerDashboardModel.setFollower(employeerTabs.getString("followers"));
                            employeerDashboardModel.setPostJob(employeerTabs.getString("post_job"));
                            try {
                                employeerDashboardModel.setSavedResumes(data.getJSONObject("extra").getString("saved_resumes"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            employeerDashboardModel.setPackageDetail(employeerTabs.getString("pkg_detail"));
                            employeerDashboardModel.setBuyPackage(employeerTabs.getString("buy_package"));
                            employeerDashboardModel.setBlog(employeerTabs.getString("blog"));
                            employeerDashboardModel.setLogout(employeerTabs.getString("logout"));
                            employeerDashboardModel.setExit(employeerTabs.getString("exit"));
                            employeerDashboardModel.setHome(employeerTabs.getString("home"));
                            employeerDashboardModel.setFaq(employeerTabs.getString("faq"));
                            employeerDashboardModel.setCandidateSearch(candidateEditTabs.getString("cand_search"));

                            JSONObject empTabs = data.getJSONObject("emp_edit_tabs");
                            employeerDashboardModel.setTabInfo(empTabs.getString("info"));
                            employeerDashboardModel.setTabspecialization(empTabs.getString("special"));
                            employeerDashboardModel.setTabPortfolio(candidateEditTabs.getString("portfolio"));
                            employeerDashboardModel.setTabSocail(empTabs.getString("social"));
                            employeerDashboardModel.setTabLocation(empTabs.getString("loc"));


                            JSONObject jobsTabs = data.getJSONObject("emp_jobs");
                            employeerDashboardModel.setJobActive(jobsTabs.getString("active"));
                            employeerDashboardModel.setJobInactive(jobsTabs.getString("inactive"));

                            JSONObject employeerPublicTabs = data.getJSONObject("compny_public_jobs");
                            employeerDashboardModel.setTabPublicOpen(employeerPublicTabs.getString("open"));
                            employeerDashboardModel.setTabPublicDetails(employeerPublicTabs.getString("details"));
                            //employeerDashboardModel.setSettings("set");
                            employeerDashboardModel.setSettings(data.getJSONObject("extra").getString("settings_txt"));
                            employeerDashboardModel.setWpmlMenuText(data.getString("wpml_menu_text"));
                            employeerDashboardModel.setEmployerSearch(data.getJSONObject("extra").getString("emp_search_txt"));
                            employeerDashboardModel.setMatchedResume(data.getJSONObject("extra").getString("matched_resume"));
                            Nokri_SharedPrefManager.saveEmployeerSettings(employeerDashboardModel, NokriSplashActivity.this);

                            Nokri_GuestDashboardModel guestDashboardModel = new Nokri_GuestDashboardModel();
                            guestDashboardModel.setHome(guestTabs.getString("home"));
                            guestDashboardModel.setExplore(guestTabs.getString("explore"));
                            guestDashboardModel.setExit(guestTabs.getString("exit"));
                            guestDashboardModel.setSignin(guestTabs.getString("signin"));
                            guestDashboardModel.setSignup(guestTabs.getString("signup"));
                            guestDashboardModel.setBlog(guestTabs.getString("templates"));
                            guestDashboardModel.setGuestName(guestTabs.getString("guset"));
                            guestDashboardModel.setCandidateDp(guestTabs.getString("cand_dp"));
                            guestDashboardModel.setFaq(employeerTabs.getString("faq"));
                            //guestDashboardModel.setSettings("Set");
                            guestDashboardModel.setSettings(data.getJSONObject("extra").getString("settings_txt"));
                            guestDashboardModel.setWpmlMenuText(data.getString("wpml_menu_text"));
                            JSONObject homeSceenTabs = data.getJSONObject("public_jobs");
                            guestDashboardModel.setTabLatest(homeSceenTabs.getString("latest"));
                            guestDashboardModel.setTabPrenium(homeSceenTabs.getString("premium"));
                            guestDashboardModel.setCandidateSearch(candidateEditTabs.getString("cand_search"));

                            Nokri_SharedPrefManager.saveGuestSettings(guestDashboardModel, NokriSplashActivity.this);
                            Nokri_PopupModel popupModel = new Nokri_PopupModel();

                            JSONObject genericDocumentionObject = data.getJSONObject("generic_txts");

                            popupModel.setCancelButton(genericDocumentionObject.getString("btn_cancel"));
                            popupModel.setConfirmButton(genericDocumentionObject.getString("btn_confirm"));
                            popupModel.setConfirmText(genericDocumentionObject.getString("confirm"));
                            popupModel.setSuccessText(genericDocumentionObject.getString("success"));

                            Nokri_Globals.EXIT_TEXT = genericDocumentionObject.getString("confirm");

                            Nokri_SharedPrefManager.savePopupSettings(popupModel, NokriSplashActivity.this);

                            JSONObject porgressDialogObject = data.getJSONObject("progress_txt");
                            Nokri_ProgressModel progressModel = new Nokri_ProgressModel();
                            progressModel.setTitle(porgressDialogObject.getString("title"));
                            progressModel.setFailMessage(porgressDialogObject.getString("msg_fail"));
                            progressModel.setSuccessMessage(porgressDialogObject.getString("msg_success"));
                            progressModel.setSuccessTitle(porgressDialogObject.getString("title_success"));
                            progressModel.setFailTitles(porgressDialogObject.getString("title_fail"));
                            progressModel.setButtonText(porgressDialogObject.getString("btn_ok"));
                            Nokri_SharedPrefManager.saveProgressSettings(progressModel, NokriSplashActivity.this);


                            JSONObject activeJobsMenuObject = data.getJSONObject("menu_active");
                            Nokri_MenuActiveJobsModel activeJobsModel = new Nokri_MenuActiveJobsModel();
                            activeJobsModel.setDelete(activeJobsMenuObject.getString("del"));
                            activeJobsModel.setEdit(activeJobsMenuObject.getString("edit"));
                            activeJobsModel.setResumeReceived(activeJobsMenuObject.getString("resume"));
                            activeJobsModel.setViewJob(activeJobsMenuObject.getString("view"));
                            Nokri_SharedPrefManager.saveActiveJobMenuSettings(activeJobsModel, NokriSplashActivity.this);

                            JSONObject jobsMenuObject = data.getJSONObject("menu_job");
                            Nokri_MenuJobModel jobModel = new Nokri_MenuJobModel();
                            jobModel.setViewCompanyProfile(jobsMenuObject.getString("company"));
                            jobModel.setViewJob(jobsMenuObject.getString("view"));
                            Nokri_SharedPrefManager.saveJobMenuSettings(jobModel, NokriSplashActivity.this);

                            JSONObject resumeReceivedMenuObject = data.getJSONObject("menu_resume");
                            JSONObject savedJobsMenuObject = data.getJSONObject("menu_saved");

                            Nokri_MenuResumeReceivedModel resumeReceivedModel = new Nokri_MenuResumeReceivedModel();
                            resumeReceivedModel.setDownload(resumeReceivedMenuObject.getString("download"));
                            resumeReceivedModel.setTakeAction(resumeReceivedMenuObject.getString("action"));
                            resumeReceivedModel.setViewProfile(resumeReceivedMenuObject.getString("profile"));
                            resumeReceivedModel.setLinkedin(resumeReceivedMenuObject.getString("linkedin"));
                            resumeReceivedModel.setDelete(savedJobsMenuObject.getString("delete"));
                            Nokri_SharedPrefManager.saveResumeReceivedbMenuSettings(resumeReceivedModel, NokriSplashActivity.this);

                            Nokri_MenuSavedJobsModel savedJobsModel = new Nokri_MenuSavedJobsModel();
                            savedJobsModel.setDeleteJob(savedJobsMenuObject.getString("delete"));
                            savedJobsModel.setViewJob(savedJobsMenuObject.getString("view"));
                            Nokri_SharedPrefManager.saveSavedJobsbMenuSettings(savedJobsModel, NokriSplashActivity.this);


                            Nokri_RateAppModel rateAppModel = new Nokri_RateAppModel();
                            rateAppModel.setCancelButton("Never");
                            rateAppModel.setConfirmButton("Maybe Later");
                            rateAppModel.setTitle("Rate Us?");
                            rateAppModel.setUrl("http://play.google.com/store/apps/details?id=" + getPackageName());
                            Nokri_SharedPrefManager.saveSaveRateAppModel(rateAppModel, NokriSplashActivity.this);


                            //------------------------ WPML Data -------------------------------------

                            Nokri_SharedPrefManager sharedPrefManager = new Nokri_SharedPrefManager();
                            WPMLModel wpmlModel = new WPMLModel();
                            wpmlModel.wpmlLogo = data.getString("wpml_logo");
                            wpmlModel.isActive = data.getBoolean("is_wpml_active");
                            wpmlModel.header1 = data.getString("wpml_header_title_1");
                            wpmlModel.header2 = data.getString("wpml_header_title_2");
                            wpmlModel.languageDescription = data.getJSONObject("extra").getString("lang_desc");
                            wpmlModel.submit = data.getJSONObject("feedback").getJSONObject("form").getString("btn_submit");
                            wpmlModel.skipText = data.getJSONObject("extra").getString("skip");
                            JSONArray siteLanguages = data.getJSONArray("site_languages");
                            wpmlModel.langArray.clear();
                            for (int i = 0; i < siteLanguages.length(); i++) {
                                LangArray langArray = new LangArray();
                                langArray.flagUrl = siteLanguages.getJSONObject(i).getString("flag_url");
                                langArray.nativeName = siteLanguages.getJSONObject(i).getString("native_name");
                                langArray.langCode = siteLanguages.getJSONObject(i).getString("code");
                                wpmlModel.langArray.add(langArray);
                            }

                            sharedPrefManager.setWPMLSettings(wpmlModel, NokriSplashActivity.this);

                            //------------------------ Ads Settings -------------------------------------

                            JSONObject adObject = data.getJSONObject("ads");

                            if (adObject.getBoolean("show")) {
                                Nokri_Globals.SHOW_AD = true;
                                if (adObject.getString("position").equals("top")) {
                                    Nokri_Globals.SHOW_AD_TOP = true;
                                } else
                                    Nokri_Globals.SHOW_AD_TOP = false;

                                if (adObject.getBoolean("is_show_banner")) {
                                    Nokri_Globals.IS_BANNER_EBABLED = true;
                                    Nokri_Globals.AD_ID = adObject.getString("banner_id");
                                }

                                if (adObject.getBoolean("is_show_initial")) {
                                    Nokri_Globals.IS_INTERTIAL_ENABLED = true;
                                    Nokri_Globals.INTERTIAL_ID = adObject.getString("ad_id");

                                    try {

                                        Nokri_Globals.AD_INITIAL_TIME = Long.parseLong(adObject.getString("time_initial"));
                                        Nokri_Globals.AD_DISPLAY_TIME = Long.parseLong(adObject.getString("time"));
                                    } catch (NumberFormatException e) {


                                    }

                                }

                            }
                            //   Nokri_Config.APP_COLOR = "#000000";
                            Nokri_Config.APP_COLOR = data.getString("app_color");
                            Nokri_SharedPrefManager.saveAppColor(data.getString("app_color"), NokriSplashActivity.this);
                            //------------------------ Ads Settings -------------------------------------
                            Nokri_SharedPrefManager.saveHomeType(data.getString("home"), NokriSplashActivity.this);
                            Nokri_SharedPrefManager.showBlog(data.getString("isBlog"), NokriSplashActivity.this);


                            if (data.getString("is_rtl").equals("0")) {
                                Nokri_Globals.IS_RTL_ENABLED = false;
                                Nokri_SharedPrefManager.saveLocal("en", NokriSplashActivity.this);
                                updateViews("en");

                            } else if (data.getString("is_rtl").equals("1")) {
                                Nokri_Globals.IS_RTL_ENABLED = true;
                                Nokri_SharedPrefManager.saveLocal("ar", NokriSplashActivity.this);
                                updateViews("ar");

                            }
                            splashDataArrived = true;

                            if (!Nokri_SharedPrefManager.isSocialLogin(NokriSplashActivity.this)
                                    && Nokri_SharedPrefManager.getEmail(NokriSplashActivity.this) != null
                                    && Nokri_SharedPrefManager.getPassword(NokriSplashActivity.this) != null) {

                            } else if (Nokri_SharedPrefManager.isSocialLogin(NokriSplashActivity.this)
                                    && Nokri_SharedPrefManager.getEmail(NokriSplashActivity.this) != null) {

                            } else {
                                if (firstTime) {
                                    startActivity(new Intent(NokriSplashActivity.this, WpmlActivity.class).putExtra("firstTime",firstTime));
                                }else{
                                    startActivity(new Intent(NokriSplashActivity.this, Nokri_GuestDashboardActivity.class));
                                }
                                finish();
                            }


                            if (loginDataArrived) {
                                if (signInType.equals("cand")) {
                                    Nokri_SharedPrefManager.saveAccountType("candidate", NokriSplashActivity.this);
                                    startActivity(new Intent(NokriSplashActivity.this, Nokri_CandidateDashboardActivity.class));
                                    finish();
                                } else {
                                    Nokri_SharedPrefManager.saveAccountType("employeer", NokriSplashActivity.this);
                                    startActivity(new Intent(NokriSplashActivity.this, Nokri_EmployerDashboardActivity.class));

                                    finish();
                                }
                            }



                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(NokriSplashActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Nokri_LanguageSupport.onAttach(base));
    }

    private void updateViews(String languageCode) {
        Nokri_LanguageSupport.setLocale(this, languageCode);
    }

    private void nokri_postSignin(String email, final String password) {

        //Nokri_DialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("email", email);
        params.addProperty("pass", password);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, password, NokriSplashActivity.this);
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
                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), NokriSplashActivity.this);
                            Nokri_SharedPrefManager.savePassword(password, NokriSplashActivity.this);
                            Nokri_SharedPrefManager.saveId(data.getString("id"), NokriSplashActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), NokriSplashActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), NokriSplashActivity.this);
                            if (Nokri_SharedPrefManager.getProfileImage(NokriSplashActivity.this) == null)
                                Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), NokriSplashActivity.this);
                            Log.d("profile_img", data.getString("profile_img"));
                            Nokri_SharedPrefManager.saveLoginType(null, NokriSplashActivity.this);
                            if (data.getString("user_type").equals("0")) {
                                if (splashDataArrived) {
                                    Nokri_SharedPrefManager.saveAccountType("candidate", NokriSplashActivity.this);
                                    Intent i = new Intent(NokriSplashActivity.this, Nokri_CandidateDashboardActivity.class);
                                    if (adId != null) {
                                        i.putExtra("ad_id", adId);
                                    }
                                    startActivity(i);
                                    finish();
                                } else {
                                    signInType = "cand";
                                }

                            } else if (data.getString("user_type").equals("1")) {
                                if (splashDataArrived) {
                                    Nokri_SharedPrefManager.saveAccountType("employeer", NokriSplashActivity.this);
                                    startActivity(new Intent(NokriSplashActivity.this, Nokri_EmployerDashboardActivity.class));
                                    finish();
                                } else {
                                    signInType = "emp";
                                }
                            }
                            loginDataArrived = true;

                        } else {
                            Log.d("response", responseObject.toString() + "error");

                            Nokri_ToastManager.showShortToast(NokriSplashActivity.this, respone.getString("message"));
                            Nokri_SharedPrefManager.invalidate(NokriSplashActivity.this);
                            startActivity(new Intent(NokriSplashActivity.this, Nokri_MainActivity.class));

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
           /*     Nokri_DialogManager.showError();
                Nokri_DialogManager.goBack(NokriSplashActivity.this);*/
            }
        });
    }


    private void nokri_postSocialSignin(String email, final String type) {
        Log.d("socail", email + type);
//        Nokri_DialogManager.showAlertDialog(this);
        JsonObject params = new JsonObject();
        params.addProperty("type", type);
        params.addProperty("email", email);

        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, email, email, NokriSplashActivity.this);
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


                            Nokri_SharedPrefManager.saveEmail(data.getString("user_email"), NokriSplashActivity.this);
                            Nokri_SharedPrefManager.saveLoginType(type, NokriSplashActivity.this);
                            Nokri_SharedPrefManager.savePassword("pass", NokriSplashActivity.this);

                            Nokri_SharedPrefManager.saveId(data.getString("id"), NokriSplashActivity.this);
                            Nokri_SharedPrefManager.saveName(data.getString("display_name"), NokriSplashActivity.this);
                            Nokri_SharedPrefManager.savePhone(data.getString("phone"), NokriSplashActivity.this);
                            if (Nokri_SharedPrefManager.getProfileImage(NokriSplashActivity.this) == null)
                                Nokri_SharedPrefManager.saveProfileImage(data.getString("profile_img"), NokriSplashActivity.this);


                            // Default Sigin in type for testing

//---------------------------------------------------------------------
                            if (Nokri_SharedPrefManager.isAccountCandidate(NokriSplashActivity.this)) {
                                // Nokri_SharedPrefManager.saveAccountType("candidate", Nokri_SigninActivity.this);

                                startActivity(new Intent(NokriSplashActivity.this, Nokri_CandidateDashboardActivity.class));
                                finish();
                            } else if (Nokri_SharedPrefManager.isAccountEmployeer(NokriSplashActivity.this)) {
                                //Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);
                                startActivity(new Intent(NokriSplashActivity.this, Nokri_EmployerDashboardActivity.class));
                                finish();
                            } else if (Nokri_SharedPrefManager.isAccountPublic(NokriSplashActivity.this)) {
                                //Nokri_SharedPrefManager.saveAccountType("employeer", Nokri_SigninActivity.this);
                                startActivity(new Intent(NokriSplashActivity.this, Nokri_GuestDashboardActivity.class));
                                finish();
                            } else
                                Nokri_ToastManager.showShortToast(NokriSplashActivity.this, Nokri_SharedPrefManager.getAccountType(NokriSplashActivity.this));


                            //    Nokri_DialogManager.hideAlertDialog();
                        } else {
                            Log.d("socail", responseObject.toString() + "error");
                            Nokri_ToastManager.showShortToast(NokriSplashActivity.this, respone.getString("message"));
                            Nokri_SharedPrefManager.invalidate(NokriSplashActivity.this);
                            startActivity(new Intent(NokriSplashActivity.this, Nokri_MainActivity.class));
                        }
                    } catch (IOException e) {
                        /*Nokri_DialogManager.showCustom(e.getMessage());
                        Nokri_DialogManager.hideAfterDelay();*/
//                        Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (JSONException e) {
                      /*  Nokri_DialogManager.showCustom(e.getMessage());
                        Nokri_DialogManager.hideAfterDelay();*/
                        //  Nokri_DialogManager.hideAlertDialog();
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              /*  Nokri_DialogManager.showCustom(t.getMessage());
                Nokri_DialogManager.goBack(NokriSplashActivity.this);*/
            }
        });
    }

    @Override
    public void onButtonClick(DialogInterface dialog) {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onNoClick(DialogInterface dialog) {

    }

 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }*/

    private boolean nokri_checkAndRequestPermission() {

        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accessCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> permissionsNeeded = new ArrayList<>();
        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (accessFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    nokri_performAcions();
                } else {
                    finish();
                }
                break;

        }


    }
}
