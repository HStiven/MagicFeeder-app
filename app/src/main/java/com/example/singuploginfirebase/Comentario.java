package com.example.singuploginfirebase;

public class Comentario {
    private String id;
    private String nombreUsuario;
    private String contenido;
    private String imagenPerfilUrl;
    private long timestamp;
    private String email; // Añadido campo email

    // Constructor sin argumentos requerido por Firestore
    public Comentario() {
        // Constructor vacío necesario para Firestore
    }

    public Comentario(String nombreUsuario, String contenido, String imagenPerfilUrl, long timestamp) {
        this.nombreUsuario = nombreUsuario;
        this.contenido = contenido;
        this.imagenPerfilUrl = imagenPerfilUrl;
        this.timestamp = timestamp;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getImagenPerfilUrl() {
        return imagenPerfilUrl;
    }

    public void setImagenPerfilUrl(String imagenPerfilUrl) {
        this.imagenPerfilUrl = imagenPerfilUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}