package chat.atc.tges.tgeschat;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PopupFormularioOffLine extends AppCompatActivity {

    private TextView tvMsg;
    private Button btnAceptar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_popup_formulario_offline);

        /*DisplayMetrics medida = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medida);

        int ancho = medida.widthPixels;
        int alto = medida.heightPixels;

        getWindow().setLayout((int) (ancho * 0.85), (int)(alto * 0.85));*/

        tvMsg = (TextView) findViewById(R.id.tvMsg);
        btnAceptar = (Button) findViewById(R.id.btnAceptar);
        String ticket = getIntent().getStringExtra("ticket");
        tvMsg.setText("Mensaje :" + ticket+".");


        this.btnAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Aceptar();
            }
        });

    }


    public void Aceptar(){
        Intent i = new Intent(PopupFormularioOffLine.this, FormularioOffLine.class);
        startActivity(i);
    }
}
