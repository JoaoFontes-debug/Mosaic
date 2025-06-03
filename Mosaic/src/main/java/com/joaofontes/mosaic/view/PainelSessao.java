
package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author aluno.lauro
 */
public class PainelSessao extends JPanel {
    
     private final ControladorPrincipal controlador;
    private JTable tabelaSessoes;

    public PainelSessao(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new java.awt.BorderLayout());
        
        // Modelo da tabela
        DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"ID", "Nome da Peça", "Data", "Imagem"}, 0
        );
        
        tabelaSessoes = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaSessoes);
        add(scrollPane, java.awt.BorderLayout.CENTER);
        
        // Botão para carregar sessões
        JButton btnCarregar = new JButton("Carregar Sessões");
        btnCarregar.addActionListener(e -> carregarSessoes());
        add(btnCarregar, java.awt.BorderLayout.SOUTH);
    }

    private void carregarSessoes() {
        // Implementar carregamento de sessões do banco
    }
}
