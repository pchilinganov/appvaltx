package chat.atc.tges.tgeschat.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import chat.atc.tges.tgeschat.LoginTelefonica;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.activity.HttpsTrustManager;
import chat.atc.tges.tgeschat.databaseOnline.VolleyRP;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;
import chat.atc.tges.tgeschat.interfaces.ServerCallback;
import chat.atc.tges.tgeschat.model.DTO_Departamento;
import chat.atc.tges.tgeschat.model.DTO_Generico;

import static chat.atc.tges.tgeschat.varPublicas.varPublicas.URL_DESARROLLO;
import static chat.atc.tges.tgeschat.varPublicas.varPublicas.usuario;

/**
 * Created by rodriguez on 22/06/2018.
 */

public class GlobalFunctions extends BaseVolleyActivity
{

    private  VolleyRP volley;
    private  RequestQueue mRequest;
    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    static boolean  sesionFinalizada=false;
    //valida que sesión está activa, caso contrario se cierra la aplicación
    public static boolean validaSesion(boolean statusSession, Context context)
    {
        if (!statusSession){
            cancelAllNotification(context);
            return sesionFinalizada;
        }
        return sesionFinalizada;
    }

    public static void cancelAllNotification(Context ctx) {
        String ns = ctx.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }

    public static void logout(final Context ctx, String usuario,VolleyRP pVolley, RequestQueue pRequestQueue) {
        String WSMethod="";
        WSMethod = "logout";
        String URL = URL_DESARROLLO + WSMethod;

        Map<String, String> params = new HashMap<String, String>();

        params.put("idusuario", usuario);

        final String json = callVolleyStringRequest(ctx, params, URL, pVolley, pRequestQueue, new ServerCallback() {
                    @Override
                    public void onSuccess(String result) {
                         cancelAllNotification(ctx);

                    }
                }
        );


    }



    //verifica conectividad a internet
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected() ;
    }

    public static boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    String pattern = "EEEEE dd MMMMM yyyy HH:mm:ss.SSSZ";
    SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat(pattern, new Locale("da", "DK"));

    public static String convertStringToTimestamp(String str_date) {
        try {
            String pattern = "EEEEE dd MMMMM yyyy HH:mm:ss.SSSZ";
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(pattern, new Locale("es", "PE"));

            String date = simpleDateFormat.format(new Date());

            return date;
        } catch (Exception e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

    public static void newIntent(Context actualClass, Class targetClass) {
        Intent intent=null;
        intent = new Intent(actualClass, targetClass);
        actualClass.startActivity(intent);
    }

    public static void rellenarActv(Context context, final AutoCompleteTextView actv, List<String> lista){
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (context, R.layout.ly_style_dropdown_actv, lista);
        actv.setThreshold(0);//will start working txt_cardview_above_two first character
        actv.setAdapter(adapter); //setting the adapter data into the AutoCompleteTextView
        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    actv.showDropDown();
                }

                return true; // return is important...
            }
        });
    }

    public static void rellenarSpinner(Context context, final Spinner spinner, List<String> lista){
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (context, R.layout.ly_style_dropdown_spinner, lista);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
    }

    public static void rellenarSpinnerIndex(Context context, final Spinner spinner, List<String> lista, int index){
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (context, R.layout.ly_style_dropdown_spinner, lista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(index);
    }

    public static String escapeNull(String cadena){
        if (cadena==null){
            return "";
        }else if (cadena.equalsIgnoreCase("null")){
            return "";
        }
        return cadena;
    }

    public static void rellenarActvVentasFija(Context context, final AutoCompleteTextView actv, List<String> lista, int itemSeleccionado, String descripcionSeleccionada){
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (context, R.layout.ly_style_dropdown_actv, lista);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actv.setAdapter(adapter); //setting the adapter data into the AutoCompleteTextView
        if (itemSeleccionado>=0) {
//            actv.setSelection(itemSeleccionado,itemSeleccionado);
//            actv.
                actv.setText(descripcionSeleccionada);
        }

        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    actv.showDropDown();
                    actv.requestFocus();
                }
                return true; // return is important...
            }
        });
    }

    public static List<DTO_Generico> fillActvFromJSONArray(Context context,JSONArray jsonArray, AutoCompleteTextView actv, String paramCod, String paramDescripcion){
        List<String> listaString=new ArrayList<>();
        List<DTO_Generico> lista = new ArrayList<>();
        DTO_Generico oGenerico;
        for (int i = 0; i < jsonArray.length(); i++) { //recorriendo arrays
            oGenerico = new DTO_Generico();
            try
            {
                oGenerico.setCodigo(jsonArray.getJSONObject(i).getString(paramCod));
                oGenerico.setDescripcion(jsonArray.getJSONObject(i).getString(paramDescripcion));
                lista.add(oGenerico); //rellena lista que almacenará datos en array de clase
                listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        GlobalFunctions.rellenarActv(context,actv,listaString);
        return lista;
    }

    public static List<DTO_Generico> fillActvFromJSONArray2(Context context, JSONArray jsonArray, AutoCompleteTextView actv, String paramCod, String paramDescripcion, String selected, String visible){
        List<String> listaString=new ArrayList<>();
        List<DTO_Generico> lista = new ArrayList<>();
        DTO_Generico oGenerico;
        int itemSeleccionado=-1;
        String descripcionSeleccionada="";

        for (int i = 0; i < jsonArray.length(); i++) { //recorriendo arrays
            oGenerico = new DTO_Generico();
            try
            {
                oGenerico.setCodigo(jsonArray.getJSONObject(i).getString(paramCod));
                oGenerico.setDescripcion(jsonArray.getJSONObject(i).getString(paramDescripcion));
                oGenerico.setSeleccionado(jsonArray.getJSONObject(i).getBoolean(selected));
                oGenerico.setVisible(jsonArray.getJSONObject(i).getBoolean(visible));
//                    lista.add(oGenerico); //rellena lista que almacenará datos en array de clase
//                    listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo

                if (jsonArray.getJSONObject(i).getBoolean(selected)==true)
                {
                    itemSeleccionado=i;
                    descripcionSeleccionada=oGenerico.getDescripcion();
                }
//                if (jsonArray.getJSONObject(i).getBoolean(visible)==true)
//                {
                    lista.add(oGenerico); //rellena lista que almacenará datos en array de clase
                    listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo
//                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        GlobalFunctions.rellenarActvVentasFija(context,actv,listaString, itemSeleccionado, descripcionSeleccionada);

        return lista;
    }

    public static List<DTO_Generico> fillActvWithTextFromJSONArray(Context context,JSONArray jsonArray, AutoCompleteTextView actv, String paramCod, String paramDescripcion){
        List<String> listaString=new ArrayList<>();
        List<DTO_Generico> lista = new ArrayList<>();
        DTO_Generico oGenerico;
        for (int i = 0; i < jsonArray.length(); i++) { //recorriendo arrays
            oGenerico = new DTO_Generico();
            try
            {
                oGenerico.setCodigo(jsonArray.getJSONObject(i).getString(paramCod));
                oGenerico.setDescripcion(jsonArray.getJSONObject(i).getString(paramDescripcion));
                lista.add(oGenerico); //rellena lista que almacenará datos en array de clase
                listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        GlobalFunctions.rellenarActv(context,actv,listaString);
        //GlobalFunctions.rellenarActvWithTextSearch(context,actv,listaString);
        return lista;
    }

    public static List<DTO_Generico> fillSpinnerFromJSONArray(Context context, JSONArray jsonArray, Spinner spinner, String paramCod, String paramDescripcion, String selected, String visible){
        List<String> listaString=new ArrayList<>();
        List<DTO_Generico> lista = new ArrayList<>();
        DTO_Generico oGenerico;
        int itemSeleccionado=-1;
        String descripcionSeleccionada="";

//        DTO_Generico oInicio= new DTO_Generico();
//        oInicio.setCodigo("");
//        oInicio.setDescripcion("Seleccione");
//        lista.add(oInicio);
        String a;

        if (paramCod.equalsIgnoreCase("cb_modalidad_pago"))
            a="sdcsdc";


        for (int i = 0; i < jsonArray.length(); i++) { //recorriendo arrays
            oGenerico = new DTO_Generico();
            try
            {
                oGenerico.setCodigo(jsonArray.getJSONObject(i).getString(paramCod));
                oGenerico.setDescripcion(jsonArray.getJSONObject(i).getString(paramDescripcion));
                oGenerico.setSeleccionado(jsonArray.getJSONObject(i).getBoolean(selected));
                //oGenerico.setVisible(jsonArray.getJSONObject(i).getBoolean(visible));
//                    lista.add(oGenerico); //rellena lista que almacenará datos en array de clase
//                    listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo

                if (jsonArray.getJSONObject(i).getBoolean(selected)==true)
                {
                    itemSeleccionado=i;

                }
//                if (jsonArray.getJSONObject(i).getBoolean(visible)==true)
//                {

                    lista.add(oGenerico); //rellena lista que almacenará datos en array de clase
                    listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo
                //}
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        //GlobalFunctions.rellenarSpinner(context,spinner,listaString, itemSeleccionado, descripcionSeleccionada);
        GlobalFunctions.rellenarSpinner(context,spinner,listaString);
        spinner.setSelection(itemSeleccionado);

        return lista;
    }

    public static void limpiarShared(Context context){
        SharedPreferences pref = context.getSharedPreferences("mta_loged", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();
    }

    public static int getIndexSpinnerGenericoSelected(Spinner spinner, List<DTO_Generico> lista, String paramDescripcion){
        int index=-1;
        String codigo="";
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getDescripcion().equalsIgnoreCase(paramDescripcion))
            codigo = lista.get(i).getCodigo();
            index = i;
        }
        return index;
    }

    public static void killAppBypackage(Context context, Activity activity, String packageTokill){

        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = context.getPackageName();

//        if(mActivityManager != null) {
//            List<ActivityManager.AppTask> tasks = mActivityManager.getAppTasks();
//            if (tasks != null && tasks.size() > 0) {
//                tasks.get(0).setExcludeFromRecents(true);
//            }
//        }

        for (ApplicationInfo packageInfo : packages) {

            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1) {
                continue;
            }
            if(packageInfo.packageName.equals(myPackage)) {
                //mActivityManager.killBackgroundProcesses(packageInfo.packageName);
                continue;
            }
            if(packageInfo.packageName.equals(packageTokill)) {
                mActivityManager.killBackgroundProcesses(packageInfo.packageName);

            }
        }
    }

    public static List<DTO_Departamento> fillSpinnerFromJSONArrayDep(Context context, JSONArray jsonArray, Spinner spinner, String paramCod, String paramDescripcion, String selected, String visible){
        List<String> listaString=new ArrayList<>();
        List<DTO_Departamento> lista = new ArrayList<>();
        DTO_Departamento oDepartamento;
        int itemSeleccionado=-1;
        String descripcionSeleccionada="";

//        DTO_Generico oInicio= new DTO_Generico();
//        oInicio.setCodigo("");
//        oInicio.setDescripcion("Seleccione");
//        lista.add(oInicio);



        for (int i = 0; i < jsonArray.length(); i++) { //recorriendo arrays
            oDepartamento = new DTO_Departamento();
            try
            {
                oDepartamento.setId(jsonArray.getJSONObject(i).getString(paramCod));
                oDepartamento.setDescripcion(jsonArray.getJSONObject(i).getString(paramDescripcion));
                oDepartamento.setSelected(jsonArray.getJSONObject(i).getBoolean(selected));
                //oGenerico.setVisible(jsonArray.getJSONObject(i).getBoolean(visible));
//                    lista.add(oGenerico); //rellena lista que almacenará datos en array de clase
//                    listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo

                if (jsonArray.getJSONObject(i).getBoolean(selected)==true)
                {
                    itemSeleccionado=i;

                }
//                if (jsonArray.getJSONObject(i).getBoolean(visible)==true)
//                {

                lista.add(oDepartamento); //rellena lista que almacenará datos en array de clase
                listaString.add(jsonArray.getJSONObject(i).getString(paramDescripcion)); //rellena lista que alimentará combo
                //}
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        //GlobalFunctions.rellenarSpinner(context,spinner,listaString, itemSeleccionado, descripcionSeleccionada);
        GlobalFunctions.rellenarSpinner(context,spinner,listaString);
        spinner.setSelection(itemSeleccionado);

        return lista;
    }


    public static String callVolleyStringRequest(final Context context, final Map<String, String> params, String URL, final VolleyRP pVolley, final RequestQueue pRequestQueue, final ServerCallback callback){
        HttpsTrustManager.allowAllSSL();
        final StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //result=response;

                callback.onSuccess(response);
                //showLoadingProgress(context,false);
                //onConnectionFinished();
                //progressLogin.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //result = error.toString();

                callback.onSuccess(error.toString());
                //showLoadingProgress(context,false);

                Toast.makeText(context,"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                //onConnectionFailed(error.toString());
                //progressLogin.dismiss();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return params;
            }
        };

        // Adding request to request queue
        VolleyRP.addToQueue(request,pRequestQueue,context,pVolley);

        return "";
    }

    public static String callVolleyStringRequest2(final Context context, final Map<String, String> params, String URL, final VolleyRP pVolley, final RequestQueue pRequestQueue, final ServerCallback callback){
        //showLoadingProgress(context,true);
        final StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //result=response;

                callback.onSuccess(response);
                //showLoadingProgress(context,false);
                //onConnectionFinished();
                //progressLogin.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //result = error.toString();

                callback.onSuccess(error.toString());
                //showLoadingProgress(context,false);

                Toast.makeText(context,"Respuesta onError: " + error.toString() , Toast.LENGTH_SHORT).show();
                //onConnectionFailed(error.toString());
                //progressLogin.dismiss();
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return params;
            }
        };

        // Adding request to request queue
        VolleyRP.addToQueue(request,pRequestQueue,context,pVolley);

        return "";
    }

    public static String getIdxDescripcionLista(List<DTO_Generico> lista, String descripcion){
        String id="";
        for (int i = 0; i < lista.size(); i++) {
            String valor = lista.get(i).getDescripcion();
            if (valor.equalsIgnoreCase(descripcion)){
                id=lista.get(i).getCodigo();
            }
        }
        return id ;
    }

    public static void logout(){
        StringRequest request = new StringRequest(Request.Method.POST, URL_DESARROLLO + "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String estado="";
                sesionFinalizada=true;
                estado="OK";
                cancelAllNotification(context.getApplicationContext());
                //Toast.makeText(GlobalFunctions.context,"Sesión finalizada.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(get,"Respuesta logout: " + error.toString() , Toast.LENGTH_SHORT).show();
                sesionFinalizada=false;
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
        VolleyRP.addToQueue(request,LoginTelefonica.mRequest,context,LoginTelefonica.volley);
    }

    //convert JSON file into String for use with JSONObject methods.
    public static String loadJSONFromAsset(Context context, String jsonFilename) {
        String json = null;
        try
        {
            InputStream is = context.getAssets().open(jsonFilename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
