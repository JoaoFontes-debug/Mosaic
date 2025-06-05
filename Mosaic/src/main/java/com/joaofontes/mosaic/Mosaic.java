package com.joaofontes.mosaic;

import com.formdev.flatlaf.FlatLightLaf;
import com.joaofontes.mosaic.view.JanelaPrincipal;
import javax.swing.SwingUtilities;
// import javax.swing.UIManager; // Descomente se for usar personalizações UIManager
// import java.awt.Insets; // Descomente se for usar personalizações UIManager

public class Mosaic {
    public static void main(String[] args) {
        FlatLightLaf.setup(); // Tema Light como padrão

         //Exemplo de personalizações adicionais (opcional, após o setup do L&F)
//         UIManager.put("Button.arc", 0); 
//         UIManager.put("Component.arc", 0); 
//         UIManager.put("Button.padding", new Insets(5, 10, 5, 10)); 
//         UIManager.put("TextField.margin", new Insets(2, 5, 2, 5));
//         UIManager.put("TextArea.margin", new Insets(2, 5, 2, 5));

        SwingUtilities.invokeLater(() -> {
            JanelaPrincipal.getInstance().setVisible(true);
        });
    }
}
