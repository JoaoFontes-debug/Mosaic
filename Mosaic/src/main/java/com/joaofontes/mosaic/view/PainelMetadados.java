
package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author aluno.lauro
 */
public class PainelMetadados extends JPanel{
    
      private final ControladorPrincipal controlador;
    private JTextField campoNomePeca;
    private JTextField campoDescricao;

    public PainelMetadados(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(3, 2, 5, 5));
        
        add(new JLabel("Nome da Peça:"));
        campoNomePeca = new JTextField();
        add(campoNomePeca);
        
        add(new JLabel("Descrição:"));
        campoDescricao = new JTextField();
        add(campoDescricao);
        
        JButton btnSalvar = new JButton("Salvar Metadados");
        btnSalvar.addActionListener(e -> salvarMetadados());
        add(btnSalvar);
    }

    private void salvarMetadados() {
        controlador.setMetadadosSessao(
            campoNomePeca.getText(),
            campoDescricao.getText()
        );
    }
}
