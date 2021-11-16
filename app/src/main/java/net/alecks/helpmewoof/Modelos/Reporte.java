package net.alecks.helpmewoof.Modelos;

import com.google.firebase.database.ServerValue;
import java.util.ArrayList;

public class Reporte {
    private String idReporte;
    private String idUsuario;
    private String nivelUsuario;
    private Object timestamp;
    private String estado;
    private ArrayList clasificación;
    private String descripción;
    private String imagen;

    public Reporte (){

    }

    public Reporte (String idReporte, String idUser, String nivelUsuario, String estado, ArrayList clasificación, String descripción, String imagen){
        this.idReporte = idReporte;
        this.idUsuario = idUser;
        this.nivelUsuario = nivelUsuario;
        this.estado = estado;
        this.clasificación = clasificación;
        this.descripción = descripción;
        this.imagen = imagen;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getIdReporte() {
        return idReporte;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNivelUsuario() {
        return nivelUsuario;
    }

    public void setNivelUsuario(String nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public void setIdReporte(String idReporte) {
        this.idReporte = idReporte;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ArrayList getClasificación() {
        return clasificación;
    }

    public void setClasificación(ArrayList clasificación) {
        this.clasificación = clasificación;
    }

    public String getDescripción() {
        return descripción;
    }

    public void setDescripción(String descripción) {
        this.descripción = descripción;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

}
