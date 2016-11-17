package com.adobe.userauthui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.adobe.creativesdk.foundation.auth.AdobeAuthException;
import com.adobe.creativesdk.foundation.auth.AdobeAuthSessionHelper;
import com.adobe.creativesdk.foundation.auth.AdobeAuthSessionLauncher;
import com.adobe.creativesdk.foundation.auth.AdobeUXAuthManager;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    /* 1 */
    private static final int USER_AUTH_REQUEST_CODE = 100;

    private AdobeUXAuthManager mUXAuthManager = AdobeUXAuthManager.getSharedAuthManager();
    private AdobeAuthSessionHelper mAuthSessionHelper;

    private static final String mRedirectURI = "ams+e6cbfc3e52b70ebd9c80aa76539c60d8679cfa01://adobeid/4267a5d697a344cca97408ec6762199f";
    private final String[] mAuthScope = {"email", "profile", "address"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuthSessionHelper = new AdobeAuthSessionHelper(mStatusCallback);
        mAuthSessionHelper.onCreate(savedInstanceState);
    }

    private AdobeAuthSessionHelper.IAdobeAuthStatusCallback mStatusCallback;
    {
        mStatusCallback = new AdobeAuthSessionHelper.IAdobeAuthStatusCallback() {
            @Override
            public void call(AdobeAuthSessionHelper.AdobeAuthStatus adobeAuthStatus, AdobeAuthException e) {
                if (!mUXAuthManager.isAuthenticated()) {
                    /* 3 */
                    login();
                } else {
                    /* 4 */
                    Log.d(TAG, "Already logged in!");
                }
            }
        };
    }

    private void login() {
        AdobeAuthSessionLauncher authSessionLauncher = new AdobeAuthSessionLauncher.Builder().withActivity(this)
                .withRedirectURI("")
                .withAdditonalScopes(mAuthScope)
                .withRequestCode(USER_AUTH_REQUEST_CODE)
                .build();

        mUXAuthManager.login(authSessionLauncher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuthSessionHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuthSessionHelper.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuthSessionHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuthSessionHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthSessionHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAuthSessionHelper.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case USER_AUTH_REQUEST_CODE:
                    Log.d(TAG, "User Auth result");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mUXAuthManager.logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
