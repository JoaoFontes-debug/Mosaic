package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.SessaoCaptura;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PainelSessao extends JPanel {
    private ControladorPrincipal controlador;
    private JTable tabelaSessoes;
    private DefaultTableModel modeloTabela;

    private JTextField campoNomePecaFiltro;
    private JTextField campoDataInicioFiltro; 
    private JTextField campoDataFimFiltro;   
    private JButton btnBuscarSessoes;
    private JButton btnMostrarTodasSessoes;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public PainelSessao(ControladorPrincipal controlador) {
        this.controlador = controlador;
        dateFormat.setLenient(false); // Validação estrita de data
        initUI();
        carregarTodasSessoes(); 
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelFiltros = new JPanel(new GridBagLayout());
        painelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Busca de Sessões"));
        GridBagConstraints gbcFiltros = new GridBagConstraints();
        gbcFiltros.insets = new Insets(5, 5, 5, 5);
        gbcFiltros.anchor = GridBagConstraints.WEST;

        gbcFiltros.gridx = 0; gbcFiltros.gridy = 0;
        painelFiltros.add(new JLabel("Nome da Peça:"), gbcFiltros);
        campoNomePecaFiltro = new JTextField(20);
        gbcFiltros.gridx = 1; gbcFiltros.gridy = 0; gbcFiltros.fill = GridBagConstraints.HORIZONTAL; gbcFiltros.weightx = 0.5;
        painelFiltros.add(campoNomePecaFiltro, gbcFiltros);

        gbcFiltros.gridx = 0; gbcFiltros.gridy = 1; gbcFiltros.fill = GridBagConstraints.NONE; gbcFiltros.weightx = 0;
        painelFiltros.add(new JLabel("Data Início (dd/MM/yyyy):"), gbcFiltros);
        campoDataInicioFiltro = new JTextField(10);
        gbcFiltros.gridx = 1; gbcFiltros.gridy = 1; gbcFiltros.fill = GridBagConstraints.HORIZONTAL;
        painelFiltros.add(campoDataInicioFiltro, gbcFiltros);
        
        gbcFiltros.gridx = 2; gbcFiltros.gridy = 1; gbcFiltros.fill = GridBagConstraints.NONE; gbcFiltros.anchor = GridBagConstraints.EAST; gbcFiltros.weightx = 0.1;
        painelFiltros.add(new JLabel("Data Fim (dd/MM/yyyy):"), gbcFiltros);
        campoDataFimFiltro = new JTextField(10);
        gbcFiltros.gridx = 3; gbcFiltros.gridy = 1; gbcFiltros.fill = GridBagConstraints.HORIZONTAL; gbcFiltros.anchor = GridBagConstraints.WEST; gbcFiltros.weightx = 0.5;
        painelFiltros.add(campoDataFimFiltro, gbcFiltros);

        JPanel painelBotoesFiltro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Centralizado com espaçamento
        btnBuscarSessoes = new JButton("Buscar Sessões");
        btnBuscarSessoes.addActionListener(e -> buscarSessoesComFiltro());
        btnMostrarTodasSessoes = new JButton("Mostrar Todas");
        btnMostrarTodasSessoes.addActionListener(e -> carregarTodasSessoes());
        painelBotoesFiltro.add(btnBuscarSessoes);
        painelBotoesFiltro.add(btnMostrarTodasSessoes);
        
        gbcFiltros.gridx = 0; gbcFiltros.gridy = 2; gbcFiltros.gridwidth = 4; 
        gbcFiltros.anchor = GridBagConstraints.CENTER; gbcFiltros.fill = GridBagConstraints.NONE;
        painelFiltros.add(painelBotoesFiltro, gbcFiltros);

        add(painelFiltros, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome da Peça", "Descrição", "Data Captura", "URL Nuvem", "Caminho Local"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaSessoes = new JTable(modeloTabela);
        tabelaSessoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaSessoes.setAutoCreateRowSorter(true); 
        
        tabelaSessoes.getColumnModel().getColumn(0).setPreferredWidth(40); 
        tabelaSessoes.getColumnModel().getColumn(1).setPreferredWidth(150); 
        tabelaSessoes.getColumnModel().getColumn(2).setPreferredWidth(250); 
        tabelaSessoes.getColumnModel().getColumn(3).setPreferredWidth(150); 
        tabelaSessoes.getColumnModel().getColumn(4).setPreferredWidth(200); 
        tabelaSessoes.getColumnModel().getColumn(5).setPreferredWidth(200); 
        tabelaSessoes.setFillsViewportHeight(true); // Para melhor preenchimento vertical

        JScrollPane scrollPane = new JScrollPane(tabelaSessoes);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void buscarSessoesComFiltro() {
        String nomePeca = campoNomePecaFiltro.getText().trim();
        Date dataInicio = null;
        Date dataFim = null;
        
        try {
            String dataInicioStr = campoDataInicioFiltro.getText().trim();
            if (!dataInicioStr.isEmpty()) {
                dataInicio = dateFormat.parse(dataInicioStr);
            }
            String dataFimStr = campoDataFimFiltro.getText().trim();
            if (!dataFimStr.isEmpty()) {
                dataFim = dateFormat.parse(dataFimStr);
                // Ajustar dataFim para o final do dia (23:59:59) para incluir todas as sessões do dia
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataFim);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                dataFim = cal.getTime();
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, 
                                        "Formato de data inválido. Use dd/MM/yyyy.", 
                                        "Erro de Formato de Data", 
                                        JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (dataInicio != null && dataFim != null && dataInicio.after(dataFim)) {
            JOptionPane.showMessageDialog(this, 
                                        "A data de início não pode ser posterior à data de fim.", 
                                        "Erro de Intervalo de Datas", 
                                        JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<SessaoCaptura> sessoes = controlador.carregarSessoesDoBanco(
                nomePeca.isEmpty() ? null : nomePeca,
                dataInicio,
                dataFim
            );
            atualizarTabela(sessoes);
            if (sessoes.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Nenhuma sessão encontrada para os filtros aplicados.", "Busca de Sessões", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                                        "Erro ao buscar sessões no banco de dados: " + e.getMessage(), 
                                        "Erro de Banco de Dados", 
                                        JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void carregarTodasSessoes() {
        campoNomePecaFiltro.setText("");
        campoDataInicioFiltro.setText("");
        campoDataFimFiltro.setText("");
        try {
            List<SessaoCaptura> sessoes = controlador.carregarTodasSessoesDoBanco();
            atualizarTabela(sessoes);
             if (sessoes.isEmpty()) {
                 System.out.println("Nenhuma sessão encontrada no banco de dados ao carregar todas.");
                 // Pode-se optar por não mostrar JOptionPane aqui para não ser intrusivo na inicialização
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                                        "Erro ao carregar todas as sessões do banco: " + e.getMessage(), 
                                        "Erro de Banco de Dados", 
                                        JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void atualizarTabela(List<SessaoCaptura> sessoes) {
        modeloTabela.setRowCount(0); 
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (sessoes != null) {
            for (SessaoCaptura sessao : sessoes) {
                modeloTabela.addRow(new Object[]{
                    sessao.getId(),
                    sessao.getNomePeca(),
                    sessao.getDescricao(),
                    sessao.getDataCaptura() != null ? displayDateFormat.format(sessao.getDataCaptura()) : "N/A",
                    sessao.getCaminhoImagem() != null ? sessao.getCaminhoImagem() : "N/A",
                    sessao.getCaminhoLocal() != null ? sessao.getCaminhoLocal() : "N/A"
                });
            }
        }
    }
}
