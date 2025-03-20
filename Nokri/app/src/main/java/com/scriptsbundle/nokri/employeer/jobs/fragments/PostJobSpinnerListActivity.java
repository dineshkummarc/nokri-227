package com.scriptsbundle.nokri.employeer.jobs.fragments;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.adapters.PostJobSpinnerListAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostJobSpinnerListActivity extends AppCompatActivity {
    PostJobSpinnerListAdapter adapter;
    ArrayList<String> items2 = new ArrayList<>();
    ArrayList<Nokri_SpinnerModel> items = new ArrayList<>();
    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();
    ListView list;
    LinearLayout progressBar;
    String calledFrom;
    int locationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job_spinner_list);

        progressBar = findViewById(R.id.progressBar);
        list = findViewById(R.id.list);
        calledFrom = getIntent().getStringExtra("calledFrom");

        locationCount = getIntent().getIntExtra("locationCount", -1);
        try {
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("list"));
            Type type = new TypeToken<ArrayList<Nokri_SpinnerModel>>() {
            }.getType();
            items = new Gson().fromJson(jsonArray.toString(), type);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new PostJobSpinnerListAdapter(items, this);
        list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (items.get(position).isHasChild()) {
                    if (calledFrom.equals("country")) {
                        if (locationCount == 0) {
                            PostJobStep3.countryId = items.get(position).getId();
                        }
                        if (locationCount == 1) {
                            PostJobStep3.stateId = items.get(position).getId();
                        }
                        if (locationCount == 2) {
                            PostJobStep3.cityId = items.get(position).getId();
                        }
                        if (locationCount == 3) {
                            PostJobStep3.townId = items.get(position).getId();
                        }
                        nokri_getSubLocations(items.get(position).getId());
                    } else {

                        nokri_getSubFields(items.get(position).getId());
                    }
                } else {
                    if (!calledFrom.equals("zone")){
                        if (calledFrom.equals("country")) {
                            if (locationCount == 0) {
                                PostJobStep3.countryId = items.get(position).getId();
                            }
                            if (locationCount == 1) {
                                PostJobStep3.stateId = items.get(position).getId();
                            }
                            if (locationCount == 2) {
                                PostJobStep3.cityId = items.get(position).getId();
                            }
                            if (locationCount == 3) {
                                PostJobStep3.townId = items.get(position).getId();
                            }
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("some_key", (Serializable) items.get(position));
                        resultIntent.putExtra("country_id",PostJobStep3.countryId);
                        resultIntent.putExtra("state_id",PostJobStep3.stateId);
                        resultIntent.putExtra("city_id",PostJobStep3.cityId);
                        resultIntent.putExtra("town_id",PostJobStep3.townId);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }else{
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("some_key", (Serializable) items.get(position));
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }

            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextWatcher = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                adapter.filter(newText);
                return true;
            }

        };

        searchView.setOnQueryTextListener(queryTextWatcher);
    }

    private void nokri_getSubFields(String id) {

      /*  dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(PostJob.this);*/
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cat_id", id);

        params.add(jsonObject);


        progressBar.setVisibility(View.VISIBLE);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(this)) {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getSubFields(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONArray response = new JSONArray(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        makeSpinnerList(response);
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        //   dialogManager.showCustom(e.getMessage());
//                        progressBar.setVisibility(View.GONE);
                        //    dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.GONE);
                   /*     dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();*/
//                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();

                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                  /*  dialogManager.showCustom(responseObject.code()+"");
                    dialogManager.hideAfterDelay();*/
//                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              /*  dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();*/
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void nokri_getSubLocations(String id) {

      /*  dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(PostJob.this);*/
        JsonArray params = new JsonArray();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country_id", id);

        params.add(jsonObject);


        progressBar.setVisibility(View.VISIBLE);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(this), Nokri_SharedPrefManager.getPassword(this), this);

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(this)) {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCountryCityState(jsonObject, Nokri_RequestHeaderManager.addHeaders());
        }

        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        JSONArray response = new JSONArray(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        makeSpinnerList(response);
                        progressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        //   dialogManager.showCustom(e.getMessage());
//                        progressBar.setVisibility(View.GONE);
                        //    dialogManager.hideAfterDelay();

                        e.printStackTrace();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.GONE);
                   /*     dialogManager.showCustom(e.getMessage());
                        dialogManager.hideAfterDelay();*/
//                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();

                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                  /*  dialogManager.showCustom(responseObject.code()+"");
                    dialogManager.hideAfterDelay();*/
//                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              /*  dialogManager.showCustom(t.getMessage());
                dialogManager.hideAfterDelay();*/
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void makeSpinnerList(JSONArray jsonArray) {
        categorySpinnerList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Nokri_SpinnerModel model = new Nokri_SpinnerModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                model.setName(jsonObject.getString("value"));
                model.setId(jsonObject.getString("key"));
                model.setHasChild(jsonObject.getBoolean("has_child"));
                categorySpinnerList.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String jsonList;
        jsonList = new Gson().toJson(categorySpinnerList);

        if (jsonList != null) {
            overridePendingTransition(R.anim.right_enter, R.anim.right_enter);
            Intent intent = new Intent(PostJobSpinnerListActivity.this, PostJobSpinnerListActivity.class);
            locationCount++;
            intent.putExtra("list", jsonList);
            intent.putExtra("calledFrom", calledFrom);
            intent.putExtra("locationCount", locationCount);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
        }

    }

}
