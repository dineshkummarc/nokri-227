package com.scriptsbundle.nokri.candidate.jobs.fragments;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.scriptsbundle.nokri.candidate.dashboard.Nokri_CandidateDashboardActivity;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;

import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.jobs.adapters.Nokri_SavedJobsAdapter;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_PopupManager;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_SavedJobsFragment extends Fragment implements Nokri_PopupManager.ConfirmInterface,View.OnClickListener {
    private ArrayList<Nokri_JobsModel> modelList;
    private RecyclerView recyclerView;

    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;

    private int nextPage=1;
    private boolean hasNextPage = true;
    private Button loadMoreButton;
    private ProgressBar progressBar;
    private String filterText="";
    private int filterNextPage = 1;
    private boolean isFilterNetPage = false;
    Nokri_CandidateDashboardActivity candidateDashboardActivity;
    private int maxNumberOfPages,currentPage;
    private Nokri_PopupManager popupManager;
    private String id;
    private Nokri_SavedJobsAdapter adapter;
    private int itemPosition;
    private boolean callFromDelete = false;
    private int getJobsCounter = 0;
    private int maxPagesOriginal = 0;
    private String message;
    private Nokri_DialogManager dialogManager;

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    public Nokri_SavedJobsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_saved_jobs, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modelList = new ArrayList<>();

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        emptyTextView = getView().findViewById(R.id.txt_empty);
//        emptyTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        popupManager = new Nokri_PopupManager(getContext(),this);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        progressBar = getView().findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);
        candidateDashboardActivity = (Nokri_CandidateDashboardActivity) getActivity();


        nextPage = 1;
        isFilterNetPage = false;
        getSavedJobs(true,"");

        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getSaved());}

    private void nokri_calculatePagination(){


        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
        JsonObject params = new JsonObject();




            params.addProperty("page_number",currentPage);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getSavedJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getSavedJobs(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());

                        JSONObject pagination = response.getJSONObject("pagination");
                        maxNumberOfPages = pagination.getInt("max_num_pages");
                        currentPage = pagination.getInt("current_page");

                            nextPage = pagination.getInt("next_page");


                        hasNextPage = pagination.getBoolean("has_next_page");
                        //Log.d("tagggggggg"," Original "+maxPagesOriginal +" Max "+maxNumberOfPages+" next Page "+nextPage+" current Page"+currentPage);
                        if(hasNextPage) {
                            if (maxPagesOriginal > maxNumberOfPages) {
                                maxPagesOriginal = maxNumberOfPages;
                           //     Log.d("tagggggggg", " next page " + nextPage + " Current Page " + currentPage);
                                nextPage = currentPage;
                            //    Log.d("tagggggggg", " next page " + nextPage + " Current Page " + currentPage + " page ");


                                getSavedJobs(false, "");




                            } else {
                                nokri_deleteRecyclerviewItem();
                                nextPage = currentPage;

                          //      Log.d("tagggggggg", " next pageeee " + nextPage + " Current Pageeee " + currentPage + " Has Next" + hasNextPage);
                                    getSavedJobs(false, "");

                            }
                        }
                        else {
                            nokri_deleteRecyclerviewItem();
                            if(loadMoreButton.getVisibility() == View.VISIBLE) {
                                nextPage = currentPage;
                                Log.d("tagggggggg", " next pageeee " + nextPage + " Current Pageeee " + currentPage + " Has Next" + hasNextPage);

                                getSavedJobs(false, "");

                            }

                         }

                        dialogManager.hideAfterDelay(1000);

                     } catch (IOException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();
                    } catch (JSONException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();

                    }

                }
                else {

                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void getSavedJobs(final Boolean showAlert, final String text){
        if(showAlert) {
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
            myCall = restService.getSavedJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getSavedJobs(params, Nokri_RequestHeaderManager.addHeaders());
        }
       // Call<ResponseBody>myCall = restService.getSavedJobs(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if(responseObject.isSuccessful()){
                    try {emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        message = response.getString("message");
                        JSONObject pagination = response.getJSONObject("pagination");
                        maxNumberOfPages = pagination.getInt("max_num_pages");
                        currentPage = pagination.getInt("current_page");
                        if(!isFilterNetPage)
                            nextPage = pagination.getInt("next_page");
                        else
                            filterNextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");




                        if(getJobsCounter == 0){
                            maxPagesOriginal = maxNumberOfPages;
                            ++getJobsCounter;
                        }



                        JSONObject data = response.getJSONObject("data");


                  //   Log.d("tagggggggg"," next page "+nextPage +" max Number of pages "+maxNumberOfPages+" current page "+currentPage);


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
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_title"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location"))
                                    model.setAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_title"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_posted"))
                                    model.setTimeRemaining(object.getString("value"));

                                if(j+1==dataArray.length())
                                    modelList.add(model);
                            }

                        }
                            if(callFromDelete)
                            {   callFromDelete = false;
                              //  modelList.remove(currentPage-1);
                                nokri_deleteRecyclerviewItem();


                                for(int i=0;i<modelList.size();i++){
                                    Nokri_JobsModel model = modelList.get(i);
                                    if(i-1>=0)
                                    {
                                        if(modelList.get(i-1).getJobId().equals(model.getJobId()))
                                        {
                                            modelList.remove(i);

                                        }

                                    }
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
                else {
                    if(showAlert) {
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
                if(showAlert){
                    dialogManager.showCustom(t.getMessage());
                    dialogManager.hideAfterDelay();}
            }
        });
    }



    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);

        adapter = new Nokri_SavedJobsAdapter(modelList, getContext(), new Nokri_SavedJobsAdapter.OnItemClickListener() {




            @Override
            public void onItemClick(Nokri_JobsModel item) {
            /*    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Nokri_JobDetailFragment.JOB_ID = item.getJobId();
                Nokri_JobDetailFragment.COMPANY_ID = item.getCompanyId();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";

                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();
    */        }

            @Override
            public void menuItemSelected(Nokri_JobsModel model, MenuItem item, int position) {
                    switch (item.getItemId()){
                        case R.id.menu_view_job:
            androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("job_id",model.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();
                            break;
                        case R.id.menu_view_delete_job:
                            id = model.getJobId();
                            popupManager.nokri_showDeletePopup();
                            itemPosition = position;
                            break;
                    }
            }

            @Override
            public void onCompanyClick(Nokri_JobsModel item) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();


                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

            }

            @Override
            public void onJobClick(Nokri_JobsModel item) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("job_id",item.getJobId());
                jobDetailFragment.setArguments(bundle);
                Nokri_JobDetailFragment.COMPANY_ID = item.getCompanyId();
                Nokri_JobDetailFragment.CALLING_SOURCE = "";

                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();

            }

            @Override
            public void onImageClick(Nokri_JobsModel item) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();


                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

            }

        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialogManager.hideAfterDelay();
    }





    private void nokri_deleteRecyclerviewItem(){
        if(modelList!=null && !modelList.isEmpty() && modelList.size()>0)
        {
            if(adapter!=null)
            {
              //  Log.d("tagggggggg", itemPosition +" item postion "+" model list size "+modelList.size());
                modelList.remove(itemPosition);



                adapter.notifyItemRemoved(itemPosition);
                adapter.notifyItemRangeChanged(itemPosition,modelList.size());
                adapter.notifyDataSetChanged();
            }
        }
        if(modelList!=null && modelList.size() == 0){

            messageContainer.setVisibility(View.VISIBLE);
            emptyTextView.setText(message);
            progressBar.setVisibility(View.GONE);
            loadMoreButton.setVisibility(View.GONE);
        }
}

    private void nokri_deleteJob() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("job_id",id);
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postDeleteJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postDeleteJobs(params, Nokri_RequestHeaderManager.addHeaders());
        }
     //   Call<ResponseBody> myCall = restService.postDeleteJobs(params,Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {



                            Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                            popupManager.nokri_showSuccessPopup(response.getString("message"));
                            callFromDelete = true;
                            nokri_calculatePagination();




                        } else {
                            dialogManager.showCustom(responseObject.message());


                        }

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

                else {
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.showCustom(t.getMessage());
                dialogManager.hideAlertDialog();
            }
        });
    }

    @Override
    public void onConfirmClick(Dialog dialog) {


        nokri_deleteJob();
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        loadMoreButton.setVisibility(View.GONE);
        if(hasNextPage) {

                getSavedJobs(false, filterText);

        }
    }




    @Override
    public void onResume() {
        super.onResume();

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }



}
