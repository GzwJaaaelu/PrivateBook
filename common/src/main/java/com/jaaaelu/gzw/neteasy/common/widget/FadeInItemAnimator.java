package com.netease.study.ui.itemanimator;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 8/11/16.
 */
public class FadeInItemAnimator extends SimpleItemAnimator {
    private List<ViewHolder> pendingAddViewHolderList = new ArrayList<>();
    private List<ViewHolder> addAnimationViewHolderList = new ArrayList<>();

    @Override
    public boolean animateRemove(ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(ViewHolder holder) {
        View view = holder.itemView;
        ViewCompat.setAlpha(view, 0);
        pendingAddViewHolderList.add(holder);
        return true;
    }

    @Override
    public boolean animateMove(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        for (int i = 0; i < pendingAddViewHolderList.size(); i++) {
            final ViewHolder viewHolder = pendingAddViewHolderList.get(i);
            addAnimationViewHolderList.add(viewHolder);

            View view = viewHolder.itemView;
            final ViewPropertyAnimatorCompat animator = ViewCompat.animate(view);
            animator.alpha(1).setDuration(1000).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {

                }

                @Override
                public void onAnimationEnd(View view) {
                    animator.setListener(null);
                    ViewCompat.setAlpha(view, 1);
                    dispatchAddFinished(viewHolder);
                    addAnimationViewHolderList.remove(viewHolder);

                    if (!isRunning()) {
                        dispatchAnimationsFinished();
                    }
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            }).start();
        }

        pendingAddViewHolderList.clear();
    }

    @Override
    public void endAnimation(ViewHolder item) {
        View view = item.itemView;
        ViewCompat.animate(view).cancel();

        for (int i = 0; i < pendingAddViewHolderList.size(); i++) {
            ViewHolder viewHolder = pendingAddViewHolderList.get(i);
            if (viewHolder == item) {
                pendingAddViewHolderList.remove(i);
                ViewCompat.setAlpha(view, 1);
                dispatchAddFinished(item);
            }
        }

        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    @Override
    public void endAnimations() {
        for (int i = 0; i < pendingAddViewHolderList.size(); i++) {
            ViewHolder viewHolder = pendingAddViewHolderList.get(i);
            View view = viewHolder.itemView;

            pendingAddViewHolderList.remove(i);
            ViewCompat.setAlpha(view, 1);
            dispatchAddFinished(viewHolder);
        }

        for (int i = 0; i < addAnimationViewHolderList.size(); i++) {
            ViewHolder viewHolder = addAnimationViewHolderList.get(i);
            View view = viewHolder.itemView;
            ViewCompat.animate(view).cancel();
        }
    }

    @Override
    public boolean isRunning() {
        return !pendingAddViewHolderList.isEmpty()
                || !addAnimationViewHolderList.isEmpty();
    }
}
