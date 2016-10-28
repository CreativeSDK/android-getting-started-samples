# Typekit UI

With the Creative SDK, you can provide your users with access to Typekit fonts that are available to them in the Creative Cloud.

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/typekit-selector.png)

In this guide, we will look at how your users can manage their Typekit Synced Fonts list and how you can let them access and use their Typekit fonts, all right from within your app.

By the end of this guide, we will have an Android app that:

1. Launches the Typekit selector
1. Downloads a list of your user's Typekit Synced List fonts
1. Applies a Typekit font to a `TextView`


## Contents

1. [GitHub](#github)
1. [Prerequisites](#prereqs)
1. [Configuration](#config)
1. [Launching the Typekit Font Browser](#launch-browser)
1. [Accessing fonts](#access-fonts)
1. [Troubleshooting and Known Issues](#troubleshooting)
1. [Class Reference](#class-reference)


<a name="github"></a>
## GitHub

You can find companion GitHub repos for the Creative SDK developer guides [on the Creative SDK GitHub organization](https://github.com/CreativeSDK/android-getting-started-samples). 

Be sure to follow all instructions in the `readme`.


<a name="prereqs"></a>
## Prerequisites
This guide will assume that you have installed all software and completed all of the steps in the following guides:

- [Getting Started](https://creativesdk.adobe.com/docs/android/#/articles/gettingstarted/index.html)
- [User Auth UI](https://creativesdk.adobe.com/docs/android/#/articles/userauth/index.html)

_**Note:**_

- _This component requires that the user is **logged in with their Adobe ID**._
- _Your Client ID must be [approved for **Production Mode** by Adobe](https://creativesdk.zendesk.com/hc/en-us/articles/204601215-How-to-complete-the-Production-Client-ID-Request) before you release your app._


<a name="config"></a>
## Configuration
Add the following Creative SDK dependencies to your _Module_ `build.gradle` file:

```language-java
/* Add the CSDK framework dependencies (Make sure these version numbers are correct) */
compile 'com.adobe.creativesdk.foundation:auth:0.9.1186'
compile 'com.adobe.creativesdk:typekit:0.9.1186'
```


<a name="launch-browser"></a>
## Launching the Typekit Font Browser

The Typekit UI component provides an Activity with a UI that allows your users to:

1. Browse all fonts available to them
1. Manage the contents of their "Synced Fonts" list

Your app can provide access to any font that your user has stored in their "Synced Fonts" list:

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/typekit-synced-fonts.png)

Launching the Typekit UI component is as simple as making one method call:

```
AdobeTypekitFontBrowser.launchActivity(context);
```

Common use patterns include wrapping this method call in a button's on-click listener or associating it with a Toolbar menu item. (For this example, go ahead and add the `launchActivity()` method in a menu item.)

_**Note:** If you followed the steps in the User Auth UI guide, and are unable to get the Typekit Font Browser to launch, try logging out of your Adobe ID in the app and logging back in._


<a name="access-fonts"></a>
## Accessing fonts

You can access Typekit fonts from the user's Synced Fonts list using APIs contained within the Typekit classes.

As a simple example, let's have a look at getting a random font from the user's Synced Fonts list and using the font to set the typeface of a `TextView`.

1. [Initializing the `AdobeTypekitManager`](#typekit-manager)
1. [Syncing fonts and triggering Typekit notifications](#syncing-fonts)
1. [Observing Typekit notifications](#typekit-notifications)
1. [Applying a Typekit font](#apply-font)


<a name="typekit-manager"></a>
### Initializing the `AdobeTypekitManager`

The `AdobeTypekitManager` class manages the downloading of Typekit fonts and other related information. Correct use of the `AdobeTypekitManager` is **required** for accessing Typekit fonts.

_**Note:** Your user must be logged in with their Adobe ID **before** you initialize the `AdobeTypekitManager`. Your `AdobeAuthSessionHelper.IAdobeAuthStatusCallback` is a good place to initialize the `AdobeTypekitManager`._

You can initialize the `AdobeTypekitManager` like this (see comments **#1-3**):

```language-java
public class MainActivity extends AppCompatActivity {

    // ...

    private AdobeUXAuthManager mUXAuthManager = AdobeUXAuthManager.getSharedAuthManager();
    private AdobeAuthSessionHelper mAuthSessionHelper;

    /* 1) Store an instance of `AdobeTypekitManager` as a member variable */
    private AdobeTypekitManager mTypekitManager = AdobeTypekitManager.getInstance();

    // ...

    private AdobeAuthSessionHelper.IAdobeAuthStatusCallback mStatusCallback;
    {
        mStatusCallback = new AdobeAuthSessionHelper.IAdobeAuthStatusCallback() {
            @Override
            public void call(AdobeAuthSessionHelper.AdobeAuthStatus adobeAuthStatus, AdobeAuthException e) {
                if (AdobeAuthSessionHelper.AdobeAuthStatus.AdobeAuthLoggedIn == adobeAuthStatus) {

                    /* 2) Call a helper method to initialize the `AdobeTypekitManager` */
                    initializeTypekitManager();
                    
                    showAuthenticatedUI();
                } else {
                    showAdobeLoginUI();
                }
            }
        };
    }

    /* 3) Make a helper method to initialize the `AdobeTypekitManager */
    private void initializeTypekitManager() {
        try {
            mTypekitManager.init(this);
        } catch (UserNotAuthenticatedException e) {
            e.printStackTrace();

            Toast.makeText(this, "Please log in to Creative Cloud to use Typekit fonts!", Toast.LENGTH_LONG).show();
        }
    }

    // ...
```

In the try/catch statement above, if your user is not yet logged into Creative Cloud via your app, they will see a `Toast` prompting them to do so. (If you followed the steps in our [User Auth UI guide](https://creativesdk.adobe.com/docs/android/#/articles/userauth/index.html), the user will have already logged in.)


<a name="syncing-fonts"></a>
### Syncing fonts and triggering Typekit notifications

You can get the list of all fonts in the user's Synced Fonts list by calling the `syncFonts()` method, often via a button, a menu item, or directly in your Activity's `onCreate()` method.

For this example, add the following method call in a button's `OnClickListener`:

```
mTypekitManager.syncFonts();
```

The `syncFonts()` method is _asynchronous_. 

To get the results, your app should observe the `TypekitManager` for notification events listed in the `TypekitNotification.Event` enum, as covered in the next section.

_**Note:** For brevity in this guide, we are calling `syncFonts()` every time we want to apply a font. This is not the most performant approach, since two round trips are required (to sync the list, then to get the font). Be sure to find the right balance of keeping the list in sync and minimizing `syncFonts()` calls._

<a name="typekit-notifications"></a>
### Observing Typekit notifications

As noted above, the `syncFonts()` method is _asynchronous_. To know when the operation is complete (and the user's Typekit Synced Fonts list has been synced to your app), we need to observe any `TypekitNotification` coming from the `TypekitManager`.

See comments **#1-3** in the example below:

```language-java
/* 1) Implement the `Observer` interface  */
public class MainActivity extends AppCompatActivity implements Observer {

    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...

        try {
            mTypekitManager.init(this);

            /* 2) Add the Activity as an Observer to watch for Typekit notifications */
            mTypekitManager.addObserver(this);
        } catch (UserNotAuthenticatedException e) {
            e.printStackTrace(); // Error : User not authenticated.

            Toast.makeText(this, "Please log in to Creative Cloud to use Typekit fonts!", Toast.LENGTH_LONG).show();
        }
    }

    // ...

    /* 3) Implement the `Observer` interface method */
    @Override
    public void update(Observable observable, Object data) {
    }

    // ...
```

Within the `update()` method above, we will watch for `TypekitNotification` events, and have the app react depending on the notification:

```language-java
@Override
public void update(Observable observable, Object data) {
    
    TypekitNotification notification = (TypekitNotification) data;
    
    switch (notification.getTypekitEvent()) {

        case TypekitNotification.Event.FONT_SELECTION_SYNC_START:
            Toast.makeText(MainActivity.this, "Syncing Typekit Synced Fonts list...", Toast.LENGTH_SHORT).show();
            break;

        case TypekitNotification.Event.FONT_SELECTION_REFRESH:
            ArrayList<AdobeTypekitFont> syncList = AdobeTypekitFont.getFonts();
            Random random = new Random();
            applyFont(syncList.get(random.nextInt(syncList.size())));
            break;

        case TypekitNotification.Event.FONT_SELECTION_SYNC_ERROR:
            Log.e(MainActivity.class.getSimpleName(), "Error: " + notification.getTypekitEvent());
            break;
        
        case TypekitNotification.Event.FONT_NETWORK_ERROR:
            Log.e(MainActivity.class.getSimpleName(), "Error: " + notification.getTypekitEvent());
            break;
        
        case TypekitNotification.Event.FONT_CACHE_EXPIRY:
            Log.e(MainActivity.class.getSimpleName(), "Warning: " + notification.getTypekitEvent());
            break;
        
        default:
            break;
    }
}
```

Note that the `FONT_SELECTION_SYNC_START` notification indicates when the font sync starts, and `FONT_SELECTION_REFRESH` indicates when the sync list has been successfully updated.

In the basic example above, when we get the `FONT_SELECTION_REFRESH` notification, we get our user's Typekit Synced Fonts list in the `syncList` variable, and pass a random font to `applyFont()`. 

We will make the `applyFont()` helper method in the next step.


<a name="apply-font"></a>
### Applying a Typekit font

There are two ways to a get a Typekit font typeface:

1. `AdobeTypekitFont.getSubsetTypeface()`
2. `AdobeTypekitFont.getTypeface()`

If you already know the entire target string beforehand, such as when applying a font to a `TextView`, use `getSubsetTypeface()`, which only downloads the characters you need and will therefore be faster.

(Before moving forward, be sure to add the two `TextView` widgets (`mTargetTextView` and `mFontNameTextView`), referenced in the sample code below, to your layout.)

Below is an example of using the `getSubsetTypeface()` method (see comments **#1-4**):

```language-java
private void applyFont(AdobeTypekitFont adobeTypekitFont) {

    /* 1) Get the string you will apply the typeface to */
    String targetString = mTargetTextView.getText().toString();

    /* 2) Pass the string and a callback to `getSubsetTypeface()` */
    adobeTypekitFont.getSubsetTypeface(targetString, new AdobeTypekitFont.ITypekitCallback<Typeface, String>() {
        @Override
        public void onSuccess(AdobeTypekitFont adobeTypekitFont, Typeface typeface) {

            /* 3) Handle success */
            mTargetTextView.setTypeface(typeface);
            mFontNameTextView.setText(String.format("Font name: %s", adobeTypekitFont.displayName()));
        }

        @Override
        public void onError(AdobeTypekitFont adobeTypekitFont, String s) {

            /* 4) Handle errors */
            Log.e(MainActivity.class.getSimpleName(), s);
        }
    });
}
```

Conversely, if you don't know the target string in advance, such as when applying the typeface to an `EditText` widget, or when you know the target string will change, use `getTypeface()`. This method works the same as the above example, but you only need to pass in a callback.

At this point, you will have an app that looks something like this:

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/typekit-apply-font.png)

You have now integrated the Creative SDK's Typekit UI component! 

While this is a very basic example of how to use the Creative SDK Typekit APIs, you can learn more [on the Creative SDK GitHub organization](https://github.com/CreativeSDK/android-getting-started-samples), including how to make your own font picker with a `Spinner` widget, and how to apply a font to an `EditText` widget.


<a name="troubleshooting"></a>
## Troubleshooting and Known Issues
Articles about common issues are at [help.creativesdk.com](http://help.creativesdk.com/), along with a place to submit tickets for bugs, feature requests, and general feedback.


<a name="class-reference"></a>
## Class Reference
In this guide, we used the classes in the list below. Explore the documentation for more class methods and details.

- AdobeTypekitFontBrowser
- AdobeTypekitManager
- TypekitNotification
- TypekitNotification.Event
- AdobeTypekitFont
- AdobeTypekitFont.ITypekitCallback