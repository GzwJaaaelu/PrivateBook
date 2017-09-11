package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaaaelu.gzw.neteasy.common.widget.ConfirmDialogFragment;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.util.BookManager;

import butterknife.BindView;

/**
 * Created by Gzw on 2017/8/14 0014.
 */

class ShowByGridHolder extends PrivaterBookHolder {

    ShowByGridHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setBookInfo(Book book) {
        super.setBookInfo(book);
        dealEmptyData(mBookDescription, book.getPublisher(), "");
        String image = book.getImage();
        if (book.getImagesStr().contains("large")) {
            image = book.getImagesStr().split(",")[1].split("=")[1].replace('\'', ' ').trim();
        }
        Glide.with(mContext)
                .load(image)
                .into(mBookImage);
    }

}
