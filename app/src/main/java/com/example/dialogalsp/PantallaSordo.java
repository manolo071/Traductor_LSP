package com.example.dialogalsp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class PantallaSordo extends AppCompatActivity {

    private EditText editTextMessage;
    private ImageButton buttonEnviar;
    private RecyclerView mensajesEnviadosRecyclerView;
    private MensajeAdapter mensajeAdapter;
    private TextToSpeech textToSpeech;
    private ImageButton buttonMicro, buttonRecepcionVoz;
    private RecyclerView mensajesRecibidosRecyclerView;
    private MensajeAdapter mensajesRecibidosAdapter;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean recepcionVozActiva = false;

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
        buttonMicro = findViewById(R.id.buttonMicro);
        buttonRecepcionVoz = findViewById(R.id.buttonRecepcionVoz);
        mensajesRecibidosRecyclerView = findViewById(R.id.mensajesRecibidos);

        // 2. Configurar RecyclerView para mensajes recibidos
        mensajesRecibidosAdapter = new MensajeAdapter();
        mensajesRecibidosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mensajesRecibidosRecyclerView.setAdapter(mensajesRecibidosAdapter);

        // 3. Configurar reconocimiento de voz
        inicializarReconocimientoVoz();

        // Configurar RecyclerView
        mensajeAdapter = new MensajeAdapter();
        mensajesEnviadosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mensajesEnviadosRecyclerView.setAdapter(mensajeAdapter);

        // 4. Configurar botones
        buttonMicro.setEnabled(false);
        buttonMicro.setAlpha(0.5f);
        buttonMicro.setOnClickListener(v -> activarMicrofono());
        buttonRecepcionVoz.setOnClickListener(v -> toggleRecepcionVoz());

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
                    mensajesEnviadosRecyclerView.scrollToPosition(mensajeAdapter.getItemCount() - 1);
                    mensajesEnviadosRecyclerView.smoothScrollToPosition(mensajeAdapter.getItemCount() - 1);
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

    private void inicializarReconocimientoVoz() {
        // Verificar permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                buttonMicro.setColorFilter(Color.RED); // Micrófono activo (rojo)
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                buttonMicro.setColorFilter(Color.WHITE); // Volver a blanco
            }

            @Override
            public void onError(int error) {
                buttonMicro.setColorFilter(Color.WHITE);
                Toast.makeText(PantallaSordo.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String textoReconocido = matches.get(0);
                    mensajesRecibidosAdapter.agregarMensaje(textoReconocido);
                    mensajesRecibidosRecyclerView.smoothScrollToPosition(
                            mensajesRecibidosAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void activarMicrofono() {
        if (!buttonMicro.isEnabled()) {
            return; // No hacer nada si el botón está deshabilitado
        }

        try {
            speechRecognizer.startListening(speechRecognizerIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al activar micrófono", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleRecepcionVoz() {
        recepcionVozActiva = !recepcionVozActiva;

        // Cambiar color del icono
        if (recepcionVozActiva) {
            buttonRecepcionVoz.setColorFilter(Color.BLACK); // Activado (negro)
            buttonMicro.setEnabled(true); // Habilitar micrófono
            buttonMicro.setAlpha(1f); // Hacer completamente visible
        } else {
            buttonRecepcionVoz.setColorFilter(Color.WHITE); // Desactivado (blanco)
            buttonMicro.setEnabled(false); // Deshabilitar micrófono
            buttonMicro.setAlpha(0.5f); // Hacer semitransparente
        }

        // Feedback visual
        Toast.makeText(this,
                recepcionVozActiva ? "Recepción de voz ACTIVADA" : "Recepción de voz DESACTIVADA",
                Toast.LENGTH_SHORT).show();
    }
}