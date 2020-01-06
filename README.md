# Android Remote Debugger
[![](https://jitpack.io/v/zerobranch/android-remote-debugger.svg)](https://jitpack.io/#zerobranch/android-remote-debugger) 
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/zerobranch/android-remote-debugger/blob/master/LICENSE)

**Android Remote Debugger** - это библиотека, которая позволяет выполнять удаленную отладку Android приложений. Она позволяет просматривать логи, базу данных, shared preferences и сетевые запросы прямо в браузере.

### Выберите язык
[English](https://github.com/zerobranch/android-remote-debugger/blob/master/README.md) 

[Русский](https://github.com/zerobranch/android-remote-debugger/blob/master/RUSSIAN_README.md)

## Возможности
* Логирование
	* Просматривать логи Вашего приложения
	* Фильтровать логи по приоритету и тегам
	* Выполнять поиск
	* Скачивать логи
	* Логируются также ошибки при падении приложения
* База данных
	* Просмотр всех баз данных
	* Редактировать записи базы данных
 	* Удалять записи базы данных
 	* Выполнять поиск по всем данным
 	* Выполнить любой sql-запрос для получения, добавления, обновления или удаления данных
* SharedPreferences
	* Просмотр всех данных
	* Редактировать, добавлять и удалять данные
	* Выполнять поиск по всем данным
* Network
	* Просматривать все сетевые запросы и ответы в удобном формате
	* Фильтровать данные по коду ответа и ошибкам
	* Скачивать логи
	* Выполнять поиск по всем данным
 
 
## Интеграция
Добавьте в корневой build.gradle следующий репозиторий:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Добавьте в build.gradle вашего модуля следующую зависимость:
```groovy
dependencies {
    implementation 'com.github.zerobranch:android-remote-debugger:1.0.0-alpha'
}
```
 
## Как использовать ?
Android Remote Debugger имеет 4 раздела:

* Logging
* Database
* Shared Preferences
* Network

Для работы разделов `Logging`, `Database` и `Shared Preferences` необходимо выполнить несколько шагов:

1. Вызвать: `AndroidRemoteDebugger.init(applicationContext)` в коде приложения.
2. После запуска Вашего приложения, Вы получите уведомление в панели уведомлений, в котором будет указана ссылка типа: http://xxx.xxx.x.xxx:8080. Просто перейдити по этой ссылке в вашем браузере. Также в logcat будет добавлена запись:  `D/AndroidRemoteDebugger: Android Remote Debugger is started. Go to: http://xxx.xxx.x.xxx:8080`

3. Чтобы просматривать логи в разделе `Logging` необходимо вызывать статические методы `AndroidRemoteDebugger.Log` в любом месте вашего приложения, например, `AndroidRemoteDebugger.Log.d("tag", "message")` или `AndroidRemoteDebugger.Log.log(priority, tag, msg, throwable)` с указанием всех параметров.

4. Просматривать логи сети в разделе `Network` возможно в том случае, если Вы используете библиотку [OkHttp3](https://github.com/square/okhttp). Для этого необходимо добавить интерцептор `NetLoggingInterceptor`. Для получения достоверных данных, рекомендуется добавлять его последнием, после других интерцепторов.

```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new NetLoggingInterceptor())
    .build();
```

**Внимание**

* Ваше Android устройство и компьютер должны быть подключены к одной сети (Wi-Fi или LAN).
* Вы также можете использовать отладку через usb или Android Default Emulator. Для этого запустите команду: `adb forward tcp:8080 tcp:8080` и перейдити по адресу: 
[http://localhost:8080/](http://localhost:8080/) или [http://0.0.0.0:8080](http://0.0.0.0:8080) в Вашем браузере.
* Есои вы используете Android Default Emulator, то возможно Вам также придется включить мобильный интернет и отключить WI-FI на эмуляторе.

## Скриншоты
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

### Дополнительная настройка

Для дополнительной настройки библиотеки используйте `AndroidRemoteDebugger.Builder`

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

### Описание параметров `AndroidRemoteDebugger.Builder`
Все параметры для `AndroidRemoteDebugger.Builder` являются необязательными. Для стандартной работы библиотеки достаточно вызвать `AndroidRemoteDebugger.init(applicationContext)`.

```java
.enabled(boolean) - управление включением
.disableInternalLogging() - отключить внутренние логи Android Remote Debugger
.disableJsonPrettyPrint() - отключение форматирования json в разделах `Logging` и `Network`
.disableNotifications() - отключить показ уведомлений статуса работы Android Remote Debugger
.excludeUncaughtException() - исключить печать логов при краше приложения
.port(int) - использовать другой порт, отличный от 8080
.enableDuplicateLogging() - все логи из раздела `Logging` будут также напечатаны в консоли
.enableDuplicateLogging(new Logger() { - callback для получения всех логов из раздела `Logging`
    @Override
    public void log(int priority, String tag, String msg, Throwable th) {
    }
})
```


Интерцептор `NetLoggingInterceptor` имеет два конструктора: пустой и с callback'ом для получения всех логов из раздела `Network`

```java
new NetLoggingInterceptor(new NetLoggingInterceptor.HttpLogger() {
    @Override
    public void log(HttpLogModel httpLogModel) {
    }
})
```

### Описание параметров страницы Logging
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
