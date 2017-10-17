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
#####下面是自己的一些特殊的配置####################################################

#####这里是自己的内部项目的配置，如javaBean   #######

-keep class com.huosuapp.text.view.** {*;} #保护自定义view
-keep class com.huosuapp.text.bean.**{*;} #保护javaBean

#保护js调用方法不被混淆
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
######下面是自己的一些特殊的配置####################################################
-keep class com.game.sdk.domain.**{*;} #保护javaBean
# keep annotated by NotProguard 保护使用NotProguard不混淆
# 特别注意内部类会被混淆掉
-keep class com.game.sdk.domain.NotProguard
-keep @com.game.sdk.domain.NotProguard class * {*;}

-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <methods>;
}
#
-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <fields>;
}
#
-keepclasseswithmembers class * {
    @com.game.sdk.domain.NotProguard <init>(...);
}




#3D 地图
-keep   class com.amap.api.mapcore.**{*;}
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
#定位
-keep   class com.amap.api.location.**{*;}
-keep   class com.aps.**{*;}
#搜索
-keep   class com.amap.api.services.**{*;}
#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.tencent.mm.sdk.** {*;}


#Bugly
#-keep public class com.tencent.bugly.**{*;}
# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }



-keep class java.** {*;}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#fastjson
-dontwarn com.alibaba.fastjson.**
#pinyin4j
-dontwarn net.soureceforge.pinyin4j.**
-dontwarn com.hp.hpl.sparta.**
-dontwarn demo.**
-dontwarn javax.**
-keep class net.sourceforge.pinyin4j.** { *;}
-keep class com.hp.hpl.sparta.** { *;}
-keep class demo.** { *;}

#greendao
#-keep class de.greenrobot.dao.** {*;}
#-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
#    public static java.lang.String TABLENAME;
#}
-keep class **$Properties

#eventbus2.0  3.0
-keep class de.greenrobot.dao.** { *;}
-keepclassmembers class ** {
    public void onEvent*(***);
}
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


#xutils
-keep class com.lidroid.xutils.** { *; }

#androidcore--保持指定规则的方法不被混淆(onClick方法不能混淆)
-keepclassmembers class * extends com.liang530.fragment.LazyFragment {
    public void *(android.view.View);
}
#-dontwarn core.base.**
#-keep class com.liang530.support.** {*;}
#-keep class com.liang530.views.** {*;}

# keep annotated by NotProguard
# 特别注意内部类会被混淆掉
-keep @com.liang530.event.NotProguard class * {*;}
-keep class * {
    @com.liang530.event.NotProguard <fields>;
}
-keepclassmembers class * {
    @com.liang530.event.NotProguard <methods>;
}


#友盟统计，特别注意下面的R文件的包名
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.huosuapp.text.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#ShareSDK
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
#地图导航jni报错，只能不混淆
-keep class com.amap.** { *; }

#Jpush
-dontwarn cn.jpush.**
-dontwarn com.google.gson.jpush.**
-keep class cn.jpush.** { *; }
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

#okhttp
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.**
-dontwarn okio.**
#apache
-dontwarn org.apache.**
-keep class org.apache.** {*;}
-dontwarn android.net.http.**
-keep class android.net.http.** { *;}
-keep class com.android.volley.**{*;}

#photoView
-dontwarn uk.co.senab.photoview.**
-keep class uk.co.senab.photoview.**{*;}

#fresco
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
# Keep native methods

-keepclassmembers class * {
    native <methods>;
}
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

#Ping++
-dontwarn com.pingplusplus.**
-keep class com.pingplusplus.** {*;}

-dontwarn  com.alipay.**
-keep class com.alipay.** {*;}

-dontwarn  com.ta.utdid2.**
-keep class com.ta.utdid2.** {*;}

-keep class com.ut.device.** {*;}

-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}

-dontwarn  com.unionpay.**
-keep class com.unionpay.** {*;}


#glide图片框架
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
