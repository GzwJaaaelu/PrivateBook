package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.model.BookNote;
import com.jaaaelu.gzw.neteasy.net.BookRequest;
import com.jaaaelu.gzw.neteasy.net.OnBookResultListener;
import com.jaaaelu.gzw.neteasy.privatebook.App;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.dialog.BookReviewDialog;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.jaaaelu.gzw.neteasy.util.WeChatSDK;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jaaaelu.gzw.neteasy.common.tools.UiTool.dealEmptyData;


public class BookDetailActivity extends BaseActivity implements QueryTransaction.QueryResultCallback<Book>,
        EvernoteLoginFragment.ResultCallback {
    public static final String BOOK_LOCAL_INFO_ARGS = "book_local_info_args";
    public static final String BOOK_SEARCH_INFO_ARGS = "book_search_info_args";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_book_name)
    TextView mBookName;
    @BindView(R.id.tv_book_author)
    TextView mBookAuthor;
    @BindView(R.id.tv_book_translator)
    TextView mBookTranslator;
    @BindView(R.id.tv_book_classify)
    TextView mBookClassify;
    @BindView(R.id.tv_book_price)
    TextView mBookPrice;
    @BindView(R.id.tv_book_publisher)
    TextView mBookPublisher;
    @BindView(R.id.tv_book_year)
    TextView mBookYear;
    @BindView(R.id.tv_book_page)
    TextView mBookPage;
    @BindView(R.id.tv_book_isbn)
    TextView mBookIsbn;
    @BindView(R.id.view_split_line)
    View mViewSplitLine;
    @BindView(R.id.iv_star)
    ImageView mStar;
    @BindView(R.id.tv_collect_info)
    TextView mCollectInfo;
    @BindView(R.id.ll_collect_book)
    LinearLayout mCollectBook;
    @BindView(R.id.tv_introduction)
    TextView mIntroduction;
    @BindView(R.id.tv_book_introduction)
    TextView mBookIntroduction;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.pb_loading)
    ProgressBar mLoading;
    @BindView(R.id.tv_loading_info)
    TextView mLoadingInfo;
    @BindView(R.id.ll_loading_book_info)
    LinearLayout mLoadingBookInfo;
    @BindView(R.id.iv_book_image)
    ImageView mBookImage;
    @BindView(R.id.tv_book_rating)
    TextView mBookRating;
    @BindView(R.id.rb_ratingBar)
    RatingBar mRatingBar;
    @BindView(R.id.nsv_scroll)
    NestedScrollView mScroll;
    @BindView(R.id.cl_root_view)
    CoordinatorLayout mRootView;

    private Book mCurrBook;
    private boolean mIsCollect;
    private Bitmap mBitmap;
    private BookNote mBookReview;

    /**
     * 跳转到当前 Activity
     *
     * @param context 上下文
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, BookDetailActivity.class));
    }

    /**
     * 跳转到当前 Activity
     *
     * @param context 上下文
     */
    public static void show(Context context, String bookFrom, Book book) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(bookFrom, book);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar(mToolbar);
        enableBtn(false);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookReviewDialog dialog = BookReviewDialog.newInstance(mCurrBook.getId());
                dialog.show(getSupportFragmentManager(), "");
            }
        });
    }

    /**
     * 是否开启以下空间可用
     *
     * @param isEnable 是否可用
     */
    private void enableBtn(boolean isEnable) {
        mCollectBook.setEnabled(isEnable);
    }

    @Override
    protected void initData() {
        super.initData();
        //  获取图书数据
        //  先从搜索的查看
        mCurrBook = getIntent().getParcelableExtra(BOOK_SEARCH_INFO_ARGS);
        if (mCurrBook != null) {
            bookFormLocal(false);
            //  从搜索点进来的图书详情，再去看看本地是否也有，因为需要更新是否收藏的按钮
            BookManager.queryBookById(this, mCurrBook.getId());
            return;
        }
        //  再从本地查看
        mCurrBook = getIntent().getParcelableExtra(BOOK_LOCAL_INFO_ARGS);
        if (mCurrBook == null) {
            //  都没有获取失败
            mLoading.setVisibility(View.GONE);
            mLoadingInfo.setText("获取图书信息失败...");
        } else {
            bookFormLocal(true);
        }
    }

    /**
     * 加载图书数据和页面
     *
     * @param isFormLocal 是否来自本地
     */
    private void bookFormLocal(boolean isFormLocal) {
        mLoadingBookInfo.setVisibility(View.GONE);
        //  本地的就都是凑上的
        mIsCollect = isFormLocal;
        setBookInfo();
        enableBtn(true);
        changeCollectBtn();


    }

    /**
     * 填充图书信息
     */
    private void setBookInfo() {
        dealEmptyData(mBookName, mCurrBook.getTitle(), "");
        dealEmptyData(mBookAuthor, mCurrBook.getAuthorStr(), "作者: ");
        //  是否有译者
        if ("".equals(mCurrBook.getTranslatorStr())) {
            mBookTranslator.setVisibility(View.GONE);
        } else {
            dealEmptyData(mBookTranslator, mCurrBook.getTranslatorStr(), "译者: ");
        }
        //  是否有分类
        if (!"".equals(mCurrBook.getTagsStr())) {
            dealEmptyData(mBookClassify, mCurrBook.getTagsStr().split(",")[1].split("=")[1].replace('\'', ' ').trim(), "分类: ");
        } else {
            dealEmptyData(mBookClassify, "", "分类: ");
        }
        //  豆瓣评分
        double average = Double.valueOf(mCurrBook.getRatingStr().split(",")[2].split("=")[1].replace('\'', ' ').trim());
        if (average != 0) {
            mBookRating.setText("豆瓣评分: " + average + " 分 / " + Integer.valueOf(mCurrBook.getRatingStr().split(",")[1].split("=")[1].replace('\'', ' ').trim()) + " 人");
            mRatingBar.setRating((float) average);
            mRatingBar.setStepSize(2.0f);
        } else {
            mBookRating.setText("评价人数不足");
        }
        mBookPrice.setText("定价: ¥ " + BookManager.handleMoneyUtil(mCurrBook.getPrice()));
        dealEmptyData(mBookPublisher, mCurrBook.getPublisher(), "出版社: ");
        dealEmptyData(mBookYear, mCurrBook.getPubdate(), "出版年: ");
        dealEmptyData(mBookPage, mCurrBook.getPages(), "页数: ");
        dealEmptyData(mBookIsbn, mCurrBook.getIsbn13(), "ISBN: ");
        dealEmptyData(mBookIntroduction, mCurrBook.getSummary(), "");

        String image = mCurrBook.getImage();
        //  获取图片的大图
        if (mCurrBook.getImagesStr().contains("large")) {
            image = mCurrBook.getImagesStr().split(",")[1].split("=")[1].replace('\'', ' ').trim();
        }
        Glide.with(this)
                .load(image)
                .into(mBookImage);

        //  获取图书图片的 Bitmap
        getBitmapFormUrl(image);
    }

    /**
     * Url 转为 Bitmap
     *
     * @param image 图片地址
     */
    private void getBitmapFormUrl(final String image) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mBitmap = Glide.with(BookDetailActivity.this)
                            .load(image)
                            .asBitmap() //必须
                            .centerCrop()
                            .into(500, 500)
                            .get();
                    if (mBitmap != null) {
                        measureBitmapColor();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    executorService.shutdown();
                }
            }
        });
    }

    /**
     * 对 Bitmap 进行取色
     */
    private void measureBitmapColor() {
        Palette.Builder builder = Palette.from(mBitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    changeThemeColor(swatch);
                }
            }
        });
    }

    /**
     * 修改 Toolbar 颜色
     *
     * @param swatch 对应颜色值
     */
    private void changeThemeColor(Palette.Swatch swatch) {
        mToolbar.setBackgroundColor(swatch.getRgb());
        getWindow().setStatusBarColor((swatch.getRgb()));

        mBookPrice.setTextColor(swatch.getRgb());
    }

    /**
     * 改变收藏状态
     */
    private void changeCollectBtn() {
        if (mIsCollect) {
            changeCollectBtnReally(R.drawable.btn_with_already_collect,
                    R.drawable.ic_star,
                    "已收藏",
                    R.color.colorPrimary);
        } else {
            changeCollectBtnReally(R.drawable.btn_with_flat_ripple,
                    R.drawable.ic_star_border,
                    "收藏",
                    R.color.white);
        }
    }

    /**
     * 真正的修改收藏状态的方法
     *
     * @param bgDrawable   按钮背景色
     * @param starDrawable 五角星资源
     * @param text         显示内容
     * @param textColor    显示文字的颜色
     */
    private void changeCollectBtnReally(int bgDrawable, int starDrawable, String text, int textColor) {
        mCollectBook.setBackground(ContextCompat.getDrawable(this, bgDrawable));
        Drawable drawable = ContextCompat.getDrawable(this, starDrawable);
        mStar.setImageDrawable(drawable);
        mCollectInfo.setText(text);
        mCollectInfo.setTextColor(ContextCompat.getColor(this, textColor));
    }

    @OnClick({R.id.ll_collect_book, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_collect_book:
                mIsCollect = !mIsCollect;
                changeCollectBtn();
                break;
            case R.id.fab:

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        collectBook();
    }

    /**
     * 对图书状态进行保存
     */
    private void collectBook() {
        if (mIsCollect) {
            if (getIntent().getParcelableExtra(BOOK_LOCAL_INFO_ARGS) == null) {
                BookManager.saveBook(mCurrBook);
            }
        } else {
            BookManager.deleteBook(mCurrBook);
        }
    }

    @Override
    public void onQueryResult(QueryTransaction<Book> transaction, @NonNull CursorResult<Book> tResult) {
        if (tResult.getCount() == 0) {
            return;
        }
        mIsCollect = true;
        changeCollectBtn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_book_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                ((Animatable) item.getIcon()).start();
                WeChatSDK.shardText("我觉得《" + mCurrBook.getTitle() + "》不错，分享给你~ \n" + mCurrBook.getAlt());
                return true;
            case R.id.action_ever_note:
                //  印象笔记
                if (EvernoteSession.getInstance().isLoggedIn()) {
                    EverNoteActivity.show(this, mCurrBook);
                } else {
                    EvernoteSession.getInstance().authenticate(this);
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            EverNoteActivity.show(this, mCurrBook);
        } else {
            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
        }
    }
}
