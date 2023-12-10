/**
 * @author Afahri Kerr (AfahriAKerr91@gmail.com)
 * @version 2.0
 * @since 12/07/2023
 * Purpose: Allow user to create a personal account
 */

package com.zybooks.weighttrackerapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class contains the logic to control the account creation screen.
 */
public class CreateAccountScreen extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText secondPassword;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_screen);

        username = findViewById(R.id.textUser);
        password = findViewById(R.id.textPass);
        secondPassword = findViewById(R.id.second_textPass);
        database = Database.getInstance(getApplicationContext());
        Button createAccountButton = findViewById(R.id.create_account_button);

        createAccountButton.setOnClickListener(view -> {
            try {
                addUser();
            } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                     BadPaddingException | InvalidKeyException |
                     InvalidAlgorithmParameterException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This method gets the user information, encrypts it and passes it to the database.
     */
    private void addUser() throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, InvalidKeySpecException {

        String user = username.getText().toString().toLowerCase().trim();
        String pass = password.getText().toString().trim();
        String secondPass = secondPassword.getText().toString().trim();

        if (user.length() != 0 && !user.equals("null")) {
            if (pass.length() != 0 && pass.equals(secondPass)) {
                if (database.addUser(user, pass)) {
                    Toast.makeText(getApplicationContext(),
                            "Account Created!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateAccountScreen.this,
                            "Username Already Taken! Try Again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(CreateAccountScreen.this,
                        "Passwords Do Not Match!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(CreateAccountScreen.this,
                    "Please Enter A Username", Toast.LENGTH_LONG).show();
        }
    }
}