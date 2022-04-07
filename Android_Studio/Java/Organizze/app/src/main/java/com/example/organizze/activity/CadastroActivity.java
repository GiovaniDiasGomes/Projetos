package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity
{
    private EditText editNome, editEmail, editSenha;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastro");

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String textoNome = editNome.getText().toString();
                String textoEmail = editEmail.getText().toString();
                String textoSenha = editSenha.getText().toString();

                // Validar se os campos foram preenchidos
                if (textoNome.isEmpty())
                {
                    Toast.makeText(CadastroActivity.this,"Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
                else if (textoEmail.isEmpty())
                {
                    Toast.makeText(CadastroActivity.this,"Preencha o email!",
                            Toast.LENGTH_SHORT).show();
                }
                else if (textoSenha.isEmpty())
                {
                    Toast.makeText(CadastroActivity.this,"Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    cadastrarUsuario();
                }
            }
        });
    }

    public void cadastrarUsuario()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();
                }
                else
                {
                    String excecao;
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e)
                    {
                        excecao = "Digite uma senha mais forte!";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        excecao = "Por favor, digite um email válido!";
                    }
                    catch (FirebaseAuthUserCollisionException e)
                    {
                        excecao = "Esta conta já foi cadastrada!";
                    }
                    catch (Exception e)
                    {
                        excecao = "Erro ao cadastrar usuário" + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
