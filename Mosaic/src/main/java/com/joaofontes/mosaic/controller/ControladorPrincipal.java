package com.joaofontes.mosaic.controller;

import com.joaofontes.mosaic.DAO.SessaoDAO;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import com.joaofontes.mosaic.model.SessaoCaptura;
import com.joaofontes.mosaic.util.GerenciadorConexao;
import com.joaofontes.mosaic.util.SeletorAreaCaptura;
import com.joaofontes.mosaic.util.ServicoArmazenamentoNuvem;
import com.joaofontes.mosaic.util.ServicoCaptura;
import com.joaofontes.mosaic.view.JanelaPrincipal;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 *
 * @author JoãoFontes
 */
public class ControladorPrincipal {

    private final ConfiguracaoCaptura configuracao = new ConfiguracaoCaptura();
    private final ServicoCaptura servicoCaptura = new ServicoCaptura(this);
    private final ServicoArmazenamentoNuvem servicoNuvem = new ServicoArmazenamentoNuvem();
    private SessaoDAO sessaoDao;
    private Rectangle areaCaptura;
    private String nomePeca;
    private String descricaoPeca;

    public ControladorPrincipal() {
        try {
            Connection conexao = GerenciadorConexao.getConexao();
            sessaoDao = new SessaoDAO(conexao);
        } catch (SQLException ex) {
            System.err.println("Erro ao conectar ao banco: " + ex.getMessage());
        }
    }

    public void iniciarCaptura() {
        servicoCaptura.iniciarCaptura(configuracao, areaCaptura);
    }

    public void pararCaptura() {
        servicoCaptura.pararCaptura();
    }

    public void salvarConfiguracao() {
        new SeletorAreaCaptura(this).exibir();
    }

    public void selecionarAreaCaptura() {
        // Implementar lógica para seleção de área
    }

    public void setMetadadosSessao(String nomePeca, String descricao) {
        this.nomePeca = nomePeca;
        this.descricaoPeca = descricao;
    }

    public void setDiretorioCaptura(String diretorio) {
        configuracao.setDiretorioCaptura(diretorio);
    }

    public ConfiguracaoCaptura getConfiguracao() {
        return configuracao;
    }

    public void processarImagensMescladas(BufferedImage imagem) {
        // Salvar localmente se configurado
        if (configuracao.getDiretorioCaptura() != null) {
            salvarImagemLocalmente(imagem);
        }

        // Salvar na nuvem
        String urlNuvem = salvarImagemNuvem(imagem);

        // Exibir automaticamente
        if (configuracao.isExibicaoAutoHabilitada()) {
            SwingUtilities.invokeLater(() -> {
                JanelaPrincipal.getInstance().exibirImagemMesclada(imagem);
            });
        }

        // Salvar sessão com metadados
        salvarSessao(urlNuvem);
    }

    private void salvarImagemLocalmente(BufferedImage imagem) {
        try {
            File diretorio = new File(configuracao.getDiretorioCaptura());
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            File arquivo = new File(diretorio, "mesclagem_" + System.currentTimeMillis() + ".png");
            ImageIO.write(imagem, "PNG", arquivo);
        } catch (IOException ex) {
            System.err.println("Erro ao salvar localmente: " + ex.getMessage());
        }
    }

    private String salvarImagemNuvem(BufferedImage imagem) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagem, "PNG", baos);
            return servicoNuvem.uploadImagem(baos.toByteArray());
        } catch (IOException ex) {
            System.err.println("Erro ao salvar na nuvem: " + ex.getMessage());
            return null;
        }
    }

    private void salvarSessao(String urlImagem) {
        SessaoCaptura sessao = new SessaoCaptura();
        sessao.setNomePeca(nomePeca);
        sessao.setDescricao(descricaoPeca);
        sessao.setDataCaptura(new java.util.Date());
        sessao.setCaminhoImagem(urlImagem);

        try {
            sessaoDao.salvarSessao(sessao);
        } catch (SQLException ex) {
            System.err.println("Erro ao salvar sessão: " + ex.getMessage());
        }
    }
}
