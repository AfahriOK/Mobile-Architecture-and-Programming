package com.zybooks.weighttrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.textUsername);
        password = findViewById(R.id.textPassword);
        Button loginButton = findViewById(R.id.login_button);
        Button createAccountButton = findViewById(R.id.signup_button);
        database = Database.getInstance(getApplicationContext());

        loginButton.setOnClickListener(view -> {
            String user = username.getText().toString().toLowerCase().trim();
            String pass = password.getText().toString().trim();
        if (database.verifyUser(user, pass)) {
            login(user);
            }else
            //incorrect
            Toast.makeText(MainActivity.this,"Account Information Invalid",Toast.LENGTH_SHORT).show();
        });

        createAccountButton.setOnClickListener(view -> {
            String user = username.getText().toString().toLowerCase().trim();
            String pass = password.getText().toString().trim();
            addUser(user, pass);
        });
    }

    private void addUser(String user, String pass) {
        /* add user and pass to database and show toast if successful */
        if (database.addUser(user, pass)) {
            Toast.makeText(MainActivity.this, "Account Created!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Username Already Taken! Try Again", Toast.LENGTH_LONG).show();
        }
    }

    private void login(String user) {
        Intent intent = new Intent(this, WeightScreen.class);
        intent.putExtra("user_key", user);
        startActivity(intent);
    }
}