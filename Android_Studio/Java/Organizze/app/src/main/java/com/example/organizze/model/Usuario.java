package com.example.organizze.model;

import android.widget.Toast;

import com.example.organizze.activity.CadastroActivity;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.logging.StreamHandler;

public class Usuario
{
    private String nome;
    private String email;
    private String senha;
    private String idUsuario;
    private Double receitaTotal = 0.00;
    private Double despesaTotal = 0.00;

    public Usuario()
    {

    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Exclude
    public String getSenha()
    {
        return senha;
    }

    public void setSenha(String senha)
    {
        this.senha = senha;
    }

    @Exclude
    public String getIdUsuario()
    {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario)
    {
        this.idUsuario = idUsuario;
    }

    public Double getReceitaTotal()
    {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal)
    {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal()
    {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal)
    {
        this.despesaTotal = despesaTotal;
    }

    public void salvar()
    {
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios").child(this.idUsuario).setValue(this);
    }
}
