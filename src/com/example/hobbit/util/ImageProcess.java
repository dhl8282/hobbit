package com.example.hobbit.util;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class ImageProcess {

	private static final String TAG = "hobbit" + ImageProcess.class.getSimpleName();
	
	public static Bitmap getScaledImage(Context context, Uri photoUri, String photoAbsolutePath) {
        try
        {
            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            return getScaledImageFromBitmap(bitmap, photoAbsolutePath);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
        return null;
	}
	
	public static Bitmap getScaledImageFromBitmap(Bitmap bitmap, String photoAbsolutePath) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.DEFAULT_PICTURE_QUALITY, bos);
	        bitmap = ExifUtil.rotateBitmap(photoAbsolutePath, bitmap);
	        
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
	        Log.d(TAG, "Photo is saved in " + photoAbsolutePath);
	    	return Bitmap.createScaledBitmap(bitmap, width/ratio, height/ratio, true);
	}
}
