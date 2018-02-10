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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;


import org.json.JSONException;
import org.json.JSONObject;
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout full_name_txt, password_txt, email_txt;
    Button regiser_btn, google_sign_btn, linkedInSignIn;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        full_name_txt=findViewById(R.id.full_name_txt);
        password_txt=findViewById(R.id.password_txt);
        email_txt=findViewById(R.id.email_txt);
        regiser_btn=findViewById(R.id.register_btn);
        regiser_btn.setOnClickListener(this);
        google_sign_btn=findViewById(R.id.google_sign_in);
        google_sign_btn.setOnClickListener(this);
        linkedInSignIn=findViewById(R.id.linked_in_sign);
        linkedInSignIn.setOnClickListener(this);

        SharedPreferences preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        boolean login=preferences.getBoolean("login", false);
        if(login){
            Intent intent=new Intent(this, PersonalBoardActivity.class);
            startActivity(intent);
            finish();
        }

        GoogleSignInOptions options=new GoogleSignInOptions.Builder()
                .requestEmail()
                .requestIdToken(getString(R.string.clientID))
                .build();

        googleSignInClient= GoogleSignIn.getClient(this, options);

        auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_btn:
                    String full_name=full_name_txt.getEditText().getText().toString();
                    String email=email_txt.getEditText().getText().toString();
                    String password=password_txt.getEditText().getText().toString();
                JSONObject requestData=new JSONObject();
                try{
                    JSONObject data=new JSONObject();
                    data.put("v_code", "1.0");
                    data.put("deviceType", "android");
                    data.put("deviceID", "android-"+Utility.deviceID);
                    data.put("apikey"  , getString(R.string.api_key));
                    data.put("requestType", "user_register");
                    data.put("accessType", "False");
                    data.put("accessName", "Normal" );
                    data.put("email", email);
                    data.put("password", password);
                    data.put("fullname", full_name);
                    requestData.put("RequestData", data);
                    Log.i("volleyJsonCreated",requestData.toString());
                    setHttpRequest(requestData, Utility.apiUrl);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.google_sign_in:

                signin();

                break;

            case R.id.linked_in_sign:

                signWithLinkedin();

                break;

        }
    }
/**
 * This Method is used to google sign in.
 * */

    private void signin() {
            Intent intent=googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account=task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);


            }catch (Exception e){
                Log.e("volleyJsonErrorG", e.toString());
            }
        }

        LISessionManager.getInstance(this).onActivityResult(this, requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
            String email=account.getEmail();
            String fullname=account.getDisplayName();
            sendHttpRequestForSocial(email, fullname);
            Toast.makeText(this, email+"  "+fullname, Toast.LENGTH_LONG).show();

    }

    private void sendHttpRequestForSocial(String email, final String fullname){
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try{
            data.put("v_code", getString(R.string.api_ver));
            data.put("deviceType", "android");
            data.put("deviceID", "82150528-23LG-4622-B303-68B4572F9305");
            data.put("apikey", getString(R.string.api_key));
            data.put("requestType", "user_register");
            data.put("accessType", "False");
            data.put("accessName", "Normal");
            data.put("email", email);
            data.put("fullname", fullname);
            data.put("singupWith", "google");
            requestData.put("RequestData", data);
            RequestQueue queue=Volley.newRequestQueue(RegisterActivity.this);
            JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.POST, Utility.apiUrl, requestData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                        Log.i("volleyJsonLog", response.toString() );
                    try {
                        boolean b=response.getBoolean("successBool");
                        if(b){
                            JSONObject object=response.getJSONObject("response");
                            String token=object.getString("userToken");
                            String userid=object.getString("user_id");
                            //Toast.makeText(RegisterActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                            SharedPreferences preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
                            preferences.edit()
                                    .putString("fullname",   fullname)
                                    .putBoolean("login", true)
                                    .putString("userToken", token)
                                    .putString("userid", userid).commit();
                            Intent intent=new Intent(RegisterActivity.this, PersonalBoardActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            JSONObject object=response.getJSONObject("ErrorObj");
                            Toast.makeText(RegisterActivity.this, object.getString("ErrorMsg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(objectRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setHttpRequest(JSONObject object, String url){

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("volleyJsonResp", response.toString());

                try {
                    String success=response.getString("successBool");
                    boolean b=Boolean.parseBoolean(success);
                    JSONObject responseObj=response.getJSONObject("response");
                    String msg=responseObj.getString("message");
                    showMsg(msg);

                    if(b){

                     /*   {"successBool":true,"successCode":"200",
                     "response":{
                     "message":"Successfully Registered. Please check your email to verify your account"
                     },
                     "ErrorObj":
                     {
                     "ErrorCode":"",
                     "ErrorMsg":""
                     }}
                      */

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.e("volleyJsonError", error.toString());
            }
        });
        requestQueue.add(request);
    }

    private void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    /**
     * Sign in with Linked in process
     */
    private void signWithLinkedin(){
        LISessionManager manager=LISessionManager.getInstance(this);

        manager.init(this, Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                String url="https://api.linkedin.com/v1/people/~:(email-address,last-name,first-name)";
                final APIHelper apiHelper= APIHelper.getInstance(RegisterActivity.this);
                apiHelper.getRequest(RegisterActivity.this, url, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse) {
                        //Toast.makeText(RegisterActivity.this, apiResponse.getResponseDataAsJson().toString(), Toast.LENGTH_LONG).show();
                        Log.i("volleyJsonLog", apiResponse.getResponseDataAsJson().toString());

                        try{
                            JSONObject jsonObject=apiResponse.getResponseDataAsJson();
                            String emailAddress=jsonObject.getString("emailAddress");
                            String name=jsonObject.getString("firstName")+" "+jsonObject.getString("lastName");
                            sendHttpRequestForSocial(emailAddress, name);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onApiError(LIApiError LIApiError) {
                        Toast.makeText(RegisterActivity.this, LIApiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);
        LISession session=manager.getSession();
        AccessToken token=session.getAccessToken();
        manager.init(token);
    }
}