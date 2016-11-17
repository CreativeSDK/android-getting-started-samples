# Getting started with the Creative SDK for Android

The Creative SDK lets you build applications that integrate with the Creative Cloud and leverage its power to benefit your users. 

From letting your users import from and save to their Creative Cloud storage, to using innovative Photoshop APIs via your application, the Creative SDK will help you expand the features of your app by using the Adobe platform.

This guide shows you how to get up and running with the Creative SDK for Android, including how to authenticate your client, a necessary step for integration with the SDK.

_**Note:** The Creative SDK for Android is now available as a **remote Maven repo**. It is no longer available as a download from this site. See the the first step of **the section "Adding the SDK to a New Project" in this guide** for details._ 


## Contents

1. [GitHub](#github)
1. [Prerequisites](#prerequisites)
1. [Registering Your Application](#register)
1. [Adding the SDK to a New Project](#new-project)
1. [Integrating the Client Auth component](#client-auth)
1. [What's Next?](#whats-next)
1. [Class Reference](#reference)
1. [Building the Sample Application](#sample-project)
1. [Explore the Android Creative SDK Documentation](#explore)


<a name="github"></a>
## GitHub

You can find companion GitHub repos for the Creative SDK developer guides [on the Creative SDK GitHub organization](https://github.com/CreativeSDK/android-getting-started-samples). 

Be sure to follow all instructions in the `readme`.

<a name="prerequisites"></a>
## Prerequisites

1. Before you can work with the Creative SDK, you must register your application and get Client ID (API Key) and Client Secret values. For details, see [Registering Your Application](#register).
1. The following software is required:

    - Mac OS X, Windows, or Linux
    - [Android Studio](https://developer.android.com/sdk/index.html) 2.0 or higher
    - [Android SDK](https://developer.android.com/sdk/index.html) 16 or higher
    - Gradle 2.1.2 or higher
    - Android build tools version 23.0.3 or higher
    - Android Support Repository 32 or higher


_**Note:** The Creative SDK supports Android API Level 16 as the lowest [minSdkVersion](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html#min), and API Level 24 as the maximum [targetSdkVersion](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html#target)._


<a name="register"></a>
## Registering Your Application

When you register your application, you are automatically approved for Development Mode. 

_**Important:** Your Client ID (API Key) must be [approved for **Production Mode** by Adobe](https://creativesdk.zendesk.com/hc/en-us/articles/204601215-How-to-complete-the-Production-Client-ID-Request) before you release your app._ See the "What's Next?" section of this guide for details on submitting your app for Production Mode approval.

To register your application for Development Mode, [follow the steps in our App Registration guide](https://creativesdk.zendesk.com/hc/en-us/articles/216369343-Why-and-how-to-register-my-app-).


<a name="new-project"></a>
## Adding the SDK to a New Project
The Creative SDK is offered as a remote Maven repository. This section will show you how to add the Maven repo and the Creative SDK frameworks.

1. Add the Maven URL for the Creative SDK repo and the Gradle Retrolambda Plugin to your _Project_ `build.gradle` file

	Your Android Studio project contains by default two `build.gradle` files. In the _Project_ `build.gradle` file, add the lines under comments **#1-3**:

    ```language-java
    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:2.2.2'

            /* 1) Add the Gradle Retrolambda Plugin */
            classpath 'me.tatarka:gradle-retrolambda:3.3.0-beta4'

        }
    }

    allprojects {
        repositories {
            jcenter()

            /* 2) Add mavenCentral */
            mavenCentral()

            /* 3) Add the Creative SDK Maven repo URL */
            maven {
                url 'https://repo.adobe.com/nexus/content/repositories/releases/'
            }
        }
    }

    task clean(type: Delete) {
        delete rootProject.buildDir
    }
    ```

	Be sure to sync your project with the Gradle files after making any edits.

1. Configure your _Module_ `build.gradle` file

	To get the project ready to use the Creative SDK, there are few things we'll need to add here. See comments **#1-4** in the code below.

    ```language-java
    apply plugin: 'com.android.application'

    /* 1) Apply the Gradle Retrolambda Plugin */
    apply plugin: 'me.tatarka.retrolambda'

    android {
        compileSdkVersion 24
        buildToolsVersion "24.0.3"

        defaultConfig {
            applicationId "com.adobe.gettingstarted"
            minSdkVersion 16 // Minimum is 16
            targetSdkVersion 24 // Maximum is 24
            versionCode 1
            versionName "1.0"
        }
        buildTypes {
            release {
                minifyEnabled false
                proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            }
        }

        /* 2) Compile for Java 1.8 or greater */
        compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
        }

        /* 3) Exclude duplicate licenses */
        packagingOptions {
            exclude 'META-INF/LICENSE.txt'
            exclude 'META-INF/LICENSE'
            exclude 'META-INF/NOTICE.txt'
            exclude 'META-INF/NOTICE'
            exclude 'META-INF/DEPENDENCIES'
            pickFirst 'AndroidManifest.xml'
        }
    }

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        testCompile 'junit:junit:4.12'
        compile 'com.android.support:appcompat-v7:24.0.0'
        compile 'com.android.support:design:24.0.0'

        /* 4) Add the CSDK framework dependencies (Make sure these version numbers are correct) */
        compile 'com.adobe.creativesdk.foundation:auth:0.9.1186'
    }
    ```

    _**Note:** `com.adobe.creativesdk.foundation:auth` is a basic requirement for Creative SDK integrations._


<a name="client-auth"></a>
## Integrating the Client Auth component
Client authentication is required to use the Creative SDK. You can authenticate your client with the Client ID (API Key) and Client Secret you received in the "Registering Your Application" section above.

1. Create an `Application` subclass

	For this example, we'll name it `MainApplication`.


1. Configure your `Application` subclass

	Add the following code to your `MainApplication` class:
    
    ```language-java
    public class MainApplication extends Application implements IAdobeAuthClientCredentials {
        
        /* Be sure to fill in the two strings below. */
        private static final String CREATIVE_SDK_CLIENT_ID = "<YOUR_API_KEY_HERE>";
        private static final String CREATIVE_SDK_CLIENT_SECRET = "<YOUR_CLIENT_SECRET_HERE>";

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
    }
    ```
   
1. Register the Main Application in the Android Manifest
    
    Inside of the top level `application` tag, we will add a new attribute: `android:name=".MainApplication"`:

    ```language-xml
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme"
        android:name=".MainApplication" >
        <activity
            // ...            
    ```       

1. Run the app

    That's it. You have registered an app, added the SDK, and implemented Client Auth in just a few simple steps.

    Go ahead and run the app. You won't see anything happening yet, but you have the foundation for your integration with the Creative SDK.

    ![](https://s3.amazonaws.com/csdk-assets-aviary-prod-us-east-1/docs/android/getting-started-app.png)


<a name="whats-next"></a>
## What's Next?

### Terms of Use (TOU) and Branding

See the guidelines in [Using the Creative Cloud Badge and Brand](https://creativesdk.adobe.com/docs/android/#/brandguidelines/index.html) and the [Terms of Use](http://wwwimages.adobe.com/content/dam/Adobe/en/legal/servicetou/Creative_SDK-en_US.pdf). 

### Submit Your Application for Review

Adobe must review all applications that use the Creative SDK before they are released. 

[Instructions for submitting your app for review are here](https://creativesdk.zendesk.com/hc/en-us/articles/204601215-How-to-complete-the-Production-Client-ID-Request).


### Troubleshooting and Support

Articles about common issues are at [help.creativesdk.com](http://help.creativesdk.com/), along with a place to submit tickets for bugs, feature requests, and general feedback.


<a name="reference"></a>
## Class Reference
In this guide, we used the classes in the list below.

- [`IAdobeAuthClientCredentials`](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/auth/IAdobeAuthClientCredentials.html)
- [`AdobeAuthManager`](https://creativesdk.adobe.com/docs/android/#/com/adobe/creativesdk/foundation/auth/AdobeAuthManager.html)

_**Tip:** to inspect the source code for a class or method in Android Studio, Command/Control-click the class or method name in your code._



<a name="sample-project"></a>
## Building the Sample Project

1. From the [Downloads page](https://creativesdk.adobe.com/downloads.html), download the Creative SDK and the sample project.
1. Unzip the file.
1. Open the `CreativeSDKSampleApp` project in Android Studio.
1. Sync the project with the Gradle files.
1. Add your Client ID (API Key) and Client Secret to `/util/CreativeSDKSampleApplication.java`.


<a name="explore"></a>
## Explore the Android Creative SDK Documentation

The Creative SDK encompasses the core frameworks below. The frameworks can be added to your app individually, as you need them, for your unique integration.

You can learn more in the Creative SDK framework guides:


### Creative Cloud Content Management

- [User Auth UI](https://creativesdk.adobe.com/docs/android/#/articles/userauth/index.html)
- [Asset Browser UI](https://creativesdk.adobe.com/docs/android/#/articles/assetbrowser/index.html)
- [TypeKit UI](https://creativesdk.adobe.com/docs/android/#/articles/typekitui/index.html)
- Creative Cloud Files API (Coming Soon)
- Lightroom Photos API (Coming Soon)
- Creative Cloud Libraries API (Coming Soon)


### Creative Tools

- [Image Editor UI](https://creativesdk.adobe.com/docs/android/#/articles/imageediting/index.html)
- Color UI (Coming Soon)


### Creative Cloud Workflows

- [Send To Desktop API](https://creativesdk.adobe.com/docs/android/#/articles/sendtodesktop/index.html)
- Behance Publish UI (Coming Soon)


### Frameworks

- [Framework Dependencies](https://creativesdk.adobe.com/docs/android/#/articles/dependencies/index.html)
