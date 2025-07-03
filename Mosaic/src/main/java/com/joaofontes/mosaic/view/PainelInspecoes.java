package com.joaofontes.mosaic.view;

import com.joaofontes.mosaic.controller.ControladorPrincipal;
import com.joaofontes.mosaic.model.Inspecao;
import com.joaofontes.mosaic.model.Mesclagem;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

        // ALTERAÇÃO CRÍTICA: Inicializa os componentes da tabela AQUI,
        // antes de qualquer método que os utilize.
        tabelaInspecoes = new JTable();
        tabelaMesclagens = new JTable();

        add(criarPainelFiltros(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(criarPainelTabelaInspecoes());
        splitPane.setBottomComponent(criarPainelTabelaMesclagens());
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // Agora que as tabelas existem, podemos adicionar os listeners
        criarEMostrarMenuDeCopia();
    }

    private void criarEMostrarMenuDeCopia() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemCopiar = new JMenuItem("Copiar");
        popupMenu.add(itemCopiar);
        itemCopiar.addActionListener(ae -> {
            JTable tabelaFonte = (JTable) popupMenu.getInvoker();
            int linha = tabelaFonte.getSelectedRow();
            int coluna = tabelaFonte.getSelectedColumn();

            if (linha != -1 && coluna != -1) {
                Object valor = tabelaFonte.getValueAt(linha, coluna);
                String texto = (valor == null) ? "" : valor.toString();
                StringSelection selecao = new StringSelection(texto);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selecao, null);
            }
        });

        MouseAdapter adaptadorMouse = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JTable tabelaFonte = (JTable) e.getSource();
                    int linha = tabelaFonte.rowAtPoint(e.getPoint());
                    int coluna = tabelaFonte.columnAtPoint(e.getPoint());
                    if (linha >= 0 && coluna >= 0) {
                        tabelaFonte.changeSelection(linha, coluna, false, false);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        tabelaInspecoes.addMouseListener(adaptadorMouse);
        tabelaMesclagens.addMouseListener(adaptadorMouse);
    }

    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Filtros de Busca de Inspeções"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painel.add(new JLabel("Nome da Peça:"), gbc);
        campoNomePecaFiltro = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        painel.add(campoNomePecaFiltro, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        painel.add(new JLabel("Data Início:"), gbc);
        campoDataInicioFiltro = new JTextField(12);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        painel.add(campoDataInicioFiltro, gbc);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        painel.add(new JLabel("Data Fim:"), gbc);
        campoDataFimFiltro = new JTextField(12);
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        painel.add(campoDataFimFiltro, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarInspecoesComFiltro());
        JButton btnMostrarTodas = new JButton("Mostrar Todas");
        btnMostrarTodas.addActionListener(e -> carregarTodasInspecoes());
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnMostrarTodas);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(painelBotoes, gbc);

        return painel;
    }

    private JPanel criarPainelTabelaInspecoes() {
        JPanel painel = new JPanel(new BorderLayout(0, 5));
        String[] colunas = {"ID", "Nome da Peça", "Descrição", "Operador", "Data de Criação"};
        modeloInspecoes = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // Agora esta linha é segura, porque tabelaInspecoes já foi inicializada.
        tabelaInspecoes.setModel(modeloInspecoes);

        tabelaInspecoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaInspecoes.getSelectionModel().addListSelectionListener(this::onInspecaoSelecionada);
        JScrollPane scrollPane = new JScrollPane(tabelaInspecoes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inspeções"));

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnApagarInspecao = new JButton("Apagar Inspeção Selecionada");
        btnApagarInspecao.addActionListener(e -> apagarInspecaoSelecionada());
        painelAcoes.add(btnApagarInspecao);
        painel.add(scrollPane, BorderLayout.CENTER);
        painel.add(painelAcoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelTabelaMesclagens() {
        JPanel painel = new JPanel(new BorderLayout(0, 5));
        String[] colunas = {"ID", "Data da Captura", "URL Nuvem", "Caminho Local"};
        modeloMesclagens = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // Agora esta linha é segura, porque tabelaMesclagens já foi inicializada.
        tabelaMesclagens.setModel(modeloMesclagens);

        JScrollPane scrollPane = new JScrollPane(tabelaMesclagens);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Imagens Mescladas da Inspeção Selecionada"));

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAbrirMesclagem = new JButton("Abrir Imagem Selecionada");
        btnAbrirMesclagem.addActionListener(e -> abrirMesclagemSelecionada());

        JButton btnApagarMesclagem = new JButton("Apagar Mesclagem Selecionada");
        btnApagarMesclagem.addActionListener(e -> apagarMesclagemSelecionada());

        painelAcoes.add(btnAbrirMesclagem);
        painelAcoes.add(btnApagarMesclagem);
        painel.add(scrollPane, BorderLayout.CENTER);
        painel.add(painelAcoes, BorderLayout.SOUTH);
        return painel;
    }

    private void onInspecaoSelecionada(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting() && tabelaInspecoes.getSelectedRow() != -1) {
            int selectedRow = tabelaInspecoes.convertRowIndexToModel(tabelaInspecoes.getSelectedRow());
            int idInspecao = (Integer) modeloInspecoes.getValueAt(selectedRow, 0);
            carregarMesclagens(idInspecao);
        } else {
            if (modeloMesclagens != null) {
                modeloMesclagens.setRowCount(0);
            }
        }
    }

    private void apagarInspecaoSelecionada() {
        int linhaSelecionada = tabelaInspecoes.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma inspeção para apagar.", "Nenhuma Seleção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int idInspecao = (Integer) modeloInspecoes.getValueAt(linhaSelecionada, 0);
        String nomePeca = (String) modeloInspecoes.getValueAt(linhaSelecionada, 1);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem a certeza que deseja apagar a inspeção '" + nomePeca + "' e TODAS as suas imagens mescladas?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            controlador.deletarInspecao(idInspecao);
            carregarTodasInspecoes();
        }
    }

    private void apagarMesclagemSelecionada() {
        int linhaMesclagemSelecionada = tabelaMesclagens.getSelectedRow();
        if (linhaMesclagemSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma imagem mesclada para apagar.", "Nenhuma Seleção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int idMesclagem = (Integer) modeloMesclagens.getValueAt(linhaMesclagemSelecionada, 0);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem a certeza que deseja apagar esta imagem mesclada?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            controlador.deletarMesclagem(idMesclagem);
            int linhaInspecaoSelecionada = tabelaInspecoes.getSelectedRow();
            if (linhaInspecaoSelecionada != -1) {
                int idInspecao = (Integer) modeloInspecoes.getValueAt(linhaInspecaoSelecionada, 0);
                carregarMesclagens(idInspecao);
            }
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
            if (modeloMesclagens != null) {
                modeloMesclagens.setRowCount(0);
            }
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
        if (inspecoes == null) {
            return;
        }
        for (Inspecao inspecao : inspecoes) {
            modeloInspecoes.addRow(new Object[]{
                inspecao.getId(),
                inspecao.getNomePeca(),
                inspecao.getDescricao(),
                inspecao.getNomeOperador(),
                displayFormat.format(inspecao.getDataCriacao())
            });
        }
    }

    private void atualizarTabelaMesclagens(List<Mesclagem> mesclagens) {
        modeloMesclagens.setRowCount(0);
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (mesclagens == null) {
            return;
        }
        for (Mesclagem mesclagem : mesclagens) {
            modeloMesclagens.addRow(new Object[]{
                mesclagem.getId(),
                displayFormat.format(mesclagem.getDataCaptura()),
                mesclagem.getCaminhoImagem() != null ? mesclagem.getCaminhoImagem() : "N/A",
                mesclagem.getCaminhoLocal() != null ? mesclagem.getCaminhoLocal() : "N/A"
            });
        }
    }

    private void abrirMesclagemSelecionada() {
        int linhaSelecionada = tabelaMesclagens.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma imagem mesclada para abrir.", "Nenhuma Seleção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (Integer) modeloMesclagens.getValueAt(linhaSelecionada, 0);
        String urlNuvem = (String) modeloMesclagens.getValueAt(linhaSelecionada, 2);
        String caminhoLocal = (String) modeloMesclagens.getValueAt(linhaSelecionada, 3);

        Mesclagem mesclagem = new Mesclagem();
        mesclagem.setId(id);
        mesclagem.setCaminhoImagem(urlNuvem);
        mesclagem.setCaminhoLocal(caminhoLocal);

        controlador.abrirMesclagemSelecionada(mesclagem);
    }
}
