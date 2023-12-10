/**
 * @author Afahri Kerr (AfahriAKerr91@gmail.com)
 * @version 2.0
 * @since 12/07/2023
 * Purpose: Allow user to see and keep track of their weight entries
 */

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

/**
 * This class contains the logic to control the weight screen.
 */
public class WeightScreen extends AppCompatActivity {

    private String user;
    private int goal;
    private Database database;
    private ArrayList<String> weights = new ArrayList<>();
    private ArrayList<String> goalDiffs = new ArrayList<>();

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
        goalDiffs = calcGoalDiff();

        //Create View and Adapter
        RecyclerView recyclerView = findViewById(R.id.data_grid);
        Adapter adapter = new Adapter(this, dates, weights, goalDiffs, user);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addButton = findViewById(R.id.add_button);
        Button goalButton = findViewById(R.id.goal_button);
        Button clearButton = findViewById(R.id.clear_button);

        addButton.setOnClickListener(view -> addWeight());
        goalButton.setOnClickListener(view -> setGoal());
        clearButton.setOnClickListener(view -> deleteAll());
    }

    /**
     * Method to delete all the weight entries for the user.
     */
    private void deleteAll() {
        if (database.clearAll(user)) {
            Toast.makeText(getApplicationContext(), "List Cleared", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, WeightScreen.class);
            intent.putExtra("user_key", user);
            startActivity(intent);
        } else {
            Toast.makeText(WeightScreen.this, "No Weights Deleted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to generate the differences between a users weight and their goal.
     * @return An array of the differences or N/A if no goal set.
     */
    private ArrayList<String> calcGoalDiff() {

        //Calculates the difference between user's weight and their goal
        if (goal == 0) {
            for (int i = 0; i < weights.size(); i++) {
                goalDiffs.add("N/A");
            }
        } else {
            for (int i = 0; i < weights.size(); i++) {
                int weight = Integer.parseInt(weights.get(i));
                int diff = weight - goal;

                if (diff > 0) {
                    goalDiffs.add("-" + diff);
                } else if (diff < 0) {
                    goalDiffs.add("+" + abs(diff));
                } else {
                    goalDiffs.add(String.valueOf(diff));
                }
            }
        }

        return goalDiffs;
    }

    /**
     * Method to launch the set goal screen.
     */
    private void setGoal() {
        Intent intent = new Intent(this, SetGoalScreen.class);
        intent.putExtra("user_key", user);
        startActivity(intent);
    }

    /**
     * Method to launch the add weight screen.
     */
    private void addWeight() {
        Intent intent = new Intent(this, AddWeightScreen.class);
        intent.putExtra("user_key", user);
        startActivity(intent);
    }
}