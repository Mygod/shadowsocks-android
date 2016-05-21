import android.Keys._

lazy val root = Project(id = "shadowsocks", base = file(".")).aggregate(core, mobile)

lazy val core = (project in file("core")).settings(android.Plugin.androidBuildAar).settings(commonSettings).settings(
  exportJars := true,
  libraryDependencies ++= Seq(
    "com.android.support" % "appcompat-v7" % "23.4.0",
    "com.j256.ormlite" % "ormlite-core" % "4.48",
    "com.j256.ormlite" % "ormlite-android" % "4.48",
    "com.github.kevinsawicki" % "http-request" % "6.0",
    "com.google.android.gms" % "play-services-analytics" % "9.0.0",
    "com.google.android.gms" % "play-services-base" % "9.0.0",
    "dnsjava" % "dnsjava" % "2.1.7",
    "eu.chainfire" % "libsuperuser" % "1.0.0.201602271131"
  )
)

lazy val mobile = (project in file("mobile")).androidBuildWith(core).settings(commonSettings).settings(
  name := "shadowsocks-mobile",
  libraryDependencies ++= Seq(
    "com.android.support" % "design" % "23.4.0",
    "com.android.support" % "gridlayout-v7" % "23.4.0",
    "com.android.support" % "cardview-v7" % "23.4.0",
    "com.google.android.gms" % "play-services-ads" % "9.0.0",
    "com.google.android.gms" % "play-services-wearable" % "9.0.0",
    "com.google.zxing" % "android-integration" % "3.2.1",
    "com.github.clans" % "fab" % "1.6.3",
    "com.github.jorgecastilloprz" % "fabprogresscircle" % "1.01",
    "com.twofortyfouram" % "android-plugin-api-for-locale" % "1.0.2",
    "net.glxn.qrgen" % "android" % "2.0"
  )
)

lazy val wear = (project in file("wear")).androidBuildWith(core).settings(commonSettings).settings(
  name := "shadowsocks-wear",
  libraryDependencies ++= Seq(
    "com.google.android.gms" % "play-services-wearable" % "9.0.0",
    "com.google.android.support" % "wearable" % "1.4.0"
  )
)

lazy val commonSettings = Seq(
  platformTarget in Android := "android-23",

  scalaVersion := "2.11.8",

  compileOrder in Compile := CompileOrder.JavaThenScala,

  javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),

  scalacOptions ++= Seq("-target:jvm-1.6", "-Xexperimental"),

  ndkJavah in Android := List(),

  ndkBuild in Android := List(),

  shrinkResources in Android := true,

  typedResources in Android := false,

  resolvers += Resolver.jcenterRepo,

  resolvers += "JRAF" at "http://JRAF.org/static/maven/2",

  proguardVersion in Android := "5.2.1",

  proguardOptions in Android ++= Seq("-keep class com.github.shadowsocks.** { <init>(...); }",
    "-keep class com.github.shadowsocks.System { *; }",
    "-keepattributes *Annotation*",
    "-dontnote com.j256.ormlite.**",
    "-dontnote org.xbill.**",
    "-dontwarn org.xbill.**")
)

lazy val nativeBuild = TaskKey[Unit]("native-build", "Build native executables")

nativeBuild := {
  "./build.sh" !
}
