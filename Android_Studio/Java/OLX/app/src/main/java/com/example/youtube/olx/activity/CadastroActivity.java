package com.example.youtube.olx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.youtube.olx.R;
import com.example.youtube.olx.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity
{
    private EditText editCadastroEmail, editCadastroSenha;
    private Switch aSwitchAcesso;
    private Button buttonAcesso;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Inicializar componentes
        inicializarComponentes();

        // Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        buttonAcesso.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = editCadastroEmail.getText().toString();
                String senha = editCadastroSenha.getText().toString();

                if(!email.isEmpty())
                {
                    if(!senha.isEmpty())
                    {
                        // Verifica o estado do switch
                        if(aSwitchAcesso.isChecked())
                        {
                            // Cadastro do usuário
                            autenticacao.createUserWithEmailAndPassword(email, senha)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(CadastroActivity.this,
                                                "Cadastro reallizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();

                                        // Direcionar para a tela principal do App

                                    }
                                    else
                                    {
                                        String erroExcecao = "";
                                        try
                                        {
                                            throw task.getException();
                                        }
                                        catch (FirebaseAuthWeakPasswordException e)
                                        {
                                            erroExcecao = "Digite uma senha mais forte!";
                                        }
                                        catch (FirebaseAuthInvalidCredentialsException e)
                                        {
                                            erroExcecao = "Por favor, digite um e-mail válido!";
                                        }
                                        catch (FirebaseAuthUserCollisionException e)
                                        {
                                            erroExcecao = "Esta conta já foi cadastrada!";
                                        }
                                        catch (Exception e)
                                        {
                                            erroExcecao = "ao cadastra usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(CadastroActivity.this,
                                                "Erro: " + erroExcecao,
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                        else
                        {
                            // Login do usuário
                            autenticacao.signInWithEmailAndPassword(email, senha)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                    {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(CadastroActivity.this,
                                                "Logado com sucesso", Toast.LENGTH_SHORT)
                                                .show();
                                        startActivity(new Intent(getApplicationContext(),
                                                AnunciosActivity.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(CadastroActivity.this,
                                                "Erro ao fazer login: " + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(CadastroActivity.this, "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else 
                {
                    Toast.makeText(CadastroActivity.this, "Preencha o e-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inicializarComponentes()
    {
        editCadastroEmail = findViewById(R.id.editCadastroEmail);
        editCadastroSenha = findViewById(R.id.editCadastroSenha);
        aSwitchAcesso = findViewById(R.id.switchAcesso);
        buttonAcesso = findViewById(R.id.buttonAcesso);
    }

}
