package com.beertastic.beertastic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beertastic.beertastic.ScaleConntector.AbstractScaleConnector;
import com.beertastic.beertastic.ScaleConntector.ScaleConnector;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LocalMultiplayer extends ListenerRegisterActivity implements IScaleEventListener, IGameLogicListener {
    ListView listView;
    TextView activePlayer;
    TextView gameMessage;
    LinearLayout gameView;

    ArrayAdapter<Player> adapter;

    private ScaleProcessor scaleProcessor;
    GameLogic game = GameLogic.getInstance();

    UsbManager usbManager;

    private double bloodAlcohol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_multiplayer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scaleProcessor = new ScaleProcessor();
        game.setListener(this);
        scaleProcessor.registerListener(game);
        scaleProcessor.registerListener(this);

        ScaleConnector.createInstance(getApplicationContext());
        AbstractScaleConnector.getInstance().onUsbConnect();

        activePlayer = (TextView) findViewById(R.id.active_player);
        gameMessage = (TextView) findViewById(R.id.game_message);
        gameView = (LinearLayout) findViewById(R.id.gameview);

        activePlayer.setTextColor(getColor(android.R.color.white));
        gameMessage.setTextColor(getColor(android.R.color.white));

        activePlayer.setPadding(50, 50, 50, 0);
        gameMessage.setPadding(50, 0, 50, 50);

        listView = (ListView) findViewById(R.id.listview_players);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        adapter = new ArrayAdapter<Player>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, game.getPlayers()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);

                // Set list item color from color array
                view.setBackgroundColor(Color.parseColor(game.getPlayers().get(position).getColor()));

                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(game.getPlayers().get(position).getName());
                text1.setTextColor(getColor(android.R.color.white));
                text1.setTypeface(null, Typeface.BOLD);
                text1.setPadding(50, 50, 50, 0);
                text2.setText(String.valueOf(game.getPlayers().get(position).getScore()));
                text2.setTextColor(getColor(android.R.color.white));
                text2.setPadding(50, 0, 50, 50);

                return view;
            }
        };
        // Assign adapter to ListView
        listView.setAdapter(adapter);

        FloatingActionButton scaleButton = (FloatingActionButton) findViewById(R.id.scale);
        scaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(myIntent);
            }
        });

        FloatingActionButton awardsButton = (FloatingActionButton) findViewById(R.id.awards);
        awardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(game.getPlayers().size() == 0){
                    Snackbar.make(view, "No awards without users", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                Intent myIntent = new Intent(view.getContext(), AwardCeremony.class);
                startActivity(myIntent);
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LocalMultiplayer.this);
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
                        if (input.getText().length() > 0) { // only if name provided
                            game.addPlayer(input.getText().toString());
                        } else {
                            Snackbar.make(view, "Please enter a name!", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                if(position == 0){
                    Snackbar.make(view, "Cannot modify current player", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(LocalMultiplayer.this);
                builder.setTitle("Edit Player");

                // Set up the input
                final EditText input = new EditText(LocalMultiplayer.this);
                // Expected input type
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                input.setText(game.getPlayers().get(position).getName());

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().length() > 0) {
                            game.changePlayerName(position, input.getText().toString());
                        } else {
                            Snackbar.make(view, "Please enter a name!", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    }
                });
                builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(LocalMultiplayer.this).setTitle("Confirm Delete")
                                .setMessage("Are you sure to delete " + game.getPlayers().get(position).getName() + "?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Perform Action & Dismiss dialog
                                                dialog.dismiss();
                                                // Notify user, need to find new view since origin gets deleted
                                                Snackbar.make(getWindow().findViewById(R.id.listview_players), "Player " + game.getPlayers().get(position).getName() + " removed", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                game.removePlayer(position);
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
                    }

                    ;
                });

                builder.setView(input);
                AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
            }
        });
    }

    @Override
    public void onWeightUpdate(double newWeight) {
        int bottleWeight = 315;
        WeightToDrinkConverter conv = new WeightToDrinkConverter(bottleWeight, 330);
        final int finalWeight = conv.getAmount(newWeight);
        scaleProcessor.postData(newWeight - bottleWeight);
    }

    @Override
    public void onAlcoholUpdate(double newBloodAlcohol) {
        if (newBloodAlcohol > bloodAlcohol) {
            DecimalFormat df = new DecimalFormat("#.###");
            bloodAlcohol = Double.valueOf(df.format(newBloodAlcohol));
        }
    }

    @Override
    public void onScaleConnect() {
        Snackbar.make(getWindow().findViewById(R.id.listview_players), "Scale connected", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onScaleDisconnect() {
        Snackbar.make(getWindow().findViewById(R.id.listview_players), "Scale disconnected", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onUIUpdate(final ArrayList<Player> players, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activePlayer.setText(players.get(0).getName());
                gameMessage.setText(message);
                gameView.setBackgroundColor(Color.parseColor(players.get(0).getColor()));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onLimitExceeded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LocalMultiplayer.this);
                builder.setTitle("Police Control!");
                bloodAlcohol = 0;

                builder.setMessage("Please test your alcohol levels by blowing on the sensor and then press OK");
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        builder.setMessage("You have around " + bloodAlcohol + " percent of alcohol in your blood.");
                        builder.setPositiveButton("Damn it!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.setMessage("You have " + bloodAlcohol + " percent of alcohol in your blood.");
                                builder.create().show();
                            }
                        });
                        builder.create().show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onDrinkRemoved() {

    }

    @Override
    public void onDrinkPlaced(double amount) {

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
        }

        ;
    };

}
