package com.joaofontes.mosaic;

import com.formdev.flatlaf.FlatLightLaf;
import com.joaofontes.mosaic.view.JanelaLogin; 
import javax.swing.SwingUtilities;

public class Mosaic {
    public static void main(String[] args) {
        // Configura o tema visual da aplicação
        FlatLightLaf.setup();
        
        // Garante que a interface gráfica seja criada na thread de eventos da AWT
        SwingUtilities.invokeLater(() -> {
            // ALTERAÇÃO CRÍTICA: A aplicação agora começa na tela de login.
            new JanelaLogin().setVisible(true);
        });
    }
}