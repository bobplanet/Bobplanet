# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/android-sdk-macosx/tools/proguard/proguard-android.txt
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

# EventBus의 callback method 이름이 바뀌지 않도록 설정
-keepclassmembers class ** {
    public void onEvent*(***);
}

# Only required if you use AsyncExecutor
#-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
#    public <init>(java.lang.Throwable);
#}

# Don't warn for missing support classes
-dontwarn de.greenrobot.event.util.*$Support
-dontwarn de.greenrobot.event.util.*$SupportManagerFragment

# for Google cloud endpoints
-keepclassmembers class * {
    @com.google.api.client.util.Key <fields>;
}
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

# for Google libraries
-dontwarn sun.misc.Unsafe

# Log.v() 제거
-assumenosideeffects class android.util.Log {
    public static int v(...);
}