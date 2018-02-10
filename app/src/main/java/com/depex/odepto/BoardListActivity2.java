package com.depex.odepto;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BoardListActivity2 extends AppCompatActivity {

    SharedPreferences preferences;
    String userid;
    String userToken;
    String fullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list2);


        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        userid=preferences.getString("userid", "0");
        fullname=preferences.getString("fullname", "0");
        userToken=preferences.getString("userToken","0");



    }
}
