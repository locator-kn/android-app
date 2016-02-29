package com.locator_app.locator.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.apiservice.errorhandling.GenericErrorHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

            bitmap = getResizedBitmap(bitmap, 1920);
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

    static private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
