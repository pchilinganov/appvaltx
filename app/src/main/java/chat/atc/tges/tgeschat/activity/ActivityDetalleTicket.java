package chat.atc.tges.tgeschat.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.adapter.RvChatTicket;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_ticket_chat;
import chat.atc.tges.tgeschat.util.GlobalFunctions;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;
import com.android.volley.RequestQueue;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityDetalleTicket extends BaseVolleyActivity {
    private ImageButton btngotoback;
    Dialog customDialog;
    int idTicket = 0;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle1;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle10;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle11;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle12;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle13;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle14;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle15;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle16;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle17;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle18;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle19;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle2;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle20;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle21;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle22;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle23;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle24;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle25;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle26;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle27;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle28;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle29;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle3;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle30;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle4;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle5;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle6;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle7;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle8;
    /* access modifiers changed from: private */
    public LinearLayout layoutdetalle9;
    /* access modifiers changed from: private */
    public RvChatTicket mAdapter;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;
    private RequestQueue mRequest;
    private TextView nroTicket;
    /* access modifiers changed from: private */
    public TextView txtTitulo1;
    /* access modifiers changed from: private */
    public TextView txtTitulo10;
    /* access modifiers changed from: private */
    public TextView txtTitulo11;
    /* access modifiers changed from: private */
    public TextView txtTitulo12;
    /* access modifiers changed from: private */
    public TextView txtTitulo13;
    /* access modifiers changed from: private */
    public TextView txtTitulo14;
    /* access modifiers changed from: private */
    public TextView txtTitulo15;
    /* access modifiers changed from: private */
    public TextView txtTitulo16;
    /* access modifiers changed from: private */
    public TextView txtTitulo17;
    /* access modifiers changed from: private */
    public TextView txtTitulo18;
    /* access modifiers changed from: private */
    public TextView txtTitulo19;
    /* access modifiers changed from: private */
    public TextView txtTitulo2;
    /* access modifiers changed from: private */
    public TextView txtTitulo20;
    /* access modifiers changed from: private */
    public TextView txtTitulo21;
    /* access modifiers changed from: private */
    public TextView txtTitulo22;
    /* access modifiers changed from: private */
    public TextView txtTitulo23;
    /* access modifiers changed from: private */
    public TextView txtTitulo24;
    /* access modifiers changed from: private */
    public TextView txtTitulo25;
    /* access modifiers changed from: private */
    public TextView txtTitulo26;
    /* access modifiers changed from: private */
    public TextView txtTitulo27;
    /* access modifiers changed from: private */
    public TextView txtTitulo28;
    /* access modifiers changed from: private */
    public TextView txtTitulo29;
    /* access modifiers changed from: private */
    public TextView txtTitulo3;
    /* access modifiers changed from: private */
    public TextView txtTitulo30;
    /* access modifiers changed from: private */
    public TextView txtTitulo4;
    /* access modifiers changed from: private */
    public TextView txtTitulo5;
    /* access modifiers changed from: private */
    public TextView txtTitulo6;
    /* access modifiers changed from: private */
    public TextView txtTitulo7;
    /* access modifiers changed from: private */
    public TextView txtTitulo8;
    /* access modifiers changed from: private */
    public TextView txtTitulo9;
    /* access modifiers changed from: private */
    public TextView txtValor1;
    /* access modifiers changed from: private */
    public TextView txtValor10;
    /* access modifiers changed from: private */
    public TextView txtValor11;
    /* access modifiers changed from: private */
    public TextView txtValor12;
    /* access modifiers changed from: private */
    public TextView txtValor13;
    /* access modifiers changed from: private */
    public TextView txtValor14;
    /* access modifiers changed from: private */
    public TextView txtValor15;
    /* access modifiers changed from: private */
    public TextView txtValor16;
    /* access modifiers changed from: private */
    public TextView txtValor17;
    /* access modifiers changed from: private */
    public TextView txtValor18;
    /* access modifiers changed from: private */
    public TextView txtValor19;
    /* access modifiers changed from: private */
    public TextView txtValor2;
    /* access modifiers changed from: private */
    public TextView txtValor20;
    /* access modifiers changed from: private */
    public TextView txtValor21;
    /* access modifiers changed from: private */
    public TextView txtValor22;
    /* access modifiers changed from: private */
    public TextView txtValor23;
    /* access modifiers changed from: private */
    public TextView txtValor24;
    /* access modifiers changed from: private */
    public TextView txtValor25;
    /* access modifiers changed from: private */
    public TextView txtValor26;
    /* access modifiers changed from: private */
    public TextView txtValor27;
    /* access modifiers changed from: private */
    public TextView txtValor28;
    /* access modifiers changed from: private */
    public TextView txtValor29;
    /* access modifiers changed from: private */
    public TextView txtValor3;
    /* access modifiers changed from: private */
    public TextView txtValor30;
    /* access modifiers changed from: private */
    public TextView txtValor4;
    /* access modifiers changed from: private */
    public TextView txtValor5;
    /* access modifiers changed from: private */
    public TextView txtValor6;
    /* access modifiers changed from: private */
    public TextView txtValor7;
    /* access modifiers changed from: private */
    public TextView txtValor8;
    /* access modifiers changed from: private */
    public TextView txtValor9;
    private VolleyRP volley;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.ly_activity_detalle_ticket);
        this.volley = VolleyRP.getInstance(this);
        this.mRequest = this.volley.getRequestQueue();
        this.customDialog = new Dialog(this, R.style.Theme_Dialog_Translucent);
        this.customDialog.requestWindowFeature(1);
        this.customDialog.setCancelable(false);
        this.customDialog.setContentView(R.layout.ly_dialog_loading);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.btngotoback = (ImageButton) findViewById(R.id.btngotoback);
        this.txtTitulo1 = (TextView) findViewById(R.id.txtTitulo1);
        this.txtTitulo2 = (TextView) findViewById(R.id.txtTitulo2);
        this.txtTitulo3 = (TextView) findViewById(R.id.txtTitulo3);
        this.txtTitulo4 = (TextView) findViewById(R.id.txtTitulo4);
        this.txtTitulo5 = (TextView) findViewById(R.id.txtTitulo5);
        this.txtTitulo6 = (TextView) findViewById(R.id.txtTitulo6);
        this.txtTitulo7 = (TextView) findViewById(R.id.txtTitulo7);
        this.txtTitulo8 = (TextView) findViewById(R.id.txtTitulo8);
        this.txtTitulo9 = (TextView) findViewById(R.id.txtTitulo9);
        this.txtTitulo10 = (TextView) findViewById(R.id.txtTitulo10);
        this.txtTitulo11 = (TextView) findViewById(R.id.txtTitulo11);
        this.txtTitulo12 = (TextView) findViewById(R.id.txtTitulo12);
        this.txtTitulo13 = (TextView) findViewById(R.id.txtTitulo13);
        this.txtTitulo14 = (TextView) findViewById(R.id.txtTitulo14);
        this.txtTitulo15 = (TextView) findViewById(R.id.txtTitulo15);
        this.txtTitulo16 = (TextView) findViewById(R.id.txtTitulo16);
        this.txtTitulo17 = (TextView) findViewById(R.id.txtTitulo17);
        this.txtTitulo18 = (TextView) findViewById(R.id.txtTitulo18);
        this.txtTitulo19 = (TextView) findViewById(R.id.txtTitulo19);
        this.txtTitulo20 = (TextView) findViewById(R.id.txtTitulo20);
        this.txtTitulo21 = (TextView) findViewById(R.id.txtTitulo21);
        this.txtTitulo22 = (TextView) findViewById(R.id.txtTitulo22);
        this.txtTitulo23 = (TextView) findViewById(R.id.txtTitulo23);
        this.txtTitulo24 = (TextView) findViewById(R.id.txtTitulo24);
        this.txtTitulo25 = (TextView) findViewById(R.id.txtTitulo25);
        this.txtTitulo26 = (TextView) findViewById(R.id.txtTitulo26);
        this.txtTitulo27 = (TextView) findViewById(R.id.txtTitulo27);
        this.txtTitulo28 = (TextView) findViewById(R.id.txtTitulo28);
        this.txtTitulo29 = (TextView) findViewById(R.id.txtTitulo29);
        this.txtTitulo30 = (TextView) findViewById(R.id.txtTitulo30);
        this.txtValor1 = (TextView) findViewById(R.id.txtValor1);
        this.txtValor2 = (TextView) findViewById(R.id.txtValor2);
        this.txtValor3 = (TextView) findViewById(R.id.txtValor3);
        this.txtValor4 = (TextView) findViewById(R.id.txtValor4);
        this.txtValor5 = (TextView) findViewById(R.id.txtValor5);
        this.txtValor6 = (TextView) findViewById(R.id.txtValor6);
        this.txtValor7 = (TextView) findViewById(R.id.txtValor7);
        this.txtValor8 = (TextView) findViewById(R.id.txtValor8);
        this.txtValor9 = (TextView) findViewById(R.id.txtValor9);
        this.txtValor10 = (TextView) findViewById(R.id.txtValor10);
        this.txtValor11 = (TextView) findViewById(R.id.txtValor11);
        this.txtValor12 = (TextView) findViewById(R.id.txtValor12);
        this.txtValor13 = (TextView) findViewById(R.id.txtValor13);
        this.txtValor14 = (TextView) findViewById(R.id.txtValor14);
        this.txtValor15 = (TextView) findViewById(R.id.txtValor15);
        this.txtValor16 = (TextView) findViewById(R.id.txtValor16);
        this.txtValor17 = (TextView) findViewById(R.id.txtValor17);
        this.txtValor18 = (TextView) findViewById(R.id.txtValor18);
        this.txtValor19 = (TextView) findViewById(R.id.txtValor19);
        this.txtValor20 = (TextView) findViewById(R.id.txtValor20);
        this.txtValor21 = (TextView) findViewById(R.id.txtValor21);
        this.txtValor22 = (TextView) findViewById(R.id.txtValor22);
        this.txtValor23 = (TextView) findViewById(R.id.txtValor23);
        this.txtValor24 = (TextView) findViewById(R.id.txtValor24);
        this.txtValor25 = (TextView) findViewById(R.id.txtValor25);
        this.txtValor26 = (TextView) findViewById(R.id.txtValor26);
        this.txtValor27 = (TextView) findViewById(R.id.txtValor27);
        this.txtValor28 = (TextView) findViewById(R.id.txtValor28);
        this.txtValor29 = (TextView) findViewById(R.id.txtValor29);
        this.txtValor30 = (TextView) findViewById(R.id.txtValor30);
        this.layoutdetalle1 = (LinearLayout) findViewById(R.id.layoutdetalle1);
        this.layoutdetalle2 = (LinearLayout) findViewById(R.id.layoutdetalle2);
        this.layoutdetalle3 = (LinearLayout) findViewById(R.id.layoutdetalle3);
        this.layoutdetalle4 = (LinearLayout) findViewById(R.id.layoutdetalle4);
        this.layoutdetalle5 = (LinearLayout) findViewById(R.id.layoutdetalle5);
        this.layoutdetalle6 = (LinearLayout) findViewById(R.id.layoutdetalle6);
        this.layoutdetalle7 = (LinearLayout) findViewById(R.id.layoutdetalle7);
        this.layoutdetalle8 = (LinearLayout) findViewById(R.id.layoutdetalle8);
        this.layoutdetalle9 = (LinearLayout) findViewById(R.id.layoutdetalle9);
        this.layoutdetalle10 = (LinearLayout) findViewById(R.id.layoutdetalle10);
        this.layoutdetalle11 = (LinearLayout) findViewById(R.id.layoutdetalle11);
        this.layoutdetalle12 = (LinearLayout) findViewById(R.id.layoutdetalle12);
        this.layoutdetalle13 = (LinearLayout) findViewById(R.id.layoutdetalle13);
        this.layoutdetalle14 = (LinearLayout) findViewById(R.id.layoutdetalle14);
        this.layoutdetalle15 = (LinearLayout) findViewById(R.id.layoutdetalle15);
        this.layoutdetalle16 = (LinearLayout) findViewById(R.id.layoutdetalle16);
        this.layoutdetalle17 = (LinearLayout) findViewById(R.id.layoutdetalle17);
        this.layoutdetalle18 = (LinearLayout) findViewById(R.id.layoutdetalle18);
        this.layoutdetalle19 = (LinearLayout) findViewById(R.id.layoutdetalle19);
        this.layoutdetalle20 = (LinearLayout) findViewById(R.id.layoutdetalle20);
        this.layoutdetalle21 = (LinearLayout) findViewById(R.id.layoutdetalle21);
        this.layoutdetalle22 = (LinearLayout) findViewById(R.id.layoutdetalle22);
        this.layoutdetalle23 = (LinearLayout) findViewById(R.id.layoutdetalle23);
        this.layoutdetalle24 = (LinearLayout) findViewById(R.id.layoutdetalle24);
        this.layoutdetalle25 = (LinearLayout) findViewById(R.id.layoutdetalle25);
        this.layoutdetalle26 = (LinearLayout) findViewById(R.id.layoutdetalle26);
        this.layoutdetalle27 = (LinearLayout) findViewById(R.id.layoutdetalle27);
        this.layoutdetalle28 = (LinearLayout) findViewById(R.id.layoutdetalle28);
        this.layoutdetalle29 = (LinearLayout) findViewById(R.id.layoutdetalle29);
        this.layoutdetalle30 = (LinearLayout) findViewById(R.id.layoutdetalle30);
        this.nroTicket = (TextView) findViewById(R.id.nroTicket);
        init();
        this.btngotoback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ActivityDetalleTicket.this.finish();
            }
        });
    }

    private void init() {
        this.idTicket = getIntent().getExtras().getInt("idticket");
        consultarNroTicket();
    }

    private void consultarNroTicket() {
        this.customDialog.show();
        HashMap hashMap = new HashMap();
        hashMap.put("id_vendedor", ((TGestionaSession) getApplication()).getIdVendedorChat());
        hashMap.put("token", ((TGestionaSession) getApplication()).getTokenMovil());
        hashMap.put("id_session", ((TGestionaSession) getApplication()).getIdSession());
        hashMap.put("idticket", String.valueOf(this.idTicket));
        this.nroTicket.setText("Ticket: " + String.valueOf(this.idTicket));
        GlobalFunctions.callVolleyStringRequest(this, hashMap, varPublicas.URL_DESARROLLO + "showTicketModal", this.volley, this.mRequest, new ServerCallback() {
            public void onSuccess(String str) {
                try {
                    JSONArray jSONArray = new JSONArray(str);
                    JSONObject jSONObject = jSONArray.getJSONObject(0).getJSONObject("listaDatos");
                    ArrayList arrayList = new ArrayList();
                    JSONArray jSONArray2 = jSONObject.getJSONArray("detalle");
                    for (int i = 0; i < jSONArray2.length(); i++) {
                        switch (i) {
                            case 0:
                                ActivityDetalleTicket.this.layoutdetalle1.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo1.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor1.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 1:
                                ActivityDetalleTicket.this.layoutdetalle2.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo2.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor2.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 2:
                                ActivityDetalleTicket.this.layoutdetalle3.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo3.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor3.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 3:
                                ActivityDetalleTicket.this.layoutdetalle4.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo4.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor4.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 4:
                                ActivityDetalleTicket.this.layoutdetalle5.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo5.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor5.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 5:
                                ActivityDetalleTicket.this.layoutdetalle6.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo6.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor6.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 6:
                                ActivityDetalleTicket.this.layoutdetalle7.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo7.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor7.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 7:
                                ActivityDetalleTicket.this.layoutdetalle8.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo8.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor8.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 8:
                                ActivityDetalleTicket.this.layoutdetalle9.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo9.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor9.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 9:
                                ActivityDetalleTicket.this.layoutdetalle10.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo10.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor10.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 10:
                                ActivityDetalleTicket.this.layoutdetalle11.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo11.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor11.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 11:
                                ActivityDetalleTicket.this.layoutdetalle12.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo12.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor12.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 12:
                                ActivityDetalleTicket.this.layoutdetalle13.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo13.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor13.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 13:
                                ActivityDetalleTicket.this.layoutdetalle14.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo14.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor14.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 14:
                                ActivityDetalleTicket.this.layoutdetalle15.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo15.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor15.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 15:
                                ActivityDetalleTicket.this.layoutdetalle16.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo16.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor16.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 16:
                                ActivityDetalleTicket.this.layoutdetalle17.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo17.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor17.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 17:
                                ActivityDetalleTicket.this.layoutdetalle18.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo18.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor18.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 18:
                                ActivityDetalleTicket.this.layoutdetalle19.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo19.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor19.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 19:
                                ActivityDetalleTicket.this.layoutdetalle20.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo20.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor20.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 20:
                                ActivityDetalleTicket.this.layoutdetalle21.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo21.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor21.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 21:
                                ActivityDetalleTicket.this.layoutdetalle22.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo22.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor22.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 22:
                                ActivityDetalleTicket.this.layoutdetalle23.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo23.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor23.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 23:
                                ActivityDetalleTicket.this.layoutdetalle24.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo24.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor24.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 24:
                                ActivityDetalleTicket.this.layoutdetalle25.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo25.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor25.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 25:
                                ActivityDetalleTicket.this.layoutdetalle26.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo26.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor26.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 26:
                                ActivityDetalleTicket.this.layoutdetalle27.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo27.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor27.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 27:
                                ActivityDetalleTicket.this.layoutdetalle28.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo28.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor28.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 28:
                                ActivityDetalleTicket.this.layoutdetalle29.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo29.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor29.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                            case 29:
                                ActivityDetalleTicket.this.layoutdetalle30.setVisibility(0);
                                ActivityDetalleTicket.this.txtTitulo30.setText(jSONArray2.getJSONObject(i).getString("titulo"));
                                ActivityDetalleTicket.this.txtValor30.setText(jSONArray2.getJSONObject(i).getString("valor"));
                                break;
                        }
                    }
                    if (jSONObject.getInt("ischat") == 1) {
                        JSONArray jSONArray3 = jSONObject.getJSONArray("chat");
                        for (int i2 = 0; i2 < jSONArray3.length(); i2++) {
                            DTO_ticket_chat dTO_ticket_chat = new DTO_ticket_chat();
                            dTO_ticket_chat.setFecha(jSONArray3.getJSONObject(i2).getString("fecha"));
                            dTO_ticket_chat.setInduser(jSONArray3.getJSONObject(i2).getInt("induser"));
                            dTO_ticket_chat.setMensaje(jSONArray3.getJSONObject(i2).getString("mensaje"));
                            dTO_ticket_chat.setUsuario(jSONArray3.getJSONObject(i2).getString("usuario"));
                            arrayList.add(dTO_ticket_chat);
                        }
                    }
                    RvChatTicket unused = ActivityDetalleTicket.this.mAdapter = new RvChatTicket(arrayList);
                    ActivityDetalleTicket.this.mRecyclerView.setAdapter(ActivityDetalleTicket.this.mAdapter);
                    ActivityDetalleTicket.this.customDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    ActivityDetalleTicket.this.customDialog.dismiss();
                }
            }
        });
    }
}
