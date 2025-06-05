// Arquivo: com/joaofontes/mosaic/model/ConfiguracaoCaptura.java
package com.joaofontes.mosaic.model;

import java.awt.Rectangle;
import java.io.Serializable;

public class ConfiguracaoCaptura implements Serializable {
    private static final long serialVersionUID = 2L; // Incrementado devido a novas serializações

    // Enum para opções de armazenamento
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

    private int id; // Para compatibilidade com ConfiguracaoDAO
    private Rectangle areaCaptura;
    private String diretorioCaptura;
    private int tempoEntreCapturasMs; 
    private boolean exibicaoAutoHabilitada;
    private int tempoFechamentoAuto; 
    private StorageOption storageOption;
    
    // Campos relacionados à mesclagem (para compatibilidade com ConfiguracaoDAO)
    private int numeroImagensMesclagem;
    private DirecaoMesclagem direcaoMesclagem;
    private TransformacaoImagem transformacaoPadrao; // Usado para cada imagem individualmente antes da mesclagem
    private TransformacaoImagem transformacaoGlobal; // Usado para a imagem final mesclada

    // Campos Cloudinary (para compatibilidade com ConfiguracaoDAO e uso direto)
    private String cloudinaryUrl; // URL completa (preferencial para a API Cloudinary)
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
        this.numeroImagensMesclagem = 2; // Valor padrão exemplo
        this.direcaoMesclagem = DirecaoMesclagem.HORIZONTAL; // Valor padrão exemplo
        this.transformacaoPadrao = TransformacaoImagem.NENHUMA; // Valor padrão exemplo
        this.transformacaoGlobal = TransformacaoImagem.NENHUMA;
    }

    // Getters e Setters
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

    // Getters e Setters para campos de mesclagem
    public int getNumeroImagensMesclagem() { return numeroImagensMesclagem; }
    public void setNumeroImagensMesclagem(int numeroImagensMesclagem) { this.numeroImagensMesclagem = numeroImagensMesclagem; }

    public DirecaoMesclagem getDirecaoMesclagemEnum() { return direcaoMesclagem; }
    public String getDirecaoMesclagem() { // Para compatibilidade com DAO que espera String
        return direcaoMesclagem != null ? direcaoMesclagem.name() : null;
    }
    public void setDirecaoMesclagem(DirecaoMesclagem direcaoMesclagem) { this.direcaoMesclagem = direcaoMesclagem; }
    public void setDirecaoMesclagem(String direcaoMesclagemStr) { // Para compatibilidade com DAO
        this.direcaoMesclagem = DirecaoMesclagem.fromString(direcaoMesclagemStr);
    }
    
    public TransformacaoImagem getTransformacaoPadraoEnum() { return transformacaoPadrao; }
    public String getTransformacaoPadrao() { // Para compatibilidade com DAO que espera String
        return transformacaoPadrao != null ? transformacaoPadrao.toPersistentString() : TransformacaoImagem.NENHUMA.toPersistentString();
    }
    public void setTransformacaoPadrao(TransformacaoImagem transformacaoPadrao) { this.transformacaoPadrao = transformacaoPadrao; }
    public void setTransformacaoPadrao(String transformacaoPadraoStr) { // Para compatibilidade com DAO
        this.transformacaoPadrao = TransformacaoImagem.fromString(transformacaoPadraoStr);
    }

    public TransformacaoImagem getTransformacaoGlobalEnum() { return transformacaoGlobal; }
    public String getTransformacaoGlobal() {
        return transformacaoGlobal != null ? transformacaoGlobal.toPersistentString() : TransformacaoImagem.NENHUMA.toPersistentString();
    }
    public void setTransformacaoGlobal(TransformacaoImagem transformacaoGlobal) { this.transformacaoGlobal = transformacaoGlobal; }
    public void setTransformacaoGlobal(String transformacaoGlobalStr) {
        this.transformacaoGlobal = TransformacaoImagem.fromString(transformacaoGlobalStr);
    }

    // Getters e Setters para campos Cloudinary individuais (para compatibilidade com DAO)
    public String getCloudName() { return cloudName; }
    public void setCloudName(String cloudName) { this.cloudName = cloudName; }

    public String getCloudApiKey() { return cloudApiKey; }
    public void setCloudApiKey(String cloudApiKey) { this.cloudApiKey = cloudApiKey; }

    public String getCloudApiSecret() { return cloudApiSecret; }
    public void setCloudApiSecret(String cloudApiSecret) { this.cloudApiSecret = cloudApiSecret; }

    // Métodos booleanos derivados de storageOption (para compatibilidade com DAO)
    public boolean isSalvarLocal() {
        return storageOption == StorageOption.LOCAL_ONLY || storageOption == StorageOption.LOCAL_AND_CLOUD;
    }
    public void setSalvarLocal(boolean salvarLocal) {
        // Este setter é mais complexo pois depende de isSalvarNuvem()
        // Se o DAO chama setSalvarLocal(true) e setSalvarNuvem(true) separadamente,
        // precisamos de uma lógica para definir storageOption corretamente.
        if (salvarLocal) {
            if (isSalvarNuvem()) { // Se nuvem já está ou será true
                this.storageOption = StorageOption.LOCAL_AND_CLOUD;
            } else {
                this.storageOption = StorageOption.LOCAL_ONLY;
            }
        } else { // salvarLocal é false
            if (isSalvarNuvem()) {
                this.storageOption = StorageOption.CLOUD_ONLY;
            } else {
                // Ambos false não é uma opção válida em StorageOption,
                // Poderia lançar exceção ou definir um padrão (e.g. LOCAL_ONLY)
                // Por ora, vamos assumir que o DAO não colocará nesse estado ou que LOCAL_ONLY é o fallback.
                 this.storageOption = StorageOption.LOCAL_ONLY; // Ou lançar erro
            }
        }
    }


    public boolean isSalvarNuvem() {
        return storageOption == StorageOption.CLOUD_ONLY || storageOption == StorageOption.LOCAL_AND_CLOUD;
    }
     public void setSalvarNuvem(boolean salvarNuvem) {
        if (salvarNuvem) {
            if (isSalvarLocal()) { // Se local já está ou será true
                this.storageOption = StorageOption.LOCAL_AND_CLOUD;
            } else {
                this.storageOption = StorageOption.CLOUD_ONLY;
            }
        } else { // salvarNuvem é false
            if (isSalvarLocal()) {
                this.storageOption = StorageOption.LOCAL_ONLY;
            } else {
                 this.storageOption = StorageOption.LOCAL_ONLY; // Ou lançar erro
            }
        }
    }
    
    // Método auxiliar para ServicoMesclagem.java, se ele precisar de uma lista de transformações
    // Este é um exemplo, ajuste conforme a necessidade real de ServicoMesclagem
    public TransformacaoImagem getTransformacaoParaImagem(int index) {
        // Lógica de exemplo: aplicar transformação padrão a todas, ou lógica mais complexa
        // Se você tiver uma lista de transformações por imagem, adicione esse campo.
        // Por enquanto, retorna a transformação padrão.
        return getTransformacaoPadraoEnum();
    }
}
