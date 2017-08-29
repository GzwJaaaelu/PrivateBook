package com.jaaaelu.gzw.neteasy.common.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Gzw on 2017/8/12 0012.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  在界面未初始化之前调用的初始化窗口
        initWindows();
        if (!initArgs(getIntent().getExtras())) {
            finish();
        }
        setContentView(getLayoutResId());
        initView();
        initData();
    }

    /**
     * 初始化窗口
     */
    protected void initWindows() {

    }

    /**
     * 初始化相关参数
     *
     * @param bundle
     * @return 如果传输正确返回 True，否则返回 False
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 获取当前界面的资源 Id
     *
     * @return 资源 Id
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化控件
     */
    protected void initView() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        //  但点击界面导航返回时，Finish 掉当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //  使用 Fragment 时确认多次 Fragment 逐级返回
        //  得到当前 Activity 下的所有 Fragment
        @SuppressLint("RestrictedApi") List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //  判断是否有数据
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                //  是否为我们自己定义的 Fragment
                if (fragment instanceof BaseFragment) {
                    //  处理返回逻辑
                    if (((BaseFragment) fragment).onBackPressed()) {
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }
}
