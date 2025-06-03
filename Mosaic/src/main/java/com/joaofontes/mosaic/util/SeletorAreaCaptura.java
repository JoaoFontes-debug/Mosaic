package com.joaofontes.mosaic.util;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JWindow;

/**
 *
 * @author aluno.lauro
 */
public class SeletorAreaCaptura extends JWindow {

    private Point startPoint;
    private Rectangle selectedArea;
    private final ControladorPrincipal controlador;
    private static final AlphaComposite TRANSPARENT = AlphaComposite.SrcOver.derive(0.0f);
    private static final AlphaComposite OPAQUE = AlphaComposite.SrcOver.derive(1.0f);

    public SeletorAreaCaptura(ControladorPrincipal controlador) {
        this.controlador = controlador;
        configurarJanela();
    }

    private void configurarJanela() {
        setBackground(new Color(0, 0, 0, 100)); // Fundo semi-transparente
        setBounds(0, 0, getToolkit().getScreenSize().width, getToolkit().getScreenSize().height);
        setAlwaysOnTop(true);
        adicionarListeners();
    }

    private void adicionarListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                selectedArea = new Rectangle(startPoint.x, startPoint.y, 0, 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedArea != null && selectedArea.width > 0 && selectedArea.height > 0) {
                    controlador.getConfiguracao().setAreaCaptura(selectedArea);
                    System.out.println("Área selecionada: " + selectedArea);
                    dispose();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    int x = Math.min(startPoint.x, e.getX());
                    int y = Math.min(startPoint.y, e.getY());
                    int width = Math.abs(e.getX() - startPoint.x);
                    int height = Math.abs(e.getY() - startPoint.y);
                    selectedArea = new Rectangle(x, y, width, height);
                    repaint();
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Preencher toda a tela com fundo escuro semi-transparente
        g2d.setComposite(OPAQUE);
        g2d.setColor(new Color(0, 0, 0, 150)); // 150/255 = ~60% opacidade
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2. Se tiver área selecionada
        if (selectedArea != null) {
            // 3. Tornar a área selecionada completamente transparente
            g2d.setComposite(TRANSPARENT);
            g2d.setColor(new Color(255, 255, 255, 0));
            g2d.fillRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);

            // 4. Desenhar borda vermelha
            g2d.setComposite(OPAQUE);
            g2d.setColor(Color.RED);
            g2d.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);

            // 5. Mostrar dimensões
            g2d.drawString(
                    selectedArea.width + "x" + selectedArea.height,
                    selectedArea.x + 5,
                    selectedArea.y + 15
            );
        }
    }

    public void exibir() {
        setVisible(true);
    }
}
