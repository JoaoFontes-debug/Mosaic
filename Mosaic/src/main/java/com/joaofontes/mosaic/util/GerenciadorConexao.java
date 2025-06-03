
package com.joaofontes.mosaic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author aluno.lauro
 */
public class GerenciadorConexao {
    
    private static final String URL = "jdbc:mysql://localhost:3306/mosaic_db";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
