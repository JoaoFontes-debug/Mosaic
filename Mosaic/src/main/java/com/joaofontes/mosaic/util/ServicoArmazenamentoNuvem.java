
package com.joaofontes.mosaic.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author aluno.lauro
 */
public class ServicoArmazenamentoNuvem {
    
      private final Cloudinary cloudinary;

    public ServicoArmazenamentoNuvem() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dkmbs6lyk",
            "api_key", "776427566469449",
            "api_secret", "FWdtJdhqEzDFuUoBiwtkE3RK3qU"
        ));
    }

    public String uploadImagem(byte[] dadosImagem) throws IOException {
        Map<?, ?> resposta = cloudinary.uploader().upload(dadosImagem, ObjectUtils.emptyMap());
        return (String) resposta.get("secure_url");
    }
}
