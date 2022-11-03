package chat.atc.tges.tgeschat.bandeja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.R;
import android.view.View.OnClickListener;
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

import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;


public class BandejaActivityTest extends BaseVolleyActivity implements OnClickListener{

    Button btnHistorial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.layout_loading);

//        btnHistorial = (Button) findViewById(R.id.bTHistorial);
//        btnHistorial.setOnClickListener(this);

        makeRequestSolicitaCodigoValidacion(); // invoca a ws que solicita codigo de activacion
    }

    @Override
    public void onClick(View v) {
            if (v==btnHistorial){
                varPublicas.estadoHistorialTicket=1;
                Intent intent = new Intent(BandejaActivityTest.this, Mensajeria.class);
                startActivity(intent);
            }
    }

    private void makeRequestSolicitaCodigoValidacion(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        /*queue.add()
        queue.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

            String url = varPublicas.URL_DESARROLLO+"GenerarPushOlvidoClave";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int a;
                    a=1;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Respuesta onError: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", varPublicas.tokenMovil);
                    params.put("getemail", "jhrodriguezgk@gmail.com");
                    params.put("getusuario", "75514889");
                    return params;
                }
            };
            //request.setTag(TextUtils.isEmpty("hey") ? "HEY" : "hey");
            request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)); //permite que ws sólo se llame una vez y no dos como venía haciendo (el correo se envíaba dos veces)
            queue.add(request);
    }

    String WS_ConfirmaValidacionCorreoUsuario_Msg="";
    boolean WS_ConfirmaValidacionCorreoUsuario_Estado=false;

    public void getDataValidacionCorreoUsuario (String cadenaJSON){

        try{
            JSONArray jsonArrayData=new JSONArray(cadenaJSON);
            WS_ConfirmaValidacionCorreoUsuario_Msg= jsonArrayData.getJSONObject(0).getString("Msg").toString();
            WS_ConfirmaValidacionCorreoUsuario_Estado= Boolean.parseBoolean(jsonArrayData.getJSONObject(0).getString("Estado").toString());

            /*JSONArray jsonArrayListaDatos = jsonArrayData.getJSONObject(0).getJSONArray("ListaDatos");

            for (int i = 0; i < jsonArrayListaDatos.length(); i++) {
                codActivacion= jsonArrayListaDatos.getJSONObject(0).getString("CODIGO").toString();
            }
            txtCodigoActivacion.setText(codActivacion);*/
        }
        catch(JSONException je) {
            je.printStackTrace();
        }
    }
}
