package com.scriptsbundle.nokri.employeer.jobs.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scriptsbundle.nokri.LinkedIn.helpers.LinkedInUser;
import com.scriptsbundle.nokri.activities.Nokri_SigninActivity;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.jobs.Activties.ApplyJobActivity;
import com.scriptsbundle.nokri.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.scriptsbundle.nokri.employeer.jobs.adapters.NearbyJobsAdapter;
import com.scriptsbundle.nokri.employeer.jobs.adapters.Nokri_DescriptionRecyclerViewAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.NearbyJobModel;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_DescriptionModel;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_Home2ScreenFragment;
import com.scriptsbundle.nokri.guest.home.fragments.Nokri_HomeScreenFragment;
import com.scriptsbundle.nokri.guest.settings.models.Nokri_SettingsModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.manager.models.Nokri_PopupModel;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_JobDetailFragment extends Fragment implements View.OnClickListener, Nokri_PopupManager.ConfirmInterface {
    private TextView jobTitleTextView, companyNameTextView, applyByTextView, dateTextView, shortDescriptonTextView, jobDescriptionTextView, nameTextView, urlTextView, addressTextView;
    private ImageButton bookmarkButton, applyNowButton;
    private WebView jobDescriptionDataTextView;
    private Nokri_FontManager fontManager;
    private RecyclerView shortDescrptionRecyclerView;
    private List<Nokri_DescriptionModel> shortDescriptionList = new ArrayList<>();
    private Nokri_DescriptionRecyclerViewAdapter shortDescriptionAdapter;
    public  String JOB_ID;
    public static String COMPANY_ID;
    public static String JOB_ID_FOR_LINKEDIN;
    public static ArrayList<String> ids, names;
    private CircularImageView companyLogoImageView;
    private static final DateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
    public static String CALLING_SOURCE = "";
    private RelativeLayout cardContaine4;
    LinearLayout linearlayout;
    View underline, underline2;
    private Dialog dialog;
    private boolean hasAppliedForJob = false;
    private boolean hasJobExpired = true;
    private String jobExpitedText = "";
    private RelativeLayout buttonContainer;
    private String popupTitle;
    private Nokri_DialogManager dialogManager;
    private TextView applyDate;
    boolean isApplyWithoutLogin;
    Button bookmarkTextView;
    private TextView applyJobTextView;
    private TextView linkedinTextView;
    private ImageButton linkedInImageButton;
    private String isCandidate;
    private String onlyCandidateCanApplyMessage;
    private String alreadyAppliedForThisJob;
    private String loginFirst;
    private String onlyCandidateCanBookmark;
    private ImageView locationImageview;
    private ImageButton shareImageButton;
    private TextView shareTextView;
    private String jobUrl;
    boolean applyWithExternal = false;
    String isUrl = "";
    String externalUrl = "";
    String externalEmail = "";
    boolean applyWithoutLogin = false;
    Nokri_SettingsModel settings;
    Nokri_PopupModel popupModel;
    RecyclerViewSkeletonScreen skeletonShortDescription;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    boolean redirection = false;
    String uploadResumeOption = "";
    String whatsAppNo = "";
    ListView nearbyListView;
    TextView nearbyHeading;
    NearbyJobsAdapter nearbyJobsAdapter;
    ArrayList<NearbyJobModel> nearbyList = new ArrayList();
    public Nokri_JobDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nokri_job_detail, container, false);

        if (getArguments() != null) {
            redirection = getArguments().getBoolean("redirection", false);
            JOB_ID = getArguments().getString("job_id");
            JOB_ID_FOR_LINKEDIN = JOB_ID;
        }

        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        loadingLayout = view.findViewById(R.id.shimmerMain);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        shortDescrptionRecyclerView = view.findViewById(R.id.short_description_recyclerview);
        shortDescrptionRecyclerView.setNestedScrollingEnabled(false);
        shortDescriptionAdapter = new Nokri_DescriptionRecyclerViewAdapter(shortDescriptionList, getContext(), 1);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        shortDescrptionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shortDescrptionRecyclerView.setItemAnimator(itemAnimator);
        shortDescrptionRecyclerView.setAdapter(shortDescriptionAdapter);
        nearbyListView = view.findViewById(R.id.nearbyList);
        nearbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (nearbyList.size()!=0){
                    Nokri_JobDetailFragment fragment = new Nokri_JobDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("job_id", String.valueOf(nearbyList.get(i).job_id));
                    bundle.putBoolean("redirection", false);
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(),fragment,nearbyList.get(i).job_title).addToBackStack(null).commit();
                }
            }
        });
        nearbyHeading = view.findViewById(R.id.nearbyHeading);
        nearbyListView.setVisibility(View.GONE);
        nearbyHeading.setVisibility(View.GONE);
        nokri_initialize(view);
        nokri_setupFonts();
        nokri_getJobDetail();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settings = Nokri_SharedPrefManager.getSettings(getActivity());
        popupModel = Nokri_SharedPrefManager.getPopupSettings(getActivity());

        if (Nokri_SharedPrefManager.getEmail(getActivity()) == null) {
            applyWithoutLogin = true;
        } else {
            applyWithoutLogin = false;
        }
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        if (redirection) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        if (Nokri_SharedPrefManager.getHomeType(getActivity()).equals("1")) {
                            Fragment homeScreenFragment = new Nokri_HomeScreenFragment();
                            getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, homeScreenFragment).commit();
                            Nokri_CandidateDashboardModel candidateDashboardModel = new Nokri_CandidateDashboardModel();
                            toolbarTitleTextView.setText(candidateDashboardModel.getDashboard());
                        }
                         else {
                            Fragment homeScreen2Fragment = new Nokri_Home2ScreenFragment();
                            getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, homeScreen2Fragment).commit();
                            Nokri_CandidateDashboardModel candidateDashboardModel = new Nokri_CandidateDashboardModel();
                            toolbarTitleTextView.setText(candidateDashboardModel.getDashboard());
                    }

                }
                    return false;
            }
        });

    }

}


    private void nokri_initialize(View view) {


        fontManager = new Nokri_FontManager();
        view.findViewById(R.id.container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));


        locationImageview = view.findViewById(R.id.img_location);
        locationImageview.setBackground(Nokri_Utils.getColoredXml(getContext(), R.drawable.location_icon));

//        shareImageButton = view.findViewById(R.id.img_btn_share);
        shareTextView = view.findViewById(R.id.txt_share);


        buttonContainer = view.findViewById(R.id.button_container);
//        buttonContainer.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));

      /*  if(CALLING_SOURCE.equals("applied") || Nokri_SharedPrefManager.isAccountEmployeer(getContext()))
            buttonContainer.setVisibility(View.GONE);*/

        jobTitleTextView = view.findViewById(R.id.txt_job_title);

        companyNameTextView = view.findViewById(R.id.txt_company_name);
        applyByTextView = view.findViewById(R.id.txt_apply_by);
        dateTextView = view.findViewById(R.id.txt_date);
        shortDescriptonTextView = view.findViewById(R.id.txt_short_description);
        jobDescriptionTextView = view.findViewById(R.id.txt_job_description);
        jobDescriptionDataTextView = view.findViewById(R.id.txt_job_description_data);
//        WebSettings webSettings = jobDescriptionDataTextView.getSettings();
//        webSettings.setDefaultFontSize(14);
//        webSettings.setSansSerifFontFamily("OpenSans.ttf");

        nameTextView = view.findViewById(R.id.txt_name);
        urlTextView = view.findViewById(R.id.txt_url);
        addressTextView = view.findViewById(R.id.txt_address);

        bookmarkTextView = view.findViewById(R.id.txt_bookmark);
        applyJobTextView = view.findViewById(R.id.txt_apply_job);
//        applyJobTextView.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        applyJobTextView.setBackground(Nokri_Utils.getColoredXml(getContext(), R.drawable.emailaddress));
        linkedinTextView = view.findViewById(R.id.txt_linkedin);
        underline = view.findViewById(R.id.line12);
        underline2 = view.findViewById(R.id.line13);
//        bookmarkButton = view.findViewById(R.id.btn_bookmark);
//        applyNowButton = view.findViewById(R.id.btn_applynow);
//        linkedInImageButton = view.findViewById(R.id.img_btn_linkedin);

        if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {
//            view.findViewById(R.id.right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.middle).setVisibility(View.GONE);
            view.findViewById(R.id.left).setVisibility(View.GONE);

        }

        cardContaine4 = view.findViewById(R.id.card_container4);
        companyLogoImageView = view.findViewById(R.id.img_logo);


        ids = new ArrayList<>();
        names = new ArrayList<>();

        bookmarkTextView.setOnClickListener(this);
        applyJobTextView.setOnClickListener(this);
        cardContaine4.setOnClickListener(this);
        linkedinTextView.setOnClickListener(this);
        shareTextView.setOnClickListener(this);

    }


    private void setupShortDescriptionAdapter() {
        shortDescriptionAdapter.notifyDataSetChanged();
    }


    private void nokri_setupFonts() {


        fontManager.nokri_setMonesrratSemiBioldFont(jobTitleTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(shortDescriptonTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(jobDescriptionTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(nameTextView, getActivity().getAssets());

        fontManager.nokri_setMonesrratSemiBioldFont(bookmarkTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(applyJobTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(linkedinTextView, getActivity().getAssets());
        fontManager.nokri_setMonesrratSemiBioldFont(shareTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(companyNameTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(applyByTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(dateTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(urlTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(addressTextView, getActivity().getAssets());

       /* fontManager.nokri_setOpenSenseFontButton(bookmarkButton,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(applyNowButton,getActivity().getAssets());
*/

    }


    private void nokri_getJobDetail() {
        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("job_id", JOB_ID);
        RestService restService;
        if (Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService = Nokri_ServiceGenerator.createService(RestService.class);
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getJobDetail(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getJobDetail(params, Nokri_RequestHeaderManager.addHeaders());
        }
        Nokri_Utils.isCallRunning = true;
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {


                        JSONObject jsonObject = new JSONObject(responseObject.body().string());


                        JSONObject data = jsonObject.getJSONObject("data");


                        JSONArray jobInfoArray = data.getJSONArray("job_info");
                        JSONArray jobContentArray = data.getJSONArray("job_content");
                        JSONArray companyInfoArray = data.getJSONArray("comp_info");
                        JSONObject jobContentObject = null;
                        if (jobContentArray.length() > 0) {
                            jobContentObject = jobContentArray.getJSONObject(0);
                        }

                        if (data.getString("nearby_jobs_switch").equals("1")){
                            nearbyListView.setVisibility(View.VISIBLE);
                            nearbyHeading.setVisibility(View.VISIBLE);
                            nearbyList.clear();
                            JSONArray nearbyJobsArray = data.getJSONArray("nearby_jobs");
                            for (int i =0;i<nearbyJobsArray.length();i++){
                                NearbyJobModel model = new NearbyJobModel();
                                model.job_id = nearbyJobsArray.getJSONObject(i).getInt("job_id");
                                model.job_title = nearbyJobsArray.getJSONObject(i).getString("job_title");
                                model.comp_name = nearbyJobsArray.getJSONObject(i).getString("comp_name");
                                model.comp_img = nearbyJobsArray.getJSONObject(i).getString("comp_img");
                                model.distance = nearbyJobsArray.getJSONObject(i).getString("distance");
                                nearbyList.add(model);
                            }
                            nearbyJobsAdapter = new NearbyJobsAdapter(getActivity(),nearbyList);
                            nearbyListView.setAdapter(nearbyJobsAdapter);
                            Nokri_Globals.setListViewHeightBasedOnItems(nearbyListView);
                        }

                        JSONArray extrasArray = data.getJSONArray("extras");

                        for (int i = 0; i < extrasArray.length(); i++) {
                            JSONObject extra = extrasArray.getJSONObject(i);
                            if (extra.getString("field_type_name").equals("job_apply"))
                                applyJobTextView.setText(extra.getString("key"));
                            if (extra.getString("field_type_name").equals("short_desc"))
                                shortDescriptonTextView.setText(extra.getString("key"));
                            if (extra.getString("field_type_name").equals("job_desc"))
                                jobDescriptionTextView.setText(extra.getString("key"));
                            if (extra.getString("field_type_name").equals("book_mark"))
                                bookmarkTextView.setText(extra.getString("key"));

                            if (extra.getString("field_type_name").equals("apply_linked"))
                                linkedinTextView.setText(extra.getString("key"));
                            if (extra.getString("field_type_name").equals("linkedin_apply_check")){
                                if (extra.getString("value").equals("0"))
                                    linkedinTextView.setVisibility(View.GONE);
                            }


                            if (extra.getString("field_type_name").equals("job_expired"))
                                jobExpitedText = extra.getString("key");
                            if (extra.getString("field_type_name").equals("page_title")) {
                                TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                                toolbarTitleTextView.setText(extra.getString("key"));
                            }
                            if (extra.getString("field_type_name").equals("cand_apply"))
                                onlyCandidateCanApplyMessage = extra.getString("key");
                            if (extra.getString("field_type_name").equals("cand_bookmark"))
                                onlyCandidateCanBookmark = extra.getString("key");
                            if (extra.getString("field_type_name").equals("is_login"))
                                loginFirst = extra.getString("key");
                            if (extra.getString("field_type_name").equals("already_applied"))
                                alreadyAppliedForThisJob = extra.getString("key");
                            if (extra.getString("field_type_name").equals("share_job")) {
                                shareTextView.setText(extra.getString("key"));
                                jobUrl = extra.getString("value");
                            }
                            if (extra.getString("field_type_name").equals("job_apply_with")) {
                                if (extra.getString("value").equals("exter")) {
                                    applyWithExternal = true;
                                    isUrl = "link";
                                }
                                if (extra.getString("value").equals("mail")) {
                                    applyWithExternal = true;
                                    isUrl = "mail";
                                }
                                if (extra.getString("value").equals("whatsapp")) {
                                    applyWithExternal = true;
                                    isUrl = "whatsapp";
                                }
                            }
                            if (extra.getString("field_type_name").equals("job_apply_url")) {
                                if (!extra.getString("value").equals("")) {
                                    if (isUrl.equals("link")) {
                                        externalUrl = extra.getString("value");
                                    } else if (isUrl.equals("mail")){
                                        externalEmail = extra.getString("value");
                                    }else{
                                        whatsAppNo = extra.getString("value");
                                    }
                                }
                            }
                            if (extra.getString("field_type_name").equals("apply_without_check")) {
                                isApplyWithoutLogin = extra.getBoolean("apply");
                            }
                            if (extra.getString("field_type_name").equals("upload_resume_option")) {
                                uploadResumeOption = extra.getString("value");
                            }
                            if (extra.getString("field_type_name").equals("apply_status")) {
                                if (extra.getBoolean("value")){
                                    applyJobTextView.setBackground(Nokri_Utils.getColoredXmlGreen(getContext(), R.drawable.emailaddress));
                                    applyJobTextView.setText(Nokri_SharedPrefManager.getSettings(getActivity()).getAppliedAlreadyString());
                                }
                            }
                        }


                        for (int i = 0; i < jobInfoArray.length(); i++) {
                            JSONObject jobInfoObject = jobInfoArray.getJSONObject(i);
                            if (jobInfoObject.getString("field_type_name").equals("job_title")) {
                                jobTitleTextView.setText(jobInfoObject.getString("value"));
                                popupTitle = jobInfoObject.getString("value");
                            } else if (jobInfoObject.getString("field_type_name").equals("job_last")) {
                                if (!hasJobExpired) {
                                    dateTextView.setText(jobInfoObject.getString("value"));
                                    applyByTextView.setText(jobInfoObject.getString("key"));
                                }
                            } else if (jobInfoObject.getString("field_type_name").equals("job_cat")) {
                                companyNameTextView.setText(jobInfoObject.getString("value"));

                            } else if (jobInfoObject.getString("field_type_name").equals("is_candidate")) {
                                isCandidate = jobInfoObject.getString("value");
                                if (!isCandidate.equals("1") && !Nokri_SharedPrefManager.isAccountPublic(getContext())){
                                    buttonContainer.setVisibility(View.GONE);
                                }
                            } else if (jobInfoObject.getString("field_type_name").equals("job_apply")) {

                                if (Nokri_SharedPrefManager.isAccountCandidate(getContext())) {
                                    try {
                                        hasAppliedForJob = jobInfoObject.getBoolean("value");
                                    } catch (Exception e) {
                                        hasAppliedForJob = true;
                                        e.printStackTrace();
                                    }

                                }
                                continue;
                            } else if (jobInfoObject.getString("field_type_name").equals("job_expire")) {
                                hasJobExpired = jobInfoObject.getBoolean("value");
                                /* companyNameTextView.setText(jobInfoObject.getString("value"));*/
                                //   if(Nokri_SharedPrefManager.isAccountCandidate(getContext())) {
                                if (hasJobExpired) {

                                    applyByTextView.setText(jobExpitedText);
                                    getView().findViewById(R.id.container).setBackgroundColor(getResources().getColor(R.color.google_red));
                                    dateTextView.setVisibility(View.GONE);
                                    buttonContainer.setVisibility(View.GONE);
                                }

                                // }
                                continue;
                            } else {
                                if (jobInfoObject.getString("value").trim().equals(""))
                                    continue;
                                Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                model.setTitle(jobInfoObject.getString("key"));
                                model.setDescription(jobInfoObject.getString("value"));
                                shortDescriptionList.add(model);
                            }

                        }
                        shortDescriptionAdapter.notifyDataSetChanged();


                        jobDescriptionDataTextView.loadDataWithBaseURL("javascript:(function () {document.getElementsByTagName('body')[0].style.marginBottom = '0'})()", jobContentObject.getString("value"), "text/html", "utf-8", null);
//                        jobDescriptionDataTextView.loadData(jobContentObject.getString("value"), "text/html; charset=utf-8", null);
                        jobDescriptionDataTextView.getSettings().setAllowFileAccessFromFileURLs(false);
                        jobDescriptionDataTextView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
                        jobDescriptionDataTextView.getSettings().setJavaScriptEnabled(true);
                        jobDescriptionDataTextView.getSettings().setPluginState(WebSettings.PluginState.ON);
                        jobDescriptionDataTextView.getSettings().setDefaultFontSize(14);
                        jobDescriptionDataTextView.getSettings().setSansSerifFontFamily("OpenSans.ttf");

                        for (int i = 0; i < companyInfoArray.length(); i++) {
                            JSONObject companyInfoObject = companyInfoArray.getJSONObject(i);
                            if (companyInfoObject.getString("field_type_name").equals("comp_img")) {
                                if (!TextUtils.isEmpty(companyInfoObject.getString("value")))
                                    Picasso.get().load(companyInfoObject.getString("value")).fit().centerCrop().into(companyLogoImageView);

                            } else if (companyInfoObject.getString("field_type_name").equals("comp_name")) {
                                nameTextView.setText(companyInfoObject.getString("value"));

                            } else if (companyInfoObject.getString("field_type_name").equals("comp_web")) {
                                urlTextView.setText(companyInfoObject.getString("value"));

                            } else if (companyInfoObject.getString("field_type_name").equals("company_adress")) {
                                addressTextView.setText(companyInfoObject.getString("value"));
                                if (companyInfoObject.getString("value").trim().equals("") || companyInfoObject.getString("value").trim().isEmpty())
                                    locationImageview.setVisibility(View.GONE);
                            }


                             /*else{
                                 Nokri_DescriptionModel model = new Nokri_DescriptionModel();
                                 model.setDescription(companyInfoObject.getString("value"));
                                 model.setTitle(companyInfoObject.getString("key"));
                                 personalInfoList.add(model);
                             }
*/
                        }
                        //                      setupPersonalInfoAdapter();


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
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
                if (skeletonShortDescription!=null)
                    skeletonShortDescription.hide();
            }
        });

    }

//Apply Job Old with dialog
//    private void nokri_getPopupLabels() {
//        JsonObject params = new JsonObject();
//        params.addProperty("job_id", JOB_ID);
//        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(getActivity());
//        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
//
//        Call<ResponseBody> myCall;
//        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
//            myCall = restService.getApplyJobPopup(params, Nokri_RequestHeaderManager.addSocialHeaders());
//        } else {
//            myCall = restService.getApplyJobPopup(params, Nokri_RequestHeaderManager.addHeaders());
//        }
//        myCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
//                if (responseObject.isSuccessful()) {
//                    try {
//                        ids = new ArrayList<>();
//                        names = new ArrayList<>();
//                        String headingText = null, coverText = null;
//                        String buttonText = null, selectResumeText = null;
//
//
//                        JSONObject response = new JSONObject(responseObject.body().string());
//                        JSONObject data = response.getJSONObject("data");
//                        JSONArray infoArray = data.getJSONArray("info");
//                        for (int i = 0; i < infoArray.length(); i++) {
//
//                            JSONObject infoObject = infoArray.getJSONObject(i);
//                            if (infoObject.getString("field_type_name").equals("job_apply")) {
//                                headingText = infoObject.getString("key");
//                            } else if (infoObject.getString("field_type_name").equals("job_resume")) {
//                                selectResumeText = infoObject.getString("key");
//                            } else if (infoObject.getString("field_type_name").equals("job_cvr")) {
//                                coverText = infoObject.getString("key");
//                            } else if (infoObject.getString("field_type_name").equals("job_btn")) {
//                                buttonText = infoObject.getString("key");
//                            }
//
//                        }
//
//
//                        JSONArray filterArray = data.getJSONArray("resumes");
//
//                        for (int i = 0; i < filterArray.length(); i++) {
//                            JSONObject valueObject = filterArray.getJSONObject(i);
//                            ids.add(valueObject.getString("key"));
//                            names.add(valueObject.getString("value"));
//                        }
//
//
//                        nokri_showDialog(headingText, ids, names, coverText, buttonText, selectResumeText);
//
//                        //   Log.d("Pointz",modelList.toString());
//
//                        dialogManager.hideAfterDelay();
//                    } catch (JSONException e) {
//                        dialogManager.showCustom(e.getMessage());
//                        dialogManager.hideAfterDelay();
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        dialogManager.showCustom(e.getMessage());
//                        dialogManager.hideAfterDelay();
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    dialogManager.showCustom(responseObject.message());
//                    dialogManager.hideAfterDelay();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
//                dialogManager.hideAfterDelay();
//            }
//        });
//    }

    private void applyJobWithExternalEmail(String resumeId, String coverLetter, JsonArray answers, String name, String email) {
        JsonObject params = new JsonObject();
        params.addProperty("job_id", JOB_ID);
        if (answers != null)
            params.add("questions_ans", answers);
        if (name != null) {
            params.addProperty("cand_name", name);
            params.addProperty("cand_email", email);

        }
        params.addProperty("cand_apply_resume", resumeId);
        params.addProperty("cand_cover_letter", coverLetter);
        params.addProperty("cand_date", sdf.format(new Date()));
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postApplyJobWithExternalEmail(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postApplyJobWithExternalEmail(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {


                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                        hasAppliedForJob = true;

                        dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAlertDialog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAlertDialog();
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


    private void nokri_applyJob(String resumeId, String coverLetter, JsonArray answers, String name, String email) {
        JsonObject params = new JsonObject();
        if (answers != null)
            params.add("questions_ans", answers);
        if (name != null) {
            params.addProperty("cand_name", name);
            params.addProperty("cand_email", email);

        }
        params.addProperty("job_id", JOB_ID);
        params.addProperty("cand_apply_resume", resumeId);
        params.addProperty("cand_cover_letter", coverLetter);
        params.addProperty("cand_date", sdf.format(new Date()));
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postApplyJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postApplyJob(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {


                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(), response.getString("message"));
                        if (!response.getBoolean("success")) {
                            Nokri_ToastManager.showShortToast(getActivity(), response.getString("message"));
                        } else {
                            hasAppliedForJob = true;
                        }
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();

                        dialogManager.hideAfterDelay();
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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    private void nokri_shareLink() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        // shareIntent.putExtra(Intent.EXTRA_TEXT, "www.google.com");
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT, jobTitleTextView.getText());
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, jobTitleTextView.getText());
        shareIntent.putExtra(Intent.EXTRA_TEXT, jobUrl);


        // Launch sharing dialog for image
        startActivity(Intent.createChooser(shareIntent, "Share Job"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_share:
                nokri_shareLink();
                break;
            case R.id.txt_bookmark:
                if (isCandidate.equals("1") && !Nokri_SharedPrefManager.isAccountPublic(getContext()))
                    nokri_bookmarkJob();
                else {

                    Nokri_ToastManager.showLongToast(getContext(), onlyCandidateCanBookmark);
                }
                break;
            case R.id.txt_apply_job:
                if (isCandidate.equals("1") && !Nokri_SharedPrefManager.isAccountPublic(getContext())) {

                    if (!hasAppliedForJob)
                        if (applyWithExternal) {
                            if (isUrl.equals("link")) {
                                showRedirectDialog(settings.getExternalApplyText(),
                                        settings.getExternalApplyText(), popupModel.getConfirmText(), popupModel.getCancelButton());
                            } else if (isUrl.equals("mail")){
                                Intent i = new Intent(getActivity(), ApplyJobActivity.class);
                                i.putExtra("job_id",JOB_ID);
                                i.putExtra("uploadResumeOption",uploadResumeOption);
                                startActivityForResult(i, 321);
                            }else{
                                showRedirectDialogWhatsApp(settings.getExternalApplyText(),
                                        settings.getExternalApplyText(), popupModel.getConfirmText(), popupModel.getCancelButton());


                            }
                        } else {
                            Intent i = new Intent(getActivity(), ApplyJobActivity.class);
                            i.putExtra("job_id",JOB_ID);
                            i.putExtra("uploadResumeOption",uploadResumeOption);
                            startActivityForResult(i, 321);
                        }
//                            nokri_getPopupLabels();
                    else
                        Nokri_ToastManager.showLongToast(getContext(), alreadyAppliedForThisJob);
                } else {
                    if (isApplyWithoutLogin) {
                        if (applyWithExternal) {
                            if (isUrl.equals("link")) {
                                showRedirectDialog(settings.getExternalApplyText(),
                                        settings.getExternalApplyText(), popupModel.getConfirmText(), popupModel.getCancelButton());
                            } else if (isUrl.equals("mail")){
                                Intent i = new Intent(getActivity(), ApplyJobActivity.class);
                                i.putExtra("job_id",JOB_ID);
                                i.putExtra("uploadResumeOption",uploadResumeOption);
                                i.putExtra("applyWithoutLogin", true);
                                startActivityForResult(i, 321);
                            }else{
                                showRedirectDialogWhatsApp(settings.getExternalApplyText(),
                                        settings.getExternalApplyText(), popupModel.getConfirmText(), popupModel.getCancelButton());
                            }
                        } else {
                            Intent i = new Intent(getActivity(), ApplyJobActivity.class);
                            i.putExtra("job_id",JOB_ID);
                            i.putExtra("uploadResumeOption",uploadResumeOption);
                            i.putExtra("applyWithoutLogin", true);
                            startActivityForResult(i, 321);
                        }
                    } else {
                        Nokri_PopupManager popupManager = new Nokri_PopupManager(getActivity(),this);
                        showLoginDialog(onlyCandidateCanBookmark);
                        Nokri_ToastManager.showLongToast(getContext(), onlyCandidateCanBookmark);
                    }
                }
                break;

            case R.id.card_container4:
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = this.COMPANY_ID;

                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();

                break;
            case R.id.txt_linkedin:
                //  if(Nokri_Utils.isAppInstalled(getContext(),"com.linkedin.android")){

                if (!Nokri_SharedPrefManager.isAccountPublic(getActivity())) {
                    if (!Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {
                        Nokri_SigninActivity.IS_SOURCE_LOGIN = false;
//                        getContext().startActivity(new Intent(getContext(), Nokri_LinkedinProfileActivity.class));
                        if (Nokri_Globals.LinkedInUrl.equals(""))
                            showLinkedInApplyDialog();
                        else {
                            Nokri_SigninActivity.nokri_applyjobLinkedIn(getActivity(), Nokri_Globals.LinkedInUrl);
                        }
//                        if (!Nokri_Config.LINKEDIN_CLIENT_ID.equals("Enter Your LINKEDIN_CLIENT_ID here"))
//                            if (Nokri_SharedPrefManager.getLinkedInPublicProfile(getActivity())!=null){
//                            }else{
//                                Nokri_ToastManager.showShortToast(getActivity(),"Please login with LinkedIn first");
////                                LinkedInBuilder.getInstance(getActivity())
////                                        .setClientID(Nokri_Config.LINKEDIN_CLIENT_ID)
////                                        .setClientSecret(Nokri_Config.LINKEDIN_CLIENT_SECRET)
////                                        .setRedirectURI(Nokri_Config.LINKEDIN_REDIRECT_URL)
////                                        .authenticate(25);
//                            }
//                        else {
//                            Nokri_ToastManager.showShortToast(getActivity(),"Invalid LinkedIn Credentials");
//                        }
                    } else {

                        Nokri_ToastManager.showLongToast(getContext(), onlyCandidateCanBookmark);

                    }
                } else {
                    Nokri_ToastManager.showLongToast(getActivity(), onlyCandidateCanBookmark);
                }

                //}
                //else
                //  Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.APP_NOT_INSTALLED);
                break;
        }
    }


    private void showLoginDialog(String title) {
        popupModel = Nokri_SharedPrefManager.getPopupSettings(getActivity());
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_delete_jobs);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        deleteJobTextView.setText(title);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        final Button closeButton = dialog.findViewById(R.id.btn_close);

        confirmButton.setText(popupModel.getConfirmButton());
//        deleteJobTextView.setText(popupModel.getConfirmText());
        closeButton.setText(popupModel.getCancelButton());
        fontManager.nokri_setMonesrratSemiBioldFont(deleteJobTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(closeButton, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(confirmButton, getActivity().getAssets());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                        Intent i = new Intent(getActivity(),Nokri_SigninActivity.class);
                        startActivity(i);
                        getActivity().finish();
            }
        });
        dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));
    }


    private void showLinkedInApplyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.linkedin_apply_layout, null);  // this line
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        EditText editText = v.findViewById(R.id.linkedinUrl);
        editText.setHint(Nokri_Globals.LinkedInHint);
        TextView cancel = v.findViewById(R.id.cancel_button);
        ImageView linkedInApply = v.findViewById(R.id.linkedInApply);
        linkedInApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("") && Patterns.WEB_URL.matcher(editText.getText().toString()).matches()) {
                    Nokri_Globals.LinkedInUrl = editText.getText().toString();
                    dialog.dismiss();
                    Nokri_SigninActivity.nokri_applyjobLinkedIn(getActivity(), editText.getText().toString());
                } else {
                    Nokri_ToastManager.showShortToast(getActivity(), Nokri_Globals.LinkedInValidUrl);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    private void nokri_bookmarkJob() {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("job_id", JOB_ID);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.bookmarkJob(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.bookmarkJob(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {


                        JSONObject jsonObject = new JSONObject(responseObject.body().string());


                        Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));


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
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


//    public void nokri_showDialog(String headingText, final ArrayList<String> ids, ArrayList<String> names, String coverText, String buttonText, String selectResumeText) {
//
//
//        dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.apply_job_popup);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        final TextFieldBoxes textFieldBoxes = dialog.findViewById(R.id.text_field_boxes);
//        textFieldBoxes.setLabelText(coverText);
//
//        final RelativeLayout container = dialog.findViewById(R.id.container1);
//        dialog.findViewById(R.id.heading).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
//        params.width = dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth() - 100;
//        container.setLayoutParams(params);
//        EditText editText;
//        textFieldBoxes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b)
//                    textFieldBoxes.setPrimaryColor(Color.parseColor(Nokri_Config.APP_COLOR));
//                else {
//                    //  textFieldBoxes.setPrimaryColor(getResources().getColor(R.color.grey));
//                }
//            }
//        });
//
//        TextView headerTextView = dialog.findViewById(R.id.txt_header);
//        TextView selectResumeTextView = dialog.findViewById(R.id.txt_select_resume);
//        headerTextView.setText(popupTitle);
//        selectResumeTextView.setText(selectResumeText);
//        final ExtendedEditText coverLetterEditText = dialog.findViewById(R.id.edittxt_cover);
//
//
//        final Spinner resumeSpinner = dialog.findViewById(R.id.spinner_resume);
//        resumeSpinner.setAdapter(new Nokri_SpinnerAdapter(getActivity().getBaseContext(), R.layout.spinner_item_popup, names));
//        resumeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != 0)
//                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));
//
//                if (ids != null && !ids.isEmpty()) {
//                    ids.get(resumeSpinner.getSelectedItemPosition());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        Button applyNowButton = dialog.findViewById(R.id.btn_ápplynow);
//        applyNowButton.setText(buttonText);
//        Nokri_Utils.setRoundButtonColor(getContext(), applyNowButton);
//        applyNowButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (applyWithExternal){
//                    if (isUrl){
//                        showRedirectDialog("Confirmation!",
//                                "You are redirecting to external apply","Confirm","Cancel");
//                    }else{
//                        if (ids != null && !ids.isEmpty() && coverLetterEditText != null && !coverLetterEditText.getText().toString().isEmpty())
//                            applyJobWithExternalEmail(ids.get(resumeSpinner.getSelectedItemPosition()), coverLetterEditText.getText().toString());
//                            //External Email
//                        else
//                            Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
//                    }
//                }else{
//                    if (ids != null && !ids.isEmpty() && coverLetterEditText != null && !coverLetterEditText.getText().toString().isEmpty())
//                        nokri_applyJob(ids.get(resumeSpinner.getSelectedItemPosition()), coverLetterEditText.getText().toString());
//
//                    else
//                        Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.EMPTY_FIELDS_PLACEHOLDER);
//                }
//
//
//            }
//        });
//        fontManager.nokri_setOpenSenseFontButton(applyNowButton, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(headerTextView, getActivity().getAssets());
//        fontManager.nokri_setMonesrratSemiBioldFont(selectResumeTextView, getActivity().getAssets());
//        fontManager.nokri_setOpenSenseFontEditText(coverLetterEditText, getActivity().getAssets());
//        ImageView imageClose = dialog.findViewById(R.id.img_close);
//        imageClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//
//    }

    public void showRedirectDialogWhatsApp(String title, String message, String ok, String cancel) {


        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.redirect_dialog);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        TextView messageTextView = dialog.findViewById(R.id.txt_message);
        deleteJobTextView.setText(title);
        messageTextView.setText(message);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        confirmButton.setText(ok);

        final Button closeButton = dialog.findViewById(R.id.btn_close);
        closeButton.setText(cancel);
        fontManager.nokri_setMonesrratSemiBioldFont(deleteJobTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(closeButton, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(confirmButton, getActivity().getAssets());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + whatsAppNo + "&text=" + "");
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(sendIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), Nokri_Globals.INVALID_URL, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));
    }

    public void showRedirectDialog(String title, String message, String ok, String cancel) {


        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.redirect_dialog);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        TextView messageTextView = dialog.findViewById(R.id.txt_message);
        deleteJobTextView.setText(title);
        messageTextView.setText(message);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        confirmButton.setText(ok);

        final Button closeButton = dialog.findViewById(R.id.btn_close);
        closeButton.setText(cancel);
        fontManager.nokri_setMonesrratSemiBioldFont(deleteJobTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(closeButton, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(confirmButton, getActivity().getAssets());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    requestRedirectUrl();
                    dialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), Nokri_Globals.INVALID_URL, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));
    }

    public void requestRedirectUrl() {

        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("job_id", JOB_ID);
        RestService restService;
        if (Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService = Nokri_ServiceGenerator.createService(RestService.class);
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postExternalUrl(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postExternalUrl(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {

                    try {


                        JSONObject jsonObject = new JSONObject(responseObject.body().string());


                        if (jsonObject.getBoolean("success")) {
                            if (!externalUrl.startsWith("http://") && !externalUrl.startsWith("https://"))
                                externalUrl = "http://" + externalUrl;
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl));
                            startActivity(browserIntent);
                        } else {
                            Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
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


    private void nokri_shareJob() {

        Uri bmpUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            bmpUri = nokri_getLocalBitmapUriPostLollipop(companyLogoImageView);

        } else
            bmpUri = getLocalBitmapUri(companyLogoImageView);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "www.google.com");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, jobTitleTextView.getText());

            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            // ...sharing failed, handle error
        }

    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    private Uri nokri_getLocalBitmapUriPostLollipop(ImageView imageView) {

        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;

        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            return FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                String id = data.getExtras().getString("id");
                String coverLetter = data.getExtras().getString("coverLetter");
                String name = data.getExtras().getString("name");
                String email = data.getExtras().getString("email");

                ArrayList<String> answers = null;
                if (data.getExtras().getStringArrayList("answersList") != null && data.getExtras().getStringArrayList("answersList").size() != 0) {
                    answers = data.getExtras().getStringArrayList("answersList");
                }
                if (applyWithExternal && !isUrl.equals("link")) {
                    if (answers != null && answers.size() != 0) {
                        JsonArray answersArray = new JsonArray();
                        for (int i = 0; i < answers.size(); i++) {
                            answersArray.add(answers.get(i));
                        }
                        if (applyWithoutLogin)
                            applyJobWithExternalEmail(id, coverLetter, answersArray, name, email);
                        else
                            applyJobWithExternalEmail(id, coverLetter, answersArray, null, null);

                    } else {
                        if (applyWithoutLogin)
                            applyJobWithExternalEmail(id, coverLetter, null, name, email);
                        else
                            applyJobWithExternalEmail(id, coverLetter, null, null, null);
                    }
                } else {
                    if (answers != null && answers.size() != 0) {
                        JsonArray answersArray = new JsonArray();
                        for (int i = 0; i < answers.size(); i++) {
                            answersArray.add(answers.get(i));
                        }
                        if (applyWithoutLogin)
                            nokri_applyJob(id, coverLetter, answersArray, name, email);
                        else
                            nokri_applyJob(id, coverLetter, answersArray, null, null);

                    } else {
                        if (applyWithoutLogin)
                            nokri_applyJob(id, coverLetter, null, name, email);
                        else
                            nokri_applyJob(id, coverLetter, null, null, null);
                    }
                }
            }
        } else if (requestCode == 25) {
            if (resultCode == RESULT_OK) {
                //Successfully signed in
                LinkedInUser user = data.getParcelableExtra("social_login");

                //acessing user info
                Log.i("LinkedInLogin", user.getId());
                Log.i("LinkedInLogin", user.getFirstName());
                Log.i("LinkedInLogin", user.getLastName());
                Log.i("LinkedInLogin", user.getAccessToken());
                Log.i("LinkedInLogin", user.getProfileUrl());
                Log.i("LinkedInLogin", user.getEmail());


                Nokri_SharedPrefManager.saveLinkedinPublicProfile(data.getStringExtra("publicProfileUrl"), getActivity());

//                Nokri_SigninActivity.nokri_applyjobLinkedIn(getActivity());

            }
        }
    }

    @Override
    public void onConfirmClick(Dialog dialog) {
    }
}

