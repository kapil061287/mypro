package com.depex.odepto;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResourceClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PersonalBoardActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {



    SharedPreferences preferences;
    SwipeRefreshLayout refreshLayout;
    RecyclerView board_list;
    AlertDialog dialog;
    Button name_initials_toolbar_button;
    TextView toolbar_username_txt;
    JSONObject requestData;
    DrawerLayout drawerLayout;
    List<Board> list;
    BoardAdapter boardAdapter;
    String fullname, userToken, userid;



    View alert_view;

    Comparator<Board> boardComparator=new Comparator<Board>() {
        @Override
        public int compare(Board board, Board t1) {
            return board.getBoardTitle().compareTo(t1.getBoardTitle());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_personal_board_activity);
        board_list=findViewById(R.id.board_list);
        preferences =getSharedPreferences("odepto_pref", MODE_PRIVATE);






        //Room.inMemoryDatabaseBuilder(this, )
        fullname=preferences.getString("fullname", "0");
        userToken=preferences.getString("userToken", "0");
        userid=preferences.getString("userid", "0");
        drawerLayout=findViewById(R.id.drawer_personal_board_activity);
        name_initials_toolbar_button=findViewById(R.id.name_initials_toolbar_button);
        toolbar_username_txt=findViewById(R.id.toolbar_username_txt);
        toolbar_username_txt.setText(fullname);

        name_initials_toolbar_button.setText(Utility.getInitialsFromName(fullname));
        //GradientDrawable drawable= (GradientDrawable) name_initials_toolbar_button.getBackground();
        //drawable.setColor(Color.parseColor("#ffffffff"));
        requestData=new JSONObject();


        try{
           JSONObject data=new JSONObject();
            data.put("requestType", "user_board");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", userToken);
            data.put("user_id", userid);
            data.put("page_no", "");
            requestData.put("RequestData", data);
            sendHttpRequest(requestData, Utility.apiUrl);
            Log.i("volleyJsonCreate", requestData.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        //toolbar.setLogo(getLogoImage());

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setLogo(null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setSubtitle("");
        refreshLayout=findViewById(R.id.swip_view);
        refreshLayout.setOnRefreshListener(this);

/**
 * Floating button for create board in this project.
 */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showDialog();
            }
        });
    }
    /**
     * Show dialog to create a board in this project.
     */
    private void showDialog() {
        alert_view=getLayoutInflater().inflate(R.layout.create_board_layout,null, false);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(alert_view);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextInputLayout board_create_txt=alert_view.findViewById(R.id.board_create_txt);
                try{
                            JSONObject requestData=new JSONObject();
                            JSONObject data=new JSONObject();
                            data.put("requestType", "create_board");
                            data.put("v_code", getString(R.string.api_ver));
                            data.put("apikey", getString(R.string.api_key));
                            data.put("userToken", userToken);
                            data.put("user_id", userid);
                            data.put("team_id", "0");
                            data.put("board_title", board_create_txt.getEditText().getText().toString());
                            requestData.put("RequestData", data);
                            Log.i("volleyJsonLog", requestData.toString());
                            sendHttpRequestForCreateBoard(requestData, Utility.apiUrl);
                }catch(Exception e){

                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        dialog=builder.create();
        dialog.show();
    }


    /**
     * Send http Request for create  new board.
     */
    private void sendHttpRequestForCreateBoard(final JSONObject requestData, final String registerUrl) {

        RequestQueue queue=Volley.newRequestQueue(this);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, registerUrl, requestData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("volleyJsonCreateCard", response.toString());
                try {
                    boolean b=response.getBoolean("successBool");
                    if(b){
                        Toast.makeText(PersonalBoardActivity.this, "Board is craeted successfully", Toast.LENGTH_LONG).show();
                        JSONObject responseData=response.getJSONObject("response");
                        JSONObject boardData=responseData.getJSONObject("boardData");
                        Board board=new Board();
                        board.setBoardUrl(boardData.getString("board_url"));
                        board.setBoardKey(boardData.getString("board_key"));
                        board.setTeamId(boardData.getString("team_id"));
                        board.setBoardVisibility(boardData.getString("board_visibility"));
                        board.setBoardType1(boardData.getString("BoardType"));
                        board.setBoardTitle(boardData.getString("board_title"));
                        board.setBoardId(boardData.getString("board_id"));
                        board.setBoardStar(boardData.getString("board_star"));
                        board.setBoardType2(boardData.getString("board_type"));
                        list.add(board);
                        Collections.sort(list, boardComparator);
                       boardAdapter.notifyItemInserted(list.size()-1);

                        //{"successBool":true,
                        // "responseType":"create_board","successCode":"200",
                        // "response":{"message":"board created successfully",
                        // "boardData":{"board_url":"s","board_key":"g09rfz",
                        // "team_id":"0","board_visibility":"0","BoardType":"0",
                        // "board_title":"S","board_star":0,"board_id":6,"board_type":"PB"}},
                        // "ErrorObj":{"ErrorCode":"","ErrorMsg":""}}
                        //sendHttpRequest(PersonalBoardActivity.this.requestData, Utility.apiUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volleyjsonError", error.toString());
            }
        });
        queue.add(request);
    }


    /**
     *Logo Icon for toolbar
     */


    /**
     * On Refresh for swipe layout
     */
    @Override
    public void onRefresh() {
        Log.i("volleyJsonLog", "Refreshing.. ");
            sendHttpRequest(requestData, Utility.apiUrl);
    }


    /**
     *
     * send HttpRequest for Boards
     *
     */
    private void sendHttpRequest(JSONObject object, String url){
        RequestQueue queue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("volleyJsonLog", response.toString());
                if(refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
                try {

                    if(!response.getBoolean("successBool")){
                        JSONObject errorObj=response.getJSONObject("ErrorObj");
                        String errorCode=errorObj.getString("ErrorCode");
                        String errorMsg=errorObj.getString("ErrorMsg");
                        if("105".equalsIgnoreCase(errorCode)){
                            Toast.makeText(PersonalBoardActivity.this, errorMsg , Toast.LENGTH_LONG ).show();
                            preferences.edit().clear().apply();
                            Intent intent=new Intent(PersonalBoardActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        return;
                    }
                    JSONObject requestData=response.getJSONObject("response");
                    JSONArray boardArr=requestData.getJSONArray("userBoard");
                    list=new ArrayList<>();
                    for(int i=0;i<boardArr.length();i++){
                        JSONObject jsonBoard=boardArr.getJSONObject(i);
                        Board board=new Board();
                        board.setBoardUrl(jsonBoard.getString("board_url"));
                        board.setBoardKey(jsonBoard.getString("board_key"));
                        board.setTeamId(jsonBoard.getString("team_id"));
                        board.setBoardVisibility(jsonBoard.getString("board_visibility"));
                        board.setBoardType1(jsonBoard.getString("BoardType"));
                        board.setBoardTitle(jsonBoard.getString("board_title"));
                        board.setBoardId(jsonBoard.getString("board_id"));
                        board.setBoardStar(jsonBoard.getString("board_star"));
                        board.setBoardType2(jsonBoard.getString("board_type"));
                       //odaptoDatabaseHelper.getSimpleDataDao().create(board);
                        list.add(board);
                    }
                    Collections.sort(list, boardComparator);
                    Utility.boards=list;
                    boardAdapter=new BoardAdapter(list);
                    board_list.setLayoutManager(new LinearLayoutManager(PersonalBoardActivity.this));
                    board_list.setAdapter(boardAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyJsonError", error.toString());
            }
        });
        //jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }


    /**
     * Board View Holder for Board Recyclerview
     */
    private class BoardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView boardTitleText;
        ImageView boardImage;
        CardView board_list_cardview;
        ImageView sync_img;

        public BoardViewHolder(View itemView) {
            super(itemView);
            this.boardTitleText=itemView.findViewById(R.id.board_title_txt);
            this.boardImage=itemView.findViewById(R.id.personal_board_img);
            this.board_list_cardview=itemView.findViewById(R.id.board_list_cardview);
            sync_img=itemView.findViewById(R.id.img_sync);
            board_list_cardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.board_list_cardview:
                    CardView list_cardview= (CardView) view;
                    list_cardview.setCardElevation(7.f);
                    int position=getAdapterPosition();
                    Board board=PersonalBoardActivity.this.list.get(position);
                    String boardid=board.getBoardId();
                    String boardTitle=board.getBoardTitle();
                    Bundle bundle=new Bundle();
                    bundle.putString("board_id", boardid);
                    bundle.putString("board_title", boardTitle);
                    Intent intent=new Intent(PersonalBoardActivity.this, BoardListActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    //Intent
                    break;
            }
        }
    }


    /**
     * Board Adapter for Board recyclerview
     */
    private class BoardAdapter extends RecyclerView.Adapter<BoardViewHolder>{

        List<Board> list;
        BoardAdapter(List<Board> list){
            this.list=list;
        }

        @Override
        public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=getLayoutInflater().inflate(R.layout.board_list_layout, parent, false);
            return new BoardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BoardViewHolder holder, int position) {
            Board board=list.get(position);
            String title=board.getBoardTitle();
            holder.boardTitleText.setText(title);

            Animation animation=AnimationUtils.loadAnimation(PersonalBoardActivity.this, R.anim.my_sync_anim);
            //Animation animation1=new RotateAnimation(0.0f, 90.0f);
            holder.sync_img.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


            //animation.start();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }







    /**
     * On Create Option Menu for Shortcut keys
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personal_board_menu, menu);
        return true;
    }





    /**
     * On Back Pressed Overried for navigation drawer...
     */
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }else{
            super.onBackPressed();
        }
    }

    /**
     *
     * Optoins menu selected listener.
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_menu:
                    Intent intent=new Intent(this, SearchActivity.class);
                    startActivity(intent);
                break;
            case R.id.help_menu:
                break;
            case R.id.notification_menu:
               drawerLayout=findViewById(R.id.drawer_personal_board_activity);
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }else
                    drawerLayout.openDrawer(Gravity.RIGHT);

                break;
            case R.id.my_card_menu:
                break;
            case R.id.settings_menu:
                break;
        }
        return true;
    }


}