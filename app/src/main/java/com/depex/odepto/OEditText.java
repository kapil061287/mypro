package com.depex.odepto;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.SingleLineTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Created by we on 1/15/2018.
 */

public class OEditText extends AppCompatEditText {
    private  boolean singleLineWrapText;
    private OnFocusChangeListener mOnfocusChangeListener;
    private boolean isRegularTextViewWhenNotEditing;
    public class MyFocusChangeListener implements OnFocusChangeListener{

        @Override
        public void onFocusChange(View view, boolean b) {
            if(singleLineWrapText){
                setInputType(getInputType());
            }
        }
    }

    public OEditText(Context context) {
        this(context, null);
    }



    public OEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array=getContext().obtainStyledAttributes(R.styleable.OEditText);
        singleLineWrapText=array.getBoolean(R.styleable.OEditText_singleLineWrapText, false);
        isRegularTextViewWhenNotEditing=array.getBoolean(R.styleable.OEditText_isRegularTextViewWhenNotEditing, false);
        array.recycle();
        setOnFocusChangeListener(new MyFocusChangeListener());

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    public boolean isSuggestionsEnabled() {
        if(isRegularTextViewWhenNotEditing){
            return isFocused();
        }
        return super.isSuggestionsEnabled();
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return  mOnfocusChangeListener;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        this.mOnfocusChangeListener=l;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection=super.onCreateInputConnection(outAttrs);
        if(singleLineWrapText && (outAttrs.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION)!=0){

            outAttrs.imeOptions^=EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        return inputConnection;
    }



    @Override
    public void setInputType(int type) {
        if(singleLineWrapText){
            type |=InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        }
        super.setInputType(type);
        if(singleLineWrapText){
            setTransformationMethod(SingleLineTransformationMethod.getInstance());
        }
    }

    public void setEditModeEnabled(boolean enabled){
        onEditModeChanged(enabled);
        //InputType.TYPE_TEXT_FLAG_MULTI_LINE;
    }



    public void onEditModeChanged(boolean enabled){
        setCursorVisible(enabled);
        if(!isRegularTextViewWhenNotEditing){
            return;
        }
        if(enabled){
            getBackground().setAlpha(255);
            setText(getText());
            return;
        }
        getBackground().setAlpha(0);
    }
}
