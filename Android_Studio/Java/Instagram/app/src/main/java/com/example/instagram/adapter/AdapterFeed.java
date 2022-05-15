package com.example.instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activity.ComentariosActivity;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.example.instagram.model.PostagemCurtida;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder>
{
    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context)
    {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemlista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder(itemlista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        final Feed feed = listaFeed.get(position);
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // Carrega os dados do feed
        final Uri uriFotoUsuario = Uri.parse(feed.getFotoUsuario());
        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());

        Glide.with(context).load(uriFotoUsuario).into(holder.fotoPerfil);
        Glide.with(context).load(uriFotoPostagem).into(holder.fotoPostagem);

        holder.descricao.setText(feed.getDescricao());
        holder.nome.setText(feed.getNomeUsuario());

        // Adicionar evento de clique nos comentários
        holder.visualizarComentario.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, ComentariosActivity.class);
                intent.putExtra("idPostagem", feed.getId());
                context.startActivity(intent);
            }
        });

        // Referência no firebase para curtidas
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens-curtidas").child(feed.getId());
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
                if (snapshot.hasChild(usuarioLogado.getId()))
                {
                    holder.likeButtonFeed.setLiked(true);
                }
                else
                {
                    holder.likeButtonFeed.setLiked(false);
                }

                // Montar objeto postagem curtida
                final PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);
                curtida.setQtdCurtidas(qtdCurtidas);

                // Adicionar eventos para curtir uma foto
                holder.likeButtonFeed.setOnLikeListener(new OnLikeListener()
                {
                    @Override
                    public void liked(LikeButton likeButton)
                    {
                        curtida.salvar();
                        holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
                    }

                    @Override
                    public void unLiked(LikeButton likeButton)
                    {
                        curtida.remover();
                        holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");
                    }
                });

                holder.qtdCurtidas.setText(curtida.getQtdCurtidas() + " curtidas");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas;
        ImageView visualizarComentario, fotoPostagem;
        LikeButton likeButtonFeed;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            fotoPerfil = itemView.findViewById(R.id.imagePerfilPostagem);
            fotoPostagem = itemView.findViewById(R.id.imagePostagemSelecionada);
            nome = itemView.findViewById(R.id.textPerfilPostagem);
            qtdCurtidas = itemView.findViewById(R.id.textQtdCurtidasPostagem);
            descricao = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizarComentario = itemView.findViewById(R.id.imageComentarioFeed);
            likeButtonFeed = itemView.findViewById(R.id.likeButtonFeed);
        }

    }


}
