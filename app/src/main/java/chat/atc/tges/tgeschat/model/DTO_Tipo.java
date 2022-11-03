package chat.atc.tges.tgeschat.model;

import java.util.List;

public class DTO_Tipo {
    private String descripcion;

    /* renamed from: id */
    private String f85id;
    List<DTO_Motivo> listaMotivo;
    private Boolean selected;
    private Boolean visible;

    public String getId() {
        return this.f85id;
    }

    public void setId(String str) {
        this.f85id = str;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String str) {
        this.descripcion = str;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public void setVisible(Boolean bool) {
        this.visible = bool;
    }

    public Boolean getSelected() {
        return this.selected;
    }

    public void setSelected(Boolean bool) {
        this.selected = bool;
    }

    public List<DTO_Motivo> getListaMotivo() {
        return this.listaMotivo;
    }

    public void setListaMotivo(List<DTO_Motivo> list) {
        this.listaMotivo = list;
    }
}
