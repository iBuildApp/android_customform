package com.ibuildapp.romanblack.CustomFormPlugin.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

public class ImageUtils {
    public static final String BITMAP_EXTRA = "BITMAP_SOURCE";
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromFile(String file,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file,  options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        ExifInterface exifInterface;
        Matrix matrix = new Matrix();
        try {
            exifInterface = new ExifInterface(file);
            int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (rotation) {
                case ExifInterface.ORIENTATION_NORMAL: {
                }
                break;
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    matrix.postRotate(90);
                }
                break;
                case ExifInterface.ORIENTATION_ROTATE_180: {
                    matrix.postRotate(180);
                }
                break;
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    matrix.postRotate(270);
                }
                break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeFile(file, options);
        Bitmap  rotateBitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return rotateBitmap;
    }
}
