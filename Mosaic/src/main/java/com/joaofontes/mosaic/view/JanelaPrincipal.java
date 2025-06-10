package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.net.URL;

public class JanelaPrincipal extends JFrame {
    private static JanelaPrincipal instancia;
    private final ControladorPrincipal controlador;
    private PainelCaptura painelCaptura;
    private PainelMetadados painelMetadados;
    private PainelConfiguracao painelConfiguracao;
    private PainelInspecoes painelInspecoes;

    private JanelaPrincipal() {
        this.controlador = new ControladorPrincipal(); 
        initUI();
    }

    public static JanelaPrincipal getInstance() {
        if (instancia == null) {
            instancia = new JanelaPrincipal();
        }
        return instancia;
    }

    private void initUI() {
        configurarJanela();
        JTabbedPane abas = new JTabbedPane();
        painelCaptura = new PainelCaptura(controlador);
        painelMetadados = new PainelMetadados(controlador);
        painelConfiguracao = new PainelConfiguracao(controlador);
        painelInspecoes = new PainelInspecoes(controlador);

        abas.addTab("Captura", painelCaptura);
        abas.addTab("Detalhes da Inspeção", painelMetadados);
        abas.addTab("Configurações", painelConfiguracao);
        abas.addTab("Inspeções Salvas", painelInspecoes);

        add(abas, BorderLayout.CENTER);
    }

    private void configurarJanela() {
        setTitle("Sistema MOSAIC");
        
        // ALTERAÇÃO: Aumentei o tamanho da janela para melhor acomodar os componentes.
        setSize(950, 700);
        setMinimumSize(new Dimension(950, 700)); // Impede que a janela seja redimensionada para um tamanho menor

        setResizable(true); // Permitir redimensionamento pode ser útil, mas mantendo um mínimo.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        
        URL iconURL = getClass().getResource("/images/Logo_Mosaic.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Ícone da aplicação não encontrado em /com/joaofontes/mosaic/icons/Logo_Mosaic.png");
        }
    }

    public void exibirImagemMesclada(BufferedImage imagem) {
        Runnable resumeCaptureAction = () -> {
            if (controlador.getConfiguracao().getAreaCaptura() != null) {
                 controlador.reiniciarCapturaPosExibicao();
            }
        };
        DialogoExibicaoAuto dialogo = new DialogoExibicaoAuto(this, imagem, controlador.getConfiguracao().getTempoFechamentoAuto(), resumeCaptureAction);
        dialogo.exibir(); 
    }
}