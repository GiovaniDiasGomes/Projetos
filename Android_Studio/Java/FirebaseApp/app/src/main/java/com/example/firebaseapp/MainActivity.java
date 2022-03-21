package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    //private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    //private FirebaseAuth usuario = FirebaseAuth.getInstance();
    private ImageView imagePhoto;
    private Button buttonUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagePhoto = findViewById(R.id.imagePhoto);
        buttonUpload = findViewById(R.id.buttonUpload);

        buttonUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Configura para imagem ser salva em memória
                imagePhoto.setDrawingCacheEnabled(true);
                imagePhoto.buildDrawingCache();

                // Recupera o bitmap da imagem (imagem a ser carregada)
                Bitmap bitmap = imagePhoto.getDrawingCache();

                // Comprimir bitmap para um formato png/jpeg
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);

                // Converte o baos para pixel brutos em uma matriz de bytes
                // (dados da imagem)
                byte[] dadosImagem = baos.toByteArray();

                // Define nós para o storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference imagens = storageReference.child("imagens");
                final StorageReference imagemRef = imagens.child("Celular.jpeg");


                //DEU CERTO NA VERSÃO 0.6.0
                /*
                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(MainActivity.this).load(uri).into(imagePhoto);
                        Toast.makeText(MainActivity.this,"Sucesso ao alterar.", Toast.LENGTH_SHORT).show();
                    }
                });
                */

                //DEU CERTO NA VERSÃO 4.3.1
                Glide.with(MainActivity.this).load(imagemRef).into(imagePhoto);


                // Deletar um arquivo
                /*
                imagemRef.delete().addOnFailureListener(MainActivity.this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MainActivity.this, "Erro ao deletar ",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(MainActivity.this, "Sucesso ao deletar ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                */



                // Nome da imagem
                //String nomeArquivo = UUID.randomUUID().toString();
                //final StorageReference imagemRef = imagens.child(nomeArquivo + ".jpeg");
                //final StorageReference imagemRef = imagens.child("Padrao.jpeg");

                // Retorna o objeto que irá controlar o upload
                /*
                UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                uploadTask.addOnFailureListener(MainActivity.this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(MainActivity.this, "Upload falhou" +
                                e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task)
                            {
                                Uri url = task.getResult();
                                Toast.makeText(MainActivity.this, "Sucesso ao fazer upload",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });*/
            }
        });

        //DatabaseReference usuarios = referencia.child("usuarios");

        //DatabaseReference usuarioPesquisa = usuarios.child("-MyE4_97vxT30lR4UsMt");


        // Aplicar filtros
        //Query usuarioPesquisa = usuarios.orderByChild("nome").equalTo("Jamilton");
        //Query usuarioPesquisa = usuarios.orderByKey().limitToFirst(3);
        //Query usuarioPesquisa = usuarios.orderByKey().limitToLast(2);

        // Aplicar filtros maior ou igual
        //Query usuarioPesquisa = usuarios.orderByChild("idade").startAt(35); //maior ou igual
        //Query usuarioPesquisa = usuarios.orderByChild("idade").endAt(22); //menor ou igual
        //Query usuarioPesquisa = usuarios.orderByChild("idade").startAt(18).endAt(30); //entre dois valores
        //Query usuarioPesquisa = usuarios.orderByChild("nome").startAt("J").endAt("J" + "\uf8ff"); //entre duas vletras

        /*usuarioPesquisa.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.i("Dados usuario: ", dataSnapshot.getValue().toString());
                //Usuario dadosUsuario = dataSnapshot.getValue(Usuario.class);
                //Log.i("Dados usuario: ", "nome: " + dadosUsuario.getNome() +
                //        " sobrenome: " + dadosUsuario.getSobrenome() +
                //        " idade: " + dadosUsuario.getIdade());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
        */


        //referencia.child("pontos").setValue("200");
        //referencia.child("usuarios").child("003").child("nome").setValue("Giovani");
        //referencia.child("usuarios").child("003").child("sobrenome").setValue("Gomes");
        //referencia.child("usuarios");


        // Gerar identificador unico
        //DatabaseReference usuarios = referencia.child("usuarios");

        //Usuario usuario = new Usuario();
        //usuario.setNome("Rodrigo");
        //usuario.setSobrenome("Matos");
        //usuario.setIdade(35);

        //usuarios.push().setValue(usuario);

        // Deslogar usuario
        //usuario.signOut();



        // Logar usuario
        /*usuario.signInWithEmailAndPassword("jamilton2@gmail.com", "ja12345")
        //.addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Log.i("SignIn", "Sucesso ao logar usuario!");
                }
                else
                {
                    Log.i("SignIn", "Erro ao logar usuario!");
                }
            }
        });
        */


        // Verifica usuario logado
        /*
        if(usuario.getCurrentUser() != null)
        {
            Log.i("CreateUser", "Usuario logado!");
        }
        else
        {
            Log.i("CreateUser", "Usuario não está logado!");
        }



        // Cadastro de usuário
        usuario.createUserWithEmailAndPassword("jamilton2@gmail.com", "ja12345")
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Log.i("CreateUser", "Sucesso ao cadastrar usuario!");
                        }
                        else
                        {
                            Log.i("CreateUser", "Erro ao cadastrar usuario!");
                        }
                    }
                });
                */


        // Instancia Database do Firebase conforme suas classes
        /*
        DatabaseReference usuarios = referencia.child("usuarios");
        DatabaseReference produtos = referencia.child("produtos");

        // Recuperar dados
        //DatabaseReference usuarios = referencia.child("usuarios").child("001");
        usuarios.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.i("FIREBASE", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        // Salvar dados
        //Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setSobrenome("Silva");
        usuario.setIdade(45);

        usuarios.child("002").setValue(usuario);

        // Salvar dados e atualizar dados
        Produto produto = new Produto();
        produto.setDescricao("IPhone");
        produto.setMarca("Apple");
        produto.setPreco(8500.95);

        produtos.child("001").setValue(produto);

        Produto produto1 = new Produto();
        produto1.setDescricao("Galaxy");
        produto1.setMarca("Samsung");
        produto1.setPreco(5000.99);

        produtos.child("002").setValue(produto1);

        Produto produto2 = new Produto();
        produto2.setDescricao("Zenfone");
        produto2.setMarca("Motorola");
        produto2.setPreco(3500.50);

        produtos.child("003").setValue(produto2);
        */
    }
}

