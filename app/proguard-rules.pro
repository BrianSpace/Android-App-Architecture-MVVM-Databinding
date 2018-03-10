# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidSdk/tools/proguard/proguard-android.txt
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
# Use unique member names to make stack trace reading easier
-useuniqueclassmembernames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Support Libraries
-keep class android.support.v4.** { *; }

# Exported Components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Dagger
-dontwarn com.google.errorprone.annotations.*

# OkHttp. Reference: https://github.com/square/okhttp
-dontwarn okhttp3.**
-dontwarn okio.**               # Same for Retrofit. Reference: https://github.com/square/retrofit
-dontwarn javax.annotation.**   # Same for Retrofit
-dontwarn retrofit2.Platform$Java8
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Gson. Reference: https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
-keep class sun.misc.Unsafe { *; }
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Application classes that will be serialized/deserialized over Gson
-keep class com.github.brianspace.moviebrowser.repository.data.** { *; }

# Glide. Reference: http://bumptech.github.io/glide/doc/download-setup.html#proguard
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Databinding
-dontwarn android.databinding.**