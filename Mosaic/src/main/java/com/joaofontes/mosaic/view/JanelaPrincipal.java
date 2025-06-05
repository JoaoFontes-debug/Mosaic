package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura; 
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
// import javax.swing.SwingUtilities; // Já importado em Mosaic.java
import java.net.URL;

public class JanelaPrincipal extends JFrame {
    private static JanelaPrincipal instancia;
    private ControladorPrincipal controlador;
    private PainelCaptura painelCaptura;
    private PainelMetadados painelMetadados;
    private PainelConfiguracao painelConfiguracao;
    private PainelSessao painelSessao;
    private JTabbedPane abas;

    private JanelaPrincipal() {
        // Inicializa o controlador
        // A configuração é carregada de um arquivo ou padrão dentro do ControladorPrincipal
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

        abas = new JTabbedPane();
        // Passa a instância do controlador para os painéis
        painelCaptura = new PainelCaptura(controlador);
        painelMetadados = new PainelMetadados(controlador);
        painelConfiguracao = new PainelConfiguracao(controlador);
        painelSessao = new PainelSessao(controlador);

        abas.addTab("Captura", painelCaptura);
        abas.addTab("Metadados", painelMetadados);
        abas.addTab("Configurações", painelConfiguracao);
        abas.addTab("Sessões Salvas", painelSessao);

        add(abas, BorderLayout.CENTER);
    }

    private void configurarJanela() {
        setTitle("Sistema MOSAIC");
        setSize(756, 567); // Tamanho fixo (20cm x 15cm @ 96 DPI)
        setResizable(false); // Desabilitar redimensionamento
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela

        URL iconURL = getClass().getResource("/icons/mosaic_icon.png"); 
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        } else {
            System.err.println("Ícone da aplicação não encontrado: /icons/mosaic_icon.png");
        }
    }

    public void exibirImagemMesclada(BufferedImage imagem) {
        Runnable resumeCaptureAction = () -> {
            if (controlador.getConfiguracao().getAreaCaptura() != null) {
                 controlador.reiniciarCapturaPosExibicao();
            } else {
                System.out.println("Nenhuma área de captura definida. Captura não será reiniciada.");
            }
        };

        DialogoExibicaoAuto dialogo = new DialogoExibicaoAuto(
            this,
            imagem,
            controlador.getConfiguracao().getTempoFechamentoAuto(), 
            resumeCaptureAction
        );
        dialogo.exibir(); 
    }
    
    public ControladorPrincipal getControlador() {
        return controlador;
    }

    public PainelMetadados getPainelMetadados() {
        return painelMetadados;
    }
}
