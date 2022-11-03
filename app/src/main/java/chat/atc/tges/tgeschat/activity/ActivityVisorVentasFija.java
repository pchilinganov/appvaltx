package chat.atc.tges.tgeschat.activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.FragmentManager;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.adapter.RvInHouseAdapter;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.fragment.FragmentInHouse;
import chat.atc.tges.tgeschat.fragment.FragmentVisorAtis;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_Departamento;
import chat.atc.tges.tgeschat.model.DTO_Distrito;
import chat.atc.tges.tgeschat.model.DTO_Generico;
import chat.atc.tges.tgeschat.model.DTO_INHouse;
import chat.atc.tges.tgeschat.model.DTO_OperacionComercial;
import chat.atc.tges.tgeschat.model.DTO_Producto;
import chat.atc.tges.tgeschat.model.DTO_Provincia;
import chat.atc.tges.tgeschat.model.DTO_Subproducto;
import chat.atc.tges.tgeschat.model.DTO_TipoProducto;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.LoginTelefonica.uri;
import static chat.atc.tges.tgeschat.LoginTelefonica.vistaIncidenciaMasiva;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.fillActvFromJSONArray;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.limpiarShared;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.newIntent;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

public class ActivityVisorVentasFija extends BaseVolleyActivity implements OnClickListener {


    //manejadores de fragments

    public static FragmentManager fragmentManager; //Manejador de fragmentos
    //variables de GUI
    AutoCompleteTextView cb_tipoIncidencia, txt_cdepartamento_visor, txt_cprovincia_visor, txt_cdistrito_visor, actvCampania, cb_tratamiento_2, cb_idcategoria_2, cb_idproducto_2, cb_idprograma_2, cb_ctipo_doc_2;
    Button  btnVerificarDocumento;
    static EditText telefono_migrar_2, num_doc_cli_2;
    TextView tvTelfMigra, tvHora;

    int maxLengthofEditText=-1;

    //variables de red
    private VolleyRP volley;
    private RequestQueue mRequest;

    SharedPreferences sharedpreferences;


    LinearLayout llMain;

    //Listas y variables de combos dependientes
    public static List<DTO_Departamento> listaDepartamentos=new ArrayList<>(); //Array que contiene el ubigeo completo
    List <String> sListaDepartamentos = new ArrayList<>(); // Array que contiene los departamentos para combo
    List <String> sListaProvincias = new ArrayList<>(); // Array que contiene los provincias para combo
    List <String> sListaDistritos = new ArrayList<>(); // Array que contiene los provincias para combo
    public static int posDepartamento=-1, posProvincia=-1, posDistrito;

    List<DTO_Generico> listOperacionComercial = new ArrayList<>(); //Array que contiene Operacion comercial completa
    List<DTO_OperacionComercial> listCampania = new ArrayList<>();
    List<DTO_Generico> listaTipoDoc = new ArrayList<>();
    List<DTO_Generico> listTipoIncidencia = new ArrayList<>();
    List<DTO_INHouse> listaINHOUSE = new  ArrayList<>();

    List<String> sListaOperacionComercial = new ArrayList<>();
    List<String> sListaCampania = new ArrayList<>();
    List<String> sListaTipoProducto = new ArrayList<>();
    List<String> sListaProducto = new ArrayList<>();
    List<String> sListaSubProducto = new ArrayList<>();
    int posCampania=-1, posOpComercial=-1, posTipoPro =-1, posPro=-1;

    JSONArray jATipoIncidencia, jATipoDocVisor, jAOperacionComercial;
    //Fin de combos dependientes

    //parametros de ws de visor
    public static String pCodTipoDoc="", pCodDepartamentoWS="", pCodProvinciaWS="", pCodDistritoWS="", pCodCampaniaWS ="", pCodOperComercialWS="", pCodTipoProdWS="", pCodProductoWS="", pCodTipoIncidencia="",pCodSubProductoWS="";

    //Dialog
    Dialog customDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_activity_visor_ventas_fija);

        //actualiza token actual del dispositivo
        ((TGestionaSession)getApplication()).setTokenMovil(FirebaseInstanceId.getInstance().getToken());

        //inicialiazando variables de red
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        //dialog
        //Progress
        customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(false); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_loading); //establecemos el contenido de nuestro dialog

        loadPreferences();

        //parsing GUI
        cb_ctipo_doc_2 = (AutoCompleteTextView) findViewById(R.id.cb_ctipo_doc_2);
        txt_cdepartamento_visor = (AutoCompleteTextView) findViewById(R.id.txt_cdepartamento_visor);
        txt_cprovincia_visor = (AutoCompleteTextView) findViewById(R.id.txt_cprovincia_visor);
        txt_cdistrito_visor = (AutoCompleteTextView) findViewById(R.id.txt_cdistrito_visor);
        //actvCampania = (AutoCompleteTextView) findViewById(R.id.cb_idcuenta);
        cb_tratamiento_2 = (AutoCompleteTextView) findViewById(R.id.cb_tratamiento_2);
        cb_idcategoria_2 = (AutoCompleteTextView) findViewById(R.id.cb_idcategoria_2);
        cb_idproducto_2 = (AutoCompleteTextView) findViewById(R.id.cb_idproducto_2);
        cb_idprograma_2 = (AutoCompleteTextView) findViewById(R.id.cb_idprograma_2);
        cb_tipoIncidencia  = (AutoCompleteTextView) findViewById(R.id.cb_tipoIncidencia);
        telefono_migrar_2 = (EditText) findViewById(R.id.telefono_migrar_2);
        num_doc_cli_2 = (EditText) findViewById(R.id.num_doc_cli_2);
        tvTelfMigra = (TextView) findViewById(R.id.tvTelfMigra);
        tvHora  = (TextView) findViewById(R.id.tvHoraATIS);
        btnVerificarDocumento = (Button) findViewById(R.id.btnVerificarDocumento);
        btnVerificarDocumento.setOnClickListener(this);

        llMain = (LinearLayout) findViewById(R.id.llMain);

        fragmentManager = getFragmentManager(); //inicializa manejador de fragmentos

//        cb_tratamiento_2.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
//            @Override
//            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                pCodOperComercialWS = listOperacionComercial.get(pos).getCodigo();
//                String item = String.valueOf(pos);
//                if (pCodOperComercialWS.equalsIgnoreCase("ALT")) {
//                    telefono_migrar_2.setText("");
//                    tvTelfMigra.setVisibility(View.GONE);
//                    telefono_migrar_2.setVisibility(View.GONE);
//                }else{
//                    tvTelfMigra.setVisibility(View.VISIBLE);
//                    telefono_migrar_2.setVisibility(View.VISIBLE);
//                }
//                //Toast.makeText(getApplicationContext(), "valor: " + pCodOperComercialWS, Toast.LENGTH_LONG).show();
//            }
//        });

        //Eventos de interacción al momento de seleccionar combos dependientes
        cb_ctipo_doc_2.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                pCodTipoDoc = listaTipoDoc.get(pos).getCodigo();
                num_doc_cli_2.setText("");

                if (pCodTipoDoc.equalsIgnoreCase("DNI")){
                    num_doc_cli_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    maxLengthofEditText = 8;
                    num_doc_cli_2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthofEditText)});
                } else if (pCodTipoDoc.equalsIgnoreCase("RUC")){
                    num_doc_cli_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    maxLengthofEditText = 11;
                    num_doc_cli_2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthofEditText)});
                } else if (pCodTipoDoc.equalsIgnoreCase("CE")){
                    num_doc_cli_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    maxLengthofEditText = 12;
                    num_doc_cli_2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthofEditText)});
                } else if (pCodTipoDoc.equalsIgnoreCase("PASAPORTE")){
                    num_doc_cli_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    maxLengthofEditText = 12;
                    num_doc_cli_2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthofEditText)});
                } else if (pCodTipoDoc.equalsIgnoreCase("OTROS EXTRANJEROS - AUT. SNM")){
                    num_doc_cli_2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    maxLengthofEditText = 15;
                    num_doc_cli_2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthofEditText)});
                }

                String item = String.valueOf(pos);
                //Toast.makeText(getApplicationContext(), "valor: " + pCodTipoDoc, Toast.LENGTH_LONG).show();
            }
        });
        txt_cdepartamento_visor.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                txt_cprovincia_visor.setText("");
                txt_cdistrito_visor.setText("");
                sListaProvincias.clear();
                pCodDepartamentoWS = listaDepartamentos.get(pos).getId();

                for (int i = 0; i < listaDepartamentos.get(pos).getListaProvincias().size(); i++) {
                    posDepartamento = pos;
                    sListaProvincias.add(listaDepartamentos.get(pos).getListaProvincias().get(i).getDescripcion());
                }
                GlobalFunctions.rellenarActv(getApplicationContext(), txt_cprovincia_visor, sListaProvincias);
                String item = String.valueOf(pos);
                //Toast.makeText(getApplicationContext(), "valor: " + pCodDepartamentoWS, Toast.LENGTH_LONG).show();
            }
        }
        );
        txt_cprovincia_visor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                txt_cdistrito_visor.setText("");
                sListaDistritos.clear();
                pCodProvinciaWS = listaDepartamentos.get(posDepartamento).getListaProvincias().get(pos).getId();
                for (int i = 0; i < listaDepartamentos.get(posDepartamento).getListaProvincias().get(pos).getListaDistritos().size(); i++) {
                    posProvincia=pos;
                    sListaDistritos.add(listaDepartamentos.get(posDepartamento).getListaProvincias().get(pos).getListaDistritos().get(i).getDescripcion());
                    GlobalFunctions.rellenarActv(getApplicationContext(), txt_cdistrito_visor, sListaDistritos);
                }
                String item = String.valueOf(pos);
                //Toast.makeText(getApplicationContext(), "valor: " + pCodProvinciaWS, Toast.LENGTH_LONG).show();
            }
        });

        txt_cdistrito_visor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                posDistrito = pos;
                pCodDistritoWS = listaDepartamentos.get(posDepartamento).getListaProvincias().get(posProvincia).getListaDistritos().get(pos).getId();
                //Toast.makeText(getApplicationContext(), "valor: " + pCodDistritoWS, Toast.LENGTH_LONG).show();
            }
        });

        cb_tratamiento_2.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                pCodTipoProdWS="";
                pCodProductoWS="";
                pCodSubProductoWS="";
                cb_idcategoria_2.setText("");
                cb_idproducto_2.setText("");
                cb_idprograma_2.setText("");
                sListaTipoProducto.clear();
                pCodOperComercialWS = listCampania.get(pos).getId();
                for (int i = 0; i < listCampania.get(pos).getListaTipoProductos().size(); i++) {
                    posCampania = pos;
                    sListaTipoProducto.add(listCampania.get(posCampania).getListaTipoProductos().get(i).getDescripcion());
                }
                GlobalFunctions.rellenarActv(getApplicationContext(), cb_idcategoria_2, sListaTipoProducto);
                
                if (pCodOperComercialWS.equalsIgnoreCase("ALT")) {
                    telefono_migrar_2.setText("");
                    tvTelfMigra.setVisibility(View.GONE);
                    telefono_migrar_2.setVisibility(View.GONE);
                }else{
                    tvTelfMigra.setVisibility(View.VISIBLE);
                    telefono_migrar_2.setVisibility(View.VISIBLE);
                }
            }
        });

        cb_idcategoria_2.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                cb_idproducto_2.setText("");
                cb_idprograma_2.setText("");
                sListaProducto.clear();
                pCodTipoProdWS = listCampania.get(posCampania).getListaTipoProductos().get(pos).getId();
                for (int i = 0; i < listCampania.get(posCampania).getListaTipoProductos().get(pos).getListaProductos().size(); i++) {
                    posTipoPro = pos;
                    sListaProducto.add(listCampania.get(posCampania).getListaTipoProductos().get(posTipoPro).getListaProductos().get(i).getDescripcion());
                }

                GlobalFunctions.rellenarActv(getApplicationContext(), cb_idproducto_2, sListaProducto);
                String item = String.valueOf(pos);
                //Toast.makeText(getApplicationContext(), "valor: " + pCodTipoProdWS, Toast.LENGTH_LONG).show();
            }
        });

        cb_idproducto_2.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                cb_idprograma_2.setText("");
                sListaSubProducto.clear();
                pCodProductoWS = listCampania.get(posCampania).getListaTipoProductos().get(posTipoPro).getListaProductos().get(pos).getId();
                for (int i = 0; i < listCampania.get(posCampania).getListaTipoProductos().get(posTipoPro).getListaProductos().get(pos).getListaSubProductos().size(); i++) {
                    posPro = pos;
                    sListaSubProducto.add(listCampania.get(posCampania).getListaTipoProductos().get(posTipoPro).getListaProductos().get(posPro).getListaSubProductos().get(i).getDescripcion());
                }
                GlobalFunctions.rellenarActv(getApplicationContext(), cb_idprograma_2, sListaSubProducto);
                String item = String.valueOf(pos);
                //Toast.makeText(getApplicationContext(), "valor: " + pCodProductoWS, Toast.LENGTH_LONG).show();
            }
        });

        cb_idprograma_2.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                pCodSubProductoWS = listCampania.get(posCampania).getListaTipoProductos().get(posTipoPro).getListaProductos().get(posPro).getListaSubProductos().get(pos).getId();
                //Toast.makeText(getApplicationContext(),"valor:" +  pCodSubProductoWS, Toast.LENGTH_LONG).show();
            }
        });

        cb_tipoIncidencia.setOnItemClickListener(new AdapterView.OnItemClickListener() { //manejo de evento de seleción de dep, prov, dist
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                pCodTipoIncidencia = listTipoIncidencia.get(pos).getCodigo();
                //Toast.makeText(getApplicationContext(),"valor:" +  pCodTipoIncidencia, Toast.LENGTH_LONG).show();
            }
        });

        getDataCombos();


        //showBottomSheetDialog2("","","","","","","","","","",0);
    }

    @Override
    public void onBackPressed() {
        newIntent(this, ActivityMenuIncidencia.class);
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

        if (id == R.id.action_GoMenu) {
            newIntent(this, ActivityMenu.class);
        }

        if (id == R.id.action_Notificaciones) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                mostrarDialogoIncidenciaMasiva();
            }else{
                Toast.makeText(ActivityVisorVentasFija.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.action_BandejaTickets) {

        }

        if (id == R.id.action_Adjuntar) {

        }

        //Comentar
        if (id == R.id.action_Comentar) {

        }

        if (id == R.id.action_CerrarSesion) {
            GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
            if (sharedpreferences.getBoolean("flagVentas",false))
            {
                finishAffinity();
            }
            else
            {
                newIntent(ActivityVisorVentasFija.this, LoginTelefonica.class);
                uri = null;
            }
            limpiarShared(this);
            Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
            vistaIncidenciaMasiva=false;
        }

        return super.onOptionsItemSelected(item);
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
    }

    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");
        LoginTelefonica.existeIncidenciaMasiva=false;
    }

    private void getDataCombos(){
        customDialog.show(); //Inicia dialog
        String WSMethod="";
        WSMethod = "in_ficha_visor_combos";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        params.put("dniVendedormodal", ActivityMenuIncidencia.numDocVendedor);
        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        params.put("ind", ((TGestionaSession) getApplication()).getTipoIncidencia());
        params.put("token", ((TGestionaSession) getApplication()).getTokenMovil());
        if (varPublicas.tokenMovil==null) varPublicas.tokenMovil ="";
        params.put("token", varPublicas.tokenMovil);

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"arrayVisor.json");
                        JSONArray jsonArray = null;
                        JSONArray jATipoDoc,jACampania,jATratamiento, jATitularFallecido, jAEnvioContratos, jAFacturaDigital, jACompartirInformacion, jAModalidadInterConnect, jAMonto, jADebitoAutomatico, jADepartamentos;
                        //JSONObject jODepartamento=null;
                        int status=-1, segmento=-1;
                        String msg="";
                        Boolean estado, sessionStatus;

                        try {
                            //parse de web service de combos
                            jsonArray = new JSONArray(result);

                            //Valida Sesiones
                            msg= jsonArray.getJSONObject(0).getString("Msg").toString();
                            estado= Boolean.parseBoolean(jsonArray.getJSONObject(0).getString("Estado").toString());
                            sessionStatus = jsonArray.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)
                            segmento = jsonArray.getJSONObject(0).getInt("segmento");

                            if(!sessionStatus){
                                GlobalFunctions.validaSesion(sessionStatus, getApplicationContext());
                                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
                                if (sharedpreferences.getBoolean("flagVentas",false)){
                                    finishAffinity();
                                } else{
                                    newIntent(ActivityVisorVentasFija.this, LoginTelefonica.class);
                                    uri = null;
                                }
                                limpiarShared(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                                vistaIncidenciaMasiva=false;
                            }

                            //Fin Valida Sesiones
                            jATipoIncidencia = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("idtipo_incidencia");
                            jATipoDocVisor = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_ctipo_doc_2");
                            jADepartamentos = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("txt_cdepartamento_visor");
                            jAOperacionComercial = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_tratamiento_2");
                            jACampania = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("cb_tratamiento_2");

                            //relleno de Combos Independientes
                            listaTipoDoc= fillActvFromJSONArray(getApplicationContext(), jATipoDocVisor, cb_ctipo_doc_2,"id","value"); //rellena combo TipoDoc
                            listOperacionComercial= fillActvFromJSONArray(getApplicationContext(), jAOperacionComercial, cb_tratamiento_2,"id","value"); //rellena Oper Comercial
                            listTipoIncidencia = fillActvFromJSONArray(getApplicationContext(), jATipoIncidencia, cb_tipoIncidencia,"id","value"); //rellena Oper Comercial
                            //Parseo de UBIGEO

                            for (int iDepartamento = 0; iDepartamento < jADepartamentos.length(); iDepartamento++) {
                                DTO_Departamento oDep=new DTO_Departamento(); //Se instancia un objeto de la clase DTO_Departamento
                                oDep.setId(jADepartamentos.getJSONObject(iDepartamento).getString("id"));
                                oDep.setDescripcion(jADepartamentos.getJSONObject(iDepartamento).getString("value"));
                                sListaDepartamentos.add(jADepartamentos.getJSONObject(iDepartamento).getString("value"));
                                List<DTO_Provincia> listaProvincias = new ArrayList<>(); //Se declara lista de Provincias
                                if (!jADepartamentos.getJSONObject(iDepartamento).isNull("txt_cprovincia_visor")) {
                                    JSONArray jAProvincias = jADepartamentos.getJSONObject(iDepartamento).getJSONArray("txt_cprovincia_visor");
                                    for (int iProvincia = 0; iProvincia < jAProvincias.length(); iProvincia++) { // Se recorre las provincias del departamento en cuestión
                                        DTO_Provincia oProv = new DTO_Provincia();
                                        oProv.setId(jAProvincias.getJSONObject(iProvincia).getString("id"));
                                        oProv.setDescripcion(jAProvincias.getJSONObject(iProvincia).getString("value"));
                                        List<DTO_Distrito> listaDistritos = new ArrayList<>(); //Se declara lista de Distritos
                                        if(!jADepartamentos.getJSONObject(iDepartamento).getJSONArray("txt_cprovincia_visor").getJSONObject(iProvincia).isNull("txt_cdistrito_visor")) {
                                            JSONArray jADistritos = jADepartamentos.getJSONObject(iDepartamento).getJSONArray("txt_cprovincia_visor").getJSONObject(iProvincia).getJSONArray("txt_cdistrito_visor");
                                            for (int iDistrito = 0; iDistrito < jADistritos.length(); iDistrito++) {
                                                DTO_Distrito oDist = new DTO_Distrito();
                                                oDist.setId(jADistritos.getJSONObject(iDistrito).getString("id"));
                                                oDist.setDescripcion(jADistritos.getJSONObject(iDistrito).getString("value"));
                                                listaDistritos.add(oDist);
                                            }
                                            oProv.setListaDistritos(listaDistritos);
                                            listaProvincias.add(oProv);
                                        }
                                    }
                                    oDep.setListaProvincias(listaProvincias);
                                    listaDepartamentos.add(oDep);
                                }

                                //GlobalFunctions.rellenarActv(getApplicationContext(),comboDepa,listaDepartamentos.g);
                            }

                            fillCombos();

                            //Parseo de Operacion Comercial, TIpoProducto, Producto, SubProducto
                            for (int iOComercial = 0; iOComercial < jACampania.length(); iOComercial++) {
                                DTO_OperacionComercial oComercial=new DTO_OperacionComercial(); //Se instancia un objeto de la clase DTO_Departamento
                                oComercial.setId(jACampania.getJSONObject(iOComercial).getString("id"));
                                oComercial.setDescripcion(jACampania.getJSONObject(iOComercial).getString("value"));
                                sListaCampania.add(jACampania.getJSONObject(iOComercial).getString("value"));
                                List<DTO_TipoProducto> listaTipoProductos = new ArrayList<>(); //Se declara lista de Provincias
                                JSONArray jATipoProductos = jACampania.getJSONObject(iOComercial).getJSONArray("cb_idcategoria_2");
                                for (int iTipoProducto = 0; iTipoProducto < jATipoProductos.length(); iTipoProducto++) { // Se recorre las provincias del departamento en cuestión
                                    DTO_TipoProducto oTipoPro=new DTO_TipoProducto();
                                    oTipoPro.setId(jATipoProductos.getJSONObject(iTipoProducto).getString("id"));
                                    oTipoPro.setDescripcion(jATipoProductos.getJSONObject(iTipoProducto).getString("value"));
                                    List<DTO_Producto> listaProductos = new ArrayList<>(); //Se declara lista de Distritos
                                    JSONArray jAProductos = jACampania.getJSONObject(iOComercial).getJSONArray("cb_idcategoria_2").getJSONObject(iTipoProducto).getJSONArray("cb_idproducto_2");
                                    for (int iProducto = 0; iProducto < jAProductos.length() ; iProducto++) {
                                        DTO_Producto oProducto = new DTO_Producto();
                                        oProducto.setId(jAProductos.getJSONObject(iProducto).getString("id"));
                                        oProducto.setDescripcion(jAProductos.getJSONObject(iProducto).getString("value"));
                                        List<DTO_Subproducto> listaSubProductos = new ArrayList<>();
                                        JSONArray jASubProductos = jACampania.getJSONObject(iOComercial).getJSONArray("cb_idcategoria_2").getJSONObject(iTipoProducto).getJSONArray("cb_idproducto_2").getJSONObject(iProducto).getJSONArray("cb_idprograma_2");
                                        for (int iSubProducto = 0; iSubProducto < jASubProductos.length(); iSubProducto++) {
                                            DTO_Subproducto oSubProducto = new DTO_Subproducto();
                                            oSubProducto.setId(jASubProductos.getJSONObject(iSubProducto).getString("id"));
                                            oSubProducto.setDescripcion(jASubProductos.getJSONObject(iSubProducto).getString("value"));
                                            listaSubProductos.add(oSubProducto);
                                        }
                                        oProducto.setListaSubProductos(listaSubProductos);
                                        listaProductos.add(oProducto);
                                    }
                                    oTipoPro.setListaProductos(listaProductos);
                                    listaTipoProductos.add(oTipoPro);
                                }
                                oComercial.setListaTipoProductos(listaTipoProductos);
                                listCampania.add(oComercial);
                                //GlobalFunctions.rellenarActv(getApplicationContext(),comboDepa,listaDepartamentos.g);
                            }

                            if (segmento==1){
                                showDialogSegmentos();
                            }

//                            actvCampania.sets

//                            actvCampania.setThreshold(100);
//                            actvCampania.setSelection(2);

//                            fillActvFromJSONArray(getApplicationContext(), jATitularFallecido, cb_titular_fallecido,"id","value");
//                            fillActvFromJSONArray(getApplicationContext(), jAEnvioContratos, cb_envio_contrato,"id","value");
//                            fillActvFromJSONArray(getApplicationContext(), jAFacturaDigital, cb_factura_digital,"id","value");
//                            fillActvFromJSONArray(getApplicationContext(), jACompartirInformacion, cb_compartir_informacion,"id","value");
//                            fillActvFromJSONArray(getApplicationContext(), jAModalidadInterConnect, cb_modalidad_pago,"id","value");
//                            fillActvFromJSONArray(getApplicationContext(), jAMonto, txt_web_experto,"id","value");
//                            fillActvFromJSONArray(getApplicationContext(), jADebitoAutomatico, cb_debito_automatico,"id","value");
                            //fillActvFromJSONArray(getApplicationContext(), jADepartamentos, txt_cdepartamento,"id","value");
                            //GlobalFunctions.rellenarActv(getApplicationContext(),txt_cdepartamento,listaDepartamentos);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        customDialog.dismiss(); //Inicia dialog
                    }
                }
        );
    }

    protected final Dialog mostrarAlerta() {
        Dialog dialog = null;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(
                        "Este módulo se activará cuando presente incidencias masivas en el App / Web Fija")
                        .setCancelable(false)
                        .setTitle("Recuerda!")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        //to perform on ok
                                        dialog.cancel();

                                    }
                                });
                        /*.setNegativeButton("cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        dialog.cancel();
                                    }
                                });*/
                builder.show();
                AlertDialog alert = builder.create();
                dialog = alert;



        return dialog;
    }


    private void callEnviarDatos(){
        customDialog.show(); //Inicia dialog
        String WSMethod="";
        WSMethod = "in_ficha_visor_enviar";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        // valores para mostrar visor IN HOUSE
//        params.put("id_vendedor", "35");
//        params.put("token", "asdasdasdfsfd");
//        params.put("cb_ctipo_doc", "DNI");
//        params.put("num_doc_cli_2", "10792532");
//        params.put("cb_tratamiento_2", "ALT");
//        params.put("telefono_migrar_2", "123");
//        params.put("txt_cdepartamento_visor", "2");
//        params.put("txt_cprovincia_visor", "2");
//        params.put("txt_cdistrito_visor", "2");
//        params.put("cb_idcuenta", "12344444");
//        params.put("cb_idcategoria_2", "20");
//        params.put("cb_idproducto_2", "368");
//        params.put("cb_idprograma_2", "12180784");

        String adicional="{\\\"hide\\\":[\\\"txt_localidad\\\"],\\\"show\\\":[\\\"txt_nombre_padre\\\",\\\"txt_nombre_madre\\\",\\\"txt_fecha_nacimiento\\\",\\\"txt_lugar_nacimiento\\\",\\\"txt_lugar_nacimiento\\\"]}";

        params.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        params.put("token", ((TGestionaSession) getApplication()).getTokenMovil());
        if (varPublicas.tokenMovil==null) varPublicas.tokenMovil ="";
        params.put("token", varPublicas.tokenMovil);
        params.put("cb_ctipo_doc_2", pCodTipoDoc);
        params.put("num_doc_cli_2", num_doc_cli_2.getText().toString());
        params.put("cb_tratamiento_2", pCodOperComercialWS);
        params.put("telefono_migrar_2", telefono_migrar_2.getText().toString());
        params.put("txt_cdepartamento_visor", pCodDepartamentoWS);
        params.put("txt_cprovincia_visor", pCodProvinciaWS);
        params.put("txt_cdistrito_visor", pCodDistritoWS);
        //params.put("actvCampania", pCodCampaniaWS);
        params.put("cb_idcategoria_2", pCodTipoProdWS);
        params.put("cb_idproducto_2", pCodProductoWS);
        params.put("cb_idprograma_2", pCodSubProductoWS);
        params.put("idtipo_incidencia", pCodTipoIncidencia);
        params.put("adicional", adicional);
        params.put("dniVendedormodal", ActivityMenuIncidencia.numDocVendedor);
        params.put("ind", ((TGestionaSession) getApplication()).getTipoIncidencia());


        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONArray jsonArray = null;
                        JSONArray jATipoDoc,jATratamiento, jATitularFallecido, jAEnvioContratos, jAFacturaDigital, jACompartirInformacion, jAModalidadInterConnect, jAMonto, jADebitoAutomatico, jADepartamentos;
                        //JSONObject jODepartamento=null;
                        int status=-1;
                        String msg="";
                        Boolean estado, sessionStatus;

                        try {
                            //parse de web service de combos
                            jsonArray = new JSONArray(result);

                            //Valida Sesiones
                            msg= jsonArray.getJSONObject(0).getString("Msg").toString();
                            estado= Boolean.parseBoolean(jsonArray.getJSONObject(0).getString("Estado").toString());
                            sessionStatus = jsonArray.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

                            if(!sessionStatus){
                                GlobalFunctions.validaSesion(sessionStatus, getApplicationContext());
                                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
                                if (sharedpreferences.getBoolean("flagVentas",false)){
                                    finishAffinity();
                                } else{
                                    newIntent(ActivityVisorVentasFija.this, LoginTelefonica.class);
                                    uri = null;
                                }
                                limpiarShared(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                                vistaIncidenciaMasiva=false;
                            }

                            //Fin valida Sesiones
                            switch (jsonArray.getJSONObject(0).getInt("flag")) {
                                case 1: //Validación de mensaje por campo o toast
                                    switch (jsonArray.getJSONObject(0).getInt("tipo_error")) {
                                        case 1: //validacion por campo
                                            JSONArray listaErrores = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("error");
                                            recorrerAllEditText(llMain);
                                            for (int i = 0; i < listaErrores.length(); i++) {
                                                String campo, msgError, inputType;
                                                campo = listaErrores.getJSONObject(i).getString("field");
                                                msgError = listaErrores.getJSONObject(i).getString("message");
                                                inputType = listaErrores.getJSONObject(i).getString("inputType");
                                                setErrorToViewByItsName(campo.trim(), msgError, inputType, i);
                                            }
                                            break;
                                        case 2: //validación por toast
                                            JSONArray listaErrores2 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("error");
                                            for (int i = 0; i < listaErrores2.length(); i++) {
                                                String msgError;
                                                msgError = listaErrores2.getJSONObject(i).getString("message");
                                                Toast.makeText(ActivityVisorVentasFija.this, msgError, Toast.LENGTH_SHORT).show();
                                            }
                                            break;
                                    }
                                    break;
                                case 2: //VISOR INHOUSE



                                    JSONObject jsonObjectINHOUSETabla = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONObject("tabla1");
                                    JSONArray jsonObjectINHOUSEBotones = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("botones");

                                    String hd_idconsul= jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("hd_idconsul");
                                    String primera_log = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("primera_log");
                                    String back="", folio="", cliente="", telefono="", departamento="", direccion="", fContrato="", producto="", estado2="", subEstado="";
                                    JSONArray jaValores = jsonObjectINHOUSETabla.getJSONArray("valores");
                                    for (int i = 0; i < jaValores.length(); i++) {
                                        back = jaValores.getJSONObject(i).getString("back");
                                        folio = jaValores.getJSONObject(i).getString("folio");
                                        cliente = jaValores.getJSONObject(i).getString("cliente_nombre");
                                        telefono = jaValores.getJSONObject(i).getString("telefono");
                                        departamento = jaValores.getJSONObject(i).getString("cliente_departamento");
                                        direccion = jaValores.getJSONObject(i).getString("cliente_direccion");
                                        fContrato = jaValores.getJSONObject(i).getString("fhproceso");
                                        producto = jaValores.getJSONObject(i).getString("idproducto");
                                        estado2 = jaValores.getJSONObject(i).getString("idgestion1");
                                        subEstado = jaValores.getJSONObject(i).getString("idsubtipo1");
                                    }
                                    showBottomSheetDialog(back, folio, cliente, telefono, departamento, direccion, fContrato, producto, estado2, subEstado,jsonObjectINHOUSEBotones, hd_idconsul,jaValores,primera_log,"","","","","","","",null,"",1);

                                    break;
                                case 3:  //VISOR ATIS
                                    JSONObject jsonObjectATISTabla = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONObject("tabla2");
                                    String segunda_log = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("segunda_log");
                                    String hd_idconsul2= jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("hd_idconsul");
                                    JSONArray jsonObjectATISBotones = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("botones");
                                    String cabecera1, cabecera2, valores, speech, fhcontrato, estado_solicitud, telefono2, gestion, detalle_gestion, back2, peticion_atis, idgestion, p_peticion, accion_asesor,
                                           botones, adicional, mensaje="";
                                    Boolean telefonoVisible, fhContratoVisible, gestionVIsible, accionVisible, pPeticionVisible;

                                    adicional = jsonArray.getJSONObject(0).getString("adicional");
                                    cabecera1 = jsonObjectATISTabla.getString("cabecera1");
                                    cabecera2 = jsonObjectATISTabla.getString("cabecera2");
                                    mensaje = jsonObjectATISTabla.getString("mensaje");
                                    speech = jsonObjectATISTabla.getJSONObject("valores").getString("speech");
                                    fhcontrato = jsonObjectATISTabla.getJSONObject("valores").getString("fhcontrato");
                                    estado_solicitud = jsonObjectATISTabla.getJSONObject("valores").getString("estado_solicitud");
                                    telefono2 = jsonObjectATISTabla.getJSONObject("valores").getString("telefono");
                                    //telefonoVisible = jsonObjectATIS.getString("telefono");
                                    gestion = jsonObjectATISTabla.getJSONObject("valores").getString("gestion");
                                    detalle_gestion = jsonObjectATISTabla.getJSONObject("valores").getString("detalle_gestion");
                                    back2 = jsonObjectATISTabla.getJSONObject("valores").getString("back");
                                    peticion_atis = jsonObjectATISTabla.getJSONObject("valores").getString("peticion_atis");
                                    idgestion = jsonObjectATISTabla.getJSONObject("valores").getString("idgestion");
                                    p_peticion = jsonObjectATISTabla.getJSONObject("valores").getString("p_peticion");
                                    accion_asesor = jsonObjectATISTabla.getJSONObject("valores").getString("accion_asesor");

                                    showBottomSheetDialog2(cabecera1, cabecera2, speech, fhcontrato, estado_solicitud, telefono2, gestion, detalle_gestion, back2, peticion_atis, idgestion, p_peticion,accion_asesor, mensaje,segunda_log, jsonObjectATISBotones, hd_idconsul2,2);

                                    break;
                                case 4: //VISOR INHOUSE
                                    JSONObject jsonObjectINHOUSETabla4 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONObject("tabla1");
                                    JSONArray jsonObjectINHOUSEBotones4 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("botones");
                                    JSONObject jsonObjectATISTabla2 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONObject("tabla2");
                                    JSONArray jsonObjectATISBotones2 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("botones");
                                    String segunda_log2 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("segunda_log");
                                    String hd_idconsul3= jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("hd_idconsul");

                                    String hd_idconsul4= jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("hd_idconsul");
                                    String primera_log4 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("primera_log");
                                    String back4="", folio4="", cliente4="", telefono4="", departamento4="", direccion4="", fContrato4="", producto4="", estado24="", subEstado4="";
                                    JSONArray jaValores4 = jsonObjectINHOUSETabla4.getJSONArray("valores");
                                    for (int i = 0; i < jaValores4.length(); i++) {
                                        back = jaValores4.getJSONObject(i).getString("back");
                                        folio = jaValores4.getJSONObject(i).getString("folio");
                                        cliente = jaValores4.getJSONObject(i).getString("cliente_nombre");
                                        telefono = jaValores4.getJSONObject(i).getString("telefono");
                                        departamento = jaValores4.getJSONObject(i).getString("cliente_departamento");
                                        direccion = jaValores4.getJSONObject(i).getString("cliente_direccion");
                                        fContrato = jaValores4.getJSONObject(i).getString("fhproceso");
                                        producto = jaValores4.getJSONObject(i).getString("idproducto");
                                        estado2 = jaValores4.getJSONObject(i).getString("idgestion1");
                                        subEstado = jaValores4.getJSONObject(i).getString("idsubtipo1");
                                    }

                                    //atis
                                    cabecera1 = jsonObjectATISTabla2.getString("cabecera1");
                                    cabecera2 = jsonObjectATISTabla2.getString("cabecera2");
                                    mensaje = jsonObjectATISTabla2.getString("mensaje");
                                    speech = jsonObjectATISTabla2.getJSONObject("valores").getString("speech");
                                    fhcontrato = jsonObjectATISTabla2.getJSONObject("valores").getString("fhcontrato");
                                    estado_solicitud = jsonObjectATISTabla2.getJSONObject("valores").getString("estado_solicitud");
                                    telefono2 = jsonObjectATISTabla2.getJSONObject("valores").getString("telefono");
                                    //telefonoVisible = jsonObjectATIS.getString("telefono");
                                    gestion = jsonObjectATISTabla2.getJSONObject("valores").getString("gestion");
                                    detalle_gestion = jsonObjectATISTabla2.getJSONObject("valores").getString("detalle_gestion");
                                    back2 = jsonObjectATISTabla2.getJSONObject("valores").getString("back");
                                    peticion_atis = jsonObjectATISTabla2.getJSONObject("valores").getString("peticion_atis");
                                    idgestion = jsonObjectATISTabla2.getJSONObject("valores").getString("idgestion");
                                    p_peticion = jsonObjectATISTabla2.getJSONObject("valores").getString("p_peticion");
                                    accion_asesor = jsonObjectATISTabla2.getJSONObject("valores").getString("accion_asesor");
                                    showBottomSheetDialog(back4, folio4, cliente4, telefono4, departamento4, direccion4, fContrato4, producto4, estado24, subEstado4,jsonObjectINHOUSEBotones4, hd_idconsul4,jaValores4,primera_log4,fhcontrato,estado_solicitud,telefono2,peticion_atis,accion_asesor,mensaje,segunda_log2,jsonObjectATISBotones2,hd_idconsul3,2);

                                    break;
                                case 5:  //REFERIDO
                                    Intent intent1 = null;
                                    intent1 = new Intent(ActivityVisorVentasFija.this, ActivityVentasFija.class);
                                    intent1.putExtra("pTipoDoc", pCodTipoDoc);
                                    intent1.putExtra("pNroDoc", num_doc_cli_2.getText().toString());
                                    intent1.putExtra("pOPComercialCode", pCodOperComercialWS);
                                    intent1.putExtra("pOPComercialValue", cb_tratamiento_2.getText().toString());
                                    intent1.putExtra("pTelfMigra", telefono_migrar_2.getText().toString());
                                    intent1.putExtra("pCampaniaCode", pCodCampaniaWS);
                                    //intent1.putExtra("pCampaniaValue", actvCampania.getText().toString());
                                    intent1.putExtra("pTipoProductoCode", pCodTipoProdWS);
                                    intent1.putExtra("pTipoProductoValue", cb_idcategoria_2.getText().toString());
                                    intent1.putExtra("pProductoCode", pCodProductoWS);
                                    intent1.putExtra("pProductoValue", cb_idproducto_2.getText().toString());
                                    intent1.putExtra("pSubProductoCode", pCodSubProductoWS);
                                    intent1.putExtra("pSubProductoValue", cb_idprograma_2.getText().toString());
                                    intent1.putExtra("pDepCode", pCodDepartamentoWS);
                                    intent1.putExtra("pDepValue", txt_cdepartamento_visor.getText().toString());
                                    intent1.putExtra("pProvCode", pCodProvinciaWS);
                                    intent1.putExtra("pProvValue", txt_cprovincia_visor.getText().toString());
                                    intent1.putExtra("pDistCode", pCodDistritoWS);
                                    intent1.putExtra("pDistValue", txt_cdistrito_visor.getText().toString());

                                    startActivity(intent1);
                                    break;
                                case 6:
                                    Intent intent=null;
                                    intent = new Intent(ActivityVisorVentasFija.this, ActivityVentasFija.class);
                                    intent.putExtra("pTipoDoc", pCodTipoDoc);
                                    intent.putExtra("pNroDoc", num_doc_cli_2.getText().toString());
                                    intent.putExtra("pOPComercialCode", pCodOperComercialWS);
                                    intent.putExtra("pOPComercialValue", cb_tratamiento_2.getText().toString());
                                    intent.putExtra("pTelfMigra", telefono_migrar_2.getText().toString());
                                    intent.putExtra("pCampaniaCode", pCodCampaniaWS);
                                    //intent.putExtra("pCampaniaValue", actvCampania.getText().toString());
                                    intent.putExtra("pTipoProductoCode", pCodTipoProdWS);
                                    intent.putExtra("pTipoProductoValue", cb_idcategoria_2.getText().toString());
                                    intent.putExtra("pProductoCode", pCodProductoWS);
                                    intent.putExtra("pProductoValue", cb_idproducto_2.getText().toString());
                                    intent.putExtra("pSubProductoCode", pCodSubProductoWS);
                                    intent.putExtra("pSubProductoValue", cb_idprograma_2.getText().toString());
                                    intent.putExtra("pDepCode", pCodDepartamentoWS);
                                    intent.putExtra("pDepValue", txt_cdepartamento_visor.getText().toString());
                                    intent.putExtra("pProvCode", pCodProvinciaWS);
                                    intent.putExtra("pProvValue", txt_cprovincia_visor.getText().toString());
                                    intent.putExtra("pDistCode", pCodDistritoWS);
                                    intent.putExtra("pDistValue", txt_cdistrito_visor.getText().toString());
                                    startActivity(intent);
                                    break;
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        customDialog.dismiss(); //Inicia dialog
                    }
                }
        );
    }

    private void fillCombos(){
        GlobalFunctions.rellenarActv(getApplicationContext(), txt_cdepartamento_visor,sListaDepartamentos); //rellena combo de departamento
        //GlobalFunctions.rellenarActv(getApplicationContext(),cb_tratamiento_2,sListaOperacionComercial); //rellena combo de Operacion Comercial
        //GlobalFunctions.rellenarActv(getApplicationContext(),actvCampania,sListaCampania); //rellena combo de Operacion Comercial
    }

    RecyclerView rvDetalleInHOUSE;
    RvInHouseAdapter rvInHouseAdapter;
    public void showBottomSheetDialog(String back, String folio, String cliente, String telefono, String departamento, String direccion, String fContrato,
                                      String producto, String estado, String subEstado, JSONArray jsonObjectBotones, final String hd_idconsul, JSONArray jAValores, final String primera_log,
                                      String fhcontratoATIS, String estado_solicitudATIS, String telefonoATIS, String peticion_atis, String accion_asesorATIS, final String mensajeATIS, final String segunda_logATIS, JSONArray jsonBotonesATIS, final String hd_idconsulATIS,int tipoVisor) {
        View viewBottomDialog=null;
        if (tipoVisor==1) { //SOLO INHOUSE
            viewBottomDialog = getLayoutInflater().inflate(R.layout.ly_dialog_visor_in_house, null);
            rvDetalleInHOUSE = (RecyclerView) viewBottomDialog.findViewById(R.id.rvDetalleINHouse);
            //adapters
            rvInHouseAdapter = new RvInHouseAdapter(getApplicationContext(), listaINHOUSE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            rvDetalleInHOUSE.setLayoutManager(mLayoutManager);
            rvDetalleInHOUSE.setItemAnimator(new DefaultItemAnimator());
            rvDetalleInHOUSE.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            rvDetalleInHOUSE.setAdapter(rvInHouseAdapter);

            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            //dialog.setCancelable(false);
            Button btnNO, btnSI;
            TextView title;
            title = (TextView) viewBottomDialog.findViewById(R.id.tvTitle);
            btnNO = viewBottomDialog.findViewById(R.id.btnCancelar);
            btnSI  = viewBottomDialog.findViewById(R.id.btnContinuar);
            String value1,value2;

            if (listaINHOUSE.size() >0) {
                listaINHOUSE.clear();
                //messages.clear();
                rvDetalleInHOUSE.removeAllViewsInLayout();
                rvInHouseAdapter.notifyDataSetChanged();
            }

            try {
                //recorriendo array de valores
                for (int i = 0; i < jAValores.length(); i++) {
                    back = jAValores.getJSONObject(i).getString("back");
                    folio = jAValores.getJSONObject(i).getString("folio");
                    cliente = jAValores.getJSONObject(i).getString("cliente_nombre");
                    telefono = jAValores.getJSONObject(i).getString("telefono");
                    departamento = jAValores.getJSONObject(i).getString("cliente_departamento");
                    direccion = jAValores.getJSONObject(i).getString("cliente_direccion");
                    fContrato = jAValores.getJSONObject(i).getString("fhproceso");
                    producto = jAValores.getJSONObject(i).getString("idproducto");
                    estado = jAValores.getJSONObject(i).getString("idgestion1");
                    subEstado = jAValores.getJSONObject(i).getString("idsubtipo1");

                    CreateDetalleINHouseItem(back, folio, cliente, telefono, departamento, direccion, fContrato, producto, estado, subEstado);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                paramBtnContinuar1 = jsonObjectBotones.getJSONObject(0).getString("field");
                value1 = jsonObjectBotones.getJSONObject(0).getString("value");
                visible1 = jsonObjectBotones.getJSONObject(0).getBoolean("visible");
                paramBtnContinuar2 = jsonObjectBotones.getJSONObject(1).getString("field");
                value2 = jsonObjectBotones.getJSONObject(1).getString("value");
                visible2 = jsonObjectBotones.getJSONObject(1).getBoolean("visible");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            btnNO.setOnClickListener(new OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             informacion_registrar_visor2("btn_visor_in1_no", primera_log,"",hd_idconsul);
                                             //actvCampania.setText("");
                                             cb_tratamiento_2.setText("");
                                             cb_ctipo_doc_2.setText("");
                                             num_doc_cli_2.setText("");
                                             telefono_migrar_2.setText("");
                                             cb_idcategoria_2.setText("");
                                             cb_idproducto_2.setText("");
                                             cb_idprograma_2.setText("");
                                             cb_tipoIncidencia.setText("");
                                             txt_cdepartamento_visor.setText("");
                                             txt_cprovincia_visor.setText("");
                                             txt_cdistrito_visor.setText("");
                                             pCodCampaniaWS=""; pCodProvinciaWS=""; pCodDistritoWS=""; pCodOperComercialWS="";
                                             pCodTipoProdWS=""; pCodSubProductoWS="";pCodProductoWS="";

                                             dialog.dismiss();
                                         }
                                     }
            );
            btnSI.setOnClickListener(new OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             // dialog.dismiss();
                                             informacion_registrar_visor2("btn_visor_in1_si", primera_log,"",hd_idconsul);
                                             //}

                                             //Muestra la ficha de ventas fija
                                             Intent intent1 = null;
                                             intent1 = new Intent(ActivityVisorVentasFija.this, ActivityVentasFija.class);
                                             intent1.putExtra("pTipoDoc", pCodTipoDoc);
                                             intent1.putExtra("pNroDoc", num_doc_cli_2.getText().toString());
                                             intent1.putExtra("pOPComercialCode", pCodOperComercialWS);
                                             intent1.putExtra("pOPComercialValue", cb_tratamiento_2.getText().toString());
                                             intent1.putExtra("pTelfMigra", telefono_migrar_2.getText().toString());
                                             intent1.putExtra("pCampaniaCode", pCodCampaniaWS);
                                             //intent1.putExtra("pCampaniaValue", actvCampania.getText().toString());
                                             intent1.putExtra("pTipoProductoCode", pCodTipoProdWS);
                                             intent1.putExtra("pTipoProductoValue", cb_idcategoria_2.getText().toString());
                                             intent1.putExtra("pProductoCode", pCodProductoWS);
                                             intent1.putExtra("pProductoValue", cb_idproducto_2.getText().toString());
                                             intent1.putExtra("pSubProductoCode", pCodSubProductoWS);
                                             intent1.putExtra("pSubProductoValue", cb_idprograma_2.getText().toString());
                                             intent1.putExtra("pDepCode", pCodDepartamentoWS);
                                             intent1.putExtra("pDepValue", txt_cdepartamento_visor.getText().toString());
                                             intent1.putExtra("pProvCode", pCodProvinciaWS);
                                             intent1.putExtra("pProvValue", txt_cprovincia_visor.getText().toString());
                                             intent1.putExtra("pDistCode", pCodDistritoWS);
                                             intent1.putExtra("pDistValue", txt_cdistrito_visor.getText().toString());
                                             startActivity(intent1);
                                         }
                                     }
            );

            TextView tvBack, tvFolio, tvCliente, tvTelefono, tvDepartamento, tvDireccion, tvFContrato, tvProducto, tvEstado, tvSubEstado, tvAccion;
            tvBack = (TextView) viewBottomDialog.findViewById(R.id.txtBack);
            tvFolio = (TextView) viewBottomDialog.findViewById(R.id.txtFolio);
            tvCliente = (TextView) viewBottomDialog.findViewById(R.id.txtCliente);
            tvTelefono = (TextView) viewBottomDialog.findViewById(R.id.txtTelefono);
            tvDepartamento = (TextView) viewBottomDialog.findViewById(R.id.txtDepartamento);
            tvDireccion = (TextView) viewBottomDialog.findViewById(R.id.txt_cdireccion);
            tvFContrato = (TextView) viewBottomDialog.findViewById(R.id.txtFechaContrato);
            tvProducto = (TextView) viewBottomDialog.findViewById(R.id.txtProducto);
            tvEstado = (TextView) viewBottomDialog.findViewById(R.id.txtEstado);
            tvSubEstado = (TextView) viewBottomDialog.findViewById(R.id.txtSubEstado);
            tvAccion = (TextView) viewBottomDialog.findViewById(R.id.txtAccion);

            tvBack.setText(back);
            tvFolio.setText(folio);
            tvCliente.setText(cliente);
            tvTelefono.setText(telefono);
            tvDepartamento.setText(departamento);
            tvDireccion.setText(direccion);
            tvFContrato.setText(fContrato);
            tvProducto.setText(producto);
            tvEstado.setText(estado);
            tvSubEstado.setText(subEstado);
            tvAccion.setText("ACCION");

            dialog.setContentView(viewBottomDialog);
            dialog.show();

        }else if (tipoVisor==2){ //IN HOUSE - ATIS
            viewBottomDialog = getLayoutInflater().inflate(R.layout.ly_dialog_visor_in_house_atiss, null);
            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            //dialog.setCancelable(false);

            final TextView tvspeech, tvfhcontrato, tvestado_solicitud, tvtelefono, tvgestion, tvdetalle_gestion, tvback, tvpeticion_atis, tvidgestion, tvp_peticion, tvaccion_asesor, tvAccion_atis,  tvHoraATIS, tvFechaContratoLabel;
            final Button btnContinuar, btnContinuar2, btnCancelar;
            //tvspeech = (TextView) view.findViewById(R.id.tvspeech);
            tvfhcontrato = (TextView) viewBottomDialog.findViewById(R.id.tvfhcontrato);
            tvFechaContratoLabel = (TextView) viewBottomDialog.findViewById(R.id.tvFechaContratoLabel);
            tvestado_solicitud = (TextView) viewBottomDialog.findViewById(R.id.tvestado_solicitud);
            tvtelefono = (TextView) viewBottomDialog.findViewById(R.id.tvtelefono2);
            //tvgestion = (TextView) view.findViewById(R.id.tvgestion);
//        tvdetalle_gestion = (TextView) view.findViewById(R.id.tvdetalle_gestion);
//        tvback = (TextView) view.findViewById(R.id.tvback2);
            tvpeticion_atis = (TextView) viewBottomDialog.findViewById(R.id.tvpeticion_atis);
//        tvidgestion = (TextView) view.findViewById(R.id.tvidgestion);
//        tvp_peticion = (TextView) view.findViewById(R.id.tvp_peticion);
            tvaccion_asesor = (TextView) viewBottomDialog.findViewById(R.id.tvaccion_asesor);
            tvAccion_atis = (TextView) viewBottomDialog.findViewById(R.id.tvAccion_atis);
            tvHoraATIS = (TextView) viewBottomDialog.findViewById(R.id.tvHoraATIS);
            btnContinuar = (Button) viewBottomDialog.findViewById(R.id.btnSIATISS);
            //btnContinuar2 = (Button) viewBottomDialog.findViewById(R.id.btnContinuar2);
            btnCancelar = (Button) viewBottomDialog.findViewById(R.id.btnCancelar);

            //IN HOUSE
            rvDetalleInHOUSE = (RecyclerView) viewBottomDialog.findViewById(R.id.rvDetalleINHouse);
            //adapters
            rvInHouseAdapter = new RvInHouseAdapter(getApplicationContext(), listaINHOUSE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            rvDetalleInHOUSE.setLayoutManager(mLayoutManager);
            rvDetalleInHOUSE.setItemAnimator(new DefaultItemAnimator());
            rvDetalleInHOUSE.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            rvDetalleInHOUSE.setAdapter(rvInHouseAdapter);



            if (listaINHOUSE.size() >0) {
                listaINHOUSE.clear();
                //messages.clear();
                rvDetalleInHOUSE.removeAllViewsInLayout();
                rvInHouseAdapter.notifyDataSetChanged();
            }

            try {
                //recorriendo array de valores
                for (int i = 0; i < jAValores.length(); i++) {
                    back = jAValores.getJSONObject(i).getString("back");
                    folio = jAValores.getJSONObject(i).getString("folio");
                    cliente = jAValores.getJSONObject(i).getString("cliente_nombre");
                    telefono = jAValores.getJSONObject(i).getString("telefono");
                    departamento = jAValores.getJSONObject(i).getString("cliente_departamento");
                    direccion = jAValores.getJSONObject(i).getString("cliente_direccion");
                    fContrato = jAValores.getJSONObject(i).getString("fhproceso");
                    producto = jAValores.getJSONObject(i).getString("idproducto");
                    estado = jAValores.getJSONObject(i).getString("idgestion1");
                    subEstado = jAValores.getJSONObject(i).getString("idsubtipo1");

                    CreateDetalleINHouseItem(back, folio, cliente, telefono, departamento, direccion, fContrato, producto, estado, subEstado);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            TextView tvBack, tvFolio, tvCliente, tvTelefono, tvDepartamento, tvDireccion, tvFContrato, tvProducto, tvEstado, tvSubEstado, tvAccion;
            tvBack = (TextView) viewBottomDialog.findViewById(R.id.txtBack);
            tvFolio = (TextView) viewBottomDialog.findViewById(R.id.txtFolio);
            tvCliente = (TextView) viewBottomDialog.findViewById(R.id.txtCliente);
            tvTelefono = (TextView) viewBottomDialog.findViewById(R.id.txtTelefono);
            tvDepartamento = (TextView) viewBottomDialog.findViewById(R.id.txtDepartamento);
            tvDireccion = (TextView) viewBottomDialog.findViewById(R.id.txt_cdireccion);
            tvFContrato = (TextView) viewBottomDialog.findViewById(R.id.txtFechaContrato);
            tvProducto = (TextView) viewBottomDialog.findViewById(R.id.txtProducto);
            tvEstado = (TextView) viewBottomDialog.findViewById(R.id.txtEstado);
            tvSubEstado = (TextView) viewBottomDialog.findViewById(R.id.txtSubEstado);
            tvAccion = (TextView) viewBottomDialog.findViewById(R.id.txtAccion);

            tvBack.setText(back);
            tvFolio.setText(folio);
            tvCliente.setText(cliente);
            tvTelefono.setText(telefono);
            tvDepartamento.setText(departamento);
            tvDireccion.setText(direccion);
            tvFContrato.setText(fContrato);
            tvProducto.setText(producto);
            tvEstado.setText(estado);
            tvSubEstado.setText(subEstado);
            tvAccion.setText("ACCION");


    //ATIS
//        tvspeech.setText(speech);
            tvFechaContratoLabel.setText("Fecha\nContrato");
            tvfhcontrato.setText(fhcontratoATIS);
            tvestado_solicitud.setText(estado_solicitudATIS);
            tvtelefono.setText(telefonoATIS);
//        tvgestion.setText(gestion);
//        tvdetalle_gestion.setText(detalle_gestion);
//        tvback.setText(back);
            tvpeticion_atis.setText(peticion_atis);
//        tvidgestion.setText(idgestion);
//        tvp_peticion.setText(p_peticion);
            tvaccion_asesor.setText(accion_asesorATIS);
            tvAccion_atis.setText(accion_asesorATIS);


//            new CountDownTimer(45000, 1000) {
//                public void onTick(long millisUntilFinished) {
//
//                    tvHoraATIS.setText("Procesando consulta en CMS/ATIS"+ " : " + millisUntilFinished / 1000);
//                    btnContinuar.setVisibility(View.GONE);
//                    btnCancelar.setVisibility(View.GONE);
//                    //here you can have your logic to set text to edittext
//                }
//
//                public void onFinish() {
//                    tvHoraATIS.setText("No se ha podido validar  si su requerimiento tiene un pedido duplicado. Se procederá al registro, pero este podría ser observado más adelante.");
//                    tvHoraATIS.setTextColor(Color.RED);
//                    btnContinuar.setVisibility(View.VISIBLE);
//                    btnCancelar.setVisibility(View.VISIBLE);
//
//                }
//            }.start();

            //recorriendo array de botones
            String field1="", field2, value1, value2;

            try {
                paramBtnContinuar1 = jsonBotonesATIS.getJSONObject(0).getString("field");
                value1 = jsonBotonesATIS.getJSONObject(0).getString("value");
                visible1 = jsonBotonesATIS.getJSONObject(0).getBoolean("visible");
                paramBtnContinuar2 = jsonBotonesATIS.getJSONObject(1).getString("field");
                value2 = jsonBotonesATIS.getJSONObject(1).getString("value");
                visible2 = jsonBotonesATIS.getJSONObject(1).getBoolean("visible");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            btnContinuar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (visible1) {
                        informacion_registrar_visor2("btn_visor_in2_continuar", "", segunda_logATIS, hd_idconsulATIS);
                    }

                    //Muestra la ficha de ventas fija
                    Intent intent1 = null;
                    intent1 = new Intent(ActivityVisorVentasFija.this, ActivityVentasFija.class);
                    intent1.putExtra("pTipoDoc", pCodTipoDoc);
                    intent1.putExtra("pNroDoc", num_doc_cli_2.getText().toString());
                    intent1.putExtra("pOPComercialCode", pCodOperComercialWS);
                    intent1.putExtra("pOPComercialValue", cb_tratamiento_2.getText().toString());
                    intent1.putExtra("pTelfMigra", telefono_migrar_2.getText().toString());
                    intent1.putExtra("pCampaniaCode", pCodCampaniaWS);
                    //intent1.putExtra("pCampaniaValue", actvCampania.getText().toString());
                    intent1.putExtra("pTipoProductoCode", pCodTipoProdWS);
                    intent1.putExtra("pTipoProductoValue", cb_idcategoria_2.getText().toString());
                    intent1.putExtra("pProductoCode", pCodProductoWS);
                    intent1.putExtra("pProductoValue", cb_idproducto_2.getText().toString());
                    intent1.putExtra("pSubProductoCode", pCodSubProductoWS);
                    intent1.putExtra("pSubProductoValue", cb_idprograma_2.getText().toString());
                    intent1.putExtra("pDepCode", pCodDepartamentoWS);
                    intent1.putExtra("pDepValue", txt_cdepartamento_visor.getText().toString());
                    intent1.putExtra("pProvCode", pCodProvinciaWS);
                    intent1.putExtra("pProvValue", txt_cprovincia_visor.getText().toString());
                    intent1.putExtra("pDistCode", pCodDistritoWS);
                    intent1.putExtra("pDistValue", txt_cdistrito_visor.getText().toString());
                    startActivity(intent1);
                }
            });

            btnCancelar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    informacion_registrar_visor2("btn_visor_cancelar", "", segunda_logATIS, hd_idconsulATIS);

                    cb_tratamiento_2.setText("");
                    cb_ctipo_doc_2.setText("");
                    num_doc_cli_2.setText("");
                    telefono_migrar_2.setText("");
                    cb_idcategoria_2.setText("");
                    cb_idproducto_2.setText("");
                    cb_idprograma_2.setText("");
                    cb_tipoIncidencia.setText("");
                    txt_cdepartamento_visor.setText("");
                    txt_cprovincia_visor.setText("");
                    txt_cdistrito_visor.setText("");
                    pCodCampaniaWS=""; pCodProvinciaWS=""; pCodDistritoWS=""; pCodOperComercialWS="";
                    pCodTipoProdWS=""; pCodSubProductoWS="";pCodProductoWS="";

                    dialog.dismiss();
                }
            });

            dialog.setContentView(viewBottomDialog);
            dialog.show();
        }


    }

    private void CreateDetalleINHouseItem(String back, String folio, String cliente, String telefono, String departamento,String direccion,String fcontrato,String producto,String estado2,String subEstado){
        DTO_INHouse dtoInHouse = new DTO_INHouse();
        dtoInHouse.setBack(back);
        dtoInHouse.setFolio(folio);
        dtoInHouse.setCliente(cliente);
        dtoInHouse.setTelefono(telefono);
        dtoInHouse.setDepartamento(departamento);
        dtoInHouse.setDireccion(direccion);
        dtoInHouse.setfContrato(fcontrato);
        dtoInHouse.setProducto(producto);
        dtoInHouse.setEstado(estado2);
        dtoInHouse.setSubEstado(subEstado);
        listaINHOUSE.add(dtoInHouse);
        rvInHouseAdapter.notifyDataSetChanged();
    }

    FragmentInHouse frag;
    FragmentVisorAtis frag2;
    String  paramBtnContinuar1="", paramBtnContinuar2="";
    Boolean visible1=false, visible2=false;
    public void showBottomSheetDialog2(String cabecera1, String cabecera2, String speech, String fhcontrato, String estado_solicitud, String telefono, String gestion, String detalle_gestion, String back,
                                       String peticion_atis, String idgestion, String p_peticion, String accion_asesor, final String mensaje, final String segunda_log, JSONArray jsonBotones, final String hd_idconsul, int tipoVisor) {
        View view=null;
        view = getLayoutInflater().inflate(R.layout.ly_dialog_visor_in_atis, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);

        final TextView tvspeech, tvfhcontrato, tvestado_solicitud, tvtelefono, tvgestion, tvdetalle_gestion, tvback, tvpeticion_atis, tvidgestion, tvp_peticion, tvaccion_asesor, tvHoraATIS;
        final Button btnContinuar, btnContinuar2, btnCancelar;
        //tvspeech = (TextView) view.findViewById(R.id.tvspeech);
        tvfhcontrato = (TextView) view.findViewById(R.id.tvfhcontrato);
        tvestado_solicitud = (TextView) view.findViewById(R.id.tvestado_solicitud);
        tvtelefono = (TextView) view.findViewById(R.id.tvtelefono2);
        //tvgestion = (TextView) view.findViewById(R.id.tvgestion);
//        tvdetalle_gestion = (TextView) view.findViewById(R.id.tvdetalle_gestion);
//        tvback = (TextView) view.findViewById(R.id.tvback2);
        tvpeticion_atis = (TextView) view.findViewById(R.id.tvpeticion_atis);
//        tvidgestion = (TextView) view.findViewById(R.id.tvidgestion);
//        tvp_peticion = (TextView) view.findViewById(R.id.tvp_peticion);
        tvaccion_asesor = (TextView) view.findViewById(R.id.tvaccion_asesor);
        tvHoraATIS = (TextView) view.findViewById(R.id.tvHoraATIS);
        btnContinuar = (Button) view.findViewById(R.id.btnContinuar_atis);
        btnContinuar2 = (Button) view.findViewById(R.id.btnContinuar2_atis);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelar_atis);

//        tvspeech.setText(speech);
        tvfhcontrato.setText(fhcontrato);
        tvestado_solicitud.setText(estado_solicitud);
        tvtelefono.setText(telefono);
//        tvgestion.setText(gestion);
//        tvdetalle_gestion.setText(detalle_gestion);
//        tvback.setText(back);
        tvpeticion_atis.setText(peticion_atis);
//        tvidgestion.setText(idgestion);
//        tvp_peticion.setText(p_peticion);
        tvaccion_asesor.setText(accion_asesor);
        dialog.setContentView(view);
        dialog.show();

        new CountDownTimer(45000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvHoraATIS.setText("Procesando consulta en CMS/ATIS"+ " : " + millisUntilFinished / 1000);
                btnContinuar.setVisibility(View.GONE);
                btnCancelar.setVisibility(View.GONE);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvHoraATIS.setText("No se ha podido validar  si su requerimiento tiene un pedido duplicado. Se procederá al registro, pero este podría ser observado más adelante.");
                tvHoraATIS.setTextColor(Color.RED);
                btnContinuar.setVisibility(View.VISIBLE);
                btnCancelar.setVisibility(View.VISIBLE);
            }
        }.start();

        //recorriendo array de botones
        String field1="", field2, value1, value2;

        try {
            paramBtnContinuar1 = jsonBotones.getJSONObject(0).getString("field");
            value1 = jsonBotones.getJSONObject(0).getString("value");
            visible1 = jsonBotones.getJSONObject(0).getBoolean("visible");
            paramBtnContinuar2 = jsonBotones.getJSONObject(1).getString("field");
            value2 = jsonBotones.getJSONObject(1).getString("value");
            visible2 = jsonBotones.getJSONObject(1).getBoolean("visible");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnContinuar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible1) {
                    informacion_registrar_visor2("btn_visor_in2_continuar", "", segunda_log, hd_idconsul);
                }

                //Muestra la ficha de ventas fija
                Intent intent1 = null;
                intent1 = new Intent(ActivityVisorVentasFija.this, ActivityVentasFija.class);
                intent1.putExtra("pTipoDoc", pCodTipoDoc);
                intent1.putExtra("pNroDoc", num_doc_cli_2.getText().toString());
                intent1.putExtra("pOPComercialCode", pCodOperComercialWS);
                intent1.putExtra("pOPComercialValue", cb_tratamiento_2.getText().toString());
                intent1.putExtra("pTelfMigra", telefono_migrar_2.getText().toString());
                intent1.putExtra("pCampaniaCode", pCodCampaniaWS);
                //intent1.putExtra("pCampaniaValue", actvCampania.getText().toString());
                intent1.putExtra("pTipoProductoCode", pCodTipoProdWS);
                intent1.putExtra("pTipoProductoValue", cb_idcategoria_2.getText().toString());
                intent1.putExtra("pProductoCode", pCodProductoWS);
                intent1.putExtra("pProductoValue", cb_idproducto_2.getText().toString());
                intent1.putExtra("pSubProductoCode", pCodSubProductoWS);
                intent1.putExtra("pSubProductoValue", cb_idprograma_2.getText().toString());
                intent1.putExtra("pDepCode", pCodDepartamentoWS);
                intent1.putExtra("pDepValue", txt_cdepartamento_visor.getText().toString());
                intent1.putExtra("pProvCode", pCodProvinciaWS);
                intent1.putExtra("pProvValue", txt_cprovincia_visor.getText().toString());
                intent1.putExtra("pDistCode", pCodDistritoWS);
                intent1.putExtra("pDistValue", txt_cdistrito_visor.getText().toString());
                startActivity(intent1);
            }
        });

        btnCancelar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (visible2) {
                    informacion_registrar_visor2("btn_visor_cancelar", "", segunda_log, hd_idconsul);
                //}
                 //actvCampania.setText("");
                 cb_tratamiento_2.setText("");
                 cb_ctipo_doc_2.setText("");
                 num_doc_cli_2.setText("");
                 telefono_migrar_2.setText("");
                 cb_idcategoria_2.setText("");
                 cb_idproducto_2.setText("");
                 cb_idprograma_2.setText("");
                 cb_tipoIncidencia.setText("");
                 txt_cdepartamento_visor.setText("");
                 txt_cprovincia_visor.setText("");
                 txt_cdistrito_visor.setText("");
                 pCodCampaniaWS=""; pCodProvinciaWS=""; pCodDistritoWS=""; pCodOperComercialWS="";
                 pCodTipoProdWS=""; pCodSubProductoWS="";pCodProductoWS="";

                 dialog.dismiss();
            }
        });
    }

    private void informacion_registrar_visor2(String field, String primera_log, String segunda_log, String hd_idconsul){
        customDialog.show(); //Inicia dialog
        String WSMethod="";
        WSMethod = "informacion_registrar_visor2";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        params.put("hd_idconsul", hd_idconsul);
        params.put("primera_log", primera_log);
        params.put("segunda_log", segunda_log);
        params.put("id_usuario", ((TGestionaSession) getApplication()).getDniVendedor());
        params.put("nombutton", field);
        params.put("ind", ((TGestionaSession)getApplication()).getTipoIncidencia());
        params.put("dniVendedormodal", ActivityMenuIncidencia.numDocVendedor);
        params.put("dniVendedor", ((TGestionaSession)getApplication()).getDniVendedor());
        params.put("token", ((TGestionaSession)getApplication()).getTokenMovil());
        params.put("id_vendedor", ((TGestionaSession)getApplication()).getDniVendedor());

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        JSONArray jsonArray = null;

                        try {
                            //parse de web service de combos
                            jsonArray = new JSONArray(result);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        customDialog.dismiss(); //Inicia dialog
                    }
                }
        );
    }


        private void showDialogSegmentos(){

            final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
            // con este tema personalizado evitamos los bordes por defecto
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
            customDialog.setCancelable(false); //obligamos al usuario a pulsar los botones para cerrarlo
            customDialog.setContentView(R.layout.ly_dialog_segmento); //establecemos el contenido de nuestro dialog

            ((TextView) customDialog.findViewById(R.id.tvRegresar)).setText("Regresar");
                    ((TextView) customDialog.findViewById(R.id.tvRegresar)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    GlobalFunctions.newIntent(ActivityVisorVentasFija.this,ActivityMenuIncidencia.class);
                }
            });

            ((Button) customDialog.findViewById(R.id.btnResidencial)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    mostrarAlerta();
                    varPublicas.gResidencialNegocios="1";
                    //customDialog.dismiss();
                }
            });

            ((Button) customDialog.findViewById(R.id.btnNegocios)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    num_doc_cli_2.setText("20");
                    cb_ctipo_doc_2.setText("RUC");
                    pCodTipoDoc="RUC";
                    num_doc_cli_2.requestFocus();
                    varPublicas.gResidencialNegocios="2";
                    customDialog.dismiss();
                }
            });
            customDialog.show();
        }

    private View getViewByName(String fieldName){
        int id = getResources().getIdentifier(fieldName, "id", getPackageName());
        View view=null;
        if(id != 0) {
            view = findViewById(id);
        }
//        if (view instanceof EditText){
//            EditText newEdit;
//            newEdit = (EditText)view;
//            newEdit.setError("Rellena coño");
//        }
        return view;
    }

    private void setErrorToViewByItsName(String fieldName, String msgError, String inputType, int position){
        int id = getResources().getIdentifier(fieldName, "id", getPackageName());
        View view=null;
        if(id != 0) {
            view = findViewById(id);
            if (position==0) view.requestFocus();
        }

//        if (inputType.contains("txt")){
//
//            EditText newEdit;
//            newEdit = (EditText)view;
//            newEdit.setError(msgError);
//        }
//
//        if (inputType.contains("select")){
//            AutoCompleteTextView newActv;
//            newActv = (AutoCompleteTextView)view;
//            newActv.setError(msgError);
//        }

        if (view instanceof EditText){
            EditText newEdit;
            newEdit = (EditText)view;
            newEdit.setError(msgError);
        }
        if (view instanceof AutoCompleteTextView){
            AutoCompleteTextView newActv;
            newActv = (AutoCompleteTextView)view;
            newActv.setError(msgError);
        }
    }

    private void recorrerAllEditText (LinearLayout llMain){
        View campoHijo=null;
        EditText tvtField;
        AutoCompleteTextView actvField;

        for( int i = 0; i < llMain.getChildCount(); i++ ) {
            campoHijo = llMain.getChildAt(i);
            if (campoHijo instanceof EditText) {
                tvtField = (EditText) campoHijo;
                tvtField.setError(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btnVerificarDocumento){
            callEnviarDatos();
            //showDialogBI();
            //manda trama json como string en bundle y que el otro activity lo trabaje
        }
    }
}

