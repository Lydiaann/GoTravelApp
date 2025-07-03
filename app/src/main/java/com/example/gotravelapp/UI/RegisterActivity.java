package com.example.gotravelapp.UI;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gotravelapp.R;
import com.example.gotravelapp.database.Repository;
import com.example.gotravelapp.entities.User;
import com.example.gotravelapp.utils.PasswordUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        repository = new Repository(getApplication());

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                User existingUser = repository.getUserByUsername(username);
                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show());
                } else {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPasswordHash(PasswordUtils.hashPassword(password));

                    repository.insertUser(newUser);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish(); // Close register screen and return to login
                    });
                }
            });
        });
    }
}
