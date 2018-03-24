package com.beertastic.beertastic;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LocalMultiplayer extends AppCompatActivity {
    ListView listView;
    private String[] colors = {"#F44336", "#2196F3", "#FFC107","#4CAF50", "#795548",
            "#607D8B", "#E91E63", "#9C27B0", "#CDDC39", "#00BCD4"};

    ArrayList<Player> players;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(LocalMultiplayer.this);
                builder.setTitle("Add Player");

                // Set up the input
                final EditText input = new EditText(LocalMultiplayer.this);
                // Expected input type
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                //builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        players.add(new Player(input.getText().toString()));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setView(input);
                AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
            }
        });

        listView = (ListView) findViewById(R.id.listview_players);

        players = new ArrayList<Player>();
        players.add(new Player("Johannes"));
        players.add(new Player("Felix"));

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, players) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                position = position % colors.length; // avoid overflow
                // Set list item color from color array
                view.setBackgroundColor(Color.parseColor(colors[position]));

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(players.get(position).getName());
                text1.setTextColor(getColor(android.R.color.white));
                text1.setTypeface(null, Typeface.BOLD);
                text1.setPadding(50,50,50,0);
                text2.setText(String.valueOf(players.get(position).getScore()));
                text2.setTextColor(getColor(android.R.color.white));
                text2.setPadding(50,0,50,50);

                return view;
            }
        };
        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LocalMultiplayer.this);
                builder.setTitle("Edit Player");

                // Set up the input
                final EditText input = new EditText(LocalMultiplayer.this);
                // Expected input type
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                input.setText(players.get(position).getName());

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        players.get(position).setName(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(LocalMultiplayer.this).setTitle("Confirm Delete")
                                .setMessage("Are you sure to delete " + players.get(position).getName() + "?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Perform Action & Dismiss dialog
                                                dialog.dismiss();
                                                Snackbar.make(view, "Player " + players.get(position).getName() + " removed", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                players.remove(position);
                                            }
                                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    };
                });

                builder.setView(input);
                AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
            }
        });
    }

}
