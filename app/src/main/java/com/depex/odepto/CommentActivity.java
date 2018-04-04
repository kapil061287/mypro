package com.depex.odepto;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.depex.odepto.adpater.AttachFromAdapter;
import com.depex.odepto.adpater.AttachmentListModel;
import com.depex.odepto.api.ProjectApi;
import com.depex.odepto.factory.StringConvertFactory;
import com.depex.odepto.recent.Card;
import com.depex.odepto.screen.GoogleDriveActivity;
import com.dropbox.chooser.android.DbxChooser;
import com.evernote.client.android.EvernoteSession;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CommentActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = "commentActivityTag";
    private static final int DROP_BOX_REQUEST_CODE=1;
    private static final int GOOGLE_DRIVE_REQUEST_CODE=2;
    private static final int CAMERA_REQUEST_CODE=3;
    private static final int ATTACH_FILE_REQUEST_CODE=4;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    FloatingActionMenu fab_menu;
    TextView textView, show_details_txt, attachmentTextView;
    TextInputLayout commentBox;
    SharedPreferences preferences;
    String userid;
    String userToken;
    DbxChooser mDbxChooser;
    String fullname;
    private boolean mLoggedIn;
    Menu optionMenu;
    RecyclerView labelCommentRecyclerview;
    RecyclerView commentRecyclerView;
    Toolbar toolbar;
    Card boardCard;
    RecyclerView attachmentRecyclerView;

    private final String ACCOUNT_PREFS_NAME="prefs";



    ImageView coverCardImage;
    FloatingActionButton fab_menu_attachment, fab_menu_members, fab_menu_due_date, fab_menu_checklist, fab_menu_labels;

    final static private String APP_KEY = "sauids7y2n12nue";
    final static private String APP_SECRET = "a7tpevka8ue3vey";

    EvernoteSession mEvernoteSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FF000000"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Custom Title");
        mDbxChooser=new DbxChooser(APP_KEY);
        preferences=getSharedPreferences("odepto_pref", MODE_PRIVATE);
        fullname=preferences.getString("fullname", "0");
        userid=preferences.getString("userid", "0");
        userToken=preferences.getString("userToken", "0");
        boardCard=new Card();
        labelCommentRecyclerview =findViewById(R.id.label_recyclerview);
        coverCardImage=findViewById(R.id.card_cover_img);
        attachmentTextView=findViewById(R.id.attachment_text_view);

        requestPermission();



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
            String json=bundle.getString("json");
            boardCard=new Gson().fromJson(json, Card.class);
            getSupportActionBar().setTitle(boardCard.getTitle());



            /*JSONObject jsonObject=createJsonFromCardLabelList(boardCard.getCardId());
            Utility.getJsonFromHttp(this, jsonObject, this,  Utility.apiUrl);*/
        }
        showComment();
        showLabels();
    }

    private void showLabels() {
        JSONObject requestData=new JSONObject();
        JSONObject data=new JSONObject();
        try {
        data.put("requestType", "card_label_list");
        data.put("v_code", getString(R.string.api_ver));
        data.put("apikey", getString(R.string.api_key));
        data.put("userToken", userToken);
        data.put("user_id", userid);
        data.put("card_id",boardCard.getCardId());
        requestData.put("RequestData", data);
        Log.i("requestData", "Label request Data : "+requestData.toString());
        new Retrofit.Builder()
                .baseUrl(Utility.SITE_URL_RETROFIT)
                .addConverterFactory(new StringConvertFactory())
                .build()
                .create(ProjectApi.class)
                .cardLabel(requestData.toString())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String responseString = response.body();
                        Log.i("responseData", "Label response : "+responseString);
                        try {
                            JSONObject res=new JSONObject(responseString);
                            boolean success=res.getBoolean("successBool");
                            if(success){
                                Gson gson=new Gson();
                                JSONObject resObj=res.getJSONObject("response");
                                JSONArray arr=resObj.getJSONArray("AllCardComment");
                                Label[]labelArr=gson.fromJson(arr.toString(), Label[].class);
                                List<Label> labels=new ArrayList<>(Arrays.asList(labelArr));
                                GridLayoutManager manager=new GridLayoutManager(CommentActivity.this, CommentActivity.this.getResources().getInteger(R.integer.card_label_span));
                                labelCommentRecyclerview.setLayoutManager(manager);
                                CardLabelViewAdapter adapter=new CardLabelViewAdapter(labels, CommentActivity.this);
                                labelCommentRecyclerview.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                            Log.e("responseDataError", "Error : "+t.toString());
                            if(Utility.isConnectvityAvailable(CommentActivity.this)){
                                showLabels();
                            }
                    }
                });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        } else {
            // Permission has already been granted
        }
    }


    private void showComment() {
        try {
            JSONObject commentsJson=createJsonForGetComment();

            new Retrofit.Builder()
                    .baseUrl(Utility.SITE_URL_RETROFIT)
                    .addConverterFactory(new StringConvertFactory())
                    .build()
                    .create(ProjectApi.class)
                    .cardComments(commentsJson.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String responseString=response.body();
                            Log.i("responseData", "Comment Api Hit : "+responseString);
                            try {
                                JSONObject res=new JSONObject(responseString);
                                boolean success=res.getBoolean("successBool");
                                if(success){
                                    JSONObject resObj=res.getJSONObject("response");
                                    JSONArray commentsArr=resObj.getJSONArray("AllCardComment");

                                    GsonBuilder builder=new GsonBuilder();
                                    //2018-04-03 17:28:04
                                    builder.setDateFormat("yy-M-d H:m:s");
                                    Gson gson=builder.create();
                                    Comment[]arr=gson.fromJson(commentsArr.toString(), Comment[].class);
                                    List<Comment> comments=new ArrayList<>(Arrays.asList(arr));
                                   //setDateInComments(comments);
                                    CommentAdapter adapter=new CommentAdapter(comments, CommentActivity.this);
                                    LinearLayoutManager manager=new LinearLayoutManager(CommentActivity.this);
                                    commentRecyclerView.setLayoutManager(manager);
                                    DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(CommentActivity.this, manager.getOrientation());
                                    commentRecyclerView.addItemDecoration(dividerItemDecoration);
                                    commentRecyclerView.setAdapter(adapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if(Utility.isConnectvityAvailable(CommentActivity.this)){
                                showComment();
                            }
                        }
                    });


        }catch (Exception e){
            Log.i("responseDataError", "Comment Error : "+e.toString());
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
                    sendComment(comments);
                break;
        }
        return true;
    }

    private void sendComment(String comments) {
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
            Log.i("requestData", requestData.toString());

            new Retrofit.Builder().addConverterFactory(new StringConvertFactory())
                    .baseUrl(Utility.SITE_URL_RETROFIT)
                    .build().create(ProjectApi.class)
                    .createComment(requestData.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            String responseString=response.body();

                                Log.i("responseData", "Send Comment : "+responseString);
                            try {
                                JSONObject res=new JSONObject(responseString);
                                boolean success=res.getBoolean("successBool");
                                if(success){
                                    allMenuToggleVisible(optionMenu, true, R.id.share_card_link,
                                            R.id.subscribe_menu, R.id.copy_card, R.id.archive_menu, R.id.delete_menu, R.id.move_card_menu);
                                    allMenuToggleVisible(optionMenu, false, R.id.done_menu);
                                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                                    commentBox.clearFocus();
                                    commentBox.getEditText().setText("");
                                    JSONObject resObj=res.getJSONObject("response");
                                    JSONArray arr=resObj.getJSONArray("lastComment");
                                    JSONObject commentObj=arr.getJSONObject(0);
                                    GsonBuilder builder=new GsonBuilder();
                                    //2018-04-03 17:28:04
                                    builder.setDateFormat("yy-M-d H:m:s");
                                    Gson gson=builder.create();
                                    Comment comment=gson.fromJson(commentObj.toString(),Comment.class);
                                    comment.setUserName(preferences.getString("fullname", "0"));
                                    CommentAdapter adapter= (CommentAdapter) commentRecyclerView.getAdapter();
                                    adapter.addItem(comment);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }



                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                                Log.i("responseDataError", "Comment Error : "+t.toString());
                                if(Utility.isConnectvityAvailable(CommentActivity.this)){
                                        sendComment(comments);
                                }
                        }
                    });

            //Utility.getJsonFromHttp(this, requestData, this, Utility.apiUrl);
        }catch (Exception e){
            Log.i("responseDataError", "Error : "+e.toString());
        }

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

                AttachmentListModel evernoteModal=new AttachmentListModel();
                evernoteModal.setAttachMentTypeName("Evernote Notes");
                evernoteModal.setImage_res(R.drawable.evernote);




                List<AttachmentListModel> listModels=new ArrayList<>();
                listModels.add(takePhoto);
                listModels.add(otherFile);
                listModels.add(googleDrive);
                listModels.add(dropBoxModal);
                //listModels.add(evernoteModal);

                AttachFromAdapter attachFromAdapter=new AttachFromAdapter(listModels, this);

                builder.setAdapter(attachFromAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AttachmentListModel model=listModels.get(i);
                        String name=model.getAttachMentTypeName();
                        Toast.makeText(CommentActivity.this, name, Toast.LENGTH_LONG).show();

                        switch (i){
                            case 0:
                                //Take photo
                                startCameraForFile();
                                break;
                            case 1:
                                //Other file
                                startFileChooser();
                                break;
                            case 2:
                                choosFileFromGoogleDrive();
                                break;
                            case 3:
                                //drop box api
                                chooseFileFromDropBox();
                                break;
                            case 4:
                                //chooseNoteFromEverNote();
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

 /*   private void chooseNoteFromEverNote() {

              Intent intent=new Intent(this, EvernoteActivity.class);
              startActivityForResult(intent, EvernoteSession.REQUEST_CODE_LOGIN);
    }*/

    private void chooseFileFromDropBox() {
       /* Intent intent=new Intent(this, DropboxApiActivity.class);
        startActivityForResult(intent, DROP_BOX_REQUEST_CODE);
*/
       mDbxChooser.forResultType(DbxChooser.ResultType.PREVIEW_LINK).launch(this, DROP_BOX_REQUEST_CODE);

    }

    private void choosFileFromGoogleDrive() {
        Intent intent=new Intent(this, GoogleDriveActivity.class);
        startActivityForResult(intent, GOOGLE_DRIVE_REQUEST_CODE);

    }

    private void startCameraForFile() {
        Intent takingPhotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takingPhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takingPhotoIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void startFileChooser() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(ATTACH_FILE_REQUEST_CODE)
                 // Filtering files and directories by file name using regexp
                .withFilterDirectories(true) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ATTACH_FILE_REQUEST_CODE:
            switch (resultCode) {
                case RESULT_OK:
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    File file = new File(filePath);
                    Log.i("responseData", "File Path : " + filePath);
                    createFileUploadInRetrofit2(file);
                    break;
                case RESULT_CANCELED:
                    break;
            }
            break;
            case CAMERA_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    Bitmap bitmap=(Bitmap)data.getExtras().get("data");
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    try {
                        File image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );
                        FileOutputStream fou=new FileOutputStream(image);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fou);
                        createFileUploadInRetrofit2(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case DROP_BOX_REQUEST_CODE:

                    if(resultCode==RESULT_OK) {
                        DbxChooser.Result result = new DbxChooser.Result(data);
                        Log.i("dropbox-api", "File Name : "+result.getName());
                        Log.i("dropbox-api", "File Link : "+result.getLink());
                        Log.i("dropbox-api", "File thumb : "+result.getThumbnails());

                    }
           /*     break;
            case EvernoteSession.REQUEST_CODE_LOGIN:
              *//*  if (resultCode == RESULT_OK) {

                } else {

                }*//*
                break;*/
        }
    }



    private void createFileUploadInRetrofit2(File file) {
        String contentType= MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        String mimeType=MimeTypeMap.getSingleton().getMimeTypeFromExtension(contentType);
        RequestBody requestBody =RequestBody.create(MediaType.parse(mimeType), file);
        MultipartBody.Part body=MultipartBody.Part.createFormData("fileToUpload", file.getName(), requestBody);
        String description="New String !";
        RequestBody description1=RequestBody.create(MultipartBody.FORM, description);
        new Retrofit.Builder()
                .baseUrl("http://192.168.1.4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProjectApi.class)
                .upload(description1, body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody=response.body();
                        try {
                            InputStream io=responseBody.byteStream();
                            BufferedReader reader=new BufferedReader(new InputStreamReader(io));
                            String line;
                            while ((line=reader.readLine())!=null){
                                Log.i("inputStreamReader", line);
                            }
                        } catch (Exception e) {
                            Log.e("responseDataError", "CommentActivity : "+e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("responseDataError", "Comment Activity : "+ t.toString());
                    }
                });
    }
}