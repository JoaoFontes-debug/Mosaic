
package com.joaofontes.mosaic;

import com.joaofontes.mosaic.view.JanelaPrincipal;


public class Mosaic {
    public static void main(String[] args) {
    
       javax.swing.SwingUtilities.invokeLater(() -> {
            JanelaPrincipal.getInstance().setVisible(true);
        });
    } 
}
