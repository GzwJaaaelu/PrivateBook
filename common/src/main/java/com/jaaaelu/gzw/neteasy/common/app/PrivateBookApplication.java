package com.jaaaelu.gzw.neteasy.common.app;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by Gzw on 2017/8/12 0012.
 */

public class PrivateBookApplication extends Application {
    public static Application getInstance() {
        return sInstance;
    }

    private static Application sInstance;
    public static Handler sHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static void showToast(@StringRes int msgId) {
        showToast(sInstance.getString(msgId));
    }

    public static void showToast(final String msg) {
        //  主线程进行操作
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(sInstance, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
