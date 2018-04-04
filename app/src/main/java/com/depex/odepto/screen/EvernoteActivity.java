package com.depex.odepto.screen;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.depex.odepto.CommentActivity;
import com.depex.odepto.R;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EvernoteActivity extends AppCompatActivity implements EvernoteLoginFragment.ResultCallback {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static final String CONSUMER_KEY = "kapil61287-5022";
    private static final String CONSUMER_SECRET = "c1a5aed569f256a6";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    private String TAG="evernote-api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evernote);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);



        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
//                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();

        if(!EvernoteSession.getInstance().isLoggedIn()) {
            Log.i("evernote-api", "Not Loged in : "+EvernoteSession.getInstance()+"");
            EvernoteSession.getInstance().authenticate(this);
        }else {
                getNotesFromEverNote();
        }

    }

    private void getNotesFromEverNote() {
        EvernoteClientFactory factory=EvernoteSession.getInstance().getEvernoteClientFactory();
        EvernoteNoteStoreClient client=factory.getNoteStoreClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url=factory.getUserStoreClient().getNoteStoreUrl();
                    Log.i(TAG, "URL  : "+url);
                } catch (EDAMUserException e) {
                    e.printStackTrace();
                } catch (EDAMSystemException e) {
                    e.printStackTrace();
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onLoginFinished(boolean successful) {
        Log.i(TAG, "Login Result : "+successful);
    }
}