package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.common.tools.UiTool;
import com.jaaaelu.gzw.neteasy.common.widget.ConfirmDialogFragment;
import com.jaaaelu.gzw.neteasy.privatebook.App;
import com.jaaaelu.gzw.neteasy.privatebook.MainActivity;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook.FindBookFragment;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.MyBookFragment;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.statisticsbook.BookStatisticsFragment;
import com.jaaaelu.gzw.neteasy.privatebook.helper.NavHelper;
import com.jaaaelu.gzw.neteasy.privatebook.helper.SharePreferencesHelper;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.jaaaelu.gzw.neteasy.util.Dateutil;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.animation.Animator.DURATION_INFINITE;


public class HomeActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavHelper.OnTabChangedListener<Integer> {
    @BindView(R.id.fab_action)
    FloatingActionButton mFab;
    private NavHelper<Integer> mNavHelper;
    private BottomNavigationView mBottomNavigation;
    private static final int DOUBLE_EXIT_TIME = 1500;
    private long mLastTime = 0;

    /**
     * 跳转到当前 Activity
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initWindows() {
        super.initWindows();
        if (!Dateutil.getDate().equals(SharePreferencesHelper.getString(SharePreferencesHelper.DATE_INFO, SharePreferencesHelper.TAG_APP_COMMON))) {
            MainActivity.show(this);
            SharePreferencesHelper.putString(SharePreferencesHelper.DATE_INFO, Dateutil.getDate(), SharePreferencesHelper.TAG_APP_COMMON);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mBottomNavigation = (BottomNavigationView) findViewById(R.id.nbv_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncBook();
            }
        });
    }

    private void syncBook() {
        final ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance("是否将全部图书信息同步至印象笔记？");
        fragment.show(getSupportFragmentManager(), "");
        fragment.onConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ObjectAnimator animator = ObjectAnimator.ofFloat(mFab, "rotation", 0, 359);
                animator.setDuration(1000);
                animator.setRepeatCount(ObjectAnimator.INFINITE);
                animator.setInterpolator(new LinearInterpolator());
                animator.start();
                fragment.dismiss();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        Log.e("HomeActivity", "initData");
        mNavHelper = new NavHelper<>(this,
                getSupportFragmentManager(),
                R.id.fl_container,
                this);

        mNavHelper.add(R.id.navigation_my_book,
                new NavHelper.Tab<>(MyBookFragment.class, R.string.title_my_book))
                .add(R.id.navigation_scan_book,
                        new NavHelper.Tab<>(FindBookFragment.class, R.string.title_find_book))
                .add(R.id.navigation_book_statistics,
                        new NavHelper.Tab<>(BookStatisticsFragment.class, R.string.title_book_statistics));

        //  从底部导航栏拿到到 Menu
        Menu menu = mBottomNavigation.getMenu();
        //  手动触发第一次点击 选中 Home
        menu.performIdentifierAction(R.id.navigation_my_book, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //  返回 True 切换换下面的被点击的导航
        //  将事件流转到工具类中
        Log.e("HomeActivity", "onNavigationItemSelected");
        return mNavHelper.performClickMenu(item.getItemId());
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> oldTab, NavHelper.Tab<Integer> newTab) {
        //  对浮动按钮进行隐藏与显示的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_my_book)) {
            //  主界面时显示
            rotation = 360;
        } else {
            transY = UiTool.dipToPx(getResources(), 76);
        }

        // 开始动画
        // 旋转，Y轴位移，弹性插值器，时间
        mFab.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastTime > DOUBLE_EXIT_TIME) {
            App.showToast("再次按返回键退出");
            mLastTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
