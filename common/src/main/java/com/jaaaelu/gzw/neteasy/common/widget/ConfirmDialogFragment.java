package com.jaaaelu.gzw.neteasy.common.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaaaelu.gzw.neteasy.common.R;
import com.jaaaelu.gzw.neteasy.common.R2;
import com.jaaaelu.gzw.neteasy.common.app.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Gzw on 2017/8/24 0024.
 */

public class ConfirmDialogFragment extends BaseDialogFragment {

    @BindView(R2.id.tv_dialog_title)
    TextView mDialogTitle;
    @BindView(R2.id.tv_dialog_content)
    TextView mDialogContent;
    @BindView(R2.id.tv_dialog_confirm)
    TextView mDialogConfirm;
    @BindView(R2.id.tv_dialog_cancel)
    TextView mDialogCancel;

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().finish();
        }
    };

    public static ConfirmDialogFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString(DIALOG_CONTENT, content);
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mDialogContent.setText(getArguments().getString(DIALOG_CONTENT));

        mDialogConfirm.setOnClickListener(mListener);

        mDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void onConfirmClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    protected int getDialogLayoutId() {
        return R.layout.dialog_fragment_confim;
    }

}
