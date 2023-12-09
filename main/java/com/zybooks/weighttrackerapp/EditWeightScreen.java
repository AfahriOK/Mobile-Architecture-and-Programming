package com.zybooks.weighttrackerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditWeightScreen extends AppCompatActivity {

    final Calendar myCalendar= Calendar.getInstance();
    private Database database;
    private String user;
    private String weight;
    private String date;
    private EditText weightText;
    private EditText dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_weight_screen);

        //Retrieve individual items data
        Intent intent = getIntent();
        user = intent.getStringExtra("user_key");
        date = intent.getStringExtra("date_key");
        weight = intent.getStringExtra("weight_key");

        //Set the forms equal to retrieved data
        weightText = findViewById(R.id.weight);
        weightText.setText(weight);
        dateText = findViewById(R.id.date);
        dateText.setText(date);

        database = Database.getInstance(getApplicationContext());
        Button deleteButton = findViewById(R.id.delete_weight_button);
        deleteButton.setOnClickListener(view -> deleteData());
        Button editButton = findViewById(R.id.edit_weight_button);
        editButton.setOnClickListener(view -> editData());

        DatePickerDialog.OnDateSetListener calendarDate = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        dateText.setOnClickListener(view -> new DatePickerDialog(EditWeightScreen.this, calendarDate, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

    }

    private void updateLabel() {
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateText.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void editData() {
        String newWeight = weightText.getText().toString();
        String newDate = dateText.getText().toString();
        if (database.editWeight(user, date, weight, newDate, newWeight)) {
            Toast.makeText(getApplicationContext(), "Entry Changed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WeightScreen.class);
            intent.putExtra("user_key", user);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteData() {
        if (database.deleteData(user,date,weight)) {
            Toast.makeText(getApplicationContext(), "Entry Deleted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WeightScreen.class);
            intent.putExtra("user_key", user);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Error On Deletion", Toast.LENGTH_SHORT).show();
        }
    }
}