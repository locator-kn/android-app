package com.locator_app.playground.bubblesample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/*
    BitmapHelper offers helper functions to manipulate Bitmaps.
    usage:
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.esschon);
        bitmap = getRoundBitmap(bitmap, size);

        canvas.drawBitmap(bitmap, 0, 0, paint);
 */

public class BitmapHelper {

    public static Bitmap getRoundBitmap(Bitmap bitmap, int size) {
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);

        Bitmap output = Bitmap.createBitmap(size,
                size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();

        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }
}
