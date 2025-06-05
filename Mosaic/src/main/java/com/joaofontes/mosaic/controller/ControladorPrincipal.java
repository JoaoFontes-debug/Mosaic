package com.joaofontes.mosaic.controller;

import com.joaofontes.mosaic.model.ConfiguracaoCaptura;
import com.joaofontes.mosaic.model.SessaoCaptura;
import com.joaofontes.mosaic.util.ServicoCaptura; 
import com.joaofontes.mosaic.util.ServicoArmazenamentoNuvem;
import com.joaofontes.mosaic.view.JanelaPrincipal;
import com.joaofontes.mosaic.util.SeletorAreaCaptura; 
import com.joaofontes.mosaic.DAO.SessaoDAO; 
// IMPORTANTE: Certifique-se de que sua classe DatabaseManager para MySQL está neste pacote
// e fornece o método estático getConnection()
import com.joaofontes.mosaic.util.DatabaseManager; 
import com.joaofontes.mosaic.util.DatabaseManager;
import com.joaofontes.mosaic.util.SeletorAreaCaptura;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;


public class ControladorPrincipal {
    private ConfiguracaoCaptura configuracao;
    private ServicoCaptura servicoCaptura;
    private ServicoArmazenamentoNuvem servicoNuvem;
    private SessaoCaptura sessaoAtual; 
    private SessaoDAO sessaoDao;

    private boolean estavaCapturandoAntesDeExibir = false;
    private static final String ARQUIVO_CONFIG_LOCAL = System.getProperty("user.home") + File.separator + ".mosaic_app_config.dat";


    public ControladorPrincipal() {
        this.configuracao = new ConfiguracaoCaptura(); // Cria uma configuração padrão inicial
        carregarConfiguracao(); // Tenta carregar do arquivo, sobrescrevendo a padrão se sucesso
        
        this.servicoCaptura = new ServicoCaptura(this); 
        // Inicializa servicoNuvem com a URL da configuração (pode ser nula inicialmente)
        this.servicoNuvem = new ServicoArmazenamentoNuvem(this.configuracao.getCloudinaryUrl());
        this.sessaoAtual = new SessaoCaptura(); 
        
        try {
            // IMPORTANTE: DatabaseManager.getConnection() deve retornar uma conexão MySQL válida
            // Certifique-se que sua classe DatabaseManager está configurada corretamente para MySQL.
            Connection conn = DatabaseManager.getConnection();
            if (conn != null) {
                this.sessaoDao = new SessaoDAO(conn);
            } else {
                 System.err.println("ControladorPrincipal: Falha ao obter conexão do DatabaseManager. SessaoDAO não inicializado.");
                 JOptionPane.showMessageDialog(null, 
                                        "Não foi possível conectar ao banco de dados MySQL.\nAs funcionalidades de sessão não estarão disponíveis.",
                                        "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            System.err.println("ControladorPrincipal: Erro SQL ao obter conexão ou inicializar SessaoDAO: " + e.getMessage());
            e.printStackTrace(); // Para debug
            JOptionPane.showMessageDialog(null, 
                                        "Erro crítico ao conectar ao banco de dados MySQL: " + e.getMessage() +
                                        "\nVerifique as configurações do banco e se o servidor MySQL está em execução.",
                                        "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ConfiguracaoCaptura getConfiguracao() {
        return configuracao;
    }

    public SessaoCaptura getSessaoAtual() {
        return sessaoAtual;
    }
    
    public void setMetadadosSessao(String nomePeca, String descricao) {
        sessaoAtual.setNomePeca(nomePeca);
        sessaoAtual.setDescricao(descricao);
        System.out.println("Metadados da sessão atualizados: " + nomePeca);
    }

    public boolean selecionarAreaCaptura() {
        SeletorAreaCaptura seletor = new SeletorAreaCaptura(JanelaPrincipal.getInstance());
        Rectangle areaSelecionada = seletor.getAreaSelecionada(); 

        if (areaSelecionada != null && areaSelecionada.width > 0 && areaSelecionada.height > 0) {
            configuracao.setAreaCaptura(areaSelecionada);
            System.out.println("Área de captura selecionada: " + areaSelecionada);
            return true;
        } else {
            System.out.println("Seleção de área cancelada ou inválida.");
            configuracao.setAreaCaptura(null); // Garante que nenhuma área antiga permaneça
            return false;
        }
    }

    public void iniciarCaptura() {
        if (configuracao.getAreaCaptura() == null) {
            JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), 
                                        "Área de captura não definida. Selecione uma área primeiro clicando em 'Iniciar Captura'.", 
                                        "Erro ao Iniciar Captura", 
                                        JOptionPane.ERROR_MESSAGE);
            return;
        }
        servicoCaptura.iniciarCaptura(configuracao, configuracao.getAreaCaptura());
        estavaCapturandoAntesDeExibir = true; 
    }

    public void pararCaptura() {
        servicoCaptura.pararCaptura();
        estavaCapturandoAntesDeExibir = false;
    }
    
    public void reiniciarCapturaPosExibicao() {
        if (estavaCapturandoAntesDeExibir && configuracao.getAreaCaptura() != null) {
            System.out.println("Reiniciando captura após exibição da imagem mesclada.");
            // Não precisa chamar selecionarAreaCaptura() novamente, usa a área já configurada
            servicoCaptura.iniciarCaptura(configuracao, configuracao.getAreaCaptura());
        } else {
            System.out.println("Não foi necessário reiniciar a captura (não estava capturando antes ou área não definida).");
        }
    }


    public void processarImagensMescladas(BufferedImage imagemMesclada) {
        System.out.println("ControladorPrincipal: Processando imagem mesclada...");
        boolean capturaEstavaAtivaAntesDoProcesso = servicoCaptura.isCapturando();
        
        // Pausa a monitorização ANTES de exibir a imagem, se a exibição automática estiver habilitada
        if (configuracao.isExibicaoAutoHabilitada() && capturaEstavaAtivaAntesDoProcesso) {
            servicoCaptura.pararCaptura(); 
            System.out.println("ControladorPrincipal: Monitorização pausada para exibição da imagem.");
            // O estado 'estavaCapturandoAntesDeExibir' será usado para decidir se retoma DEPOIS que o diálogo fechar.
            // Se a captura já estava parada manualmente pelo usuário antes deste ponto, não deve ser reiniciada automaticamente.
             estavaCapturandoAntesDeExibir = true; // Indica que a captura estava ativa e foi parada por este método.
        } else if (!capturaEstavaAtivaAntesDoProcesso) {
            estavaCapturandoAntesDeExibir = false; // Garante que se já estava parada, não tentará reiniciar.
        }


        String nomeBaseArquivo = "mosaic_" + UUID.randomUUID().toString();
        String caminhoLocalSalvo = null;
        String urlNuvemSalva = null;

        // 1. Salvar localmente
        if (configuracao.isSalvarLocal()) { // Usa o novo método de ConfiguracaoCaptura
            if (configuracao.getDiretorioCaptura() != null && !configuracao.getDiretorioCaptura().isEmpty()) {
                try {
                    File diretorio = new File(configuracao.getDiretorioCaptura());
                    if (!diretorio.exists()) diretorio.mkdirs();
                    File arquivoImagem = new File(diretorio, nomeBaseArquivo + ".png");
                    ImageIO.write(imagemMesclada, "png", arquivoImagem);
                    caminhoLocalSalvo = arquivoImagem.getAbsolutePath();
                    System.out.println("Imagem salva localmente em: " + caminhoLocalSalvo);
                } catch (IOException e) {
                    System.err.println("Erro ao salvar imagem localmente: " + e.getMessage());
                    JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(),
                                                "Erro ao salvar imagem localmente: " + e.getMessage(),
                                                "Erro de Salvamento Local", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                 System.err.println("Diretório de captura local não configurado. Imagem não salva localmente.");
                 JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(),
                                                "Diretório de captura local não configurado nas Configurações.\nA imagem não foi salva localmente.",
                                                "Configuração Local Ausente", JOptionPane.WARNING_MESSAGE);
            }
        }

        // 2. Salvar na nuvem
        if (configuracao.isSalvarNuvem()) { // Usa o novo método
            String currentCloudinaryUrl = configuracao.getCloudinaryUrl();
            if (currentCloudinaryUrl != null && !currentCloudinaryUrl.isEmpty()) {
                // Verifica se a URL do Cloudinary mudou desde a última inicialização do serviço
                if (servicoNuvem == null || !currentCloudinaryUrl.equals(servicoNuvem.getActiveCloudinaryUrl())) {
                    servicoNuvem.reinitialize(currentCloudinaryUrl);
                }

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(imagemMesclada, "png", baos);
                    byte[] imagemBytes = baos.toByteArray();
                    urlNuvemSalva = servicoNuvem.uploadImagem(imagemBytes, nomeBaseArquivo); // Passa o nome do arquivo para public_id
                    System.out.println("Imagem salva na nuvem: " + urlNuvemSalva);
                } catch (IOException e) {
                    System.err.println("Erro ao fazer upload para Cloudinary: " + e.getMessage());
                     JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(),
                                                "Erro ao salvar imagem na nuvem: " + e.getMessage() + 
                                                "\nVerifique a URL Cloudinary nas Configurações e sua conexão com a internet.",
                                                "Erro de Salvamento na Nuvem", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.err.println("URL Cloudinary não configurada. Imagem não salva na nuvem.");
                JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(),
                                        "A URL do Cloudinary não está configurada nas Configurações.\nA imagem não pôde ser salva na nuvem.",
                                        "Configuração Cloudinary Ausente", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        // 3. Salvar informações da sessão no banco de dados
        if (caminhoLocalSalvo != null || urlNuvemSalva != null) { // Salva se pelo menos um local foi bem-sucedido
            sessaoAtual.setDataCaptura(new Date());
            sessaoAtual.setCaminhoImagem(urlNuvemSalva); 
            sessaoAtual.setCaminhoLocal(caminhoLocalSalvo); 
            
            if (sessaoDao != null) {
                try {
                    sessaoDao.salvarSessao(sessaoAtual);
                    System.out.println("Sessão salva no banco de dados. ID: " + sessaoAtual.getId());
                    // Limpar metadados para a próxima peça após salvar com sucesso
                    sessaoAtual = new SessaoCaptura(); // Cria uma nova instância para a próxima captura
                    if (JanelaPrincipal.getInstance() != null && JanelaPrincipal.getInstance().getPainelMetadados() != null) {
                        JanelaPrincipal.getInstance().getPainelMetadados().limparCamposMetadados();
                    }
                     JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(),
                                                "Imagem e sessão salvas com sucesso!",
                                                "Salvamento Concluído", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    System.err.println("Erro ao salvar sessão no banco: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(),
                                                    "Erro ao salvar informações da sessão no banco de dados: " + e.getMessage(),
                                                    "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                 System.err.println("SessaoDAO não inicializado. Sessão não salva no banco.");
                 JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(),
                                                "Conexão com o banco não disponível. Informações da sessão não salvas.",
                                                "Erro de Banco de Dados", JOptionPane.WARNING_MESSAGE);
            }
        }


        // 4. Exibir imagem mesclada (se habilitado)
        if (configuracao.isExibicaoAutoHabilitada()) {
            final BufferedImage finalImageToDisplay = imagemMesclada; // Para usar na lambda
            SwingUtilities.invokeLater(() -> {
                JanelaPrincipal.getInstance().exibirImagemMesclada(finalImageToDisplay);
                // A lógica de reiniciar a captura agora está no WindowListener de DialogoExibicaoAuto
            });
        } else {
            // Se a exibição automática não está habilitada, mas a captura estava ativa e foi parada por este método,
            // precisamos decidir se ela deve ser reiniciada.
            // Se 'estavaCapturandoAntesDeExibir' for true, significa que paramos a captura no início deste método.
            if (estavaCapturandoAntesDeExibir) {
                 reiniciarCapturaPosExibicao();
            }
        }
    }

    public void salvarConfiguracao() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_CONFIG_LOCAL))) {
            oos.writeObject(configuracao);
            System.out.println("Configurações salvas localmente em: " + ARQUIVO_CONFIG_LOCAL);
            
            // Se Cloudinary URL mudou, reinicializar o serviço de nuvem
            // Isso garante que o serviço use as credenciais mais recentes na próxima vez que for necessário
            if (servicoNuvem != null && configuracao.getCloudinaryUrl() != null) {
                 if (!configuracao.getCloudinaryUrl().equals(servicoNuvem.getActiveCloudinaryUrl())) {
                    servicoNuvem.reinitialize(configuracao.getCloudinaryUrl());
                 }
            } else if (servicoNuvem != null && configuracao.getCloudinaryUrl() == null) {
                // Se a URL foi removida, podemos querer limpar a configuração do serviço de nuvem
                servicoNuvem.reinitialize(null); // Ou alguma outra lógica para desabilitar
            }


            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), 
                                            "Configurações salvas com sucesso!", 
                                            "Salvar Configurações", 
                                            JOptionPane.INFORMATION_MESSAGE)
            );
        } catch (IOException e) {
            System.err.println("Erro ao salvar configurações localmente: " + e.getMessage());
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(JanelaPrincipal.getInstance(), 
                                            "Erro ao salvar configurações localmente: " + e.getMessage(), 
                                            "Erro ao Salvar Configurações", 
                                            JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    public void carregarConfiguracao() {
        File configFile = new File(ARQUIVO_CONFIG_LOCAL);
        if (configFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFile))) {
                Object obj = ois.readObject();
                if (obj instanceof ConfiguracaoCaptura) {
                    this.configuracao = (ConfiguracaoCaptura) obj;
                    System.out.println("Configurações carregadas de: " + ARQUIVO_CONFIG_LOCAL);
                    
                    // Após carregar, reinicializa o serviço de nuvem com a URL carregada
                    if (this.servicoNuvem != null) {
                        this.servicoNuvem.reinitialize(this.configuracao.getCloudinaryUrl());
                    } else { // Se servicoNuvem ainda não foi instanciado
                        this.servicoNuvem = new ServicoArmazenamentoNuvem(this.configuracao.getCloudinaryUrl());
                    }

                } else {
                     System.err.println("Tipo de objeto inesperado no arquivo de configuração.");
                     // Mantém a configuração padrão.
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erro ao carregar configurações de " + ARQUIVO_CONFIG_LOCAL + ": " + e.getMessage() + ". Usando configurações padrão.");
                e.printStackTrace();
                // Mantém a configuração padrão se o carregamento falhar
                // Se houve erro, garante que o serviço de nuvem use a URL da config padrão (pode ser nula)
                if (this.servicoNuvem != null) {
                    this.servicoNuvem.reinitialize(this.configuracao.getCloudinaryUrl());
                } else {
                    this.servicoNuvem = new ServicoArmazenamentoNuvem(this.configuracao.getCloudinaryUrl());
                }
            }
        } else {
            System.out.println("Arquivo de configuração local (" + ARQUIVO_CONFIG_LOCAL + ") não encontrado. Usando configurações padrão.");
            // A configuração padrão já foi instanciada no construtor.
            // Garante que o serviço de nuvem use a URL da config padrão (pode ser nula)
            if (this.servicoNuvem != null) {
                this.servicoNuvem.reinitialize(this.configuracao.getCloudinaryUrl());
            } else {
                this.servicoNuvem = new ServicoArmazenamentoNuvem(this.configuracao.getCloudinaryUrl());
            }
        }
    }
    
    public List<SessaoCaptura> carregarSessoesDoBanco(String nomePecaFilter, Date startDateFilter, Date endDateFilter) throws SQLException {
        if (sessaoDao == null) {
            System.err.println("ControladorPrincipal: Tentativa de carregar sessões, mas SessaoDAO não está inicializado.");
            throw new SQLException("SessaoDAO não está inicializado. Verifique a conexão com o banco de dados.");
        }
        return sessaoDao.buscarSessoes(nomePecaFilter, startDateFilter, endDateFilter);
    }

    public List<SessaoCaptura> carregarTodasSessoesDoBanco() throws SQLException {
         if (sessaoDao == null) {
            System.err.println("ControladorPrincipal: Tentativa de carregar todas as sessões, mas SessaoDAO não está inicializado.");
            throw new SQLException("SessaoDAO não está inicializado. Verifique a conexão com o banco de dados.");
        }
        return sessaoDao.buscarSessoes(null, null, null); 
    }
}
