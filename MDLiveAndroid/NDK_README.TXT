===============================================================================
                         USING ANDROID NDK with EmbedKit
===============================================================================

This document describes

Software Requirements
---------------------
Simply adhere to current software requirements, as established for previously configured
EmbedKit, prior to the introduction of NDK.

Please follow instructions under the following URL
 "https://developer.android.com/intl/zh-cn/ndk/index.html", if you have not already
 downloaded the Android NDK, and have installed it on your machine. Download and extract
 the NDK in the same parent directory where your SDK is located. The compressed NDK
 download will be named something like "android-ndk-r10e-windows-x86_64.exe". After
 extracting, simply rename it to "ndk".  The only other configuration needed is to
 add the NDK location entry in you "local.properties" file.

 ex. "local.properties" NDK entry:

 sdk.dir=C\:\\Users\\Emilio\\AppData\\Local\\Android\\sdk
 ndk.dir=C\:\\Users\\Emilio\\AppData\\Local\\Android\\ndk



Project Structure
-----------------
The EmbedKit project structure has been enhanced to include a new 'src' directory called
'jni' under the 'main' directory, ("app\src\main\jni"). Within this directory, you will
find the new 'main.c' containing Native declarations for the "getProdApiKeyFromNative",
and "getProdSecretKeyFromNative" calls. Also, you will find an empty 'C' file, which is
necessary to resolve a Google bug discovered with building NDK within the Android
Studio CTE. The NDK code will not currently compile without having multiple 'C/C++'
files under the 'jni' folder. Stay tuned for updates from Google on when this will be fixed.

For further instructions or clarifications, please visit the URL:
"https://developer.android.com/intl/zh-cn/ndk/index.html"


Project Build
-------------
You do not need to build the NDK portion of the Embed kit seperately. Executing the
Gradle script "AssembleRelease" (or any other Gradle assemble script) will automatically
build all 'C/C++' modules located in the 'JNI' folder.

After a successful NDK build, you will find NDK objects distributed under:
"MDLiveAndroid\app\build\intermediates\ndk".