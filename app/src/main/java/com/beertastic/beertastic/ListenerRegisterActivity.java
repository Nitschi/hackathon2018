package com.beertastic.beertastic;

import android.support.v7.app.AppCompatActivity;

import com.beertastic.beertastic.ScaleConntector.AbstractScaleConnector;
import com.beertastic.beertastic.ScaleConntector.IScaleUpdateListener;

/**
 * Created by Johannes on 25.03.2018.
 */

abstract class ListenerRegisterActivity extends AppCompatActivity implements IScaleUpdateListener {
    @Override
    protected void onResume()
    {
        super.onResume();
        AbstractScaleConnector.getInstance().registerUpdateListener(this);
    }

    @Override

    protected void onPause()
    {
        super.onPause();
        AbstractScaleConnector.getInstance().deregisterUpdateListener(this);
    }
}
