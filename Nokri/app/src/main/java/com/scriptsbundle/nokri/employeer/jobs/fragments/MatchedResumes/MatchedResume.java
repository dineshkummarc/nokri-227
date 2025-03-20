package com.scriptsbundle.nokri.employeer.jobs.fragments.MatchedResumes;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeInterface;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchedResume extends Fragment implements SavedResumeInterface, AdapterView.OnItemSelectedListener, View.OnClickListener {

    LinearLayout progressBar, emptyLayout;
    ProgressBar progress_bar;
    ArrayList<MatchedResumeModel> matchedResumeModels = new ArrayList<>();
    RecyclerView recyclerView;
    MatchedResumeAdapter adapter;
    Nokri_DialogManager dialogManager;
    ArrayList<String> filterList = new ArrayList<>();
    ArrayList<Nokri_SpinnerModel> filterObjectsList = new ArrayList<>();
    Spinner spinner;
    TextView emptyTextView;
    Nokri_FontManager manager;
    private int maxNumOfPages,currentPage,nextPage=1,increment,currentNoOfJobs;
    private boolean hasNextPage,loading = true;
    private Button loadMoreButton;

    public boolean firstTime = true;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    public MatchedResume() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matched_resume, container, false);

        Nokri_EmployeerDashboardModel employeerDashboardModel = Nokri_SharedPrefManager.getEmployeerSettings(getActivity());
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        toolbarTitleTextView.setText(employeerDashboardModel.getMatchedResume());

        dialogManager = new Nokri_DialogManager();
        firstTime = true;

        mainLayout = view.findViewById(R.id.mainLayout);
        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        loadingLayout = view.findViewById(R.id.shimmerMain);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        progress_bar = view.findViewById(R.id.progress_bar);
        loadMoreButton = view.findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        manager = new Nokri_FontManager();

        spinner = view.findViewById(R.id.filter);
        emptyTextView = view.findViewById(R.id.txt_empty);
        manager.nokri_setOpenSenseFontTextView(emptyTextView, getActivity().getAssets());
        spinner.setOnItemSelectedListener(this);


        adapter = new MatchedResumeAdapter(getActivity(), matchedResumeModels, MatchedResume.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),R.drawable.my_divider));

        new Nokri_FontManager().nokri_setOpenSenseFontButton(loadMoreButton,getActivity().getAssets());
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        loadMoreButton.setOnClickListener(this);
        getMatchedResumes(null,1,false);

        return view;
    }

    private void getMatchedResumes(String id,int pageNumber,boolean calledFromLoadMore) {
        if (!calledFromLoadMore){
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }else{
            progress_bar.setVisibility(View.VISIBLE);

        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        Call<ResponseBody> myCall;
        if (id != null) {
            JsonObject params = new JsonObject();
            params.addProperty("job_class", id);
            params.addProperty("page_number",pageNumber);
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.filterActiveJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.filterActiveJobs(params, Nokri_RequestHeaderManager.addHeaders());
            }
        } else {
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                myCall = restService.getMatchedResumes(Nokri_RequestHeaderManager.addSocialHeaders());
            } else {
                myCall = restService.getMatchedResumes(Nokri_RequestHeaderManager.addHeaders());
            }
        }


        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {


                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());
                        if (jsonObject.getBoolean("success")) {
                                JSONArray jobsFilter = jsonObject.getJSONObject("data").getJSONObject("job_filter").getJSONArray("value");
                                filterObjectsList.clear();
                                filterList.clear();
                                for (int i = 0; i < jobsFilter.length(); i++) {
                                    JSONObject filterObject = jsonObject.getJSONObject("data").getJSONObject("job_filter").getJSONArray("value").getJSONObject(i);
                                    Nokri_SpinnerModel model = new Nokri_SpinnerModel();
                                    model.setName(filterObject.getString("value"));
                                    model.setId(filterObject.getString("key"));
                                    filterList.add(model.getName());
                                    filterObjectsList.add(model);
                                }

                                if (spinner.getAdapter() == null)
                                    spinner.setAdapter(new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, filterList));


                            JSONArray jobs = jsonObject.getJSONObject("data").getJSONArray("jobs");


                            for (int i = 0; i < jobs.length(); i++) {
                                JSONArray jobObject = jobs.getJSONArray(i);
                                MatchedResumeModel model = new MatchedResumeModel();
                                for (int j = 0; j < jobObject.length(); j++) {
                                    String fieldType = jobObject.getJSONObject(j).getString("field_type_name");
                                    if (fieldType.equals("job_id")) {
                                        model.id = jobObject.getJSONObject(j).getString("value");
                                    } else if (fieldType.equals("job_name")) {
                                        model.name = jobObject.getJSONObject(j).getString("value");
                                    } else if (fieldType.equals("job_post")) {
                                        model.jobPostDateLabel = jobObject.getJSONObject(j).getString("key");
                                        model.jobPostDateValue = jobObject.getJSONObject(j).getString("value");
                                    } else if (fieldType.equals("job_expiry")) {
                                        model.expiryLabel = jobObject.getJSONObject(j).getString("key");
                                        model.expiryValue = jobObject.getJSONObject(j).getString("value");
                                    } else if (fieldType.equals("job_status")) {
                                        model.jobStatus = jobObject.getJSONObject(j).getString("key");
                                    } else if (fieldType.equals("view_resumes")) {
                                        model.viewResumeLabel = jobObject.getJSONObject(j).getString("key");
                                    }
                                }
                                matchedResumeModels.add(model);
                            }
                            JSONObject pagination = jsonObject.getJSONObject("pagination");
                            maxNumOfPages = pagination.getInt("max_num_pages");
                            currentPage = pagination.getInt("current_page");
                            nextPage = pagination.getInt("next_page");

                            increment = pagination.getInt("increment");
                            currentNoOfJobs = pagination.getInt("current_no_of_ads");
                            hasNextPage = pagination.getBoolean("has_next_page");
                            if(!hasNextPage){
                                loadMoreButton.setVisibility(View.GONE);
                                dialogManager.hideAlertDialog();
                            }
                            else {
                                loadMoreButton.setVisibility(View.VISIBLE);
                            }



                            adapter.notifyDataSetChanged();
                            if (matchedResumeModels.size() == 0) {
                                emptyLayout.setVisibility(View.VISIBLE);
                                emptyTextView.setText(jsonObject.getString("message"));
                            } else {
                                emptyLayout.setVisibility(View.GONE);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Nokri_ToastManager.showShortToast(getActivity(), e.getMessage());
                    }


                } else {
                    Nokri_ToastManager.showShortToast(getActivity(), responseObject.message());
                }
                dialogManager.hideAlertDialog();
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showShortToast(getActivity(),t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onDelete(String id, int position) {

    }

    @Override
    public void onView(String id) {
        MatchedCandidates fragment = new MatchedCandidates();
        Bundle bundle = new Bundle();
        bundle.putString("jobId",id);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_placeholder,fragment).addToBackStack(null).commit();
    }

    @Override
    public void onDownload(String url) {

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (firstTime) {
            firstTime = false;
        } else {
            String id = filterObjectsList.get(i).getId();
            getMatchedResumes(id,1,false);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==loadMoreButton.getId()){
            loadMoreButton.setVisibility(View.GONE);
            if (hasNextPage)
                getMatchedResumes(null,nextPage,true);
        }
    }
}
