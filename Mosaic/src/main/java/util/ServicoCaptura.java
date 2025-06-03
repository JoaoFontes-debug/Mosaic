/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

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
    
    public ServicoCaptura() {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            throw new RuntimeException("Erro ao inicializar serviço de captura", ex);
        }
    }
    
    public void iniciarCaptura(ConfiguracaoCaptura config) {
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
        // Implementar lógica de comparação
        return true;
    }
    
    private void processarMesclagem(ConfiguracaoCaptura config) {
        BufferedImage imagemMesclada = new ServicoMesclagem().mesclarImagens(imagensCapturadas, config);
        imagensCapturadas.clear();
        
        if (config.isExibicaoAutoHabilitada()) {
            visao.JanelaPrincipal.getInstance().exibirImagemMesclada(imagemMesclada);
        }
        
        // Salvar imagem e metadados
    }
}
