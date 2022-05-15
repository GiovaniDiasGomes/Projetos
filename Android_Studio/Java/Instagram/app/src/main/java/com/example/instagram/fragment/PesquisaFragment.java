package com.example.instagram.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;

import com.example.instagram.R;
import com.example.instagram.activity.PerfilAmigoActivity;
import com.example.instagram.adapter.AdapterPesquisa;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment
{
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;
    private String idUsuarioLogado;

    public PesquisaFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        // Iniciar componentes
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);

        // Configurações iniciais
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Configurar RecyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());

        // Configurar adapter
        recyclerPesquisa.setAdapter(adapterPesquisa);

        // Configurar evento de clique
        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                recyclerPesquisa, new RecyclerItemClickListener.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Usuario usuarioSelecionado = listaUsuarios.get(position);
                Intent intent = new Intent(getActivity(), PerfilAmigoActivity.class);
                intent.putExtra("usuarioSelecionado", usuarioSelecionado);
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

        // Configurar o searchview
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                String textoDigitado = newText.toUpperCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });

        return view;
    }

    private void pesquisarUsuarios(String texto)
    {
        // Limpar lista
        listaUsuarios.clear();

        // Pesquisa usuários caso tenha texto na pesquisa
        if(texto.length() >= 3)
        {
            Query query = usuariosRef.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    // Limpar lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        // Verificar se é o usuário logado e remove da lista
                        Usuario usuario = ds.getValue(Usuario.class);
                        if (idUsuarioLogado.equals(usuario.getId()))
                        {
                            continue;
                        }

                        // Adiciona usuário na lista
                        listaUsuarios.add(usuario);
                    }

                    adapterPesquisa.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }
        else { //para limpar o recyclerView caso não tenha nenhuma letra na persquisa

            listaUsuarios.clear();  //<<++++ adicionar!!!
            adapterPesquisa.notifyDataSetChanged(); //<<++++ adicionar!!!
        }

    }

}
