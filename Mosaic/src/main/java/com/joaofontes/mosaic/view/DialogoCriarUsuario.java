package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;

public class DialogoCriarUsuario extends JDialog {
    private final ControladorPrincipal controlador;
    private JTextField campoNome, campoEmail;
    private JPasswordField campoSenha, campoConfirmarSenha;

    public DialogoCriarUsuario(Frame owner, ControladorPrincipal controlador) {
        super(owner, "Criar Nova Conta", true);
        this.controlador = controlador;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; painelCampos.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; campoNome = new JTextField(25); painelCampos.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; painelCampos.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; campoEmail = new JTextField(25); painelCampos.add(campoEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; painelCampos.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; campoSenha = new JPasswordField(25); painelCampos.add(campoSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; painelCampos.add(new JLabel("Confirmar Senha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; campoConfirmarSenha = new JPasswordField(25); painelCampos.add(campoConfirmarSenha, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCriar = new JButton("Criar Conta");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnCriar.addActionListener(e -> criarConta());
        btnCancelar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnCriar);
        
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void criarConta() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        String confirmarSenha = new String(campoConfirmarSenha.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!senha.equals(confirmarSenha)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.", "Senha Fraca", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controlador.criarUsuario(nome, email, senha)) {
            JOptionPane.showMessageDialog(this, "Conta criada com sucesso! Pode agora fazer login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}