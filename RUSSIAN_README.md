# Android Remote Debugger

**Android Remote Debugger** - это библиотека, которая позволяет выполнять удаленную отладку Android приложений. Она позволяет просматривать логи, базу данных, shared preferences и сетевые запросы прямо в браузере.

### Выберите язык
[English](https://github.com/zerobranch/SwipeLayout/blob/master/README.md) 

[Русский](https://github.com/zerobranch/SwipeLayout/blob/master/RUSSIAN_README.md)

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
    implementation 'com.github.zerobranch:android-remote-debugger:0.1.4-alpha'
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
2. После запуска Вашего приложения, Вы получите уведомление в панели уведомлений, в котором будет указана ссылка. Просто перейдити по этой ссылке в вашем браузере.

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
        .enabled(true)
        .enableJsonPrettyPrint()
        .enableDuplicateLogging()
        .excludeUncaughtException()
        .build()
);
```

### Описание параметров `AndroidRemoteDebugger.Builder`
Все параметры для `AndroidRemoteDebugger.Builder` являются необязательными. Для стандартной работы библиотеки достаточно вызвать `AndroidRemoteDebugger.init(applicationContext)`.

```
.enabled(true) - управление включением
.enableJsonPrettyPrint() - включение форматирования json в разделах `Logging` и `Network`
.excludeUncaughtException() - исключить печать логов при краше приложения
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

**Примечание**
* Ссылку на страницу отладчика можно также получить следующим образом: http://ip-адрес-вашего-android-устройства:порт (ip-адрес-вашего-android-устройства можно посмотреть в настройках Вашего смартфона)
* Если вы используете отладку через usb или Android Default Emulator и используете другой порт, например, 8081, то нужно запустить следующую команду: `adb forward tcp:8081 tcp:8081`
* Данную библиотеку можно использовать на одном androd устройсте для двух приложений ОДНОВРЕМЕННО только с разными портами.


## License

```
The MIT License (MIT)

Copyright (c) 2018 zerobranch

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
