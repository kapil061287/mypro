package com.depex.odepto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;


public class CardEditText extends OEditText {
    public CardEditText(Context context) {
        this(context, null);
    }

    public CardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);    }

    public CardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode!=KeyEvent.FLAG_KEEP_TOUCH_MODE){
            return super.onKeyDown(keyCode, event);
        }
        clearFocus();
        return true;
    }
}