package com.depex.odepto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class BoardListActivity2 extends AppCompatActivity implements OnVolleySuccessListener {

    SharedPreferences preferences;
    String userid;
    String userToken;
    String board_id;
    ListsLinearLayout listsLinearLayout;
    String board_title;
    String fullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list2);


        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        userid=preferences.getString("userid", "0");
        fullname=preferences.getString("fullname", "0");
        userToken=preferences.getString("userToken","0");
        listsLinearLayout=findViewById(R.id.listLinearLayoutBoard);
        Bundle bundle=getIntent().getExtras();
        /*Bundle bundle=new Bundle();
        bundle.putString("board_id", boardid);
        bundle.putString("board_title", boardTitle);*/
        board_id=bundle.getString("board_id");
        board_title=bundle.getString("board_title");

        View view1=LayoutInflater.from(this).inflate(R.layout.list_cards_layout, listsLinearLayout,false);
        View view2=LayoutInflater.from(this).inflate(R.layout.list_cards_layout, listsLinearLayout,false);
        View view3=LayoutInflater.from(this).inflate(R.layout.list_cards_layout, listsLinearLayout,false);
        View view4=LayoutInflater.from(this).inflate(R.layout.list_cards_layout, listsLinearLayout,false);



        listsLinearLayout.addView(view1);
        listsLinearLayout.addView(view2);
        listsLinearLayout.addView(view3);
        listsLinearLayout.addView(view4);



        //createBoardList();




    }

    private void createBoardList() {
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String id=bundle.getString("board_id");
        board_title=bundle.getString("board_title");
        //toolbar.setTitle(board_title);
        board_id=id;
        JSONObject requestData=new JSONObject();
        try{
            JSONObject data=new JSONObject();
            data.put("requestType",     "list_board");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", userToken);
            data.put("user_id",userid);
            data.put("board_id", id);
            requestData.put("RequestData", data);
            //sendHttpRequestGettingBoardList(requestData, Utility.apiUrl);
            Utility.getJsonFromHttp(this, requestData, this, Utility.apiUrl);

        }catch (Exception e){
            e.printStackTrace();
        }
        //Toast.makeText(this, id, Toast.LENGTH_LONG).show();
    }

    List<BoardList> boardLists=new ArrayList<>();

    @Override
    public void onSuccess(String s, Object... objects) {

        try {
            JSONObject jsonObject=new JSONObject(s);
            boolean success=jsonObject.getBoolean("successBool");
            if(success){
                JSONObject res=jsonObject.getJSONObject("response");
                String board_id=res.getString("board_id");

                JSONArray boardListArr=res.getJSONArray("boardList");
                for(int i=0;i<boardListArr.length();i++){
                    JSONObject jsonObject1=boardListArr.getJSONObject(i);
                    String list_id=jsonObject1.getString("list_id");
                    String listTitle=jsonObject1.getString("list_title");
                    BoardList boardList=new BoardList();
                    boardList.setId(list_id);
                    boardList.setTitle(listTitle);
                    boardLists.add(boardList);
                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onError(VolleyError error) {

    }
}
