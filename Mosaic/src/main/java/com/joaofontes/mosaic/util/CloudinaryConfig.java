package com.joaofontes.mosaic.util;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv; 

public class CloudinaryConfig {
    private static Cloudinary cloudinaryInstance;

    static {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String cloudinaryUrl = dotenv.get("CLOUDINARY_URL");

            if (cloudinaryUrl == null || cloudinaryUrl.trim().isEmpty()) {
                cloudinaryUrl = System.getenv("CLOUDINARY_URL");
            }

            if (cloudinaryUrl != null && !cloudinaryUrl.trim().isEmpty()) {
                cloudinaryInstance = new Cloudinary(cloudinaryUrl);
                System.out.println("CloudinaryConfig: Instância Cloudinary criada a partir da URL de ambiente/dotenv.");
            } else {
                System.out.println("CloudinaryConfig: CLOUDINARY_URL não encontrada no .env ou variáveis de ambiente. A instância será nula até que uma URL seja fornecida via UI.");
            }
        } catch (Exception e) { // Captura Exception genérica para cobrir problemas com Dotenv ou Cloudinary
            System.err.println("CloudinaryConfig: Erro ao inicializar Cloudinary a partir de .env/variáveis de ambiente: " + e.getMessage());
            // e.printStackTrace(); // Para debug
        }
    }

    public static Cloudinary getCloudinaryInstance() {
        if (cloudinaryInstance == null) {
             System.out.println("CloudinaryConfig: Tentando obter instância, mas é nula. Isso é esperado se CLOUDINARY_URL não foi definida no ambiente/dotenv.");
        }
        return cloudinaryInstance;
    }
}
