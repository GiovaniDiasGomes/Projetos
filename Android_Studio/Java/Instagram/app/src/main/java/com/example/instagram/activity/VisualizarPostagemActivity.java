package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.adapter.AdapterFeed;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.PostagemCurtida;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity
{
    private TextView textPerfilPostagem, textQtdCurtidasPostagem, textDescricaoPostagem,
            textVisualizarComentariosPostagem;
    private ImageView imagePostagemSelecionada, visualizarComentario;
    private CircleImageView imagePerfilPostagem;
    private LikeButton likeButtonFeed;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        // Inicializar componentes
        inicializarComponentes();

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar postagem");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_fechar_preto_24dp);

        // Recuperar dados da activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            // Exibe dados do usuário
            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this).load(uri).into(imagePerfilPostagem);
            textPerfilPostagem.setText(usuario.getNome());

            // Exibe dados da postagem
            Uri uriPostagem = Uri.parse(postagem.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this).load(uriPostagem)
                    .into(imagePostagemSelecionada);
            textDescricaoPostagem.setText(postagem.getDescricao());

            // Referência no firebase para curtidas
            //Feed feed = new Feed();
            final Usuario usarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

            //DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
            //        .child("postagens-curtidas").child(feed.getId());
            DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase().child("postagens-curtidas")
                    .child(postagem.getId());
            curtidasRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    int qtdCurtidas = 0;
                    if(snapshot.hasChild("qtdCurtidas"))
                    {
                        PostagemCurtida postagemCurtida = snapshot.getValue(PostagemCurtida.class);
                        qtdCurtidas = postagemCurtida.getQtdCurtidas();
                    }

                    // Verifica se já foi clicado
                    if (snapshot.hasChild(usarioLogado.getId()))
                    {
                        likeButtonFeed.setLiked(true);
                    }
                    else
                    {
                        likeButtonFeed.setLiked(false);
                    }

                    final PostagemCurtida curtida = new PostagemCurtida();
                    textQtdCurtidasPostagem.setText(qtdCurtidas + " curtidas");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

            // Apresentar os comentários???

        }

    }

    private void inicializarComponentes()
    {
        imagePerfilPostagem = findViewById(R.id.imagePerfilPostagem);
        textPerfilPostagem = findViewById(R.id.textPerfilPostagem);
        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
        textQtdCurtidasPostagem = findViewById(R.id.textQtdCurtidasPostagem);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);


        likeButtonFeed = findViewById(R.id.likeButtonFeed);
        visualizarComentario = findViewById(R.id.imageComentarioFeed);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return false;
    }

}
