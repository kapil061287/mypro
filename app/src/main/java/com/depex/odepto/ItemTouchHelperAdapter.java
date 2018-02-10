package com.depex.odepto;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosistion);
    void onItemDismiss(int position);
}
