package com.example.iot;

public class Tarea {
    private String id;
    private String nombre;
    private String descripcion;
    private Boolean estado; // Estado: true = activa, false = completada

    // Constructor vacío
    public Tarea() {

        this.estado = true; // Estado predeterminado
    }

    // Constructor con parámetros
    public Tarea(String id, String nombre, String descripcion, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
