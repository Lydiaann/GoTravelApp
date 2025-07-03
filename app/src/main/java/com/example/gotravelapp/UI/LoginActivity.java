package com.example.gotravelapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gotravelapp.R;
import com.example.gotravelapp.database.Repository;
import com.example.gotravelapp.entities.User;
import com.google.android.material.textfield.TextInputEditText;

import com.example.gotravelapp.utils.PasswordUtils;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private Repository repository;
    // Example hardcoded credentials
    private final String validUsername = "Admin";
    private final String validPassword = "Password123";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // XML layout filename
        repository = new Repository(getApplication());
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = new User();
            user.setUsername("Admin");
            user.setPasswordHash(PasswordUtils.hashPassword("Password123"));
            repository.insertUser(user); //
        });

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String hash = PasswordUtils.hashPassword(password);

            Executors.newSingleThreadExecutor().execute(() -> {
                User user = repository.getUserByUsername(username);
                if (user != null && user.getPasswordHash().equals(hash)) {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}