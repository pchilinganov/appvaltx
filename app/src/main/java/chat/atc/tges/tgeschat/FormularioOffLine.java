package chat.atc.tges.tgeschat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.VolleyMultiPart.VolleyMultipartRequest;
import chat.atc.tges.tgeschat.VolleyMultiPart.VolleySingleton;
import chat.atc.tges.tgeschat.activity.ActivityConsultaTicket;
import chat.atc.tges.tgeschat.activity.ActivityMenu;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_Generico;
import chat.atc.tges.tgeschat.model.DTO_ticket;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.util.GlobalFunctions.newIntent;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

public class FormularioOffLine extends BaseVolleyActivity {
    AutoCompleteTextView actvTipoDoc, actvDepartamento, actvProvincia, actvDistrito, actvTipoVia,actvTipoComplejoVivienda;
    EditText txtNumeroDoc,txtNumeroServicio,txtNombreVia,txtNumero,txtPiso,txtNroDepartamento,txtComplejoVivienda,txtBloqueManzana,txtLote;
    LinearLayout lyadjuntar;
    String idDepartamento="",idProvincia="", idDistrito="", idTipoVia="", idComplejoVivienda="";
    Button btnAceptar, btnCancelar;

    String pCodTipoDoc = "";
    String token="", cbo_type_document="",cbo_departamento="",cbo_provincia="",cbo_tipo_via="",cbo_tipo_complejo="",cbo_distrito="",txt_number_document="",txt_number_servicio="",
            txt_direccion_correcta="",txt_bloque_manzana="",txt_lote="",txt_complejo_vivienda="",txt_nro_departamento="",txt_piso="",txt_numero_via="",txt_nombre_via="",id_session="",id_vendedor="";

    List<DTO_Generico> listTipoDoc = new ArrayList<>();
    List<DTO_Generico> listTipoVia = new ArrayList<>();
    List<DTO_Generico> listTipoComplejoVivienda = new ArrayList<>();
    List<DTO_Generico> listDepartamento = new ArrayList<>();
    List<DTO_Generico> listProvincia = new ArrayList<>();
    List<DTO_Generico> listDistrito = new ArrayList<>();
    private VolleyRP volley;
    private RequestQueue mRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_dialog_formulario_offline);
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();
        init();

        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        this.btnCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Cancelar();
            }
        });

        /*this.txtNumeroDoc.setText( ((TGestionaSession) getApplication()).getTokenMovil());
        this.txtNumeroServicio.setText( ((TGestionaSession) getApplication()).getIdSession());
        this.txtDireccionCorrecta.setText( ((TGestionaSession) getApplication()).getIdVendedorChat());*/

    }

    public void Cancelar(){
        actvTipoDoc.setText("");
        actvDepartamento.setText("");
        //actvProvincia.setText("");
        actvProvincia.clearListSelection();
        actvProvincia.setListSelection(0);
        actvProvincia.setText("Seleccione");
        //actvDistrito.setText("");
        actvDistrito.clearListSelection();
        actvDistrito.setListSelection(0);
        actvDistrito.setText("Seleccione");
        actvTipoVia.setText("");
        actvTipoComplejoVivienda.setText("");
        txtNumeroDoc.setText("");
        txtNumeroServicio.setText("");
        txtNombreVia.setText("");
        txtNumero.setText("");
        txtPiso.setText("");
        txtNroDepartamento.setText("");
        txtComplejoVivienda.setText("");
        txtBloqueManzana.setText("");
        txtLote.setText("");
    }

    public void init() {
        txtNumeroDoc = (EditText) findViewById(R.id.txtNumeroDoc);
        txtNumeroServicio = (EditText) findViewById(R.id.txtNumeroServicio);
        txtNombreVia = (EditText) findViewById(R.id.txtNombreVia);
        txtNumero = (EditText) findViewById(R.id.txtNumero);
        txtPiso = (EditText) findViewById(R.id.txtPiso);
        txtNroDepartamento = (EditText) findViewById(R.id.txtNroDepartamento);
        txtComplejoVivienda = (EditText) findViewById(R.id.txtComplejoVivienda);
        txtBloqueManzana = (EditText) findViewById(R.id.txtBloqueManzana);
        txtLote = (EditText) findViewById(R.id.txtLote);
        btnAceptar = (Button) findViewById(R.id.btnAceptar);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source,
                                       int start,
                                       int end,
                                       Spanned dest,
                                       int dstart,
                                       int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))
                            && !Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        txtNombreVia.setFilters(new InputFilter[]{filter});
        txtNumero.setFilters(new InputFilter[]{filter});
        txtPiso.setFilters(new InputFilter[]{filter});
        txtNroDepartamento.setFilters(new InputFilter[]{filter});
        txtComplejoVivienda.setFilters(new InputFilter[]{filter});
        txtBloqueManzana.setFilters(new InputFilter[]{filter});
        txtLote.setFilters(new InputFilter[]{filter});

        actvTipoDoc = (AutoCompleteTextView) findViewById(R.id.actvTipoDoc);
        actvDepartamento = (AutoCompleteTextView) findViewById(R.id.actvDepartamento);
        actvProvincia = (AutoCompleteTextView) findViewById(R.id.actvProvincia);
        actvDistrito = (AutoCompleteTextView) findViewById(R.id.actvDistrito);
        actvTipoVia = (AutoCompleteTextView) findViewById(R.id.actvTipoVia);
        actvTipoComplejoVivienda = (AutoCompleteTextView) findViewById(R.id.actvTipoComplejoVivienda);
        lyadjuntar  = (LinearLayout)  findViewById(R.id.ly_adjunta_file);

        actvTipoDoc.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                txtNumeroDoc.setText("");
                pCodTipoDoc = listTipoDoc.get(pos).getCodigo();
                if (pCodTipoDoc.equalsIgnoreCase("1")) {
                    txtNumeroDoc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                } else if (pCodTipoDoc.equalsIgnoreCase("2")) {
                    txtNumeroDoc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                }
            }
        });

        actvDepartamento.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                //String documento = txtNumeroDoc.getText().toString();
                Log.d("IDDEPARTAMENTO",String.valueOf(pos));

                /*if( idServicio == "" ) {
                    Toast.makeText(getApplicationContext(),"Seleccione un servicio",Toast.LENGTH_SHORT).show();
                    actvTipoTransaccion.setText("");
                }
                else if( pCodTipoDoc == "" ) {
                    Toast.makeText(getApplicationContext(),"Seleccione un tipo de documento",Toast.LENGTH_SHORT).show();
                    actvTipoTransaccion.setText("");
                }
                else if( documento.contentEquals("")) {
                    Toast.makeText(getApplicationContext(),"Ingrese el número de documento",Toast.LENGTH_SHORT).show();
                    actvTipoTransaccion.setText("");
                } else if( txtNumeroAsoc.getText().toString().equals("") ) {
                    Toast.makeText(getApplicationContext(),"Ingrese el número de asociado",Toast.LENGTH_SHORT).show();
                    actvTipoTransaccion.setText("");
                } else {*/

                idDepartamento = listDepartamento.get(pos).getCodigo();
                getProvincia(idDepartamento);

                listDistrito.clear();
                actvDistrito.clearListSelection();
                actvDistrito.setListSelection(0);
                actvDistrito.setText("Seleccione");

                idProvincia= "";
                idDistrito = "";

                /*}*/
            }
        });

        actvProvincia.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                //String documento = txtNumeroDoc.getText().toString();
                Log.d("IDPROVINCIA",String.valueOf(pos));


                idProvincia = listProvincia.get(pos).getCodigo();
                getDistrito(idDepartamento,idProvincia);


            }
        });

        actvDistrito.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                //String documento = txtNumeroDoc.getText().toString();
                Log.d("IDDISTRITO",String.valueOf(pos));


                idDistrito = listDistrito.get(pos).getCodigo();

            }
        });

        actvTipoVia.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                //String documento = txtNumeroDoc.getText().toString();
                Log.d("IDTIPOVIA",String.valueOf(pos));


                idTipoVia = listTipoVia.get(pos).getCodigo();

            }
        });

        actvTipoComplejoVivienda.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                //String documento = txtNumeroDoc.getText().toString();
                Log.d("IDCOMPLEJOVIVIENDA",String.valueOf(pos));


                idComplejoVivienda = listTipoComplejoVivienda.get(pos).getCodigo();

            }
        });


        this.btnAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                registrarFormulario();
            }
        });

        lyadjuntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisoAdjuntar();
            }
        });


        String WSMethod = "";
        WSMethod = "showTipoDocChat";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        final String json = GlobalFunctions.callVolleyStringRequest(this, params, URL, volley, mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                        try {
                            JSONArray JOListaDatos = new JSONArray(result);
                            JSONArray JATIpoOperacion,JATipoVia,JATipoComplejoVivienda,JADepartamento; // JATipoServicio, JATipoTransaccion, JATipoMotivo;
                            //parse de web service de combos
                            JATIpoOperacion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_idtipodoc");
                            JATipoVia = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idtipovia");
                            JATipoComplejoVivienda = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idtipocomplejovivienda");
                            JADepartamento = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("iddepartamento");
                            // JATipoMotivo =      JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_idtipodoc");

                            for (int i = 0; i < JATIpoOperacion.length(); i++) {
                                listTipoDoc = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(), JATIpoOperacion, actvTipoDoc, "id", "value");
                            }
                            for (int i = 0; i < JATipoVia.length(); i++) {
                                listTipoVia = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(), JATipoVia, actvTipoVia, "id", "value");
                            }
                            for (int i = 0; i < JATipoComplejoVivienda.length(); i++) {
                                listTipoComplejoVivienda = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(), JATipoComplejoVivienda, actvTipoComplejoVivienda, "id", "value");
                            }
                            for (int i = 0; i < JADepartamento.length(); i++) {
                                listDepartamento = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(), JADepartamento, actvDepartamento, "id", "value");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );


    }

    private final int REQUEST_PERMISSION_PHONE_READ = 1;
    private static final int PICK_FILE_REQUEST = 1;

    public void permisoAdjuntar() {
        int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FormularioOffLine.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation("Permiso necesario", "Para continuar con el envío de archivos, otorgar permisos de lectura.", Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_READ);
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_READ);
            }
        } else {
            showFileChooser222();
        }
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

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(FormularioOffLine.this,
                new String[]{permissionName}, permissionRequestCode);
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

    public void onClick(View view) {
        if (this.btnAceptar == view) {
            registrarFormulario();
        }
    }

    public void getProvincia(String idDepartamento) {

        String WSMethod="";

        WSMethod = "showTipoProvincia";

        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("cb_iddepartamento",idDepartamento);
        listProvincia.clear();
        actvProvincia.clearListSelection();
        actvProvincia.setListSelection(0);
        actvProvincia.setText("Seleccione");

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                        try {
                            JSONArray JOListaDatos = new JSONArray(result);
                            JSONArray JATIpoOperacion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idprovincia");

                            for (int i = 0;i < JATIpoOperacion.length(); i++){
                                listProvincia = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(),JATIpoOperacion, actvProvincia,"id", "value");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }
                }
        );
    }

    public void getDistrito(String idDepartamento, String idProvincia) {

        String WSMethod="";

        WSMethod = "showTipoDistrito";

        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("cb_iddepartamento",idDepartamento);
        params.put("cb_idprovincia",idProvincia);
        listDistrito.clear();
        actvDistrito.clearListSelection();
        actvDistrito.setListSelection(0);
        actvDistrito.setText("Seleccione");

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                        try {
                            JSONArray JOListaDatos = new JSONArray(result);
                            JSONArray JATIpoOperacion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("iddistrito");

                            for (int i = 0;i < JATIpoOperacion.length(); i++){
                                listDistrito = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(),JATIpoOperacion, actvDistrito,"id", "value");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }
                }
        );
    }

    private void registrarFormulario() {

        if (selectedFileUri != null)
        {
            try {
                inputStream = getContentResolver().openInputStream(selectedFileUri);
                BufferedReader br = null;
                if (inputStream != null) {
                    br = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    bytes = convertirTobyteArray(inputStream);

                    //cbo_type_document = actvTipoDoc.getText().toString().trim();
                    //cbo_type_document = String.valueOf(getItem().getId());
                    cbo_type_document = pCodTipoDoc;

                    //cbo_departamento = actvDepartamento.getText().toString().trim();
                    cbo_departamento = idDepartamento;

                    //cbo_provincia = actvProvincia.getText().toString().trim();
                    cbo_provincia = idProvincia;

                    //cbo_distrito = actvDistrito.getText().toString().trim();
                    cbo_distrito = idDistrito;

                    //cbo_tipo_via = actvTipoVia.getText().toString().trim();
                    cbo_tipo_via = idTipoVia;

                    //cbo_tipo_complejo = actvTipoComplejoVivienda.getText().toString().trim();
                    cbo_tipo_complejo = idComplejoVivienda;

                    txt_number_document = txtNumeroDoc.getText().toString().trim();
                    txt_number_servicio = txtNumeroServicio.getText().toString().trim();
                    txt_nombre_via = txtNombreVia.getText().toString().trim();
                    txt_numero_via = txtNumero.getText().toString().trim();
                    txt_piso = txtPiso.getText().toString().trim();
                    txt_nro_departamento = txtNroDepartamento.getText().toString().trim();
                    txt_complejo_vivienda = txtComplejoVivienda.getText().toString().trim();
                    txt_bloque_manzana = txtBloqueManzana.getText().toString().trim();
                    txt_lote = txtLote.getText().toString().trim();

                    token = ((TGestionaSession) getApplication()).getTokenMovil();
                    id_session = ((TGestionaSession) getApplication()).getIdSession();
                    id_vendedor = ((TGestionaSession) getApplication()).getIdVendedorChat();

                    final  ProgressDialog progressDialog = new ProgressDialog(this);

                    /*if(txt_bloque_manzana!="" && txt_nro_departamento==""){
                        txt_piso="01";
                    }
                    if(txt_piso=="" && txt_nro_departamento!=""){
                        int contadorNumero  = 0;
                        for(int i = 0; i <= txt_nro_departamento.length() -1 ;i++) {
                            if(esNumero(txt_nro_departamento.charAt(i))) {
                                contadorNumero++;
                            }
                        }
                        if(contadorNumero>=1 && contadorNumero<=3){
                            txt_piso = String.valueOf(txt_nro_departamento.charAt(0));
                        }else{
                            txt_piso = txt_nro_departamento.substring(0,2);
                        }
                    }*/

                    if(cbo_type_document.isEmpty()){
                        //actvTipoDoc.setError("Seleccionar Tipo Documento");
                        Toast toastDocumento = Toast.makeText(getApplicationContext(), "Seleccionar Tipo Documento", Toast.LENGTH_SHORT);
                        toastDocumento.show();
                    }else if(cbo_departamento.isEmpty()){
                        //actvDepartamento.setError("Seleccionar Departamento");
                        Toast toastDepartamento = Toast.makeText(getApplicationContext(), "Seleccionar Departamento", Toast.LENGTH_SHORT);
                        toastDepartamento.show();
                    }else if(cbo_provincia.isEmpty() || cbo_provincia==""){
                        //actvProvincia.setError("Seleccionar Provincia");
                        Toast toastProvincia = Toast.makeText(getApplicationContext(), "Seleccionar Provincia", Toast.LENGTH_SHORT);
                        toastProvincia.show();
                    }else if(cbo_distrito.isEmpty() || cbo_distrito==""){
                        //actvDistrito.setError("Seleccionar Distrito");
                        Toast toastDistrito = Toast.makeText(getApplicationContext(), "Seleccionar Distrito", Toast.LENGTH_SHORT);
                        toastDistrito.show();

                    /*}else if(cbo_tipo_via.isEmpty()){
                        actvTipoVia.setError("Seleccionar");
                    }else if(cbo_tipo_complejo.isEmpty()){
                        actvTipoComplejoVivienda.setError("Seleccionar");
                    }else if(txt_number_document.isEmpty()){
                        txtNumeroDoc.setError("Completar");
                    }else if(txt_number_servicio.isEmpty()){
                        txtNumeroServicio.setError("Completar");
                    }else if(txt_direccion_correcta.isEmpty()){
                        txtDireccionCorrecta.setError("Completar");
                    }else if(txt_nombre_via.isEmpty()){
                        txtNombreVia.setError("Completar");
                    }else if(txt_numero_via.isEmpty()){
                        txtNumero.setError("Completar");
                    }/*else if(txt_piso.isEmpty()){
                        txtPiso.setError("Completar");
                    }else if(txt_nro_departamento.isEmpty()){
                        txtNroDepartamento.setError("Completar");
                    }*//*else if(txt_complejo_vivienda.isEmpty()){
                        txtComplejoVivienda.setError("Completar");
                    }else if(txt_bloque_manzana.isEmpty()){
                        txtBloqueManzana.setError("Completar");
                    }else if(txt_lote.isEmpty()){
                        txtLote.setError("Completar");*/
                    }else {
                        progressDialog.show();
                        String WSMethod = "";
                        WSMethod = "saveform";
                        String URL = URL_DESARROLLO + WSMethod;
                        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                                new Response.Listener<NetworkResponse>() {
                                    @Override
                                    public void onResponse(NetworkResponse response) {
                                        progressDialog.dismiss();
                                        String resultResponse = new String(response.data);
                                        try {
                                            JSONObject jsonObject = new JSONObject(resultResponse);
                                            String msg = jsonObject.getString("msg");
                                            //Toast.makeText(FormularioOffLine.this, msg, Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            Intent i = new Intent(FormularioOffLine.this, PopupFormularioOffLine.class);
                                            i.putExtra("ticket", msg);
                                            startActivity(i);
                                            actvTipoDoc.setText("");
                                            txtNumeroDoc.setText("");
                                            txtNumeroServicio.setText("");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(FormularioOffLine.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("id_session", id_session);
                                params.put("id_vendedor", id_vendedor);
                                params.put("token", token);
                                params.put("cbo_type_document", cbo_type_document);
                                params.put("cbo_departamento", cbo_departamento);
                                params.put("cbo_provincia", cbo_provincia);
                                params.put("cbo_distrito", cbo_distrito);
                                params.put("cbo_tipo_via", cbo_tipo_via);
                                params.put("cbo_tipo_complejo", cbo_tipo_complejo);
                                params.put("txt_number_document", txt_number_document);
                                params.put("txt_number_servicio", txt_number_servicio);
                                params.put("txt_direccion_correcta", txt_direccion_correcta);
                                params.put("txt_nombre_via", txt_nombre_via);
                                params.put("txt_numero_via", txt_numero_via);
                                params.put("txt_piso", txt_piso);
                                params.put("txt_nro_departamento", txt_nro_departamento);
                                params.put("txt_complejo_vivienda", txt_complejo_vivienda);
                                params.put("txt_bloque_manzana", txt_bloque_manzana);
                                params.put("txt_lote", txt_lote);
                                //params.put("file_evidencia_error", );
                                return params;
                            }

                            @Override
                            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                                // file name could found file base or direct access from real path
                                // for now just get bitmap data from ImageView
                                params.put("file_evidencia_error", new VolleyMultipartRequest.DataPart(fileNameUpload, bytes, getMimeType(fileNameUpload)));
                                return params;
                            }
                        };
            /*RequestQueue requestQueue = Volley.newRequestQueue(FormularioOffLine.this);
            requestQueue.add(request);*/
                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
                    }
                } else {
                    //subscriber.onError(new InputException("There's something seriously wrong with this file"));
                }

            } catch (IOException e) {
                e.printStackTrace();
                //subscriber.onError(e);
            }
            //subirFichero();
        }else{
            Toast toast = Toast.makeText(this, "Adjuntar evidencia.", Toast.LENGTH_SHORT);
            toast.show();
        }


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

    public static byte[] convertirTobyteArray(InputStream is) throws IOException {

        byte[] bytes = null;

        if(is != null){
            bytes = new byte[is.available()];

            is.read(bytes);
        }

        return bytes;

    }

    /*private static boolean esNumero(char numero) {
        if (numero >= 0 && numero <= 9) {
            return true;
        }
        return false;
    }*/

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


            }
        }
    }

}