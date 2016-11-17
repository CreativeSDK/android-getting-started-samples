package com.adobe.imageeditorui;

import android.app.Application;

import com.adobe.creativesdk.aviary.IAdobeAuthRedirectCredentials;
import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

/**
 * Created by ash on 3/11/16.
 */
public class MainApplication extends Application implements IAdobeAuthClientCredentials, IAdobeAuthRedirectCredentials {

    private static final String CREATIVE_SDK_CLIENT_ID = Keys.CSDK_CLIENT_ID;
    private static final String CREATIVE_SDK_CLIENT_SECRET = Keys.CSDK_CLIENT_SECRET;
    private static final String CREATIVE_SDK_REDIRECT_URI = Keys.CSDK_REDIRECT_URI;

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String getRedirectUri() {
        return CREATIVE_SDK_REDIRECT_URI;
    }
}