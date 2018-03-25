package com.beertastic.beertastic;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AwardCeremony extends AppCompatActivity {



    ArrayList<String> labels = new ArrayList<String>();
    GameLogic game = GameLogic.getInstance();
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<BarEntry> scores = new ArrayList<BarEntry>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Integer> colors = new ArrayList<Integer>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_award_ceremony);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HorizontalBarChart barChart = (HorizontalBarChart) findViewById(R.id.barchart);
        players = game.getPlayers();
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p1.getScore() - p2.getScore(); // Descending
            }
        });

        for(int i = 0; i< players.size(); i++){
            scores.add(new BarEntry(players.get(i).getScore(),i));
            names.add(players.get(i).getName());
            colors.add(Color.parseColor(players.get(i).getColor()));

        }
        BarDataSet bardataset = new BarDataSet(scores, "Points");
        BarData data = new BarData(names, bardataset);
        barChart.setData(data); // set the data and list of labels into chart

        barChart.setDescription("Final Scores");  // set the description
        bardataset.setColors(colors);
        barChart.animateY(2000);

    }
}
