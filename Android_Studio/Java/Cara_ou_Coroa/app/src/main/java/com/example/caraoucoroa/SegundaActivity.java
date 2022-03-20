package com.example.caraoucoroa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SegundaActivity extends AppCompatActivity
{
    private ImageView imageResultado;
    private Button botaoVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);

        imageResultado = findViewById(R.id.imagemResultado);
        botaoVoltar = findViewById(R.id.botaoVoltar);

        // Recuperar dados
        Bundle dados = getIntent().getExtras();
        int numero = dados.getInt("numero");

        if (numero == 0) //cara
        {
            imageResultado.setImageResource(R.drawable.moeda_cara);
        }
        else //coroa
        {
            imageResultado.setImageResource(R.drawable.moeda_coroa);
        }

        botaoVoltar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }
}
