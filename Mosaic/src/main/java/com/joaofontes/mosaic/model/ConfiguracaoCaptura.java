package com.joaofontes.mosaic.model;

import java.awt.Rectangle;
import java.io.Serializable;

/**
 * VERSÃO CORRIGIDA: Esta versão da classe inclui os métodos baseados em String
 * para garantir a compatibilidade com a classe ConfiguracaoDAO.
 */
public class ConfiguracaoCaptura implements Serializable {
    private static final long serialVersionUID = 4L; // Versionamento

    public enum StorageOption {
        LOCAL_ONLY("Apenas Local"),
        CLOUD_ONLY("Apenas Nuvem"),
        LOCAL_AND_CLOUD("Local e Nuvem");
        private final String displayName;
        StorageOption(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    private int id;
    private Rectangle areaCaptura;
    private String diretorioCaptura;
    private int tempoEntreCapturasMs; 
    private boolean exibicaoAutoHabilitada;
    private int tempoFechamentoAuto; 
    private StorageOption storageOption;
    private int numeroImagensParaMesclar;
    private DirecaoMesclagem direcaoMesclagem;
    private TransformacaoImagem transformacaoPadrao;
    private TransformacaoImagem transformacaoGlobal;
    private String cloudinaryUrl;
    private String cloudName;
    private String cloudApiKey;
    private String cloudApiSecret;

    // Valores padrão
    public ConfiguracaoCaptura() {
        this.tempoEntreCapturasMs = 1000; 
        this.exibicaoAutoHabilitada = true;
        this.tempoFechamentoAuto = 5; 
        this.storageOption = StorageOption.LOCAL_AND_CLOUD; 
        this.diretorioCaptura = System.getProperty("user.home") + "/MosaicCapturas";
        this.numeroImagensParaMesclar = 2;
        this.direcaoMesclagem = DirecaoMesclagem.HORIZONTAL;
        this.transformacaoPadrao = TransformacaoImagem.NENHUMA;
        this.transformacaoGlobal = TransformacaoImagem.NENHUMA;
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Rectangle getAreaCaptura() { return areaCaptura; }
    public void setAreaCaptura(Rectangle areaCaptura) { this.areaCaptura = areaCaptura; }
    public String getDiretorioCaptura() { return diretorioCaptura; }
    public void setDiretorioCaptura(String diretorioCaptura) { this.diretorioCaptura = diretorioCaptura; }
    public int getTempoEntreCapturasMs() { return tempoEntreCapturasMs; }
    public void setTempoEntreCapturasMs(int tempoEntreCapturasMs) { this.tempoEntreCapturasMs = tempoEntreCapturasMs; }
    public boolean isExibicaoAutoHabilitada() { return exibicaoAutoHabilitada; }
    public void setExibicaoAutoHabilitada(boolean exibicaoAutoHabilitada) { this.exibicaoAutoHabilitada = exibicaoAutoHabilitada; }
    public int getTempoFechamentoAuto() { return tempoFechamentoAuto; }
    public void setTempoFechamentoAuto(int tempoFechamentoAuto) { this.tempoFechamentoAuto = tempoFechamentoAuto; }
    public StorageOption getStorageOption() { return storageOption; }
    public void setStorageOption(StorageOption storageOption) { this.storageOption = storageOption; }
    public String getCloudinaryUrl() { return cloudinaryUrl; }
    public void setCloudinaryUrl(String cloudinaryUrl) { this.cloudinaryUrl = cloudinaryUrl; }
    public String getCloudName() { return cloudName; }
    public void setCloudName(String cloudName) { this.cloudName = cloudName; }
    public String getCloudApiKey() { return cloudApiKey; }
    public void setCloudApiKey(String cloudApiKey) { this.cloudApiKey = cloudApiKey; }
    public String getCloudApiSecret() { return cloudApiSecret; }
    public void setCloudApiSecret(String cloudApiSecret) { this.cloudApiSecret = cloudApiSecret; }
    
    // MÉTODOS DE COMPATIBILIDADE RESTAURADOS/ADICIONADOS
    public int getNumeroImagensMesclagem() { return numeroImagensParaMesclar; }
    public void setNumeroImagensMesclagem(int numero) { this.numeroImagensParaMesclar = Math.max(2, numero); }
    
    public DirecaoMesclagem getDirecaoMesclagemEnum() { return direcaoMesclagem; }
    public String getDirecaoMesclagem() { return direcaoMesclagem != null ? direcaoMesclagem.name() : DirecaoMesclagem.HORIZONTAL.name(); }
    public void setDirecaoMesclagem(DirecaoMesclagem direcao) { this.direcaoMesclagem = direcao; }
    public void setDirecaoMesclagem(String direcaoStr) { this.direcaoMesclagem = DirecaoMesclagem.fromString(direcaoStr); }
    
    public TransformacaoImagem getTransformacaoPadraoEnum() { return transformacaoPadrao; }
    public String getTransformacaoPadrao() { return transformacaoPadrao != null ? transformacaoPadrao.toPersistentString() : TransformacaoImagem.NENHUMA.toPersistentString(); }
    public void setTransformacaoPadrao(TransformacaoImagem transformacao) { this.transformacaoPadrao = transformacao; }
    public void setTransformacaoPadrao(String transformacaoStr) { this.transformacaoPadrao = TransformacaoImagem.fromString(transformacaoStr); }

    public TransformacaoImagem getTransformacaoGlobalEnum() { return transformacaoGlobal; }
    public void setTransformacaoGlobal(TransformacaoImagem transformacao) { this.transformacaoGlobal = transformacao; }
    
    public boolean isSalvarLocal() { return storageOption == StorageOption.LOCAL_ONLY || storageOption == StorageOption.LOCAL_AND_CLOUD; }
    public void setSalvarLocal(boolean salvar) {
        if (salvar) { this.storageOption = isSalvarNuvem() ? StorageOption.LOCAL_AND_CLOUD : StorageOption.LOCAL_ONLY; }
        else { this.storageOption = isSalvarNuvem() ? StorageOption.CLOUD_ONLY : StorageOption.LOCAL_ONLY; }
    }

    public boolean isSalvarNuvem() { return storageOption == StorageOption.CLOUD_ONLY || storageOption == StorageOption.LOCAL_AND_CLOUD; }
    public void setSalvarNuvem(boolean salvar) {
        if (salvar) { this.storageOption = isSalvarLocal() ? StorageOption.LOCAL_AND_CLOUD : StorageOption.CLOUD_ONLY; }
        else { this.storageOption = isSalvarLocal() ? StorageOption.LOCAL_ONLY : StorageOption.LOCAL_ONLY; }
    }

    public TransformacaoImagem getTransformacaoParaImagem(int index) { return getTransformacaoPadraoEnum() != null ? getTransformacaoPadraoEnum() : TransformacaoImagem.NENHUMA; }
    public int getNumeroImagensParaMesclar() { return this.numeroImagensParaMesclar; }
    public void setNumeroImagensParaMesclar(int num) { this.numeroImagensParaMesclar = Math.max(2, num); }
}