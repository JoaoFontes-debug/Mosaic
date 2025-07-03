package com.joaofontes.mosaic.service;

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

    private final ControladorPrincipal controlador;
    private Timer temporizador;
    private final Robot robot;
    private Rectangle areaCapturaAtual;
    private BufferedImage imagemAnterior;
    private boolean capturando = false;

    public ServicoCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("Erro ao iniciar serviço de captura", e);
        }
    }

    public void iniciarCaptura(ConfiguracaoCaptura config, Rectangle area) {
        if (area == null || area.width <= 0 || area.height <= 0) {
            return;
        }
        if (capturando) {
            pararCaptura();
        }
        this.areaCapturaAtual = area;
        this.imagemAnterior = null;
        temporizador = new Timer(config.getTempoEntreCapturasMs(), e -> capturarTela(config));
        temporizador.setInitialDelay(0);
        temporizador.start();
        capturando = true;
    }

    public void pararCaptura() {
        if (temporizador != null && temporizador.isRunning()) {
            temporizador.stop();
        }
        capturando = false;
    }

    public boolean isCapturando() {
        return capturando;
    }

    private void capturarTela(ConfiguracaoCaptura config) {
        if (areaCapturaAtual == null) {
            pararCaptura();
            return;
        }
        try {
            BufferedImage imagemAtual = robot.createScreenCapture(areaCapturaAtual);
            if (imagemMudou(imagemAtual, config.getLimiarMudanca())) {
                controlador.onNovaImagemDetectada(imagemAtual);
            }
            imagemAnterior = imagemAtual;
        } catch (Exception e) {
            pararCaptura();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Erro na captura: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
        }
    }

    private boolean imagemMudou(BufferedImage novaImagem, double limiar) {
        if (imagemAnterior == null || novaImagem.getWidth() != imagemAnterior.getWidth() || novaImagem.getHeight() != imagemAnterior.getHeight()) {
            return true;
        }

        long diff = 0;
        int pixelsTotais = novaImagem.getWidth() * novaImagem.getHeight();
        if (pixelsTotais == 0) {
            return false;
        }

        for (int y = 0; y < novaImagem.getHeight(); y++) {
            for (int x = 0; x < novaImagem.getWidth(); x++) {
                int rgb1 = imagemAnterior.getRGB(x, y);
                int rgb2 = novaImagem.getRGB(x, y);

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;

                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;

                diff += Math.abs(r1 - r2);
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
            }
        }

        double mediaDiferenca = diff / (double) pixelsTotais;
        double porcentagemDiferenca = (mediaDiferenca / (255.0 * 3.0)) * 100.0;

        return porcentagemDiferenca > limiar;
    }
}
