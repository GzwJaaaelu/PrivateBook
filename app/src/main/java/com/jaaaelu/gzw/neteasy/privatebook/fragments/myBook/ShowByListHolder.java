package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;

import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;

import butterknife.BindView;

import static com.jaaaelu.gzw.neteasy.common.tools.UiTool.dealEmptyData;

/**
 * Created by Gzw on 2017/8/14 0014.
 */

public class ShowByListHolder extends PrivaterBookHolder {
    @BindView(R.id.tv_book_description)
    TextView mBookDescription;

    protected ShowByListHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setBookInfo(Book book) {
        super.setBookInfo(book);
        dealEmptyData(mBookDescription, book.getAuthorStr(), "作者: ");
        Glide.with(mContext)
                .load(book.getImage())
                .into(mBookImage);
    }
}
