package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

  private final Connection conexao;

    public UsuarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public int contarUsuarios() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void salvarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome_completo, email, password_hash, nivel_acesso) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPasswordHash());
            stmt.setString(4, usuario.getNivelAcesso());
            stmt.executeUpdate();
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuarioDoResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public void salvarTokenRecuperacao(String email, String token, String expiryDate) throws SQLException {
        String sql = "UPDATE usuarios SET reset_token = ?, token_expires_at = ? WHERE email = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setString(2, expiryDate);
            stmt.setString(3, email);
            stmt.executeUpdate();
        }
    }

    public Usuario buscarPorTokenRecuperacao(String token) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE reset_token = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuarioDoResultSet(rs);
                }
            }
        }
        return null;
    }

    public void atualizarSenhaEInvalidarToken(int userId, String novoPasswordHash) throws SQLException {
        String sql = "UPDATE usuarios SET password_hash = ?, reset_token = NULL, token_expires_at = NULL WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novoPasswordHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public List<Usuario> buscarTodosUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome_completo, email, nivel_acesso FROM usuarios ORDER BY nome_completo";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Usuario user = new Usuario();
                user.setId(rs.getInt("id"));
                user.setNomeCompleto(rs.getString("nome_completo"));
                user.setEmail(rs.getString("email"));
                user.setNivelAcesso(rs.getString("nivel_acesso"));
                usuarios.add(user);
            }
        }
        return usuarios;
    }

    public void removerUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarNivelAcesso(int id, String novoNivel) throws SQLException {
        String sql = "UPDATE usuarios SET nivel_acesso = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novoNivel);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void resetarSenha(int id, String novoPasswordHash) throws SQLException {
        String sql = "UPDATE usuarios SET password_hash = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novoPasswordHash);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    private Usuario extrairUsuarioDoResultSet(ResultSet rs) throws SQLException {
        Usuario user = new Usuario();
        user.setId(rs.getInt("id"));
        user.setNomeCompleto(rs.getString("nome_completo"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setNivelAcesso(rs.getString("nivel_acesso"));
        user.setResetToken(rs.getString("reset_token"));
        user.setTokenExpiresAt(rs.getString("token_expires_at"));
        return user;
    }
}
