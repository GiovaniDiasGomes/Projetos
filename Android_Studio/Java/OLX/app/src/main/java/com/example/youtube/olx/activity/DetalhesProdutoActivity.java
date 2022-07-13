package com.example.youtube.olx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youtube.olx.R;
import com.example.youtube.olx.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetalhesProdutoActivity extends AppCompatActivity
{
    private CarouselView carouselView;
    private TextView textTituloDetalhe, textDescricaoDetalhe, textEstadoDetalhe, textPrecoDetalhe;
    private Anuncio anuncioSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        // Configurar toolbar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_seta_voltar_branco_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhe do produto");

        // Inicializar componentes
        inicializarComponentes();

        // Recuperar anúncio para exibição
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");

        if(anuncioSelecionado != null)
        {
            textTituloDetalhe.setText(anuncioSelecionado.getTitulo());
            textDescricaoDetalhe.setText(anuncioSelecionado.getDescricao());
            textEstadoDetalhe.setText(anuncioSelecionado.getEstado());
            textPrecoDetalhe.setText(anuncioSelecionado.getValor());

            ImageListener imageListener = new ImageListener()
            {
                @Override
                public void setImageForPosition(int position, ImageView imageView)
                {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

        }

    }

    public void visualizarTelefone(View view)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity(intent);
    }

    private void inicializarComponentes()
    {
        carouselView = findViewById(R.id.carouselView);
        textTituloDetalhe = findViewById(R.id.textTituloDetalhe);
        textDescricaoDetalhe = findViewById(R.id.textDescricaoDetalhe);
        textEstadoDetalhe = findViewById(R.id.textEstadoDetalhe);
        textPrecoDetalhe = findViewById(R.id.textPrecoDetalhe);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return false;
    }
}
