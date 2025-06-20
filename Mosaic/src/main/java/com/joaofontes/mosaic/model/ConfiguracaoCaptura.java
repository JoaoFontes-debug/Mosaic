package com.joaofontes.mosaic.model;

import java.awt.Rectangle;
import java.io.Serializable;

public class ConfiguracaoCaptura implements Serializable {

    private static final long serialVersionUID = 6L; // Versionamento

    public enum StorageOption {
        LOCAL_ONLY("Apenas Local"),
        CLOUD_ONLY("Apenas Nuvem"),
        LOCAL_AND_CLOUD("Local e Nuvem");
        private final String displayName;

        StorageOption(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private int id;
    private Rectangle areaCaptura;
    private String diretorioCaptura;
    private int tempoEntreCapturasMs;
    private int numeroImagensParaMesclar;
    private boolean exibicaoAutoHabilitada;
    private int tempoFechamentoAuto;
    private int atrasoReiniciarCaptura;
    private double limiarMudanca; // NOVO CAMPO para sensibilidade
    private StorageOption storageOption;
    private DirecaoMesclagem direcaoMesclagem;
    private TransformacaoImagem transformacaoPadrao;
    private TransformacaoImagem transformacaoGlobal;
    private String cloudinaryUrl;
    private String cloudName;
    private String cloudApiKey;
    private String cloudApiSecret;

    public ConfiguracaoCaptura() {
        this.tempoEntreCapturasMs = 1000;
        this.numeroImagensParaMesclar = 2;
        this.exibicaoAutoHabilitada = true;
        this.tempoFechamentoAuto = 5;
        this.atrasoReiniciarCaptura = 5;
        this.limiarMudanca = 1.5; // Valor padrão de sensibilidade
        this.storageOption = StorageOption.LOCAL_AND_CLOUD;
        this.diretorioCaptura = System.getProperty("user.home") + "/MosaicCapturas";
        this.direcaoMesclagem = DirecaoMesclagem.HORIZONTAL;
        this.transformacaoPadrao = TransformacaoImagem.NENHUMA;
        this.transformacaoGlobal = TransformacaoImagem.NENHUMA;
    }

    // GETTER E SETTER PARA O NOVO CAMPO
    public double getLimiarMudanca() {
        return limiarMudanca;
    }

    public void setLimiarMudanca(double limiar) {
        this.limiarMudanca = limiar;
    }

    // --- Getters e Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rectangle getAreaCaptura() {
        return areaCaptura;
    }

    public void setAreaCaptura(Rectangle areaCaptura) {
        this.areaCaptura = areaCaptura;
    }

    public String getDiretorioCaptura() {
        return diretorioCaptura;
    }

    public void setDiretorioCaptura(String diretorioCaptura) {
        this.diretorioCaptura = diretorioCaptura;
    }

    public int getTempoEntreCapturasMs() {
        return tempoEntreCapturasMs;
    }

    public void setTempoEntreCapturasMs(int tempoEntreCapturasMs) {
        this.tempoEntreCapturasMs = tempoEntreCapturasMs;
    }

    public int getNumeroImagensParaMesclar() {
        return numeroImagensParaMesclar;
    }

    public void setNumeroImagensParaMesclar(int num) {
        this.numeroImagensParaMesclar = Math.max(2, num);
    }

    public boolean isExibicaoAutoHabilitada() {
        return exibicaoAutoHabilitada;
    }

    public void setExibicaoAutoHabilitada(boolean exibicaoAutoHabilitada) {
        this.exibicaoAutoHabilitada = exibicaoAutoHabilitada;
    }

    public int getTempoFechamentoAuto() {
        return tempoFechamentoAuto;
    }

    public void setTempoFechamentoAuto(int tempoFechamentoAuto) {
        this.tempoFechamentoAuto = tempoFechamentoAuto;
    }

    public int getAtrasoReiniciarCaptura() {
        return atrasoReiniciarCaptura;
    }

    public void setAtrasoReiniciarCaptura(int segundos) {
        this.atrasoReiniciarCaptura = Math.max(0, segundos);
    }

    public StorageOption getStorageOption() {
        return storageOption;
    }

    public void setStorageOption(StorageOption storageOption) {
        this.storageOption = storageOption;
    }

    public DirecaoMesclagem getDirecaoMesclagemEnum() {
        return direcaoMesclagem;
    }

    public TransformacaoImagem getTransformacaoPadraoEnum() {
        return transformacaoPadrao;
    }

    public void setTransformacaoPadrao(TransformacaoImagem transformacao) {
        this.transformacaoPadrao = transformacao;
    }

    public TransformacaoImagem getTransformacaoGlobalEnum() {
        return transformacaoGlobal;
    }

    public void setTransformacaoGlobal(TransformacaoImagem transformacao) {
        this.transformacaoGlobal = transformacao;
    }

    public String getCloudinaryUrl() {
        return cloudinaryUrl;
    }

    public void setCloudinaryUrl(String cloudinaryUrl) {
        this.cloudinaryUrl = cloudinaryUrl;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getCloudApiKey() {
        return cloudApiKey;
    }

    public void setCloudApiKey(String cloudApiKey) {
        this.cloudApiKey = cloudApiKey;
    }

    public String getCloudApiSecret() {
        return cloudApiSecret;
    }

    public void setCloudApiSecret(String cloudApiSecret) {
        this.cloudApiSecret = cloudApiSecret;
    }

    public boolean isSalvarLocal() {
        return storageOption == StorageOption.LOCAL_ONLY || storageOption == StorageOption.LOCAL_AND_CLOUD;
    }

    public boolean isSalvarNuvem() {
        return storageOption == StorageOption.CLOUD_ONLY || storageOption == StorageOption.LOCAL_AND_CLOUD;
    }

    public TransformacaoImagem getTransformacaoParaImagem(int index) {
        return getTransformacaoPadraoEnum() != null ? getTransformacaoPadraoEnum() : TransformacaoImagem.NENHUMA;
    }

    // MÉTODOS DE COMPATIBILIDADE PARA O DAO
    public String getDirecaoMesclagem() {
        return direcaoMesclagem != null ? direcaoMesclagem.name() : DirecaoMesclagem.HORIZONTAL.name();
    }

    public String getTransformacaoPadrao() {
        return transformacaoPadrao != null ? transformacaoPadrao.toPersistentString() : TransformacaoImagem.NENHUMA.toPersistentString();
    }

    public void setDirecaoMesclagem(String direcaoStr) {
        this.direcaoMesclagem = DirecaoMesclagem.fromString(direcaoStr);
    }

    public void setTransformacaoPadrao(String transformacaoStr) {
        this.transformacaoPadrao = TransformacaoImagem.fromString(transformacaoStr);
    }

    public void setSalvarLocal(boolean salvar) {
        if (salvar) {
            this.storageOption = isSalvarNuvem() ? StorageOption.LOCAL_AND_CLOUD : StorageOption.LOCAL_ONLY;
        } else {
            this.storageOption = isSalvarNuvem() ? StorageOption.CLOUD_ONLY : StorageOption.LOCAL_ONLY;
        }
    }

    public void setSalvarNuvem(boolean salvar) {
        if (salvar) {
            this.storageOption = isSalvarLocal() ? StorageOption.LOCAL_AND_CLOUD : StorageOption.CLOUD_ONLY;
        } else {
            this.storageOption = isSalvarLocal() ? StorageOption.LOCAL_ONLY : StorageOption.LOCAL_ONLY;
        }
    }
}
