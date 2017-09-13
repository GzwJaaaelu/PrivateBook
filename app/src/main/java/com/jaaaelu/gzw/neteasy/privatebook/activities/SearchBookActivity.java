package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.common.widget.RecycleViewWithEmpty;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.model.Books;
import com.jaaaelu.gzw.neteasy.model.HistorySearchInfo;
import com.jaaaelu.gzw.neteasy.net.BookRequest;
import com.jaaaelu.gzw.neteasy.net.OnBookResultListener;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook.SearchHistoryAdapter;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook.ShowSearchBookAdapter;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jaaaelu.gzw.neteasy.common.tools.UiTool.getLayoutTransition;

public class SearchBookActivity extends BaseActivity implements OnBookResultListener<Books>, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.et_book_info_edit)
    EditText mBookInfoEdit;
    @BindView(R.id.rl_search_history)
    RecyclerView mSearchHistory;
    @BindView(R.id.rl_search_book_info)
    RecycleViewWithEmpty mSearchBookInfo;
    @BindView(R.id.iv_go_back)
    ImageView mGoBack;
    @BindView(R.id.ll_look_around)
    LinearLayout mLookAround;
    @BindView(R.id.ll_search_book_root)
    LinearLayout mSearchBookRoot;
    @BindView(R.id.ll_loading_book_info)
    LinearLayout mLoadingBookInfo;
    @BindView(R.id.btn_look_round)
    Button mLookRoundBtn;
    @BindView(R.id.srl_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.pb_loading_more)
    ProgressBar mLoadingMore;
    @BindView(R.id.ll_end_hint)
    LinearLayout mEndHint;
    private ShowSearchBookAdapter mAdapter;
    private SearchHistoryAdapter mHistoryAdapter;
    private int mCurrStartIndex = BookRequest.SEARCH_START_INDEX;
    private boolean mHasQueryNext = true;
    private String mCurrKeyword = "";

    /**
     * 跳转到当前 Activity
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, SearchBookActivity.class));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search_book;
    }

    @Override
    protected void initView() {
        super.initView();

        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(this);

        mSearchBookRoot.setLayoutTransition(getLayoutTransition());
        mBookInfoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = v.getText().toString();
                    if ("".equals(keyword)) {
                        mBookInfoEdit.startAnimation(shakeAnimation(5));
                        return true;
                    }
                    queryBookByKeyWord(keyword);
                    closeInput();
                    return true;
                }
                return false;
            }
        });

        mLoadingBookInfo.setVisibility(View.GONE);
        mSearchHistory.setLayoutManager(new LinearLayoutManager(this));
        mHistoryAdapter = new SearchHistoryAdapter(this);
        mSearchHistory.setAdapter(mHistoryAdapter);
    }

    /**
     * 借鉴了别人的动画方法
     *
     * @param counts 次数
     * @return 动画
     */
    private Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    /**
     * 关闭可软键盘
     */
    private void closeInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().peekDecorView().getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 通过关键词找书
     *
     * @param keyWord 关键词
     */
    public void queryBookByKeyWord(String keyWord) {
        mCurrKeyword = keyWord;
        if (!mRefreshLayout.isRefreshing()) {
            mLoadingBookInfo.setVisibility(View.VISIBLE);
        }
        BookRequest.getInstance().queryBookByKeyWord(keyWord, mCurrStartIndex, SearchBookActivity.this);
        saveHistoryInfo(keyWord);
    }

    /**
     * 保存搜索历史
     *
     * @param keyWord 关键词
     */
    private void saveHistoryInfo(final String keyWord) {
        BookManager.queryHistoryByKeWord(new QueryTransaction.QueryResultCallback<HistorySearchInfo>() {
            @Override
            public void onQueryResult(QueryTransaction<HistorySearchInfo> transaction, @NonNull CursorResult<HistorySearchInfo> tResult) {
                if (tResult.getCount() == 0) {
                    realSave(keyWord, 1);
                } else {
                    int count = tResult.getItem(0).getSearchTimes();
                    realSave(keyWord, count + 1);
                }

            }
        }, keyWord);
    }

    /**
     * 真正保存的逻辑
     *
     * @param keyWord 关键词
     * @param count   出现次数
     */
    private void realSave(String keyWord, int count) {
        new HistorySearchInfo(keyWord,
                System.currentTimeMillis(),
                count).save();
    }

    @Override
    protected void initData() {
        super.initData();
        BookManager.queryAllHistory(new QueryTransaction.QueryResultListCallback<HistorySearchInfo>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<HistorySearchInfo> tResult) {
                if (!tResult.isEmpty()) {
                    mHistoryAdapter.setHistorySearchList(tResult);
                }
            }
        });
    }


    @Override
    public void onSuccess(Books books) {
        for (Book book : books.getBooks()) {
            book.saveAuthorStr();
            book.saveImagesStr();
            book.saveRatingStr();
            book.saveTagsStr();
            book.saveTranslatorStr();
        }
        if (mAdapter == null) {
            mAdapter = new ShowSearchBookAdapter(books.getBooks());
            mSearchBookInfo.setLayoutManager(new LinearLayoutManager(this));
            mSearchBookInfo.setAdapter(mAdapter);
            changeVisibility(true);
            return;
        }
        if (mCurrStartIndex == BookRequest.SEARCH_START_INDEX) {
            mAdapter.setBooks(books.getBooks());
        } else {
            mAdapter.setBooksAndNotClear(books.getBooks());
        }
        changeVisibility(true);

        mCurrStartIndex += BookRequest.SEARCH_TOTAL_COUNT;

        if ((books.getCount() + books.getStart()) >= books.getTotal()
                || mCurrStartIndex > BookRequest.SEARCH_MAX_START_INDEX) {
            mHasQueryNext = false;
        }
    }

    private void changeVisibility(boolean showBookInfoList) {
        mRefreshLayout.setRefreshing(false);
        mLoadingBookInfo.setVisibility(View.GONE);
        mSearchHistory.setVisibility(showBookInfoList ? View.GONE : View.VISIBLE);
        mSearchBookInfo.setVisibility(showBookInfoList ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFailure(Throwable t) {
        changeVisibility(false);
        if (t != null) {
            t.printStackTrace();
        }
    }

    @OnClick(R.id.iv_go_back)
    public void onViewClicked() {
        finish();
    }

    @OnClick(R.id.btn_look_round)
    public void lookRound() {
        WebViewActivity.show(this);
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        mHasQueryNext = true;
        mCurrStartIndex = BookRequest.SEARCH_START_INDEX;
        queryBookByKeyWord(mCurrKeyword);
    }
}
