package com.jaaaelu.gzw.neteasy.privatebook.fragments.findBook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.jaaaelu.gzw.neteasy.model.Book;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.activities.BookDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Created by Gzw on 2017/8/14 0014.
 */

public class ShowSearchBookAdapter extends RecyclerView.Adapter<ShowSearchBookAdapter.ShowByListHolder> {
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

    @Override
    public ShowByListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_book, parent, false);
        return new ShowByListHolder(view);
    }

    @Override
    public void onBindViewHolder(ShowByListHolder holder, int position) {
        holder.setBookInfo(mBooks.get(position));
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    class ShowByListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_book_image)
        ImageView mBookImage;
        @BindView(R.id.tv_book_name)
        TextView mBookName;
        @BindView(R.id.tv_book_description)
        TextView mBookDescription;
        Context mContext;
        Book mCurrBook;


        ShowByListHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBookDetailView();
                }
            });
        }

        private void goBookDetailView() {
            Intent intent = new Intent(mContext, BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.BOOK_SEARCH_INFO_ARGS, mCurrBook);
            mContext.startActivity(intent);
        }

        void setBookInfo(Book book) {
            mCurrBook = book;
            mBookName.setText(book.getTitle());
            mBookDescription.setText("作者：" + book.getAuthorStr());
            Glide.with(mContext)
                    .load(book.getImage())
                    .into(mBookImage);
        }
    }
}
