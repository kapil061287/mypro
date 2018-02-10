package com.depex.odepto;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class AnimationLinearLayout extends LinearLayout {

    private ValueAnimator valueAnimator;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private int mCurrentBotton;
    private int mCurrentLeft;
    private int mCurrentRight;
    private int mCurrentTop;
    private OnLayoutChangeListener onLayoutChangeListener;
    private boolean skipNextAnimation;

    public AnimationLinearLayout(Context context) {
        this(context, null);
    }

    public AnimationLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animatorUpdateListener=new CustAnimupdateListener();
        onLayoutChangeListener=new LayoutChangeListener();
        addOnLayoutChangeListener(onLayoutChangeListener);
    }


    public void skipNextAnimation() {
        skipNextAnimation=true;
    }

    class LayoutChangeListener implements OnLayoutChangeListener {
        @Override
        public void onLayoutChange(final View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if(oldBottom==0 && oldTop==0 && oldRight==0 && oldLeft==0){
                mCurrentLeft=AnimationLinearLayout.this.getLeft();
                mCurrentBotton=AnimationLinearLayout.this.getBottom();
                mCurrentRight=AnimationLinearLayout.this.getRight();
                mCurrentTop=AnimationLinearLayout.this.getTop();
            }else if(skipNextAnimation){
                    skipNextAnimation=false;
            }else {
                    List<PropertyValuesHolder> pvhs=new ArrayList<>();
                    int largestDiff=0;
                    boolean isAnimating=AnimationLinearLayout.this.valueAnimator!=null && AnimationLinearLayout.this.valueAnimator.isRunning();
                    if(isAnimating){
                        valueAnimator.cancel();
                    }else{
                        mCurrentLeft=oldLeft;
                        mCurrentTop=oldTop;
                        mCurrentRight=oldRight;
                        mCurrentBotton=oldBottom;
                    }

                    if(mCurrentLeft!=left){
                        pvhs.add(PropertyValuesHolder.ofInt(new Property<View, Integer>(Integer.class, "left") {

                            @Override
                            public void set(View object, Integer value) {
                                view.setLeft(value.intValue());
                            }

                            @Override
                            public Integer get(View view) {
                                return Integer.valueOf(view.getLeft());
                            }
                        }, mCurrentLeft, left));
                        largestDiff=Math.max(0,Math.abs(mCurrentLeft-left));
                    }
                    if(mCurrentRight!=right){
                        pvhs.add(PropertyValuesHolder.ofInt(new Property<View, Integer>(Integer.class, "right") {

                            @Override
                            public void set(View object, Integer value) {
                                object.setRight(value.intValue());
                            }

                            @Override
                            public Integer get(View view) {
                                return Integer.valueOf(view.getRight());
                            }
                        }, mCurrentRight, right));
                        largestDiff=Math.max(0, Math.abs(mCurrentRight-right));
                    }
                    if(mCurrentBotton!=bottom){
                        pvhs.add(PropertyValuesHolder.ofInt(new Property<View, Integer>(Integer.class, "bottom") {
                            @Override
                            public void set(View object, Integer value) {
                                object.setBottom(value.intValue());
                            }

                            @Override
                            public Integer get(View view) {
                                return Integer.valueOf(view.getBottom());
                            }
                        }, mCurrentBotton, bottom));
                        largestDiff=Math.max(0, Math.abs(mCurrentBotton-bottom));
                    }
                    if(mCurrentTop!=top){
                        pvhs.add(PropertyValuesHolder.ofInt(new Property<View, Integer>(Integer.class, "top") {
                            @Override
                            public Integer get(View view) {
                                return view.getTop();
                            }

                            @Override
                            public void set(View object, Integer value) {
                                object.setTop(value.intValue());
                            }
                        }));
                    }
                valueAnimator= ObjectAnimator.ofPropertyValuesHolder(AnimationLinearLayout.this,
                        (PropertyValuesHolder[])pvhs.toArray(new PropertyValuesHolder[0]));
                    valueAnimator.setDuration(calculateTranslationDuration(getContext(), (float) largestDiff));
                    valueAnimator.addUpdateListener(animatorUpdateListener);
                    valueAnimator.start();
            }

        }
    }

    public static int calculateTranslationDuration(Context context, float translationPx) {
        return calculateTranslationDuration(context, translationPx, 1.0f);
    }

    public static int calculateTranslationDuration(Context context, float translationPx, float velocityDp) {
        return Math.abs(Math.round(translationPx / (velocityDp * context.getResources().getDisplayMetrics().density)));
    }

    public class CustAnimupdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
           mCurrentLeft=getLeft();
           mCurrentRight=getRight();
           mCurrentTop=getTop();
           mCurrentBotton=getBottom();
        }
    }
}