package com.joaofontes.mosaic.view;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class DialogoExibicaoAuto extends JDialog {
    // private BufferedImage imagem; // Removido, pois o JLabel já a contém
    private int tempoRestante; 
    private Timer temporizadorFechamento;
    private JLabel labelTempo;
    private Runnable onDialogCloseAction; 

    public DialogoExibicaoAuto(JFrame parent, BufferedImage imagem, int segundosExibicao, Runnable onDialogCloseAction) {
        super(parent, "Imagem Mesclada", false); 
        // this.imagem = imagem; // Removido
        this.tempoRestante = segundosExibicao;
        this.onDialogCloseAction = onDialogCloseAction;
        initUI(imagem); // Passa a imagem para initUI

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (temporizadorFechamento != null && temporizadorFechamento.isRunning()) {
                    temporizadorFechamento.stop();
                }
                if (onDialogCloseAction != null) {
                    onDialogCloseAction.run();
                }
            }
        });
    }

    private void initUI(BufferedImage imagemParaExibir) { // Recebe a imagem
        setLayout(new BorderLayout());

        JLabel labelImagem = new JLabel(new ImageIcon(imagemParaExibir));
        labelImagem.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelImagem, BorderLayout.CENTER);

        labelTempo = new JLabel("Fechando em " + tempoRestante + "s", SwingConstants.CENTER);
        add(labelTempo, BorderLayout.SOUTH);

        if (tempoRestante > 0) {
            temporizadorFechamento = new Timer(1000, e -> {
                tempoRestante--;
                if (tempoRestante >= 0) {
                    labelTempo.setText("Fechando em " + tempoRestante + "s");
                } else {
                    ((Timer) e.getSource()).stop();
                    dispose(); 
                }
            });
            temporizadorFechamento.start();
        } else { // Se tempoRestante for 0 ou menos, não inicia o timer e permite fechar manualmente
            labelTempo.setText("Imagem Mesclada (feche manualmente)");
        }
        
        // Ajusta o tamanho do diálogo ao conteúdo
        pack(); 
        // Garante um tamanho mínimo ou preferido
        int prefWidth = Math.max(300, imagemParaExibir.getWidth() + 40); // Adiciona um pouco de padding
        int prefHeight = Math.max(200, imagemParaExibir.getHeight() + 70); // Espaço para o contador e padding
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        pack(); 
        setLocationRelativeTo(getParent()); 
    }

    public void exibir() {
        setVisible(true);
    }
}
