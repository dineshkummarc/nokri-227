package com.scriptsbundle.nokri.guest.faq.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.guest.faq.adapters.Nokri_ExpandableAdapter;
import com.scriptsbundle.nokri.guest.faq.models.Child;
import com.scriptsbundle.nokri.guest.faq.models.Parent;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

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
public class Nokri_FaqFragment extends Fragment {
    private Nokri_FontManager fontManager;
    private RecyclerView recyclerView;
    private Nokri_ExpandableAdapter adapter;
    private List<Parent> parentList;
    private Nokri_DialogManager dialogManager;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_FaqFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());

        String toolbarTitle = "";


        if(Nokri_SharedPrefManager.isAccountPublic(getContext())) {
            Nokri_GuestDashboardModel model = Nokri_SharedPrefManager.getGuestSettings(getContext());
            toolbarTitle = model.getFaq();
        }
        else
        if(Nokri_SharedPrefManager.isAccountEmployeer(getContext())){

            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
            toolbarTitle = model.getFaq();
        }
        else
        if(Nokri_SharedPrefManager.isAccountCandidate(getContext()))
        {

            Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
            toolbarTitle = model.getFaq();

        }


        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(toolbarTitle);




        nokri_getFaq();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_faq, container, false);
    }

    private void nokri_getFaq(){


        RestService restService;
        if(!Nokri_SharedPrefManager.isAccountPublic(getContext()))
         restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        else
            restService =  Nokri_ServiceGenerator.createService(RestService.class);


        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getFaq( Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getFaq( Nokri_RequestHeaderManager.addHeaders());
        }
        //  Call<ResponseBody> myCall = service.getCandidateProfile(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject jsonObject = new JSONObject(responseObject.body().string());

                       if(jsonObject.getBoolean("success")){
                        JSONArray dataArray = jsonObject.getJSONArray("data");


                        nokri_setupRecyclerView(dataArray);

                        }
                        else {
                           Nokri_ToastManager.showLongToast(getContext(), jsonObject.getString("message"));
                           dialogManager.hideAlertDialog();
                       }
                       }
                        catch (JSONException e) {
                            dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(getContext(),responseObject.message());
                        e.printStackTrace();
                    } catch (IOException e) {
                        dialogManager.hideAlertDialog();
                        Nokri_ToastManager.showLongToast(getContext(),responseObject.message());
                        e.printStackTrace();
                    }


                    }
                else {
                    dialogManager.hideAlertDialog();
                    Nokri_ToastManager.showLongToast(getContext(),responseObject.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
            }
        });

    }
    private void nokri_setupRecyclerView(JSONArray dataArray) throws JSONException {

        parentList = new ArrayList<>();
        for(int i = 0;i<dataArray.length();i++){
            List<Child>childList = new ArrayList<>();
            childList.add(new Child(dataArray.getJSONObject(i).getString("Description")));
            parentList.add(new Parent(dataArray.getJSONObject(i).getString("title"),childList));
        }

        recyclerView = getView().findViewById(R.id.recyclerview);

        adapter = new Nokri_ExpandableAdapter(parentList,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnGroupExpandCollapseListener(new GroupExpandCollapseListener() {
            @Override
            public void onGroupExpanded(ExpandableGroup group) {

            }

            @Override
            public void onGroupCollapsed(ExpandableGroup group) {

            }
        });

        dialogManager.hideAfterDelay();
    }
}
