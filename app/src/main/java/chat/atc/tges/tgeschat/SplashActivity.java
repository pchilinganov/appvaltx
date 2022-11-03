package chat.atc.tges.tgeschat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import chat.atc.tges.tgeschat.util.GlobalFunctions;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    Boolean flagVentas=false;
    public static Uri uri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        getSupportActionBar().hide();

        this.overridePendingTransition(R.anim.slide_up,
                R.anim.slide_down);

        init();

        //getDataFromBundle();
    }

    private void getDataFromBundle() {
        uri = ActivityCompat.getReferrer(SplashActivity.this);

        if (uri!=null && (uri.toString().equalsIgnoreCase("android-app://pe.vasslatam.movistar.mobile.sales"))) {
            Log.d("valor de uri", uri.toString());
            Log.d("FROM BUNDLE", "appVentas");
            //vistaIncidenciaMasiva =false;
            //Proviene de AppVentas
            //dniVendedor = getIntent().getStringExtra("DNI");
            //txtUsuario.setText(dniVendedor);
            flagVentas = true;
            SharedPreferences.Editor editor = getSharedPreferences("mta_loged", MODE_PRIVATE).edit();
            editor.putBoolean("flagVentas", flagVentas); // Pasar a appProd
            GlobalFunctions.newIntent(this,LoginTelefonica.class);
            //makeRequestLogin("loginIntegracion", dniVendedor,"");
        }
    }

    private void init() {
        new CountDownTimer(1000, 3000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {


                if (sharedpreferences.contains("data")) {
                    startActivity(new Intent(SplashActivity.this, LoginTelefonica.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    SplashActivity.this.finish();
                }
                else
                {
                    startActivity(new Intent(SplashActivity.this, LoginTelefonica.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    SplashActivity.this.finish();
                }
            }
        }.start();
    }
}