package chat.atc.tges.tgeschat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_Generico;
import chat.atc.tges.tgeschat.model.DTO_Invoices;
import chat.atc.tges.tgeschat.model.DTO_Tipo;
import chat.atc.tges.tgeschat.model.DTO_ticket;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

public class ActivityConsultaTicket extends BaseVolleyActivity implements View.OnClickListener {
    public static String pCodEstado = "";
    public static String pCodTipo = "";
    public static String pCodTipoDoc = "";
    public static String pcodMotivo = "";
    Button btnbuscar;
    TextView cantidadTicket;
    AutoCompleteTextView cb_estado;
    AutoCompleteTextView cb_motivo;
    AutoCompleteTextView cb_tipo_documento;
    AutoCompleteTextView cbo_tipo;
    Dialog customDialog;
    List<DTO_Generico> listaDocumentos = new ArrayList();
    List<DTO_Generico> listaEstado = new ArrayList();
    List<DTO_Generico> listaMotivo = new ArrayList();
    List<DTO_Tipo> listaTipo = new ArrayList();
    private RequestQueue mRequest;
    private TableLayout mTableLayout;
    int posTipo = -1;
    List<String> sListaDocumentos = new ArrayList();
    List<String> sListaEstado = new ArrayList();
    List<String> sListaMotivo = new ArrayList();
    List<String> sListaTipo = new ArrayList();
    EditText txt_nro_consulta;
    EditText txt_nro_documento;
    EditText txt_nro_ticket;
    private VolleyRP volley;

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.ly_activity_consulta_ticket);
        this.volley = VolleyRP.getInstance(this);
        this.mRequest = this.volley.getRequestQueue();
        this.mTableLayout = (TableLayout) findViewById(R.id.tableInvoices);
        this.cbo_tipo = (AutoCompleteTextView) findViewById(R.id.cbo_tipo);
        this.txt_nro_ticket = (EditText) findViewById(R.id.txt_nro_ticket);
        this.txt_nro_consulta = (EditText) findViewById(R.id.txt_nro_consulta);
        this.cb_motivo = (AutoCompleteTextView) findViewById(R.id.cb_motivo);
        this.cb_estado = (AutoCompleteTextView) findViewById(R.id.cb_estado);
        this.cb_tipo_documento = (AutoCompleteTextView) findViewById(R.id.cb_tipo_documento);
        this.txt_nro_documento = (EditText) findViewById(R.id.txt_nro_documento);
        this.btnbuscar = (Button) findViewById(R.id.btnbuscar);
        this.cantidadTicket = (TextView) findViewById(R.id.cantidadTicket);
        this.mTableLayout.setStretchAllColumns(true);
        this.customDialog = new Dialog(this, R.style.Theme_Dialog_Translucent);
        this.customDialog.requestWindowFeature(1);
        this.customDialog.setCancelable(false);
        this.customDialog.setContentView(R.layout.ly_dialog_loading);
        new LoadDataTask().execute(new Integer[]{0});
        this.cbo_tipo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ActivityConsultaTicket.pCodTipo = ActivityConsultaTicket.this.listaTipo.get(i).getId();
                ActivityConsultaTicket.this.getComboTipo(ActivityConsultaTicket.pCodTipo);
            }
        });
        this.cb_estado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ActivityConsultaTicket.pCodEstado = ActivityConsultaTicket.this.listaEstado.get(i).getCodigo();
            }
        });
        this.cb_motivo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ActivityConsultaTicket.pcodMotivo = ActivityConsultaTicket.this.listaMotivo.get(i).getCodigo();
            }
        });
        this.cb_tipo_documento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                ActivityConsultaTicket.pCodTipoDoc = ActivityConsultaTicket.this.listaDocumentos.get(i).getCodigo();
            }
        });
        this.btnbuscar.setOnClickListener(this);
        poblarCampos();
    }

    /* access modifiers changed from: private */
    public void getComboTipo(String str) {
        this.customDialog.show();
        HashMap hashMap = new HashMap();
        hashMap.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        hashMap.put("token", ((TGestionaSession) getApplication()).getTokenMovil());
        hashMap.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        hashMap.put("idtipo", str);
        GlobalFunctions.callVolleyStringRequest(this, hashMap, varPublicas.URL_DESARROLLO + "getMotivoByTipo_combo", this.volley, this.mRequest, new ServerCallback() {
            public void onSuccess(String str) {
                try {
                    JSONArray jSONArray = new JSONArray(str).getJSONObject(0).getJSONObject("listaDatos").getJSONArray("idmotivo");
                    ActivityConsultaTicket.this.sListaMotivo.clear();
                    for (int i = 0; i < jSONArray.length(); i++) {
                        DTO_Generico dTO_Generico = new DTO_Generico();
                        dTO_Generico.setCodigo(jSONArray.getJSONObject(i).getString("id"));
                        dTO_Generico.setDescripcion(jSONArray.getJSONObject(i).getString(FirebaseAnalytics.Param.VALUE));
                        dTO_Generico.setSeleccionado(false);
                        ActivityConsultaTicket.this.sListaMotivo.add(jSONArray.getJSONObject(i).getString(FirebaseAnalytics.Param.VALUE));
                        ActivityConsultaTicket.this.listaMotivo.add(dTO_Generico);
                    }
                    GlobalFunctions.rellenarActvVentasFija(ActivityConsultaTicket.this.getApplicationContext(), ActivityConsultaTicket.this.cb_motivo, ActivityConsultaTicket.this.sListaMotivo, -1, "");
                    ActivityConsultaTicket.this.customDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ActivityConsultaTicket.this.customDialog.dismiss();
                }
            }
        });
    }

    public void poblarCampos() {
        this.customDialog.show();
        HashMap hashMap = new HashMap();
        hashMap.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        hashMap.put("token", ((TGestionaSession) getApplication()).getTokenMovil());
        hashMap.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        GlobalFunctions.callVolleyStringRequest(this, hashMap, varPublicas.URL_DESARROLLO + "modulo_consulta_ticket_combo", this.volley, this.mRequest, new ServerCallback() {
            public void onSuccess(String str) {
                try {
                    JSONObject jSONObject = new JSONArray(str).getJSONObject(0).getJSONObject("listaDatos");
                    JSONArray jSONArray = jSONObject.getJSONArray("idtipo");
                    JSONArray jSONArray2 = jSONObject.getJSONArray("idstateTicket");
                    JSONArray jSONArray3 = jSONObject.getJSONArray("cbo_type_document");
                    for (int i = 0; i < jSONArray.length(); i++) {
                        DTO_Tipo dTO_Tipo = new DTO_Tipo();
                        dTO_Tipo.setId(jSONArray.getJSONObject(i).getString("id"));
                        dTO_Tipo.setDescripcion(jSONArray.getJSONObject(i).getString(FirebaseAnalytics.Param.VALUE));
                        dTO_Tipo.setSelected(false);
                        dTO_Tipo.setVisible(true);
                        ActivityConsultaTicket.this.sListaTipo.add(jSONArray.getJSONObject(i).getString(FirebaseAnalytics.Param.VALUE));
                        ActivityConsultaTicket.this.listaTipo.add(dTO_Tipo);
                    }
                    GlobalFunctions.rellenarActvVentasFija(ActivityConsultaTicket.this.getApplicationContext(), ActivityConsultaTicket.this.cbo_tipo, ActivityConsultaTicket.this.sListaTipo, -1, "");
                    DTO_Generico dTO_Generico = null;
                    for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                        dTO_Generico = new DTO_Generico();
                        dTO_Generico.setCodigo(jSONArray2.getJSONObject(i2).getString("id"));
                        dTO_Generico.setDescripcion(jSONArray2.getJSONObject(i2).getString(FirebaseAnalytics.Param.VALUE));
                        dTO_Generico.setSeleccionado(false);
                        ActivityConsultaTicket.this.sListaEstado.add(jSONArray2.getJSONObject(i2).getString(FirebaseAnalytics.Param.VALUE));
                        ActivityConsultaTicket.this.listaEstado.add(dTO_Generico);
                    }
                    GlobalFunctions.rellenarActvVentasFija(ActivityConsultaTicket.this.getApplicationContext(), ActivityConsultaTicket.this.cb_estado, ActivityConsultaTicket.this.sListaEstado, -1, "");
                    for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                        DTO_Generico dTO_Generico2 = new DTO_Generico();
                        dTO_Generico2.setCodigo(jSONArray3.getJSONObject(i3).getString("id"));
                        dTO_Generico2.setDescripcion(jSONArray3.getJSONObject(i3).getString(FirebaseAnalytics.Param.VALUE));
                        dTO_Generico2.setSeleccionado(false);
                        ActivityConsultaTicket.this.sListaDocumentos.add(jSONArray3.getJSONObject(i3).getString(FirebaseAnalytics.Param.VALUE));
                        ActivityConsultaTicket.this.listaDocumentos.add(dTO_Generico);
                    }
                    GlobalFunctions.rellenarActvVentasFija(ActivityConsultaTicket.this.getApplicationContext(), ActivityConsultaTicket.this.cb_tipo_documento, ActivityConsultaTicket.this.sListaDocumentos, -1, "");
                    ActivityConsultaTicket.this.customDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ActivityConsultaTicket.this.customDialog.dismiss();
                }
            }
        });
    }

    public void loadData(ArrayList<DTO_ticket> arrayList) {
        int i;
        ArrayList<DTO_ticket> arrayList2 = arrayList;
        TextView textView = this.cantidadTicket;
        textView.setText("Nro de tickets " + arrayList.size());
        int dimension = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        int dimension2 = (int) getResources().getDimension(R.dimen.font_size_small);
        getResources().getDimension(R.dimen.font_size_medium);
        new DTO_Invoices();
        DTO_ticket[] dTO_ticketArr = new DTO_ticket[arrayList.size()];
        int i2 = 0;
        int i3 = 0;
        while (i3 < arrayList.size()) {
            DTO_ticket dTO_ticket = new DTO_ticket();
            dTO_ticket.f87id = arrayList2.get(i3).f87id;
            dTO_ticket.contador = arrayList2.get(i3).contador;
            dTO_ticket.amountDue = BigDecimal.valueOf(((double) i3) * 20.0d);
            int i4 = i3 + 1;
            dTO_ticket.invoiceAmount = BigDecimal.valueOf(((double) i4) * 120.0d);
            dTO_ticket.estado = arrayList2.get(i3).estado;
            dTO_ticket.motivo = arrayList2.get(i3).motivo;
            dTO_ticket.solicitante = arrayList2.get(i3).solicitante;
            dTO_ticketArr[i3] = dTO_ticket;
            i3 = i4;
        }
        new SimpleDateFormat("dd MMM, yyyy");
        new DecimalFormat("0.00");
        int length = dTO_ticketArr.length;
        this.mTableLayout.removeAllViews();
        int i5 = -1;
        while (i5 < length) {
            DTO_ticket dTO_ticket2 = null;
            if (i5 > -1) {
                dTO_ticket2 = dTO_ticketArr[i5];
            } else {
                new TextView(this).setText("");
            }
            TextView textView2 = new TextView(this);
            textView2.setLayoutParams(new TableRow.LayoutParams(-2, -2));
            textView2.setGravity(3);
            int i6 = 5;
            textView2.setPadding(5, 15, i2, 15);
            if (i5 == -1) {
                textView2.setText("#");
                textView2.setBackgroundColor(Color.parseColor("#f0f0f0"));
                textView2.setTextSize(i2, (float) dimension2);
            } else {
                textView2.setBackgroundColor(Color.parseColor("#f8f8f8"));
                textView2.setText(String.valueOf(dTO_ticket2.contador));
                textView2.setTextSize(i2, (float) dimension);
            }
            TextView textView3 = new TextView(this);
            if (i5 == -1) {
                textView3.setLayoutParams(new TableRow.LayoutParams(-1, -2));
                textView3.setTextSize(i2, (float) dimension2);
            } else {
                textView3.setLayoutParams(new TableRow.LayoutParams(-2, -1));
                textView3.setTextSize(i2, (float) dimension);
            }
            textView3.setGravity(17);
            textView3.setPadding(5, 15, i2, 15);
            if (i5 == -1) {
                textView3.setText("Ticket");
                textView3.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                textView3.setBackgroundColor(Color.parseColor("#6BC832"));
                textView3.setTextColor(Color.parseColor("#FFFFFF"));
                textView3.setPadding(5, 5, 5, 5);
                textView3.setTextSize(13.0f);
                textView3.setText(GlobalFunctions.escapeNull(String.valueOf(dTO_ticket2.f87id)));
                final int i7 = dTO_ticket2.f87id;
                textView3.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(ActivityConsultaTicket.this, ActivityDetalleTicket.class);
                        intent.putExtra("idticket", i7);
                        ActivityConsultaTicket.this.getApplicationContext().startActivity(intent);
                    }
                });
            }
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            linearLayout.setPadding(i2, 10, i2, 10);
            linearLayout.setBackgroundColor(Color.parseColor("#f8f8f8"));
            TextView textView4 = new TextView(this);
            if (i5 == -1) {
                textView4.setLayoutParams(new TableRow.LayoutParams(-1, -1));
                textView4.setPadding(5, 5, i2, 5);
                textView4.setTextSize(i2, (float) dimension2);
            } else {
                textView4.setLayoutParams(new TableRow.LayoutParams(-1, -1));
                textView4.setPadding(5, i2, i2, 5);
                textView4.setTextSize(i2, (float) dimension);
            }
            textView4.setGravity(48);
            if (i5 == -1) {
                textView4.setText("Estado Ticket");
                textView4.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
                textView4.setGravity(17);
                textView4.setTextSize(i2, (float) dimension);
                textView4.setPadding(2, 2, 1, 5);
                textView4.setTextColor(Color.parseColor("#00afff"));
                textView4.setBackgroundColor(Color.parseColor("#ffffff"));
                if (Build.VERSION.SDK_INT >= 24) {
                    textView4.setText(Html.fromHtml(dTO_ticket2.estado, i2));
                } else {
                    textView4.setText(Html.fromHtml(dTO_ticket2.estado));
                }
            }
            linearLayout.addView(textView4);
            LinearLayout linearLayout2 = new LinearLayout(this);
            linearLayout2.setOrientation(1);
            linearLayout2.setGravity(5);
            linearLayout2.setPadding(i2, 10, i2, 10);
            linearLayout2.setLayoutParams(new TableRow.LayoutParams(-1, -1));
            TextView textView5 = new TextView(this);
            if (i5 == -1) {
                textView5.setLayoutParams(new TableRow.LayoutParams(-1, -1));
                textView5.setPadding(5, 5, 1, 5);
                linearLayout2.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                textView5.setLayoutParams(new TableRow.LayoutParams(-1, -2));
                i6 = 5;
                textView5.setPadding(5, 0, 1, 5);
                linearLayout2.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            textView5.setGravity(i6);
            if (i5 == -1) {
                textView5.setText("Solicitante");
                textView5.setBackgroundColor(Color.parseColor("#f7f7f7"));
                i = 0;
                textView5.setTextSize(0, (float) dimension2);
            } else {
                i = 0;
                textView5.setBackgroundColor(Color.parseColor("#ffffff"));
                textView5.setTextColor(Color.parseColor("#000000"));
                textView5.setText(dTO_ticket2.solicitante);
                textView5.setTextSize(0, (float) dimension);
            }
            linearLayout2.addView(textView5);
            TableRow tableRow = new TableRow(this);
            int i8 = i5 + 1;
            tableRow.setId(i8);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(-1, -2);
            layoutParams.setMargins(i, i, i, i);
            tableRow.setPadding(i, i, i, i);
            tableRow.setLayoutParams(layoutParams);
            tableRow.addView(textView2);
            tableRow.addView(textView3);
            tableRow.addView(linearLayout);
            tableRow.addView(linearLayout2);
            if (i5 > -1) {
                tableRow.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        TableRow tableRow = (TableRow) view;
                    }
                });
            }
            this.mTableLayout.addView(tableRow, layoutParams);
            if (i5 > -1) {
                TableRow tableRow2 = new TableRow(this);
                TableLayout.LayoutParams layoutParams2 = new TableLayout.LayoutParams(-1, -2);
                layoutParams2.setMargins(0, 0, 0, 0);
                tableRow2.setLayoutParams(layoutParams2);
                TextView textView6 = new TextView(this);
                TableRow.LayoutParams layoutParams3 = new TableRow.LayoutParams(-1, -2);
                layoutParams3.span = 4;
                textView6.setLayoutParams(layoutParams3);
                textView6.setBackgroundColor(Color.parseColor("#d9d9d9"));
                textView6.setHeight(1);
                tableRow2.addView(textView6);
                this.mTableLayout.addView(tableRow2, layoutParams2);
            }
            i5 = i8;
            i2 = 0;
        }
    }

    private void buscarTicket() {
        this.customDialog.show();
        HashMap hashMap = new HashMap();
        hashMap.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        hashMap.put("token", ((TGestionaSession) getApplication()).getTokenMovil());
        hashMap.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        hashMap.put("idt", this.txt_nro_ticket.getText().toString());
        hashMap.put("num_order", this.txt_nro_consulta.getText().toString());
        hashMap.put("idtipo", pCodTipo);
        hashMap.put("idstateTicket", pCodEstado);
        hashMap.put("cbo_type_document", pCodTipoDoc);
        hashMap.put("txt_number_document", this.txt_nro_documento.getText().toString());
        GlobalFunctions.callVolleyStringRequest(this, hashMap, varPublicas.URL_DESARROLLO + "buscarTickets", this.volley, this.mRequest, new ServerCallback() {
            public void onSuccess(String str) {
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    if (jSONArray.getJSONObject(0).getBoolean("Estado")) {
                        JSONArray jSONArray2 = jSONArray.getJSONObject(0).getJSONArray("listaDatos");
                        ArrayList arrayList = new ArrayList();
                        for (int i = 0; i < jSONArray2.length(); i++) {
                            DTO_ticket dTO_ticket = new DTO_ticket();
                            dTO_ticket.solicitante = jSONArray2.getJSONObject(i).getString("solicitante");
                            dTO_ticket.motivo = jSONArray2.getJSONObject(i).getString("motivo");
                            dTO_ticket.estado = jSONArray2.getJSONObject(i).getString("estado");
                            dTO_ticket.contador = jSONArray2.getJSONObject(i).getInt("cont");
                            dTO_ticket.f87id = jSONArray2.getJSONObject(i).getInt("id");
                            dTO_ticket.tipo = jSONArray2.getJSONObject(i).getString("tipo");
                            arrayList.add(dTO_ticket);
                        }
                        ActivityConsultaTicket.this.loadData(arrayList);
                    } else {
                        Toast.makeText(ActivityConsultaTicket.this.getApplicationContext(), jSONArray.getJSONObject(0).getJSONObject("ListaDatos").getJSONArray("error").getJSONObject(0).getString("message"), 1).show();
                    }
                    ActivityConsultaTicket.this.customDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ActivityConsultaTicket.this.customDialog.dismiss();
                }
            }
        });
    }

    public void onClick(View view) {
        if (this.btnbuscar == view) {
            buscarTicket();
        }
    }

    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
        }

        LoadDataTask() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Integer... numArr) {
            try {
                Thread.sleep(2000);
                return "Task Completed.";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "Task Completed.";
            }
        }
    }
}
