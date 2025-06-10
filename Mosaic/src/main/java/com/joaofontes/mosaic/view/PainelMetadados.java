package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;

public class PainelMetadados extends JPanel {

    private final ControladorPrincipal controlador;
    private JTextField campoNomePeca;
    private JTextArea areaDescricao;
    private JButton btnIniciarInspecao;

    public PainelMetadados(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Aumenta a margem
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Aumenta o espaçamento
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 1: Nome da Peça
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // O label não estica
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Nome da Peça/Inspeção:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // O campo de texto estica
        gbc.fill = GridBagConstraints.HORIZONTAL;
        campoNomePeca = new JTextField(30); // Tamanho aumentado
        add(campoNomePeca, gbc);

        // Linha 2: Descrição
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Alinha o label ao topo
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Descrição/Observações:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Área de texto ocupa todo o espaço vertical disponível
        gbc.fill = GridBagConstraints.BOTH;
        areaDescricao = new JTextArea(5, 30);
        areaDescricao.setLineWrap(true);
        areaDescricao.setWrapStyleWord(true);
        add(new JScrollPane(areaDescricao), gbc);

        // Linha 3: Botão
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0; // Botão não estica
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTHEAST; // Alinha o botão ao canto inferior direito
        btnIniciarInspecao = new JButton("Iniciar Nova Inspeção");
        btnIniciarInspecao.setToolTipText("Define os dados para uma nova série de mesclagens.");
        btnIniciarInspecao.addActionListener(e -> iniciarNovaInspecao());
        add(btnIniciarInspecao, gbc);
    }

    private void iniciarNovaInspecao() {
        controlador.iniciarNovaInspecao(
                campoNomePeca.getText(),
                areaDescricao.getText()
        );
    }
}
