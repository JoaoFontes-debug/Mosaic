package com.joaofontes.mosaic.DAO;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String NOME_ARQUIVO_BD = "mosaic_database.db";
    private static final String URL_CONEXAO_SQLITE = "jdbc:sqlite:" + NOME_ARQUIVO_BD;
    
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                File dbFile = new File(NOME_ARQUIVO_BD);
                boolean primeiraExecucao = !dbFile.exists();
                connection = DriverManager.getConnection(URL_CONEXAO_SQLITE);
                System.out.println("Conexão com o banco de dados SQLite estabelecida.");
                if (primeiraExecucao) {
                    System.out.println("Primeira execução detetada. A criar tabelas...");
                    criarTabelasSeNaoExistirem(connection);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Driver SQLite JDBC não encontrado. Verifique se o JAR foi adicionado às bibliotecas do projeto.");
                throw new SQLException("Driver SQLite não encontrado.", e);
            }
        }
        return connection;
    }

    private static void criarTabelasSeNaoExistirem(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Tabela de Inspeções com colunas de data como TEXT
            String sqlInspecoes = "CREATE TABLE IF NOT EXISTS inspecoes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nome_peca TEXT NOT NULL,"
                    + "descricao TEXT,"
                    + "data_criacao TEXT NOT NULL" // CORRETO: Usar TEXT para datas
                    + ");";
            stmt.execute(sqlInspecoes);
            
            // Tabela de Mesclagens com colunas de data como TEXT
            String sqlMesclagens = "CREATE TABLE IF NOT EXISTS mesclagens ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "id_inspecao INTEGER NOT NULL,"
                    + "data_captura TEXT NOT NULL," // CORRETO: Usar TEXT para datas
                    + "caminho_imagem TEXT,"
                    + "caminho_local TEXT,"
                    + "FOREIGN KEY (id_inspecao) REFERENCES inspecoes(id) ON DELETE CASCADE"
                    + ");";
            stmt.execute(sqlMesclagens);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão com o banco: " + e.getMessage());
        }
    }
}