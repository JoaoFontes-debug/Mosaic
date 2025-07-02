package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.Mesclagem;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MesclagemDAO {
    private final Connection conexao;
    private final SimpleDateFormat sqliteTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MesclagemDAO(Connection conexao) {
        this.conexao = conexao;
    }
    
    public void salvarMesclagem(Mesclagem mesclagem) throws SQLException {
        String sql = "INSERT INTO mesclagens (id_inspecao, data_captura, caminho_imagem, caminho_local) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mesclagem.getIdInspecao());
            
            // ALTERAÇÃO CRÍTICA: Formata a data para String antes de salvar.
            stmt.setString(2, sqliteTimestampFormat.format(mesclagem.getDataCaptura()));
            
            stmt.setString(3, mesclagem.getCaminhoImagem());
            stmt.setString(4, mesclagem.getCaminhoLocal());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) mesclagem.setId(generatedKeys.getInt(1));
            }
        }
    }

    public void deletarPorId(int idMesclagem) throws SQLException {
        String sql = "DELETE FROM mesclagens WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idMesclagem);
            stmt.executeUpdate();
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
                
                String dataString = rs.getString("data_captura");
                if (dataString != null) {
                    try {
                        mesclagem.setDataCaptura(sqliteTimestampFormat.parse(dataString));
                    } catch (ParseException e) {
                        System.err.println("Erro ao fazer parse da data da mesclagem: " + dataString);
                        e.printStackTrace();
                        mesclagem.setDataCaptura(null);
                    }
                }
                
                mesclagem.setCaminhoImagem(rs.getString("caminho_imagem"));
                mesclagem.setCaminhoLocal(rs.getString("caminho_local"));
                mesclagens.add(mesclagem);
            }
        }
        return mesclagens;
    }
}