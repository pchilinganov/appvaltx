package chat.atc.tges.tgeschat.dialogs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.util.GlobalFunctions.validarEmail;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

//import static chat.atc.tges.tgeschat.LoginTelefonica.handleSSLHandshake;

//import com.android.volley.AuthFailureError;
//import com.android.volley.VolleyError;

public class Contrasena_Restablece_dialog extends DialogFragment implements OnClickListener {

    private static final int REQUEST_READ_PHONE_STATE_PERMISSION = 225;
    EditText txtUsuario,txtEmail,txtCodigoActivacion,txtContrasena_Actual_Nueva,txtConfirmarContrasena,txtContrasenaActual, txtEmailNew;
    TextInputLayout tilUsuario, tilEmail,tilCodValidacion, tilContrasena_Actual_Nueva, tilContrasenaNew, tilContrasenaActual, tilEmailNew;
    View separator;
    TextView lblTitle;
    ImageView imgSent,imgSecureSent;
    Button btnRestablecerContrasena, btnRegresar;
    String token;

    public static VolleyRP volley;
    public static RequestQueue mRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Oculta Teclado al iniciar layout
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        listarIncidencias();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_reestablece_contrasena, container, false);

        //handleSSLHandshake(); //Usar en rul de movistartayuda.com

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        txtUsuario = (EditText) v.findViewById(R.id.txtUsuario);
        txtCodigoActivacion = (EditText) v.findViewById(R.id.txtCodigoValidacion);
        txtContrasenaActual = (EditText) v.findViewById(R.id.txtContrasenaActual);
        txtEmail = (EditText) v.findViewById(R.id.txtEmail);
        txtContrasena_Actual_Nueva = (EditText) v.findViewById(R.id.txtContrasena_Actual_Nueva);
        txtConfirmarContrasena = (EditText) v.findViewById(R.id.txtContrasenaNew);
        txtEmailNew = (EditText) v.findViewById(R.id.txtEmailNew);
        tilUsuario = (TextInputLayout) v.findViewById(R.id.tilUsuario);
        tilEmail = (TextInputLayout) v.findViewById(R.id.tilEmail);
        tilCodValidacion = (TextInputLayout) v.findViewById(R.id.tilCodValidacion);
        tilContrasena_Actual_Nueva = (TextInputLayout) v.findViewById(R.id.tilContrasena_Actual_Nueva);
        tilContrasenaNew = (TextInputLayout) v.findViewById(R.id.tilContrasenaNew);
        tilEmailNew = (TextInputLayout) v.findViewById(R.id.tilEmailNew);
        tilContrasenaActual = (TextInputLayout) v.findViewById(R.id.tilContrasenaActual);
        separator = (View) v.findViewById(R.id.separator);
        //imgSecureSent = (ImageView) v.findViewById(R.id.imgViewEnvioCodigoValidacion);
        btnRestablecerContrasena = (Button) v.findViewById(R.id.btnRestablecerContrasena);
        btnRestablecerContrasena.setOnClickListener(this);
       /* btnRegresar = (Button) v.findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(this);*/

       //valida campos de GUI
        validaciones();

        if (((TGestionaSession)getActivity().getApplication()).getDialogContrasena()==0)
        {   //Contraseña Nueva
            //txtContrasena_Actual_Nueva.setHint("Contraseña Nueva");
        }
        else if (((TGestionaSession)getActivity().getApplication()).getDialogContrasena()==1)
        { // Contraseña Caduca
            tilEmail.setVisibility(View.GONE);
            //txtUsuario.setText(varPublicas.usuario);
            //lblTitle.setText("");
            mostrarCamposOcultos();
            txtUsuario.setText(((TGestionaSession)getActivity().getApplication()).getUsuario());
            txtUsuario.setText( LoginTelefonica.txtUsuario.getText().toString());
            txtUsuario.setEnabled(false);
            txtCodigoActivacion.setVisibility(View.GONE);
            //txtContrasenaActual.setHint("Contraseña Actual");
            tilContrasenaActual.setVisibility(View.VISIBLE);
            tilContrasenaNew.setVisibility(View.VISIBLE);
            tilContrasena_Actual_Nueva.setVisibility(View.VISIBLE);
            //txtContrasena_Actual_Nueva.setHint("Contraseña Nueva");
            tilEmailNew.setVisibility(View.VISIBLE);
        }

        //token de firebase asignado a dispositivo
        token = FirebaseInstanceId.getInstance().getToken();
        ((TGestionaSession)getActivity().getApplication()).setTokenMovil(token);
        //makeRequestSolicitaCodigoValidacion();

        return v;
    }

    String nuevaContrasena="",confirmaContrasena="";
    @Override
    public void onClick(View v) {
        if (v==btnRestablecerContrasena){

            ((TGestionaSession)getActivity().getApplication()).setUsuario(txtUsuario.getText().toString().trim());
            if(((TGestionaSession)getActivity().getApplication()).getDialogContrasena()==0) { //Reestablecimiento Contraseña
                if (btnRestablecerContrasena.getText().equals("ACEPTAR")) {

                    if (!validarEmail(txtEmail.getText().toString().trim()))
                    {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                        //txtEmail.setError("Ingrese un email válido.");
                        txtEmail.setError("Ingrese un email válido.");
                    }
                    else
                    {
                        if (txtUsuario.getText().toString().length() > 0 || txtEmail.getText().toString().length() >0) { //Si el campo de texto CodActivacion tiene valor
                            makeRequestValidaCorreoUsuario(); //WS que verifica si email pertenece a usuario
                        } else
                            Toast.makeText(getContext(), "Campos Usuarios e Email deben tener datos.", Toast.LENGTH_SHORT);
                    }
                }

                nuevaContrasena = txtContrasena_Actual_Nueva.getText().toString();
                confirmaContrasena = txtConfirmarContrasena.getText().toString();

                if (btnRestablecerContrasena.getText().equals("VALIDAR")) {
                    if (txtCodigoActivacion.length() > 0) {
                        codActivacion=txtCodigoActivacion.getText().toString();
                        makeRequestConfirmaCodigoValidacion();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Campo Código de Validación no puede ir vacío.", Toast.LENGTH_LONG);
                    }
                    return;
                }

                if (btnRestablecerContrasena.getText().equals("RESTABLECER")) {
                    if (nuevaContrasena.length() > 0 && confirmaContrasena.length() > 0)
                    {
                        if (nuevaContrasena.equalsIgnoreCase(confirmaContrasena))
                        {
                            makeRequestRecuperaContrasena();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Contraseñas deben coincidir.", Toast.LENGTH_SHORT);
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Campos de nueva contraseña y confirmación no pueden ir vacíos.", Toast.LENGTH_LONG);
                    }
                }
            }
            else if (((TGestionaSession)getActivity().getApplication()).getDialogContrasena()==1) //Clave Caduca
            {
                if (!validarEmail(txtEmailNew.getText().toString().trim()))
                {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                    //txtEmail.setError("Ingrese un email válido.");
                    txtEmailNew.setError("Ingrese un email válido.");
                }
                else
                {
                    btnRestablecerContrasena.setText("RESTABLECER");
                    tilContrasena_Actual_Nueva.setVisibility(View.VISIBLE);
                    tilContrasenaNew.setVisibility(View.VISIBLE);
                    tilContrasenaActual.setVisibility(View.VISIBLE);
                    makeRequestCambioClaveCaduca();
                }
            }
        }

        if (v==btnRegresar)
        {
            startActivity(new Intent(getContext(), LoginTelefonica.class));
        }
    }

    private void mostrarCamposOcultos(){
        txtContrasena_Actual_Nueva.setVisibility(View.VISIBLE);
        txtConfirmarContrasena.setVisibility(View.VISIBLE);
    }

    private void validaciones(){
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                              @Override
                                              public void onFocusChange(View v, boolean hasFocus) {
                                                  // TODO Auto-generated method stub

                                                  if (hasFocus)
                                                  {
                                                      if (!validarEmail(txtEmail.getText().toString())) {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                                                          //txtEmail.setError("Ingrese un email válido.");
                                                          txtEmail.setError("Ingrese un email válido.");
                                                      }
                                                  }
                                              }
                                          }
        );
        /*----Validación de usuario y contraseña (mínimo 8 caracteres)------*/
        txtEmailNew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                @Override
                                                public void onFocusChange(View v, boolean hasFocus) {
                                                    // TODO Auto-generated method stub

                                                    if (hasFocus)
                                                    {
                                                        if (!validarEmail(txtEmailNew.getText().toString())) {     //try using value.getText().length()<3  instead of the value.getText().trim().length()**
                                                            //txtEmail.setError("Ingrese un email válido.");
                                                            txtEmailNew.setError("Ingrese un email válido.");
                                                        }
                                                    }
                                                }
                                            }
        );
    }

    private void makeRequestValidaCorreoUsuario() {
        final ProgressDialog progress= new ProgressDialog(getActivity());

        //RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO()+"ValidarUsuarioOC";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getDataValidacionCorreoUsuario(response);
                Toast.makeText(getActivity(),WS_ConfirmaValidacionCorreoUsuario_Msg , Toast.LENGTH_LONG).show();
                if (WS_ConfirmaValidacionCorreoUsuario_Estado) { // Si el correo está asociado al usuario
                    separator.setVisibility(View.VISIBLE);
                    tilCodValidacion.setVisibility(View.VISIBLE);
                    btnRestablecerContrasena.setText("VALIDAR");
                    makeRequestSolicitaCodigoValidacion(); // invoca a ws que solicita codigo de activacion
                }

                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("txtusuario", txtUsuario.getText().toString());
                params.put("txtemail", txtEmail.getText().toString());


                return params;
            }};
        
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
        //queue.add(request);
        progress.setMessage("Cargando....");
        progress.show();
        progress.setCancelable(false);
    }

    String WS_ConfirmaValidacionCorreoUsuario_Msg="";
    boolean WS_ConfirmaValidacionCorreoUsuario_Estado=false;

    public void getDataValidacionCorreoUsuario (String cadenaJSON){

        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_ConfirmaValidacionCorreoUsuario_Msg= jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_ConfirmaValidacionCorreoUsuario_Estado= Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());

            /*JSONArray jsonArrayListaDatos = jsonArrayData.getJSONObject(0).getJSONArray("ListaDatos");

            for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                codActivacion= jsonArrayListaDatos.getJSONObject(0).getString("CODIGO").toString();
            }
            txtCodigoActivacion.setText(codActivacion);*/
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    private void listarIncidencias(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "listarIncidencias", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the rv
                //incidencias.clear();
                //getDataIncidencias(response);
                //Toast.makeText(LoginTelefonica.this,"" + "Existe incidencia: "+  existeIncidenciaMasiva, Toast.LENGTH_LONG).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error en listarIncidencias(): " + error.toString() , Toast.LENGTH_SHORT).show();
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

                /* if (varPublicas.tokenMovil==null) {
                    varPublicas.tokenMovil ="";
                }*/
                params.put("idCanal", varPublicas.idCanal);
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
    }

    //Solicita código de activación
    private void makeRequestSolicitaCodigoValidacion(){
        final ProgressDialog progress= new ProgressDialog(getActivity());
        //RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO()+"GenerarPushOlvidoClave";

        //String url = "http://192.168.10.183/ws_siac/GenerarPushOlvidoClave";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //txtCodigoActivacion
                getDataCodValidacion(response);
                //if (response.equalsIgnoreCase("true")) {
                Toast.makeText(getActivity(), WS_ValidaCod_Msg, Toast.LENGTH_LONG).show();
                /*}
                else
                {*/
                //Toast.makeText(getActivity(), "Error al validar usuario y/o correo " + response.toString(), Toast.LENGTH_LONG).show();
                //}
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", ((TGestionaSession)getActivity().getApplication()).getTokenMovil());
                params.put("getemail", txtEmail.getText().toString());
                params.put("getusuario", txtUsuario.getText().toString());

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); //permite que ws sólo se llame una vez y no dos como venía haciendo (el correo se envíaba dos veces)
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
        //queue.add(request);
        progress.setMessage("Cargando....");
        progress.show();
        progress.setCancelable(false);
    }

    String WS_ConfirmaValidacion_Msg;
    boolean WS_ConfirmaValidacion_Estado;

    public void getDataCodValidacion (String cadenaJSON){
        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_ValidaCod_Msg= jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_ValidaCod_Estado= Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());

            //JSONArray jsonArrayListaDatos = jsonArrayData.getJSONObject(0).getJSONArray("ListaDatos");

            /*for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                codActivacion= jsonArrayListaDatos.getJSONObject(0).getString("CODIGO").toString();
            }*/
            txtCodigoActivacion.setText(codActivacion);
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    /*--Solicita código Activación--*/

    //Confirma código de Activación

    private void makeRequestConfirmaCodigoValidacion(){
        final ProgressDialog progress= new ProgressDialog(getActivity());
        //RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO()+ "ValidarCodigoOlvidoClave";
        //String url = "http://192.168.10.183/ws_siac/GenerarPushOlvidoClave";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataConfirmaCodValidacion(response);
                Toast.makeText(getActivity(),WS_ValidaCod_Msg , Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("txtusuario", ((TGestionaSession)getActivity().getApplication()).getUsuario());
                params.put("txtcodigo", codActivacion);

                return params;
            }};
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
        //queue.add(request);
        progress.setMessage("Cargando....");
        progress.show();
        progress.setCancelable(false);
    }

    String WS_ValidaCod_Msg, codActivacion;
    boolean WS_ValidaCod_Estado;

    public void getDataConfirmaCodValidacion (String cadenaJSON){

        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_ValidaCod_Msg= jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_ValidaCod_Estado= Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());

            if (WS_ValidaCod_Estado){
                btnRestablecerContrasena.setText("RESTABLECER");
                tilContrasena_Actual_Nueva.setVisibility(View.VISIBLE);
                tilContrasenaNew.setVisibility(View.VISIBLE);

                tilUsuario.setVisibility(View.GONE);
                tilEmail.setVisibility(View.GONE);
                tilCodValidacion.setVisibility(View.GONE);
                separator.setVisibility(View.GONE);
            }

            JSONArray jsonArrayListaDatos = jsonArrayData.getJSONObject(0).getJSONArray("ListaDatos");
        }
        catch(JSONException je) {
            Toast.makeText(getActivity(),"Error en confirmación de CodActivación: " + je.toString() , Toast.LENGTH_SHORT).show();
        }
    }

    //Cuando la contraseña es caduca
    private void makeRequestRecuperaContrasena()
    {
        final ProgressDialog progress= new ProgressDialog(getActivity());
        //RequestQueue queue = Volley.newRequestQueue(getContext());

        //String url = "http://192.168.10.183/ws_siac/CambioOlvidoClave";
        String url = ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO()+"CambioOlvidoClave";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataRecuperaContrasena(response);
                if (WS_RecuperaContra_Estado){
                    Toast.makeText(getActivity(),WS_RecuperaContra_Msg, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getActivity(),"Bienvenido al Menú Principal: ", Toast.LENGTH_SHORT).show();
                    
                    startActivity(new Intent(getContext(), Mensajeria.class));
                    dismiss();
                }
                else
                {
                    Toast.makeText(getActivity(),"No se pudo restablecer contraseña: " + WS_RecuperaContra_Msg, Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
                //onConnectionFinished();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Volley Error al reestablecer contraseña: " + error.toString() , Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("txt_usuario", ((TGestionaSession)getActivity().getApplication()).getUsuario());
                params.put("clave_nueva", txtContrasena_Actual_Nueva.getText().toString());
                params.put("clave_nueva1", txtConfirmarContrasena.getText().toString());
                params.put("codigo", codActivacion);
                //rellenar
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //queue.add(request);
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
        progress.setMessage("Cargando....");
        progress.show();
        progress.setCancelable(false);
    }

    String WS_RecuperaContra_Msg;
    Boolean WS_RecuperaContra_Estado;
    private void getDataRecuperaContrasena(String cadenaJSON){
        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_RecuperaContra_Msg= jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_RecuperaContra_Estado= Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());
            ((TGestionaSession)getActivity().getApplication()).setIdVendedorChat(jsonArrayData.getJSONObject(0).getString("idvendedor").toString());
            ((TGestionaSession)getActivity().getApplication()).setIdSession(jsonArrayData.getJSONObject(0).getString("idSession").toString());

            JSONArray jsonArrayListaDatos = jsonArrayData.getJSONObject(0).getJSONArray("ListaDatos");
            ((TGestionaSession)getActivity().getApplication()).setNomVendedor( jsonArrayListaDatos.getJSONObject(0).getString("NOMBRES").toString());

            txtCodigoActivacion.setText(codActivacion);
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    //Cambio de clave Caduca
    private void makeRequestCambioClaveCaduca(){
        final ProgressDialog progress= new ProgressDialog(getActivity());
        //RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO()+"CambioClaveCaduco";

        //String url = "http://192.168.10.183/ws_siac/CambioClaveCaduco";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataClaveCaduca(response);
                Toast.makeText(getActivity(), WS_ConfirmaCambioCaduca_Msg.toString() , Toast.LENGTH_LONG).show();
                //Envía a formulario principal
                //Toast.makeText(getActivity(),"Bienvenido al Menú Principal: ", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getContext(), BandejaActivityTest.class));
                //dismiss();
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("txt_usuario", ((TGestionaSession)getActivity().getApplication()).getUsuario());
                params.put("txt_actual", txtContrasenaActual.getText().toString());
                params.put("clave_nueva", txtContrasena_Actual_Nueva.getText().toString());
                params.put("clave_nueva1", txtConfirmarContrasena.getText().toString());
                params.put("txt_email", txtEmailNew.getText().toString().trim());
                //txt_usuario, txt_actual, clave_nueva, clave_nueva1, txtimei
                return params;
            }};
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //queue.add(request);
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
        progress.setMessage("Cargando....");
        progress.show();
        progress.setCancelable(false);
    }

    String WS_ConfirmaCambioCaduca_Msg;
    boolean WS_ConfirmaCambioCaduca_Estado;

    public void getDataClaveCaduca (String cadenaJSON){
        try
        {
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_ConfirmaCambioCaduca_Msg= jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_ConfirmaCambioCaduca_Estado= Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());
            ((TGestionaSession)getActivity().getApplication()).setIdVendedorChat(jsonArrayData.getJSONObject(0).getString("idvendedor").toString());
            ((TGestionaSession)getActivity().getApplication()).setIdSession(jsonArrayData.getJSONObject(0).getString("idSession").toString());
            ((TGestionaSession)getActivity().getApplication()).setNomVendedor(jsonArrayData.getJSONObject(0).getString("NOMBRES").toString());
            JSONArray jsonArrayListaDatos = jsonArrayData.getJSONObject(0).getJSONArray("ListaDatos");

            if (WS_ConfirmaCambioCaduca_Estado){
                startActivity(new Intent(getContext(), LoginTelefonica.class));
            }
        }
        catch(JSONException je) {
            Toast.makeText(getContext(), "No se ha confirmar el cambio de clave caduca.", Toast.LENGTH_LONG);
        }
    }
}