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

# 상용버전에서는 불필요한 로그 생성을 막기 위해 Log.v() 제거
-assumenosideeffects class android.util.Log {
    public static int v(...);
}

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

# Google Cloud Endpoints가 사용하는 JSON 객체 이름은 그대로 유지
-keepclassmembers class * {
    @com.google.api.client.util.Key <fields>;
}
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

# Google library에 대한 warning 무시
-dontwarn sun.misc.Unsafe

# ButterKnife가 만들어내는 클래스 관련 설정
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# MaterialList에서 내부적으로 사용하는 Picasso 경고 무시
-dontwarn com.squareup.picasso.*

# Google Analytics로 전송되는 Activity나 Fragment 이름 유지
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
