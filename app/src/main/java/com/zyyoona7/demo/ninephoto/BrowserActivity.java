package com.zyyoona7.demo.ninephoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.piasy.biv.indicator.ProgressIndicator;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.zyyoona7.imgbrowser.BrowserPagerAdapter;
import com.zyyoona7.imgbrowser.ImageBrowserLayout;

import java.util.ArrayList;

public class BrowserActivity extends AppCompatActivity {

    private static final String TAG = "BrowserActivity";

    public static final String KEY_PHOTO_LIST = "keyPhotoList";
    public static final String KEY_PHOTO_POS_LIST = "keyPhotoPosList";
    public static final String KEY_CURRENT_POSITION = "keyCurrentPosition";

    private ImageBrowserLayout mImageBrowserLayout;
    private ArrayList<String> mPhotoList;
    private ArrayList<String> mPhotoPosList;
    private int mCurrentPosition;

    public static void start(Activity fromActy, ArrayList<String> photoList,
                             ArrayList<String> photoPosList, int currentPosition) {
        Intent starter = new Intent(fromActy, BrowserActivity.class);
        starter.putExtra(KEY_PHOTO_LIST, photoList);
        starter.putExtra(KEY_PHOTO_POS_LIST, photoPosList);
        starter.putExtra(KEY_CURRENT_POSITION, currentPosition);
        fromActy.startActivity(starter);
        fromActy.overridePendingTransition(0, 0); // No activity animation
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageBrowserLayout.initBigImageViewer(this);
        setContentView(R.layout.activity_browser);

        initExtra();

        mImageBrowserLayout = findViewById(R.id.ibl_browser);
        mImageBrowserLayout.setData(mPhotoList, mPhotoPosList, mCurrentPosition);
        mImageBrowserLayout.setProgressIndicator(new BrowserPagerAdapter.ProgressIndicatorFactory() {
            @Override
            public ProgressIndicator getProgressIndicator() {
                return new ProgressPieIndicator();
            }
        });
        mImageBrowserLayout.enterFullImage(savedInstanceState == null);

        mImageBrowserLayout.
                setOnGesturePositionUpdateListener(new ImageBrowserLayout.SimplePositionUpdateListener() {
                    @Override
                    public void onFinished() {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });

        mImageBrowserLayout.setOnItemClickListener(new BrowserPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: position=" + position);
                onBackPressed();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initExtra() {
        if (getIntent() != null) {
            mPhotoList = (ArrayList<String>) getIntent().getSerializableExtra(KEY_PHOTO_LIST);
            mPhotoPosList = (ArrayList<String>) getIntent().getSerializableExtra(KEY_PHOTO_POS_LIST);
            mCurrentPosition = getIntent().getIntExtra(KEY_CURRENT_POSITION, 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mImageBrowserLayout.exitFullImage(true) && !mImageBrowserLayout.isLeaving()) {
            super.onBackPressed();
        }
    }
}
