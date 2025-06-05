package com.joaofontes.mosaic.model;

import java.util.Date;

public class SessaoCaptura {

    private int id;
    private String nomePeca;
    private String descricao;
    private Date dataCaptura;
    private String caminhoImagem;
    private String caminhoLocal;

    public SessaoCaptura() {
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomePeca() {
        return nomePeca;
    }

    public void setNomePeca(String nomePeca) {
        this.nomePeca = nomePeca;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataCaptura() {
        return dataCaptura;
    }

    public void setDataCaptura(Date dataCaptura) {
        this.dataCaptura = dataCaptura;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    public String getCaminhoLocal() {
        return caminhoLocal;
    }

    public void setCaminhoLocal(String caminhoLocal) {
        this.caminhoLocal = caminhoLocal;
    }
}
