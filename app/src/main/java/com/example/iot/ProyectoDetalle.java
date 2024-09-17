package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProyectoDetalle extends AppCompatActivity {

    private TextView tvNombreProyecto, tvDescripcionProyecto;
    private Button btnCrearTarea;
    private RecyclerView rvTareas;
    private TareaAdapter tareaAdapter;
    private List<Tarea> tareaList;
    private FirebaseFirestore db;
    private String proyectoId;
    private static final int EDITAR_TAREA_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyecto_detalle);

        // Obtener referencias de los TextViews, RecyclerView y el botón
        tvNombreProyecto = findViewById(R.id.tv_nombre_proyecto);
        tvDescripcionProyecto = findViewById(R.id.tv_descripcion_proyecto);
        btnCrearTarea = findViewById(R.id.btn_crear_tarea);
        rvTareas = findViewById(R.id.rv_tareas);

        // Inicializar RecyclerView para tareas
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        tareaList = new ArrayList<>();
        tareaAdapter = new TareaAdapter(tareaList, this, proyectoId); // Asegúrate de que el constructor en TareaAdapter sea correcto
        rvTareas.setAdapter(tareaAdapter);

        db = FirebaseFirestore.getInstance();

        // Obtener los datos del proyecto desde el Intent
        Intent intent = getIntent();
        String nombreProyecto = intent.getStringExtra("nombreProyecto");
        String descripcionProyecto = intent.getStringExtra("descripcionProyecto");
        proyectoId = intent.getStringExtra("proyectoId");

        // Verificar si los datos están disponibles y establecerlos en los TextViews
        if (nombreProyecto != null && descripcionProyecto != null) {
            tvNombreProyecto.setText(nombreProyecto);
            tvDescripcionProyecto.setText(descripcionProyecto);
        } else {
            // Manejar el caso en que los datos no estén disponibles
            Toast.makeText(this, "No se pudo obtener los detalles del proyecto", Toast.LENGTH_SHORT).show();
        }

        // Acción al hacer clic en "Crear Tarea"
        btnCrearTarea.setOnClickListener(v -> {
            Intent crearTareaIntent = new Intent(ProyectoDetalle.this, CrearTarea.class);
            crearTareaIntent.putExtra("proyectoId", proyectoId);  // Pasar el ID del proyecto para relacionar la tarea
            startActivityForResult(crearTareaIntent, 1);  // Código de solicitud 1 para crear tarea
        });

        // Obtener y mostrar las tareas del proyecto
        obtenerTareas();
    }

    private void obtenerTareas() {
        db.collection("Proyectos").document(proyectoId).collection("Tareas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tareaList.clear(); // Limpiar la lista antes de agregar nuevos datos
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tarea tarea = document.toObject(Tarea.class);
                            if (tarea != null) {
                                tareaList.add(tarea);
                            }
                        }
                        tareaAdapter.notifyDataSetChanged(); // Notificar al adaptador de los cambios
                    } else {
                        Toast.makeText(ProyectoDetalle.this, "Error al obtener tareas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1 || requestCode == EDITAR_TAREA_REQUEST_CODE) && resultCode == RESULT_OK) {
            obtenerTareas();  // Recargar las tareas después de crear o editar una tarea
        }
    }

}

