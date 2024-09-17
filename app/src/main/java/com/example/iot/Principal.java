package com.example.iot;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity {
    private RecyclerView rvProyectos;
    private ProyectoAdapter proyectoAdapter;
    private List<Proyecto> proyectoList;
    private FirebaseFirestore db;
    private Button btnCrearProyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Inicializar RecyclerView y Firestore
        rvProyectos = findViewById(R.id.rv_proyectos);
        rvProyectos.setLayoutManager(new LinearLayoutManager(this));
        proyectoList = new ArrayList<>();
        proyectoAdapter = new ProyectoAdapter(proyectoList, this); // Pasar el contexto aquí
        rvProyectos.setAdapter(proyectoAdapter);

        db = FirebaseFirestore.getInstance();

        // Botón para crear un nuevo proyecto
        btnCrearProyecto = findViewById(R.id.btnCrearProyecto);
        btnCrearProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, CrearProyecto.class);
                startActivityForResult(intent, 1);  // Código de solicitud 1 para crear
            }
        });

        // Obtener los proyectos desde Firebase
        obtenerProyectos();
    }

    private void obtenerProyectos() {
        db.collection("Proyectos")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("PrincipalActivity", "Proyectos fetched: " + queryDocumentSnapshots.size());
                        if (!queryDocumentSnapshots.isEmpty()) {
                            proyectoList.clear(); // Limpiar la lista antes de agregar nuevos datos
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Proyecto proyecto = document.toObject(Proyecto.class);
                                if (proyecto != null) {
                                    proyectoList.add(proyecto);
                                }
                            }
                            proyectoAdapter.notifyDataSetChanged();// Notificar al adaptador de los cambios
                        }
                    }
                });
    }

    // Método para recibir el resultado de las actividades CrearProyecto o EditarProyecto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("PrincipalActivity", "onActivityResult called with requestCode: " + requestCode + " and resultCode: " + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {  // Resultado de CrearProyecto
            String proyectoId = data.getStringExtra("proyectoId");
            String nombre = data.getStringExtra("nombre");
            String descripcion = data.getStringExtra("descripcion");
            long fechaLimite = data.getLongExtra("fechaLimite", 0);

            Proyecto nuevoProyecto = new Proyecto(proyectoId, nombre, descripcion, fechaLimite);
            proyectoList.add(nuevoProyecto);
            proyectoAdapter.notifyItemInserted(proyectoList.size() - 1);
            rvProyectos.scrollToPosition(proyectoList.size() - 1);

        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {  // Resultado de EditarProyecto
            Log.d("PrincipalActivity", "Data received for EditarProyecto");
            // Recargar los proyectos desde Firebase para obtener los datos actualizados
            obtenerProyectos();
        }
    }
    public void proyectoDetalle(View v){
        Intent intent = new Intent(this, ProyectoDetalle.class);
        startActivity(intent);
    }

}
