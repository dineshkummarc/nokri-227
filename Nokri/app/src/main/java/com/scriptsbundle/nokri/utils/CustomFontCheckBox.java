package com.scriptsbundle.nokri.utils;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.AttributeSet;

public class CustomFontCheckBox extends AppCompatCheckBox {

    Context context;
    public CustomFontCheckBox(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomFontCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFontCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        //set your typeface here.
        setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans.ttf"));
    }
}