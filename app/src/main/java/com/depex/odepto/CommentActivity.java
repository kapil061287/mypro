package com.depex.odepto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okio.Okio;
import okio.Sink;


public class CommentActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, OnVolleySuccessListener {
    FloatingActionMenu fab_menu;
    TextView textView, show_details_txt, attachmentTextView;
    TextInputLayout commentBox;
    SharedPreferences preferences;
    String userid;
    String userToken;
    String fullname;
    Menu optionMenu;
    RecyclerView labelCommentRecyclerview;
    RecyclerView commentRecyclerView;
    Toolbar toolbar;
    BoardCard boardCard;
    RecyclerView attachmentRecyclerView;
    ImageView coverCardImage;
    FloatingActionButton fab_menu_attachment, fab_menu_members, fab_menu_due_date, fab_menu_checklist, fab_menu_labels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Custom Title");
        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        fullname=preferences.getString("fullname", "0");
        userid=preferences.getString("userid", "0");
        userToken=preferences.getString("userToken", "0");
        boardCard=new BoardCard();
        labelCommentRecyclerview =findViewById(R.id.label_recyclerview);
        coverCardImage=findViewById(R.id.card_cover_img);
        attachmentTextView=findViewById(R.id.attachment_text_view);

        commentBox=findViewById(R.id.comment_text_layout);
        commentBox.getEditText().setOnFocusChangeListener(this);
        fab_menu =findViewById(R.id.fab_menu);
        fab_menu_attachment=findViewById(R.id.fab_menu_attachment);
        fab_menu_members=findViewById(R.id.fab_menu_members);
        fab_menu_labels=findViewById(R.id.fab_menu_labels);
        fab_menu_due_date=findViewById(R.id.fab_menu_due_date);

        fab_menu_checklist=findViewById(R.id.fab_menu_checklist);
        show_details_txt=findViewById(R.id.show_details_txt);
        show_details_txt.setOnClickListener(this);
        fab_menu_attachment.setOnClickListener(this);
        fab_menu_members.setOnClickListener(this);
        fab_menu_labels.setOnClickListener(this);
        fab_menu_due_date.setOnClickListener(this);
        fab_menu_checklist.setOnClickListener(this);
        commentRecyclerView=findViewById(R.id.comment_recycler_view);
        attachmentRecyclerView=findViewById(R.id.attach_img_resources_recyclerview);



        /*
        Get Card info from previous activity...
         */
        Intent prevIntent=getIntent();
        Bundle bundle=prevIntent.getExtras();
        if(bundle!=null) {
            String cardname = bundle.getString("card_name");
            getSupportActionBar().setTitle(cardname);
            boardCard.setTitle(cardname);
            boardCard.setCardId(bundle.getString("card_id"));
            boardCard.setCardComments(bundle.getString("cardComments"));
            boardCard.setDelStatus(bundle.getString("del_status"));
            String cardid=boardCard.getCardId();

            JSONObject jsonObject=createJsonFromCardLabelList(cardid);
            Utility.getJsonFromHttp(this, jsonObject, this,  Utility.apiUrl);
        }

        showComment();

    }

    private void showComment() {

        try {
            JSONObject commentsJson=createJsonForGetComment();
            Utility.getJsonFromHttp(this, commentsJson, this, Utility.apiUrl);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private JSONObject createJsonForGetComment() throws JSONException {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        data.put("requestType", "all_card_comment");
        data.put("v_code", getString(R.string.api_ver));
        data.put("apikey", getString(R.string.api_key));
        data.put("userToken", userToken);
        data.put("user_id", userid);
        data.put("card_id", boardCard.getCardId());
        requestData.put("RequestData", data);
        return  requestData;
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
            Log.i("onSuccessComment1", requestData.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return requestData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment_activity_menu, menu);
        optionMenu=menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_board_menu:
                break;
            case R.id.move_card_menu:
                break;
            case R.id.subscribe_menu:
                break;
            case R.id.copy_card:
                break;
            case R.id.archive_menu:
                break;
            case R.id.delete_menu:
                break;
            case R.id.done_menu:
                String comments=commentBox.getEditText().getText().toString();
                JSONObject requestData=new JSONObject();
                try{
                    JSONObject data=new JSONObject();
                    data.put("requestType", "card_comments");
                    data.put("v_code", getString(R.string.api_ver));
                    data.put("apikey", getString(R.string.api_key));
                    data.put("userToken",  userToken);
                    data.put("user_id", userid);
                    data.put("card_id", boardCard.getCardId());
                    data.put("comments", comments);
                    requestData.put("RequestData", data);
                    Utility.getJsonFromHttp(this, requestData, this, Utility.apiUrl);

                }catch (Exception e){
                    e.printStackTrace();
                }
                allMenuToggleVisible(optionMenu, true, R.id.share_card_link,
                        R.id.subscribe_menu, R.id.copy_card, R.id.archive_menu, R.id.delete_menu, R.id.move_card_menu);
                allMenuToggleVisible(optionMenu, false, R.id.done_menu);
                toolbar.setNavigationIcon(null);
                commentBox.clearFocus();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_menu_attachment:
                fab_menu.close(false);
                break;
            case R.id.fab_menu_labels:
                fab_menu.close(false);
                break;
            case R.id.fab_menu_checklist:
                fab_menu.close(false);
                break;
            case R.id.fab_menu_due_date:
                fab_menu.close(false);
                break;
            case R.id.fab_menu_members:
                fab_menu.close(false);
                break;
            case R.id.show_details_txt:
                PopupMenu popupMenu=new PopupMenu(this, view);
                getMenuInflater().inflate(R.menu.comment_activities_menu, popupMenu.getMenu());
                popupMenu.show();
                break;

        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
            if(b){
                allMenuToggleVisible(optionMenu, false, R.id.share_card_link,
                        R.id.subscribe_menu, R.id.copy_card, R.id.archive_menu, R.id.delete_menu, R.id.move_card_menu);
                toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
                allMenuToggleVisible(optionMenu, true, R.id.done_menu);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commentBox.getEditText().clearFocus();
                    }
                });
            }else{
                allMenuToggleVisible(optionMenu, true, R.id.share_card_link,
                        R.id.subscribe_menu, R.id.copy_card, R.id.archive_menu, R.id.delete_menu, R.id.move_card_menu);
                allMenuToggleVisible(optionMenu, false, R.id.done_menu);
                toolbar.setNavigationIcon(null);
            }

    }

    public void allMenuToggleVisible(Menu menu,  boolean b, int... id){
        for(int i : id){
            menu.findItem(i).setVisible(b);
        }
    }

    @Override
    public void onSuccess(String s, Object... views) {

        Log.i("onSuccessComment", s);
        try {
            JSONObject response = new JSONObject(s);
            boolean b = response.getBoolean("successBool");
            if (b) {
                String responseType = response.getString("responseType");
                JSONObject responseData = response.getJSONObject("response");
                switch (responseType) {
                    case "card_label_list":
                        JSONArray jsonArray=responseData.getJSONArray("AllCardComment");
                            createCardColorlabel(jsonArray,labelCommentRecyclerview);
                        break;
                    case "all_card_comment":

                        JSONArray attachments=responseData.getJSONArray("attachments");
                        if(attachments!=null){
                            createAttachmentRecycler(attachments, attachmentRecyclerView);
                        }
                        JSONArray jsonArray1=responseData.getJSONArray("AllCardComment");
                        createCommentRecycler(jsonArray1, commentRecyclerView);
                        break;
                    case "card_comments":


                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createAttachmentRecycler(JSONArray attachments, RecyclerView attachmentRecyclerView) throws JSONException {
                List<Attachment> attachmentList=new ArrayList<>();
                    for(int i=0;i<attachments.length();i++){
                        Attachment attachment=new Attachment();
                        attachment.setAttachmentId(attachments.getJSONObject(i).getString("id"));
                        attachment.setCardId(attachments.getJSONObject(i).getString("cardid"));
                        attachment.setUserId(attachments.getJSONObject(i).getString("userid"));
                        attachment.setCkey(attachments.getJSONObject(i).getString("ckey"));
                        String cover_image=attachments.getJSONObject(i).getString("cover_image");
                        attachment.setCoverImage(cover_image.equals("1"));
                        attachment.setAttachmentUrl("http://www.odapto.com/"+attachments.getJSONObject(i).getString("attachments"));
                        attachment.setStatus(attachments.getJSONObject(i).getString("status"));
                        if(attachment.isCoverImage())
                            GlideApp.with(this).load(attachment.getAttachmentUrl()).into(coverCardImage);


                        attachmentList.add(attachment);
                    }
                    AttachmentAdapter attachmentAdapter=new AttachmentAdapter(this, attachmentList);
                    GridLayoutManager manager =new GridLayoutManager(this, 3);
                    //LinearLayoutManager manager=new LinearLayoutManager(this);
                    attachmentTextView.setVisibility(View.VISIBLE);
                    attachmentRecyclerView.setLayoutManager(manager);
                    attachmentRecyclerView.setVisibility(View.VISIBLE);
                    attachmentRecyclerView.setAdapter(attachmentAdapter);

    }

    private void createCardColorlabel(JSONArray jsonArray, View view) throws Exception {
        List<CardLabelButton> buttons = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            CardLabelButton button = new CardLabelButton();
            button.setCkey(object.getString("ckey"));
            button.setColor(object.getString("labels"));
            buttons.add(button);
        }

        final RecyclerView recyclerView= (RecyclerView) view;

        final GridLayoutManager manager=new GridLayoutManager(this, getResources().getInteger(R.integer.card_label_span));
        CardLabelViewAdapter adapter=new CardLabelViewAdapter(buttons, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }

    void createCommentRecycler(JSONArray jsonArray, RecyclerView view){
            Log.i("commentRecycler", jsonArray.toString());
            ArrayList<Comment> commentList=new ArrayList<>();
            try {
            for(int i=0;i<jsonArray.length();i++){
                    Comment comment=new Comment();
                    JSONObject commentObj=jsonArray.getJSONObject(i);
                    String userid=commentObj.getString("userid");
                    String username=commentObj.getString("username");
                    comment.setUsreid(userid);
                    comment.setUserName(username);
                    String card_id=commentObj.getString("card_id");
                    comment.setCardId(card_id);
                    String comments=commentObj.getString("comments");
                    comment.setComment(comments);
                    String ckey=commentObj.getString("ckey");
                    comment.setCkey(ckey);
                    commentList.add(comment);
            }
            CommentAdapter commentAdapter=new CommentAdapter(commentList, this);
                commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                commentRecyclerView.setAdapter(commentAdapter);

            } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onError(VolleyError error) {
        Log.e("onSuccessListenter", error.toString());
    }
}