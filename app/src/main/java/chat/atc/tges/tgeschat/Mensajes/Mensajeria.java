package chat.atc.tges.tgeschat.Mensajes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import chat.atc.tges.tgeschat.BandejaActivity222;
import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.Services.OnClearFromRecentService;
import chat.atc.tges.tgeschat.Services.RefreshLogCat;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.VolleyMultiPart.VolleyMultipartRequest;
import chat.atc.tges.tgeschat.VolleyMultiPart.VolleySingleton;
import chat.atc.tges.tgeschat.activity.ActivityMenu;
import chat.atc.tges.tgeschat.activity.HttpsTrustManager;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.dialogs.ComentarioTicket_dialog;
import chat.atc.tges.tgeschat.dialogs.Encuesta_dialog;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.dialogs.Descargas_dialog;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_Generico;
import chat.atc.tges.tgeschat.upload.MultipartUtility;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;
import okhttp3.OkHttpClient;
//import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static chat.atc.tges.tgeschat.LoginTelefonica.existeIncidenciaMasiva;
import static chat.atc.tges.tgeschat.LoginTelefonica.uri;
import static chat.atc.tges.tgeschat.LoginTelefonica.vistaIncidenciaMasiva;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.limpiarShared;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.newIntent;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.*;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

public class Mensajeria extends BaseVolleyActivity implements  OnClickListener{

    //declaración de variables globales
    private String usuario="",contrasenia="";
    int chat_id=0;
    public static int idChatEncuesta=0;

    public static final String MENSAJE = "MENSAJE";
    public static String idChat;
    private BroadcastReceiver bR;
    private ImageButton imgBtnAdjuntar;
    public static RecyclerView  rv;
    TextView tvFileName;
    private ImageButton bTEnviarMensaje;
    private EditText eTEscribirMensaje;
    private EditText eTRECEPTOR;
    private List<MensajeDeTexto> mensajeDeTextos;
    private MensajeriaAdapter adapter;
    public static FragmentManager fragmentManager;
    PowerManager.WakeLock wakeLock;
    private String selectedFilePath;
    ProgressDialog dialog;
    private String MENSAJE_ENVIAR = "";
    private String idTicket = "";
    private String RECEPTOR;
    private VolleyRP volley;
    private RequestQueue mRequest;
    String idChatRecibidoPorPush="", estadoTicket="";
    String mensaje = "";
    String estadoEncuestaBusqXConsulta=""; // indicador de si el ticket solicitado por pestaña "Consulta " tiene encuesta pendiente
    private MenuItem mIAdjuntar;
    private MenuItem mIComentar;
    SharedPreferences sharedpreferences;
    //Servicio de vida de app
    public static Intent service;

    //web socket
    private OkHttpClient client;
    okhttp3.Request request;
    WebSocket ws;

    //Permiso para acceder a Escritura y lectura de ficheros (Android M)
    private final int REQUEST_PERMISSION_PHONE_READ =1;
    private final int REQUEST_PERMISSION_PHONE_WRITE =2;
    private final int REQUEST_PERMISSION_RECORD =3;

    private boolean bEnvioMensajeChat=false; // indica si se está enviando un mensaje de chat

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajeria);
        mensajeDeTextos = new ArrayList<>();
        fragmentManager= getFragmentManager();
        Intent extras = getIntent();
        Bundle bundle = extras.getExtras();
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        //test de variables globales a través de Application
        usuario = ((TGestionaSession)getApplication()).getUsuario();
        contrasenia = ((TGestionaSession)getApplication()).getContrasenia();

        /*//Inicia servicio
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));*/

        //Inicia servicio de control de sesión
        Intent intent = new Intent(this, OnClearFromRecentService.class);
        startService(intent);

        estadoEncuestaBusqXConsulta="";

        ((TGestionaSession)getApplication()).setTokenMovil(FirebaseInstanceId.getInstance().getToken());

        chat_id=((TGestionaSession)getApplication()).getChat_id(); //variable local global

        getSupportActionBar().setTitle("Chat");

        loadPreferences();
        //mostrarDialogoTipoDoc();

        bTEnviarMensaje = (ImageButton) findViewById(R.id.bTenviarMensaje);
        bTEnviarMensaje.setOnClickListener(this);
        eTEscribirMensaje = (EditText) findViewById(R.id.eTEsribirMensaje);
        eTEscribirMensaje.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Actualiza mensajes leídos
                if (chat_id>0){
                    //if(idTicket!= null && Integer.valueOf(idTicket)!=0)   // debe borrar las notificaciones de un ticket determinado
                        //cancelNotification(getApplicationContext(), Integer.valueOf(idTicket));
                    WSMensajesLeidos();
                }
                return false;
            }
        }
        );

        rv = (RecyclerView) findViewById(R.id.rvMensajes);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rv.setLayoutManager(lm);

        adapter = new MensajeriaAdapter(mensajeDeTextos,this);
        rv.setAdapter(adapter);

        bTEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mensaje = validarCadena(eTEscribirMensaje.getText().toString());
                //RECEPTOR = eTRECEPTOR.getText().toString();
                //if(!mensaje.isEmpty() && !RECEPTOR.isEmpty()){
                if (GlobalFunctions.isOnline(getApplicationContext())) {
                    if (!mensaje.isEmpty()) {
                        MENSAJE_ENVIAR = mensaje;

                        bEnvioMensajeChat=true;
                        validaLimitesMaximosChatSistemaVendedor();
                    }else{
                        habilitarControlesChat(true);
                    }
                }
                else {
                    Toast.makeText(Mensajeria.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
                }
            }
        }
        );
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        setScrollbarChat();

        bR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Se valida encuesta
                String estadoEncuesta= intent.getStringExtra("key_encuesta");
                String estadoIM=intent.getStringExtra("key_IM");
                if (estadoEncuesta!=null && estadoEncuesta.equalsIgnoreCase("OK"))
                {
                    int chatIdPushEncuesta=0;
                    chatIdPushEncuesta= Integer.parseInt(intent.getStringExtra("key_idChat"));
                    if (chatIdPushEncuesta==chat_id)
                    {
                        eTEscribirMensaje.setText("Chat Finalizado");
                        eTEscribirMensaje.setEnabled(false);
                        bTEnviarMensaje.setEnabled(false);
                        idChatEncuesta=chat_id;
                        //validar si el chat recibido por push es igual al actual y mostrar si es así, en otro caso, mostrar mensaje de nueva encuest pendiente.
                        mostrarDialogoEncuesta();
                    }
                    else
                    {
                        //Toast.makeText(Mensajeria.this,"Chat: " + chatIdPushEncuesta + " ha lanzado una encuesta de satisfacción", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(estadoIM!=null && estadoIM.equalsIgnoreCase("OK"))
                {
                    mostrarDialogoIncidenciaMasiva();
                }
                else
                {
                    String mensaje = intent.getStringExtra("key_mensaje");
                    String hora = intent.getStringExtra("key_hora"); //
                    String idChatRecibidoPorPush = intent.getStringExtra("key_idChat");
                    String idTipoRemitente = intent.getStringExtra("key_tipoRemitente");
                    String tipoMsgArchivo = intent.getStringExtra("key_tipoMsg");
                    String urlArchivo = intent.getStringExtra("key_urlArchivo");
                    String ticket = intent.getStringExtra("key_idTicket");
                    idTicket=ticket;
                    Log.d("IDCHA recibido x Push:", idChatRecibidoPorPush);
                    Log.d("IDCHA: chat_id:", ""+chat_id);

                    if ((Integer.parseInt(idChatRecibidoPorPush) == chat_id || chat_id == 0) && mensajeDeTextos.size() > 0) //Si el IdChat enviado por push es igual al idchat del chat actual, o si el idChat actual es 0 [nuevo registro] , que aparezca el mensaje de chat en pantalla
                    {
                        Log.d("IDCHAT recibido x Push:", idChatRecibidoPorPush);
                        Log.d("IDCHAT: chat_id:", ""+chat_id);
                        //CreateMensaje(mensaje, hora, 2, agenteMesaAyuda, tipoMsgArchivo, urlArchivo);
                        CreateMensaje(mensaje, hora, Integer.parseInt(idTipoRemitente), agenteMesaAyuda, tipoMsgArchivo, urlArchivo);
                        getSupportActionBar().setSubtitle("Tickets # "+ ticket);//  .setTitle("MESA DE AYUDA #T-" + ticket);
                        //CreateMensaje(mensaje, hora, 2, ((TGestionaSession) getApplication()).getAgenteMesaAyuda(), tipoMsgArchivo, urlArchivo);
                    }
                    /*if (ticket!=null && Integer.parseInt(ticket) >0)
                    {
                        //getSupportActionBar().setTitle("MESA DE AYUDA #T-" + ticket);
                        getSupportActionBar().setSubtitle("Tickets # "+ ticket);//  .setTitle("MESA DE AYUDA #T-" + ticket);
                    }*/
                } // aquí se agrega otro br en caso haya
            }
        };

        //Retorna historial de chat
        if (estadoHistorialTicket==1){
                if(bundle!=null){ //este codigo responde al presionar el push de un mensaje nuevo

                    idTicket = bundle.getString("key_idTicket");
                    idChatRecibidoPorPush = bundle.getString("key_idChat");
                    estadoTicket = bundle.getString("key_Estado");
                    estadoEncuestaBusqXConsulta= bundle.getString("key_EstadoEncuestaConsulta");

                    if (idChatRecibidoPorPush!=null && Integer.parseInt(idChatRecibidoPorPush) != chat_id)
                    {   //si el idChat enviado por push es diferente al idChat del chat actual
                        chat_id = Integer.parseInt(idChatRecibidoPorPush); // se actualiza el idChat
                        //RecibirHistorialTicket();
                    }
                    else
                    {
                        //RecibirHistorialTicket();
                    }

                    //Verifica estado de ticket
                    if (estadoTicket!=null) {
                        if (estadoTicket.equalsIgnoreCase("Cerrado")) {
                            /*eTEscribirMensaje.setEnabled(false);
                            bTEnviarMensaje.setEnabled(false);
                            eTEscribirMensaje.setHint("Ticket Cerrado");*/
                        }
                        else if (estadoTicket.equalsIgnoreCase("Abierto"))
                        {
                            /*eTEscribirMensaje.setEnabled(true);
                            bTEnviarMensaje.setEnabled(true);*/
                        }
                    }

                    if (estadoEncuestaBusqXConsulta!=null && !estadoEncuestaBusqXConsulta.equalsIgnoreCase("0")){
                        mostrarDialogoEncuesta();
                    }
                }

            getSupportActionBar().setTitle("Chat");

                /*
            //web socket //descomentar en uso de socket
            request = new okhttp3.Request.Builder().url("ws://181.65.211.138:8089/socket.io").build();
            EchoWebSocketListener listener = new EchoWebSocketListener();*/

            /*client = new OkHttpClient(); //descomentar cuando se use socket
            //ws = client.newWebSocket(request, listener); //descomentar cuando se use socket*/
            // fin web socket

            //Cuando ya se haya generado un ticket
            if (idTicket!=null && !idTicket.equalsIgnoreCase(""))
                getSupportActionBar().setSubtitle("Ticket " + idTicket);

            setScrollbarChat(); //mantiene el chat en el último mensaje enviado o recibido


        }

        idChat =String.valueOf(chat_id);

        if(chat_id==0){
            //mostrarDialogoTipoDoc();//aqui estaba habilitado esta linea
        }

        if (chat_id>0){
            WSMensajesLeidos();
        }

        //pasa parametros a servicio que mantiene comunicación con servidor durante el tiempo de vida de app
        service = new Intent(this, RefreshLogCat.class);
        startService(service);

        showPhoneStatePermissionWrite();
    }

    Boolean flagVentas=false;
    String idVendedorChat="", nomVendedor="", apeVendedor="", idCanal="", dniVendedor="", idSession="";
    private void loadPreferences() {
        sharedpreferences = getSharedPreferences("mta_loged",
                Context.MODE_PRIVATE);

        usuario= sharedpreferences.getString("usuario", null);
        idVendedorChat = sharedpreferences.getString("idVendedorChat", null);
        nomVendedor = sharedpreferences.getString("nomVendedor", null);
        apeVendedor = sharedpreferences.getString("apeVendedor", null);
        idCanal = sharedpreferences.getString("idCanal", null);
        dniVendedor = sharedpreferences.getString("dniVendedor", null);
        idSession = sharedpreferences.getString("idSession", null);
        flagVentas = sharedpreferences.getBoolean("flagVentas", false);

        ((TGestionaSession)getApplication()).setUsuario(usuario);
        ((TGestionaSession)getApplication()).setIdVendedorChat(idVendedorChat);
        varPublicas.idVendedorChat=idVendedorChat;
        ((TGestionaSession)getApplication()).setNomVendedor(nomVendedor);
        ((TGestionaSession)getApplication()).setApeVendedor(apeVendedor);
        ((TGestionaSession)getApplication()).setDniVendedor(dniVendedor);
        ((TGestionaSession)getApplication()).setIdCanal(idCanal);
        varPublicas.idCanal=idCanal;
        ((TGestionaSession)getApplication()).setIdSession(idSession);
        varPublicas.idSession=idSession;
    }

    //Web Socket
    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {

//            webSocket.send("What's up ?");
//            webSocket.send(ByteString.decodeHex("deadbeef"));
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            if (bytes == null) {
                return;
            }
            output("Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            output("Error : " + t.getMessage()+"response: "+response);
        }
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //output.setText(output.getText().toString() + "\n\n" + txt);
                //Toast.makeText(Mensajeria.this,outputSocket.getText().toString() + "\n\n" + txt, Toast.LENGTH_SHORT).show();
                Log.d("Socket Run: ",txt);
            }
        });
    }


    List<DTO_Generico> listTipoDoc = new ArrayList<>();
    List<DTO_Generico> listServicio = new ArrayList<>();
    List<DTO_Generico> listMotivo = new ArrayList<>();
    List<DTO_Generico> listTransaccion = new ArrayList<>();

    String pCodTipoDoc ="", numeroAsociado="";

    private void mostrarDialogoTipoDoc()
    {
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(false); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_tipo_solicitud); //establecemos el contenido de nuestro dialog
        final AutoCompleteTextView actvTipoDoc, actvTipoServicio, actvTipoTransaccion;
        final EditText txtNumeroDoc;
        final Button btnValidar, btnCancelar;
        String idServicio = "", idTransaccion = "";


        txtNumeroDoc = (EditText) customDialog.findViewById(R.id.txtNumeroDoc);
        actvTipoDoc = (AutoCompleteTextView) customDialog.findViewById(R.id.actvTipoDoc);
        actvTipoDoc.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                txtNumeroDoc.setText("");
                pCodTipoDoc = listTipoDoc.get(pos).getCodigo();
                if (pCodTipoDoc.equalsIgnoreCase("1")){
                    txtNumeroDoc.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
                } else if(pCodTipoDoc.equalsIgnoreCase("2")){
                    txtNumeroDoc.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
                }
            }
        });

        actvTipoServicio = (AutoCompleteTextView) customDialog.findViewById(R.id.actvTipoServ);

        actvTipoTransaccion = (AutoCompleteTextView) customDialog.findViewById(R.id.actvTipoTransaccion);
        actvTipoTransaccion.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                txtNumeroDoc.setText("");
                pCodTipoDoc = listTipoDoc.get(pos).getCodigo();
                if (pCodTipoDoc.equalsIgnoreCase("1")){
                    txtNumeroDoc.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
                } else if(pCodTipoDoc.equalsIgnoreCase("2")){
                    txtNumeroDoc.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
                }
            }
        });


        btnValidar = (Button) customDialog.findViewById(R.id.btnValidar);
        btnCancelar = (Button) customDialog.findViewById(R.id.btnCancelar);
        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                numeroAsociado = txtNumeroDoc.getText().toString();
                //if (pCodTipoDoc.equalsIgnoreCase("2") && numeroAsociado.substring(0,1).equalsIgnoreCase("20"))
                String WSMethod="";
                WSMethod = "valida_tipo_documento";
                String URL = URL_DESARROLLO + WSMethod;

                Map<String, String> params = new HashMap<String, String>();
                params.put("cb_idtipodoc", pCodTipoDoc);
                params.put("txt_document", numeroAsociado);
                params.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
                params.put("token", ((TGestionaSession) getApplication()).getTokenMovil());

                final String json = GlobalFunctions.callVolleyStringRequest(getApplicationContext(),params,URL,volley,mRequest, new ServerCallback() {
                            @Override
                            public void onSuccess(String result) {
                                //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                                try {
                                    JSONArray JOListaDatos = new JSONArray(result);
                                    JSONArray JATIpoOperacion;
                                    String nroMensaje, msg;
                                    nroMensaje = JOListaDatos.getJSONObject(0).getString("NroMensaje");
                                    msg = JOListaDatos.getJSONObject(0).getString("Msg");

                                    if(nroMensaje.equalsIgnoreCase("250")){
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                    } else{
                                        customDialog.dismiss();
                                    }
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                //Toast.makeText(getApplicationContext(), "valor: " + pCodTipoDoc + "  " + numeroAsociado, Toast.LENGTH_LONG).show();
                //customDialog.dismiss();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
                newIntent(Mensajeria.this,ActivityMenu.class);
            }
        });

        String WSMethod="";
        WSMethod = "showTipoDocChat";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        /*List<DTO_Generico> listServicio = new ArrayList<>();
    List<DTO_Generico> listMotivo = new ArrayList<>();
    List<DTO_Generico> listTransaccion = new ArrayList<>();*/

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                        try {
                            JSONArray JOListaDatos = new JSONArray(result);
                            JSONArray JATIpoOperacion, JATipoServicio, JATipoTransaccion, JATipoMotivo;
                            //parse de web service de combos
                            JATIpoOperacion =   JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_idtipodoc");
                            JATipoServicio =    JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idservicio");
                            JATipoTransaccion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idtipo");
                            // JATipoMotivo =      JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_idtipodoc");

                            for (int i = 0;i < JATIpoOperacion.length(); i++){
                                listTipoDoc = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(),JATIpoOperacion, actvTipoDoc,"id", "value");
                            }
                            for (int i = 0;i < JATipoTransaccion.length(); i++){
                                listTransaccion = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(),JATipoTransaccion, actvTipoTransaccion,"id", "value");
                            }
                            for (int i = 0;i < JATipoServicio.length(); i++){
                                listServicio = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(),JATipoServicio, actvTipoServicio,"id", "value");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
        );

        customDialog.show();
    }

    private void habilitarControlesChat(boolean estado){
        eTEscribirMensaje.setEnabled(estado);
        bTEnviarMensaje.setEnabled(estado);
    }

    private void mostrarDialogoEncuesta()
    {
        Encuesta_dialog myDiag = new Encuesta_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "Encuesta_dialog");
    }

    @Override
    public void onBackPressed() {
        newIntent(this,ActivityMenu.class);
        existeIncidenciaMasiva = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mIAdjuntar = menu.findItem(R.id.action_Adjuntar);
        mIComentar = menu.findItem(R.id.action_Comentar);

        mIComentar.setVisible(false); //Opción Comentar

        //}
        if(ComentarioTicket_dialog.nroTicket!=null &&  ComentarioTicket_dialog.nroTicket.length()>0 && mIComentar!=null)
        {
            mIComentar.setVisible(true);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_NuevoChat) {
            //RecibirHistorialTicket();
            bEnvioMensajeChat=false;
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                validaLimitesMaximosChatSistemaVendedor();
            }
            else
            {
                Toast.makeText(Mensajeria.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.action_Notificaciones) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                mostrarDialogoIncidenciaMasiva();
            }else{
                Toast.makeText(Mensajeria.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.action_Descargas) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
            mostrarDialogoDescargas();
            }else{
                Toast.makeText(Mensajeria.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.action_BandejaTickets) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                Intent intent = new Intent(Mensajeria.this, BandejaActivity222.class);
                startActivity(intent);
            }else{
                Toast.makeText(Mensajeria.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.action_Adjuntar) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                //Solicita Permiso para accedesr a archivos

                //Elige archivo de explorador
                if (chat_id==0) {
                    Toast.makeText(Mensajeria.this,"No se puede enviar un archivo adjunto como primer mensaje de chat.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    showPhoneStatePermissionRead();
                }
            }
            else{
                Toast.makeText(Mensajeria.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.action_GoMenu){
            onBackPressed();
        }

        //Comentar
        if (id == R.id.action_Comentar) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                //Solicita Permiso para accedesr a archivos
                if(estadoTicket.equalsIgnoreCase("Abierto") && idTicket!=null && !idTicket.equalsIgnoreCase("0") && ( ((TGestionaSession)getApplication()).getIdCanal().equalsIgnoreCase("2841") || ((TGestionaSession)getApplication()).getIdCanal().equalsIgnoreCase("4571"))) { //
                    ComentarioTicket_dialog.nroTicket = idTicket;
                    ComentarioTicket_dialog();
                }
                return true;
            }
            else
            {
                Toast.makeText(Mensajeria.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }
        }

        if (id == R.id.action_CerrarSesion) {
            GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);

//            sharedpreferences = getSharedPreferences("mta_loged",
//                    Context.MODE_PRIVATE);
            if (sharedpreferences.getBoolean("flagVentas",false)){
                finishAffinity();
                GlobalFunctions.killAppBypackage(getApplicationContext(),Mensajeria.this, "chat.atc.tges.tgeschat");
            } else{
                newIntent(Mensajeria.this, LoginTelefonica.class);
                uri = null;
            }
            limpiarShared(this);
            Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
            vistaIncidenciaMasiva=false;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ComentarioTicket_dialog()
    {
        ComentarioTicket_dialog myDiag = new ComentarioTicket_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "ComentarioTicket_dialog");
    }

    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");
        LoginTelefonica.existeIncidenciaMasiva=false;
    }

    private void mostrarDialogoDescargas()
    {
        Descargas_dialog myDiag = new Descargas_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "Descargas_dialog");
    }

    private String validarCadena(String cadena)
    {
        for(int i=0;i<cadena.length();i++) {
            if (!("" + cadena.charAt(i)).equalsIgnoreCase(" "))
                return cadena.substring(i, cadena.length());
        }
        return "";
    }
    int chat=0;

    private void MandarMensaje(){

        HttpsTrustManager.allowAllSSL();

        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "recibirChat", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getDataMensajeEnviado(response); //obtener datos de servidor (hora, etc)
                //((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(eTEscribirMensaje, 0);
                habilitarControlesChat(true);
                eTEscribirMensaje.setText(""); //limpia la ventana de chat

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("idvendedor", idVendedorChat);
                params.put("idvendedor", ((TGestionaSession)getApplication()).getIdVendedorChat());
                params.put("imei","0");
                params.put("latitud","0");
                params.put("longitud","0");
                //params.put("token", tokenMovil);
                params.put("token", ((TGestionaSession)getApplication()).getTokenMovil());
                params.put("mensaje",MENSAJE_ENVIAR);
                params.put("id_session",((TGestionaSession)getApplication()).getIdSession());
                params.put("servicio", pCodTipoDoc);
                params.put("asociado",numeroAsociado);
                params.put("cb_idtipodoc", pCodTipoDoc);
                params.put("txt_document",numeroAsociado);
                //params.put("id_chat","0");
                if (chat_id>0) //ya hay un chat abierto
                    chat=-1;

                if (chat==0) // chat <=0
                    params.put("id_chat",String.valueOf(0));
                else
                    params.put("id_chat",String.valueOf(chat_id));

                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }
    String  WS_MsgEnviado_Msg="";
    Boolean WS_MsgEnviado_Estado=false;
        Boolean WS_Session_Msg=false;

    String mensajeEnv="", fechaHora="";
    int contMensajesServerVend =0;
    public void getDataMensajeEnviado (String cadenaJSON)
    {
        try
        {
            JSONArray jsonArrayDataUsuario=new JSONArray(cadenaJSON);
            WS_MsgEnviado_Msg= jsonArrayDataUsuario.getJSONObject(0).getString("Msg").toString();
            WS_MsgEnviado_Estado= jsonArrayDataUsuario.getJSONObject(0).getBoolean("Estado");
            WS_Session_Msg= jsonArrayDataUsuario.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

            if (WS_Session_Msg!=null && !WS_Session_Msg)
            {

                GlobalFunctions.validaSesion(WS_Session_Msg, getApplicationContext());
//                Intent intent = new Intent(this, LoginTelefonica.class);
//                startActivity(intent);
//                Toast.makeText(this, WS_MsgEnviado_Msg, Toast.LENGTH_SHORT).show();
//
//                limpiarShared(this);

                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);

//            sharedpreferences = getSharedPreferences("mta_loged",
//                    Context.MODE_PRIVATE);
                if (sharedpreferences.getBoolean("flagVentas",false)){
                    finishAffinity();
                    GlobalFunctions.killAppBypackage(getApplicationContext(),Mensajeria.this, "chat.atc.tges.tgeschat");
                } else{
                    newIntent(Mensajeria.this, LoginTelefonica.class);
                    uri = null;
                }
                limpiarShared(this);
                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                vistaIncidenciaMasiva=false;

                return;
            }

            //Si el chat es nuevo
            if (chat_id==0) { //descomentar cuando se use socket
                chat_id = jsonArrayDataUsuario.getJSONObject(0).getInt("id_chat");
                idTicket = jsonArrayDataUsuario.getJSONObject(0).getString("idticket");

                //Activar websocket para la lista de nuevos tickets en tiempo real
                /*JSONObject json = new JSONObject();
                try {
                    json.put("tipo","push_new_chat");
                    json.put("chat_id",chat_id);
                    json.put("id_seller",((TGestionaSession)getApplication()).getDniVendedor());
                    ws.send(json.toString());
                }catch (JSONException e){
                    Toast.makeText(Mensajeria.this, e.toString(), Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Toast.makeText(Mensajeria.this, ex.toString(), Toast.LENGTH_SHORT).show();
                }*/

                //client.connectionPool().evictAll(); //descomentar para uso de socket
            }

            mensajeEnv= jsonArrayDataUsuario.getJSONObject(0).getString("mensaje").toString();
            fechaHora= jsonArrayDataUsuario.getJSONObject(0).getString("fecha_hora").toString();

            //se muestra mensaje cuando responde el ws
            if (mensajeDeTextos.size()>=2 && contMensajesServerVend > 0)
            {
                //CreateMensaje(mensajeEnv, fechaHora, 1, nomVendedor, "text", "");
                CreateMensaje(mensajeEnv, fechaHora, 1, ((TGestionaSession)getApplication()).getNomVendedor(), "text", "");
                contMensajesServerVend++;
            }
            else if (mensajeDeTextos.size()==2 && contMensajesServerVend > 0)
            {
                //CreateMensaje(mensajeEnv, fechaHora, 1, nomVendedor, "text", "");
                CreateMensaje(mensajeEnv, fechaHora, 1, ((TGestionaSession)getApplication()).getNomVendedor(), "text", "");
                contMensajesServerVend++;
            }
            else
            {
                contMensajesServerVend++;
            }

            //Activar websocket para cada envío de chat en tiempo real
            /*JSONObject json = new JSONObject();
            try {
                json.put("tipo","push_new_chat");
                json.put("chat_id",chat_id); //Por ver
                json.put("id_seller",((TGestionaSession)getApplication()).getDniVendedor());
                ws.send(json.toString());
            }catch (JSONException e){
                Toast.makeText(Mensajeria.this, e.toString(), Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(Mensajeria.this, ex.toString(), Toast.LENGTH_SHORT).show();
            }*/
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    private void logout(){

        HttpsTrustManager.allowAllSSL();

        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String estado="";
                estado="OK";
                GlobalFunctions.cancelAllNotification(getApplicationContext());

                //Detiene servicio
                //stopService(service);
                //Toast.makeText(BandejaActivity.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("idusuario", usuario);
                params.put("idusuario", usuario);
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    private void RecibirHistorialTicket(){

        final ProgressDialog progress= new ProgressDialog(this);
        mensajeDeTextos.clear();
        rv.removeAllViews();
        HttpsTrustManager.allowAllSSL();
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarChatTicket", new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                eTEscribirMensaje.setText(""); //limpia la ventana de chat
                getDataHistorial(response);
                setScrollbarChat(); //mantiene el chat en el último mensaje enviado o recibido
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta mensajeria: " + error.toString() , Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ticket",idTicket);
                params.put("token", ((TGestionaSession)getApplication()).getTokenMovil());
                params.put("id_chat",""+ chat_id);
                params.put("idvendedor", ((TGestionaSession)getApplication()).getIdVendedorChat());
                params.put("id_session", ((TGestionaSession)getApplication()).getIdSession());
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
        progress.setMessage("Cargando...");
        progress.show();
        progress.setCancelable(false);
    }

    String  WS_MsgHistorial_Msg="";
    Boolean WS_MsgHistorial_Estado=false;
    JSONArray jsonArrayListaDatos= new JSONArray();

    public void getDataHistorial (String cadenaJSON){
        try{
            String estadoEncuesta="", nroTickets="", estado_chat="",nroVendedorTicker="";
            JSONArray jsonArrayDataHistorial=new JSONArray(cadenaJSON);
            WS_MsgHistorial_Msg= jsonArrayDataHistorial.getJSONObject(0).getString("Msg").toString();
            WS_MsgHistorial_Estado= jsonArrayDataHistorial.getJSONObject(0).getBoolean("Estado");
            WS_Session_Msg= jsonArrayDataHistorial.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

            if (WS_Session_Msg!=null && !WS_Session_Msg)
            {
                GlobalFunctions.validaSesion(WS_Session_Msg, getApplicationContext());
//                Intent intent = new Intent(this, LoginTelefonica.class);
//                startActivity(intent);
//                Toast.makeText(this, WS_MsgEnviado_Msg, Toast.LENGTH_SHORT).show();
//
//                limpiarShared(this);

                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);

//            sharedpreferences = getSharedPreferences("mta_loged",
//                    Context.MODE_PRIVATE);
                if (sharedpreferences.getBoolean("flagVentas",false)){
                    finishAffinity();
                    GlobalFunctions.killAppBypackage( getApplicationContext(),Mensajeria.this,"chat.atc.tges.tgeschat");
                } else{
                    newIntent(Mensajeria.this, LoginTelefonica.class);
                    uri = null;
                }
                limpiarShared(this);
                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                vistaIncidenciaMasiva=false;
                return;
            }

            if (  estadoEncuestaBusqXConsulta!= null && estadoEncuestaBusqXConsulta.length()>0){
                estadoEncuesta = estadoEncuestaBusqXConsulta;
            }else{
                estadoEncuesta = jsonArrayDataHistorial.getJSONObject(0).getString("enviar_encuesta");
                estadoEncuestaBusqXConsulta="";
            }

            nroTickets= jsonArrayDataHistorial.getJSONObject(0).getString("tickets").toString(); //nro de tickets asociados al chat
            estado_chat=jsonArrayDataHistorial.getJSONObject(0).getString("estado_chat").toString(); //estado de chat : finalizado o no
            nroVendedorTicker=jsonArrayDataHistorial.getJSONObject(0).getString("id_vendedor").toString(); //estado de chat : finalizado o no

            jsonArrayListaDatos = jsonArrayDataHistorial.getJSONObject(0).getJSONArray("ListaDatos");
            if(mensajeDeTextos.size() ==0) { //Valida si array tiene elementos anteriores, con tal de no repetir la información
                String mensajeHistorial="", fechaHora="", nombre="",tipo="", tipoMsg="", urlArchivo="";
                int idPropietario=0, tipoMensaje=0;
                for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                    mensajeHistorial= jsonArrayListaDatos.getJSONObject(i).getString("mensaje").toString();
                    fechaHora= jsonArrayListaDatos.getJSONObject(i).getString("hora").toString();
                    nombre= jsonArrayListaDatos.getJSONObject(i).getString("nombres").toString();
                    tipo=jsonArrayListaDatos.getJSONObject(i).getString("tipo").toString();
                    tipoMsg=jsonArrayListaDatos.getJSONObject(i).getString("tipo_msg").toString();
                    urlArchivo=jsonArrayListaDatos.getJSONObject(i).getString("url").toString();

                    //idPropietario= jsonArrayListaDatos.getJSONObject(i).getInt("id_propietario");
                    //chat_id= jsonArrayListaDatos.getJSONObject(i).getInt("id_chat");

                    if (tipo.equalsIgnoreCase("vendedor"))
                        tipoMensaje=1;
                    else if (tipo.equalsIgnoreCase("agente"))
                        tipoMensaje=2;

                    //Toast.makeText(Mensajeria.this,"mensajeHistorial" + mensajeHistorial + "fecha Hora" + fechaHora+ "tipo " + tipo + "tipomensaje " + tipoMensaje, Toast.LENGTH_LONG).show();
                    CreateMensaje(mensajeHistorial, fechaHora, tipoMensaje,nombre, tipoMsg, urlArchivo);
                    eTEscribirMensaje.setEnabled(false);
                    bTEnviarMensaje.setEnabled(false);
                }
                eTEscribirMensaje.setEnabled(true);
                bTEnviarMensaje.setEnabled(true);
            }
            //mostra encuesta en caso esté pendiente
            if (estadoEncuesta.equalsIgnoreCase("5"))
            {
                mostrarDialogoEncuesta();
            }
            //Actualiza barra de título mostrando los tickets pertenecientes al chat actual
            if (nroTickets!=null){
                getSupportActionBar().setTitle("Chat");
                getSupportActionBar().setSubtitle("Tickets # " + nroTickets);
            }
            //valida que chats finalizados estén inhabilitados para la interacción
            if (estado_chat.equalsIgnoreCase("finalizado"))
            {
                //inhabilitar chat
                eTEscribirMensaje.setEnabled(false);
                bTEnviarMensaje.setEnabled(false);
                eTEscribirMensaje.setHint("Chat Finalizado");
                eTEscribirMensaje.setVisibility(View.GONE);
                bTEnviarMensaje.setVisibility(View.GONE);
                //mIAdjuntar.setVisible(false);
                return;
            }
            else
            {
                eTEscribirMensaje.setEnabled(true);
                bTEnviarMensaje.setEnabled(true);
                eTEscribirMensaje.setHint("");
                eTEscribirMensaje.setFocusable(true);
                eTEscribirMensaje.setVisibility(View.VISIBLE);
                bTEnviarMensaje.setVisibility(View.VISIBLE);
                //mIAdjuntar.setVisible(true);
            }
            //valida que chat de un vendedor no pueda ser mofificado por otro
            if (!nroVendedorTicker.equalsIgnoreCase(((TGestionaSession)getApplication()).getIdVendedorChat()))
            {
                //inhabilitar chat
                eTEscribirMensaje.setEnabled(false);
                bTEnviarMensaje.setEnabled(false);
                eTEscribirMensaje.setHint("Chat ajeno");
                eTEscribirMensaje.setVisibility(View.GONE);
                bTEnviarMensaje.setVisibility(View.GONE);
                //mIAdjuntar.setVisible(false);
                return;
            }
            else
            {
                eTEscribirMensaje.setEnabled(true);
                bTEnviarMensaje.setEnabled(true);
                eTEscribirMensaje.setVisibility(View.VISIBLE);
                bTEnviarMensaje.setVisibility(View.VISIBLE);
                //eTEscribirMensaje.setHint("Chat ajeno");
//                mIAdjuntar.setVisible(true);

            }
            contMensajesServerVend=mensajeDeTextos.size();
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }

    private void WSMensajesLeidos(){
        HttpsTrustManager.allowAllSSL();
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "mensajeLeido", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resultado="";
                resultado="OK";
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Error de mensajes Leidos: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idvendedor", ((TGestionaSession)getApplication()).getIdVendedorChat());
                params.put("token", ((TGestionaSession)getApplication()).getTokenMovil());
                params.put("idchat",""+chat_id);
                 params.put("id_session", ((TGestionaSession)getApplication()).getIdSession());

                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    private static String validaChatMsg="";
    private static Boolean validaChatLimite=false;
    private static Boolean WS_Status_Session=false;

    private void makeRequestValidaLimiteChatsSistema(){
        final ProgressDialog progressLogin= new ProgressDialog(this);
        String url = varPublicas.URL_DESARROLLO+"limiteChatSistema";
        HttpsTrustManager.allowAllSSL();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getDataValida(response);
                makeRequestValidaLimiteChatsVendedor(); //valida si existe límite de chats de Vendedor

                onConnectionFinished();
                //progressLogin.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                onConnectionFailed(error.toString());
                //progressLogin.dismiss();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("authorization", ((TGestionaSession) getApplication()).getbAuthenticationHash());
                params.put("idvendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
                params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
                if (varPublicas.tokenMovil==null)
                {
                    varPublicas.tokenMovil ="";
                }
                params.put("token", ((TGestionaSession) getApplication()).getTokenMovil());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("authorization", "Basic bXRhX2FwcDpiVzkyYVhOMFlYSjBZWGwxWkdGaGNIQT0=");
                //params.put("Accept-Language", "fr");

                return params;
            }
        };
        addToQueue(request);
        //progressLogin.setMessage("Validando Aceso...");
        //progressLogin.show();
        //progressLogin.setCancelable(false);
    }

    public void getDataValida(String cadenaJSON)
    {
        try
        {
            JSONArray jsonArrayDataValida=new JSONArray(cadenaJSON);
            validaChatMsg= jsonArrayDataValida.getJSONObject(0).getString("Msg").toString();
            validaChatLimite= Boolean.parseBoolean(jsonArrayDataValida.getJSONObject(0).getString("Estado").toString());
            WS_Status_Session = jsonArrayDataValida.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

            if (!WS_Status_Session){ //si el campo session_status no es nulo
                Boolean estadoSesion= jsonArrayDataValida.getJSONObject(0).getBoolean("session_status");
                if (!estadoSesion){
                    GlobalFunctions.validaSesion(WS_Session_Msg, getApplicationContext());
//                Intent intent = new Intent(this, LoginTelefonica.class);
//                startActivity(intent);
//                Toast.makeText(this, WS_MsgEnviado_Msg, Toast.LENGTH_SHORT).show();
//
//                limpiarShared(this);

                    GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);

//            sharedpreferences = getSharedPreferences("mta_loged",
//                    Context.MODE_PRIVATE);
                    if (sharedpreferences.getBoolean("flagVentas",false)){
                        finishAffinity();
                        GlobalFunctions.killAppBypackage( getApplicationContext(),Mensajeria.this,"chat.atc.tges.tgeschat");
                    } else{
                        newIntent(Mensajeria.this, LoginTelefonica.class);
                        uri = null;
                    }
                    limpiarShared(this);
                    Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                    vistaIncidenciaMasiva=false;
                    return;
                }
            }

            if (validaChatLimite)
            {
                Intent intent = new Intent(this, LoginTelefonica.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), validaChatMsg, Toast.LENGTH_LONG).show();
                return;
            } /*else{
                 //si no se ha excedido el límite de chats
                Intent intent = new Intent(Mensajeria.this, Mensajeria.class);
                startActivity(intent);
                chat_id = 0;
                ComentarioTicket_dialog.nroTicket = "";
            }*/
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }


    private static String validaChatVendMsg="";
    private static Boolean validaChatLimiteVend=false;
    private static Boolean WS_StatusVend_Session=false;

    private void makeRequestValidaLimiteChatsVendedor(){
        final ProgressDialog progressLogin= new ProgressDialog(this);
        String url = varPublicas.URL_DESARROLLO+"limiteChatsVendedor";
        HttpsTrustManager.allowAllSSL();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataValidaChatVend(response);

                onConnectionFinished();
                progressLogin.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mensajeria.this,"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                onConnectionFailed(error.toString());
                progressLogin.dismiss();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("authorization", ((TGestionaSession) getApplication()).getbAuthenticationHash());
                params.put("idvendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
                params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
                if (varPublicas.tokenMovil==null)
                {
                    varPublicas.tokenMovil ="";
                }
                params.put("token", ((TGestionaSession) getApplication()).getIdSession());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("authorization", "Basic bXRhX2FwcDpiVzkyYVhOMFlYSjBZWGwxWkdGaGNIQT0=");
                //params.put("Accept-Language", "fr");

                return params;
            }
        };
        addToQueue(request);
        progressLogin.setMessage("Validando Acceso...");
        progressLogin.show();
        progressLogin.setCancelable(false);
    }

    public void getDataValidaChatVend(String cadenaJSON)
    {
        try
        {
            JSONArray jsonArrayDataValida=new JSONArray(cadenaJSON);
            validaChatVendMsg = jsonArrayDataValida.getJSONObject(0).getString("Msg").toString();
            validaChatLimiteVend = Boolean.parseBoolean(jsonArrayDataValida.getJSONObject(0).getString("Estado").toString());
            WS_StatusVend_Session = jsonArrayDataValida.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

            if (bEnvioMensajeChat){ //Si el caso proviene de un envío de mensaje de chat
                if (validaChatLimiteVend )
                {

                    if (chat_id != 0) {
                        if (mensajeDeTextos.size() >= 2 && contMensajesServerVend > 0) {
                            //CreateMensaje(mensajeEnv, fechaHora, 1, nomVendedor, "text", "");
                            //CreateMensaje(mensajeEnv, fechaHora, 1, ((TGestionaSession) getApplication()).getNomVendedor(), "text", "");
                            contMensajesServerVend++;

                            MandarMensaje(); //en web service , recibir datos de hora de servidor ,etc
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), validaChatVendMsg, Toast.LENGTH_SHORT).show();
                    }
                }
                else if ( !validaChatLimite && !validaChatLimiteVend) // si no hay límite de chat del sistema ni de vendedor
                {
                    if (chat_id == 0) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        Date date = new Date();
                        //CreateMensaje(eTEscribirMensaje.getText().toString() , dateFormat.format(date), 1, nomVendedor, "text", "");
                        CreateMensaje(eTEscribirMensaje.getText().toString(), dateFormat.format(date), 1, ((TGestionaSession) getApplication()).getNomVendedor(), "text", "");
                        eTEscribirMensaje.setText("");
                        MandarMensaje();

                        //Valida si se ha superado el límite máximo de chats permitidos por el sistema y por vendedor
                        //se muestra mensaje cuando responde el ws
                            //CreateMensaje(mensajeEnv, fechaHora, 1, nomVendedor, "text", "");

//                        CreateMensaje(eTEscribirMensaje.getText().toString(), dateFormat.format(date), 1, ((TGestionaSession) getApplication()).getNomVendedor(), "text", "");
//                        eTEscribirMensaje.setText("");
                    } else{
                        /*CreateMensaje(mensajeEnv, fechaHora, 1, ((TGestionaSession)getApplication()).getNomVendedor(), "text", "");
                        contMensajesServerVend++;*/
                        MandarMensaje();
                    }
                }

            } else{ //Si el caso proviene de pulsar el botón Nuevo Chat
                if (validaChatLimiteVend)
                {
                    Toast.makeText(getApplicationContext(), validaChatVendMsg, Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(Mensajeria.this, Mensajeria.class);
                    startActivity(intent);
                    chat_id = 0;
                    ComentarioTicket_dialog.nroTicket = "";
                }
            }

            /*if (validaChatLimiteVend)
            {
                Toast.makeText(getApplicationContext(), validaChatVendMsg, Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (bEnvioMensajeChat) {
                    Intent intent = new Intent(Mensajeria.this, Mensajeria.class);
                    startActivity(intent);
                    chat_id = 0;
                    ComentarioTicket_dialog.nroTicket = "";
                } else {
                     // si no hay límite de chats de sistema o vendedor
                        if (chat_id == 0) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            Date date = new Date();
                            //Valida si se ha superado el límite máximo de chats permitidos por el sistema y por vendedor
                            CreateMensaje(eTEscribirMensaje.getText().toString(), dateFormat.format(date), 1, ((TGestionaSession) getApplication()).getNomVendedor(), "text", "");
                            eTEscribirMensaje.setText("");
                        }
                        MandarMensaje(); //en web service , recibir datos de hora de servidor ,etc
                }
            }*/
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }

    private void validaLimitesMaximosChatSistemaVendedor(){
        makeRequestValidaLimiteChatsSistema(); //valida si existe límite de chats de Sistema y límite de chat de vendedor
    }

    public void CreateMensaje(String mensaje, String hora, int tipoDeMensaje, String nombre, String tipoMsgArchivo, String urlArchivo){
        MensajeDeTexto mensajeDeTextoAuxiliar = new MensajeDeTexto();
        mensajeDeTextoAuxiliar.setId("0");
        mensajeDeTextoAuxiliar.setNombre(nombre);
        mensajeDeTextoAuxiliar.setMensaje(mensaje);
        mensajeDeTextoAuxiliar.setTipoMensaje(tipoDeMensaje);
        mensajeDeTextoAuxiliar.setHoraDelMensaje(hora);
        mensajeDeTextoAuxiliar.setTipoMsgArchivo(tipoMsgArchivo);
        mensajeDeTextoAuxiliar.setUrlArchivo(urlArchivo);
        mensajeDeTextos.add(mensajeDeTextoAuxiliar);
        adapter.notifyDataSetChanged();
        setScrollbarChat(); //mantiene el chat en el último mensaje enviado o recibido
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bR);
        //Actualiza mensajes leídos
        if (chat_id>0){
            WSMensajesLeidos();
        }
        //Toast.makeText(Mensajeria.this,"Evento onPause de Activity Mensajeria" , Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(bR,new IntentFilter(MENSAJE));
        //Actualiza mensajes leídos
        if (chat_id>0){
            WSMensajesLeidos();
        }

        //Reestablece todos los mensajes del chat
        if (chat_id>0){
            RecibirHistorialTicket();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(Mensajeria.this,"Evento onDestroy de Activity Mensajeria" , Toast.LENGTH_SHORT).show();
    }

    public void setScrollbarChat(){
        rv.scrollToPosition(adapter.getItemCount()-1);
    }

    //uploadd

    public static String BASE_URL = URL_DESARROLLO +"adjuntarArchivo";


    static final int PICK_IMAGE_REQUEST = 1;
    String filePath;

    /*private void imageBrowse() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if(requestCode == PICK_IMAGE_REQUEST){
                Uri picUri = data.getData();

                filePath = getPath(picUri);

                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);

                subirAServidor(filePath);
                //imageView.setImageURI(picUri);
            }

            if(requestCode == FILE_SELECT_CODE){
                Uri uri = data.getData();
                //Log.d(TAG, "File Uri: " + uri.toString());
                // Get the path
                String path = getPathDocument(uri);

                subirAServidor(path);
                //Log.d(TAG, "File Path: " + path);
            }
        }
    }*/

    /*private void subirAServidor(String filePath) {
        try {
            final File rutaFotoCamaraGaleria= new File(filePath);
            //filenameGaleria = getFilename();
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this.getApplicationContext(), uploadId, BASE_URL)
                    .addFileToUpload(rutaFotoCamaraGaleria.getPath(), "archivo")
                    .addParameter("idvendedor", idVendedorChat)
                    .addParameter("idchat", "501")
                    .addParameter("token", tokenMovil)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(1)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(UploadInfo uploadInfo) {}

                        @Override
                        public void onError(UploadInfo uploadInfo, Exception e) {
                            Toast.makeText(getApplicationContext(),"Error de subida: " + e.toString(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                            //ELiminar imagen
                            //ELiminar imagen
                            /*File eliminar = new File(rutaFotoCamaraGaleria.getPath());
                            if (eliminar.exists()) {
                                if (eliminar.delete()) {
                                    System.out.println("archivo eliminado:" + rutaFotoCamaraGaleria.getPath());
                                } else {
                                    System.out.println("archivo no eliminado" + rutaFotoCamaraGaleria.getPath());
                                }
                            }
                            Toast.makeText(getApplicationContext(),"Imagen subida exitosamente.",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(UploadInfo uploadInfo) {
                            Toast.makeText(getApplicationContext(),"Subida de imagen cancelada.",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .startUpload();

        } catch (Exception exc) {
            System.out.println(exc.getMessage()+" "+exc.getLocalizedMessage());
        }
    }*/

    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private String getPathDocument(Uri contentUri) {
        String[] proj = { MediaStore.Files.FileColumns.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private static final int PICK_FILE_REQUEST = 1;
    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        //intent.setType("*/*");
        intent.setType("image/*|application/pdf");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }

    private void showFileChooser222() {
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String[] mimetypes = {  "image/jpeg", "image/pjpeg", "image/png", "image/gif", "application/pdf",
                                "application/msword", "application/vnd.ms-excel", "application/vnd.ms-powerpoint",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                "application/vnd.openxmlformats-officedocument.presentationml.presentation"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."),PICK_FILE_REQUEST);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result="";
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static byte[] convertirTobyteArray(InputStream is) throws IOException {

        byte[] bytes = null;

        if(is != null){
            bytes = new byte[is.available()];

            is.read(bytes);
        }

        return bytes;

    }

    Uri selectedFileUri;
    public static String fileNameUpload;
    public static byte[] bytes;
    InputStream inputStream;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                selectedFileUri = data.getData();
                //obtiene nombre de archivo
                if (selectedFileUri.getScheme().equals("file")) {
                    fileNameUpload = selectedFileUri.getLastPathSegment();
                } else {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(
                                selectedFileUri,
                                new String[]{ MediaStore.Images.ImageColumns.DISPLAY_NAME }
                                , null,
                                null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            fileNameUpload = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                            //Log.d(TAG, "name is " + fileName);
                            //Toast.makeText(Mensajeria.this, "name is " + fileNameUpload, Toast.LENGTH_SHORT).show();
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }

                if (selectedFileUri != null)
                {
                    try {
                        inputStream = getContentResolver().openInputStream(selectedFileUri);
                        BufferedReader br = null;
                        if (inputStream != null) {
                            br = new BufferedReader(new InputStreamReader(inputStream));

                            String line;
                            bytes = convertirTobyteArray(inputStream);
                            subirImagen();
                        } else {
                            //subscriber.onError(new InputException("There's something seriously wrong with this file"));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        //subscriber.onError(e);
                    }
                    //subirFichero();
                }
            }
        }
    }

    private void subirImagen()
    {
        String url = URL_DESARROLLO + "adjuntarArchivo";
        dialog = ProgressDialog.show(Mensajeria.this, "", "Subiendo archivo...", true);
        dialog.setCancelable(false);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        dialog.dismiss();
                        String resultResponse = new String(response.data);
                        try {
                            JSONArray jsonArray = new JSONArray(resultResponse);
                            JSONObject result = jsonArray.getJSONObject(0);
                            String status = result.getString("Estado");
                            String message = result.getString("Msg");
                            urlAdjuntar = result.getString("url");
                            horaAdjuntar = result.getString("hora");

                            if (status.equalsIgnoreCase("true")) {
                                // tell everybody you have succed upload image and post strings
                                Toast.makeText(Mensajeria.this,"Mensaje : " + message, Toast.LENGTH_SHORT);
                                CreateMensaje(fileNameUpload, horaAdjuntar, 1, ((TGestionaSession) getApplication()).getNomVendedor(), "file", urlAdjuntar);
                            } else {
                                Toast.makeText(Mensajeria.this,"Mensaje : " + message, Toast.LENGTH_SHORT);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("Estado");
                        String message = response.getString("Msg");

                        Toast.makeText(Mensajeria.this,"Mensaje : " + message, Toast.LENGTH_SHORT);
                        Toast.makeText(Mensajeria.this,"Status : " + status, Toast.LENGTH_SHORT);
                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idchat", "" + chat_id);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("archivo", new DataPart(fileNameUpload, bytes, getMimeType(fileNameUpload)));
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    public String getMimeType(String url) {
        url=url.replace(" ","");
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        }
        return type;
    }

    private void subirFichero(){
        //on upload button Click
        //if (selectedFilePath != null)
        if (selectedFileUri != null)
        {
            dialog = ProgressDialog.show(Mensajeria.this, "", "Subiendo archivo...", true);
            dialog.setCancelable(false);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try
                    {
                        //creating new thread to handle Http Operations
                        //uploadFile(selectedFilePath);

                        //Ejecuta tarea asíncrona
                        MiTareaAsincrona mta = new MiTareaAsincrona();
                        mta.execute();
                        //subirOkClient(selectedFilePath);
                    }
                    catch (OutOfMemoryError e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Mensajeria.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                }
            }).start();
        }
        else
        {
            Toast.makeText(Mensajeria.this, "Please choose a File First", Toast.LENGTH_SHORT).show();
        }
    }
/*
    private void subirOkClient(String selectedFilePath){
        /*OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"archivo\"; filename=\""+selectedFilePath+"\"\r\nContent-Type: text/plain\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"token\"\r\n\r\nfmnBXnzzt04:APA91bFm3we-AkdAvsin3ezRGFiOQQSvsudNWRYCTVcy8OrkPQxh8gkbf6vWvunuZhY9flDtRWcogaBcE1c-SzpkLpI_gz5zuoCGg6XI4VXt59b1hzgchGoR5NOUiQ4LOLeMXfDYFW18\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"idvendedor\"\r\n\r\n9\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"idchat\"\r\n\r\n501\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://192.168.10.210/chat/lhc_web/webservice/Api_ws.php?rquest=adjuntarArchivo")
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "453955c4-33f0-af35-e5cd-e4195f975579")
                .build();
        okhttp3.Response response;
        try {
            response = client.newCall(request).execute();
            int i=1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }
*/
    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(((TGestionaSession)getApplication()).getURL_DESARROLLO() + "adjuntarArchivo");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("archivo",selectedFilePath);
                /*connection.setRequestProperty("idchat","841");
                connection.setRequestProperty("idvendedor",varPublicas.idVendedorChat);
                connection.setRequestProperty("token",varPublicas.tokenMovil);*/

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\""
                        + selectedFilePath + "\"" + lineEnd + "Content-Type: image/png");

                /*dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\""
                        + selectedFile + "\"" + lineEnd + "Content-Type: image/png\r\n\r\n\r\n" +twoHyphens + boundary + lineEnd+ "Content-Disposition: form-data; name=\"token\"\r\n\r\nfmnBXnzzt04:APA91bFm3we-AkdAvsin3ezRGFiOQQSvsudNWRYCTVcy8OrkPQxh8gkbf6vWvunuZhY9flDtRWcogaBcE1c-SzpkLpI_gz5zuoCGg6XI4VXt59b1hzgchGoR5NOUiQ4LOLeMXfDYFW18\r\n"+twoHyphens + boundary + lineEnd+"Content-Disposition: form-data; name=\"idvendedor\"\r\n\r\n9\r\n"+twoHyphens + boundary + lineEnd+"Content-Disposition: form-data; name=\"idchat\"\r\n\r\n841\r\n"+twoHyphens + boundary + lineEnd);*/

                //dataOutputStream.writeBytes(twoHyphens);
/*
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"idchat\""
                        + lineEnd  + "841"  );

                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"token\""
                        + lineEnd  + varPublicas.tokenMovil  );

                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"idvendedor\""
                        + lineEnd   + varPublicas.idVendedorChat  );

                dataOutputStream.writeBytes(lineEnd);

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);*/

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    try {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(Mensajeria.this, "Insufficient Memory!", Toast.LENGTH_SHORT).show();
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                String respuestaServidor = "";
                //DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                InputStreamReader dataInputStreamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferReader = new BufferedReader(dataInputStreamReader);

                StringBuilder response = new StringBuilder();
                try{
                    String line = "";
                    while((line = bufferReader.readLine()) != null) {
                        response.append(line);
                    }

                    serverResponseCode = connection.getResponseCode();
                    respuestaServidor = dataInputStreamReader.toString();

                    /*while ((dataInputStreamReader.readLine()) != null) {
                        /*os.writeBytes(userInput);
                        os.writeByte('\n');
                        respuestaServidor +=  respuestaServidor + dataInputStreamReader.readLine();
                    }*/
                    //respuestaServidor =connection.ge;
                }catch (OutOfMemoryError e){
                    Toast.makeText(Mensajeria.this, "Memory Insufficient!", Toast.LENGTH_SHORT).show();
                }
                String serverResponseMessage = connection.getResponseMessage();

                //Log.i(TAG, "Server Response is: " + respuestaServidor + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Mensajeria.this, "Archivo subido correctamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

                /*if (wakeLock.isHeld()) {

                    wakeLock.release();
                }*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Mensajeria.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Mensajeria.this, "URL Error!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Mensajeria.this, "Cannot Read/Write File", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }

    static String removerTildes(String cadena) {
        return cadena.replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }

    //Asynctask para subir archivos
    String nombreArchivo="", urlAdjuntar="", horaAdjuntar="";
    private class MiTareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            MultipartUtility mpu = new MultipartUtility();
            //Log.e("MSG", "inicio");
            try {
                //File filePath= new File(selectedFilePath);
                //nombreArchivo =filePath.getName();
                //nombreArchivo = removerTildes(nombreArchivo);
                mpu.MultipartUtilityV2(URL_DESARROLLO + "adjuntarArchivo", getApplicationContext());
                mpu.addFilePart("archivo", bytes);
                //mpu.addFilePart("archivo", bytes);
//                mpu.addFilePart("archivo", filePath);
//                mpu.addFilePart("archivo", filePath);
                mpu.addFormField("idvendedor",((TGestionaSession)getApplication()).getIdVendedorChat());
                mpu.addFormField("idvendedor",((TGestionaSession)getApplication()).getIdVendedorChat());
                mpu.addFormField("idchat",""+chat_id);
                mpu.addFormField("idchat",""+chat_id);
                mpu.addFormField("id_session",""+((TGestionaSession)getApplication()).getIdSession());
                mpu.addFormField("id_session",""+((TGestionaSession)getApplication()).getIdSession());

                String response = mpu.finish();

                JSONArray jsonArrayDataAdjunto=new JSONArray(response);
                WS_MsgHistorial_Msg= jsonArrayDataAdjunto.getJSONObject(0).getString("Msg").toString();
                WS_MsgHistorial_Estado= jsonArrayDataAdjunto.getJSONObject(0).getBoolean("Estado");
                if (WS_MsgHistorial_Estado) {
                    urlAdjuntar = jsonArrayDataAdjunto.getJSONObject(0).getString("url");
                    horaAdjuntar = jsonArrayDataAdjunto.getJSONObject(0).getString("hora");
                }

                //Log.e("MSG", response);
            } catch (IOException e) {
                int a=1;
                dialog.dismiss();
                //Log.e("Error", "Exception xd: " + e.getLocalizedMessage());
            } catch (JSONException e){
                Log.e("Error", "JSON Exception: " + e.getMessage());
                dialog.dismiss();
                //Toast.makeText(Mensajeria.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            Toast.makeText(Mensajeria.this,"Mensaje: "+ WS_MsgHistorial_Msg, Toast.LENGTH_LONG).show();
            //String mensaje="Se adjunto el archivo " + nombreArchivo;

            //MENSAJE_ENVIAR = mensaje;
            //CreateMensaje(nombreArchivo,horaAdjuntar,1,varPublicas.nomVendedor,"file",urlAdjuntar);
            //if (WS_MsgHistorial_Estado) {
                CreateMensaje(nombreArchivo, horaAdjuntar, 1, ((TGestionaSession) getApplication()).getNomVendedor(), "file", urlAdjuntar);
            //}
        }

        @Override
        protected void onCancelled() {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v==imgBtnAdjuntar){
            showFileChooser222();
            //openFolder();
            //imageBrowse();
        }
    }
    
    // Permisos solicitados para escribir , leer archivos o imágenes de dispositivo

    private void showPhoneStatePermissionRead() {
        //int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        //int permissionRecord = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);

        //Permiso de Escritura
        /*if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mensajeria.this, WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permiso necesario", "Para continuar con el uso del sistema ", WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_WRITE);
            } else {
                requestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_WRITE);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Permiso Escritura ya otorgado!", Toast.LENGTH_SHORT).show();
        }*/

        //Permiso de Lectura
        if (permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mensajeria.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation("Permiso necesario", "Para continuar con el envío de archivos, otorgar permisos de lectura.", Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_READ);
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_READ);
            }
        } else {
            //Toast.makeText(getApplicationContext(), "Permiso Lectura ya otorgado!", Toast.LENGTH_SHORT).show();
            showFileChooser222();
        }

        //Permiso de Grabación

        /*if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mensajeria.this,Manifest.permission.RECORD_AUDIO)) {
                showExplanation("Permiso necesario", "Para continuar con el uso del sistema ", Manifest.permission.RECORD_AUDIO, REQUEST_PERMISSION_RECORD);
            } else {
                requestPermission(Manifest.permission.RECORD_AUDIO, REQUEST_PERMISSION_RECORD);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permiso grabación ya otorgado!", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void showPhoneStatePermissionWrite() {
        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        //int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        //int permissionRecord = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);

        //Permiso de Escritura
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Mensajeria.this, WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permiso necesario", "Para continuar con el uso del sistema ", WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_WRITE);
            } else {
                requestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_WRITE);
            }
        }
        else
        {
            //Toast.makeText(getApplicationContext(), "Permiso Escritura ya otorgado!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_WRITE:
                /*if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permiso escritura ya otorgado.", Toast.LENGTH_SHORT).show();
                    mIAdjuntar.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Permiso escritura denegado.", Toast.LENGTH_SHORT).show();
                    mIAdjuntar.setEnabled(true);
                }*/
                break;
            case REQUEST_PERMISSION_PHONE_READ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permiso de lectura otorgado.", Toast.LENGTH_SHORT).show();
                    //mIAdjuntar.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Permiso lectura denegado.", Toast.LENGTH_SHORT).show();
                    //mIAdjuntar.setEnabled(false);
                }
                break;
        }
    }

    private void showExplanation(String title,String message,final String permission,final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(Mensajeria.this,
                new String[]{permissionName}, permissionRequestCode);
    }
}
