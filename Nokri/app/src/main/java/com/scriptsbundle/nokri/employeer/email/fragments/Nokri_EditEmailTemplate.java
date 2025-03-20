package com.scriptsbundle.nokri.employeer.email.fragments;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.email.adapters.Nokri_EditEmailAdapter;
import com.scriptsbundle.nokri.employeer.email.model.Nokri_EditEmailTemplateModel;
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
public class Nokri_EditEmailTemplate extends Fragment implements Nokri_PopupManager.ConfirmInterface,View.OnClickListener{

    private TextView titleTexView,srTextView,nameTextView,updateTextView,deleteTextView,emptyTextView;
    private Button addNewButton;
    private Nokri_FontManager fontManager;
    private RecyclerView recyclerView;
    private List<Nokri_EditEmailTemplateModel> modelList;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private Nokri_EditEmailAdapter adapter;
    private Nokri_PopupManager popupManager;
    public static String ID;
    public static boolean IS_Update = false;


    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;

    private Nokri_DialogManager dialogManager;
    public Nokri_EditEmailTemplate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_edit_email_template, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();
        nokri_setFonts();
        nokri_getEmailTemplateList();
        Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());

        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(model.getTemplates());
    }

    private void nokri_initialize(){
        fontManager = new Nokri_FontManager();

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);

        titleTexView = getView().findViewById(R.id.txt_title);
        srTextView = getView().findViewById(R.id.txt_sr);
        nameTextView = getView().findViewById(R.id.txt_name);
        updateTextView = getView().findViewById(R.id.txt_update);
        deleteTextView = getView().findViewById(R.id.txt_delete);
        emptyTextView = getView().findViewById(R.id.txt_empty);

        addNewButton = getView().findViewById(R.id.btn_add_new);

        recyclerView = getView().findViewById(R.id.recyclerview);
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        popupManager = new Nokri_PopupManager(getContext(),this);

        addNewButton.setOnClickListener(this);

    }


    private void nokri_setFonts() {
        fontManager.nokri_setMonesrratSemiBioldFont(titleTexView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(srTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(nameTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(updateTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(deleteTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
     //   fontManager.nokri_setOpenSenseFontButton(addNewButton,getActivity().getAssets());
    }
    private void setupRecyclerview() {

        //populuateListWithDummyData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_EditEmailAdapter(modelList,getContext(),new Nokri_EditEmailAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Nokri_EditEmailTemplateModel item, int flag) {


                if(flag == 0) {
                    IS_Update = true;
                    ID = item.getId();
                    androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment emailFragment = new Nokri_AddEmailTemplateFragment();
                    fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),emailFragment).addToBackStack(null).commit();
                }
                if(flag==1){
                    IS_Update = false;
                    ID = item.getId();
                    popupManager.nokri_showDeletePopup();


                }
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialogManager.hideAlertDialog();}

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }



    private void nokri_getEmailTemplateList(){
        dialogManager = new Nokri_DialogManager();
        mainLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        Nokri_Utils.isCallRunning = true;
        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getEmployeerEmailList(Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getEmployeerEmailList( Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getCandidateResumeList(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                dialogManager.hideAlertDialog();
                mainLayout.setVisibility(View.VISIBLE);
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                Nokri_Utils.isCallRunning = false;
                if(responseObject.isSuccessful())
                {

                    try {
                        modelList = new ArrayList<>();
                        JSONObject response = new JSONObject(responseObject.body().string());
                        String buttonText = null;
                        String deleteButtonText = null;
                        if(response.getBoolean("success")){
                            JSONObject data = response.getJSONObject("data");
                            JSONArray extras = data.getJSONArray("extras");
                            for(int i =0;i<extras.length();i++){
                                JSONObject object = extras.getJSONObject(i);
                                if(object.getString("field_type_name").equals("section_name"))
                                {
                                    titleTexView.setText(object.getString("value"));
                                }
                                else
                                if(object.getString("field_type_name").equals("btn_txt"))
                                {
                                    addNewButton.setText(object.getString("value"));

                                }
                                else
                                if(object.getString("field_type_name").equals("sr_text"))
                                {srTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("name"))
                                {nameTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("update"))
                                {updateTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("del"))
                                {deleteTextView.setText(object.getString("value"));}
                                else
                                if(object.getString("field_type_name").equals("btn_update"))
                                {buttonText = object.getString("value");}

                                if(object.getString("field_type_name").equals("btn_del"))
                                {
                                    deleteButtonText = object.getString("value");
                                }
                                if(object.getString("field_type_name").equals("not_added"))
                                {
                                    emptyTextView.setText(object.getString("value"));
                                }
                            }


                            JSONArray dataArray = data.getJSONArray("templates");
                            if(dataArray.length()<=0)
                            {   messageContainer.setVisibility(View.VISIBLE);
                                dialogManager.hideAlertDialog();

                            }
                            else
                                messageContainer.setVisibility(View.GONE);



                            for(int i=0;i<dataArray.length();i++) {
                                JSONArray objectArray = dataArray.getJSONArray(i);
                                Nokri_EditEmailTemplateModel model = new Nokri_EditEmailTemplateModel();
                                model.setSrNum(i+1+"");
                                model.setButtonText(buttonText);
                                model.setDeleteButtonText(deleteButtonText);
                                for (int j = 0; j < objectArray.length(); j++) {
                                    JSONObject dataObject = objectArray.getJSONObject(j);

                                    if (dataObject.getString("field_type_name").equals("temp_name")) {
                                        model.setName(dataObject.getString("value"));
                                    } /*else if (dataObject.getString("field_type_name").equals("resume_url")) {
                                        model.setLink(dataObject.getString("value"));
                                    }*/
                                    else if (dataObject.getString("field_type_name").equals("temp_id"))
                                        model.setId(dataObject.getString("value"));

                                    if(j+1 == objectArray.length())
                                        modelList.add(model);
                                }
                            }
                            setupRecyclerview();}
                        else
                        {
                            dialogManager.showCustom(response.getString("message"));
                            dialogManager.hideAfterDelay();
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
                else{
                    dialogManager.showCustom(responseObject.message());
                    dialogManager.hideAfterDelay();
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
                dialogManager.hideAfterDelay();
            }
        });
    }

    @Override
    public void onConfirmClick(Dialog dialog) {
         nokri_deleteTemplate();
        dialog.dismiss();
    }

    private void nokri_deleteTemplate() {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());
        JsonObject params = new JsonObject();
        params.addProperty("temp_id",ID);

        RestService restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());

        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postDeleteTemplate(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postDeleteTemplate(params, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){

                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        if (response.getBoolean("success")) {
                            dialogManager.hideAlertDialog();
                            popupManager.nokri_showSuccessPopup(response.getString("message"));
                            nokri_getEmailTemplateList();
                        } else {
                            dialogManager.showCustom(responseObject.message());

                            dialogManager.hideAfterDelay();
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
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                dialogManager.hideAfterDelay();
            }
        });
    }


    @Override
    public void onClick(View view) {
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment emailFragment = new Nokri_AddEmailTemplateFragment();
        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),emailFragment).addToBackStack(null).commit();
        IS_Update = false;
    }
}
