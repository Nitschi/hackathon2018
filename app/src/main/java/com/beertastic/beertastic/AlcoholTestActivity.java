package com.beertastic.beertastic;


/**
 * Created by alexander on 25.03.18.
 */

public class AlcoholTestActivity extends ListenerRegisterActivity {

    private double bloodAlcoholMax = 0;
    @Override
    public void onWeightUpdate(double newWeight) {

    }

    @Override
    public void onAlcoholUpdate(double newBloodAlcohol) {
        if(newBloodAlcohol > bloodAlcoholMax)
        {
            bloodAlcoholMax = newBloodAlcohol;
        }
    }

    @Override
    public void onScaleConnect() {

    }

    @Override
    public void onScaleDisconnect() {

    }
}
