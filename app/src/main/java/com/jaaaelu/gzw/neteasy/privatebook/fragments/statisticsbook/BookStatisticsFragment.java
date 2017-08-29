package com.jaaaelu.gzw.neteasy.privatebook.fragments.statisticsbook;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jaaaelu.gzw.neteasy.common.app.BaseFragment;
import com.jaaaelu.gzw.neteasy.common.widget.PieChart;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.jaaaelu.gzw.neteasy.zxing.activity.CaptureActivity;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;


public class BookStatisticsFragment extends BaseFragment {

    @BindView(R.id.tv_private_book_total_count)
    TextView mPrivateBookTotalCount;
    @BindView(R.id.pie_chart)
    PieChart mPieChart;
    @BindView(R.id.tv_private_book_total_price)
    TextView mPrivateBookTotalPrice;
    @BindView(R.id.tv_love_auth)
    TextView mLoveAuth;
    @BindView(R.id.tv_auth_book_count)
    TextView mAuthBookCount;
    @BindView(R.id.tv_love_publisher)
    TextView mLovePublisher;
    @BindView(R.id.tv_publisher_book_count)
    TextView mPublisherBookCount;
    @BindView(R.id.cl_empty_view)
    ConstraintLayout mEmptyView;
    Unbinder unbinder;

    private List<Book> mPrivateBookList;
    private HashMap<String, Integer> mCountMap;

    public BookStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_book_statistics;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    @Override
    protected void initData() {
        super.initData();
        mCountMap = new HashMap<>();
        queryBook();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BookManager.DataChange) {
            queryBook();
            BookManager.DataChange = false;
        }
    }

    private void queryBook() {
        BookManager.queryAllBook(new QueryTransaction.QueryResultListCallback<Book>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<Book> tResult) {
                mPrivateBookList = tResult;
                setStatisticsInfo();
            }
        });
    }


    private void setStatisticsInfo() {
        if (mPrivateBookList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mPrivateBookTotalCount.setText("0");
        } else {
            mEmptyView.setVisibility(View.GONE);
            mPrivateBookTotalCount.setText("" + mPrivateBookList.size());
            mPrivateBookTotalPrice.setText(calculatePrivateBookTotalPrice());
            setFavoriteInfo();
        }
    }

    private void setFavoriteInfo() {
        String auth = calculateMyFavoriteAuth();
        String publisher = calculateMyFavoritePublisher();
        mLoveAuth.setText(getAlreadyDealText(auth.split("-")[0] + " / 最爱作者"));
        mAuthBookCount.setText(auth.split("-")[1]);
        mLovePublisher.setText(getAlreadyDealText(publisher.split("-")[0] + " / 最爱出版社"));
        mPublisherBookCount.setText(publisher.split("-")[1]);
    }

    private SpannableString getAlreadyDealText(String content) {
        SpannableString newContent = new SpannableString(content);
        newContent.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textThirdColor)), content.indexOf("/"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        newContent.setSpan(new AbsoluteSizeSpan(sp2px(getActivity(), 13)), content.indexOf("/"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return newContent;
    }

    private String calculatePrivateBookTotalPrice() {
        double sum = 0.0;
        for (Book book : mPrivateBookList) {
            sum += handleMoneyUtil(book.getPrice());
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return "¥ " + df.format(sum);
    }

    private double handleMoneyUtil(String price) {
        double rmbPrice = 0.0;
        if (TextUtils.isEmpty(price)) {
            return rmbPrice;
        }
        if (price.contains("元")) {
            rmbPrice = Double.valueOf(price.split("元")[0]);
        } else if (price.contains("CNY")) {
            rmbPrice = Double.valueOf(price.split(" ")[1]);
        } else if (price.contains("NT$")) {
            rmbPrice = Double.valueOf(price.replace('$', ' ').split(" ")[1]) * 0.2202;
        } else if (price.contains("USD")) {
            rmbPrice = Double.valueOf(price.split(" ")[1]) * 6.6379;
        } else {
            try {
                rmbPrice = Double.valueOf(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rmbPrice;
    }

    private String calculateMyFavoriteAuth() {
        mCountMap.clear();
        for (Book book : mPrivateBookList) {
            String authStr = book.getAuthorStr();
            String[] allAuth = authStr.split(",");
            for (String auth : allAuth) {
                String newAuth = auth;
                if (auth.contains("]")) {
                    newAuth = auth.substring(auth.indexOf("]") + 1, auth.length()).trim();
                }
                boolean containsBook = mCountMap.containsKey(newAuth);
                Log.e("Auth", newAuth);
                if (containsBook) {
                    int count = mCountMap.get(newAuth);
                    mCountMap.put(newAuth, (count + 1));
                } else {
                    mCountMap.put(newAuth, 1);
                }
            }
        }

        return calculateMostInMap();
    }

    private String calculateMostInMap() {
        String mostKey = "暂无";
        int mostValue = 0;
        for (String key : mCountMap.keySet()) {
            int count = mCountMap.get(key);
            if (count > mostValue) {
                mostKey = key;
                mostValue = count;
            }
        }
        return mostKey + "-" + mostValue + " 本";
    }

    private String calculateMyFavoritePublisher() {
        mCountMap.clear();
        for (Book book : mPrivateBookList) {
            String publisher = book.getPublisher();
            boolean containsBook = mCountMap.containsKey(publisher);
            if (containsBook) {
                int count = mCountMap.get(publisher);
                mCountMap.put(publisher, (count + 1));
            } else {
                mCountMap.put(publisher, 1);
            }
        }
        return calculateMostInMap();
    }

    @OnClick(R.id.cl_empty_view)
    public void onViewClicked() {
        CaptureActivity.show(getActivity());
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
