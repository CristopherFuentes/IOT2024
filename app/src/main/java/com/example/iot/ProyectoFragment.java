package com.example.iot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProyectoFragment extends Fragment {
    private RecyclerView rvProyectos;
    private ProyectoAdapter proyectoAdapter;
    private List<Proyecto> proyectoList;
    private FirebaseFirestore db;

    public static ProyectoFragment newInstance(String tipo) {
        ProyectoFragment fragment = new ProyectoFragment();
        Bundle args = new Bundle();
        args.putString("tipo", tipo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        proyectoList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proyecto, container, false);
        rvProyectos = view.findViewById(R.id.rv_proyectos);
        rvProyectos.setLayoutManager(new LinearLayoutManager(getContext()));
        proyectoAdapter = new ProyectoAdapter(proyectoList, getContext());
        rvProyectos.setAdapter(proyectoAdapter);

        String tipo = getArguments() != null ? getArguments().getString("tipo") : "";
        obtenerProyectos(tipo);

        return view;
    }

    void obtenerProyectos(String tipo) {
        db.collection("Proyectos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        proyectoList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Proyecto proyecto = document.toObject(Proyecto.class);
                            if (proyecto != null && filtrarProyecto(proyecto, tipo)) {
                                proyectoList.add(proyecto);
                            }
                        }
                        proyectoAdapter.notifyDataSetChanged();
                    }
                });
    }

    private boolean filtrarProyecto(Proyecto proyecto, String tipo) {
        long fechaLimite = proyecto.getFechaLimite();
        long fechaActual = System.currentTimeMillis();
        long unDiaAntes = fechaActual + (24 * 60 * 60 * 1000); // 1 día antes de la fecha actual

        switch (tipo) {
            case "en_ejecucion":
                return fechaLimite > fechaActual; // Proyectos en ejecución
            case "por_vencer":
                return fechaLimite <= unDiaAntes && fechaLimite > fechaActual; // Proyectos por vencer (1 día antes)
            case "terminados":
                return fechaLimite <= fechaActual; // Proyectos terminados
            default:
                return false;
        }
    }

}
