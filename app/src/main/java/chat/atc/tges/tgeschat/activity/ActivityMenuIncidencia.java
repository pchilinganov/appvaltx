package chat.atc.tges.tgeschat.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.Services.RefreshLogCat;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static chat.atc.tges.tgeschat.LoginTelefonica.existeIncidenciaMasiva;
import static chat.atc.tges.tgeschat.LoginTelefonica.incidencias;
import static chat.atc.tges.tgeschat.LoginTelefonica.uri;
import static chat.atc.tges.tgeschat.LoginTelefonica.vistaIncidenciaMasiva;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.limpiarShared;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.newIntent;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;


public class ActivityMenuIncidencia extends BaseVolleyActivity implements SwipeRefreshLayout.OnRefreshListener,OnClickListener {

    RelativeLayout rlChat, rlVentasFija;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    TextView tvNombreUsuario;
    private VolleyRP volley;
    private RequestQueue mRequest;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedpreferences;
    public static FragmentManager fragmentManager;
    RelativeLayout rlSpaceLeft, rlSpaceRight;
    private final int REQUEST_PERMISSION_PHONE_WRITE =2;
    Intent service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_activity_menu_incidencia);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  //Oculta teclado

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        rlChat = (RelativeLayout) findViewById(R.id.rlChat);
        rlChat.setOnClickListener(this);
        fragmentManager= getFragmentManager();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        //rlAsistencias.setBackgroundColor(Color.GRAY);
        rlVentasFija = (RelativeLayout) findViewById(R.id.rlVentasFija);
        rlVentasFija.setOnClickListener(this);
        tvNombreUsuario = (TextView) findViewById(R.id.tvNombreUsuario);

        rlSpaceLeft = (RelativeLayout) findViewById(R.id.rlSpaceLeft);
        rlSpaceRight = (RelativeLayout) findViewById(R.id.rlSpaceRight);

        loadPreferences();

        ((TGestionaSession)getApplication()).setTokenMovil(FirebaseInstanceId.getInstance().getToken());

        tvNombreUsuario.setText(((TGestionaSession)getApplication()).getNomVendedor());

//        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().hide(); // oculta ActionBar
        getSupportActionBar().setTitle("MovistarTAyuda");

        //habilitaModulosxUsuario();
        //callValidarAccesoContingencia();
        //validaTamanioMenuItem();



        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        callValidarAccesoContingencia();
                    }
                }
        );
        listarIncidencias();

        service = new Intent(this, RefreshLogCat.class);
        startService(service);
        showPhoneStatePermissionWrite();

//        if (!vistaIncidenciaMasiva) mostrarDialogoIncidenciaMasiva();

    }
    Boolean flagVentas=false;
    String usuario="", idVendedorChat="", nomVendedor="", apeVendedor="", idCanal="", dniVendedor="", idSession="";
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
        tvNombreUsuario.setText(nomVendedor);
    }

    @Override
    public void onResume() {
        super.onResume();


        //Verifica si hay incidencia masiva y la muestra cuando el usuario se logea
        if (!vistaIncidenciaMasiva) mostrarDialogoIncidenciaMasiva();
        loadPreferences();
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        callValidarAccesoContingencia();
                    }
                }
        );
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        callValidarAccesoContingencia();
                    }
                }
        );
        //se invoca al método para que aparezcan los gráficos coloreados
        //getInbox();

    }

//    private void validaTamanioMenuItem() {
//        if (rlVentasFija.getVisibility()==View.GONE && rlChat.getVisibility()==View.VISIBLE)
//        {
//                params.setMargins(140,20,140,20);
//                rlChat.setLayoutParams(params);
//        } else
//        {
//            //rlChat.setLayoutParams(null);
//        }
//        //else if (rlVentasFija.getVisibility()==View.VISIBLE && rlChat.getVisibility()==View.VISIBLE){
////                params.setMargins(16,16,16,16);
////                rlChat.setLayoutParams(params);
////                rlVentasFija.setLayoutParams(params);
////        }
//    }

    private void listarIncidencias(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarIncidencias", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the rv
                incidencias.clear();
                getDataIncidencias(response);
                if (!vistaIncidenciaMasiva) mostrarDialogoIncidenciaMasiva();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityMenuIncidencia.this,"Error en listarIncidencias(): " + error.toString() , Toast.LENGTH_SHORT).show();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("authorization", ((TGestionaSession) getApplication()).getbAuthenticationHash());
                //params.put("idvendedor",varPublicas.idVendedorChat);

                /*if (varPublicas.tokenMovil==null) {
                    varPublicas.tokenMovil ="";
                }*/
                params.put("idCanal", varPublicas.idCanal);
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }

    String  WS_MsgIncidencia_Msg="";
    Boolean WS_MsgIncidencia_Estado=false;

    JSONArray jsonArrayListaDatos = new JSONArray();

    public void getDataIncidencias (String cadenaJSON) {
        try
        {
            JSONArray jsonArrayDataIncidencias=new JSONArray(cadenaJSON);
            WS_MsgIncidencia_Msg= jsonArrayDataIncidencias.getJSONObject(0).getString("Msg").toString();
            WS_MsgIncidencia_Estado= jsonArrayDataIncidencias.getJSONObject(0).getBoolean("Estado");
            boolean existeArrayIM = jsonArrayDataIncidencias.getJSONObject(0).isNull("ListaDatos");
            if (!existeArrayIM)
            {
                jsonArrayListaDatos = jsonArrayDataIncidencias.getJSONObject(0).getJSONArray("ListaDatos");
            }
            //Establece la variable de clase a true
            if (WS_MsgIncidencia_Estado && !existeArrayIM && jsonArrayListaDatos.length() >0 )
            {
                existeIncidenciaMasiva=true;
            }
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }

    private void habilitaModulosxUsuario(){
            if (idCanal!=null && idCanal.equalsIgnoreCase("2841")){
                rlChat.setVisibility(View.GONE);
            } else{
                rlChat.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ficha, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_Save) {
            //Toast.makeText(this, "hola", Toast.LENGTH_SHORT).show();
        }


        if (id == R.id.action_Notificaciones) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                mostrarDialogoIncidenciaMasiva();
            }else{
                Toast.makeText(ActivityMenuIncidencia.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.action_Descargas) {

        }

        if (id == R.id.action_BandejaTickets) {

        }

        if (id == R.id.action_Adjuntar) {

        }
        //Comentar
        if (id == R.id.action_Comentar) {

        }

        if (id == R.id.action_CerrarSesion) { //pasar a appProd
            GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);

//            sharedpreferences = getSharedPreferences("mta_loged",
//                    Context.MODE_PRIVATE);
            if (sharedpreferences.getBoolean("flagVentas",false)){
                finishAffinity();
                GlobalFunctions.killAppBypackage(getApplicationContext(), ActivityMenuIncidencia.this, "chat.atc.tges.tgeschat");
            } else{
                newIntent(ActivityMenuIncidencia.this, LoginTelefonica.class);
                uri = null;
            }
            limpiarShared(this);
            Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
            vistaIncidenciaMasiva=false;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { //Pasar a app Real
        newIntent(this, ActivityMenu.class);
    }

    private void callValidarAccesoContingencia()
    {
        Log.d("ws","valida_contingencia_fija");
        //customDialog.show(); //Inicia dialog
        String WSMethod="";
        WSMethod = "valida_contingencia_fija";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        params.put("token", "asdasd");

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONArray jsonArray = null;
                        int NroMensaje, abrirbtnpuntual = 0, abrirbtnmasivas = 0;
                        String msg;
                        Boolean sessionStatus=false;

                        try {
                            //parse de web service de combos
                            jsonArray = new JSONArray(result);

                            sessionStatus = jsonArray.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

                            if(!sessionStatus) {
                                GlobalFunctions.validaSesion(sessionStatus, getApplicationContext());
                                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
                                if (sharedpreferences.getBoolean("flagVentas",false)){
                                    finishAffinity();
                                } else{
                                    newIntent(ActivityMenuIncidencia.this, LoginTelefonica.class);
                                    uri = null;
                                }
                                limpiarShared(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                                vistaIncidenciaMasiva=false;
                            }

                            NroMensaje = jsonArray.getJSONObject(0).getInt("NroMensaje");
                            
                            abrirbtnpuntual = jsonArray.getJSONObject(0).getInt("btnpuntual");
                            abrirbtnmasivas = jsonArray.getJSONObject(0).getInt("btnmasiva");
                            
                            if (!jsonArray.getJSONObject(0).isNull("NroMensaje")) {




                                if (abrirbtnpuntual == 1) {
                                    //rlChat.setVisibility(View.VISIBLE);
                                    //msg = jsonArray.getJSONObject(0).getString("Msg");
                                    rlChat.setBackgroundColor(Color.parseColor("#01a9df"));
                                    //rlVentasFija.setBackgroundColor(Color.parseColor("#777777"));
                                    //rlVentasFija.setEnabled(false);
                                    //rlChat.setVisibility(View.VISIBLE);
                                    rlChat.setEnabled(true);
                                    rlSpaceLeft.setVisibility(View.GONE);
                                    rlSpaceRight.setVisibility(View.GONE);
                                }
                                else {

                                    rlChat.setEnabled(false);
                                    rlChat.setBackgroundColor(Color.parseColor("#777777"));
                                }

                                if (abrirbtnmasivas == 1)
                                {

                                    rlVentasFija.setBackgroundColor(Color.parseColor("#01a9df")); //celeste
                                    //rlChat.setBackgroundColor(Color.parseColor("#777777")); // plomo
                                    //rlChat.setEnabled(false);
                                    rlVentasFija.setEnabled(true);
                                    rlSpaceLeft.setVisibility(View.GONE);
                                    rlSpaceRight.setVisibility(View.GONE);
                                } else {
                                    rlVentasFija.setEnabled(false);
                                    rlVentasFija.setBackgroundColor(Color.parseColor("#777777"));

                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        //customDialog.dismiss(); //Inicia dialog
                    }
                }
        );
    }

    private void callValidarBase(String numDocVendedor){
        //customDialog.show(); //Inicia dialog
        Log.d("ws","valida_base_vendedor");
        String WSMethod="";
        WSMethod = "valida_base_vendedor";
        String URL = URL_DESARROLLO + WSMethod;
        Map<String, String> params = new HashMap<String, String>();
        params.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        params.put("dniVendedormodal", numDocVendedor);
        params.put("ind", ((TGestionaSession) getApplication()).getTipoIncidencia());
        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        params.put("token", "asdads");

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result)
                    {
                        JSONArray jsonArray = null;
                        int NroMensaje=0;
                        String msg;
                        Boolean sessionStatus=false;

                        try {
                            //parse de web service de combos
                            jsonArray = new JSONArray(result);
                            sessionStatus = jsonArray.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

                            if(!sessionStatus){
                                GlobalFunctions.validaSesion(sessionStatus, getApplicationContext());
                                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
                                if (sharedpreferences.getBoolean("flagVentas",false)){
                                    finishAffinity();
                                    GlobalFunctions.killAppBypackage(getApplicationContext(), ActivityMenuIncidencia.this, "chat.atc.tges.tgeschat");
                                } else{
                                    newIntent(ActivityMenuIncidencia.this, LoginTelefonica.class);
                                    uri = null;
                                }
                                limpiarShared(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                                vistaIncidenciaMasiva=false;
                            }
                            if (!jsonArray.getJSONObject(0).isNull("NroMensaje"))
                            {
                                NroMensaje = jsonArray.getJSONObject(0).getInt("NroMensaje");
                            }
                            if (NroMensaje==200){
                                GlobalFunctions.newIntent(ActivityMenuIncidencia.this, ActivityVisorVentasFija.class);
                            }
                            else if (NroMensaje==250)
                            {
                                msg = jsonArray.getJSONObject(0).getString("Msg");
                                Toast.makeText(ActivityMenuIncidencia.this, msg, Toast.LENGTH_SHORT).show();
                                GlobalFunctions.newIntent(ActivityMenuIncidencia.this, ActivityMenu.class);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        //customDialog.dismiss(); //Inicia dialog
                    }
                }
        );
    }
    public static String numDocVendedor="";
    private void mostrarValidaBaseVendedort(){
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(true); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_valida_base); //establecemos el contenido de nuestro dialog

        final TextView tvTitulo;
        final EditText txtNumDoc;
        final Button btnVerificarDocumento;

        txtNumDoc =  (EditText) customDialog.findViewById(R.id.txtNumDoc);
        btnVerificarDocumento =  (Button)customDialog.findViewById(R.id.btnVerificarDocumento);



        ((Button) customDialog.findViewById(R.id.btnVerificarDocumento)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                numDocVendedor = txtNumDoc.getText().toString();
                if (numDocVendedor.equalsIgnoreCase(""))
                    Toast.makeText(getApplicationContext(), "Debe ingresar un número de docmento", Toast.LENGTH_SHORT).show();
                else{
                callValidarBase(numDocVendedor);
                customDialog.dismiss();
                }
            }
        });


        customDialog.show();
    }

    private void showPhoneStatePermissionWrite() {
        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        //int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        //int permissionRecord = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);

        //Permiso de Escritura
        if (permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityMenuIncidencia.this, WRITE_EXTERNAL_STORAGE)) {
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
        ActivityCompat.requestPermissions(ActivityMenuIncidencia.this,
                new String[]{permissionName}, permissionRequestCode);
    }

    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");
        vistaIncidenciaMasiva=true;
    }

    @Override
    public void onClick(View v) {

        if (v==rlChat)
        {

            ((TGestionaSession)getApplication()).setTipoIncidencia("1");
            mostrarValidaBaseVendedort();
        }

        if (v==rlVentasFija)
        {
            ((TGestionaSession)getApplication()).setTipoIncidencia("2");
            mostrarValidaBaseVendedort();
            //callValidarBase();
        }
    }
}
