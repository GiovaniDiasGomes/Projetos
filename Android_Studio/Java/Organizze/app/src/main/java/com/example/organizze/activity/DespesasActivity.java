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

public class DespesasActivity extends AppCompatActivity
{
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        // Preenche o campo data com a data atual
        campoData.setText(DateCustom.dataAtual());

        recuperarDespesaTotal();
    }

    public void salvarDespesa(View view)
    {
        if (validarCampoDespesa())
        {
            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("d");

            Double despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa(despesaAtualizada);

            movimentacao.salvar(data);

            finish();
        }

    }

    public Boolean validarCampoDespesa()
    {
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();
        String textoValor = campoValor.getText().toString();

        // Validar se os campos foram preenchidos
        if (textoValor.isEmpty())
        {
            Toast.makeText(DespesasActivity.this,"Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (textoData.isEmpty())
        {
            Toast.makeText(DespesasActivity.this,"Data não foi preenchida corretamente!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (textoCategoria.isEmpty())
        {
            Toast.makeText(DespesasActivity.this,"Categoria não foi preenchida!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (textoDescricao.isEmpty())
        {
            Toast.makeText(DespesasActivity.this,"Descrição não foi preenchida!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    public void recuperarDespesaTotal()
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
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    public void atualizarDespesa(Double despesa)
    {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);
    }


}
