
package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.SessaoCaptura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author aluno.lauro
 */
public class SessaoDAO {
    
      private final Connection conexao;

    public SessaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void salvarSessao(SessaoCaptura sessao) throws SQLException {
        String sql = "INSERT INTO sessoes (nome_peca, descricao, data_captura, caminho_imagem) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, sessao.getNomePeca());
            stmt.setString(2, sessao.getDescricao());
            stmt.setTimestamp(3, new java.sql.Timestamp(sessao.getDataCaptura().getTime()));
            stmt.setString(4, sessao.getCaminhoImagem());
            stmt.executeUpdate();
        }
    }
}
