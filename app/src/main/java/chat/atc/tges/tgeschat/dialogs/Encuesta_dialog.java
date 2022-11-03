package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.activity.HttpsTrustManager;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

/**
 * Created by rodriguez on 30/04/2018.
 */

public class Encuesta_dialog extends DialogVolleyFragment implements OnClickListener {
    ImageButton btnMuyMalo, btnMuyBueno, btnBueno, btnMalo, btnRegular;
    Button  btnSI, btnNO;
    public static VolleyRP volley;
    public static RequestQueue mRequest;

    LinearLayout llEncuesta1, llEncuesta2;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createEncuestaDialog();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de activación
     *
     * @return Diálogo
     */

    public AlertDialog createEncuestaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_encuesta_dialog, null);

        volley = VolleyRP.getInstance(getActivity());
        mRequest = volley.getRequestQueue();

        btnMuyBueno = (ImageButton) v.findViewById(R.id.btnMuyBueno);
        btnMuyBueno.setOnClickListener(this);
        btnBueno = (ImageButton) v.findViewById(R.id.btnBueno);
        btnBueno.setOnClickListener(this);
        btnRegular = (ImageButton) v.findViewById(R.id.btnRegular);
        btnRegular.setOnClickListener(this);
        btnMalo = (ImageButton) v.findViewById(R.id.btnMalo);
        btnMalo.setOnClickListener(this);
        btnMuyMalo = (ImageButton) v.findViewById(R.id.btnMuyMalo);
        btnMuyMalo.setOnClickListener(this);
        btnSI = (Button) v.findViewById(R.id.btnContinuar);
        btnSI.setOnClickListener(this);
        btnNO = (Button) v.findViewById(R.id.btnCancelar);
        btnNO.setOnClickListener(this);
        btnMuyMalo = (ImageButton) v.findViewById(R.id.btnMuyMalo);
        btnMuyMalo.setOnClickListener(this);
        llEncuesta1  = (LinearLayout) v.findViewById(R.id.llEncuesta1);
        llEncuesta2  = (LinearLayout) v.findViewById(R.id.llEncuesta2);
        //chat_id=0;
        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);

        if (Mensajeria.idChatEncuesta <=0){
            Toast.makeText(getActivity(), "No está asociado un id de chat para realizar la encuesta. idChat=0", Toast.LENGTH_LONG).show();
            dismiss();
        }

        /*
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }
        ); */

        //listarIncidencias();

        /*swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        listarIncidencias();
                    }
                }
        );*/

        return builder.create();
    }

    private void makeRequestEncuesta() {
        final ProgressDialog progress= new ProgressDialog(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = varPublicas.URL_DESARROLLO+"recibirDataEncuesta";
        HttpsTrustManager.allowAllSSL();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataEncuesta(response);
                //Toast centrado
                Toast toast = Toast.makeText(getActivity(), WS_Encuesta_Msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                //valor de chat se iguala a cero para que no se vuelva a mostrar la ventana en Activity Mensajeria.java
                //Mensajeria.idChatEncuesta=0;

                //Abre una nueva ventana de chat para generar otro ticket
                Intent intent = new Intent(getActivity(), Mensajeria.class);
                intent.putExtra("key_EstadoEncuestaConsulta",0);
                //intent.putExtra("key_EstadoEncuesta",String.valueOf(messages.get(position).getEstadoEncuesta()));
                //intent.putExtra("key_NroTickets",String.valueOf(messages.get(position).getNroTickets()));
                startActivity(intent);

                progress.dismiss();
            }
        }   , new Response.ErrorListener() {
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
                params.put("evaluacion", evaluacion);
                params.put("evaluacion2", evaluacion2);
                params.put("idchat", ""+ Mensajeria.idChatEncuesta);
                params.put("token", ((TGestionaSession)getActivity().getApplication()).getTokenMovil());
                params.put("idvendedor",((TGestionaSession)getActivity().getApplication()).getIdVendedorChat());
                params.put("id_session",((TGestionaSession)getActivity().getApplication()).getIdSession());

                return params;
            }};
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //queue.add(request);
        VolleyRP.addToQueue(request,mRequest,getActivity(),volley);
        progress.setMessage("Calificando...");
        progress.show();
    }

    String WS_Encuesta_Msg ="";
    boolean WS_Encuesta_Estado =false;

    public void getDataEncuesta(String cadenaJSON){
        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_Encuesta_Msg = jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_Encuesta_Estado = Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

    String evaluacion, evaluacion2="";
    @Override
    public void onClick(View v) {
        if (v==btnMuyMalo){
            evaluacion ="5";
        } else if (v== btnMalo){
            evaluacion ="4";
        } else if (v== btnRegular){
            evaluacion ="3";
        } else if (v== btnBueno){
            evaluacion ="2";
        } else if (v== btnMuyBueno){
            evaluacion ="1";
        } else if (v== btnSI){
            evaluacion2 ="1";
            makeRequestEncuesta();
        } else if (v== btnNO){
            evaluacion2 ="2";
            makeRequestEncuesta();
        }
        Toast.makeText(getActivity(), "Gracias por Participar!.", Toast.LENGTH_LONG);

        mostrarEncuesta2();
    }

    public void mostrarEncuesta2(){
        llEncuesta1.setVisibility(View.GONE);
        llEncuesta2.setVisibility(View.VISIBLE);
    }

}
