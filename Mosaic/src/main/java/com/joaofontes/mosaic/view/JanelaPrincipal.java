package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class JanelaPrincipal extends JFrame {

    private static JanelaPrincipal instancia;
    private final ControladorPrincipal controlador;

    private JanelaPrincipal(ControladorPrincipal controlador) {
        this.controlador = controlador;
        this.controlador.setJanelaPrincipal(this);
        initUI();
    }

    public static JanelaPrincipal getInstance(ControladorPrincipal controlador) {
        if (instancia == null) {
            instancia = new JanelaPrincipal(controlador);
        }
        return instancia;
    }

    private void initUI() {
        configurarJanela();
        JTabbedPane abas = new JTabbedPane();

        abas.addTab("Captura", new PainelCaptura(controlador));
        abas.addTab("Metadados da Inspeção", new PainelMetadados(controlador));
        abas.addTab("Configurações", new PainelConfiguracao(controlador));
        abas.addTab("Inspeções Salvas", new PainelInspecoes(controlador));

        if (controlador.isUsuarioAdmin()) {
            abas.addTab("Administração", new PainelAdmin(controlador));
            System.out.println("Utilizador admin detetado. A carregar painel de administração.");
        }

        add(abas, BorderLayout.CENTER);
    }

    private void configurarJanela() {
        setTitle("Sistema MOSAIC");
        setSize(950, 700);
        setMinimumSize(new Dimension(950, 700));
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        URL iconURL = getClass().getResource("/images/Logo_Mosaic.jpg");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }
    }

    public void exibirImagemMesclada(BufferedImage imagem) {
        BufferedImage imagemParaExibir = redimensionarImagemParaTela(imagem);
        Runnable onDialogCloseAction = () -> {
            System.out.println("Janela de exibição fechada. Notificando controlador para reiniciar a captura com atraso.");
            controlador.iniciarContagemParaReiniciar();
        };
        DialogoExibicaoAuto dialogo = new DialogoExibicaoAuto(this, imagemParaExibir, controlador.getConfiguracao().getTempoFechamentoAuto(), onDialogCloseAction);
        dialogo.exibir();
    }

    private BufferedImage redimensionarImagemParaTela(BufferedImage imagemOriginal) {
        Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
        int larguraMaxima = (int) (tamanhoTela.width * 0.9);
        int alturaMaxima = (int) (tamanhoTela.height * 0.9);
        if (imagemOriginal.getWidth() <= larguraMaxima && imagemOriginal.getHeight() <= alturaMaxima) {
            return imagemOriginal;
        }
        double ratio = Math.min((double) larguraMaxima / imagemOriginal.getWidth(), (double) alturaMaxima / imagemOriginal.getHeight());
        int novaLargura = (int) (imagemOriginal.getWidth() * ratio);
        int novaAltura = (int) (imagemOriginal.getHeight() * ratio);
        Image imagemRedimensionada = imagemOriginal.getScaledInstance(novaLargura, novaAltura, Image.SCALE_SMOOTH);
        BufferedImage novaImagemBuffer = new BufferedImage(novaLargura, novaAltura, imagemOriginal.getType());
        Graphics2D g2d = novaImagemBuffer.createGraphics();
        g2d.drawImage(imagemRedimensionada, 0, 0, null);
        g2d.dispose();
        return novaImagemBuffer;
    }
}
