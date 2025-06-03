/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.joaofontes.mosaic.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author JoãoFontes
 */
public class DialogoExibicaoAuto extends JDialog {
    private int tempoRestante;
    private Timer temporizador;
    private JLabel rotuloTempo;

    public DialogoExibicaoAuto(javax.swing.JFrame parent, BufferedImage imagem, int segundosExibicao) {
        super(parent, "Imagem Mesclada", false);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(parent);
        
        this.tempoRestante = segundosExibicao;
        
        // Configurar imagem
        JLabel rotuloImagem = new JLabel(new ImageIcon(imagem));
        JScrollPane scrollPane = new JScrollPane(rotuloImagem);
        add(scrollPane, BorderLayout.CENTER);
        
        // Painel de controle
        JPanel painelControle = new JPanel(new FlowLayout());
        rotuloTempo = new JLabel("Fechando em: " + tempoRestante + "s");
        JButton btnFechar = new JButton("Fechar Agora");
        JButton btnSalvar = new JButton("Salvar Imagem");
        
        painelControle.add(rotuloTempo);
        painelControle.add(btnFechar);
        painelControle.add(btnSalvar);
        add(painelControle, BorderLayout.SOUTH);
        
        // Configurar temporizador
        temporizador = new Timer(1000, (ActionEvent e) -> {
            tempoRestante--;
            rotuloTempo.setText("Fechando em: " + tempoRestante + "s");
            
            if (tempoRestante <= 0) {
                temporizador.stop();
                fecharDialogo();
            }
        });
        
        // Configurar ações dos botões
        btnFechar.addActionListener(e -> fecharDialogo());
        btnSalvar.addActionListener(e -> salvarImagem(imagem));
    }
    
    public void exibir() {
    // Mova a inicialização do temporizador para aqui
    temporizador = new Timer(1000, (ActionEvent e) -> {
        tempoRestante--;
        rotuloTempo.setText("Fechando em: " + tempoRestante + "s");
        
        if (tempoRestante <= 0) {
            fecharDialogo();
        }
    });
    temporizador.start(); // Inicie o temporizador aqui
    setVisible(true);
}
    
    private void fecharDialogo() {
        temporizador.stop();
        dispose();
    }
    
    private void salvarImagem(BufferedImage imagem) {
        // Implementar lógica de salvamento
    }
}
