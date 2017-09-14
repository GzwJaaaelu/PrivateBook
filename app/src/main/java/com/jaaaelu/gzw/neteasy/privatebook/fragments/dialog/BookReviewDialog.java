package com.jaaaelu.gzw.neteasy.privatebook.fragments.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.jaaaelu.gzw.neteasy.common.tools.UiTool;
import com.jaaaelu.gzw.neteasy.model.BookNote;
import com.jaaaelu.gzw.neteasy.net.BookRequest;
import com.jaaaelu.gzw.neteasy.net.OnBookResultListener;
import com.jaaaelu.gzw.neteasy.privatebook.App;
import com.jaaaelu.gzw.neteasy.privatebook.R;

/**
 * Created by Gzw on 2017/9/14 0014.
 */

public class BookReviewDialog extends BottomSheetDialogFragment implements OnBookResultListener<BookNote> {
    private static final String BOOK_ID = "book_id";
    private RecyclerView mBookReview;
    private BookNote mReviewData;
    private BookReviewAdapter mAdapter;
    private Toolbar mToolbar;

    public BookReviewDialog() {
        // Required empty public constructor
    }

    public static BookReviewDialog newInstance(String bookId) {

        Bundle args = new Bundle();
        args.putString(BOOK_ID, bookId);
        BookReviewDialog fragment = new BookReviewDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_book_review, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mBookReview = (RecyclerView) view.findViewById(R.id.rv_book_review);
        mBookReview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new BookReviewAdapter();
        mBookReview.setAdapter(mAdapter);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  去查找是否有图书的笔记
        BookRequest.getInstance().queryBookNote(getArguments().getString(BOOK_ID, ""), this);
    }

    /**
     * 为了解决顶部导航栏变黑
     */
    private static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Window window = getWindow();
            if (window == null) {
                return;
            }
            //  得到屏幕高度
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            //  得到状态栏的高度
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());
            //  计算 Dialog 高度
            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        }
    }


    @Override
    public void onSuccess(BookNote bookNote) {
        mReviewData = bookNote;
        if (mReviewData != null
                && mReviewData.getReviews() != null
                && !mReviewData.getReviews().isEmpty()) {
            mAdapter.setReviews(mReviewData.getReviews());
            mToolbar.setTitle("评论");
        } else {
            dialogDismiss();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        dialogDismiss();
        t.printStackTrace();
    }

    private void dialogDismiss() {
        App.sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                App.showToast("当前图书还没有人评论过...");
                dismiss();
            }
        }, 1200);
    }
}
