package com.example.dialogalsp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginBtn;
    TextView registerRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        registerRedirect = findViewById(R.id.registerRedirect);

        loginBtn.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.equals("test@correo.com") && password.equals("1234")) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        registerRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

    }
}
