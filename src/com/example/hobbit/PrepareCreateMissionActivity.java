package com.example.hobbit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.hobbit.util.Constants;
import com.example.hobbit.util.ExifUtil;
import com.example.hobbit.util.ImageProcess;
import com.example.hobbit.util.Mission;

public class PrepareCreateMissionActivity extends Activity {

    private static final String TAG = "hobbit" + PrepareCreateMissionActivity.class.getSimpleName();
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_LOAD_PHOTO = 2;
    static final String FILE_HEADER = "file://";
    private Uri mPhotoUri;
    private String mPhotoAbsolutePath;
    private Mission parentMission = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
		if (getIntent().hasExtra(Constants.INTENT_EXTRA_MISSION)) {
			parentMission = (Mission) getIntent().getExtras().get(Constants.INTENT_EXTRA_MISSION);
//			Log.d(TAG, parentMission.getHint());
//			Log.d(TAG, parentMission.getLocalPhotoPath());
//			Log.d(TAG, parentMission.getMissionId());
//			Log.d(TAG, parentMission.getMongoDBId());
//			Log.d(TAG, parentMission.getParentMissionId());
//			Log.d(TAG, parentMission.getPhotoUrl());
//			Log.d(TAG, parentMission.getTitle());
//			Log.d(TAG, parentMission.getUserId());
        }
		
        showOption();
    }

    private void showOption() {
    	 
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
 
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                	dispatchTakePictureIntent();
                }
                else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_LOAD_PHOTO);
 
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
    
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(Environment.getExternalStorageDirectory()
                        + File.separator
                        + Constants.PICTURE_DIR
                        + Constants.HOBBIT_DIR + timeStamp + ".jpg");
        mPhotoAbsolutePath = file.getAbsolutePath();
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
                mPhotoUri = Uri.fromFile(photoFile);
                Log.d(TAG, "mImageUri is " + mPhotoUri);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    
    private Bitmap grabImage()
    {
        this.getContentResolver().notifyChange(mPhotoUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap, scaledBitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mPhotoUri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.DEFAULT_PICTURE_QUALITY, bos);
            bitmap = ExifUtil.rotateBitmap(mPhotoAbsolutePath, bitmap);
            
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.d(TAG, "width is " + width);
            Log.d(TAG, "height is " + height);
            int ratio;
            
            //picture in portrait mode
            if (width < height) {
            	ratio = height / Constants.DEFAULT_PICTURE_LENGTH; 
            } else { //picture in landscape mode
            	ratio = width / Constants.DEFAULT_PICTURE_LENGTH;
            }
            
            Log.d(TAG, "ratio is " + ratio);
            Log.d(TAG, "new width is " + width/ratio);
            Log.d(TAG, "new height is " + height/ratio);
        	scaledBitmap = Bitmap.createScaledBitmap(bitmap, width/ratio, height/ratio, true);
            Log.d(TAG, "Photo is saved in " + mPhotoAbsolutePath);
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
        if(requestCode==REQUEST_TAKE_PHOTO && resultCode==RESULT_OK) {
            // Rescan gallery to fetch newly added photo
        	Log.d(TAG, "Camera is going to be launched for taking photo");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse(FILE_HEADER
                            + Environment.getExternalStorageDirectory())));
        } else if(requestCode==REQUEST_LOAD_PHOTO && resultCode==RESULT_OK && data!=null) {
        	Log.d(TAG, "Select photo from library");
        	Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
    
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
    
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mPhotoAbsolutePath = cursor.getString(columnIndex);
            mPhotoUri = Uri.parse(FILE_HEADER + mPhotoAbsolutePath);
            cursor.close();
        }
        
        Intent intent = new Intent(this, CreateMissionActivity.class);
//        Bitmap bitmap = grabImage();
        Bitmap bitmap = ImageProcess.getScaledImage(this, mPhotoUri, mPhotoAbsolutePath);
        intent.putExtra(Constants.INTENT_EXTRA_PHOTO_BITMAP, bitmap);
        intent.putExtra(Constants.INTENT_EXTRA_PHOTO_ABS_PATH, mPhotoAbsolutePath);
        if(parentMission != null) {
        	Log.d(TAG, "Put parent mission in extra");
        	intent.putExtra(Constants.INTENT_EXTRA_PARENT_MISSION, parentMission);
        }
        startActivity(intent);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
