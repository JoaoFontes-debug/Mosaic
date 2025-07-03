package com.joaofontes.mosaic.model;

/**
 *
 * @author JoãoFontes
 */
import java.util.Date;


public class Mesclagem {

    private int id;
    private int idInspecao; // Foreign key to Inspecao
    private Date dataCaptura;
    private String caminhoImagem; // Cloud URL
    private String caminhoLocal;  // Local file path

    public Mesclagem() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdInspecao() {
        return idInspecao;
    }

    public void setIdInspecao(int idInspecao) {
        this.idInspecao = idInspecao;
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
