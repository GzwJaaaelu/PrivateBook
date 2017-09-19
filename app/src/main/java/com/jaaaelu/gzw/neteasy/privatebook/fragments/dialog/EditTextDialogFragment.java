package com.jaaaelu.gzw.neteasy.privatebook.fragments.dialog;

import android.content.res.ColorStateList;
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
import android.widget.EditText;
import android.widget.TextView;

import com.jaaaelu.gzw.neteasy.privatebook.R;

import java.lang.reflect.Field;

import butterknife.ButterKnife;


/**
 * Created by admin on 2016/11/21.
 */

public class EditTextDialogFragment extends DialogFragment {
    private EditText mContent;
    private TextView mTitle;
    private TextView mConfirm;
    private TextView mCancel;
    private ChangeContentListener mListener;

    public static EditTextDialogFragment newInstance(String title, int titleColor) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("titleColor", titleColor);
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditTextDialogFragment newInstance(String title, String content, int titleColor) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putInt("titleColor", titleColor);
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentListener(ChangeContentListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        View view = inflater.inflate(R.layout.fragmen_edittextt_dialog, container, false);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        mTitle.setText(getArguments().getString("title"));
        mTitle.setBackgroundColor(getArguments().getInt("titleColor"));
        mContent = (EditText) view.findViewById(R.id.et_dialog_content);
        mContent.setText(getArguments().getString("content", ""));
        mContent.setSelection(getArguments().getString("content", "").length());
        mContent.setSelection(getEditTextContent().length());
        mConfirm = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        mCancel = (TextView) view.findViewById(R.id.tv_dialog_cancel);
        mContent.setBackgroundTintList(ColorStateList.valueOf(getArguments().getInt("titleColor")));
        mConfirm.setTextColor(getArguments().getInt("titleColor"));
        mCancel.setTextColor(getArguments().getInt("titleColor"));
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.changeContent(getEditTextContent());
                dismiss();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public String getEditTextContent() {
        return mContent.getText().toString();
    }

    public interface ChangeContentListener {
        void changeContent(String newContent);
    }
}
