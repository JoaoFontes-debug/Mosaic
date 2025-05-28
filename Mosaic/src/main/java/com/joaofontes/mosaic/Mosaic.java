
package com.joaofontes.mosaic;

import java.sql.Connection;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author JoãoFontes
 */
public class Mosaic {

    public static void main(String[] args) {
       
        
        try {
            Connection con;
            Statement st;
            
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbTesteMosaic", "root", "admin");
            st = con.createStatement();
            
            st.executeUpdate("insert into tbimagens(url, descricao, nome_peca) values('google.com', 'teste da conexao', 'porca') ");
             JOptionPane.showMessageDialog(null, "dados inseridos com sucesso!!!!");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "O driver não está na biblioteca");
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexao ou operações no banco de dados");
            
        }
        
    }
}
