package com.jaaaelu.gzw.neteasy.privatebook;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jaaaelu.gzw.neteasy.privatebook.activities.HomeActivity;

public class MainActivity extends AppCompatActivity {
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private ShowHome mShowHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowHome = new ShowHome();
        sHandler.postDelayed(mShowHome, 1800);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sHandler.removeCallbacks(mShowHome);
    }

    private class ShowHome implements Runnable {

        @Override
        public void run() {
            HomeActivity.show(getApplicationContext());
            finish();
        }
    }
}
