package com.example.cardview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardview.R;
import com.example.cardview.model.Postagem;

import java.util.List;

public class PostagemAdapter extends RecyclerView.Adapter<PostagemAdapter.MyViewHolder>
{
    private List<Postagem> postagems;

    public PostagemAdapter(List<Postagem> listaPostagens)
    {
        this.postagems = listaPostagens;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postagem_detalhe, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        Postagem postagem = postagems.get(position);
        holder.textoAutor.setText(postagem.getAutor());
        holder.imagemPostagem.setImageResource(postagem.getImagem());
        holder.textoAutor.setText(postagem.getPostagem());
    }

    @Override
    public int getItemCount()
    {
        return postagems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView textoAutor;
        private ImageView imagemPostagem;
        private TextView textoPostagem;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textoAutor = itemView.findViewById(R.id.textoAutor);
            imagemPostagem = itemView.findViewById(R.id.imagePostagem);
            textoPostagem = itemView.findViewById(R.id.textoPostagem);
        }
    }
}
