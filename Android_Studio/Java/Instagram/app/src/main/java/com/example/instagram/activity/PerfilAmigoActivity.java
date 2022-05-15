package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.Snapshot;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity
{
    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private String idUsuarioLogado;
    private List<Postagem> postagens;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Inicializar componentes
        inicializarComponentes();

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_fechar_preto_24dp);

        // Recuperar usuário selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Configurar referência postagens usuário
            postagensUsuarioRef = ConfiguracaoFirebase.getFirebase().child("postagens")
                    .child(usuarioSelecionado.getId());

            // Configurar nome do usuário na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            // Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if(caminhoFoto != null)
            {
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this).load(url).into(imagePerfil);
            }


        }

        // Inicializar Image Loader
        inicializarImageLoader();

        // Carregar as fotos das postagens de um usuário
        carregarFotosPostagem();

        // Abrir foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Postagem postagem = postagens.get(position);
                Intent intent = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);

                intent.putExtra("postagem", postagem);
                intent.putExtra("usuario", usuarioSelecionado);
                startActivity(intent);

            }
        });

    }

    public void inicializarImageLoader()
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()).build();
        ImageLoader.getInstance().init(config);
    }

    public void carregarFotosPostagem()
    {
        // Recuperar as fotos postadas pelo usuário
        postagens = new ArrayList<>();
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
                    postagens.add(postagem);
                    urlFotos.add(postagem.getCaminhoFoto());
                }

                Collections.reverse(urlFotos);

                // Configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void habilitarBotaoSeguir(boolean segueUsuario)
    {
        if(segueUsuario)
        {
            buttonAcaoPerfil.setText("Seguindo");
        }
        else
        {
            buttonAcaoPerfil.setText("Seguir");

            // Adicionar evento para seguir o usuário
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });

        }
    }

    private void recuperarDadosUsuarioLogado()
    {
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                // Recuperar dados de usuário logado
                usuarioLogado = snapshot.getValue(Usuario.class);

                // Verifica se usuário já está seguindo amigo selecionado
                verificaSegueUsuarioAmigo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioAmigo)
    {
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();

        dadosUsuarioLogado.put("nome", usuarioLogado.getNome());
        dadosUsuarioLogado.put("caminhoFoto", usuarioLogado.getCaminhoFoto());

        DatabaseReference seguidorRef = seguidoresRef.child(usuarioAmigo.getId())
                .child(usuarioLogado.getId());

        seguidorRef.setValue(dadosUsuarioLogado);

        // Alterar botao ação para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        // Incrementar seguindo do usuário logado
        int seguindo = usuarioLogado.getSeguindo() + 1;

        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);

        DatabaseReference usuarioSeguindo = usuariosRef.child(usuarioLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        // Incrementar seguidores do amigo
        int seguidores = usuarioAmigo.getSeguidores() + 1;

        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);

        DatabaseReference usuarioSeguidores = usuariosRef.child(usuarioAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);


    }

    private void verificaSegueUsuarioAmigo()
    {
        DatabaseReference seguidorRef = seguidoresRef.child(usuarioSelecionado.getId())
                .child(idUsuarioLogado);

        seguidorRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                // Já está seguindo
                if(snapshot.exists())
                {
                    Log.i("dadosUsuario", ": Seguindo");
                    habilitarBotaoSeguir(true);
                }
                else
                {
                    // Ainda não está seguindo
                    Log.i("dadosUsuario", ": Seguir");
                    habilitarBotaoSeguir(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void recuperarDadosPerfilAmigo()
    {
        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(new ValueEventListener()
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void inicializarComponentes()
    {
        imagePerfil = findViewById(R.id.imagePerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        gridViewPerfil = findViewById(R.id.gridViewPerfil);
        buttonAcaoPerfil.setText("Carregando");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Recuperar dados do amigo selecionado
        recuperarDadosPerfilAmigo();

        // Recuperar dados usuário logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return false;
    }
}
