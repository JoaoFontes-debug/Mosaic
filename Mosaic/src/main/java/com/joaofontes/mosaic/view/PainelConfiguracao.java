package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PainelConfiguracao extends JPanel {
    private ControladorPrincipal controlador;

    private JTextField campoDiretorioCaptura;
    private JSpinner spinnerTempoEntreCapturas; 
    private JCheckBox checkExibicaoAuto;
    private JSpinner spinnerTempoFechamentoAuto; 
    
    private JRadioButton radioLocalOnly;
    private JRadioButton radioCloudOnly;
    private JRadioButton radioLocalAndCloud;
    private ButtonGroup groupStorageOption;
    private JTextField campoCloudinaryUrl; // Para URL completa
    // Campos para credenciais Cloudinary individuais (opcional, se preferir sobre a URL completa)
    // private JTextField campoCloudName;
    // private JTextField campoCloudApiKey;
    // private JPasswordField campoCloudApiSecret;


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
        gbc.gridx = 0; gbc.gridy = yPos;
        add(new JLabel("Diretório de Captura Local:"), gbc);
        campoDiretorioCaptura = new JTextField(30);
        campoDiretorioCaptura.setEditable(false); 
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(campoDiretorioCaptura, gbc);
        btnEscolherDiretorio = new JButton("Escolher...");
        btnEscolherDiretorio.addActionListener(e -> escolherDiretorio());
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(btnEscolherDiretorio, gbc);
        yPos++;

        // Tempo entre Capturas
        gbc.gridx = 0; gbc.gridy = yPos; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Tempo entre Capturas (segundos):"), gbc);
        spinnerTempoEntreCapturas = new JSpinner(new SpinnerNumberModel(1, 1, 300, 1)); 
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        add(spinnerTempoEntreCapturas, gbc);
        yPos++;
        gbc.gridwidth = 1; 

        // Exibição Automática
        gbc.gridx = 0; gbc.gridy = yPos;
        add(new JLabel("Habilitar Exibição Automática:"), gbc);
        checkExibicaoAuto = new JCheckBox();
        checkExibicaoAuto.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(checkExibicaoAuto, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // Tempo de Fechamento Automático da Exibição
        gbc.gridx = 0; gbc.gridy = yPos;
        add(new JLabel("Tempo Fechamento Exibição (segundos):"), gbc);
        spinnerTempoFechamentoAuto = new JSpinner(new SpinnerNumberModel(5, 0, 60, 1)); // 0 para manual
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(spinnerTempoFechamentoAuto, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // Opções de Armazenamento
        gbc.gridx = 0; gbc.gridy = yPos;
        add(new JLabel("Opção de Armazenamento:"), gbc);
        
        JPanel panelStorageOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioLocalOnly = new JRadioButton(ConfiguracaoCaptura.StorageOption.LOCAL_ONLY.toString());
        radioCloudOnly = new JRadioButton(ConfiguracaoCaptura.StorageOption.CLOUD_ONLY.toString());
        radioLocalAndCloud = new JRadioButton(ConfiguracaoCaptura.StorageOption.LOCAL_AND_CLOUD.toString());
        groupStorageOption = new ButtonGroup();
        groupStorageOption.add(radioLocalOnly);
        groupStorageOption.add(radioCloudOnly);
        groupStorageOption.add(radioLocalAndCloud);
        panelStorageOptions.add(radioLocalOnly);
        panelStorageOptions.add(radioCloudOnly);
        panelStorageOptions.add(radioLocalAndCloud);
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(panelStorageOptions, gbc);
        yPos++;
        gbc.gridwidth = 1;

        // URL de Configuração Cloudinary
        gbc.gridx = 0; gbc.gridy = yPos;
        add(new JLabel("URL Cloudinary:"), gbc);
        campoCloudinaryUrl = new JTextField(35);
        campoCloudinaryUrl.setToolTipText("Ex: cloudinary://api_key:api_secret@cloud_name");
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(campoCloudinaryUrl, gbc);
        yPos++;
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;


        // Botão Salvar Configurações
        btnSalvarConfiguracoes = new JButton("Salvar Configurações");
        btnSalvarConfiguracoes.addActionListener(e -> salvarConfiguracoes());
        gbc.gridx = 0; gbc.gridy = yPos; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(btnSalvarConfiguracoes, gbc);
    }

    private void escolherDiretorio() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecionar Diretório de Captura Local");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false); 

        String currentPath = campoDiretorioCaptura.getText();
        if (currentPath != null && !currentPath.isEmpty()) {
            File currentDir = new File(currentPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                fileChooser.setCurrentDirectory(currentDir);
            }
        } else { // Se vazio, usa o diretório home do usuário
             fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        int userSelection = fileChooser.showSaveDialog(this); // showSaveDialog é mais apropriado para "escolher um lugar para salvar"
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            campoDiretorioCaptura.setText(selectedFile.getAbsolutePath());
        }
    }

    private void carregarConfiguracoes() {
        ConfiguracaoCaptura config = controlador.getConfiguracao();
        campoDiretorioCaptura.setText(config.getDiretorioCaptura());
        spinnerTempoEntreCapturas.setValue(config.getTempoEntreCapturasMs() / 1000); 
        checkExibicaoAuto.setSelected(config.isExibicaoAutoHabilitada());
        spinnerTempoFechamentoAuto.setValue(config.getTempoFechamentoAuto());
        
        switch (config.getStorageOption()) {
            case LOCAL_ONLY: radioLocalOnly.setSelected(true); break;
            case CLOUD_ONLY: radioCloudOnly.setSelected(true); break;
            case LOCAL_AND_CLOUD: default: radioLocalAndCloud.setSelected(true); break;
        }
        campoCloudinaryUrl.setText(config.getCloudinaryUrl() != null ? config.getCloudinaryUrl() : "");
        
        // Carregar outros campos de ConfiguraçaoCaptura se você os adicionou na UI
        // Ex: spinnerNumeroImagens.setValue(config.getNumeroImagensMesclagem());
    }

    private void salvarConfiguracoes() {
        ConfiguracaoCaptura config = controlador.getConfiguracao(); // Pega a instância atual
        config.setDiretorioCaptura(campoDiretorioCaptura.getText());
        config.setTempoEntreCapturasMs((Integer) spinnerTempoEntreCapturas.getValue() * 1000); 
        config.setExibicaoAutoHabilitada(checkExibicaoAuto.isSelected());
        config.setTempoFechamentoAuto((Integer) spinnerTempoFechamentoAuto.getValue());

        if (radioLocalOnly.isSelected()) {
            config.setStorageOption(ConfiguracaoCaptura.StorageOption.LOCAL_ONLY);
        } else if (radioCloudOnly.isSelected()) {
            config.setStorageOption(ConfiguracaoCaptura.StorageOption.CLOUD_ONLY);
        } else {
            config.setStorageOption(ConfiguracaoCaptura.StorageOption.LOCAL_AND_CLOUD);
        }
        
        String cloudinaryUrlInput = campoCloudinaryUrl.getText().trim();
        config.setCloudinaryUrl(cloudinaryUrlInput.isEmpty() ? null : cloudinaryUrlInput);
        
        // Salvar outros campos de ConfiguraçaoCaptura se você os adicionou na UI
        // Ex: config.setNumeroImagensMesclagem((Integer) spinnerNumeroImagens.getValue());

        controlador.salvarConfiguracao(); 
        // O feedback visual (JOptionPane) é tratado pelo ControladorPrincipal.salvarConfiguracao()
    }
}
