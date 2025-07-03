package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelAdmin extends JPanel {
    private final ControladorPrincipal controlador;
    private JTable tabelaUsuarios;
    private DefaultTableModel modeloTabela;

    public PainelAdmin(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
        carregarUsuarios();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID", "Nome Completo", "E-mail", "Nível de Acesso"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaUsuarios = new JTable(modeloTabela);
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Gestão de Utilizadores"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtualizar = new JButton("Atualizar Lista");
        JButton btnPromover = new JButton("Promover/Rebaixar");
        JButton btnResetarSenha = new JButton("Resetar Senha");
        JButton btnRemover = new JButton("Remover Utilizador");

        btnAtualizar.addActionListener(e -> carregarUsuarios());
        btnRemover.addActionListener(e -> removerUsuarioSelecionado());
        btnPromover.addActionListener(e -> alterarNivelUsuarioSelecionado());
        btnResetarSenha.addActionListener(e -> resetarSenhaUsuarioSelecionado());

        painelAcoes.add(btnAtualizar);
        painelAcoes.add(btnPromover);
        painelAcoes.add(btnResetarSenha);
        painelAcoes.add(btnRemover);
        add(painelAcoes, BorderLayout.SOUTH);
    }

    private void carregarUsuarios() {
        try {
            List<Usuario> usuarios = controlador.carregarTodosUsuarios();
            modeloTabela.setRowCount(0);
            for (Usuario user : usuarios) {
                modeloTabela.addRow(new Object[]{user.getId(), user.getNomeCompleto(), user.getEmail(), user.getNivelAcesso()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar utilizadores: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Usuario getUsuarioSelecionado() {
        int linhaSelecionada = tabelaUsuarios.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um utilizador na tabela primeiro.", "Nenhuma Seleção", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int id = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        String nome = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        String email = (String) modeloTabela.getValueAt(linhaSelecionada, 2);
        String nivel = (String) modeloTabela.getValueAt(linhaSelecionada, 3);
        
        Usuario user = new Usuario();
        user.setId(id);
        user.setNomeCompleto(nome);
        user.setEmail(email);
        user.setNivelAcesso(nivel);
        return user;
    }

    private void removerUsuarioSelecionado() {
        Usuario user = getUsuarioSelecionado();
        if (user == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem a certeza que deseja remover o utilizador '" + user.getNomeCompleto() + "'?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controlador.removerUsuario(user.getId());
                JOptionPane.showMessageDialog(this, "Utilizador removido com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarUsuarios();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao remover utilizador: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void alterarNivelUsuarioSelecionado() {
        Usuario user = getUsuarioSelecionado();
        if (user == null) return;

        String novoNivel = user.getNivelAcesso().equals("ADMIN") ? "OPERADOR" : "ADMIN";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja alterar o nível do utilizador '" + user.getNomeCompleto() + "' para " + novoNivel + "?",
                "Confirmar Alteração de Nível", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controlador.atualizarNivelAcesso(user.getId(), novoNivel);
                JOptionPane.showMessageDialog(this, "Nível de acesso atualizado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarUsuarios();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao alterar nível de acesso: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetarSenhaUsuarioSelecionado() {
        Usuario user = getUsuarioSelecionado();
        if (user == null) return;

        String novaSenha = JOptionPane.showInputDialog(this, "Digite a nova senha para o utilizador '" + user.getNomeCompleto() + "':", "Resetar Senha", JOptionPane.PLAIN_MESSAGE);
        
        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            if (novaSenha.trim().length() < 6) {
                JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.", "Senha Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                controlador.resetarSenha(user.getId(), novaSenha);
                JOptionPane.showMessageDialog(this, "Senha do utilizador '" + user.getNomeCompleto() + "' foi alterada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao resetar a senha: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}