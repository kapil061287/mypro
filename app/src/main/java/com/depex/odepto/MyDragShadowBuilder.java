package com.depex.odepto;

import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.view.View;


public class MyDragShadowBuilder extends View.DragShadowBuilder {

    private static final float ROTATE_DEGREES = 2.0f;
    private final float mDownX;
    private final float mDownY;
    private final float mScale;
    private final int mShadowRotateOffsetX;
    private final int mShadowRotateOffsetY;
    private final int mViewHeight;
    private final int mViewWidth;

    public MyDragShadowBuilder(View view, float downX, float downY) {
        this(view, downX, downY, 1.0f);
    }

    public MyDragShadowBuilder(View view, float downX, float downY, float scale) {
        super(view);
        this.mScale = scale;
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        this.mDownX = Math.max(0.0f, downX - ((float) loc[0])) * this.mScale;
        this.mDownY = Math.max(0.0f, downY - ((float) loc[1])) * this.mScale;
        this.mViewWidth = Math.round(((float) getView().getWidth()) * this.mScale);
        this.mViewHeight = Math.round(((float) getView().getHeight()) * this.mScale);
        this.mShadowRotateOffsetX = (int) (((double) this.mViewHeight) * Math.sin(Math.toRadians(2.0d)));
        this.mShadowRotateOffsetY = (int) (((double) this.mViewWidth) * Math.sin(Math.toRadians(2.0d)));
    }

    public void onDrawShadow(Canvas canvas) {
        View view = getView();
        canvas.save();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(1, 1));
        canvas.rotate(ROTATE_DEGREES);
        canvas.translate((float) this.mShadowRotateOffsetX, 0.0f);
        canvas.scale(this.mScale, this.mScale);
        /*if (view != null) {
            View pressedView = view.findViewById(R.id.card_proper);
            if (pressedView != null) {
                pressedView.setPressed(false);
                pressedView.refreshDrawableState();
                view.setPressed(false);
                view.refreshDrawableState();
            }
            view.draw(canvas);
        }*/
        canvas.restore();
    }

    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowTouchPoint.x = ((int) this.mDownX) + this.mShadowRotateOffsetX;
        shadowTouchPoint.y = ((int) this.mDownY) + ((int) (((double) this.mDownX) * Math.sin(Math.toRadians(2.0d))));
        shadowSize.set(this.mViewWidth + this.mShadowRotateOffsetX, this.mViewHeight + this.mShadowRotateOffsetY);
    }
}
