package com.jaaaelu.gzw.neteasy.net;

import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.model.Books;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 网络请求的所有接口
 * Created by admin on 2017/7/7.
 */

interface BookService {

    @GET("isbn/{isbnNumber}")
    Call<Book> queryBookByISBN(@Path("isbnNumber") String isbnNumber);

    @GET("search")
    Call<Books> queryBookByKeyWord(@Query("q") String keyWord,
                                   @Query("start") int start,
                                   @Query("count") int count);
}
