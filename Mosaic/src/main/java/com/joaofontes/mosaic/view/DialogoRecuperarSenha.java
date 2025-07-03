package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;

public class DialogoRecuperarSenha extends JDialog {
    private final ControladorPrincipal controlador;
    private CardLayout cardLayout;
    private JPanel painelPrincipal;
    private JTextField campoEmail, campoToken;
    private JPasswordField campoNovaSenha, campoConfirmarNovaSenha;

    public DialogoRecuperarSenha(Frame owner, ControladorPrincipal controlador) {
        super(owner, "Recuperar Senha", true);
        this.controlador = controlador;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        painelPrincipal.add(criarPainelPedirEmail(), "PEDIR_EMAIL");
        painelPrincipal.add(criarPainelResetarSenha(), "RESETAR_SENHA");

        add(painelPrincipal);
        cardLayout.show(painelPrincipal, "PEDIR_EMAIL");
    }

    private JPanel criarPainelPedirEmail() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        painel.add(new JLabel("Digite o seu e-mail para receber um token de recuperação."), BorderLayout.NORTH);
        
        JPanel painelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelCampo.add(new JLabel("E-mail:"));
        campoEmail = new JTextField(25);
        painelCampo.add(campoEmail);
        
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEnviar = new JButton("Enviar Token");
        btnEnviar.addActionListener(e -> enviarToken());
        painelBotao.add(btnEnviar);
        
        painel.add(painelCampo, BorderLayout.CENTER);
        painel.add(painelBotao, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelResetarSenha() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; painel.add(new JLabel("Verifique o seu e-mail e insira o token recebido."), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST; painel.add(new JLabel("Token:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; campoToken = new JTextField(20); painel.add(campoToken, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; painel.add(new JLabel("Nova Senha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; campoNovaSenha = new JPasswordField(20); painel.add(campoNovaSenha, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; painel.add(new JLabel("Confirmar Nova Senha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; campoConfirmarNovaSenha = new JPasswordField(20); painel.add(campoConfirmarNovaSenha, gbc);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnResetar = new JButton("Redefinir Senha");
        btnResetar.addActionListener(e -> resetarSenha());
        painelBotao.add(btnResetar);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; painel.add(painelBotao, gbc);
        return painel;
    }

    private void enviarToken() {
        String email = campoEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite o seu e-mail.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (controlador.iniciarRecuperacaoSenha(email)) {
            JOptionPane.showMessageDialog(this, "Se existir uma conta com este e-mail, um token de recuperação foi enviado.", "Verifique o seu E-mail", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(painelPrincipal, "RESETAR_SENHA");
            pack();
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível enviar o e-mail. Verifique as configurações (mail.properties) e a sua conexão.", "Erro de Envio", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetarSenha() {
        String token = campoToken.getText().trim();
        String novaSenha = new String(campoNovaSenha.getPassword());
        String confirmarSenha = new String(campoConfirmarNovaSenha.getPassword());

        if (token.isEmpty() || novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!novaSenha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
         if (novaSenha.length() < 6) {
            JOptionPane.showMessageDialog(this, "A nova senha deve ter pelo menos 6 caracteres.", "Senha Fraca", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controlador.finalizarRecuperacaoSenha(token, novaSenha)) {
            JOptionPane.showMessageDialog(this, "Senha redefinida com sucesso! Pode agora fazer login com a nova senha.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}