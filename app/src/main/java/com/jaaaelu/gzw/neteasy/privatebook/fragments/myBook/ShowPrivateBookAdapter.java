package com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;

import java.util.ArrayList;
import java.util.List;

import static com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowPrivateBookAdapter.ViewType.SHOW_BY_LIST;


/**
 * Created by Gzw on 2017/8/14 0014.
 */

class ShowPrivateBookAdapter extends RecyclerView.Adapter<PrivaterBookHolder> {

    interface ViewType {
        int SHOW_BY_LIST = 0;
        int SHOW_BY_GRID = 1;
    }

    private List<Book> mBooks = new ArrayList<>();
    private int mCurrViewType = SHOW_BY_LIST;

    public ShowPrivateBookAdapter(List<Book> books) {
        mBooks = books;
    }


    public ShowPrivateBookAdapter() {
    }

    public void setBooks(List<Book> books) {
        mBooks.clear();
        mBooks.addAll(books);
        notifyDataSetChanged();
    }

    @Override
    public PrivaterBookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  不同类型显示不同界面
        if (viewType == SHOW_BY_LIST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_book, parent, false);
            return new ShowByListHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_book, parent, false);
            return new ShowByGridHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(PrivaterBookHolder holder, int position) {
        holder.setBookInfo(mBooks.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrViewType;
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    void setCurrViewType(int currViewType) {
        mCurrViewType = currViewType;
    }

    int getCurrViewType() {
        return mCurrViewType;
    }
}
