package com.depex.odepto.view;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


public class FixedSizeImageView extends AppCompatImageView {
    private boolean mBlockMeasurement;
    private boolean mIsFixedSize = true;

    public FixedSizeImageView(Context context) {
        super(context);
    }

    public FixedSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedSizeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageBitmap(Bitmap bm) {
        this.mBlockMeasurement = true;
        super.setImageBitmap(bm);
        this.mBlockMeasurement = false;
    }

    public void setImageDrawable(Drawable drawable) {
        this.mBlockMeasurement = true;
        super.setImageDrawable(drawable);
        this.mBlockMeasurement = false;
    }

    public void requestLayout() {
        if (!this.mBlockMeasurement || !this.mIsFixedSize) {
            super.requestLayout();
        }
    }
}