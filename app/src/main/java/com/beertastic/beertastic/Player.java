package com.beertastic.beertastic;

import android.graphics.Color;

/**
 * Created by felix on 24/03/18.
 */

public class Player {
    public Player(String name){
        this.name = name;
        this.color = ColorFactory.getColor(); // new color from factory
    }

    private String name;
    private int score = 0;
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
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
