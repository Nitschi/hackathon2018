package com.beertastic.beertastic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlcoholTestActivity extends ListenerRegisterActivity {

    private double maxBloodAlcohol = 0;
    TextView promilleDisp;
    Button okayButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_test);
        promilleDisp = (TextView)findViewById(R.id.promilleDisplay);
        okayButton = (Button)findViewById(R.id.promilleButton);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onWeightUpdate(double newWeight) {

    }

    @Override
    public void onAlcoholUpdate(double newBloodAlcohol) {

        if(newBloodAlcohol > maxBloodAlcohol) {
            maxBloodAlcohol = newBloodAlcohol;
        }
        final double finalBloodAlcohol = maxBloodAlcohol;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                promilleDisp.setText(String.valueOf(finalBloodAlcohol) + "Promille");
            }
        });
    }

    @Override
    public void onScaleConnect() {

    }

    @Override
    public void onScaleDisconnect() {

    }
}
