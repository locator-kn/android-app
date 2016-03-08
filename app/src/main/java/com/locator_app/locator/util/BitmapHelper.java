package com.locator_app.locator.util;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rx.Observable;


public class BitmapHelper {

    public static Bitmap getRoundBitmap(Bitmap bitmap, int size) {
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);

        Bitmap output = Bitmap.createBitmap(size,
                size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    public static File toJpgFile(Bitmap bitmap, String filename, int quality) {
        File f = new File(LocatorApplication.getAppContext().getCacheDir(), filename);
        try {
            f.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File toJpgFile(Bitmap bitmap, String filename) {
        return toJpgFile(bitmap, filename, 75);
    }

    public static File toJpgFile(Bitmap bitmap) {
        String filename = "pic" + Long.toString(System.currentTimeMillis()) + ".jpg";
        return toJpgFile(bitmap, filename);
    }

    public static Bitmap get(Uri uri, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String filename = getRealPathFromURI(uri);
        BitmapFactory.decodeFile(filename, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    public static String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = LocatorApplication.getAppContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
