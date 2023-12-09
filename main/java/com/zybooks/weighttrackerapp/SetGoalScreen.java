package com.zybooks.weighttrackerapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SetGoalScreen extends AppCompatActivity {

    private static final int REQUEST_SEND_SMS_PERMISSIONS = 0;
    private EditText goalText;
    private EditText phoneText;
    private Database database;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal_screen);

        Intent intent = getIntent();
        user = intent.getStringExtra("user_key");
        database = Database.getInstance(getApplicationContext());
        Button permissionButton = findViewById(R.id.permission_button);
        Button numberButton = findViewById(R.id.add_number_button);
        Button setGoalButton = findViewById(R.id.set_goal_button);
        goalText = findViewById(R.id.goal);
        phoneText = findViewById(R.id.phone_number);


        setGoalButton.setOnClickListener(view -> setGoal());
        permissionButton.setOnClickListener(view -> getPermission());
        numberButton.setOnClickListener(view -> addNumber());
    }

    private void setGoal() {
        // add goal to database
        if (goalText.getText().length() != 0) {
            int goal = Integer.parseInt(goalText.getText().toString());
                if (database.setGoal(user, goal)) {
                    Toast.makeText(getApplicationContext(), "Goal set", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, WeightScreen.class);
                    intent.putExtra("user_key", user);
                    startActivity(intent);
                } else {
                    Toast.makeText(SetGoalScreen.this, "Error adding your goal", Toast.LENGTH_LONG).show();
                }
        } else {
            Toast.makeText(SetGoalScreen.this, "Please enter a valid goal", Toast.LENGTH_LONG).show();
        }
    }


    private void getPermission() {
        String permission = Manifest.permission.SEND_SMS;
        String number = database.getNumber(user);
        if (!number.equals("EMPTY")) {
            if (ContextCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, REQUEST_SEND_SMS_PERMISSIONS);
                Toast.makeText(SetGoalScreen.this, "Permissions are currently denied\nPress allow or go to android settings", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SetGoalScreen.this, "Permissions already granted", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SetGoalScreen.this, "Please enter a phone number first", Toast.LENGTH_LONG).show();
        }
    }


    private void addNumber() {
        // add number to database
        String phoneNumber = phoneText.getText().toString();

        if (phoneNumber.matches("\\d{10}")) {
            if (database.addNumber(user, phoneNumber)){
                Toast.makeText(SetGoalScreen.this,"Your number has been added",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SetGoalScreen.this,"There was an error adding your number",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SetGoalScreen.this,"Please enter a valid phone number",Toast.LENGTH_LONG).show();
        }


    }
}