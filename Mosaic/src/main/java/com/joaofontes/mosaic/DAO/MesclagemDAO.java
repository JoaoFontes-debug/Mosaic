package com.joaofontes.mosaic.DAO;

/**
 *
 * @author JoãoFontes
 */
import com.joaofontes.mosaic.model.Mesclagem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesclagemDAO {

    private final Connection conexao;

    public MesclagemDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void salvarMesclagem(Mesclagem mesclagem) throws SQLException {
        String sql = "INSERT INTO mesclagens (id_inspecao, data_captura, caminho_imagem, caminho_local) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mesclagem.getIdInspecao());
            stmt.setTimestamp(2, new Timestamp(mesclagem.getDataCaptura().getTime()));
            stmt.setString(3, mesclagem.getCaminhoImagem());
            stmt.setString(4, mesclagem.getCaminhoLocal());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mesclagem.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Mesclagem> buscarMesclagensPorInspecaoId(int idInspecao) throws SQLException {
        List<Mesclagem> mesclagens = new ArrayList<>();
        String sql = "SELECT * FROM mesclagens WHERE id_inspecao = ? ORDER BY data_captura ASC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idInspecao);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Mesclagem mesclagem = new Mesclagem();
                mesclagem.setId(rs.getInt("id"));
                mesclagem.setIdInspecao(rs.getInt("id_inspecao"));
                mesclagem.setDataCaptura(rs.getTimestamp("data_captura"));
                mesclagem.setCaminhoImagem(rs.getString("caminho_imagem"));
                mesclagem.setCaminhoLocal(rs.getString("caminho_local"));
                mesclagens.add(mesclagem);
            }
        }
        return mesclagens;
    }
}
