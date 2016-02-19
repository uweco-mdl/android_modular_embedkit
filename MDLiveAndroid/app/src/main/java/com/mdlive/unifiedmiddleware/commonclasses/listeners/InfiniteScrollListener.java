package com.mdlive.unifiedmiddleware.commonclasses.listeners;

import android.widget.AbsListView;

/**
 * Created by dhiman_da on 6/25/2015.
 */
public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
    private int mBufferItemCount = 0;
    private int mCurrentPage = 0;
    private int mItemCount = 0;
    private boolean isLoading = true;

    public InfiniteScrollListener(int bufferItemCount) {
        this.mBufferItemCount = bufferItemCount;
    }

    public abstract void loadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Do Nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount < mItemCount) {
            this.mItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.isLoading = true;
            }
        }

        if (isLoading && (totalItemCount > mItemCount)) {
            isLoading = false;
            mItemCount = totalItemCount;
            mCurrentPage++;
        }

        if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mBufferItemCount)) {
            loadMore(mCurrentPage + 1, totalItemCount);
            isLoading = true;
        }
    }
}
