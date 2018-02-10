package com.depex.odepto;

import android.content.Context;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Utility {


    public static String deviceID;
    public static String siteUrl="https://www.odapto.com/";
    public static final String apiUrl =siteUrl+"api/index.php";
    public static List<Board> boards=new ArrayList<>();

    public static void setVisibility(int visibility, View... views){
        for(int i=0;i<views.length;i++ ){
            views[i].setVisibility(visibility);
        }
    }



    public static String getInitialsFromName(String name){
        String[]arr=name.split(" ");
        int i=0;
        String initials="";
        for(String s : arr) {
             initials= initials+String.valueOf(s.charAt(0)).toUpperCase();
        }
        return initials;
    }




    public static void getJsonFromHttp(final OnVolleySuccessListener volleyListener, JSONObject object, Context context, String url,   final Object... objects){
        RequestQueue queue= Volley.newRequestQueue(context);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    volleyListener.onSuccess(response.toString(), objects);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyListener.onError(error);
            }
        });
        queue.add(request);
    }
}