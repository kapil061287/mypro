package com.depex.odepto;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by we on 12/18/2017.
 */

public class DragItemHelperCallback extends ItemTouchHelper.Callback {
    RecyclerView.ViewHolder viewHolder;
    public DragItemHelperCallback(BoardListActivity.BoardListViewHolder boardListViewHolder) {
        this.viewHolder=boardListViewHolder;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return 1;
    }

    @Override
    public boolean isLongPressDragEnabled() {

        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
