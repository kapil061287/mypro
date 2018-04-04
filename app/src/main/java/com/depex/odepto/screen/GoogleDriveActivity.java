package com.depex.odepto.screen;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.depex.odepto.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleDriveActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int REQUEST_CODE_SIGN_IN = 2;
    private static final int REQUEST_CODE_OPEN_DIALOG = 3;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private Metadata mMetadata;
    private String TAG="google-drive-access";
    private DriveContents mDriveCotents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive);
        ButterKnife.bind(this);
        toolbar.setTitle("Connecting google ...");
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        signIn();

    }


    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }



    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_SIGN_IN:
                    if(resultCode==RESULT_OK){
                        Log.i(TAG, "Signed in successfully.");
                        mDriveClient=Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
                        mDriveResourceClient=Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                        downloadFileFromDrive();
                    }
                break;
            case REQUEST_CODE_OPEN_DIALOG:
                    Bundle bundle=data.getExtras();
                DriveId driveId=bundle.getParcelable(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                loadFileFromDrive(driveId);
                //writeFileFromDrive(driveId);
                break;
        }
    }



    private void loadFileFromDrive(DriveId driveId) {
        DriveFile driveFile=driveId.asDriveFile();
        mDriveResourceClient.getMetadata(driveFile)
                .continueWithTask(new Continuation<Metadata, Task<DriveContents>>() {
                    @Override
                    public Task<DriveContents> then(@NonNull Task<Metadata> task) throws Exception {
                        if(task.isSuccessful()){
                            mMetadata=task.getResult();
                            String webLink=mMetadata.getAlternateLink();
                            finishThisActivity(webLink);
                            Log.i(TAG, webLink+"");


                           // return mDriveResourceClient.openFile(driveFile, DriveFile.MODE_READ_WRITE);
                            return null;

                        }else {
                            return Tasks.forException(task.getException());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DriveContents>() {

            @Override
            public void onSuccess(DriveContents driveContents) {
                mDriveCotents=driveContents;

            }
        });
    }

    private void finishThisActivity(String webLink) {
        Bundle bundle=new Bundle();
        bundle.putString("webLink", webLink);
        Intent intent=new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void downloadFileFromDrive() {
        Log.i(TAG, "Creating new contents.");
        mDriveResourceClient.createContents()
                .continueWithTask(
                        new Continuation<DriveContents, Task<Void>>() {

            @Override
            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                return createFileIntentSender(task.getResult());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Drive Api  : "+e.toString());
            }
        });
    }


    public void writeFileFromDrive(DriveId driveId){
        DriveFile driveFile=driveId.asDriveFile();
        Task<DriveContents> openFileTask=mDriveResourceClient.openFile(driveFile, DriveFile.MODE_READ_ONLY);
        openFileTask.continueWithTask(
                new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {

                        mDriveCotents=task.getResult();
                        InputStream io=mDriveCotents.getInputStream();
                        try(BufferedReader reader=new BufferedReader(new InputStreamReader(io))){
                            StringBuilder sb=new StringBuilder();
                            String line;
                            while ((line=reader.readLine())!=null){
                                sb.append(line);
                            }
                            Log.i(TAG, ""+sb.toString());
                        }
                        return null;
                    }
                }
        );
    }




    private Task<Void> createFileIntentSender(DriveContents driveContents){
        Log.i(TAG, "Creating file intent sender !");
        OpenFileActivityOptions openFileActivityOptions=new OpenFileActivityOptions.Builder().build();
        return  mDriveClient.newOpenFileActivityIntentSender(openFileActivityOptions)
                .continueWith(
                        new Continuation<IntentSender, Void>() {
                    @Override
                    public Void then(@NonNull Task<IntentSender> task) throws Exception {
                        startIntentSenderForResult(task.getResult(), REQUEST_CODE_OPEN_DIALOG, null, 0, 0,0);
                        return null;
                    }
                });
    }
}