package com.example.dialogalsp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Crear una vista de texto para mostrar un mensaje
        welcomeText = new TextView(this);
        welcomeText.setTextSize(20);
        welcomeText.setPadding(32, 100, 32, 32);

        // Obtener el tipo de usuario desde el intent
        String userType = getIntent().getStringExtra("userType");

        if (userType != null) {
            if (userType.equals("oyente")) {
                welcomeText.setText("Bienvenido/a, usuario oyente 👋");
            } else if (userType.equals("sordo")) {
                welcomeText.setText("Bienvenido/a, usuario sordo 🤟");
            } else {
                welcomeText.setText("Bienvenido a DIALOGALSP");
            }
        } else {
            welcomeText.setText("Bienvenido a DIALOGALSP");
        }

        setContentView(welcomeText);
    }
}
