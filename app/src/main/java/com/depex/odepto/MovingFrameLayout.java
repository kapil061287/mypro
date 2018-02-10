package com.depex.odepto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by we on 1/13/2018.
 */

public class MovingFrameLayout extends FrameLayout {


    private  OnLayoutChangeListener onLayoutChangeListener;


    public MovingFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public MovingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onLayoutChangeListener=new OLayoutChangeListener();
        addOnLayoutChangeListener(onLayoutChangeListener);
    }

    public class OLayoutChangeListener implements OnLayoutChangeListener{

        @Override
        public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if(oldLeft!=0 || oldRight !=0 || oldTop!=0 || oldBottom!=0){
                float largestDiff=0.f;
                if(oldTop!=top){
                    largestDiff=Math.max(0.f, Math.abs(oldTop-top)+MovingFrameLayout.this.getTranslationY());
                    MovingFrameLayout.this.setTranslationY(((float) oldTop-top)+MovingFrameLayout.this.getTranslationY());
                }
                if(oldLeft!=left){
                    largestDiff=Math.max(0.f, Math.abs(oldLeft-left)+MovingFrameLayout.this.getTranslationX());
                    MovingFrameLayout.this.setTranslationX(((float)oldLeft-left)+MovingFrameLayout.this.getTranslationX());
                }
                if(largestDiff!=0.f){
                    MovingFrameLayout.this.animate().cancel();
                    MovingFrameLayout.this.animate().translationX(0.f).translationY(0.f)
                            .setDuration((long)AnimationLinearLayout.calculateTranslationDuration(getContext(), largestDiff));
                }

            }
        }
    }
}