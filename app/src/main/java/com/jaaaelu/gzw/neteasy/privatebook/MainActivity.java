package com.jaaaelu.gzw.neteasy.privatebook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.privatebook.activities.HomeActivity;
import com.jaaaelu.gzw.neteasy.privatebook.helper.SharePreferencesHelper;
import com.jaaaelu.gzw.neteasy.util.Dateutil;

import butterknife.BindView;
import me.wangyuwei.particleview.ParticleView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.pv_particle)
    ParticleView mParticle;

    /**
     * 跳转到当前 Activity
     *
     * @param context
     */
    public static void show(Context context) {
        SharePreferencesHelper.putString(SharePreferencesHelper.DATE_INFO, Dateutil.getDate(), SharePreferencesHelper.TAG_APP_COMMON);
        context.startActivity(new Intent(context, MainActivity.class));
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        mParticle.startAnim();

        mParticle.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                finish();
            }
        });
    }
}
