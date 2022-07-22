package com.example.ifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ifood.R;
import com.example.ifood.helper.UsuarioFirebase;
import com.example.ifood.model.Empresa;
import com.example.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

public class NovoProdutoEmpresaActivity extends AppCompatActivity
{
    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private Button buttonSalvarProduto;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        // Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // Inicializar componentes
        inicializarComponentes();

        // Configurações toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void validarDadosProduto(View view)
    {
        // Validar se os campos foram preenchidos
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if(!nome.isEmpty())
        {
            if(!descricao.isEmpty())
            {
                if(!preco.isEmpty())
                {
                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();
                    finish();
                    exibirMensagem("Produto salvo com sucesso!");

                }
                else
                {
                    exibirMensagem("Digite um preço para o produto");
                }
            }
            else
            {
                exibirMensagem("Digite uma descrição para o produto");
            }
        }
        else
        {
            exibirMensagem("Digite um nome para o produto");
        }

    }

    private void exibirMensagem(String texto)
    {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes()
    {
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
        buttonSalvarProduto = findViewById(R.id.buttonSalvarProduto);
    }

}
