package com.depex.odepto;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout email_txt, password_txt;
    Button login_btn, linked_in_btn, google_sign_in;
    SharedPreferences preferences;

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_txt=findViewById(R.id.email_txt);
        linked_in_btn=findViewById(R.id.linked_in_btn);
        linked_in_btn.setOnClickListener(this);
        password_txt=findViewById(R.id.password_txt);
        login_btn=findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        layout=findViewById(R.id.parent_login_constraint_layout);
        google_sign_in=findViewById(R.id.google_sign_btn);
        google_sign_in.setOnClickListener(this);

        GoogleSignInOptions options=new GoogleSignInOptions.Builder()
                .requestEmail()
                .requestIdToken(getString(R.string.clientID))
                .build();

        googleSignInClient= GoogleSignIn.getClient(this, options);

        auth= FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();


        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        boolean b=preferences.getBoolean("login", false);
        if(b){
            Intent intent=new Intent(this, PersonalBoardActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                String username=email_txt.getEditText().getText().toString();
                String password=password_txt.getEditText().getText().toString();
                JSONObject requestData=new JSONObject();
                try{
                    JSONObject data=new JSONObject();
                    data.put("requestType", "user_login");
                    data.put("v_code", "1.0");
                    data.put("apikey", getString(R.string.api_key));
                    data.put("deviceType", "android");
                    data.put("deviceID", "82150528-23FD-4622-B303-68B4572F9305");
                   // data.put("deviceID", "android-"+Utility.deviceID);
                    data.put("username",username);
                    data.put("password", password);
                    requestData.put("RequestData", data);
                    Log.i("volleyJsonCreated", requestData.toString());
                    sendHttpRequest(requestData, Utility.apiUrl);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.linked_in_btn:
                signWithLinkedin();
                break;
            case R.id.google_sign_btn:
                signin();
                break;

        }
    }


    public void sendHttpRequest(JSONObject object, String url){
        RequestQueue queue= Volley.newRequestQueue(this);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("volleyJsonResp", response.toString());
                try {
                    boolean successBool=response.getBoolean("successBool");
                    JSONObject responseData;
                    if(successBool) {
                        responseData = response.getJSONObject("response");
                        String msg = responseData.getString("message");
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                        String userid = responseData.getString("user_id");
                        String fullname = responseData.getString("fullname");
                        String userToken = responseData.getString("userToken");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("userid", userid);
                        editor.putString("fullname", fullname);
                        editor.putString("userToken", userToken);
                        editor.putBoolean("login", true);
                        editor.commit();
                        Intent intent=new Intent(LoginActivity.this, PersonalBoardActivity.class );
                        startActivity(intent);
                    }else{
                        responseData=response.getJSONObject("ErrorObj");
                        String code=responseData.getString("ErrorCode");
                        if(code.equals("109")){
                            Intent intent=new Intent(LoginActivity.this, VerifyActivity.class);
                            startActivity(intent);
                        }else if(code.equals("108")){
                            String msg=responseData.getString("ErrorMsg");
                            Snackbar.make(layout, msg, Snackbar.LENGTH_INDEFINITE).setAction("OK", null).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //{
                // "successBool":true,
                // "successCode":"200",
                // "response":
                // {
                // "message":"You are successfully loggedin"
                // ,"user_id":"146",
                // "fullname":"Kapil",
                // "userToken":"89e62449cb7c1429ba5d671bcf1db891"
                // },"ErrorObj":
                // {
                // "ErrorCode":"","ErrorMsg":""
                // }}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyJsonError",error.toString());
            }
        });
        queue.add(request);
    }





    /**
     * Sign in with Linked in process
     */
    private void sendHttpRequestForSocial(String email, final String fullname){
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try{
            data.put("v_code", getString(R.string.api_ver));
            data.put("deviceType", "android");
            data.put("deviceID", "82150528-23LG-4622-B303-68B4572F9305");
            data.put("apikey", getString(R.string.api_key));
            data.put("requestType", "user_login");
            data.put("accessType", "False");
            data.put("accessName", "Normal");
            data.put("username", email);
            data.put("fullname", fullname);
            data.put("loginWith", "gmail");
            requestData.put("RequestData", data);
            Log.i("volleyJsonLog", requestData.toString());
            RequestQueue queue=Volley.newRequestQueue(LoginActivity.this);
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

                            SharedPreferences preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
                            preferences.edit()
                                    .putString("fullname",   fullname)
                                    .putBoolean("login", true)
                                    .putString("userToken", token)
                                    .putString("userid", userid).apply();
                            Intent intent=new Intent(LoginActivity.this, PersonalBoardActivity.class);
                            startActivity(intent);

                            finish();
                        }else{
                            JSONObject object=response.getJSONObject("ErrorObj");
                            Toast.makeText(LoginActivity.this, object.getString("ErrorMsg"), Toast.LENGTH_LONG).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        LISessionManager.getInstance(this).onActivityResult(this, requestCode, resultCode, data);


        if(requestCode==1 && resultCode==RESULT_OK){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{

                GoogleSignInAccount account=task.getResult(ApiException.class);

                String email=account.getEmail();
                String username=account.getDisplayName();



                firebaseAuthWithGoogle(account);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private  void firebaseAuthWithGoogle(GoogleSignInAccount account){

            String name=account.getDisplayName();
            String email=account.getEmail();
            Toast.makeText(this, name+"   "+email, Toast.LENGTH_LONG).show();
            sendHttpRequestForSocial(email, name);

    }






    /**
     * This Method is used to google sign in.
     * */

    private void signin() {
        Intent intent=googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
    }


    private void signWithLinkedin(){
        LISessionManager manager=LISessionManager.getInstance(this);

        manager.init(this, Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                String url="https://api.linkedin.com/v1/people/~:(email-address,last-name,first-name)";
                final APIHelper apiHelper= APIHelper.getInstance(LoginActivity.this);
                apiHelper.getRequest(LoginActivity.this, url, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse) {
                        //Toast.makeText(LoginActivity.this, apiResponse.getResponseDataAsJson().toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(LoginActivity.this, LIApiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);
        LISession session=manager.getSession();
        AccessToken token=session.getAccessToken();
        manager.init(token);
    }

}