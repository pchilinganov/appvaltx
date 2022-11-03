package chat.atc.tges.tgeschat.model;

import java.util.List;

public class DTO_TipoProducto {

    private String id;
    private String descripcion;
    List<DTO_Producto> listaProductos;

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

    public List<DTO_Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<DTO_Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }
}
