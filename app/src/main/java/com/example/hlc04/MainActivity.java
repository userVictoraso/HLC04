package com.example.hlc04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.hlc04.databinding.ActivityMainBinding;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    static final String URL = "https://dam.org.es/ficheros/cambio.txt";
    public static final String ACTION_RESP = "RESPUESTA_DESCARGA";

    double dolarValue;
    private TextWatcher tv1;
    private TextWatcher tv2;
    ActivityMainBinding binding;

    IntentFilter intentFilter;
    BroadcastReceiver broadcastReceiver;

    DecimalFormat df = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        intentFilter = new IntentFilter(ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastReceiver = new ReceptorOperacion();

        System.out.println("ASDASDASDASDASDASDASD11111111111111111111111111" + getDolarValue());
        startService(new Intent(MainActivity.this, DownloadService.class));
        System.out.println("ASDASDASDASDASDASDASD22222222222222222222222222" + getDolarValue());

        //TODO: Arreglar el seteo.
        //TODO: Utilizar un servicio para descargar el fichero continuamente cada 5 minutos.
        //TODO: Modificar la aplicación Conversor de moneda para que lance el servicio al iniciarse y finalice el servicio al terminar.
        //TODO: Receptor de anuncios para mostrar una notificación en pantalla con el valor del cambio cada vez que se descarga el archivo.

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
    public void onResume(){
        super.onResume();
        //---registrar el receptor ---
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        //--- anular el registro del recpetor ---
        unregisterReceiver(broadcastReceiver);
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
                    System.out.println(resultado);
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
        setDolarValue("0,84");
        return this.dolarValue;
    }

    public void setDolarValue(String dolarValue) {
        System.out.println("NGÁ: " + dolarValue);
        System.out.println("NGÁ: " + dolarValue);
        System.out.println("NGÁ: " + dolarValue);
        System.out.println("NGÁ: " + dolarValue);
        System.out.println("NGÁ: " + dolarValue);
        System.out.println("NGÁ: " + dolarValue);
        System.out.println("NGÁ: " + dolarValue);

        String stringToDouble = dolarValue.replaceAll(",", ".");
        this.dolarValue = Double.parseDouble(stringToDouble);
        System.out.println("NGE: " + this.dolarValue);
        System.out.println("NGE: " + this.dolarValue);
        System.out.println("NGE: " + this.dolarValue);
        System.out.println("NGE: " + this.dolarValue);
        System.out.println("NGE: " + this.dolarValue);
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }


    public class ReceptorOperacion extends BroadcastReceiver {
        MainActivity mainActivity;
        @Override
        public void onReceive(Context context, Intent intent) {
            String respuesta = intent.getStringExtra("resultado");
            mainActivity.mostrarMensaje(respuesta);
        }
    }
}
