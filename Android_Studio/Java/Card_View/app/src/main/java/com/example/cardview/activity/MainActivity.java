package com.example.cardview.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.cardview.R;
import com.example.cardview.adapter.PostagemAdapter;
import com.example.cardview.model.Postagem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView recyclerPostagem;
    private List<Postagem> postagens;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postagens = new ArrayList<>();

        recyclerPostagem = findViewById(R.id.recyclerView);

        // Define layout
        //RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        //recyclerPostagem.setLayoutManager(layoutManager);

        //LinearLayoutManager layoutManager= new LinearLayoutManager(this); // Habilitar layout lateral
        //layoutManager.setOrientation(LinearLayout.HORIZONTAL); // Rola pro lado

        RecyclerView.LayoutManager layoutManager= new GridLayoutManager(this, 2);
        recyclerPostagem.setLayoutManager(layoutManager);

        // Define Adapter
        this.prepararPostagens();
        PostagemAdapter adapter = new PostagemAdapter(postagens);
        recyclerPostagem.setAdapter(adapter);

    }

    public void prepararPostagens()
    {
        Postagem postagem = new Postagem("Giovani Dias Gomes", R.drawable.imagem1, "#tbt Viagem legal");
        this.postagens.add(postagem);

        postagem = new Postagem("Hotel JM", R.drawable.imagem2, "Viaje, aproveite nossos descontos!");
        this.postagens.add(postagem);

        postagem = new Postagem("Maria Luiza", R.drawable.imagem3, "#Paris");
        this.postagens.add(postagem);

        postagem = new Postagem("Marcos Paulo", R.drawable.imagem4, "Que foto linda");
        this.postagens.add(postagem);
    }
}
