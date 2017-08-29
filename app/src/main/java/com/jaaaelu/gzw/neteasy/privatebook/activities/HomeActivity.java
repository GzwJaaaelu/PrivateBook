package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.statisticsbook.BookStatisticsFragment;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.MyBookFragment;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook.FindBookFragment;
import com.jaaaelu.gzw.neteasy.privatebook.helper.NavHelper;

public class HomeActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavHelper.OnTabChangedListener<Integer> {
    private NavHelper<Integer> mNavHelper;
    private BottomNavigationView mBottomNavigation;

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
    protected void initView() {
        super.initView();
        mBottomNavigation = (BottomNavigationView) findViewById(R.id.nbv_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
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
//        if (item.getItemId() == R.id.navigation_scan_book) {
//            ScanBookActivity.show(this);
//            return true;
//        }
        return mNavHelper.performClickMenu(item.getItemId());
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> oldTab, NavHelper.Tab<Integer> newTab) {

    }
}
