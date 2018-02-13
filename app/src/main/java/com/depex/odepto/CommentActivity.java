package com.depex.odepto;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.depex.odepto.activity.BaseGoogleDriveActivity;
import com.depex.odepto.adpater.AttachFromAdapter;
import com.depex.odepto.adpater.AttachmentListModel;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okio.Okio;
import okio.Sink;


public class CommentActivity extends BaseGoogleDriveActivity implements View.OnClickListener, View.OnFocusChangeListener, OnVolleySuccessListener {
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final String TAG = "commentActivityTag";
    FloatingActionMenu fab_menu;
    TextView textView, show_details_txt, attachmentTextView;
    TextInputLayout commentBox;
    SharedPreferences preferences;
    String userid;
    String userToken;
    String fullname;
    private boolean mLoggedIn;
    Menu optionMenu;
    RecyclerView labelCommentRecyclerview;
    RecyclerView commentRecyclerView;
    Toolbar toolbar;
    BoardCard boardCard;
    RecyclerView attachmentRecyclerView;

    private final String ACCOUNT_PREFS_NAME="prefs";

    DriveClient mDriveClient;
    GoogleSignInClient mGoogleSignInClient;
    DriveResourceClient mDriveResourceClient;
    Bitmap bitmapToSave;

    DropboxAPI<AndroidAuthSession> mApi;
    ImageView coverCardImage;
    FloatingActionButton fab_menu_attachment, fab_menu_members, fab_menu_due_date, fab_menu_checklist, fab_menu_labels;

    public boolean ismLoggedIn() {
        return mLoggedIn;
    }

    public void setmLoggedIn(boolean mLoggedIn) {
        this.mLoggedIn = mLoggedIn;
    }

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


        AndroidAuthSession session=buildSession();
        mApi=new DropboxAPI<>(session);



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

    //Drop Box logout
    private void logout(){
        mApi.getSession().unlink();
        clearKeys();
        setmLoggedIn(false);
    }



    //drop box preferences clear method fro logout
    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
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

                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                AttachmentListModel takePhoto=new AttachmentListModel();
                takePhoto.setAttachMentTypeName("Take Photo");
                takePhoto.setImage_res(R.drawable.ic_camera_black_24dp);

                AttachmentListModel otherFile=new AttachmentListModel();
                otherFile.setImage_res(R.drawable.ic_attachment_comment_draw);
                otherFile.setAttachMentTypeName("Other File");

                AttachmentListModel googleDrive=new AttachmentListModel();
                googleDrive.setAttachMentTypeName("Google Drive");
                googleDrive.setImage_res(R.drawable.ic_google_drive_logo);

                AttachmentListModel dropBoxModal=new AttachmentListModel();
                dropBoxModal.setAttachMentTypeName("Drop Box");
                dropBoxModal.setImage_res(R.drawable.ic_dropbox);


                List<AttachmentListModel> listModels=new ArrayList<>();
                listModels.add(takePhoto);
                listModels.add(otherFile);
                listModels.add(googleDrive);

                AttachFromAdapter attachFromAdapter=new AttachFromAdapter(listModels, this);

                builder.setAdapter(attachFromAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AttachmentListModel model=listModels.get(i);
                        String name=model.getAttachMentTypeName();
                        Toast.makeText(CommentActivity.this, name, Toast.LENGTH_LONG).show();

                        switch (i){
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                signIn();
                                break;
                            case 3:
                                dropBox();
                                break;
                        }
                    }
                });
                builder.setTitle("Attach From ...");
                builder.create().show();

                //builder.setAdapter()

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


    private static final boolean USE_OAUTH1 = false;

    //drop box initialiation method
    private void dropBox() {

            if(mLoggedIn){
                logout();
            }else {

                    mApi.getSession().startOAuth2Authentication(this);

            }



    }


    //drop box build session method
    private AndroidAuthSession buildSession(){
        AppKeyPair appKeyPair=new AppKeyPair(getString(R.string.drop_box_app_key), getString(R.string.drop_box_app_secret));
        AndroidAuthSession session=new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";


    //Drop box api load auth method
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences preferences=getSharedPreferences(ACCOUNT_PREFS_NAME, MODE_PRIVATE);
        String key=preferences.getString(ACCESS_KEY_NAME, null);
        String secret=preferences.getString(ACCESS_SECRET_NAME, null);
        if(key==null || secret==null || key.length()==0 || secret.length()==0){
            return;
        }

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
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


    //Volley API
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


    //Google Drive method

    public void pinFile(DriveFile driveFile){
        Task<Metadata> pinFileTask=getDriveResourceClient().getMetadata(driveFile).continueWithTask(new Continuation<Metadata, Task<Metadata>>() {
            @Override
            public Task<Metadata> then(@NonNull Task<Metadata> task) throws Exception {
                Metadata metadata=task.getResult();
                if(!metadata.isPinnable()){
                    showMessage("Meta data is pinnable");
                    return Tasks.forResult(metadata);
                }
                if(metadata.isPinned()){
                    showMessage("Task is pinned already");
                    return Tasks.forResult(metadata);
                }

                MetadataChangeSet changeSet=new MetadataChangeSet.Builder().setPinned(true).build();
                return getDriveResourceClient().updateMetadata(driveFile, changeSet);

            }
        });

        pinFileTask.addOnSuccessListener(new OnSuccessListener<Metadata>() {
            @Override
            public void onSuccess(Metadata metadata) {
                showMessage("Meta data updated !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to update metadata", e);
                showMessage("Update meta data failed");
                finish();
            }
        });

    }


    //Google Drive method
    @Override
    protected void onDriveClientReady() {
            pickImageFile().addOnSuccessListener(this, new OnSuccessListener<DriveId>() {
                @Override
                public void onSuccess(DriveId driveId) {
                    pinFile(driveId.asDriveFile());
                }
            });

    }

}