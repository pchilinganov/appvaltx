package chat.atc.tges.tgeschat.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * Created by rodriguez on 12/07/2018.
 */

public class RefreshLogCat extends Service {


    private static final String TAG = "" ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        miHilo();

		/*handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				UPDATE_LISTENER.actualizarCronometro(cronometro);
			}
		};*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Hilo termino","HIlo terminó de ejecutarse");
    }

    protected void miHilo(){
        Thread t= new Thread(){
            public void run(){
                iniciaHilo();
            }
        };
        t.start();
    }

    private void iniciaHilo()
    {
        try {
            while (1==1){

                writeLogCat();
                    Thread.sleep(300000);	//cada 5 minutos
            }
        } catch (Exception ex) {
            Log.d("Exception", ex.getMessage());
        }
    }

    private void writeLogCat(){

        //Obteneindo fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);

        if(isExternalStorageWritable()){
            File appDirectory = new File(Environment.getExternalStorageDirectory()+ "/MyAppfolder");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat"+fecha+".txt");

            if(!appDirectory.exists()){
                appDirectory.mkdir();
            }

            if(!logDirectory.exists()){
                logDirectory.mkdir();
            }

            if(!logFile.exists()){
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Process process = Runtime.getRuntime().exec("logcat -f "+logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(RefreshLogCat.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        Process process = Runtime.getRuntime().exec("logcat -f "+logFile);
                    }
                    else
                    {
                        Process process = Runtime.getRuntime().exec("logcat -f "+logFile);
                    }
                }

            }
            catch (IOException e){
                e.printStackTrace();
            }*/
        }
        else if (isExternalStorageReadable())
        {
            Log.i(TAG, "ONLY READABLE");
        }
        else
        {
            Log.i(TAG, "NOT ACCESSIBLE");
        }
    }

    public boolean isExternalStorageReadable(){

        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }
    public boolean isExternalStorageWritable(){

        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }
}
