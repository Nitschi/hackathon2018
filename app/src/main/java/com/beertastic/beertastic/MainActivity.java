package com.beertastic.beertastic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IScaleUpdateListener {

    Button b1;
    TextView t1;
    //ScaleConnector scale = null;
    UsbManager usbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity", "onCreate called.");

        b1 = (Button)findViewById(R.id.buttonGetLastSerialOutput);
        t1 = (TextView)findViewById(R.id.textViewSerialOuput);

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
//        if (scale == null) {
//            Log.i("MainActivity_UI", "new Scale Object created.");
//            scale = new ScaleConnector(this, usbManager);
//        }
        ScaleConnector.createInstance(this, usbManager);

        b1.setOnClickListener(b1OnClick);


        IntentFilter filter = new IntentFilter();
        filter.addAction(ScaleConnector.ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        //call the onUsbConnect method to cover the case in which the scale is already connected with the phone when the app starts
        ScaleConnector.getInstance().onUsbConnect();

    }


    OnClickListener b1OnClick = new OnClickListener(){
        public void onClick(View v)
        {

            Log.i("MainActivity_UI", "Button pressed. Received string: " + ScaleConnector.getInstance().getLastReceivedMessage());
            t1.setText("new int: " + ScaleConnector.getInstance().getLastReceivedMessage());
        }



    };

    @Override
    protected void onResume()
    {
        super.onResume();
        ScaleConnector.getInstance().registerUpdateListener(this);
    }

    @Override
    
    protected void onPause()
    {
        super.onPause();
        ScaleConnector.getInstance().deregisterUpdateListener(this);
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ScaleConnector.ACTION_USB_PERMISSION)) {
                boolean granted =
                        intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    ScaleConnector.getInstance().connectToScale();
                    //b1.setEnabled(true);
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                ScaleConnector.getInstance().onUsbConnect();
                //b1.setEnabled(true);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                ScaleConnector.getInstance().onUsbDisconnect();
                //b1.setEnabled(false);
            }
        };
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        final double finalWeight = newWeight;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t1.setText("current weight: " + finalWeight);
            }
        });

    }
}
