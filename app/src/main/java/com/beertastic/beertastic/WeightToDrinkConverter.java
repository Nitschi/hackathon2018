package com.beertastic.beertastic;

/**
 * Created by felix on 24-Mar-18.
 */

public class WeightToDrinkConverter {

    private double bottleWeight;
    private double bottleCapacity;

    public WeightToDrinkConverter(double bottleWeight, double bottleCapacity){
        this.bottleCapacity = bottleCapacity;
        this.bottleWeight = bottleWeight;
    }

    public double getRawPercentage(double weight){
        return (weight - bottleWeight) / bottleCapacity;
    }

    public int getPercentage(double weight){
        return Math.min(100, Math.max(0, (int) Math.round(getRawPercentage(weight)*100)));
    }

    public int getAmount(double weight){
        return Math.min((int) bottleCapacity, Math.max(0, (int) Math.round(weight-bottleWeight)));
    }
}
