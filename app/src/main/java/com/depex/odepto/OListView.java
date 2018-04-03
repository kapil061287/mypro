package com.depex.odepto;

import android.content.Context;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;
import android.widget.Space;

import java.util.ArrayList;


public class OListView extends ListView {

    private Space mFooterSpaceview;
    private int mCurrentPositionOver;
    private boolean mIsCurrentListOver;
    private AdapterWratpper mAdapterWrapper;
    private int mMinimumHeight;
    private  int mScrollActivationArea;
    private ViewTreeObserver.OnPreDrawListener mSkipDrawListener;

    public OListView(Context context) {
        super(context);
    }

    public OListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCurrentPositionOver=-1;
        mIsCurrentListOver=false;
        mSkipDrawListener= new preDrawListener();
        TypedArray tr= context.obtainStyledAttributes(attrs, R.styleable.OListView);
        this.mScrollActivationArea=tr.getDimensionPixelSize(R.styleable.snappingItem_snapItems, 40);
        tr.recycle();
        mMinimumHeight=getResources().getDimensionPixelSize(R.dimen.card_list_minimum_height);
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        this.mAdapterWrapper=createAdapterWrapper(adapter);
        super.setAdapter(mAdapterWrapper);
    }

    public class preDrawListener implements ViewTreeObserver.OnPreDrawListener {

        @Override
        public boolean onPreDraw() {
            OListView.this.getViewTreeObserver().removeOnPreDrawListener(this);
            return false;
        }
    }

    public class AdapterWratpper extends HeaderViewListAdapter{

        public AdapterWratpper( ArrayList<FixedViewInfo> footerViewInfos, ListAdapter adapter) {
            super(null, footerViewInfos, adapter);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public void notifyDataSetChanged() {
            BaseAdapter adapter = (BaseAdapter) getWrappedAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view= super.getView(position, convertView, parent);
            if(view!=mFooterSpaceview){
                updateRow(view, position, true);
            }
            return view;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mMinimumHeight>getMeasuredHeight()){
            setMeasuredDimension(getMeasuredWidth(), mMinimumHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            //getViewTreeObserver().addOnPreDrawListener(mSkipDrawListener);
        }
    }

    private int pointToPositionWithHalves(float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            float translationY = child.getTranslationY();
            float top = ((float) child.getTop()) + translationY;
            if (y < top + (((((float) (child.getBottom() + getDividerHeight())) + translationY) - top) / 2.0f)) {
                return getFirstVisiblePosition() + i;
            }
        }
        return getLastVisiblePosition() + 1;
    }


    private void updateRow(View view, int position, boolean b) {

    }

    AdapterWratpper createAdapterWrapper(ListAdapter adapter){
        ArrayList<FixedViewInfo> footerViewInfo=new ArrayList(1);
        FixedViewInfo fixedViewInfo=new FixedViewInfo();
        View space= new Space(getContext());
        fixedViewInfo.view=space;
        if(mFooterSpaceview!=null)
        this.mFooterSpaceview.setLayoutParams(new ViewGroup.LayoutParams(-1,1));
        footerViewInfo.add(fixedViewInfo);
        return new AdapterWratpper(footerViewInfo, adapter);
    }
}