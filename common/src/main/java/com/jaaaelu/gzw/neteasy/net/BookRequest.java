package com.jaaaelu.gzw.neteasy.net;

import android.support.annotation.NonNull;

import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.model.BookNote;
import com.jaaaelu.gzw.neteasy.model.Books;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Gzw on 2017/8/23 0023.
 */

public class BookRequest {
    private static volatile BookRequest sInstance;
    private BookService mBookService;
    private static final int SEARCH_START_INDEX = 0;
    private static final int SEARCH_TOTAL_COUNT = 100;

    private BookRequest() {
        mBookService = Network.getBookService();
    }

    public static BookRequest getInstance() {
        if (sInstance == null) {
            synchronized (BookRequest.class) {
                if (sInstance == null) {
                    sInstance = new BookRequest();
                }
            }
        }
        return sInstance;
    }

    public void queryBookByISBN(String isbn, final OnBookResultListener<Book> listener) {
        mBookService.queryBookByISBN(isbn).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Book> call, @NonNull Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void queryBookByKeyWord(String keyWord, final OnBookResultListener<Books> listener) {
        mBookService.queryBookByKeyWord(keyWord, SEARCH_START_INDEX , SEARCH_TOTAL_COUNT).enqueue(new Callback<Books>() {
            @Override
            public void onResponse(@NonNull Call<Books> call, @NonNull Response<Books> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Books> call, @NonNull Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void queryBookNote(String bookId, final OnBookResultListener<BookNote> listener) {
        mBookService.queryBookNote(bookId).enqueue(new Callback<BookNote>() {
            @Override
            public void onResponse(@NonNull Call<BookNote> call, @NonNull Response<BookNote> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookNote> call, @NonNull Throwable t) {
                listener.onFailure(t);
            }
        });
    }
}
