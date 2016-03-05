package com.locator_app.locator.view;

import android.content.Context;
import android.widget.Toast;

import com.locator_app.locator.apiservice.errorhandling.RequestError;

public class UiError {
    private final static int MIN_CALL_DELAY_MS = 15000;
    private static long lastCall;
    private static boolean calledRecently() {
        long delay = System.currentTimeMillis() - lastCall;
        lastCall = System.currentTimeMillis();
        return delay < MIN_CALL_DELAY_MS;
    }

    public static boolean showError(Context context, Throwable error) {
        if (error instanceof RequestError &&
                ((RequestError) error).getType() == RequestError.RequestErrorType.ServerUnreachable) {
            if (!calledRecently()) {
                Toast.makeText(context, "Kein Internet :(", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    public static void showError(Context context, Throwable error, String message) {
        if (!showError(context, error)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
