package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateCustom;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity
{
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        // Preenche o campo data com a data atual
        campoData.setText(DateCustom.dataAtual());

        recuperarReceitaTotal();
    }

    public void salvarReceita(View view)
    {
        if (validarCampoReceita())
        {
            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("r");

            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);

            movimentacao.salvar(data);

            finish();
        }

    }

    public Boolean validarCampoReceita()
    {
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();
        String textoValor = campoValor.getText().toString();

        // Validar se os campos foram preenchidos
        if (textoValor.isEmpty())
        {
            Toast.makeText(ReceitasActivity.this,"Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (textoData.isEmpty())
        {
            Toast.makeText(ReceitasActivity.this,"Data não foi preenchida corretamente!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (textoCategoria.isEmpty())
        {
            Toast.makeText(ReceitasActivity.this,"Categoria não foi preenchida!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (textoDescricao.isEmpty())
        {
            Toast.makeText(ReceitasActivity.this,"Descrição não foi preenchida!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    public void recuperarReceitaTotal()
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    public void atualizarReceita(Double receita)
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);
    }
}
