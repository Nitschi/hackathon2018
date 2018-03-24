package com.beertastic.beertastic;

import android.graphics.Color;

/**
 * Created by felix on 24/03/18.
 */

public class Player {
    public Player(String name, Color color){
        this.name = name;
        this.color = color;
    }

    private String name;
    private int score = 0;
    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

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
