package com.zyyoona7.demo.ninephoto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zyyoona7.ninegrid.NineGridLayout;
import com.zyyoona7.imgbrowser.ImageBrowserLayout;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {


    private ArrayList<String> mPhotoList = new ArrayList<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        RecyclerView recyclerView = findViewById(R.id.rv_main2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Main2Adapter adapter = new Main2Adapter();
        recyclerView.setAdapter(adapter);

        adapter.setNewData(generateData());

        adapter.setPhotoItemClickListener(new Main2Adapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(NineGridLayout gridLayout, int nglPos, int rvPos, ArrayList<String> photoList) {

                BrowserActivity.start(Main2Activity.this, photoList, positionView(gridLayout), nglPos);
            }
        });
    }


    private List<ContentEntity> generateData() {
        List<ContentEntity> contentList = new ArrayList<>(1);
        generatePhotoList();
        contentList.add(new ContentEntity());
        contentList.add(new ContentEntity(mPhotoList.subList(0, 1)));
        contentList.add(new ContentEntity(mPhotoList.subList(0, 2)));
        contentList.add(new ContentEntity(mPhotoList.subList(0, 3)));
        contentList.add(new ContentEntity(mPhotoList.subList(0, 4)));
        contentList.add(new ContentEntity(mPhotoList.subList(0, 5)));
        contentList.add(new ContentEntity(mPhotoList.subList(0, 6)));
        contentList.add(new ContentEntity(mPhotoList.subList(0, 7)));
        contentList.add(new ContentEntity(mPhotoList.subList(0, 8)));
        return contentList;
    }

    private void generatePhotoList() {
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/9/99/Las_Meninas_01.jpg");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/2/2c/Rotating_earth_%28large%29.gif");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/f/f1/El_caballero_de_la_mano_en_el_pecho.jpg");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/a/aa/SmallFullColourGIF.gif");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/6/62/The_Garden_of_Earthly_Delights_by_Bosch_High_Resolution_2.jpg");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/f/fb/La_Anunciaci%C3%B3n_%28Fra_Angelico-Prado%29.jpg");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Carlos_V_en_M%C3%BChlberg%2C_by_Titian%2C_from_Prado_in_Google_Earth.jpg/3000px-Carlos_V_en_M%C3%BChlberg%2C_by_Titian%2C_from_Prado_in_Google_Earth.jpg");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/b/bb/Rembrandt_Harmensz._van_Rijn_014.jpg");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/b/bb/Rembrandt_Harmensz._van_Rijn_014.jpg");
        mPhotoList.add("https://upload.wikimedia.org/wikipedia/commons/1/16/Raffael_048.jpg");
    }

    private ArrayList<String> positionView(NineGridLayout layout) {
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
}
