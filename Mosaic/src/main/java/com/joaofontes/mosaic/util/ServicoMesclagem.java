
package com.joaofontes.mosaic.util;

import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura.DirecaoMesclagem;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura.TransformacaoImagem;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
            resultado.add(aplicarTransformacao(img, transform));
        }
        
        return resultado;
    }
    
    private BufferedImage aplicarTransformacao(BufferedImage img, TransformacaoImagem transform) {
        switch (transform) {
            case ESPELHAR_HORIZONTAL:
                return espelharHorizontal(img);
            case ESPELHAR_VERTICAL:
                return espelharVertical(img);
            case ROTACIONAR_90:
                return rotacionar(img, Math.PI / 2);
            case ROTACIONAR_180:
                return rotacionar(img, Math.PI);
            default:
                return img;
        }
    }
    
    private BufferedImage espelharHorizontal(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }
    
    private BufferedImage espelharVertical(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -img.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }
    
    private BufferedImage rotacionar(BufferedImage img, double angulo) {
        AffineTransform tx = new AffineTransform();
        tx.rotate(angulo, img.getWidth() / 2, img.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(img, null);
    }
}
