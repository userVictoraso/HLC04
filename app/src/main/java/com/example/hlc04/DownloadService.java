package com.example.hlc04;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadService extends Service {
    final String COIN_REPOSITORY = "https://dam.org.es/ficheros/cambio.txt";
    public double dolarValue;

    public DownloadService(){}

    @Override
    public void onCreate() {
        super.onCreate();
        mostrarMensaje("Iniciando servicio");
    }

    public int onStartCommand(Intent intent, int flags, int startID) {
        URL url = null;
        try {
            url = new URL(COIN_REPOSITORY);
            descargaOkHTTP(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la URL");
        }
        return super.onStartCommand(intent, flags, startID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mostrarMensaje("Finalizando servicio");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void descargaOkHTTP(URL web) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(COIN_REPOSITORY).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    String myResponseFixed = myResponse.replaceAll(",", ".");
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dolarValue = Double.parseDouble(myResponseFixed);
                            } catch (NumberFormatException n){
                                n.printStackTrace();
                            }
                        }
                    };
                }
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show();
    }

    public double getValue(){
        return dolarValue;
    }

}
