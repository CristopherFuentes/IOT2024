package com.example.iot;

public class Proyecto {
    private String id;
    private String nombre;
    private String descripcion;
    private long fechaLimite;

    // Constructor vacío requerido por Firebase Firestore
    public Proyecto() {
    }

    // Constructor con parámetros para inicializar el proyecto
    public Proyecto(String id, String nombre, String descripcion, long fechaLimite) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(long fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
}




