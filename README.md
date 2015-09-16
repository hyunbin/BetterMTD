# BetterMTD
A work-in-progress, modern Android app for the Champaign-Urbana bus system. 

## Release Version
A release version of the code can be found on the Play Store as [Transit for CUMTD](https://play.google.com/store/apps/details?id=me.hyunbin.transit).

## Building the Project
In order to test debug builds, you'll have to add your own MTD API Key, which can be found [here](https://developer.cumtd.com/).
The file <code>[APIService.java](https://github.com/hyunbin/BetterMTD/blob/master/mobile/src/main/java/me/hyunbin/transit/ApiService.java#L22)</code> looks for a <code>MTD.java</code> file with the static variable <code>apiKey</code>. 

So, you must first create a file named <code>MTD.java</code> under <code>/mobile/src/main/java/me/hyunbin/transit/</code> and add your key as follows:

```java
package me.hyunbin.transit;

public class MTD {
    public static final String apiKey = "your key goes here";
}

```

Afterwards you should be able to compile and test the code. :]
