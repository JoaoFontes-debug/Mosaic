package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class PainelControleFlutuante extends JWindow {

    private final ControladorPrincipal controlador;
    private final JLabel labelStatus;
    
    // ALTERAÇÃO: Variáveis para permitir que a janela seja arrastada
    private Point pontoInicialClique;

    public PainelControleFlutuante(Frame owner, ControladorPrincipal controlador) {
        super(owner);
        this.controlador = controlador;
        this.labelStatus = new JLabel("Aguardando captura...");
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new LineBorder(Color.GRAY, 1));
        mainPanel.setBackground(new Color(240, 240, 240));

        labelStatus.setBorder(new EmptyBorder(5, 10, 5, 10));
        mainPanel.add(labelStatus, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        JButton btnParar = new JButton("Parar");
        btnParar.setMargin(new Insets(5, 15, 5, 15));
        btnParar.setFocusable(false);
        btnParar.addActionListener(e -> controlador.pararCaptura());
        buttonPanel.add(btnParar);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        getContentPane().add(mainPanel);
        pack();

        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        int x = screenBounds.width - getWidth() - 20;
        int y = screenBounds.height - getHeight() - 50;
        setLocation(x, y);

        setAlwaysOnTop(true);

        // ALTERAÇÃO: Adiciona listeners para tornar a janela arrastável
        MouseAdapter dragListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pontoInicialClique = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - pontoInicialClique.x;
                int yMoved = e.getY() - pontoInicialClique.y;

                int newX = thisX + xMoved;
                int newY = thisY + yMoved;

                setLocation(newX, newY);
            }
        };
        
        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
    }

    public void setStatus(String texto) {
        SwingUtilities.invokeLater(() -> {
            labelStatus.setText(texto);
            pack();
        });
    }

    public void setVisivel(boolean visivel) {
        SwingUtilities.invokeLater(() -> setVisible(visivel));
    }
}