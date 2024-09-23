package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CrearProyecto extends AppCompatActivity {
    private EditText etNombre, etDescripcion;
    private DatePicker dpFechaLimite;
    private Button btnGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_proyecto);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Referenciar las vistas
        etNombre = findViewById(R.id.et_nombre);
        etDescripcion = findViewById(R.id.et_descripcion);
        dpFechaLimite = findViewById(R.id.dp_fecha_limite);
        btnGuardar = findViewById(R.id.btn_guardar);

        // Configurar el botón "Guardar"
        btnGuardar.setOnClickListener(v -> {
            // Obtener los datos del formulario
            String nombre = etNombre.getText().toString();
            String descripcion = etDescripcion.getText().toString();

            // Obtener la fecha seleccionada en el DatePicker
            int day = dpFechaLimite.getDayOfMonth();
            int month = dpFechaLimite.getMonth();
            int year = dpFechaLimite.getYear();

            // Convertir la fecha en un objeto Calendar
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            long fechaLimite = calendar.getTimeInMillis(); // Puedes almacenarlo como timestamp

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(CrearProyecto.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Crear el objeto del proyecto y guardarlo en Firebase
                guardarProyectoEnFirebase(nombre, descripcion, fechaLimite);
            }
        });
    }

    private void guardarProyectoEnFirebase(String nombre, String descripcion, long fechaLimite) {
        // Crear un nuevo proyecto en Firebase usando un HashMap
        Map<String, Object> proyecto = new HashMap<>();
        proyecto.put("nombre", nombre);
        proyecto.put("descripcion", descripcion);
        proyecto.put("fechaLimite", fechaLimite); // Guardar fecha como timestamp

        // Guardar en la colección "Proyectos"
        db.collection("Proyectos")
                .add(proyecto)
                .addOnSuccessListener(documentReference -> {
                    // Obtener el ID del documento generado por Firebase
                    String proyectoId = documentReference.getId();

                    // Actualizar el proyecto con el ID del documento
                    db.collection("Proyectos")
                            .document(proyectoId)
                            .update("id", proyectoId)
                            .addOnSuccessListener(aVoid -> {
                                // Crear un Intent para devolver el resultado a la actividad principal
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("proyectoId", proyectoId);
                                resultIntent.putExtra("nombre", nombre);
                                resultIntent.putExtra("descripcion", descripcion);
                                resultIntent.putExtra("fechaLimite", fechaLimite);

                                // Establecer el resultado como OK y enviar los datos
                                setResult(RESULT_OK, resultIntent);
                                finish();  // Cerrar la actividad
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CrearProyecto.this, "Error al actualizar el ID del proyecto", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CrearProyecto.this, "Error al crear el proyecto", Toast.LENGTH_SHORT).show();
                });
    }



    public void volverP(View v){
        Intent i = new Intent(this, Principal.class);
        startActivity(i);
    }
}


