package com.jaaaelu.gzw.neteasy.net;

import com.jaaaelu.gzw.neteasy.model.Book;

/**
 * Created by Gzw on 2017/8/23 0023.
 */

public interface OnBookResultListener<T> {

    void onSuccess(T t);

    void onFailure(Throwable t);
}
