package com.scriptsbundle.nokri.guest.home.fragments;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
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

import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.candidate.jobs.adapters.Nokri_JobsAdapter;
import com.scriptsbundle.nokri.candidate.jobs.models.Nokri_JobsModel;
import com.scriptsbundle.nokri.candidate.profile.fragments.Nokri_CompanyPublicProfileFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.scriptsbundle.nokri.guest.blog.adapters.Nokri_BlogGridAdapter;
import com.scriptsbundle.nokri.guest.blog.models.Nokri_BlogGridModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
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


public class Nokri_FeaturedJobsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView bottomRecyclerView;
    private List<Nokri_JobsModel> modelList;
    private Nokri_BlogGridAdapter blogGridAdapter;
    private List<Nokri_BlogGridModel>blogGridModelList;
    private TextView emptyTextViewBottom;
    private TextView emptyTextView;
    private ImageView messageImage;
    RelativeLayout blog;
    private LinearLayout messageContainer;
    private boolean mUserSeen = false;
    private boolean mViewCreated = false;
    private TextView blogTitleTextView;
    private int nextPage=1;
    private int blogGridNextPage =1;
    private boolean blogGridHasNextPage = true;
    private boolean hasNextPage = true;
    private Button loadMoreButton;
    private Button loadMoreButtonBottom;
    private ProgressBar progressBarBottom;
    private LinearLayout messageContainerBotton;

    private ProgressBar progressBar;
    private nokri_pagerCallback pagerCallback;
    public static String CALLING_SOURCE="";
    public static boolean CALLED_FROM_DASHBOARD = false;
    private RecyclerView middleRecyclerView;
    private TextView emptyTextViewMiddle;
    private Nokri_JobsAdapter adapter;
    private ImageView messageImageMiddle;
    private ImageView messageImageBottom;
    private LinearLayout messageContainerMiddle;

    private Button loadMoreButtonMiddle;
    private ProgressBar progressBarMiddle;
    private RelativeLayout secondCntainer;

    public void nokri_setPagerCallback(nokri_pagerCallback pagerCallback) {
        this.pagerCallback = pagerCallback;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nokri_initialize(view);
        blog = view.findViewById(R.id.blog);
        nextPage = 1;
        try {
            getFeaturedJobsFromHome(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!CALLING_SOURCE.equals("")){
            blogGridNextPage = 1;
            getBlogsFromHome();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
        } catch (Exception e) {
        }


    }

    public Nokri_FeaturedJobsFragment() {
    }

    public void getFeaturedJobsFromHome(boolean showAlert) throws JSONException {
        emptyTextView.setText("");
        JSONObject response = Nokri_HomeScreenFragment.FEATURED_JOBS_RESPONSE;
        JSONObject pagination = response.getJSONObject("pagination");

        nextPage = pagination.getInt("next_page");


        hasNextPage = pagination.getBoolean("has_next_page");
        JSONObject data = response.getJSONObject("data");
        if(CALLED_FROM_DASHBOARD) {

        }
        if(!hasNextPage){
            loadMoreButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        else
            loadMoreButton.setVisibility(View.VISIBLE);
        JSONArray jobsArray = data.getJSONArray("jobs");

        if(jobsArray.length() == 0){
            messageContainer.setVisibility(View.VISIBLE);
            emptyTextView.setText(response.getString("message"));
            progressBar.setVisibility(View.GONE);
            loadMoreButton.setVisibility(View.GONE);
            nokri_setupAdapter();
            if(showAlert)
            return;

        }
        else
            messageContainer.setVisibility(View.GONE);
        for(int i = 0;i<jobsArray.length();i++)
        {
            JSONArray dataArray =  jobsArray.getJSONArray(i);
            Nokri_JobsModel model = new Nokri_JobsModel();
            for(int j =0;j<dataArray.length();j++)
            {model.setShowMenu(false);
                JSONObject object =   dataArray.getJSONObject(j);
                if(object.getString("field_type_name").equals("job_id"))
                    model.setJobId(object.getString("value"));
                if(object.getString("field_type_name").equals("company_id"))
                    model.setCompanyId(object.getString("value"));
                else if (object.getString("field_type_name").equals("job_name"))
                    model.setJobTitle(object.getString("value"));
                else if (object.getString("field_type_name").equals("company_name"))
                    model.setJobDescription(object.getString("value"));
                else if (object.getString("field_type_name").equals("job_salary"))
                    model.setSalary(object.getString("value"));
                else if (object.getString("field_type_name").equals("job_type"))
                    model.setJobType(object.getString("value"));
                else if (object.getString("field_type_name").equals("company_logo"))
                    model.setCompanyLogo(object.getString("value"));
                else if (object.getString("field_type_name").equals("job_location")) {
                    model.setAddress(object.getString("value"));

                } else if (object.getString("field_type_name").equals("job_posted")) {
                    model.setTimeRemaining(object.getString("value"));

                }
                model.setFeatured(true);
                if(j+1 == dataArray.length())
                    modelList.add(model);
            }

        }
        nokri_setupAdapter();
        if(!hasNextPage){
            progressBarBottom.setVisibility(View.GONE);
            getView().findViewById(R.id.load_more_layout).setVisibility(View.GONE);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_featured_jobs, container, false);
    }


    private void nokri_loadBlogGrid() {
        RestService restService;
        if (!Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class);

        JsonObject params = new JsonObject();
        params.addProperty("page_number", blogGridNextPage);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getHomeBlog(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getHomeBlog(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getFollowedCompanies(Nokri_RequestHeaderManager.addHeaders());
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {

                        emptyTextViewBottom.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject extra = response.getJSONObject("extra");
                        blogTitleTextView.setText(extra.getString("page_title"));
                        loadMoreButtonBottom.setText(extra.getString("load_more"));
                        JSONObject data = response.getJSONObject("data");
                        JSONObject pagination = data.getJSONObject("pagination");

                        blogGridNextPage = pagination.getInt("next_page");

                        blogGridHasNextPage = pagination.getBoolean("has_next_page");
                        if (!blogGridHasNextPage) {
                            loadMoreButtonBottom.setVisibility(View.GONE);
                            progressBarBottom.setVisibility(View.GONE);
                        } else {
                            loadMoreButtonBottom.setVisibility(View.VISIBLE);
                            progressBarBottom.setVisibility(View.VISIBLE);
                        }
                        JSONArray postArray = data.getJSONArray("post");

                        if (postArray.length() == 0) {
                            messageContainerBotton.setVisibility(View.VISIBLE);
                            emptyTextViewBottom.setText(response.getString("message"));
                            progressBarBottom.setVisibility(View.GONE);
                            loadMoreButtonBottom.setVisibility(View.GONE);
                            setupBlogAdapter();
                            return;
                        } else
                            messageContainerBotton.setVisibility(View.GONE);
                        for (int i = 0; i < postArray.length(); i++) {
                            JSONObject object = postArray.getJSONObject(i);
                            Nokri_BlogGridModel model = new Nokri_BlogGridModel();
                            model.setHtmlResponse(false);
                            model.setId(object.getString("post_id"));
                            model.setHeadingText(object.getString("title"));
                            model.setParagraphText(object.getString("excerpt"));
                            model.setDateText(object.getString("date"));
                            model.setHeaderImage(object.getString("image"));
                            model.setHasImage(object.getBoolean("has_image"));
                            model.setCommentsText(extra.getString("comment_title") + " " + object.getString("comments"));
                            blogGridModelList.add(model);

                             /*   if(j+1==dataArray.length())
                                    modelList.add(model);*/

                        }
                        //   Log.d("Pointz",modelList.toString());
                        setupBlogAdapter();
                        if (!blogGridHasNextPage) {

                            progressBarBottom.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {


                        e.printStackTrace();

                    }

                } else {

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }

        });
    }


    public void getBlogsFromHome() {
        try {

            emptyTextViewBottom.setText("");
            JSONObject response = Nokri_HomeScreenFragment.BLOGS_RESPONSE;
            JSONObject extra = response.getJSONObject("extra");
            blogTitleTextView.setText(extra.getString("page_title"));
            loadMoreButtonBottom.setText(extra.getString("load_more"));
            JSONObject data = response.getJSONObject("data");
            JSONObject pagination = data.getJSONObject("pagination");

            blogGridNextPage = pagination.getInt("next_page");

            blogGridHasNextPage = pagination.getBoolean("has_next_page");
            if (!blogGridHasNextPage) {
                loadMoreButtonBottom.setVisibility(View.GONE);
                progressBarBottom.setVisibility(View.GONE);
            } else {
                loadMoreButtonBottom.setVisibility(View.VISIBLE);
                progressBarBottom.setVisibility(View.VISIBLE);
            }
            JSONArray postArray = data.getJSONArray("post");

            if (postArray.length() == 0) {
                messageContainerBotton.setVisibility(View.VISIBLE);
                emptyTextViewBottom.setText(response.getString("message"));
                progressBarBottom.setVisibility(View.GONE);
                loadMoreButtonBottom.setVisibility(View.GONE);
                setupBlogAdapter();
                return;
            } else
                messageContainerBotton.setVisibility(View.GONE);
            for (int i = 0; i < postArray.length(); i++) {
                JSONObject object = postArray.getJSONObject(i);
                Nokri_BlogGridModel model = new Nokri_BlogGridModel();
                model.setHtmlResponse(false);
                model.setId(object.getString("post_id"));
                model.setHeadingText(object.getString("title"));
                model.setParagraphText(object.getString("excerpt"));
                model.setDateText(object.getString("date"));
                model.setHeaderImage(object.getString("image"));
                model.setHasImage(object.getBoolean("has_image"));
                model.setCommentsText(extra.getString("comment_title") + " " + object.getString("comments"));
                blogGridModelList.add(model);

                             /*   if(j+1==dataArray.length())
                                    modelList.add(model);*/

            }
            //   Log.d("Pointz",modelList.toString());
            setupBlogAdapter();
            if (!blogGridHasNextPage) {

                progressBarBottom.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void nokri_initialize(View view){
        RelativeLayout relativeLayout = getView().findViewById(R.id.mainRelLayout);
        if (Nokri_Globals.DESIGN_TYPE == 1)
            relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        emptyTextView = getView().findViewById(R.id.txt_empty);
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        emptyTextViewBottom = getView().findViewById(R.id.txt_empty2);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        Picasso.get().load(R.drawable.logo).into(messageImage);
        loadMoreButtonBottom = getView().findViewById(R.id.btn_load_more2);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButtonBottom);
        progressBarBottom = getView().findViewById(R.id.progress_bar2);
        messageContainerBotton = getView().findViewById(R.id.msg_container2);
        loadMoreButtonBottom.setOnClickListener(this);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);
        progressBar = getView().findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);

        //Nokri_Utils.setRoundButtonColor(getContext(),loadMoreButton);

        modelList = new ArrayList<>();

        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        blogTitleTextView = getView().findViewById(R.id.txt_blog_title);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(blogTitleTextView,getActivity().getAssets());


        blogGridModelList = new ArrayList<>();
        bottomRecyclerView = getView().findViewById(R.id.recyclerview2);
        bottomRecyclerView.setNestedScrollingEnabled(false);
    }

    public interface nokri_pagerCallback {

        void onJobClick(Nokri_JobsModel model);

        void onCompanyClick(Nokri_JobsModel model);

        void onIconClick(Nokri_JobsModel model);

        void loadThisFragmentExternally();

        void onBlogItemClicked(Nokri_BlogGridModel blogGridModel);

        void onClickBlogLoadMode();
    }

    private void setupBlogAdapter() {
        bottomRecyclerView = getView().findViewById(R.id.recyclerview2);

        blogGridAdapter = new Nokri_BlogGridAdapter(blogGridModelList, getContext(), new Nokri_BlogGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_BlogGridModel item) {
                if (pagerCallback != null)
                    pagerCallback.onBlogItemClicked(item);
            }
        });
        blogGridAdapter.setMultiLine(true);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        bottomRecyclerView.setLayoutManager(layoutManager);

        bottomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomRecyclerView.setAdapter(blogGridAdapter);


    }


    private void nokri_setupAdapter(){
        Nokri_JobsAdapter adapter = new Nokri_JobsAdapter(modelList,getContext(), new Nokri_JobsAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Nokri_JobsModel item) {

            }

            @Override
            public void onCompanyClick(Nokri_JobsModel item) {

                if(CALLING_SOURCE.equals(""))
                {
                    androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                    Nokri_CompanyPublicProfileFragment.COMPANY_ID = item.getCompanyId();
                    fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

                }
                else
                    pagerCallback.onCompanyClick(item);
            }

            @Override
            public void menuItemSelected(Nokri_JobsModel model, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_view_job:
                        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                        Nokri_JobDetailFragment.CALLING_SOURCE = "";
                        Bundle bundle = new Bundle();
                        bundle.putString("job_id",model.getJobId());
                        jobDetailFragment.setArguments(bundle);
                        Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).commit();
                        break;
                    case  R.id.menu_view_company_profile:
                        androidx.fragment.app.FragmentManager fragmentManager2 = getFragmentManager();
                        androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                        Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                        Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
                        fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).commit();
                        break;
                }
            }
        });
        adapter.setOnImageClickListener(new Nokri_JobsAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Nokri_JobsModel model) {
                if(CALLING_SOURCE.equals(""))
                {
                    androidx.fragment.app.FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    Fragment companyPublicProfileFragment = new Nokri_CompanyPublicProfileFragment();

                    Nokri_CompanyPublicProfileFragment.COMPANY_ID = model.getCompanyId();
                    fragmentTransaction2.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),companyPublicProfileFragment).addToBackStack(null).commit();

                }
                else
                    pagerCallback.onIconClick(model);
            }
        });
        adapter.setOnJobClickListener(new Nokri_JobsAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Nokri_JobsModel model) {
                if(CALLING_SOURCE.equals(""))
                {
                    androidx.fragment.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
                    androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment jobDetailFragment = new Nokri_JobDetailFragment();
                    Nokri_JobDetailFragment.CALLING_SOURCE = "";
                    Bundle bundle = new Bundle();
                    bundle.putString("job_id",model.getJobId());
                    jobDetailFragment.setArguments(bundle);
                    Nokri_JobDetailFragment.COMPANY_ID = model.getCompanyId();
                    fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(),jobDetailFragment).addToBackStack(null).commit();

                }
                else
                    pagerCallback.onJobClick(model);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    private void nokri_getFeaturedJobs(final Boolean showAlert) {
        Nokri_DialogManager dialogManager = new Nokri_DialogManager();
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            dialogManager.showAlertDialog(getActivity());
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class);
        JsonObject params = new JsonObject();

        params.addProperty("page_number", nextPage);


        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getFeaturedJobs(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getFeaturedJobs(params, Nokri_RequestHeaderManager.addHeaders());
        }
        // Call<ResponseBody> myCall = restService.getAppliedJobs(Nokri_RequestHeaderManager.addHeaders());
        Nokri_DialogManager finalDialogManager = dialogManager;
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {

                if (responseObject.isSuccessful()) {
                    try {

                        emptyTextViewMiddle.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject pagination = response.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");

                        JSONObject data = response.getJSONObject("data");

                        if (CALLED_FROM_DASHBOARD) {
                            TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                            toolbarTitleTextView.setText(data.getString("tab_title"));

                        }
                        if (!hasNextPage) {
                            loadMoreButtonMiddle.setVisibility(View.GONE);
                            progressBarMiddle.setVisibility(View.GONE);
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.GONE);
                        } else {
                            loadMoreButtonMiddle.setVisibility(View.VISIBLE);
                            progressBarMiddle.setVisibility(View.VISIBLE);
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.VISIBLE);
                        }

                        JSONArray jobsArray = data.getJSONArray("jobs");

                        if (jobsArray.length() == 0) {
                            messageContainerMiddle.setVisibility(View.VISIBLE);
                            emptyTextViewMiddle.setText(response.getString("message"));
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.GONE);
                            progressBarMiddle.setVisibility(View.GONE);
                            loadMoreButtonMiddle.setVisibility(View.GONE);
                            nokri_setupAdapter();
                            if (showAlert && CALLING_SOURCE.equals(""))
                                finalDialogManager.hideAlertDialog();
                            return;

                        } else
                            messageContainerMiddle.setVisibility(View.GONE);
                        for (int i = 0; i < jobsArray.length(); i++) {
                            JSONArray dataArray = jobsArray.getJSONArray(i);
                            Nokri_JobsModel model = new Nokri_JobsModel();
                            for (int j = 0; j < dataArray.length(); j++) {
                                model.setShowMenu(false);
                                JSONObject object = dataArray.getJSONObject(j);
                                if (object.getString("field_type_name").equals("job_id"))
                                    model.setJobId(object.getString("value"));
                                if (object.getString("field_type_name").equals("company_id"))
                                    model.setCompanyId(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_name"))
                                    model.setJobTitle(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_name"))
                                    model.setJobDescription(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_salary"))
                                    model.setSalary(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_type"))
                                    model.setJobType(object.getString("value"));
                                else if (object.getString("field_type_name").equals("company_logo"))
                                    model.setCompanyLogo(object.getString("value"));
                                else if (object.getString("field_type_name").equals("job_location")) {
                                    model.setAddress(object.getString("value"));
                                } else if (object.getString("field_type_name").equals("job_posted"))
                                    model.setTimeRemaining(object.getString("value"));
                                else if (object.getString("field_type_name").equals("is_feature")) {
                                    model.setFeatured(object.getBoolean("value"));
                                }

                                if (j + 1 == dataArray.length())
                                    modelList.add(model);
                            }

                        }
                        nokri_setupAdapter();
                        if (!hasNextPage) {

                            progressBarMiddle.setVisibility(View.GONE);
                            getView().findViewById(R.id.load_more_layout).setVisibility(View.GONE);
                        }
                        if (showAlert && CALLING_SOURCE.equals(""))
                            finalDialogManager.hideAfterDelay();

                    } catch (IOException e) {
                        if (showAlert) {
                            finalDialogManager.showCustom(e.getMessage());
                            finalDialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (JSONException e) {
                        if (showAlert) {
                            finalDialogManager.showCustom(e.getMessage());
                            finalDialogManager.hideAfterDelay();
                        }

                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        finalDialogManager.showCustom(responseObject.message());
                        finalDialogManager.hideAfterDelay();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (showAlert) {
                    Nokri_ToastManager.showLongToast(getContext(), t.getMessage());
                    finalDialogManager.hideAfterDelay();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_load_more) {
            loadMoreButtonMiddle.setVisibility(View.GONE);
            if (hasNextPage && CALLING_SOURCE.equals("")) {

                nokri_getFeaturedJobs(false);

            } else
                pagerCallback.loadThisFragmentExternally();
        } else if (i == R.id.btn_load_more2) {
            if (pagerCallback != null)
                pagerCallback.onClickBlogLoadMode();
        }
    }
}

