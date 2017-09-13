package com.jaaaelu.gzw.neteasy.privatebook;

import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.privatebook.activities.HomeActivity;

import butterknife.BindView;
import me.wangyuwei.particleview.ParticleView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.pv_particle)
    ParticleView mParticle;

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
                HomeActivity.show(MainActivity.this);
                finish();
            }
        });
    }
}
