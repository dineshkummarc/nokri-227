package com.scriptsbundle.nokri.employeer.follow.fragments;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.scriptsbundle.nokri.employeer.dashboard.Nokri_EmployerDashboardActivity;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;

import com.scriptsbundle.nokri.employeer.jobs.fragments.NokriPublicProfileFragment;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;

import com.scriptsbundle.nokri.employeer.follow.adapters.Nokri_EmployeeFollowingAdapter;
import com.scriptsbundle.nokri.employeer.follow.models.Nokri_FollowingModel;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_EmployeeFollowingFragment extends Fragment implements Nokri_PopupManager.ConfirmInterface,View.OnClickListener{

    private RecyclerView recyclerView;

    private List<Nokri_FollowingModel> modelList;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;

    private String id;
    private Nokri_PopupManager popupManager;
    private Nokri_EmployeeFollowingAdapter adapter;
    private Nokri_EmployerDashboardActivity employeerDashboardActivity;
    private Nokri_DialogManager dialogManager;
    private ProgressBar progressBar;
    private int nextPage=1;
    private boolean hasNextPage = true;
    private Button loadMoreButton;

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    public Nokri_EmployeeFollowingFragment() {


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
        employeerDashboardActivity = (Nokri_EmployerDashboardActivity) getActivity();

        modelList = new ArrayList<>();


        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        popupManager = new Nokri_PopupManager(getContext(),this);
        emptyTextView = getView().findViewById(R.id.txt_empty);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        nextPage = 1;
        nokri_loadMore(true);
        Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getFollower());

        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        progressBar = getView().findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);

    }
    private void nokri_getFollowedCompanies(String name) {
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();

        if (!name.equals(""))
        {
            params.addProperty("c_name",name);
        }
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            if(name.equals(""))
            myCall = restService.getEmployeerFollowedCompanies(Nokri_RequestHeaderManager.addSocialHeaders());
            else
                myCall = restService.getEmployeerFollowedCompaniesByName(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {      if(name.equals(""))
            myCall = restService.getEmployeerFollowedCompanies( Nokri_RequestHeaderManager.addHeaders());
        else
            myCall = restService.getEmployeerFollowedCompaniesByName(params, Nokri_RequestHeaderManager.addHeaders());
        }
       // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject data = response.getJSONObject("data");
                        TextView pageTitle = getActivity().findViewById(R.id.toolbar_title);
                        pageTitle.setText(data.getString("page_title"));

                        JSONArray companiesArray = data.getJSONArray("comapnies");
                        if(companiesArray.length() == 0){
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            setupAdapter();
                            dialogManager.hideAlertDialog();

                            return;
                        }
                        else
                            messageContainer.setVisibility(View.GONE);
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_FollowingModel model = new Nokri_FollowingModel();
                            model.setUnfollow(data.getString("btn_text"));
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("follower_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_dp"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_name"))
                                    model.setCompanyName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_pro"))
                                    model.setCompanyAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_link"))
                                    model.setLink(object.getString("value"));
                                if(j+1==dataArray.length())
                                    modelList.add(model);
                            }
                        }
                     //   Log.d("Pointz",modelList.toString());
                        setupAdapter();
                        dialogManager .hideAfterDelay();
                    } catch (JSONException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();
                    }

                }
                else {
                    dialogManager .showCustom(responseObject.message());
                    dialogManager .hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager .hideAfterDelay();
            }
        });
    }
    private void nokri_loadMore(final Boolean showAlert){

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
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {


                myCall = restService.companyFollowersLoadMore(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else
            myCall = restService.companyFollowersLoadMore(params, Nokri_RequestHeaderManager.addHeaders());

        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                dialogManager.hideAlertDialog();
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
                        pageTitle.setText(data.getString("page_title"));

                        JSONArray companiesArray = data.getJSONArray("comapnies");
                        if(companiesArray.length() == 0){
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
                        for(int i = 0;i<companiesArray.length();i++){
                            JSONArray dataArray =  companiesArray.getJSONArray(i);
                            Nokri_FollowingModel model = new Nokri_FollowingModel();
                            model.setUnfollow(data.getString("btn_text"));
                            for(int j =0;j<dataArray.length();j++)
                            {
                                JSONObject object =   dataArray.getJSONObject(j);
                                if(object.getString("field_type_name").equals("follower_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_dp"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_name"))
                                    model.setCompanyName(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_pro"))
                                    model.setCompanyAddress(object.getString("value"));
                                else if (object.getString("field_type_name").equals("follower_link"))
                                    model.setLink(object.getString("value"));
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
                dialogManager.hideAlertDialog();
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                if(showAlert)
                dialogManager .hideAfterDelay();
            }
        });


    }

    private void nokri_unFollow() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("follower_id",id);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postDeleteFollowedEmployees(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postDeleteFollowedEmployees(params, Nokri_RequestHeaderManager.addHeaders());
        }
        //Call<ResponseBody> myCall = restService.postDeleteFollowedCompanies(params,Nokri_RequestHeaderManager.addHeaders());

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {
                            dialogManager .hideAlertDialog();
                            popupManager.nokri_showSuccessPopup(response.getString("message"));
                            recyclerView.removeAllViews();
                            modelList.clear();
                            nextPage = 1;
                           nokri_loadMore(true);
                        } else {
                            dialogManager .showCustom(responseObject.message());

                            dialogManager .hideAfterDelay();
                        }

                    } catch (JSONException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager .showCustom(e.getMessage());
                        dialogManager .hideAfterDelay();
                        e.printStackTrace();
                    }
                }

                else {
                    dialogManager .showCustom(responseObject.message());
                    dialogManager .hideAfterDelay();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }

    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                id = item.getCompanyId();
                popupManager.nokri_showDeletePopup();
            }
        });

    recyclerView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    }

    @Override
    public void onConfirmClick(Dialog dialog) {
        nokri_unFollow();
        dialog.dismiss();
    }




    @Override
    public void onResume() {
        super.onResume();

        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }




    @Override
    public void onClick(View v) {
        loadMoreButton.setVisibility(View.GONE);
            if (hasNextPage)
                nokri_loadMore(false);

    }



}
