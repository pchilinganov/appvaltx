package chat.atc.tges.tgeschat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

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
import chat.atc.tges.tgeschat.activity.ActivityMenu;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_Generico;
import chat.atc.tges.tgeschat.util.GlobalFunctions;

import static chat.atc.tges.tgeschat.util.GlobalFunctions.newIntent;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

public class FormularioValidacion extends BaseVolleyActivity implements View.OnClickListener {

    TextView tvRazonSocial;
    AutoCompleteTextView actvTipoDoc, actvTipoServicio, actvTipoTransaccion, actvMotivo;
    EditText txtNumeroDoc,txtRazonSocial,txtNumeroAsoc;
    Button btnValidar, btnCancelar;
    String idServicio = "", idTransaccion = "", idMotivo = "",pCodTipoDoc = "";
    SharedPreferences sharedpreferences;
    String idCanal = "";

    int subServicio = 0;

    List<DTO_Generico> listTipoDoc = new ArrayList<>();
    List<DTO_Generico> listServicio = new ArrayList<>();
    List<DTO_Generico> listMotivo = new ArrayList<>();
    List<DTO_Generico> listTransaccion = new ArrayList<>();
    private VolleyRP volley;
    private RequestQueue mRequest;
    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_dialog_tipo_solicitud);
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();
        init();
    }

    public void init() {
        this.sharedpreferences = getSharedPreferences("mta_loged", 0);
        this.idCanal = this.sharedpreferences.getString("idCanal", (String) null);

        txtNumeroDoc = (EditText) findViewById(R.id.txtNumeroDoc);
        txtRazonSocial = (EditText) findViewById(R.id.txtRazonSocial);
        tvRazonSocial = (TextView) findViewById(R.id.tvRazonSocial);
        tvRazonSocial.setVisibility(View.GONE);
        txtRazonSocial.setVisibility(View.GONE);

        txtNumeroAsoc = (EditText) findViewById(R.id.txtNumeroAsoc);

        actvTipoDoc = (AutoCompleteTextView) findViewById(R.id.actvTipoDoc);

        actvMotivo = (AutoCompleteTextView) findViewById(R.id.actvMotivo);
        actvMotivo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getModalTransaccion(listMotivo.get(position).getCodigo());
                mostrarDialogoTipoDoc(listMotivo.get(position).getCodigo());
            }
        });

        actvTipoDoc.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                txtNumeroDoc.setText("");
                pCodTipoDoc = listTipoDoc.get(pos).getCodigo();
                if (pCodTipoDoc.equalsIgnoreCase("1")) {
                    txtNumeroDoc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    tvRazonSocial.setVisibility(View.GONE);
                    txtRazonSocial.setVisibility(View.GONE);
                    txtRazonSocial.setText("");
                } else if (pCodTipoDoc.equalsIgnoreCase("2")) {
                    txtNumeroDoc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                    if(Integer.parseInt(idCanal)==6435 || Integer.parseInt(idCanal)==7749 || Integer.parseInt(idCanal)==7756 || Integer.parseInt(idCanal)==9199 || Integer.parseInt(idCanal)==9245 || Integer.parseInt(idCanal)==9452) {
                        if (Integer.parseInt(idServicio) == 1 || Integer.parseInt(idServicio) == 3) {
                            tvRazonSocial.setVisibility(View.VISIBLE);
                            txtRazonSocial.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    tvRazonSocial.setVisibility(View.GONE);
                    txtRazonSocial.setVisibility(View.GONE);
                    txtRazonSocial.setText("");
                }
            }
        });

        actvTipoServicio = (AutoCompleteTextView) findViewById(R.id.actvTipoServ);
        actvTipoServicio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtNumeroAsoc.setText("");
                idServicio = listServicio.get(position).getCodigo();

                if (idServicio.equalsIgnoreCase("1")) {
                    txtNumeroAsoc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }else{
                    txtNumeroAsoc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
                }


                Log.d("IDSERVICIO",idServicio);

                if( Integer.parseInt(idServicio) == 3 ) {
                    dialogoSubservicio();
                }

                if( idTransaccion != "" ) {
                    getMotivos(idServicio, idTransaccion);
                }
                //if( idServicio )


            }
        });


        actvTipoTransaccion = (AutoCompleteTextView) findViewById(R.id.actvTipoTransaccion);
        actvTipoTransaccion.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                String documento = txtNumeroDoc.getText().toString();
                Log.d("IDTRANSACCION",String.valueOf(pos));

                 if( idServicio == "" ) {
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
                }else if(pCodTipoDoc.equalsIgnoreCase("2") && (Integer.parseInt(idServicio)==1 || Integer.parseInt(idServicio)==3) && (Integer.parseInt(idCanal)==6435 || Integer.parseInt(idCanal)==7749 || Integer.parseInt(idCanal)==7756 || Integer.parseInt(idCanal)==9199 || Integer.parseInt(idCanal)==9245 || Integer.parseInt(idCanal)==9452)) { //RUC
                     if( txtRazonSocial.getText().toString().equals("") ) {
                         Toast.makeText(getApplicationContext(),"Ingrese la razón social",Toast.LENGTH_SHORT).show();
                         actvTipoTransaccion.setText("");
                     }
                     else if (txtNumeroDoc.length() != 0) {
                         String caracter = documento.substring(0, 2);
                         if (caracter.equals("10") || caracter.equals("15") || caracter.equals("16") || caracter.equals("17") || caracter.equals("20")) {
                             idTransaccion = listTransaccion.get(pos).getCodigo();

                             if( Integer.parseInt(idTransaccion) != 5 ) {
                                 if( idServicio != "" ) {
                                     getMotivos(idServicio, idTransaccion);
                                 }
                             } else {
                                 getModalEvidencia();
                             }
                         } else {
                             Toast.makeText(getApplicationContext(), "Ingresar Número de Documento correcto", Toast.LENGTH_SHORT).show();
                             txtNumeroDoc.setText("");
                             actvTipoTransaccion.setText("");
                         }
                     }
                 }
                else {

                    idTransaccion = listTransaccion.get(pos).getCodigo();

                    if( Integer.parseInt(idTransaccion) != 5 ) {
                        if( idServicio != "" ) {
                            getMotivos(idServicio, idTransaccion);
                        }
                    } else {
                        getModalEvidencia();
                    }
                }
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
                    JSONArray JATIpoOperacion, JATipoServicio, JATipoTransaccion, JATipoMotivo;
                    //parse de web service de combos
                    JATIpoOperacion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_idtipodoc");
                    JATipoServicio = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idservicio");
                    JATipoTransaccion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idtipo");
                    // JATipoMotivo =      JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_idtipodoc");

                    for (int i = 0; i < JATIpoOperacion.length(); i++) {
                        listTipoDoc = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(), JATIpoOperacion, actvTipoDoc, "id", "value");
                    }
                    for (int i = 0; i < JATipoTransaccion.length(); i++) {
                        listTransaccion = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(), JATipoTransaccion, actvTipoTransaccion, "id", "value");
                    }
                    for (int i = 0; i < JATipoServicio.length(); i++) {
                        listServicio = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(), JATipoServicio, actvTipoServicio, "id", "value");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                }
            }
        );


    }

    //public String getIMEI(Activity activity) {
        /*TelephonyManager telephonyManager = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return "";
        }
        return telephonyManager.getDeviceId();*/
    //}


    private void dialogoSubservicio() {
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.ly_md_movs_total);

        final RadioButton rbSi, rbNo;

        rbSi = (RadioButton) customDialog.findViewById(R.id.chkoptSi);
        rbNo = (RadioButton) customDialog.findViewById(R.id.chkoptNo);

        rbSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subServicio = 1;
                customDialog.dismiss();
            }
        });

        rbNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subServicio = 2;
                customDialog.dismiss();
            }
        });

        customDialog.show();

    }


    private void mostrarDialogoTipoDoc(String _idMotivo)
    {
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.ly_modal_motivo);

        final TextView txtTituloRec, txtmsj, txtmsjLink, txtmsgCompany;
        final Button btnAceptar;

        txtTituloRec = (TextView) customDialog.findViewById(R.id.txtTituloRec);
        txtmsj = (TextView) customDialog.findViewById(R.id.txtmensaje);
        txtmsjLink = (TextView) customDialog.findViewById(R.id.txtmensajelink);
        txtmsgCompany = (TextView) customDialog.findViewById(R.id.txtmensajeCompany);
        btnAceptar = (Button) customDialog.findViewById(R.id.btnAceptar);

        String WSMethod="";

        WSMethod = "finishTransactionChatBot";

        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();
        /*[17:52] Edgar Ivan Alvarado Osorio
    Parametros: {id_vendedor, imei, latitud, longitud, token, asociado, cb_idtipodoc, txt_document, idtipo,
     idservicio, idmotivo}
*/
        params.put("id_vendedor",((TGestionaSession) getApplication()).getIdVendedorChat());
        params.put("imei","");
        //params.put("latitud","");//no se usa
        //params.put("longitud","");//no se usa
        //params.put("token","");//no se usa
        params.put("asociado",txtNumeroAsoc.getText().toString());
        params.put("cb_idtipodoc",pCodTipoDoc);
        params.put("idtipo",idTransaccion);
        params.put("idservicio",idServicio);
        params.put("idmotivo",_idMotivo);
        params.put("subservicio",String.valueOf(subServicio));



        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {

                        try {
                            JSONArray JOListaDatos = new JSONArray(result);
                            JSONObject jsTransaccion;

                            jsTransaccion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos");

                            txtTituloRec.setText(Html.fromHtml(jsTransaccion.getString("mensaje1")));

                            txtmsj.setText(Html.fromHtml(jsTransaccion.getString("mensaje2")));


                            final String linkUri = jsTransaccion.getString("mensaje3");

                            txtmsjLink.setText(Html.fromHtml(linkUri));
                            txtmsgCompany.setText(Html.fromHtml(jsTransaccion.getString("mensaje4")));

                            txtmsjLink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Uri uri = Uri.parse(linkUri);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        getApplicationContext().startActivity(intent);
                                    }catch (Exception e){
                                        Log.d("URL=>",e.getMessage());
                                    }
                                }
                            });

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }
        );



        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                finish();
            }
        });

        customDialog.show();

    }

    private final int REQUEST_PERMISSION_PHONE_READ = 1;
    private static final int PICK_FILE_REQUEST = 1;

    public void permisoAdjuntar() {
        int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FormularioValidacion.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
        ActivityCompat.requestPermissions(FormularioValidacion.this,
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

    public void modalSitienEvidencia() {

        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);

        final Button btnCancelar,btnAceptar;
        final LinearLayout lyadjuntar;
        final EditText descripcionMensaje;

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.ly_modal_upload_file);

        btnCancelar = (Button) customDialog.findViewById(R.id.btnCancelar);
        btnAceptar  = (Button) customDialog.findViewById(R.id.btnAceptar);
        lyadjuntar  = (LinearLayout) customDialog.findViewById(R.id.ly_adjunta_file);

        descripcionMensaje = (EditText) customDialog.findViewById(R.id.txtdescripcionincidencia);

        lyadjuntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisoAdjuntar();
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtDescripcion = descripcionMensaje.getText().toString();

                if( txtDescripcion.contentEquals("") || selectedFileUri == null ) {
                    modalSinEvidenciaUploadfile();
                    //Toast.makeText(FormularioValidacion.this, "Ingrese la descripción", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*if( selectedFileUri == null ) {
                    Toast.makeText(FormularioValidacion.this, "Seleccione un archivo", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                if (selectedFileUri != null)
                {
                    try {
                        inputStream = getContentResolver().openInputStream(selectedFileUri);
                        BufferedReader br = null;
                        if (inputStream != null) {
                            br = new BufferedReader(new InputStreamReader(inputStream));

                            String line;
                            bytes = convertirTobyteArray(inputStream);
                            subirImagen(txtDescripcion);
                        } else {
                            //subscriber.onError(new InputException("There's something seriously wrong with this file"));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        //subscriber.onError(e);
                    }
                    //subirFichero();
                }

                customDialog.dismiss();
                //finish();


            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalSinEvidenciaUploadfile();
            }
        });

        // customDialog.dismiss();
        customDialog.show();
    }


    public void modalSinEvidenciaUploadfile() {

        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.ly_modal_incidencia);

        final RadioButton chkoptSi, chkoptNo;
        final LinearLayout lycontainer,ly_shotboton;
        final TextView txtNoevidencia, txtTitleEviden;
        final Button btnAceptar;

        chkoptNo = (RadioButton) customDialog.findViewById(R.id.chkoptNo);
        chkoptSi = (RadioButton) customDialog.findViewById(R.id.chkoptSi);
        lycontainer = (LinearLayout) customDialog.findViewById(R.id.ly_container_evidencia);
        txtNoevidencia = (TextView) customDialog.findViewById(R.id.txtNoevidencia);
        txtTitleEviden = (TextView) customDialog.findViewById(R.id.txtTitleEviden);
        ly_shotboton = (LinearLayout) customDialog.findViewById(R.id.ly_shotboton);
        btnAceptar = (Button) customDialog.findViewById(R.id.btnAceptar);

        txtNoevidencia.setVisibility(View.VISIBLE);
        lycontainer.setBackgroundColor(Color.parseColor("#FFA692"));
        txtTitleEviden.setVisibility(View.GONE);
        chkoptNo.setVisibility(View.GONE);
        chkoptSi.setVisibility(View.GONE);
        ly_shotboton.setVisibility(View.VISIBLE);
        chkoptSi.setChecked(false);

        String WSMethod="";

        WSMethod = "finishTransactionNoEvidencia";

        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("id_vendedor",((TGestionaSession) getApplication()).getIdVendedorChat());
        params.put("imei","");
        //params.put("latitud","");//no se usa
        //params.put("longitud","");//no se usa
        //params.put("token","");//no se usa
        params.put("asociado",txtNumeroAsoc.getText().toString());
        params.put("cb_idtipodoc",pCodTipoDoc);
        params.put("idtipo",idTransaccion);
        params.put("idservicio",idServicio);
        params.put("subservicio",String.valueOf(subServicio));



        final String json = GlobalFunctions.callVolleyStringRequest(getApplicationContext(),params,URL,volley,mRequest, new ServerCallback() {
                @Override
                public void onSuccess(String result) {

                    try {
                        JSONArray JOListaDatos = new JSONArray(result);

                        JSONObject jsTransaccion;

                        jsTransaccion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos");

                        txtNoevidencia.setText(jsTransaccion.getString("mensaje1")+"\n\n"+jsTransaccion.getString("mensaje2")+"\n\n"+jsTransaccion.getString("mensaje3"));

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        );

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                finish();
            }
        });

        customDialog.show();

    }


    public void getModalEvidencia() {
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.setContentView(R.layout.ly_modal_incidencia);

        final RadioButton chkoptSi, chkoptNo;
        final LinearLayout lycontainer,ly_shotboton;
        final TextView txtNoevidencia, txtTitleEviden;
        final Button btnAceptar;

        chkoptNo = (RadioButton) customDialog.findViewById(R.id.chkoptNo);
        chkoptSi = (RadioButton) customDialog.findViewById(R.id.chkoptSi);
        lycontainer = (LinearLayout) customDialog.findViewById(R.id.ly_container_evidencia);
        txtNoevidencia = (TextView) customDialog.findViewById(R.id.txtNoevidencia);
        txtTitleEviden = (TextView) customDialog.findViewById(R.id.txtTitleEviden);
        ly_shotboton = (LinearLayout) customDialog.findViewById(R.id.ly_shotboton);
        btnAceptar = (Button) customDialog.findViewById(R.id.btnAceptar);

        chkoptSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtNoevidencia.setVisibility(View.GONE);
                lycontainer.setBackgroundColor(Color.WHITE);
                chkoptNo.setChecked(false);
                customDialog.dismiss();
                modalSitienEvidencia();
            }
        });

        chkoptNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String WSMethod="";

                WSMethod = "finishTransactionNoEvidencia";

                String URL = URL_DESARROLLO + WSMethod;

                Map<String, String> params = new HashMap<String, String>();

                params.put("id_vendedor",((TGestionaSession) getApplication()).getIdVendedorChat());
                params.put("imei","");
                //params.put("latitud","");//no se usa
                //params.put("longitud","");//no se usa
                //params.put("token","");//no se usa
                params.put("asociado",txtNumeroAsoc.getText().toString());
                params.put("cb_idtipodoc",pCodTipoDoc);
                params.put("idtipo",idTransaccion);
                params.put("idservicio",idServicio);
                params.put("subservicio",String.valueOf(subServicio));



                final String json = GlobalFunctions.callVolleyStringRequest(getApplicationContext(),params,URL,volley,mRequest, new ServerCallback() {
                            @Override
                            public void onSuccess(String result) {

                        try {
                            JSONArray JOListaDatos = new JSONArray(result);

                            JSONObject jsTransaccion;

                            jsTransaccion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos");

                            txtNoevidencia.setVisibility(View.VISIBLE);
                            txtNoevidencia.setText(jsTransaccion.getString("mensaje1")+"\n\n"+jsTransaccion.getString("mensaje2")+"\n\n"+jsTransaccion.getString("mensaje3"));

                            lycontainer.setBackgroundColor(Color.parseColor("#FFA692"));
                            txtTitleEviden.setVisibility(View.GONE);
                            chkoptNo.setVisibility(View.GONE);
                            chkoptSi.setVisibility(View.GONE);
                            ly_shotboton.setVisibility(View.VISIBLE);
                            chkoptSi.setChecked(false);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        }
                    }
                );
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                finish();
            }
        });

        customDialog.show();
    }


    public void getMotivos(String idserv, String idtransac) {

        String WSMethod="";

        WSMethod = "getMotivo";

        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("idtipo",idtransac);
        params.put("idservicio",idserv);
        listMotivo.clear();
        actvMotivo.clearListSelection();
        actvMotivo.setListSelection(0);
        actvMotivo.setText("Seleccione");

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                @Override
                public void onSuccess(String result) {
                    //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                    try {
                        JSONArray JOListaDatos = new JSONArray(result);
                        JSONArray JATIpoOperacion = JOListaDatos.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idmotivo");

                        for (int i = 0;i < JATIpoOperacion.length(); i++){
                            listMotivo = GlobalFunctions.fillActvWithTextFromJSONArray(getApplicationContext(),JATIpoOperacion, actvMotivo,"id", "value");
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

    public static byte[] convertirTobyteArray(InputStream is) throws IOException {

        byte[] bytes = null;

        if(is != null){
            bytes = new byte[is.available()];

            is.read(bytes);
        }

        return bytes;

    }

    private void subirImagen(final String txtDescripcion)
    {
        String url = URL_DESARROLLO + "recibirChatIncidencia";

        dialog = ProgressDialog.show(FormularioValidacion.this, "", "Subiendo archivo...", true);
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

                            ((TGestionaSession)getApplication()).setChat_id(Integer.parseInt(result.getString("id_chat")));
                            Intent intent = new Intent( FormularioValidacion.this, Mensajeria.class );

                            //intent.putExtra( "id_chat", result.getString("id_chat") );
                            startActivity( intent );

                            /*


                            String status = result.getString("Estado");
                            String message = result.getString("Msg");
                            //urlAdjuntar = result.getString("url");
                            //horaAdjuntar = result.getString("hora");

                            if (status.equalsIgnoreCase("true")) {
                                // tell everybody you have succed upload image and post strings
                                Toast.makeText(FormularioValidacion.this,"Mensaje : " + message, Toast.LENGTH_SHORT);
                                //CreateMensaje(fileNameUpload, horaAdjuntar, 1, ((TGestionaSession) getApplication()).getNomVendedor(), "file", urlAdjuntar);
                            } else {
                                Toast.makeText(FormularioValidacion.this,"Mensaje : " + message, Toast.LENGTH_SHORT);
                            }
                            */
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

                        //Toast.makeText(Mensajeria.this,"Mensaje : " + message, Toast.LENGTH_SHORT);
                        //Toast.makeText(Mensajeria.this,"Status : " + status, Toast.LENGTH_SHORT);
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

                String idsesion = ((TGestionaSession)getApplication()).getIdSession();
                String idvendedorChat = ((TGestionaSession)getApplication()).getIdVendedorChat();
                Log.d("IDSESSIONAPP",idsesion);
                Log.d("IDVENDEDORAPP",idvendedorChat);

                params.put("idvendedor", idvendedorChat);
                params.put("imei","0");
                params.put("latitud","0");
                params.put("longitud","0");

                params.put("token", ((TGestionaSession)getApplication()).getTokenMovil());
                params.put("mensaje",txtDescripcion);
                params.put("id_session",idsesion);
                params.put("idservicio", idServicio);
                params.put("asociado",txtNumeroAsoc.getText().toString());
                params.put("cb_idtipodoc", pCodTipoDoc);
                params.put("txt_document",txtNumeroDoc.getText().toString());
                params.put("txt_razonsocial",txtRazonSocial.getText().toString());

                params.put("idtipo", idTransaccion );
                params.put("subservicio",String.valueOf(subServicio));
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


    @Override
    public void onClick(View v) {

    }
}
