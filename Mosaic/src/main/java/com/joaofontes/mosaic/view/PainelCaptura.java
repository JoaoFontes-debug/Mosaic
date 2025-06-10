package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PainelCaptura extends JPanel {
    private final ControladorPrincipal controlador;
    private JButton btnIniciar;
    private JButton btnParar;

    public PainelCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        GridBagConstraints gbc = new GridBagConstraints();

        // ALTERAÇÃO: Adiciona o logo
        URL logoUrl = getClass().getResource("/images/Logo_Mosaic.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            // Redimensiona o logo para um tamanho apropriado se necessário
            Image image = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(image));
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(0, 0, 40, 0); // Espaço abaixo do logo
            gbc.anchor = GridBagConstraints.CENTER;
            add(logoLabel, gbc);
        } else {
            System.err.println("Logo não encontrado em: /images/Logo_Mosaic.jpg");
            // Pode adicionar um JLabel de texto como fallback
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 40, 0);
            add(new JLabel("MOSAIC"), gbc);
        }

        // ALTERAÇÃO: Painel para os botões para garantir tamanho e espaçamento consistentes
        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBotoes = new GridBagConstraints();
        gbcBotoes.fill = GridBagConstraints.HORIZONTAL;
        gbcBotoes.insets = new Insets(5, 0, 5, 0);
        gbcBotoes.ipadx = 50; // Aumenta a largura interna dos botões
        gbcBotoes.ipady = 10; // Aumenta a altura interna dos botões

        btnIniciar = new JButton("Iniciar Captura");
        btnIniciar.addActionListener(e -> {
            boolean areaSelecionada = controlador.selecionarAreaCaptura();
            if (areaSelecionada) {
                controlador.iniciarCaptura();
            }
        });
        gbcBotoes.gridx = 0;
        gbcBotoes.gridy = 0;
        painelBotoes.add(btnIniciar, gbcBotoes);

        btnParar = new JButton("Parar Captura");
        btnParar.addActionListener(e -> {
            controlador.pararCaptura();
            JOptionPane.showMessageDialog(this, "Captura parada.", "Captura Parada", JOptionPane.INFORMATION_MESSAGE);
        });
        gbcBotoes.gridx = 0;
        gbcBotoes.gridy = 1;
        painelBotoes.add(btnParar, gbcBotoes);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(painelBotoes, gbc);
    }
}