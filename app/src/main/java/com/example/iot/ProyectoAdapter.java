package com.example.iot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder> {
    private List<Proyecto> proyectoList;
    private Context context;  // Necesario para iniciar actividades
    private static final int EDITAR_PROYECTO_REQUEST_CODE = 2;  // Código de solicitud para editar proyecto

    public ProyectoAdapter(List<Proyecto> proyectoList, Context context) {
        this.proyectoList = proyectoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout del item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proyecto, parent, false);
        return new ProyectoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        Proyecto proyecto = proyectoList.get(position);

        holder.nombreTextView.setText(proyecto.getNombre());
        holder.descripcionTextView.setText(proyecto.getDescripcion());

        // Convertir el timestamp a una fecha legible
        Date fecha = new Date(proyecto.getFechaLimite());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaLimite = sdf.format(fecha);

        holder.fechaLimiteTextView.setText(fechaLimite);  // Mostrar la fecha formateada

        // Acción del ImageButton Editar
        holder.btnEditar.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                Intent intent = new Intent(context, EditarProyecto.class);
                intent.putExtra("proyectoId", proyecto.getId());  // Pasar el ID del proyecto
                // Iniciar la actividad para obtener el resultado
                ((AppCompatActivity) context).startActivityForResult(intent, EDITAR_PROYECTO_REQUEST_CODE);
            }
        });

        // Acción del ImageButton Eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            // Mostrar el AlertDialog para confirmar eliminación
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("¿Estás seguro de que deseas eliminar este proyecto?")
                    .setPositiveButton("SI", (dialog, which) -> {
                        eliminarProyecto(proyecto.getId(), holder.getAdapterPosition());
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        // Acción al hacer clic en el proyecto para ir a ProyectoDetalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProyectoDetalle.class);
            intent.putExtra("proyectoId", proyecto.getId());  // Pasar el ID del proyecto
            intent.putExtra("nombreProyecto", proyecto.getNombre());  // Pasar el nombre del proyecto
            intent.putExtra("descripcionProyecto", proyecto.getDescripcion());  // Pasar la descripción
            Log.d("PrincipalActivity", "Data received for EditarProyecto");
            context.startActivity(intent);  // Iniciar la actividad ProyectoDetalle
        });
    }

    // Método para eliminar un proyecto de Firebase
    private void eliminarProyecto(String proyectoId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Proyectos").document(proyectoId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    proyectoList.remove(position);  // Eliminar de la lista local
                    notifyItemRemoved(position);  // Notificar al adapter
                    Toast.makeText(context, "Proyecto eliminado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar el proyecto", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return proyectoList.size();
    }

    // ViewHolder para el RecyclerView
    public static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, descripcionTextView, fechaLimiteTextView;
        ImageButton btnEditar, btnEliminar;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.tv_nombre);
            descripcionTextView = itemView.findViewById(R.id.tv_descripcion);
            fechaLimiteTextView = itemView.findViewById(R.id.tv_fecha_limite);
            btnEditar = itemView.findViewById(R.id.btn_editar_tarea);  // ImageButton Editar
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_tarea);  // ImageButton Eliminar
        }
    }
}
