package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.SessaoCaptura; // Import para getSessaoAtual
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class PainelMetadados extends JPanel {
    private ControladorPrincipal controlador;
    private JTextField campoNomePeca;
    private JTextArea areaDescricao;
    private JButton btnSalvarMetadados;

    public PainelMetadados(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nome da Peça:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        campoNomePeca = new JTextField(20);
        add(campoNomePeca, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        areaDescricao = new JTextArea(5, 20);
        areaDescricao.setLineWrap(true);
        areaDescricao.setWrapStyleWord(true);
        JScrollPane scrollDescricao = new JScrollPane(areaDescricao);
        add(scrollDescricao, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; gbc.weighty = 0;
        btnSalvarMetadados = new JButton("Salvar Metadados");
        btnSalvarMetadados.addActionListener(e -> salvarMetadados());
        add(btnSalvarMetadados, gbc);

        carregarMetadados(); 
    }

    private void salvarMetadados() {
        controlador.setMetadadosSessao(
            campoNomePeca.getText(),
            areaDescricao.getText()
        );
        JOptionPane.showMessageDialog(this, 
                                    "Metadados salvos com sucesso!", 
                                    "Salvar Metadados", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }

    private void carregarMetadados() {
        SessaoCaptura sessao = controlador.getSessaoAtual();
        if (sessao != null) { 
            campoNomePeca.setText(sessao.getNomePeca() != null ? sessao.getNomePeca() : "");
            areaDescricao.setText(sessao.getDescricao() != null ? sessao.getDescricao() : "");
        }
    }
    
    public void atualizarCamposMetadados(String nomePeca, String descricao) {
        campoNomePeca.setText(nomePeca);
        areaDescricao.setText(descricao);
    }

    public void limparCamposMetadados() {
        campoNomePeca.setText("");
        areaDescricao.setText("");
    }
}