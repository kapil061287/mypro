package com.depex.odepto;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.depex.odepto.activity.BaseGoogleDriveActivity;
import com.depex.odepto.adpater.AttachFromAdapter;
import com.depex.odepto.adpater.AttachmentListModel;
import com.depex.odepto.api.ProjectApi;
import com.depex.odepto.recent.Card;
import com.depex.odepto.screen.GoogleDriveActivity;
import com.depex.odepto.services.DownloadRandomPicture;
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
import com.google.gson.Gson;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.nbsp.materialfilepicker.utils.FileUtils;

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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Okio;
import okio.Sink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CommentActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, OnVolleySuccessListener {
    private static final String TAG = "commentActivityTag";
    private static final int DROP_BOX_REQUEST_CODE=1;
    private static final int GOOGLE_DRIVE_REQUEST_CODE=2;
    private static final int CAMERA_REQUEST_CODE=3;
    private static final int ATTACH_FILE_REQUEST_CODE=4;
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
    Card boardCard;
    RecyclerView attachmentRecyclerView;

    private final String ACCOUNT_PREFS_NAME="prefs";



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
            JSONObject jsonObject=createJsonFromCardLabelList(boardCard.getCardId());
            Utility.getJsonFromHttp(this, jsonObject, this,  Utility.apiUrl);
        }
        showComment();
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
                listModels.add(dropBoxModal);

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
            Log.i("resposneData", "Comment Activity : "+e.toString());
        }
    }


    @Override
    public void onError(VolleyError error) {
        Log.e("onSuccessListenter", error.toString());
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