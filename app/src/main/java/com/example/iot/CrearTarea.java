package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class CrearTarea extends AppCompatActivity {

    private EditText etNombreTarea, etDescripcionTarea;
    private Button btnGuardarTarea;
    private FirebaseFirestore db;
    private String proyectoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_tarea);

        // Inicializar vistas
        etNombreTarea = findViewById(R.id.et_nombre_tarea);
        etDescripcionTarea = findViewById(R.id.et_descripcion_tarea);
        btnGuardarTarea = findViewById(R.id.btn_guardar_tarea);

        db = FirebaseFirestore.getInstance();

        // Obtener el ID del proyecto desde el Intent
        Intent intent = getIntent();
        proyectoId = intent.getStringExtra("proyectoId");

        if (proyectoId == null) {
            Toast.makeText(this, "ID del proyecto no disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar el botón de guardar
        btnGuardarTarea.setOnClickListener(v -> guardarTarea());
    }

    private void guardarTarea() {
        String nombre = etNombreTarea.getText().toString();
        String descripcion = etDescripcionTarea.getText().toString();
        String estado = "activa"; // Estado predeterminado

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo documento en la colección de tareas
        Tarea nuevaTarea = new Tarea(null, nombre, descripcion, estado); // ID se asignará después

        db.collection("Proyectos").document(proyectoId).collection("Tareas")
                .add(nuevaTarea)
                .addOnSuccessListener(documentReference -> {
                    // Actualizar la tarea con el ID asignado por Firestore
                    nuevaTarea.setId(documentReference.getId());
                    documentReference.set(nuevaTarea)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(CrearTarea.this, "Tarea creada exitosamente", Toast.LENGTH_SHORT).show();
                                finish(); // Volver a la actividad anterior
                            })
                            .addOnFailureListener(e -> Toast.makeText(CrearTarea.this, "Error al crear la tarea", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(CrearTarea.this, "Error al crear la tarea", Toast.LENGTH_SHORT).show());
    }
}
