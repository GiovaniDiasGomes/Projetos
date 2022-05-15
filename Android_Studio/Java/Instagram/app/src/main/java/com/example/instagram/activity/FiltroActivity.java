package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterMiniaturas;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity
{
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private List<ThumbnailItem> listaFiltros;
    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapaterMiniaturas;
    private String idUsuarioLogado;
    private TextInputEditText textDescricaoFiltro;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private Usuario usuarioLogado;
    private AlertDialog dialog;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        // Configurações Iniciais
        listaFiltros = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        // Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);

        // Recuperar dados do usuário logado
        recuperarDadosPostagem();

        // Configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_fechar_preto_24dp);

        // Recupera a imagem escolhida pelo usuário
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);

            // Configurar recycler view de filtros
            adapaterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                    (this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adapaterMiniaturas);

            // Adicionar evento de clique no recycler view
            recyclerFiltros.addOnItemTouchListener(new RecyclerItemClickListener
                    (getApplicationContext(), recyclerFiltros,
                            new RecyclerItemClickListener.OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(View view, int position)
                                {
                                    ThumbnailItem item = listaFiltros.get(position);

                                    imagemFiltro = imagem.copy(imagem.getConfig(), true);
                                    Filter filtro = item.filter;
                                    imageFotoEscolhida.setImageBitmap
                                            (filtro.processFilter(imagemFiltro));
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

            // Recuperar filtros
            recuperarFiltros();


        }


    }

    private void abrirDialogCarregamento(String titulo)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void recuperarDadosPostagem()
    {
        abrirDialogCarregamento("Carregando dados, aguarde!");
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                // Recuperar dados de usuário logado
                usuarioLogado = snapshot.getValue(Usuario.class);

                // Recuperar seguidores
                DatabaseReference seguidoresRef = firebaseRef.child("seguidores")
                        .child(idUsuarioLogado);
                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        seguidoresSnapshot = snapshot;
                        dialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void publicarPostagem()
    {
        abrirDialogCarregamento("Salvando postagem");
        final Postagem postagem = new Postagem();
        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setDescricao(textDescricaoFiltro.getText().toString());

        // Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        // Salvar imagem no firebase storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        final StorageReference imagemRef = storageRef.child("imagens").child("postagens")
                    .child(postagem.getId() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(FiltroActivity.this,
                        "Erro ao salvar imagem, tente novamente!",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                // Recuperar local da foto
                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        // Recuperar local da foto
                        Uri url = task.getResult();
                        postagem.setCaminhoFoto(url.toString());

                        // Atualizar quantidade de postagens
                        int qtdPostagem = usuarioLogado.getPostagens() + 1;
                        usuarioLogado.setPostagens(qtdPostagem);
                        usuarioLogado.atualizarQtdPostagem();

                        // Salvar postagem
                        if (postagem.salvar(seguidoresSnapshot))
                        {
                            Toast.makeText(FiltroActivity.this,
                                    "Sucesso ao salvar postagem!", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            finish();
                        }

                    }
                });


            }
        });

    }

    private void recuperarFiltros()
    {
        // Limpar itens
        listaFiltros.clear();
        ThumbnailsManager.clearThumbs();

        // Configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        // Listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for (Filter filtro : filtros)
        {
            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapaterMiniaturas.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.ic_salvar_postagem:
                publicarPostagem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return false;
    }
}
