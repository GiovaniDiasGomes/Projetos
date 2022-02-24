package com.example.caraoucoroa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_segunda.*

class SegundaActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda)

        val dados: Bundle? = intent.extras
        val numero = dados?.getInt("numero")

        if(numero ==0)
        {
            image_resultado.setImageResource(R.drawable.moeda_cara)
        }
        else
        {
            image_resultado.setImageResource(R.drawable.moeda_coroa)
        }

        button_voltar.setOnClickListener {
            finish()
            val intent = Intent(applicationContext,MainActivity:: class.java)
            startActivity(intent)

        }
    }
}
