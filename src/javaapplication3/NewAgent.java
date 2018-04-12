/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication3;

import NineMensMorris.GameInfo;
import NineMensMorris.PlayerAgent;

/**
 *
 * @author Gustavo
 */
public class NewAgent implements PlayerAgent {

    public NewAgent() {
        
    }

    @Override
    public String setPiece(GameInfo gi) {
        Arvore arvore = new Arvore(3, Arvore.SET_ACTION, gi);
        
        return arvore.executeMinimax();

    }

    @Override
    public String movePiece(GameInfo gi) {
        Arvore arvore = new Arvore(3, Arvore.SET_ACTION, gi);
        
        return arvore.executeMinimax();
    }

    @Override
    public String removePiece(GameInfo gi) {
        Arvore arvore = new Arvore(3, Arvore.SET_ACTION, gi);
        
        return arvore.executeMinimax();
    }
    
}
