package net.alecks.helpmewoof.Modelos;

import com.google.firebase.database.ServerValue;

public class Comentario {

    private String comentario;
    private String idUsuario;
    private String nombreUsuario;
    private Object timestamp;


    public Comentario() {
    }

    public Comentario(String comentario, String idUsuario, String nombreUsuario) {
        this.comentario = comentario;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public Comentario(String comentario, String idUsuario, String nombreUsuario, Object timestamp) {
        this.comentario = comentario;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.timestamp = timestamp;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
