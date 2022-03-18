package com.example.atmconsultoria;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.net.URI;

public class MainActivity extends AppCompatActivity
{

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                enviarEmail();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder
                (
                        R.id.nav_principal, R.id.nav_servico, R.id.nav_clientes,
                        R.id.nav_contato, R.id.nav_sobre
                )
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void enviarEmail()
    {
        //String celular = "tel:119996352464";
        //Intent intent = new Intent(Intent.ACTION_CALL); Ligação dentro do App
        //Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(celular)); Outra forma de Ligar, mais simples
        //String imagem = "http://colorindonuvens.com/wp-content/uploads/2020/03/Wallpaper-4kPaisagens-ColorindoNuvens-6.jpg";
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imagem)); Pasar link de imagem
        //String endereco = "https://www.google.com.br/maps/place/Campinas+Medical+Comercial+Hospitalar+Ltda/@-22.9522172,-47.091432,15z/data=!4m5!3m4!1s0x0:0xf617d28c03745ecb!8m2!3d-22.9522117!4d-47.091446?hl=pt-BR";
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(endereco));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"atendimento@atmconsultoria.com.br",
                        "at@atmconsultoria.com.br"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contato pelo App");
        intent.putExtra(Intent.EXTRA_TEXT, "Mensagem automática, \nobrigado pela atenção");

        intent.setType("message/rfc822"); //Enviar email
        //intent.setType("text/plain"); Escolher um App
        //intent.setType("image/*"); Abrir qualquer tipo de imagem
        //intent.setType("application/pdf"); Abrir arquivo PDF

        //startActivity(intent);
        //startActivity(Intent.createChooser(intent, "Compartilhar")); Texto da tela de compartilhar
        startActivity(Intent.createChooser(intent, "Escolha um App"));
    }
}
