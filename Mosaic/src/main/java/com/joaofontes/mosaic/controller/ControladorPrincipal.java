/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.joaofontes.mosaic.controller;

import util.ServicoCaptura;
import util.ServicoMesclagem;

/**
 *
 * @author JoãoFontes
 */
public class ControladorPrincipal {
     private final ConfiguracaoCaptura configuracao = new ConfiguracaoCaptura();
    private final ServicoCaptura servicoCaptura = new ServicoCaptura();
    private final ServicoMesclagem servicoMesclagem = new ServicoMesclagem();

    public void iniciarCaptura() {
        servicoCaptura.iniciarCaptura(configuracao);
    }
    
    public void pararCaptura() {
        servicoCaptura.pararCaptura();
    }
    
    public void salvarConfiguracao() {
        // Implementar persistência
    }
    
    public void carregarConfiguracao() {
        // Implementar carregamento
    }
    
    public void novaSessao(SessaoCaptura sessao) {
        // Implementar criação de nova sessão
    }
    
    public void setDiretorioCaptura(String diretorio) {
        configuracao.setDiretorioCaptura(diretorio);
    }
    
    public ConfiguracaoCaptura getConfiguracao() {
        return configuracao;
    }
}
