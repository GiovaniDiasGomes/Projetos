package com.example.alcoolougasolina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity
{
    private TextInputEditText editPrecoAlcool, editPrecoGasolina;
    private TextView textoResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPrecoAlcool = findViewById(R.id.editPrecoAlcool);
        editPrecoGasolina = findViewById(R.id.editPrecoGasolina);
        textoResultado = findViewById(R.id.textoResultado);
    }

    public void calcularPreco(View view)
    {
        // recuperar valores digitados
        String precoAlcool = editPrecoAlcool.getText().toString();
        String precoGasolina = editPrecoGasolina.getText().toString();

        // validar os campos digitados
        Boolean camposValidados = validarCampos(precoAlcool, precoGasolina);
        if (camposValidados)
        {
            resultado(precoAlcool, precoGasolina);
        }
        else
        {
            textoResultado.setText("Preencha os preços primeiro!");
        }
    }

    public Boolean validarCampos(String pAlcool, String pGasolina)
    {
        Boolean camposValidados = true;

        if(pAlcool == null || pAlcool.equals(""))
        {
            camposValidados = false;
        }
        else if (pGasolina == null || pGasolina.equals(""))
        {
            camposValidados = false;
        }

        return camposValidados;
    }

    public void resultado(String precoAlcool, String precoGasolina)
    {
        // convertendo string para números
        Double valorAlcool = Double.parseDouble( precoAlcool);
        Double valorGasolina = Double.parseDouble( precoGasolina);

        // cálculo de menor preço
        Double resultado = valorAlcool/valorGasolina;

        if (resultado >= 0.7)
        {
            textoResultado.setText("É melhor utilizar Gasolina!");
        }
        else
        {
            textoResultado.setText("É melhor utilizar Álcool!");
        }
    }
}
