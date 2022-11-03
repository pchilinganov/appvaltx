package chat.atc.tges.tgeschat.model;

public class DTO_ticket_chat {
    private String fecha;
    private int induser;
    private String mensaje;
    private String usuario;

    public String getUsuario() {
        return this.usuario;
    }

    public void setUsuario(String str) {
        this.usuario = str;
    }

    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String str) {
        this.fecha = str;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String str) {
        this.mensaje = str;
    }

    public int getInduser() {
        return this.induser;
    }

    public void setInduser(int i) {
        this.induser = i;
    }
}
