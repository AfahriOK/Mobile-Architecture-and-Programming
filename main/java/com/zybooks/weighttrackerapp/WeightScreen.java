package com.zybooks.weighttrackerapp;

import static java.lang.Math.abs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeightScreen extends AppCompatActivity {

    private String user;
    private int goal;
    private Database database;
    private ArrayList<String> weights = new ArrayList<>();
    private ArrayList<String> goals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_screen);

        Intent intent = getIntent();
        user = intent.getStringExtra("user_key");
        database = Database.getInstance(getApplicationContext());

        //Set up array lists to pass to view
        goal = database.getGoal(user);
        ArrayList<String> dates = database.getDates(user);
        weights = database.getWeights(user);
        goals = calcGoalDiff();

        //Create View and Adapter
        RecyclerView recyclerView = findViewById(R.id.data_grid);
        Adapter adapter = new Adapter(this, dates, weights, goals, user);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addButton = findViewById(R.id.add_button);
        Button goalButton = findViewById(R.id.goal_button);

        addButton.setOnClickListener(view -> addWeight());
        goalButton.setOnClickListener(view -> setGoal());
    }

    private ArrayList<String> calcGoalDiff() {

        //Calculates the difference between user's weight and their goal
        if (goal == 0) {
            for (int i = 0; i < weights.size(); i++) {
                goals.add("N/A");
            }
        } else {
            for (int i = 0; i < weights.size(); i++) {
                int weight = Integer.parseInt(weights.get(i));
                int diff = weight - goal;

                if (diff > 0) {
                    goals.add("-" + diff);
                } else if (diff < 0) {
                    goals.add("+" + abs(diff));
                } else {
                    goals.add(String.valueOf(diff));
                }
            }
        }

        return goals;
    }
    private void setGoal() {
        Intent intent = new Intent(this, SetGoalScreen.class);
        intent.putExtra("user_key", user);
        startActivity(intent);
    }

    private void addWeight() {
        Intent intent = new Intent(this, AddWeightScreen.class);
        intent.putExtra("user_key", user);
        startActivity(intent);
    }
}