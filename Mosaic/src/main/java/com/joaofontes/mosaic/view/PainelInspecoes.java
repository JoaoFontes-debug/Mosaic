package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.Inspecao;
import com.joaofontes.mosaic.model.Mesclagem;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PainelInspecoes extends JPanel {
    private final ControladorPrincipal controlador;
    private JTable tabelaInspecoes, tabelaMesclagens;
    private DefaultTableModel modeloInspecoes, modeloMesclagens;
    private JTextField campoNomePecaFiltro, campoDataInicioFiltro, campoDataFimFiltro;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public PainelInspecoes(ControladorPrincipal controlador) {
        this.controlador = controlador;
        dateFormat.setLenient(false);
        initUI();
        carregarTodasInspecoes();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(criarPainelFiltros(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(criarPainelTabelaInspecoes());
        splitPane.setBottomComponent(criarPainelTabelaMesclagens());
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Filtros de Busca de Inspeções"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Aumenta o espaçamento
        gbc.anchor = GridBagConstraints.EAST; // Alinha os labels à direita

        // Linha 0: Nome da Peça
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; painel.add(new JLabel("Nome da Peça:"), gbc);
        campoNomePecaFiltro = new JTextField(25); // Tamanho aumentado
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; painel.add(campoNomePecaFiltro, gbc);

        // Linha 1: Datas
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; painel.add(new JLabel("Data Início:"), gbc);
        campoDataInicioFiltro = new JTextField(12); // Tamanho aumentado
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; painel.add(campoDataInicioFiltro, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; painel.add(new JLabel("Data Fim:"), gbc);
        campoDataFimFiltro = new JTextField(12); // Tamanho aumentado
        gbc.gridx = 3; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5; painel.add(campoDataFimFiltro, gbc);
        
        // Linha 2: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarInspecoesComFiltro());
        JButton btnMostrarTodas = new JButton("Mostrar Todas");
        btnMostrarTodas.addActionListener(e -> carregarTodasInspecoes());
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnMostrarTodas);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE; painel.add(painelBotoes, gbc);
        
        return painel;
    }

    private JScrollPane criarPainelTabelaInspecoes() {
        String[] colunas = {"ID", "Nome da Peça", "Descrição", "Data de Criação"};
        modeloInspecoes = new DefaultTableModel(colunas, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        tabelaInspecoes = new JTable(modeloInspecoes);
        tabelaInspecoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaInspecoes.getSelectionModel().addListSelectionListener(this::onInspecaoSelecionada);
        JScrollPane scrollPane = new JScrollPane(tabelaInspecoes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inspeções"));
        return scrollPane;
    }
    
    private JScrollPane criarPainelTabelaMesclagens() {
        String[] colunas = {"ID", "Data da Captura", "URL Nuvem", "Caminho Local"};
        modeloMesclagens = new DefaultTableModel(colunas, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        tabelaMesclagens = new JTable(modeloMesclagens);
        JScrollPane scrollPane = new JScrollPane(tabelaMesclagens);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Imagens Mescladas da Inspeção Selecionada"));
        return scrollPane;
    }

    private void onInspecaoSelecionada(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting() && tabelaInspecoes.getSelectedRow() != -1) {
            int selectedRow = tabelaInspecoes.convertRowIndexToModel(tabelaInspecoes.getSelectedRow());
            int idInspecao = (Integer) modeloInspecoes.getValueAt(selectedRow, 0);
            carregarMesclagens(idInspecao);
        }
    }

    private void carregarMesclagens(int idInspecao) {
        try {
            List<Mesclagem> mesclagens = controlador.carregarMesclagensPorInspecao(idInspecao);
            atualizarTabelaMesclagens(mesclagens);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar imagens mescladas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void carregarTodasInspecoes() {
        campoNomePecaFiltro.setText("");
        campoDataInicioFiltro.setText("");
        campoDataFimFiltro.setText("");
        buscarInspecoesComFiltro();
    }
    
    private void buscarInspecoesComFiltro() {
        try {
            modeloMesclagens.setRowCount(0);
            String nomePeca = campoNomePecaFiltro.getText().trim();
            Date dataInicio = parseDate(campoDataInicioFiltro.getText());
            Date dataFim = parseDate(campoDataFimFiltro.getText());
            List<Inspecao> inspecoes = controlador.carregarInspecoes(nomePeca.isEmpty() ? null : nomePeca, dataInicio, dataFim);
            atualizarTabelaInspecoes(inspecoes);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar inspeções: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Date parseDate(String text) throws ParseException {
        return text.trim().isEmpty() ? null : dateFormat.parse(text.trim());
    }
    
    private void atualizarTabelaInspecoes(List<Inspecao> inspecoes) {
        modeloInspecoes.setRowCount(0);
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (inspecoes == null) return;
        for (Inspecao inspecao : inspecoes) {
            modeloInspecoes.addRow(new Object[]{
                inspecao.getId(),
                inspecao.getNomePeca(),
                inspecao.getDescricao(),
                displayFormat.format(inspecao.getDataCriacao())
            });
        }
    }
    
    private void atualizarTabelaMesclagens(List<Mesclagem> mesclagens) {
        modeloMesclagens.setRowCount(0);
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (mesclagens == null) return;
        for (Mesclagem mesclagem : mesclagens) {
            modeloMesclagens.addRow(new Object[]{
                mesclagem.getId(),
                displayFormat.format(mesclagem.getDataCaptura()),
                mesclagem.getCaminhoImagem() != null ? mesclagem.getCaminhoImagem() : "N/A",
                mesclagem.getCaminhoLocal() != null ? mesclagem.getCaminhoLocal() : "N/A"
            });
        }
    }
}