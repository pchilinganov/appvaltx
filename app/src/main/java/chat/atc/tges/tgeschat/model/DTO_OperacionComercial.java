package chat.atc.tges.tgeschat.model;

import java.util.List;

public class DTO_OperacionComercial
{
    private String id;
    private String descripcion;
    List<DTO_TipoProducto> listaTipoProductos;

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

    public List<DTO_TipoProducto> getListaTipoProductos() {
        return listaTipoProductos;
    }

    public void setListaTipoProductos(List<DTO_TipoProducto> listaTipoProductos) {
        this.listaTipoProductos = listaTipoProductos;
    }
}
