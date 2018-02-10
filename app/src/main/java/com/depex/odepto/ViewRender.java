package com.depex.odepto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public abstract class ViewRender<T> {
    private Context context;
    private int layoutRes;

    public ViewRender(Context context, int layoutRes) {
        this.context = context;
        this.layoutRes = layoutRes;
    }

    public abstract void bindView(View v, T data);

    public View newView(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layoutRes, parent, false);
    }

    public View getView(ViewGroup parent, T data) {
        View view = newView(parent);
        bindView(view, data);
        return view;
    }
}