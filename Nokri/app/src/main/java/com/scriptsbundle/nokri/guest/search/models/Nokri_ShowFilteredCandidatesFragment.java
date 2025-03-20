package com.scriptsbundle.nokri.guest.search.models;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.employeer.follow.adapters.Nokri_EmployeeFollowingAdapter;
import com.scriptsbundle.nokri.employeer.follow.models.Nokri_FollowingModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.NokriPublicProfileFragment;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_ShowFilteredCandidatesFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;

    private List<Nokri_FollowingModel> modelList;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;

    private String id;
    private Nokri_EmployeeFollowingAdapter adapter;
    private Nokri_DialogManager dialogManager;
    private ProgressBar progressBar;
    private int nextPage=1;
    private boolean hasNextPage = true;
    private Button loadMoreButton;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    public Nokri_ShowFilteredCandidatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_following, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_getFilteredCandidate(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.GONE);
        toolbar.findViewById(R.id.createAlert).setVisibility(View.GONE);
        toolbar.findViewById(R.id.collapse).setVisibility(View.GONE);
        toolbar.findViewById(R.id.clearFilters).setVisibility(View.VISIBLE);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.refresh).setVisibility(View.GONE);
        toolbar.findViewById(R.id.collapse).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.createAlert).setVisibility(View.GONE);
        toolbar.findViewById(R.id.clearFilters).setVisibility(View.GONE);

    }

    private void nokri_initialize(){
        modelList = new ArrayList<>();

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        emptyTextView = getView().findViewById(R.id.txt_empty);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        Picasso.get().load(R.drawable.logo).into(messageImage);
        Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getCompany());
        nextPage = 1;

        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        progressBar = getView().findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);
    }

    private void nokri_getFilteredCandidate(final boolean showAlert){

        if(showAlert) {
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }





        JsonObject params = new JsonObject();

        params.addProperty("page_number",nextPage);

 if(showAlert) {

     Nokri_CandidateSearchModel candidateSearchModel = Nokri_SharedPrefManager.getCandidateSearchModel(getContext());

     if (candidateSearchModel.isSearchOnly()) {
         params.addProperty("cand_title", candidateSearchModel.getTitle());
     }
     if (!candidateSearchModel.isSearchOnly()) {
         if (!candidateSearchModel.getTitle().trim().isEmpty())
             params.addProperty("cand_title", candidateSearchModel.getTitle());


         if (!candidateSearchModel.getTitle().trim().isEmpty())
             params.addProperty("cand_title", candidateSearchModel.getTitle());

         if(!candidateSearchModel.getLocation().trim().isEmpty())
         params.addProperty("cand_location", candidateSearchModel.getLocation());

         if(!candidateSearchModel.getType().trim().isEmpty())
         params.addProperty("cand_type", candidateSearchModel.getType());

         if(!candidateSearchModel.getExperience().trim().isEmpty())
         params.addProperty("cand_experience", candidateSearchModel.getExperience());

         if(!candidateSearchModel.getLevel().trim().isEmpty())
         params.addProperty("cand_level", candidateSearchModel.getLevel());

         if(!candidateSearchModel.getSkill().trim().isEmpty())
         params.addProperty("cand_skills", candidateSearchModel.getSkill());


         if(!candidateSearchModel.getGender().trim().isEmpty())
             params.addProperty("cand_gender", candidateSearchModel.getGender());

         if(!candidateSearchModel.getExperience().trim().isEmpty())
             params.addProperty("cand_qualification", candidateSearchModel.getQualification());

         if(!candidateSearchModel.getSalaryRange().trim().isEmpty())
             params.addProperty("cand_salary_range", candidateSearchModel.getSalaryRange());

         if(!candidateSearchModel.getSalaryType().trim().isEmpty())
             params.addProperty("cand_salary_type", candidateSearchModel.getSalaryType());

         if(!candidateSearchModel.getSalaryCurrency().trim().isEmpty())
             params.addProperty("cand_salary_curr", candidateSearchModel.getSalaryCurrency());

     }
 }



        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class);

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {


            myCall = restService.getFilteredCandidates(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else
            myCall = restService.getFilteredCandidates(params, Nokri_RequestHeaderManager.addHeaders());

        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
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

                        nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");

                        if(!hasNextPage){
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }



                        JSONObject data = response.getJSONObject("data");
                        TextView pageTitle = getActivity().findViewById(R.id.toolbar_title);
                        pageTitle.setText(response.getJSONObject("extra").getString("page_title"));

                        JSONArray candidates = data.getJSONArray("candidates");
                        if(candidates.length() == 0){
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
                        for(int i = 0;i<candidates.length();i++){
                            JSONArray dataArray =  candidates.getJSONArray(i);
                            Nokri_FollowingModel model = new Nokri_FollowingModel();
                            model.setHideUnfollowButton(true);
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("cand_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_dp"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_name"))
                                    model.setCompanyName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_headline"))
                                    model.setCompanyAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("cand_location"))
                                    model.setLink(object.getString("value"));
                                else if (object.getString("field_type_name").equals("is_featured"))
                                    model.setFeatured(object.getBoolean("value"));
                                if(j+1==dataArray.length())
                                    modelList.add(model);
                            }
                        }
                        //   Log.d("Pointz",modelList.toString());
                        setupAdapter();
                        if(!hasNextPage){
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        if(showAlert)
                            dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        if(showAlert){
                            dialogManager .showCustom(e.getMessage());
                            dialogManager .hideAfterDelay();}
                        e.printStackTrace();
                    } catch (IOException e) {
                        if(showAlert){
                            dialogManager .showCustom(e.getMessage());
                            dialogManager .hideAfterDelay();}
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                if(showAlert){
                    dialogManager .hideAfterDelay();
                    mainLayout.setVisibility(View.VISIBLE);
                    shimmerContainer.stopShimmer();
                    shimmerContainer.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    Nokri_Utils.isCallRunning = false;
                }
            }
        });


    }
    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        adapter = new Nokri_EmployeeFollowingAdapter(modelList, getActivity().getApplicationContext(), new Nokri_EmployeeFollowingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_FollowingModel item) {
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment publicProfileFragment = new NokriPublicProfileFragment();
                NokriPublicProfileFragment.USER_ID = item.getCompanyId();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),publicProfileFragment).addToBackStack(null).commit();
            }

            @Override
            public void onRemoveClick(Nokri_FollowingModel item) {
              //  id = item.getCompanyId();
               // popupManager.nokri_showDeletePopup();
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        loadMoreButton.setVisibility(View.GONE);
        if (hasNextPage)
            nokri_getFilteredCandidate(false);
    }
}
