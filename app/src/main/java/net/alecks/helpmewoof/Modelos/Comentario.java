package net.alecks.helpmewoof.Modelos;

import com.google.firebase.database.ServerValue;

public class Comentario {

    private String comentario;
    private String nivelU;
    private Object timestamp;

    public Comentario() {
    }

    public Comentario(String comentario, String nivelU) {
        this.comentario = comentario;
        this.nivelU = nivelU;
        this.timestamp = ServerValue.TIMESTAMP;

    }

    public Comentario(String comentario, String nivelU, Object timestamp) {
        this.comentario = comentario;
        this.nivelU = nivelU;
        this.timestamp = timestamp;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNivelU() {
        return nivelU;
    }

    public void setNivelU(String nivelU) {
        this.nivelU = nivelU;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
