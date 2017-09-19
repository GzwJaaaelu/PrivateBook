package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.jaaaelu.gzw.neteasy.common.app.EventNoteBaseActivity;
import com.jaaaelu.gzw.neteasy.common.tools.UiTool;
import com.jaaaelu.gzw.neteasy.common.widget.ConfirmDialogFragment;
import com.jaaaelu.gzw.neteasy.evernote.task.CreateNewNoteTask;
import com.jaaaelu.gzw.neteasy.evernote.task.DeleteNoteTask;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.App;
import com.jaaaelu.gzw.neteasy.privatebook.MainActivity;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook.FindBookFragment;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.MyBookFragment;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.statisticsbook.BookStatisticsFragment;
import com.jaaaelu.gzw.neteasy.privatebook.helper.NavHelper;
import com.jaaaelu.gzw.neteasy.privatebook.helper.SharePreferencesHelper;
import com.jaaaelu.gzw.neteasy.util.BookManager;
import com.jaaaelu.gzw.neteasy.util.Dateutil;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.vrallev.android.task.TaskResult;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;


public class HomeActivity extends EventNoteBaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer>, EvernoteLoginFragment.ResultCallback {
    @BindView(R.id.fab_action)
    ImageButton mFab;
    private NavHelper<Integer> mNavHelper;
    private BottomNavigationView mBottomNavigation;
    private static final int DOUBLE_EXIT_TIME = 1500;
    private long mLastTime = 0;
    private Notebook mNotebook;
    private List<Book> mBooks;
    private boolean mIsLoginFormActivity = false;
    private MyBookFragment mBookFragment;
    private Animation mAnimation;
    private ObjectAnimator mAnimator;

    /**
     * 跳转到当前 Activity
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initWindows() {
        super.initWindows();
        if (!Dateutil.getDate().equals(SharePreferencesHelper.getString(SharePreferencesHelper.DATE_INFO, SharePreferencesHelper.TAG_APP_COMMON))) {
            MainActivity.show(this);
            SharePreferencesHelper.putString(SharePreferencesHelper.DATE_INFO, Dateutil.getDate(), SharePreferencesHelper.TAG_APP_COMMON);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mBottomNavigation = (BottomNavigationView) findViewById(R.id.nbv_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsLoginFormActivity = true;
                syncBook();
            }
        });
    }

    private void syncBook() {
        final ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance("是否将全部图书信息同步至印象笔记？");
        fragment.show(getSupportFragmentManager(), "");
        fragment.onConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.dismiss();
                loginEventNote();
            }
        });
    }

    private void loginEventNote() {
        if (EvernoteSession.getInstance().isLoggedIn()) {
            startAnimAndSync();
        } else {
            EvernoteSession.getInstance().authenticate(this);
        }
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if (successful) {
            if (!mIsLoginFormActivity && mBookFragment != null) {
                mBookFragment.startQuery();
            } else if (mIsLoginFormActivity) {
                startAnimAndSync();
                mIsLoginFormActivity = false;
            }
        } else {
            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAnimAndSync() {
        try {
            startAnim();
            queryBook();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSync() {
        StringBuilder content = new StringBuilder("私人藏书图书信息同步<br/>");
        for (Book book : mBooks) {
            content.append("    ")
                    .append(book.getTitle())
                    .append("-")
                    .append(book.getIsbn13())
                    .append("-")
                    .append(book.getCustomTag())
                    .append("-")
                    .append(book.getReadState())
                    .append("-")
                    .append("<a href='")
                    .append(book.getAlt())
                    .append("'>《")
                    .append(book.getTitle())
                    .append("》</a>")
                    .append("<br/>");
        }

        createNewNote("私人藏书图书信息同步", content.toString(), null);
    }

    private void queryBook() {
        BookManager.queryAllBook(new QueryTransaction.QueryResultListCallback<Book>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull List<Book> tResult) {
                mBooks = tResult;
                queryEverNoteBook();
            }
        });
    }

    @Override
    protected void onQueryNoteBookException() {
        cancelAnim();
        App.showToast("同步失败...");
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
                new DeleteNoteTask(ref).start(this);
            }
        }
        startSync();
    }

    @Override
    protected void doSomethingWhenGetNoteBook(Notebook notebook) {
        mNotebook = notebook;
        queryEverNote();
    }

    /**
     * 创建笔记
     *
     * @param title     标题
     * @param content   内容
     * @param imageData 图片数据
     */
    public void createNewNote(String title, String content, CreateNewNoteTask.ImageData imageData) {
        if (mNotebook == null) {
            cancelAnim();
            App.showToast("同步失败...");
            return;
        }
        new CreateNewNoteTask(title, content, imageData, mNotebook, null).start(this);
    }

    public void startAnim() {
        mAnimator = ObjectAnimator.ofFloat(mFab, "rotation", 359, 0);
        mAnimator.setDuration(720);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                fabAnim(360, 0);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void cancelAnim() {
        mAnimator.cancel();
    }

    /**
     * 创建笔记时回调
     *
     * @param note 创建的笔记
     */
    @TaskResult
    public void onCreateNewNote(Note note) {
        cancelAnim();
        if (note != null) {
            App.showToast("同步完成...");
        } else {
            App.showToast("同步失败...");
        }
    }

    @Override
    protected void initData() {
        super.initData();
        Log.e("HomeActivity", "initData");
        mNavHelper = new NavHelper<>(this,
                getSupportFragmentManager(),
                R.id.fl_container,
                this);

        mNavHelper.add(R.id.navigation_my_book,
                new NavHelper.Tab<>(MyBookFragment.class, R.string.title_my_book))
                .add(R.id.navigation_scan_book,
                        new NavHelper.Tab<>(FindBookFragment.class, R.string.title_find_book))
                .add(R.id.navigation_book_statistics,
                        new NavHelper.Tab<>(BookStatisticsFragment.class, R.string.title_book_statistics));

        //  从底部导航栏拿到到 Menu
        Menu menu = mBottomNavigation.getMenu();
        //  手动触发第一次点击 选中 Home
        menu.performIdentifierAction(R.id.navigation_my_book, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //  返回 True 切换换下面的被点击的导航
        //  将事件流转到工具类中
        Log.e("HomeActivity", "onNavigationItemSelected");
        return mNavHelper.performClickMenu(item.getItemId());
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> oldTab, NavHelper.Tab<Integer> newTab) {
        //  对浮动按钮进行隐藏与显示的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_my_book)) {
            //  主界面时显示
            rotation = 360;
            mBookFragment = (MyBookFragment) newTab.fragment;
        } else {
            transY = UiTool.dipToPx(getResources(), 76);
        }

        fabAnim(rotation, transY);
    }

    public void fabAnim(float rotation, float transY) {
        // 开始动画
        // 旋转，Y轴位移，弹性插值器，时间
        mFab.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastTime > DOUBLE_EXIT_TIME) {
            App.showToast("再次按返回键退出");
            mLastTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
