
package com.joaofontes.mosaic.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

/**
 *
 * @author aluno.lauro
 */
public class DialogoExibicaoAuto extends JDialog {
    
     private int tempoRestante;
    private Timer temporizador;
    private JLabel rotuloTempo;
    private final BufferedImage imagem;

    public DialogoExibicaoAuto(javax.swing.JFrame parent, BufferedImage imagem, int segundosExibicao) {
        super(parent, "Imagem Mesclada", false);
        this.imagem = imagem;
        this.tempoRestante = segundosExibicao;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        
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
                fecharDialogo();
            }
        });
        
        // Configurar ações dos botões
        btnFechar.addActionListener(e -> fecharDialogo());
        btnSalvar.addActionListener(e -> salvarImagem());
    }
    
    private void fecharDialogo() {
        temporizador.stop();
        dispose();
    }
    
    private void salvarImagem() {
        JFileChooser seletor = new JFileChooser();
        seletor.setSelectedFile(new File("mesclagem_" + System.currentTimeMillis() + ".png"));
        
        if (seletor.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(imagem, "PNG", seletor.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void exibir() {
        temporizador.start();
        setVisible(true);
    }
}
