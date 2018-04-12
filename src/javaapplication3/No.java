/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication3;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gustavo
 */
public class No {
    
    private int jogador;
    private int[][] tabuleiro;
    private int profundidade;
    private String pecaAlterada;
    private int pecasPorColocar;
    private List<No> filhos;
    private Integer avaliacao;
    private boolean comeuPeca;

    
    
    
    public No(int turno, int[][] tabuleiro, int profundidade, String pecaAlterada, int pecasPorColocar) {
        
        this.jogador = turno;
        
        this.tabuleiro = tabuleiro;
        
        this.profundidade = profundidade;
        
        this.pecaAlterada = pecaAlterada;
        
        this.pecasPorColocar = pecasPorColocar;
        
        this.filhos = new ArrayList<>();
        
        
    }

    public No() {
        this.filhos = new ArrayList<>();
    }
    
    
    

    public int getJogador() {
        return jogador;
    }

    public void setJogador(int turno) {
        this.jogador = turno;
    }

    public int[][] getTabuleiro() {
        return tabuleiro;
    }

    public void setTabuleiro(int[][] tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public int getProfundidade() {
        return profundidade;
    }

    public void setProfundidade(int profundidade) {
        this.profundidade = profundidade;
    }

    public String getPecaAlterada() {
        return pecaAlterada;
    }

    public void setPecaAlterada(String pecaAlterada) {
        this.pecaAlterada = pecaAlterada;
    }

    public int getPecasPorColocar() {
        return pecasPorColocar;
    }

    public void setPecasPorColocar(int pecasPorColocar) {
        this.pecasPorColocar = pecasPorColocar;
    }

    public List<No> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<No> filhos) {
        this.filhos = filhos;
    }
    
    public void addFilho(No no){
        
        if(this.filhos == null){
            this.filhos = new ArrayList<>();
        }
        
        this.filhos.add(no);
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }

    public boolean isComeuPeca() {
        return comeuPeca;
    }

    public void setComeuPeca(boolean teveRemocao) {
        this.comeuPeca = teveRemocao;
    }
    
    
    
}
