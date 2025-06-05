package com.joaofontes.mosaic.util;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.Timer; 
import java.awt.AWTException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
// import java.util.Arrays; // Não usado atualmente

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
            // Considerar lançar uma RuntimeException ou ter um estado de erro no serviço
        }
    }

    public void iniciarCaptura(ConfiguracaoCaptura config, Rectangle area) {
        if (robot == null) {
            System.err.println("Serviço de captura não inicializado (Robot falhou). Não é possível iniciar.");
            return;
        }
        if (capturando) {
            System.out.println("Captura de tela já está em andamento. Parando a anterior antes de iniciar nova.");
            pararCaptura(); // Garante que não haja múltiplos timers
        }

        if (area == null || area.width <= 0 || area.height <= 0) {
            System.err.println("Área de captura inválida fornecida para iniciarCaptura. Área: " + area);
            // Poderia notificar o usuário via JOptionPane no Controlador, mas aqui apenas logamos.
            return;
        }

        this.areaCapturaAtual = area;
        this.imagemAnterior = null; // Reseta a imagem anterior para forçar a primeira captura/processamento
        this.larguraAnterior = 0;   // Reseta dimensões para forçar a primeira captura
        this.alturaAnterior = 0;

        if (temporizador != null && temporizador.isRunning()) { // Segurança extra
            temporizador.stop();
        }

        temporizador = new Timer(config.getTempoEntreCapturasMs(), e -> capturarTela());
        temporizador.setInitialDelay(0); 
        temporizador.start();
        capturando = true;
        System.out.println("Serviço de captura de tela iniciado. Área: " + area + ", Intervalo: " + config.getTempoEntreCapturasMs() + "ms");
    }

    public void pararCaptura() {
        if (temporizador != null && temporizador.isRunning()) {
            temporizador.stop();
        }
        capturando = false;
        System.out.println("Serviço de captura de tela parado.");
    }
    
    public boolean isCapturando() {
        return capturando;
    }

    private void capturarTela() {
        if (areaCapturaAtual == null || areaCapturaAtual.width <= 0 || areaCapturaAtual.height <= 0 ) {
            System.err.println("Área de captura inválida ou não definida no loop de captura. Parando captura.");
            pararCaptura(); // Para o timer se a área se tornar inválida
            // Notificar o controlador ou a UI pode ser uma boa adição aqui.
            // Ex: controlador.notificarErroCaptura("Área de captura tornou-se inválida.");
            return;
        }

        try {
            BufferedImage imagemAtual = robot.createScreenCapture(areaCapturaAtual);

            if (imagemAnterior == null || imagemMudou(imagemAnterior, imagemAtual)) {
                System.out.println("Mudança detectada na área de captura. Processando...");
                controlador.processarImagensMescladas(imagemAtual); 
                imagemAnterior = imagemAtual; 
                larguraAnterior = imagemAtual.getWidth(); // Atualiza dimensões para próxima comparação
                alturaAnterior = imagemAtual.getHeight();
            }
        } catch (SecurityException se) {
            System.err.println("Erro de segurança durante a captura de tela (provavelmente permissões): " + se.getMessage());
            se.printStackTrace();
            pararCaptura();
            controlador.pararCaptura(); // Notifica o controlador para atualizar estado da UI
             SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, 
                "Erro de segurança ao capturar a tela. Verifique as permissões do sistema.\nA captura foi interrompida.", 
                "Erro de Captura", JOptionPane.ERROR_MESSAGE));
        } catch (Exception e) { // Captura outras exceções genéricas
            System.err.println("Erro inesperado durante a captura de tela: " + e.getMessage());
            e.printStackTrace();
            pararCaptura(); 
            controlador.pararCaptura();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, 
                "Ocorreu um erro inesperado durante a captura da tela.\nA captura foi interrompida.", 
                "Erro de Captura", JOptionPane.ERROR_MESSAGE));
        }
    }

    private boolean imagemMudou(BufferedImage img1, BufferedImage img2) {
        // Verifica primeiro se as dimensões mudaram (mais rápido)
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            // Se as dimensões da área de captura mudaram (improvável sem re-seleção, mas seguro verificar)
            // ou se a imagem anterior tinha dimensões diferentes (ex: primeira captura)
            if (img1.getWidth() != larguraAnterior || img1.getHeight() != alturaAnterior) {
                 return true; // Considera como mudança se as dimensões da imagem anterior não batem
            }
        }
        
        // Compara uma amostra de pixels para performance.
        // Ajuste o passo (step) conforme necessário. Um passo menor é mais preciso, mas mais lento.
        int step = Math.max(1, Math.min(img1.getWidth(), img1.getHeight()) / 20); // Ex: 1/20 da menor dimensão

        for (int x = 0; x < img1.getWidth(); x += step) {
            for (int y = 0; y < img1.getHeight(); y += step) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return true; // Encontrou um pixel diferente
                }
            }
        }
        return false; // Nenhuma diferença encontrada na amostragem
    }
}
