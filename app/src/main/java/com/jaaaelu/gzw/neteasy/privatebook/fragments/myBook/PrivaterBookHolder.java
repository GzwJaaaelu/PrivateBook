package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaaaelu.gzw.neteasy.common.widget.ConfirmDialogFragment;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.BookDetailActivity;
import com.jaaaelu.gzw.neteasy.util.BookManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gzw on 2017/8/14 0014.
 */

class PrivaterBookHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_book_image)
    ImageView mBookImage;
    @BindView(R.id.tv_book_name)
    TextView mBookName;
    @BindView(R.id.tv_book_description)
    TextView mBookDescription;
    Context mContext;
    private Book mCurrBook;
    static OnBookListChange sListener;

    PrivaterBookHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBookDetailView();
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteBook(v);
                return true;
            }
        });
    }


    private void deleteBook(View v) {
        final ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance("是否取消收藏该图书？");
        fragment.show(((FragmentActivity)v.getContext()).getSupportFragmentManager(), "");
        fragment.onConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookManager.deleteBook(mCurrBook);
                sListener.onBookListChange();
                fragment.dismiss();
            }
        });
    }

    private void goBookDetailView() {
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.BOOK_LOCAL_INFO_ARGS, mCurrBook);
        mContext.startActivity(intent);
    }

    public void setBookInfo(Book book) {
        mCurrBook = book;
        mBookName.setText(book.getTitle());
    }
}
