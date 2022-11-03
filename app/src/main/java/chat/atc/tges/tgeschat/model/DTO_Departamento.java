package chat.atc.tges.tgeschat.model;

import java.util.List;

public class DTO_Departamento {

    private String id;
    private String descripcion;
    private Boolean visible;
    private Boolean selected;
    List<DTO_Provincia> listaProvincias;

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

    public List<DTO_Provincia> getListaProvincias() {
        return listaProvincias;
    }

    public void setListaProvincias(List<DTO_Provincia> listaProvincias) {
        this.listaProvincias = listaProvincias;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
