package com.beertastic.beertastic;

import java.util.ArrayList;

/**
 * Created by felix on 24-Mar-18.
 */

interface IScaleEventListener {
    void onDrinkRemoved();
    void onDrinkPlaced(double amount);
}

public class ScaleProcessor {

    private final int historyLength = 5;
    private double[] history = new double[historyLength];
    private int idx = 0;

    private enum Status {DrinkOn, DrinkOff};

    private Status currStatus = Status.DrinkOff;
    private double currWeight = 0;

    private double emptyThreshold = -10;
    private double noiseThreshold = 2.;

    private ArrayList<IScaleEventListener> listeners = new ArrayList<>();

    public ScaleProcessor(){

    }

    public void registerListener(IScaleEventListener i){
        listeners.add(i);
    }

    public void postData(double newWeight){
        history[idx] = newWeight;
        idx = ++idx % historyLength;


        double min = history[0];
        double max = history[0];
        double avg = history[0];
        for (int i=1; i<historyLength; i++){
            min = Math.min(min, history[i]);
            max = Math.max(max, history[i]);
            avg += history[i];
        }
        avg /= historyLength;

        if (max - min > noiseThreshold){
            return;
        }

        if (avg < emptyThreshold && currStatus == Status.DrinkOn) {
            postDrinkRemoved();
            currStatus = Status.DrinkOff;
        }
        else if (avg >= emptyThreshold && currStatus == Status.DrinkOff){
            postDrinkPlaced(avg);
            currStatus = Status.DrinkOn;
            currWeight = avg;
        }
    }

    private void postDrinkRemoved(){
        for (IScaleEventListener l: listeners){
            l.onDrinkRemoved();
        }
    }

    private void postDrinkPlaced(double newWeight){
        for (IScaleEventListener l: listeners){
            l.onDrinkPlaced(newWeight);
        }
    }
}
