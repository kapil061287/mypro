package com.depex.odepto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


public class SnappingHorizontalScrollView extends HorizontalScrollView implements View.OnTouchListener {
    public boolean snapItem;
    public  float currentDensity;
    private static final int SWIPE_PAGE_ON_FACTOR = 10;
    boolean mStart;
    private  float mPrevScrollX;
    private int mActiveItem;
    private boolean mFlingCallInProgress = false;
    public int horizontalScrollRegion;
    private boolean mScrollable = true;
    private PublishSubject<Integer> mScrollPositionSubject=PublishSubject.create();
    private int mItemWidth = getResources().getDimensionPixelOffset(R.dimen.list_width);
    private Set<IScrollListener> mScrollListener=new HashSet<>();
    private boolean mTravelledMinimumDistanceForHorizontalScroll;
    public SnappingHorizontalScrollView(Context context) {
        super(context);
    }



    public interface IScrollListener {
        void onScroll(int i, int i2, int i3, int i4, int i5);
    }

    public SnappingHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array=getContext().obtainStyledAttributes(attrs, R.styleable.snappingItem);
        snapItem =array.getBoolean(R.styleable.snappingItem_snapItems, false);
        currentDensity=getResources().getDisplayMetrics().density;
        horizontalScrollRegion=getResources().getDimensionPixelSize(R.dimen.horizontal_scroll_region);
        setOnTouchListener(this);
    }

    public SnappingHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(!mScrollable){
            return false;
        }
        if(!snapItem){
            return false;
        }
        int x= (int) motionEvent.getRawX();
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP:
                mStart=true;
                int minFactor=this.mItemWidth/10;
                if(this.mPrevScrollX-((float)x)>((float)minFactor)){
                    if(mActiveItem<getMaxItemCount()-1){
                        mActiveItem++;
                    }
                }else if(((float) x) - this.mPrevScrollX > ((float) minFactor) && this.mActiveItem > 0){
                    mActiveItem--;
                }
                scrollToActiveItem();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(mStart){
                    mPrevScrollX=(float)x;
                    mStart=false;
                }
                return false;
                default:
                    return false;
        }
    }

    public Observable<Integer> getScrollPositionObservable(){
        return this.mScrollPositionSubject;
    }

    private int getMaxItemCount(){
        return ((LinearLayout)getChildAt(0)).getChildCount();
    }

    private LinearLayout getLinearLayout(){
        return (LinearLayout)getChildAt(0);
    }

    private void centerCurrentItem(){
        if(getMaxItemCount()==0){
            this.mActiveItem=0;

        }
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed && this.snapItem){
            centerCurrentItem();
        }
    }

    private  void scrollToActiveItem(){
        int maxItemCount=getMaxItemCount();
        if(maxItemCount==0){
            scrollTo(0,0);
        }
        int targetItem=Math.max(0, Math.min(maxItemCount-1, mActiveItem));
        mActiveItem=targetItem;
        super.smoothScrollTo(getTargetScroll(targetItem, getLinearLayout().getScaleX()),0);
    }

    public int getTargetScroll(int targetPosition, float scaleX){
        return getTargetScroll(targetPosition, scaleX, false);
    }

    public int getTargetScroll(int targetPosition, float scaleX , boolean fillEmpty){
        View targetView=getLinearLayout().getChildAt(targetPosition );
        while(targetView==null){
            if(targetPosition==0) {
                return 0;
            }
            targetPosition--;
            targetView=getLinearLayout().getChildAt(targetPosition);
        }
        int targetLeft=targetView.getLeft();
        float childWidth=((float)(targetView.getRight()-targetLeft))*scaleX;
        float subtractWidthAmount=0.f;
        if(fillEmpty && targetPosition>0){
            subtractWidthAmount=(((float)(targetLeft-getLinearLayout().getChildAt(targetPosition-1).getRight()))+childWidth)* 0.5f;
        }
        return Math.round(((((float) targetLeft) * scaleX) - ((((float) ((getWidth() - getPaddingLeft()) - getPaddingRight())) - childWidth) / 2.0f)) - subtractWidthAmount);
    }
    public void addScrollListener(IScrollListener iScrollListener){
        this.mScrollListener.add(iScrollListener);
    }
    public void removeScrollListener(IScrollListener iScrollListener){
        this.mScrollListener.remove(iScrollListener);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollPositionSubject.onNext(l);
        for(IScrollListener scrollListener : this.mScrollListener){
            //scrollListener.onScroll(l, oldl, getMaxScrollAmount(), );
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public int getMaxScrollAmount() {
        if(getLinearLayout().getWidth()<getWidth()){
            return 0;
        }
        return getMaxScrollAmount(getLinearLayout().getScaleX());
    }

    public int getMaxScrollAmount(float scaleX){
        return super.getMaxScrollAmount();
    }

   public void  setCurrentItemAndCenter(int currentItem){
        this.mActiveItem=currentItem;
        scrollToActiveItem();
    }

    public void setCurrentItem(int currentItem){
        mActiveItem=currentItem;
    }

    public void setSnapItem(boolean snapItem){
        this.snapItem=snapItem;
    }

    public void setHorizontalScrollable(boolean scrollable) {
        this.mScrollable = scrollable;
    }

    @Override
    public void fling(int velocityX) {
        mFlingCallInProgress=true;
        super.fling(velocityX);
        mFlingCallInProgress=false;
    }

    @Override
    public ArrayList<View> getFocusables(int direction) {
        if(mFlingCallInProgress){
            return new ArrayList<>(0);
        }
        return super.getFocusables(direction);
    }
}
