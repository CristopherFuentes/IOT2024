package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProyectoDetalle extends AppCompatActivity {
    private TextView tvNombreProyecto, tvDescripcionProyecto;
    private Button btnCrearTarea;
    private FirebaseFirestore db;
    private String proyectoId;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyecto_detalle);

        // Inicialización de vistas
        tvNombreProyecto = findViewById(R.id.tv_nombre_proyecto);
        tvDescripcionProyecto = findViewById(R.id.tv_descripcion_proyecto);
        btnCrearTarea = findViewById(R.id.btn_crear_tarea);
        db = FirebaseFirestore.getInstance();

        proyectoId = getIntent().getStringExtra("proyectoId");

        // Cargar detalles del proyecto antes de configurar el ViewPager
        cargarDetallesProyecto();

        // Configurar el TabLayout y ViewPager
        viewPager = findViewById(R.id.viewPagerTareas);
        tabLayout = findViewById(R.id.tabLayout);

        // Crear tarea
        btnCrearTarea.setOnClickListener(v -> {
            Intent crearTareaIntent = new Intent(ProyectoDetalle.this, CrearTarea.class);
            crearTareaIntent.putExtra("proyectoId", proyectoId);
            startActivityForResult(crearTareaIntent, 1);
        });
    }

    private void cargarDetallesProyecto() {
        db.collection("Proyectos").document(proyectoId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String nombreProyecto = task.getResult().getString("nombre");
                        String descripcionProyecto = task.getResult().getString("descripcion");

                        if (nombreProyecto != null && descripcionProyecto != null) {
                            tvNombreProyecto.setText(nombreProyecto);
                            tvDescripcionProyecto.setText(descripcionProyecto);

                            // Configurar el ViewPager después de cargar los detalles
                            setupViewPager();
                        } else {
                            Toast.makeText(ProyectoDetalle.this, "No se encontraron los detalles del proyecto", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProyectoDetalle.this, "Error al obtener los detalles del proyecto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // true para tareas activas
        adapter.addFragment(TareaFragment.newInstance(proyectoId, true), "Activas");
        // false para tareas completadas
        adapter.addFragment(TareaFragment.newInstance(proyectoId, false), "Completadas");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // Escuchar cambios en el TabLayout

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Recargar el fragmento actual después de crear una nueva tarea
            TareaFragment fragment = (TareaFragment) adapter.getItem(viewPager.getCurrentItem());
            fragment.obtenerTareas();
        }
    }

    public void VOLVEEEEER(View v){
        Intent i = new Intent(this, Principal.class);
        startActivity(i);
    }
}