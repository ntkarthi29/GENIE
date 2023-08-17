package com.example.genie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (isValidCredentials(username, password)) {
                    // Successful login, transition to the main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finish the login activity
                } else {
                    // Invalid credentials, show error message
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidCredentials(String username, String password) {
        // Perform your validation logic here
        // For demonstration purposes, consider valid credentials as "admin" for both username and password
        return username.equals("admin") && password.equals("admin");
    }
}
