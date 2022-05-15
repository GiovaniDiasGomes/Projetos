package com.example.instagram.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activity.EditarPerfilActivity;
import com.example.instagram.activity.PerfilAmigoActivity;
import com.example.instagram.adapter.AdapterGrid;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment
{
    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    private GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;
    private Usuario usuarioLogado;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfil;
    private AdapterGrid adapterGrid;

    public PerfilFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        // Configurar referência postagens usuário
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase().child("postagens")
                .child(usuarioLogado.getId());

        // Configurações dos componentes
        inicializarComponentes(view);

        // Abrir edição do perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(intent);

            }
        });

        // Inicializar Image Loader
        inicializarImageLoader();

        // Carregar as fotos das postagens de um usuário
        carregarFotosPostagem();


        return view;
    }

    private void recuperarDadosUsuarioLogado()
    {
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Usuario usuario = snapshot.getValue(Usuario.class);

                String postagens = String.valueOf(usuario.getPostagens());
                String seguindo = String.valueOf(usuario.getSeguindo());
                String seguidores = String.valueOf(usuario.getSeguidores());

                // Configurar valores recuperados
                textPublicacoes.setText(postagens);
                textSeguindo.setText(seguindo);
                textSeguidores.setText(seguidores);

                imagePerfil.setImageResource(R.drawable.avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public void carregarFotosPostagem()
    {
        // Recuperar as fotos postadas pelo usuário
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                // Configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid/3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Postagem postagem = ds.getValue(Postagem.class);
                    urlFotos.add(postagem.getCaminhoFoto());
                }

                Collections.reverse(urlFotos);

                // Configurar adapter
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void recuperarFotoUsuario()
    {
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if(caminhoFoto != null)
        {
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity()).load(url).into(imagePerfil);
        }

    }

    public void inicializarImageLoader()
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()).build();
        ImageLoader.getInstance().init(config);
    }

    private void inicializarComponentes(View view)
    {
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imagePerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // Recuperar dados do amigo selecionado
        recuperarDadosUsuarioLogado();

        // Recuperar foto do usuário
        recuperarFotoUsuario();

    }

    @Override
    public void onStop()
    {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }

}
