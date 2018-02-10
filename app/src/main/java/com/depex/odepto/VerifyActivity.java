package com.depex.odepto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout verify_txt;
    Button cancel_ver_btn, verify_btn;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        verify_btn=findViewById(R.id.verify_btn);
        verify_txt=findViewById(R.id.verify_txt);
        cancel_ver_btn=findViewById(R.id.cancel_ver_btn);
        verify_btn.setOnClickListener(this);
        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.verify_btn:
                String verify_code=verify_txt.getEditText().getText().toString();
                JSONObject data=new JSONObject();
                JSONObject requestData=new JSONObject();

                try{
                    data.put("requestType" ,"verify_code");
                    data.put("v_code", getString(R.string.api_ver));
                    data.put("apikey", getString(R.string.api_key));
                    data.put("deviceID",  "82150528-23FD-4622-B303-68B4572F9305");
                    data.put("varCode", verify_code);
                    requestData.put("RequestData", data);
                    sendHttpRequest(requestData, Utility.apiUrl);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.cancel_ver_btn:
                break;
        }
    }

    private void sendHttpRequest(JSONObject object, String url){
        RequestQueue queue= Volley.newRequestQueue(this);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("volleyJsonMsg", response.toString());

                try {
                    boolean successBool=response.getBoolean("successBool");
                    if(successBool){
                        JSONObject resData=response.getJSONObject("response");
                        String msg=resData.getString("message");
                        showMsg(msg);
                        String userid=resData.getString("user_id");
                        String fullname=resData.getString("fullname");
                        String userToken=resData.getString("userToken");
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("userid", userid);
                        editor.putString("fullname", fullname);
                        editor.putBoolean("login", true);
                        editor.putString("userToken", userToken);
                        editor.commit();
                        Intent intent=new Intent(VerifyActivity.this, PersonalBoardActivity.class);
                        startActivity(intent);
                    }

                /*{"successBool":true,
                "successCode":"200",
                 "response":
                {"message":"Your account is verified","user_id":"146",
                "fullname":"Kapil","userToken":"1559775268abee2e290f799b95a42976"},
                "ErrorObj":{"ErrorCode":"","ErrorMsg":""}}
                */
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyJsonError", error.toString());
            }
        });
        queue.add(request);
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}