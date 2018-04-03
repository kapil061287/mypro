package com.depex.odepto.screen;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;

import com.bumptech.glide.request.transition.Transition;

import com.depex.odepto.BoardList;
import com.depex.odepto.CommentActivity;
import com.depex.odepto.GlideApp;
import com.depex.odepto.R;
import com.depex.odepto.Utility;
import com.depex.odepto.adpater.ItemAdpater;
import com.depex.odepto.api.ProjectApi;

import com.depex.odepto.factory.StringConvertFactory;
import com.depex.odepto.listener.OnCardItemClickListener;
import com.depex.odepto.recent.Card;
import com.depex.odepto.view.OBoardView;
import com.google.gson.Gson;

import com.woxthebox.draglistview.BoardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BoardListActivity3 extends AppCompatActivity implements BoardView.BoardListener, OnCardItemClickListener,  NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @BindView(R.id.board_view_drag)
    OBoardView boardView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navview_board_list)
    NavigationView navigationView;

    @BindView(R.id.board_list_drawer_layout)
    DrawerLayout drawerLayout;
    private Card moveCard;

    SharedPreferences preferences;
    private String boardTitle;
    private String boardid;

    private Menu menu;

    @BindView(R.id.root_view)
    CoordinatorLayout rootView;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_board_list);
        ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_white_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoardListActivity3.super.onBackPressed();
            }
        });


        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        boardView.setSnapToColumnInLandscape(true);
        boardView.setSnapToColumnsWhenScrolling(true);
        boardView.setSnapToColumnWhenDragging(true);
        boardView.setSnapDragItemToTouch(true);
        floatingActionButton.setOnClickListener(this);
        boardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        boardView.setBoardListener(this);
        Bundle bundle=getIntent().getExtras();
        boardid=bundle.getString("board_id");
        navigationView.setNavigationItemSelectedListener(this);
        boardTitle=bundle.getString("board_title");
        String imgUrl=bundle.getString("bg_img");

        GlideApp.with(this).load(imgUrl).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

            }
        });


        getSupportActionBar().setTitle(boardTitle);
        initBoardList(boardid);
    }



    private void initBoardList(String boardId) {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("requestType", "list_board");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id", preferences.getString("userid", "0"));
            data.put("board_id", boardId);

            requestData.put("RequestData", data);
            Log.i("requestData","New Board List Activity"+ requestData.toString());
            new Retrofit.Builder()
                    .baseUrl(Utility.SITE_URL_RETROFIT)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectApi.class)
                    .getBoardList(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            String responseString=response.body();
                                Log.i("responseData", "NEw BoardList : "+response.body());
                            try {
                                JSONObject res=new JSONObject(responseString);
                                boolean success=res.getBoolean("successBool");
                                if(success){
                                    JSONObject resObj=res.getJSONObject("response");
                                    JSONArray listArr=resObj.getJSONArray("boardList");
                                    Gson gson=new Gson();
                                    BoardList[]arr=gson.fromJson(listArr.toString(), BoardList[].class);
                                    Log.i("responseData", "New BoardList with Cards : "+Arrays.toString(arr));
                                    for(int i=0;i<=arr.length;i++){
                                        if(i<arr.length) {
                                            renderCard(arr[i]);
                                        }else {
                                            if (i >= arr.length) {
                                                ArrayList<Card> cards = new ArrayList<>();
                                                //addColumn(null, cards);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e("responseDataError", "New Board Activity : "+e.toString());
                            }
                        }



                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            if(Utility.isConnectvityAvailable(BoardListActivity3.this)){
                                initBoardList(boardId);
                            }else {
                                Toast.makeText(BoardListActivity3.this, "Sorry Internet is not available !", Toast.LENGTH_LONG).show();
                            }

                                Log.e("responseDataError", "New Board list : "+t.toString());
                        }
                    });

        }catch (Exception e){
            Log.i("responseDataError", "New Board list : "+e.toString());
        }
    }


    private void renderCard(BoardList boardList) {
        List<Card> cards=boardList.getCards();
        if(cards==null) {
            cards=new ArrayList<>();
        }
        addColumn(boardList, cards);
    }


    public void addColumn(BoardList boardList, List<Card> cards){
        ItemAdpater adpater=new ItemAdpater(cards, BoardListActivity3.this, BoardListActivity3.this);

        View header= LayoutInflater.from(BoardListActivity3.this).inflate(R.layout.content_header_drag, null, false);
        TextView textView=header.findViewById(R.id.header_list_text);
        View footer=LayoutInflater.from(BoardListActivity3.this).inflate(R.layout.content_footer_drag, null, false);
        if(boardList!=null) {
            textView.setText(boardList.getTitle());
        }else {
            textView.setText("Add List");
        }

        if(boardList!=null) {
            boardView.addColumnList(adpater, header, footer, boardList, false);
        }else {
            boardView.addColumnList(adpater, header, null, null, false);
        }
        Button button=footer.findViewById(R.id.card_create_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCardCreate(boardList);
            }
        });
    }



    private void initCardCreate(BoardList boardList) {
        final View view1=getLayoutInflater().inflate(R.layout.create_card_layout, null, false);
        new AlertDialog.Builder(this)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String card_title=((TextInputLayout)view1.findViewById(R.id.create_card_txt)).getEditText().getText().toString();
                        createCard(boardList, card_title);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setTitle("Create Card")
                .setView(view1)
                .create()
                .show();

            Toast.makeText(this, "Card create ", Toast.LENGTH_LONG).show();
    }

    private void createCard(BoardList boardList, String card_title) {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("requestType", "create_card");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id", preferences.getString("userid", "0"));
            data.put("list_id", boardList.getId());
            data.put("card_title", card_title);
            requestData.put("RequestData", data);
            Log.i("requestData", "New Board Activity Create Card : "+requestData.toString());

            new Retrofit.Builder()
                    .addConverterFactory(new StringConvertFactory())
                    .baseUrl(Utility.SITE_URL_RETROFIT)
                    .build()
                    .create(ProjectApi.class)
                    .createCard(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String responseString=response.body();
                            Log.i("responseData", "New Board Activity 3 : "+responseString);
                            try {
                                JSONObject res=new JSONObject(responseString);
                                boolean success=res.getBoolean("successBool");
                                if(success){
                                        JSONObject resObj=res.getJSONObject("response");
                                        Card card=new Gson().fromJson(resObj.toString(), Card.class);
                                        Map<Integer, BoardList> map=boardView.getColumnMap();
                                        for(Map.Entry<Integer, BoardList> entry : map.entrySet()){
                                            Integer key=entry.getKey();
                                            BoardList boardList1=entry.getValue();
                                            if(boardList.equals(boardList1)){
                                                int rowIndex=boardView.getItemCount(key);
                                                boardView.addItem(key , rowIndex, card, true);
                                                break;
                                            }
                                        }
                                }
                            } catch (JSONException e) {
                                Log.e("responseDataError", "New Board Activity : "+e.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("responseData", "New Board Activity : "+t.toString());
                            if(Utility.isConnectvityAvailable(BoardListActivity3.this)) createCard(boardList, card_title);
                                else Toast.makeText(BoardListActivity3.this, "Please Check your connection !", Toast.LENGTH_LONG).show();

                        }
                    });

        }catch (Exception e){
            Log.e("responseDataError", "Board New Activity : "+e.toString());
        }
    }

    @Override
    public void onItemDragStarted(int column, int row) {
        Log.i("cardAdapterIn", "Drag Started : "+column+", "+row);
        ItemAdpater itemAdpater= (ItemAdpater) boardView.getAdapter(column);
        moveCard=itemAdpater.getCards().get(row);
    }

    @Override
    public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {

    }

    @Override
    public void onItemChangedColumn(int oldColumn, int newColumn) {

    }

    @Override
    public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
        if(fromColumn!=toColumn && moveCard!=null) {
            //Toast.makeText(this, "Columne Num : " + toColumn, Toast.LENGTH_SHORT).show();
            Log.i("columnMap", boardView.getColumnMap().toString());
            Map<Integer, BoardList> map = boardView.getColumnMap();
            BoardList boardList = map.get(toColumn);
            moveCard(moveCard, boardList);
        }else
        {
            //TODO change sequence of card in list
        }
    }


    private void moveCard(Card card, BoardList boardList) {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("requestType", "move_card");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id", preferences.getString("userid", "0"));
            data.put("list_id", boardList.getId());
            data.put("board_id", boardList.getBoardId());
            data.put("card_id", card.getCardId());
            requestData.put("RequestData", data);
            Log.i("requestData", "new Board List Move Card : "+requestData.toString());
            new Retrofit.Builder()
                    .baseUrl(Utility.SITE_URL_RETROFIT)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectApi.class)
                    .moveCard(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String responseString=response.body();
                            Log.i("responseData", "New Board Activity Move Card : "+responseString);
                            JSONObject res=new JSONObject();
                            try {
                                boolean success=res.getBoolean("successBool");
                                if(success){

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                                Log.e("responseDataError", "New Board Activity Move Card : "+t.toString());
                                if(Utility.isConnectvityAvailable(BoardListActivity3.this)){
                                    moveCard(card, boardList);
                                }
                        }
                    });
        }catch (Exception e){
            Log.e("responseDataError", "New Board List : "+e.toString());
        }
    }

    @Override
    public void onCardItemClickListener(Card card) {
            String json=new Gson().toJson(card);
            Bundle bundle=new Bundle();
            bundle.putString("json", json);
            startCommentActivity(bundle);
    }


    private void startCommentActivity(Bundle bundle) {
        Intent intent=new Intent(this, CommentActivity.class);
        if(bundle!=null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        MenuItem item=menu.add(1,1,1, "More");
        item.setIcon(R.drawable.ic_more_horiz_black_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        View view=LayoutInflater.from(this).inflate(R.layout.content_footer_drag, null, false);
        ((Button)view.findViewById(R.id.card_create_btn)).setText("Click Me");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }else{
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(this, "Click On View ", Toast.LENGTH_LONG).show();
        if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                    createList();
                break;
        }
    }

    private void createList() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Create List");
        View view=LayoutInflater.from(this).inflate(R.layout.create_list, rootView, false);
        builder.setView(view);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextInputLayout textInputLayout=view.findViewById(R.id.list_name);
                String listName=textInputLayout.getEditText().getText().toString();
                    createList2(listName);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }


    private void createList2(String listTitle) {

        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try{
            data.put("requestType","create_list");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", preferences.getString("userToken", "0"));
            data.put("user_id", preferences.getString("userid", "0"));
            data.put("board_id", boardid);
            data.put("list_title", listTitle);
            requestData.put("RequestData", data);
            //   sendHttpRequestTCreateList(requestData, Utility.apiUrl);

        }catch (Exception e){
            e.printStackTrace();
        }


        new Retrofit.Builder()
                .addConverterFactory(new StringConvertFactory())
                .baseUrl(Utility.SITE_URL_RETROFIT)
                .build()
                .create(ProjectApi.class)
                .createList(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                           String responseString=response.body();
                           Log.i("responseData","Create list : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                JSONObject resObj=res.getJSONObject("response");
                                JSONObject list=resObj.getJSONObject("list");
                                BoardList boardList=new Gson().fromJson(list.toString(), BoardList.class);
                                renderCard(boardList);
                            }
                        } catch (JSONException e) {
                            Log.e("respnoseDataError", "Create list :  "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("responseDataError", "Create List : "+t.toString());
                    }
                });
    }
}