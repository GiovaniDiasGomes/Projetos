package com.example.ifood.model;

import com.example.ifood.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Empresa implements Serializable
{
    private String idEmpresa;
    private String urlImagem;
    private String nome;
    private String categoria;
    private String tempo;
    private Double precoEntrega;

    public Empresa()
    {

    }

    public void salvar()
    {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas").child(getIdEmpresa());
        empresaRef.setValue(this);

    }

    public String getIdEmpresa()
    {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa)
    {
        this.idEmpresa = idEmpresa;
    }

    public String getUrlImagem()
    {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem)
    {
        this.urlImagem = urlImagem;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getCategoria()
    {
        return categoria;
    }

    public void setCategoria(String categoria)
    {
        this.categoria = categoria;
    }

    public String getTempo()
    {
        return tempo;
    }

    public void setTempo(String tempo)
    {
        this.tempo = tempo;
    }

    public Double getPrecoEntrega()
    {
        return precoEntrega;
    }

    public void setPrecoEntrega(Double precoEntrega)
    {
        this.precoEntrega = precoEntrega;
    }

}
