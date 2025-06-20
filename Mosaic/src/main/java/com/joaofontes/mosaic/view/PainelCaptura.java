package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PainelCaptura extends JPanel {
    private final ControladorPrincipal controlador;

    public PainelCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        GridBagConstraints gbc = new GridBagConstraints();

        // ALTERAÇÃO: O código do logo foi restaurado.
        URL logoUrl = getClass().getResource("/images/Logo_Mosaic.png");
        if (logoUrl != null) {
            ImageIcon logoIcon = new ImageIcon(logoUrl);
            Image image = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(image));
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 40, 0); // Espaço abaixo do logo
            gbc.anchor = GridBagConstraints.CENTER;
            add(logoLabel, gbc);
        } else {
            System.err.println("ERRO: Logo não encontrado em: /images/Logo_Mosaic.jpg");
            // Adiciona um texto de fallback se a imagem não for encontrada
            gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 40, 0);
            JLabel fallbackLabel = new JLabel("MOSAIC");
            fallbackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            add(fallbackLabel, gbc);
        }

        // Painel para os botões
        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBotoes = new GridBagConstraints();
        gbcBotoes.fill = GridBagConstraints.HORIZONTAL;
        gbcBotoes.insets = new Insets(5, 0, 5, 0);
        gbcBotoes.ipadx = 50; 
        gbcBotoes.ipady = 10;

        JButton btnIniciar = new JButton("Iniciar Captura");
        btnIniciar.addActionListener(e -> {
            controlador.iniciarProcessoDeCaptura();
        });
        gbcBotoes.gridx = 0; gbcBotoes.gridy = 0;
        painelBotoes.add(btnIniciar, gbcBotoes);

        JButton btnParar = new JButton("Parar Captura");
        btnParar.addActionListener(e -> {
            controlador.pararCaptura(); 
            JOptionPane.showMessageDialog(this, "Captura finalizada.", "Captura Parada", JOptionPane.INFORMATION_MESSAGE);
        });
        gbcBotoes.gridx = 0; gbcBotoes.gridy = 1;
        painelBotoes.add(btnParar, gbcBotoes);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(0, 0, 0, 0);
        add(painelBotoes, gbc);
    }
}