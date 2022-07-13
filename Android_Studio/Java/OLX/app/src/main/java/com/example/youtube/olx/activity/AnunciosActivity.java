package com.example.youtube.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.youtube.olx.R;
import com.example.youtube.olx.adapter.AdapterAnuncios;
import com.example.youtube.olx.helper.ConfiguracaoFirebase;
import com.example.youtube.olx.helper.RecyclerItemClickListener;
import com.example.youtube.olx.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity
{
    private FirebaseAuth autenticacao;
    private Button buttonRegiao, buttonCategoria;
    private RecyclerView recyclerAnunciosPublicos;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        // Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios");

        // Inicializar componentes
        inicializarComponentes();

        // Configurar RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();

        // Aplicar evento de clique
        recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerAnunciosPublicos, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Anuncio anuncioSelecionado = listaAnuncios.get(position);
                Intent intent = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                intent.putExtra("anuncioSelecionado", anuncioSelecionado);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position)
            {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

            }
        }));

    }

    public void filtrarPorEstado(View view)
    {
        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o estado desejado");

        // Configurar Spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        // Configurar spinners de estado
        final Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstados);


        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorEstado = true;
            }
        });
        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });

        AlertDialog dialog = dialogEstado.create();
        dialog.show();

    }

    public void filtrarPorCategoria(View view)
    {
        if(filtrandoPorEstado == true)
        {
            AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
            dialogEstado.setTitle("Selecione a categoria desejada");

            // Configurar Spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            // Configurar spinners de categorias
            final Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            String[] estados = getResources().getStringArray(R.array.categorias);
            ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, estados);
            adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapterEstados);


            dialogEstado.setView(viewSpinner);

            dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();
                }
            });
            dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {

                }
            });

            AlertDialog dialog = dialogEstado.create();
            dialog.show();

        }
        else
        {
            Toast.makeText(this, "Selecione primeiro um estado",
                    Toast.LENGTH_SHORT).show();
        }


    }

    public void recuperarAnunciosPorCategoria()
    {
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Recupeando anúncios")
                .setCancelable(false).build();
        dialog.show();

        // Configurar nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios")
                .child(filtroEstado).child(filtroCategoria);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                listaAnuncios.clear();
                for(DataSnapshot anuncios : snapshot.getChildren())
                {
                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add(anuncio);

                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    public void recuperarAnunciosPorEstado()
    {
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Recupeando anúncios")
                .setCancelable(false).build();
        dialog.show();

        // Configurar nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios")
                .child(filtroEstado);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                listaAnuncios.clear();
                for(DataSnapshot categorias : snapshot.getChildren())
                {
                    for(DataSnapshot anuncios : categorias.getChildren())
                    {
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);

                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }



    public void recuperarAnunciosPublicos()
    {
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Recupeando anúncios")
                .setCancelable(false).build();
        dialog.show();


        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot estados : snapshot.getChildren())
                {
                    for(DataSnapshot categorias : estados.getChildren())
                    {
                        for(DataSnapshot anuncios : categorias.getChildren())
                        {
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);

                        }
                    }
                }

                Collections.reverse(listaAnuncios);
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
        buttonRegiao = findViewById(R.id.buttonRegiao);
        buttonCategoria = findViewById(R.id.buttonCategoria);
        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(autenticacao.getCurrentUser() == null)
        {
            // Usuário deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }
        else
        {
            // Usuário logado
            menu.setGroupVisible(R.id.group_logado, true);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
                break;

            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
