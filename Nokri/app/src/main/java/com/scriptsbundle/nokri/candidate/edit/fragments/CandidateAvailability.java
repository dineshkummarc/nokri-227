package com.scriptsbundle.nokri.candidate.edit.fragments;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.candidate.edit.ViewModel.CandidateAvailabilityViewModel;
import com.scriptsbundle.nokri.candidate.edit.models.CandidateAvailabilityList;
import com.scriptsbundle.nokri.candidate.edit.models.TimeModel;
import com.scriptsbundle.nokri.employeer.jobs.fragments.PostJobSpinnerListActivity;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

@SuppressLint("NewApi")
public class CandidateAvailability extends Fragment implements CompoundButton.OnCheckedChangeListener {
    ArrayList<TimeModel> startArray = new ArrayList<>();
    ArrayList<TimeModel> endArray = new ArrayList<>();
    ArrayList<String> timeZones = new ArrayList<>();
    RadioGroup radioGroupDays;    CheckBox m, t, w, th, f, s, su;
    EditText startTime, endTime;
    JsonArray sendingArray = new JsonArray();
    Button submit;
    Nokri_FontManager fontManager;
    int selectedRadioButtonIndex = 0;
    LinearLayout mainLayout,noData,timeLayout, dayAndTimeLayout;
    RadioButton radioOff, radioOn, radioSelective;
    ImageView monPointer, tuePointer, wedPointer, thuPointer, friPointer, satPointer, sunPointer;
    ArrayAdapter timeZonesAdapter;
    TextView categoryValue, categoryTitle,selectedHoursLabel,toLabel;
    LinearLayout zonesLayout;
    String selectedZone;

    public CandidateAvailability() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_candidate_availability, container, false);

        fontManager = new Nokri_FontManager();
        timeLayout = view.findViewById(R.id.timeLayout);
        noData = view.findViewById(R.id.noData);
        mainLayout = view.findViewById(R.id.mainLayout);
        dayAndTimeLayout = view.findViewById(R.id.dayAndTimeLayout);
        dayAndTimeLayout.setVisibility(View.GONE);
        timeLayout.setVisibility(View.GONE);
        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);
        submit = view.findViewById(R.id.submit);
        fontManager.nokri_setOpenSenseFontButton(submit, getActivity().getAssets());
        Nokri_Utils.setEditBorderButton(getContext(), submit);
        radioGroupDays = view.findViewById(R.id.radioGroupDays);
        categoryTitle = view.findViewById(R.id.category_title);
        categoryValue = view.findViewById(R.id.category_value);
        zonesLayout = view.findViewById(R.id.country_spinner_layout);
        selectedHoursLabel = view.findViewById(R.id.selectedHoursLabel);
        toLabel = view.findViewById(R.id.toLabel);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject jsonObject = new JsonObject();
                if (radioOn.isChecked()) {
                    jsonObject.addProperty("hour_type", "0");
                } else if (radioOff.isChecked()) {
                    jsonObject.addProperty("hour_type", "1");
                } else {
                    jsonObject.addProperty("hour_type", "2");
                }

                jsonObject.addProperty("zones",selectedZone);
                JsonArray jsonArray = new JsonArray();
                for (int i = 0; i < 7; i++) {
                    CheckBox checkBox = (CheckBox) radioGroupDays.getChildAt(i);
                    JsonObject jsonObject1 = new JsonObject();
                    jsonObject1.addProperty("day_name", startArray.get(i).day);
                    jsonObject1.addProperty("start_time", startArray.get(i).time);
                    jsonObject1.addProperty("end_time", endArray.get(i).time);
                    if (checkBox.isChecked()) {
                        jsonObject1.addProperty("closed", true);
                    } else {
                        jsonObject1.addProperty("closed", false);
                    }

                    jsonArray.add(jsonObject1);
                }

                jsonObject.add("days", jsonArray);
                postData(jsonObject);
                Log.d("Array", jsonObject.toString());
            }
        });

        monPointer = view.findViewById(R.id.monPointer);
        tuePointer = view.findViewById(R.id.tuePointer);
        wedPointer = view.findViewById(R.id.wedPointer);
        thuPointer = view.findViewById(R.id.thuPointer);
        friPointer = view.findViewById(R.id.friPointer);
        satPointer = view.findViewById(R.id.satPointer);
        sunPointer = view.findViewById(R.id.sunPointer);

        setPointerTint();

        monPointer.setVisibility(View.INVISIBLE);
        tuePointer.setVisibility(View.INVISIBLE);
        wedPointer.setVisibility(View.INVISIBLE);
        thuPointer.setVisibility(View.INVISIBLE);
        friPointer.setVisibility(View.INVISIBLE);
        satPointer.setVisibility(View.INVISIBLE);
        sunPointer.setVisibility(View.INVISIBLE);


        m = view.findViewById(R.id.m);
        t = view.findViewById(R.id.t);
        w = view.findViewById(R.id.w);
        th = view.findViewById(R.id.th);
        f = view.findViewById(R.id.f);
        s = view.findViewById(R.id.s);
        su = view.findViewById(R.id.su);

        setCheckBoxTint(m);
        setCheckBoxTint(t);
        setCheckBoxTint(w);
        setCheckBoxTint(th);
        setCheckBoxTint(f);
        setCheckBoxTint(s);
        setCheckBoxTint(su);

        m.setOnCheckedChangeListener(this);
        t.setOnCheckedChangeListener(this);
        w.setOnCheckedChangeListener(this);
        th.setOnCheckedChangeListener(this);
        f.setOnCheckedChangeListener(this);
        s.setOnCheckedChangeListener(this);
        su.setOnCheckedChangeListener(this);


        radioOn = view.findViewById(R.id.radioOn);
        radioOff = view.findViewById(R.id.radioOff);
        radioSelective = view.findViewById(R.id.radioSelective);

        setRadioTint(radioOn);
        setRadioTint(radioOff);
        setRadioTint(radioSelective);


        radioOn.setOnCheckedChangeListener(this);
        radioOff.setOnCheckedChangeListener(this);
        radioSelective.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    dayAndTimeLayout.setVisibility(View.VISIBLE);

                    GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) compoundButton.getBackground()).getStateDrawable(0));
                    drawable.setColor(Color.parseColor(Nokri_Config.APP_COLOR));
                    compoundButton.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    dayAndTimeLayout.setVisibility(View.GONE);
                    GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) compoundButton.getBackground()).getStateDrawable(1));
                    drawable.setStroke(2, Color.parseColor(Nokri_Config.APP_COLOR));
                    compoundButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
//            compoundButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        });


//        startArray.add(new TimeModel("09:00","M"));
//        startArray.add(new TimeModel("08:00","T"));
//        startArray.add(new TimeModel("09:00","W"));
//        startArray.add(new TimeModel("10:00","Th"));
//        startArray.add(new TimeModel("11:00","F"));
//        startArray.add(new TimeModel("2:00","S"));
//        startArray.add(new TimeModel("09:00","Su"));
//
//
//
//        endArray.add(new TimeModel("10:00","M"));
//        endArray.add(new TimeModel("11:00","T"));
//        endArray.add(new TimeModel("2:00","W"));
//        endArray.add(new TimeModel("6:00","Th"));
//        endArray.add(new TimeModel("5:00","F"));
//        endArray.add(new TimeModel("4:00","S"));
//        endArray.add(new TimeModel("9:00","Su"));


        nokri_setFonts();

        startTime.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setTime(startTime, true);

            }
        });


        endTime.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setTime(endTime, false);

            }
        });


        getData();

        return view;
    }

    public void getData() {
        CandidateAvailabilityViewModel viewModel = ViewModelProviders.of(getActivity()).get(CandidateAvailabilityViewModel.class);
        viewModel.getCandidateAvailability().observe(getActivity(), new Observer<CandidateAvailabilityList>() {
            @Override
            public void onChanged(CandidateAvailabilityList candidateAvailabilityList) {
                if (candidateAvailabilityList != null) {
                    mainLayout.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                    List<CandidateAvailabilityList.Zone> zones = candidateAvailabilityList.getData().getZones();
                    timeZones.clear();
                    for (int i = 0; i < zones.size(); i++) {
                        timeZones.add(zones.get(i).getValue());
                        if (zones.get(i).isSelected()) {
                            selectedZone = zones.get(i).getValue();
                            categoryValue.setText(selectedZone);
                        }
                        if (selectedZone==null){
                            selectedZone = zones.get(0).getValue();
                        }
                    }
                    zonesLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makeSpinnerList(candidateAvailabilityList.getData().getZones());
                            String jsonList;
                            jsonList = new Gson().toJson(categorySpinnerList);
                            Intent intent = new Intent(getActivity(), PostJobSpinnerListActivity.class);
                            intent.putExtra("list", jsonList);
                            intent.putExtra("calledFrom", "zone");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, 321);
                        }
                    });
                    startArray.clear();
                    endArray.clear();
                    List<CandidateAvailabilityList.Day> days = candidateAvailabilityList.getData().getDays();
                    for (int i = 0; i < days.size(); i++) {
                        startArray.add(new TimeModel(days.get(i).start_time, days.get(i).day_name));
                        endArray.add(new TimeModel(days.get(i).end_time, days.get(i).day_name));
                        if (!days.get(i).getClosed()){
                            if (i==0)
                                m.setChecked(true);
                            if (i==1)
                                t.setChecked(true);
                            if (i==2)
                                w.setChecked(true);
                            if (i==3)
                                th.setChecked(true);
                            if (i==4)
                                f.setChecked(true);
                            if (i==5)
                                s.setChecked(true);
                            if (i==6)
                                su.setChecked(true);

                        }
                    }
                    CandidateAvailabilityList.Extra extra = candidateAvailabilityList.getData().getExtra();
                    radioOn.setText(extra.getOpen());
                    radioOff.setText(extra.getNot_available());
                    radioSelective.setText(extra.getSelective_hours());
                    categoryTitle.setText(extra.getTime_zone());
                    submit.setText(extra.getSubmit());
                    selectedHoursLabel.setText(extra.getSelected_hours());
                    toLabel.setText(extra.getTo());
                    String index = candidateAvailabilityList.getData().getHoursType();
                    if (index.equals("0")) {
                        radioOn.setChecked(true);
                    } else if (index.equals("1")) {
                        radioOff.setChecked(true);
                    } else
                        radioSelective.setChecked(true);

                }else{
                    mainLayout.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            if (resultCode == RESULT_OK) {
                Nokri_SpinnerModel model = (Nokri_SpinnerModel) data.getSerializableExtra("some_key");
                selectedZone = model.getId();
                categoryValue.setText(selectedZone);
            }
        }
    }

    ArrayList<Nokri_SpinnerModel> categorySpinnerList = new ArrayList<>();

    private void makeSpinnerList(List<CandidateAvailabilityList.Zone> zones) {
        int index = 0;
        categorySpinnerList.clear();
        for (int i = 0; i < zones.size(); i++) {
            Nokri_SpinnerModel model = new Nokri_SpinnerModel();
            CandidateAvailabilityList.Zone zone = zones.get(i);
            model.setName(zone.getValue());
            model.setId(zone.getKey());
            try {

                model.setHasChild(zone.isHas_child());
            } catch (Exception e) {
                e.printStackTrace();
            }
            categorySpinnerList.add(model);

        }
    }

    private void setPointerTint() {

        monPointer.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
        tuePointer.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
        wedPointer.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
        thuPointer.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
        friPointer.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
        satPointer.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
        sunPointer.setColorFilter(Color.parseColor(Nokri_Config.APP_COLOR), PorterDuff.Mode.SRC_IN);
    }


    private void nokri_setFonts() {
        fontManager.nokri_setOpenSenseFontTextView(startTime, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(endTime, getActivity().getAssets());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (b) {
//            if (compoundButton.getId() == radioSelective.getId())
//                dayAndTimeLayout.setVisibility(View.VISIBLE);
//            else if(compoundButton.getId() == radioSelective.getId()||compoundButton.getId() == radioSelective.getId())
//                dayAndTimeLayout.setVisibility(View.GONE);


            GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) compoundButton.getBackground()).getStateDrawable(0));
            drawable.setColor(Color.parseColor(Nokri_Config.APP_COLOR));
            compoundButton.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
//            if (compoundButton.getId() == radioSelective.getId())
//                dayAndTimeLayout.setVisibility(View.GONE);
            GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) compoundButton.getBackground()).getStateDrawable(1));
            drawable.setStroke(2, Color.parseColor(Nokri_Config.APP_COLOR));
            compoundButton.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
//            compoundButton.setTextColor(Color.parseColor("#FFFFFF"));
        }
        if (compoundButton.getId() == radioSelective.getId()) {
            if (b) {
                dayAndTimeLayout.setVisibility(View.VISIBLE);
            } else {
                dayAndTimeLayout.setVisibility(View.GONE);
            }
        }
        if (compoundButton.getId() == m.getId()) {
            startTime.setText(startArray.get(0).time);
            endTime.setText(endArray.get(0).time);
            selectedRadioButtonIndex = 0;


            setPointers(true, false, false, false, false, false, false);

        }
        if (compoundButton.getId() == t.getId()) {
            startTime.setText(startArray.get(1).time);
            endTime.setText(endArray.get(1).time);
            selectedRadioButtonIndex = 1;
            setPointers(false, true, false, false, false, false, false);

        }
        if (compoundButton.getId() == w.getId()) {
            startTime.setText(startArray.get(2).time);
            endTime.setText(endArray.get(2).time);
            selectedRadioButtonIndex = 2;
            setPointers(false, false, true, false, false, false, false);
        }
        if (compoundButton.getId() == th.getId()) {
            startTime.setText(startArray.get(3).time);
            endTime.setText(endArray.get(3).time);
            selectedRadioButtonIndex = 3;
            setPointers(false, false, false, true, false, false, false);

        }
        if (compoundButton.getId() == f.getId()) {
            startTime.setText(startArray.get(4).time);
            endTime.setText(endArray.get(4).time);
            selectedRadioButtonIndex = 4;
            setPointers(false, false, false, false, true, false, false);

        }
        if (compoundButton.getId() == s.getId()) {
            startTime.setText(startArray.get(5).time);
            endTime.setText(endArray.get(5).time);
            selectedRadioButtonIndex = 5;
            setPointers(false, false, false, false, false, true, false);

        }
        if (compoundButton.getId() == su.getId()) {
            startTime.setText(startArray.get(6).time);
            endTime.setText(endArray.get(6).time);
            selectedRadioButtonIndex = 6;
            setPointers(false, false, false, false, false, false, true);
        }
    }

    public void setTime(EditText editText, boolean isStartSelected) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                datetime.set(Calendar.MINUTE, minute);
                String am_pm = null;
                if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "AM";
                else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                    am_pm = "PM";

                String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                String time = strHrsToShow + ":" + selectedMinute + " " + am_pm;
                editText.setText(time);
                if (isStartSelected) {
                    startArray.get(selectedRadioButtonIndex).time = time;
                } else {
                    endArray.get(selectedRadioButtonIndex).time = time;
                }
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void setPointers(boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        timeLayout.setVisibility(View.VISIBLE);
        if (mon) {
            monPointer.setVisibility(View.VISIBLE);
        } else {
            monPointer.setVisibility(View.INVISIBLE);
        }
        if (tue) {
            tuePointer.setVisibility(View.VISIBLE);
        } else {
            tuePointer.setVisibility(View.INVISIBLE);
        }
        if (wed) {
            wedPointer.setVisibility(View.VISIBLE);
        } else {
            wedPointer.setVisibility(View.INVISIBLE);
        }
        if (thu) {
            thuPointer.setVisibility(View.VISIBLE);
        } else {
            thuPointer.setVisibility(View.INVISIBLE);
        }
        if (fri) {
            friPointer.setVisibility(View.VISIBLE);
        } else {
            friPointer.setVisibility(View.INVISIBLE);
        }
        if (sat) {
            satPointer.setVisibility(View.VISIBLE);
        } else {
            satPointer.setVisibility(View.INVISIBLE);
        }
        if (sun) {
            sunPointer.setVisibility(View.VISIBLE);
        } else {
            sunPointer.setVisibility(View.INVISIBLE);
        }
    }

    public void setRadioTint(RadioButton radio) {
        if (radio.isChecked()) {

            timeLayout.setVisibility(View.VISIBLE);

            GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) radio.getBackground()).getStateDrawable(1));
            drawable.setColor(Color.parseColor(Nokri_Config.APP_COLOR));
            radio.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) radio.getBackground()).getStateDrawable(1));
            drawable.setStroke(2, Color.parseColor(Nokri_Config.APP_COLOR));
            radio.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
//            compoundButton.setTextColor(Color.parseColor("#FFFFFF"));
        }

    }

    public void setCheckBoxTint(CheckBox checkBox) {
        if (checkBox.isChecked()) {

            timeLayout.setVisibility(View.VISIBLE);

            GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) checkBox.getBackground()).getStateDrawable(1));
            drawable.setColor(Color.parseColor(Nokri_Config.APP_COLOR));
            checkBox.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            GradientDrawable drawable = ((GradientDrawable) ((StateListDrawable) checkBox.getBackground()).getStateDrawable(1));
            drawable.setStroke(2, Color.parseColor(Nokri_Config.APP_COLOR));
            checkBox.setTextColor(Color.parseColor(Nokri_Config.APP_COLOR));
//            compoundButton.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }
    Nokri_DialogManager dialogManager;
    public void postData(JsonObject params){
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(getActivity());


        RestService restService = Nokri_ServiceGenerator.createService(RestService.class, Nokri_SharedPrefManager.getEmail(getContext()), Nokri_SharedPrefManager.getPassword(getContext()), getContext());

        Call<ResponseBody> myCall;
        if (Nokri_SharedPrefManager.isSocialLogin(getContext())) {
            myCall = restService.postScheduledHours(params, Nokri_RequestHeaderManager.addSocialHeaders());
        } else {
            myCall = restService.postScheduledHours(params, Nokri_RequestHeaderManager.addHeaders());
        }


        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    dialogManager.hideAlertDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success")){
                            Nokri_ToastManager.showShortToast(getActivity(),jsonObject.getString("message"));
                        }else{
                            Nokri_ToastManager.showShortToast(getActivity(), Nokri_Globals.SOMETHING_WENT_WRONG);
                        }
                    } catch (JSONException e) {
                        Nokri_ToastManager.showShortToast(getActivity(), Nokri_Globals.SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                    } catch (IOException e) {
                        Nokri_ToastManager.showShortToast(getActivity(), Nokri_Globals.SOMETHING_WENT_WRONG);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogManager.hideAlertDialog();
                Nokri_ToastManager.showShortToast(getActivity(), Nokri_Globals.SOMETHING_WENT_WRONG);
            }
        });




    }
}