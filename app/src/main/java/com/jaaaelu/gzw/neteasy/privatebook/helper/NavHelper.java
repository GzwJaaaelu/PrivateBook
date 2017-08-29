package com.jaaaelu.gzw.neteasy.privatebook.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

/**
 * 解决对 Fragment 的调度与重用问题。
 * 达到最优的 Fragment 切换
 * Created by admin on 2017/5/25.
 */

public class NavHelper<T> {
    //  用于初始化的必需参数
    private final FragmentManager mFragmentManager;
    private final int mContainerLayoutId;
    private final Context mContext;
    private OnTabChangedListener<T> mTabChangedListener;
    //  所有的 Tab
    private SparseArray<Tab<T>> mTabs = new SparseArray();
    //  当前选中的 Tab
    private Tab<T> mCurrTab;
    //  上一次选中的 Tab
    private Tab<T> mLastTab;

    public NavHelper(Context context, FragmentManager fragmentManager, int containerLayoutId,
                     OnTabChangedListener<T> tabChangedListener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mContainerLayoutId = containerLayoutId;
        mTabChangedListener = tabChangedListener;
    }

    /**
     * 添加 Tab
     *
     * @param menuId Tab 对应的菜单 Id
     * @param tab    Tab
     * @return 方便用户多次调用
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        mTabs.put(menuId, tab);
        return this;
    }

    /**
     * 获取当前的 Tab
     *
     * @return
     */
    public Tab<T> getCurrTab() {
        return mCurrTab;
    }

    /**
     * 执行点击菜单的操作
     *
     * @param menuId
     * @return 是否能够处理
     */
    public boolean performClickMenu(int menuId) {
        Tab<T> tab = mTabs.get(menuId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行真实的 Tab 选择操作
     *
     * @param tab
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;
        if (mCurrTab != null) {
            if (mCurrTab == tab) {
                //  如果当前 Tab 就是点击的 Tab
                //  那么不做处理或者可以做某些处理
                notifyReselect(tab);
                return;
            }
            oldTab = mCurrTab;
        }
        mCurrTab = tab;
        doTabChanged(oldTab, mCurrTab);
    }

    /**
     * 进行 Fragment 的真实的调度操作
     *
     * @param oldTab
     * @param newTab
     */
    private void doTabChanged(Tab<T> oldTab, Tab<T> newTab) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (oldTab != null) {
            if (oldTab.fragment != null) {
                //  从界面中移除，但是还在 Fragment 的缓存空间中
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab != null) {
            if (newTab.fragment == null) {
                //  首次新建并缓存
                Fragment fragment = Fragment.instantiate(mContext,
                        newTab.clx.getName(), null);
                newTab.fragment = fragment;
                //  添加到 FragmentManager
                ft.add(mContainerLayoutId, fragment, newTab.clx.getName());
            } else {
                //  FragmentManager 的缓存空间中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        ft.commit();
        //  通知回调
        notifySelect(oldTab, newTab);
    }

    /**
     * 回调侦听
     *
     * @param oldTab 旧的 Tab
     * @param newTab 新的 Tab
     */
    private void notifySelect(Tab<T> oldTab, Tab<T> newTab) {
        if (mTabChangedListener != null) {
            mTabChangedListener.onTabChanged(oldTab, newTab);
        }
    }


    private void notifyReselect(Tab<T> tab) {
        // TODO 二次点击 Tab 所做的操作
    }

    /**
     * 所有的 Tab 基础属性
     *
     * @param <T>
     */
    public static class Tab<T> {
        //  Fragment Class 信息
        public Class<?> clx;
        //  额外的信息，用户存些什么都行
        public T extra;
        //  内部缓存的对应 Fragment
        Fragment fragment;

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }
    }

    /**
     * 定义事件处理完成后的回调接口
     *
     * @param <T>
     */
    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> oldTab, Tab<T> newTab);
    }
}
