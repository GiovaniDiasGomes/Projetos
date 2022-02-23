package com.example.projeto_sorteio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sortearNumero(view: View)
    {
        var texto = findViewById<View>(R.id.textoSorteio) as TextView
        var numeroSorteado = Random.nextInt(11)
        texto.setText("Número sorteado é: $numeroSorteado")
    }
}
