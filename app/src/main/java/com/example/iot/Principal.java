package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class Principal extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button btnCrearProyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Inicializar TabLayout y ViewPager
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPagerTareas);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // Botón para crear un nuevo proyecto
        btnCrearProyecto = findViewById(R.id.btnCrearProyecto);
        btnCrearProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, CrearProyecto.class);
                startActivityForResult(intent, 1);  // Código de solicitud 1 para crear
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ProyectoFragment.newInstance("en_ejecucion"), "En Ejecución");
        adapter.addFragment(ProyectoFragment.newInstance("por_vencer"), "Por Vencer");
        adapter.addFragment(ProyectoFragment.newInstance("terminados"), "Terminados");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("PrincipalActivity", "onActivityResult called with requestCode: " + requestCode + " and resultCode: " + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {  // Resultado de CrearProyecto
            String proyectoId = data.getStringExtra("proyectoId");
            String nombre = data.getStringExtra("nombre");
            String descripcion = data.getStringExtra("descripcion");
            long fechaLimite = data.getLongExtra("fechaLimite", 0);

            // Aquí necesitas notificar a los fragmentos que un nuevo proyecto ha sido creado
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof ProyectoFragment) {
                    // Supongamos que puedes determinar el tipo basado en el título
                    String tipo = ""; // Determina el tipo del fragmento
                    if (fragment.getArguments() != null) {
                        tipo = fragment.getArguments().getString("tipo");
                    }
                    ((ProyectoFragment) fragment).obtenerProyectos(tipo); // Actualiza el fragmento
                }
            }
        }
    }
}

