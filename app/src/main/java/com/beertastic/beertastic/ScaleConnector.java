package com.beertastic.beertastic;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Johannes on 24.03.2018.
 */

interface IScaleUpdateListener{
    void onWeightUpdate(double newWeight);
    void onScaleConnect();
    void onScaleDisconnect();
}

public class ScaleConnector {
    private static ScaleConnector instance = null;
    public static ScaleConnector getInstance()
    {
        return instance;
    }
    public static void createInstance(android.content.Context context, UsbManager usbManager)
    {
        if (instance == null)
        {
            instance = new ScaleConnector(context, usbManager);
        }
    }

    public static final String ACTION_USB_PERMISSION = "com.beertastic.beertastic.USB_PERMISSION";
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;
    android.content.Context context;
    UsbManager usbManager;
    ByteBuffer byteBuffer;
    ArrayList<IScaleUpdateListener> updateListeners = new ArrayList<IScaleUpdateListener>();

    boolean isConnected = false;
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

    private String lastReceivedMessage = "";

    private  ScaleConnector(android.content.Context context, UsbManager usbManager)
    {
        this.context = context;
        this.usbManager = usbManager;
        byteBuffer = ByteBuffer.allocate(100);
    }

    public void connectToScale()
    {
        Log.i("serial", "connectToScale called");
        connection = usbManager.openDevice(device);
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if (serialPort != null) {
            if (serialPort.open()) { //Set Serial Connection Parameters.
                serialPort.setBaudRate(9600);
                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                serialPort.read(mScaleMeasurementReceivedCallback); //
                Log.i("ScaleConnector", "serial connection estabilshed");

                if (!isConnected) {
                    for (IScaleUpdateListener listener : updateListeners) {
                        listener.onScaleConnect();
                    }
                    isConnected = true;
                }

            } else {
                Log.d("ScaleConnector", "PORT NOT OPEN");
            }
        } else {
            Log.d("ScaleConnector", "PORT IS NULL");
        }
    }
    byte[] currentBytes;

    private UsbSerialInterface.UsbReadCallback mScaleMeasurementReceivedCallback = new UsbSerialInterface.UsbReadCallback() {

        @Override
        public void onReceivedData(byte[] arg0)
        {
            byte[] carryFeed = new byte[] {13, 10};

            Log.v("ScaleConnector", "byte array length: " + arg0.length);

            if ((arg0.length == 2) && (arg0[0] == carryFeed[0]) && (arg0[1] == carryFeed [1]))
            {
                Log.v("serial", "new line");
                int position = byteBuffer.position();
                byte[] array = new byte[byteBuffer.position()];
                byteBuffer.rewind();
                byteBuffer.get(array);

                try {
                    double newValue = Double.parseDouble(new String(array));
                    for (IScaleUpdateListener listener:updateListeners) {
                        listener.onWeightUpdate(newValue);
                    }
                    if (updateListeners.size() == 0)
                    {
                        Log.v("ScaleConnector", "no listener registered.");
                    }
                    lastReceivedMessage = String.valueOf(newValue);
                    Log.v("serial", "position:" + position + " current string: " + lastReceivedMessage);

                }
                catch (Exception exception)
                {
                    Log.e("ScaleConnector", "failed to convert received bytes to double: " + exception.toString());
                }

                byteBuffer.clear();
                return;
            }

            byteBuffer.put(arg0);
            Log.v("serial", "current buffer pos: " + byteBuffer.position());

        }

    };

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    public void onUsbConnect()
    {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    public  void onUsbDisconnect()
    {
        if (isConnected) {
            for (IScaleUpdateListener listener : updateListeners) {
                listener.onScaleDisconnect();
            }
            isConnected = false;
        }
        try {
            serialPort.close();
        }
        catch (Exception exception) {
            Log.d("serial", "tried to close serial connection but serial connection was null " + exception.toString());
        }
    }


}
