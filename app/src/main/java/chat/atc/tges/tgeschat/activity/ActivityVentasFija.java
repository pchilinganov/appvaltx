package chat.atc.tges.tgeschat.activity;

import android.app.FragmentManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_Departamento;
import chat.atc.tges.tgeschat.model.DTO_Distrito;
import chat.atc.tges.tgeschat.model.DTO_Generico;
import chat.atc.tges.tgeschat.model.DTO_OperacionComercial___;
import chat.atc.tges.tgeschat.model.DTO_Provincia;
import chat.atc.tges.tgeschat.util.ExpandCollapse;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.LoginTelefonica.uri;
import static chat.atc.tges.tgeschat.LoginTelefonica.vistaIncidenciaMasiva;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.fillActvFromJSONArray2;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.fillSpinnerFromJSONArray;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.getIndexSpinnerGenericoSelected;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.limpiarShared;
import static chat.atc.tges.tgeschat.util.GlobalFunctions.newIntent;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;

public class ActivityVentasFija extends BaseVolleyActivity implements OnClickListener, AdapterView.OnItemSelectedListener {

    public static FragmentManager fragmentManager;
    //DatePicker
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    //Fin Date Picker

    SharedPreferences sharedpreferences;


    ScrollView scVentasFija;

    //Dialog
    Dialog customDialog;

    //Variables network
    private VolleyRP volley;
    private RequestQueue mRequest;

    Button btn_svas_linea, btn_svas_internet, btn_bloque_canales, btnGuardar;
    RadioButton rb_titular, rb_acreditado;
    //Campos hidden
    TextView walter,  txt_pidsubproducto, txt_bxa, txt_multizonal, hid_provincia, hid_departamento, hid_idprograma, hid_idproducto,
            hid_idcategoria, txt_actuacion_tecnica, txt_promocion2, txt_ps_bloques, txt_promocion, txt_ps_descuento, txt_ps_oferta, txt_referido, txt_fhproceso,
            txt_nom_user, txt_tipo_venta, txt_idventa, txt_iduser, txt_cpostal, Dni_Representante_Legal_tv, Nombre_Representante_Legal_tv, txt_nombre_nuevo_titular_tv, txt_apepa_nuevo_titular_tv, txt_apema_nuevo_titular_tv, txt_dni_nuevo_titular_tv,
            txt_cdepartamento_tv, txt_cprovincia_tv, txt_cdistrito_tv, idpreventa_tv, txt_nombre_call_tv, txt_detalle_call_tv;

    ImageView imgVer, imgVer2, imgVer3, imgVer4, imgVer5, imgVer6;

    EditText rb_otros, txt_telefono, txt_fecha_nacimiento, txt_nombre_padre, txt_nombre_madre, txt_telefono_referencia, txt_telefono_celular,  txt_cemail, txtDominioRV, txt_web_experto, txt_cndoc, txt_nom_cliente, txt_apepaterno_cliente, txt_apematerno_cliente,
            Dni_Representante_Legal, Nombre_Representante_Legal, txt_cdireccion, txt_coordenadas_x, txt_coordenadas_y, txt_nombre_nuevo_titular, txt_apepa_nuevo_titular, txt_apema_nuevo_titular, txt_dni_nuevo_titular,
            txt_call_asesor_dni, txt_codigo_atis, txt_codigo_con, txt_cliente_cms, txt_servicio_cms, txt_idgrabacion, txt_plan_embajador,
            txt_cod_empleado, txt_baja_ps_descuento, txt_ps_administrativa, txt_equifax, txt_observacion, serie_equipo6, serie_equipo5, serie_equipo4, serie_equipo3, serie_equipo2, serie_equipo1,
            txt_rwdistrito, txt_rwprovincia, txt_rwdepartamento, txt_codigo_cip, idpreventa, txt_nombre_call, txt_detalle_call;

    LinearLayout llMain;
    RelativeLayout rlDatos, rlDatosDC, rlDatosCli, rlDatosCallCenterData, rlDatosVentaData, rlDatosVentaProductos;

    public static EditText txt_svalinea, txt_svainternet, txt_bloque_canales;

    //Codigos de Combos
    String  pDominioEmail="", pCodTitularFallecido = "", pCodServicio="", pCodLugarNacimiento = "", pCodMedioContratacion = "", pCodEnvioContrato = "", pCodFacturaDigital = "", pCodFacturaDigitalSms = "",
            pCodCompartirInformacion = "", pCodModalidadPago = "", pCodMonto = "", pCodDebitoAutomatico = "", pCodTipoTecnologia = "", pCodRepetidorSmartWifi = "",
            pCodBloqueProducto = "", pCodEdificio = "", pCodPaginasBlancas = "", pCodPublicarGuia = "", pCodCuotasFinanciamiento = "", pCodPreRePaq = "", pCodAplicaZonaDigitalizacion = "",
            pCodSVALinea = "", pCodSVAInternet = "", pCodDecosAdicionales = "", pCodAdecoSH = "", pCodAdecoHD = "", pCodAdecoSMART = "", pCodAdecoDVRHD = "", pCodDecodificadorAdicional = "",
            pCodCuotasDecodificador="", pCodTipoDecos="", pCodPromoModemMG="", pCodDecoPropiedadCliente="", pCodReemplazoPuntoSmart="", pCodReemplazoPuntoAdicionalSD="",
            pCodReemplazoPuntoAdicionalHD="", pCodReemplazoPuntoAdicionalDVR="", pCodReemplazoComodatoSDHD="", pCodPaquetizacion="", pCodVelocidadPromocional="",
            pCodTipoEquipoDeco="", pCodDecoHDPromoDIG="";

    //Listas y variables de combos dependientes
    List<DTO_Departamento> listaDepartamentos=new ArrayList<>(); //Array que contiene el ubigeo completo
    List<DTO_Provincia> listaProvincias=new ArrayList<>(); //Array que contiene el ubigeo completo
    List<DTO_Distrito> listaDistritos=new ArrayList<>(); //Array que contiene el ubigeo completo
    List <String> sListaDepartamentos = new ArrayList<>(); // Array que contiene los departamentos para combo
    List <String> sListaProvincias = new ArrayList<>(); // Array que contiene los provincias para combo
    List <String> sListaDistritos = new ArrayList<>(); // Array que contiene los provincias para combo
    List<DTO_OperacionComercial___> listOperacionComercial = new ArrayList<>(); //Array que contiene Operacion comercial completa
    List<DTO_Generico> list_cb_tratamiento, list_rb_emails, list_cb_titular_fallecido, list_cb_medio_contratacion, list_cb_envio_contrato,
            list_cb_factura_digital, list_cb_compartir_informacion, list_cb_modalidad_pago, list_txt_monto, list_txt_web_experto, list_cb_debito_automatico,
            list_cb_idcuenta, list_cb_tipo_tecnologia, list_cb_repetidor_smart_wifi,
            list_cb_ctipo_doc, list_cb_idcategoria, list_cb_idproducto, list_cb_idprograma,list_cb_bloque_producto,
            list_txt_lugar_nacimiento, list_cb_nacionalidad, list_cb_edificio, list_cb_factura_digital_sms,
            list_cb_paginas_blancas, list_cb_publicar_guia, list_cb_cuotas_financiamiento, list_cb_pre_re_paq,
            list_aplica_zona_digitalizacion, list_cb_svalinea, list_cb_svainternet, list_cb_decos_adicionales,
            list_cb_adeco_sh, list_cb_adeco_hd, list_cb_adeco_smart, list_cb_adeco_dvrhd, list_cb_decodificador_adicional, list_cb_cuotas_decodificador,
            list_cb_tipo_decos, list_cb_promo_modem_mg, list_cb_deco_propiedad_cliente, list_reemplazo_punto_smart, list_reemplazo_punto_adicional_sd, list_reemplazo_punto_adicional_hd,
            list_reemplazo_punto_adicional_dvr, list_reemplazo_comodato_sd_hd, list_cb_tipo_servicio, list_cb_paquetizacion, list_cb_velocidad_promocional, list_cb_tipo_equip_deco,
            list_cb_deco_hd_promo_dig, list_cb_plan,list_cb_servicios, list_txt_cdepartamento
            = new ArrayList<>();
    Spinner cb_orden, cb_tratamiento, cb_ctipo_doc, txt_lugar_nacimiento, txt_localidad, cb_titular_fallecido, cb_medio_contratacion, cb_envio_contrato, cb_factura_digital, cb_factura_digital_sms, cb_paginas_blancas, cb_publicar_guia,
            cb_compartir_informacion, cb_modalidad_pago, txt_monto, cb_debito_automatico, cb_cuotas_financiamiento, cb_idcuenta, cb_idcategoria, cb_idproducto, cb_idprograma, cb_pre_re_paq, cb_bloque_producto, cb_tipo_tecnologia,
            aplica_zona_digitalizacion, cb_svalinea, cb_svainternet, cb_bloquetv, cb_tipo_equip_deco, cb_repetidor_smart_wifi, cb_decos_adicionales, cb_adeco_sh, cb_adeco_hd, cb_adeco_smart, cb_adeco_dvrhd, cb_decodificador_adicional,
            cb_cuotas_decodificador, cb_tipo_decos, cb_promo_modem_mg, cb_deco_hd_promo_dig, cb_velocidad_promocional, cb_deco_propiedad_cliente, reemplazo_punto_smart, reemplazo_punto_adicional_sd, reemplazo_punto_adicional_hd,
            reemplazo_punto_adicional_dvr, reemplazo_comodato_sd_hd, cb_tipo_servicio, cb_paquetizacion, cb_edificio,
            rb_emails,txt_cdepartamento, txt_cprovincia, txt_cdistrito, cb_nacionalidad;

    AutoCompleteTextView  cb_plan, cb_servicios;
    private MenuItem mGuardar;

    int posOpComercial=-1,  posDepartamento=-1, posProvincia=-1, posTipoPro =-1, posPro=-1;

    JSONArray jATipoDocVisor;
    //Fin de combos dependientes

    //parametros de ws de visor
    String pCodTipoDoc, pCodDepartamentoWS, pCodProvinciaWS, pCodDistritoWS, pCodOperComercialWS, pCodTipoProdWS, pCodProductoWS, pCodSubProductoWS;
    //Fin parametros ws visor

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_ventas_fija);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);  //Oculta teclado
        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        fragmentManager= getFragmentManager();

        //parsing
        cb_tratamiento = (Spinner)findViewById(R.id.cb_tratamiento);
        cb_tratamiento.setOnItemSelectedListener(this);
        cb_ctipo_doc = (Spinner)findViewById(R.id.cb_ctipo_doc);
        cb_ctipo_doc.setOnItemSelectedListener(this);
        txt_lugar_nacimiento = (Spinner)findViewById(R.id.txt_lugar_nacimiento);
        txt_lugar_nacimiento.setOnItemSelectedListener(this);
        txt_localidad = (Spinner)findViewById(R.id.txt_localidad);
        txt_localidad.setOnItemSelectedListener(this);
        cb_titular_fallecido = (Spinner)findViewById(R.id.cb_titular_fallecido);
        cb_titular_fallecido.setOnItemSelectedListener(this);
        cb_medio_contratacion = (Spinner)findViewById(R.id.cb_medio_contratacion);
        cb_medio_contratacion.setOnItemSelectedListener(this);
        cb_envio_contrato = (Spinner)findViewById(R.id.cb_envio_contrato);
        cb_envio_contrato.setOnItemSelectedListener(this);
        cb_factura_digital = (Spinner)findViewById(R.id.cb_factura_digital);
        cb_factura_digital.setOnItemSelectedListener(this);
        cb_factura_digital_sms = (Spinner)findViewById(R.id.cb_factura_digital_sms);
        cb_factura_digital_sms.setOnItemSelectedListener(this);
        cb_paginas_blancas = (Spinner)findViewById(R.id.cb_paginas_blancas);
        cb_paginas_blancas.setOnItemSelectedListener(this);
        cb_publicar_guia = (Spinner)findViewById(R.id.cb_publicar_guia);
        cb_publicar_guia.setOnItemSelectedListener(this);
        cb_compartir_informacion = (Spinner)findViewById(R.id.cb_compartir_informacion);
        cb_compartir_informacion.setOnItemSelectedListener(this);
        cb_modalidad_pago = (Spinner)findViewById(R.id.cb_modalidad_pago);
        cb_modalidad_pago.setOnItemSelectedListener(this);

        txt_monto = (Spinner)findViewById(R.id.txt_monto);
        txt_monto.setOnItemSelectedListener(this);
        cb_debito_automatico = (Spinner)findViewById(R.id.cb_debito_automatico);
        cb_debito_automatico.setOnItemSelectedListener(this);
        cb_cuotas_financiamiento = (Spinner)findViewById(R.id.cb_cuotas_financiamiento);
        cb_cuotas_financiamiento.setOnItemSelectedListener(this);
        cb_idcuenta = (Spinner)findViewById(R.id.cb_idcuenta);
        cb_idcuenta.setOnItemSelectedListener(this);
        cb_idcategoria = (Spinner)findViewById(R.id.cb_idcategoria);
        cb_idcategoria.setOnItemSelectedListener(this);
        cb_idproducto = (Spinner)findViewById(R.id.cb_idproducto);
        cb_idproducto.setOnItemSelectedListener(this);
        cb_idprograma = (Spinner)findViewById(R.id.cb_idprograma);
        cb_idprograma.setOnItemSelectedListener(this);
        cb_pre_re_paq = (Spinner)findViewById(R.id.cb_pre_re_paq);
        cb_pre_re_paq.setOnItemSelectedListener(this);
        cb_bloque_producto = (Spinner)findViewById(R.id.cb_bloque_producto);
        cb_bloque_producto.setOnItemSelectedListener(this);
        cb_tipo_tecnologia = (Spinner)findViewById(R.id.cb_tipo_tecnologia);
        cb_tipo_tecnologia.setOnItemSelectedListener(this);
        aplica_zona_digitalizacion = (Spinner)findViewById(R.id.aplica_zona_digitalizacion);
        aplica_zona_digitalizacion.setOnItemSelectedListener(this);
        cb_svalinea = (Spinner)findViewById(R.id.cb_svalinea);
        cb_svalinea.setOnItemSelectedListener(this);
        cb_bloquetv = (Spinner)findViewById(R.id.cb_bloquetv);
        cb_bloquetv.setOnItemSelectedListener(this);
        cb_tipo_equip_deco = (Spinner)findViewById(R.id.cb_tipo_equip_deco);
        cb_tipo_equip_deco.setOnItemSelectedListener(this);
        cb_repetidor_smart_wifi = (Spinner)findViewById(R.id.cb_repetidor_smart_wifi);
        cb_repetidor_smart_wifi.setOnItemSelectedListener(this);
        cb_decos_adicionales = (Spinner)findViewById(R.id.cb_decos_adicionales);
        cb_decos_adicionales.setOnItemSelectedListener(this);
        cb_adeco_sh = (Spinner)findViewById(R.id.cb_adeco_sh);
        cb_adeco_sh.setOnItemSelectedListener(this);
        cb_adeco_hd = (Spinner)findViewById(R.id.cb_adeco_hd);
        cb_adeco_hd.setOnItemSelectedListener(this);
        cb_adeco_smart = (Spinner)findViewById(R.id.cb_adeco_smart);
        cb_adeco_smart.setOnItemSelectedListener(this);
        cb_adeco_dvrhd = (Spinner)findViewById(R.id.cb_adeco_dvrhd);
        cb_adeco_dvrhd.setOnItemSelectedListener(this);
        cb_decodificador_adicional = (Spinner)findViewById(R.id.cb_decodificador_adicional);
        cb_decodificador_adicional.setOnItemSelectedListener(this);
        cb_cuotas_decodificador = (Spinner)findViewById(R.id.cb_cuotas_decodificador);
        cb_cuotas_decodificador.setOnItemSelectedListener(this);
        cb_tipo_decos = (Spinner)findViewById(R.id.cb_tipo_decos);
        cb_tipo_decos.setOnItemSelectedListener(this);
        cb_promo_modem_mg = (Spinner)findViewById(R.id.cb_promo_modem_mg);
        cb_promo_modem_mg.setOnItemSelectedListener(this);
        cb_deco_hd_promo_dig = (Spinner)findViewById(R.id.cb_deco_hd_promo_dig);
        cb_deco_hd_promo_dig.setOnItemSelectedListener(this);
        cb_velocidad_promocional = (Spinner)findViewById(R.id.cb_velocidad_promocional);
        cb_velocidad_promocional.setOnItemSelectedListener(this);
        cb_deco_propiedad_cliente = (Spinner)findViewById(R.id.cb_deco_propiedad_cliente);
        cb_deco_propiedad_cliente.setOnItemSelectedListener(this);
        reemplazo_punto_smart = (Spinner)findViewById(R.id.reemplazo_punto_smart);
        reemplazo_punto_smart.setOnItemSelectedListener(this);
        reemplazo_punto_adicional_sd = (Spinner)findViewById(R.id.reemplazo_punto_adicional_sd);
        reemplazo_punto_adicional_sd.setOnItemSelectedListener(this);
        reemplazo_punto_adicional_hd = (Spinner)findViewById(R.id.reemplazo_punto_adicional_hd);
        reemplazo_punto_adicional_hd.setOnItemSelectedListener(this);
        reemplazo_punto_adicional_dvr = (Spinner)findViewById(R.id.reemplazo_punto_adicional_dvr);
        reemplazo_punto_adicional_dvr.setOnItemSelectedListener(this);
        reemplazo_comodato_sd_hd = (Spinner)findViewById(R.id.reemplazo_comodato_sd_hd);
        reemplazo_comodato_sd_hd.setOnItemSelectedListener(this);
        cb_tipo_servicio = (Spinner)findViewById(R.id.cb_tipo_servicio);
        cb_tipo_servicio.setOnItemSelectedListener(this);
        cb_paquetizacion = (Spinner)findViewById(R.id.cb_paquetizacion);
        cb_paquetizacion.setOnItemSelectedListener(this);

        scVentasFija = (ScrollView)findViewById(R.id.scVentasFija);
        idpreventa_tv  =  (TextView) findViewById(R.id.idpreventa_tv);
        txt_cdepartamento_tv  =  (TextView) findViewById(R.id.txt_cdepartamento_tv);
        txt_cprovincia_tv  =  (TextView) findViewById(R.id.txt_cprovincia_tv);
        txt_cdistrito_tv  =  (TextView) findViewById(R.id.txt_cdistrito_tv);
        llMain = (LinearLayout) findViewById(R.id.llMain);
        walter =  (TextView) findViewById(R.id.walter);
        txt_pidsubproducto = (TextView) findViewById(R.id.txt_pidsubproducto);
        txt_bxa = (TextView) findViewById(R.id.txt_bxa);
        txt_multizonal = (TextView) findViewById(R.id.txt_multizonal);
        hid_provincia = (TextView) findViewById(R.id.hid_provincia);
        hid_departamento = (TextView) findViewById(R.id.hid_departamento);
        hid_idprograma = (TextView) findViewById(R.id.hid_idprograma);
        hid_idproducto = (TextView) findViewById(R.id.hid_idproducto);
        hid_idcategoria = (TextView) findViewById(R.id.hid_idcategoria);
        txt_actuacion_tecnica = (TextView) findViewById(R.id.txt_actuacion_tecnica);
        txt_promocion2 = (TextView) findViewById(R.id.txt_promocion2);
        txt_ps_bloques = (TextView) findViewById(R.id.txt_ps_bloques);
        hid_idcategoria = (TextView) findViewById(R.id.hid_idcategoria);
        txt_promocion = (TextView) findViewById(R.id.txt_promocion);
        txt_ps_descuento = (TextView) findViewById(R.id.txt_ps_descuento);
        txt_referido = (TextView) findViewById(R.id.txt_referido);
        txt_fhproceso = (TextView) findViewById(R.id.txt_fhproceso);
        txt_nom_user = (TextView) findViewById(R.id.txt_nom_user);
        txt_tipo_venta = (TextView) findViewById(R.id.txt_tipo_venta);
        txt_idventa = (TextView) findViewById(R.id.txt_idventa);
        txt_iduser = (TextView) findViewById(R.id.txt_iduser);
        txt_cpostal = (TextView) findViewById(R.id.txt_cpostal);
        txt_codigo_cip = (EditText) findViewById(R.id.txt_codigo_cip);
        Dni_Representante_Legal_tv  = (TextView) findViewById(R.id.Dni_Representante_Legal_tv);
        Nombre_Representante_Legal_tv = (TextView) findViewById(R.id.Nombre_Representante_Legal_tv);
        txt_nombre_nuevo_titular_tv  = (TextView) findViewById(R.id.txt_nombre_nuevo_titular_tv);
        txt_apepa_nuevo_titular_tv = (TextView) findViewById(R.id.txt_apepa_nuevo_titular_tv);
        txt_apema_nuevo_titular_tv = (TextView) findViewById(R.id.txt_apema_nuevo_titular_tv);
        txt_dni_nuevo_titular_tv = (TextView) findViewById(R.id.txt_dni_nuevo_titular_tv);
        txt_nombre_call_tv = (TextView) findViewById(R.id.txt_nombre_call_tv);
        txt_detalle_call_tv = (TextView) findViewById(R.id.txt_detalle_call_tv);
        txt_plan_embajador  = (EditText) findViewById(R.id.txt_plan_embajador);
        txt_telefono =(EditText)findViewById(R.id.txt_telefono);
        txt_observacion = (EditText)findViewById(R.id.txt_observacion);
        txt_nombre_call = (EditText) findViewById(R.id.txt_nombre_call);
        txt_detalle_call = (EditText) findViewById(R.id.txt_detalle_call);
        idpreventa = (EditText)findViewById(R.id.idpreventa);
//        cb_tratamiento = (AutoCompleteTextView)  findViewById(R.id.cb_tratamiento);
//        cb_tratamiento.setOnItemSelectedListener(this);
        cb_tratamiento.setOnItemSelectedListener(this);
//        cb_ctipo_doc = (AutoCompleteTextView)  findViewById(R.id.cb_ctipo_doc);
//        cb_ctipo_doc.setOnItemSelectedListener(this);
        cb_ctipo_doc.setOnItemSelectedListener(this);
        txt_cdepartamento = (Spinner)  findViewById(R.id.txt_cdepartamento);
        txt_cdepartamento.setOnItemSelectedListener(this);
        txt_cprovincia = (Spinner)  findViewById(R.id.txt_cprovincia);
        txt_cprovincia.setOnItemSelectedListener(this);
        txt_cdistrito = (Spinner)  findViewById(R.id.txt_cdistrito);
        txt_cdistrito.setOnItemSelectedListener(this);
//        txt_lugar_nacimiento  = (AutoCompleteTextView)  findViewById(R.id.txt_lugar_nacimiento);
        txt_lugar_nacimiento.setOnItemSelectedListener(this);
        txt_idgrabacion = (EditText)  findViewById(R.id.txt_idgrabacion);
        rb_emails   = (Spinner)  findViewById(R.id.rb_emails);
        rb_emails.setOnItemSelectedListener(this);
        cb_edificio   = (Spinner)  findViewById(R.id.cb_edificio);
        cb_nacionalidad = (Spinner)  findViewById(R.id.cb_nacionalidad);
        cb_nacionalidad.setOnItemSelectedListener(this);
//        txt_monto   = (AutoCompleteTextView)  findViewById(R.id.txt_monto);
//        cb_titular_fallecido = (AutoCompleteTextView)  findViewById(R.id.cb_titular_fallecido);
//        cb_medio_contratacion = (AutoCompleteTextView)  findViewById(R.id.cb_medio_contratacion);
//        cb_envio_contrato = (AutoCompleteTextView)  findViewById(R.id.cb_envio_contrato);
//        cb_factura_digital = (AutoCompleteTextView)  findViewById(R.id.cb_factura_digital);
//        cb_factura_digital_sms = (AutoCompleteTextView)  findViewById(R.id.cb_factura_digital_sms);
//        cb_compartir_informacion = (AutoCompleteTextView)  findViewById(R.id.cb_compartir_informacion);
//        cb_modalidad_pago = (AutoCompleteTextView)  findViewById(R.id.cb_modalidad_pago);

//        cb_publicar_guia = (AutoCompleteTextView)  findViewById(R.id.cb_publicar_guia);
//        cb_cuotas_financiamiento  = (AutoCompleteTextView)  findViewById(R.id.cb_cuotas_financiamiento);
        txt_web_experto = (EditText)  findViewById(R.id.txt_web_experto);
//        cb_tipo_equip_deco = (AutoCompleteTextView)  findViewById(R.id.cb_tipo_equip_deco);
//        cb_debito_automatico = (AutoCompleteTextView)  findViewById(R.id.cb_debito_automatico);
//        cb_paginas_blancas = (AutoCompleteTextView)  findViewById(R.id.cb_paginas_blancas);
//        cb_idcuenta = (AutoCompleteTextView)  findViewById(R.id.cb_idcuenta);
//        cb_pre_re_paq = (AutoCompleteTextView)  findViewById(R.id.cb_pre_re_paq);
//        aplica_zona_digitalizacion = (AutoCompleteTextView)  findViewById(R.id.aplica_zona_digitalizacion);
//        cb_svalinea = (AutoCompleteTextView)  findViewById(R.id.cb_svalinea);
        cb_svainternet = (Spinner)  findViewById(R.id.cb_svainternet);

        cb_orden  = (Spinner)  findViewById(R.id.cb_orden);
        cb_orden.setOnItemSelectedListener(this);

//        cb_idcategoria = (AutoCompleteTextView)  findViewById(R.id.cb_idcategoria);
//        cb_idproducto = (AutoCompleteTextView)  findViewById(R.id.cb_idproducto);
//        cb_idprograma = (AutoCompleteTextView)  findViewById(R.id.cb_idprograma);
//        cb_bloque_producto  = (AutoCompleteTextView)  findViewById(R.id.cb_bloque_producto);
//        cb_tipo_tecnologia = (AutoCompleteTextView)  findViewById(R.id.cb_tipo_tecnologia);

        cb_plan =  (AutoCompleteTextView)  findViewById(R.id.cb_plan);
        cb_servicios =  (AutoCompleteTextView)  findViewById(R.id.cb_servicios);

        txt_telefono_referencia = (EditText) findViewById(R.id.txt_telefono_referencia);
        txt_telefono_celular = (EditText) findViewById(R.id.txt_telefono_celular);
        txt_cemail = (EditText) findViewById(R.id.txt_cemail);
        txt_cndoc = (EditText) findViewById(R.id.txt_cndoc);
        txt_nom_cliente = (EditText) findViewById(R.id.txt_nom_cliente);
        txt_apepaterno_cliente = (EditText) findViewById(R.id.txt_apepaterno_cliente);
        txt_apematerno_cliente = (EditText) findViewById(R.id.txt_apematerno_cliente);
        txt_fecha_nacimiento = (EditText) findViewById(R.id.txt_fecha_nacimiento);

        txt_fecha_nacimiento.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha();
            }
        });
        txt_fecha_nacimiento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    obtenerFecha();
                    //txt_fecha_nacimiento.seted(false);
                }
                //Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
            }
        });
        txt_nombre_madre = (EditText) findViewById(R.id.txt_nombre_madre);
        txt_nombre_padre = (EditText) findViewById(R.id.txt_nombre_padre);
        Dni_Representante_Legal = (EditText) findViewById(R.id.Dni_Representante_Legal);
        rb_otros = (EditText) findViewById(R.id.rb_otros);
        Nombre_Representante_Legal = (EditText) findViewById(R.id.Nombre_Representante_Legal);
        txt_cdireccion = (EditText) findViewById(R.id.txt_cdireccion);
        txt_coordenadas_x = (EditText) findViewById(R.id.txt_coordenadas_x);
        txt_coordenadas_y = (EditText) findViewById(R.id.txt_coordenadas_y);
        txt_nombre_nuevo_titular = (EditText) findViewById(R.id.txt_nombre_nuevo_titular);
        txt_apepa_nuevo_titular = (EditText) findViewById(R.id.txt_apepa_nuevo_titular);
        txt_apema_nuevo_titular = (EditText) findViewById(R.id.txt_apema_nuevo_titular);
        txt_dni_nuevo_titular = (EditText) findViewById(R.id.txt_dni_nuevo_titular);
        txt_call_asesor_dni = (EditText) findViewById(R.id.txt_call_asesor_dni);
        txt_codigo_atis = (EditText) findViewById(R.id.txt_codigo_atis);
        txt_codigo_atis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (txt_codigo_atis.length()>0){
                        String textoPrimerCaracter = txt_codigo_atis.getText().toString().substring(0,1);
                        if (textoPrimerCaracter.equalsIgnoreCase("0")){
                            txt_codigo_atis.setError("Nro. Atis no pasa validación");
                        }
                        else
                        {
                            txt_codigo_atis.setError(null);
                        }
                    }
                }
            }
        });
        txt_codigo_con = (EditText) findViewById(R.id.txt_codigo_con);
        txt_dni_nuevo_titular = (EditText) findViewById(R.id.txt_dni_nuevo_titular);
        txt_svalinea = (EditText) findViewById(R.id.txt_svalinea);
        txt_svainternet = (EditText) findViewById(R.id.txt_svainternet);
        txt_cliente_cms = (EditText) findViewById(R.id.txt_cliente_cms);
        txt_servicio_cms = (EditText) findViewById(R.id.txt_servicio_cms);
        txt_rwdistrito = (EditText) findViewById(R.id.txt_rwdistrito);
        txt_rwprovincia = (EditText) findViewById(R.id.txt_rwprovincia);
        txt_rwdepartamento = (EditText) findViewById(R.id.txt_rwdepartamento);
        txt_bloque_canales = (EditText) findViewById(R.id.txt_bloque_canales);
        txt_cod_empleado  = (EditText) findViewById(R.id.txt_cod_empleado);
        txt_baja_ps_descuento = (EditText) findViewById(R.id.txt_baja_ps_descuento);
        txt_ps_administrativa  = (EditText) findViewById(R.id.txt_ps_administrativa);
        txt_equifax  = (EditText) findViewById(R.id.txt_equifax);
        serie_equipo1  = (EditText) findViewById(R.id.serie_equipo1);
        serie_equipo2  = (EditText) findViewById(R.id.serie_equipo2);
        serie_equipo3  = (EditText) findViewById(R.id.serie_equipo3);
        serie_equipo4  = (EditText) findViewById(R.id.serie_equipo4);
        serie_equipo5  = (EditText) findViewById(R.id.serie_equipo5);
        serie_equipo6  = (EditText) findViewById(R.id.serie_equipo6);
        imgVer = (ImageView)  findViewById(R.id.imgVerMas);
        imgVer2 = (ImageView)  findViewById(R.id.imgVerMas2);
        imgVer3 = (ImageView)  findViewById(R.id.imgVerMas3);
        imgVer4 = (ImageView)  findViewById(R.id.imgVerMas4);
        imgVer5 = (ImageView)  findViewById(R.id.imgVerMas5);
        imgVer6 = (ImageView)  findViewById(R.id.imgVerMas6);
        imgVer.setOnClickListener(this);
        imgVer2.setOnClickListener(this);
        imgVer3.setOnClickListener(this);
        imgVer4.setOnClickListener(this);
        imgVer5.setOnClickListener(this);
        imgVer6.setOnClickListener(this);
        rlDatos = (RelativeLayout)  findViewById(R.id.rlDatos);
        rlDatosDC = (RelativeLayout)  findViewById(R.id.rlDatosDC);
        rlDatosCli = (RelativeLayout)  findViewById(R.id.rlDatosCli);
        rlDatosCallCenterData = (RelativeLayout)  findViewById(R.id.rlDatosCallCenterData);
        rlDatosVentaData = (RelativeLayout)  findViewById(R.id.rlDatosVentaData);
        rlDatosVentaProductos = (RelativeLayout)  findViewById(R.id.rlDatosVentaProductos);

        btn_svas_linea = (Button) findViewById(R.id.btn_svas_linea);
        btn_svas_internet = (Button) findViewById(R.id.btn_svas_internet);
        btn_bloque_canales = (Button) findViewById(R.id.btn_bloque_canales);
        btn_svas_linea.setOnClickListener(this);
        btn_svas_internet.setOnClickListener(this);
        btn_bloque_canales.setOnClickListener(this);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(this);

        //Progress
        customDialog = new Dialog(ActivityVentasFija.this,R.style.Theme_Dialog_Translucent);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(false); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_loading); //establecemos el contenido de nuestro dialog

        rb_titular = (RadioButton) findViewById(R.id.rb_titular);
        rb_acreditado = (RadioButton) findViewById(R.id.rb_acreditado);

        //getSupportActionBar().hide(); // oculta ActionBar
        getSupportActionBar().setTitle("MTA - Ventas Fija");
        getDataFromBundle();
        listarCombos();

        loadPreferences();



        ExpandAll();
        txt_telefono.requestFocus();
        //CollapseAll();

        //Dni de asesor aparece por defecto
        txt_call_asesor_dni.setText(((TGestionaSession)getApplication()).getDniVendedor());
        //tipoDoc="DNI";
        if (tipoDoc.equalsIgnoreCase("RUC")) // validacion de web cb_ctipo_doc_changed
        {
            Nombre_Representante_Legal.setVisibility(View.VISIBLE);
            Nombre_Representante_Legal_tv.setVisibility(View.VISIBLE);
            Dni_Representante_Legal_tv.setVisibility(View.VISIBLE);
            Dni_Representante_Legal.setVisibility(View.VISIBLE);
            txt_cndoc.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(11)});
        }
        else if (tipoDoc.equalsIgnoreCase("DNI")) // validacion de web cb_ctipo_doc_changed
        {
            txt_cndoc.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(8)});
        } if (tipoDoc.equalsIgnoreCase("CE")) // validacion de web cb_ctipo_doc_changed
        {
            txt_cndoc.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(12)});
        }

//        pCodTipoDoc ="DNI";
//        pCodOperComercialWS ="ALT";
//        pCodTipoProdWS ="20";
//        pCodProductoWS ="168";
//        pCodSubProductoWS="12182397";
//        pCodDepartamentoWS ="AREQUIPA";
//        pCodProvinciaWS ="AREQUIPA";
//        pCodDistritoWS ="PAUCARPATA";
//        ExpandCollapse.Expand(rlDatos);
        //ExpandCollapse.Expand(rlDatos);
//
//        cb_tratamiento.setError("error");
//        cb_tratamiento.requestFocus();
        //getDataFromBundle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ficha_venta, menu);
        mGuardar = menu.findItem(R.id.action_Save);

        return true;
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

    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; ++i)
            {
                if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890]*").matcher(String.valueOf(source.charAt(i))).matches())
                {
                    return "";
                }
            }

            return null;
        }
    };



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_Save) {
            callRegistrarFicha("ficha_fija_validar");
            //Toast.makeText(this, "hola", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.action_GoMenu){
            newIntent(this, ActivityMenu.class);
        }

        if (id == R.id.action_New){
            finish();
            newIntent(this, ActivityMenuIncidencia.class);
        }

        if (id == R.id.action_Notificaciones) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                mostrarDialogoIncidenciaMasiva();
            }else{
                Toast.makeText(ActivityVentasFija.this,"Revise su conexión a internet.", Toast.LENGTH_LONG).show();
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

        if (id == R.id.action_CerrarSesion) {
            GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
            if (sharedpreferences.getBoolean("flagVentas",false)){
                finishAffinity();
            } else{
                newIntent(ActivityVentasFija.this, LoginTelefonica.class);
                uri = null;
            }
            limpiarShared(this);

            Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
            vistaIncidenciaMasiva=false;
        }
        return super.onOptionsItemSelected(item);
    }

    String tipoDoc, nroDoc, opComercialCode, opComercialValue, telfMigra, pCampaniaCode, pCampaniaValue, tipoProductoCode,tipoProductoValue, productoCode, productoValue, subProductoCode, subProductoValue, depCode, depValue, provCode, provValue, distCode, distValue;
    private void getDataFromBundle() {
        Bundle parametros = this.getIntent().getExtras();

        tipoDoc = parametros.getString("pTipoDoc");
        nroDoc = parametros.getString("pNroDoc");
        opComercialCode = parametros.getString("pOPComercialCode");
        opComercialValue = parametros.getString("pOPComercialValue");
//        cb_tratamiento.setEnabled(true);
//        cb_tratamiento.setText(opComercialValue);
        telfMigra = parametros.getString("pTelfMigra");
        pCampaniaCode = parametros.getString("pCampaniaCode");
        pCampaniaValue = parametros.getString("pCampaniaValue");
//        cb_idcuenta.setEnabled(true);
//        cb_idcuenta.setText(pCampaniaValue);
        tipoProductoCode = parametros.getString("pTipoProductoCode");
        tipoProductoValue = parametros.getString("pTipoProductoValue");
//        cb_idcategoria.setEnabled(true);
//        cb_idcategoria.setText(tipoProductoValue);
        productoCode = parametros.getString("pProductoCode");
        productoValue = parametros.getString("pProductoValue");
//        cb_idproducto.setEnabled(true);
//        cb_idproducto.setText(productoValue);
        subProductoCode = parametros.getString("pSubProductoCode");
        subProductoValue = parametros.getString("pSubProductoValue");
//        cb_idprograma.setEnabled(true);
//        cb_idprograma.setText(subProductoValue);
        codDepartamento = depCode = parametros.getString("pDepCode");
        depValue = parametros.getString("pDepValue");
//        txt_cdepartamento.setEnabled(true);
//        txt_cdepartamento.setText(depValue);
        codProvincia = provCode = parametros.getString("pProvCode");
        provValue = parametros.getString("pProvValue");
//        txt_cprovincia.setText(provValue);
        codDistrito = distCode = parametros.getString("pDistCode");
        distValue = parametros.getString("pDistValue");
//        txt_cdistrito.setText(distValue);
    }

    int iDepSeleccionado=-1, iProvSeleccionado=-1, iDistSeleccionado=-1;
    private void listarCombos(){
        String WSMethod="";

        customDialog.show();
        WSMethod = "in_ficha_gestion_referido";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_vendedor", ((TGestionaSession)getApplication()).getIdVendedorChat());
        params.put("token", ((TGestionaSession)getApplication()).getTokenMovil());
        params.put("cb_ctipo_doc_2", tipoDoc);
        params.put("num_doc_cli_2", nroDoc);
        params.put("cb_tratamiento_2", opComercialCode);
        params.put("telefono_migrar_2", telfMigra);
        params.put("txt_cdepartamento_visor", depCode);
        params.put("txt_cprovincia_visor", provCode);
        params.put("txt_cdistrito_visor", distCode);
        params.put("campania", pCampaniaCode);
        params.put("cb_idcategoria_2",tipoProductoCode);
        params.put("cb_idproducto_2", productoCode);
        params.put("cb_idprograma_2", subProductoCode);
        params.put("idtipo_incidencia", ActivityVisorVentasFija.pCodTipoIncidencia);
        params.put("ind", ((TGestionaSession)getApplication()).getTipoIncidencia());
        params.put("dniVendedormodal", ActivityMenuIncidencia.numDocVendedor);
        params.put("dniVendedor", ((TGestionaSession)getApplication()).getDniVendedor());

        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                        JSONArray  jsonArrayGlobal = null;
                        JSONObject JOListaDatos = null;
                        JSONArray JACamposFicha;

                        try {
                            //parse de web service de combos
                            jsonArrayGlobal = new JSONArray(result);
                            JOListaDatos = jsonArrayGlobal.getJSONObject(0).getJSONObject("ListaDatos");
                            JACamposFicha = JOListaDatos.getJSONArray("campos_ficha");
                            String field="", value="", message="", inputType="";
                            boolean enabled, visible, focus, readonly=false, sessionStatus=false;

                            sessionStatus = jsonArrayGlobal.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

                            if(!sessionStatus){
                                GlobalFunctions.validaSesion(sessionStatus, getApplicationContext());
                                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
                                if (sharedpreferences.getBoolean("flagVentas",false)){
                                    finishAffinity();
                                } else{
                                    newIntent(ActivityVentasFija.this, LoginTelefonica.class);
                                    uri = null;
                                }
                                limpiarShared(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                                vistaIncidenciaMasiva=false;
                            }

                            for (int i = 0; i < JACamposFicha.length(); i++) {
                                if (!JACamposFicha.getJSONObject(i).isNull("field"))  field = JACamposFicha.getJSONObject(i).getString("field"); else field="";
                                if (!JACamposFicha.getJSONObject(i).isNull("message"))  message = JACamposFicha.getJSONObject(i).getString("message"); else message="";
                                if (!JACamposFicha.getJSONObject(i).isNull("enabled"))  enabled = JACamposFicha.getJSONObject(i).getBoolean("enabled"); else enabled=false;
                                if (!JACamposFicha.getJSONObject(i).isNull("visible"))  visible = JACamposFicha.getJSONObject(i).getBoolean("visible"); else visible=false;
                                if (!JACamposFicha.getJSONObject(i).isNull("focus"))  focus = JACamposFicha.getJSONObject(i).getBoolean("focus"); else focus=false;
                                if (!JACamposFicha.getJSONObject(i).isNull("type"))  inputType = JACamposFicha.getJSONObject(i).getString("type"); else inputType="";

                                if (!JACamposFicha.getJSONObject(i).isNull("readonly")) {
                                    readonly = JACamposFicha.getJSONObject(i).getBoolean("readonly");
                                }else{
                                    readonly = !enabled;
                                }

                                switch (inputType){
                                    case "hidden": //campo tipo texto
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField(inputType,field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "text":   //campo tipo texto
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField(inputType,field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "textarea":   //campo tipo texto
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField("text",field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "radio":   //campo tipo radio
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField(inputType,field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "select": //campo tipo combo
                                            int id = getResources().getIdentifier(field, "id", getPackageName());
                                        View view = null;

                                        if (field.equalsIgnoreCase("cb_plan") || field.equalsIgnoreCase("cb_servicios") || field.equalsIgnoreCase("txt_bloque_canales")){
                                        } else {
                                            validateField(inputType, field, message, "", enabled, visible, focus, readonly, i);
                                        }

                                        switch (field){
                                            //case "cb_ctipo_doc"  : list_cb_ctipo_doc = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_ctipo_doc"  : list_cb_ctipo_doc = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_ctipo_doc,"id","value", "selected", "visible"); break;
                                            case "cb_idcategoria": list_cb_idcategoria = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_idcategoria,"id","value", "selected", "visible"); break;
                                            case "cb_idproducto": list_cb_idproducto = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_idproducto,"id","value", "selected", "visible"); break;
                                            case "cb_idprograma": list_cb_idprograma = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_idprograma,"id","value", "selected", "visible"); break;
                                            case "cb_bloque_producto": list_cb_bloque_producto = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_bloque_producto,"id","value", "selected", "visible"); break;
                                            //case "cb_tratamiento": list_cb_tratamiento = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tratamiento": list_cb_tratamiento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tratamiento,"id","value", "selected", "visible"); break;
                                            case "rb_emails": list_rb_emails = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),rb_emails,"id","value", "selected", "visible"); break;
                                            //case "txt_lugar_nacimiento": list_txt_lugar_nacimiento = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "txt_lugar_nacimiento": list_txt_lugar_nacimiento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), txt_lugar_nacimiento,"id","value", "selected", "visible"); break;
                                            //case "cb_nacionalidad": list_cb_nacionalidad = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_nacionalidad": list_cb_nacionalidad = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_nacionalidad,"id","value", "selected", "visible"); break;
                                            case "cb_edificio": list_cb_edificio = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_edificio,"id","value", "selected", "visible"); break;
                                            //case "cb_titular_fallecido": list_cb_titular_fallecido = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_titular_fallecido": list_cb_titular_fallecido = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_titular_fallecido,"id","value", "selected", "visible"); break;
                                            //case "cb_medio_contratacion": list_cb_medio_contratacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_medio_contratacion": list_cb_medio_contratacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_medio_contratacion,"id","value", "selected", "visible"); break;
                                            //case "cb_envio_contrato": list_cb_envio_contrato = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_envio_contrato": list_cb_envio_contrato = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_envio_contrato,"id","value", "selected", "visible"); break;
                                            //case "cb_factura_digital": list_cb_factura_digital = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_factura_digital": list_cb_factura_digital = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_factura_digital,"id","value", "selected", "visible"); break;
                                            //case "cb_factura_digital_sms": list_cb_factura_digital_sms = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_factura_digital_sms": list_cb_factura_digital_sms = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_factura_digital_sms,"id","value", "selected", "visible"); break;
                                            //case "cb_paginas_blancas": list_cb_paginas_blancas = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_paginas_blancas": list_cb_paginas_blancas = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_paginas_blancas,"id","value", "selected", "visible"); break;
                                            //case "cb_publicar_guia": list_cb_publicar_guia = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_publicar_guia": list_cb_publicar_guia = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_publicar_guia,"id","value", "selected", "visible"); break;
                                            //case "cb_compartir_informacion": list_cb_compartir_informacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_compartir_informacion": list_cb_compartir_informacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_compartir_informacion,"id","value", "selected", "visible"); break;
                                            //case "cb_modalidad_pago": list_cb_modalidad_pago = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_modalidad_pago": list_cb_modalidad_pago = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_modalidad_pago,"id","value", "selected", "visible"); break;
                                            //case "txt_monto": list_txt_monto = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "txt_monto": list_txt_monto = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), txt_monto,"id","value", "selected", "visible"); break;
                                            //case "cb_debito_automatico": list_cb_debito_automatico = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_debito_automatico": list_cb_debito_automatico = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_debito_automatico,"id","value", "selected", "visible"); break;
                                            //case "cb_cuotas_financiamiento": list_cb_cuotas_financiamiento = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_cuotas_financiamiento": list_cb_cuotas_financiamiento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_cuotas_financiamiento,"id","value", "selected", "visible"); break;
                                            //case "cb_idcuenta": list_cb_idcuenta = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_idcuenta": list_cb_idcuenta = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_idcuenta,"id","value", "selected", "visible"); break;
                                            //case "cb_pre_re_paq": list_cb_pre_re_paq = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_pre_re_paq": list_cb_pre_re_paq = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_pre_re_paq,"id","value", "selected", "visible"); break;
                                            //case "cb_tipo_tecnologia": list_cb_tipo_tecnologia = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_tecnologia": list_cb_tipo_tecnologia = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_tecnologia,"id","value", "selected", "visible"); break;
                                            //case "aplica_zona_digitalizacion": list_aplica_zona_digitalizacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "aplica_zona_digitalizacion": list_aplica_zona_digitalizacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), aplica_zona_digitalizacion,"id","value", "selected", "visible"); break;
                                            //case "cb_svalinea": list_cb_svalinea = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_svalinea": list_cb_svalinea = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_svalinea,"id","value", "selected", "visible"); break;
                                            //case "cb_svainternet": list_cb_svainternet = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_svainternet": list_cb_svainternet = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_svainternet,"id","value", "selected", "visible"); break;
                                            //case "cb_repetidor_smart_wifi": list_cb_repetidor_smart_wifi = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_repetidor_smart_wifi": list_cb_repetidor_smart_wifi = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_repetidor_smart_wifi,"id","value", "selected", "visible"); break;
                                            //case "cb_decos_adicionales": list_cb_decos_adicionales = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_decos_adicionales": list_cb_decos_adicionales = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_decos_adicionales,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_sh": list_cb_adeco_sh = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_sh": list_cb_adeco_sh = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_sh,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_hd": list_cb_adeco_hd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_hd": list_cb_adeco_hd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_hd,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_smart": list_cb_adeco_smart = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_smart": list_cb_adeco_smart = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_smart,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_dvrhd": list_cb_adeco_dvrhd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_dvrhd": list_cb_adeco_dvrhd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_dvrhd,"id","value", "selected", "visible"); break;
                                            //case "cb_decodificador_adicional": list_cb_decodificador_adicional = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_decodificador_adicional": list_cb_decodificador_adicional = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_decodificador_adicional,"id","value", "selected", "visible"); break;
                                            //case "cb_cuotas_decodificador": list_cb_cuotas_decodificador = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_cuotas_decodificador": list_cb_cuotas_decodificador = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_cuotas_decodificador,"id","value", "selected", "visible"); break;
                                            //case "cb_tipo_decos": list_cb_tipo_decos = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_decos": list_cb_tipo_decos = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_decos,"id","value", "selected", "visible"); break;
                                            //case "cb_promo_modem_mg": list_cb_promo_modem_mg = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_promo_modem_mg": list_cb_promo_modem_mg = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_promo_modem_mg,"id","value", "selected", "visible"); break;
                                            //case "cb_deco_propiedad_cliente": list_cb_deco_propiedad_cliente = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_deco_propiedad_cliente": list_cb_deco_propiedad_cliente = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_deco_propiedad_cliente,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_smart": list_reemplazo_punto_smart = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_smart": list_reemplazo_punto_smart = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_smart,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_adicional_sd": list_reemplazo_punto_adicional_sd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_adicional_sd": list_reemplazo_punto_adicional_sd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_adicional_sd,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_adicional_hd": list_reemplazo_punto_adicional_hd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_adicional_hd": list_reemplazo_punto_adicional_hd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_adicional_hd,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_adicional_dvr": list_reemplazo_punto_adicional_dvr = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_adicional_dvr": list_reemplazo_punto_adicional_dvr = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_adicional_dvr,"id","value", "selected", "visible"); break;
                                            case "reemplazo_comodato_sd_hd": list_reemplazo_comodato_sd_hd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_comodato_sd_hd,"id","value", "selected", "visible"); break;
                                            //case "cb_paquetizacion": list_cb_paquetizacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_paquetizacion": list_cb_paquetizacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_paquetizacion,"id","value", "selected", "visible"); break;
                                            //case "cb_velocidad_promocional": list_cb_velocidad_promocional = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_velocidad_promocional": list_cb_velocidad_promocional = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_velocidad_promocional,"id","value", "selected", "visible"); break;
                                            //case "cb_tipo_servicio": list_cb_tipo_servicio = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_servicio": list_cb_tipo_servicio = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_servicio,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_equip_deco": list_cb_tipo_equip_deco = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_equip_deco,"id","value", "selected", "visible"); break;
                                            //case "cb_deco_hd_promo_dig": list_cb_deco_hd_promo_dig = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_deco_hd_promo_dig": list_cb_deco_hd_promo_dig = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_deco_hd_promo_dig,"id","value", "selected", "visible"); break;
                                            case "cb_plan": list_cb_plan = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_plan,"id","value", "selected", "visible"); break;
                                            case "cb_servicios": list_cb_servicios = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_servicios,"id","value", "selected", "visible"); break;
                                            case "txt_cdepartamento":
                                                boolean visibleDep,enabledDep, selectedDep=false, visibleProv, enabledProv, selectedProv, visibleDist, enabledDist, selectedDist;

                                                //Parseo de UBIGEO
                                                list_txt_cdepartamento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), txt_cdepartamento,"id","value", "selected", "visible");

                                                JSONArray jADepartamentos=JACamposFicha.getJSONObject(i).getJSONArray("options");
                                                DTO_Departamento oDep =null;
                                                for (int iDepartamento = 0; iDepartamento < jADepartamentos.length(); iDepartamento++) {
                                                    oDep =new DTO_Departamento(); //Se instancia un objeto de la clase DTO_Departamento
                                                    oDep.setId(jADepartamentos.getJSONObject(iDepartamento).getString("id"));
                                                    oDep.setDescripcion(jADepartamentos.getJSONObject(iDepartamento).getString("value"));
                                                    visibleDep = Boolean.parseBoolean(jADepartamentos.getJSONObject(iDepartamento).getString("visible"));
                                                    selectedDep = Boolean.parseBoolean(jADepartamentos.getJSONObject(iDepartamento).getString("selected"));
                                                    if (selectedDep) iDepSeleccionado = iDepartamento;
                                                    if (visibleDep){txt_cdepartamento.setVisibility(View.VISIBLE); txt_cdepartamento_tv.setVisibility(View.VISIBLE);} else {txt_cdepartamento.setVisibility(View.GONE);txt_cdepartamento_tv.setVisibility(View.GONE);}
                                                    sListaDepartamentos.add(jADepartamentos.getJSONObject(iDepartamento).getString("value"));
                                                    if (!jADepartamentos.getJSONObject(iDepartamento).getJSONObject("childs").isNull("options"))
                                                    {
                                                        List<DTO_Provincia> listaProvincias = new ArrayList<>(); //Se declara lista de Provincias
                                                        //if (iDepSeleccionado>-1) {
                                                            //sListaProvincias.clear();
                                                            JSONArray jAProvincias = jADepartamentos.getJSONObject(iDepartamento).getJSONObject("childs").getJSONArray("options");
                                                            DTO_Provincia oProv=null;
                                                            for (int iProvincia = 0; iProvincia < jAProvincias.length(); iProvincia++) { // Se recorre las provincias del departamento en cuestión
                                                                oProv = new DTO_Provincia();
                                                                oProv.setId(jAProvincias.getJSONObject(iProvincia).getString("id"));
                                                                oProv.setDescripcion(jAProvincias.getJSONObject(iProvincia).getString("value"));
                                                                oProv.setSeleccionado(jAProvincias.getJSONObject(iProvincia).getBoolean("selected"));
                                                                visibleProv = Boolean.parseBoolean(jAProvincias.getJSONObject(iProvincia).getString("visible"));
                                                                selectedProv = Boolean.parseBoolean(jAProvincias.getJSONObject(iProvincia).getString("selected"));
                                                                if (selectedProv) iProvSeleccionado = iProvincia;
                                                                if (visibleProv) {txt_cprovincia.setVisibility(View.VISIBLE); txt_cprovincia_tv.setVisibility(View.VISIBLE);} else {txt_cprovincia.setVisibility(View.GONE);txt_cprovincia_tv.setVisibility(View.GONE);}
                                                                //sListaProvincias.add(jADepartamentos.getJSONObject(iDepSeleccionado).getJSONObject("childs").getJSONArray("options").getJSONObject(iProvincia).getString("value"));
// -----------------GOOD arriba
                                                                if (!jAProvincias.getJSONObject(iProvincia).getJSONObject("childs").isNull("options")) {
                                                                    List<DTO_Distrito> listaDistritos = new ArrayList<>(); //Se declara lista de Distritos
                                                                    //if (iProvSeleccionado>-1) {
                                                                        //sListaDistritos.clear();
                                                                        JSONArray jADistritos = jAProvincias.getJSONObject(iProvincia).getJSONObject("childs").getJSONArray("options");
                                                                        DTO_Distrito oDist=null;
                                                                        for (int iDistrito = 0; iDistrito < jADistritos.length(); iDistrito++) {
                                                                            oDist = new DTO_Distrito();
                                                                            oDist.setId(jADistritos.getJSONObject(iDistrito).getString("id"));
                                                                            oDist.setDescripcion(jADistritos.getJSONObject(iDistrito).getString("value"));
                                                                            oDist.setSeleccionado(jADistritos.getJSONObject(iDistrito).getBoolean("selected"));
                                                                            visibleDist = Boolean.parseBoolean(jADistritos.getJSONObject(iDistrito).getString("visible"));
                                                                            selectedDist = Boolean.parseBoolean(jADistritos.getJSONObject(iDistrito).getString("selected"));
                                                                            if (visibleDist) {txt_cdistrito.setVisibility(View.VISIBLE);txt_cdistrito_tv.setVisibility(View.VISIBLE);}  else {txt_cdistrito.setVisibility(View.GONE);txt_cdistrito_tv.setVisibility(View.GONE);}
                                                                            //if (selectedDist) iDistSeleccionado = iDistrito;
                                                                            //sListaDistritos.add(jADistritos.getJSONObject(iDistrito).getString("value"));
                                                                            listaDistritos.add(oDist);
                                                                        }
                                                                        //GlobalFunctions.rellenarSpinner(getApplicationContext(), txt_cdistrito, sListaDistritos);
                                                                        //if (iDistSeleccionado > -1) txt_cdistrito.setSelection(iDistSeleccionado);
                                                                        //txt_cdistrito.setSelection(iDistSeleccionado);
                                                                        oProv.setListaDistritos(listaDistritos);
                                                                        listaProvincias.add(oProv);
                                                                    //}
                                                                }
                                                            }
                                                            //GlobalFunctions.rellenarSpinner(getApplicationContext(), txt_cprovincia, sListaProvincias);
//                                                            if (iProvSeleccionado > -1)
//                                                                txt_cprovincia.setSelection(iProvSeleccionado);
                                                            //txt_cprovincia.setSelection(iProvSeleccionado);
                                                            oDep.setListaProvincias(listaProvincias);
                                                        //}
                                                    }
                                                    listaDepartamentos.add(oDep);
                                                }
                                                GlobalFunctions.rellenarSpinner(getApplicationContext(),txt_cdepartamento,sListaDepartamentos);
                                                if (iDepSeleccionado>-1) txt_cdepartamento.setSelection(iDepSeleccionado);
//                                                txt_cprovincia.setSelection(iProvSeleccionado);
//                                                txt_cdistrito.setSelection(iDistSeleccionado);
                                                    break;
                                        }
                                        break;
                                }
                            }
                            customDialog.dismiss();
                            scVentasFija.fullScroll(ScrollView.FOCUS_UP);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            customDialog.dismiss();
                        }
                        //getDataFromBundle();
                    }
                }
        );
    }

    private void validaCampania(){
        String WSMethod="";
        customDialog.show();
        WSMethod = "cb_idcuenta_change";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("id_vendedor", "75512941");
        params.put("token", "scsdcsdcsdc");
        params.put("cb_idcuenta", codCampania);
        params.put("cb_deco_propiedad_cliente", codDecoPropiedadCliente);
        params.put("cb_idcategoria_2",tipoProductoCode);
        params.put("cb_idproducto_2", productoCode);
        params.put("cb_idprograma_2", subProductoCode);
        params.put("txt_idventa", txt_idventa.getText().toString());
        params.put("cb_tratamiento", codTratamiento);
        params.put("txt_telefono", txt_telefono.getText().toString());
        params.put("cb_modalidad_pago", codModalidadPago);
        params.put("cb_ctipo_doc", codTipoDoc);
        params.put("txt_cndoc", nroDoc);

        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());

        final String json = GlobalFunctions.callVolleyStringRequest(this,params,URL,volley,mRequest, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //result = GlobalFunctions.loadJSONFromAsset(getApplicationContext(),"array.json");
                        JSONArray  jsonArrayGlobal = null;
                        JSONObject JOListaDatos = null;
                        JSONArray JACamposFicha;

                        try {
                            //parse de web service de combos
                            jsonArrayGlobal = new JSONArray(result);
                            JOListaDatos = jsonArrayGlobal.getJSONObject(0).getJSONObject("ListaDatos");
                            JACamposFicha = JOListaDatos.getJSONArray("campos_ficha");
                            String field="", value="", message="", inputType="";
                            boolean enabled, visible, focus, readonly=false, sessionStatus=false;

                            sessionStatus = jsonArrayGlobal.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

                            if(!sessionStatus){
                                GlobalFunctions.validaSesion(sessionStatus, getApplicationContext());
                                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
                                if (sharedpreferences.getBoolean("flagVentas",false)){
                                    finishAffinity();
                                } else{
                                    newIntent(ActivityVentasFija.this, LoginTelefonica.class);
                                    uri = null;
                                }
                                limpiarShared(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                                vistaIncidenciaMasiva=false;
                            }

                            for (int i = 0; i < JACamposFicha.length(); i++) {
                                if (!JACamposFicha.getJSONObject(i).isNull("field"))  field = JACamposFicha.getJSONObject(i).getString("field"); else field="";
                                if (!JACamposFicha.getJSONObject(i).isNull("message"))  message = JACamposFicha.getJSONObject(i).getString("message"); else message="";
                                if (!JACamposFicha.getJSONObject(i).isNull("enabled"))  enabled = JACamposFicha.getJSONObject(i).getBoolean("enabled"); else enabled=false;
                                if (!JACamposFicha.getJSONObject(i).isNull("visible"))  visible = JACamposFicha.getJSONObject(i).getBoolean("visible"); else visible=false;
                                if (!JACamposFicha.getJSONObject(i).isNull("focus"))  focus = JACamposFicha.getJSONObject(i).getBoolean("focus"); else focus=false;
                                if (!JACamposFicha.getJSONObject(i).isNull("type"))  inputType = JACamposFicha.getJSONObject(i).getString("type"); else inputType="";

                                if (!JACamposFicha.getJSONObject(i).isNull("readonly")) {
                                    readonly = JACamposFicha.getJSONObject(i).getBoolean("readonly");
                                }else{
                                    readonly = !enabled;
                                }

                                switch (inputType){
                                    case "hidden": //campo tipo texto
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField(inputType,field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "text":   //campo tipo texto
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField(inputType,field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "textarea":   //campo tipo texto
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField("text",field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "radio":   //campo tipo radio
                                        value = JACamposFicha.getJSONObject(i).getString("value");
                                        validateField(inputType,field,message,value,enabled,visible,focus, readonly,i);
                                        break;
                                    case "select": //campo tipo combo


                                        if (field.equalsIgnoreCase("cb_plan") || field.equalsIgnoreCase("cb_servicios") || field.equalsIgnoreCase("txt_bloque_canales")){
                                        } else {
                                            validateField(inputType, field, message, "", enabled, visible, focus, readonly, i);
                                        }

                                        switch (field){
                                            //case "cb_ctipo_doc"  : list_cb_ctipo_doc = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_ctipo_doc"  : list_cb_ctipo_doc = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_ctipo_doc,"id","value", "selected", "visible"); break;
                                            case "cb_idcategoria": list_cb_idcategoria = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_idcategoria,"id","value", "selected", "visible"); break;
                                            case "cb_idproducto": list_cb_idproducto = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_idproducto,"id","value", "selected", "visible"); break;
                                            case "cb_idprograma": list_cb_idprograma = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_idprograma,"id","value", "selected", "visible"); break;
                                            case "cb_bloque_producto": list_cb_bloque_producto = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_bloque_producto,"id","value", "selected", "visible"); break;
                                            //case "cb_tratamiento": list_cb_tratamiento = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tratamiento": list_cb_tratamiento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tratamiento,"id","value", "selected", "visible"); break;
                                            case "rb_emails": list_rb_emails = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),rb_emails,"id","value", "selected", "visible"); break;
                                            //case "txt_lugar_nacimiento": list_txt_lugar_nacimiento = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "txt_lugar_nacimiento": list_txt_lugar_nacimiento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), txt_lugar_nacimiento,"id","value", "selected", "visible"); break;
                                            //case "cb_nacionalidad": list_cb_nacionalidad = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_nacionalidad": list_cb_nacionalidad = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_nacionalidad,"id","value", "selected", "visible"); break;
                                            case "cb_edificio": list_cb_edificio = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_edificio,"id","value", "selected", "visible"); break;
                                            //case "cb_titular_fallecido": list_cb_titular_fallecido = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_titular_fallecido": list_cb_titular_fallecido = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_titular_fallecido,"id","value", "selected", "visible"); break;
                                            //case "cb_medio_contratacion": list_cb_medio_contratacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_medio_contratacion": list_cb_medio_contratacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_medio_contratacion,"id","value", "selected", "visible"); break;
                                            //case "cb_envio_contrato": list_cb_envio_contrato = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_envio_contrato": list_cb_envio_contrato = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_envio_contrato,"id","value", "selected", "visible"); break;
                                            //case "cb_factura_digital": list_cb_factura_digital = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_factura_digital": list_cb_factura_digital = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_factura_digital,"id","value", "selected", "visible"); break;
                                            //case "cb_factura_digital_sms": list_cb_factura_digital_sms = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_factura_digital_sms": list_cb_factura_digital_sms = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_factura_digital_sms,"id","value", "selected", "visible"); break;
                                            //case "cb_paginas_blancas": list_cb_paginas_blancas = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_paginas_blancas": list_cb_paginas_blancas = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_paginas_blancas,"id","value", "selected", "visible"); break;
                                            //case "cb_publicar_guia": list_cb_publicar_guia = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_publicar_guia": list_cb_publicar_guia = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_publicar_guia,"id","value", "selected", "visible"); break;
                                            //case "cb_compartir_informacion": list_cb_compartir_informacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_compartir_informacion": list_cb_compartir_informacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_compartir_informacion,"id","value", "selected", "visible"); break;
                                            //case "cb_modalidad_pago": list_cb_modalidad_pago = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_modalidad_pago": list_cb_modalidad_pago = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_modalidad_pago,"id","value", "selected", "visible"); break;
                                            //case "txt_monto": list_txt_monto = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "txt_monto": list_txt_monto = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), txt_monto,"id","value", "selected", "visible"); break;
                                            //case "cb_debito_automatico": list_cb_debito_automatico = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_debito_automatico": list_cb_debito_automatico = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_debito_automatico,"id","value", "selected", "visible"); break;
                                            //case "cb_cuotas_financiamiento": list_cb_cuotas_financiamiento = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_cuotas_financiamiento": list_cb_cuotas_financiamiento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_cuotas_financiamiento,"id","value", "selected", "visible"); break;
                                            //case "cb_idcuenta": list_cb_idcuenta = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_idcuenta": list_cb_idcuenta = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_idcuenta,"id","value", "selected", "visible"); break;
                                            //case "cb_pre_re_paq": list_cb_pre_re_paq = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_pre_re_paq": list_cb_pre_re_paq = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_pre_re_paq,"id","value", "selected", "visible"); break;
                                            //case "cb_tipo_tecnologia": list_cb_tipo_tecnologia = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_tecnologia": list_cb_tipo_tecnologia = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_tecnologia,"id","value", "selected", "visible"); break;
                                            //case "aplica_zona_digitalizacion": list_aplica_zona_digitalizacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "aplica_zona_digitalizacion": list_aplica_zona_digitalizacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), aplica_zona_digitalizacion,"id","value", "selected", "visible"); break;
                                            //case "cb_svalinea": list_cb_svalinea = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_svalinea": list_cb_svalinea = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_svalinea,"id","value", "selected", "visible"); break;
                                            //case "cb_svainternet": list_cb_svainternet = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_svainternet": list_cb_svainternet = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_svainternet,"id","value", "selected", "visible"); break;
                                            //case "cb_repetidor_smart_wifi": list_cb_repetidor_smart_wifi = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_repetidor_smart_wifi": list_cb_repetidor_smart_wifi = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_repetidor_smart_wifi,"id","value", "selected", "visible"); break;
                                            //case "cb_decos_adicionales": list_cb_decos_adicionales = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_decos_adicionales": list_cb_decos_adicionales = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_decos_adicionales,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_sh": list_cb_adeco_sh = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_sh": list_cb_adeco_sh = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_sh,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_hd": list_cb_adeco_hd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_hd": list_cb_adeco_hd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_hd,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_smart": list_cb_adeco_smart = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_smart": list_cb_adeco_smart = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_smart,"id","value", "selected", "visible"); break;
                                            //case "cb_adeco_dvrhd": list_cb_adeco_dvrhd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_adeco_dvrhd": list_cb_adeco_dvrhd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_adeco_dvrhd,"id","value", "selected", "visible"); break;
                                            //case "cb_decodificador_adicional": list_cb_decodificador_adicional = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_decodificador_adicional": list_cb_decodificador_adicional = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_decodificador_adicional,"id","value", "selected", "visible"); break;
                                            //case "cb_cuotas_decodificador": list_cb_cuotas_decodificador = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_cuotas_decodificador": list_cb_cuotas_decodificador = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_cuotas_decodificador,"id","value", "selected", "visible"); break;
                                            //case "cb_tipo_decos": list_cb_tipo_decos = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_decos": list_cb_tipo_decos = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_decos,"id","value", "selected", "visible"); break;
                                            //case "cb_promo_modem_mg": list_cb_promo_modem_mg = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_promo_modem_mg": list_cb_promo_modem_mg = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_promo_modem_mg,"id","value", "selected", "visible"); break;
                                            //case "cb_deco_propiedad_cliente": list_cb_deco_propiedad_cliente = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_deco_propiedad_cliente": list_cb_deco_propiedad_cliente = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_deco_propiedad_cliente,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_smart": list_reemplazo_punto_smart = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_smart": list_reemplazo_punto_smart = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_smart,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_adicional_sd": list_reemplazo_punto_adicional_sd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_adicional_sd": list_reemplazo_punto_adicional_sd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_adicional_sd,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_adicional_hd": list_reemplazo_punto_adicional_hd = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_adicional_hd": list_reemplazo_punto_adicional_hd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_adicional_hd,"id","value", "selected", "visible"); break;
                                            //case "reemplazo_punto_adicional_dvr": list_reemplazo_punto_adicional_dvr = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "reemplazo_punto_adicional_dvr": list_reemplazo_punto_adicional_dvr = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_punto_adicional_dvr,"id","value", "selected", "visible"); break;
                                            case "reemplazo_comodato_sd_hd": list_reemplazo_comodato_sd_hd = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), reemplazo_comodato_sd_hd,"id","value", "selected", "visible"); break;
                                            //case "cb_paquetizacion": list_cb_paquetizacion = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_paquetizacion": list_cb_paquetizacion = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_paquetizacion,"id","value", "selected", "visible"); break;
                                            //case "cb_velocidad_promocional": list_cb_velocidad_promocional = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_velocidad_promocional": list_cb_velocidad_promocional = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_velocidad_promocional,"id","value", "selected", "visible"); break;
                                            //case "cb_tipo_servicio": list_cb_tipo_servicio = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_servicio": list_cb_tipo_servicio = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_servicio,"id","value", "selected", "visible"); break;
                                            case "cb_tipo_equip_deco": list_cb_tipo_equip_deco = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_tipo_equip_deco,"id","value", "selected", "visible"); break;
                                            //case "cb_deco_hd_promo_dig": list_cb_deco_hd_promo_dig = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),newActv,"id","value", "selected", "visible"); break;
                                            case "cb_deco_hd_promo_dig": list_cb_deco_hd_promo_dig = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), cb_deco_hd_promo_dig,"id","value", "selected", "visible"); break;
                                            case "cb_plan": list_cb_plan = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_plan,"id","value", "selected", "visible"); break;
                                            case "cb_servicios": list_cb_servicios = fillActvFromJSONArray2(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"),cb_servicios,"id","value", "selected", "visible"); break;
                                            case "txt_cdepartamento":
                                                boolean visibleDep,enabledDep, selectedDep=false, visibleProv, enabledProv, selectedProv, visibleDist, enabledDist, selectedDist;

                                                //Parseo de UBIGEO
                                                list_txt_cdepartamento = fillSpinnerFromJSONArray(getApplicationContext(), JACamposFicha.getJSONObject(i).getJSONArray("options"), txt_cdepartamento,"id","value", "selected", "visible");

                                                JSONArray jADepartamentos=JACamposFicha.getJSONObject(i).getJSONArray("options");
                                                DTO_Departamento oDep =null;
                                                for (int iDepartamento = 0; iDepartamento < jADepartamentos.length(); iDepartamento++) {
                                                    oDep =new DTO_Departamento(); //Se instancia un objeto de la clase DTO_Departamento
                                                    oDep.setId(jADepartamentos.getJSONObject(iDepartamento).getString("id"));
                                                    oDep.setDescripcion(jADepartamentos.getJSONObject(iDepartamento).getString("value"));
                                                    visibleDep = Boolean.parseBoolean(jADepartamentos.getJSONObject(iDepartamento).getString("visible"));
                                                    selectedDep = Boolean.parseBoolean(jADepartamentos.getJSONObject(iDepartamento).getString("selected"));
                                                    if (selectedDep) iDepSeleccionado = iDepartamento;
                                                    if (visibleDep){txt_cdepartamento.setVisibility(View.VISIBLE); txt_cdepartamento_tv.setVisibility(View.VISIBLE);} else {txt_cdepartamento.setVisibility(View.GONE);txt_cdepartamento_tv.setVisibility(View.GONE);}
                                                    sListaDepartamentos.add(jADepartamentos.getJSONObject(iDepartamento).getString("value"));
                                                    if (!jADepartamentos.getJSONObject(iDepartamento).getJSONObject("childs").isNull("options"))
                                                    {
                                                        List<DTO_Provincia> listaProvincias = new ArrayList<>(); //Se declara lista de Provincias
                                                        //if (iDepSeleccionado>-1) {
                                                        //sListaProvincias.clear();
                                                        JSONArray jAProvincias = jADepartamentos.getJSONObject(iDepartamento).getJSONObject("childs").getJSONArray("options");
                                                        DTO_Provincia oProv=null;
                                                        for (int iProvincia = 0; iProvincia < jAProvincias.length(); iProvincia++) { // Se recorre las provincias del departamento en cuestión
                                                            oProv = new DTO_Provincia();
                                                            oProv.setId(jAProvincias.getJSONObject(iProvincia).getString("id"));
                                                            oProv.setDescripcion(jAProvincias.getJSONObject(iProvincia).getString("value"));
                                                            oProv.setSeleccionado(jAProvincias.getJSONObject(iProvincia).getBoolean("selected"));
                                                            visibleProv = Boolean.parseBoolean(jAProvincias.getJSONObject(iProvincia).getString("visible"));
                                                            selectedProv = Boolean.parseBoolean(jAProvincias.getJSONObject(iProvincia).getString("selected"));
                                                            if (selectedProv) iProvSeleccionado = iProvincia;
                                                            if (visibleProv) {txt_cprovincia.setVisibility(View.VISIBLE); txt_cprovincia_tv.setVisibility(View.VISIBLE);} else {txt_cprovincia.setVisibility(View.GONE);txt_cprovincia_tv.setVisibility(View.GONE);}
                                                            //sListaProvincias.add(jADepartamentos.getJSONObject(iDepSeleccionado).getJSONObject("childs").getJSONArray("options").getJSONObject(iProvincia).getString("value"));
// -----------------GOOD arriba
                                                            if (!jAProvincias.getJSONObject(iProvincia).getJSONObject("childs").isNull("options")) {
                                                                List<DTO_Distrito> listaDistritos = new ArrayList<>(); //Se declara lista de Distritos
                                                                //if (iProvSeleccionado>-1) {
                                                                //sListaDistritos.clear();
                                                                JSONArray jADistritos = jAProvincias.getJSONObject(iProvincia).getJSONObject("childs").getJSONArray("options");
                                                                DTO_Distrito oDist=null;
                                                                for (int iDistrito = 0; iDistrito < jADistritos.length(); iDistrito++) {
                                                                    oDist = new DTO_Distrito();
                                                                    oDist.setId(jADistritos.getJSONObject(iDistrito).getString("id"));
                                                                    oDist.setDescripcion(jADistritos.getJSONObject(iDistrito).getString("value"));
                                                                    oDist.setSeleccionado(jADistritos.getJSONObject(iDistrito).getBoolean("selected"));
                                                                    visibleDist = Boolean.parseBoolean(jADistritos.getJSONObject(iDistrito).getString("visible"));
                                                                    selectedDist = Boolean.parseBoolean(jADistritos.getJSONObject(iDistrito).getString("selected"));
                                                                    if (visibleDist) {txt_cdistrito.setVisibility(View.VISIBLE);txt_cdistrito_tv.setVisibility(View.VISIBLE);}  else {txt_cdistrito.setVisibility(View.GONE);txt_cdistrito_tv.setVisibility(View.GONE);}
                                                                    //if (selectedDist) iDistSeleccionado = iDistrito;
                                                                    //sListaDistritos.add(jADistritos.getJSONObject(iDistrito).getString("value"));
                                                                    listaDistritos.add(oDist);
                                                                }
                                                                //GlobalFunctions.rellenarSpinner(getApplicationContext(), txt_cdistrito, sListaDistritos);
                                                                //if (iDistSeleccionado > -1) txt_cdistrito.setSelection(iDistSeleccionado);
                                                                //txt_cdistrito.setSelection(iDistSeleccionado);
                                                                oProv.setListaDistritos(listaDistritos);
                                                                listaProvincias.add(oProv);
                                                                //}
                                                            }
                                                        }
                                                        //GlobalFunctions.rellenarSpinner(getApplicationContext(), txt_cprovincia, sListaProvincias);
//                                                            if (iProvSeleccionado > -1)
//                                                                txt_cprovincia.setSelection(iProvSeleccionado);
                                                        //txt_cprovincia.setSelection(iProvSeleccionado);
                                                        oDep.setListaProvincias(listaProvincias);
                                                        //}
                                                    }
                                                    listaDepartamentos.add(oDep);
                                                }
                                                GlobalFunctions.rellenarSpinner(getApplicationContext(),txt_cdepartamento,sListaDepartamentos);
                                                if (iDepSeleccionado>-1) txt_cdepartamento.setSelection(iDepSeleccionado);
//                                                txt_cprovincia.setSelection(iProvSeleccionado);
//                                                txt_cdistrito.setSelection(iDistSeleccionado);
                                                break;
                                        }
                                        break;
                                }
                            }
                            customDialog.dismiss();

                            //scVentasFija.smoothScrollTo(0,0);
                            scVentasFija.fullScroll(ScrollView.FOCUS_UP);

                            //cb_ctipo_doc.setText(pCodTipoDoc);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            customDialog.dismiss();
                        }
                        //getDataFromBundle();
                    }
                }
        );
    }

    private void callRegistrarFicha(String nombreMetodo){
        String WSMethod="";
        WSMethod = nombreMetodo;
        String URL = URL_DESARROLLO + WSMethod;
        Map<String, String> params = new HashMap<String, String>();

        params.put("idtipo_incidencia", ActivityVisorVentasFija.pCodTipoIncidencia);
        params.put("ind", ((TGestionaSession)getApplication()).getTipoIncidencia());
        params.put("dniVendedormodal", ActivityMenuIncidencia.numDocVendedor);
        params.put("dniVendedor", ((TGestionaSession)getApplication()).getDniVendedor());

        params.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        params.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        params.put("token", ((TGestionaSession) getApplication()).getTokenMovil());
        if (varPublicas.tokenMovil==null) varPublicas.tokenMovil ="";
        params.put("token", varPublicas.tokenMovil);
        params.put("txt_iduser", ((TGestionaSession)getApplication()).getDniVendedor());
        params.put("cb_tipo_servicio", codTipoServicio);
        params.put("cb_idcategoria", tipoProductoCode);
        params.put("cb_idproducto", productoCode);
            if (rb_titular.isChecked()){
                txt_tipo_venta.setText("1");
            }
        params.put("txt_tipo_venta", txt_tipo_venta.getText().toString());
        params.put("cb_ctipo_doc", codTipoDoc);
        params.put("txt_cndoc", nroDoc);
        params.put("cb_titular_fallecido", codTitularFallecido);
        params.put("txt_nombre_nuevo_titular", txt_nombre_nuevo_titular.getText().toString());
        params.put("txt_apepa_nuevo_titular", txt_apepa_nuevo_titular.getText().toString());
        params.put("txt_apema_nuevo_titular", txt_apema_nuevo_titular.getText().toString());
        params.put("txt_dni_nuevo_titular", txt_dni_nuevo_titular.getText().toString());
        params.put("txt_telefono_referencia", txt_telefono_referencia.getText().toString());
        params.put("txt_telefono_celular", txt_telefono_celular.getText().toString());
        params.put("cb_tratamiento", codTratamiento);
        params.put("txt_idgrabacion", txt_idgrabacion.getText().toString());

        params.put("txt_cemail", txt_cemail.getText().toString());
        params.put("rb_emails", codEmail);
        params.put("rb_otros", rb_otros.getText().toString());

        params.put("cb_decos_adicionales", codDecosAdicionales);
        params.put("cb_adeco_hd", codADecoHD);
        params.put("cb_adeco_smart", codADecoSmart);
        params.put("cb_adeco_dvrhd", codAdecoDVRHD);
        params.put("txt_cliente_cms", txt_cliente_cms.getText().toString());
        params.put("txt_servicio_cms", txt_servicio_cms.getText().toString());
        params.put("cb_paquetizacion", codPaquetizacion);
        params.put("txt_telefono", txt_telefono.getText().toString());
        params.put("txt_nom_cliente", txt_nom_cliente.getText().toString());
        params.put("txt_apepaterno_cliente", txt_apepaterno_cliente.getText().toString());
        params.put("txt_apematerno_cliente", txt_apematerno_cliente.getText().toString());
        params.put("txt_codigo_atis", txt_codigo_atis.getText().toString());
        params.put("cb_modalidad_pago", codModalidadPago);
        params.put("txt_monto", codMonto);
        params.put("cb_idcuenta", codCampania);
        params.put("cb_debito_automatico", codDebitoAutomatico);
        params.put("txt_web_experto", txt_web_experto.getText().toString());
        params.put("cb_deco_propiedad_cliente", codDecoPropiedadCliente);
        params.put("txt_cdepartamento", codDepartamento);
        params.put("txt_cprovincia", codProvincia);
        params.put("txt_cdistrito", codDistrito);
        params.put("p_er_ciudadsitiada", ""); //NO EXISTE EN CONTROLES
        params.put("cb_tipo_tecnologia", codTipoTecnologia);
//        params.put("txt_codigo_consulta_previa",); //NO EXISTE
        //params.put("cb_nacionalidad", );
//        params.put("txt_localidad", txt_localidad); //NO EXISTE
        params.put("txt_nombre_padre", txt_nombre_padre.getText().toString());
        params.put("txt_nombre_madre", txt_nombre_madre.getText().toString());
        params.put("txt_fecha_nacimiento", txt_fecha_nacimiento.getText().toString());
        params.put("txt_lugar_nacimiento", codLugarNacimiento);
        params.put("reemplazo_punto_smart", codReemplazoPuntoSmart);
        params.put("reemplazo_punto_adicional_sd", codReemplazoPuntoAdicionalSD); //hacer codes
        params.put("reemplazo_punto_adicional_hd", codReemplazoPuntoAdicionalHD); // hacer codes
        params.put("reemplazo_punto_adicional_dvr", codReemplazoPuntoAdicionalDVR); // hacer codes
        params.put("reemplazo_comodato_sd_hd", codReemplazoComodato); //hacer codes
        params.put("cb_idprograma", codSubProducto);
        params.put("cb_idprograma_value", subProductoValue);
        params.put("cb_idcuenta", codCampania);
        params.put("cb_tratamiento", codTratamiento); // VER

        //params.put("txt_pidsubproducto", );
        params.put("txt_bloque_canales", txt_bloque_canales.getText().toString());
        params.put("cb_repetidor_smart_wifi", codRepetidorSmartWifi);
        params.put("cb_velocidad_promocional", codVelocidadPromocional);
        params.put("txt_rwdepartamento", codDepartamento);
        params.put("txt_rwprovincia", codProvincia);
        params.put("txt_rwdistrito", codDistrito);
        params.put("txt_cpostal", txt_cpostal.getText().toString());
        //CALL

        params.put("txt_idventa", txt_idventa.getText().toString());
        params.put("txt_nombre_call", txt_nombre_call.getText().toString());
        params.put("txt_detalle_call", txt_detalle_call.getText().toString());
        params.put("txt_call_asesor_dni", txt_call_asesor_dni.getText().toString());
        params.put("cb_envio_contrato", codEnvioContrato);
        params.put("cb_factura_digital", codFacturaDigital);
        params.put("cb_paginas_blancas", codPaginasBlancas);
        params.put("cb_cuotas_financiamiento", codCuotasFinanciamiento);
        params.put("aplica_zona_digitalizacion", codAplicaZonaDigitalizacion);
        params.put("txt_svalinea", txt_svalinea.getText().toString());
        params.put("txt_svainternet", txt_svainternet.getText().toString());
        params.put("txt_bloque_canales", txt_bloque_canales.getText().toString());
        params.put("cb_tipo_equip_deco", codTipoEquipoDeco);
        params.put("cb_compartir_informacion", codCompartirInformacion);
        params.put("cb_cuotas_decodificador", codCuotasDecodificador);
        params.put("cb_tipo_decos", codTipoDecos); //hacer codes
        params.put("txt_observacion", txt_observacion.getText().toString());
        params.put("serie_equipo1", serie_equipo1.getText().toString());
        params.put("serie_equipo2", serie_equipo2.getText().toString());
        params.put("serie_equipo3", serie_equipo3.getText().toString());
        params.put("serie_equipo4", serie_equipo4.getText().toString());
        params.put("serie_equipo5", serie_equipo5.getText().toString());
        params.put("serie_equipo6", serie_equipo6.getText().toString());
        params.put("walter", walter.getText().toString());
        params.put("txt_cdireccion", txt_cdireccion.getText().toString());
        //params.put("txt_plan_embajador", txt_plan_embajador.getText().toString());
        params.put("cb_pre_re_paq", codPreRePaq); //hacer codes
        params.put("residencial_negocios", varPublicas.gResidencialNegocios); //hacer codes



        customDialog.show();
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

                            sessionStatus = jsonArray.getJSONObject(0).isNull("session_status"); //Devuelve true si es null (no existe session_status en array)

                            if(!sessionStatus){
                                GlobalFunctions.validaSesion(sessionStatus, getApplicationContext());
                                GlobalFunctions.logout(getApplicationContext(),((TGestionaSession)getApplication()).getUsuario(),volley,mRequest);
                                if (sharedpreferences.getBoolean("flagVentas",false)){
                                    finishAffinity();
                                } else{
                                    newIntent(ActivityVentasFija.this, LoginTelefonica.class);
                                    uri = null;
                                }
                                limpiarShared(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", Toast.LENGTH_SHORT).show();
                                vistaIncidenciaMasiva=false;
                            }

                            // Fin Valida Sesiones
                        if (!jsonArray.getJSONObject(0).isNull("NroMensaje") )
                        {
                            if (jsonArray.getJSONObject(0).getString("NroMensaje").equalsIgnoreCase("24"))
                            {
                                String title= jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getString("message");
                                mostrarConfirmacion(title);
                            }

                            if (jsonArray.getJSONObject(0).getString("NroMensaje").equalsIgnoreCase("200")){
                                String idPreventa="", idTicket="";
                                idPreventa= jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("campos_ficha").getJSONObject(0).getString("value");
                                idTicket= jsonArray.getJSONObject(0).getString("idticket");
                                mostrarConfirmacionRegistro(idTicket, idPreventa);
                                idpreventa_tv.setVisibility(View.VISIBLE);
                                idpreventa.setVisibility(View.VISIBLE);
                                idpreventa.setText(idPreventa);
                                idpreventa.setFocusable(true);
                                idpreventa.setFocusableInTouchMode(true);
                                idpreventa.requestFocusFromTouch();
                                idpreventa.performClick();
                                idpreventa.requestFocus();

                                JSONArray camposFicha = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("campos_ficha");
                                for (int i = 0; i < camposFicha.length(); i++) {
                                    String fieldName= camposFicha.getJSONObject(i).getString("field");
                                    Boolean enabled = camposFicha.getJSONObject(i).getBoolean("enabled");
                                    validateField(fieldName,enabled);
                                }
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                scVentasFija.smoothScrollTo(0,0);
                                mGuardar.setEnabled(false);

                                //ExpandCollapse.Expand(rlDatos);
                            }
                        }
                        else
                        {
                            switch (jsonArray.getJSONObject(0).getInt("flag")){
                                case 1: //Validación de mensaje por campo o toast
                                    switch (jsonArray.getJSONObject(0).getInt("tipo_error")){
                                        case 1: //validacion por campo
                                            JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONObject("error");
                                            recorrerAllEditText(llMain);
                                                String campo, msgError, inputType;
                                                campo = jsonObject.getString("field");
                                                msgError = jsonObject.getString("message");
                                                //if (!jsonObject.isNull("inputType")) {
//                                                    inputType = jsonObject.getString("inputType");
//                                                    setErrorToViewByItsName(campo.trim(), msgError, inputType);
//                                                }
                                            //ExpandAll();
                                            setErrorToViewByItsName(campo.trim(), msgError);
                                            break;
                                        case 2: //validación por toast
                                            JSONArray listaErrores2 = jsonArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("error");
                                            for (int i = 0; i < listaErrores2.length(); i++) {
                                                String msgError2;
                                                msgError2 = listaErrores2.getJSONObject(i).getString("message");
                                                Toast.makeText(ActivityVentasFija.this,msgError2,Toast.LENGTH_SHORT);
                                            }
                                            break;
                                    }
                                    break;
                                }
                            }
                            customDialog.dismiss();
                            //limpiarAllEditText(llMain);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            customDialog.dismiss();
                        }
                    }
                }
        );
    }

    private void mostrarConfirmacionRegistro(String idTicket, String idPreventa){
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(false); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_confirma_venta); //establecemos el contenido de nuestro dialog

        final TextView tvConfirmacion;

        final Button btnAceptar;

        tvConfirmacion =  (TextView) customDialog.findViewById(R.id.tvConfirmacion);
        btnAceptar =  (Button)customDialog.findViewById(R.id.btnAceptar);

        tvConfirmacion.setText("Se generó Ticket MTA " + idTicket +" \n" +
                                "Se generó ID pedido " + idPreventa + " \n" +
                "Consulta el estado de tu solicitud en el módulo de CONSULTA DE TICKETS ");

        ((Button) customDialog.findViewById(R.id.btnAceptar)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {

                    customDialog.dismiss();

            }
        });


        customDialog.show();
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int mesActual = month + 1;

                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);

                    txt_fecha_nacimiento.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
                    txt_fecha_nacimiento.setError(null);
            }
        },anio, mes, dia);
        recogerFecha.show();
    }

//    public boolean getParent(){
//        ViewParent parent = this.getParent();
//        RelativeLayout r;
//        if (parent == null) {
//            Log.d("TEST", "this.getParent() is null");
//        }
//        else {
//            if (parent instanceof ViewGroup) {
//                ViewParent grandparent = ((ViewGroup) parent).getParent();
//                if (grandparent == null) {
//                    Log.d("TEST", "((ViewGroup) this.getParent()).getParent() is null");
//                }
//                else {
//                    if (parent instanceof RelativeLayout) {
//                        r = (RelativeLayout) grandparent;
//                    }
//                    else {
//                        Log.d("TEST", "((ViewGroup) this.getParent()).getParent() is not a RelativeLayout");
//                    }
//                }
//            }
//            else {
//                Log.d("TEST", "this.getParent() is not a ViewGroup");
//            }
//        }
//
//    }

    private void setErrorToViewByItsName(String fieldName, String msgError){
        int id = getResources().getIdentifier(fieldName, "id", getPackageName());
        View view=null;
        if(id != 0) {
            view = findViewById(id);
            //if (position==0) view.requestFocus();
        }

//        if (fieldName.contains("txt")){
//            EditText newEdit;
//            newEdit = (EditText)view;
//            newEdit.setEnabled(true);
//            newEdit.setError(msgError);
//            newEdit.requestFocus();
//            //
//            // newEdit.setVisibility(View.VISIBLE);
//        }

//        if (fieldName.contains("cb")){
//            Spinner newSpinner;
//            newSpinner = (Spinner)view;
//            newSpinner.requestFocus();
//            TextView errorText = (TextView)newSpinner.getSelectedView();
//            errorText.setError(msgError);
//            errorText.setFocusable(true);
//            errorText.setFocusableInTouchMode(true);
//            errorText.requestFocusFromTouch();
//            errorText.performClick();
//            errorText.requestFocus();

        if (view instanceof EditText){
            EditText newEdit;
            newEdit = (EditText)view;
            newEdit.setEnabled(true);
            newEdit.setError(msgError);

            if (fieldName.equalsIgnoreCase("txt_fecha_nacimiento")){
                txt_nombre_madre.requestFocus();
            } else{
                newEdit.requestFocus();
            }
        }

        if (view instanceof Spinner){
            Spinner newSpinner;
            newSpinner = (Spinner)view;
            newSpinner.requestFocus();
            TextView errorText = (TextView)newSpinner.getSelectedView();
            errorText.setFocusableInTouchMode(true);
            errorText.requestFocusFromTouch();
            errorText.performClick();
            errorText.requestFocus();
            errorText.setError(msgError);
            errorText.setFocusable(true);
        }
//        errorText.setTextColor(Color.RED);//just to highlight that this is an error    errorText.setText("Please Select Remarks");    return;}
//        if (view instanceof EditText){
//            EditText newEdit;
//            newEdit = (EditText)view;
//            newEdit.setError(msgError);
//        }
//        if (view instanceof AutoCompleteTextView){
//            AutoCompleteTextView newActv;
//            newActv = (AutoCompleteTextView)view;
//            newActv.setError(msgError);
//        }
    }

    private void limpiarAllEditText (LinearLayout llMain){
        View campoHijo=null;
        EditText tvtField;
        //TextView tvField;
        AutoCompleteTextView actvField;

        for( int i = 0; i < llMain.getChildCount(); i++ ) {
            campoHijo = llMain.getChildAt(i);
            if (campoHijo instanceof EditText) {
                tvtField = (EditText) campoHijo;
                //tvField = (TextView) (tvtField.getText().toString() +"_tv");
                tvtField.setText("");
            }
        }
    }

    private void recorrerAllEditText (LinearLayout llMain){
        View campoHijo=null;
        EditText tvtField;
        //TextView tvField;
        AutoCompleteTextView actvField;

        for( int i = 0; i < llMain.getChildCount(); i++ ) {
            campoHijo = llMain.getChildAt(i);
            if (campoHijo instanceof EditText) {
                tvtField = (EditText) campoHijo;
                //tvField = (TextView) (tvtField.getText().toString() +"_tv");
                tvtField.setError(null);
            }
        }
    }

    private void validateField(String inputType, String fieldName, String msgError, String value, Boolean enabled, Boolean visible, Boolean focus, Boolean readonly, int position){
        int id = getResources().getIdentifier(fieldName, "id", getPackageName());

        if (fieldName.equalsIgnoreCase("cb_modalidad_pago")){
            String a;
            a="sfsfsd";
        }
        View view=null;
        if(id != 0)
        {
            view = findViewById(id);
        }

        switch (inputType){
//            case "radio":
//                int idTv1  = getResources().getIdentifier(fieldName + "_tv", "id", getPackageName()); //identificador del TextView asociado al EditText
////                View viewTv1=null;
////                if(id != 0)
////                {
////                    viewTv1 = findViewById(idTv1);
////                }
////                if (viewTv1!=null) {
////                    TextView newTv;
////                    newTv = (TextView) viewTv1;
////                    if (visible == true) newTv.setVisibility(View.VISIBLE);
////                    else newTv.setVisibility(View.GONE);
////                }
//
//                CheckBox newCheck;
//                newCheck = (CheckBox) view;
//                //newCheck.setEnabled(true);
//                newCheck.setText(value);
//                if (focus) newCheck.requestFocus();
//                if (visible==true) newCheck.setVisibility(View.VISIBLE); else newCheck.setVisibility(View.GONE);
//                if (msgError.equalsIgnoreCase("")) newCheck.setError(null); else newCheck.setError(msgError);
//                break;
            case "select":
                int idTv2  = getResources().getIdentifier(fieldName + "_tv", "id", getPackageName()); //identificador del TextView asociado al EditText
                TextView newTv;
                View viewTv2=null;
                if(id != 0)
                {
                    viewTv2 = findViewById(idTv2);
                }
                if (viewTv2!=null) {

                    newTv = (TextView) viewTv2;
                    if (visible == true) newTv.setVisibility(View.VISIBLE);
                    else newTv.setVisibility(View.GONE);
                }

                Spinner newSpinner;
                newSpinner = (Spinner) view;
                if (!enabled || readonly) newSpinner.setEnabled(false); else newSpinner.setEnabled(true);

                //newActv.setText(value);
                if (focus) newSpinner.requestFocus();

                if (visible == true) newSpinner.setVisibility(View.VISIBLE);
                else newSpinner.setVisibility(View.GONE);

                //((TextView)newSpinner.getChildAt(0)).setError(msgError);
                //if (msgError.equalsIgnoreCase("")) newTv.setError(null); else newTv.setError(msgError);
                break;

            case "text":
                int idTv  = getResources().getIdentifier(fieldName + "_tv", "id", getPackageName()); //identificador del TextView asociado al EditText
                View viewTv=null;
                if(id != 0)
                {
                    viewTv = findViewById(idTv);
                }
                if (viewTv!=null)
                {
                    //TextView newTv;
                    newTv = (TextView) viewTv;
                    if (visible == true) newTv.setVisibility(View.VISIBLE);
                    else newTv.setVisibility(View.GONE);
                }



                EditText newEdit;
                newEdit = (EditText)view;

                if (fieldName.equalsIgnoreCase("txt_bloque_canales") && !visible){
                    btn_bloque_canales.setVisibility(View.GONE);
                }
//                newEdit.setEnabled(!readonly);
//                newEdit.setEnabled(enabled);
                if (!enabled || readonly) newEdit.setEnabled(false); else newEdit.setEnabled(true);
                //if (enabled) newEdit.setBackgroundColor(Color.WHITE);
                newEdit.setText(value);

                if (focus) newEdit.requestFocus();
                if (msgError.equalsIgnoreCase("")) newEdit.setError(null); else newEdit.setError(msgError);
                if (visible==true) newEdit.setVisibility(View.VISIBLE); else newEdit.setVisibility(View.GONE);
                break;

            case "hidden":
//                String campoHidden="";
//                campoHidden = (String)view;
//                campoHidden=value;
                break;

//            case "radio":
//                RadioButton newRadio;
//                newRadio = (RadioButton)view;
//                newRadio.setEnabled(enabled);
//                if (focus) newRadio.requestFocus();
//                newRadio.setChecked(Boolean.parseBoolean(value));
//                if (msgError.equalsIgnoreCase("")) newRadio.setError(null); else newRadio.setError(msgError);
//                if (visible==true) newRadio.setVisibility(View.VISIBLE); else newRadio.setVisibility(View.GONE);
//                break;
        }
    }

    private void validateField(String fieldName, boolean enabled){
        int id = getResources().getIdentifier(fieldName, "id", getPackageName());

        View view=null;
        if(id != 0)
        {
            view = findViewById(id);
            view.setEnabled(enabled);
        }
    }

    private void mostrarDialogoSVALinea(){
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(true); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_sva_linea); //establecemos el contenido de nuestro dialog

        final CheckBox chkMultidestino, chkServiciosEnLinea, chkNoAplica;
        final AutoCompleteTextView cb_plan, cb_servicios;

        chkMultidestino = (CheckBox)customDialog.findViewById(R.id.opcion_0_1);
        chkServiciosEnLinea = (CheckBox)customDialog.findViewById(R.id.opcion_0_2);
        chkNoAplica =  (CheckBox)customDialog.findViewById(R.id.chkNoAplica);
        cb_plan = (AutoCompleteTextView) customDialog.findViewById(R.id.cb_plan);
        cb_servicios = (AutoCompleteTextView) customDialog.findViewById(R.id.cb_servicios);

        //Genera listas de plan y servicios
        List<String> listaPlan = new ArrayList<>();
        for (int i = 0; i < list_cb_plan.size(); i++) {
            listaPlan.add(list_cb_plan.get(i).getDescripcion());
        }

        List<String> listaServicios = new ArrayList<>();
        for (int i = 0; i < list_cb_servicios.size(); i++) {
            listaServicios.add(list_cb_servicios.get(i).getDescripcion());
        }
        //rellena los combos con las listas anteriores
        GlobalFunctions.rellenarActv(this,cb_plan,listaPlan);
        GlobalFunctions.rellenarActv(this,cb_servicios,listaServicios);

        //Eventos de selección de checks
        chkMultidestino.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    cb_plan.setEnabled(true);
                    chkNoAplica.setChecked(false);
                }
                else
                {
                    cb_plan.setEnabled(false);
                    cb_plan.setText("");
                    cb_plan.setHint("Seleccione");
                }
            }
        });

        chkServiciosEnLinea.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    cb_servicios.setEnabled(true);
                    chkNoAplica.setChecked(false);
                } else{
                    cb_servicios.setEnabled(false);
                    cb_servicios.setText("");
                    cb_servicios.setHint("Seleccione");
                }
            }
        });

        chkNoAplica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked ){
                    cb_plan.setEnabled(false);
                    cb_plan.setText("");
                    cb_plan.setHint("Seleccione");
                    chkMultidestino.setChecked(false);
                    cb_servicios.setEnabled(false);
                    cb_servicios.setText("");
                    cb_servicios.setHint("Seleccione");
                    chkServiciosEnLinea.setChecked(false);
                }
            }
        });

        ((Button) customDialog.findViewById(R.id.btnClose)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
            }
        });

        ((Button) customDialog.findViewById(R.id.btnGuardar)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                txt_svalinea.setText("");
                String valorMulti="", valorServicios="";

                if (chkMultidestino.isChecked()) {
                    if (!cb_plan.getText().toString().equalsIgnoreCase("Seleccione")) {
                        valorMulti = cb_plan.getText().toString();
                        txt_svalinea.setText(valorMulti);
                        txt_svalinea.requestFocus();
                        customDialog.dismiss();
                    }
                }
                if (chkServiciosEnLinea.isChecked()) {
                    if (!cb_servicios.getText().toString().equalsIgnoreCase("Seleccione")) {
                        valorServicios = cb_servicios.getText().toString();
                        if (chkMultidestino.isChecked()) {
                            txt_svalinea.setText(valorMulti + " + " + valorServicios);
                            txt_svalinea.requestFocus();
                        } else {
                            txt_svalinea.setText("");
                            txt_svalinea.setText(valorServicios);
                        }
                    }
                customDialog.dismiss();
                    //pasara campos seleccionados a la ficha
                }
            }
        }
        );
        customDialog.show();
    }

    String valor1="", valor2="", valor3="", valor4="", valor5="", valor6="", cadena="";
    private void mostrarDialogoBloqueCanales(){
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(true); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_bloque_canales); //establecemos el contenido de nuestro dialog
        txt_bloque_canales.setText("");
        final CheckBox chkOpcion11, chkOpcion12, chkOpcion13,chkOpcion14,chkOpcion15,chkOpcion16, chkNoAplica;

        chkOpcion11 =  (CheckBox)customDialog.findViewById(R.id.opcion_1_1);
        chkOpcion12 =  (CheckBox)customDialog.findViewById(R.id.opcion_1_2);
        chkOpcion13 =  (CheckBox)customDialog.findViewById(R.id.opcion_1_3);
        chkOpcion14 =  (CheckBox)customDialog.findViewById(R.id.opcion_1_4);
        chkOpcion15 =  (CheckBox)customDialog.findViewById(R.id.opcion_1_5);
        chkOpcion16 =  (CheckBox)customDialog.findViewById(R.id.opcion_1_6);
        chkNoAplica =  (CheckBox)customDialog.findViewById(R.id.chkNoAplica);

        //Eventos de selección de checks
        chkOpcion11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    valor1 = chkOpcion11.getText().toString();
                    chkNoAplica.setChecked(false);
                    cadena = valor1;
                }
            }
        });
        chkOpcion12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    valor2 = chkOpcion12.getText().toString();
                    chkNoAplica.setChecked(false);
                    if (cadena.isEmpty()) cadena=valor2; else  cadena=cadena + "+" + valor2;
                }
            }
        });
        chkOpcion13.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    valor3 = chkOpcion13.getText().toString();
                    chkNoAplica.setChecked(false);
                    if (cadena.isEmpty()) cadena=valor3; else  cadena=cadena + "+" + valor3;
                }
            }
        });
        chkOpcion14.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    valor4 = chkOpcion14.getText().toString();
                    chkNoAplica.setChecked(false);
                    if (cadena.isEmpty()) cadena=valor4; else  cadena=cadena + "+" + valor4;
                }
            }
        });
        chkOpcion15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    valor5 = chkOpcion15.getText().toString();
                    chkNoAplica.setChecked(false);
                    if (cadena.isEmpty()) cadena=valor5; else  cadena=cadena + "+" + valor5;
                }
            }
        });
        chkOpcion16.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    valor6 = chkOpcion16.getText().toString();
                    chkNoAplica.setChecked(false);
                    if (cadena.isEmpty()) cadena=valor6; else  cadena=cadena + "+" + valor6;
                }
            }
        });

        chkNoAplica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    chkOpcion11.setChecked(false);
                    chkOpcion12.setChecked(false);
                    chkOpcion13.setChecked(false);
                    chkOpcion14.setChecked(false);
                    chkOpcion15.setChecked(false);
                    //chkOpcion16.setChecked(false);
                }
            }
        });

        ((Button) customDialog.findViewById(R.id.btnClose)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
            }
        });

        ((Button) customDialog.findViewById(R.id.btnGuardar)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                //valor1=""; valor2=""; valor3=""; valor4=""; valor5=""; valor6="";

                txt_bloque_canales.setText(cadena);
                txt_bloque_canales.requestFocus();
                customDialog.dismiss();
                //pasara campos seleccionados a la ficha
            }
        });
        customDialog.show();
    }

    String valorI1="";
    private void mostrarDialogoSVAInternet(){
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(true); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_sva_internet); //establecemos el contenido de nuestro dialog

        final CheckBox chkOpcion11, chkNoAplica;

        chkOpcion11 =  (CheckBox)customDialog.findViewById(R.id.opcion_0_1);
        chkNoAplica =  (CheckBox)customDialog.findViewById(R.id.chkNoAplica);

        //Eventos de selección de checks
        chkOpcion11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    valorI1 = chkOpcion11.getText().toString();
                    chkNoAplica.setChecked(false);
                }
            }
        });

        chkNoAplica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    txt_svainternet.setText("");
                    chkOpcion11.setChecked(false);

                }
            }
        });

        ((Button) customDialog.findViewById(R.id.btnClose)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
            }
        });

        ((Button) customDialog.findViewById(R.id.btnGuardar)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                txt_svainternet.setText("");
                txt_svainternet.setText(valorI1);
                customDialog.dismiss();
                //pasara campos seleccionados a la ficha
            }
        });
        customDialog.show();
    }

    private void ExpandAll(){
        ExpandCollapse.Expand(rlDatos);
        ExpandCollapse.Expand(rlDatosDC);
        ExpandCollapse.Expand(rlDatosCli);
        ExpandCollapse.Expand(rlDatosCallCenterData);
        ExpandCollapse.Expand(rlDatosVentaData);
        ExpandCollapse.Expand(rlDatosVentaProductos);
    }

    private void CollapseAll(){
        ExpandCollapse.Collapse(rlDatos);
        ExpandCollapse.Collapse(rlDatosDC);
        ExpandCollapse.Collapse(rlDatosCli);
        ExpandCollapse.Collapse(rlDatosCallCenterData);
        ExpandCollapse.Collapse(rlDatosVentaData);
        ExpandCollapse.Collapse(rlDatosVentaProductos);
    }

    //Evento cuando se selecciona spinner
    int posTratamiento=-1, posTipoDoc=-1, posModalidadPago, posLugarNacimiento, posDepartamento1, posLocalidad, posTitularFallecido, posEnvioContrato, posFacturaDigital, posCompartirInformacion,
        posMonto=-1, posDebitoAutomatico=-1, posCampania=-1, posTipoProducto=-1, posProducto=-1, posSubProducto=-1, posTipoTecnologia=-1, posRepetidorSmartWifi=-1, posDecosAdicionales=-1,
        posADecoHd=-1, posADecoSmart=-1, posTipoServicio=-1, posProvincia1=-1, posDistrito1=-1, posEmails=-1, posReemplazoPuntoSmart=-1, posDecoPropiedadCliente=-1,
        posAdecoDVRHD=-1, posPaquetizacion=-1, posReemplazoPuntoAdicionalSD=-1, posReemplazoPuntoAdicionalHD=-1, posReemplazoPuntoAdicionalDVR=-1, posReemplazoComodato=-1,
        posVelocidadPromocional=-1, posPaginasBlancas=-1, posCuotasFinanciamiento=-1, posAplicaZonaDigitalizacion=-1, posTipoEquipoDeco=-1, posCuotasDecodificador=-1, posPreRePaq=-1, posTipoDecos=-1;
    String codTratamiento="", codTipoDoc="", codModalidadPago="", codLugarNacimiento="", codDepartamento="", codProvincia="", codDistrito="", codLocalidad="", codTitularFallecido="", codEnvioContrato="", codFacturaDigital="", codCompartirInformacion="",
           codMonto="", codDebitoAutomatico="", codCampania="", codTipoProducto="", codProducto="", codSubProducto="", codTipoTecnologia="", codRepetidorSmartWifi="", codDecosAdicionales="",
           codADecoHD="", codADecoSmart="", codTipoServicio="", codEmail="", codReemplazoPuntoSmart="", codDecoPropiedadCliente="",
            codAdecoDVRHD="", codPaquetizacion="", codReemplazoPuntoAdicionalSD="", codReemplazoPuntoAdicionalHD="", codReemplazoPuntoAdicionalDVR="", codReemplazoComodato="", codPreRePaq="",
            codVelocidadPromocional="", codPaginasBlancas="", codCuotasFinanciamiento="", codAplicaZonaDigitalizacion="", codTipoEquipoDeco="", codCuotasDecodificador="", codTipoDecos="";
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent == rb_emails)
        {
            posEmails = position;
            if (posEmails != -1) {
                codEmail = list_rb_emails.get(posEmails).getCodigo().toString();
                if (codEmail.equalsIgnoreCase("Otros")){
                    rb_otros.setVisibility(View.VISIBLE);
                }
                else
                {
                    rb_otros.setVisibility(View.GONE);
                }
            }

            if (posEmails > 0){
                cb_envio_contrato.setSelection(1); //SE MUESTRA EMAIL
            } else{
                cb_envio_contrato.setSelection(2); //SE MUESTRA FISICO
            }
            //Toast.makeText(this, codEmail, Toast.LENGTH_SHORT).show();
        }

        if (parent == cb_tratamiento) {
            posTratamiento = position;
            if (posTratamiento != -1)
            {
                codTratamiento = list_cb_tratamiento.get(posTratamiento).getCodigo();
                if (codTratamiento.equalsIgnoreCase("ATV")|| codTratamiento.equalsIgnoreCase("ABA")||codTratamiento.equalsIgnoreCase("ALT"))
                {
                    cb_modalidad_pago.setEnabled(true);
                    txt_codigo_cip.setEnabled(true);
                }
                else
                {
                    cb_modalidad_pago.setEnabled(true);
                    txt_codigo_cip.setEnabled(true);
                }
            }
            //Toast.makeText(this, codTratamiento, Toast.LENGTH_SHORT).show();
        }

        if (parent == cb_ctipo_doc) { posTipoDoc = position; if (posTipoDoc != -1) codTipoDoc = list_cb_ctipo_doc.get(posTipoDoc).getCodigo().toString(); //Toast.makeText(this, codTipoDoc, Toast.LENGTH_SHORT).show();
        }
        if (parent == txt_lugar_nacimiento) { posLugarNacimiento = position; if (posLugarNacimiento != -1) codLugarNacimiento = list_txt_lugar_nacimiento.get(posLugarNacimiento).getCodigo().toString(); //Toast.makeText(this, codLugarNacimiento, Toast.LENGTH_SHORT).show();
        }
        if (parent == txt_cdepartamento)
        {
            posDepartamento1 = position;
            if (posDepartamento1 != -1) {
                codDepartamento = listaDepartamentos.get(posDepartamento1).getId();
                sListaProvincias.clear();
            }

            for (int i = 0; i < listaDepartamentos.get(posDepartamento1).getListaProvincias().size(); i++)
            {
                if (listaDepartamentos.get(posDepartamento1).getListaProvincias().get(i).getSeleccionado()){
                    posProvincia1=i;
                }
                sListaProvincias.add(listaDepartamentos.get(posDepartamento1).getListaProvincias().get(i).getDescripcion());
            }
            GlobalFunctions.rellenarSpinnerIndex(getApplicationContext(),txt_cprovincia,sListaProvincias, posProvincia1);
            //txt_cprovincia.setSelection(ActivityVisorVentasFija.posProvincia);


            //Toast.makeText(this, codDepartamento, Toast.LENGTH_SHORT).show();
        }

//        txt_cprovincia.setSelection(ActivityVisorVentasFija.posProvincia);
//        txt_cdistrito.setSelection(ActivityVisorVentasFija.posDistrito);

        if (parent == txt_cprovincia) {
//            if (ActivityVisorVentasFija.posProvincia>-1){
//                posProvincia1 = ActivityVisorVentasFija.posProvincia;
//            }else {
                posProvincia1 = position;
//            }

            if (posProvincia1 != -1)
            {
                codProvincia = listaDepartamentos.get(posDepartamento1).getListaProvincias().get(posProvincia1).getId();
                sListaDistritos.clear();
            }

            for (int i = 0; i < listaDepartamentos.get(posDepartamento1).getListaProvincias().get(posProvincia1).getListaDistritos().size(); i++)
            {
                if (listaDepartamentos.get(posDepartamento1).getListaProvincias().get(posProvincia1).getListaDistritos().get(i).getSeleccionado()){
                    posDistrito1=i;
                }
                sListaDistritos.add(listaDepartamentos.get(posDepartamento1).getListaProvincias().get(posProvincia1).getListaDistritos().get(i).getDescripcion());
            }

            GlobalFunctions.rellenarSpinnerIndex(getApplicationContext(),txt_cdistrito,sListaDistritos, posDistrito1);
//            if (ActivityVisorVentasFija.posProvincia>-1){
//                txt_cprovincia.setSelection(ActivityVisorVentasFija.posProvincia);
//            }
//            ActivityVisorVentasFija.posProvincia=-1;


            //Toast.makeText(this, codProvincia, Toast.LENGTH_SHORT).show();
        }

        if (parent == txt_cdistrito) {

//            if (ActivityVisorVentasFija.posDistrito>-1){
//                posDistrito1 = ActivityVisorVentasFija.posDistrito;
//            } else{
                posDistrito1 = position;
//            }
            //posDistrito1 = position;
            if (posDistrito1 != -1 ) {
                codDistrito = listaDepartamentos.get(posDepartamento1).getListaProvincias().get(posProvincia1).getListaDistritos().get(posDistrito1).getId();
            }
            posProvincia1=0;
            posDistrito1=0;

//            if (ActivityVisorVentasFija.posDistrito>-1){
//                txt_cdistrito.setSelection(ActivityVisorVentasFija.posDistrito);
//            }
//
//            ActivityVisorVentasFija.posDistrito = -1;
            //Toast.makeText(this, codDistrito, Toast.LENGTH_SHORT).show();
        }

        //distrito
        //if (parent == txt_localidad) { posLocalidad = position; if (posLocalidad != -1) codLocalidad = .get(posLocalidad).getCodigo().toString(); Toast.makeText(this, codLocalidad, Toast.LENGTH_SHORT).show();}
        if (parent == cb_titular_fallecido) {
            posTitularFallecido = position;
            if (posTitularFallecido != -1) {
                codTitularFallecido = list_cb_titular_fallecido.get(posTitularFallecido).getCodigo();
                if (codTitularFallecido.equalsIgnoreCase("si"))
                {
                    txt_nombre_nuevo_titular.setVisibility(View.VISIBLE);
                    txt_nombre_nuevo_titular.setEnabled(true);
                    txt_nombre_nuevo_titular_tv.setVisibility(View.VISIBLE);
                    txt_apepa_nuevo_titular.setVisibility(View.VISIBLE);
                    txt_apepa_nuevo_titular.setEnabled(true);
                    txt_apepa_nuevo_titular_tv.setVisibility(View.VISIBLE);
                    txt_apema_nuevo_titular.setVisibility(View.VISIBLE);
                    txt_apema_nuevo_titular.setEnabled(true);
                    txt_apema_nuevo_titular_tv.setVisibility(View.VISIBLE);
                    txt_dni_nuevo_titular.setVisibility(View.VISIBLE);
                    txt_dni_nuevo_titular.setEnabled(true);
                    txt_dni_nuevo_titular_tv.setVisibility(View.VISIBLE);
                } else {
                    txt_nombre_nuevo_titular.setVisibility(View.GONE);
                    txt_nombre_nuevo_titular.setEnabled(false);
                    txt_nombre_nuevo_titular_tv.setVisibility(View.GONE);
                    txt_apepa_nuevo_titular.setVisibility(View.GONE);
                    txt_apepa_nuevo_titular.setEnabled(false);
                    txt_apepa_nuevo_titular_tv.setVisibility(View.GONE);
                    txt_apema_nuevo_titular.setVisibility(View.GONE);
                    txt_apema_nuevo_titular.setEnabled(false);
                    txt_apema_nuevo_titular_tv.setVisibility(View.GONE);
                    txt_dni_nuevo_titular.setVisibility(View.GONE);
                    txt_dni_nuevo_titular.setEnabled(false);
                    txt_dni_nuevo_titular_tv.setVisibility(View.GONE);
                }
            }
            //Toast.makeText(this, codTitularFallecido, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_envio_contrato) { posEnvioContrato = position; if (posEnvioContrato != -1) codEnvioContrato = list_cb_envio_contrato.get(posEnvioContrato).getCodigo().toString();
        //Toast.makeText(this, codEnvioContrato, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_factura_digital) { posFacturaDigital = position; if (posFacturaDigital != -1) codFacturaDigital = list_cb_factura_digital.get(posFacturaDigital).getCodigo().toString();
        //Toast.makeText(this, codFacturaDigital, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_compartir_informacion) { posCompartirInformacion = position; if (posCompartirInformacion != -1) codCompartirInformacion = list_cb_compartir_informacion.get(posCompartirInformacion).getCodigo().toString();
        //Toast.makeText(this, codCompartirInformacion, Toast.LENGTH_SHORT).show();
            }
        if (parent == txt_monto) { posMonto = position; if (posMonto != -1) codMonto = list_txt_monto.get(posMonto).getCodigo().toString();
        //Toast.makeText(this, codMonto, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_debito_automatico) { posDebitoAutomatico = position; if (posDebitoAutomatico != -1) codDebitoAutomatico = list_cb_debito_automatico.get(posDebitoAutomatico).getCodigo().toString();
        //Toast.makeText(this, codDebitoAutomatico, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_idcuenta) {
            posCampania = position;

            if (posCampania != -1) {
                codCampania = list_cb_idcuenta.get(posCampania).getCodigo().toString();
                validaCampania();
//                if(codCampania.equalsIgnoreCase("PORT")){
//                    mostrarDialogoOK("Para la campaña [ Portabilidad ] es necesario ingresar el teléfono a portar.");
//                }
            }
        //Toast.makeText(this, codCampania, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_idcategoria) { posTipoProducto = position; if (posTipoProducto != -1) codTipoProducto = list_cb_idcategoria.get(posTipoProducto).getCodigo().toString();
        //Toast.makeText(this, codTipoProducto, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_idproducto) { posProducto = position; if (posProducto != -1) codProducto = list_cb_idproducto.get(posProducto).getCodigo().toString();
        //Toast.makeText(this, codProducto, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_idprograma) { posSubProducto = position; if (posSubProducto != -1) codSubProducto = list_cb_idprograma.get(posSubProducto).getCodigo().toString();
        //Toast.makeText(this, codSubProducto, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_tipo_tecnologia) { posTipoTecnologia = position; if (posTipoTecnologia != -1) codTipoTecnologia = list_cb_tipo_tecnologia.get(posTipoTecnologia).getCodigo().toString();
        //Toast.makeText(this, codTipoTecnologia, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_tipo_decos) { posTipoDecos = position; if (posTipoDecos != -1) codTipoDecos = list_cb_tipo_decos.get(posTipoDecos).getCodigo().toString();
        //Toast.makeText(this, codTipoDecos, Toast.LENGTH_SHORT).show();
            }

        if (parent == cb_repetidor_smart_wifi) { posRepetidorSmartWifi = position; if (posRepetidorSmartWifi != -1) codRepetidorSmartWifi = list_cb_repetidor_smart_wifi.get(posRepetidorSmartWifi).getCodigo().toString();
        //Toast.makeText(this, codRepetidorSmartWifi, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_adeco_dvrhd) { posAdecoDVRHD = position; if (posAdecoDVRHD != -1) codAdecoDVRHD = list_cb_adeco_dvrhd.get(posAdecoDVRHD).getCodigo().toString();
        //Toast.makeText(this, codAdecoDVRHD, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_paquetizacion) { posPaquetizacion = position; if (posPaquetizacion != -1) codPaquetizacion = list_cb_paquetizacion.get(posPaquetizacion).getCodigo().toString();
        //Toast.makeText(this, codPaquetizacion, Toast.LENGTH_SHORT).show();
            }
        if (parent == reemplazo_punto_adicional_sd) { posReemplazoPuntoAdicionalSD = position; if (posReemplazoPuntoAdicionalSD != -1) codReemplazoPuntoAdicionalSD = list_reemplazo_punto_adicional_sd.get(posReemplazoPuntoAdicionalSD).getCodigo().toString();
        //Toast.makeText(this, codReemplazoPuntoAdicionalSD, Toast.LENGTH_SHORT).show();
            }
        if (parent == reemplazo_punto_adicional_hd) { posReemplazoPuntoAdicionalHD = position; if (posReemplazoPuntoAdicionalHD != -1) codReemplazoPuntoAdicionalHD = list_reemplazo_punto_adicional_hd.get(posReemplazoPuntoAdicionalHD).getCodigo().toString();
        //Toast.makeText(this, codReemplazoPuntoAdicionalHD, Toast.LENGTH_SHORT).show();
            }
        if (parent == reemplazo_punto_adicional_dvr) { posReemplazoPuntoAdicionalDVR = position; if (posReemplazoPuntoAdicionalDVR != -1) codReemplazoPuntoAdicionalDVR = list_reemplazo_punto_adicional_dvr.get(posReemplazoPuntoAdicionalDVR).getCodigo().toString();
        //Toast.makeText(this, codReemplazoPuntoAdicionalDVR, Toast.LENGTH_SHORT).show();
            }
        if (parent == reemplazo_comodato_sd_hd) { posReemplazoComodato = position; if (posReemplazoComodato != -1) codReemplazoComodato = list_reemplazo_comodato_sd_hd.get(posReemplazoComodato).getCodigo().toString();
        //Toast.makeText(this, codReemplazoComodato, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_velocidad_promocional) { posVelocidadPromocional = position; if (posVelocidadPromocional != -1) codVelocidadPromocional = list_cb_velocidad_promocional.get(posVelocidadPromocional).getCodigo().toString();
        //Toast.makeText(this, codVelocidadPromocional, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_paginas_blancas) { posPaginasBlancas = position; if (posPaginasBlancas != -1) codPaginasBlancas = list_cb_paginas_blancas.get(posPaginasBlancas).getCodigo().toString();
        //Toast.makeText(this, codPaginasBlancas, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_cuotas_financiamiento) { posCuotasFinanciamiento = position; if (posCuotasFinanciamiento != -1) codCuotasFinanciamiento = list_cb_cuotas_financiamiento.get(posCuotasFinanciamiento).getCodigo().toString();
        //Toast.makeText(this, codCuotasFinanciamiento, Toast.LENGTH_SHORT).show();
            }
        if (parent == aplica_zona_digitalizacion) { posAplicaZonaDigitalizacion = position; if (posAplicaZonaDigitalizacion != -1) codAplicaZonaDigitalizacion = list_aplica_zona_digitalizacion.get(posAplicaZonaDigitalizacion).getCodigo().toString();
        //Toast.makeText(this, codAplicaZonaDigitalizacion, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_tipo_equip_deco) { posTipoEquipoDeco = position; if (posTipoEquipoDeco != -1) codTipoEquipoDeco = list_cb_tipo_equip_deco.get(posTipoEquipoDeco).getCodigo().toString();
        //Toast.makeText(this, codTipoEquipoDeco, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_cuotas_decodificador) { posCuotasDecodificador = position; if (posCuotasDecodificador != -1) codCuotasDecodificador = list_cb_cuotas_decodificador.get(posCuotasDecodificador).getCodigo().toString();
        //Toast.makeText(this, codCuotasDecodificador, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_pre_re_paq) { posPreRePaq = position; if (posPreRePaq != -1) codPreRePaq = list_cb_pre_re_paq.get(posPreRePaq).getCodigo().toString();
        //Toast.makeText(this, codPreRePaq, Toast.LENGTH_SHORT).show();
            }


        if (parent == cb_decos_adicionales)
        {
            posDecosAdicionales = position;
            if (posDecosAdicionales != -1)
                codDecosAdicionales = list_cb_decos_adicionales.get(posDecosAdicionales).getCodigo();

            if (codDecosAdicionales.equalsIgnoreCase("SI")){
                cb_adeco_hd.setEnabled(true);
                cb_adeco_smart.setEnabled(true);
            } else{
                cb_adeco_hd.setSelection(0);
                cb_adeco_smart.setSelection(0);
                cb_adeco_hd.setEnabled(false);
                cb_adeco_smart.setEnabled(false);
            }
            //Toast.makeText(this, codDecosAdicionales, Toast.LENGTH_SHORT).show();
        }
        if (parent == cb_adeco_hd) { posADecoHd = position; if (posADecoHd != -1) codADecoHD = list_cb_adeco_hd.get(posADecoHd).getCodigo().toString();
        //Toast.makeText(this, codADecoHD, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_adeco_smart) { posADecoSmart = position; if (posADecoSmart != -1) codADecoSmart = list_cb_adeco_smart.get(posADecoSmart).getCodigo().toString();
        //Toast.makeText(this, codADecoSmart, Toast.LENGTH_SHORT).show();
            }
        if (parent == cb_tipo_servicio) { posTipoServicio = position; if (posTipoServicio != -1) codTipoServicio = list_cb_tipo_servicio.get(posTipoServicio).getCodigo().toString();
        //Toast.makeText(this, codTipoServicio, Toast.LENGTH_SHORT).show();
            }
        if (parent == reemplazo_punto_smart) {
            posReemplazoPuntoSmart = position;
            if (posReemplazoPuntoSmart != -1) {
                codReemplazoPuntoSmart = list_reemplazo_punto_smart.get(posReemplazoPuntoSmart).getCodigo();
                if (codReemplazoPuntoSmart.equalsIgnoreCase("NO")){
                    reemplazo_punto_adicional_sd.setVisibility(View.GONE);
                    reemplazo_punto_adicional_hd.setVisibility(View.GONE);
                    reemplazo_punto_adicional_dvr.setVisibility(View.GONE);
                    reemplazo_comodato_sd_hd.setVisibility(View.GONE);
                } else{
                    reemplazo_punto_adicional_sd.setVisibility(View.VISIBLE);
                    reemplazo_punto_adicional_hd.setVisibility(View.VISIBLE);
                    reemplazo_punto_adicional_dvr.setVisibility(View.VISIBLE);
                    reemplazo_comodato_sd_hd.setVisibility(View.VISIBLE);
                }
            }
            //Toast.makeText(this, codReemplazoPuntoSmart, Toast.LENGTH_SHORT).show();
        }

        if (parent == cb_modalidad_pago) {
            posModalidadPago = position;
            //if (list_cb_modalidad_pago!=null && list_cb_modalidad_pago.size() > 0)list_cb_modalidad_pago.clear();
            if (posModalidadPago != -1) codModalidadPago = list_cb_modalidad_pago.get(posModalidadPago).getCodigo().toString();
            //Toast.makeText(this, codModalidadPago, Toast.LENGTH_SHORT).show();
            //FieldToJson oJavaToJson= new FieldToJson();
            if (codModalidadPago.equalsIgnoreCase("CONTADO")){
                txt_monto.setEnabled(true);
            }else{
                txt_monto.setEnabled(false);
                txt_monto.setSelection(0);
            }
//            Gson gson = new Gson();
//            if(codModalidadPago.equalsIgnoreCase("CONTADO")){
//                oJavaToJson.setField("cb_factura_digital");
//                oJavaToJson.setValue(pCodFacturaDigital);
//                oJavaToJson.setEnabled(cb_factura_digital.isEnabled());
//                oJavaToJson.setVisible((cb_factura_digital.getVisibility()==View.VISIBLE)?true:false);
//                oJavaToJson.setFocus(cb_factura_digital.isFocused());
//                oJavaToJson.setType("select");
//                ItemToJson oItemToJson;
//                List<ItemToJson> listItems = new ArrayList<>();
//                for (int i = 0; i < list_cb_factura_digital.size(); i++) {
//                    oItemToJson= new ItemToJson();
//                    oItemToJson.setId(list_cb_factura_digital.get(i).getCodigo());
//                    oItemToJson.setValue(list_cb_factura_digital.get(i).getDescripcion());
//                    listItems.add(oItemToJson);
//                }
//                oJavaToJson.setOptions(listItems);
//            }
//            String JSON= gson.toJson(oJavaToJson);
//            String JSON2= gson.toJson(oJavaToJson);
        }

        if (parent == cb_adeco_hd)
        {
            posADecoHd = position;
            if (posADecoHd != -1)
                codADecoHD = list_cb_adeco_hd.get(posADecoHd).getCodigo();
            //Toast.makeText(this, codADecoHD, Toast.LENGTH_SHORT).show();
        }

        if (parent == cb_deco_propiedad_cliente)
        {
            posDecoPropiedadCliente = position;

            if (posTratamiento != -1) {
                codDecoPropiedadCliente = list_cb_ctipo_doc.get(posDecoPropiedadCliente).getCodigo();
                if (codDecoPropiedadCliente.equalsIgnoreCase("6"))
                {
                    int index=-1;
                    index= getIndexSpinnerGenericoSelected(cb_deco_propiedad_cliente, list_cb_deco_propiedad_cliente,"NO");
                    cb_decos_adicionales.setSelection(index);
                    cb_decos_adicionales.setEnabled(false);
                   // set_decos_enabled(false);
                } else{
                    cb_decos_adicionales.setEnabled(true);
                }
            }
        }
    }
    private void mostrarDialogoOK(String title){
        final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        // con este tema personalizado evitamos los bordes por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
        customDialog.setCancelable(false); //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setContentView(R.layout.ly_dialog_ok); //establecemos el contenido de nuestro dialog

        TextView title1 = customDialog.findViewById(R.id.title1);
        title1.setText(title);
        ((Button) customDialog.findViewById(R.id.btnOK)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //ExpandAll();
                customDialog.dismiss();
            }
        });

        ((Button) customDialog.findViewById(R.id.btnNOATIS)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

        private void mostrarConfirmacion(String title){
            final Dialog customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
            // con este tema personalizado evitamos los bordes por defecto
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //deshabilitamos el título por defecto
            customDialog.setCancelable(false); //obligamos al usuario a pulsar los botones para cerrarlo
            customDialog.setContentView(R.layout.ly_dialog_bi_sino); //establecemos el contenido de nuestro dialog

            TextView title1 = customDialog.findViewById(R.id.title1);
            title1.setText(title);
            ((Button) customDialog.findViewById(R.id.btnSIATIS)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    callRegistrarFicha("ficha_fija_guardar");
                    //ExpandAll();
                    customDialog.dismiss();
                }
            });

            ((Button) customDialog.findViewById(R.id.btnNOATIS)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    customDialog.dismiss();
                }
            });
            customDialog.show();
        }

    private void mostrarDialogoIncidenciaMasiva()
    {
        IncidenciaMasiva_dialog myDiag = new IncidenciaMasiva_dialog();
        myDiag.setCancelable(false);
        myDiag.show(fragmentManager, "IncidenciaMasiva_dialog");
        LoginTelefonica.existeIncidenciaMasiva=false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v==imgVer){
            if (rlDatos.getVisibility()==View.GONE){
                ExpandCollapse.Expand(rlDatos);
            }else if (rlDatos.getVisibility()==View.VISIBLE){
                ExpandCollapse.Collapse(rlDatos);
            }
        }

        if (v==imgVer2){
            if (rlDatosDC.getVisibility()==View.GONE){
                ExpandCollapse.Expand(rlDatosDC);
            }else if (rlDatosDC.getVisibility()==View.VISIBLE){
                ExpandCollapse.Collapse(rlDatosDC);
            }
        }

        if (v==imgVer3){
            if (rlDatosCli.getVisibility()==View.GONE){
                ExpandCollapse.Expand(rlDatosCli);
            }else if (rlDatosCli.getVisibility()==View.VISIBLE){
                ExpandCollapse.Collapse(rlDatosCli);
            }
        }

        if (v==imgVer4){
            if (rlDatosCallCenterData.getVisibility()==View.GONE){
                ExpandCollapse.Expand(rlDatosCallCenterData);
            }else if (rlDatosCallCenterData.getVisibility()==View.VISIBLE){
                ExpandCollapse.Collapse(rlDatosCallCenterData);
            }
        }

        if (v==imgVer5){
            if (rlDatosVentaData.getVisibility()==View.GONE){
                ExpandCollapse.Expand(rlDatosVentaData);
            }else if (rlDatosVentaData.getVisibility()==View.VISIBLE){
                ExpandCollapse.Collapse(rlDatosVentaData);
            }
        }

        if (v==imgVer6){
            if (rlDatosVentaProductos.getVisibility()==View.GONE){
                ExpandCollapse.Expand(rlDatosVentaProductos);
            }else if (rlDatosVentaProductos.getVisibility()==View.VISIBLE){
                ExpandCollapse.Collapse(rlDatosVentaProductos);
            }
        }

        if (v==btn_svas_linea){
            mostrarDialogoSVALinea();
        }

        if (v==btn_svas_internet){
            mostrarDialogoSVAInternet();
        }

        if (v==btn_bloque_canales){
            cadena="";
            mostrarDialogoBloqueCanales();
        }

        if (v==btnGuardar){
            callRegistrarFicha("ficha_fija_validar");
           // ExpandAll();
        }
    }
}


