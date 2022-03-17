# Android Remote Debugger
[![](https://jitpack.io/v/zerobranch/android-remote-debugger.svg)](https://jitpack.io/#zerobranch/android-remote-debugger) 
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/zerobranch/android-remote-debugger/blob/master/LICENSE)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20Remote%20Debugger-green.svg?style=flat)](https://android-arsenal.com/details/1/8040)

**Android Remote Debugger** is a library for remote debugging Android applications. It allows you to view logs, databases, shared preferences and network requests directly in the browser.

### Choose language
[English](https://github.com/zerobranch/android-remote-debugger/blob/master/README.md) 

[Русский](https://github.com/zerobranch/android-remote-debugger/blob/master/RUSSIAN_README.md)

## Features
* Logging
	* View the logs of your application
	* Filter logs by priority and tags
	* Search logs
	* Download logs
	* Crash errors are also logged
* Database
	* View all databases
	* Edit database entries
 	* Delete database entries
 	* Search data
 	* Run custom sql query to get, add, update or delete data
* SharedPreferences
	* View all data
	* Edit, add and delete data
	* Search data
* Network
	* View all network requests and responses in a convenient format
	* Filter data by response code and errors
	* Download logs
	* Search data

## Integration
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the following dependency to your module's build.gradle:
```groovy
dependencies {
    debugImplementation 'com.github.zerobranch.android-remote-debugger:debugger:1.1.2'
    releaseImplementation 'com.github.zerobranch.android-remote-debugger:noop:1.1.2'
}
```
**Note:** The final line above will use a no-op version, which does nothing.
It should only be used in production build.
This makes it impossible to run the server on a production build.
 
## How to use ?
Android Remote Debugger has 4 sections:

* Logging
* Database
* Shared Preferences
* Network

For the `Logging`, `Database` and `Shared Preferences` sections to work, several steps are necessary:

1. Call: **`AndroidRemoteDebugger.init(applicationContext)`** in the application code.

2. After launching your application, you will receive a notification in the notification panel, in which a link of the type: http://xxx.xxx.x.xxx:8080 will be indicated. Just follow this link in your browser. Also, an entry will be added to logcat: `D/AndroidRemoteDebugger: Android Remote Debugger is started. Go to: http://xxx.xxx.x.xxx:8080`

3. To view the logs in the `Logging` section, you must call the static methods `AndroidRemoteDebugger.Log` anywhere in your application, for example, `AndroidRemoteDebugger.Log.d("tag", "message")` or `AndroidRemoteDebugger.Log.log(priority, tag, msg, throwable)` with all parameters specified.

4. To view network logs in the `Network` section, it is necessary to use the [OkHttp3](https://github.com/square/okhttp) library and add the `NetLoggingInterceptor` interceptor. Recommended to add it after all others interceptors to get actual network traffic data.

```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new NetLoggingInterceptor())
    .build();
```

**Attention**

* Your Android device and computer must be connected to the same network (Wi-Fi or LAN).
* You can also use debugging via usb or Android Default Emulator. To do this, run the command: `adb forward tcp:8080 tcp:8080` and go to the address:
[http://localhost:8080](http://localhost:8080) or [http://0.0.0.0:8080](http://0.0.0.0:8080) in your browser.
* If you use Android Default Emulator, then you may also need to turn on the mobile Internet and turn off WI-FI.

## Screenshots
### Logging
<img src="/screenshots/logging.png" alt="logging.png" title="logging.png" width="1194" height="600" /> 

### Database
<img src="/screenshots/database_1.png" alt="database_1.png" title="database_1.png" width="1194" height="600" /> 
<img src="/screenshots/database_2.png" alt="database_2.png" title="database_2.png" width="1194" height="600" /> 

### Network
<img src="/screenshots/network_1.png" alt="network_1.png" title="network_1.png" width="1194" height="600" /> 
<img src="/screenshots/network_2.png" alt="network_2.png" title="network_2.png" width="1194" height="600" /> 
<img src="/screenshots/network_3.png" alt="network_3.png" title="network_3.png" width="1194" height="600" /> 

### Shared Preferences
<img src="/screenshots/shared_preferences.png" alt="shared_preferences.png" title="shared_preferences.png" width="1194" height="600" /> 

### Additional settings
To configure the library, use `AndroidRemoteDebugger.Builder`

```java
AndroidRemoteDebugger.init(
    new AndroidRemoteDebugger.Builder(applicationContext)
        .enabled(boolean)
        .disableInternalLogging()
        .enableDuplicateLogging()
        .disableJsonPrettyPrint()
        .disableNotifications()
        .excludeUncaughtException()
        .port(int)
        .build()
);
```

### Description of `AndroidRemoteDebugger.Builder` parameters
All parameters for `AndroidRemoteDebugger.Builder` are optional. For standard library operation, just call `AndroidRemoteDebugger.init(applicationContext)`.

```java
.enabled(boolean) - library enable control
.disableInternalLogging() - disable internal logs of Android Remote Debugger
.disableJsonPrettyPrint() - disable pretty print json in `Logging` and` Network` sections
.disableNotifications() - disable Android Remote Debugger status notifications
.excludeUncaughtException() - exclude log printing when application crashes
.port(int) - use a different port than 8080
.enableDuplicateLogging() - all logs from `Logging` section will also be printed in logcat
.enableDuplicateLogging(new Logger() { - callback to get all logs from `Logging` section
    @Override
    public void log(int priority, String tag, String msg, Throwable th) {
    }
})
```

The `NetLoggingInterceptor` interceptor has two constructors: empty and with a callback to get all logs from `Network` section

```java
new NetLoggingInterceptor(new NetLoggingInterceptor.HttpLogger() {
    @Override
    public void log(HttpLogModel httpLogModel) {
    }
})
```

### Description of Logging page parameters
<img src="/screenshots/logging_2.png" alt="logging_2.png" title="logging_2.png" width="489" height="260" /> 

1. Choosing a logging level
2. Filter the logs by tag
3. Enable/disable autoscroll when receiving new logs
4. Go to the top of the list
5. Go to the end of the list
6. Enable/disable colors for logs
7. Delete all logs

### Note
* A link to the debugger page can also be obtained as follows: http://ip-address-of-your-android-device:port (you can see the ip-address-of-your-android-device in the settings of your device).
* If you use debugging via usb or Android Default Emulator and you want to use a different port, for example, 8081, then you need to run the following command: `adb forward tcp:8081 tcp:8081`.
* To use this library on one Android device for two applications simultaneously, you need to use different ports.

## R8 / ProGuard
If you use R8, you don't have to do anything. The specific rules are included automatically.

If you don't use R8 you have to apply the following rules:
```
-keep class zerobranch.androidremotedebugger.source.models.** { *; }
-keep class zerobranch.androidremotedebugger.source.local.LogLevel
```

You might also need rules from [OkHttp3](https://github.com/square/okhttp) and [Gson](https://github.com/google/gson), which are dependencies of this library.

## License

```
Copyright 2020 Arman Sargsyan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
