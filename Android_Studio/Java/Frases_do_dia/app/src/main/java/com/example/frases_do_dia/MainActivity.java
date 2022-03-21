package com.example.frases_do_dia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gerarNovaFrase(View view)
    {
        String[] frases = {
                "A persistência é o caminho do êxito.",
                "No meio da dificuldade encontra-se a oportunidade.",
                "Pedras no caminho? Eu guardo todas. Um dia vou construir um castelo.",
                "É parte da cura o desejo de ser curado."
        };

        int numero = new Random().nextInt(4); // 0,1,2,3
        TextView texto = findViewById(R.id.textoResultado);
        texto.setText(frases[numero]);
    }
}
