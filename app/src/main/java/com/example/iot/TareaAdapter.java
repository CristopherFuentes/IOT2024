package com.example.iot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {
    private final List<Tarea> tareaList;
    private final TareaFragment context; // Mantén el contexto como TareaFragment
    private final String proyectoId;
    private static final int EDITAR_TAREA_REQUEST_CODE = 1001;

    public TareaAdapter(List<Tarea> tareaList, TareaFragment context, String proyectoId) {
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        // Aquí enlazas los datos con las vistas
        Tarea tarea = tareaList.get(position);
        holder.tvNombre.setText(tarea.getNombre());
        holder.tvDescripcion.setText(tarea.getDescripcion());

        // Controlar el estado del Switch
        String estadoTexto = tarea.getEstado() ? "Activa" : "Completada";
        holder.estadoTextView.setText(estadoTexto);
        holder.swEstadoTarea.setChecked(tarea.getEstado());

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context.requireActivity(), EditarTarea.class); // Usa requireActivity()
            intent.putExtra("tareaId", tarea.getId());
            intent.putExtra("proyectoId", proyectoId);
            intent.putExtra("nombreTarea", tarea.getNombre());
            intent.putExtra("descripcionTarea", tarea.getDescripcion());
            intent.putExtra("estadoTarea", tarea.getEstado());
            context.startActivityForResult(intent, EDITAR_TAREA_REQUEST_CODE); // Usa el contexto del fragmento
        });

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(context.requireActivity())
                    .setTitle("Eliminar tarea")
                    .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                    .setPositiveButton("Eliminar", (dialog, which) -> eliminarTarea(tarea.getId(), position))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        holder.swEstadoTarea.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Actualizar el estado de la tarea en Firestore
            db.collection("Proyectos").document(proyectoId)
                    .collection("Tareas").document(tarea.getId())
                    .update("estado", isChecked)
                    .addOnSuccessListener(aVoid -> {
                        // Cambiar el texto según el estado
                        holder.estadoTextView.setText(isChecked ? "Activa" : "Completada");

                        // Verificar que se ha guardado en Firebase
                        Log.d("TareaAdapter", "Estado de la tarea actualizado en Firebase: " + isChecked);

                        // Actualizar el estado de la tarea en la lista sin removerla
                        tarea.setEstado(isChecked);

                        // Refrescar el RecyclerView para que muestre la tarea en el estado correcto
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Si falla, revertir el estado del Switch
                        holder.swEstadoTarea.setChecked(!isChecked);
                        Toast.makeText(context.requireActivity(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                    });
        });


    }

    @Override
    public int getItemCount() {
        return tareaList.size();
    }

    private void eliminarTarea(String tareaId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Proyectos").document(proyectoId)
                .collection("Tareas").document(tareaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context.requireActivity(), "Tarea eliminada", Toast.LENGTH_SHORT).show();
                    tareaList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> Toast.makeText(context.requireActivity(), "Error al eliminar tarea", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, estadoTextView;
        Switch swEstadoTarea;
        ImageButton btnEliminar;
        ImageButton btnEditar;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_tarea);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_tarea);
            estadoTextView = itemView.findViewById(R.id.tv_estado_tarea);
            swEstadoTarea = itemView.findViewById(R.id.sw_estado_tarea);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_tarea);
            btnEditar = itemView.findViewById(R.id.btn_editar_tarea);
        }
    }


}
