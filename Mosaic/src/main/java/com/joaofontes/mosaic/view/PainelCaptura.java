package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class PainelCaptura extends JPanel {
    private ControladorPrincipal controlador;
    private JButton btnIniciar;
    private JButton btnParar;

    public PainelCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        btnIniciar = new JButton("Iniciar Captura");
        btnIniciar.addActionListener(e -> {
            boolean areaSelecionada = controlador.selecionarAreaCaptura();
            if (areaSelecionada) {
                controlador.iniciarCaptura();
                JOptionPane.showMessageDialog(this, 
                                            "Captura iniciada.\nMonitorando a área selecionada.", 
                                            "Captura Iniciada", 
                                            JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, 
                                            "Seleção de área cancelada ou falhou.\nA captura não foi iniciada.", 
                                            "Captura Não Iniciada", 
                                            JOptionPane.WARNING_MESSAGE);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; 
        add(btnIniciar, gbc);

        btnParar = new JButton("Parar Captura");
        btnParar.addActionListener(e -> {
            controlador.pararCaptura();
            JOptionPane.showMessageDialog(this, 
                                        "Captura parada.", 
                                        "Captura Parada", 
                                        JOptionPane.INFORMATION_MESSAGE);
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(btnParar, gbc);
    }
}
