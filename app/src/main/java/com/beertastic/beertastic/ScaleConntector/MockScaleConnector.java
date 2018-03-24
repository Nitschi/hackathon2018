package com.beertastic.beertastic.ScaleConntector;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Johannes on 24.03.2018.
 */

public class MockScaleConnector extends AbstractScaleConnector {
    Timer timer;

    public static void createInstance()
    {
        if (instance == null)
        {
            instance = new MockScaleConnector();
        }
    }

    public MockScaleConnector()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (IScaleUpdateListener listener : updateListeners) {
                    listener.onWeightUpdate(42);
                }

            }
        }, 100, 100);
    }
    @Override
    public void connectToScale() {

    }

    @Override
    public String getLastReceivedMessage() {
        return "42";
    }

    @Override
    public void onUsbConnect() {

    }

    @Override
    public void onUsbDisconnect() {

    }
}
