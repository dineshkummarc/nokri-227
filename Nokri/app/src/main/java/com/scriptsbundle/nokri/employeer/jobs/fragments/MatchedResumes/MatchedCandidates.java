package com.scriptsbundle.nokri.employeer.jobs.fragments.MatchedResumes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
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

import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.employeer.jobs.fragments.NokriPublicProfileFragment;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeInterface;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumeModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.SavedResumesAdapter;
import com.scriptsbundle.nokri.employeer.jobs.fragments.SavedResumes.VerticalSpaceItemDecoration;
import com.scriptsbundle.nokri.manager.Nokri_RequestHeaderManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.manager.Nokri_ToastManager;
import com.scriptsbundle.nokri.network.Nokri_ServiceGenerator;
import com.scriptsbundle.nokri.rest.RestService;
import com.scriptsbundle.nokri.utils.Nokri_Globals;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchedCandidates extends Fragment implements SavedResumeInterface, View.OnClickListener {
    RecyclerView recyclerView;
    LinearLayout progressBar, emptyLayout;
    NestedScrollView parentLayout;
    ProgressBar progress_bar;
    SavedResumesAdapter adapter;
    TextView emptyTextView;
    ArrayList<SavedResumeModel> savedResumes = new ArrayList<>();

    private int maxNumOfPages, currentPage, nextPage, increment, currentNoOfJobs;
    private boolean hasNextPage, loading = true;
    private Button loadMoreButton;


    public MatchedCandidates() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matched_candidates, container, false);


        parentLayout = view.findViewById(R.id.parentLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progress_bar = view.findViewById(R.id.progress_bar);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        emptyTextView = view.findViewById(R.id.txt_empty);
        loadMoreButton = view.findViewById(R.id.btn_load_more);
        Nokri_Utils.setRoundButtonColor(getContext(), loadMoreButton);
        adapter = new SavedResumesAdapter(getActivity(), savedResumes, this);
        adapter.setCalledFromMatched(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.my_divider));
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            String jobId = getArguments().getString("jobId");
            getMatchedCandidates(jobId, false, 1);
        }
        loadMoreButton.setOnClickListener(this);


        return view;
    }


    public void getMatchedCandidates(String jobId, boolean calledFromLoadMore, int pageNumber) {
        if (!calledFromLoadMore) {
            progressBar.setVisibility(View.VISIBLE);
            savedResumes.clear();
        } else {
            progress_bar.setVisibility(View.VISIBLE);
        }
        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());
        Call<ResponseBody> myCall;
        JsonObject params = new JsonObject();
        params.addProperty("job_id", jobId);
        params.addProperty("page_number", pageNumber);


        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.getMatchedCandidates(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.getMatchedCandidates(params, Nokri_RequestHeaderManager.addHeaders());
        }


        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObject) {
                if (responseObject.isSuccessful()) {
                    try {
                        JSONObject response = new JSONObject(responseObject.body().string());
                        Log.v("response", responseObject.message());
                        if (response.getBoolean("success")) {
                            progressBar.setVisibility(View.GONE);
                            //    nokri_getLocationAndAddress();
                            JSONObject data = response.getJSONObject("data");
                            TextView toolbarTitleTextView = getActivity().findViewById(R.id.toolbar_title);

                            toolbarTitleTextView.setText(data.getString("page_title"));

                            JSONArray candidateList = data.getJSONArray("jobs");
                            for (int i = 0; i < candidateList.length(); i++) {
                                JSONArray jsonArray = candidateList.getJSONArray(i);
                                SavedResumeModel model = new SavedResumeModel();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    String entity = jsonArray.getJSONObject(j).getString("field_type_name");

                                    if (entity.equals("cand_id")) {
                                        model.id = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("cand_name")) {
                                        model.name = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("cand_dp")) {
                                        model.url = jsonArray.getJSONObject(j).getString("value");
                                    } else if (entity.equals("attachment_id")) {
                                        model.attachmentUrl = jsonArray.getJSONObject(j).getString("value");
                                    }
                                }

                                savedResumes.add(model);

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
                            if (savedResumes.size() == 0) {
                                loadMoreButton.setVisibility(View.GONE);
                                progress_bar.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                                emptyTextView.setText(response.getString("message"));
                            } else {
                                emptyLayout.setVisibility(View.GONE);
                            }
                        } else {
                            Nokri_ToastManager.showShortToast(getActivity(), responseObject.message());

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
        androidx.fragment.app.FragmentManager fragmentManager = getFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment publicProfileFragment = new NokriPublicProfileFragment();
        NokriPublicProfileFragment.USER_ID = id;

        fragmentTransaction.replace(getActivity().findViewById(R.id.fragment_placeholder).getId(), publicProfileFragment).addToBackStack(null).commit();

    }

    @Override
    public void onDownload(String url) {
//        if (ContextCompat.checkSelfPermission(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE);
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            if (!url.equals(""))
                new DownloadFileFromURL().execute(url);
            else
                Nokri_ToastManager.showShortToast(getActivity(), Nokri_Globals.INVALID_URL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Nokri_ToastManager.showShortToast(getActivity(), "Permission Granted");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Nokri_ToastManager.showShortToast(getActivity(), "Permission Denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == loadMoreButton.getId()) {
            loadMoreButton.setVisibility(View.GONE);
            if (hasNextPage)
                getMatchedCandidates(null, true, nextPage);
        }

    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        boolean isSuccessfullyDownloaded = true;
        String errorMessage = "";

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Nokri_ToastManager.showShortToast(getActivity(), "Download Started");

        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream

//                String fileName = .substring(url.lastIndexOf('/') + 1);
                String fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);

                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/Download/" + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                isSuccessfullyDownloaded = false;
                errorMessage = e.getLocalizedMessage();
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            if (!isSuccessfullyDownloaded) {
                Nokri_ToastManager.showShortToast(getActivity(), errorMessage);
            } else {
                Snackbar.make(parentLayout, "Downloaded", Snackbar.LENGTH_LONG)
                        .show();
            }
            isSuccessfullyDownloaded = true;
        }
    }
}
