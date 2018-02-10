package com.depex.odepto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;


public class OCardView extends CardView {
    private Drawable mForgroundDrawable;
    public OCardView(@NonNull Context context) {
        this(context, null);
        if(mForgroundDrawable!=null){
            setForeground(mForgroundDrawable);
        }
    }

    public OCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void setForeground(Drawable foreground) {
        if(Build.VERSION.SDK_INT<21){
            if(getPaddingLeft()==0){
                mForgroundDrawable=foreground;
                return;
            }else {
                foreground=new InsetDrawable(foreground, getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
            }
        }
        super.setForeground(foreground);
    }
}
