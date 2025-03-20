package com.scriptsbundle.nokri.candidate.jobs.fragments;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.jobs.adapters.CandidateNotificationAdapter;
import com.scriptsbundle.nokri.candidate.jobs.models.CandidateNotificationModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.MatchedResumes.DividerItemDecoration;
import com.scriptsbundle.nokri.employeer.jobs.fragments.Nokri_JobDetailFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeInterface;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.VerticalSpaceItemDecoration;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CandidateJobNotifications extends Fragment implements SavedResumeInterface, View.OnClickListener {

    RecyclerView recyclerView;
    LinearLayout progressBar, emptyLayout;
    ConstraintLayout parentLayout;
    ProgressBar progress_bar;
    CandidateNotificationAdapter adapter;
    TextView emptyTextView;
    ArrayList<CandidateNotificationModel> candNotifications = new ArrayList<>();
    RecyclerViewSkeletonScreen skeleton;
    ShimmerFrameLayout container1;


    private int maxNumOfPages, currentPage, nextPage, increment, currentNoOfJobs;
    private boolean hasNextPage, loading = true;
    private Button loadMoreButton;

    public CandidateJobNotifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_candidate_job_notifications, container, false);
        container1 = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        container1.startShimmer();

        parentLayout = view.findViewById(R.id.parentLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progress_bar = view.findViewById(R.id.progress_bar);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        emptyTextView = view.findViewById(R.id.txt_empty);
        loadMoreButton = view.findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(), loadMoreButton);
        adapter = new CandidateNotificationAdapter(getActivity(), candNotifications, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.my_divider));
        recyclerView.setAdapter(adapter);

//        skeleton = Skeleton.bind(recyclerView).adapter(adapter)
//                .load(R.layout.item_candidate_notification)
//                .shimmer(true)      // whether show shimmer animation.                      default is true
//                .count(10)          // the shimmer color.                                   default is #a2878787
//                .angle(20)          // the shimmer angle.                                   default is 20;
//                .duration(1000)     // the shimmer animation duration.                      default is 1000;
//                .frozen(false) .show();

        getCandidateNotifications(false,1);
        loadMoreButton.setOnClickListener(this);
        return view;
    }



    public void getCandidateNotifications(boolean calledFromLoadMore, int pageNumber) {

        if (!calledFromLoadMore){
            candNotifications.clear();
        }
//        progressBar.setVisibility(View.VISIBLE);
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        Call<ResponseBody> myCall;


        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getCandidateNotifications( Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getCandidateNotifications(Nokri_RequestHeaderManager.addHeaders());
        }


        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);
                        if (response.getBoolean("success")) {
                            progressBar.setVisibility(View.GONE);
                            //    nokri_getLocationAndAddress();
                            JSONObject data = response.getJSONObject("data");
                            toolbarTitleTextView.setText(data.getString("page_title"));

                            JSONArray candidateList = data.getJSONArray("notification");

                            for (int i = 0; i < candidateList.length(); i++) {
                                JSONArray jsonArray = candidateList.getJSONArray(i);
                                CandidateNotificationModel model = new CandidateNotificationModel();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    String entity = jsonArray.getJSONObject(j).getString("field_type_name");

                                    if (entity.equals("company_name")) {
                                        model.setCompanyName(jsonArray.getJSONObject(j).getString("value"));
                                    } else if (entity.equals("company_img")) {
                                        model.setImageUrl(jsonArray.getJSONObject(j).getString("value"));
                                    } else if (entity.equals("job_post")) {
                                        model.setPost(jsonArray.getJSONObject(j).getString("value"));
                                    } else if (entity.equals("posting_time")) {
                                        model.setPostingTime(jsonArray.getJSONObject(j).getString("value"));
                                    } else if (entity.equals("job_title")) {
                                        model.setJobTitle(jsonArray.getJSONObject(j).getString("value"));
                                    } else if (entity.equals("job_id")) {
                                        model.setJobId(jsonArray.getJSONObject(j).getString("value"));
                                    }
                                }

                                candNotifications.add(model);

                            }
                            adapter.notifyDataSetChanged();

                            JSONObject pagination = response.getJSONObject("pagination");
                            maxNumOfPages = pagination.getInt("max_num_pages");
                            currentPage = pagination.getInt("current_page");
                            nextPage = pagination.getInt("next_page");

                            increment = pagination.getInt("increment");
                            hasNextPage = pagination.getBoolean("has_next_page");


                            if (!hasNextPage) {
                                loadMoreButton.setVisibility(View.GONE);
                                progress_bar.setVisibility(View.GONE);
                            } else {
                                loadMoreButton.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.VISIBLE);
                            }
                            if (candNotifications.size() == 0) {
                                loadMoreButton.setVisibility(View.GONE);
                                progress_bar.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                                emptyTextView.setText(response.getString("message"));
                            } else {
                                emptyLayout.setVisibility(View.GONE);
                            }
                        } else {

                            loadMoreButton.setVisibility(View.GONE);
                            progress_bar.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                            emptyTextView.setText(response.getString("message"));
                            progressBar.setVisibility(View.GONE);

                        }

                    } catch (Exception e) {
                        Nokri_ToastManager.showLongToast(getContext(), e.getMessage());
                        progressBar.setVisibility(View.GONE);

                        e.printStackTrace();
                    }
                } else {
                    Nokri_ToastManager.showLongToast(getContext(), responseObject.message());
                    progressBar.setVisibility(View.GONE);


                }

                container1.hideShimmer();
                container1.setVisibility(View.GONE);
//                skeleton.hide();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onDelete(String id, int position) {

    }

    @Override
    public void onView(String id) {
        Nokri_JobDetailFragment fragment = new Nokri_JobDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("job_id",id);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_placeholder,fragment).addToBackStack(null).commit();
    }

    @Override
    public void onDownload(String url) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==loadMoreButton.getId()){
            getCandidateNotifications(true,nextPage);
        }
    }
}
