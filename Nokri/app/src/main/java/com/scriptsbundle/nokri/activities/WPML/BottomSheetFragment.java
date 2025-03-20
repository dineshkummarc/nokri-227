package com.scriptsbundle.nokri.activities.WPML;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    BottomSheetFragment bottomSheet;
    ListView listView;
    WPMLAdapter adapter;
    WPMLInterface wpmlInterface;
    public BottomSheetFragment(WPMLInterface wpmlInterface) {
        this.wpmlInterface = wpmlInterface;
    }

    public void setBottomSheetInstance(BottomSheetFragment bottomSheet){
        this.bottomSheet = bottomSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.language_bottom_sheet, container, false);
        Nokri_SharedPrefManager manager = new Nokri_SharedPrefManager();
        WPMLModel model = manager.getWPMLSettings(getActivity());
        listView = view.findViewById(R.id.listView);
        adapter = new WPMLAdapter(getActivity(),model.langArray);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wpmlInterface.onLanguageSelected(model.langArray.get(position));
                bottomSheet.dismiss();

            }
        });
        return view;
    }
}