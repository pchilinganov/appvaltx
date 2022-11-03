package chat.atc.tges.tgeschat;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import chat.atc.tges.tgeschat.activity.ActivityMenu;
import chat.atc.tges.tgeschat.activity.ActivityVentasFija;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.dialogs.Contrasena_Restablece_dialog;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.model.Incidencia;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;


import static chat.atc.tges.tgeschat.util.GlobalFunctions.limpiarShared;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.newIntent;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.chat_id;

public class LoginTelefonica  extends BaseVolleyActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
    {
    //ImageCaptcha
    ImageView refreshButton;
    EditText captchaInput;
    Button submitButton;
    public static Uri uri;

    //usando valores globales de Application
    String  usuario="",
            contrasenia="",
            idVendedorChat="",
            dialogContrasena="",
            tokenMovil="",
            URL_DESARROLLO="",
            nomVendedor="",
            apeVendedor="",
            idCanal="",
            dniVendedor="",
            idSession="";

    //reCaptcha
    final String siteKey="6Lca7mcUAAAAAKGFSbVa03AAhY_5oM9gvKxW4V2P";
    final String secretKey="6Lca7mcUAAAAAPivfurPlSN0XH8MwAwKAgCJd7xU";

    public static EditText txtUsuario, txtContrasenia;
    Button btnLogin, btnCatpcha;
    boolean btnSeleccionado=false;
    TextView lblRecuperaContrasena, tvTitulo, tvEntorno;
    String loginEstado = "",loginMensaje = "";
    JSONObject oUsuario;

    public static FragmentManager fragmentManager;
    private GoogleApiClient mGoogleApiClient;

    public static VolleyRP volley;
    public static RequestQueue mRequest;

    SharedPreferences sharedpreferences;

    //Incidencia masiva
    public static List<Incidencia> incidencias = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fragmentManager= getFragmentManager();
        //GlobalFunctions.killAppBypackage(LoginTelefonica.this,getApplicationContext(),"chat.atc.tges.tgeschat");
        //Se invoca al servicio
        uri = ActivityCompat.getReferrer(LoginTelefonica.this);
        if (uri!=null && (uri.toString().equalsIgnoreCase("android-app://pe.vasslatam.movistar.mobile.sales") ))
        {
            setContentView(R.layout.splash_activity);
            getSupportActionBar().hide();
            this.overridePendingTransition(R.anim.slide_up,
                    R.anim.slide_down);
            init();
            //makeRequestLogin("loginIntegracion", "45642084","");

        } else{
            setContentView(R.layout.layout_login);

            getSupportActionBar().hide(); // oculta barra de acción
            volley = VolleyRP.getInstance(this);
            mRequest = volley.getRequestQueue();

            //Obtenemos el token
            tokenMovil = ((TGestionaSession)getApplication()).getTokenMovil();

            URL_DESARROLLO =((TGestionaSession)getApplication()).getURL_DESARROLLO();

            txtUsuario = (EditText) findViewById(R.id.txtUsuario);
            txtUsuario.setNextFocusDownId(R.id.txtContrasenia); // Establece txtContrasenia como siguiente control a recibir el foco
            txtUsuario.setOnKeyListener(new OnKeyListener() {   // Código a ejecutar cuando se presiona tecla Enter

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter key press
                        txtUsuario.clearFocus();
                        txtContrasenia.requestFocus();
                        return true;
                    }
                    return false;
                }
            });

            tvTitulo = (TextView) findViewById(R.id.tvTitulo);
            //tvEntorno = (TextView) findViewById(R.id.tvEntorno);

            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                tvTitulo.setText( "v. " + version);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            //if ()
            txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);
            txtContrasenia.setNextFocusDownId(R.id.btnLogin); // Establece captchaInput como siguiente control a recibir el foco
            txtContrasenia.setOnKeyListener(new OnKeyListener() {   // Código a ejecutar cuando se presiona tecla Enter

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter key press
                        txtContrasenia.clearFocus();
                        btnLogin.requestFocus();
                        return true;
                    }
                    return false;
                }
            }
            );

            //getDataFromBundle();

            /*captchaInput.setNextFocusDownId(R.id.btnLogin); // Establece btnLogin como siguiente control a recibir el foco
            captchaInput.setOnKeyListener(new OnKeyListener() {   // Código a ejecutar cuando se presiona tecla Enter

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)
                            && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter key press
                        captchaInput.clearFocus();
                        btnLogin.requestFocus();
                        return true;
                    }
                    return false;
                }
            });*/

            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);

            /*btnCatpcha= (Button) findViewById(R.id.btnCaptcha);
            btnCatpcha.setOnClickListener(this);*/

            lblRecuperaContrasena = (TextView)findViewById(R.id.lblRecordarContrasena);
            lblRecuperaContrasena.setOnClickListener(this);

            validaciones();

            //handleSSLHandshake(); //Usar en rul de movistartayuda.com

            //variable publica de chat igual a 0 (cuando se cierra sesión por inactividad o manualmente)
            ((TGestionaSession)getApplication()).setChat_id(0);
            //chat_id=0;
//            if (GlobalFunctions.isOnline(getApplicationContext())) {
//                listarIncidencias();
//            }else{
//                Toast.makeText(LoginTelefonica.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
//            }

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(SafetyNet.API)
                    .addConnectionCallbacks(LoginTelefonica.this)
                    .addOnConnectionFailedListener(LoginTelefonica.this)
                    .build();

            mGoogleApiClient.connect();

            //Botón obtiene el foco

            txtUsuario.clearFocus();
            btnLogin.requestFocus();
        }
    }

    private void init() {
        new CountDownTimer(1000, 3000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }.start();
    }

    Boolean flagVentas=false;

    private void getDataFromBundle() {
        uri = ActivityCompat.getReferrer(LoginTelefonica.this);

        if (uri!=null && (uri.toString().equalsIgnoreCase("android-app://pe.vasslatam.movistar.mobile.sales")))
        {
            Log.d("valor de uri",uri.toString());
            Log.d("FROM BUNDLE","appVentas");
            vistaIncidenciaMasiva =false;
            //Proviene de AppVentas
            dniVendedor = getIntent().getStringExtra("DNI");
            //txtUsuario.setText(dniVendedor);
            flagVentas = true;
            SharedPreferences.Editor editor = getSharedPreferences("mta_loged", MODE_PRIVATE).edit();
            editor.putBoolean("flagVentas", flagVentas); // Pasar a appProd
            makeRequestLogin("loginIntegracion", dniVendedor,"");
        }
        else
        {
            Log.d("FROM BUNDLE","appMTA");
            flagVentas = false;
            SharedPreferences.Editor editor = getSharedPreferences("mta_loged", MODE_PRIVATE).edit();
            editor.putBoolean("flagVentas", flagVentas); // Pasar a appProd
            validaIngresoDirecto();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //variable publica de chat igual a 0 (cuando se cierra sesión por inactividad o manualmente)
        ((TGestionaSession)getApplication()).setChat_id(0);
        chat_id=0;
        //init();
        getDataFromBundle();   //Pasar a appProd

        //validaIngresoDirecto();
        //Toast.makeText(LoginTelefonica.this,"Evento onResume de Activity LoginTelefonica" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(LoginTelefonica.this,"Evento onPause de Activity Logintelefonica" , Toast.LENGTH_SHORT).show();
    }

    private void validaIngresoDirecto() {
        sharedpreferences = getSharedPreferences("mta_loged",
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains("usuario")) {
            GlobalFunctions.newIntent(LoginTelefonica.this, ActivityMenu.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            finish();
        }
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            String ok="";
            ok="OK";
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
        }*/
    }

//    @Override
//    public void onBackPressed() {
//        Intent setIntent = new Intent(Intent.ACTION_MAIN);
//        setIntent.addCategory(Intent.CATEGORY_HOME);
//        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(setIntent);
//    }

    private void validaciones(){

        /*---Deshabilita CopyPaste---*/
        txtUsuario.setLongClickable(false);
        txtContrasenia.setLongClickable(false);
        /*------------Fin------------*/

        /*----Validación de usuario y contraseña (mínimo 8 caracteres)------*/
        /*txtUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                @Override
                                                public void onFocusChange(View v, boolean hasFocus) {
                                                    // TODO Auto-generated method stub

                                                    if (hasFocus)
                                                    {
                                                        if (txtUsuario.getText().toString().trim().length() < 8) {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                                                            txtUsuario.setError("Mínimo 8 caracteres");
                                                        }
                                                    }
                                                    else
                                                    {
                                                        if (txtUsuario.getText().toString().trim().length() < 8)
                                                        {
                                                            txtUsuario.setError("Mínimo 8 caracteres");
                                                        }
                                                        else
                                                        {
                                                            txtUsuario.setError(null);
                                                        }
                                                    }
                                                }
                                            }
        );*/

        /*txtContrasenia.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                    @Override
                                                    public void onFocusChange(View v, boolean hasFocus) {
                                                        // TODO Auto-generated method stub

                                                        if (hasFocus) {
                                                            if (txtContrasenia.getText().toString().trim().length() < 8) {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                                                                txtContrasenia.setError("Mínimo 8 caracteres");
                                                            }

                                                        } else {
                                                            if (txtContrasenia.getText().toString().trim().length() < 8) {
                                                                txtContrasenia.setError("Mínimo 8 caracteres");
                                                            } else {
                                                                // your code here
                                                                txtContrasenia.setError(null);
                                                            }
                                                        }
                                                    }
                                                }
        );*/
        /*----------------------------------------Fin de Validación mínimo 8 caracteres----------------------------------------------*/
    }

    private void listarIncidencias(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarIncidencias", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the rv
                incidencias.clear();
                getDataIncidencias(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginTelefonica.this,"Error en listarIncidencias(): " + error.toString() , Toast.LENGTH_SHORT).show();
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
            boolean existeArray = jsonArrayDataIncidencias.getJSONObject(0).isNull("ListaDatos");
            if (!existeArray)
            {
                jsonArrayListaDatos = jsonArrayDataIncidencias.getJSONObject(0).getJSONArray("ListaDatos");
            }
            //Establece la variable de clase a true
            if (WS_MsgIncidencia_Estado && !existeArray && jsonArrayListaDatos.length() >0)
            {
                existeIncidenciaMasiva=true;
            }
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }

    //Muestra Dialogo de Búsqueda de Expediente Proactivo
    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");

    }
    public static Boolean existeIncidenciaMasiva=false;
    public static Boolean vistaIncidenciaMasiva=false;
    ProgressDialog progressLogin;
    private void makeRequestLogin(final String nomWS, final String dniVendedor, final String contrasenia){

        if (!nomWS.equalsIgnoreCase("loginIntegracion")) {
            progressLogin = new ProgressDialog(this);
        }
        String url = varPublicas.URL_DESARROLLO+nomWS;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                getDataUsuario(response);

                if(loginEstado.equalsIgnoreCase("8"))
                {
                    if (!nomWS.equalsIgnoreCase("loginIntegracion"))
                    {
                        Toast.makeText(LoginTelefonica.this, loginMensaje, Toast.LENGTH_LONG).show();
                        ((TGestionaSession) getApplication()).setDialogContrasena(1);
                        mostrarDialogRestableceContrasena();
                    }
                }
                else if(loginEstado.equalsIgnoreCase("4"))
                {
                    Toast.makeText(LoginTelefonica.this,loginMensaje, Toast.LENGTH_LONG).show();
                    //mostrarDialogoBloqueoIntegracion(loginMensaje);
                    if (nomWS.equalsIgnoreCase("loginIntegracion"))  finishAffinity();
                }
                else if(loginEstado.equalsIgnoreCase("6") || loginEstado.equalsIgnoreCase("3"))
                {
                    if (flagVentas){
                        finishAffinity();
                    }
                    Toast.makeText(LoginTelefonica.this, loginMensaje, Toast.LENGTH_LONG).show();
                }
                else if (loginEstado.equalsIgnoreCase("99")) {
                    Toast.makeText(LoginTelefonica.this, loginMensaje, Toast.LENGTH_LONG).show();
                    if (nomWS.equalsIgnoreCase("loginIntegracion")){
                        GlobalFunctions.newIntent(LoginTelefonica.this, ActivityMenu.class);
                    } else if(nomWS.equalsIgnoreCase("login2")){
                        GlobalFunctions.newIntent(LoginTelefonica.this, ActivityMenu.class);
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    /*}else{
                        Toast.makeText(LoginTelefonica.this, "Código captcha incorrecto.", Toast.LENGTH_SHORT).show();
                    }
                }*/
                }
                else
                    Toast.makeText(LoginTelefonica.this,loginMensaje, Toast.LENGTH_LONG).show();

                onConnectionFinished();
                if (!nomWS.equalsIgnoreCase("loginIntegracion")) {
                    progressLogin.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginTelefonica.this,"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                onConnectionFailed(error.toString());
                if (!nomWS.equalsIgnoreCase("loginIntegracion")) {
                    progressLogin.dismiss();
                }
            }
        }
        )

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                //String usuario="", contrasenia="";

                //usuario = txtUsuario.getText().toString();
                //contrasenia = txtContrasenia.getText().toString();

                Log.d("txtusuario","Param txtusuario" + dniVendedor);
                Log.d("txtpassword","Param txtpassword" + contrasenia);

                ((TGestionaSession)getApplication()).setUsuario(usuario);
                ((TGestionaSession)getApplication()).setContrasenia(contrasenia);

                Map<String, String> params = new HashMap<String, String>();
                //params.put("authorization", ((TGestionaSession) getApplication()).getbAuthenticationHash());
                params.put("txtusuario", dniVendedor);
                params.put("txtpassword", contrasenia);

                /*if (((TGestionaSession) getApplication()).getTokenMovil()==null){
                    ((TGestionaSession) getApplication()).setTokenMovil("");
                }*/
                //params.put("token", varPublicas.tokenMovil);

                params.put("token", ((TGestionaSession) getApplication()).getTokenMovil());


                if (varPublicas.tokenMovil==null)
                {
                    varPublicas.tokenMovil ="";
                }
                params.put("token", varPublicas.tokenMovil);
                //params.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
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
        if (!nomWS.equalsIgnoreCase("loginIntegracion")) {
            progressLogin.setMessage("Validando Acesso....");
            progressLogin.show();
            progressLogin.setCancelable(false);
        }
    }

    public void getDataUsuario (String cadenaJSON)
    {
        try
        {
            JSONArray jsonArrayDataUsuario=new JSONArray(cadenaJSON);
            loginMensaje= jsonArrayDataUsuario.getJSONObject(0).getString("Msg").toString();
            loginEstado= jsonArrayDataUsuario.getJSONObject(0).getString("NroMensaje").toString();

            JSONArray jsonArrayListaDatos = jsonArrayDataUsuario.getJSONObject(0).getJSONArray("ListaDatos");

            for (int i = 0; i < jsonArrayListaDatos.length(); i++) {

                usuario = jsonArrayListaDatos.getJSONObject(0).getString("idusuario").toString();
                idVendedorChat = jsonArrayListaDatos.getJSONObject(0).getString("id_vendedor").toString();
                nomVendedor = jsonArrayListaDatos.getJSONObject(0).getString("NOMBRES").toString();
                apeVendedor=jsonArrayListaDatos.getJSONObject(0).getString("APELLIDOS").toString();
                idCanal=jsonArrayListaDatos.getJSONObject(0).getString("id_canal").toString();
                dniVendedor=jsonArrayListaDatos.getJSONObject(0).getString("DNI").toString();
                idSession=jsonArrayListaDatos.getJSONObject(0).getString("SCOOKIE").toString();

                SharedPreferences.Editor editor = getSharedPreferences("mta_loged", MODE_PRIVATE).edit();
                editor.putString("usuario", usuario);
                editor.putString("idVendedorChat", idVendedorChat);
                editor.putString("nomVendedor", nomVendedor);
                editor.putString("apeVendedor", apeVendedor);
                editor.putString("idCanal", idCanal);
                editor.putString("dniVendedor", dniVendedor);
                editor.putString("idSession", idSession);
                editor.putBoolean("flagVentas", flagVentas);

                editor.apply();

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
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }

    private void mostrarDialogoBloqueoIntegracion(String msg){
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(true); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_bloqueo_integracion); //establecemos el contenido de nuestro dialog

        final TextView txtMsg;
        txtMsg =  (TextView)customDialog.findViewById(R.id.txt_msg);
        txtMsg.setText(msg);
        ((Button) customDialog.findViewById(R.id.btnAceptar)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }

    private void mostrarDialogRestableceContrasena()
    {
        Contrasena_Restablece_dialog myDiag = new Contrasena_Restablece_dialog();

        myDiag.setCancelable(true);
        myDiag.show(getSupportFragmentManager(), "Contrasena_Restablece");
    }

    /**
     * Enables https connections : PELIGROSO para entornos de producción , debido a que es vulnerable
     */

    /*@SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }*/

    @Override
    public void onClick(View v) {
        if (v==btnLogin){
            listarIncidencias();
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                makeRequestLogin("login2", txtUsuario.getText().toString(), txtContrasenia.getText().toString());
            }
            else
            {
                Toast.makeText(LoginTelefonica.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }


        }

        if (v == lblRecuperaContrasena)
        {
            if (GlobalFunctions.isOnline(getApplicationContext()))
            {
                mostrarDialogRestableceContrasena();
                ((TGestionaSession) getApplication()).setDialogContrasena(0);
            }
            else
            {
                Toast.makeText(LoginTelefonica.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }
            //varPublicas.dialogContrasena = 0; // Diálogo de restablecer contraseña
        }

        if (v == btnCatpcha){
            SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, siteKey)
                    .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                        @Override
                        public void onResult(SafetyNetApi.RecaptchaTokenResult result) {
                            Status status = result.getStatus();

                            if ((status != null) && status.isSuccess()) {

                                //tvResult.setText("isSuccess()\n");
                                Toast.makeText(LoginTelefonica.this, "isSuccess()", Toast.LENGTH_LONG).show();
                                // Indicates communication with reCAPTCHA service was
                                // successful. Use result.getTokenResult() to get the
                                // user response token if the user has completed
                                // the CAPTCHA.

                                if (!result.getTokenResult().isEmpty()) {
                                    Toast.makeText(LoginTelefonica.this, "!result.getTokenResult().isEmpty()", Toast.LENGTH_LONG).show();
                                    //tvResult.append("!result.getTokenResult().isEmpty()");
                                    // User response token must be validated using the
                                    // reCAPTCHA site verify API.
                                }
                                else
                                {
                                    Toast.makeText(LoginTelefonica.this, "result.getTokenResult().isEmpty()", Toast.LENGTH_LONG).show();
                                    //tvResult.append("result.getTokenResult().isEmpty()");
                                }
                            } else {

                                Log.e("MY_APP_TAG", "Error occurred " +
                                        "when communicating with the reCAPTCHA service.");
                                Toast.makeText(LoginTelefonica.this, "Error occurred when communicating with the reCAPTCHA service.", Toast.LENGTH_LONG).show();
                                /*tvResult.setText("Error occurred " +
                                        "when communicating with the reCAPTCHA service.");*/

                                // Use status.getStatusCode() to determine the exact
                                // error code. Use this code in conjunction with the
                                // information in the "Handling communication errors"
                                // section of this document to take appropriate action
                                // in your app.
                            }
                        }
                    });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*Toast.makeText(this, "onConnected()", Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        /*Toast.makeText(this,
                "onConnectionSuspended: " + i,
                Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*Toast.makeText(this,
                "onConnectionFailed():\n" + connectionResult.getErrorMessage(),
                Toast.LENGTH_LONG).show();*/
    }
}

