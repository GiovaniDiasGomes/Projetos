package com.example.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.ifood.R;
import com.example.ifood.adapter.AdapterPedido;
import com.example.ifood.adapter.AdapterProduto;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.listener.RecyclerItemClickListener;
import com.example.ifood.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity
{
    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        // Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        // Inicializar componentes
        iniciailizarComponentes();

        // Configurações toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurar recyclerview
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        recyclerPedidos.setAdapter(adapterPedido);

        // Recuperar pedidos
        recuperarPedidos();

        // Adicionar evento de clique no recyclerview
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {

            }

            @Override
            public void onLongItemClick(View view, int position)
            {
                Pedido pedido = pedidos.get(position);
                pedido.setStatus("finalizado");
                pedido.atualizarStatus();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

            }
        }));

    }

    private void recuperarPedidos()
    {
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando dados")
                .setCancelable(false).build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef.child("pedidos_empresa").child(idEmpresa);

        Query pedidoPesquisa = pedidoRef.orderByChild("status").equalTo("confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                pedidos.clear();
                if(snapshot.getValue() != null)
                {
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private void iniciailizarComponentes()
    {
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }

}
