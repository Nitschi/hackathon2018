package com.beertastic.beertastic;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by alexander on 24.03.18.
 */

public class GameLogic implements IScaleEventListener {

    private static GameLogic game;
    private GameLogic() {

        players = new ArrayList<Player>();
    }
    public static GameLogic getInstance() {
        if( GameLogic.game == null) {
            GameLogic.game = new GameLogic();
        }
        return GameLogic.game;
    }

    private ArrayList<Player> players;

    private static final int WAIT_FOR_NEXT_PLAYER = 0;
    private static final int BEER_ON_SCALE_BEFORE_DRINK = 1;
    private static final int PLAYER_DRINKING = 2;
    private static final int BEER_ON_SCALE_AFTER_DRINK = 3;
    private int currentState = WAIT_FOR_NEXT_PLAYER;


    private void startRound() {
        currentState = WAIT_FOR_NEXT_PLAYER;
        Player currentPlayer = players.get(0);
        //Send message "currentPlayer, please place your drink on the scale"
    }

    private void weighBeerBeforeDrink(){
        //save weight
        //send message that they can now drink

    }

    private void evaluateRound(int score) {
        Player currentPlayer = players.get(0);
        currentPlayer.setScore(currentPlayer.getScore() + score);
        Collections.rotate(players , -1 );  //rotates the list, effectively setting the finished player to the
                                            // end and the second player to the beginning of the list

    }

    private void addToScore(Player player, int addScore) {
        player.setScore(player.getScore() + addScore);
    }


    private void addPlayer (Player newPlayer) {
        players.add(newPlayer);
    }

    @Override
    public void onDrinkRemoved() {
        if( currentState == BEER_ON_SCALE_BEFORE_DRINK) {
            currentState = PLAYER_DRINKING;
        } else {
            currentState = WAIT_FOR_NEXT_PLAYER;
            startRound();
        }
    }

    @Override
    public void onDrinkPlaced(double amount) {
        if ( currentState == WAIT_FOR_NEXT_PLAYER) {
            currentState = BEER_ON_SCALE_BEFORE_DRINK;
            weighBeerBeforeDrink();
        } else if (currentState == PLAYER_DRINKING) {
            currentState = BEER_ON_SCALE_AFTER_DRINK;
            evaluateRound((int) amount);
        }

    }
}
