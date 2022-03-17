package zerobranch.example;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import zerobranch.example.db.DBHelper;
import zerobranch.androidremotedebugger.AndroidRemoteDebugger;
import zerobranch.androidremotedebugger.logging.NetLoggingInterceptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
        AndroidRemoteDebugger.init(
                new AndroidRemoteDebugger.Builder(this)
                        .enabled(true)
                        .enableDuplicateLogging()
                        .build()
        );

        findViewById(R.id.verbose).setOnClickListener(v -> {
            AndroidRemoteDebugger.Log.v("tag", "This is a simple message.");
        });

        findViewById(R.id.info).setOnClickListener(v -> {
            AndroidRemoteDebugger.Log.i("tag", "This is an info message.");
        });

        findViewById(R.id.debug).setOnClickListener(v -> {
            AndroidRemoteDebugger.Log.d("tag", "This is a debug message.");
        });

        findViewById(R.id.warn).setOnClickListener(v -> {
            AndroidRemoteDebugger.Log.w("tag", "This is a warning message.");
        });

        findViewById(R.id.error).setOnClickListener(v -> {
            AndroidRemoteDebugger.Log.e("tag", "This error message");
        });

        findViewById(R.id.fatal).setOnClickListener(v -> {
            AndroidRemoteDebugger.Log.wtf("tag", "This is a fatal message.");
            int a = 1 / 0;
        });


        findViewById(R.id.flavor).setOnClickListener(v -> {
//                dbHelper.insertFlavor();
//                AndroidRemoteDebugger.stop();
//            dbHelper.insertFlavor();

            Set<String> integers = new HashSet<>();
            integers.add("100");
            integers.add("1001");
            integers.add("10032");
            integers.add("910024");

            SharedPreferences sharedPref = getSharedPreferences("test_preference", Context.MODE_PRIVATE);
            sharedPref.edit().putInt("key_2", 123).apply();
            sharedPref.edit().putBoolean("key_3", false).apply();
            sharedPref.edit().putFloat("key_4", 4.2f).apply();
            sharedPref.edit().putLong("key_5", 123L).apply();
            sharedPref.edit().putStringSet("key_5", integers).apply();

            SharedPreferences sharedPref1 = getSharedPreferences("QWE_PREF_2", Context.MODE_PRIVATE);
            sharedPref1.edit().putString("key_21", "value_21").apply();

            SharedPreferences sharedPref2 = getSharedPreferences("QWE_PREF_3", Context.MODE_PRIVATE);
            sharedPref2.edit().putString("key_31", "value_31").apply();

            SharedPreferences sharedPref3 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            sharedPref3.edit().putString("key_31", "value_31").apply();
        });

        findViewById(R.id.network1).setOnClickListener(v -> {
            send("http://www.mocky.io/v2/5dfa87362f00006700ff9a18"); // link and query
        });

        findViewById(R.id.network2).setOnClickListener(v -> {
            send("http://www.mocky.io/v2/5dfa884b2f00007200ff9a22"); // difficult link
        });

//        findViewById(R.id.network3).setOnClickListener(v -> {
//            send("http://www.mocky.io/v2/5d79ff7a320000749834ec26"); // empty
//        });
//
//        findViewById(R.id.network4).setOnClickListener(v -> {
//            send("http://www.mocky.io/v2/5d7bcf0a350000a96f3cadea"); // just link
//        });
//
//        findViewById(R.id.network5).setOnClickListener(v -> {
//            send("http://www.mocky.io/v2/5d90d3f63000002b00cacfe2"); // error
//        });
    }

    private void send(String url) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new NetLoggingInterceptor())
                    .build();

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"name\": \"Mercury\", \"radius\": 2439.7}");
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Api-key", "jfp74mvnWRQWRQWRf83Tls3j")
                    .addHeader("Token", "hdsjJ7f3Hkd7EWTHSDV32hfGJSAj72l")
                    .addHeader("DeviceId", "whs67DFSWE2gjfDADSUg4")
                    .build();

            try {
                Response response = client
                        .newCall(request)
                        .execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }).start();
    }
}


