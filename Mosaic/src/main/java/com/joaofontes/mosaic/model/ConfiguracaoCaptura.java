
package com.joaofontes.mosaic.model;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aluno.lauro
 */
public class ConfiguracaoCaptura {
    
     public enum DirecaoMesclagem { HORIZONTAL, VERTICAL }
    public enum TransformacaoImagem { 
        NENHUMA, 
        ESPELHAR_HORIZONTAL, 
        ESPELHAR_VERTICAL, 
        ROTACIONAR_90, 
        ROTACIONAR_180 
    }
    
    private Rectangle areaCaptura;
    private String diretorioCaptura;
    private int tempoFechamentoAuto = 5;
    private boolean exibicaoAutoHabilitada = true;
    private int numeroImagensMesclagem = 5;
    private DirecaoMesclagem direcaoMesclagem = DirecaoMesclagem.HORIZONTAL;
    private TransformacaoImagem transformacaoPadrao = TransformacaoImagem.NENHUMA;
    private Map<Integer, TransformacaoImagem> transformacoes = new HashMap<>();
    private int intervaloCaptura = 1000; // em milissegundos
    
    // Getters e Setters
    public Rectangle getAreaCaptura() { return areaCaptura; }
    public void setAreaCaptura(Rectangle areaCaptura) { this.areaCaptura = areaCaptura; }
    
    public String getDiretorioCaptura() { return diretorioCaptura; }
    public void setDiretorioCaptura(String diretorioCaptura) { this.diretorioCaptura = diretorioCaptura; }
    
    public int getTempoFechamentoAuto() { return tempoFechamentoAuto; }
    public void setTempoFechamentoAuto(int tempoFechamentoAuto) { this.tempoFechamentoAuto = tempoFechamentoAuto; }
    
    public boolean isExibicaoAutoHabilitada() { return exibicaoAutoHabilitada; }
    public void setExibicaoAutoHabilitada(boolean exibicaoAutoHabilitada) { this.exibicaoAutoHabilitada = exibicaoAutoHabilitada; }
    
    public int getNumeroImagensMesclagem() { return numeroImagensMesclagem; }
    public void setNumeroImagensMesclagem(int numeroImagensMesclagem) { this.numeroImagensMesclagem = numeroImagensMesclagem; }
    
    public DirecaoMesclagem getDirecaoMesclagem() { return direcaoMesclagem; }
    public void setDirecaoMesclagem(DirecaoMesclagem direcaoMesclagem) { this.direcaoMesclagem = direcaoMesclagem; }
    
    public TransformacaoImagem getTransformacaoPadrao() { return transformacaoPadrao; }
    public void setTransformacaoPadrao(TransformacaoImagem transformacaoPadrao) { this.transformacaoPadrao = transformacaoPadrao; }
    
    public int getIntervaloCaptura() { return intervaloCaptura; }
    public void setIntervaloCaptura(int intervaloCaptura) { this.intervaloCaptura = intervaloCaptura; }
    
    public TransformacaoImagem getTransformacaoParaImagem(int index) {
        return transformacoes.getOrDefault(index, transformacaoPadrao);
    }
    
    public void setTransformacaoParaImagem(int index, TransformacaoImagem transformacao) {
        transformacoes.put(index, transformacao);
    }
}
