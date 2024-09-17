package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

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

        // Obtener los datos pasados por Intent
        Intent intent = getIntent();
        tareaId = intent.getStringExtra("tareaId");
        String nombreTarea = intent.getStringExtra("nombreTarea");
        String descripcionTarea = intent.getStringExtra("descripcionTarea");
        boolean estadoTarea = intent.getBooleanExtra("estadoTarea", true);

        // Establecer datos en los campos
        etNombreTarea.setText(nombreTarea);
        etDescripcionTarea.setText(descripcionTarea);
        swEstadoTarea.setChecked(estadoTarea);

        db = FirebaseFirestore.getInstance();

        btnGuardarCambios.setOnClickListener(v -> {
            String nuevoNombre = etNombreTarea.getText().toString();
            String nuevaDescripcion = etDescripcionTarea.getText().toString();
            boolean nuevoEstado = swEstadoTarea.isChecked(); // Estado: activo o completado

            actualizarTarea(nuevoNombre, nuevaDescripcion, nuevoEstado);
        });
    }

    private void actualizarTarea(String nombre, String descripcion, boolean estado) {
        db.collection("Proyectos").document(proyectoId)
                .collection("Tareas").document(tareaId)
                .update("nombre", nombre, "descripcion", descripcion, "estado", estado)
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
