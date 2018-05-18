package me.ccrama.redditslide.util;

import android.app.Activity;
import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ToastHelper {
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    @interface Duration {
    }

    private ToastHelper() {
    }

    /**
     * Handles showing a toast after a number of toasts in the queue
     *
     * @param activity      the activity to display the toast on
     * @param text          the text to display in the toast
     * @param duration      the duration of the toast to show
     * @param toastsInQueue the queue of toasts to show this toast after
     */
    public static void makeTextDelayed(final Activity activity, final CharSequence text,
            final @Duration int duration, List<Toast> toastsInQueue) {
        int delayDuration = 0;
        if (toastsInQueue != null && !toastsInQueue.isEmpty()) {
            for (Toast toast : toastsInQueue) {
                delayDuration += toast.getDuration() == Toast.LENGTH_SHORT ? 4000 : 7000;
            }
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, text, duration).show();
                    }
                });
            }
        }, delayDuration);
    }
}
