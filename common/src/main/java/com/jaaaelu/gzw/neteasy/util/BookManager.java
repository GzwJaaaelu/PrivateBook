package com.jaaaelu.gzw.neteasy.util;

import android.text.TextUtils;
import android.util.Log;

import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.model.Book_Table;
import com.jaaaelu.gzw.neteasy.model.HistorySearchInfo;
import com.jaaaelu.gzw.neteasy.model.HistorySearchInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.text.DecimalFormat;

/**
 * Created by Gzw on 2017/8/25 0025.
 */

public class BookManager {
    public static boolean DataChange = false;

    public static void queryAllHistory(QueryTransaction.QueryResultListCallback<HistorySearchInfo> callback) {
        SQLite.select().from(HistorySearchInfo.class)
                .orderBy(HistorySearchInfo_Table.searchTime.desc())
                .async()
                .queryListResultCallback(callback)
                .execute();
    }

    public static void queryAllBook(QueryTransaction.QueryResultListCallback<Book> callback) {
        SQLite.select().from(Book.class)
                .orderBy(Book_Table.title.asc())
                .async()
                .queryListResultCallback(callback)
                .execute();
    }

    public static void queryBookById(QueryTransaction.QueryResultCallback<Book> callback, String id) {
        SQLite.select().from(Book.class)
                .where(Book_Table.id.eq(id))
                .async()
                .queryResultCallback(callback)
                .execute();
    }

    public static void queryHistoryByKeWord(QueryTransaction.QueryResultCallback<HistorySearchInfo> callback, String keyWord) {
        SQLite.select().from(HistorySearchInfo.class)
                .where(HistorySearchInfo_Table.keyWord.eq(keyWord))
                .async()
                .queryResultCallback(callback)
                .execute();
    }


    public static void saveBook(Book book) {
        book.saveAuthorStr();
        book.saveImagesStr();
        book.saveRatingStr();
        book.saveTagsStr();
        book.saveTranslatorStr();
        book.save();
        DataChange = true;
    }

    public static void deleteBook(Book book) {
        book.delete();
        DataChange = true;
    }

    public static double handleMoneyUtil(String price) {
        double rmbPrice = 0.0;
        Log.e("handleMoneyUtil", price);
        if (TextUtils.isEmpty(price)) {
            return rmbPrice;
        }
        if (price.contains("元")) {
            rmbPrice = Double.valueOf(price.split("元")[0]);
        } else if (price.contains("CNY")) {
            rmbPrice = Double.valueOf(price.split("Y")[1].trim());
        } else if (price.contains("NT$")) {
            rmbPrice = Double.valueOf(price.replace('$', ' ').split(" ")[price.replace('$', ' ').split(" ").length - 1]) * 0.2202;
        } else if (price.contains("USD")) {
            rmbPrice = Double.valueOf(price.split(" ")[1]) * 6.6379;
        } else if (price.contains("円")) {
            rmbPrice = Double.valueOf(price.split("円")[0]) * 0.06087;
        } else if (price.contains("HK$")) {
            rmbPrice = Double.valueOf(price.replace('$', ' ').split(" ")[1]) * 0.8437;
        } else {
            try {
                rmbPrice = Double.valueOf(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Double.valueOf(new DecimalFormat("0.00").format(rmbPrice));
    }
}
