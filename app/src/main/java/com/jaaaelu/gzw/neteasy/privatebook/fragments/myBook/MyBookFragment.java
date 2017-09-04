package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.jaaaelu.gzw.neteasy.common.app.BaseFragment;
import com.jaaaelu.gzw.neteasy.common.widget.RecycleViewWithEmpty;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.jaaaelu.gzw.neteasy.zxing.activity.CaptureActivity;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook.FindBookFragment.MY_PERMISSIONS_REQUEST_READ_CAMERA;
import static com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowPrivateBookAdapter.ViewType.SHOW_BY_GRID;
import static com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowPrivateBookAdapter.ViewType.SHOW_BY_LIST;


public class MyBookFragment extends BaseFragment {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_show_my_book)
    RecycleViewWithEmpty mShowMyBook;
    @BindView(R.id.iv_recyclerView_style)
    ImageView mRecyclerViewStyle;
    @BindView(R.id.iv_go_add_book)
    ImageView mIvGoAddBook;
    private boolean mShowList = true;
    private List<Book> mBooks;
    private ShowPrivateBookAdapter mAdapter;
    ListView mListView;

    public MyBookFragment() {
        // Required empty public constructor
        mBooks = new ArrayList<>();
        mAdapter = new ShowPrivateBookAdapter(mBooks);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mShowMyBook.setLayoutManager(new LinearLayoutManager(getActivity()));
        mShowMyBook.setEmptyView(view.findViewById(R.id.cl_empty_view));
        mShowMyBook.setAdapter(mAdapter);

        PrivaterBookHolder.sListener = new OnBookListChange() {
            @Override
            public void onBookListChange() {
                queryBook();
            }
        };
    }

    @Override
    protected void initData() {
        super.initData();
        queryBook();
    }

    private void queryBook() {
        BookManager.queryAllBook(new QueryTransaction.QueryResultListCallback<Book>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<Book> tResult) {
                mBooks.clear();
                mBooks.addAll(tResult);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BookManager.DataChange) {
            queryBook();
            BookManager.DataChange = false;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my_book;
    }

    @OnClick(R.id.iv_recyclerView_style)
    public void onViewClicked() {
        mRecyclerViewStyle.setImageResource(mShowList ? R.drawable.ic_show_grid_book : R.drawable.ic_show_list_book);
        mShowList = !mShowList;
        changeRecycleView();
    }

    private void changeRecycleView() {
        if (mAdapter.getCurrViewType() == SHOW_BY_LIST) {
            mAdapter.setCurrViewType(SHOW_BY_GRID);
            mShowMyBook.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        } else {
            mAdapter.setCurrViewType(SHOW_BY_LIST);
            mShowMyBook.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.iv_go_add_book)
    public void onAddBook() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
            CaptureActivity.show(getActivity());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_CAMERA);
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
