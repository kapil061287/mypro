package com.depex.odepto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;



import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by we on 12/28/2017.
 */

public class AvatarView extends FrameLayout {

    RoundedImageView  roundedImageView;
    GradientDrawable placeholderDrawable;

    public AvatarView(@NonNull Context context) {
        this(context, null);
            int dimen=context.getResources().getDimensionPixelSize(R.dimen.avatar);
            setLayoutParams(new ViewGroup.LayoutParams(dimen, dimen));
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton_fab_colorDisabled)
        placeholderDrawable.setShape(GradientDrawable.OVAL);
    }
}
