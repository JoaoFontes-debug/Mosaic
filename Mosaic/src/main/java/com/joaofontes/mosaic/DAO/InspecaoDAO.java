package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.Inspecao;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InspecaoDAO {
    private final Connection conexao;
    private final SimpleDateFormat sqliteTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public InspecaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void salvarInspecao(Inspecao inspecao) throws SQLException {
        String sql = "INSERT INTO inspecoes (nome_peca, descricao, data_criacao) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, inspecao.getNomePeca());
            stmt.setString(2, inspecao.getDescricao());
            
            // ALTERAÇÃO CRÍTICA: Formata a data para String antes de salvar.
            stmt.setString(3, sqliteTimestampFormat.format(inspecao.getDataCriacao()));
            
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) inspecao.setId(generatedKeys.getInt(1));
            }
        }
    }

    public void deletarPorId(int idInspecao) throws SQLException {
        String sql = "DELETE FROM inspecoes WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idInspecao);
            stmt.executeUpdate();
        }
    }

    public List<Inspecao> buscarInspecoes(String nomePeca, java.util.Date startDate, java.util.Date endDate) throws SQLException {
        List<Inspecao> inspecoes = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM inspecoes WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nomePeca != null && !nomePeca.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(nome_peca) LIKE LOWER(?)");
            params.add("%" + nomePeca.trim() + "%");
        }
        
        if (startDate != null) {
            sqlBuilder.append(" AND data_criacao >= ?");
            params.add(sqliteTimestampFormat.format(startDate));
        }
        if (endDate != null) {
            long endTime = endDate.getTime() + (24L * 60L * 60L * 1000L - 1L);
            sqlBuilder.append(" AND data_criacao <= ?");
            params.add(sqliteTimestampFormat.format(new java.util.Date(endTime)));
        }
        
        sqlBuilder.append(" ORDER BY data_criacao DESC");
        
        try (PreparedStatement stmt = conexao.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Inspecao inspecao = new Inspecao();
                inspecao.setId(rs.getInt("id"));
                inspecao.setNomePeca(rs.getString("nome_peca"));
                inspecao.setDescricao(rs.getString("descricao"));
                
                String dataString = rs.getString("data_criacao");
                if (dataString != null) {
                    try {
                        inspecao.setDataCriacao(sqliteTimestampFormat.parse(dataString));
                    } catch (ParseException e) {
                        System.err.println("Erro ao fazer parse da data da inspeção: " + dataString);
                        e.printStackTrace();
                        inspecao.setDataCriacao(null);
                    }
                }
                
                inspecoes.add(inspecao);
            }
        }
        return inspecoes;
    }
}