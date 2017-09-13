package com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.jaaaelu.gzw.neteasy.common.app.BaseFragment;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.SearchBookActivity;
import com.jaaaelu.gzw.neteasy.zxing.activity.CaptureActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class FindBookFragment extends BaseFragment {
    @BindView(R.id.iv_scan_book)
    ImageView mScanBook;

    public FindBookFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_find_book;
    }

    @OnClick(R.id.iv_scan_book)
    public void onViewClicked() {
        CaptureActivity.show(getActivity());
    }

    @OnClick({R.id.cv_find_book_by_scan, R.id.cv_find_book_by_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cv_find_book_by_scan:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
                    CaptureActivity.show(getActivity());
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_READ_CAMERA);
                }
                break;
            case R.id.cv_find_book_by_search:
                SearchBookActivity.show(getActivity());
//                WebViewActivity.show(getActivity());
                break;
        }
    }
}
