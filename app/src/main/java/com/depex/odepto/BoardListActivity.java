package com.depex.odepto;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.depex.odepto.helper.CustomSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BoardListActivity extends AppCompatActivity implements  OnVolleySuccessListener, OnStartDragListener, AdapterView.OnItemSelectedListener , GestureDetector.OnGestureListener{


    RecyclerView boardListRecycler;
    String userid, userToken, fullname;
    SharedPreferences preferences;
    SpinnerMoveCardBoardAdapter moveCardBoardAdapter;
    SpinnerMoveCardListAdapter moveCardListAdapter;
    BoardCard moveCard;
    BoardList moveCardBoardList;
    String boardTitle;
    GestureDetector detector;
    Toolbar toolbar;
    RecyclerView.ViewHolder cardViewholder;
    boolean flagMenu=false;
    BoardListViewHolder moveBoardListViewHolder;
    BoardListAdapter boardListAdapter;
    Menu menu;
    boolean sameFlag;
    RecyclerView.Adapter moveCardAdapter;
    NavigationView navigationView;
    List<BoardList> boardLists=new ArrayList<>();
        String board_id;
    DrawerLayout drawerLayout;
    Spinner listSpinner;
    View contextView;
    Spinner boardSpinner;
    List<BoardList> listSpinnerArrayList=new ArrayList<>();
    Menu navigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_board_list);
        drawerLayout=findViewById(R.id.board_list_drawer_layout);
        navigationView=findViewById(R.id.navview_board_list);
        navigationMenu=navigationView.getMenu();
        detector=new GestureDetector(this, this);
        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        userid=preferences.getString("userid", "0");
        boardListRecycler=findViewById(R.id.board_list_recycler);

        fullname=preferences.getString("fullname", "0");
        userToken=preferences.getString("userToken","0");
       // Toast.makeText(this, userToken, Toast.LENGTH_LONG).show();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        boardListAdapter = new BoardListAdapter(boardLists);
        boardListRecycler.setLayoutManager(new LinearLayoutManager(BoardListActivity.this, LinearLayout.HORIZONTAL, false));
        boardListRecycler.setAdapter(boardListAdapter);

        createBoardList();
    }




    /**
     * Activity Options fab_menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        MenuItem item=menu.add(1,1,1, "More");
        item.setIcon(R.drawable.ic_more_horiz_black_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }




    /**
     * create board list layout.
     */
    private void createBoardList() {
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String id=bundle.getString("board_id");
        boardTitle=bundle.getString("board_title");
        toolbar.setTitle(boardTitle);
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


    /**
     * On Item selected Listener for horizontal more button.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
            //Toast.makeText(this, "Hello Androidj", Toast.LENGTH_LONG).show();
            if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }else{
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
            break;
        }
        return true;
    }


    /**
     * On back button Pressed override for navigatrion drawer.
     */
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
            drawerLayout.closeDrawer(Gravity.RIGHT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.move_card_board_spinner:
                listSpinnerArrayList.clear();
                if (Utility.boards.get(i).getBoardId().equals(board_id)) {
                    listSpinnerArrayList.addAll(boardLists);
                    if (moveCardListAdapter != null) {
                        moveCardListAdapter.notifyDataSetChanged();
                    } else {
                        moveCardListAdapter = new SpinnerMoveCardListAdapter(this, listSpinnerArrayList);
                    }

                    listSpinner.setAdapter(moveCardListAdapter);
                } else {
                    if (moveCardListAdapter != null) {
                        moveCardListAdapter.notifyDataSetChanged();
                    }
                    Board board = Utility.boards.get(i);
                    String board_id = board.getBoardId();
                    try {
                        JSONObject jsonListBoard = createJsonforListBoard(board_id);
                        Utility.getJsonFromHttp(this, jsonListBoard, this, Utility.apiUrl, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }




    public JSONObject createJsonforListBoard(String board_id)throws Exception{
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        data.put("requestType", "list_board");
        data.put("v_code", getString(R.string.api_ver));
        data.put("apikey", getString(R.string.api_key));
        data.put("userToken", userToken);
        data.put("user_id", userid);
        data.put("board_id", board_id);
        requestData.put("RequestData", data);
        return requestData;
    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {



    }
//Gesture Detector method  start from here
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    //getsture detecter method end


    /**
     * View Holder for board lists.
     */

    class BoardListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener, MenuItem.OnMenuItemClickListener {
        String title;
        BoardList boardList=null;
        TextView list_title_txt;
        EditText list_title_edit;
        CardView board_list_list_cardview;
        TextView option_menu_btn;
        RecyclerView board_list_card_recyclerview;
        Button create_card_btn, create_list_btn;



        public BoardListViewHolder(View itemView) {
            super(itemView);
            list_title_txt=itemView.findViewById(R.id.list_title_txt);
            list_title_txt.setOnClickListener(this);
            list_title_edit=itemView.findViewById(R.id.list_title_edit);
            list_title_edit.setOnFocusChangeListener(this);
            board_list_list_cardview=itemView.findViewById(R.id.board_list_list_cardview);
            board_list_list_cardview.setOnClickListener(this);
            board_list_card_recyclerview=itemView.findViewById(R.id.board_list_card_recycler);
            option_menu_btn=itemView.findViewById(R.id.textViewOptions);
            option_menu_btn.setOnClickListener(this);
            create_list_btn=itemView.findViewById(R.id.create_list_btn);
            create_card_btn=itemView.findViewById(R.id.card_create_btn);
           // create_card_btn.setOnClickListener(this);
            create_list_btn.setOnClickListener(this);

         //   Toast.makeText(BoardListActivity.this, "Position :"+String.valueOf(position), Toast.LENGTH_LONG).show();
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.board_list_list_cardview:

                    break;
                case R.id.list_title_txt:
                    list_title_txt.setVisibility(View.GONE);
                    list_title_edit.setVisibility(View.VISIBLE);
                    list_title_edit.requestFocus();
                    list_title_edit.setSelection(list_title_edit.getText().toString().length());
                    MenuItem item=BoardListActivity.this.menu.add(1,2,1, "Done");
                    item.setIcon(R.drawable.ic_check_black_24dp);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    BoardListActivity.this.menu.removeItem(1);
                    toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    item.setOnMenuItemClickListener(this);

                    break;
                case R.id.textViewOptions:
                    PopupMenu popupMenu=new PopupMenu(BoardListActivity.this, view);
                    Menu menu=popupMenu.getMenu();

                    getMenuInflater().inflate(R.menu.list_menu_board_list, menu);
                    popupMenu.show();
                    break;

                case R.id.card_create_btn:

                    break;
                case R.id.create_list_btn:
                        this.board_list_list_cardview.setVisibility(View.VISIBLE);
                        this.list_title_edit.setVisibility(View.VISIBLE);
                        this.list_title_edit.requestFocus();
                        flagMenu=true;
                       // BoardListActivity.this.currentEditText=list_title_edit;
                       // BoardListActivity.this.currentTextView=list_title_txt;
                        MenuItem item1=BoardListActivity.this.menu.add(1,2,1, "Done");
                        item1.setIcon(R.drawable.ic_check_black_24dp);
                        item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                        BoardListActivity.this.menu.removeItem(1);
                        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        item1.setOnMenuItemClickListener(this);
                        create_list_btn.setVisibility(View.GONE);
                       // create_card_btn.setVisibility(View.VISIBLE);
                    break;
            }
        }




        @Override
        public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                  //  Toast.makeText(BoardListActivity.this, "Lose Focus ", Toast.LENGTH_LONG).show();
                    if(!((EditText)view).getText().toString().equals(title)){
                        Toast.makeText(BoardListActivity.this, "Title Changed", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(BoardListActivity.this, "Not Changed", Toast.LENGTH_LONG).show();
                    }
                }else{
                   // Toast.makeText(BoardListActivity.this, "Get Focus", Toast.LENGTH_LONG).show();
                }
        }




        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case 2:
                    BoardListActivity.this.menu.removeItem(2);
                    MenuItem item=menu.add(1,1,1, "More");
                    item.setIcon(R.drawable.ic_more_horiz_black_24dp);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                   // item.setOnMenuItemClickListener(BoardListActivity.this);

                    if(flagMenu) {
                        if (list_title_edit.getText().toString().length() == 0 || list_title_edit.getText().toString().equals(list_title_txt.getText().toString())) {
                            Utility.setVisibility(View.GONE, board_list_card_recyclerview, list_title_txt, list_title_edit, board_list_list_cardview, this.create_card_btn);
                            Utility.setVisibility(View.VISIBLE, create_list_btn);
                            toolbar.setNavigationIcon(null);
                        } else {
                            Utility.setVisibility(View.GONE, board_list_card_recyclerview, list_title_txt, list_title_edit, board_list_list_cardview, this.create_card_btn);
                            Utility.setVisibility(View.VISIBLE, create_list_btn);
                            //list_title_txt.setText(list_title_edit.getText().toString());
                            toolbar.setNavigationIcon(null);
                            creatList(list_title_edit.getText().toString());
                            //int position=getAdapterPosition();
                            //createBoardList();
                        }
                        flagMenu=false;
                    }else{
                        Utility.setVisibility(View.VISIBLE, board_list_card_recyclerview, list_title_txt, board_list_list_cardview, this.create_card_btn);
                        Utility.setVisibility(View.GONE, create_list_btn, list_title_edit);
                        toolbar.setNavigationIcon(null);

                    }
                    //Toast.makeText(BoardListActivity.this, list_title_edit.getText().toString(), Toast.LENGTH_LONG).show();
                break;
            }
            return true;
        }
    }


    /**
     * Send http request for create card .
     * @param requestData
     * @param registerUrl
     */
    private void sendHttpRequestToCreateCard(JSONObject requestData, String registerUrl) {
                RequestQueue queue=Volley.newRequestQueue(this);
                JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, registerUrl, requestData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            Log.i("cardResponse", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(request);

    }


    /**
     * Create List in board ID
     */
    private void creatList(String title) {
            JSONObject requestData=new JSONObject();
            JSONObject data=new JSONObject();
            try{
                data.put("requestType","create_list");
                data.put("v_code", getString(R.string.api_ver));
                data.put("apikey", getString(R.string.api_key));
                data.put("userToken", userToken);
                data.put("user_id", userid);
                data.put("board_id", board_id);
                data.put("list_title", title);
                requestData.put("RequestData", data);
             //   sendHttpRequestTCreateList(requestData, Utility.apiUrl);
                Utility.getJsonFromHttp(this, requestData, this, Utility.apiUrl);
            }catch (Exception e){
                e.printStackTrace();
            }

    }


    /**
     * Adapter for board lists.
     */
    private class BoardListAdapter extends RecyclerView.Adapter<BoardListViewHolder>{

        List<BoardList> list;
        BoardListAdapter(List<BoardList> list){
            this.list=list;
        }

        @Override
        public BoardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=getLayoutInflater().inflate(R.layout.list_of_board_layout,null, false);
            return new BoardListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BoardListViewHolder holder, final int position) {

            if(position==list.size()){
                Utility.setVisibility(View.GONE, holder.list_title_edit, holder.list_title_txt, holder.board_list_card_recyclerview
                , holder.create_card_btn, holder.board_list_list_cardview);
                Utility.setVisibility(View.VISIBLE, holder.create_list_btn);
                return ;
            }else{
                Utility.setVisibility(View.VISIBLE, holder.list_title_txt, holder.board_list_card_recyclerview
                        , holder.create_card_btn, holder.board_list_list_cardview);
                Utility.setVisibility(View.GONE, holder.create_list_btn, holder.list_title_edit);
            }

                holder.title=list.get(position).getTitle();
                holder.list_title_txt.setText(holder.title);
                holder.list_title_edit.setText(holder.title);
                BoardList boardList=list.get(position);

                  holder.create_card_btn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          final BoardList boardList1=list.get(position);
                              AlertDialog.Builder builder=new AlertDialog.Builder(BoardListActivity.this);
                              final View view1=getLayoutInflater().inflate(R.layout.create_card_layout, null, false);
                              builder.setView(view1);
                              builder.setPositiveButton("Add Card", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialogInterface, int i) {
                                      String list_id=boardList1.getId();
                                      String card_title=((TextInputLayout)view1.findViewById(R.id.create_card_txt)).getEditText().getText().toString();
                                      JSONObject requestData=new JSONObject();
                                      JSONObject data=new JSONObject();
                                      try{
                                          data.put("requestType", "create_card");
                                          data.put("v_code", getString(R.string.api_ver));
                                          data.put("apikey", getString(R.string.api_key));
                                          data.put("userToken", userToken);
                                          data.put("user_id", userid);
                                          data.put("list_id", list_id);
                                          data.put("card_title", card_title);
                                          requestData.put("RequestData", data);
                                          sendHttpRequestToCreateCard(requestData, Utility.apiUrl);
                                          Log.i("volleyJsonLog", requestData.toString());
                                      }catch (Exception e){
                                          e.printStackTrace();
                                      }
                                  }
                              });
                              builder.setNegativeButton("Cancel", null);
                              builder.create().show();
                      }
                  });

            String id=boardList.getId();
            try{
                JSONObject requestData=new JSONObject();
                JSONObject data=new JSONObject();
                data.put("requestType", "list_card");
                data.put("v_code", getString(R.string.api_ver));
                data.put("apikey", getString(R.string.api_key));
                data.put("userToken", userToken);
                data.put("user_id", userid);
                data.put("list_id", id);
                requestData.put("RequestData", data);
                Log.i("volleyJsonLogCreate", requestData.toString());
                //sendHttpRequestGettingBoardCards(requestData, Utility.apiUrl, holder.board_list_card_recyclerview);
                Utility.getJsonFromHttp(BoardListActivity.this,requestData, BoardListActivity.this, Utility.apiUrl, holder.board_list_card_recyclerview );

            }catch (Exception e){
                e.printStackTrace();
            }
        }


        @Override
        public int getItemCount() {
            return list.size()+1;
        }
    }




    /**
     * View Holder for cards of board.
     */
    private class BoardCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , ItemTouchHelperViewHolder {
        TextView board_card_txt_title;
        View itemView;
        ImageView cover_img;
        RecyclerView card_label_recyclerview_on_list;
        public BoardCardViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            this.cover_img=itemView.findViewById(R.id.card_cover);
            card_label_recyclerview_on_list=itemView.findViewById(R.id.card_label_recyclerview_on_list);
            this.board_card_txt_title=itemView.findViewById(R.id.board_card_txt_title);
        }

        @Override
        public void onClick(View view) {
            Object o =view.getTag();
            if(o instanceof  BoardCard) {
                BoardCard card=(BoardCard)o;
                Intent commentActivityIntent = new Intent(BoardListActivity.this, CommentActivity.class);
                Bundle bundle =new Bundle();
                bundle.putString("card_name", card.getTitle());
                bundle.putString("card_id", card.getCardId());
                bundle.putString("cardComments", card.getCardComments());
                bundle.putString("del_status", card.getDelStatus());
                commentActivityIntent.putExtras(bundle);
                startActivity(commentActivityIntent);
            }
        }

        @Override
        public void onItemSelected() {
            //RotateAnimation animation=new RotateAnimation(0.f,20.f);
            //itemView.setAnimation(animation);
            //itemView.animate();
            //itemView.setBackgroundColor(Color.LTGRAY);
            itemView.setRotation(2.f);
        }

        @Override
        public void onItemClear() {
            //itemView.setBackgroundColor(Color.parseColor("#55ffffff"));
            itemView.setRotation(0);
        }
    }


    /**
     * Adapter for cards of board.
     */
    private class BoardCardAdapter extends RecyclerView.Adapter<BoardCardViewHolder>  implements  ItemTouchHelperAdapter {
        List<BoardCard> list;
        OnStartDragListener onStartDragListener;
        BoardCardAdapter(List<BoardCard> list, OnStartDragListener onStartDragListener){
            this.list=list;
            this.onStartDragListener=onStartDragListener;
        }

        @Override
        public BoardCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=getLayoutInflater().inflate(R.layout.board_list_card_layout, null, false);
            return new BoardCardViewHolder(view);
        }



        @Override
        public void onBindViewHolder(final BoardCardViewHolder holder, final int position) {
                    holder.board_card_txt_title.setText(list.get(position).getTitle());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BoardCard card=list.get(position);
                            view.setTag(card);
                            holder.onClick(view);

                        }
                    });

                    BoardCard card=list.get(position);
                    if(card.getCoverImage()!=null){
                        GlideApp.with(BoardListActivity.this).load(card.coverImage).into(holder.cover_img);
                    }

                    /*holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                                onStartDragListener.onStartDrag(holder);
                            }
                            return false;
                        }
                    });*/
                    holder.itemView.setOnDragListener(new View.OnDragListener() {
                        @Override
                        public boolean onDrag(View view, DragEvent dragEvent) {

                            return true;
                        }
                    });





                    holder.itemView.setTag(list.get(position));
                    //TODO register for context menu 19 feb 2018
                    registerForContextMenu(holder.itemView);





                    /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            registerForContextMenu(view);

                            //onStartDrag(holder);
                            return true;
                        }
                    });
*/

            //holder.card_label_recyclerview_on_list
            JSONObject object=createJsonFromCardLabelList(list.get(position).getCardId());
            Log.i("onSuccessVolley", object.toString());
            Utility.getJsonFromHttp(BoardListActivity.this, object, BoardListActivity.this, Utility.apiUrl, holder.card_label_recyclerview_on_list);
        }


        @Override
        public int getItemCount() {
            return list.size();
        }



        @Override
        public boolean onItemMove(int fromPosition, int toPosistion) {
            Collections.swap(list, fromPosition, toPosistion);
            notifyItemMoved(fromPosition, toPosistion);
            return true;
        }


        @Override
        public void onItemDismiss(int position) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.move_card:
               createAlertDialogForMoveCard();
                break;
            case R.id.copy_card:
                break;
        }
        return true;
    }




    private void createAlertDialogForMoveCard() {

        int selectBoardPosition=0;
        for(int i=0;i< Utility.boards.size();i++){
            if(Utility.boards.get(i).getBoardTitle().equals(boardTitle)){
                selectBoardPosition=i;
            }
        }

        Log.i("selectBoardPosition", ""+selectBoardPosition);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.move_card_alert_dialog, null, false);
        listSpinner=view.findViewById(R.id.move_card_list_spinner);
        boardSpinner=view.findViewById(R.id.move_card_board_spinner);
        moveCardBoardAdapter=new SpinnerMoveCardBoardAdapter(this, Utility.boards);
        boardSpinner.setAdapter(moveCardBoardAdapter);
        boardSpinner.setOnItemSelectedListener(this);
        boardSpinner.setSelection(selectBoardPosition);
        builder.setView(view);
        builder.setTitle("Move to ...");
        builder.setPositiveButton("Move", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(((Board)boardSpinner.getSelectedItem()).getBoardId().equals(board_id)) {
                    BoardList moveCardBoardList = (BoardList) listSpinner.getSelectedItem();
                    //position
                    int position=boardListAdapter.list.indexOf(moveCardBoardList);
                    RecyclerView.ViewHolder viewHolder= boardListRecycler.findViewHolderForAdapterPosition(position);

                    if(viewHolder instanceof BoardListViewHolder){
                        moveBoardListViewHolder= (BoardListViewHolder) viewHolder;
                        moveCardAdapter=moveBoardListViewHolder.board_list_card_recyclerview.getAdapter();
                    }else{
                        Log.i("viewHolder", "View Holder not found");
                    }
                }
                    JSONObject object=createJsonForMoveCard(((BoardList)listSpinner.getSelectedItem()).getId(), ((Board)boardSpinner.getSelectedItem()).getBoardId());
                Log.i("onSuccessMove", object.toString());
                    Utility.getJsonFromHttp(BoardListActivity.this, object, BoardListActivity.this, Utility.apiUrl);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    JSONObject createJsonForMoveCard(String listid, String board_id){
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try{
            data.put("requestType", "move_card");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", userToken);
            data.put("user_id", userid);
            data.put("list_id", listid);
            data.put("board_id", board_id);
            data.put("card_id", moveCard.getCardId());
            requestData.put("RequestData", data);
        }catch (Exception e){
            Log.e("onError", e.toString());
            e.printStackTrace();
        }
        return requestData;
    }

    private JSONObject createJsonFromCardLabelList(String cardid) {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            data.put("requestType", "card_label_list");
            data.put("v_code", getString(R.string.api_ver));
            data.put("apikey", getString(R.string.api_key));
            data.put("userToken", userToken);
            data.put("user_id", userid);
            data.put("card_id",cardid);
            requestData.put("RequestData", data);
        }catch (Exception e){
            e.printStackTrace();
        }
        return requestData;
    }

    ItemTouchHelper boardCardItemTouchHelper;



    @Override
    public void onSuccess(String s, Object... objects) {
        Log.i("onSuccessVolley", s);
        try {
            JSONObject response=new JSONObject(s);
            boolean b=response.getBoolean("successBool");
            if(b) {
                String responseType=response.getString("responseType");
                JSONObject responseData = response.getJSONObject("response");
                switch (responseType) {
                    case "list_board":
                        listSpinnerArrayList.clear();
                        if(objects.length==1){
                            if(((Integer)objects[0])==1){
                                JSONArray listsJsonArray = responseData.getJSONArray("boardList");
                                for (int i = 0; i < listsJsonArray.length(); i++) {
                                    JSONObject listJsonObject = listsJsonArray.getJSONObject(i);
                                    BoardList boardList = new BoardList();
                                    boardList.setTitle(listJsonObject.getString("list_title"));
                                    boardList.setId(listJsonObject.getString("list_id"));
                                    if (!listSpinnerArrayList.contains(boardList)) {
                                        listSpinnerArrayList.add(boardList);
                                    }
                                }
                                Log.i("boardLists", listSpinnerArrayList.toString());
                                moveCardListAdapter = new SpinnerMoveCardListAdapter(this, listSpinnerArrayList);
                                listSpinner.setAdapter(moveCardListAdapter);
                            }
                        }else {
                            JSONArray listsJsonArray = responseData.getJSONArray("boardList");
                            for (int i = 0; i < listsJsonArray.length(); i++) {
                                JSONObject listJsonObject = listsJsonArray.getJSONObject(i);
                                BoardList boardList = new BoardList();
                                boardList.setTitle(listJsonObject.getString("list_title"));
                                boardList.setId(listJsonObject.getString("list_id"));
                                if (!boardLists.contains(boardList)) {
                                    boardLists.add(boardList);
                                }
                            }
                            Log.i("boardLists", boardLists.toString());

                            //boardListAdapter = new BoardListAdapter(boardLists);
                           // boardListRecycler.setLayoutManager(new LinearLayoutManager(BoardListActivity.this, LinearLayout.HORIZONTAL, false));
                            //boardListRecycler.setAdapter(boardListAdapter);
                            if(boardListAdapter!=null)
                            boardListAdapter.notifyDataSetChanged();
                            Log.i("boardlistAdapterNull", boardListAdapter+"");
                            //GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                            CustomSnapHelper helper=new CustomSnapHelper();
                            helper.attachToRecyclerView(boardListRecycler);
                        }
                        break;
                    case "list_card":
                        try {
                            List<BoardCard> boardCards = new ArrayList<>();
                            JSONArray cardListJsonArray = responseData.getJSONArray("CardList");
                            for (int i = 0; i < cardListJsonArray.length(); i++) {
                                BoardCard card = new BoardCard();
                                card.setTitle(cardListJsonArray.getJSONObject(i).getString("card_title"));
                                card.setCardComments(cardListJsonArray.getJSONObject(i).getString("cardComments"));
                                card.setCardId(cardListJsonArray.getJSONObject(i).getString("card_id"));
                                card.setDelStatus(cardListJsonArray.getJSONObject(i).getString("del_status"));
                                card.setCoverImage(Utility.siteUrl+cardListJsonArray.getJSONObject(i).getString("cover_image"));
                                card.setTotalAttachments(cardListJsonArray.getJSONObject(i).getString("total_attachments"));
                                boardCards.add(card);
                            }

                            BoardCardAdapter adapter = new BoardCardAdapter(boardCards, this);
                            RecyclerView recyclerView = (RecyclerView) objects[0];
                            recyclerView.setLayoutManager(new LinearLayoutManager(BoardListActivity.this));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);

                            //ItemTouchHelper.Callback callback=new SimpleItemTouchHelperCallback(moveCardListAdapter);
                            //boardCardItemTouchHelper =new ItemTouchHelper(callback);
                           // boardCardItemTouchHelper.attachToRecyclerView(recyclerView);

                        } catch (JSONException e) {
                            Log.e("onSuccessError", e.toString());
                        }
                        break;
                    case "create_list":
                        Log.i("createListjson", response.toString());
                        String listName=responseData.getString("list_name");
                        String listId=responseData.getString("list_id");
                        String boardId=responseData.getString("board_id");
                        BoardList list=new BoardList();
                        list.setTitle(listName);
                        list.setId(listId);
                        boardLists.add(list);
                        boardListAdapter.notifyItemInserted(boardLists.size()-1);

                        break;

                    case "card_label_list":
                        JSONArray jsonArray=responseData.getJSONArray("AllCardComment");
                        if(objects[0] instanceof RecyclerView) {
                            createCardColorlabel(jsonArray,(RecyclerView)objects[0]);
                        }
                        break;

                    case "move_card":
                        if(contextView.getParent() instanceof RecyclerView) {
                            RecyclerView recyclerView = (RecyclerView) contextView.getParent();
                            BoardCardAdapter adapter= (BoardCardAdapter) recyclerView.getAdapter();
                            int position=adapter.list.indexOf(moveCard);
                            boolean removeSuccess=adapter.list.remove(moveCard);
                            if(removeSuccess) {
                                adapter.notifyItemRemoved(position);
                            }

                            if(moveCardAdapter instanceof BoardCardAdapter){
                                Log.i("viewHolder", "Card Adapter found at place");
                                BoardCardAdapter adapter1= (BoardCardAdapter) adapter;
                                Log.i("viewHolder", adapter1.list.toString());
                            }else if(moveCardAdapter==null){
                                List<BoardCard> boardCards=new ArrayList<>();
                                boardCards.add(moveCard);
                                BoardCardAdapter boardCardAdapter=new BoardCardAdapter(boardCards, BoardListActivity.this);
                                LinearLayoutManager manager=new LinearLayoutManager(BoardListActivity.this);
                                if(moveBoardListViewHolder!=null) {
                                    moveBoardListViewHolder.board_list_card_recyclerview.setAdapter(boardCardAdapter);
                                    moveBoardListViewHolder.board_list_card_recyclerview.setLayoutManager(manager);
                                }
                            }
                            Log.i("cardAdapterPosition", adapter.list.toString());
                        }
                        String msg=responseData.getString("message");
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void createCardColorlabel(JSONArray jsonArray, View view) throws JSONException {
        List<CardLabelButton> buttons=new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            JSONObject object=jsonArray.getJSONObject(i);
            CardLabelButton button=new CardLabelButton();
            button.setCkey(object.getString("ckey"));
            button.setColor(object.getString("labels"));
            buttons.add(button);
        }


     RecyclerView recyclerView= (RecyclerView) view;

        GridLayoutManager manager=new GridLayoutManager(BoardListActivity.this, 6);
        CardLabelViewAdapter adapter=new CardLabelViewAdapter(buttons, BoardListActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

    }



    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if(boardCardItemTouchHelper!=null)
            boardCardItemTouchHelper.startDrag(viewHolder);
    }

    RecyclerView.Adapter moveAdapter;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getTag() instanceof BoardCard) {
            moveCard = (BoardCard) v.getTag();
            contextView=v;
            RecyclerView recyclerView= (RecyclerView) v.getParent();
            moveAdapter=recyclerView.getAdapter();
            Log.i("viewContext" , v.getParent().getClass().toString());
            getMenuInflater().inflate(R.menu.context_card_menu, menu);
        }
    }


    @Override
    public void onError(VolleyError error) {
        Log.i("onErrorVolley", error.toString());
    }
}