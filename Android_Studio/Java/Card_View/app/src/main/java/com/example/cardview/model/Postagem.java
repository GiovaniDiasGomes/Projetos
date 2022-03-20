package com.example.cardview.model;

public class Postagem
{
    private String autor;
    private int imagem;
    private String postagem;

    public Postagem()
    {

    }

    public Postagem(String autor, int imagem, String postagem) {
        this.autor = autor;
        this.imagem = imagem;
        this.postagem = postagem;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public String getPostagem() {
        return postagem;
    }

    public void setPostagem(String postagem) {
        this.postagem = postagem;
    }
}
