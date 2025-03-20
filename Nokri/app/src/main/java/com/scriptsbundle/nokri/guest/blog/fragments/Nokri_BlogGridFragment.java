package com.scriptsbundle.nokri.guest.blog.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.dashboard.models.Nokri_CandidateDashboardModel;
import com.scriptsbundle.nokri.employeer.dashboard.models.Nokri_EmployeerDashboardModel;
import com.scriptsbundle.nokri.guest.blog.adapters.Nokri_BlogGridAdapter;
import com.scriptsbundle.nokri.guest.blog.models.Nokri_BlogGridModel;
import com.scriptsbundle.nokri.guest.dashboard.models.Nokri_GuestDashboardModel;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_BlogGridFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private Nokri_BlogGridAdapter adapter;
    private List<Nokri_BlogGridModel> modelList;
    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private int counter = 0;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Boolean checkLoading = false;
    private ProgressBar progressBar;
    private int nextPage = 1;
    private boolean hasNextPage;
    private Button loadMoreButton;
    public static String BLOG_ID;
    private Nokri_DialogManager dialogManager;

    RelativeLayout mainLayout;
    ShimmerFrameLayout shimmerContainer;
    LinearLayout loadingLayout;
    public Nokri_BlogGridFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nokri_initialize(view);
    }

    private void nokri_initialize(View view) {
        mainLayout = view.findViewById(R.id.mainLayout);
        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        loadingLayout = view.findViewById(R.id.shimmerMain);
        recyclerView = view.findViewById(R.id.recyclerview);
        emptyTextView = view.findViewById(R.id.txt_empty);
        messageImage = view.findViewById(R.id.img_message);
        messageContainer = view.findViewById(R.id.msg_container);
        loadMoreButton = view.findViewById(R.id.btn_load_more);
        new Nokri_FontManager().nokri_setOpenSenseFontButton(loadMoreButton, getActivity().getAssets());
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView, getActivity().getAssets());
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        modelList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_bar);
        loadMoreButton.setOnClickListener(this);

        nextPage = 1;
        counter = 1;

        String toolbarTitle = "";


        if (Nokri_SharedPrefManager.isAccountPublic(getContext())) {
            Nokri_GuestDashboardModel model = Nokri_SharedPrefManager.getGuestSettings(getContext());
            toolbarTitle = model.getBlog();
        } else if (Nokri_SharedPrefManager.isAccountEmployeer(getContext())) {

            Nokri_EmployeerDashboardModel model = Nokri_SharedPrefManager.getEmployeerSettings(getContext());
            toolbarTitle = model.getBlog();
        } else if (Nokri_SharedPrefManager.isAccountCandidate(getContext())) {

            Nokri_CandidateDashboardModel model = Nokri_SharedPrefManager.getCandidateSettings(getContext());
            toolbarTitle = model.getBlog();

        }


        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

        toolbarTitleTextView.setText(toolbarTitle);


        nokri_loadMore(true);

        swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            checkLoading = true;
//            adforest_swipeRefresh();
            nokri_recreate_Submit_jobSearch();

        });

    }

    public void nokri_recreate_Submit_jobSearch() {
        Nokri_BlogGridFragment nokri_blogGridFragment = new Nokri_BlogGridFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, nokri_blogGridFragment);
        transaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_blog_grid, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    private void nokri_loadMore(final Boolean showAlert) {
        if (showAlert) {
            dialogManager = new Nokri_DialogManager();
            mainLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            Nokri_Utils.isCallRunning = true;
        }
        RestService restService;
        if (!Nokri_SharedPrefManager.isAccountPublic(getContext()))
            restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        else
            restService = Nokri_ServiceGenerator.createService(RestService.class);

        JsonObject params = new JsonObject();
        params.addProperty("page_number", nextPage);
        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getBlog(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getBlog(params, Nokri_RequestHeaderManager.addHeaders());
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
                if (responseObject.isSuccessful()) {
                    try {

                        emptyTextView.setText("");
                        JSONObject response = new JSONObject(responseObject.body().string());
                        JSONObject extra = response.getJSONObject("extra");
                        loadMoreButton.setText(extra.getString("load_more"));


                        JSONObject data = response.getJSONObject("data");
                        JSONObject pagination = data.getJSONObject("pagination");

                        nextPage = pagination.getInt("next_page");

                        hasNextPage = pagination.getBoolean("has_next_page");

                        if (!hasNextPage) {
                            loadMoreButton.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            loadMoreButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        JSONArray postArray = data.getJSONArray("post");

                        if (postArray.length() == 0) {
                            messageContainer.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);
                            loadMoreButton.setVisibility(View.GONE);
                            if (showAlert)
                                dialogManager.hideAlertDialog();
                            setupAdapter();
                            return;
                        } else
                            messageContainer.setVisibility(View.GONE);
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
                            modelList.add(model);

                             /*   if(j+1==dataArray.length())
                                    modelList.add(model);*/

                        }
                        //   Log.d("Pointz",modelList.toString());
                        setupAdapter();
                        if (!hasNextPage) {

                            progressBar.setVisibility(View.GONE);
                        }
                        if (showAlert)
                            dialogManager.hideAfterDelay();
                    } catch (JSONException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (showAlert) {
                            dialogManager.showCustom(e.getMessage());
                            dialogManager.hideAfterDelay();
                        }
                        e.printStackTrace();

                    }

                } else {
                    if (showAlert) {
                        dialogManager.showCustom(responseObject.message());
                        dialogManager.hideAfterDelay();
                    }
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

    @Override
    public void onClick(View view) {
        loadMoreButton.setVisibility(View.GONE);

        //     if (hasNextPage)
        nokri_loadMore(false);


    }

    private void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);

        adapter = new Nokri_BlogGridAdapter(modelList, getContext(), new Nokri_BlogGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_BlogGridModel item) {
                BLOG_ID = item.getId();
                androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
                androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment editEmailTemplate = new Nokri_BlogDetailFragment();
                fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), editEmailTemplate).addToBackStack(null).commit();
            }
        });
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
