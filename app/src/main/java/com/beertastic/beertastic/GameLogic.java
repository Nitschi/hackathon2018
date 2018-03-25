package com.beertastic.beertastic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Alexander on 24.03.18.
 */

public class GameLogic implements IScaleEventListener {

    private static GameLogic game;
    private GameLogic() {

        players = new ArrayList<>();
    }
    public static GameLogic getInstance() {
        if( GameLogic.game == null) {
            GameLogic.game = new GameLogic();
        }
        return GameLogic.game;
    }

    public void setListener(IGameLogicListener listener) {
        this.listener = listener;
    }

    private IGameLogicListener listener = null;
    private ArrayList<Player> players;

    private static final int WAIT_FOR_NEXT_PLAYER = 0;
    private static final int BEER_ON_SCALE_BEFORE_DRINK = 1;
    private static final int PLAYER_DRINKING = 2;
    private static final int BEER_ON_SCALE_AFTER_DRINK = 3;
    private int currentState = -1;
    private double amountBefore = 0;
    private int limit = 0;

    public void resetGame(){
        players = new ArrayList<>();
        startRound();
    }

    public void startRound() {
        currentState = WAIT_FOR_NEXT_PLAYER;
        Random rand = new Random();
        limit = (int) (rand.nextDouble() * 80 + 20); //random value between 20 and 100
        updateUI("Please place your drink onto the scale");
    }

    private void weighBeerBeforeDrink(double amount){
        amountBefore = amount;
        updateUI("Drink now. But know your limit of "+ String.valueOf(limit) + "ml!");

    }

    private int calculateScore(double amount){
        double deltaAmount = amount - amountBefore;
        int score = (int) deltaAmount;
        if( deltaAmount < limit) {
            return score;
        }
        else {
            return 0;
        }
    }

    private void evaluateRound(double amount) {
        Player currentPlayer = players.get(0);
        double deltaAmount = amountBefore - amount;
        int score = (int) deltaAmount;
        if( deltaAmount > limit) {
            score = -20;
        }
        currentPlayer.setScore(currentPlayer.getScore() + score);

        if(score == -20){
            updateUI("Wowowow, slow down! That were " + (int) deltaAmount + "ml instead of " + limit + "ml. " +
                    "Learn your limit! " + score + " points and check your alcohol level!");
            /*try {
                wait(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            limitExceeded();
        } else{
            updateUI("Cheers! You just earned " + score + " additional points! Good Job!");
        }

    }

    public void addPlayer (String name) {
        players.add(new Player(name));
        if (players.size() == 1) startRound();
    }

    public void removePlayer(int index) {
        players.remove(index);
    }

    public void changePlayerName(int index, String name){
        players.get(index).setName(name);
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public ArrayList<Player> getPendingPlayers() {
        if (players.size() > 1) {
            return new ArrayList<>(players.subList(1, players.size()));
        } else {
            return new ArrayList<>();
        }
    }

    public Player getCurrentPlayer() {return players.get(0);}

    private void updateUI(String message){

        if (listener == null) {
            return;
        }
        listener.onUIUpdate(players, message);
    }

    private void limitExceeded(){
        if (listener == null) {
            return;
        }
        listener.onLimitExceeded();
    }

    @Override
    public void onDrinkRemoved() {
        if( currentState == BEER_ON_SCALE_BEFORE_DRINK) {
            currentState = PLAYER_DRINKING;
            updateUI("Enjoy your drink, but don't forget your limit is " + limit + "ml!");
        } else if (currentState == BEER_ON_SCALE_AFTER_DRINK) {
            currentState = WAIT_FOR_NEXT_PLAYER;
            Collections.rotate(players , -1 );  //rotates the list, effectively setting the finished player to the
            // end and the second player to the beginning of the list
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
