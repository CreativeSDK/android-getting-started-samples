# Send To Desktop API

The Creative SDK provides a simple way for your mobile users to send their work from your app directly to Adobe desktop apps. 

You can specify which application the shared data will open in. Three applications support this feature: Photoshop, Illustrator, and InDesign. 

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/send-to-desktop-flow.jpeg)

In this guide, we will look at how you can connect your mobile application to the full power of Creative Cloud on the desktop using Send To Desktop.

By the end of the guide, we will have an Android app that lets a user log in to Creative Cloud, then:

1. View an image in an `ImageView`
1. Press a button to send the selected image to Photoshop on their desktop

_**Note:** for Send To Desktop to work, your user must be logged into the **same Creative Cloud account** in your Android app and on the desktop._


## Contents

1. [GitHub](#github)
1. [Prerequisites](#prereqs)
1. [Configuration](#config)
1. [Setting up the UI](#ui-setup)
1. [Making a Send To Desktop helper method](#send-to-desktop-method)
1. [Best practices](#best-practices)
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
compile 'com.adobe.creativesdk.foundation:assetux:0.9.1186'
```


<a name="ui-setup"></a>
## Setting up the UI
Since Send To Desktop is a headless API, you can set up your UI any way you like.

For this example, we will keep things extremely simple with two UI elements:

- A "Send to Photoshop" button
- An `ImageView`

In your `content_main.xml` layout file, add the following `Button` and `ImageView`:

```language-xml
<Button
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="@string/send_to_desktop_button"
    android:id="@+id/sendToDesktopButton"
    android:layout_alignParentTop="true"
    android:layout_centerHorizontal="true" />

<ImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:id="@+id/selectedImageView"
    android:layout_centerVertical="true"
    android:layout_centerHorizontal="true"
    android:adjustViewBounds="true" />
```

Then in `MainActivity.java`, add variables for the `Button` and `ImageView` and assign their values in the `onCreate()` method. 

See the code under comments **#1-4** below (_**Note:** you will need to assign a valid `Uri` to `mSelectedImageUri` for this example to work_):

```language-java
public class MainActivity extends AppCompatActivity {

    private AdobeUXAuthManager mUXAuthManager = AdobeUXAuthManager.getSharedAuthManager();
    private AdobeAuthSessionHelper mAuthSessionHelper;

    /* 1) Create the member variables */
    private Button mSendToDesktopButton;
    private ImageView mSelectedImageView;
    private Uri mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuthSessionHelper = new AdobeAuthSessionHelper(mStatusCallback);
        mAuthSessionHelper.onCreate(savedInstanceState);

        /* 2) Assign values to the member variables */
        mSendToDesktopButton = (Button) findViewById(R.id.sendToDesktopButton);
        mSelectedImageView = (ImageView) findViewById(R.id.selectedImageView);
        mSelectedImageUri = Uri.parse("<YOUR_URI_STRING>");

		/* 3) Set the ImageView Uri */
        mSelectedImageView.setImageURI(mSelectedImageUri);

    }
    // ...
```

Your app will look something like this:

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/send-to-desktop.png)

We will use this basic setup to send the image from the Android device to Photoshop on the desktop.

<a name="send-to-desktop-method"></a>
## Making a `sendToDesktop()` helper method
We will use [the `AdobeSendToDesktopApplication.sendToDesktop()` method](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/sendtodesktop/AdobeSendToDesktopApplication.html) to send our image to the desktop. 

The `AdobeSendToDesktopApplication.sendToDesktop()` method supports sending the following asset types: 

- `Uri`
- `AdobeAssetFile`
- `Bitmap`
- `InputStream`

In this case, we have an `android.net.Uri`, so we will pass that as an argument to our own `sendToDesktop()` method that we are creating here (see comments **#1-3** below):

```language-java
private void sendToDesktop() throws IOException {

    /* 1) Specify the Adobe desktop app to send to */
    AdobeCreativeCloudApplication creativeCloudApplication = AdobeCreativeCloudApplication.AdobePhotoshopCreativeCloud;

    /* 2) Make a callback to handle success and error */
    final IAdobeSendToDesktopCallBack sendToDesktopCallBack = new IAdobeSendToDesktopCallBack() {
        @Override
        public void onSuccess() {
            Toast.makeText(MainActivity.this, "Opening in Photoshop on your desktop!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(AdobeSendToDesktopException e) {
            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
    };

    /* 3) Send the image to the desktop! */
    AdobeSendToDesktopApplication.sendToDesktop(mSendToPhotoshopUri, "image/jpeg", creativeCloudApplication, sendToDesktopCallBack);
}
```

Now, go to your Activity's `onCreate()` method and add a `View.OnClickListener` to the button:

```language-java
View.OnClickListener sendToDesktopButtonListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        try {
            sendToDesktop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
};
mSendToDesktopButton.setOnClickListener(sendToDesktopButtonListener);
```

If your user is logged in to the same Creative Cloud account on the desktop, Photoshop will open with the image that we sent from the device:

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/send-to-desktop-flow.jpeg)

See the Best Practices section below for how to improve the Send To Desktop experience. 

<a name="best-practices"></a>
## Best Practices


### Adding a `ProgressBar`
Since Send To Desktop is sending the image over a wireless network, it can take some time before `IAdobeSendToDesktopCallBack.onSuccess()` or `onError()` is called. For this reason, it is recommendable to make good use of an Android `ProgressBar` to let your user know that the application is working.

[You can read more about Android's `ProgressBar` in the Android documentation](http://developer.android.com/reference/android/widget/ProgressBar.html).


<a name="troubleshooting"></a>
## Troubleshooting and Known Issues
Articles about common issues are at [help.creativesdk.com](http://help.creativesdk.com/), along with a place to submit tickets for bugs, feature requests, and general feedback.


<a name="class-reference"></a>
## Class Reference
In this guide, we used the classes in the list below. Explore the documentation for more class methods and details.

- [AdobeSendToDesktopApplication](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/sendtodesktop/AdobeSendToDesktopApplication.html)
- [AdobeCreativeCloudApplication](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/sendtodesktop/AdobeCreativeCloudApplication.html)
- [IAdobeSendToDesktopCallBack](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/sendtodesktop/IAdobeSendToDesktopCallBack.html)
- [AdobeSendToDesktopException](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/sendtodesktop/AdobeSendToDesktopException.html)