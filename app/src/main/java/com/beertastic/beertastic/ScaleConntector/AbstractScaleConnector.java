package com.beertastic.beertastic.ScaleConntector;

import android.util.Log;

import java.util.ArrayList;

public abstract class AbstractScaleConnector {
    protected String lastReceivedMessage = "";
    protected ArrayList<IScaleUpdateListener> updateListeners = new ArrayList<IScaleUpdateListener>();
    protected static AbstractScaleConnector instance = null;
    public static final String ACTION_USB_PERMISSION = "com.beertastic.beertastic.USB_PERMISSION";

    public static AbstractScaleConnector getInstance()
    {
        return instance;
    }



    public void registerUpdateListener(IScaleUpdateListener listener)
    {
        if (!updateListeners.contains(listener))
        {
            updateListeners.add(listener);
            Log.i("ScaleConnector", "listener registered. Number of registered listeners: " + updateListeners.size());
        }
        {
            Log.i("ScaleConnector", "listener has already been registered");
        }
    }

    public void deregisterUpdateListener(IScaleUpdateListener listener)
    {
        if (updateListeners.contains(listener))
        {
            updateListeners.remove(listener);
            Log.i("ScaleConnector", "listener removed. Number of remaining listeners: " + updateListeners.size());
        }
        else
        {
            Log.e("ScaleConnector", "the listener to be removed was not registered.  Number of remaining listeners: " + updateListeners.size());
        }
    }

    public abstract void connectToScale();

    public abstract String getLastReceivedMessage();

    public abstract void onUsbConnect();

    public abstract void onUsbDisconnect();
}
