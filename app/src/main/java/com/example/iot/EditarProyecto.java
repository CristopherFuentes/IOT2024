package com.example.iot;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditarProyecto extends AppCompatActivity {

    private EditText etNombre, etDescripcion, etFechaLimite;
    private Button btnGuardar;
    private FirebaseFirestore db;
    private String proyectoId;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_proyecto);

        // Inicializar vistas
        etNombre = findViewById(R.id.et_nombre);
        etDescripcion = findViewById(R.id.et_descripcion);
        etFechaLimite = findViewById(R.id.et_fecha_limite);
        btnGuardar = findViewById(R.id.btn_guardar);

        db = FirebaseFirestore.getInstance();

        // Obtener el ID del proyecto desde el Intent
        Intent intent = getIntent();
        proyectoId = intent.getStringExtra("proyectoId");

        if (proyectoId == null) {
            Toast.makeText(this, "ID del proyecto no disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar el DatePicker
        etFechaLimite.setOnClickListener(v -> {
            new DatePickerDialog(EditarProyecto.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Cargar los datos del proyecto
        cargarDatos();

        // Configurar el botón de guardar
        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatos() {
        db.collection("Proyectos").document(proyectoId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Proyecto proyecto = task.getResult().toObject(Proyecto.class);
                        if (proyecto != null) {
                            etNombre.setText(proyecto.getNombre());
                            etDescripcion.setText(proyecto.getDescripcion());

                            // Convertir la fecha de límite a formato legible
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(proyecto.getFechaLimite());
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            etFechaLimite.setText(sdf.format(calendar.getTime()));
                        }
                    } else {
                        Toast.makeText(EditarProyecto.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarCambios() {
        String nombre = etNombre.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        long fechaLimite = myCalendar.getTimeInMillis();

        // Actualizar los datos del proyecto en Firestore
        db.collection("Proyectos").document(proyectoId)
                .update("nombre", nombre, "descripcion", descripcion, "fechaLimite", fechaLimite)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarProyecto.this, "Proyecto actualizado", Toast.LENGTH_SHORT).show();

                    // Crear un Intent con los datos actualizados
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("proyectoId", proyectoId);
                    resultIntent.putExtra("nombre", nombre);
                    resultIntent.putExtra("descripcion", descripcion);
                    resultIntent.putExtra("fechaLimite", fechaLimite);
                    setResult(RESULT_OK, resultIntent);  // Enviar el resultado
                    finish();  // Finalizar la actividad de edición
                })
                .addOnFailureListener(e -> Toast.makeText(EditarProyecto.this, "Error al actualizar proyecto", Toast.LENGTH_SHORT).show());
    }


    private final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // Formato de la fecha
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etFechaLimite.setText(sdf.format(myCalendar.getTime()));
    }
    public void volverp(View v){
        Intent i = new Intent(this, ProyectoDetalle.class);
        startActivity(i);
    }
}




