# Image Editor UI

The Creative SDK Image Editor UI component provides a solution for developers seeking to add powerful photo editing to their Android apps. 

The Image Editor includes over twenty advanced imaging tools covering everything from Effects and Crop to Redeye and Blemish. The tools are all GPU-accelerated, so all image modifications happen in real time or close to it.

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/image-editor.png)

In this Image Editor guide, we will walk through how add the Creative SDK Image Editor to your Android Studio project. Integrating the Image Editor typically takes less than 15 minutes of development time.

By the end of the guide, we will have an Android app that:

1. Launches the Image Editor when the app loads for the first time
1. Lets users edit an image
1. Shows users the resulting image in the Main Activity


<a name="contents"></a>
## Contents

1. [GitHub](#github)
1. [Prerequisites](#prerequisites)
1. [Configuration](#config)
1. [Adding the Creative SDK Image Editor](#image-editor)
1. [Handling Activity Results](#activity-results)
1. [Options, Customization, and Optimization](#options)
1. [Troubleshooting and Known Issues](#troubleshooting)
1. [Class Reference](#reference)


<a name="github"></a>
## GitHub

You can find companion GitHub repos for the Creative SDK developer guides [on the Creative SDK GitHub organization](https://github.com/CreativeSDK/android-getting-started-samples). 

Be sure to follow all instructions in the `readme`.


<a name="prerequisites"></a>
## Prerequisites
This guide will assume that you have installed all software and completed all of the steps in [the Getting Started guide](https://creativesdk.adobe.com/docs/android/#/articles/gettingstarted/index.html).

_**Note:**_

- _This component offers a better experience for users when they log in with their Adobe ID (no further code required)._
- _Your Client ID must be [approved for **Production Mode** by Adobe](https://creativesdk.zendesk.com/hc/en-us/articles/204601215-How-to-complete-the-Production-Client-ID-Request) before you release your app._


<a name="config"></a>
## Configuration

1. Add the Creative SDK dependencies

    First, let's add the following Creative SDK dependencies to your _Module_ `build.gradle` file:

    ```language-java
    /* Add the CSDK framework dependencies (Make sure these version numbers are correct) */
    compile 'com.adobe.creativesdk.foundation:auth:0.9.1186'
    compile 'com.adobe.creativesdk:image:4.6.3'
    compile 'com.localytics.android:library:3.8.0'
    ```


1. Add `manifestPlaceholders` to Gradle

    In your `build.gradle` file, add a `manifestPlaceholders` value:

    ```language-java
    android {
        // ...

        defaultConfig {
            // ...

            manifestPlaceholders = [appPackageName: "${applicationId}"]
        }
    }
    ```

    This allows the SDK manifest to add its own requirements to the `AndroidManifest`.

    _**Tip:** You can see the generated manifest file at `app/build/intermediates/manifests/full/debug/`._

1. Add the Maven repository for Localytics

    In your _Project_ `build.gradle` file, add the Maven Repository for Localytics

    ```language-java
    allprojects {
        repositories {

            // ...

            maven {
                url 'http://maven.localytics.com/public'
            }
        }
    }
    ``` 

The app is now configured and ready to integrate the Image Editor.


<a name="image-editor"></a>
## Adding the Creative SDK Image Editor
As mentioned in the [Overview section for this guide](#overview), our sample app will launch the Image Editor automatically when the app loads for the first time.

Accordingly, we will add the code related to launching the Image Editor in our Main Activity's `onCreate()` method.
 
See comments **#1-3** in the Main Activity's `onCreate()` method below:

```language-java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    /* 1) Make a new Uri object (Replace this with a real image on your device) */
    Uri imageUri = Uri.parse("content://media/external/images/media/####");

    /* 2) Create a new Intent */
    Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
        .setData(imageUri)
        .build();

    /* 3) Start the Image Editor with request code 1 */
    startActivityForResult(imageEditorIntent, 1);
}
```

Now the Image Editor will launch automatically the first time the app loads. When the Image Editor launches, it will load with the image we set in our `imageUri` variable.


<a name="activity-results"></a>
## Handling Activity Results
In this section, we will complete the final step in the basic app that we are creating: displaying the edited image in the Main Activity UI layout after the user edits the image and clicks "Done".

1. Add an `ImageView` to the layout

    Let's add an `ImageView` to the Main Activity's layout:

    ```language-xml
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        // ...

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/editedImageView"
            android:adjustViewBounds="true"
            android:layout_centerHorizontal="true"
            android:layout_centerVertical="true"/>
    </RelativeLayout>
    ```

1. Display the resulting image

    See comments **#1-5** in the `MainActivity` code below:

    ```language-java
    public class MainActivity extends AppCompatActivity {

        /* 1) Add a member variable for our Image View */
        private ImageView mEditedImageView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // ...

            /* 2) Find the layout's ImageView by ID */
            mEditedImageView = (ImageView) findViewById(R.id.editedImageView);

            Uri imageUri = Uri.parse("content://media/external/images/media/####");

            Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                    .setData(imageUri)
                    .build();

            startActivityForResult(imageEditorIntent, 1);
        }

        // ...

        /* 3) Handle the results */
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {

                    /* 4) Make a case for the request code we passed to startActivityForResult() */
                    case 1:
                        
                        /* 5) Show the image! */
                        Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                        mEditedImageView.setImageURI(editedImageUri);

                        break;
                }
            }
        }
    }    
    ```

    Using this code, you would get the edited image back and display it in the `ImageView`. Here is an example of a result using a black and white filter:

    ![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/image-editor-edited-image.png)

    Of course, this is just a basic example of what you might do in `onActivityResult()`. Below are a couple of other examples of things you might do within a switch case:

    ```language-java
    /* Log the image URI in the Android Studio console */
    Log.d("URI!", editedImageUri.toString());

    /* Find out if the image has been edited */
    Bundle extra = data.getExtras();
    if (extra != null) {
        boolean changed = extra.getBoolean(AdobeImageIntent.EXTRA_OUT_BITMAP_CHANGED);
        // ... do stuff
    }
    ```

This is everything you need for integrating the Creative SDK Image Editor UI component! For more advanced customization, read on.


<a name="options"></a>
## Options, Customization, and Optimization
The Creative SDK Image Editor offers a number of customization options for look and behavior so developers can tailor it to suit their needs.

- [Optional Image Editor Methods](#methods)
- [Preloading the Content Delivery Service](#preloading-cds)
- [Implementing High Resolution Image Editing](#high-res)
- [Adding Custom Exif Tags](#exif-tags)
- [Customizing the Image Editor UI](#customizing)
- [Validating your configuration](#validating)


<a name="methods"></a>
### Optional Image Editor Methods

The `AdobeImageIntent.Builder` class contains a number of methods that allow you to customize the functionality of the Image Editor. The only required method is `setData()`.

A typical example of utilizing some of these methods might be:

```language-java
Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
        .setData(uri) // input image source
        .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
        .withOutputSize(MegaPixels.Mp5) // output size
        .withOutputQuality(90) // output quality
        .build();
```

|Method name                                |Description                                            |
|---                                        |---                                                    |
|setData(Uri) **(* required)**                  |Sets the input image Uri.<br><br>Supported schemes are: <br>- [scheme_file](http://developer.android.com/reference/android/content/ContentResolver.html#SCHEME_FILE) (e.g. file://mnt/sdcard/download/image.jpg)<br>- [scheme_content](http://developer.android.com/reference/android/content/ContentResolver.html#SCHEME_CONTENT) (e.g. content://media/external/images/media/112232)<br><br>_Note: `http` will work for low-resolution editing only, but it is not a feature that we currently support._ |
|withToolList(ToolLoaderFactory.Tools[])    |Sets the selected list of tools to be shown to the user. An example of creating a `tools` argument:<br><br>`ToolLoaderFactory.Tools[] tools;`<br>`tools = {ToolLoaderFactory.Tools.EFFECTS};`<br>|
|withOutput(File)<br>withOutput(Uri)        |Sets the output location where the edited image will be saved.|
|withOutputFormat(Bitmap.CompressFormat)    |Defines the edited image’s file format.<br><br>Accepted values are JPEG or PNG.  |
|withOutputQuality(int)                     |If the outputformat is JPEG, defines the quality of the saved JPEG image.   |
|withOutputSize(MegaPixels)                 |Sets the output file size (in megapixels).<br><br>If this method is not called, or a value of 0 is passed, then the preview sized image will be returned (usually the screen size of the device).   |
|saveWithNoChanges(boolean)                 |Indicates what to do if the user presses the “Done” button without making changes to the image.<br><br>If `true` is passed, the image will always be saved and a `RESULT_OK` will be returned to your `onActivityResult()` method. Further, the returned `Intent` will have the `EXTRA_OUT_BITMAP_CHANGED` extra with a value of `false`.<br><br>If false is passed, then you will receive a `RESULT_CANCELED`, and the image will not be saved at all. The returned `Intent` will contain the `EXTRA_OUT_BITMAP_CHANGED` extra, with a value of `false`.|
|withOptions(Bundle)                        |An optional bundle that you can receive back in your `onActivityResult()` method.                  |
|withPreviewSize(int)                       |Changes the size of the preview used in the editor.<br><br>This is not the size of the output file, but only the size of the preview used during the edit.   |
|withNoExitConfirmation(boolean)            |Set whether the user will see a confirmation dialog when clicking the back button while there are unsaved image edits.<br><br>Setting this to `true` will hide that confirmation. The default setting is `false`.|
|withVibrationEnabled(boolean)              |Enables or disables the vibration of selected widgets at runtime. Make sure to set your vibration permissions in your `AndroidManifest`:<br><br>`<uses-permission android:name="android.permission.VIBRATE" />`|
|withAccentColor(Color)                     |Sets the color of UI hightlights such as selected tools or settings.|


<a name="preloading-cds"></a>
### Preloading the Content Delivery Service
The Image Editor synchronizes its external content in a background thread. When the Image Editor Activity starts, it will request any updated content from Adobe's servers in order to update the UI. The first time the editor is launched, this operation can take up to a few seconds (usually less in most cases, however).

For this reason we suggest starting the CDS service in your application within the `onCreate()` method of your Activity. To do so, simply add this snippet:

```language-java
Intent cdsIntent = AdobeImageIntent.createCdsInitIntent(getBaseContext(), "CDS");
startService(cdsIntent);
```

The CDS service runs in a separate process on a background thread, so you don’t have to worry about extra stress on the UI thread.


<a name="high-res"></a>
### Implementing High Resolution Image Editing
By default, the Image Editor works on a medium resolution image in order to speed up the performance of the editor. The image returned in your `onActivityResult()` method will have, more or less, the same amount of pixels as the device screen.

If you want to save the edited images in high resolution, you must call the [`withOutputSize(MegaPixels)`](#withOutputSize) method of the `AviaryIntent.Builder` class.

The optional second parameter is the maximum number of megapixels you would like to use for high resolution processing.


<a name="exif-tags"></a>
### Adding Custom Exif Tags
By default, almost all existing tags of an image are preserved. In addition, you can save custom exif tags by extending the `AdobeImageEditorActivity` class and overriding the `onSaveCustomTags()` method.

You can save your custom tag in the `ExifInterface`, which is provided as an argument in the method. For example, if you would like to save a custom artist, do the following in `onSaveCustomTags()`:

```language-java
exif.buildTag(ExifInterface.TAG_ARTIST, "cool artist name");
```

_**Note:** For a list of supported tags, check the class:_

```language-java
it.sephiroth.android.library.exif2.ExifInterface
```


<a name="customizing"></a>
### Customizing the Image Editor UI
To customize the Image Editor UI, developers can override some default values that exist in the SDK.

To achieve that, we will first find the default values in the SDK (so we can see what's there), then make a new XML file that overwrites only the values that we want to customize.

In the steps below, we will walk through an example of customizing the Crop tool. You can apply these steps similarly to whatever values you want to customize.


1. Build your project

    Before customizing your integration, you'll need to do a full build. In Android Studio, you can do this by going to the "Build" menu.

1. Find the default values in the SDK

    Now we need to find the `values.xml` file. To make it easier to find, make sure your Android Studio file browser is set to Project Files mode (by default it is set to Android mode; just click the menu to select a different option):
    
    ![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/image-editor-file-browser-mode.png)

    When the file browser is in Project Files mode, go to this file:

    ```language-java
    /app/build/intermediates/exploded-aar/com.adobe.creativesdk/image/#.#.#/res/values/values.xml
    ```

    For example, if we wanted to customize the Crop tool options, we would need to override:

    - "com_adobe_image_editor_crop_labels" (the label the user sees in the UI)
    - "com_adobe_image_editor_crop_values" (the value that configures the crop itself)
    - "com_adobe_image_editor_crop_selected" (the crop selected by default when the user opens the menu)

1. Make a new `values.xml` file in your project

    You can switch your Android Studio file browser back to Android mode (the same way we switched the mode above).

    When the file browser is in Android mode, go to:
    `/app/res/values`

    In this values directory, you will probably already have `strings.xml`, `styles.xml`, and a `dimens.xml` directory.

    Let's make a new file in the values directory and call it `values.xml` (notice that this filename matches the filename we found in the SDK earlier).

    After you make the `values.xml` file, let's override those default SDK values we found earlier:

    - "com_adobe_image_editor_crop_labels"
    - "com_adobe_image_editor_crop_values"
    - "com_adobe_image_editor_crop_selected"

    Add the following to `values.xml`:

    ```language-xml
    <resources>
        /* Strings that the user will see in the UI (You can use any string you like) */
        <string-array name="com_adobe_image_editor_crop_labels">
            <item>Square</item>
            <item>3:2</item>
            <item>2:4</item>
            <item>Narrow</item>
        </string-array>

        /* Ratio values used by the Crop tool (You can set these to any ratios you like) */
        <string-array name="com_adobe_image_editor_crop_values">
            <item>1:1</item>
            <item>3:2</item>
            <item>2:4</item>
            <item>6:2</item>
        </string-array>

        /* The array item that is auto-selected on load */
        <integer name="com_adobe_image_editor_crop_selected">1</integer>
    </resources>
    ```

    This code overrides the SDK's default values and only shows:

    1. The strings “Square”, “3:2”, “2:4”, and “Narrow” as the labels
    1. Crops with 1:1, 3:2, 2:4, and 6:2 ratios
    1. The 3:2 crop (the second element in our array) automatically selected

    _**Warning:** be sure an item actually exists at the `com_adobe_image_editor_crop_selected` that you set or your app will crash._

    Here is an example of what you will see:

    ![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/image-editor-custom-crop.png)

1. Explore

    This was just one of many possibilities for customizing the Image Editor UI. Explore the SDK's `values.xml` file for more customization options.

    In addition to the styles contained in the SDK's `values.xml`, there are also variant overrides (e.g. android-21, android-19, etc) that can be found in their respective build folders:

    ```language-java
    .../build/intermediates/exploded-aar/com.adobe.creativesdk/image/4.0.0/res
    ```

    To customize these variants, override them in your project using the method we used above, being sure to maintain the same folder structure.

    For instance, if you're overriding the file:

    ```language-java
    .../res/values-v21/values.xml
    ```

    ...be sure to place your override file in `res/values-v21` of your project and name it `values.xml`.


<a name="validating"></a>
### Validating your configuration
We’ve included a utility class to help you validate your current configuration against the Creative SDK Image Editor module:

```language-java
/com/adobe/creativesdk/aviary/utils/AdobeImageEditorIntentConfigurationValidator.class
```

In your Activity's `onCreate()` method use the validator to verify that everything is setup correctly:

```language-java
if (BuildConfig.DEBUG) {
    try {
        AdobeImageEditorIntentConfigurationValidator.validateConfiguration(this);
    }
    catch (Throwable e) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(e.getMessage()).show();
    }
}
```


<a name="troubleshooting"></a> 
## Troubleshooting and known issues
Articles about common issues are at [help.creativesdk.com](http://help.creativesdk.com/), along with a place to submit tickets for bugs, feature requests, and general feedback.

<a name="65k-methods"></a>
### 65K methods error
[Please see this article on our support site for more information](https://creativesdk.zendesk.com/hc/en-us/articles/205837473-Android-65K-methods-error).

<a name="heap-space-error"></a>
### Java heap space error
[Please see this article on our support site for more information](https://creativesdk.zendesk.com/hc/en-us/articles/205490006-Android-Java-heap-space-error).


<!-- Class Reference -->
<a name="reference"></a>
## Class Reference
In this guide, we used the classes in the list below (undocumented classes are followed by their path in the SDK):

- [`AdobeImageIntent` and `AdobeImageIntent.Builder`](#methods)
- `CdsProvider`
    SDK path: `/com/adobe/creativesdk/aviary/internal/cds/`
- `AviaryIntentConfigurationValidator`
    SDK path: `/com/adobe/creativesdk/aviary/utils/`

_**Tip:** to inspect the source code for a class or method in Android Studio, Command/Control-click the class or method name in your code._