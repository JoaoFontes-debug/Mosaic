package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import javax.swing.*;
import java.awt.*;


public class PainelConfiguracao extends JPanel {

    private final ControladorPrincipal controlador;
    private JTextField campoDiretorioCaptura;
    private JSpinner spinnerIntervaloVerificacao;
    private JSpinner spinnerNumImagensParaMesclar;
    private JCheckBox checkExibicaoAuto;
    private JSpinner spinnerTempoFechamentoAuto;
    private JSpinner spinnerAtrasoReiniciar;
    private JSlider sliderSensibilidade; // NOVO COMPONENTE UI

    private JRadioButton radioLocalOnly, radioCloudOnly, radioLocalAndCloud;
    private JTextField campoCloudinaryUrl;

    public PainelConfiguracao(ControladorPrincipal controlador) {
        this.controlador = controlador;
        initUI();
        carregarConfiguracoes();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Configurações de Captura e Armazenamento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        int yPos = 0;

        // Diretório
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel("Diretório de Captura Local:"), gbc);
        campoDiretorioCaptura = new JTextField(30);
        campoDiretorioCaptura.setEditable(false);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(campoDiretorioCaptura, gbc);
        JButton btnEscolherDir = new JButton("Escolher...");
        btnEscolherDir.addActionListener(e -> escolherDiretorio());
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(btnEscolherDir, gbc);
        yPos++;

        // Intervalo de Verificação
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Intervalo de Verificação (s):"), gbc);
        spinnerIntervaloVerificacao = new JSpinner(new SpinnerNumberModel(1.0, 0.4, 300.0, 0.1));
        spinnerIntervaloVerificacao.setEditor(new JSpinner.NumberEditor(spinnerIntervaloVerificacao, "0.0"));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(spinnerIntervaloVerificacao, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // Nº de Capturas
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel("Nº de capturas por mesclagem:"), gbc);
        spinnerNumImagensParaMesclar = new JSpinner(new SpinnerNumberModel(2, 2, 10, 1));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(spinnerNumImagensParaMesclar, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // NOVO CAMPO: Sensibilidade da Detecção
        gbc.gridx = 0;
        gbc.gridy = yPos;
        JLabel labelSensibilidade = new JLabel("Sensibilidade da Detecção:");
        labelSensibilidade.setToolTipText("Quão diferente a imagem precisa ser para ser capturada. Menor = Mais sensível.");
        add(labelSensibilidade, gbc);
        sliderSensibilidade = new JSlider(0, 50); // 0.0% a 5.0%
        sliderSensibilidade.setMajorTickSpacing(10);
        sliderSensibilidade.setMinorTickSpacing(5);
        sliderSensibilidade.setPaintTicks(true);
        sliderSensibilidade.setPaintLabels(false); // Labels serão tratados manualmente
        JLabel labelValorSlider = new JLabel();
        sliderSensibilidade.addChangeListener(e -> {
            double valor = sliderSensibilidade.getValue() / 10.0;
            labelValorSlider.setText(String.format("%.1f%%", valor));
        });
        JPanel painelSlider = new JPanel(new BorderLayout(5, 0));
        painelSlider.add(sliderSensibilidade, BorderLayout.CENTER);
        painelSlider.add(labelValorSlider, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(painelSlider, gbc);
        yPos++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        // Outras Configurações
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel("Exibição Automática:"), gbc);
        checkExibicaoAuto = new JCheckBox();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(checkExibicaoAuto, gbc);
        yPos++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel("Tempo Fechamento Exibição (s):"), gbc);
        spinnerTempoFechamentoAuto = new JSpinner(new SpinnerNumberModel(5, 0, 60, 1));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(spinnerTempoFechamentoAuto, gbc);
        yPos++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel("Atraso para Reiniciar Captura (s):"), gbc);
        spinnerAtrasoReiniciar = new JSpinner(new SpinnerNumberModel(5, 0, 60, 1));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(spinnerAtrasoReiniciar, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // Armazenamento
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel("Opção de Armazenamento:"), gbc);
        JPanel panelStorage = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioLocalOnly = new JRadioButton("Apenas Local");
        radioCloudOnly = new JRadioButton("Apenas Nuvem");
        radioLocalAndCloud = new JRadioButton("Local e Nuvem");
        ButtonGroup groupStorage = new ButtonGroup();
        groupStorage.add(radioLocalOnly);
        groupStorage.add(radioCloudOnly);
        groupStorage.add(radioLocalAndCloud);
        panelStorage.add(radioLocalOnly);
        panelStorage.add(radioCloudOnly);
        panelStorage.add(radioLocalAndCloud);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(panelStorage, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // Cloudinary
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel("URL Cloudinary:"), gbc);
        campoCloudinaryUrl = new JTextField(35);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(campoCloudinaryUrl, gbc);
        yPos++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        // Botão Salvar
        JButton btnSalvar = new JButton("Salvar Configurações");
        btnSalvar.addActionListener(e -> salvarConfiguracoes());
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnSalvar, gbc);
    }

    private void escolherDiretorio() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setDialogTitle("Selecionar Diretório");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            campoDiretorioCaptura.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void carregarConfiguracoes() {
        ConfiguracaoCaptura config = controlador.getConfiguracao();
        campoDiretorioCaptura.setText(config.getDiretorioCaptura());
        spinnerIntervaloVerificacao.setValue(config.getTempoEntreCapturasMs() / 1000.0);
        spinnerNumImagensParaMesclar.setValue(config.getNumeroImagensParaMesclar());
        sliderSensibilidade.setValue((int) (config.getLimiarMudanca() * 10));
        checkExibicaoAuto.setSelected(config.isExibicaoAutoHabilitada());
        spinnerTempoFechamentoAuto.setValue(config.getTempoFechamentoAuto());
        spinnerAtrasoReiniciar.setValue(config.getAtrasoReiniciarCaptura());
        switch (config.getStorageOption()) {
            case LOCAL_ONLY ->
                radioLocalOnly.setSelected(true);
            case CLOUD_ONLY ->
                radioCloudOnly.setSelected(true);
            default ->
                radioLocalAndCloud.setSelected(true);
        }
        campoCloudinaryUrl.setText(config.getCloudinaryUrl());
    }

    private void salvarConfiguracoes() {
        ConfiguracaoCaptura config = controlador.getConfiguracao();
        config.setDiretorioCaptura(campoDiretorioCaptura.getText());
        config.setTempoEntreCapturasMs((int) (((Double) spinnerIntervaloVerificacao.getValue()) * 1000));
        config.setNumeroImagensParaMesclar((Integer) spinnerNumImagensParaMesclar.getValue());
        config.setLimiarMudanca(sliderSensibilidade.getValue() / 10.0);
        config.setExibicaoAutoHabilitada(checkExibicaoAuto.isSelected());
        config.setTempoFechamentoAuto((Integer) spinnerTempoFechamentoAuto.getValue());
        config.setAtrasoReiniciarCaptura((Integer) spinnerAtrasoReiniciar.getValue());
        if (radioLocalOnly.isSelected()) {
            config.setStorageOption(ConfiguracaoCaptura.StorageOption.LOCAL_ONLY);
        } else if (radioCloudOnly.isSelected()) {
            config.setStorageOption(ConfiguracaoCaptura.StorageOption.CLOUD_ONLY);
        } else {
            config.setStorageOption(ConfiguracaoCaptura.StorageOption.LOCAL_AND_CLOUD);
        }
        config.setCloudinaryUrl(campoCloudinaryUrl.getText().trim());
        controlador.salvarConfiguracao();
    }
}
