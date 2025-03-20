package com.scriptsbundle.nokri.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;

import com.scriptsbundle.nokri.R;
import com.scriptsbundle.nokri.manager.Nokri_DialogManager;
import com.scriptsbundle.nokri.manager.Nokri_GoogleAnalyticsManager;
import com.scriptsbundle.nokri.manager.Nokri_SharedPrefManager;
import com.scriptsbundle.nokri.utils.Nokri_LanguageSupport;
import com.scriptsbundle.nokri.utils.Nokri_Utils;


public class Nokri_ImagePreview extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";
    private Nokri_DialogManager dialogManager ;
    private ArrayList<String> _images;
    private GalleryPagerAdapter _adapter;


    ViewPager _pager;

    ImageButton _closeButton;
    LinearLayout _thumbnails;

    @Override
    protected void onResume() {
        super.onResume();
        Nokri_GoogleAnalyticsManager.getInstance().trackScreenView(getClass().getSimpleName());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nokri_image_preview);
        dialogManager = new Nokri_DialogManager();
        dialogManager.showAlertDialog(this);
        Nokri_LanguageSupport.setLocale(this, Nokri_SharedPrefManager.getLocal(this));
        _thumbnails = findViewById(R.id.thumbnails);
        _pager = findViewById(R.id.pager);
        _closeButton = findViewById(R.id.btn_close);
        _images = new ArrayList<>();

        _images = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_NAME);


        _adapter = new GalleryPagerAdapter(this);

        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(6);
       // _pager.setOnPageChangeListener(this);

        _closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Close clicked");
                finish();
            }
        });


        Nokri_Utils.changeSystemBarColor(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

    @Override
    public void onPageSelected(int position) {

        if(_thumbnails!=null && _thumbnails.getChildCount()>0) {

            for(int i = 0;i<_thumbnails.getChildCount();i++){
                if(i == position) {
                    //_thumbnails.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.app_blue));
                    _thumbnails.getChildAt(position).setBackgroundResource(R.drawable.item_highlight);
                 //   _thumbnails.getChildAt(i).setBackgroundColor(0x00000000);
                 //   _thumbnails.getChildAt(position).setPadding(0, 25, 0, 25);
                    continue;
                }
            else
                    //_thumbnails.getChildAt(i).setBackgroundColor(0x00000000);
                    _thumbnails.getChildAt(i).setBackgroundResource(0x00000000);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class GalleryPagerAdapter extends PagerAdapter {

        Context _context;
        LayoutInflater _inflater;

        public GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return _images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

/*
            int borderSize = _thumbnails.getPaddingTop();


            final int thumbnailSize = ((FrameLayout.LayoutParams)
                    _pager.getLayoutParams()).bottomMargin - (borderSize*2);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);


            final ImageView thumbView = new ImageView(_context);
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbView.setLayoutParams(params);


            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Thumbnail clicked");

                    // Set the pager position when thumbnail clicked
                    _pager.setCurrentItem(position);
                }
            });

            _thumbnails.addView(thumbView);*/
            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

            // Asynchronously load the image and set the thumbnail and pager view
            Glide.with(_context)

                    .asBitmap().load(_images.get(position))
                    .into(new SimpleTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImage(ImageSource.bitmap(resource));
                            dialogManager.hideAfterDelay();
                        }
                    });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

   /* public static Bitmap padBitmap(Bitmap bitmap)
    {
        int paddingX;
        int paddingY;

        if (bitmap.getWidth() == bitmap.getHeight())
        {
            paddingX = 0;
            paddingY = 0;
        }
        else if (bitmap.getWidth() > bitmap.getHeight())
        {
            paddingX = 0;
            paddingY = bitmap.getWidth() - bitmap.getHeight();
        }
        else
        {
            paddingX = bitmap.getHeight() - bitmap.getWidth();
            paddingY = 0;
        }

        Bitmap paddedBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + paddingX,
                bitmap.getHeight() + paddingY,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF); // this represents white color
        canvas.drawBitmap(
                bitmap,
                paddingX / 2,
                paddingY / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));

        return paddedBitmap;
    }
*/
}
