/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.util.DialogoExibicaoAuto;
import java.awt.CardLayout;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author JoãoFontes
 */
public class JanelaPrincipal extends JFrame{
    
    private static JanelaPrincipal instance;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelPrincipal = new JPanel(cardLayout);
    private final ControladorPrincipal controlador = new ControladorPrincipal();

    private JanelaPrincipal() {
        configurarJanela();
        initUI();
    }

    public static JanelaPrincipal getInstance() {
        if (instance == null) {
            instance = new JanelaPrincipal();
        }
        return instance;
    }

    private void configurarJanela() {
        setTitle("Sistema MOSAIC");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        // Configurar barra de menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuVisualizacao = new JMenu("Visualização");
        JMenuItem itemCaptura = new JMenuItem("Captura");
        JMenuItem itemConfig = new JMenuItem("Configurações");
        JMenuItem itemMetadados = new JMenuItem("Metadados");
        JMenuItem itemSessao = new JMenuItem("Sessões");
        
        itemCaptura.addActionListener(e -> cardLayout.show(painelPrincipal, "CAPTURA"));
        itemConfig.addActionListener(e -> cardLayout.show(painelPrincipal, "CONFIG"));
        itemMetadados.addActionListener(e -> cardLayout.show(painelPrincipal, "METADADOS"));
        itemSessao.addActionListener(e -> cardLayout.show(painelPrincipal, "SESSAO"));
        
        menuVisualizacao.add(itemCaptura);
        menuVisualizacao.add(itemConfig);
        menuVisualizacao.add(itemMetadados);
        menuVisualizacao.add(itemSessao);
        menuBar.add(menuVisualizacao);
        
        setJMenuBar(menuBar);

        // Adicionar painéis
        painelPrincipal.add(new PainelCaptura(controlador), "CAPTURA");
        painelPrincipal.add(new PainelConfiguracao(controlador), "CONFIG");
        painelPrincipal.add(new PainelMetadados(controlador), "METADADOS");
        painelPrincipal.add(new PainelSessao(controlador), "SESSAO");
        
        add(painelPrincipal);
    }

    public void exibirImagemMesclada(BufferedImage imagem) {
    new DialogoExibicaoAuto(this, imagem, 
        controlador.getConfiguracao().getTempoFechamentoAuto()).exibir();
}
}
