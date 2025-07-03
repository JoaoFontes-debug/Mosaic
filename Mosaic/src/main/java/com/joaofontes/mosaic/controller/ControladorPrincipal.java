package com.joaofontes.mosaic.controller;

import com.joaofontes.mosaic.DAO.DatabaseManager;
import com.joaofontes.mosaic.DAO.InspecaoDAO;
import com.joaofontes.mosaic.DAO.MesclagemDAO;
import com.joaofontes.mosaic.DAO.UsuarioDAO;
import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import com.joaofontes.mosaic.model.Inspecao;
import com.joaofontes.mosaic.model.Mesclagem;
import com.joaofontes.mosaic.model.Usuario;
import com.joaofontes.mosaic.service.ServicoArmazenamentoNuvem;
import com.joaofontes.mosaic.service.ServicoCaptura;
import com.joaofontes.mosaic.service.ServicoEmail;
import com.joaofontes.mosaic.service.ServicoMesclagem;
import com.joaofontes.mosaic.view.JanelaPrincipal;
import com.joaofontes.mosaic.service.SeletorAreaCaptura;
import com.joaofontes.mosaic.view.PainelControleFlutuante;
import org.mindrot.jbcrypt.BCrypt;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class ControladorPrincipal {

    private ConfiguracaoCaptura configuracao;
    private final ServicoCaptura servicoCaptura;
    private ServicoArmazenamentoNuvem servicoNuvem;
    private final ServicoMesclagem servicoMesclagem;
    private final ServicoEmail servicoEmail;
    private Inspecao inspecaoAtiva;
    private InspecaoDAO inspecaoDAO;
    private MesclagemDAO mesclagemDAO;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;
    private JanelaPrincipal janelaPrincipal;
    private PainelControleFlutuante painelFlutuante;
    private Timer restartDelayTimer;
    private static final String ARQUIVO_CONFIG_LOCAL = System.getProperty("user.home") + File.separator + ".mosaic_app_config.dat";
    private final List<BufferedImage> imagensAcumuladas = new ArrayList<>();

    public ControladorPrincipal() {
        this.configuracao = new ConfiguracaoCaptura();
        carregarConfiguracao();
        this.servicoCaptura = new ServicoCaptura(this);
        this.servicoNuvem = new ServicoArmazenamentoNuvem(this.configuracao.getCloudinaryUrl());
        this.servicoMesclagem = new ServicoMesclagem();
        this.servicoEmail = new ServicoEmail();

        try {
            Connection conn = DatabaseManager.getConnection();
            if (conn != null) {
                this.inspecaoDAO = new InspecaoDAO(conn);
                this.mesclagemDAO = new MesclagemDAO(conn);
                this.usuarioDAO = new UsuarioDAO(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro crítico ao conectar ao banco: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setJanelaPrincipal(JanelaPrincipal janela) {
        this.janelaPrincipal = janela;
    }

    public boolean autenticarUsuario(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        try {
            Usuario usuario = usuarioDAO.buscarPorEmail(email);
            if (usuario != null && BCrypt.checkpw(password, usuario.getPasswordHash())) {
                this.usuarioLogado = usuario;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro de banco de dados ao tentar autenticar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean criarUsuario(String nome, String email, String password) {
        try {
            if (usuarioDAO.buscarPorEmail(email) != null) {
                JOptionPane.showMessageDialog(null, "Este e-mail já está em uso.", "Erro", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            boolean primeiroUsuario = (usuarioDAO.contarUsuarios() == 0);
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNomeCompleto(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setPasswordHash(hashedPassword);
            if (primeiroUsuario) {
                novoUsuario.setNivelAcesso("ADMIN");
                System.out.println("Nenhum utilizador encontrado. O novo utilizador '" + email + "' será definido como ADMIN.");
            } else {
                novoUsuario.setNivelAcesso("OPERADOR");
                System.out.println("Utilizadores existentes encontrados. O novo utilizador '" + email + "' será definido como OPERADOR.");
            }
            usuarioDAO.salvarUsuario(novoUsuario);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro de banco de dados ao criar utilizador.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean iniciarRecuperacaoSenha(String email) {
        try {
            Usuario user = usuarioDAO.buscarPorEmail(email);
            if (user == null) {
                return true;
            }
            String token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 15);
            Date expiryDate = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            usuarioDAO.salvarTokenRecuperacao(email, token, sdf.format(expiryDate));
            return servicoEmail.enviarEmailRecuperacao(email, token);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro de banco de dados ao iniciar recuperação.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean finalizarRecuperacaoSenha(String token, String novaSenha) {
        try {
            Usuario user = usuarioDAO.buscarPorTokenRecuperacao(token);
            if (user == null) {
                JOptionPane.showMessageDialog(null, "Token inválido ou expirado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date expiryDate = sdf.parse(user.getTokenExpiresAt());
            if (new Date().after(expiryDate)) {
                JOptionPane.showMessageDialog(null, "Token expirado. Por favor, solicite um novo.", "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String novoHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
            usuarioDAO.atualizarSenhaEInvalidarToken(user.getId(), novoHash);
            return true;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao finalizar a recuperação de senha.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isUsuarioAdmin() {
        return this.usuarioLogado != null && "ADMIN".equals(this.usuarioLogado.getNivelAcesso());
    }

    public List<Usuario> carregarTodosUsuarios() {
        if (!isUsuarioAdmin()) {
            throw new SecurityException("Acesso negado. Apenas administradores podem carregar utilizadores.");
        }
        try {
            return usuarioDAO.buscarTodosUsuarios();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar lista de utilizadores: " + e.getMessage(), e);
        }
    }

    public void removerUsuario(int id) {
        if (!isUsuarioAdmin()) {
            throw new SecurityException("Acesso negado.");
        }
        if (usuarioLogado.getId() == id) {
            throw new IllegalArgumentException("Não pode remover a sua própria conta.");
        }
        try {
            usuarioDAO.removerUsuario(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover utilizador: " + e.getMessage(), e);
        }
    }

    public void atualizarNivelAcesso(int id, String novoNivel) {
        if (!isUsuarioAdmin()) {
            throw new SecurityException("Acesso negado.");
        }
        if (usuarioLogado.getId() == id && !"ADMIN".equals(novoNivel)) {
            throw new IllegalArgumentException("Não pode rebaixar a sua própria conta de administrador.");
        }
        try {
            usuarioDAO.atualizarNivelAcesso(id, novoNivel);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar nível de acesso: " + e.getMessage(), e);
        }
    }

    public void resetarSenha(int id, String novaSenha) {
        if (!isUsuarioAdmin()) {
            throw new SecurityException("Acesso negado.");
        }
        try {
            String novoHash = BCrypt.hashpw(novaSenha, BCrypt.gensalt());
            usuarioDAO.resetarSenha(id, novoHash);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao resetar senha: " + e.getMessage(), e);
        }
    }

    private PainelControleFlutuante getPainelFlutuante() {
        if (this.painelFlutuante == null) {
            this.painelFlutuante = new PainelControleFlutuante(this.janelaPrincipal, this);
        }
        return this.painelFlutuante;
    }

    public void iniciarProcessoDeCaptura() {
        if (this.janelaPrincipal == null) {
            return;
        }
        this.janelaPrincipal.setExtendedState(Frame.ICONIFIED);
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            boolean areaSelecionada = selecionarAreaCaptura();
            if (areaSelecionada) {
                iniciarCaptura();
            } else {
                restaurarJanelaPrincipal();
            }
        });
    }

    public void iniciarCaptura() {
        if (configuracao.getAreaCaptura() == null) {
            JOptionPane.showMessageDialog(null, "Área de captura não definida.", "Erro", JOptionPane.ERROR_MESSAGE);
            restaurarJanelaPrincipal();
            return;
        }
        if (inspecaoAtiva == null) {
            JOptionPane.showMessageDialog(null, "Por favor, inicie uma nova inspeção na aba 'Metadados'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            restaurarJanelaPrincipal();
            return;
        }
        imagensAcumuladas.clear();
        servicoCaptura.iniciarCaptura(configuracao, configuracao.getAreaCaptura());
        getPainelFlutuante().setStatus("Capturando 0 de " + configuracao.getNumeroImagensParaMesclar() + "...");
        getPainelFlutuante().setVisivel(true);
    }

    public void pararCaptura() {
        servicoCaptura.pararCaptura();
        if (restartDelayTimer != null && restartDelayTimer.isRunning()) {
            restartDelayTimer.stop();
        }
        getPainelFlutuante().setVisivel(false);
        restaurarJanelaPrincipal();
    }

    public void onNovaImagemDetectada(BufferedImage novaImagem) {
        if (inspecaoAtiva == null || !servicoCaptura.isCapturando()) {
            return;
        }
        imagensAcumuladas.add(novaImagem);
        String status = "Capturando " + imagensAcumuladas.size() + " de " + configuracao.getNumeroImagensParaMesclar() + "...";
        getPainelFlutuante().setStatus(status);
        if (imagensAcumuladas.size() >= configuracao.getNumeroImagensParaMesclar()) {
            servicoCaptura.pararCaptura();
            getPainelFlutuante().setStatus("Processando...");
            List<BufferedImage> imagensParaMesclar = new ArrayList<>(imagensAcumuladas);
            imagensAcumuladas.clear();
            SwingUtilities.invokeLater(() -> processarEsalvarLoteDeImagens(imagensParaMesclar));
        }
    }

    private void processarEsalvarLoteDeImagens(List<BufferedImage> loteDeImagens) {
        if (inspecaoAtiva == null) {
            restaurarJanelaPrincipal();
            return;
        }
        getPainelFlutuante().setVisivel(false);
        BufferedImage imagemMesclada = servicoMesclagem.mesclarImagens(loteDeImagens, configuracao);
        String nomeBaseArquivo = "merge_" + inspecaoAtiva.getId() + "_" + UUID.randomUUID().toString();
        String caminhoLocal = salvarImagemLocalmente(imagemMesclada, nomeBaseArquivo + ".png");
        String urlNuvem = salvarImagemNuvem(imagemMesclada, nomeBaseArquivo);
        if (caminhoLocal != null || urlNuvem != null) {
            salvarDadosDaMesclagem(caminhoLocal, urlNuvem);
        }
        if (configuracao.isExibicaoAutoHabilitada()) {
            exibirImagem(imagemMesclada);
        } else {
            iniciarContagemParaReiniciar();
        }
    }

    private void exibirImagem(BufferedImage imagem) {
        if (this.janelaPrincipal == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> this.janelaPrincipal.exibirImagemMesclada(imagem));
    }

    public void iniciarContagemParaReiniciar() {
        int delaySegundos = configuracao.getAtrasoReiniciarCaptura();
        if (delaySegundos <= 0) {
            reiniciarCaptura();
            return;
        }
        getPainelFlutuante().setVisivel(true);
        final AtomicInteger countdown = new AtomicInteger(delaySegundos);
        getPainelFlutuante().setStatus("Reiniciando em " + countdown.get() + "s...");
        if (restartDelayTimer != null && restartDelayTimer.isRunning()) {
            restartDelayTimer.stop();
        }
        restartDelayTimer = new Timer(1000, e -> {
            int tempoRestante = countdown.decrementAndGet();
            getPainelFlutuante().setStatus("Reiniciando em " + tempoRestante + "s...");
            if (tempoRestante <= 0) {
                ((Timer) e.getSource()).stop();
                getPainelFlutuante().setVisivel(false);
                reiniciarCaptura();
            }
        });
        restartDelayTimer.setRepeats(true);
        restartDelayTimer.start();
    }

    private void reiniciarCaptura() {
        if (configuracao.getAreaCaptura() != null) {
            iniciarCaptura();
        } else {
            restaurarJanelaPrincipal();
        }
    }

    private void restaurarJanelaPrincipal() {
        if (this.janelaPrincipal == null) {
            return;
        }
        this.janelaPrincipal.setExtendedState(Frame.NORMAL);
        this.janelaPrincipal.toFront();
        this.janelaPrincipal.requestFocus();
    }

    public void iniciarNovaInspecao(String nome, String desc) {
        if (nome == null || nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O nome da peça não pode estar vazio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (this.usuarioLogado == null) {
            JOptionPane.showMessageDialog(null, "Erro: Nenhum utilizador está logado. Não é possível iniciar a inspeção.", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.inspecaoAtiva = new Inspecao();
        this.inspecaoAtiva.setNomePeca(nome);
        this.inspecaoAtiva.setDescricao(desc);
        this.inspecaoAtiva.setDataCriacao(new Date());
        this.inspecaoAtiva.setNomeOperador(this.usuarioLogado.getNomeCompleto());

        JOptionPane.showMessageDialog(null, "Nova inspeção '" + nome + "' iniciada pelo operador " + this.usuarioLogado.getNomeCompleto() + ".", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void salvarDadosDaMesclagem(String cLocal, String cNuvem) {
        try {
            if (inspecaoAtiva.getId() == 0) {
                inspecaoDAO.salvarInspecao(inspecaoAtiva);
            }
            Mesclagem m = new Mesclagem();
            m.setIdInspecao(inspecaoAtiva.getId());
            m.setDataCaptura(new Date());
            m.setCaminhoLocal(cLocal);
            m.setCaminhoImagem(cNuvem);
            mesclagemDAO.salvarMesclagem(m);
            JOptionPane.showMessageDialog(null, "Imagem salva na inspeção '" + inspecaoAtiva.getNomePeca() + "'.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao salvar no banco: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletarInspecao(int idInspecao) {
        try {
            inspecaoDAO.deletarPorId(idInspecao);
            JOptionPane.showMessageDialog(null, "Inspeção e todas as suas mesclagens foram apagadas com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao apagar inspeção: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletarMesclagem(int idMesclagem) {
        try {
            mesclagemDAO.deletarPorId(idMesclagem);
            JOptionPane.showMessageDialog(null, "Mesclagem apagada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao apagar mesclagem: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String salvarImagemLocalmente(BufferedImage i, String n) {
        if (configuracao.getDiretorioCaptura() == null || configuracao.getDiretorioCaptura().isEmpty()) {
            return null;
        }
        try {
            File d = new File(configuracao.getDiretorioCaptura());
            if (!d.exists()) {
                d.mkdirs();
            }
            File f = new File(d, n);
            ImageIO.write(i, "png", f);
            return f.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    private String salvarImagemNuvem(BufferedImage i, String n) {
        String u = configuracao.getCloudinaryUrl();
        if (u == null || u.isEmpty()) {
            return null;
        }
        if (servicoNuvem == null || !u.equals(servicoNuvem.getActiveCloudinaryUrl())) {
            servicoNuvem.reinitialize(u);
        }
        try (ByteArrayOutputStream o = new ByteArrayOutputStream()) {
            ImageIO.write(i, "png", o);
            return servicoNuvem.uploadImagem(o.toByteArray(), n);
        } catch (IOException e) {
            return null;
        }
    }

    public List<Inspecao> carregarInspecoes(String n, Date d1, Date d2) throws SQLException {
        return inspecaoDAO.buscarInspecoes(n, d1, d2);
    }

    public List<Mesclagem> carregarMesclagensPorInspecao(int id) throws SQLException {
        return mesclagemDAO.buscarMesclagensPorInspecaoId(id);
    }

    public boolean selecionarAreaCaptura() {
        if (this.janelaPrincipal == null) {
            JOptionPane.showMessageDialog(null, "Erro: A janela principal não foi inicializada.", "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        SeletorAreaCaptura s = new SeletorAreaCaptura(this.janelaPrincipal);
        Rectangle r = s.getAreaSelecionada();
        if (r != null) {
            configuracao.setAreaCaptura(r);
            return true;
        }
        configuracao.setAreaCaptura(null);
        return false;
    }

    public void abrirMesclagemSelecionada(Mesclagem mesclagem) {
        String caminhoLocal = mesclagem.getCaminhoLocal();
        if (caminhoLocal != null && !caminhoLocal.isEmpty()) {
            File ficheiro = new File(caminhoLocal);
            if (ficheiro.exists()) {
                try {
                    Desktop.getDesktop().open(ficheiro);
                    return;
                } catch (IOException e) {
                    System.err.println("Erro ao tentar abrir o ficheiro local: " + caminhoLocal);
                    e.printStackTrace();
                }
            }
        }

        String urlNuvem = mesclagem.getCaminhoImagem();
        if (urlNuvem != null && !urlNuvem.isEmpty()) {
            try {
                Desktop.getDesktop().browse(new URI(urlNuvem));
                return;
            } catch (IOException | URISyntaxException e) {
                System.err.println("Erro ao tentar abrir a URL da nuvem: " + urlNuvem);
                e.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(null, "Não foi possível abrir a imagem. Nem o caminho local nem a URL da nuvem estão disponíveis ou são válidos.", "Erro ao Abrir Imagem", JOptionPane.ERROR_MESSAGE);
    }

    public ConfiguracaoCaptura getConfiguracao() {
        return configuracao;
    }

    public void salvarConfiguracao() {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(ARQUIVO_CONFIG_LOCAL))) {
            o.writeObject(configuracao);
            if (servicoNuvem != null) {
                servicoNuvem.reinitialize(configuracao.getCloudinaryUrl());
            }
            JOptionPane.showMessageDialog(null, "Configurações salvas!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar configurações.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void carregarConfiguracao() {
        File f = new File(ARQUIVO_CONFIG_LOCAL);
        if (f.exists()) {
            try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(f))) {
                this.configuracao = (ConfiguracaoCaptura) o.readObject();
            } catch (Exception e) {
                System.err.println("Erro ao carregar configs: " + e.getMessage());
            }
        }
    }
}
