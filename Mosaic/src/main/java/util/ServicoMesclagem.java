
package util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JoãoFontes
 */
public class ServicoMesclagem {
    public BufferedImage mesclarImagens(List<BufferedImage> imagens, ConfiguracaoCaptura config) {
        // Aplicar transformações
        List<BufferedImage> imagensTransformadas = aplicarTransformacoes(imagens, config);
        
        // Calcular dimensões
        int larguraTotal = 0;
        int alturaTotal = 0;
        
        for (BufferedImage img : imagensTransformadas) {
            if (config.getDirecaoMesclagem() == DirecaoMesclagem.HORIZONTAL) {
                larguraTotal += img.getWidth();
                alturaTotal = Math.max(alturaTotal, img.getHeight());
            } else {
                alturaTotal += img.getHeight();
                larguraTotal = Math.max(larguraTotal, img.getWidth());
            }
        }
        
        // Criar imagem final
        BufferedImage imagemFinal = new BufferedImage(
            larguraTotal, alturaTotal, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g = imagemFinal.createGraphics();
        int posicao = 0;
        
        for (BufferedImage img : imagensTransformadas) {
            if (config.getDirecaoMesclagem() == DirecaoMesclagem.HORIZONTAL) {
                g.drawImage(img, posicao, 0, null);
                posicao += img.getWidth();
            } else {
                g.drawImage(img, 0, posicao, null);
                posicao += img.getHeight();
            }
        }
        
        g.dispose();
        return imagemFinal;
    }
    
    private List<BufferedImage> aplicarTransformacoes(
        List<BufferedImage> imagens, ConfiguracaoCaptura config) {
        
        List<BufferedImage> resultado = new ArrayList<>();
        
        for (int i = 0; i < imagens.size(); i++) {
            BufferedImage img = imagens.get(i);
            TransformacaoImagem transform = config.getTransformacaoParaImagem(i);
            
            switch (transform) {
                case ESPELHAR_HORIZONTAL:
                    img = espelharHorizontal(img);
                    break;
                case ROTACIONAR_90:
                    img = rotacionar(img, Math.PI / 2);
                    break;
                // Outras transformações
            }
            
            resultado.add(img);
        }
        
        return resultado;
    }
    
    private BufferedImage espelharHorizontal(BufferedImage img) {
        // Implementar espelhamento
        return img;
    }
    
    private BufferedImage rotacionar(BufferedImage img, double angulo) {
        // Implementar rotação
        return img;
    }
    
}
