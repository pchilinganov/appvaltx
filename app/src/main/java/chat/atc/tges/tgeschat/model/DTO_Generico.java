package chat.atc.tges.tgeschat.model;

public class DTO_Generico {

    public DTO_Generico(){
    }

    public DTO_Generico(String codigo, String descripcion, boolean seleccionado){
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.seleccionado = seleccionado;
    }

    String codigo;
    String descripcion;
    private boolean seleccionado;
    private boolean visible;

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
