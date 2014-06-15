package com.example.hobbit.util;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.Toast;

public class ImageProcess {

	private static final String TAG = "hobbit" + ImageProcess.class.getSimpleName();
	private final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final static int cacheSize = maxMemory / 8;
	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };
    private static DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

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

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public static Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
}
