package com.joaofontes.mosaic.DAO;

import com.joaofontes.mosaic.model.ConfiguracaoCaptura;

import java.sql.*;

public class ConfiguracaoDAO {

    private final Connection conexao;

    public ConfiguracaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void salvarConfiguracao(ConfiguracaoCaptura config) throws SQLException {
        String sql = "INSERT INTO configuracoes "
                + "(id, tempo_exibicao, exibicao_auto, num_imagens, direcao_mesclagem, transformacao_padrao, "
                + " cloud_name, cloud_api_key, cloud_api_secret, salvar_local, salvar_nuvem) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "tempo_exibicao = VALUES(tempo_exibicao), "
                + "exibicao_auto = VALUES(exibicao_auto), "
                + "num_imagens = VALUES(num_imagens), "
                + "direcao_mesclagem = VALUES(direcao_mesclagem), "
                + "transformacao_padrao = VALUES(transformacao_padrao), "
                + "cloud_name = VALUES(cloud_name), "
                + "cloud_api_key = VALUES(cloud_api_key), "
                + "cloud_api_secret = VALUES(cloud_api_secret), "
                + "salvar_local = VALUES(salvar_local), "
                + "salvar_nuvem = VALUES(salvar_nuvem)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, 1); // ID Fixo
            stmt.setInt(2, config.getTempoFechamentoAuto());
            stmt.setBoolean(3, config.isExibicaoAutoHabilitada());
            stmt.setInt(4, config.getNumeroImagensParaMesclar());
            stmt.setString(5, config.getDirecaoMesclagem()); // Usa método de compatibilidade
            stmt.setString(6, config.getTransformacaoPadrao()); // Usa método de compatibilidade
            stmt.setString(7, config.getCloudName());
            stmt.setString(8, config.getCloudApiKey());
            stmt.setString(9, config.getCloudApiSecret());
            stmt.setBoolean(10, config.isSalvarLocal());
            stmt.setBoolean(11, config.isSalvarNuvem());
            stmt.executeUpdate();
        }
    }

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
                    config.setNumeroImagensParaMesclar(rs.getInt("num_imagens"));
                    config.setDirecaoMesclagem(rs.getString("direcao_mesclagem")); // Usa método de compatibilidade
                    config.setTransformacaoPadrao(rs.getString("transformacao_padrao")); // Usa método de compatibilidade
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
