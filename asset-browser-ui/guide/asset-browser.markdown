# Asset Browser UI

The Creative SDK lets you provide your users with access to their assets in the Creative Cloud via the Asset Browser UI component. 

With the Asset Broswer UI component, your users will see a familiar interface for the Creative Cloud that lets them view and select their Creative Cloud Files, Lightroom Photos, Photoshop Mixes, Sketches, and more.

![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/asset-browser.png)

In this guide, we will cover how to launch the Asset Browser UI component and let a user select an image.

By the end of this guide, we will have an Android app that:

1. Launches the Asset Browser when the user clicks a button
1. Lets the user select a photo to be downloaded from Creative Cloud
1. Displays the photo in the app's UI


## Contents

1. [GitHub](#github)
1. [Prerequisites](#prereqs)
1. [Configuration](#config)
1. [Launching the Asset Browser](#launch)
1. [Downloading Assets](#download)
1. [Uploading Assets](#upload)
1. [Customization](#custom)
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

<a name="launch"></a>
## Launching the Asset Browser
One common pattern for working with the Asset Browser is to create a button in an Activity's layout and launch the Asset Browser when the user presses the button.

In such a scenario, we might create a helper method and name it, for example, `launchAssetBrowser()`:

```language-java
private void launchAssetBrowser() {
    AdobeUXAssetBrowser assetBrowser = AdobeUXAssetBrowser.getSharedInstance();

    try {
        assetBrowser.popupFileBrowser(this, 300); // Can be any int
    }
    catch (AdobeCSDKException e) {
        Log.e(MainActivity.class.getSimpleName(), "Error: " + e.getMessage());
    }
}
```

This helper method would then be called inside of an on-click listener for the button in our layout.

<a name="download"></a>
## Downloading Assets
The process for downloading an asset will depend on what kind of asset your app works with.

As an example, let's have a look at the process for allowing the user to select a single Lightroom Photo and open it to be displayed in the Main Activity of your app.

Inside of your Activity's `onActivityResult()` method:

1. Unpack the `AdobeSelection` from the `Intent data` argument.
1. Check if the `AdobeSelection` is an instance of `AdobeSelectionPhotoAsset`.
1. Create an `IAdobeGenericRequestCallback` and fill in the methods within.
1. Download the `AdobePhotoAsssetRendition` that you want.

See these steps (comments **#1-4**) in the example code below:

```language-java
if (data != null && resultCode == RESULT_OK) {
    switch (requestCode) {
        case 300: // The request code we used in launchAssetBrowser()

            /* 1) */
            AdobeUXAssetBrowser.ResultProvider assetBrowserResult = new AdobeUXAssetBrowser.ResultProvider(data);
            ArrayList listOfSelectedAssetFiles = assetBrowserResult.getSelectionAssetArray();
            AdobeSelection selection = (AdobeSelection) listOfSelectedAssetFiles.get(0);

            /* 2) */
            if (selection instanceof AdobeSelectionPhotoAsset) {

                /* 3) */
                IAdobeGenericRequestCallback<byte[], AdobePhotoException> downloadCallBack = new IAdobeGenericRequestCallback<byte[], AdobePhotoException>() {
                    @Override
                    public void onCancellation() {
                        /* 3.a) Cancellation code here */
                    }

                    @Override
                    public void onCompletion(byte[] bytes) {

                        /* 3.b) */
                        InputStream inputStream = new ByteArrayInputStream(bytes);
                        Bitmap image = BitmapFactory.decodeStream(inputStream);
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mSelectedAssetImageView.setImageBitmap(image);
                    }

                    @Override
                    public void onError(AdobePhotoException e) {
                        /* 3.c) Error handler here */
                    }

                    @Override
                    public void onProgress(double v) {
                        /* 3.d) Code for indicating download progress here */
                    }
                };

                /* 4) */
                AdobePhotoAsset photoAsset = ((AdobeSelectionPhotoAsset) selection).getSelectedItem();
                Map<String, AdobePhotoAssetRendition> renditionMap = photoAsset.getRenditions();
                photoAsset.downloadRendition(renditionMap.get(AdobePhotoAsset.AdobePhotoAssetRenditionImage2048), downloadCallBack);
            }

            break;
            
        default:
            break;
    }
}
```

The actual code you write in `onActivityResult()` will depend on what types of Creative Cloud assets your app is intended to work with.

For details on the available methods for each type of Creative Cloud asset, see our class references:

- [AdobeSelectionAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionAsset.html)
- [AdobeSelectionAssetFile](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionAssetFile.html)
- [AdobeSelectionCompFile](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionCompFile.html)
- [AdobeSelectionDrawAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionDrawAsset.html)
- [AdobeSelectionLibraryAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionLibraryAsset.html)
- [AdobeSelectionPhotoAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionPhotoAsset.html)
- [AdobeSelectionPSMixFile](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionPSMixFile.html)
- [AdobeSelectionSketchAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionSketchAsset.html)

### Common patterns in the download workflow
The example above is written in a very procedural manner for the sake of demonstration. However, you may find that you prefer to create helper methods to keep your `onActivityResult()` method more readable.

Here are a few common patterns for downloading assets:

- [Unpacking the `AdobeSelection`](#unpacking)
- [Checking the type of `AdobeSelection`](#checking-selection-type)
- [Limiting options available to the user in the Asset Browser](#limiting-options)

<a name="unpacking"></a>
**Unpacking the `AdobeSelection`**
Step **#2** ("Unpack the `AdobeSelection`") in the example code above is often abstracted to a helper method:

```language-java
private AdobeSelection getSelection(Intent data) {
    AdobeUXAssetBrowser.ResultProvider assetBrowserResult = new AdobeUXAssetBrowser.ResultProvider(data);
    ArrayList listOfSelectedAssetFiles = assetBrowserResult.getSelectionAssetArray();
    AdobeSelection selection = (AdobeSelection) listOfSelectedAssetFiles.get(0);
    return selection;
}
```

You can then call this method and assign the return value to a variable in one line:

```language-java
AdobeSelection selection = getSelection(data);
```

<a name="checking-selection-type"></a>
**Checking the type of `AdobeSelection`**

For step **#3** ("Check the type of `AdobeSelection`") in the example code above, we only checked for Lightroom Photos. 

Most apps will only work with a few types of assets, but for apps that work with many or all asset types, a common pattern is to:

1. Abstract the type checking to a helper method.
2. Check for the various asset types in an if/else-if statement.
3. Send each asset type to its own helper method that you create for further work.

Below is a simple example (see steps **#1-3** in the comments):

```language-java
/* 1) */
private void downloadAndAttachAsset(AdobeSelection selection){

    /* 2) */
    if(selection instanceof AdobeSelectionAsset)
        /* 3) */
        downloadAndAttachImage(((AdobeSelectionAsset) selection).getSelectedItem());

    else if (selection instanceof AdobeSelectionPhotoAsset)
        downloadAndAttachPhoto(((AdobeSelectionPhotoAsset) selection).getSelectedItem());
    else if (selection instanceof AdobeSelectionLibraryAsset)
        downloadAndAttachLibraryItem((AdobeSelectionLibraryAsset) selection).getSelectedItem());
    else if (selection instanceof AdobeSelectionDrawAsset)
        downloadAndAttachDraw((AdobeSelectionDrawAsset) selection).getSelectedItem());
    else if (selection instanceof AdobeSelectionSketchAsset)
        downloadAndAttachSketch((AdobeSelectionSketchAsset) selection).getSelectedItem());
    else if (selection instanceof AdobeSelectionCompFile)
        downloadAndAttachComp((AdobeSelectionCompFile) selection).getSelectedItem());
    else if (selection instanceof AdobeSelectionPSMixFile)
        downloadAndAttachMix((AdobeSelectionPSMixFile) selection).getSelectedItem());
}
```

In other words, for this code example above, if you pass in an `AdobeSelection` that is an instance of `AdobeSelectionCompFile`, the `AdobeSelection` will be passed to `downloadAndAttachComp()`, which is a method you would create to do further work on the `AdobeSelectionCompFile`, like downloading the file and attaching it to the Activity layout.

<a name="limiting-options"></a>
**Limiting options available to the user in the Asset Browser**
One way to eliminate the need for a lot of type checking is to limit the kinds of assets available to the user in the Asset Browser.

As with our example above, if your app only works with Lightroom Photos, there's no reason to make Creative Cloud Files available to the user in the first place. You can limit the options available by customizing the Asset Browser.

[See the Customization section of this guide for details.](#custom)

<a name="upload"></a>
## Uploading Assets
File uploads are not handled by the Asset Browser UI component, but instead by headless APIs. For more information, please see our class references for the asset type you are working with.

- [Creative Cloud Files API: `AdobeAssetFile`](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeAssetFile.html)
- [Lightroom Photos API: `AdobePhotoAsset`](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobePhotoAsset.html)
- [Creative Cloud Libraries: `AdobeLibraryComposite`](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeLibraryComposite.html)

<a name="custom"></a>
## Customization

Creative Cloud users can store a diverse range of assets, like Creative Cloud Files, Lightroom Photos, Photoshop Mixes, Sketches, and more. But if your app is only designed to work with one of those specific file types, it's best to limit what is available in the Asset Browser to that specific file type. 

This can be achieved by customizing the Asset Browser with `AdobeUXAssetBrowserConfiguration`. It has three properties, which are all optional: `options`, `dataSourceFilter`, and `mimeTypeFilter`.

Let's take a closer look at these options.

- [Set startup options for the Asset Browser](#options)
- [Filter based on data source](#data-source-filter)
- [Filter based on mime type](#mime-type-filter)

_**Note:** These options **may be cached on the device**, so while you are making frequent changes during development, you may need to delete the app from the device and reinstall before you see the changes in the Asset Browser._

<a name="options"></a>
### Set startup options for the Asset Browser
The Asset Browser startup options can be set with the `options` property.

The available options are:

- ENABLE_MULTI_SELECT* - Enable multiple file selection in the Asset Browser.
- SHOW_MULTI_SELECT_ON_POPUP* - Allow the user to toggle multiple file selection in the Asset Browser. 
    _**Note:** Currently, this option does nothing._
- ENABLE_MYACCOUNT_OPTION - Enable the "My Account" item in the Asset Browser's dropdown menu.
- SHOW_LIST_VIEW_ON_POPUP - Display a list view as the initial collection view.
    _**Note:** This option has no effect on the display of Lightroom Photos._
- SORT_ALPHABETICALLY_ON_POPUP - Sort the initial collection view alphabetically.

_* Use both of the options together to enable and show multi-select._

Below is an example of using the `options` property (see comments **#1-2**):

```language-java
private void launchAssetBrowser() {
    AdobeUXAssetBrowser assetBrowser = AdobeUXAssetBrowser.getSharedInstance();
    AdobeUXAssetBrowserConfiguration configuration = new AdobeUXAssetBrowserConfiguration();

    /* 1) Display assets in list view and enable multi-select */
    configuration.options = EnumSet.of(
            AdobeUXAssetBrowserOption.SHOW_LIST_VIEW_ON_POPUP,
            AdobeUXAssetBrowserOption.ENABLE_MULTI_SELECT
    );

    /* 2) Pass the configuration as the 3rd argument to popupFileBrowser() */
    try {
        assetBrowser.popupFileBrowser(this, 300, configuration);
    }
    catch (AdobeCSDKException e) {
        Log.e(MainActivity.class.getSimpleName(), "Error: " + e.getMessage());
    }
}
```

<a name="data-source-filter"></a>
### Filter based on data source
The Asset Browser can be configured to make available only assets from certain sources, like Creative Cloud or Lightroom, with the `dataSourceFilter` property.

Below is an example using the `dataSourceFilter` property to make available only Lightroom Photos in the Asset Browser (see comments **#1-2**):

```language-java
private void launchAssetBrowser() {
    AdobeUXAssetBrowser assetBrowser = AdobeUXAssetBrowser.getSharedInstance();
    AdobeUXAssetBrowserConfiguration configuration = new AdobeUXAssetBrowserConfiguration();

    /* 1) Set the dataSourceFilter property */
    configuration.dataSourceFilter = AdobeAssetDataSourceFilter.createFromDataSources(
            /* 1.a) Provide a set of source types */
            EnumSet.of(AdobeAssetDataSourceType.AdobeAssetDataSourcePhotos),
            /* 1.b) Select the type of filter, INCLUSION or EXCLUSION */
            AdobeAssetDataSourceFilterType.ADOBE_ASSET_DATASOURCE_FILTER_INCLUSION
    );

    try {
        /* 2) Pass the configuration as the 3rd argument to popupFileBrowser() */
        assetBrowser.popupFileBrowser(this, DEFAULT_SIGN_IN_REQUEST_CODE, configuration);
    }
    catch (AdobeCSDKException e) {
        Log.e(MainActivity.class.getSimpleName(), "Error: " + e.getMessage());
    }
}
```

**The `AdobeAssetDataSourceFiles` enum**

Available in the `AdobeAssetDataSourceFiles` enum (used in **1.a** above) are:

- `AdobeAssetDataSourceFiles`: Creative Cloud files
- `AdobeAssetDataSourcePhotos`: Lightroom photos
- `AdobeAssetDataSourceCompositions`: Comp CC files
- `AdobeAssetDataSourceDraw`: Illustrator Draw files
- `AdobeAssetDataSourceLibrary`: Libraries
- `AdobeAssetDataSourcePSMix`: Photoshop Mix files
- `AdobeAssetDataSourceSketches`: Photoshop Sketch files

**The `AdobeAssetDataSourceFilterType` enum**
Available in the `AdobeAssetDataSourceFilterType` enum (used in **1.b** above) are:

- `ADOBE_ASSET_DATASOURCE_FILTER_INCLUSION`: only include the data sources in the set
- `ADOBE_ASSET_DATASOURCE_FILTER_EXCLUSION`: only exclude the data sources in the set

<a name="mime-type-filter"></a>
### Filter based on mime type
The Asset Browser can be configured to make available only assets of a certain mime type, like JPEG, GIF, or ILLUSTRATOR, with the `mimeTypeFilter` property.

Below is an example using the `mimeTypeFilter` property to make available all mime types _except for_ Illustrator files in the Asset Browser (see comments **#1-2**):

```language-java
private void launchAssetBrowser() {
    AdobeUXAssetBrowser assetBrowser = AdobeUXAssetBrowser.getSharedInstance();
    AdobeUXAssetBrowserConfiguration configuration = new AdobeUXAssetBrowserConfiguration();

    /* 1) Set the mimeTypeFilter property */
    configuration.mimeTypeFilter = AdobeAssetMIMETypeFilter.createFromMimeTypes(
            /* 1.a) Provide a set of mime types */
            EnumSet.of(AdobeAssetMimeTypes.MIMETYPE_ILLUSTRATOR),
            /* 1.b) Select the type of filter, INCLUSION or EXCLUSION */
            AdobeAssetMIMETypeFilterType.ADOBE_ASSET_MIMETYPE_FILTERTYPE_EXCLUSION
    );

    try {
        /* 2) Pass the configuration as the 3rd argument to popupFileBrowser() */
        assetBrowser.popupFileBrowser(this, DEFAULT_SIGN_IN_REQUEST_CODE, configuration);
    }
    catch (AdobeCSDKException e) {
        Log.e(MainActivity.class.getSimpleName(), "Error: " + e.getMessage());
    }
}
```

**The `AdobeAssetMimeTypes` enum**
Available in the `AdobeAssetMimeTypes` enum (used in **1.a** above) are numerous mimetypes, including JPEF, GIF, MP4, PNG, RAW, and more.

**The `AdobeAssetDataSourceFilterType` enum**
Available in the `AdobeAssetDataSourceFilterType` enum (used in **1.b** above) are:

- `ADOBE_ASSET_DATASOURCE_FILTER_INCLUSION`: only include the data sources in the set
- `ADOBE_ASSET_DATASOURCE_FILTER_EXCLUSION`: only exclude the data sources in the set


<a name="best-practices"></a>
## Best Practices


### Adding a `ProgressBar`
Since the Asset Browser is downloading images over a wireless network, it can take some time before `IAdobeGenericRequestCallback` methods are called. For this reason, it is recommendable to make good use of an Android `ProgressBar` to let your user know that the application is working.

[You can read more about Android's `ProgressBar` in the Android documentation](http://developer.android.com/reference/android/widget/ProgressBar.html).


<a name="troubleshooting"></a>
## Troubleshooting and Known Issues
Articles about common issues are at [help.creativesdk.com](http://help.creativesdk.com/), along with a place to submit tickets for bugs, feature requests, and general feedback.


<a name="class-reference"></a>
## Class Reference
In this guide, we used the classes in the list below. Explore the documentation for more class methods and details.

- [AdobeSelection](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelection.html)
- [AdobeSelectionAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionAsset.html)
- [AdobeSelectionAssetFile](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionAssetFile.html)
- [AdobeSelectionCompFile](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionCompFile.html)
- [AdobeSelectionDrawAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionDrawAsset.html)
- [AdobeSelectionLibraryAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionLibraryAsset.html)
- [AdobeSelectionPhotoAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionPhotoAsset.html)
- [AdobeSelectionPSMixFile](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionPSMixFile.html)
- [AdobeSelectionSketchAsset](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeSelectionSketchAsset.html)
- [AdobeUXAssetBrowser](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeUXAssetBrowser.html)
- [AdobeUXAssetBrowserConfiguration](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeUXAssetBrowserConfiguration.html)
- [AdobeUXAssetBrowser.ResultProvider](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/AdobeUXAssetBrowser.ResultProvider.html)
- [IAdobeGenericRequestCallback](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/storage/IAdobeGenericRequestCallback.html)
