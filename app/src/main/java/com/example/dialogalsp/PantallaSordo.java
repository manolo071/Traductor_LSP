package com.example.dialogalsp;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class PantallaSordo extends AppCompatActivity {

    private EditText editTextMessage;
    private ImageButton buttonEnviar;
    private RecyclerView mensajesEnviadosRecyclerView;
    private MensajeAdapter mensajeAdapter;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_sordo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonEnviar = findViewById(R.id.buttonEnviar);
        mensajesEnviadosRecyclerView = findViewById(R.id.mensajesEnviados);

        // Configurar RecyclerView
        mensajeAdapter = new MensajeAdapter();
        mensajesEnviadosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mensajesEnviadosRecyclerView.setAdapter(mensajeAdapter);

        // Inicializar TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(PantallaSordo.this,
                            "El idioma no es compatible", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PantallaSordo.this,
                        "Error al inicializar TextToSpeech", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón de enviar
        buttonEnviar.setOnClickListener(v -> enviarMensaje());
    }

    private void enviarMensaje() {
        String mensaje = editTextMessage.getText().toString().trim();
        if (!mensaje.isEmpty()) {
            // Agregar mensaje al RecyclerView
            mensajeAdapter.agregarMensaje(mensaje);

            // Hacer scroll automático al final de la lista
            mensajesEnviadosRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    // Desplazarse a la última posición
                    mensajesEnviadosRecyclerView.smoothScrollToPosition(mensajeAdapter.getItemCount() - 1);

                    // Alternativa más rápida pero sin animación:
                    // mensajesEnviadosRecyclerView.scrollToPosition(mensajeAdapter.getItemCount() - 1);
                }
            });

            // Reproducir mensaje con TextToSpeech
            textToSpeech.speak(mensaje, TextToSpeech.QUEUE_FLUSH, null, null);

            // Limpiar el EditText
            editTextMessage.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}