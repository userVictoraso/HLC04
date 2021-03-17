package com.example.hlc04;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.hlc04.databinding.ActivityMainBinding;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final long time = 60000 * 5;
    static final String URL = "https://dam.org.es/ficheros/cambio.txt";
    public static final String ACTION_RESP = "RESPUESTA_DESCARGA";

    final Handler handler = new Handler();

    double dolarValue = 0.87; //Valor asignado por defecto.
    private TextWatcher tv1;
    private TextWatcher tv2;
    ActivityMainBinding binding;

    Intent intent;
    IntentFilter intentFilter;
    BroadcastReceiver broadcastReceiver;

    DecimalFormat df = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.popsound);

        intentFilter = new IntentFilter(ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastReceiver = new ReceptorOperacion();

        //LANZAR EL SERVICIO AL INICIAR LA APP
        intent = new Intent(MainActivity.this, DownloadService.class);
        startService(intent);

        /**LANZAR SERVICIO CADA 5 MINUTOS*/
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mostrarMensaje("Lanzando el servicio de nuevo");
                        startService(intent);
                        mediaPlayer.start(); //EMITE UN SONIDO CUANDO LANZA EL SERVICIO
                    }
                }, time);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, time);

        //TODO: Utilizar un servicio para descargar el fichero continuamente cada 5 minutos y mostrar el mensaje en el Broadcast.


        binding.switchMoneda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.editTextDolares.setEnabled(true);
                    binding.editTextEuro.setEnabled(false);
                    limpiarCampos();
                } else {
                    binding.editTextEuro.setEnabled(true);
                    binding.editTextDolares.setEnabled(false);
                    limpiarCampos();
                }
            }
        });

        comprobarDolarAEuro();
        comprobarEuroADolar();
        binding.editTextEuro.addTextChangedListener(tv1);
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mostrarMensaje("Servicio parado");
        stopService(intent);
    }

    public void limpiarCampos() {
        binding.editTextDolares.removeTextChangedListener(tv2);
        binding.editTextEuro.removeTextChangedListener(tv1);
        binding.editTextDolares.setText("");
        binding.editTextEuro.setText("");
        binding.editTextDolares.addTextChangedListener(tv2);
        binding.editTextEuro.addTextChangedListener(tv1);
    }

    public void comprobarEuroADolar() {
        tv1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (binding.editTextEuro.toString().length() < 1) {
                        limpiarCampos();
                    }
                    double resultado = Double.valueOf(binding.editTextEuro.getText().toString()) * getDolarValue();
                    binding.editTextDolares.removeTextChangedListener(tv2);
                    binding.editTextDolares.setText(String.valueOf(df.format(resultado)) + " $");
                    binding.editTextDolares.addTextChangedListener(tv2);
                } catch (NumberFormatException e) {
                    limpiarCampos();
                }
            }
        };
    }

    public void comprobarDolarAEuro() {
        tv2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (binding.editTextDolares.toString().length() == 0) {
                        limpiarCampos();
                    }
                    double resultado = Double.valueOf(binding.editTextDolares.getText().toString()) / getDolarValue();
                    binding.editTextEuro.removeTextChangedListener(tv1);
                    binding.editTextEuro.setText(String.valueOf(df.format(resultado)) + " €");
                    binding.editTextEuro.addTextChangedListener(tv1);
                } catch (NumberFormatException e) {
                    limpiarCampos();
                }
            }
        };
    }

    public double getDolarValue() {
        return this.dolarValue;
    }

    public void setDolarValueFromString(String dolarValue) {
        String stringToDouble = dolarValue.replaceAll(",", ".");
        this.dolarValue = Double.parseDouble(stringToDouble);
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public class ReceptorOperacion extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String respuesta = intent.getStringExtra("resultado");
            setDolarValueFromString(respuesta);
            mostrarMensaje("Valor asignado: " + respuesta);
        }
    }
}
