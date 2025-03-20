package com.scriptsbundle.nokri.employeer.payment.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.scriptsbundle.nokri.employeer.payment.adapters.Nokri_PricingAdapter;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.custom.Nokri_SpinnerAdapter;
import com.scriptsbundle.nokri.employeer.jobs.models.Nokri_SpinnerModel;
import com.scriptsbundle.nokri.employeer.payment.models.Nokri_PricingModel;
import com.scriptsbundle.nokri.manager.Nokri_FontManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.utils.Nokri_Config;
import com.scriptsbundle.nokri.utils.Nokri_Utils;

import java.util.ArrayList;

public class Nokri_PackagesFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView dollarTextView, amountTextView;
    //  private Button selectPlanButton;
    private Nokri_FontManager fontManager;
    private Nokri_PricingModel model;
    private Nokri_PricingAdapter adapter;
    private RecyclerView recyclerView;

    private OnSelectPlanClickListener listener;
    private Spinner paymentSpinner;
    private ArrayList<Nokri_SpinnerModel> spinnerModel;
    private ArrayList<String> spinnerIds = new ArrayList<>();
    private ArrayList<String> spinnerNames = new ArrayList<>();
    private TextView headingTextView;
    private RelativeLayout outerCircle;
    private Button paymentButton;

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onButtonClick(model);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null)
            listener.onItemClick(spinnerIds.get(position), spinnerNames.get(position), position, model);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnSelectPlanClickListener {
        void onButtonClick(Nokri_PricingModel model);

        void onItemClick(String id, String name, int position, Nokri_PricingModel model);
    }

    public void nokri_setOnSetPlanClickListener(OnSelectPlanClickListener listener) {
        this.listener = listener;
    }

    public Nokri_PackagesFragment() {
        // Required empty public constructor
    }

    public void nokri_setPricingModel(Nokri_PricingModel model) {
        this.model = model;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nokri_initialize(view);
        nokri_setFonts();
    }

    public void nokri_setSpinnerModel(ArrayList<Nokri_SpinnerModel> spinnerModel) {
        this.spinnerModel = spinnerModel;
    }

    private void nokri_setSpinnerAdapter() {

        for (int i = 0; i < spinnerModel.size(); i++) {
            spinnerNames.add(spinnerModel.get(i).getName());
            spinnerIds.add(spinnerModel.get(i).getId());

        }

        Nokri_SpinnerAdapter spinnerAdapter = new Nokri_SpinnerAdapter(getContext(), R.layout.spinner_item_popup, spinnerNames, true);
        spinnerAdapter.setTextColorWhire(true);

        paymentSpinner.setAdapter(spinnerAdapter);


        paymentSpinner.setOnItemSelectedListener(this);
    }

    private void nokri_setupRecyclerview() {


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new Nokri_PricingAdapter(model.getPreniumJobs(), getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nokri_packages, container, false);
    }

    private void nokri_setFonts() {
        fontManager.nokri_setOpenSenseFontTextView(dollarTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontTextView(amountTextView, getActivity().getAssets());
        fontManager.nokri_setOpenSenseFontButton(paymentButton, getActivity().getAssets());
    }

    private void nokri_initialize(View view) {
        fontManager = new Nokri_FontManager();
        getView().findViewById(R.id.heading_container).setBackgroundColor(Color.parseColor(Nokri_Config.APP_COLOR));
        Nokri_Utils.setXmlDrawableSingleLayer(getContext(), getView().findViewById(R.id.inner_circle), R.drawable.circle);
        Nokri_Utils.setRoundButtonColor(getContext(), getView().findViewById(R.id.rounded_button_container));
        dollarTextView = view.findViewById(R.id.txt_dollar);
        amountTextView = view.findViewById(R.id.txt_amount);

        outerCircle = view.findViewById(R.id.outer_circle);
        outerCircle.setBackground(Nokri_Utils.getColoredXml(getContext(), R.drawable.circle));
        recyclerView = view.findViewById(R.id.recyclerview);


        paymentSpinner = view.findViewById(R.id.spinner_payment);
        paymentSpinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        paymentButton = view.findViewById(R.id.btn_payment);
        Nokri_Utils.setRoundButtonColor(getContext(), paymentButton);
        if (model != null) {
            if (model.getButtonVisible()) {
                getView().findViewById(R.id.rounded_button_container).setVisibility(View.GONE);
                paymentButton.setVisibility(View.VISIBLE);
                paymentButton.setText(model.getButtonText());
                paymentButton.setOnClickListener(this);

            }

            headingTextView = view.findViewById(R.id.txt_heading);
            fontManager.nokri_setMonesrratSemiBioldFont(headingTextView, getContext().getAssets());
            headingTextView.setText(model.getProductTitle());
            dollarTextView.setText(model.getCurrency());
            amountTextView.setText(model.getProductPrice());
            nokri_setupRecyclerview();
            nokri_setSpinnerAdapter();
        }


    }

}
