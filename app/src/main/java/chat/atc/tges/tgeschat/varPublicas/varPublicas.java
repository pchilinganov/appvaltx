package chat.atc.tges.tgeschat.varPublicas;

import java.util.ArrayList;
import java.util.List;

import chat.atc.tges.tgeschat.model.Message;

public class varPublicas {

    public static String idUsuario_lh="";
    public static String usuario="";
    public static String idVendedorChat="";
    public static String idCanal="";
    public static String nomVendedor="";
    public static String apeVendedor="";
    public static String tokenMovil="";
    public static int dniVendedor=0;
    public static String idSession="";

    //Push a chat desde web

    public static String agenteMesaAyuda="";
    public static String nroVersion = "9"; //Nro Versión de apk prod=9, des=3

    //Reestablecimiento contraseñas
    public static int dialogContrasena; // 0: Reestablecimiento | 1: Contraseña Caduca

    //Conexiones
    // public static String URL_DESARROLLO="https://prueba.tgestionatayuda.com.pe/chat/lhc_web/webservice/Api_ws_"+ varPublicas.nroVersion  +".php?rquest=";
    //public static String URL_DESARROLLO="http://192.168.10.183/ws_siac/";
    //public static String URL_DESARROLLO="http://181.65.211.138:4046/ws_siac/"; //IP PUBLICA
    //public static String URL_DESARROLLO="http://192.168.10.210/chat/lhc_web/index.php?rquest=";
    //public static String URL_DESARROLLO = "http://181.65.211.138:8097/chat/lhc_web/webservice/Api_ws_" + nroVersion+ ".php?rquest=";
    //http://181.65.211.138:8089/chat/appmtayuda/webservice/Api_ws_
    public static String URL_DESARROLLO="https://movistartayuda.com/chat/appmtayuda/webservice/Api_ws_"+ varPublicas.nroVersion  +".php?rquest=";// esta es la verdadera ruta
    //public static String URL_DESARROLLO= "http://192.168.10.121/chat/appmtayuda/webservice/Api_ws_1.php?rquest=";

    //public static String URL_DESARROLLO = "https://movistartayuda.com/chat/appmtayuda/webservice/Api_ws_9.php?rquest=";
    //public static String URL_DESARROLLO= "http://192.168.10.186/chat/appmtayuda/webservice/Api_ws_9.php?rquest=";
    //public static String URL_DESARROLLO= "https://www.movistartayuda.com/chat/appmtayuda/webservice/Api_ws_"+ varPublicas.nroVersion  +".php?rquest=";//esto es produccion

    public static int estadoHistorialTicket=0;

    public static String URL_DESARROLLO2="http://192.168.10.183/ws_siac/";

    public static int chat_id=0;

    public static List<Message> listaMensajesbandeja=new ArrayList<>();
    //public static List<Message> listaMensajesbandeja=new ArrayList<>();

    public static String gResidencialNegocios="0";
}