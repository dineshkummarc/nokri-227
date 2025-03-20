package com.scriptsbundle.nokri.candidate.jobs.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.scriptsbundle.nokri.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;

import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.jobs.adapters.Nokri_JobsAdapter;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
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


public class Nokri_JobsForYouFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Nokri_JobsAdapter adapter;
    private List<Nokri_JobsModel> modelList;

    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private int nextPage=1;
    private boolean hasNextPage = true;
    private Button loadMoreButton;
    private ProgressBar progressBar;
    private int filterNextPage = 1;
    private boolean isFilterNetPage = false;
    private Nokri_DialogManager dialogManager;

    private String filterText="";
    Nokri_CandidateDashboardActivity candidateDashboardActivity;

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;


    public Nokri_JobsForYouFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        modelList = new ArrayList<>();
        emptyTextView = getView().findViewById(R.id.txt_empty);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        progressBar = getView().findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);
        candidateDashboardActivity = (Nokri_CandidateDashboardActivity) getActivity();
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        // populuateListWithDummyData();
        nextPage = 1;
        nokri_getJobsApplied(true,"");
        isFilterNetPage = false;




    }

    private void nokri_getJobsApplied(final Boolean showAlert, String text){
        if(showAlert)
        {
            dialogManager = new Nokri_DialogManager();

            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        JsonObject params = new JsonObject();
        if(!isFilterNetPage)
            params.addProperty("page_number",nextPage);
        else
            params.addProperty("page_number",filterNextPage);
        if(!text.equals("")) {
            params.addProperty("keyword", text);
        }
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getJobsForYou(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getJobsForYou(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getAppliedJobs(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if(responseObject.isSuccessful()){
                    try {

                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");
                        if(!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");
                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

                        toolbarTitleTextView.setText(data.getString("page_title"));
                        if(!hasNextPage){
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        JSONArray jobsArray = data.getJSONArray("jobs");

                        if(jobsArray.length() == 0){
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            setupAdapter();
                            if(showAlert)
                                dialogManager.hideAlertDialog();
                            return;

                        }
                        else
                            messageContainer.setVisibility(View.GONE);
                        for(int i = 0;i<jobsArray.length();i++)
                        {
                            JSONArray dataArray =  jobsArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();
                            for(int j =0;j<dataArray.length();j++)
                            {model.setShowMenu(false);
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location")) {
                                    model.setAddress(object.getString("value"));

                                }
                                else if (object.getString("field_type_name").equals("job_posted"))
                                    model.setTimeRemaining(object.getString("value"));
                                if(j+1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        setupAdapter();
                        if(!hasNextPage){

                            progressBar.setVisibility(View.GONE);
                        }
                        if(showAlert)
                            dialogManager.hideAfterDelay();

                    } catch (IOException e) {
                        if(showAlert){
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();}
                        e.printStackTrace();
                    } catch (JSONException e) {
                        if(showAlert){
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();}

                        e.printStackTrace();

                    }

                }
                else {if(showAlert) {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(showAlert){

                    mainLayout.setVisibility(View.VISIBLE);
                    shimmerContainer.stopShimmer();
                    shimmerContainer.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    Nokri_Utils.isCallRunning = false;
                    dialogManager.hideAfterDelay();}
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());    }
        });
    }

    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        adapter = new Nokri_JobsAdapter(modelList, getContext(), new Nokri_JobsAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(Nokri_JobsModel item) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                Bundle bundle = new Bundle();
                bundle.putString("job_id",item.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID = item.getCompanyId();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();
            }

            @Override
            public void onCompanyClick(Nokri_JobsModel item) {


            }


            @Override
            public void menuItemSelected(Nokri_JobsModel model, MenuItem item) {
             /*   switch (item.getItemId()){
                    case R.id.menu_view_job:
                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                        Nokri_JobDetailFragment.CALLING_SOURCE = "";
                        Nokri_JobDetailFragment.JOB_ID = model.getJobId();
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).commit();
                        break;
                    case  R.id.menu_view_company_profile:
                        android.support.v4.app.FragmentManager fragmentManager2 = getFragmentManager();
                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                        Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                        Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
                        fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).commit();
                        break;
                }*/
            }
        });
        adapter.setOnJobClickListener(model -> {
            androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
            androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment jobDetailFragment = new Nokri_JobDetailFragment();
            Nokri_JobDetailFragment.CALLING_SOURCE = "";
            Bundle bundle = new Bundle();
            bundle.putString("job_id",model.getJobId());
            jobDetailFragment.setArguments(bundle);
            Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
            fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();

        });
        adapter.setOnImageClickListener(item -> {
            androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
            androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();
            Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();
            fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        dialogManager.hideAfterDelay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_job_applied, container, false);
    }


    @Override
    public void onClick(View view) {
        loadMoreButton.setVisibility(View.GONE);
        if(hasNextPage) {
            //    isFilterNetPage = false;
            nokri_getJobsApplied(false, filterText);

        }
    }



    @Override
    public void onResume() {
        super.onResume();

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }




}
