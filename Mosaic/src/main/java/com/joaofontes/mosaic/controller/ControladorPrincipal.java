package com.joaofontes.mosaic.controller;

import com.joaofontes.mosaic.DAO.DatabaseManager;
import com.joaofontes.mosaic.DAO.InspecaoDAO;
import com.joaofontes.mosaic.DAO.MesclagemDAO;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import com.joaofontes.mosaic.model.Inspecao;
import com.joaofontes.mosaic.model.Mesclagem;
import com.joaofontes.mosaic.util.ServicoArmazenamentoNuvem;
import com.joaofontes.mosaic.util.ServicoCaptura;
import com.joaofontes.mosaic.util.ServicoMesclagem;
import com.joaofontes.mosaic.view.JanelaPrincipal;
import com.joaofontes.mosaic.util.SeletorAreaCaptura;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ControladorPrincipal {
    private ConfiguracaoCaptura configuracao;
    private ServicoCaptura servicoCaptura;
    private ServicoArmazenamentoNuvem servicoNuvem;
    private ServicoMesclagem servicoMesclagem;
    private Inspecao inspecaoAtiva;
    private InspecaoDAO inspecaoDAO;
    private MesclagemDAO mesclagemDAO;

    private boolean estavaCapturandoAntesDeExibir = false;
    private static final String ARQUIVO_CONFIG_LOCAL = System.getProperty("user.home") + File.separator + ".mosaic_app_config.dat";
    private List<BufferedImage> imagensAcumuladas = new ArrayList<>();

    public ControladorPrincipal() {
        this.configuracao = new ConfiguracaoCaptura(); 
        carregarConfiguracao(); 
        this.servicoCaptura = new ServicoCaptura(this); 
        this.servicoNuvem = new ServicoArmazenamentoNuvem(this.configuracao.getCloudinaryUrl());
        this.servicoMesclagem = new ServicoMesclagem();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            if (conn != null) {
                this.inspecaoDAO = new InspecaoDAO(conn);
                this.mesclagemDAO = new MesclagemDAO(conn);
            } else {
                 JOptionPane.showMessageDialog(null, "Não foi possível conectar ao banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro crítico ao conectar ao banco: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void iniciarNovaInspecao(String nomePeca, String descricao) {
        if (nomePeca == null || nomePeca.trim().isEmpty()) {
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "O nome da peça não pode estar vazio.", "Dados Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.inspecaoAtiva = new Inspecao();
        this.inspecaoAtiva.setNomePeca(nomePeca);
        this.inspecaoAtiva.setDescricao(descricao);
        this.inspecaoAtiva.setDataCriacao(new Date());
        System.out.println("Nova inspeção iniciada: " + nomePeca);
        JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "Nova inspeção '" + nomePeca + "' foi iniciada. As próximas mesclagens serão associadas a ela.", "Inspeção Iniciada", JOptionPane.INFORMATION_MESSAGE);
    }

    public void iniciarCaptura() {
        if (configuracao.getAreaCaptura() == null) {
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "Área de captura não definida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (inspecaoAtiva == null) {
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "Por favor, inicie uma nova inspeção na aba 'Metadados' antes de capturar.", "Nenhuma Inspeção Ativa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        imagensAcumuladas.clear();
        servicoCaptura.iniciarCaptura(configuracao, configuracao.getAreaCaptura());
        estavaCapturandoAntesDeExibir = true; 
    }

    public void onNovaImagemDetectada(BufferedImage novaImagem) {
        if (inspecaoAtiva == null || !servicoCaptura.isCapturando()) return;

        imagensAcumuladas.add(novaImagem);
        System.out.println("Imagem " + imagensAcumuladas.size() + "/" + configuracao.getNumeroImagensParaMesclar() + " capturada para a inspeção '" + inspecaoAtiva.getNomePeca() + "'.");

        if (imagensAcumuladas.size() >= configuracao.getNumeroImagensParaMesclar()) {
            List<BufferedImage> imagensParaMesclar = new ArrayList<>(imagensAcumuladas);
            imagensAcumuladas.clear();
            SwingUtilities.invokeLater(() -> processarEsalvarLoteDeImagens(imagensParaMesclar));
        }
    }
    
    private void processarEsalvarLoteDeImagens(List<BufferedImage> loteDeImagens) {
        if (inspecaoAtiva == null) return;
        
        BufferedImage imagemMesclada = servicoMesclagem.mesclarImagens(loteDeImagens, configuracao);

        String nomeBaseArquivo = "merge_" + inspecaoAtiva.getId() + "_" + UUID.randomUUID().toString();
        String caminhoLocal = salvarImagemLocalmente(imagemMesclada, nomeBaseArquivo + ".png");
        String urlNuvem = salvarImagemNuvem(imagemMesclada, nomeBaseArquivo);

        if (caminhoLocal != null || urlNuvem != null) {
            salvarDadosDaMesclagem(caminhoLocal, urlNuvem);
        }

        if (configuracao.isExibicaoAutoHabilitada()) {
            exibirImagem(imagemMesclada);
        }
    }
    
    private void salvarDadosDaMesclagem(String caminhoLocal, String urlNuvem) {
        try {
            if (inspecaoAtiva.getId() == 0) {
                inspecaoDAO.salvarInspecao(inspecaoAtiva);
            }
            Mesclagem novaMesclagem = new Mesclagem();
            novaMesclagem.setIdInspecao(inspecaoAtiva.getId());
            novaMesclagem.setDataCaptura(new Date());
            novaMesclagem.setCaminhoLocal(caminhoLocal);
            novaMesclagem.setCaminhoImagem(urlNuvem);
            mesclagemDAO.salvarMesclagem(novaMesclagem);
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "Imagem mesclada salva na inspeção '" + inspecaoAtiva.getNomePeca() + "'.", "Salvamento Concluído", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "Erro ao salvar dados no banco: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String salvarImagemLocalmente(BufferedImage imagem, String nomeArquivo) {
        if (configuracao.getDiretorioCaptura() == null || configuracao.getDiretorioCaptura().isEmpty()) return null;
        try {
            File diretorio = new File(configuracao.getDiretorioCaptura());
            if (!diretorio.exists()) diretorio.mkdirs();
            File arquivoImagem = new File(diretorio, nomeArquivo);
            ImageIO.write(imagem, "png", arquivoImagem);
            return arquivoImagem.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    private String salvarImagemNuvem(BufferedImage imagem, String nomeArquivoPublico) {
        String url = configuracao.getCloudinaryUrl();
        if (url == null || url.isEmpty()) return null;
        if (servicoNuvem == null || !url.equals(servicoNuvem.getActiveCloudinaryUrl())) {
            servicoNuvem.reinitialize(url);
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(imagem, "png", baos);
            return servicoNuvem.uploadImagem(baos.toByteArray(), nomeArquivoPublico);
        } catch (IOException e) {
            return null;
        }
    }

    private void exibirImagem(BufferedImage imagem) {
        if (servicoCaptura.isCapturando()) {
            servicoCaptura.pararCaptura(); 
            estavaCapturandoAntesDeExibir = true;
        } else {
            estavaCapturandoAntesDeExibir = false;
        }
        SwingUtilities.invokeLater(() -> JanelaPrincipal.getInstance().exibirImagemMesclada(imagem));
    }

    public List<Inspecao> carregarInspecoes(String nomePeca, Date dataInicio, Date dataFim) throws SQLException {
        if (inspecaoDAO == null) throw new SQLException("DAO de Inspeção não inicializado.");
        return inspecaoDAO.buscarInspecoes(nomePeca, dataInicio, dataFim);
    }

    public List<Mesclagem> carregarMesclagensPorInspecao(int idInspecao) throws SQLException {
        if (mesclagemDAO == null) throw new SQLException("DAO de Mesclagem não inicializado.");
        return mesclagemDAO.buscarMesclagensPorInspecaoId(idInspecao);
    }
    
    public void pararCaptura() {
        servicoCaptura.pararCaptura();
        estavaCapturandoAntesDeExibir = false;
    }
    
    public void reiniciarCapturaPosExibicao() {
        if (estavaCapturandoAntesDeExibir && configuracao.getAreaCaptura() != null) {
            servicoCaptura.iniciarCaptura(configuracao, configuracao.getAreaCaptura());
        }
    }
    
    public boolean selecionarAreaCaptura() {
        SeletorAreaCaptura seletor = new SeletorAreaCaptura(JanelaPrincipal.getInstance());
        Rectangle areaSelecionada = seletor.getAreaSelecionada(); 
        if (areaSelecionada != null && areaSelecionada.width > 0 && areaSelecionada.height > 0) {
            configuracao.setAreaCaptura(areaSelecionada); return true;
        }
        configuracao.setAreaCaptura(null); return false;
    }
    
    public ConfiguracaoCaptura getConfiguracao() { return configuracao; }

    public void salvarConfiguracao() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_CONFIG_LOCAL))) {
            oos.writeObject(configuracao);
            if (servicoNuvem != null) servicoNuvem.reinitialize(configuracao.getCloudinaryUrl());
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "Configurações salvas!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), "Erro ao salvar configurações.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void carregarConfiguracao() {
        File configFile = new File(ARQUIVO_CONFIG_LOCAL);
        if (configFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFile))) {
                this.configuracao = (ConfiguracaoCaptura) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erro ao carregar configurações: " + e.getMessage());
            }
        }
    }
}