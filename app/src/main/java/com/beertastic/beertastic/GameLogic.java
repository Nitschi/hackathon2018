package com.beertastic.beertastic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
    private double amountBefore = 0;
    private double limit = 0;


    public void resetGame(){
        players = new ArrayList<Player>();
        startRound();
    }

    private void startRound() {
        currentState = WAIT_FOR_NEXT_PLAYER;
        Random rand = new Random();
        limit = rand.nextDouble() * 80 + 20; //random value between 20 and 100

        //TODO: Send message "currentPlayer, please place your drink on the scale"
    }

    private void weighBeerBeforeDrink(double amount){
        amountBefore = amount;
        //send message that they can now drink

    }

    private int calculateScore(double amount){
        if(amount > amountBefore) {
            //new Beer or Error?
        }
        double deltaAmount = amount - amountBefore;
        int score = (int) deltaAmount;
        if( deltaAmount < limit) {
            return score;
        }
        else {
            //TODO: STRAFWASSER!
            return 0;
        }
    }

    private void evaluateRound(double amount) {
        Player currentPlayer = players.get(0);
        currentPlayer.setScore(currentPlayer.getScore() + calculateScore(amount));
        //TODO: show Evaluation here
        Collections.rotate(players , -1 );  //rotates the list, effectively setting the finished player to the
                                            // end and the second player to the beginning of the list

    }

    public void addPlayer (String name) {
        players.add(new Player(name));
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
            weighBeerBeforeDrink(amount);
        } else if (currentState == PLAYER_DRINKING) {
            currentState = BEER_ON_SCALE_AFTER_DRINK;
            evaluateRound(amount);
        }

    }
}
