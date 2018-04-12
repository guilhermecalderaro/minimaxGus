/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication3;


import NineMensMorris.GameInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Gustavo
 */
public class Arvore {
    
    public static final int MAX_TURN = 1; 
    public static final int MIN_TURN = 2;
    public static final int VAZIO = 0;
    
    public static final int SET_ACTION = 1; 
    public static final int MOVE_ACTION = 2; 
    public static final int REMOVE_ACTION = 3; 
    
    private int profundidadeMaxima = 0 ;  //MAX profundidadeMaxima
    
    private GameInfo info;            
    private No raiz;        
    
    public Arvore( int profundidadeMaxima,int tipoMovimento, GameInfo info )
    {
        this.profundidadeMaxima = profundidadeMaxima;
        
        this.info = info;
        
        montaArvore( tipoMovimento );
    }

    
    private void montaArvore( int tipoMovimento ){
        
        int turno = MAX_TURN;
        int[][] tabuleiro = info.getSpots();
        int profundidade = 0;
        String pecaAlterada = "";
        int pecasPorColocar = info.getPiecesToPlace() + info.getOpponentPiecesToPlace();
        
        this.raiz = new No(turno, tabuleiro, profundidade, pecaAlterada, pecasPorColocar);
        
        generateChildren(raiz, tipoMovimento );
        
        System.out.println("fim de geração");
        
    }
    
    
    
    private void generateChildren( No no, int tipoMovimento ){
                        
        switch (tipoMovimento) {
            case SET_ACTION:
                generateChildrenForActionSet( no );
                break;
            case MOVE_ACTION:
                generateChildrenForActionMove( no );
                break;
            case REMOVE_ACTION:
                generateChildrenForActionRemove( no );
                break;
            default:
                break;
        }
        
    }
    
    private void generateChildrenForActionSet( No no )
    {
        // lista com coordenadas de casas vazias para o estado do nodo
        List<String> tabuleiro = info.getEmptySpots( no.getTabuleiro() );

        // percorre lista com coordenadas de casas vazias
        for( String peca : tabuleiro )
        {
            // cria novo estado do jogo a partir de um clone do estado anterior
            int[][] novoTabuleiro = cloneState( no.getTabuleiro() );

            String[] coords = peca.split(",");
            int linha = Integer.parseInt( coords[0]);
            int coluna = Integer.parseInt( coords[1]);

            // coloca peça do jogador no novo estado
            novoTabuleiro[linha][coluna] = no.getJogador();

            // subtraí quantidade de peças que precisam ser colocadas no tabuleiro
            int pecasPorColocar = no.getPecasPorColocar() - 1;
            
            int profundidade = no.getProfundidade() - 1;
            
            int jogador = no.getJogador() == MAX_TURN ? MIN_TURN : MAX_TURN;
            
            List<String> allowedRemoves = null;

            // Se jogador forma linha com 3 peças então pode remover peça
            // Nesse caso colocar peça e remover peça são consideradas apenas uma jogada
            if (( no.getJogador() == MAX_TURN && info.isPlayerLineOfThree( peca, novoTabuleiro )) ||
                ( no.getJogador() == MIN_TURN && info.isOpponentLineOfThree( peca, novoTabuleiro )) ){
                
                // lista com coordenadas de peças que podem ser removidas 
                allowedRemoves = no.getJogador() == MAX_TURN 
                                                  ? info.getAllowedRemoves( novoTabuleiro )
                                                  : info.getOpponentAllowedRemoves( novoTabuleiro ); 
                
                // percorre lista com coordenadas de peças que podem ser removidas
                for (String allowedRemove : allowedRemoves )
                {
                    // cria novo estado para a remoção de peça a partir do clone do estado anterior
                    int[][] novoTabuleiroRemocao = cloneState( novoTabuleiro );

                    String[] removeCoords = allowedRemove.split(",");
                    int removeLinha = Integer.parseInt( removeCoords[0]);
                    int removeColuna = Integer.parseInt( removeCoords[1]);

                    // remove peça no novo estado
                    novoTabuleiroRemocao[removeLinha][removeColuna] = VAZIO;

                    // Cria nodo para jogada de remover peça
                    No filho = new No();
                    filho.setJogador(jogador);
                    filho.setPecaAlterada(peca);
                    filho.setPecasPorColocar(pecasPorColocar);
                    filho.setProfundidade(profundidade);
                    filho.setTabuleiro(novoTabuleiroRemocao);
                    filho.setComeuPeca(true);
                    
                    

                    // caso algum dos jogadores esteja com menos de 3 peças então considera fim do jogo e calcula pontuação do nodo folha
                    if ( info.getPlayerSpots(novoTabuleiroRemocao ).size() < 3 ||
                         info.getOpponentSpots(novoTabuleiroRemocao ).size() < 3 ){
                        
                        filho.setAvaliacao(no.getJogador() == MAX_TURN ? 5000 : -5000);
                        
                        // adiciona nodo como filho do nodo anterior
                        no.addFilho( filho );
                    }
                    // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                    else if ( filho.getProfundidade() < profundidadeMaxima){                    
                        
                        generateChildren(filho, MOVE_ACTION );
                        
                        // adiciona nodo como filho do nodo anterior
                        no.addFilho( filho );
                    }
                    // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                    else
                    {
                        //child.score = scoreGameState( novoTabuleiroRemocao, filho.playerTurn, filho.pecasPorColocar, filho.opponentePiecesToPlace );
                        filho.setAvaliacao(avaliar(filho));
                        
                        // adiciona nodo como filho do nodo anterior
                        no.addFilho( filho );
                    }
                }                                                
            }

            // Caso nenhum nodo para a jogada de remover peça foi criado então cria-se somente uma jogada para colocar peça 
            if ( allowedRemoves == null || allowedRemoves.isEmpty() )
            {

                No filho = new No();
                filho.setJogador(jogador);
                filho.setPecaAlterada(peca);
                filho.setPecasPorColocar(pecasPorColocar);
                filho.setProfundidade(profundidade);
                filho.setTabuleiro(novoTabuleiro);
                filho.setComeuPeca(false);
                    

                // verifica próxima ação de acordo com as peças que falta colocar do jogador
                int tipoMovimento = filho.getPecasPorColocar() > 0 ? SET_ACTION : MOVE_ACTION;

                // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                if ( filho.getProfundidade() < profundidadeMaxima){ 
                    
                    generateChildren(filho, tipoMovimento );
                    
                    // adiciona nodo como filho do nodo anterior
                    no.addFilho( filho );
                }
                // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                else{
                    
                    filho.setAvaliacao(avaliar(filho));
                    
                    // adiciona nodo como filho do nodo anterior
                    no.addFilho( filho );
                }
            }
        }
    }
    
    private void generateChildrenForActionMove( No no )
    {
        // lista com coordenadas de movimentos permitidos para o estado do nodo
        List<String> allowedMoves = no.getJogador() == MAX_TURN
                                    ? info.getAllowedMoves(no.getTabuleiro() )
                                    : info.getOpponentAllowedMoves(no.getTabuleiro() );

        // Caso não haja movimentos permitidos então o jogo acaba e é calculada a pontuação para o nó folha
        if ( allowedMoves.isEmpty() )
        {
            no.setAvaliacao(no.getJogador() == MAX_TURN ? -5000 : 5000);
            return;
        }

        // percorre lista de coordenadas das movimentações permitidas
        for( String move : allowedMoves )
        {
            String[] coords = move.split(";");

            String source = coords[0];
            String[] sourceCoords = source.split(",");
            int sourceLine = Integer.parseInt( sourceCoords[0]);
            int sourceCol = Integer.parseInt( sourceCoords[1]);

            for ( int x = 1;x < coords.length; x++ )
            {
                // cria novo estado para a movimentação de peça a partir do clone do estado anterior
                int[][] novoTabuleiro = cloneState(no.getTabuleiro() );

                String target = coords[ x ];
                String[] targetCoords = target.split(",");
                int targetLine = Integer.parseInt( targetCoords[0]);
                int targetCol = Integer.parseInt( targetCoords[1]);

                // realiza movimentação de peça no novo estado do jogo
                novoTabuleiro[sourceLine][sourceCol] = VAZIO;
                novoTabuleiro[targetLine][targetCol] = no.getJogador();

                List<String> allowedRemoves = null;
                
                int jogador = no.getJogador() == MAX_TURN ? MIN_TURN : MAX_TURN;
                int pecasPorColocar = no.getPecasPorColocar();
                int profundidade = no.getProfundidade() - 1;

                // Se jogador forma linha com 3 peças então pode remover peça
                // Nesse caso colocar peça e remover peça são consideradas apenas uma jogada
                if (( no.getJogador() == MAX_TURN && info.isPlayerLineOfThree(target, novoTabuleiro )) ||
                    ( no.getJogador() == MIN_TURN && info.isOpponentLineOfThree(target, novoTabuleiro )) ){
                    
                    // lista com coordenadas de peças que podem ser removidas 
                    allowedRemoves = no.getJogador() == MAX_TURN ? info.getAllowedRemoves(novoTabuleiro) : info.getOpponentAllowedRemoves(novoTabuleiro); 
                    
                    // percorre lista com coordenadas de peças que podem ser removidas
                    for (String allowedRemove : allowedRemoves )
                    {
                        // cria novo estado para a remoção de peça a partir do clone do estado anterior
                        int[][] novoTabuleiroRemover = cloneState(novoTabuleiro);

                        String[] removeCoords = allowedRemove.split(",");
                        int removeLine = Integer.parseInt( removeCoords[0]);
                        int removeCol = Integer.parseInt( removeCoords[1]);

                        // remove peça no novo estado
                        novoTabuleiroRemover[removeLine][removeCol] = VAZIO;

                        // Cria nodo para jogada de mover peça
                        No filho = new No();
                        filho.setJogador(jogador);
                        filho.setPecaAlterada(source+";"+target);
                        filho.setPecasPorColocar(pecasPorColocar);
                        filho.setProfundidade(profundidade);
                        filho.setTabuleiro(novoTabuleiroRemover);
                        filho.setComeuPeca(true);
                        

                        // caso algum dos jogadores esteja com menos de 3 peças então considera fim do jogo e calcula pontuação do nodo folha
                        if ( info.getPlayerSpots(novoTabuleiroRemover ).size() < 3 ||
                             info.getOpponentSpots(novoTabuleiroRemover ).size() < 3 ){
                            
                            filho.setAvaliacao(no.getJogador() == MAX_TURN ? 5000 : -5000);
                        
                            // adiciona nodo como filho do nodo anterior
                            no.addFilho( filho );
                            
                        }    
                        // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                        else if ( filho.getProfundidade() < profundidadeMaxima){                    
                            
                            generateChildren( filho, MOVE_ACTION );
                            
                            // adiciona nodo como filho do nodo anterior
                            no.addFilho( filho );
                        
                        }
                        // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                        else{
                           
                            filho.setAvaliacao(avaliar(filho));
                            
                            // adiciona nodo como filho do nodo anterior
                            no.addFilho( filho );
                        
                        }

                    }                                                
                }

                // Caso nenhum nodo para a jogada de remover peça foi criado então cria-se somente uma jogada para colocar peça 
                if ( allowedRemoves == null || allowedRemoves.isEmpty() )
                {
                    // Cria nodo para jogada de mover peça
                    No filho = new No();
                    filho.setJogador(jogador);
                    filho.setPecaAlterada(source+";"+target);
                    filho.setPecasPorColocar(pecasPorColocar);
                    filho.setProfundidade(profundidade);
                    filho.setTabuleiro(novoTabuleiro);
                    filho.setComeuPeca(false);


                    // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                    if ( filho.getProfundidade() < profundidadeMaxima){
                        
                        generateChildren( filho, MOVE_ACTION );
                        
                        // adiciona nodo como filho do nodo anterior
                        no.addFilho(filho);
                    
                    }
                    // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                    else{
                        
                        filho.setAvaliacao(avaliar(filho));
                        
                        // adiciona nodo como filho do nodo anterior
                        no.addFilho(filho);
                        
                    }
                }                                        
            }                
        }            
    }
    
    private void generateChildrenForActionRemove( No no ){
        
        List<String> allowedRemoves = no.getJogador() == MAX_TURN ? info.getAllowedRemoves(no.getTabuleiro()) : info.getOpponentAllowedRemoves(no.getTabuleiro()); 
                        
        for (String allowedRemove : allowedRemoves ){
            
            int[][] novoTabuleiro = cloneState(no.getTabuleiro() );

            String[] removeCoords = allowedRemove.split(",");
            int removeLine = Integer.parseInt( removeCoords[0]);
            int removeCol = Integer.parseInt( removeCoords[1]);

            novoTabuleiro[removeLine][removeCol] = VAZIO;
            
            int jogador = no.getJogador() == MAX_TURN ? MIN_TURN : MAX_TURN;
            int pecasPorColocar = no.getPecasPorColocar();
            int profundidade = no.getProfundidade() - 1;

            
            // Cria nodo para jogada de mover peça
            No filho = new No();
            filho.setJogador(jogador);
            filho.setPecaAlterada(allowedRemove);
            filho.setPecasPorColocar(pecasPorColocar);
            filho.setProfundidade(profundidade);
            filho.setTabuleiro(novoTabuleiro);
            filho.setComeuPeca(true);

            

            if ( filho.getProfundidade() < profundidadeMaxima && filho.getPecasPorColocar() == 0) {                    
                
                generateChildren( filho, SET_ACTION );
                
                // adiciona nodo como filho do nodo anterior
                no.addFilho( filho );
            }
            else if( filho.getProfundidade() < profundidadeMaxima){
                
                generateChildren( filho, MOVE_ACTION );
                
                // adiciona nodo como filho do nodo anterior
                no.addFilho( filho );
                
            }
            else{
                
                filho.setAvaliacao(avaliar(filho));
                
                // adiciona nodo como filho do nodo anterior
                no.addFilho( filho );
                
            }
        }
    }
    
    
    public String executeMinimax()
    {
        int melhorAvaliacao = getMinimax( raiz );
        
        List<String> escolhas = new ArrayList();
        
        for ( No filho : raiz.getFilhos() ){
            
            if ( filho.getAvaliacao() == melhorAvaliacao ){
                
                escolhas.add(filho.getPecaAlterada());
                
            }
            
        }
        
        if ( !escolhas.isEmpty() )
        {
            Random random = new Random();
            int selecionado = random.nextInt(escolhas.size());

            return escolhas.get(selecionado);
        }
        
        
        return "";
        
    }
    
    private int getMinimax( No no )
    {
        int valorMaximo = -999999999;
        int valorMinimo = 999999999;
        
        if ( no.getFilhos().isEmpty() )
        {
            return no.getAvaliacao();
        }
        
        for ( No filho : no.getFilhos() )
        {
            int avaliacaoFilho = getMinimax( filho );
            
            if ( no.getJogador() == MAX_TURN)
            {
                valorMaximo = avaliacaoFilho > valorMaximo ? avaliacaoFilho : valorMaximo;
            }
            else
            {
                valorMinimo = avaliacaoFilho < valorMinimo ? avaliacaoFilho : valorMinimo;
            }
        }
                
        no.setAvaliacao(no.getJogador() == MAX_TURN ? valorMaximo : valorMinimo);
        
        return no.getAvaliacao();
                
    }
    
    
    
    
    
    private int[][] cloneState( int[][] state )
    {
        int[][] clone = new int[state.length][];
        for( int i = 0; i < state.length; i++ )
        {
            clone[i] = Arrays.copyOf( state[i], state[i].length );
        }
        
        return clone;
    }
    
    
    
    
    
    public int avaliar(No no){
        
        //Instancia a classe de avaliação
        EvaluationFunction avaliacao;
        avaliacao = new EvaluationFunction(this.info, no.getTabuleiro());
        
        //Verifica de quem é a vez de jogar
        //Caso jogador 1, faz as condiçoes baseadas nas informaçoes das peças dele no tabuleiro
        if ( no.getJogador() == MAX_TURN ){
            
            //1 para jogador fechou linha e 0 se não fechou
            int jogadorQueFechouLinha = no.isComeuPeca() ? 1 : 0;
            
            if ( no.getPecasPorColocar() == 0 && info.getPlayerSpots( no.getTabuleiro() ).size() <= 3 ){
                return avaliacao.phase3(jogadorQueFechouLinha);
            }
            else if (no.getPecasPorColocar() == 0 ){                
                return avaliacao.phase2(jogadorQueFechouLinha);             
            }
            else{                
                return avaliacao.phase1(jogadorQueFechouLinha);             
            }
            
        }
        //Senão, faz as condiçoes baseadas nas informações das peças do oponente
        else{
            
            //-1 para oponente fechou linha e 0 se não fechou
            int jogadorQueFechouLinha = no.isComeuPeca() ? -1 : 0;
            
            if ( no.getPecasPorColocar() == 0 && info.getOpponentSpots( no.getTabuleiro() ).size() <= 3 )            
            {
                return avaliacao.phase3(jogadorQueFechouLinha);
            }
            else if ( no.getPecasPorColocar() == 0 )
            {
                return avaliacao.phase2(jogadorQueFechouLinha);
            }
            else
            {
                return avaliacao.phase1(jogadorQueFechouLinha);
            }
            
        }

    }
    
    
}
