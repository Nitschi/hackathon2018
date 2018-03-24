package com.beertastic.beertastic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LocalMultiplayer extends AppCompatActivity {
    ListView listView;
    private String[] colors = {"#F44336", "#2196F3", "#FFC107","#4CAF50", "#795548",
            "#607D8B", "#E91E63", "#9C27B0", "#CDDC39", "#00BCD4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_multiplayer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView = (ListView) findViewById(R.id.listview_players);

        //Player player;
        //ArrayList<Player> players = new ArrayList<Player>();

        final Player[] playersArray = new Player[] {
                new Player("Johannes"),
                new Player("Alex"),
                new Player("Felix"),
                new Player("Kohli")
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, playersArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                position = position % colors.length; // avoid overflow
                // Set list item color from color array
                view.setBackgroundColor(Color.parseColor(colors[position]));

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(playersArray[position].getName());

                text2.setText(String.valueOf(playersArray[position].getScore()));

                return view;
            }
        };
        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

}
