package com.scriptsbundle.nokri.manager;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import 	android.text.SpannableString;
import 	android.text.Spannable;
import com.scriptsbundle.nokri.custom.CustomTypefaceSpan;

/**
 * Created by Glixen Technologies on 21/12/2017.
 */

public class Nokri_FontManager {
public void nokri_setMonesrratSemiBioldFont(TextView view, AssetManager assetManager){
    Typeface font = Typeface.createFromAsset(assetManager,"Montserrat-SemiBold.ttf");
    view.setTypeface(font);
}

    public void nokri_setOpenSenseFontTextView(TextView view, AssetManager assetManager){
        Typeface font = Typeface.createFromAsset(assetManager,"OpenSans.ttf");
        view.setTypeface(font);
    }

    public void nokri_setOpenSenseFontButton(Button view, AssetManager assetManager){
        Typeface font = Typeface.createFromAsset(assetManager,"OpenSans.ttf");
        view.setTypeface(font);
    }
    public void nokri_setOpenSenseFontEditText(EditText view, AssetManager assetManager){
        Typeface font = Typeface.createFromAsset(assetManager,"OpenSans.ttf");
        view.setTypeface(font);
    }

    public void nokri_applyFontToMenuItem(MenuItem mi, AssetManager assetManager) {
        Typeface font = Typeface.createFromAsset(assetManager, "OpenSans.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}
