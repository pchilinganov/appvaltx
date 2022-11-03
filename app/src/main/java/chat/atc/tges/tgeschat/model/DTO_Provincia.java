package chat.atc.tges.tgeschat.model;

import java.util.List;

public class DTO_Provincia {

    private String id;
    private String descripcion;
    private Boolean seleccionado;
    List<DTO_Distrito> listaDistritos;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<DTO_Distrito> getListaDistritos() {
        return listaDistritos;
    }

    public void setListaDistritos(List<DTO_Distrito> listaDistritos) {
        this.listaDistritos = listaDistritos;
    }

    public Boolean getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
