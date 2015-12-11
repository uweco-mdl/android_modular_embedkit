# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/byim/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep class com.mdlive.sav.MDLiveGetStarted {
    public <methods>;
}

-keep class com.mdlive.sav.MDLiveChooseProvider{
    public static <fields>;
}

-keep class com.mdlive.sav.WaitingRoom.MDLiveWaitingRoom{}
-keep class com.mdlive.sav.payment.MDLiveConfirmappointment{}
-keep class com.mdlive.sav.payment.MDLivePayment{}
-keep class com.mdlive.sav.appointment.AppointmentActivity{}


# VSeeKit
-keep public class com.vsee.kit.** {*;}
-keep public class com.vsee.kit.evisit.** {*;}
