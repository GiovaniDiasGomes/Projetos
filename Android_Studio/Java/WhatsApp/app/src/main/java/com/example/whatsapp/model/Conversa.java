package com.example.whatsapp.model;

import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa
{
    private String idRemetente;
    private String idDestinatario;
    private String ultimamensagem;
    private Usuario usuarioExibicao;
    private String isGroup;
    private Grupo grupo;

    public Conversa()
    {
        this.setIsGroup("false");
    }

    public String getIdRemetente()
    {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente)
    {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario()
    {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario)
    {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimamensagem()
    {
        return ultimamensagem;
    }

    public void setUltimamensagem(String ultimamensagem)
    {
        this.ultimamensagem = ultimamensagem;
    }

    public Usuario getUsuarioExibicao()
    {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao)
    {
        this.usuarioExibicao = usuarioExibicao;
    }

    public String getIsGroup()
    {
        return isGroup;
    }

    public void setIsGroup(String isGroup)
    {
        this.isGroup = isGroup;
    }

    public Grupo getGrupo()
    {
        return grupo;
    }

    public void setGrupo(Grupo grupo)
    {
        this.grupo = grupo;
    }

    public void salvar()
    {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabse();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef.child(this.getIdRemetente()).child(this.getIdDestinatario()).setValue(this);
    }
}

