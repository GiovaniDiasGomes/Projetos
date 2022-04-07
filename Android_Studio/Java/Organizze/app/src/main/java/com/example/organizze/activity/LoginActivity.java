package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity
{
    private EditText editEmail, editSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        botaoEntrar = findViewById(R.id.botaoEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String textoEmail = editEmail.getText().toString();
                String textoSenha = editSenha.getText().toString();

                // Validar se os campos foram preenchidos
                if (textoEmail.isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Preencha o email!",
                            Toast.LENGTH_SHORT).show();
                }
                else if (textoSenha.isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Preencha a senha!",
                        Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(LoginActivity.this,"Ok",
                    //        Toast.LENGTH_SHORT).show();
                    usuario = new Usuario();
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    validarLogin();
                }
            }
        });
    }

    public void validarLogin()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    abrirTelaPrincipal();
                }
                else
                {
                    String excecao;
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e)
                    {
                        excecao = "Usuário não está cadastrado!";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        excecao = "Email e senha não correspondem a um usuário cadastrado!";
                    }
                    catch (Exception e)
                    {
                        excecao = "Erro ao cadastrar usuário" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirTelaPrincipal()
    {
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }

}
