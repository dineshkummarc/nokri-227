package com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes;

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
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.MatchedResumes.DividerItemDecoration;
import com.scriptsbundle.nokri.employeer.jobs.fragments.NokriPublicProfileFragment;
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

public class SavedResumesFragment extends Fragment implements SavedResumeInterface {
    RecyclerView recyclerView;
    SavedResumesAdapter adapter;
    Nokri_DialogManager dialogManager;
    ArrayList<SavedResumeModel> savedResumes = new ArrayList<>();

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    public SavedResumesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_resumes, container, false);


        mainLayout = view.findViewById(R.id.mainLayout);
        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        loadingLayout = view.findViewById(R.id.shimmerMain);
        Nokri_EmployeerDashboardModel employeerDashboardModel = Nokri_SharedPrefManager.getEmployeerSettings(getActivity());
        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
        toolbarTitleTextView.setText(employeerDashboardModel.getSavedResumes());
        recyclerView = view.findViewById(R.id.recyclerviewSavedResumes);
        adapter = new SavedResumesAdapter(getActivity(), savedResumes, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),R.drawable.my_divider));
        getSavedResumes();
        return view;
    }

    public void getSavedResumes() {


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
            myCall = restService.getSavedResumes(Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getSavedResumes(Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            //    nokri_getLocationAndAddress();
                            JSONObject data = response.getJSONObject("data");
                            savedResumes.clear();
                            JSONArray candidateList = data.getJSONArray("Resumes");
                            for (int i = 0; i < candidateList.length(); i++) {
                                JSONArray jsonArray = candidateList.getJSONArray(i);
                                SavedResumeModel model = new SavedResumeModel();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    String entity = jsonArray.getJSONObject(j).getString("field_type_name");

                                    if (entity.equals("candidate_id")) {
                                        model.id = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("candidate_name")) {
                                        model.name = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("candidate_logo")) {
                                        model.url = jsonArray.getJSONObject(j).getString("value");
                                    }
                                }

                                savedResumes.add(model);
                                adapter.notifyDataSetChanged();

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

                dialogManager.hideAlertDialog();
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                dialogManager.hideAlertDialog();
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                dialogManager.hideAfterDelay();

            }
        });


    }

    @Override
    public void onDelete(String id, int position) {
        deleteSavedResume(id, position);
    }

    @Override
    public void onView(String id) {
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment publicProfileFragment = new NokriPublicProfileFragment();
        NokriPublicProfileFragment.USER_ID = id;

        fragmentTransaction.add(getActivity().findViewById(R.id.fragment_placeholder).getId(),publicProfileFragment).addToBackStack(null).commit();

    }

    @Override
    public void onDownload(String url) {

    }

    public void deleteSavedResume(String resumeId, int position) {


        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resume_id", resumeId);


        Log.v("tagzzzzzz", jsonObject.toString());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.removeSavedResumes(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.removeSavedResumes(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.postCandidateLocation(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showShortToast(getActivity(), response.getString("message"));

                            savedResumes.remove(position);
                            adapter.notifyDataSetChanged();
                        }else{
                            dialogManager.hideAlertDialog();
                            Nokri_ToastManager.showShortToast(getActivity(), response.getString("message"));

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
}
