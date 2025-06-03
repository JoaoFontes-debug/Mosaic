
package com.joaofontes.mosaic.model;

import java.util.Date;

/**
 *
 * @author aluno.lauro
 */
public class SessaoCaptura {
    
    private String nomePeca;
    private String descricao;
    private Date dataCaptura;
    private String caminhoImagem;

    // Getters e Setters
    public String getNomePeca() { return nomePeca; }
    public void setNomePeca(String nomePeca) { this.nomePeca = nomePeca; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public Date getDataCaptura() { return dataCaptura; }
    public void setDataCaptura(Date dataCaptura) { this.dataCaptura = dataCaptura; }
    
    public String getCaminhoImagem() { return caminhoImagem; }
    public void setCaminhoImagem(String caminhoImagem) { this.caminhoImagem = caminhoImagem; }
}
