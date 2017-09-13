package com.jaaaelu.gzw.neteasy.common.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jaaaelu.gzw.neteasy.zxing.activity.CaptureActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Gzw on 2017/8/12 0012.
 */

public abstract class BaseFragment extends Fragment {
    public static final int MY_PERMISSIONS_REQUEST_READ_CAMERA = 0;
    private View mRootView;                     //  如果 View 需要复用的话
    private Unbinder mRootUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            //  初始化当前根布局，但是不在创建时就添加到 container，所以第三个参数为 False
            mRootView = inflater.inflate(getLayoutResId(), container, false);
            initView(mRootView);
        } else {
            if (mRootView.getParent() != null) {
                //  当前 mRootView 从其父控件中移除
                ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  当界面初始化完成之后再初始化数据
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
    protected void initView(View view) {
        mRootUnbinder = ButterKnife.bind(this, view);
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回 True 代表我已处理返回逻辑， Activity 不用自己 Finish
     * 返回 False 代表 Fragment 自己没有处理
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 权限回调
     *
     * @param requestCode  请求码
     * @param permissions  权限列表
     * @param grantResults 结果列表
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CaptureActivity.show(getActivity());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "您拒绝了二维码扫描的必要权限，无法使用扫码查书的功能，您可以尝试使用关键字查书功能...", Toast.LENGTH_LONG).show();
                }

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
