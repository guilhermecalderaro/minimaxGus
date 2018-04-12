package javaapplication3;

import NineMensMorris.GameInfo;
import java.util.List;



public final class EvaluationFunction {
    
    private int[][] tabuleiro;
    private GameInfo info;
    
    private static int COLUNA_ZERO = 0;
    private static int COLUNA_SETE = 7;
    private static int LINHA_ZERO = 0;
    private static int MAIS_UMA_CASA = 1;
    private static int MENOS_UMA_CASA = -1;
    

    
    


    public EvaluationFunction(GameInfo info, int[][] tabuleiro) {
        this.tabuleiro = tabuleiro;
        this.info = info;
    }
    
    

    
    
    public int[][] getTabuleiro() {
        return tabuleiro;
    }

    public void setTabuleiro(int[][] spots) {
        this.tabuleiro = spots;
    }

    public GameInfo getInfo() {
        return info;
    }

    public void setInfo(GameInfo info) {
        this.info = info;
    }
    

    

    
    
    public int phase1(int jogadorQueFechouLinha) {
        return evaluate(0, 0, 0, 0, 2, 5, 0, jogadorQueFechouLinha);
    }    
    
    public int phase2(int jogadorQueFechouLinha) {
        return evaluate(0, 0, 0, 0, 2, 5, 0, jogadorQueFechouLinha);
    }    
    
    public int phase3(int jogadorQueFechouLinha) {
        return evaluate(0, 0, 0, 0, 2, 5, 0, jogadorQueFechouLinha);
    }
    
    public int vitoria(int jogador){
        return jogador == Arvore.MAX_TURN ? 5000 : -5000;
    }
    
    private int evaluate(int w1, int w2, int w3, int w4, int w5, int w6, int w7, int jogadorQueFechouLinha) {        
        
        int notaAvaliacao = 0;
        if(w1 !=0){
            notaAvaliacao += w1 * (jogadorQueFechouLinha);
        }
        if(w2 !=0){
            notaAvaliacao += w2 * (getTotalLinhasFechadas( Arvore.MAX_TURN) - getTotalLinhasFechadas(Arvore.MIN_TURN));
        }
        if(w3 !=0){
            notaAvaliacao += w3 * (getTotalPecasAdversariaBloqueadas(Arvore.MIN_TURN) - getTotalPecasAdversariaBloqueadas(Arvore.MAX_TURN));
        }
        if(w4 !=0){
            notaAvaliacao += w4 * (getInfo().getPlayerSpots(getTabuleiro()).size() - getInfo().getOpponentSpots(getTabuleiro()).size());
        }
        if(w5 != 0){
            notaAvaliacao += w5 * (getQuantidadeLDuasPecas(1) - getQuantidadeLDuasPecas(2));
        }
        if(w6 !=0){
            notaAvaliacao += w6 * (getQuantidadeLTresPecas(Arvore.MAX_TURN) - getQuantidadeLTresPecas(Arvore.MIN_TURN));
        }
        if(w7 !=0){
            notaAvaliacao += w7 * (getQuantidadeLCincoPecas(Arvore.MAX_TURN) - getQuantidadeLCincoPecas(Arvore.MIN_TURN));
        }
        
        
        return notaAvaliacao;
    }
    
    
    
    
    
    
    

    
    

    /*Retorna total de linhas fechadas pelo jogador*/
    public int getTotalLinhasFechadas(int jogador){

        //Busca todos campos do tabuleiro que estão ocupados por uma peça do Jogador Atual, Jogador ou Oponente;
        List<String> playerSpots =  jogador == Arvore.MAX_TURN ? getInfo().getPlayerSpots(getTabuleiro()) : getInfo().getOpponentSpots(getTabuleiro());
        
        /*Inicialização da variavel count(contadora) que irá contar, e retornar no final do método quantas
        linhas de três estão formadas no tabuleiro a favor do Jogador*/
        int count = 0;
                
        //Percorre todas as peças do Jogador Atual, pará verificar se há 3 em sequencia(linha formada)        
        for (int i=0; i<playerSpots.size(); i++){
            
            /*PlayerSpots retorna as posições das peças do Jogador, a posição consiste em linha e coluna (L,C)
            linha está na posição 0 da String e a coluna na posição 2, pois elas estão separadas por uma Virgula, que está na posição 1*/
            int linha = Integer.parseInt(""+playerSpots.get(i).charAt(0));
            int coluna = Integer.parseInt(""+playerSpots.get(i).charAt(2));
            
            /*Verificação se a coluna é um numero par, todos numeros pares ficam em cantos, sendo assim não podem
            formar linha no sentido coluna(do centro do tabuleiro para extremidade).*/
            if( coluna % 2 != 0){

                /*Verifica se a prixima casa para fechar uma linha contem uma peça do Jogador Atual*/
                if(playerSpots.contains(linha + "," + (proximaCasa(coluna)))){
                    /*Verifica se 2 casas depois para fechar uma linha contem uma peça do Jogador Atual*/
                    if(playerSpots.contains(linha + "," + (proximaCasa(proximaCasa(coluna))))){
                        /*Caso todas verificações forem verdadeiras até aqui, é porque o Jogador tem uma linha
                        formada*/
                        count++;
                    }
                }
            }
            /* Verificação identica para os numero pares(peças que estão no canto) porém com o acrécimo de uma
            verificação do sentido coluna(do centro do tabuleiro para extremidade), para essa verificação
            adicional só é necessário fazer as verificações se tiver uma peça na linha 0, pois um trio fechado
            em sentido coluna, abrangira as 3 linhas do tabuleiro, logo, se não tiver uma peça na linha 0 é
            porque não tem um trio fechado.
            */
            else if( coluna % 2 == 0  && linha == LINHA_ZERO){
                if(playerSpots.contains((proximaCasa(linha)) + "," + coluna)){
                    if(playerSpots.contains((proximaCasa(proximaCasa(linha))) + "," + coluna)){
                        count++;
                    }
                }
            }
        }
        
        //Retorno do numero de trios fechados encontrado no tabuleiro
        return count;
    }
    
    
    /*Retorna total de peças bloqueadas do jogador*/
    public int getTotalPecasAdversariaBloqueadas(int jogador){

        //Busca todos campos do tabuleiro que estão ocupados por uma peça do Adversário;
        List<String> playerSpots = (jogador == Arvore.MAX_TURN) ? getInfo().getOpponentSpots(getTabuleiro()) : getInfo().getPlayerSpots(getTabuleiro());
        
        //Busca todos campos do tabuleiro que estão livres(sem nenhuma peça tanto do Jogador quanto Oponente)
        List<String> emptySpots = getInfo().getEmptySpots(getTabuleiro());
        
        /*Inicialização da variavel count(contadora) que irá contar, e retornar no final do método quantas
        linhas de três estão formadas no tabuleiro a favor do Jogador*/
        int count = 0;
                
        //Percorre todas as peças do Adversario       
        for (int i=0; i<playerSpots.size(); i++){
            
             /*PlayerSpots retorna as posições das peças do Jogador, a posição consiste em linha e coluna (L,C)
            linha está na posição 0 da String e a coluna na posição 2, pois elas estão separadas por uma Virgula, que está na posição 1*/
            int linha = Integer.parseInt(""+playerSpots.get(i).charAt(0));
            int coluna = Integer.parseInt(""+playerSpots.get(i).charAt(2));
            
            /*Verificação se a coluna é um numero par, todos numeros pares ficam em cantos, sendo assim não podem se
            movimentar no sentido coluna(do centro do tabuleiro para extremidade)*/
            if(coluna % 2 == 0){
                if(!emptySpots.contains(linha + "," + proximaCasa(coluna))){
                    if(!emptySpots.contains(linha + "," + casaAnterior(coluna))){
                        /*Se todas verificações forem verdadeiras até aqui, incrementa o count(contador de numeros de
                        peças bloqueadas*/
                        count++;
                    }
                }
            }
            /*Senão, se for impar, necessita de verificações adicionais para verificar disponibilidade de se
            movimentar, no sentido coluna(do centro do tabuleiro para extremidade)*/
            else if(coluna % 2 != 0){
                
                /*Verificação do spot com a coluna seguinte(coluna+1)para ver se está livre, se não há peças bloqueando,
                    verificação feita: se a peça não estiver na lista de tabuleiro livres(vazios) é porque há peça*/
                if(!emptySpots.contains(linha + "," + proximaCasa(coluna))){
                   /*Verificação do spot com a coluna anterior(coluna-1)para ver se está livre, se não há peças bloqueando,
                    verificação feita: se a peça não estiver na lista de tabuleiro livres(vazios) é porque há peça*/
                    if(!emptySpots.contains(linha + "," + casaAnterior(coluna))){
                        /*Peças de numero impar estão posicionadas no meio, possibilitando movimentação do tipo coluna
                        (do centro do tabuleiro para a extremidade), logo é necessario verificar disponibilidade de 
                        movimentação em tal sentido*/

                        /*No caso de estar na extremidade exterior(linha == 0) do tabuleiro só é necessaria uma verificação,
                        pois só há uma movimentação possivel no sentido coluna*/
                        switch(linha){
                        case 0:
                            if(!emptySpots.contains(proximaCasa(linha) + "," + coluna)){
                                /*Se todas verificações forem verdadeiras até aqui, incrementa o count(contador de numeros de
                                peças bloqueadas*/
                                count++;
                                break;
                            }
                        case 1:
                            if((!emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (!emptySpots.contains(casaAnterior(linha) + "," + coluna))){
                                /*Se todas verificações forem verdadeiras até aqui, incrementa o count(contador de numeros de
                                peças bloqueadas*/
                                count++;
                                break;
                            }
                        case 2:
                            if(!emptySpots.contains(casaAnterior(linha) + ","  + coluna)){
                                /*Se todas verificações forem verdadeiras até aqui, incrementa o count(contador de numeros de
                                peças bloqueadas*/
                                count++;
                                break;
                            }
                        }
                    }
                }
            }
        }
            
        return count;
    }
    
    //Quantidade de vezes que o Jogador atual possui de L de três peças
    private int getQuantidadeLDuasPecas(int jogador) {
        //Busca todos campos do tabuleiro que estão ocupados por uma peça do Adversário;
        List<String> playerSpots = (jogador == 1) ? getInfo().getPlayerSpots(getTabuleiro()) : getInfo().getOpponentSpots(getTabuleiro());
        
        //Busca todos campos do tabuleiro que estão livres(sem nenhuma peça tanto do Jogador quanto Oponente)
        List<String> emptySpots = getInfo().getEmptySpots(getTabuleiro());
        
        /*Inicialização da variavel count(contadora) que irá contar, e retornar no final do método quantas
        linhas em L de três peças estão formadas no tabuleiro a favor do Jogador Atual*/
        int count = 0;
                
        //Percorre todas as peças do Adversario       
        for (int i=0; i<playerSpots.size(); i++){
            
             /*PlayerSpots retorna as posições das peças do Jogador, a posição consiste em linha e coluna (L,C)
            linha está na posição 0 da String e a coluna na posição 2, pois elas estão separadas por uma Virgula, que está na posição 1*/
            int linha = Integer.parseInt(""+playerSpots.get(i).charAt(0));
            int coluna = Integer.parseInt(""+playerSpots.get(i).charAt(2));
            
            
            /*Verificação se a coluna é um numero par, todos numeros pares ficam em cantos, sendo assim não podem se
            movimentar no sentido coluna(do centro do tabuleiro para extremidade)*/
            if(coluna%2==0){
                if(playerSpots.contains(linha + "," + proximaCasa(proximaCasa(proximaCasa(proximaCasa(coluna)))))){
                    if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(linha + "," + proximaCasa(proximaCasa(coluna)))) && (emptySpots.contains(linha + "," + proximaCasa(proximaCasa(proximaCasa(coluna)))))){
                        count++;
                    }
                }
                
            }
            else if(coluna%2==1){
                switch(linha){
                case 0:
                    if((emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + casaAnterior(coluna)))){
                        if(playerSpots.contains(proximaCasa(linha) + "," + proximaCasa(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna)) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + casaAnterior(coluna)))){
                        if(playerSpots.contains(proximaCasa(proximaCasa(linha)) + "," + proximaCasa(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + proximaCasa(coluna)))){
                        if(playerSpots.contains(proximaCasa(linha) + "," + casaAnterior(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna)) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + proximaCasa(coluna)))){
                        if(playerSpots.contains(proximaCasa(proximaCasa(linha)) + "," + casaAnterior(coluna))){
                            count++;
                        }
                    }
                    break;    
                    
                case 1:
                    if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(casaAnterior(linha) + "," + proximaCasa(coluna)))){
                        if(playerSpots.contains(casaAnterior(linha) + "," + casaAnterior(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(casaAnterior(linha) + "," + casaAnterior(coluna)))){
                        if(playerSpots.contains(casaAnterior(linha) + "," + proximaCasa(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + proximaCasa(coluna)))){
                        if(playerSpots.contains(proximaCasa(linha) + "," + casaAnterior(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + coluna)) && (emptySpots.contains(proximaCasa(linha) + "," + casaAnterior(coluna)))){
                        if(playerSpots.contains(proximaCasa(linha) + "," + proximaCasa(coluna))){
                            count++;
                        }
                    }
                    break;
                          
                case 2:
                    if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna)) && (emptySpots.contains(casaAnterior(linha) + "," + casaAnterior(coluna)))){
                        if(playerSpots.contains(casaAnterior(linha) + "," + proximaCasa(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna)) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + casaAnterior(coluna)))){
                        if(playerSpots.contains(casaAnterior(casaAnterior(linha)) + "," + proximaCasa(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna)) && (emptySpots.contains(casaAnterior(linha) + "," + proximaCasa(coluna)))){
                        if(playerSpots.contains(casaAnterior(linha) + "," + casaAnterior(coluna))){
                            count++;
                        }
                    }
                    else if((emptySpots.contains(casaAnterior(linha) + "," + coluna)) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna)) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + proximaCasa(coluna)))){
                        if(playerSpots.contains(casaAnterior(casaAnterior(linha)) + "," + casaAnterior(coluna))){
                            count++;
                        }
                    }
                    break;
                }
            }
        }
        
        return count;
                
    }
    
    
    //Quantidade de vezes que o Jogador atual possui de L de três peças
    private int getQuantidadeLTresPecas(int jogador) {
        //Busca todos campos do tabuleiro que estão ocupados por uma peça do Adversário;
        List<String> playerSpots = (jogador == Arvore.MAX_TURN) ? getInfo().getOpponentSpots(getTabuleiro()) : getInfo().getPlayerSpots(getTabuleiro());
        
        //Busca todos campos do tabuleiro que estão livres(sem nenhuma peça tanto do Jogador quanto Oponente)
        List<String> emptySpots = getInfo().getEmptySpots(getTabuleiro());
        
        /*Inicialização da variavel count(contadora) que irá contar, e retornar no final do método quantas
        linhas em L de três peças estão formadas no tabuleiro a favor do Jogador Atual*/
        int count = 0;
                
        //Percorre todas as peças do Adversario       
        for (int i=0; i<playerSpots.size(); i++){
            
             /*PlayerSpots retorna as posições das peças do Jogador, a posição consiste em linha e coluna (L,C)
            linha está na posição 0 da String e a coluna na posição 2, pois elas estão separadas por uma Virgula, que está na posição 1*/
            int linha = Integer.parseInt(""+playerSpots.get(i).charAt(0));
            int coluna = Integer.parseInt(""+playerSpots.get(i).charAt(2));
            
            /*Verificação se a coluna é um numero par, todos numeros pares ficam em cantos, sendo assim não podem se
            movimentar no sentido coluna(do centro do tabuleiro para extremidade)*/
            if(coluna % 2 == 0){
                if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(linha + "," + casaAnterior(coluna)))){
                    if((playerSpots.contains(linha + "," + proximaCasa(proximaCasa(coluna)))) && (playerSpots.contains(linha + "," + casaAnterior(casaAnterior(coluna))))){
                        count ++;
                    }
                }
            }
            else if(coluna % 2 != 0){
                switch(linha){
                case 0:
                    if((playerSpots.contains(linha + "," + proximaCasa(coluna))) && (playerSpots.contains(proximaCasa(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + casaAnterior(coluna))) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + proximaCasa(coluna))) && (playerSpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + casaAnterior(coluna))) && (emptySpots.contains(proximaCasa(linha) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + casaAnterior(coluna))) && (playerSpots.contains(proximaCasa(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + casaAnterior(coluna))) && (playerSpots.contains(proximaCasa(proximaCasa(linha)) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(proximaCasa(linha) + "," + coluna))){
                            count++;
                        }
                    }
                    break;
                case 1:
                    if((playerSpots.contains(linha + "," + proximaCasa(coluna))) && (playerSpots.contains(casaAnterior(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + casaAnterior(coluna))) && (emptySpots.contains(proximaCasa(linha) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + proximaCasa(coluna))) && (playerSpots.contains(proximaCasa(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + casaAnterior(coluna))) && (emptySpots.contains(casaAnterior(linha) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + casaAnterior(coluna))) && (playerSpots.contains(casaAnterior(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(proximaCasa(linha) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + casaAnterior(coluna))) && (playerSpots.contains(proximaCasa(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(casaAnterior(linha) + "," + coluna))){
                            count++;
                        }
                    }
                case 2:
                    if((playerSpots.contains(linha + "," + proximaCasa(coluna))) && (playerSpots.contains(casaAnterior(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + casaAnterior(coluna))) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + proximaCasa(coluna))) && (playerSpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + casaAnterior(coluna))) && (emptySpots.contains(casaAnterior(linha) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + casaAnterior(coluna))) && (playerSpots.contains(casaAnterior(linha) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna))){
                            count++;
                        }
                    }
                    else if((playerSpots.contains(linha + "," + casaAnterior(coluna))) && (playerSpots.contains(casaAnterior(casaAnterior(linha)) + "," + coluna))){
                        if((emptySpots.contains(linha + "," + proximaCasa(coluna))) && (emptySpots.contains(casaAnterior(linha) + "," + coluna))){
                            count++;
                        }
                    }
                }
            }
        }
        
        return count;
                
    }
    
    
    //Quantidade de vezes que o Jogador atual possui de L de cinco peças
    private int getQuantidadeLCincoPecas(int jogador) {
        //Busca todos campos do tabuleiro que estão ocupados por uma peça do Adversário;
        List<String> playerSpots = (jogador == Arvore.MAX_TURN) ? getInfo().getOpponentSpots(getTabuleiro()) : getInfo().getPlayerSpots(getTabuleiro());
        
        /*Inicialização da variavel count(contadora) que irá contar, e retornar no final do método quantas
        linhas em L de cindo peças estão formadas no tabuleiro a favor do Jogador Atual*/
        int count = 0;
                
        //Percorre todas as peças do Adversario       
        for (int i=0; i<playerSpots.size(); i++){
            
             /*PlayerSpots retorna as posições das peças do Jogador, a posição consiste em linha e coluna (L,C)
            linha está na posição 0 da String e a coluna na posição 2, pois elas estão separadas por uma Virgula, que está na posição 1*/
            int linha = Integer.parseInt(""+playerSpots.get(i).charAt(0));
            int coluna = Integer.parseInt(""+playerSpots.get(i).charAt(2));
            
            /*Verificação se a coluna é um numero par, todos numeros pares ficam em cantos*/
            if(coluna % 2 == 0){
                if((playerSpots.contains(linha + "," + proximaCasa(coluna))) && (playerSpots.contains(linha + "," + proximaCasa(proximaCasa(coluna))))){
                    if((playerSpots.contains(linha + "," + casaAnterior(coluna))) && (playerSpots.contains(linha + "," + casaAnterior(casaAnterior(coluna))))){
                        count++;
                    }
                }
            }
        }
        
        return count;
    }


    
    
    
    
    
    
    
    
        

    
    private int proximaCasa(int numero){
        if(numero == COLUNA_SETE){
            numero = COLUNA_ZERO;
        }
        else{
            numero += MAIS_UMA_CASA;
        }
        
        return numero;
    }
    
    private int casaAnterior(int numero){
        if(numero == COLUNA_ZERO){
            numero = COLUNA_SETE;
        }
        else{
            numero += MENOS_UMA_CASA;
        }
        
        return numero;
    }
    
}
