package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.hobbit.util.Constants;

public class ZoomImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        ImageView missionImageZoom = (ImageView) findViewById(R.id.imageViewZoom);
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra(Constants.INTENT_EXTRA_PHOTO_BITMAP);
        missionImageZoom.setImageBitmap(bitmap);
    }
}
