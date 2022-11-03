package chat.atc.tges.tgeschat.model;

import java.util.List;

public class DTO_Producto {

    private String id;
    private String descripcion;

    public List<DTO_Subproducto> getListaSubProductos() {
        return listaSubProductos;
    }

    public void setListaSubProductos(List<DTO_Subproducto> listaSubProductos) {
        this.listaSubProductos = listaSubProductos;
    }

    List<DTO_Subproducto> listaSubProductos;

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
}
