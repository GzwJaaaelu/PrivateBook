package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

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
import butterknife.OnClick;

import static com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowPrivateBookAdapter.ViewType.SHOW_BY_GRID;
import static com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowPrivateBookAdapter.ViewType.SHOW_BY_LIST;


public class MyBookFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_show_my_book)
    RecycleViewWithEmpty mShowMyBook;
    @BindView(R.id.iv_recyclerView_style)
    ImageView mRecyclerViewStyle;
    @BindView(R.id.iv_go_add_book)
    ImageView mIvGoAddBook;
    @BindView(R.id.srl_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    private boolean mShowList = true;
    private List<Book> mBooks;
    private ShowPrivateBookAdapter mAdapter;

    public MyBookFragment() {
        // Required empty public constructor
        mBooks = new ArrayList<>();
        mAdapter = new ShowPrivateBookAdapter(mBooks);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(this);

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
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //  如果图书更新了就重新查询
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

    /**
     * 切换 RecycleView 显示方式
     */
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
        //  进行权限校验后跳转至二维码界面
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
            CaptureActivity.show(getActivity());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_CAMERA);
        }
    }

    @Override
    public void onRefresh() {
        queryBook();
        mRefreshLayout.setRefreshing(true);
    }
}
