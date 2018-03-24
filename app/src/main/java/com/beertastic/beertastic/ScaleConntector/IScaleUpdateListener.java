package com.beertastic.beertastic.ScaleConntector;

/**
 * Created by Johannes on 24.03.2018.
 */
public interface IScaleUpdateListener{
    void onWeightUpdate(double newWeight);
    void onScaleConnect();
    void onScaleDisconnect();
}
