package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.jaaaelu.gzw.neteasy.common.widget.ConfirmDialogFragment;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.util.BookManager;

import butterknife.BindView;

/**
 * Created by Gzw on 2017/8/14 0014.
 */

class ShowByListHolder extends PrivaterBookHolder {
    @BindView(R.id.tv_book_description)
    TextView mBookDescription;


    ShowByListHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setBookInfo(Book book) {
        super.setBookInfo(book);
        mBookDescription.setText("作者：" + book.getAuthorStr());
        Glide.with(mContext)
                .load(book.getImage())
                .into(mBookImage);
    }
}
