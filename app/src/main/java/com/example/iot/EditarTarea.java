package com.example.iot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class EditarTarea extends AppCompatActivity {

    private EditText etNombreTarea, etDescripcionTarea;
    private Switch swEstadoTarea;
    private Button btnGuardarCambios;
    private FirebaseFirestore db;
    private String tareaId;
    private String proyectoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tarea);

        etNombreTarea = findViewById(R.id.et_nombre_tarea);
        etDescripcionTarea = findViewById(R.id.et_descripcion_tarea);
        swEstadoTarea = findViewById(R.id.sw_estado_tarea);
        btnGuardarCambios = findViewById(R.id.btn_guardar_cambios);

        Intent intent = getIntent();
        tareaId = intent.getStringExtra("tareaId");
        proyectoId = intent.getStringExtra("proyectoId");

        if (tareaId == null || proyectoId == null) {
            Toast.makeText(this, "Error: No se han proporcionado los IDs necesarios", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        // Cargar la tarea desde Firestore
        cargarTarea();

        btnGuardarCambios.setOnClickListener(v -> {
            String nuevoNombre = etNombreTarea.getText().toString();
            String nuevaDescripcion = etDescripcionTarea.getText().toString();
            boolean nuevoEstado = swEstadoTarea.isChecked();
            actualizarTarea(nuevoNombre, nuevaDescripcion, nuevoEstado);
        });
    }

    private void cargarTarea() {
        db.collection("Proyectos").document(proyectoId)
                .collection("Tareas").document(tareaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreTarea = documentSnapshot.getString("nombre");
                        String descripcionTarea = documentSnapshot.getString("descripcion");
                        Boolean estadoTarea = documentSnapshot.getBoolean("estado");

                        // Verificar y manejar valores nulos
                        if (nombreTarea != null) {
                            etNombreTarea.setText(nombreTarea);
                        } else {
                            etNombreTarea.setText(""); // Establecer un valor predeterminado
                        }

                        if (descripcionTarea != null) {
                            etDescripcionTarea.setText(descripcionTarea);
                        } else {
                            etDescripcionTarea.setText(""); // Establecer un valor predeterminado
                        }

                        // Manejar el estado de la tarea de manera segura
                        if (estadoTarea != null) {
                            swEstadoTarea.setChecked(estadoTarea);
                        } else {
                            swEstadoTarea.setChecked(false); // Establecer un valor predeterminado si el estado es nulo
                        }
                    } else {
                        Toast.makeText(EditarTarea.this, "La tarea no existe", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarTarea.this, "Error al cargar la tarea", Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarTarea(String nombre, String descripcion, boolean nuevoEstado) {
        if (proyectoId == null || tareaId == null) {
            Toast.makeText(EditarTarea.this, "Error: IDs de tarea o proyecto no válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Proyectos").document(proyectoId)
                .collection("Tareas").document(tareaId)
                .update("nombre", nombre, "descripcion", descripcion, "estado", nuevoEstado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarTarea.this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarTarea.this, "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
                });
    }
}
