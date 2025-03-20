package com.scriptsbundle.nokri.candidate.profile.fragments;


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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.profile.adapter.Nokri_RecentJobsAdapter;
import com.scriptsbundle.nokri.candidate.profile.model.Nokri_RecentJobsModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nokri_RecentJobsFragment extends Fragment implements View.OnClickListener{


    private Nokri_RecentJobsAdapter adapter;
    private List<Nokri_RecentJobsModel> modelList;

    private RecyclerView recyclerView;


    private TextView emptyTextView;
    private ImageView messageImage;
    private LinearLayout messageContainer;
    private int nextPage=1;
    private boolean hasNextPage,loading = true;
    Nokri_CompanyPublicProfileFragment companyPublicProfileFragment;
    private Button loadMoreButton;

    private ProgressBar progressBar;
    private nokri_pagerCallback pagerCallback;
    private boolean mUserSeen = false;
    private boolean mViewCreated = false;

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }

    public Nokri_RecentJobsFragment() {
        // Required empty public constructor

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!mUserSeen && isVisibleToUser) {
            mUserSeen = true;
            onUserFirstSight();
            tryViewCreatedFirstSight();
        }
        onUserVisibleChanged(isVisibleToUser);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Override this if you want to get savedInstanceState.
        mViewCreated = true;
        tryViewCreatedFirstSight();
    }
    private void tryViewCreatedFirstSight() {
        if (mUserSeen && mViewCreated) {
            onViewCreatedFirstSight(getView());
        }
    }


    protected void onViewCreatedFirstSight(View view) {

    }

    protected void onUserFirstSight() {

    }

    protected void onUserVisibleChanged(boolean visible) {
        if(getContext()!=null)
        {
            if(visible){

                if(companyPublicProfileFragment != null) {
                    companyPublicProfileFragment.setNextPage(1);
                    nextPage = 1;
         //       companyPublicProfileFragment.nokri_loadMore(true);
                  //loadDatas();

                }


            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        emptyTextView = getView().findViewById(R.id.txt_empty);
        new Nokri_FontManager().nokri_setOpenSenseFontTextView(emptyTextView,getActivity().getAssets());
        messageImage = getView().findViewById(R.id.img_message);
        messageContainer = getView().findViewById(R.id.msg_container);
        loadMoreButton = getView().findViewById(R.id.btn_load_more);
        new Nokri_FontManager().nokri_setOpenSenseFontButton(loadMoreButton,getActivity().getAssets());
//        Picasso.get()(getContext()).load(R.drawable.logo).into(messageImage);
        companyPublicProfileFragment = (Nokri_CompanyPublicProfileFragment) getParentFragment();



        progressBar = getView().findViewById(R.id.progress_bar);


        loadMoreButton.setOnClickListener(this);

        recyclerView  = getView().findViewById(R.id.recyclerview);


      //  nokri_loadMore(false);
doStuff();
    }
    public interface nokri_pagerCallback {


        void onItemClick(Nokri_RecentJobsModel model);
        void onLoadMoreClick();
    }

    public void nokri_setPagerCallback(nokri_pagerCallback pagerCallback){
        this.pagerCallback = pagerCallback;
    }



    public void setData(boolean hasNextPage, ArrayList<Nokri_RecentJobsModel> jobsList)
    {
        this.hasNextPage = hasNextPage;
        this.modelList = jobsList;


        if(loadMoreButton!=null) {
            if (hasNextPage) {
                loadMoreButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            if (!hasNextPage) {
                loadMoreButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }


   private void doStuff(){
       emptyTextView.setText("");

       if(modelList.size() == 0){
           messageContainer.setVisibility(View.VISIBLE);
           emptyTextView.setText(companyPublicProfileFragment.getMessage());
           progressBar.setVisibility(View.GONE);
           loadMoreButton.setVisibility(View.GONE);


           return;
       }
       else
           messageContainer.setVisibility(View.GONE);


       setupAdapter();



       if(hasNextPage)
           loadMoreButton.setVisibility(View.VISIBLE);
       if(!hasNextPage){
           loadMoreButton.setVisibility(View.GONE);
           progressBar.setVisibility(View.GONE);
       }
   }

   /* private void loadDatas(){
        Log.d("Insied ","load datas");
        hasNextPage = companyPublicProfileFragment.hasNextPage();
        modelList = companyPublicProfileFragment.getJobsList();
        emptyTextView.setText("");
        Nokri_ToastManager.showShortToast(getContext(),modelList.size()+"");
        if(modelList.size() == 0){
            messageContainer.setVisibility(View.VISIBLE);
            emptyTextView.setText(companyPublicProfileFragment.getMessage());
            progressBar.setVisibility(View.GONE);
            loadMoreButton.setVisibility(View.GONE);
            setupAdapter();

            return;
        }
        else
            messageContainer.setVisibility(View.GONE);


        setupAdapter();



        if(hasNextPage)
            loadMoreButton.setVisibility(View.VISIBLE);
        if(!hasNextPage){
            loadMoreButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }*/


    public void setupAdapter() {
        recyclerView = getView().findViewById(R.id.recyclerview);

        adapter = new Nokri_RecentJobsAdapter(modelList, getActivity().getBaseContext(), new Nokri_RecentJobsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Nokri_RecentJobsModel item) {
                pagerCallback.onItemClick(item);
            }
        });

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();


        //   Nokri_DialogManager.hideAfterDelay();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_recent_jobs, container, false);
    }

    @Override
    public void onClick(View view) {
        if(hasNextPage){
            loadMoreButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            //companyPublicProfileFragment.nokri_loadMore(false);


            //loadData();
        }
        if(pagerCallback!=null)
            pagerCallback.onLoadMoreClick();
    }


    public void loadData(){
     /*if(companyPublicProfileFragment!=null)
      loadDatas();*/

    }
}
