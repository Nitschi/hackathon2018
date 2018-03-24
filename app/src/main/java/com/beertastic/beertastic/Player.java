package com.beertastic.beertastic;

/**
 * Created by felix on 24/03/18.
 */

public class Player {
    public Player(String name){
        this.name = name;
    }

    private String name;
    private int score = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
