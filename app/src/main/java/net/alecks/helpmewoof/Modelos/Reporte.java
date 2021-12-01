package net.alecks.helpmewoof.Modelos;

import com.google.firebase.database.ServerValue;
import java.util.ArrayList;
import java.util.Map;

public class Reporte {
    private String idReporte;
    private String idUsuario;
    private String nivelUsuario;
    private String estado;
    private String eliminar;
    private ArrayList clasificación;
    private String descripción;
    private String imagen;
    private Map coordenadas;
    private Object timestamp;

    public Reporte (){

    }
    //Con imagen
    public Reporte (String idReporte, String idUsuario, String nivelUsuario, String estado, String eliminar, ArrayList clasificación, String descripción, String imagen, Map coordenadas){
        this.idReporte = idReporte;
        this.idUsuario = idUsuario;
        this.nivelUsuario = nivelUsuario;
        this.estado = estado;
        this.eliminar = eliminar;
        this.clasificación = clasificación;
        this.descripción = descripción;
        this.imagen = imagen;
        this.coordenadas = coordenadas;
        this.timestamp = ServerValue.TIMESTAMP;
    }
    //Sin imagen
    public Reporte (String idReporte, String idUsuario, String nivelUsuario, String estado, String eliminar, ArrayList clasificación, String descripción, Map coordenadas){
        this.idReporte = idReporte;
        this.idUsuario = idUsuario;
        this.nivelUsuario = nivelUsuario;
        this.estado = estado;
        this.eliminar = eliminar;
        this.clasificación = clasificación;
        this.descripción = descripción;
        this.coordenadas = coordenadas;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(String idReporte) {
        this.idReporte = idReporte;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEliminar(){return eliminar;}

    public void setEliminar(){this.eliminar = eliminar;}

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

    public Map getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(Map coordenadas) {
        this.coordenadas = coordenadas;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
