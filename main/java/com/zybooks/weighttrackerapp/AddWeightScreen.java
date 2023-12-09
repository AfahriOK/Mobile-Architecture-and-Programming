package com.zybooks.weighttrackerapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddWeightScreen extends AppCompatActivity {

    final Calendar myCalendar= Calendar.getInstance();
    private EditText dateText;
    private EditText weightText;
    private String user;
    private Database database;
    private int goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight_screen);

        Intent intent = getIntent();
        user = intent.getStringExtra("user_key");
        database = Database.getInstance(getApplicationContext());
        goal = database.getGoal(user);
        Button addWeightButton = findViewById(R.id.add_weight_button);
        weightText = findViewById(R.id.weight);
        dateText = findViewById(R.id.date);

        addWeightButton.setOnClickListener(view -> confirmWeight());

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        dateText.setOnClickListener(view -> new DatePickerDialog(AddWeightScreen.this, date, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateLabel() {
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateText.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void confirmWeight() {
        String date = "0";
        int weight = 0;

        /* add weight to the database */
        if (dateText.getText().length() != 0) {
            date = dateText.getText().toString();
        }
        if (weightText.getText().length() != 0) {
            weight = Integer.parseInt(weightText.getText().toString());
        }

        //Makes sure all entries are valid before sending SMS
        if ((weight != 0) && (!date.equals("0"))) {
            /* if goal weight is equal to weight and has sms permission send congratulatory sms */
            if ((goal == weight) && (hasPermission())) {
                String phoneNumber = database.getNumber(user);
                //Makes sure the user has entered a phone number
                if (!phoneNumber.equals("EMPTY")) {
                    String message = "You did It!!! Congratulations on reaching your goal!";
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phoneNumber, null, message, null, null);
                } else {
                    //Reminder if they elected to receive SMS and haven't set a valid number
                    Toast.makeText(getApplicationContext(), "Please add your phone number to receive SMS", Toast.LENGTH_LONG).show();
                }
            }
        }

        //Checks to make sure there is a valid entry
        if (date.equals("0")) {
            Toast.makeText(AddWeightScreen.this, "Please enter a date", Toast.LENGTH_LONG).show();
            return;
        }
        if (weight == 0) {
            Toast.makeText(AddWeightScreen.this, "Please enter a weight", Toast.LENGTH_LONG).show();
            return;
        }

        if (database.addWeight(user, date, weight)) {
            Toast.makeText(getApplicationContext(), "Weight added", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WeightScreen.class);
            intent.putExtra("user_key", user);
            startActivity(intent);
        } else {
            //Database design won't allow you to enter the same entry
            Toast.makeText(AddWeightScreen.this, "Duplicate entry", Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasPermission() {
        String permission = Manifest.permission.SEND_SMS;

        //returns true if permission granted else false
        return ContextCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED;
    }
}