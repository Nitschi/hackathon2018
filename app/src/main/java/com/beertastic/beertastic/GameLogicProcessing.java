package com.beertastic.beertastic;


import java.util.ArrayList;

/**
 * Created by alexander on 24.03.18.
 */
interface IGameLogicListener {
    void onUIUpdate(ArrayList<Player> players, String message);
    void onLimitExceeded();
}

