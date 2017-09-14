package com.jaaaelu.gzw.neteasy.privatebook.fragments.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaaaelu.gzw.neteasy.model.BookNote;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.util.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gzw on 2017/9/14 0014.
 */

class BookReviewAdapter extends RecyclerView.Adapter<BookReviewAdapter.BookReviewHolder> {
    private List<BookNote.ReviewsBean> mReviews = new ArrayList<>();
    private Context mContext;

    BookReviewAdapter() {
    }


    BookReviewAdapter(List<BookNote.ReviewsBean> reviews) {
        this.mReviews = reviews;
    }

    public void setReviews(List<BookNote.ReviewsBean> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    @Override
    public BookReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_book_review, parent, false);
        return new BookReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookReviewHolder holder, int position) {
        holder.setData();
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    class BookReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_reviewer_avatar)
        ImageView mReviewerAvatar;
        @BindView(R.id.tv_reviewer_nick)
        TextView mReviewerNick;
        @BindView(R.id.tv_review_time)
        TextView mReviewTime;
        @BindView(R.id.tv_review_votes)
        TextView mReviewVotes;
        @BindView(R.id.tv_review_content)
        TextView mReviewContent;
        @BindView(R.id.rb_ratingBar)
        RatingBar mRatingBar;

        BookReviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData() {
            BookNote.ReviewsBean review = mReviews.get(getAdapterPosition());
            Glide.with(mContext)
                    .load(review.getAuthor().getLarge_avatar())
                    .transform(new GlideCircleTransform(mContext))
                    .into(mReviewerAvatar);
            mReviewerNick.setText(review.getAuthor().getName());
            mReviewTime.setText(review.getUpdated());
            mReviewVotes.setText("  " + review.getVotes());
            if (review.getSummary().contains("entityMap")) {
                mReviewContent.setText(review.getTitle());
            } else {
                mReviewContent.setText(review.getSummary());
            }
            mRatingBar.setRating(TextUtils.isEmpty(review.getRating().getValue()) ? 0.0f
                    : Float.valueOf(review.getRating().getValue()));
        }
    }
}
