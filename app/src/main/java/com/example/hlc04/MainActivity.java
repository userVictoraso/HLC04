package com.example.hlc04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
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
    double dolarValue;
    private TextWatcher tv1;
    private TextWatcher tv2;
    ActivityMainBinding binding;
    DecimalFormat df = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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

    public void startService() {
        startService(new Intent(this, DownloadService.class));
        Toast.makeText(getBaseContext(), "Valor actualizado",
                Toast.LENGTH_SHORT).show();
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
                    double resultado = Double.valueOf(binding.editTextEuro.getText().toString()) * dolarValue;
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
                    double resultado = Double.valueOf(binding.editTextDolares.getText().toString()) / dolarValue;
                    binding.editTextEuro.removeTextChangedListener(tv1);
                    binding.editTextEuro.setText(String.valueOf(df.format(resultado)) + " €");
                    binding.editTextEuro.addTextChangedListener(tv1);
                } catch (NumberFormatException e) {
                    limpiarCampos();
                }
            }
        };
    }
}
