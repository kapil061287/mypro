package com.depex.odepto.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.depex.odepto.R;
import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DropboxApiActivity extends AppCompatActivity {

    private static final int DBX_CHOOSER_REQUEST = 2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    final static private String APP_KEY = "sauids7y2n12nue";
    final static private String APP_SECRET = "a7tpevka8ue3vey";
    private DropboxAPI<AndroidAuthSession> mDropboxApi;
    private final String TAG="dropbox-api";
    DbxChooser mDbxChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_api);
        ButterKnife.bind(this);
        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);


        mDbxChooser=new DbxChooser(APP_KEY);

        //Build Android auth session for drop box
        //buildAuthSession();

        mDbxChooser.forResultType(DbxChooser.ResultType.PREVIEW_LINK).launch(this, DBX_CHOOSER_REQUEST);

    }

    private void buildAuthSession() {
        AppKeyPair pair=new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session=new AndroidAuthSession(pair);
        mDropboxApi=new DropboxAPI<>(session);
        mDropboxApi.getSession().startOAuth2Authentication(this);
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        if(mDropboxApi.getSession().authenticationSuccessful()){
            try{
                mDropboxApi.getSession().finishAuthentication();
                String accessToken=mDropboxApi.getSession().getOAuth2AccessToken();
                Log.i(TAG, accessToken);


            }catch (Exception e){
                Log.e(TAG, "DropBoxApi  : "+e.toString());
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==DBX_CHOOSER_REQUEST){
            if(resultCode==RESULT_OK){
                DbxChooser.Result result = new DbxChooser.Result(data);
                Bundle bundle=new Bundle();
                Intent intent=new Intent();
                bundle.putString("name", result.getName());
                bundle.putString("link", result.getLink().toString());
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
            }
        }
    }
}