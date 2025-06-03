
package com.joaofontes.mosaic.util;

import com.cloudinary.Cloudinary;

/**
 *
 * @author aluno.lauro
 */
public class CloudnaryConfig {
    public static Cloudinary getCloudinaryInstance() {
        // Substitua com sua URL de configuração real
        Cloudinary cloudinary = new Cloudinary("CLOUDINARY_URL=cloudinary://776427566469449:FWdtJdhqEzDFuUoBiwtkE3RK3qU@dkmbs6lyk");
        return cloudinary;
    }
}
