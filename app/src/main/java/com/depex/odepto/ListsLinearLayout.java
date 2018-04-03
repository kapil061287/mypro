package com.depex.odepto;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.LinearLayout;


public class ListsLinearLayout extends LinearLayout {

private  boolean scrollSilently;

    public ListsLinearLayout(Context context) {
        this(context, null);
    }

    public ListsLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListsLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPivotX(0.f);
    }

    public int getChildWidth(){
        MarginLayoutParams params=(MarginLayoutParams) getChildAt(0).getLayoutParams();
        return (getChildAt(0)).getWidth()+params.leftMargin+ params.rightMargin;
    }


    public void setScrollSilently(boolean scrollSilently) {
        this.scrollSilently = scrollSilently;
    }
}
