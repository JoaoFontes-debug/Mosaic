package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.ConfiguracaoCaptura;

import java.sql.*;

/**
 * DAO para a tabela configuracoes.
 * VERSÃO CORRIGIDA: Compatível com SQLite e MySQL.
 * Assume que existe apenas um registo de configuração (id = 1).
 */
public class ConfiguracaoDAO {

    private final Connection conexao;

    public ConfiguracaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Insere ou atualiza um registo de configuração.
     * Tenta usar a sintaxe 'INSERT ... ON CONFLICT' do SQLite.
     * Se falhar, tenta usar a sintaxe 'INSERT ... ON DUPLICATE KEY UPDATE' do MySQL.
     * Nota: O ControladorPrincipal atualmente salva as configurações num ficheiro .dat,
     * e não chama este método. Para usar este DAO, o ControladorPrincipal precisa ser modificado.
     */
    public void salvarConfiguracao(ConfiguracaoCaptura config) throws SQLException {
        // SQL para SQLite
        String sqlSQLite = "INSERT INTO configuracoes " +
            "(id, tempo_exibicao, exibicao_auto, num_imagens, direcao_mesclagem, transformacao_padrao, " +
            " cloud_name, cloud_api_key, cloud_api_secret, salvar_local, salvar_nuvem) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT(id) DO UPDATE SET " +
            "tempo_exibicao = excluded.tempo_exibicao, " +
            "exibicao_auto = excluded.exibicao_auto, " +
            "num_imagens = excluded.num_imagens, " +
            "direcao_mesclagem = excluded.direcao_mesclagem, " +
            "transformacao_padrao = excluded.transformacao_padrao, " +
            "cloud_name = excluded.cloud_name, " +
            "cloud_api_key = excluded.cloud_api_key, " +
            "cloud_api_secret = excluded.cloud_api_secret, " +
            "salvar_local = excluded.salvar_local, " +
            "salvar_nuvem = excluded.salvar_nuvem";
        
        // SQL para MySQL
        String sqlMySQL = "INSERT INTO configuracoes " +
            "(id, tempo_exibicao, exibicao_auto, num_imagens, direcao_mesclagem, transformacao_padrao, " +
            " cloud_name, cloud_api_key, cloud_api_secret, salvar_local, salvar_nuvem) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "tempo_exibicao = VALUES(tempo_exibicao), " +
            "exibicao_auto = VALUES(exibicao_auto), " +
            "num_imagens = VALUES(num_imagens), " +
            "direcao_mesclagem = VALUES(direcao_mesclagem), " +
            "transformacao_padrao = VALUES(transformacao_padrao), " +
            "cloud_name = VALUES(cloud_name), " +
            "cloud_api_key = VALUES(cloud_api_key), " +
            "cloud_api_secret = VALUES(cloud_api_secret), " +
            "salvar_local = VALUES(salvar_local), " +
            "salvar_nuvem = VALUES(salvar_nuvem)";

        String sqlToUse;
        try {
            // Verifica o nome do driver para decidir qual SQL usar
            if (conexao.getMetaData().getDriverName().toLowerCase().contains("sqlite")) {
                sqlToUse = sqlSQLite;
                System.out.println("Usando sintaxe SQL para SQLite em ConfiguracaoDAO.");
            } else {
                sqlToUse = sqlMySQL;
                System.out.println("Usando sintaxe SQL para MySQL em ConfiguracaoDAO.");
            }
        } catch (SQLException e) {
            // Fallback para MySQL em caso de erro ao obter metadados
            sqlToUse = sqlMySQL;
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sqlToUse)) {
            // Usa id fixo = 1 para a linha de configuração global.
            stmt.setInt(1, 1);
            stmt.setInt(2, config.getTempoFechamentoAuto());
            stmt.setBoolean(3, config.isExibicaoAutoHabilitada());
            // CORREÇÃO: Chama o método getNumeroImagensParaMesclar()
            stmt.setInt(4, config.getNumeroImagensParaMesclar());
            // CORREÇÃO: Chama os métodos get...() que retornam String
            stmt.setString(5, config.getDirecaoMesclagem());
            stmt.setString(6, config.getTransformacaoPadrao());
            stmt.setString(7, config.getCloudName());
            stmt.setString(8, config.getCloudApiKey());
            stmt.setString(9, config.getCloudApiSecret());
            stmt.setBoolean(10, config.isSalvarLocal());
            stmt.setBoolean(11, config.isSalvarNuvem());

            stmt.executeUpdate();
        }
    }

    /**
     * Carrega a configuração (apenas o registo com id = 1).
     * Retorna um objeto ConfiguracaoCaptura ou null se não existir.
     */
    public ConfiguracaoCaptura carregarConfiguracao() throws SQLException {
        String sql = "SELECT * FROM configuracoes WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ConfiguracaoCaptura config = new ConfiguracaoCaptura();
                    config.setId(rs.getInt("id"));
                    config.setTempoFechamentoAuto(rs.getInt("tempo_exibicao"));
                    config.setExibicaoAutoHabilitada(rs.getBoolean("exibicao_auto"));
                    // CORREÇÃO: Chama o método setNumeroImagensParaMesclar()
                    config.setNumeroImagensParaMesclar(rs.getInt("num_imagens"));
                    // CORREÇÃO: Chama os métodos set...(String)
                    config.setDirecaoMesclagem(rs.getString("direcao_mesclagem"));
                    config.setTransformacaoPadrao(rs.getString("transformacao_padrao"));
                    config.setCloudName(rs.getString("cloud_name"));
                    config.setCloudApiKey(rs.getString("cloud_api_key"));
                    config.setCloudApiSecret(rs.getString("cloud_api_secret"));
                    config.setSalvarLocal(rs.getBoolean("salvar_local"));
                    config.setSalvarNuvem(rs.getBoolean("salvar_nuvem"));
                    return config;
                } else {
                    return null;
                }
            }
        }
    }
}