# Framework Dependencies

In the Creative SDK for Android, we’ve broken the functionality into smaller dependencies so that developers can include only the pieces that they need, thereby reducing the size of their apps.

_**Note:** Your Client ID must be approved for Production Mode by Adobe before you release your app._

## Dependencies Overview

Below is a table that contains all of the framework dependencies for each feature of the Creative SDK:

|Feature   						|Dependency			  									|
|---							|---													|
|Asset Browser UI Component 	|`com.adobe.creativesdk.foundation:assetux:0.9.1186`	|
|Behance UI Component   		|`com.adobe.creativesdk:behance:0.9.1186`				|
|**Client Auth API\***			|`com.adobe.creativesdk.foundation:auth:0.9.1186`		|
|Color Tool UI Component   		|`com.adobe.creativesdk:color:0.9.1186`					|
|Creative Cloud Files API   	|`com.adobe.creativesdk.foundation:assetcore:0.9.1186`	|
|Creative Cloud Libraries API	|`com.adobe.creativesdk.foundation:assetcore:0.9.1186`	|
|Image Editor UI Component  	|`com.adobe.creativesdk:image:4.6.3`					|
|Lightroom Photos API   		|`com.adobe.creativesdk.foundation:assetcore:0.9.1186`	|
|SendToDesktop API   			|`com.adobe.creativesdk.foundation:assetcore:0.9.1186`	|
|TypeKit UI Component			|`com.adobe.creativesdk:typekit:0.9.1186`				|
|**User Auth UI Component\*\***	|`com.adobe.creativesdk.foundation:auth:0.9.1186`		|

_\***Note:** Client Auth is required for all apps using the Creative SDK._
_\*\***Note:** User Auth is required for all components except for the Image Editor UI Component._

## Adding dependencies to Gradle
To add Creative SDK dependencies to your Android project, open your _Module_ `build.gradle` file and locate the `dependencies` block at the bottom of the file.

In the `dependencies` block, add the Creative SDK dependencies that you need.


### Examples
An example of the correct format for an app using the Creative SDK Image Editor:

```
compile 'com.adobe.creativesdk.foundation:auth:0.9.1186'
compile 'com.adobe.creativesdk:image:4.6.3'
```

An example of the correct format for an app using the Creative SDK Asset Browser UI and Creative Cloud Files API:

```
compile 'com.adobe.creativesdk.foundation:auth:0.9.1186'
compile 'com.adobe.creativesdk.foundation:assetux:0.9.1186'
compile 'com.adobe.creativesdk.foundation:assetcore:0.9.1186'
```

_**Note:** for a more in-depth look at adding the Creative SDK repo to your app and writing Gradle files, [see our Getting Started guide](https://creativesdk.adobe.com/docs/android/#/articles/gettingstarted/index.html)._
