package com.joaofontes.mosaic.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // PREENCHA COM OS SEUS DADOS DO MYSQL
    private static final String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/mosaic_db";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "admin";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_USER, MYSQL_PASSWORD);
                System.out.println("Conexão com o banco de dados MySQL estabelecida.");
                // Assegura que as tabelas existem na primeira conexão
                criarTabelasSeNaoExistirem(connection);
            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL JDBC não encontrado. Verifique as dependências (pom.xml).");
                throw new SQLException("Driver MySQL não encontrado.", e);
            }
        }
        return connection;
    }

    private static void criarTabelasSeNaoExistirem(Connection conn) throws SQLException {
        if (conn == null || conn.isClosed()) {
            System.err.println("Não é possível criar tabelas, conexão com o banco não está ativa.");
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            // Tabela de Inspeções (Mestre)
            String sqlInspecoes = "CREATE TABLE IF NOT EXISTS inspecoes ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nome_peca VARCHAR(255) NOT NULL,"
                    + "descricao TEXT,"
                    + "data_criacao DATETIME NOT NULL"
                    + ");";
            stmt.execute(sqlInspecoes);
            System.out.println("Tabela 'inspecoes' verificada/criada com sucesso.");

            // Tabela de Mesclagens (Detalhe), antiga 'sessoes'
            String sqlMesclagens = "CREATE TABLE IF NOT EXISTS mesclagens ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "id_inspecao INT NOT NULL,"
                    + "data_captura DATETIME NOT NULL,"
                    + "caminho_imagem VARCHAR(1024),"
                    + "caminho_local VARCHAR(1024),"
                    + "FOREIGN KEY (id_inspecao) REFERENCES inspecoes(id) ON DELETE CASCADE"
                    + // Adiciona relação
                    ");";
            stmt.execute(sqlMesclagens);
            System.out.println("Tabela 'mesclagens' verificada/criada com sucesso.");

        } catch (SQLException e) {
            System.err.println("Erro ao criar/verificar tabelas: " + e.getMessage());
            throw e;
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
