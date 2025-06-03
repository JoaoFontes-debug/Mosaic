
package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura.DirecaoMesclagem;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura.TransformacaoImagem;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author aluno.lauro
 */
public class PainelConfiguracao extends JPanel {
    
     private final ControladorPrincipal controlador;
    private JCheckBox checkExibicaoAuto;
    private JSpinner spinnerTempoExibicao;
    private JComboBox<TransformacaoImagem> comboTransformacao;
    private JComboBox<DirecaoMesclagem> comboDirecao;
    private JSpinner spinnerImagensMesclagem;

    public PainelConfiguracao(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(0, 2, 5, 5));
        
        // Exibição automática
        add(new JLabel("Exibir imagem após mesclagem:"));
        checkExibicaoAuto = new JCheckBox("", controlador.getConfiguracao().isExibicaoAutoHabilitada());
        add(checkExibicaoAuto);
        
        // Tempo de exibição
        add(new JLabel("Tempo de exibição (segundos):"));
        spinnerTempoExibicao = new JSpinner(new SpinnerNumberModel(
            controlador.getConfiguracao().getTempoFechamentoAuto(), 1, 60, 1));
        add(spinnerTempoExibicao);
        
        // Número de imagens para mesclagem
        add(new JLabel("Número de imagens para mesclagem:"));
        spinnerImagensMesclagem = new JSpinner(new SpinnerNumberModel(
            controlador.getConfiguracao().getNumeroImagensMesclagem(), 2, 10, 1));
        add(spinnerImagensMesclagem);
        
        // Direção de mesclagem
        add(new JLabel("Direção de mesclagem:"));
        comboDirecao = new JComboBox<>(DirecaoMesclagem.values());
        comboDirecao.setSelectedItem(controlador.getConfiguracao().getDirecaoMesclagem());
        add(comboDirecao);
        
        // Transformação padrão
        add(new JLabel("Transformação padrão:"));
        comboTransformacao = new JComboBox<>(TransformacaoImagem.values());
        comboTransformacao.setSelectedItem(controlador.getConfiguracao().getTransformacaoPadrao());
        add(comboTransformacao);
        
        // Botão salvar
        JButton btnSalvar = new JButton("Salvar Configurações");
        btnSalvar.addActionListener(e -> salvarConfiguracoes());
        add(btnSalvar);
    }

    private void salvarConfiguracoes() {
        controlador.getConfiguracao().setExibicaoAutoHabilitada(checkExibicaoAuto.isSelected());
        controlador.getConfiguracao().setTempoFechamentoAuto((Integer) spinnerTempoExibicao.getValue());
        controlador.getConfiguracao().setNumeroImagensMesclagem((Integer) spinnerImagensMesclagem.getValue());
        controlador.getConfiguracao().setDirecaoMesclagem((DirecaoMesclagem) comboDirecao.getSelectedItem());
        controlador.getConfiguracao().setTransformacaoPadrao((TransformacaoImagem) comboTransformacao.getSelectedItem());
        controlador.salvarConfiguracao();
    }
}
