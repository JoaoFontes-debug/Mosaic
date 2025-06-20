package com.joaofontes.mosaic.view;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class DialogoExibicaoAuto extends JDialog {
    private int tempoRestante; 
    private Timer temporizadorFechamento;
    private final Runnable onDialogCloseAction; 

    public DialogoExibicaoAuto(JFrame parent, BufferedImage imagem, int segundosExibicao, Runnable onDialogCloseAction) {
        super(parent, "Imagem Mesclada", false); 
        this.tempoRestante = segundosExibicao;
        this.onDialogCloseAction = onDialogCloseAction;
        initUI(imagem);

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

    private void initUI(BufferedImage imagemParaExibir) {
        setLayout(new BorderLayout());

        JLabel labelImagem = new JLabel(new ImageIcon(imagemParaExibir));
        labelImagem.setHorizontalAlignment(SwingConstants.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(labelImagem);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JLabel labelTempo = new JLabel("", SwingConstants.CENTER);
        add(labelTempo, BorderLayout.SOUTH);

        if (tempoRestante > 0) {
            labelTempo.setText("Fechando em " + tempoRestante + "s");
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
        } else {
            labelTempo.setText("Imagem Mesclada (feche manualmente)");
        }
        
        // CORREÇÃO: Maximiza o JDialog manualmente para preencher o ecrã.
        Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, tamanhoTela.width, tamanhoTela.height);
    }

    public void exibir() {
        setVisible(true);
    }
}