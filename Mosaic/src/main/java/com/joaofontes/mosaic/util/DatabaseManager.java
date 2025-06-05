package com.joaofontes.mosaic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // --- INÍCIO DA SEÇÃO QUE VOCÊ PRECISA CONFIGURAR PARA MYSQL ---
    private static final String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/ mosaic_db"; // Ex: jdbc:mysql://localhost:3306/mosaicdb
    private static final String MYSQL_USER = "root"; // Ex: root
    private static final String MYSQL_PASSWORD = "admin"; // Ex: admin
    // --- FIM DA SEÇÃO DE CONFIGURAÇÃO MYSQL ---

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Carrega o driver JDBC do MySQL
                Class.forName("com.mysql.cj.jdbc.Driver"); // Para MySQL Connector/J 8.x+
                // Class.forName("com.mysql.jdbc.Driver"); // Para versões mais antigas do Connector/J

                System.out.println("Tentando conectar ao MySQL: " + MYSQL_DB_URL);
                connection = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_USER, MYSQL_PASSWORD);
                System.out.println("Conexão com o banco de dados MySQL estabelecida com sucesso.");
                
                // É uma boa prática verificar e criar tabelas aqui se elas não existirem
                criarTabelasSeNaoExistirem(connection); 

            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL JDBC não encontrado. Verifique suas dependências (POM.xml ou JARs).");
                e.printStackTrace();
                throw new SQLException("Driver MySQL não encontrado.", e);
            } catch (SQLException e) {
                System.err.println("Erro ao conectar ao banco de dados MySQL: " + e.getMessage());
                System.err.println("SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
                e.printStackTrace();
                throw e; 
            }
        }
        return connection;
    }

    private static void criarTabelasSeNaoExistirem(Connection conn) throws SQLException {
        if (conn == null || conn.isClosed()) {
            System.err.println("DatabaseManager: Não é possível criar tabelas, conexão com o banco não está ativa.");
            return;
        }
        
        // Tabela de Sessões (Exemplo para MySQL)
        // Ajuste os tipos de dados conforme necessário para MySQL (e.g., TEXT, DATETIME)
        String sqlSessoes = "CREATE TABLE IF NOT EXISTS sessoes (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "nome_peca VARCHAR(255)," +
                            "descricao TEXT," +
                            "data_captura DATETIME NOT NULL," +
                            "caminho_imagem VARCHAR(1024)," + 
                            "caminho_local VARCHAR(1024)" +   
                            ");";
        
        // Você pode adicionar outras tabelas aqui, como uma tabela de configurações se desejar.
        // String sqlConfiguracoes = "CREATE TABLE IF NOT EXISTS configuracoes_app (" +
        //                         "config_key VARCHAR(100) PRIMARY KEY," +
        //                         "config_value TEXT" +
        //                         ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlSessoes);
            System.out.println("DatabaseManager: Tabela 'sessoes' verificada/criada com sucesso no MySQL.");
            // stmt.execute(sqlConfiguracoes);
            // System.out.println("DatabaseManager: Tabela 'configuracoes_app' verificada/criada com sucesso no MySQL.");
        } catch (SQLException e) {
            System.err.println("DatabaseManager: Erro SQL ao criar/verificar tabelas no MySQL: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão com o banco de dados MySQL fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
}
