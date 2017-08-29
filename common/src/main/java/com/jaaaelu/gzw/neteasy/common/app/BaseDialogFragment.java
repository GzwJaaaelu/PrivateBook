package com.jaaaelu.gzw.neteasy.common.app;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by admin on 2017/3/14.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    protected static final String DIALOG_TITLE = "dialog_title";
    protected static final String DIALOG_CONTENT = "dialog_content";
    private Unbinder mUnBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        View view = null;
        if (getDialogLayoutId() != 0) {
            view = inflater.inflate(getDialogLayoutId(), container, false);
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mUnBinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 用来获取布局Id
     *
     * @return 弹框布局的Id值
     */
    protected abstract int getDialogLayoutId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        mUnBinder = null;
    }
}
