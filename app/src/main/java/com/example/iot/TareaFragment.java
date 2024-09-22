package com.example.iot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TareaFragment extends Fragment {
    private RecyclerView rvTareas;
    private TareaAdapter tareaAdapter;
    private List<Tarea> tareaList;
    private FirebaseFirestore db;
    private String proyectoId;
    private boolean estadoTarea;

    public static TareaFragment newInstance(String proyectoId, boolean estadoTarea) {
        TareaFragment fragment = new TareaFragment();
        Bundle args = new Bundle();
        args.putString("proyectoId", proyectoId);
        args.putBoolean("estadoTarea", estadoTarea);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            proyectoId = getArguments().getString("proyectoId");
            estadoTarea = getArguments().getBoolean("estadoTarea");
        }
        db = FirebaseFirestore.getInstance();
        tareaList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tarea, container, false);

        // Inicializar RecyclerView
        rvTareas = view.findViewById(R.id.rv_tareas);
        rvTareas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar el adaptador antes de cargar datos
        tareaAdapter = new TareaAdapter(tareaList, this, proyectoId);
        rvTareas.setAdapter(tareaAdapter);

        // Cargar tareas al crear la vista
        obtenerTareas();

        return view;
    }

    // Método público para recargar tareas
    @SuppressLint("NotifyDataSetChanged")
    public void obtenerTareas() {
        db.collection("Proyectos").document(proyectoId)
                .collection("Tareas")
                .whereEqualTo("estado", estadoTarea) // Filtrar directamente en la consulta
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tareaList.clear(); // Limpiar la lista antes de agregar nuevas tareas
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tarea tarea = document.toObject(Tarea.class);
                            tareaList.add(tarea);
                        }
                        // Notificamos al adaptador que los datos han cambiado
                        tareaAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error al obtener tareas", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        obtenerTareas(); // Recargar tareas cuando se vuelve a la pantalla
    }
}