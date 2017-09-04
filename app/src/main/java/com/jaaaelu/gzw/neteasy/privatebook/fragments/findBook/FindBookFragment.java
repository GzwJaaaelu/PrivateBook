package com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jaaaelu.gzw.neteasy.common.app.BaseFragment;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.SearchBookActivity;
import com.jaaaelu.gzw.neteasy.privatebook.activities.WebViewActivity;
import com.jaaaelu.gzw.neteasy.zxing.activity.CaptureActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class FindBookFragment extends BaseFragment {
    public static final int MY_PERMISSIONS_REQUEST_READ_CAMERA = 0;
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
