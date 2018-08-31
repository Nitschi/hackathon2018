package com.beertastic.beertastic.ScaleConntector;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Johannes on 24.03.2018.
 */



public class ScaleConnector extends AbstractScaleConnector {

    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;
    android.content.Context context;
    UsbManager usbManager;
    ByteBuffer byteBuffer;

    boolean isConnected = false;

    public static void createInstance(android.content.Context context)
    {
        if (instance == null)
        {
            instance = new ScaleConnector(context);
        }
    }

    private  ScaleConnector(android.content.Context context)
    {
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(context.USB_SERVICE);
        byteBuffer = ByteBuffer.allocate(100);

        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractScaleConnector.ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(broadcastReceiver, filter);

    }

    @Override
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
                    String receivedString = new String(array);
                    double weightValue = Double.parseDouble(receivedString)/1000;

                    for (IScaleUpdateListener listener:updateListeners) {
                        listener.onWeightUpdate(weightValue);
                    }
                    if (updateListeners.size() == 0)
                    {
                        Log.v("ScaleConnector", "no listener registered.");
                    }
                    lastReceivedMessage = String.valueOf(weightValue);
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

    @Override
    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    @Override
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

    @Override
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

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AbstractScaleConnector.ACTION_USB_PERMISSION)) {
                boolean granted =
                        intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    AbstractScaleConnector.getInstance().connectToScale();
                    //b1.setEnabled(true);
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                AbstractScaleConnector.getInstance().onUsbConnect();
                //b1.setEnabled(true);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                AbstractScaleConnector.getInstance().onUsbDisconnect();
                //b1.setEnabled(false);
            }
        };
    };


}
