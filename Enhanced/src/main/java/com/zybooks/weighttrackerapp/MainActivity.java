/**
 * @author Afahri Kerr (AfahriAKerr91@gmail.com)
 * @version 2.0
 * @since 12/07/2023
 * Purpose: Allow user to log in using personal account or create one if necessary
 */

package com.zybooks.weighttrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class contains the logic to control the initial login main screen.
 */
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
            try {
                if (database.verifyUser(user, pass)) {
                    login(user);
                    } else
                    //incorrect
                    Toast.makeText(MainActivity.this,"Account Information Invalid",Toast.LENGTH_SHORT).show();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                     InvalidAlgorithmParameterException | IllegalBlockSizeException |
                     BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

        createAccountButton.setOnClickListener(view -> createAccount());
    }

    /**
     * Method to launch the create account screen.
     */
    private void createAccount() {
        Intent intent = new Intent(this, CreateAccountScreen.class);
        startActivity(intent);
    }

    /**
     * Method to launch the weight screen.
     * @param user Username of user.
     */
    private void login(String user) {
        Intent intent = new Intent(this, WeightScreen.class);
        intent.putExtra("user_key", user);
        startActivity(intent);
    }
}