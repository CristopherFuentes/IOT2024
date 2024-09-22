package com.example.iot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String proyectoNombre = intent.getStringExtra("nombreProyecto");
        String proyectoId = intent.getStringExtra("proyectoId");

        // Intent para abrir la aplicación cuando se toque la notificación
        Intent activityIntent = new Intent(context, ProyectoDetalle.class);
        activityIntent.putExtra("proyectoId", proyectoId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "proyectos_channel")
                .setSmallIcon(R.drawable.ic_notification)  // Asegúrate de tener un ícono para la notificación
                .setContentTitle("Proyecto por vencer")
                .setContentText("El proyecto " + proyectoNombre + " está por vencer mañana.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
