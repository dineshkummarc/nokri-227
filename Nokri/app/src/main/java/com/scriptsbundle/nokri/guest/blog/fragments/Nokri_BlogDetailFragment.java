package com.scriptsbundle.nokri.guest.blog.fragments;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.guest.blog.adapters.Nokri_BlogDetailAdapter;
import com.scriptsbundle.nokri.guest.blog.adapters.Nokri_BlogGridAdapter;
import com.scriptsbundle.nokri.guest.blog.models.Nokri_BlogGridModel;
import com.scriptsbundle.nokri.guest.blog.models.Nokri_CommentsModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Config;
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
public class Nokri_BlogDetailFragment extends Fragment implements View.OnFocusChangeListener,View.OnClickListener{

    private RecyclerView recyclerView,commentsRecyclerView;
    private Nokri_BlogGridAdapter adapter;
    private List<Nokri_BlogGridModel> blodModelList;
    private TextView tagTextView,commentsTextView,totalCommentsTextView;
    private EditText commentsEditText,nameEditText,emailEditText;
    private Button publishButton;
    private Nokri_FontManager fontManager;
    private List<Nokri_CommentsModel>commentsModelList;
    private Nokri_BlogDetailAdapter blogDetailAdapter;
    private String popupTitleText="",popupSubmitText="",popupCancelText="",popuphintText="";
    private boolean hasNextPage;
    private int nextPage=1;
    private ProgressBar progressBar;
    private Button loadMoreButton;
    private String commentId;
    private Nokri_DialogManager dialogManager;
    private RelativeLayout tagsContainer;
    private String pleaseLoginFirst;
    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    public Nokri_BlogDetailFragment() {
        // Required empty public constructor
    }
    private void setupBlogAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new Nokri_BlogGridAdapter(blodModelList, getContext(), new Nokri_BlogGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_BlogGridModel item) {

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nokri_initialize();

        nokri_setFonts();
        nokri_getBlogDetails(true,"loadmore");}

    private void nokri_setFonts() {
        fontManager.nokri_setOpenSenseFontTextView(tagTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(commentsTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(totalCommentsTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(commentsEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(nameEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontEditText(emailEditText,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(publishButton,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(loadMoreButton,getActivity().getAssets());
    }

    private void nokri_initialize() {

        mainLayout = getView().findViewById(R.id.mainLayout);
        shimmerContainer = getView().findViewById(R.id.shimmer_view_container);
        loadingLayout = getView().findViewById(R.id.shimmerMain);
        fontManager = new Nokri_FontManager();
        tagTextView = getView().findViewById(R.id.txt_tag);
        commentsTextView = getView().findViewById(R.id.txt_comments);
        totalCommentsTextView = getView().findViewById(R.id.txt_total_comments);
        commentsEditText = getView().findViewById(R.id.edittxt_comments);
        nameEditText = getView().findViewById(R.id.edittxt_name);

        emailEditText = getView().findViewById(R.id.edittxt_email);
        publishButton = getView().findViewById(R.id.btn_publish);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);

        Nokri_Utils.setRoundButtonColor(getContext(),publishButton);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);

        progressBar = getView().findViewById(R.id.progress_bar);

        tagsContainer = getView().findViewById(R.id.tags_container);

        nameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);
        commentsEditText.setOnFocusChangeListener(this);
        blodModelList = new ArrayList<>();
        commentsModelList = new ArrayList<>();
        publishButton.setOnClickListener(this);
        loadMoreButton.setOnClickListener(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nokri_blog_detail, container, false);
    }



    @Override
    public void onFocusChange(View view, boolean selected) {
        switch (view.getId()){

            case R.id.edittxt_comments:
            if(selected){
                commentsEditText.setHintTextColor(getResources().getColor(R.color.grey));
                nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
            }
                break;
            case R.id.edittxt_name:
                if(selected){
                    commentsEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    emailEditText.setHintTextColor(getResources().getColor(R.color.grey));
                }
                break;
            case R.id.edittxt_email:
                if(selected){
                    commentsEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    nameEditText.setHintTextColor(getResources().getColor(R.color.grey));
                    emailEditText.setHintTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                }
                break;

        }
    }


    private void nokri_postComment(boolean isReply, String message) {

        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());

            RestService restService;

            if(!Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService =  Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()),getContext());
            else
                restService =  Nokri_ServiceGenerator.createService(RestService.class);

            JsonObject params = new JsonObject();
        params.addProperty("post_id", Nokri_BlogGridFragment.BLOG_ID);
        if(isReply)
            params.addProperty("message",message);
        else
        params.addProperty("message",commentsEditText.getText().toString());
        if(isReply)
            params.addProperty("comment_id",commentId);
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postBlogComment(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.postBlogComment(params, Nokri_RequestHeaderManager.addHeaders());
        }
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if(responseObject.isSuccessful()){
                    try {
                        dialogManager.hideAlertDialog();
                        commentsEditText.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Nokri_ToastManager.showLongToast(getContext(),response.getString("message"));
                        //Nokri_DialogManager.hideAfterDelay();
                        nokri_getBlogDetails(true,"");
                    } catch (JSONException e) {

                        dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();
                            Log.v("Exception---->",e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {

                            Log.v("Exception---->",e.getMessage());
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
                nokri_getBlogDetails(true,"");
                Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                 dialogManager.hideAfterDelay();
            }
        });
    }

    private void nokri_getBlogDetails(final Boolean showAlert, String callFrom) {
        if (showAlert)
        {
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }
        RestService restService;
        if(!Nokri_SharedPrefManager.isAccountPublic(getContext()))
         restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class);

        JsonObject params = new JsonObject();

        params.addProperty("post_id", Nokri_BlogGridFragment.BLOG_ID);

        if (callFrom.equals("loadmore")) {
            params.addProperty("page_num", nextPage);

        }
        Call<ResponseBody> myCall;
        if(Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getBlogDetails(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else

        {
            myCall = restService.getBlogDetails(params, Nokri_RequestHeaderManager.addHeaders());
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
                if(responseObject.isSuccessful()){
                    try {
                        blodModelList = new ArrayList<>();
                        //commentsModelList = new ArrayList<>();
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject extra = response.getJSONObject("extra");
                        JSONObject publish = extra.getJSONObject("publish");
                        pleaseLoginFirst = publish.getString("msg");
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        toolbarTitleTextView.setText(extra.getString("page_title"));
                        commentsTextView.setText(extra.getString("comment_title"));
                        commentsEditText.setHint(publish.getString("comment"));
                        nameEditText.setHint(publish.getString("name"));
                        emailEditText.setHint(publish.getString("email"));
                        publishButton.setText(publish.getString("btn"));

                        JSONObject commentForm =  extra.getJSONObject("comment_form");
                        popupTitleText=commentForm.getString("title");
                        popupSubmitText=commentForm.getString("btn_submit");
                        popupCancelText=commentForm.getString("btn_cancel");
                        popuphintText=commentForm.getString("textarea");;
                        JSONObject data = response.getJSONObject("data");

                  
                        JSONObject postObject = data.getJSONObject("post");



                            Nokri_BlogGridModel model = new Nokri_BlogGridModel();
                            model.setId(postObject.getString("post_id"));
                            model.setHeadingText(postObject.getString("title"));
                            model.setParagraphText(postObject.getString("desc"));
                            model.setDateText(postObject.getString("date"));
                            model.setHeaderImage(postObject.getString("image"));
                            model.setHasImage(postObject.getBoolean("has_image"));
                            model.setCommentsText(extra.getString("comment_title")+" "+postObject.getString("comment_count"));
                            model.setHtmlResponse(true);
                            blodModelList.add(model);

                             /*   if(j+1==dataArray.length())
                                    modelList.add(model);*/
                        String tagText = "";

                        //   Log.d("Pointz",modelList.toString());
                        JSONArray tags = postObject.getJSONArray("tags");
                       if(tags.length() == 0)
                           tagsContainer.setVisibility(View.GONE);
                        for(int i = 0;i<tags.length();i++){
                            JSONObject tag = tags.getJSONObject(i);
                            tagText+= " # "+ tag.getString("name");

                        }

                        tagTextView.setText(tagText);
                        setupBlogAdapter();
                        totalCommentsTextView.setText("("+postObject.getString("comment_count")+")");
                        totalCommentsTextView.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
                        JSONObject commentsObject = postObject.getJSONObject("comments");

                        JSONObject pagination = commentsObject.getJSONObject("pagination");

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
                        JSONArray commnetsArray = commentsObject.getJSONArray("comments");
                        for(int i=0;i<commnetsArray.length();i++){
                            Nokri_CommentsModel commentsModel = new Nokri_CommentsModel();
                            JSONObject comment = commnetsArray.getJSONObject(i);
                            commentsModel.setId(comment.getString("comment_id"));
                            commentsModel.setProfilImage(comment.getString("img"));
                            commentsModel.setNameText(comment.getString("comment_author"));
                            commentsModel.setCommentText(comment.getString("comment_content"));
                            commentsModel.setDateText(comment.getString("comment_date"));
                            commentsModel.setReplyButtonText(comment.getString("reply_btn_text"));
                            commentsModel.setReply(false);

                            commentsModelList.add(commentsModel);
                            JSONArray replyArray = comment.getJSONArray("reply");
                            for(int j=0;j<replyArray.length();j++){
                                JSONObject replyObject = replyArray.getJSONObject(j);
                                Nokri_CommentsModel replyModel = new Nokri_CommentsModel();
                                replyModel.setId(comment.getString("blog_id"));
                                replyModel.setProfilImage(replyObject.getString("img"));
                                replyModel.setNameText(replyObject.getString("comment_author"));
                                replyModel.setCommentText(replyObject.getString("comment_content"));
                                replyModel.setDateText(replyObject.getString("comment_date"));
                                replyModel.setReply(true);
                                commentsModelList.add(replyModel);
                            }
                        }



                        setupCommentsAdapter();
                        if(!hasNextPage){

                            progressBar.setVisibility(View.GONE);
                        }
                        if(showAlert)
                            dialogManager.hideAlertDialog();
                    } catch (JSONException e) {
                        if(showAlert){

                            dialogManager.hideAfterDelay();

                        e.printStackTrace();}
                    } catch (IOException e) {

                        if(showAlert) {

                            dialogManager.hideAfterDelay();

                            e.printStackTrace();
                        }
                    }

                }
                else {
                    if(showAlert) {

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
                    Nokri_ToastManager.showLongToast(getContext(),t.getMessage());
                    dialogManager.hideAfterDelay();}
            }
        });
    }


    private  void setupCommentsAdapter(){


        commentsRecyclerView = getView().findViewById(R.id.recyclerview_comments);

        commentsRecyclerView.setNestedScrollingEnabled(false);
        blogDetailAdapter = new Nokri_BlogDetailAdapter(commentsModelList, getContext(), new Nokri_BlogDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_CommentsModel item) {

                if(!Nokri_SharedPrefManager.isAccountPublic(getContext())) {
                    commentId = item.getId();
                    nokri_showDilogMessage("");
                }
                else
                    Nokri_ToastManager.showLongToast(getContext(), pleaseLoginFirst);

            }
        });



        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        commentsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        commentsRecyclerView.setAdapter(blogDetailAdapter);
        blogDetailAdapter.notifyDataSetChanged();
    }
    private void nokri_showDilogMessage(final String comntId) {
      final Dialog  dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_popup);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);
        Send.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        Cancel.setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));


        TextView titleTextView = dialog.findViewById(R.id.txt_title);
        Send.setText(popupSubmitText);
        Cancel.setText(popupCancelText);
        titleTextView.setText(popupTitleText);
        fontManager.nokri_setMonesrratSemiBioldFont(titleTextView,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(Send,getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(Cancel,getActivity().getAssets());

        final EditText message = dialog.findViewById(R.id.editText3);
        message.setHint(popuphintText);

/*
        Send.setText(sendCmntBtn.getText());
        Cancel.setText(settingsMain.getAlertCancelText());
        message.setHint(editTextMessage.getHint());
*/

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().trim().isEmpty()) {
                    commentsModelList.clear();
                    nextPage = 1;
                    nokri_postComment(true,message.getText().toString());
                    message.setText("");
                    dialog.dismiss();
                }
                else
                    Nokri_ToastManager.showLongToast(getContext(), Nokri_Globals.REQUIRED_REPY_TEXT);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_load_more:
                loadMoreButton.setVisibility(View.GONE);

                    if (hasNextPage)
                       nokri_getBlogDetails(false,"loadmore");

                break;
            case R.id.btn_publish:


                nextPage = 1;
                if(!Nokri_SharedPrefManager.isAccountPublic(getContext())) {

                    if(!commentsEditText.getText().toString().trim().isEmpty()) {
                        commentsModelList.clear();
                        nokri_postComment(false, "comments");

                    }else
                        Nokri_ToastManager.showShortToast(getContext(), Nokri_Globals.COMMENT_REQUIRED_TEXT);
                }
                else
                    Nokri_ToastManager.showLongToast(getContext(), pleaseLoginFirst);
                break;
        }

    }
}
