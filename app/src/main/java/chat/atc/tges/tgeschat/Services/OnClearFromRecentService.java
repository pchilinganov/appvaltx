package chat.atc.tges.tgeschat.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import chat.atc.tges.tgeschat.Mensajes.Mensajeria;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

import static chat.atc.tges.tgeschat.util.GlobalFunctions.cancelAllNotification;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.usuario;

/**
 * Created by rodriguez on 22/06/2018.
 */

public class OnClearFromRecentService extends Service {

    private VolleyRP volley;
    private RequestQueue mRequest;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //GlobalFunctions.logout();

        /*if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            logout();
            cancelAllNotification(getApplicationContext());
        }*/
        //stopSelf();

        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Toast.makeText(OnClearFromRecentService.this,"Cerrando" , Toast.LENGTH_SHORT).show();
        Log.e("ClearFromRecentService", "END");
        if (varPublicas.idSession!= null || !varPublicas.idSession.equalsIgnoreCase("")) {
            cancelAllNotification(getApplicationContext());
            volley = VolleyRP.getInstance(this);
            mRequest = volley.getRequestQueue();

            //Code here
            //GlobalFunctions.logout();
            logout();
        }
        //stopSelf();
    }

    private void logout(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String estado="";
                estado="OK";
                stopService(Mensajeria.service);
                cancelAllNotification(getApplicationContext());
                //Toast.makeText(BandejaActivity.this,response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OnClearFromRecentService.this,"Respuesta servicio: " + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
        )
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idusuario", usuario);
                return params;
            }
        };
        VolleyRP.addToQueue(request,mRequest,this,volley);
    }
}
