package com.joaofontes.mosaic.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;

public class ServicoArmazenamentoNuvem {
    private Cloudinary cloudinary;
    private String activeCloudinaryUrl; // Para rastrear a URL usada na inicialização

    public ServicoArmazenamentoNuvem(String cloudinaryUrl) {
        initialize(cloudinaryUrl);
    }

    public ServicoArmazenamentoNuvem() { // Construtor padrão
        // Tenta inicializar com a configuração padrão (variáveis de ambiente/arquivo .env)
        // Isso pode ser uma URL completa ou credenciais separadas dependendo de CloudinaryConfig
        this.cloudinary = CloudinaryConfig.getCloudinaryInstance();
        if (this.cloudinary != null) {
            // Se CloudinaryConfig retorna uma instância, tentamos obter a URL dela se possível
            // (a API da Cloudinary não expõe diretamente a URL de configuração original de forma simples)
            // Vamos assumir que se foi inicializado, está pronto, ou a URL será fornecida via reinitialize.
            this.activeCloudinaryUrl = "config_default"; // Placeholder
            System.out.println("ServicoArmazenamentoNuvem: Cloudinary inicializado com configuração padrão (CloudinaryConfig).");
        } else {
            System.err.println("ServicoArmazenamentoNuvem: CloudinaryConfig não retornou uma instância. Cloudinary não está configurado.");
        }
    }
    
    private void initialize(String cloudinaryUrl) {
        if (cloudinaryUrl != null && !cloudinaryUrl.trim().isEmpty()) {
            try {
                this.cloudinary = new Cloudinary(cloudinaryUrl);
                this.activeCloudinaryUrl = cloudinaryUrl;
                // Teste de ping opcional para verificar a conexão
                // this.cloudinary.api().ping(ObjectUtils.emptyMap()); 
                System.out.println("ServicoArmazenamentoNuvem: Cloudinary inicializado com URL fornecida: " + cloudinaryUrl);
            } catch (Exception e) { // Captura Exception mais genérica pois a construção pode falhar por vários motivos
                System.err.println("ServicoArmazenamentoNuvem: Falha ao inicializar Cloudinary com URL: " + cloudinaryUrl + ". Erro: " + e.getMessage());
                System.err.println("ServicoArmazenamentoNuvem: Tentando fallback para configuração padrão (CloudinaryConfig)...");
                this.cloudinary = CloudinaryConfig.getCloudinaryInstance(); // Fallback
                if (this.cloudinary == null) {
                    System.err.println("ServicoArmazenamentoNuvem: Fallback para CloudinaryConfig também falhou. Cloudinary não está configurado.");
                    this.activeCloudinaryUrl = null;
                } else {
                    this.activeCloudinaryUrl = "config_default_fallback"; // Placeholder
                     System.out.println("ServicoArmazenamentoNuvem: Cloudinary inicializado com configuração padrão (fallback).");
                }
            }
        } else {
            System.out.println("ServicoArmazenamentoNuvem: Nenhuma URL Cloudinary fornecida na inicialização, usando configuração padrão (CloudinaryConfig).");
            this.cloudinary = CloudinaryConfig.getCloudinaryInstance();
             if (this.cloudinary == null) {
                System.err.println("ServicoArmazenamentoNuvem: CloudinaryConfig não retornou uma instância. Cloudinary não está configurado.");
                this.activeCloudinaryUrl = null;
            } else {
                 this.activeCloudinaryUrl = "config_default_no_url_param"; // Placeholder
                 System.out.println("ServicoArmazenamentoNuvem: Cloudinary inicializado com configuração padrão (sem URL no construtor).");
            }
        }
    }

    public void reinitialize(String cloudinaryUrl) {
        System.out.println("ServicoArmazenamentoNuvem: Tentando reinicializar Cloudinary com URL: " + cloudinaryUrl);
        initialize(cloudinaryUrl);
    }
    
    public String getActiveCloudinaryUrl() {
        return activeCloudinaryUrl;
    }

    public String uploadImagem(byte[] imagemBytes, String nomeArquivoPublico) throws IOException {
        if (cloudinary == null) {
            throw new IOException("Cloudinary não está configurado. Verifique as configurações e a URL/credenciais.");
        }
        try {
            Map<?, ?> params = ObjectUtils.asMap(
                "public_id", nomeArquivoPublico, 
                "overwrite", true,
                "resource_type", "image"
                // Outras opções podem ser adicionadas aqui, como tags, pastas, etc.
            );
            Map<?, ?> uploadResult = cloudinary.uploader().upload(imagemBytes, params);
            return (String) uploadResult.get("url");
        } catch (Exception e) {
            String errorMessage = "Erro ao fazer upload da imagem para Cloudinary: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += ". Causa: " + e.getCause().getMessage();
            }
            System.err.println(errorMessage);
            // e.printStackTrace(); // Para debug mais detalhado
            throw new IOException(errorMessage, e);
        }
    }
}
