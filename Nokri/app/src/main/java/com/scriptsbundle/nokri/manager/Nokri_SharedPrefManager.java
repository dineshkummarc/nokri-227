package com.scriptsbundle.nokri.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scriptsbundle.nokri.activities.WPML.WPMLModel;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.employeer.EmployerSearch.EmployerSearchModel;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.payment.models.Nokri_PricingModel;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.guest.home.models.Nokri_RateAppModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuActiveJobsModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuJobModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuResumeReceivedModel;
import com.scriptsbundle.nokri.guest.models.Nokri_MenuSavedJobsModel;
import com.scriptsbundle.nokri.guest.search.models.LocationModel;
import com.scriptsbundle.nokri.guest.search.models.Nokri_CandidateSearchModel;
import com.scriptsbundle.nokri.guest.search.models.Nokri_JobSearchModel;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.models.Nokri_PopupModel;
import com.scriptsbundle.nokri.manager.models.Nokri_ProgressModel;
import com.scriptsbundle.nokri.manager.notification.FireBaseNotificationModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Glixen Technologies on 11/01/2018.
 */

public class Nokri_SharedPrefManager {

    public static void isLoginFromLinkedin(Boolean email, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoginFromLinkedIn", email);
        editor.apply();
    }


    public void setWPMLLanguage(String firstTime,Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", firstTime);
        editor.apply();
    };

    public String getWPMLLanguage(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("language", "");
    }

    public void setFirstTime(boolean firstTime,Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstTime", firstTime);
        editor.apply();
    };

    public boolean getFirstTime(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("firstTime", true);
    }


    public void setWPMLSettings(WPMLModel model, Context context) {
        Gson gson = new Gson();
        String wpmlSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("wpml_settings", wpmlSettings);
        editor.apply();

    }


    public static WPMLModel getWPMLSettings(Context context) {


        Gson gson = new Gson();
        String wpmlSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("wpml_settings", null);
        WPMLModel model = gson.fromJson(wpmlSettings, WPMLModel.class);
        return model;
    }

    public static Boolean getIsLoginFromLinkedIn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isLoginFromLinkedIn", false);
    }
    public static void saveEmail(String email, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public static String getEmail(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("email", null);
    }


    public static void saveAppColor(String color, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("color", color);
        editor.apply();
    }

    public static String getAppColor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("color", "#000000");
    }

    public static void saveHomeType(String home, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("homeType", home);
        editor.apply();
    }

    public static String getHomeType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("homeType", "1");
    }
    public static void showBlog(String blog, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("hideBlog", blog);
        editor.apply();
    }

    public static String hideBlog(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("hideBlog", "1");
    }


    public static void saveLocal(String email, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("local", email);
        editor.apply();
    }

    public static String getLocal(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("local", "en");
    }

    public static void savePassword(String password, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pass", password);
        editor.apply();
    }

    public static String getPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("pass", null);
    }

    public static void saveId(String id, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", id);
        editor.apply();
    }

    public static String getId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("id", null);
    }

    public static void saveName(String name, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", name);
        editor.apply();
    }

    public static String getName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("name", null);
    }

    public static void savePhone(String phone, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone", phone);
        editor.apply();
    }

    public static String getPhone(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("phone", null);
    }

    public static void saveProfileImage(String profileImage, Context context) {
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("profileImage", profileImage);
            editor.apply();
        }

    }

    public static String getProfileImage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("profileImage", null);
    }

    public static void saveCoverImage(String coverImage, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("coverImage", coverImage);
        editor.apply();
    }

    public static String getCoverImage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("coverImage", null);
    }

    public static void saveHeadline(String headline, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("headline", headline);
        editor.apply();
    }

    public static String getHeadline(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("headline", null);
    }

    public static void saveDateOfBirth(String dob, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("dob", dob);
        editor.apply();
    }

    public static String getDateOfBirth(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("dob", null);
    }

    public static void saveAbout(String about, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("about", about);
        editor.apply();
    }

    public static String getAbout(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("about", null);
    }

    public static void saveLastEducation(String coverImage, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastEducation", coverImage);
        editor.apply();
    }

    public static String getLastEducation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("lastEducation", null);
    }


    public static void saveLoginType(String password, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loginType", password);
        editor.apply();
    }

    public static String getLoginType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("loginType", null);
    }

    public static boolean isSocialLogin(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("loginType", null) != null)
            return true;
        else
            return false;
    }

    public static void invalidate(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", null);
        editor.putString("pass", null);
        editor.putString("id", null);
        editor.putString("name", null);
        editor.putString("phone", null);
        editor.putString("profileImage", null);
        editor.putString("coverImage", null);
        editor.putString("headline", null);
        editor.putString("dob", null);
        editor.putString("about", null);
        editor.putString("lastEducation", null);
        editor.putString("loginType", null);
        editor.putString("accountType", "public");
        editor.putString("linkedin_public_profile", null);
        editor.apply();
    }


    public static void saveEstablishedSince(String established, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("established", established);
        editor.apply();
    }

    public static String getEstablishedSince(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("established", null);
    }

    public static void saveNumberOfEmployees(String noe, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("noe", noe);
        editor.apply();
    }

    public static String getNumberOfEmployees(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("noe", null);
    }

    public static void saveMemberSince(String memberSince, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("member", memberSince);
        editor.apply();
    }

    public static String getMemberSince(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("member", null);
    }

    public static void saveAccountType(String accountType, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("accountType", accountType);
        editor.apply();
    }

    public static String getAccountType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("accountType", null);
    }

    public static boolean isAccountPublic(Context context) {
        String accountType = getAccountType(context);

        if (accountType == null || !accountType.equals("public"))
            return false;
        else
            return true;
    }

    public static boolean isAccountCandidate(Context context) {
        String accountType = getAccountType(context);

        if (accountType == null || !accountType.equals("candidate"))
            return false;
        else
            return true;
    }

    public static boolean isAccountEmployeer(Context context) {
        String accountType = getAccountType(context);

        if (accountType == null || !accountType.equals("employeer"))
            return false;
        else
            return true;
    }



    public static void saveLocationSettings(LocationModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("locationSettings", candidateSettings);
        editor.apply();

    }


    public static LocationModel getLocationSettings(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("locationSettings", null);
        LocationModel model = gson.fromJson(candidateSettings, LocationModel.class);
        return model;
    }

    public static void saveCandidateSettings(Nokri_CandidateDashboardModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("candidate_settings", candidateSettings);
        editor.apply();

    }


    public static Nokri_CandidateDashboardModel getCandidateSettings(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("candidate_settings", null);
        Nokri_CandidateDashboardModel model = gson.fromJson(candidateSettings, Nokri_CandidateDashboardModel.class);
        return model;
    }

    public static void saveSettings(Nokri_SettingsModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("settings", candidateSettings);
        editor.apply();

    }

    public static Nokri_SettingsModel getSettings(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("settings", null);
        Nokri_SettingsModel model = gson.fromJson(candidateSettings, Nokri_SettingsModel.class);
        return model;
    }


    public static void saveEmployeerSettings(Nokri_EmployeerDashboardModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("employeer_settings", candidateSettings);
        editor.apply();

    }

    public static Nokri_EmployeerDashboardModel getEmployeerSettings(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("employeer_settings", null);
        Nokri_EmployeerDashboardModel model = gson.fromJson(candidateSettings, Nokri_EmployeerDashboardModel.class);
        return model;
    }

    public static void saveGuestSettings(Nokri_GuestDashboardModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("guest_settings", candidateSettings);
        editor.apply();

    }

    public static Nokri_GuestDashboardModel getGuestSettings(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("guest_settings", null);
        Nokri_GuestDashboardModel model = gson.fromJson(candidateSettings, Nokri_GuestDashboardModel.class);
        return model;
    }


    public static void saveJobSearchModel(Nokri_JobSearchModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("job_search", candidateSettings);
        editor.apply();

    }

    public static void saveCandidateSearchModel(Nokri_CandidateSearchModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("candidate_search", candidateSettings);
        editor.apply();

    }

    public static Nokri_JobSearchModel getJobSearchModel(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("job_search", null);
        Nokri_JobSearchModel model = gson.fromJson(candidateSettings, Nokri_JobSearchModel.class);
        return model;
    }

    public static Nokri_CandidateSearchModel getCandidateSearchModel(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("candidate_search", null);
        Nokri_CandidateSearchModel model = gson.fromJson(candidateSettings, Nokri_CandidateSearchModel.class);
        return model;
    }


    public static void saveEmployerSearchModel(EmployerSearchModel model, Context context) {
        Gson gson = new Gson();
        String candidateSettings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("employer_search", candidateSettings);
        editor.apply();

    }

    public static EmployerSearchModel getEmployerSearchModel(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("employer_search", null);
        EmployerSearchModel model = gson.fromJson(candidateSettings, EmployerSearchModel.class);
        return model;
    }


    public static void saveFirebaseNotificationToken(String token, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firebase_token", token);
        editor.apply();
    }

    public static String getFirebaseNotificationToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("firebase_token", null);
    }

    public static void saveFirebaseNotificationImage(String notificationImage, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firebase_notification_image", notificationImage);
        editor.apply();
    }

    public static String getFirebaseNotificationImage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("firebase_notification_image", "");
    }


    public static void saveFirebaseNotificationTitle(String notificationTitle, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firebase_notification_title", notificationTitle);
        editor.apply();
    }

    public static String getFirebaseNotificationTitme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("firebase_notification_title", "");
    }

    public static void saveFirebaseNotificationMessage(String notificationMessage, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firebase_notification_message", notificationMessage);
        editor.apply();
    }

    public static String getFirebaseNotificationMessage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("firebase_notification_message", "");
    }


    public static ArrayList<Nokri_PricingModel> getPricingModel(Context context) {


        Gson gson = new Gson();
        String candidateSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("pricing_pages", null);
        Type type = new TypeToken<ArrayList<Nokri_PricingModel>>() {
        }.getType();

        ArrayList<Nokri_PricingModel> model = gson.fromJson(candidateSettings, type);
        return model;
    }

    public static void savePopupSettings(Nokri_PopupModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup_settings", popupSetings);
        editor.apply();

    }

    public static Nokri_PopupModel getPopupSettings(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("popup_settings", null);
        Nokri_PopupModel model = gson.fromJson(popupSettings, Nokri_PopupModel.class);
        return model;
    }


    public static void saveProgressSettings(Nokri_ProgressModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup_progress", popupSetings);
        editor.apply();

    }

    public static Nokri_ProgressModel getProgressSettings(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("popup_progress", null);
        Nokri_ProgressModel model = gson.fromJson(popupSettings, Nokri_ProgressModel.class);
        return model;
    }


    public static void saveJobMenuSettings(Nokri_MenuJobModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("menu_job", popupSetings);
        editor.apply();

    }

    public static Nokri_MenuJobModel getJobMenuSettings(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("menu_job", null);
        Nokri_MenuJobModel model = gson.fromJson(popupSettings, Nokri_MenuJobModel.class);
        return model;
    }


    public static void saveActiveJobMenuSettings(Nokri_MenuActiveJobsModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("active_menu_job", popupSetings);
        editor.apply();

    }

    public static Nokri_MenuActiveJobsModel getActiveJobMenuSettings(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("active_menu_job", null);
        Nokri_MenuActiveJobsModel model = gson.fromJson(popupSettings, Nokri_MenuActiveJobsModel.class);
        return model;
    }


    public static void saveResumeReceivedbMenuSettings(Nokri_MenuResumeReceivedModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("resume_received_menu", popupSetings);
        editor.apply();

    }

    public static Nokri_MenuResumeReceivedModel getResumeReceivedMenuSettings(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("resume_received_menu", null);
        Nokri_MenuResumeReceivedModel model = gson.fromJson(popupSettings, Nokri_MenuResumeReceivedModel.class);
        return model;
    }

    public static void saveSavedJobsbMenuSettings(Nokri_MenuSavedJobsModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("saved_jobs_menu", popupSetings);
        editor.apply();

    }

    public static Nokri_MenuSavedJobsModel getSavedJobsMenuSettings(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("saved_jobs_menu", null);
        Nokri_MenuSavedJobsModel model = gson.fromJson(popupSettings, Nokri_MenuSavedJobsModel.class);
        return model;
    }


    public static void saveLinkedinPublicProfile(String profileUrl, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("linkedin_public_profile", profileUrl);
        editor.apply();
    }

    public static String getLinkedInPublicProfile(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("linkedin_public_profile", null);
    }

    public static boolean isAccountLinkedIn(Context context) {
        if (getLinkedInPublicProfile(context) != null)
            return true;
        else
            return false;


    }

    public static void saveFireBaseNotification(FireBaseNotificationModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firebase_notification_model", popupSetings);
        editor.apply();

    }

    public static FireBaseNotificationModel getFirebaseNotification(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("firebase_notification_model", null);
        FireBaseNotificationModel model = gson.fromJson(popupSettings, FireBaseNotificationModel.class);
        return model;
    }

    public static void saveSaveRateAppModel(Nokri_RateAppModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("rate_app", popupSetings);
        editor.apply();

    }


    public static Nokri_RateAppModel getRateAppModel(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("rate_app", null);
        Nokri_RateAppModel model = gson.fromJson(popupSettings, Nokri_RateAppModel.class);
        return model;
    }


}

