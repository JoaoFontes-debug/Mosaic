package com.joaofontes.mosaic.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class SeletorAreaCaptura extends JDialog {
    private Rectangle areaSelecionada;
    private Point pontoInicial;
    private Point pontoFinalAtual; // Renomeado para clareza
    private BufferedImage capturaTelaCheia;
    private JPanel painelSelecao;
    private static final Color COR_SELECAO_FUNDO = new Color(0, 100, 255, 50); // Azul semi-transparente
    private static final Color COR_SELECAO_BORDA = Color.BLUE;
    private static final Font FONTE_INSTRUCAO = new Font("Arial", Font.BOLD, 16);
    private static final Color COR_INSTRUCAO = Color.WHITE;
    private static final Color COR_SOMBRA_INSTRUCAO = Color.BLACK;


    public SeletorAreaCaptura(JFrame parent) {
        super(parent, "Selecione a Área de Captura", true); 
        setUndecorated(true); 
        // Define o JDialog para ser translúcido se suportado, para melhor efeito de overlay
        try {
            if (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
                setBackground(new Color(0, 0, 0, 1)); // Quase totalmente transparente, mas não zero para evitar problemas
                setOpacity(0.9f); // Um pouco de opacidade para o overlay geral, se desejar
            } else {
                 setBackground(new Color(0,0,0,70)); // Fallback para um cinza escuro semi-transparente
            }
        } catch (Exception e) {
            setBackground(new Color(0,0,0,70)); // Fallback em caso de erro
            System.err.println("Translucidez não suportada ou erro ao aplicar: " + e.getMessage());
        }
        
        try {
            Robot robot = new Robot();
            // Captura a tela inteira (ou todas as telas, se houver múltiplas)
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            // Para múltiplas telas, é preciso iterar sobre GraphicsEnvironment.getScreenDevices()
            // e criar um retângulo que englobe todas. Por simplicidade, usando a tela principal.
            capturaTelaCheia = robot.createScreenCapture(screenRect);
            setBounds(screenRect); // Faz o diálogo cobrir a tela inteira
        } catch (AWTException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Erro ao capturar a tela para seleção: " + e.getMessage(), "Erro de Captura", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        painelSelecao = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Desenha a captura de tela como fundo do painel
                if (capturaTelaCheia != null) {
                    g.drawImage(capturaTelaCheia, 0, 0, this.getWidth(), this.getHeight(), this);
                }
                
                // Desenha o retângulo de seleção dinâmico
                if (pontoInicial != null && pontoFinalAtual != null) {
                    Graphics2D g2d = (Graphics2D) g.create(); // Cria uma cópia para não afetar outras renderizações
                    
                    Rectangle r = calcularRetanguloSelecao(pontoInicial, pontoFinalAtual);
                    g2d.setColor(COR_SELECAO_FUNDO); 
                    g2d.fillRect(r.x, r.y, r.width, r.height);
                    g2d.setColor(COR_SELECAO_BORDA); 
                    g2d.setStroke(new BasicStroke(1)); // Borda fina
                    g2d.drawRect(r.x, r.y, r.width, r.height);
                    
                    // Mostra as dimensões da seleção
                    String dimText = r.width + "x" + r.height;
                    g2d.setFont(FONTE_INSTRUCAO.deriveFont(12f));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(dimText);
                    // Posição do texto de dimensão (canto inferior direito da seleção)
                    int textX = r.x + r.width - textWidth - 5;
                    int textY = r.y + r.height - 5;
                    if (textY < r.y + fm.getAscent() + 5) textY = r.y + fm.getAscent() +5; // Evita sair pra cima
                    if (textX < r.x + 5) textX = r.x + 5; // Evita sair pra esquerda

                    g2d.setColor(COR_SOMBRA_INSTRUCAO);
                    g2d.drawString(dimText, textX + 1, textY + 1);
                    g2d.setColor(COR_INSTRUCAO);
                    g2d.drawString(dimText, textX, textY);

                    g2d.dispose();
                }
                 // Adiciona uma instrução na tela
                Graphics2D g2dText = (Graphics2D) g.create();
                g2dText.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2dText.setFont(FONTE_INSTRUCAO);
                String instrucao = "Clique e arraste para selecionar a área. Pressione ESC para cancelar.";
                FontMetrics fmText = g2dText.getFontMetrics();
                int strWidth = fmText.stringWidth(instrucao);
                int xText = (getWidth() - strWidth) / 2; // Centralizado
                int yText = getHeight() - fmText.getHeight() - 20; // Perto da base
                
                g2dText.setColor(COR_SOMBRA_INSTRUCAO);
                g2dText.drawString(instrucao, xText + 1, yText + 1);
                g2dText.setColor(COR_INSTRUCAO);
                g2dText.drawString(instrucao, xText, yText);
                g2dText.dispose();
            }
        };
        
        // O painel deve ser opaco=false para que o JDialog translúcido funcione corretamente
        // se o JDialog tiver alguma cor de fundo (mesmo que quase transparente).
        // Se o JDialog é setBackground(new Color(0,0,0,0)), o painel pode ser opaco.
        painelSelecao.setOpaque(false); 
        setContentPane(painelSelecao); 
        
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pontoInicial = e.getPoint();
                pontoFinalAtual = pontoInicial; 
                painelSelecao.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                pontoFinalAtual = e.getPoint();
                painelSelecao.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (pontoInicial != null && pontoFinalAtual != null) {
                    areaSelecionada = calcularRetanguloSelecao(pontoInicial, pontoFinalAtual);
                    if (areaSelecionada.width < 10 || areaSelecionada.height < 10) { 
                        areaSelecionada = null; // Invalida seleção pequena
                         JOptionPane.showMessageDialog(SeletorAreaCaptura.this, 
                                                    "Área selecionada é muito pequena (mínimo 10x10 pixels). Tente novamente.", 
                                                    "Seleção Inválida", JOptionPane.WARNING_MESSAGE);
                        pontoInicial = null; 
                        pontoFinalAtual = null;
                        painelSelecao.repaint(); // Limpa o retângulo da tela
                    } else {
                        dispose(); 
                    }
                }
            }
        };

        painelSelecao.addMouseListener(mouseAdapter);
        painelSelecao.addMouseMotionListener(mouseAdapter);

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                areaSelecionada = null;
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE_ACTION");
        getRootPane().getActionMap().put("ESCAPE_ACTION", escapeAction);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    private Rectangle calcularRetanguloSelecao(Point p1, Point p2) {
        if (p1 == null || p2 == null) return new Rectangle();
        int x = Math.min(p1.x, p2.x);
        int y = Math.min(p1.y, p2.y);
        int width = Math.abs(p1.x - p2.x);
        int height = Math.abs(p1.y - p2.y);
        return new Rectangle(x, y, width, height);
    }

    public Rectangle getAreaSelecionada() {
        // Torna o diálogo visível. Como é modal, esta chamada bloqueará 
        // até que o diálogo seja fechado (dispose() é chamado).
        setVisible(true); 
        return areaSelecionada; // Retorna a área que foi definida antes do dispose()
    }
}
