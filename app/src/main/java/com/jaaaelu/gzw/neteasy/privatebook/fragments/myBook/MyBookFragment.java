package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;


import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.Notebook;
import com.jaaaelu.gzw.neteasy.common.app.EventNoteBaseFragment;
import com.jaaaelu.gzw.neteasy.common.tools.UiTool;
import com.jaaaelu.gzw.neteasy.common.widget.RecycleViewWithEmpty;
import com.jaaaelu.gzw.neteasy.evernote.task.GetNoteHtmlTask;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.net.BookRequest;
import com.jaaaelu.gzw.neteasy.net.OnBookResultListener;
import com.jaaaelu.gzw.neteasy.privatebook.App;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.HomeActivity;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowPrivateBookAdapter.ViewType.SHOW_BY_GRID;
import static com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowPrivateBookAdapter.ViewType.SHOW_BY_LIST;


public class MyBookFragment extends EventNoteBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
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
    @BindView(R.id.sp_show_tag)
    Spinner mShowTag;
    private boolean mShowList = true;
    private List<Book> mBooks;
    private ShowPrivateBookAdapter mAdapter;
    private ArrayList<String> mTagList;
    private List<String> mBookInfoList;
    private HomeActivity mActivity;

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

        mTagList = new ArrayList<>();
        mTagList.add("我的藏书");
        SpinnerAdapter adapter =
                new ArrayAdapter<>(getActivity(), R.layout.item_tag_in_toolbar, mTagList);

        mShowTag.setAdapter(adapter);

        mShowTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    queryBookByTag(position);
                } else {
                    queryBook();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        queryBook();

        mActivity = (HomeActivity) getActivity();
    }

    private void queryBook() {
        BookManager.queryAllBook(new QueryTransaction.QueryResultListCallback<Book>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<Book> tResult) {
                changeOldDBData(tResult);
                setBookData(tResult);
                addTag();
            }
        });
    }

    private void queryBookByTag(int position) {
        BookManager.queryBookByTag(new QueryTransaction.QueryResultListCallback<Book>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<Book> tResult) {
                setBookData(tResult);
            }
        }, mTagList.get(position));
    }

    private void changeOldDBData(List<Book> tResult) {
        for (Book book : tResult) {
            if ("default".equals(book.getCustomTag())) {
                book.setCustomTag(Book.TAG_TYPE);
            }
        }
    }

    private void setBookData(List<Book> tResult) {
        if (tResult.isEmpty()) {
            mActivity.fabAnim(0, UiTool.dipToPx(getResources(), 76));
        } else {
            mActivity.fabAnim(360, 0);
        }
        mBooks.clear();
        mBooks.addAll(tResult);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.setRefreshing(false);
    }

    private void addTag() {
        mTagList.clear();
        HashSet<String> set = new HashSet<>();
        for (Book book : mBooks) {
            set.add(book.getCustomTag());
        }

        mTagList.add("我的藏书");
        mTagList.addAll(set);
        set.add(Book.TAG_TYPE);
        BookManager.setCustomTag(set);
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
    public void onSyncBook() {
        loginEventNote();
    }

    private void loginEventNote() {
        if (EvernoteSession.getInstance().isLoggedIn()) {
            startQuery();
        } else {
            EvernoteSession.getInstance().authenticate(getActivity());
        }
    }

    public void startQuery() {
        try {
            mActivity.startAnim();
            queryEverNoteBook();
        } catch (Exception e) {
            e.printStackTrace();
            App.showToast("数据获取失败...");
        } finally {
            mActivity.cancelAnim();
        }
    }

    @Override
    protected void onQueryNoteBookException() {
        App.showToast("数据获取失败...");
    }

    @Override
    public void onRefresh() {
        queryBook();
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void doSomethingWhenGetNoteBook(Notebook notebook) {
        if (notebook != null) {
            queryEverNote();
        } else {
            mActivity.cancelAnim();
            App.showToast("没有为您找到同步的图书信息...");
        }
    }

    /**
     * 找到笔记时回调
     *
     * @param noteRefList 笔记列表
     */
    @TaskResult(id = QUERY_NOTE_BOOK_ID)
    public void onFindNotes(List<NoteRef> noteRefList) {
        for (NoteRef ref : noteRefList) {
            if ("私人藏书图书信息同步".equals(ref.getTitle())) {
                new GetNoteHtmlTask(ref).start(getActivity(), "html");
            }
        }
    }

    /**
     * 查询到笔记内容时回调
     *
     * @param html 页面
     * @param task 对应任务
     */
    @TaskResult(id = "html")
    public void onGetNoteContentHtml(String html, GetNoteHtmlTask task) {
        String[] books = html.split("<br/>");
        //  掐头去尾
        if (books.length > 2) {
            bookToList(books);
            queryBookOneByOne();
        } else {
            mActivity.cancelAnim();
            App.showToast("没有为您找到同步的图书信息...");
        }
    }

    private List<String> bookToList(String[] books) {
        mBookInfoList = new ArrayList<>();
        for (int i = 1; i < books.length - 1; i++) {
            mBookInfoList.add(books[i]);
        }
        return mBookInfoList;
    }

    private void queryBookOneByOne() {
        for (int i = 0; i < mBookInfoList.size(); i++) {
            String isbn = mBookInfoList.get(i).split("-")[1];
            final int finalI = i;
            BookRequest.getInstance().queryBookByISBN(isbn, new OnBookResultListener<Book>() {
                @Override
                public void onSuccess(Book book) {
                    book.setCustomTag(mBookInfoList.get(finalI).split("-")[2]);
                    book.setReadState(Integer.valueOf(mBookInfoList.get(finalI).split("-")[3]));
                    BookManager.saveBook(book);
                    mBooks.add(book);

                    if (finalI == mBookInfoList.size() - 1) {
                        addTag();
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }
        mActivity.cancelAnim();
        mActivity.fabAnim(360, 0);
    }
}
