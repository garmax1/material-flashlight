# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Soft\android-sdk/tools/proguard/proguard-android.txt
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

#### Readable trace
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-useuniqueclassmembernames

#### Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#### Timber
-dontwarn org.jetbrains.annotations.**

#### Kotlin
-dontwarn kotlin.**

#### Support library
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keep public class android.support.R$* { *; }