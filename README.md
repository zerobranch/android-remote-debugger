# Android Remote Debugger
[![](https://jitpack.io/v/zerobranch/android-remote-debugger.svg)](https://jitpack.io/#zerobranch/android-remote-debugger) 
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/zerobranch/android-remote-debugger/blob/master/LICENSE)

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
    implementation 'com.github.zerobranch:android-remote-debugger:1.0.0-alpha'
}
```
 
## How to use ?
Android Remote Debugger has 4 sections:

* Logging
* Database
* Shared Preferences
* Network

For the `Logging`, `Database` and `Shared Preferences` sections to work, several steps are necessary:

1. Call: `AndroidRemoteDebugger.init (applicationContext)` in the application code.

2. After launching your application, you will receive a notification in the notification panel, in which a link of the type: http://xxx.xxx.x.xxx:8080 will be indicated. Just follow this link in your browser. Also, an entry will be added to logcat: `D/AndroidRemoteDebugger: Android Remote Debugger is started. Go to: http://xxx.xxx.x.xxx:8080`

3. To view the logs in the `Logging` section, you must call the static methods` AndroidRemoteDebugger.Log` anywhere in your application, for example, `AndroidRemoteDebugger.Log.d("tag", "message")` or `AndroidRemoteDebugger.Log.log(priority, tag, msg, throwable)` with all parameters specified.

4. To view network logs in the `Network` section, it is necessary to use the [OkHttp3](https://github.com/square/okhttp) library. To do this, add the `NetLoggingInterceptor` interceptor. Recommended to add it after all others interceptors to get actual network traffic data.

```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new NetLoggingInterceptor())
    .build();
```

**Attention**

* Your Android device and computer must be connected to the same network (Wi-Fi or LAN).
* You can also use debugging via usb or Android Default Emulator. To do this, run the command: `adb forward tcp:8080 tcp:8080` and go to the address:
[http://localhost:8080/](http://localhost:8080/) or [http://0.0.0.0:8080](http://0.0.0.0:8080) in your browser.
* If you use Android Default Emulator, then you may also need to turn on the mobile Internet and turn off WI-FI on the emulator.

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

1. Выбор уровня логирования
2. Отфильтровать логи по тегу
3. Включить/отключить автоскролл при получении новых логов
4. Перейти в начало списка
5. Перейти в конец списка
6. Вкоючить/отключить цвета для логов
7. Удалить все логи

### Примечание
* Ссылку на страницу отладчика можно также получить следующим образом: http://ip-адрес-вашего-android-устройства:порт (ip-адрес-вашего-android-устройства можно посмотреть в настройках Вашего смартфона)
* Если вы используете отладку через usb или Android Default Emulator и используете другой порт, например, 8081, то нужно запустить следующую команду: `adb forward tcp:8081 tcp:8081`
* Данную библиотеку можно использовать на одном androd устройстве для двух приложений одновременно только с РАЗНЫМИ портами.

## R8 / ProGuard
Если вы используете R8, вам не нужно ничего делать. Конкретные правила будут включены автоматически.

Если вы не используете R8, то Вам необходимо включить следующие правила:
```
-keep class com.zerobranch.androidremotedebugger.source.models.** { *; }
-keep class com.zerobranch.androidremotedebugger.source.local.LogLevel
```

Вам также могут понадобиться правила от [OkHttp3](https://github.com/square/okhttp) и [Gson](https://github.com/google/gson), которые являются зависимостями этой библиотеки.


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
