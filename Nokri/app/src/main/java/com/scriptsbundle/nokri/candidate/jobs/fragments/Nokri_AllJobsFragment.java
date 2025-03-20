package com.scriptsbundle.nokri.candidate.jobs.fragments;


import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.scriptsbundle.nokri.candidate.jobs.adapters.Nokri_JobsAdapter;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.scriptsbundle.nokri.guest.search.models.Nokri_JobSearchModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_AllJobsFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private Nokri_JobsAdapter adapter;
    private List<Nokri_JobsModel> modelList;
    private TextView emptyTextView, noOfJobs;
    //    View view;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private int nextPage = 1;
    private boolean hasNextPage = true;
    Nokri_CandidateDashboardActivity candidateDashboardActivity;
    private Button loadMoreButton;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    private int filterNextPage = 1;
    private boolean isFilterNetPage = false;
    String stringCAT_keyName, ad_title, MilesNumber, searchEditText = "";
    ImageView imageViewCollapse;
    private String filterText = "";
    private ProgressBar progressBar;
    String job_title, e_distance, requestForm = "", categoryId = "";
    double e_lat, e_long;
    LinearLayout linearLayoutCollapse, linearLayoutCustom;
    NestedScrollView nestedScroll;
    private LinearLayout searchNow;
    private int pagenumber;
    LinearLayout textLinear;
    double longtitude, latitude;
    public static String ALL_JOBS_SOURCE = "";
    private Nokri_DialogManager dialogManager;
    public static String myId;
    private Boolean spinnerTouched2 = false, checkRequest = false;


    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    public Nokri_AllJobsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_nokri_all_jobs, container, false);


    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);


        swipeRefreshLayout.setOnRefreshListener(() -> {

            checkLoading = true;
//            adforest_swipeRefresh();
            nokri_recreate_Submit_jobSearch();

        });


        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        if (Nokri_Globals.DESIGN_TYPE==1){
            mainLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        noOfJobs = getView().findViewById(R.id.noofjobs);
        textLinear = getView().findViewById(R.id.txt_linear);
        noOfJobs.setTextColor(Color.BLACK);
//       noOfJobs.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(noOfJobs, getActivity().getAssets());
        emptyTextView = getView().findViewById(R.id.txt_empty);
//        emptyTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView, getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        imageViewCollapse = getActivity().findViewById(R.id.collapse);
        linearLayoutCollapse = getView().findViewById(R.id.linearLayout);
        nestedScroll = getView().findViewById(R.id.scrollViewUp);
//        view = view.findViewById(R.id.line);
        Nokri_Utils.setRoundButtonColor(getContext(), loadMoreButton);
        new Nokri_FontManager().nokri_setOpenSenseFontButton(loadMoreButton, getActivity().getAssets());
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        if (ALL_JOBS_SOURCE.equals("")) {
            textLinear.setVisibility(View.GONE);
        }
        modelList = new ArrayList<>();

        progressBar = getView().findViewById(R.id.progress_bar);
        nextPage = 1;
        isFilterNetPage = false;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            job_title = bundle.getString("job_title", "");
            e_lat = bundle.getDouble("e_lat");
            e_long = bundle.getDouble("e_long");
            e_distance = bundle.getString("e_distance", "");
            pagenumber = bundle.getInt("nextPage");
            requestForm = bundle.getString("requestFrom", "");
            categoryId = bundle.getString("job_category", "");
        }
        if (requestForm != null && requestForm.equals("Home")) {
            Submit_jobSearch(true);
        } else {
            if (ALL_JOBS_SOURCE.equals(""))
                nokri_loadMore(true, filterText);

            else {
                nokri_filterJobsExternal(true);
            }
        }


        loadMoreButton.setOnClickListener(this);
    }

    public void nokri_recreate_Submit_jobSearch() {
        Nokri_AllJobsFragment nokri_allJobsFragment = new Nokri_AllJobsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("job_title", job_title);
        bundle.putString("job_category", categoryId);
        bundle.putDouble("e_lat", e_lat);
        bundle.putDouble("e_long", e_long);
        bundle.putString("e_distance", e_distance);
        bundle.putInt("page_number", pagenumber);
        bundle.putString("requestFrom", requestForm);
        Log.d("infoparamsofrefresh", bundle.toString());
        nokri_allJobsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, nokri_allJobsFragment);
        transaction.commit();
    }

    private void Submit_jobSearch(final boolean showAlert) {

        JsonObject params = new JsonObject();
        params.addProperty("page_number", nextPage);
        params.addProperty("job_title", job_title);
        params.addProperty("e_lat", e_lat);
        params.addProperty("e_long", e_long);
        params.addProperty("e_distance", e_distance);
        if (!categoryId.isEmpty()) {
            params.addProperty("job_category", categoryId);
        }
        Log.d("infoparams", params.toString());
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addSocialHeaders());
            Log.d("params", params.toString());
        } else {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addHeaders());
            Log.d("elseparams", params.toString());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if (responseObject.isSuccessful()) {
                    try {


                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());

                        JSONObject pagination = response.getJSONObject("pagination");

                        if (!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");
                        noOfJobs.setText(data.getString("no_txt"));
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));
                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                        }
//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            setupAdapter();
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();

                            for (int j = 0; j < dataArray.length(); j++) {
                                model.setShowMenu(false);
                                JSONObject object = dataArray.getJSONObject(j);
                                String s = object.getString("field_type_name");
                                if ("job_id".equals(s)) {
                                    model.setJobId(object.getString("value"));
                                } else if ("company_id".equals(s)) {
                                    model.setCompanyId(object.getString("value"));
                                } else if ("job_name".equals(s)) {
                                    model.setJobTitle(object.getString("value"));
                                } else if ("company_name".equals(s)) {
                                    model.setJobDescription(object.getString("value"));
                                } else if ("job_salary".equals(s)) {
                                    model.setSalary(object.getString("value"));
                                } else if ("job_type".equals(s)) {
                                    model.setJobType(object.getString("value"));
                                } else if ("company_logo".equals(s)) {
                                    model.setCompanyLogo(object.getString("value"));
                                } else if ("job_location".equals(s)) {
                                    model.setAddress(object.getString("value"));
                                } else if ("job_time".equals(s)) {
                                    model.setTimeRemaining(object.getString("value"));
                                }
                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        setupAdapter();
                        if (!hasNextPage) {

                            progressBar.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                        if (showAlert)
                            dialogManager.hideAfterDelay();
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    private void nokri_loadMore(final Boolean showAlert, String text) {
        Log.d("sdfaljdfklasdjfasl", "called");
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }
        RestService restService;
        if (!Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();
        if (!isFilterNetPage)
            params.addProperty("page_number", nextPage);
        else
            params.addProperty("page_number", filterNextPage);
        if (!text.equals("")) {
            params.addProperty("keyword", text);
        }

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getAllJobsAddMore(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getAllJobsAddMore(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if (responseObject.isSuccessful()) {
                    try {

                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        if (!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");

                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));

                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            setupAdapter();
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();

                            for (int j = 0; j < dataArray.length(); j++) {
                                JSONObject object = dataArray.getJSONObject(j);

                                if (object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location")) {
                                    model.setAddress(object.getString("value"));

                                }else if (object.getString("field_type_name").equals("is_feature")) {
                                    model.setFeatured(object.getBoolean("value"));

                                } else if (object.getString("field_type_name").equals("job_posted"))
                                    model.setTimeRemaining(object.getString("value"));
                                if (j + 1 == dataArray.length()) {
                                    model.setShowMenu(false);
                                    modelList.add(model);
                                }
                            }

                        }
                        setupAdapter();
                        if (!hasNextPage) {

                            progressBar.setVisibility(View.GONE);
                        }
                        //progressBar.setVisibility(View.GONE);
                        if (showAlert)
                            dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (showAlert) {
                    mainLayout.setVisibility(View.VISIBLE);
                    shimmerContainer.stopShimmer();
                    shimmerContainer.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    Nokri_Utils.isCallRunning = false;
                    dialogManager.hideAfterDelay();
                }
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
            }
        });
    }


    public void nokri_filterJobsExternal(final boolean showAlert) {

        Nokri_JobSearchModel jobSearchModel = Nokri_SharedPrefManager.getJobSearchModel(getContext());

        JsonObject params = new JsonObject();
        params.addProperty("page_number", nextPage);


        String jobCategory = "";
        if (jobSearchModel.getSubCategory3().equals("") && jobSearchModel.getSubCategory2().equals("") && jobSearchModel.getSubCategory1().equals(""))
            jobCategory = jobSearchModel.getJobCategory();
        else if (jobSearchModel.getSubCategory2().equals("") && jobSearchModel.getSubCategory1().equals(""))
            jobCategory = jobSearchModel.getSubCategory3();

        else if (jobSearchModel.getSubCategory1().equals(""))
            jobCategory = jobSearchModel.getSubCategory2();
        else
            jobCategory = jobSearchModel.getSubCategory1();


        if (!jobSearchModel.getSearchNow().equals(""))
            params.addProperty("job_title", jobSearchModel.getSearchNow());
        if (!jobSearchModel.getJobCategory().equals(""))

            params.addProperty("job_category", jobCategory);
        if (!jobSearchModel.getJobQualification().equals(""))
            params.addProperty("job_qualifications", jobSearchModel.getJobQualification());
        if (!jobSearchModel.getJobType().equals(""))
            params.addProperty("job_type", jobSearchModel.getJobType());
        if (!jobSearchModel.getSalaryCurrency().equals(""))
            params.addProperty("job_currency", jobSearchModel.getSalaryCurrency());
        if (!jobSearchModel.getJobShift().equals(""))
            params.addProperty("job_shift", jobSearchModel.getJobShift());
        if (!jobSearchModel.getJobLevel().equals(""))
            params.addProperty("job_level", jobSearchModel.getJobLevel());
        if (!jobSearchModel.getJobSkills().equals(""))
            params.addProperty("job_tags", jobSearchModel.getJobSkills());
        if (jobSearchModel.getLocation() != null)
            if (!jobSearchModel.getLocation().trim().isEmpty())
                params.addProperty("ad_location", jobSearchModel.getLocation());
        if (jobSearchModel.getLocation()!=null){
            if (!jobSearchModel.getLatitude().equals("")){

                params.addProperty("e_lat", jobSearchModel.getLatitude());
                params.addProperty("e_long", jobSearchModel.getLongitude());
                params.addProperty("e_distance", jobSearchModel.getDistance());
            }
        }


//        Log.v("zzzzzzzzz", params.toString());
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
//            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addSocialHeaders());

        } else {
            myCall = restService.postFilters(params, Nokri_RequestHeaderManager.addHeaders());

        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if (responseObject.isSuccessful()) {
                    try {

                        emptyTextView.setText("");
//
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        if (!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");
//
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(data.getString("page_title"));
                        noOfJobs.setText(data.getString("no_txt"));

                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else
                            loadMoreButton.setVisibility(View.VISIBLE);

//                        educationEditText.setAdapter(new Nokri_SpinnerAdapter(getContext(),R.layout.spinner_item_popup,educationList));


                        JSONArray companiesArray = data.getJSONArray("jobs");
                        if (companiesArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);

                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            setupAdapter();
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
                        for (int i = 0; i < companiesArray.length(); i++) {
                            JSONArray dataArray = companiesArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();

                            for (int j = 0; j < dataArray.length(); j++) {
                                model.setShowMenu(false);
                                JSONObject object = dataArray.getJSONObject(j);
                                String s = object.getString("field_type_name");
                                if ("job_id".equals(s)) {
                                    model.setJobId(object.getString("value"));
                                } else if ("company_id".equals(s)) {
                                    model.setCompanyId(object.getString("value"));
                                } else if ("job_name".equals(s)) {
                                    model.setJobTitle(object.getString("value"));
                                } else if ("company_name".equals(s)) {
                                    model.setJobDescription(object.getString("value"));
                                } else if ("job_salary".equals(s)) {
                                    model.setSalary(object.getString("value"));
                                } else if ("job_type".equals(s)) {
                                    model.setJobType(object.getString("value"));
                                } else if ("company_logo".equals(s)) {
                                    model.setCompanyLogo(object.getString("value"));
                                } else if ("job_location".equals(s)) {
                                    model.setAddress(object.getString("value"));
                                }else if (object.getString("field_type_name").equals("is_feature")) {
                                    model.setFeatured(object.getBoolean("value"));
                                } else if ("job_time".equals(s)) {
                                    model.setTimeRemaining(object.getString("value"));
                                }
                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        setupAdapter();
                        if (!hasNextPage) {

                            progressBar.setVisibility(View.GONE);
                        }
                        //progressBar.setVisibility(View.GONE);
                        if (showAlert)
                            dialogManager.hideAfterDelay();
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });

    }


    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_JobsAdapter(modelList, getContext(), new Nokri_JobsAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(Nokri_JobsModel item) {

            }

            @Override
            public void onCompanyClick(Nokri_JobsModel item) {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();
                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void menuItemSelected(Nokri_JobsModel model, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_view_job:
                        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                        Nokri_JobDetailFragment.CALLING_SOURCE = "";
                        Bundle bundle = new Bundle();
                        bundle.putString("job_id",model.getJobId());
                        jobDetailFragment.setArguments(bundle);
                        Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
                        break;
//                    case R.id.menu_view_company_profile:
//                        android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
//                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
//                        Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();
//
//                        Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
//                        fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();
//                        break;
                }

            }
        });
        adapter.setOnJobClickListener(new Nokri_JobsAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();
            }
        });
        adapter.setOnImageClickListener(new Nokri_JobsAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Nokri_JobsModel model) {
                androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
//                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();
//
//                Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
//                fragmentTransaction2.add(getActivity().findViewById(R.id.fragment_placeholder).getId(), companyPublicProfileFragment).addToBackStack(null).commit();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), jobDetailFragment).addToBackStack(null).commit();

            }
        });
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
     /*   recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems =((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("stufffffffff", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data

                            if(hasNextPage) {
                                progressBar.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                }
            }
        });*/
        adapter.notifyDataSetChanged();


        //   Nokri_DialogManager.hideAfterDelay();
    }

//    private void nokri_filterJobs(String text) {
//        JsonObject params = new JsonObject();
//        params.addProperty("keyword", text);
//
//        dialogManager = new Nokri_DialogManager();
//        dialogManager.showAlertDialog(getActivity());
//        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
//
//        Call<ResponseBody> myCall;
//        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
//            myCall = restService.filterAllJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
//        } else {
//            myCall = restService.filterAllJobs(params, Nokri_RequestHeaderManager.addHeaders());
//        }
//        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
//        myCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
//                if (responseObject.isSuccessful()) {
//                    try {
//                        modelList = new ArrayList<>();
//
//                        JSONObject response = new JSONObject(responseObject.body().string());
//                        JSONObject data = response.getJSONObject("data");
//
//                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
//                        toolbarTitleTextView.setText(data.getString("page_title"));
//                        noOfJobs.setText(data.getString("no_txt"));
//
//                        JSONArray jobsArray = data.getJSONArray("jobs");
//                        if (jobsArray.length() == 0) {
//                            messageContainer.setVisibility(View.VISIBLE);
//                            emptyTextView.setText(response.getString("message"));
//                            dialogManager.hideAlertDialog();
//                            setupAdapter();
//                            return;
//                        } else
//                            messageContainer.setVisibility(View.GONE);
//                        for (int i = 0; i < jobsArray.length(); i++) {
//                            JSONArray dataArray = jobsArray.getJSONArray(i);
//                            Nokri_JobsModel model = new Nokri_JobsModel();
//                            for (int j = 0; j < dataArray.length(); j++) {
//                                model.setShowMenu(false);
//                                JSONObject object = dataArray.getJSONObject(j);
//                                if (object.getString("field_type_name").equals("job_id"))
//                                    model.setJobId(object.getString("value"));
//                                else if (object.getString("field_type_name").equals("job_name"))
//                                    model.setJobTitle(object.getString("value"));
//                                else if (object.getString("field_type_name").equals("company_name"))
//                                    model.setJobDescription(object.getString("value"));
//                                else if (object.getString("field_type_name").equals("job_salary"))
//                                    model.setSalary(object.getString("value"));
//                                else if (object.getString("field_type_name").equals("job_type"))
//                                    model.setJobType(object.getString("value"));
//                                else if (object.getString("field_type_name").equals("company_logo"))
//                                    model.setCompanyLogo(object.getString("value"));
//                                else if (object.getString("field_type_name").equals("job_location")) {
//                                    model.setAddress(object.getString("value"));
//
//                                }
//                                if (j + 1 == dataArray.length())
//                                    modelList.add(model);
//                            }
//
//                        }
//                        setupAdapter();
//
//                        //   Log.d("Pointz",modelList.toString());
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
//
//    }


    @Override
    public void onResume() {
        super.onResume();

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }


    @Override
    public void onClick(View view) {

        loadMoreButton.setVisibility(View.GONE);
        if (hasNextPage) {
            if (requestForm.equals("Home")) {
                Submit_jobSearch(false);
            } else {
                if (ALL_JOBS_SOURCE.equals(""))
                    nokri_loadMore(false, filterText);
                else
                    nokri_filterJobsExternal(false);

            }
        }
    }
}
