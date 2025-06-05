package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.SessaoCaptura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;

public class SessaoDAO {
    private final Connection conexao;

    public SessaoDAO(Connection conexao) {
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão com o banco de dados não pode ser nula para SessaoDAO.");
        }
        this.conexao = conexao;
    }

    public void salvarSessao(SessaoCaptura sessao) throws SQLException {
        String sql = "INSERT INTO sessoes (nome_peca, descricao, data_captura, caminho_imagem, caminho_local) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, sessao.getNomePeca());
            stmt.setString(2, sessao.getDescricao());
            stmt.setTimestamp(3, new Timestamp(sessao.getDataCaptura().getTime())); 
            stmt.setString(4, sessao.getCaminhoImagem()); 
            stmt.setString(5, sessao.getCaminhoLocal());  
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sessao.setId(generatedKeys.getInt(1));
                } else {
                    System.err.println("SessaoDAO: Falha ao obter o ID gerado para a sessão salva.");
                }
            }
        } catch (SQLException e) {
            System.err.println("SessaoDAO: Erro SQL ao salvar sessão: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-lança para tratamento superior se necessário
        }
    }

    public List<SessaoCaptura> buscarSessoes(String nomePeca, Date startDate, Date endDate) throws SQLException {
        List<SessaoCaptura> sessoes = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT id, nome_peca, descricao, data_captura, caminho_imagem, caminho_local FROM sessoes WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nomePeca != null && !nomePeca.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(nome_peca) LIKE LOWER(?)"); 
            params.add("%" + nomePeca.trim() + "%");
        }
        if (startDate != null) {
            sqlBuilder.append(" AND data_captura >= ?");
            params.add(new Timestamp(startDate.getTime()));
        }
        if (endDate != null) {
            long endTime = endDate.getTime() + (24L * 60L * 60L * 1000L - 1L); 
            sqlBuilder.append(" AND data_captura <= ?");
            params.add(new Timestamp(endTime));
        }
        sqlBuilder.append(" ORDER BY data_captura DESC");

        System.out.println("SessaoDAO - SQL Executado: " + sqlBuilder.toString()); 

        try (PreparedStatement stmt = conexao.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SessaoCaptura sessao = new SessaoCaptura();
                sessao.setId(rs.getInt("id"));
                sessao.setNomePeca(rs.getString("nome_peca"));
                sessao.setDescricao(rs.getString("descricao"));
                sessao.setDataCaptura(rs.getTimestamp("data_captura"));
                sessao.setCaminhoImagem(rs.getString("caminho_imagem"));
                sessao.setCaminhoLocal(rs.getString("caminho_local"));
                sessoes.add(sessao);
            }
        } catch (SQLException e) {
            System.err.println("SessaoDAO: Erro SQL ao buscar sessoes: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return sessoes;
    }

    // buscarTodasSessoes não é mais necessário explicitamente se buscarSessoes(null,null,null) faz o mesmo.
    // public List<SessaoCaptura> buscarTodasSessoes() throws SQLException {
    //    return buscarSessoes(null, null, null); 
    // }
}
