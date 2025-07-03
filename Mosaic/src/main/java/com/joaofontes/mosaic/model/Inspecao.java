package com.joaofontes.mosaic.model;

/**
 *
 * @author JoãoFontes
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Inspecao {

    private int id;
    private String nomePeca;
    private String descricao;
    private String nomeOperador; // NOVO CAMPO
    private Date dataCriacao;
    private List<Mesclagem> mesclagens;

    public Inspecao() {
        this.mesclagens = new ArrayList<>();
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

    public String getNomeOperador() {
        return nomeOperador;
    }

    public void setNomeOperador(String nomeOperador) {
        this.nomeOperador = nomeOperador;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<Mesclagem> getMesclagens() {
        return mesclagens;
    }

    public void setMesclagens(List<Mesclagem> mesclagens) {
        this.mesclagens = mesclagens;
    }

    public void addMesclagem(Mesclagem mesclagem) {
        this.mesclagens.add(mesclagem);
    }
}
