package com.joaofontes.mosaic.util;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import java.awt.Rectangle;
import java.awt.Robot;
import javax.swing.Timer; 
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ServicoCaptura {
    private ControladorPrincipal controlador;
    private Timer temporizador;
    private Robot robot;
    private Rectangle areaCapturaAtual;
    private BufferedImage imagemAnterior;
    private boolean capturando = false;
    private int larguraAnterior = 0;
    private int alturaAnterior = 0;

    public ServicoCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Erro crítico ao criar Robot para captura de tela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void iniciarCaptura(ConfiguracaoCaptura config, Rectangle area) {
        if (robot == null || area == null || area.width <= 0 || area.height <= 0) {
            System.err.println("Serviço de captura não pode ser iniciado. Robot ou área inválida.");
            return;
        }
        if (capturando) {
            pararCaptura();
        }
        this.areaCapturaAtual = area;
        this.imagemAnterior = null;
        this.larguraAnterior = 0;
        this.alturaAnterior = 0;
        temporizador = new Timer(config.getTempoEntreCapturasMs(), e -> capturarTela());
        temporizador.setInitialDelay(0);
        temporizador.start();
        capturando = true;
        System.out.println("Serviço de captura iniciado. Intervalo de Verificação: " + config.getTempoEntreCapturasMs() + "ms");
    }

    public void pararCaptura() {
        if (temporizador != null && temporizador.isRunning()) {
            temporizador.stop();
        }
        capturando = false;
        System.out.println("Serviço de captura parado.");
    }
    
    public boolean isCapturando() {
        return capturando;
    }

    private void capturarTela() {
        if (areaCapturaAtual == null) {
            pararCaptura();
            return;
        }
        try {
            BufferedImage imagemAtual = robot.createScreenCapture(areaCapturaAtual);
            if (imagemAnterior == null || imagemMudou(imagemAnterior, imagemAtual)) {
                // ALTERAÇÃO: Chama o novo método do controlador para acumular imagens.
                controlador.onNovaImagemDetectada(imagemAtual);
                imagemAnterior = imagemAtual;
                larguraAnterior = imagemAtual.getWidth();
                alturaAnterior = imagemAtual.getHeight();
            }
        } catch (Exception e) {
            System.err.println("Erro durante a captura de tela: " + e.getMessage());
            pararCaptura();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, 
                "Ocorreu um erro durante a captura da tela.\nA captura foi interrompida.", 
                "Erro de Captura", JOptionPane.ERROR_MESSAGE));
        }
    }

    private boolean imagemMudou(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            if (img1.getWidth() != larguraAnterior || img1.getHeight() != alturaAnterior) {
                 return true;
            }
        }
        int step = Math.max(1, Math.min(img1.getWidth(), img1.getHeight()) / 20);
        for (int x = 0; x < img1.getWidth(); x += step) {
            for (int y = 0; y < img1.getHeight(); y += step) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) return true;
            }
        }
        return false;
    }
}