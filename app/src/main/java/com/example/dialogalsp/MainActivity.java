package com.example.dialogalsp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CardView btnSordo = findViewById(R.id.btnSordo);
        CardView btnOyente = findViewById(R.id.btnOyente);

        btnSordo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PantallaSordo.class);
            startActivity(intent);
            finish(); // Opcional: cierra esta actividad si no la necesitas más
        });

        btnOyente.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PantallaOyente.class);
            startActivity(intent);
            finish(); // Opcional: cierra esta actividad si no la necesitas más
        });
    }
}
