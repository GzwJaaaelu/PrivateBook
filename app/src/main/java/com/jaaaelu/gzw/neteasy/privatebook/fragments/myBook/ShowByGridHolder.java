package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;

import android.view.View;

import com.bumptech.glide.Glide;
import com.jaaaelu.gzw.neteasy.model.Book;

import static com.jaaaelu.gzw.neteasy.common.tools.UiTool.dealEmptyData;

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
        //  获取大图
        String image = book.getImage();
        if (book.getImagesStr().contains("large")) {
            image = book.getImagesStr().split(",")[1].split("=")[1].replace('\'', ' ').trim();
        }
        Glide.with(mContext)
                .load(image)
                .into(mBookImage);
    }

}
