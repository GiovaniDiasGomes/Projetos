package com.example.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.helper.ConfiguracaoFirebase;
import com.example.ifood.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoActivity extends AppCompatActivity
{
    private EditText editCadastroEmail, editCadastroSenha;
    private Switch aSwitchAcesso, aSwitchTipoUsuario;
    private Button buttonAcesso;
    private LinearLayout linearTipoUsuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);


        // Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        // Verificar usuário logado
        verificarUsuarioLogado();

        // Inicializar componentes
        inicializarComponentes();

        aSwitchAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    // Empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }
                else
                {
                    // Usuário
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

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
                                                Toast.makeText(AutenticacaoActivity.this,
                                                        "Cadastro realizado com sucesso",
                                                        Toast.LENGTH_SHORT).show();
                                                String tipoUsuario = getTipoUsuario();
                                                UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                                abrirTelaPrincipal(tipoUsuario);
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
                                                    erroExcecao = "Digite uma senha mais forte";
                                                }
                                                catch (FirebaseAuthInvalidCredentialsException e)
                                                {
                                                    erroExcecao = "Por favor, digite um email válido";
                                                }
                                                catch (FirebaseAuthUserCollisionException e)
                                                {
                                                    erroExcecao = "Esta conta já foi cadastrada";
                                                }
                                                catch (Exception e)
                                                {
                                                    erroExcecao = "ao cadastrar usuário" +
                                                            e.getMessage();
                                                    e.printStackTrace();
                                                }

                                                Toast.makeText(AutenticacaoActivity.this,
                                                        "Erro: " + erroExcecao,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            // Login no app
                            autenticacao.signInWithEmailAndPassword(email, senha)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(AutenticacaoActivity.this,
                                                        "Logado com sucesso",
                                                        Toast.LENGTH_SHORT).show();
                                                String tipoUsuario = task.getResult().getUser()
                                                        .getDisplayName();
                                                abrirTelaPrincipal(tipoUsuario);
                                            }
                                            else
                                            {
                                                Toast.makeText(AutenticacaoActivity.this,
                                                        "Erro ao fazer login" +
                                                                task.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }
                    }
                    else
                    {
                        Toast.makeText(AutenticacaoActivity.this, "Preencha a senha",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(AutenticacaoActivity.this, "Preencha o e-mail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void verificarUsuarioLogado()
    {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null)
        {
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario()
    {
        return aSwitchTipoUsuario.isChecked() ? "E" : "U";
    }

    private void abrirTelaPrincipal(String tipoUsuario)
    {
        if (tipoUsuario.equals("E"))
        {
            // Empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }
        else
        {
            // Usuário
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    private void inicializarComponentes()
    {
        editCadastroEmail = findViewById(R.id.editCadastroEmail);
        editCadastroSenha = findViewById(R.id.editCadastroSenha);
        aSwitchAcesso = findViewById(R.id.switchAcesso);
        aSwitchTipoUsuario = findViewById(R.id.switchTipoUsuario);
        buttonAcesso = findViewById(R.id.buttonAcesso);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }

}
