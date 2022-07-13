package com.example.youtube.olx.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.youtube.olx.R;
import com.example.youtube.olx.helper.ConfiguracaoFirebase;
import com.example.youtube.olx.helper.Permissoes;
import com.example.youtube.olx.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageView imageCadstro1, imageCadstro2, imageCadstro3;
    private Spinner spinnerEstado, spinnerCategoria;
    private EditText editTitulo, editDescricao;
    private CurrencyEditText editValor;
    private MaskEditText editTelefone;
    private Anuncio anuncio;
    private StorageReference storage;
    private AlertDialog dialog;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        // Configurações iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        // Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        // Inicializar componentes
        inicializarcomponentes();
        carregarDadosSpinner();

    }

    public void validarDadosAnuncio(View view)
    {
        anuncio = configurarAnuncio();
        String valor = String.valueOf(editValor.getRawValue());

        if(listaFotosRecuperadas.size() != 0)
        {
            if( !anuncio.getEstado().isEmpty())
            {
                if( !anuncio.getCategoria().isEmpty())
                {
                    if( !anuncio.getTitulo().isEmpty())
                    {
                        if( !valor.isEmpty() && !valor.equals("0"))
                        {
                            if( !anuncio.getTelefone().isEmpty()) // && anuncio.getTelefone().length() >= 11) // Com problema... Trocar dependência
                            {
                                if( !anuncio.getDescricao().isEmpty())
                                {
                                    salvarAnuncio();
                                }
                                else
                                {
                                    exibirMensagemErro("Preencha o campo descrição!");
                                }
                            }
                            else
                            {
                                exibirMensagemErro("Preencha o campo telefone!");
                            }
                        }
                        else
                        {
                            exibirMensagemErro("Preencha o campo valor!");
                        }
                    }
                    else
                    {
                        exibirMensagemErro("Preencha o campo título!");
                    }
                }
                else
                {
                    exibirMensagemErro("Preencha o campo categoria!");
                }
            }
            else
            {
                exibirMensagemErro("Preencha o campo estado!");
            }
        }
        else
        {
            exibirMensagemErro("Selecione ao menos uma foto!");
        }
    }

    private void exibirMensagemErro(String mensagem)
    {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void salvarAnuncio()
    {
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Salvando Anúncio")
                .setCancelable(false).build();
        dialog.show();

        // Salvar imagem no Storage
        for(int i=0; i < listaFotosRecuperadas.size(); i++)
        {
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }

    }

    private void salvarFotoStorage(String urlString, final int totalFotos, int contador)
    {
        // Criar nó no storage
        final StorageReference imagemAnuncio = storage.child("imagens").child("anuncios")
                .child(anuncio.getIdAnuncio()).child("imagem" + contador);

        // Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        Uri url = task.getResult();
                        String urlConvertida = url.toString();

                        listaURLFotos.add(urlConvertida);
                        if(totalFotos == listaURLFotos.size())
                        {
                            anuncio.setFotos(listaURLFotos);
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                exibirMensagemErro("Falha ao fazer upload!");
            }
        });

    }

    private Anuncio configurarAnuncio()
    {
        String estado = spinnerEstado.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String titulo = editTitulo.getText().toString();
        String valor = editValor.getText().toString();
        String telefone = editTelefone.getText().toString();
        String descricao = editDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;

    }

    private void alertaValidacaoPermissao()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o APP é necessario aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            // Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            // Configurar imagem no ImageView
            if(requestCode == 1)
            {
                imageCadstro1.setImageURI(imagemSelecionada);
            }
            else if (requestCode == 2)
            {
                imageCadstro2.setImageURI(imagemSelecionada);
            }
            else if(requestCode == 3)
            {
                imageCadstro3.setImageURI(imagemSelecionada);
            }

            listaFotosRecuperadas.add(caminhoImagem);

        }
    }

    private void carregarDadosSpinner()
    {
        // Configurar spinners de estado
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstados);

        // Configurar spinners de categoria
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategorias);

    }

    private void inicializarcomponentes()
    {
        imageCadstro1 = findViewById(R.id.imageCadastro1);
        imageCadstro2 = findViewById(R.id.imageCadastro2);
        imageCadstro3 = findViewById(R.id.imageCadastro3);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editTitulo = findViewById(R.id.editTitulo);
        editValor = findViewById(R.id.editValor);
        editTelefone = findViewById(R.id.editTelefone);
        editDescricao = findViewById(R.id.editDescricao);
        imageCadstro1.setOnClickListener(this);
        imageCadstro2.setOnClickListener(this);
        imageCadstro3.setOnClickListener(this);

        // Configurar localidade para pt -> portugues BR -> Brasil
        Locale locale = new Locale("pt", "BR");
        editValor.setLocale(locale);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults)
        {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED)
            {
                alertaValidacaoPermissao();
            }
        }
    }
}
