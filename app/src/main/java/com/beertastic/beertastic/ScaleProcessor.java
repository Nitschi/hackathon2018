package com.beertastic.beertastic;

import android.util.Log;

import com.beertastic.beertastic.ScaleConntector.IScaleUpdateListener;

import java.util.ArrayList;

/**
 * Created by felix on 24-Mar-18.
 */

interface IScaleEventListener {
    void onDrinkRemoved();
    void onDrinkPlaced(double amount);
}

public class ScaleProcessor {

    private static ScaleProcessor instance;

    public static ScaleProcessor getInstance() {
        if( instance == null) {
            instance = new ScaleProcessor();
        }
        return instance;
    }

    private final int historyLength = 5;
    private double[] history = new double[historyLength];
    private int idx = 0;

    private enum Status {DrinkOn, DrinkOff};

    private Status currStatus = Status.DrinkOff;
    private double currWeight = 0;

    private double emptyThreshold = -100;
    private double noiseThreshold = 2.;

    private ArrayList<IScaleEventListener> listeners = new ArrayList<>();

    private ScaleProcessor(){

    }

    public void registerListener(IScaleEventListener i){
        if (!listeners.contains(i))
        {
            listeners.add(i);
            Log.i("ScaleProcessor", "listener registered. Number of registered listeners: " + listeners.size());
        }
        {
            Log.i("ScaleProcessor", "listener has already been registered");
        }
    }

    public void deregisterUpdateListener(IScaleEventListener i)
    {
        if (listeners.contains(i))
        {
            listeners.remove(i);
            Log.i("ScaleProcessor", "listener removed. Number of remaining listeners: " + listeners.size());
        }
        else
        {
            Log.e("ScaleProcessor", "the listener to be removed was not registered.  Number of remaining listeners: " + listeners.size());
        }
    }

    public void postData(double newWeight){
        Log.v("ScaleProcessor", "current weight: " + String.valueOf(newWeight));

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
            Log.w("ScaleProcessor", "noice threshold (" + String.valueOf(noiseThreshold) + ") exeeded. max-min=" + String.valueOf(max-min));
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
