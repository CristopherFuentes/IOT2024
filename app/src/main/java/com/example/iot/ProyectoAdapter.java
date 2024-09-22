package com.example.iot;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProyectoAdapter extends RecyclerView.Adapter<ProyectoAdapter.ProyectoViewHolder> {
    private List<Proyecto> proyectoList;
    private Context context;
    private static final int EDITAR_PROYECTO_REQUEST_CODE = 2;
    private FirebaseFirestore db;

    public ProyectoAdapter(List<Proyecto> proyectoList, Context context) {
        this.proyectoList = proyectoList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ProyectoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proyecto, parent, false);
        return new ProyectoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProyectoViewHolder holder, int position) {
        Proyecto proyecto = proyectoList.get(position);

        holder.nombreTextView.setText(proyecto.getNombre());

        // Mostrar la cantidad de tareas del proyecto
        db.collection("Proyectos").document(proyecto.getId())
                .collection("Tareas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot tasksSnapshot = task.getResult();
                        if (tasksSnapshot != null) {
                            int taskCount = tasksSnapshot.size();
                            holder.cantidadTareasTextView.setText(String.valueOf(taskCount) + " tareas");
                        }
                    }
                });

        // Convertir el timestamp a una fecha legible
        Date fecha = new Date(proyecto.getFechaLimite());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaLimite = sdf.format(fecha);

        holder.fechaLimiteTextView.setText(fechaLimite);

        // Notificar si el proyecto está por vencer
        long timeDiff = proyecto.getFechaLimite() - System.currentTimeMillis();
        long oneDayInMillis = 24 * 60 * 60 * 1000;
        if (timeDiff <= oneDayInMillis && timeDiff > 0) {
            enviarNotificacion(context, proyecto.getNombre(), "El proyecto vence mañana");
        }

        holder.btnEditar.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                Intent intent = new Intent(context, EditarProyecto.class);
                intent.putExtra("proyectoId", proyecto.getId());
                ((AppCompatActivity) context).startActivityForResult(intent, EDITAR_PROYECTO_REQUEST_CODE);
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("¿Estás seguro de que deseas eliminar este proyecto?")
                    .setPositiveButton("SI", (dialog, which) -> eliminarProyecto(proyecto.getId(), holder.getAdapterPosition()))
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProyectoDetalle.class);
            intent.putExtra("proyectoId", proyecto.getId());
            intent.putExtra("nombreProyecto", proyecto.getNombre());
            context.startActivity(intent);
        });
    }

    private void eliminarProyecto(String proyectoId, int position) {
        db.collection("Proyectos").document(proyectoId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    proyectoList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Proyecto eliminado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar el proyecto", Toast.LENGTH_SHORT).show();
                });
    }

    private void enviarNotificacion(Context context, String titulo, String contenido) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "proyectos_vencimiento";
        String channelName = "Notificaciones de Proyectos";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Principal.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public int getItemCount() {
        return proyectoList.size();
    }

    public static class ProyectoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, cantidadTareasTextView, fechaLimiteTextView;
        ImageButton btnEditar, btnEliminar;

        public ProyectoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.tv_nombre);
            cantidadTareasTextView = itemView.findViewById(R.id.tv_cantidad_tareas);
            fechaLimiteTextView = itemView.findViewById(R.id.tv_fecha_limite);
            btnEditar = itemView.findViewById(R.id.btn_editar_tarea);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_tarea);
        }
    }
}
