package com.joaofontes.mosaic;

import io.github.cdimascio.dotenv.Dotenv;

public class TesteDotenv {
    public static void main(String[] args) {
        // Carrega novamente (poderia puxar de CloudnaryConfig, mas vamos testar diretamente)
        Dotenv dotenv = Dotenv.configure()
                              .ignoreIfMalformed()
                              .ignoreIfMissing()
                              .load();

        String cloudUrl = dotenv.get("CLOUDINARY_URL");
        System.out.println("Variável .env CLOUDINARY_URL = " + cloudUrl);
    }
}