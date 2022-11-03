package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import chat.atc.tges.tgeschat.Mensajes.DividerItemDecoration;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.adapter.RvIncidenciasAdapter;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;
import chat.atc.tges.tgeschat.model.Incidencia;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

/**
 * Created by rodriguez on 30/04/2018.
 */

public class IncidenciaMasiva_dialog extends DialogVolleyFragment { //implements SwipeRefreshLayout.OnRefreshListener

    List<Incidencia> incidencias = new ArrayList<>();
    RvIncidenciasAdapter IAdapter;
    List<String> listaTextoIncidencia = new ArrayList<>();
    RecyclerView recyclerView;
    private VolleyRP volley;
    private RequestQueue mRequest;
    AlertDialog.Builder builder;
    TextView linkweb;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createIncidenciaMasivaDialog();

    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de activación
     *
     * @return Diálogo
     */

    public AlertDialog createIncidenciaMasivaDialog() {


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_incidencia_masiva, null);

        volley = VolleyRP.getInstance(getActivity());
        mRequest = volley.getRequestQueue();
        IAdapter = new RvIncidenciasAdapter(listaTextoIncidencia);

        /*recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        recyclerView.setAdapter(IAdapter);*/ //PIERO

        linkweb = v.findViewById(R.id.linkweb);
        linkweb.setMovementMethod(LinkMovementMethod.getInstance());

        builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        builder.setPositiveButton("OMITIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);



        listarIncidencias();

        return builder.create();
    }

    private void listarIncidencias(){
        final ProgressDialog progress= new ProgressDialog(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO() + "listarIncidencias", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // clear the rv
                incidencias.clear();
                getDataIncidencias(response);
                progress.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(),"Error Dialogo Incidencia Masiva: " + error.toString() , Toast.LENGTH_SHORT).show();
                Log.d("Error IMasiva: ",error.toString());
                progress.dismiss();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", ((TGestionaSession)getActivity().getApplication()).getTokenMovil());
                params.put("idvendedor",((TGestionaSession)getActivity().getApplication()).getIdVendedorChat());
                params.put("idCanal", varPublicas.idCanal);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRP.addToQueue(request,mRequest,getActivity(),volley);
    }

    String  WS_MsgIncidencia_Msg="";
    Boolean WS_MsgIncidencia_Estado=false;

    public void getDataIncidencias (String cadenaJSON){
        try
        {
            JSONArray jsonArrayDataIncidencias=new JSONArray(cadenaJSON);
            WS_MsgIncidencia_Msg= jsonArrayDataIncidencias.getJSONObject(0).getString("Msg").toString();
            WS_MsgIncidencia_Estado= jsonArrayDataIncidencias.getJSONObject(0).getBoolean("Estado");
            /*Incidencia oIncidencia;
            if (!WS_MsgIncidencia_Estado) { // No hay data de incidencias
                oIncidencia = new Incidencia();
                oIncidencia.setCuerpo1("");
                oIncidencia.setMotivo("");
                oIncidencia.setCuerpo2("");
                oIncidencia.setCuerpo3(WS_MsgIncidencia_Msg);
                oIncidencia.setTicketDoit("");
                incidencias.add(oIncidencia);
                IAdapter.notifyDataSetChanged();
            }*/

            //boolean existeArray=jsonArrayDataIncidencias.getJSONObject(0).isNull("ListaDatos");
            if (!WS_MsgIncidencia_Estado){
                listaTextoIncidencia.add(0,WS_MsgIncidencia_Msg);
                IAdapter.notifyDataSetChanged();
            }
            else
            {
            //if(!existeArray){ //si existe datos en ListaDatos
                JSONArray jsonArrayListaDatos = jsonArrayDataIncidencias.getJSONObject(0).getJSONArray("ListaDatos"); //Hay incidencias
                listaTextoIncidencia.clear();
                //JSONArray jsonArrayListaTextoIncidencia = jsonArrayListaDatos.getJSONObject(0).getString("0"); //Hay incidencias
                for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                    listaTextoIncidencia.add(i,jsonArrayListaDatos.getJSONObject(i).getString("texto"));
                }
                IAdapter.notifyDataSetChanged();
            }
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }
}
