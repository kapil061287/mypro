package com.depex.odepto;

import android.view.View;

import com.android.volley.VolleyError;

/**
 * @author Kapil Choudhary
 */

public interface OnVolleySuccessListener {
    /**
     * This is usded for volley api when response get from volley Response.Listener interface in onResponse method.
     * @param s
     * @param objects
     */
    void onSuccess(String s, Object... objects);
   void onError(VolleyError error);
}
