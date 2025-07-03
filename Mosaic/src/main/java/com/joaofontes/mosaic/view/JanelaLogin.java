package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class JanelaLogin extends JFrame {

   private final ControladorPrincipal controlador;
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton btnLogin;
    private JButton btnCriarUsuario;

    public JanelaLogin() {
        this.controlador = new ControladorPrincipal();
        initUI();
    }

    private void initUI() {
        setTitle("MOSAIC - Autenticação");
        setSize(450, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        URL iconURL = getClass().getResource("/images/Logo_Mosaic.jpg"); 
        if (iconURL != null) setIconImage(new ImageIcon(iconURL).getImage());

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; campoEmail = new JTextField(25); painel.add(campoEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; painel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; campoSenha = new JPasswordField(25); painel.add(campoSenha, gbc);

        JLabel linkRecuperar = new JLabel("<html><a href=''>Esqueci a minha senha</a></html>");
        linkRecuperar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        painel.add(linkRecuperar, gbc);
        
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCriarUsuario = new JButton("Criar Conta");
        btnLogin = new JButton("Login");
        painelBotoes.add(btnCriarUsuario);
        painelBotoes.add(btnLogin);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        painel.add(painelBotoes, gbc);

        add(painel);
        adicionarListeners(linkRecuperar);
    }

    private void adicionarListeners(JLabel linkRecuperar) {
        btnLogin.addActionListener(e -> tentarLogin());
        campoSenha.addActionListener(e -> tentarLogin());
        btnCriarUsuario.addActionListener(e -> abrirDialogoCriacao());
        linkRecuperar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoRecuperacao();
            }
        });
    }

    private void tentarLogin() {
        String email = campoEmail.getText().trim();
        String password = new String(campoSenha.getPassword());

        if (controlador.autenticarUsuario(email, password)) {
            JanelaPrincipal.getInstance(controlador).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "E-mail ou senha inválidos.", "Falha na Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirDialogoCriacao() {
        DialogoCriarUsuario dialogo = new DialogoCriarUsuario(this, controlador);
        dialogo.setVisible(true);
    }
    
    private void abrirDialogoRecuperacao() {
        DialogoRecuperarSenha dialogo = new DialogoRecuperarSenha(this, controlador);
        dialogo.setVisible(true);
    }
}