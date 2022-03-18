package com.example.calculadoradegorjeta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity
{
    private EditText editValor;
    private SeekBar seekBarGorjeta;
    private TextView textoPorcentagem, textoGorjeta, textoTotal;
    private double porcentagem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editValor = findViewById(R.id.editValor);
        seekBarGorjeta = findViewById(R.id.seekBarGorjeta);
        textoPorcentagem = findViewById(R.id.textoPorcentagem);
        textoGorjeta = findViewById(R.id.textoGorjeta);
        textoTotal = findViewById(R.id.textoTotal);

        // Adicionar listener SeekBar
        seekBarGorjeta.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                porcentagem = progress;
                textoPorcentagem.setText(Math.round(porcentagem) + "%");
                calcular();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void calcular()
    {
        String valorRecuperado = editValor.getText().toString();
        if(valorRecuperado == null || valorRecuperado.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Digite um valor primeiro", Toast.LENGTH_LONG).show();
        }
        else
        {
            // Converter String para double
            double valorDigitado = Double.parseDouble(valorRecuperado);

            // cálculo da gorjeta e do total
            double gorjeta = valorDigitado * (porcentagem/100);
            double total = valorDigitado + gorjeta;

            // exibe a gorjeta e o total
            textoGorjeta.setText("R$ " + String.format("%.2f",gorjeta));
            textoTotal.setText("R$ " + String.format("%.2f", total));
        }

    }
}
