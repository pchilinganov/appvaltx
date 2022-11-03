package chat.atc.tges.tgeschat.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
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
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.databaseOnline.DialogVolleyFragment;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

/**
 * Created by rodriguez on 20/06/2018.
 */

public class ComentarioTicket_dialog extends DialogVolleyFragment {

    private VolleyRP volley;
    private RequestQueue mRequest;
    public static String nroTicket;
    TextView lblTitle;
    EditText txtComentario;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createComentarioTicketDialog();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de activación
     *
     * @return Diálogo
     */

    public AlertDialog createComentarioTicketDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_comentario_ticket_dialog, null);

        volley = VolleyRP.getInstance(getActivity());
        mRequest = volley.getRequestQueue();
        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        //lblRpta = (TextView) v.findViewById(R.id.lblRpta);
        txtComentario = (EditText) v.findViewById(R.id.txtComentario);

        //Muestra la vista que contiene el diseño del dialog
        builder.setView(v);

        builder.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //validar que el usuario es del canal 104 (PostVenta)

                if (((TGestionaSession)getActivity().getApplication()).getIdCanal().equalsIgnoreCase("2841") || ((TGestionaSession)getActivity().getApplication()).getIdCanal().equalsIgnoreCase("4571")){
                    makeRequestEnviarComentarioTicket();

                }else{
                    Toast.makeText(getActivity(),"Usuario no autorizado para comentar tickets." , Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        }
                );

        //Establecer título de diálogo
        lblTitle.setText("Ticket # "+nroTicket);

        return builder.create();
    }

    public void showToast(){
        Toast.makeText(getActivity(), WS_Comment_Msg, Toast.LENGTH_SHORT).show();
    }

    private void makeRequestEnviarComentarioTicket() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ((TGestionaSession)getActivity().getApplication()).getURL_DESARROLLO()+"guardarComentarioTicket";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                getDataComentarioTicket(response);

                //showToast();

                //comentarioTicket=true;
                //Toast centrado
                //Toast toast = Toast.makeText(getActivity(), WS_Comment_Msg, Toast.LENGTH_LONG);
                /*toast.setGravity(Gravity.CENTER,0,0);
                toast.show();*/

            }
        }   , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(),"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                Log.d("Error makeRequestEnviarComentarioTicket","Respuesta onError: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idvendedor", varPublicas.idVendedorChat);
                params.put("idticket", nroTicket);
                params.put("token",varPublicas.tokenMovil);
                params.put("comentario",txtComentario.getText().toString());
                params.put("id_session", varPublicas.idSession );
                return params;
            }
        };
        queue.add(request);
    }

    String  WS_Comment_Msg ="";
    boolean WS_Comment_Estado =false;

    public void getDataComentarioTicket(String cadenaJSON){
        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_Comment_Msg = jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_Comment_Estado = Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }
}
