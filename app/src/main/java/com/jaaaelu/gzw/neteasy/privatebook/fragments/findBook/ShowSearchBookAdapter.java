package com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.BookDetailActivity;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.PrivaterBookHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gzw on 2017/8/14 0014.
 */

public class ShowSearchBookAdapter extends RecyclerView.Adapter<PrivaterBookHolder> {
    private List<Book> mBooks = new ArrayList<>();

    public ShowSearchBookAdapter(List<Book> books) {
        mBooks = books;
    }


    public ShowSearchBookAdapter() {
    }

    public void setBooks(List<Book> books) {
        mBooks.clear();
        mBooks.addAll(books);
        notifyDataSetChanged();
    }

    public void setBooksAndNotClear(List<Book> books) {
        mBooks.addAll(books);
        notifyDataSetChanged();
    }

    @Override
    public PrivaterBookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_book, parent, false);
        return new ShowByListHolder(view);
    }

    @Override
    public void onBindViewHolder(PrivaterBookHolder holder, int position) {
        holder.setBookInfo(mBooks.get(position));
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    private class ShowByListHolder extends com.jaaaelu.gzw.neteasy.privatebook.fragments.myBook.ShowByListHolder {

        ShowByListHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }

        @Override
        protected void goBookDetailView() {
            BookDetailActivity.show(mContext, BookDetailActivity.BOOK_SEARCH_INFO_ARGS, mCurrBook);
        }
    }
}
