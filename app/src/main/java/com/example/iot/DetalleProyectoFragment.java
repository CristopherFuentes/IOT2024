package com.example.iot;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetalleProyectoFragment extends Fragment {
    private static final String ARG_PROYECTO_ID = "proyectoId";
    private String proyectoId;

    public static DetalleProyectoFragment newInstance(String proyectoId) {
        DetalleProyectoFragment fragment = new DetalleProyectoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROYECTO_ID, proyectoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            proyectoId = getArguments().getString(ARG_PROYECTO_ID);
        }
    }

    // Resto de la implementación de DetalleProyectoFragment
}
