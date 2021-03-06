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

-keep class com.mdlive.myhealth.MedicalHistoryActivity {
    public <methods>;
}

-keep public class * extends android.view.View
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.Fragment

# allow resource IDs to be properly mapped
-keep class *.R
-keep class **.R$*
-keepclassmembers class **.R$* {
       public static <fields>;
}
-keepclasseswithmembers class **.R$* {
    public static <fields>;
}