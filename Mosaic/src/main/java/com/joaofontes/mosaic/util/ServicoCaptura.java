/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.joaofontes.mosaic.util;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 *
 * @author JoãoFontes
 */
public class ServicoCaptura {
      private Timer temporizador;
    private final Robot robot;
    private BufferedImage ultimaCaptura;
    private final List<BufferedImage> imagensCapturadas = new ArrayList<>();
    private final ControladorPrincipal controlador;
    
    public ServicoCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            throw new RuntimeException("Erro ao iniciar serviço de captura", ex);
        }
    }
    
    public void iniciarCaptura(ConfiguracaoCaptura config, Rectangle area) {
        config.setAreaCaptura(area);
        imagensCapturadas.clear();
        
        temporizador = new Timer(config.getIntervaloCaptura(), e -> {
            capturarTela(config);
        });
        temporizador.start();
    }
    
    public void pararCaptura() {
        if (temporizador != null && temporizador.isRunning()) {
            temporizador.stop();
        }
    }
    
    private void capturarTela(ConfiguracaoCaptura config) {
        Rectangle area = config.getAreaCaptura();
        if (area == null) return;
        
        BufferedImage capturaAtual = robot.createScreenCapture(area);
        
        if (imagemMudou(capturaAtual)) {
            imagensCapturadas.add(capturaAtual);
            
            if (imagensCapturadas.size() >= config.getNumeroImagensMesclagem()) {
                processarMesclagem(config);
            }
        }
        ultimaCaptura = capturaAtual;
    }
    
   private boolean imagemMudou(BufferedImage novaImagem) {
    if (ultimaCaptura == null) return true;
    
    // Verificar se as imagens têm o mesmo tamanho
    if (novaImagem.getWidth() != ultimaCaptura.getWidth() || 
        novaImagem.getHeight() != ultimaCaptura.getHeight()) {
        return true;
    }
    
    // Comparação eficiente por hash
    int[] pixelsAtuais = novaImagem.getRGB(
        0, 0, novaImagem.getWidth(), novaImagem.getHeight(), null, 0, novaImagem.getWidth());
    
    int[] pixelsUltimos = ultimaCaptura.getRGB(
        0, 0, ultimaCaptura.getWidth(), ultimaCaptura.getHeight(), null, 0, ultimaCaptura.getWidth());
    
    return !java.util.Arrays.equals(pixelsAtuais, pixelsUltimos);
}
    
    private void processarMesclagem(ConfiguracaoCaptura config) {
        ServicoMesclagem servicoMesclagem = new ServicoMesclagem();
        BufferedImage imagemMesclada = servicoMesclagem.mesclarImagens(imagensCapturadas, config);
        imagensCapturadas.clear();
        controlador.processarImagensMescladas(imagemMesclada);
    }
}
