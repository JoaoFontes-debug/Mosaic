package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PainelConfiguracao extends JPanel {
    private final ControladorPrincipal controlador;
    private JTextField campoDiretorioCaptura;
    private JSpinner spinnerIntervaloVerificacao;
    private JCheckBox checkExibicaoAuto;
    private JSpinner spinnerTempoFechamentoAuto; 
    private JSpinner spinnerNumImagensParaMesclar; // NOVO COMPONENTE UI
    
    private JRadioButton radioLocalOnly;
    private JRadioButton radioCloudOnly;
    private JRadioButton radioLocalAndCloud;
    private ButtonGroup groupStorageOption;
    private JTextField campoCloudinaryUrl;

    private JButton btnSalvarConfiguracoes;
    private JButton btnEscolherDiretorio;

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

        // Diretório de Captura
        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("Diretório de Captura Local:"), gbc);
        campoDiretorioCaptura = new JTextField(30);
        campoDiretorioCaptura.setEditable(false); 
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; add(campoDiretorioCaptura, gbc);
        btnEscolherDiretorio = new JButton("Escolher...");
        btnEscolherDiretorio.addActionListener(e -> escolherDiretorio());
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; add(btnEscolherDiretorio, gbc);
        yPos++;

        // Intervalo de Verificação
        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE;
        JLabel labelIntervalo = new JLabel("Intervalo para Verificar Mudanças (segundos):");
        labelIntervalo.setToolTipText("Com que frequência o software deve verificar se a tela mudou. Valores menores que 1 são permitidos (ex: 0.4).");
        add(labelIntervalo, gbc);
        SpinnerNumberModel modelIntervalo = new SpinnerNumberModel(1.0, 0.4, 300.0, 0.1); 
        spinnerIntervaloVerificacao = new JSpinner(modelIntervalo);
        spinnerIntervaloVerificacao.setEditor(new JSpinner.NumberEditor(spinnerIntervaloVerificacao, "0.0"));
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; add(spinnerIntervaloVerificacao, gbc);
        yPos++;
        gbc.gridwidth = 1; 

        // NOVO CAMPO: Número de Imagens para Mesclar
        gbc.gridx = 0; gbc.gridy = yPos;
        JLabel labelNumImagens = new JLabel("Número de capturas para mesclar:");
        labelNumImagens.setToolTipText("Quantas imagens com mudanças devem ser capturadas antes de serem mescladas.");
        add(labelNumImagens, gbc);
        SpinnerNumberModel modelNumImagens = new SpinnerNumberModel(2, 2, 10, 1); // Ex: de 2 a 10 imagens
        spinnerNumImagensParaMesclar = new JSpinner(modelNumImagens);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; add(spinnerNumImagensParaMesclar, gbc);
        yPos++;
        gbc.gridwidth = 1; 


        // Exibição Automática
        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("Habilitar Exibição Automática:"), gbc);
        checkExibicaoAuto = new JCheckBox();
        checkExibicaoAuto.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 1; gbc.gridwidth = 2; add(checkExibicaoAuto, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // Tempo de Fechamento Automático da Exibição
        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("Tempo Fechamento Exibição (segundos):"), gbc);
        spinnerTempoFechamentoAuto = new JSpinner(new SpinnerNumberModel(5, 0, 60, 1));
        gbc.gridx = 1; gbc.gridwidth = 2; add(spinnerTempoFechamentoAuto, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // Opções de Armazenamento
        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("Opção de Armazenamento:"), gbc);
        JPanel panelStorageOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioLocalOnly = new JRadioButton(ConfiguracaoCaptura.StorageOption.LOCAL_ONLY.toString());
        radioCloudOnly = new JRadioButton(ConfiguracaoCaptura.StorageOption.CLOUD_ONLY.toString());
        radioLocalAndCloud = new JRadioButton(ConfiguracaoCaptura.StorageOption.LOCAL_AND_CLOUD.toString());
        groupStorageOption = new ButtonGroup();
        groupStorageOption.add(radioLocalOnly); groupStorageOption.add(radioCloudOnly); groupStorageOption.add(radioLocalAndCloud);
        panelStorageOptions.add(radioLocalOnly); panelStorageOptions.add(radioCloudOnly); panelStorageOptions.add(radioLocalAndCloud);
        gbc.gridx = 1; gbc.gridwidth = 2; add(panelStorageOptions, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // URL de Configuração Cloudinary
        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("URL Cloudinary:"), gbc);
        campoCloudinaryUrl = new JTextField(35);
        campoCloudinaryUrl.setToolTipText("Ex: cloudinary://api_key:api_secret@cloud_name");
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; add(campoCloudinaryUrl, gbc);
        yPos++;
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        // Botão Salvar Configurações
        btnSalvarConfiguracoes = new JButton("Salvar Configurações");
        btnSalvarConfiguracoes.addActionListener(e -> salvarConfiguracoes());
        gbc.gridx = 0; gbc.gridy = yPos; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        add(btnSalvarConfiguracoes, gbc);
    }

    private void escolherDiretorio() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
        fileChooser.setDialogTitle("Selecionar Diretório de Captura Local");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false); 
        String currentPath = campoDiretorioCaptura.getText();
        if (currentPath != null && !currentPath.isEmpty()) {
            File currentDir = new File(currentPath);
            if (currentDir.exists()) fileChooser.setCurrentDirectory(currentDir);
        }
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            campoDiretorioCaptura.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void carregarConfiguracoes() {
        ConfiguracaoCaptura config = controlador.getConfiguracao();
        campoDiretorioCaptura.setText(config.getDiretorioCaptura());
        spinnerIntervaloVerificacao.setValue(config.getTempoEntreCapturasMs() / 1000.0); 
        spinnerNumImagensParaMesclar.setValue(config.getNumeroImagensParaMesclar()); // CARREGA O NOVO CAMPO
        checkExibicaoAuto.setSelected(config.isExibicaoAutoHabilitada());
        spinnerTempoFechamentoAuto.setValue(config.getTempoFechamentoAuto());
        switch (config.getStorageOption()) {
            case LOCAL_ONLY: radioLocalOnly.setSelected(true); break;
            case CLOUD_ONLY: radioCloudOnly.setSelected(true); break;
            case LOCAL_AND_CLOUD: default: radioLocalAndCloud.setSelected(true); break;
        }
        campoCloudinaryUrl.setText(config.getCloudinaryUrl() != null ? config.getCloudinaryUrl() : "");
    }

    private void salvarConfiguracoes() {
        ConfiguracaoCaptura config = controlador.getConfiguracao();
        config.setDiretorioCaptura(campoDiretorioCaptura.getText());
        config.setTempoEntreCapturasMs((int) (((Double) spinnerIntervaloVerificacao.getValue()) * 1000)); 
        config.setNumeroImagensParaMesclar((Integer) spinnerNumImagensParaMesclar.getValue()); // SALVA O NOVO CAMPO
        config.setExibicaoAutoHabilitada(checkExibicaoAuto.isSelected());
        config.setTempoFechamentoAuto((Integer) spinnerTempoFechamentoAuto.getValue());
        if (radioLocalOnly.isSelected()) { config.setStorageOption(ConfiguracaoCaptura.StorageOption.LOCAL_ONLY); } 
        else if (radioCloudOnly.isSelected()) { config.setStorageOption(ConfiguracaoCaptura.StorageOption.CLOUD_ONLY); } 
        else { config.setStorageOption(ConfiguracaoCaptura.StorageOption.LOCAL_AND_CLOUD); }
        String cloudinaryUrlInput = campoCloudinaryUrl.getText().trim();
        config.setCloudinaryUrl(cloudinaryUrlInput.isEmpty() ? null : cloudinaryUrlInput);
        controlador.salvarConfiguracao();
    }
}