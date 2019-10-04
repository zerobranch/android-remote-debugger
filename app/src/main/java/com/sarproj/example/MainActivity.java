package com.sarproj.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sarproj.example.db.DBHelper;
import com.sarproj.remotedebugger.RemoteDebugger;
import com.sarproj.remotedebugger.logging.NetLoggingInterceptor;
import com.sarproj.remotedebugger.utils.FileUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getApplicationContext());

        setContentView(R.layout.activity_main);

        RemoteDebugger.init(new RemoteDebugger
                .Builder(getApplication())
                .enabled(true)
                .enableInternalLogging()
                .enableJsonPrettyPrint()
                .enableDefaultLogging() // todo переименовать mirror log to console и протестить теги (todo протестить когда запущен другой сервер а мой нет)
        );

//        RemoteLog.d("My debug");
//        RemoteLog.d("My debug", "testTag");
//        RemoteLog.d("My debug", "testTag", new RuntimeException("Null pointer"));
//
//        RemoteLog.v("My Verbose");
//        RemoteLog.v("My Verbose", "testTag");
//        RemoteLog.v("My Verbose", "testTag", new RuntimeException("Null pointer"));
//
//        RemoteLog.w("My warning");
//        RemoteLog.w("My warning", "testTag");
//        RemoteLog.w("My warning", "testTag", new RuntimeException("Null pointer"));
//
//        RemoteLog.i("My info");
//        RemoteLog.i("My info", "testTag");
//        RemoteLog.i("My info", "testTag", new RuntimeException("Null pointer"));
//
//        RemoteLog.e("My error");
//        RemoteLog.e("My error", "testTag");
//        RemoteLog.e("My error", "testTag", new RuntimeException("Null pointer"));
//
//        RemoteLog.f("My fatal");
//        RemoteLog.f("My fatal", "testTag");
//        RemoteLog.f("My fatal", "testTag", new RuntimeException("Null pointer"));


        findViewById(R.id.user).setOnClickListener(v -> {
//                dbHelper.insertUser();
//                RemoteDebugger.init(getApplicationContext(), true, true);
            dbHelper.insertUser();
        });

        findViewById(R.id.debug).setOnClickListener(v -> {
            String val = FileUtils.getTextFromAssets(getAssets(), "test.json");
            RemoteDebugger.Log.d("test json", val);

            int i = 4;
            while (i > 0) {
                RemoteDebugger.Log.d("testTag", "[ " + i + " ] debug это уровенть отладки");
                i--;
            }

            i = -4;
            while (i < 0) {
                RemoteDebugger.Log.d("farcry", "[ " + i + " ] debug это уровенть отладки");
                i++;
            }
        });

        findViewById(R.id.info).setOnClickListener(v -> {
            int i = 4;
            while (i > 0) {
                RemoteDebugger.Log.i("testTag", "[ " + i + " ] \"info\" информационная линия просмотра");
                i--;
            }

            RemoteDebugger.Log.i("testTag", "[ " + 3 + " ] info самая минимальная линия");

            i = -4;
            while (i < 0) {
                RemoteDebugger.Log.i("farcry", "[ " + i + " ] info информационная линия просмотра");
                i++;
            }
        });

        findViewById(R.id.verbose).setOnClickListener(v -> {
            int i = 4;
            while (i > 0) {
                RemoteDebugger.Log.v("testTag", "[ " + i + " ] verbose 'самая' минимальная линия");
                i--;
            }

            i = -4;
            while (i < 0) {
                RemoteDebugger.Log.v("farcry", "[ " + i + " ] verbose самая минимальная линия");
                i++;
            }
        });

//        findViewById(R.id.error).setOnLongClickListener(view -> {
//            try {
//                int a = 1 / 0;
//            } catch (Throwable th) {
//                RemoteDebugger.e("testTag", "asd", th);
//            }
//            return false;
//        });

        findViewById(R.id.error).setOnClickListener(v -> {
            int i = 4;
            while (i > 0) {
                RemoteDebugger.Log.e("testTag", "[ " + i + " ] error " +
                        "2019-04-10 21:32:32.374 23388-23413/? E/StandaloneKeepAlive: Attempting to start service when the app is in background is not allowed on Android O+. Intent: Intent { cmp=com.google.android.googlequicksearchbox/com.google.android.apps.gsa.shared.util.keepalive.StandaloneKeepAlive$KeepAliveService }\n" +
                        "    java.lang.IllegalStateException: Not allowed to start service Intent { cmp=com.google.android.googlequicksearchbox/com.google.android.apps.gsa.shared.util.keepalive.StandaloneKeepAlive$KeepAliveService }: app is in background uid UidRecord{60ade97 u0a35 TRNB bg:+1h36m26s353ms idle change:uncached procs:1 seq(163,163,163)}\n" +
                        "        at android.app.ContextImpl.startServiceCommon(ContextImpl.java:1577)\n" +
                        "        at android.app.ContextImpl.startService(ContextImpl.java:1532)\n" +
                        "        at android.content.ContextWrapper.startService(ContextWrapper.java:664)\n" +
                        "        at com.google.android.apps.gsa.shared.util.keepalive.StandaloneKeepAlive.a(SourceFile:58)\n" +
                        "        at com.google.android.apps.gsa.shared.util.keepalive.StandaloneKeepAlive.a(SourceFile:9)\n" +
                        "        at com.google.android.apps.gsa.search.core.service.bm.a(SourceFile:16)\n" +
                        "        at com.google.android.apps.gsa.search.core.service.bp.a(SourceFile:16)\n" +
                        "        at com.google.android.apps.gsa.search.core.service.am.run(Unknown Source:8)\n" +
                        "        at com.google.android.libraries.gsa.n.a.a.run(Unknown Source:2)\n" +
                        "        at com.google.android.apps.gsa.shared.util.c.b.cg.a(SourceFile:2)\n" +
                        "        at com.google.android.apps.gsa.shared.util.c.b.cf.run(SourceFile:5)\n" +
                        "        at com.google.android.apps.gsa.shared.util.c.b.ap.a(SourceFile:7)\n" +
                        "        at com.google.android.apps.gsa.shared.util.c.b.aq.run(Unknown Source:2)\n" +
                        "        at com.google.android.apps.gsa.shared.util.c.b.g.run(Unknown Source:3)\n" +
                        "        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458)\n" +
                        "        at java.util.concurrent.FutureTask.run(FutureTask.java:266)\n" +
                        "        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:301)\n" +
                        "        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)\n" +
                        "        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)\n" +
                        "        at java.lang.Thread.run(Thread.java:764)\n" +
                        "        at com.google.android.apps.gsa.shared.util.c.b.l.run(SourceFile:6)");
                i--;
            }

            i = -4;
            while (i < 0) {
                RemoteDebugger.Log.e("farcry", "[ " + i + " ] error тут возможно произошла ошибка");
                i++;
            }
        });

        findViewById(R.id.warn).setOnClickListener(v -> {
            int i = 4;
            while (i > 0) {
                RemoteDebugger.Log.w("testTag", "[ " + i + " ] warn это навернео уровень предупреждения");
                i--;
            }

            i = -4;
            while (i < 0) {
                RemoteDebugger.Log.w("farcry", "[ " + i + " ] warn это навернео уровень предупреждения");
                i++;
            }
        });

        findViewById(R.id.fatal).setOnClickListener(v -> {
            int i = 4;
            while (i > 0) {
                RemoteDebugger.Log.f("testTag", "[ " + i + " ] fatal фатальная почти невозможная ошибка");
                i--;
            }

            i = -4;
            while (i < 0) {
                RemoteDebugger.Log.f("farcry", "[ " + i + " ] fatal фатальная почти невозможная ошибка");
                i++;
            }
        });

        findViewById(R.id.flavor).setOnClickListener(v -> {
//                dbHelper.insertFlavor();
//                RemoteDebugger.stop();
            dbHelper.insertFlavor();
        });

//        findViewById(R.id.flavor).setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                Set<String> integers = new HashSet<>();
//                integers.addLog("100");
//                integers.addLog("1001");
//                integers.addLog("10032");
//                integers.addLog("910024");
//
//                SharedPreferences sharedPref = getSharedPreferences("QWE_PREF_1", Context.MODE_PRIVATE);
//                sharedPref.edit().putInt("key_2", 123).apply();
//                sharedPref.edit().putBoolean("key_3", false).apply();
//                sharedPref.edit().putFloat("key_4", 4.2f).apply();
//                sharedPref.edit().putLong("key_5", 123L).apply();
//                sharedPref.edit().putStringSet("key_5", integers).apply();
//
//                SharedPreferences sharedPref1 = getSharedPreferences("QWE_PREF_2", Context.MODE_PRIVATE);
//                sharedPref1.edit().putString("key_21", "value_21").apply();
//
//                SharedPreferences sharedPref2 = getSharedPreferences("QWE_PREF_3", Context.MODE_PRIVATE);
//                sharedPref2.edit().putString("key_31", "value_31").apply();
//
//                SharedPreferences sharedPref3 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//                sharedPref3.edit().putString("key_31", "value_31").apply();
//                return false;
//            }
//        });

        findViewById(R.id.network1).setOnClickListener(v -> {
//            RemoteDebugger.Log.d("tags", "asd");
            send("http://www.mocky.io/v2/5d7bcf0a350000a96f3cadea?type=everType&profile=sky"); // link and query
        });

        findViewById(R.id.network2).setOnClickListener(v -> {
            send("http://www.mocky.io/v2/5d7bd3e63500006f009139e6"); // difficult link
        });

        findViewById(R.id.network3).setOnClickListener(v -> {
            send("http://www.mocky.io/v2/5d79ff7a320000749834ec26"); // empty
        });

        findViewById(R.id.network4).setOnClickListener(v -> {
            send("http://www.mocky.io/v2/5d7bcf0a350000a96f3cadea"); // just link
        });

        findViewById(R.id.network5).setOnClickListener(v -> {
            send("http://www.mocky.io/v2/5d90d3f63000002b00cacfe2"); // error
        });

        findViewById(R.id.network6).setOnClickListener(v -> {
            send("http://www.blablabla.io/v2/5d7bcf0a350000a96f3cadea?type=everType&profile=sky");
        });

        findViewById(R.id.network7).setOnClickListener(v -> {
            send("http://www.mocky.io/v2/5d94a7962f00002a008ff899");
        });
    }

    private void send(String url) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        return chain.proceed(originalRequest);
                    })
//                                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(new NetLoggingInterceptor(/*httpLogModel -> {
                        System.out.println("--- " + httpLogModel.toString());
                    }*/))
                    .build();

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"planet\":\"earth\",\"star\":\"sun\",\"nextStar\":\"proxima\"}");
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Api-key", "j4hg45k3lk2")
                    .addHeader("Token", "gj3h2k4923hb4k2")
                    .addHeader("DeviceId", "Samsung")
                    .build();

            try {
                Response response = client.newCall(request)
                        .execute();
                response.body().string();
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }).start();
    }
}
