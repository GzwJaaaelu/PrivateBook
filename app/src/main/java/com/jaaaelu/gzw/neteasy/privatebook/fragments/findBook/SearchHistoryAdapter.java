package com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaaaelu.gzw.neteasy.model.HistorySearchInfo;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.SearchBookActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gzw on 2017/8/26 0026.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchHistory> {
    private List<HistorySearchInfo> mHistorySearchList = new ArrayList<>();
    private SearchBookActivity mActivity;

    public SearchHistoryAdapter(List<HistorySearchInfo> historySearchList) {
        mHistorySearchList = historySearchList;
    }

    public SearchHistoryAdapter() {

    }

    public SearchHistoryAdapter(SearchBookActivity activity) {
        mActivity = activity;
    }

    @Override
    public SearchHistory onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new SearchHistory(view);
    }

    @Override
    public void onBindViewHolder(SearchHistory holder, int position) {
        holder.setData(mHistorySearchList.get(position));
    }

    @Override
    public int getItemCount() {
        return mHistorySearchList.size();
    }

    public void setHistorySearchList(List<HistorySearchInfo> historySearchList) {
        mHistorySearchList.clear();
        mHistorySearchList.addAll(historySearchList);
        notifyDataSetChanged();
    }

    class SearchHistory extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_access_time)
        ImageView mAccessTime;
        @BindView(R.id.tv_search_keyword)
        TextView mSearchKeyword;
        @BindView(R.id.iv_delete_history)
        ImageView mDeleteHistory;

        public SearchHistory(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.queryBookByKeyWord( mHistorySearchList.get(getAdapterPosition()).getKeyWord());
                }
            });

            mDeleteHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHistorySearchList.get(getAdapterPosition()).delete();
                    mHistorySearchList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }

        void setData(HistorySearchInfo historySearchInfo) {
            mSearchKeyword.setText(historySearchInfo.getKeyWord());
        }
    }
}
