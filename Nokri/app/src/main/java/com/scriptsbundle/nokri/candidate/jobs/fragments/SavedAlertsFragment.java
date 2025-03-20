package com.scriptsbundle.nokri.candidate.jobs.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.candidate.jobs.adapters.SavedAlertsAdapter;
import com.scriptsbundle.nokri.candidate.jobs.models.JobAlertsModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeInterface;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.VerticalSpaceItemDecoration;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
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
public class SavedAlertsFragment extends Fragment implements SavedResumeInterface {

    RecyclerView recyclerView;
    SavedAlertsAdapter adapter;
    Nokri_DialogManager dialogManager;
    ArrayList<JobAlertsModel> savedAlerts = new ArrayList<>();
    LinearLayout emptyLayout;
    TextView emptyTextView;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    public SavedAlertsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_alerts, container, false);

        mainLayout = view.findViewById(R.id.mainLayout);
        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        loadingLayout = view.findViewById(R.id.shimmerMain);
        Nokri_CandidateDashboardModel candidateDashboardModel = Nokri_SharedPrefManager.getCandidateSettings(getActivity());
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        toolbarTitleTextView.setText(candidateDashboardModel.getJobAlerts());
        recyclerView = view.findViewById(R.id.recyclerviewSavedAlerts);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));
        adapter = new SavedAlertsAdapter(getActivity(), savedAlerts, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        emptyLayout = view.findViewById(R.id.emptyLayout);
        emptyTextView = view.findViewById(R.id.txt_empty);

        getSavedAlerts();
        return view;
    }

    @Override
    public void onDelete(String id, int position) {
        deleteJobAlert(id,position);
    }

    @Override
    public void onView(String id) {

    }

    @Override
    public void onDownload(String url) {

    }


    public void deleteJobAlert(String id, int position){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("alert_id",id);

        Log.v("tagzzzzzz", jsonObject.toString());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.deleteJobAlert(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.deleteJobAlert(jsonObject,Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            savedAlerts.remove(position);
                            adapter.notifyDataSetChanged();
                            Nokri_ToastManager.showShortToast(getActivity(),response.getString("message"));

                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAlertDialog();
                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();

                    }
                } else {
                    Nokri_ToastManager.showLongToast(getContext(), responseObject.message());
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




    public void getSavedAlerts() {


        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;


        JsonObject jsonObject = new JsonObject();


        Log.v("tagzzzzzz", jsonObject.toString());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getSavedJobAlerts(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getSavedJobAlerts(Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
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
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            //    nokri_getLocationAndAddress();
                            JSONObject data = response.getJSONObject("data");
                            savedAlerts.clear();
                            JSONArray alertsList = data.getJSONArray("alerts");
                            for (int i = 0; i < alertsList.length(); i++) {
                                JSONArray jsonArray = alertsList.getJSONArray(i);
                                JobAlertsModel model = new JobAlertsModel();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    String entity = jsonArray.getJSONObject(j).getString("field_type_name");

                                    if (entity.equals("alert_key")) {
                                        model.id = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("alert_name")) {
                                        model.name = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("alert_frequency")) {
                                        model.frequency = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("alert_category")) {
                                        model.category = jsonArray.getJSONObject(j).getString("value");
                                    }
                                }

                                savedAlerts.add(model);
                                adapter.notifyDataSetChanged();

                            }

                            if (savedAlerts.size()==0){
                                emptyLayout.setVisibility(View.VISIBLE);
                                emptyTextView.setText(response.getString("message"));
                            }

                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAlertDialog();
                        }

                    } catch (JSONException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        dialogManager.hideAfterDelay();
                        e.printStackTrace();

                    }
                } else {
                    Nokri_ToastManager.showLongToast(getContext(), responseObject.message());
                    dialogManager.hideAfterDelay();

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
}
