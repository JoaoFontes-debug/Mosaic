
package com.joaofontes.mosaic.util;

import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import com.joaofontes.mosaic.model.DirecaoMesclagem;
import com.joaofontes.mosaic.model.TransformacaoImagem;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

    /**
     * Mescla uma lista de imagens com base na configuração fornecida.
     * As transformações individuais são aplicadas a cada imagem antes da mesclagem.
     * @param imagens A lista de BufferedImages a serem mescladas.
     * @param config As configurações de captura, incluindo direção da mesclagem e transformações.
     * @return Uma única BufferedImage resultante da mesclagem.
     */
    public BufferedImage mesclarImagens(List<BufferedImage> imagens, ConfiguracaoCaptura config) {
        if (imagens == null || imagens.isEmpty()) {
            System.err.println("ServicoMesclagem: Lista de imagens para mesclar está vazia ou nula.");
            // Retorna uma pequena imagem em branco ou lança exceção
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }

        // Aplicar transformações individuais a cada imagem
        List<BufferedImage> imagensTransformadas = aplicarTransformacoesIndividuais(imagens, config);
        
        // Calcular dimensões da imagem final
        int larguraTotal = 0;
        int alturaTotal = 0;
        
        // Usa getDirecaoMesclagemEnum() para comparação correta com a enum
        DirecaoMesclagem direcao = config.getDirecaoMesclagemEnum();
        if (direcao == null) {
            System.err.println("ServicoMesclagem: Direção de mesclagem não definida na configuração. Usando HORIZONTAL como padrão.");
            direcao = DirecaoMesclagem.HORIZONTAL; // Fallback para um padrão seguro
        }

        for (BufferedImage img : imagensTransformadas) {
            if (img == null) continue; // Pula imagens nulas que podem ter vindo de transformações falhas
            if (direcao == DirecaoMesclagem.HORIZONTAL) {
                larguraTotal += img.getWidth();
                alturaTotal = Math.max(alturaTotal, img.getHeight());
            } else { // VERTICAL
                alturaTotal += img.getHeight();
                larguraTotal = Math.max(larguraTotal, img.getWidth());
            }
        }

        if (larguraTotal <= 0 || alturaTotal <= 0) {
            System.err.println("ServicoMesclagem: Dimensões calculadas para imagem final são inválidas (<=0).");
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // Retorna imagem mínima
        }
        
        // Criar imagem final
        BufferedImage imagemMesclada = new BufferedImage(
            larguraTotal, alturaTotal, BufferedImage.TYPE_INT_RGB); // Usar TYPE_INT_ARGB se precisar de transparência
        
        Graphics2D g = imagemMesclada.createGraphics();
        int posicaoAtual = 0; // Posição x para horizontal, y para vertical
        
        for (BufferedImage img : imagensTransformadas) {
            if (img == null) continue;
            if (direcao == DirecaoMesclagem.HORIZONTAL) {
                g.drawImage(img, posicaoAtual, 0, null);
                posicaoAtual += img.getWidth();
            } else { // VERTICAL
                g.drawImage(img, 0, posicaoAtual, null);
                posicaoAtual += img.getHeight();
            }
        }
        
        g.dispose();

        // Aplicar transformação global à imagem mesclada final, se houver
        TransformacaoImagem transformacaoGlobal = config.getTransformacaoGlobalEnum();
        if (transformacaoGlobal != null && transformacaoGlobal != TransformacaoImagem.NENHUMA) {
            imagemMesclada = aplicarTransformacaoUnica(imagemMesclada, transformacaoGlobal);
        }

        return imagemMesclada;
    }
    
    /**
     * Aplica transformações individuais a cada imagem na lista.
     * @param imagens Lista de imagens originais.
     * @param config Configurações que definem qual transformação aplicar.
     * @return Lista de imagens transformadas.
     */
    private List<BufferedImage> aplicarTransformacoesIndividuais(
        List<BufferedImage> imagens, ConfiguracaoCaptura config) {
        
        List<BufferedImage> resultado = new ArrayList<>();
        if (imagens == null) return resultado;

        for (int i = 0; i < imagens.size(); i++) {
            BufferedImage img = imagens.get(i);
            if (img == null) {
                resultado.add(null); // Mantém o placeholder se a imagem original for nula
                continue;
            }
            // getTransformacaoParaImagem(i) deve retornar a TransformacaoImagem para a imagem no índice i.
            // Na implementação atual de ConfiguracaoCaptura, ele retorna a 'transformacaoPadrao'.
            TransformacaoImagem transform = config.getTransformacaoParaImagem(i); 
            resultado.add(aplicarTransformacaoUnica(img, transform));
        }
        
        return resultado;
    }
    
    /**
     * Aplica uma única transformação a uma BufferedImage.
     * @param img A imagem original.
     * @param transform A transformação a ser aplicada.
     * @return A imagem transformada, ou a original se a transformação for NENHUMA ou nula.
     */
    private BufferedImage aplicarTransformacaoUnica(BufferedImage img, TransformacaoImagem transform) {
        if (img == null || transform == null) {
            return img; // Retorna a imagem original se ela ou a transformação for nula
        }

        switch (transform) {
            case ESPELHAR_HORIZONTAL:
                return espelharHorizontal(img);
            case ESPELHAR_VERTICAL:
                return espelharVertical(img);
            case ROTACIONAR_90:
                return rotacionar(img, Math.PI / 2.0); // 90 graus
            case ROTACIONAR_180:
                return rotacionar(img, Math.PI);      // 180 graus
            case ROTACIONAR_270:
                return rotacionar(img, 3.0 * Math.PI / 2.0); // 270 graus
            case NENHUMA:
            default:
                return img; // Nenhuma transformação ou transformação desconhecida
        }
    }
    
    private BufferedImage espelharHorizontal(BufferedImage img) {
        if (img == null) return null;
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        // Cria uma nova imagem para o resultado para evitar modificar a original se ela for usada em outro lugar
        return op.filter(img, null); 
    }
    
    private BufferedImage espelharVertical(BufferedImage img) {
        if (img == null) return null;
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -img.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }
    
    /**
     * Rotaciona uma imagem em torno do seu centro.
     * @param img A imagem a ser rotacionada.
     * @param anguloRadianos O ângulo de rotação em radianos.
     * @return A imagem rotacionada.
     */
    private BufferedImage rotacionar(BufferedImage img, double anguloRadianos) {
        if (img == null) return null;

        final double sin = Math.abs(Math.sin(anguloRadianos));
        final double cos = Math.abs(Math.cos(anguloRadianos));
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int newW = (int) Math.floor(w * cos + h * sin);
        final int newH = (int) Math.floor(h * cos + w * sin);

        BufferedImage imagemRotacionada = new BufferedImage(newW, newH, img.getType()); // Ou TYPE_INT_ARGB
        Graphics2D g2d = imagemRotacionada.createGraphics();
        
        // Configurações para melhor qualidade de rotação
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Translada para o novo centro
        g2d.translate((newW - w) / 2.0, (newH - h) / 2.0);
        // Rotaciona em torno do centro da imagem original
        g2d.rotate(anguloRadianos, w / 2.0, h / 2.0);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return imagemRotacionada;
    }
}
