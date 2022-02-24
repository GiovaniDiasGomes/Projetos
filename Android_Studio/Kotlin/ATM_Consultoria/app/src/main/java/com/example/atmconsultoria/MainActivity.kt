package com.example.atmconsultoria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(applicationContext, "onCreate", Toast.LENGTH_LONG).show()

        button_cliente.setOnClickListener {
            Toast.makeText(this, "Cliente foi clicado", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DetalhesClientesActivity::class.java)
            startActivity(intent)
        }
        button_empresa.setOnClickListener {
            Toast.makeText(this, "Empresa foi clicado", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DetalheEmpresaActivity::class.java)
            startActivity(intent)
        }
        button_servico.setOnClickListener {

            Toast.makeText(this, "Serviço foi clicado", Toast.LENGTH_LONG).show()

            val intent = Intent(this, DetalheServicoActivity::class.java)
            startActivity(intent)
        }
        button_contato.setOnClickListener {
            Toast.makeText(this, "Contato foi clicado", Toast.LENGTH_LONG).show()

            val intent = Intent(this, DetalheContatoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart()
    {
        super.onStart()
        Toast.makeText(applicationContext, "onStart", Toast.LENGTH_SHORT).show()
    }

    override fun onResume()
    {
        super.onResume()
        Toast.makeText(applicationContext, "onResume", Toast.LENGTH_SHORT).show()
    }

    override fun onPause()
    {
        super.onPause()
        Toast.makeText(applicationContext, "onPause", Toast.LENGTH_SHORT).show()
    }

    override fun OnStop()
    {
        super.onStop()
        Toast.makeText(applicationContext, "onStop", Toast.LENGTH_LONG).show()
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(applicationContext, "onRestart", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(applicationContext, "onDestroy", Toast.LENGTH_LONG).show()
    }
}
