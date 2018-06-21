/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhominimax;


import NineMensMorris.GameInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Carlos
 */
public abstract class MinimaxTree {
    
    public static final int MAX_TURN = 1; 
    public static final int MIN_TURN = 2;
    
    public static final int SET_ACTION = 1; 
    public static final int MOVE_ACTION = 2; 
    public static final int REMOVE_ACTION = 3; 
    
    private int depth = 0 ;  //MAX depth
    
    private GameInfo gameInfo;            
    private Node root;        
    
    public MinimaxTree( int depth,int action, GameInfo gameInfo )
    {
        this.depth = depth;
        
        this.gameInfo = gameInfo;
          
        buildTree( action );
    }
    
    public String executeMinimax()
    {
        int selectedValue = getMinimax( root );
        
        List<String> choices = new ArrayList();
        
        for ( Node node : root.children )
        {
            if ( node.score == selectedValue )
            {
                choices.add( node.value );                
            }
        }
        
        if ( !choices.isEmpty() )
        {
            Random random = new Random();
            int selected = random.nextInt(choices.size());

            return choices.get( selected);
        }
        
        
        return "";
        
    }
    
    private int getMinimax( Node node )
    {
        //int maxValue = -999999999;
        //int minValue = 999999999;
        
        if ( node.children.isEmpty() )
        {
            return node.score;
        }
        
        for ( Node child : node.children )
        {
            int childValue = getMinimax( child );
                                    
            if ( node.playerTurn == MAX_TURN )
            {
                node.score = childValue > node.score ? childValue : node.score;
                
                if ( node.parent != null && node.score > node.parent.score )
                {
                    break;
                }
                //maxValue = childValue > maxValue ? childValue : maxValue;
            }
            else
            {
                node.score = childValue < node.score ? childValue : node.score;
                
                if ( node.parent != null && node.score < node.parent.score )
                {
                    break;
                }
                
                //minValue = childValue < minValue ? childValue : minValue;
            }
            
            //node.score = node.playerTurn == MAX_TURN ? maxValue : minValue;
            
        }
                
        //node.score = node.playerTurn == MAX_TURN ? maxValue : minValue;
        
        return node.score;
                
    }
    
    private void buildTree( int action )
    {
        this.root = new Node( MAX_TURN, gameInfo.getSpots(), gameInfo.getPiecesToPlace(), gameInfo.getOpponentPiecesToPlace() );
        
        generateChildren( root, action );
        
        //System.out.println("fim de geração");
        
    }
    
    private void generateChildren( Node node, int action )
    {
                        
        switch (action) {
            case SET_ACTION:
                generateChildrenForActionSet( node );
                break;
            case MOVE_ACTION:
                generateChildrenForActionMove( node );
                break;
            case REMOVE_ACTION:
                generateChildrenForActionRemove( node );
                break;
            default:
                break;
        }
        
        
    }
    
    private void generateChildrenForActionSet( Node node )
    {
        // lista com coordenadas de casas vazias para o estado do nodo
        List<String> spots = gameInfo.getEmptySpots( node.getGameState() );

        // percorre lista com coordenadas de casas vazias
        for( String spot : spots )
        {
            // cria novo estado do jogo a partir de um clone do estado anterior
            int[][] newState = cloneState( node.getGameState() );

            String[] coords = spot.split(",");
            int line = Integer.parseInt( coords[0]);
            int col = Integer.parseInt( coords[1]);

            // coloca peça do jogador no novo estado
            newState[line][col] = node.playerTurn;

            // subtraí quantidade de peças que precisam ser colocadas no tabuleiro
            int piecesToPlace = node.playerTurn == MAX_TURN ? node.piecesToPlace - 1 : node.piecesToPlace;
            int opponentePiecesToPlace = node.playerTurn == MIN_TURN ? node.opponentePiecesToPlace - 1 : node.opponentePiecesToPlace;

            
            List<String> allowedRemoves = null;

            // Se jogador forma linha com 3 peças então pode remover peça
            // Nesse caso colocar peça e remover peça são consideradas apenas uma jogada
            if (( node.playerTurn == MAX_TURN && gameInfo.isPlayerLineOfThree( spot, newState )) ||
                ( node.playerTurn == MIN_TURN && gameInfo.isOpponentLineOfThree( spot, newState )) )
            {
                
                // lista com coordenadas de peças que podem ser removidas 
                allowedRemoves = node.playerTurn == MAX_TURN 
                                                  ? gameInfo.getAllowedRemoves( newState )
                                                  : gameInfo.getOpponentAllowedRemoves( newState ); 
                
                // percorre lista com coordenadas de peças que podem ser removidas
                for (String allowedRemove : allowedRemoves )
                {
                    // cria novo estado para a remoção de peça a partir do clone do estado anterior
                    int[][] newStateRemove = cloneState( newState );

                    String[] removeCoords = allowedRemove.split(",");
                    int removeLine = Integer.parseInt( removeCoords[0]);
                    int removeCol = Integer.parseInt( removeCoords[1]);

                    // remove peça no novo estado
                    newStateRemove[removeLine][removeCol] = 0;

                    // Cria nodo para jogada de remover peça
                    Node child = new Node( node.playerTurn == MAX_TURN ? MIN_TURN : MAX_TURN, newStateRemove, piecesToPlace, opponentePiecesToPlace );
                    child.value = spot;
                    // adiciona nodo como filho do nodo anterior
                    node.addChild( child );
                    
                    // verifica próxima ação de acordo com as peças que falta colocar do jogador
                    int childAction = (( child.playerTurn == MAX_TURN && child.piecesToPlace > 0 ) ||
                                    ( child.playerTurn == MIN_TURN && child.opponentePiecesToPlace > 0 ))
                                        ? SET_ACTION
                                        : MOVE_ACTION;

                    // caso algum dos jogadores esteja com menos de 3 peças então considera fim do jogo e calcula pontuação do nodo folha
                    if ( gameInfo.getPlayerSpots( newStateRemove ).size() < 3 ||
                         gameInfo.getOpponentSpots( newStateRemove ).size() < 3 )
                    {                        
                        child.score = scoreGameState( child );
                    }
                    // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                    else if ( child.level < depth)
                    {                    
                        generateChildren( child, childAction );
                    }
                    // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                    else
                    {                        
                        child.score = scoreGameState( child  );
                    }

                }                                                
            }

            // Caso nenhum nodo para a jogada de remover peça foi criado então cria-se somente uma jogada para colocar peça 
            if ( allowedRemoves == null || allowedRemoves.isEmpty() )
            {
                
                // Cria nodo para jogada de colocar peça
                Node child = new Node( node.playerTurn == MAX_TURN ? MIN_TURN : MAX_TURN, newState, piecesToPlace, opponentePiecesToPlace );
                child.value = spot;

                // adiciona nodo como filho do nodo anterior
                node.addChild( child );

                // verifica próxima ação de acordo com as peças que falta colocar do jogador
                int childAction = (( child.playerTurn == MAX_TURN && child.piecesToPlace > 0 ) ||
                                    ( child.playerTurn == MIN_TURN && child.opponentePiecesToPlace > 0 ))
                                        ? SET_ACTION
                                        : MOVE_ACTION;

                // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                if ( child.level < depth)
                {                    
                    generateChildren( child, childAction );
                }
                // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                else
                {                    
                    child.score = scoreGameState( child );
                }
            }
        }
    }
    
    private void generateChildrenForActionMove( Node node )
    {
        // lista com coordenadas de movimentos permitidos para o estado do nodo
        List<String> allowedMoves = node.playerTurn == MAX_TURN
                                    ? gameInfo.getAllowedMoves( node.getGameState() )
                                    : gameInfo.getOpponentAllowedMoves( node.getGameState() );

        // Caso não haja movimentos permitidos então o jogo acaba e é calculada a pontuação para o nó folha
        if ( allowedMoves.isEmpty() )
        {            
            node.score = scoreGameState( node );
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
                int[][] newState = cloneState( node.getGameState() );

                String target = coords[ x ];
                String[] targetCoords = target.split(",");
                int targetLine = Integer.parseInt( targetCoords[0]);
                int targetCol = Integer.parseInt( targetCoords[1]);

                // realiza movimentação de peça no novo estado do jogo
                newState[sourceLine][sourceCol] = 0;
                newState[targetLine][targetCol] = node.playerTurn;

                List<String> allowedRemoves = null;

                // Se jogador forma linha com 3 peças então pode remover peça
                // Nesse caso colocar peça e remover peça são consideradas apenas uma jogada
                if (( node.playerTurn == MAX_TURN && gameInfo.isPlayerLineOfThree( target, newState )) ||
                    ( node.playerTurn == MIN_TURN && gameInfo.isOpponentLineOfThree( target, newState )) )
                {
                    // lista com coordenadas de peças que podem ser removidas 
                    allowedRemoves = node.playerTurn == MAX_TURN 
                                                      ? gameInfo.getAllowedRemoves( newState )
                                                      : gameInfo.getOpponentAllowedRemoves( newState ); 
                    
                    // percorre lista com coordenadas de peças que podem ser removidas
                    for (String allowedRemove : allowedRemoves )
                    {
                        // cria novo estado para a remoção de peça a partir do clone do estado anterior
                        int[][] newStateRemove = cloneState( newState );

                        String[] removeCoords = allowedRemove.split(",");
                        int removeLine = Integer.parseInt( removeCoords[0]);
                        int removeCol = Integer.parseInt( removeCoords[1]);

                        // remove peça no novo estado
                        newStateRemove[removeLine][removeCol] = 0;

                        // Cria nodo para jogada de mover peça
                        Node child = new Node( node.playerTurn == MAX_TURN ? MIN_TURN : MAX_TURN, newStateRemove );
                        child.value = source+";"+target;
                        // adiciona nodo como filho do nodo anterior
                        node.addChild( child );

                        // caso algum dos jogadores esteja com menos de 3 peças então considera fim do jogo e calcula pontuação do nodo folha
                        if ( gameInfo.getPlayerSpots( newStateRemove ).size() < 3 ||
                             gameInfo.getOpponentSpots( newStateRemove ).size() < 3 )
                        {
                            //child.score = scoreGameState( newStateRemove, child.playerTurn, child.piecesToPlace, child.opponentePiecesToPlace );
                            child.score = scoreGameState( child );
                        }    
                        // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                        else if ( child.level < depth)
                        {                    
                            generateChildren( child, MOVE_ACTION );
                        }
                        // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                        else
                        {                            
                            child.score = scoreGameState( child );
                        }

                    }                                                
                }

                // Caso nenhum nodo para a jogada de remover peça foi criado então cria-se somente uma jogada para colocar peça 
                if ( allowedRemoves == null || allowedRemoves.isEmpty() )
                {
                    // Cria nodo para jogada de mover peça
                    Node child = new Node( node.playerTurn == MAX_TURN ? MIN_TURN : MAX_TURN, newState );
                    child.value = source+";"+target;

                    // adiciona nodo como filho do nodo anterior
                    node.addChild( child );

                    // caso não tenha alcançado a profundidade estipulada então gera nós filhos
                    if ( child.level < depth)
                    {                    
                        generateChildren( child, MOVE_ACTION );
                    }
                    // profundidade estipulada atingida, nesse caso calcula pontuação do nó folha
                    else
                    {                        
                        child.score = scoreGameState( child );
                    }
                }                                        
            }                
        }            
    }
    
    private void generateChildrenForActionRemove( Node node )
    {
        
        List<String> allowedRemoves = node.playerTurn == MAX_TURN 
                                                          ? gameInfo.getAllowedRemoves( node.getGameState() )
                                                          : gameInfo.getOpponentAllowedRemoves( node.getGameState() ); 
        
        // subtraí quantidade de peças que precisam ser colocadas no tabuleiro
        int piecesToPlace = node.playerTurn == MAX_TURN ? node.piecesToPlace - 1 : node.piecesToPlace;
        int opponentePiecesToPlace = node.playerTurn == MIN_TURN ? node.opponentePiecesToPlace - 1 : node.opponentePiecesToPlace;
        
        int parentAction = (( node.playerTurn == MAX_TURN && node.piecesToPlace > 0 ) ||
                                    ( node.playerTurn == MIN_TURN && node.opponentePiecesToPlace > 0 ))
                                        ? SET_ACTION
                                        : MOVE_ACTION;
        
//        
        for (String allowedRemove : allowedRemoves )
        {
            int[][] newState = cloneState( node.getGameState() );

            String[] removeCoords = allowedRemove.split(",");
            int removeLine = Integer.parseInt( removeCoords[0]);
            int removeCol = Integer.parseInt( removeCoords[1]);

            newState[removeLine][removeCol] = 0;
                        
            // Cria nodo para jogada de colocar peça
            Node child = null;
            
            if ( parentAction == SET_ACTION )
            {                            
                child = new Node( node.playerTurn == MAX_TURN ? MIN_TURN : MAX_TURN, newState, piecesToPlace, opponentePiecesToPlace );
            }
            else
            {
                child = new Node( node.playerTurn == MAX_TURN ? MIN_TURN : MAX_TURN, newState );
            }
                        
            child.value = allowedRemove;

            // adiciona nodo como filho do nodo anterior
            node.addChild( child );
            
            int childAction = (( child.playerTurn == MAX_TURN && child.piecesToPlace > 0 ) ||
                                    ( child.playerTurn == MIN_TURN && child.opponentePiecesToPlace > 0 ))
                                        ? SET_ACTION
                                        : MOVE_ACTION;

            if ( child.level < depth)
            {                    
                generateChildren( child, childAction );
            }
            else
            {                
                child.score = scoreGameState( child );
            }
        }
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
    
    public abstract int scoreGameState( Node node  );
    
        
    public class Node
    {
        private int playerTurn = MAX_TURN;
        private int[][] gameState;
                
        private int piecesToPlace = 0;
        private int opponentePiecesToPlace = 0; 
                        
        private int score = 0;
        private int level = 0;
        
        private String value = "";
        private String path = "";
        
        private Node parent;
        private List<Node> children = new ArrayList();
        
        public Node( int playerTurn, int[][] gameState, int piecesToPlace, int opponentPiecesToPlace )
        {            
            this.playerTurn = playerTurn;
            this.gameState = gameState;
            
            this.piecesToPlace = piecesToPlace;
            this.opponentePiecesToPlace = opponentPiecesToPlace;
            
            this.score = playerTurn == MAX_TURN ? -999999999 : 999999999;
            
        }
        
        public Node( int playerTurn, int[][] gameState )
        {
            this.playerTurn = playerTurn;
            this.gameState = gameState;
            
            this.score = playerTurn == MAX_TURN ? -999999999 : 999999999;
        }
        
        public void addChild( Node node )
        {            
            node.parent = this;
            node.path = this.path + "." + this.children.size();
            node.level = this.level + 1;
            
            children.add( node );
        }
        
        public void addChild( int playerTurn, int[][] gameState )
        {
            Node child = new Node( playerTurn, gameState );
            
            addChild( child );
        }

        public int[][] getGameState() 
        {
            return gameState;
        }
        
        public int getPlayerTurn() {
            return playerTurn;
        }

        public int getPiecesToPlace() {
            return piecesToPlace;
        }

        public int getOpponentePiecesToPlace() {
            return opponentePiecesToPlace;
        }

        public String getValue() {
            return value;
        }
        
        public Node getParent()
        {
            return parent;
        }
    }
    
}