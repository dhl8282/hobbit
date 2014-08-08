package com.example.pics;

import java.io.InputStream;

import com.example.hobbit.R;
import com.example.pics.util.Mission;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MissionView extends RelativeLayout {
    private TextView mTitleTextView, mDescriptionTextView;
    private ImageView mImageView;

    public static MissionView inflate(ViewGroup parent) {
        MissionView missionView = (MissionView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mission_view, parent, false);
        return missionView;
    }

    public MissionView(Context c) {
        this(c, null);
    }

    public MissionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.mission_view_children, this, true);
        setupChildren();
    }

    private void setupChildren() {
        mTitleTextView = (TextView) findViewById(R.id.textview_cell_main_mission_title);
        mDescriptionTextView = (TextView) findViewById(R.id.textview_cell_main_mission_user_name);
        mImageView = (ImageView) findViewById(R.id.imageview_cell_main_mission_thumbnail);
    }

    public void setMission(Mission mission) {
        mTitleTextView.setText(mission.getTitle());
        mDescriptionTextView.setText(mission.getHint());
        // TODO: set up image URL
        showPhotoFromUrl(mImageView, mission.getPhotoUrl());
    }
    
    public ImageView getImageView () {
        return mImageView;
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getDescriptionTextView() {
        return mDescriptionTextView;
    }
    
    private void showPhotoFromUrl(ImageView imageView, String url) {
        new DownloadImageTask(imageView).execute(url);
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(result, 200, 300, true);
                bmImage.setImageBitmap(scaledBitmap);
            }
        }
    }
}
