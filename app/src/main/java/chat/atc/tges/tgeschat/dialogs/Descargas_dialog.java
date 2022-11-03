package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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

import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.TGestionaSession;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;

/**
 * Created by rodriguez on 20/06/2018.
 */

public class Descargas_dialog extends DialogVolleyFragment {

    TextView lblAnularPenalidad, lblCancelacionOrdenes, lblOrdenesSuspension, lblAnulacionPensalidadPlanSeguro,lblCartillaV2, lblGuiaRapidaUso, lblBaseErroresDelivery, lblTitle3;

    public static VolleyRP volley;
    public static RequestQueue mRequest;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createTopConsultasTiendasDialog();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de activación
     *
     * @return Diálogo
     */

    public AlertDialog createTopConsultasTiendasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_top_consultas_tiendas_dialog, null);

        volley = VolleyRP.getInstance(getActivity());
        mRequest = volley.getRequestQueue();

        lblAnularPenalidad = (TextView) v.findViewById(R.id.lblAnularPenalidad);
        lblCancelacionOrdenes = (TextView) v.findViewById(R.id.lblCancelacionOrdenes);
        lblOrdenesSuspension = (TextView) v.findViewById(R.id.lblOrdenesSuspension);
        lblAnulacionPensalidadPlanSeguro = (TextView) v.findViewById(R.id.lblAnulacionPenalidadSeguroPlanInteligente);
        lblCartillaV2  = (TextView) v.findViewById(R.id.lblCartillaV2);
        lblGuiaRapidaUso  = (TextView) v.findViewById(R.id.lblGuiaRapidaUso);
        lblBaseErroresDelivery = (TextView) v.findViewById(R.id.lblBaseErroresDelivery);
        lblTitle3 = (TextView) v.findViewById(R.id.lblTitle3);

        makeRequestObtenerEnlaceDinamico();

        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);


        builder.setPositiveButton("VOLVER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        })
                /*.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })*/;

        //Anular Penalidad
        String text = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/anular-penalidad.docx'><font color='#2C6B9E'>"+ ">> Anular penalidad" +"</font></a>";
        lblAnularPenalidad.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnularPenalidad.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnularPenalidad.setText(Html.fromHtml(text));

        //Anular Penalidad
        String text2 = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/cancelación-ordenes-registradas-canal.docx'>"+ ">> Cancelación de ordenes registradas en el mismo canal" +"</a>";
        lblCancelacionOrdenes.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblCancelacionOrdenes.setMovementMethod(LinkMovementMethod.getInstance());
        lblCancelacionOrdenes.setText(Html.fromHtml(text2));

        //Anular Penalidad
        String text3 = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/anular-penalidad.docx'>"+ ">> Órdenes de suspensión por perdida o robo" +"</a>";
        lblOrdenesSuspension.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblOrdenesSuspension.setMovementMethod(LinkMovementMethod.getInstance());
        lblOrdenesSuspension.setText(Html.fromHtml(text3));

        //Anular Penalidad
        String text4 = "<a href='https://movistartayuda.com/web_chat/descargas/consulta-tiendas/anular-penalidad.docx'>"+ ">> Anulación de penalidad-seguro y plan inteligente" +"</a>";
        lblAnulacionPensalidadPlanSeguro.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnulacionPensalidadPlanSeguro.setMovementMethod(LinkMovementMethod.getInstance());
        lblAnulacionPensalidadPlanSeguro.setText(Html.fromHtml(text4));

        //Cartilla V2
        String text5 = "<a href='https://movistartayuda.com/web_chat/descargas/cartilla-uso/cartilla-v2.pdf'>"+ ">> Cartilla V.2" +"</a>";
        lblCartillaV2.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblCartillaV2.setMovementMethod(LinkMovementMethod.getInstance());
        lblCartillaV2.setText(Html.fromHtml(text5));

        //Guia Rapida Uso
        String text6 = "<a href='https://movistartayuda.com/web_chat/descargas/cartilla-uso/video-instructivo.mp4'>"+ ">> Guía Rápida de Uso (Video)" +"</a>";
        lblGuiaRapidaUso.setClickable(true);
        //holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
        lblGuiaRapidaUso.setMovementMethod(LinkMovementMethod.getInstance());
        lblGuiaRapidaUso.setText(Html.fromHtml(text6));

        return builder.create();
    }

    private void makeRequestObtenerEnlaceDinamico() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO()+"enviarPushBaseErrores";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataUrl(response);

                if (((TGestionaSession)getActivity().getApplication()).getIdCanal().equalsIgnoreCase("2841")){ //canal postventa
                    lblBaseErroresDelivery.setVisibility(View.VISIBLE);
                    lblTitle3.setVisibility(View.VISIBLE);
                    //Anular Penalidad
                    //WS_Url="181.65.211.138:8089/chat/lhc_web/var/storage/archivo_web/archivo_1_4.xlsx";
                    String text7 ="<a href='" + WS_Url + "'>" + " >> " + WS_Archivo_desc +"</a>";
                    //String text7 ="<a href='https://movistartayuda.com/web_chat/descargas/cartilla-uso/cartilla-v2.pdf'>" + " >> " + WS_Archivo_desc +"</a>";
                    lblBaseErroresDelivery.setClickable(true);
                    lblBaseErroresDelivery.setMovementMethod(LinkMovementMethod.getInstance());
                    lblBaseErroresDelivery.setText(Html.fromHtml(text7));
                }
                else
                {
                    lblBaseErroresDelivery.setVisibility(View.GONE);
                    lblTitle3.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idvendedor", ((TGestionaSession)getActivity().getApplication()).getIdVendedorChat());
                params.put("token", ((TGestionaSession)getActivity().getApplication()).getTokenMovil());
                params.put("id_session", ((TGestionaSession)getActivity().getApplication()).getIdSession() );
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //queue.add(request);
        VolleyRP.addToQueue(request,mRequest,getContext(),volley);
    }

    String WS_Url_Msg ="", WS_Archivo_desc="", WS_Url="";
    boolean WS_Url_Estado =false;

    public void getDataUrl(String cadenaJSON){
        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_Url_Msg = jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_Url_Estado = Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());
            WS_Archivo_desc= jsonArrayData.getJSONObject(0).getString("detalle").toString();
            WS_Url= jsonArrayData.getJSONObject(0).getString("url").toString();
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }

}
