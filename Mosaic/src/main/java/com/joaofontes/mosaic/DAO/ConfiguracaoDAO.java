package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.ConfiguracaoCaptura;

import java.sql.*;

/**
 * DAO para a tabela configuracoes.
 * Assume que existe apenas um registro de configuração (id = 1),
 * ou que você trabalharia com um INSERT ... ON DUPLICATE KEY UPDATE.
 */
public class ConfiguracaoDAO {

    private final Connection conexao;

    public ConfiguracaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Insere ou atualiza um registro de configuração.
     * Aqui usamos INSERT ... ON DUPLICATE KEY UPDATE (MySQL).
     */
    public void salvarConfiguracao(ConfiguracaoCaptura config) throws SQLException {
        String sql = "INSERT INTO configuracoes " +
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

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Se você adotou somente um registro (id fixo = 1),
            // então config.getId() = 1. Ou use config.getId() normalmente.
            stmt.setInt(1, config.getId() == 0 ? 1 : config.getId());
            stmt.setInt(2, config.getTempoFechamentoAuto());
            stmt.setBoolean(3, config.isExibicaoAutoHabilitada());
            stmt.setInt(4, config.getNumeroImagensMesclagem());
            stmt.setString(5, config.getDirecaoMesclagem());
            stmt.setString(6, config.getTransformacaoPadrao());

            // Novos campos:
            stmt.setString(7, config.getCloudName());
            stmt.setString(8, config.getCloudApiKey());
            stmt.setString(9, config.getCloudApiSecret());
            stmt.setBoolean(10, config.isSalvarLocal());
            stmt.setBoolean(11, config.isSalvarNuvem());

            stmt.executeUpdate();
        }
    }

    /**
     * Carrega a configuração (apenas o primeiro registro ou id = 1).
     * Retorna um objeto ConfiguracaoCaptura ou null se não existir.
     */
    public ConfiguracaoCaptura carregarConfiguracao() throws SQLException {
        String sql = "SELECT * FROM configuracoes WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, 1);  // supondo id fixo = 1
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ConfiguracaoCaptura config = new ConfiguracaoCaptura();
                    config.setId(rs.getInt("id"));
                    config.setTempoFechamentoAuto(rs.getInt("tempo_exibicao"));
                    config.setExibicaoAutoHabilitada(rs.getBoolean("exibicao_auto"));
                    config.setNumeroImagensMesclagem(rs.getInt("num_imagens"));
                    config.setDirecaoMesclagem(rs.getString("direcao_mesclagem"));
                    config.setTransformacaoPadrao(rs.getString("transformacao_padrao"));

                    // Preenche os novos campos:
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
