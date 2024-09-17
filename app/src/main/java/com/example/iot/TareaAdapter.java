package com.example.iot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {
    private List<Tarea> tareaList;
    private Context context;
    private String proyectoId;
    private static final int EDITAR_TAREA_REQUEST_CODE = 1001;  // Puedes cambiar el valor si lo deseas


    public TareaAdapter(List<Tarea> tareaList, Context context, String proyectoId) {
        this.tareaList = tareaList;
        this.context = context;
        this.proyectoId = proyectoId;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareaList.get(position);

        // Setear nombre y descripción de la tarea
        holder.nombreTextView.setText(tarea.getNombre());
        holder.descripcionTextView.setText(tarea.getDescripcion());

        // Botón para editar la tarea
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarTarea.class);
            intent.putExtra("tareaId", tarea.getId()); // Pasar el ID de la tarea
            intent.putExtra("nombreTarea", tarea.getNombre());
            intent.putExtra("descripcionTarea", tarea.getDescripcion());
            intent.putExtra("estadoTarea", tarea.getEstado());
            ((AppCompatActivity) context).startActivityForResult(intent, EDITAR_TAREA_REQUEST_CODE);
        });

        // Botón para eliminar la tarea
        holder.btnEliminar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                    .setPositiveButton("SI", (dialog, which) -> {
                        eliminarTarea(tarea.getId(), holder.getAdapterPosition());
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });
    }

    // Método para eliminar una tarea de Firebase
    private void eliminarTarea(String tareaId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Proyectos").document(proyectoId).collection("Tareas").document(tareaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    tareaList.remove(position);  // Eliminar de la lista local
                    notifyItemRemoved(position);  // Notificar al adaptador
                    Toast.makeText(context, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar la tarea", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public int getItemCount() {
        return tareaList.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, descripcionTextView, estadoTextView;
        ImageButton btnEditar, btnEliminar;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.tv_nombre_tarea);
            descripcionTextView = itemView.findViewById(R.id.tv_descripcion_tarea);
            estadoTextView = itemView.findViewById(R.id.tv_estado_tarea);
            btnEditar = itemView.findViewById(R.id.btn_editar_tarea);  // ImageButton Editar
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_tarea);  // ImageButton Eliminar
        }
    }
}
