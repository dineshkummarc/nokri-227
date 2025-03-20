package com.scriptsbundle.nokri.utils;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.models.RatingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingsBottomSheet extends BottomSheetDialogFragment implements RatingsAdapter.ItemClickListener {

    LinearLayout progress,ratingContainer;
    ListView ratingsList;
    String id;
    TextView reviewsLabel;
    ArrayList<RatingModel> ratings = new ArrayList<>();
    RatingsListAdapter adapter;
    boolean calledFromEmployer;
    public RatingsBottomSheet(String id, boolean calledFromEmployer) {
        this.id = id;
        this.calledFromEmployer = calledFromEmployer;
    }

    Call<ResponseBody> call;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rating_list,
                container, false);

        ratingsList = v.findViewById(R.id.ratings);
        progress = v.findViewById(R.id.progress);
        ratingContainer = v.findViewById(R.id.ratingContainer);
        reviewsLabel = v.findViewById(R.id.reviewsLabel);


        getData();

        return v;
    }


    public void getData(){
        progress.setVisibility(View.VISIBLE);
        ratingContainer.setVisibility(View.GONE);
        JsonObject params = new JsonObject();
        params.addProperty("user_id",id);

        if (calledFromEmployer){

            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                call = Nokri_ServiceGenerator.createService(RestService.class).getEmployerRatings(params,Nokri_RequestHeaderManager.addSocialHeaders());
            }else{
                call = Nokri_ServiceGenerator.createService(RestService.class).getEmployerRatings(params,Nokri_RequestHeaderManager.addHeaders());
            }
        }else{
            if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
                call = Nokri_ServiceGenerator.createService(RestService.class).getCandidateRatings(params,Nokri_RequestHeaderManager.addSocialHeaders());
            }else{
                call = Nokri_ServiceGenerator.createService(RestService.class).getCandidateRatings(params,Nokri_RequestHeaderManager.addHeaders());
            }
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseBody) {
                progress.setVisibility(View.GONE);
                ratingContainer.setVisibility(View.VISIBLE);
                if (responseBody.isSuccessful()){
                    try {
                        JSONObject response = new JSONObject(responseBody.body().string());
                        JSONObject ratingSection = response.getJSONObject("data").getJSONObject("user_reviews");
                        JSONArray reviewArray = ratingSection.getJSONArray("reviews_data");
                        reviewsLabel.setText(ratingSection.getJSONObject("extra").getString("page_title"));
                        ratings.clear();
                        for (int i = 0;i<reviewArray.length();i++){
                            RatingModel model =new RatingModel();
                            JSONObject jsonObject1 = reviewArray.getJSONObject(i);
                            model.setDate(jsonObject1.getString("_rating_date"));
                            model.setRaterName(jsonObject1.getString("_rating_poster"));
                            model.setRating(jsonObject1.getDouble("_rating_avg"));
                            model.setContainsReply(jsonObject1.getBoolean("has_reply"));
                            model.setAuthorReply(jsonObject1.getString("reply_text"));
                            model.setAuthorName(jsonObject1.getString("reply_heading"));
                            model.setRaterTitle(jsonObject1.getString("_rating_title"));
                            model.setRaterComments(jsonObject1.getString("_rating_description"));
                            model.setCanReply(jsonObject1.getBoolean("can_reply"));
                            if (calledFromEmployer)
                                model.setUrl(jsonObject1.getString("cand_image"));
                            else
                                model.setUrl(jsonObject1.getString("emp_image"));
                            model.setCommentID(jsonObject1.getString("cid"));
                            model.replyLabel = ratingSection.getJSONObject("extra").getString("reply_btn_text");
                            model.cancel = ratingSection.getJSONObject("extra").getString("cancel_btn");
                            model.submit = ratingSection.getJSONObject("extra").getString("submit");
                            ratings.add(model);
                        }


                        adapter = new RatingsListAdapter(getActivity(),ratings,true);
//                        ratingsList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        ratingsList.setAdapter(adapter);
                        adapter.setClickListener(RatingsBottomSheet.this);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                }else{
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (call !=null){
            call.cancel();
        }
    }

    @Override
    public void onItemClick(RatingModel model, int position, String comment,AlertDialog dialog) {
        postReply(model,comment,dialog);
    }

    public void postReply(RatingModel model, String replyText, AlertDialog dialog){
        JsonObject params = new JsonObject();
        params.addProperty("cid",model.getCommentID());
        params.addProperty("reply_text",replyText);
        Call<ResponseBody> call;
        if (Nokri_SharedPrefManager.isSocialLogin(getActivity())) {
            call = Nokri_ServiceGenerator.createService(RestService.class).postReply(params, Nokri_RequestHeaderManager.addSocialHeaders());
        }else{
            call = Nokri_ServiceGenerator.createService(RestService.class).postReply(params, Nokri_RequestHeaderManager.addHeaders());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (response.isSuccessful()){
                    model.setAuthorReply(replyText);
                    model.setContainsReply(true);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }
}