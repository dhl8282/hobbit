package com.example.hobbit;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class PrepareCreateMissionActivity extends Activity {

    private static final String TAG = "hobbit" + PrepareCreateMissionActivity.class.getSimpleName();
    static final int REQUEST_TAKE_PHOTO = 1;
    private Uri mImageUri;
    private String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakePictureIntent();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(Environment.getExternalStorageDirectory()
                        + File.separator
                        + Constants.PICTURE_DIR
                        + Constants.HOBBIT_DIR + timeStamp + ".jpg");
        mPhotoPath = file.getAbsolutePath();
        return file;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    public Bitmap grabImage()
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap, scaledBitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            // TODO : rotate image
            // bitmap = ExifUtil.rotateBitmap(mCurrentPhotoPath, bitmap);
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 300, true);
            Log.d(TAG, "Photo is saved in " + mPhotoPath);
            return scaledBitmap;
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
        return null;
    }

    //called after camera intent finished
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==REQUEST_TAKE_PHOTO && resultCode==RESULT_OK)
        {
            // Rescan gallery to fetch newly added photo
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
            Intent intent = new Intent(this, CreateMissionActivity.class);
            Bitmap bitmap = grabImage();
            intent.putExtra("picture", bitmap);
            intent.putExtra("path", mPhotoPath);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
