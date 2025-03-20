package com.scriptsbundle.nokri.custom;

        import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
        import androidx.viewpager.widget.ViewPager;
        import android.util.AttributeSet;
        import android.view.View;

/**
 * Created by Glixen Technologies on 28/12/2017.
 */

public class CustomViewPager extends ViewPager {
    private View mCurrentView;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        if (height != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
   }


