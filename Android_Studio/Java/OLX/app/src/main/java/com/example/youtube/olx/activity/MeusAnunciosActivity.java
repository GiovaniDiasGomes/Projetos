    package com.example.youtube.olx.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.youtube.olx.adapter.AdapterAnuncios;
import com.example.youtube.olx.helper.ConfiguracaoFirebase;
import com.example.youtube.olx.helper.RecyclerItemClickListener;
import com.example.youtube.olx.model.Anuncio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.example.youtube.olx.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

    public class MeusAnunciosActivity extends AppCompatActivity
{
    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        // Configurações iniciais
        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario());

        // Inicializar componetes
        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurar RecyclerView
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter(adapterAnuncios);

        // Recupera anúncios para o usuário
        recuperarAnuncios();

        // Adicionar evento de clique no RecyclerView
        recyclerAnuncios.addOnItemTouchListener(new RecyclerItemClickListener
                (this, recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener()
                {
            @Override
            public void onItemClick(View view, int position)
            {

            }

            @Override
            public void onLongItemClick(View view, int position)
            {
                Anuncio anuncioSelecionado = anuncios.get(position);
                anuncioSelecionado.remover();

                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

            }
        }));



    }

    private void recuperarAnuncios()
    {
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Recuperando anúncios")
                .setCancelable(false).build();
        dialog.show();


        anuncioUsuarioRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                anuncios.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    anuncios.add(ds.getValue(Anuncio.class));
                }

                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public void inicializarComponentes()
    {
        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
    }

}
