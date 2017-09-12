package com.jaaaelu.gzw.neteasy.common.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by Gzw on 2017/8/15 0015.
 */

public class RecycleViewWithEmptyAndLoadData extends RecyclerView {
    private View mEmptyView;
    private OnLoadDataListener mLoadDataListener;

    final private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public RecycleViewWithEmptyAndLoadData(Context context) {
        super(context);
        init();
    }

    public RecycleViewWithEmptyAndLoadData(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecycleViewWithEmptyAndLoadData(Context context, AttributeSet attrs,
                                           int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //  判断是否到底了
                if (recyclerView.getChildCount()
                        + ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition()
                        >= recyclerView.getLayoutManager().getItemCount())   {
                    if (mLoadDataListener != null) {
                        mLoadDataListener.onLoadData();
                    }
                }
            }
        });
    }

    void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible =
                    getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        checkIfEmpty();
    }

    public void setLoadDataListener(OnLoadDataListener loadDataListener) {
        mLoadDataListener = loadDataListener;
    }

    public interface OnLoadDataListener {
        void onLoadData();
    }
}
