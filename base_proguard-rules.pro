# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\android\android-sdk-windows/tools/proguard/proguard-android.txt
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
-dontwarn
-dontoptimize
-ignorewarnings						# 忽略警告，避免打包时某些警告出现
-optimizationpasses 5				# 指定代码的压缩级别
-dontusemixedcaseclassnames			# 是否使用大小写混合
#-dontskipnonpubliclibraryclasses	# 是否混淆第三方jar
-dontpreverify                      # 混淆时是否做预校验
-verbose                            # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法
-dontnote
-keepattributes *Annotation*   #保护注解
# 保留行号
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature   #保护泛型-否则会出现泛型找不到,socket无法使用
#####################记录生成的日志数据,gradle build时在本项目根目录输出################
#apk 包内所有 class 的内部结构
#-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
#####################记录生成的日志数据，gradle build时 在本项目根目录输出-end############
#android
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-dontwarn android.support.**
-keep class android.** {*;}
-keep class com.google.** {*;}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
#保护js调用方法不被混淆
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#去除不必要的代码（发布时去掉日志输出代码）
-assumenosideeffects class com.liang530.log.L {
    public static *** d(...);
    public static *** v(...);
    public static *** e(...);
    public static *** i(...);
}


################<span></span>混淆保护自己项目的部分代码以及引用的第三方jar包library#########################
#如果混淆报错又找不到该jar提供的混淆配置，则可以直接忽略这个jar
#android studio 不可用
#-libraryjars libs/umeng-analytics-v5.2.4.jar