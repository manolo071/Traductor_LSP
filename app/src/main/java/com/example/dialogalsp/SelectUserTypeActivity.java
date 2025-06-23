package com.example.dialogalsp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SelectUserTypeActivity extends AppCompatActivity {

    RadioGroup userTypeGroup;
    RadioButton radioOyente, radioSordo;
    Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        userTypeGroup = findViewById(R.id.userTypeGroup);
        radioOyente = findViewById(R.id.radioOyente);
        radioSordo = findViewById(R.id.radioSordo);
        continueBtn = findViewById(R.id.continueBtn);

        continueBtn.setOnClickListener(v -> {
            int selectedId = userTypeGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Selecciona una opción", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            if (selectedId == R.id.radioOyente) {
                intent.putExtra("userType", "oyente");
            } else if (selectedId == R.id.radioSordo) {
                intent.putExtra("userType", "sordo");
            }

            startActivity(intent);
            finish();
        });
    }
}
