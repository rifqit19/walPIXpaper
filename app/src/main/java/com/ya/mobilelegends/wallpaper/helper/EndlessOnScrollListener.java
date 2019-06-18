package com.ya.mobilelegends.wallpaper;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessOnScrollListener.class.getSimpleName();

    private int previousTotal = 0;
    private boolean loading = true;
    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessOnScrollListener(RecyclerView.LayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = (LinearLayoutManager) linearLayoutManager;
    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        int visibleThreshold = 0;
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

            loading = true;
            current_page++;
            onLoadMore(current_page);

            Log.e("totalItem", totalItemCount+ " ");
            Log.e("firstVisibleItem", firstVisibleItem+ " ");
            Log.e("visibleThreshold", visibleThreshold+ " ");
            Log.e("visibleItemCount", visibleItemCount+ " ");

        }
    }
    public abstract void onLoadMore(Integer current_page);
}
