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
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                throw new SQLException("Falha ao conectar ao banco de dados.", e);
            }
        }
        return connection;
    }

    private static void criarTabelasSeNaoExistirem(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nome_completo TEXT NOT NULL,"
                    + "email TEXT NOT NULL UNIQUE,"
                    + "password_hash TEXT NOT NULL,"
                    + "nivel_acesso TEXT NOT NULL DEFAULT 'OPERADOR',"
                    + "reset_token TEXT,"
                    + "token_expires_at TEXT"
                    + ");";
            stmt.execute(sqlUsuarios);
            System.out.println("Tabela 'usuarios' verificada/criada com sucesso.");

            // ALTERAÇÃO: Adicionada a coluna 'nome_operador'
            String sqlInspecoes = "CREATE TABLE IF NOT EXISTS inspecoes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nome_peca TEXT NOT NULL,"
                    + "descricao TEXT,"
                    + "nome_operador TEXT NOT NULL," // NOVO CAMPO
                    + "data_criacao TEXT NOT NULL"
                    + ");";
            stmt.execute(sqlInspecoes);
            System.out.println("Tabela 'inspecoes' verificada/criada com sucesso.");

            String sqlMesclagens = "CREATE TABLE IF NOT EXISTS mesclagens ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "id_inspecao INTEGER NOT NULL,"
                    + "data_captura TEXT NOT NULL,"
                    + "caminho_imagem TEXT,"
                    + "caminho_local TEXT,"
                    + "FOREIGN KEY (id_inspecao) REFERENCES inspecoes(id) ON DELETE CASCADE"
                    + ");";
            stmt.execute(sqlMesclagens);
            System.out.println("Tabela 'mesclagens' verificada/criada com sucesso.");
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
