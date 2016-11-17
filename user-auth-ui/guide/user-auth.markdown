# User Auth UI

Most Creative SDK components require an authenticated user. The User Auth UI component provides the user a familiar Adobe ID login screen where they can enter their Adobe ID username and password.

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/user-auth-login.png)

In this guide, we will cover how to display the Creative SDK User Auth UI component and check for an authenticated user.

By the end of this guide, we will have an Android app that:

1. Shows the Adobe ID Login screen on launch (if a user is not already logged in)
2. Shows the user the Main Activity (if a user is logged in)
3. Allows the user to log out from the app menu


## Contents

1. [GitHub](#github)
1. [Prerequisites](#prereqs)
1. [Configuration](#config)
1. [Allowing the user to log in](#login)
1. [Activity Lifecycle methods](#lifecycle)
1. [Allowing the user to log out](#logout)
1. [Troubleshooting and Known Issues](#troubleshooting)
1. [Class Reference](#class-reference)


<a name="github"></a>
## GitHub

You can find companion GitHub repos for the Creative SDK developer guides [on the Creative SDK GitHub organization](https://github.com/CreativeSDK/android-getting-started-samples). 

Be sure to follow all instructions in the `readme`.


<a name="prereqs"></a>
## Prerequisites
This guide will assume that you have installed all software and completed all of the steps in [the Getting Started guide](https://creativesdk.adobe.com/docs/android/#/articles/gettingstarted/index.html).

- _Your Client ID must be [approved for **Production Mode** by Adobe](https://creativesdk.zendesk.com/hc/en-us/articles/204601215-How-to-complete-the-Production-Client-ID-Request) before you release your app._
- _Make sure you have the Redirect URI that you receieved during the "Registering Your Application" section of [the Getting Started guide](https://creativesdk.adobe.com/docs/android/#/articles/gettingstarted/index.html). We will use during this guide._


<a name="config"></a>
## Configuration
Add the following Creative SDK dependency to your _Module_ `build.gradle` file:

```language-java
/* Add the CSDK framework dependency (Make sure the version number is correct) */
compile 'com.adobe.creativesdk.foundation:auth:0.9.1186'
```


<a name="login"></a>
## Allowing the user to log in
For this example, user login involves the following flow in the Main Activity.

1. The `AdobeUXAuthManager` is initialized. _**Note:** this must be done before auth operations such as login and logout._
1. The `AdobeAuthSessionHelper` retrieves the user's current auth status and supplies it in a callback.
1. If the user is not logged in, they will see the Adobe ID login screen.
1. You launch the Adobe ID login screen by calling `AdobeUXAuthManager.login()` and passing in an `AdobeAuthSessionLauncher`


These steps are numbered **#1-4** in the code comments below:

```language-java
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    /* 1 */
    private AdobeUXAuthManager mUXAuthManager = AdobeUXAuthManager.getSharedAuthManager();
    private AdobeAuthSessionHelper mAuthSessionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...

        /* 2 */
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
                    Log.d(TAG, "Already logged in!");
                }
            }
        };
    }

    /* 4 */
    private void login() {
        private final String[] authScope = {"email", "profile", "address"};

        AdobeAuthSessionLauncher authSessionLauncher = new AdobeAuthSessionLauncher.Builder()
                .withActivity(this)
                .withRedirectURI("<YOUR_REDIRECT_URI_HERE>")
                .withAdditonalScopes(authScope)
                .withRequestCode(1001) // Can be any int
                .build();

        mUXAuthManager.login(authSessionLauncher);
    }

    // ...
```

Note that as part of building the `AdobeAuthSessionLauncher`, you call:

1. `withRedirectURI()`, passing in the Redirect URI that you received during the "Registering Your Application" section of [the Getting Started guide](https://creativesdk.adobe.com/docs/android/#/articles/gettingstarted/index.html)
1. `withAdditonalScopes()`, passing in a String array of User Auth scopes

**These are both required** to authenticate a user. 

The Redirect URI helps authenticate your client to Adobe. The additional scopes let the user authorize your app for the required level of access to their information (the user will be shown a screen noting what information they are authorizing access for).

<a name="overrides"></a>
## Activity Lifecycle methods
The `AdobeAuthSessionHelper` contains methods for checking the user's auth status during all parts of the Activity Lifecycle. 

Be sure to override the following Activity methods, calling the related `AdobeAuthSessionHelper` methods within:

```language-java
    // ...

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
    }

    // ...
}
```


<a name="logout"></a>
## Allowing the user to log out
Allowing a user to log out is quite simple. All we need to do is call the `AdobeUXAuthManager.logout()` method. This can be done in as little as one line of code.

For example, in our Main Activity, if we have a menu item with an ID of `action_logout` in `menu_main.xml`, we could do the following (see comments **#1-2**):

```language-java
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* 1) If the user selects the logout action from the menu */
        if (id == R.id.action_logout) {
            /* 2) Log the user out */
            mUXAuthManager.logout();
        }

        return super.onOptionsItemSelected(item);
    }
```

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/user-auth-logout.png)

Now that you have implemented User Auth, you're ready to move on the other Creative SDK components!


<a name="troubleshooting"></a>
## Troubleshooting and Known Issues
Articles about common issues are at [help.creativesdk.com](http://help.creativesdk.com/), along with a place to submit tickets for bugs, feature requests, and general feedback.


<a name="class-reference"></a>
## Class Reference
In this guide, we used the classes in the list below. Explore the documentation for more class methods and details.

- [AdobeAuthSessionHelper](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/auth/AdobeAuthSessionHelper.html)
- [AdobeAuthSessionHelper.AdobeAuthStatus](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/auth/AdobeAuthSessionHelper.AdobeAuthStatus.html)
- [AdobeAuthSessionHelper.IAdobeAuthStatusCallback](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/auth/AdobeAuthSessionHelper.IAdobeAuthStatusCallback.html)
- [AdobeUXAuthManager](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/auth/AdobeUXAuthManager.html)