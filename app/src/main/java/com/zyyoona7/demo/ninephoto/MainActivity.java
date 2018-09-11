package com.zyyoona7.demo.ninephoto;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zyyoona7.ninegrid.NineGridLayout;
import com.zyyoona7.imgbrowser.ImageBrowserLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NineGridLayout mNineGridLayout;
    private GridAdapter mAdapter;

    private AppCompatButton mPlayBtn;
    private AppCompatButton mStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNineGridLayout = findViewById(R.id.ngl_main);
        mPlayBtn = findViewById(R.id.btn_play);
        mStopBtn = findViewById(R.id.btn_stop);
        mAdapter = new GridAdapter();
        mNineGridLayout.setAdapter(mAdapter);

        final ArrayList<String> list = new ArrayList<>(1);
        final ArrayList<String> bigList = new ArrayList<>(1);
        list.add("http://n.sinaimg.cn/front/20171020/xD1H-fymzqse2441930.jpg");
//        list.add("http://www.nmplus.hk/wp-content/uploads/2016/04/ezgif.com-optimize-5.gif");
//        list.add("http://www.nmplus.hk/wp-content/uploads/2016/04/GIF_1121.gif");
        list.add("http://dasouji.com/wp-content/uploads/2014/10/%E8%B6%85%E9%95%BF%E5%9B%BE3-1.jpg");
//        list.add("http://www.nmplus.hk/wp-content/uploads/2016/03/f1d92e07.gif");
//        list.add("http://dasouji.com/wp-content/uploads/2014/10/%E8%B6%85%E9%95%BF%E5%9B%BE3-4.jpg");

        bigList.add("https://upload.wikimedia.org/wikipedia/commons/9/99/Las_Meninas_01.jpg");
        bigList.add("https://upload.wikimedia.org/wikipedia/commons/2/2c/Rotating_earth_%28large%29.gif");
        bigList.add("https://upload.wikimedia.org/wikipedia/commons/f/f1/El_caballero_de_la_mano_en_el_pecho.jpg");
        bigList.add("https://upload.wikimedia.org/wikipedia/commons/a/aa/SmallFullColourGIF.gif");
        bigList.add("https://upload.wikimedia.org/wikipedia/commons/6/62/The_Garden_of_Earthly_Delights_by_Bosch_High_Resolution_2.jpg");
        bigList.add("https://upload.wikimedia.org/wikipedia/commons/f/fb/La_Anunciaci%C3%B3n_%28Fra_Angelico-Prado%29.jpg");

        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        mAdapter.setDataList(list);
                    }
                })
                .start();

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.playGif();
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.stopGif();
            }
        });

        mNineGridLayout.setOnItemClickListener(new NineGridLayout.OnItemClickListener() {
            @Override
            public void onItemClick(NineGridLayout nineGridLayout, View childView, int position) {
//                Toast.makeText(MainActivity.this, "点击了 " + position, Toast.LENGTH_SHORT).show();

                BrowserActivity.start(MainActivity.this, bigList, packPosition(mNineGridLayout), position);

            }
        });

        AppCompatButton goBtn = findViewById(R.id.btn_go);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        final AppCompatImageView picIv1=findViewById(R.id.iv_pic_1);
        AppCompatImageView picIv2=findViewById(R.id.iv_pic_2);
        GlideApp.with(picIv1)
                .setDefaultRequestOptions(new RequestOptions()
                        .dontAnimate())
                .load(list.get(0))
                .into(new ImageViewTarget<Drawable>(picIv1) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        picIv1.setImageDrawable(resource);
                    }
                });
        GlideApp.with(picIv2)
                .setDefaultRequestOptions(new RequestOptions()
                        .dontAnimate())
                .load(list.get(1))
                .into(picIv2);
    }

    private ArrayList<String> packPosition(NineGridLayout layout) {
        int childCount = layout.getChildCount();
        ArrayList<String> photoPosList = new ArrayList<>(1);
        for (int i = 0; i < childCount; i++) {
            View childView = layout.getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            photoPosList.add(ImageBrowserLayout.positionView(childView));
        }
        return photoPosList;
    }

    @Override
    protected void onDestroy() {
        mAdapter.clearGif();
        super.onDestroy();
    }
}
