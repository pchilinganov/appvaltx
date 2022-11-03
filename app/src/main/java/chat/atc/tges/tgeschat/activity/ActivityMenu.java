package chat.atc.tges.tgeschat.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import chat.atc.tges.tgeschat.BuildConfig;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.FormularioValidacion;
import chat.atc.tges.tgeschat.FormularioOffLine;
import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.Services.RefreshLogCat;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.dialogs.IncidenciaMasiva_dialog;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;

public class ActivityMenu extends BaseVolleyActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static FragmentManager fragmentManager;
    private final int REQUEST_PERMISSION_PHONE_WRITE = 2;
    Boolean WS_MsgIncidencia_Estado = false;
    String WS_MsgIncidencia_Msg = "";
    String apeVendedor = "";
    String dniVendedor = "";
    Boolean flagVentas = false;
    String idCanal = "";
    /* access modifiers changed from: private */
    public String idLogVenta = "";
    String idSession = "";
    String idVendedorChat = "";
    JSONArray jsonArrayListaDatos = new JSONArray();
    /* access modifiers changed from: private */
    public RequestQueue mRequest;
    String nomVendedor = "";
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
    RelativeLayout rlChat;
    RelativeLayout rlOffLine;
    RelativeLayout rlConsultaTicket;
    RelativeLayout rlSpaceLeft;
    RelativeLayout rlSpaceRight;
    //RelativeLayout rlVentasFija;
    Intent service;
    SharedPreferences sharedpreferences;
    /* access modifiers changed from: private */
    public SwipeRefreshLayout swipeRefreshLayout;
    TextView tvNombreUsuario;
    String usuario = "";
    /* access modifiers changed from: private */
    public VolleyRP volley;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.ly_activity_menu);
        getWindow().setSoftInputMode(3);
        this.volley = VolleyRP.getInstance(this);
        this.mRequest = this.volley.getRequestQueue();
        this.rlChat = (RelativeLayout) findViewById(R.id.rlChat);
        this.rlChat.setOnClickListener(this);
        this.rlOffLine = (RelativeLayout) findViewById(R.id.rlOffLine);
        this.rlOffLine.setOnClickListener(this);
        fragmentManager = getFragmentManager();
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        this.swipeRefreshLayout.setOnRefreshListener(this);
        /*this.rlVentasFija = (RelativeLayout) findViewById(R.id.rlVentasFija);
        this.rlVentasFija.setOnClickListener(this);*/
        this.rlConsultaTicket = (RelativeLayout) findViewById(R.id.rlConsultaTicket);
        this.rlConsultaTicket.setOnClickListener(this);
        this.tvNombreUsuario = (TextView) findViewById(R.id.tvNombreUsuario);
        this.rlSpaceLeft = (RelativeLayout) findViewById(R.id.rlSpaceLeft);
        this.rlSpaceRight = (RelativeLayout) findViewById(R.id.rlSpaceRight);
        loadPreferences();
        ((TGestionaSession) getApplication()).setTokenMovil(FirebaseInstanceId.getInstance().getToken());
        this.tvNombreUsuario.setText(((TGestionaSession) getApplication()).getNomVendedor());
        getSupportActionBar().setTitle((CharSequence) "MovistarTAyuda");
        //habilitaModulosxUsuario();
        callValidarAccesoContingencia();
        this.swipeRefreshLayout.post(new Runnable() {
            public void run() {
                ActivityMenu.this.callValidarAccesoContingencia();
            }
        });
        listarIncidencias();
        this.service = new Intent(this, RefreshLogCat.class);
        startService(this.service);
        showPhoneStatePermissionWrite();
        this.rlChat.setVisibility(0);
        this.rlOffLine.setVisibility(0);
        //this.rlVentasFija.setVisibility(0);
        this.rlSpaceLeft.setVisibility(8);
        this.rlSpaceRight.setVisibility(8);
    }

    private void loadPreferences() {
        this.sharedpreferences = getSharedPreferences("mta_loged", 0);
        this.usuario = this.sharedpreferences.getString("usuario", (String) null);
        this.idVendedorChat = this.sharedpreferences.getString("idVendedorChat", (String) null);
        this.nomVendedor = this.sharedpreferences.getString("nomVendedor", (String) null);
        this.apeVendedor = this.sharedpreferences.getString("apeVendedor", (String) null);
        this.idCanal = this.sharedpreferences.getString("idCanal", (String) null);
        this.dniVendedor = this.sharedpreferences.getString("dniVendedor", (String) null);
        this.idSession = this.sharedpreferences.getString("idSession", (String) null);
        this.flagVentas = Boolean.valueOf(this.sharedpreferences.getBoolean("flagVentas", false));
        ((TGestionaSession) getApplication()).setUsuario(this.usuario);
        ((TGestionaSession) getApplication()).setIdVendedorChat(this.idVendedorChat);
        varPublicas.idVendedorChat = this.idVendedorChat;
        ((TGestionaSession) getApplication()).setNomVendedor(this.nomVendedor);
        ((TGestionaSession) getApplication()).setApeVendedor(this.apeVendedor);
        ((TGestionaSession) getApplication()).setDniVendedor(this.dniVendedor);
        ((TGestionaSession) getApplication()).setIdCanal(this.idCanal);
        varPublicas.idCanal = this.idCanal;
        ((TGestionaSession) getApplication()).setIdSession(this.idSession);
        varPublicas.idSession = this.idSession;
        this.tvNombreUsuario.setText(this.nomVendedor);
    }

    public void onResume() {
        super.onResume();
        if (!LoginTelefonica.vistaIncidenciaMasiva.booleanValue()) {
            mostrarDialogoIncidenciaMasiva();
        }
        loadPreferences();
        this.swipeRefreshLayout.post(new Runnable() {
            public void run() {
                ActivityMenu.this.callValidarAccesoContingencia();
            }
        });
    }

    public void onRefresh() {
        this.swipeRefreshLayout.post(new Runnable() {
            public void run() {
                ActivityMenu.this.callValidarAccesoContingencia();
            }
        });
    }

    private void listarIncidencias() {
        VolleyRP.addToQueue(new StringRequest(1, varPublicas.URL_DESARROLLO + "listarIncidencias", new Response.Listener<String>() {
            public void onResponse(String str) {
                LoginTelefonica.incidencias.clear();
                ActivityMenu.this.getDataIncidencias(str);
                if (!LoginTelefonica.vistaIncidenciaMasiva.booleanValue()) {
                    ActivityMenu.this.mostrarDialogoIncidenciaMasiva();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                ActivityMenu activityMenu = ActivityMenu.this;
                Toast.makeText(activityMenu, "Error en listarIncidencias(): " + volleyError.toString(), 0).show();
            }
        }) {
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap hashMap = new HashMap();
                hashMap.put("idCanal", varPublicas.idCanal);
                return hashMap;
            }
        }, this.mRequest, this, this.volley);
    }

    public void getDataIncidencias(String str) {
        try {
            JSONArray jSONArray = new JSONArray(str);
            this.WS_MsgIncidencia_Msg = jSONArray.getJSONObject(0).getString("Msg").toString();
            this.WS_MsgIncidencia_Estado = Boolean.valueOf(jSONArray.getJSONObject(0).getBoolean("Estado"));
            boolean isNull = jSONArray.getJSONObject(0).isNull("ListaDatos");
            if (!isNull) {
                this.jsonArrayListaDatos = jSONArray.getJSONObject(0).getJSONArray("ListaDatos");
            }
            if (this.WS_MsgIncidencia_Estado.booleanValue() && !isNull && this.jsonArrayListaDatos.length() > 0) {
                LoginTelefonica.existeIncidenciaMasiva = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void habilitaModulosxUsuario() {
        if (this.idCanal == null || !this.idCanal.equalsIgnoreCase("2841")) {
            this.rlChat.setVisibility(0);
        } else {
            this.rlChat.setVisibility(8);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ficha, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_Notificaciones) {
            if (GlobalFunctions.isOnline(getApplicationContext())) {
                mostrarDialogoIncidenciaMasiva();
            } else {
                Toast.makeText(this, "Revise su conexión a internet.", 1).show();
            }
            return true;
        }
        if (itemId == R.id.action_CerrarSesion) {
            GlobalFunctions.logout(getApplicationContext(), ((TGestionaSession) getApplication()).getUsuario(), this.volley, this.mRequest);
            if (this.sharedpreferences.getBoolean("flagVentas", false)) {
                finishAffinity();
                GlobalFunctions.killAppBypackage(getApplicationContext(), this, BuildConfig.APPLICATION_ID);
            } else {
                GlobalFunctions.newIntent(this, LoginTelefonica.class);
                LoginTelefonica.uri = null;
            }
            GlobalFunctions.limpiarShared(this);
            Toast.makeText(getApplicationContext(), "Sesión MTA finalizada", 0).show();
            LoginTelefonica.vistaIncidenciaMasiva = false;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onBackPressed() {
        if (LoginTelefonica.uri == null || !LoginTelefonica.uri.toString().equalsIgnoreCase("android-app://pe.vasslatam.movistar.mobile.sales")) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.setFlags(268435456);
            startActivity(intent);
            return;
        }
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("pe.vasslatam.movistar.mobile.sales");
        if (launchIntentForPackage != null) {
            startActivity(launchIntentForPackage);
        }
    }

    /* access modifiers changed from: private */
    public void callValidarAccesoContingencia() {
        Log.d("ws", "valida_canal_contingencia_fija");
        HashMap hashMap = new HashMap();
        hashMap.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        hashMap.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        hashMap.put("token", "asdasd");
        GlobalFunctions.callVolleyStringRequest(this, hashMap, varPublicas.URL_DESARROLLO + "valida_canal_contingencia_fija", this.volley, this.mRequest, new ServerCallback() {
            public void onSuccess(String str) {
                Boolean.valueOf(false);
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    Boolean valueOf = Boolean.valueOf(jSONArray.getJSONObject(0).isNull("session_status"));
                    if (!valueOf.booleanValue()) {
                        GlobalFunctions.validaSesion(valueOf.booleanValue(), ActivityMenu.this.getApplicationContext());
                        GlobalFunctions.logout(ActivityMenu.this.getApplicationContext(), ((TGestionaSession) ActivityMenu.this.getApplication()).getUsuario(), ActivityMenu.this.volley, ActivityMenu.this.mRequest);
                        if (ActivityMenu.this.sharedpreferences.getBoolean("flagVentas", false)) {
                            ActivityMenu.this.finishAffinity();
                        } else {
                            GlobalFunctions.newIntent(ActivityMenu.this, LoginTelefonica.class);
                            LoginTelefonica.uri = null;
                        }
                        GlobalFunctions.limpiarShared(ActivityMenu.this.getApplicationContext());
                        Toast.makeText(ActivityMenu.this.getApplicationContext(), "Sesión MTA finalizada", 0).show();
                        LoginTelefonica.vistaIncidenciaMasiva = false;
                    }
                    int i = jSONArray.getJSONObject(0).getInt("NroMensaje");
                    if (!jSONArray.getJSONObject(0).isNull("NroMensaje")) {
                        if (i == 200) {
                            ActivityMenu.this.rlChat.setVisibility(0);
                            ActivityMenu.this.rlOffLine.setVisibility(0);
                            //ActivityMenu.this.rlVentasFija.setVisibility(0);
                            ActivityMenu.this.rlSpaceLeft.setVisibility(8);
                            ActivityMenu.this.rlSpaceRight.setVisibility(8);
                        } else if (i == 250) {
                            jSONArray.getJSONObject(0).getString("Msg");
                            ActivityMenu.this.rlChat.setVisibility(0);
                            ActivityMenu.this.rlOffLine.setVisibility(0);
                            //ActivityMenu.this.rlVentasFija.setVisibility(8);
                            ActivityMenu.this.rlSpaceLeft.setVisibility(4);
                            ActivityMenu.this.rlSpaceRight.setVisibility(4);
                        }
                    }
                    ActivityMenu.this.swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callValidarBase() {
        Log.d("ws", "valida_base_vendedor");
        HashMap hashMap = new HashMap();
        hashMap.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        hashMap.put("dniVendedor", ((TGestionaSession) getApplication()).getDniVendedor());
        hashMap.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        hashMap.put("token", "asdads");
        GlobalFunctions.callVolleyStringRequest(this, hashMap, varPublicas.URL_DESARROLLO + "valida_base_vendedor", this.volley, this.mRequest, new ServerCallback() {
            public void onSuccess(String str) {
                Boolean.valueOf(false);
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    Boolean valueOf = Boolean.valueOf(jSONArray.getJSONObject(0).isNull("session_status"));
                    if (!valueOf.booleanValue()) {
                        GlobalFunctions.validaSesion(valueOf.booleanValue(), ActivityMenu.this.getApplicationContext());
                        GlobalFunctions.logout(ActivityMenu.this.getApplicationContext(), ((TGestionaSession) ActivityMenu.this.getApplication()).getUsuario(), ActivityMenu.this.volley, ActivityMenu.this.mRequest);
                        if (ActivityMenu.this.sharedpreferences.getBoolean("flagVentas", false)) {
                            ActivityMenu.this.finishAffinity();
                            GlobalFunctions.killAppBypackage(ActivityMenu.this.getApplicationContext(), ActivityMenu.this, BuildConfig.APPLICATION_ID);
                        } else {
                            GlobalFunctions.newIntent(ActivityMenu.this, LoginTelefonica.class);
                            LoginTelefonica.uri = null;
                        }
                        GlobalFunctions.limpiarShared(ActivityMenu.this.getApplicationContext());
                        Toast.makeText(ActivityMenu.this.getApplicationContext(), "Sesión MTA finalizada", 0).show();
                        LoginTelefonica.vistaIncidenciaMasiva = false;
                    }
                    int i = !jSONArray.getJSONObject(0).isNull("NroMensaje") ? jSONArray.getJSONObject(0).getInt("NroMensaje") : 0;
                    if (i == 200) {
                        GlobalFunctions.newIntent(ActivityMenu.this, ActivityVisorVentasFija.class);
                    } else if (i == 250) {
                        Toast.makeText(ActivityMenu.this, jSONArray.getJSONObject(0).getString("Msg"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showPhoneStatePermissionWrite() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            showExplanation("Permiso necesario", "Para continuar con el uso del sistema ", "android.permission.WRITE_EXTERNAL_STORAGE", 2);
        } else {
            requestPermission("android.permission.WRITE_EXTERNAL_STORAGE", 2);
        }
    }

    private void showExplanation(String str, String str2, final String str3, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(str).setMessage(str2).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityMenu.this.requestPermission(str3, i);
            }
        });
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void requestPermission(String str, int i) {
        ActivityCompat.requestPermissions(this, new String[]{str}, i);
    }

    /* access modifiers changed from: private */
    public void mostrarDialogoIncidenciaMasiva() {
        IncidenciaMasiva_dialog incidenciaMasiva_dialog = new IncidenciaMasiva_dialog();
        incidenciaMasiva_dialog.setCancelable(false);
        incidenciaMasiva_dialog.show(fragmentManager, "IncidenciaMasiva_dialog");
        LoginTelefonica.vistaIncidenciaMasiva = true;
    }

    private void validaventafija() {
        HashMap hashMap = new HashMap();
        hashMap.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        hashMap.put("token", "asdads");
        GlobalFunctions.callVolleyStringRequest(this, hashMap, varPublicas.URL_DESARROLLO + "inicioLogVentaApp", this.volley, this.mRequest, new ServerCallback() {
            public void onSuccess(String str) {
                Boolean.valueOf(false);
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    String unused = ActivityMenu.this.idLogVenta = jSONArray.getJSONObject(0).getJSONObject("listaDatos").getString("idlogventa");
                    Log.d("RRRRRRR", jSONArray.toString());
                    Intent intent = new Intent(ActivityMenu.this.getApplicationContext(), ActivityMenuIncidencia.class);
                    intent.putExtra("idLogVenta", ActivityMenu.this.idLogVenta);
                    ActivityMenu.this.getApplicationContext().startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClick(View view) {
        if (view == this.rlChat) {
            GlobalFunctions.newIntent(this, FormularioValidacion.class);
        }
        if (view == this.rlOffLine) {
            GlobalFunctions.newIntent(this, FormularioOffLine.class);
        }
        /*if (view == this.rlVentasFija) {
            validaventafija();
        }*/
        if (view == this.rlConsultaTicket) {
            GlobalFunctions.newIntent(this, ActivityConsultaTicket.class);
        }
    }
}
