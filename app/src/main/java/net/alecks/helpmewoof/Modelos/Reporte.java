package net.alecks.helpmewoof.Modelos;

import java.util.ArrayList;

public class Reporte {
    private String idReporte;
    private String estado;
    private ArrayList clasificación;
    private String descripción;
    private String imagen;


    public Reporte (){

    }

    public Reporte (String idReporte, String estado, ArrayList clasificación, String descripción, String imagen){
        this.idReporte = idReporte;
        this.estado = estado;
        this.clasificación = clasificación;
        this.descripción = descripción;
        this.imagen = imagen;
    }

    public String getIdReporte() {
        return idReporte;
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
