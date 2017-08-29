package com.jaaaelu.gzw.neteasy.util;

import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.model.Book_Table;
import com.jaaaelu.gzw.neteasy.model.HistorySearchInfo;
import com.jaaaelu.gzw.neteasy.model.HistorySearchInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

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
}
