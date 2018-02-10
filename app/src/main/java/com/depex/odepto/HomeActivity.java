package com.depex.odepto;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    Button register_click_btn, login_click_btn;

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        boolean b=preferences.getBoolean("login", false);
        if(b){

            Intent personalBoardIntent=new Intent(this, PersonalBoardActivity.class);
            finish();
            startActivity(personalBoardIntent);

        }
/*
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        1);
            }
        }
*/
        register_click_btn=findViewById(R.id.register_click_btn);
        login_click_btn=findViewById(R.id.login_click_btn);
        login_click_btn.setOnClickListener(this);
        register_click_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_click_btn:
                Intent intent=new Intent(this, LoginActivity.class);
                finish();
                startActivity(intent);

                break;
            case R.id.register_click_btn:
                Intent intent1=new Intent(this, RegisterActivity.class);
                finish();
                startActivity(intent1);
        }
    }
}
