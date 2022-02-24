package com.example.alcoolougasolina

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btCalcular(@Suppress("UNUSED_PARAMETER")view: View)
    {
        // Recupera valores digitados
        //val precoAlcool = findViewById<View>(R.id.edit_preco_alcool) as TextView
        // Apenas com apply plugin: 'kotlin-android-extensions' em Gradle Scripts => build.gradle
        val precoAlcool = edit_preco_alcool.text.toString()
        val precoGasolina = edit_preco_gasolina.text.toString()

        val validaCampos = validarCampos(precoAlcool, precoGasolina)
        if (validaCampos == true)
        {
            calcularMelhorPreco(precoAlcool, precoGasolina)
        }
        else
        {
            text_resultado.setText("Preencha os campos vazios!")
        }
    }

    fun validarCampos(precoAlcool: String, precoGasolina: String) : Boolean
    {
        var camposValidados: Boolean = true

        if (precoAlcool.equals("")) //precoAlcool == null (null sempre será falso)
        {
            camposValidados = false
        }
        else if(precoGasolina.equals("")) //precoGasolina == null (null sempre será falso)
        {
            camposValidados = false
        }

        return camposValidados
    }

    fun calcularMelhorPreco(precoAlcool: String, precoGasolina: String)
    {
        // Converter valores strings para números
        val valorAlcool = precoAlcool.toDouble()
        val valorGasolina = precoGasolina.toDouble()

        /* Faz cálculo (precoAlcool/precoGasolina)
            * Se resultado >= 0.7 melhor utilizar Gasolina
            * senão melhor utilizar Álcool
         */
        val resultadoPreco = valorAlcool / valorGasolina

        if (resultadoPreco >= 0.7)
        {
            text_resultado.setText("Melhor utilizar Gasolina!")
        }
        else
        {
            text_resultado.setText("Melhor utilizar Álcool!")
        }
    }
}
