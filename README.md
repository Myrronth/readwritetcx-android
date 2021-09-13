# ReadWriteTcx for Android

A library to read (parse) and write TCX files, built for Android. The reference schema is [Training Center Database v2]
(https://www8.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd).

Currently, only a small subset is implementented:
- Activities
  - Activity
    - Sport
    - Id
    - Lap
    - Notes
- ActivityTrackpointExtension
- ActivityLapExtension

[![](https://jitpack.io/v/Myrronth/readwritetcx-android.svg)](https://jitpack.io/#Myrronth/readwritetcx-android)

## Download

Add the Jitpack repository to your root build file:

```kotlin
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

```kotlin
dependencies {
    implementation("com.github.myrronth:readwritetcx-android:0.1.0")
}
```

## Usage

See the tests or the example app. 
