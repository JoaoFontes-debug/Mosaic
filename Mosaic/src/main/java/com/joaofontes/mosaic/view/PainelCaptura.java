/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author JoãoFontes
 */
public class PainelCaptura extends JPanel {
    private final ControladorPrincipal controlador;
    private JTextField campoDiretorioCaptura;

    public PainelCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("Captura de Imagens");
        titulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titulo, gbc);

        // Botões de controle
        JButton btnIniciar = new JButton("Iniciar Captura");
        JButton btnParar = new JButton("Parar Captura");
        JButton btnSelecionarArea = new JButton("Selecionar Área de Captura");
        
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(btnIniciar, gbc);
        
        gbc.gridy = 2;
        add(btnParar, gbc);

        // Diretório de captura
        JLabel lblDiretorio = new JLabel("Diretório de Salvamento:");
        campoDiretorioCaptura = new JTextField(30);
        JButton btnSelecionarDir = new JButton("Selecionar");
        
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(lblDiretorio, gbc);
        
        gbc.gridx = 1;
        add(campoDiretorioCaptura, gbc);
        
        gbc.gridx = 2;
        add(btnSelecionarDir, gbc);
       
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        add(btnSelecionarArea, gbc);

        // Ações dos botões
        btnIniciar.addActionListener(e -> controlador.iniciarCaptura());
        btnParar.addActionListener(e -> controlador.pararCaptura());
        btnSelecionarDir.addActionListener(e -> selecionarDiretorio());
        btnSelecionarArea.addActionListener(e -> controlador.selecionarAreaCaptura());
    }

    private void selecionarDiretorio() {
        javax.swing.JFileChooser seletor = new javax.swing.JFileChooser();
        seletor.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        if (seletor.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            campoDiretorioCaptura.setText(seletor.getSelectedFile().getAbsolutePath());
            controlador.setDiretorioCaptura(seletor.getSelectedFile().getAbsolutePath());
        }
    }
}
