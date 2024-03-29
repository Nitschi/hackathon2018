package com.beertastic.beertastic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.beertastic.beertastic.ScaleConntector.AbstractScaleConnector;
import com.beertastic.beertastic.ScaleConntector.ScaleConnector;

import java.util.ArrayList;


public class MainActivity extends ListenerRegisterActivity implements IGameLogicListener {

    TextView t1;
    TextView t2;
    TextView ttest;
    int removedCounter = 0;
    int placedCounter = 0;
    double placedAmount = 0;
    //ScaleConnector scale = null;

    private GameLogic gameLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i("MainActivity", "onCreate called.");

        t1 = (TextView)findViewById(R.id.textViewSerialOuput);
        t2 = (TextView)findViewById(R.id.textViewPercentage);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWeightUpdate(double newWeight) {
        Log.i("onWeightUpdate_UI", "OnUpdate Called. Received string: " + newWeight);
        WeightToDrinkConverter conv = new WeightToDrinkConverter(315, 330);
        final int finalWeight = conv.getAmount(newWeight);
        final int finalPercentage = conv.getPercentage(newWeight);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t1.setText(String.valueOf(finalWeight) + "ml");
                t2.setText(String.valueOf(finalPercentage) + "%");
            }
        });
        ScaleProcessor.getInstance().postData(finalWeight);
    }

    @Override
    public void onAlcoholUpdate(double newAlcoholRatio) {
        Log.i("onAlcoholUpdate_UI", "onAlcoholUpdate called. Value: " + newAlcoholRatio);
    }

    @Override
    public void onDrinkRemoved() {
        removedCounter++;
        //updateEvent();
    }

    @Override
    public void onDrinkPlaced(double amount) {
        placedCounter++;
        placedAmount = amount;
        //updateEvent();
    }

    private void updateEvent(){
        final int finalPlacedCounter = placedCounter;
        final int finalRemovedCounter = removedCounter;
        final double finalAmount = placedAmount;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ttest.setText("Removed: " + finalRemovedCounter + ", Placed: " + finalPlacedCounter + "Amount: " + placedAmount);
            }
        });
    }

    @Override
    public void onScaleConnect() {
        Log.i("MainActivity_UI", "scale connected.");
    }

    @Override
    public void onScaleDisconnect() {
        Log.i("MainActivity_UI", "scale disconnected.");
    }

    @Override
    public void onUIUpdate(final ArrayList<Player> players, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ttest.setText("CurrPlayer: " + players.get(0).getName() + ", Message: " + message);
            }
        });
    }

    @Override
    public void onLimitExceeded() {

    }
}
