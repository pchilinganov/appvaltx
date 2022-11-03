package chat.atc.tges.tgeschat;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import chat.atc.tges.tgeschat.listeners.LogoutListener;
import chat.atc.tges.tgeschat.model.Message;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

/**
 * Created by rodriguez on 04/01/2018.
 * Se controla tiempo de inactividad de usuario
 */

public class TGestionaSession extends Application {

    private LogoutListener listener;
    private Timer timer;
    private static TGestionaSession application;

    //test de valores globales
    private String usuario="",
            contrasenia="",
            idVendedorChat="",

            idCanal="",
            nomVendedor="",
            apeVendedor="",
            tokenMovil="",
            bAuthorizationHash="bW92aXN0YXJ0YXl1ZGFhcHA=",
            dniVendedor="",
            idSession="",
            agenteMesaAyuda="",
            //URL_DESARROLLO="https://prueba.tgestionatayuda.com.pe/chat/lhc_web/webservice/Api_ws_"+ varPublicas.nroVersion  +".php?rquest=";
            //http://181.65.211.138:8089/chat/appmtayuda/webservice/Api_ws_
            URL_DESARROLLO="https://movistartayuda.com/chat/appmtayuda/webservice/Api_ws_"+ varPublicas.nroVersion  +".php?rquest=";
            //URL_DESARROLLO="http://181.65.211.138:8097/chat/lhc_web/webservice/Api_ws_"+ varPublicas.nroVersion  +".php?rquest=";
            //URL_DESARROLLO= "https://www.movistartayuda.com/chat/appmtayuda/webservice/Api_ws_"+ varPublicas.nroVersion  +".php?rquest=";//esto es produccion
            //URL_DESARROLLO= "http://192.168.10.186/chat/appmtayuda/webservice/Api_ws_9.php?rquest=";
            //public static String URL_DESARROLLO= "http://192.168.10.186/chat/appmtayuda/webservice/Api_ws_1.php?rquest=";

    private String tipoIncidencia="";
    private int estadoHistorialTicket=0;
    private int chat_id=0;
    private int dialogContrasena=0; // 0: Reestablecimiento | 1: Contraseña Caduca
    private List<Message> listaMensajesbandeja=new ArrayList<>();

    public void startUserSession(){
        cancelTimer();
        /*timer = new Timer();
        timer.schedule(new TimerTask() { //Programa la ejecución de una función dentro de un determinado tiempo (delay)
            @Override
            public void run() {
                listener.onSessionLogout();
            }
        },3600000); //1 hora de inactividad 3600000*/
    }

    private void cancelTimer()
    {
        if (timer != null)
            timer.cancel();
    }

    public void registerSessionListener(LogoutListener listener)
    {
        this.listener=listener;
    }

    public void onUserInteracted()
    {
        startUserSession();
    }

    public TGestionaSession getInstance() {
        return application;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getIdVendedorChat() {
        return idVendedorChat;
    }

    public void setIdVendedorChat(String idVendedorChat) {
        this.idVendedorChat = idVendedorChat;
    }

    public String getIdCanal() {
        return idCanal;
    }

    public void setIdCanal(String idCanal) {
        this.idCanal = idCanal;
    }

    public String getNomVendedor() {
        return nomVendedor;
    }

    public void setNomVendedor(String nomVendedor) {
        this.nomVendedor = nomVendedor;
    }

    public String getApeVendedor() {
        return apeVendedor;
    }

    public void setApeVendedor(String apeVendedor) {
        this.apeVendedor = apeVendedor;
    }

    public String getTokenMovil() {
        return tokenMovil;
    }

    public void setTokenMovil(String tokenMovil) {
        this.tokenMovil = tokenMovil;
        varPublicas.tokenMovil = tokenMovil;
    }

    public String getDniVendedor() {
        return dniVendedor;
    }

    public void setDniVendedor(String dniVendedor) {
        this.dniVendedor = dniVendedor;
    }

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }

    public String getAgenteMesaAyuda() {
        return agenteMesaAyuda;
    }

    public void setAgenteMesaAyuda(String agenteMesaAyuda) {
        this.agenteMesaAyuda = agenteMesaAyuda;
    }

    public int getDialogContrasena() {
        return dialogContrasena;
    }

    public void setDialogContrasena(int dialogContrasena) {
        this.dialogContrasena = dialogContrasena;
    }

    public String getURL_DESARROLLO() {
        return URL_DESARROLLO;
    }

    public void setURL_DESARROLLO(String URL_DESARROLLO) {
        this.URL_DESARROLLO = URL_DESARROLLO;
    }

    public int getEstadoHistorialTicket() {
        return estadoHistorialTicket;
    }

    public void setEstadoHistorialTicket(int estadoHistorialTicket) {
        this.estadoHistorialTicket = estadoHistorialTicket;
    }

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public List<Message> getListaMensajesbandeja() {
        return listaMensajesbandeja;
    }

    public void setListaMensajesbandeja(List<Message> listaMensajesbandeja) {
        this.listaMensajesbandeja = listaMensajesbandeja;
    }

    public String getbAuthenticationHash() {
        return bAuthorizationHash;
    }

    public void setbAuthenticationHash(String bAuthenticationHash) {
        this.bAuthorizationHash = bAuthenticationHash;
    }

    public String getTipoIncidencia() {
        return tipoIncidencia;
    }

    public void setTipoIncidencia(String tipoIncidencia) {
        this.tipoIncidencia = tipoIncidencia;
    }
}
