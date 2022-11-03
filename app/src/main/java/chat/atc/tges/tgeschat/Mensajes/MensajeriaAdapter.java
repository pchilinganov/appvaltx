package chat.atc.tges.tgeschat.Mensajes;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.varPublicas.varPublicas;

public class MensajeriaAdapter extends RecyclerView.Adapter<MensajeriaAdapter.MensajesViewHolder> {

    private List<MensajeDeTexto> mensajeDeTextos;
    private Context context;

    public MensajeriaAdapter(List<MensajeDeTexto> mensajeDeTextos, Context context) {
        this.mensajeDeTextos = mensajeDeTextos;
        this.context = context;
    }

    //Infla el cardView
    @Override
    public MensajeriaAdapter.MensajesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mensajes,parent,false);
        return new MensajeriaAdapter.MensajesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MensajeriaAdapter.MensajesViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) holder.mensajeBG.getLayoutParams();

        LinearLayout.LayoutParams llMensaje = (LinearLayout.LayoutParams) holder.TvMensaje.getLayoutParams();
        LinearLayout.LayoutParams llHora = (LinearLayout.LayoutParams) holder.TvHora.getLayoutParams();
        LinearLayout.LayoutParams llNombre = (LinearLayout.LayoutParams) holder.TvNombre.getLayoutParams();
        LinearLayout.LayoutParams llFecha = (LinearLayout.LayoutParams) holder.TvTimeText.getLayoutParams();

        if(mensajeDeTextos.get(position).getTipoMensaje()==1){// VENDEDOR
            //holder.mensajeBG.setBackgroundResource(R.mipmap.in_message_bg);
            //holder.mensajeBG.setBackgroundResource(R.drawable.my_message);
            holder.TvNombre.setText(varPublicas.nomVendedor);
            holder.TvNombre.setTextColor(Color.parseColor("#1461A8"));
            rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            llMensaje.gravity = Gravity.RIGHT;
            llHora.gravity = Gravity.RIGHT;
            llNombre.gravity = Gravity.RIGHT;
            fl.gravity = Gravity.RIGHT;
            holder.TvMensaje.setGravity(Gravity.RIGHT);
            //agregar negritas
            holder.TvMensaje.setTextColor(Color.WHITE);
            holder.TvMensaje.setBackgroundResource(R.drawable.my_message);

            //holder.TvMensaje.setBackgroundResource(R.mipmap.in_message_bg);
        }else if(mensajeDeTextos.get(position).getTipoMensaje()==2){// ASESOR
            //holder.mensajeBG.setBackgroundResource(R.drawable.their_message);
            //holder.mensajeBG.setBackgroundResource(R.mipmap.out_message_bg);

            holder.TvNombre.setText(varPublicas.agenteMesaAyuda);

            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
            rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            llMensaje.gravity = Gravity.LEFT;
            llHora.gravity = Gravity.LEFT;
            llNombre.gravity = Gravity.LEFT;
            fl.gravity = Gravity.LEFT;
            holder.TvMensaje.setGravity(Gravity.LEFT);
            //holder.TvMensaje.setBackgroundResource(R.mipmap.out_message_bg);
            holder.TvMensaje.setBackgroundResource(R.drawable.their_message);
        }

        holder.cardView.setLayoutParams(rl);
        holder.mensajeBG.setLayoutParams(fl);
        holder.TvMensaje.setLayoutParams(llMensaje);
        holder.TvHora.setLayoutParams(llHora);
        holder.TvNombre.setText(mensajeDeTextos.get(position).getNombre());
        holder.TvMensaje.setText(mensajeDeTextos.get(position).getMensaje());

        //logica para mostrar fecha flotante
        String previousTs = "";
        /*if(position>1){
            MensajeDeTexto pm = mensajeDeTextos.get(position-1);
            previousTs = pm.getTimeStamp();
        }
        setTimeTextVisibility(m.getTimeStamp(), previousTs, holder.timeText);*/

        if (mensajeDeTextos.get(position).getTipoMsgArchivo().equalsIgnoreCase("file")){
            holder.TvMensaje.setClickable(true);
            holder.TvMensaje.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='"+ mensajeDeTextos.get(position).getUrlArchivo() +"'>"+ mensajeDeTextos.get(position).getMensaje() +"</a>";
            holder.TvMensaje.setText(Html.fromHtml(text));
            if(mensajeDeTextos.get(position).getTipoMensaje()==1) {//EMISOR
                holder.TvMensaje.setLinkTextColor(Color.WHITE);
            }else{
                holder.TvMensaje.setLinkTextColor(Color.BLUE);
            }
        }

        holder.TvHora.setText(mensajeDeTextos.get(position).getHoraDelMensaje());
        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) holder.cardView.getBackground().setAlpha(0);
        else holder.cardView.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent));

        // apply click events
        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return mensajeDeTextos.size();
    }

    private void applyClickEvents(final MensajeriaAdapter.MensajesViewHolder holder, final int position) {

        holder.TvMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(Mensajeria.rv);
                if(holder.TvHora.getVisibility() == View.VISIBLE && holder.TvNombre.getVisibility() == View.VISIBLE)
                {
                    holder.TvNombre.setVisibility(View.GONE);
                    holder.TvHora.setVisibility(View.GONE);
                }
                else
                {
                    holder.TvNombre.setVisibility(View.VISIBLE);
                    holder.TvHora.setVisibility(View.VISIBLE);
                }
            }
        });

        /*holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                //mostrarDialogoIncidenciaMasiva();
                return true;
            }
        });*/
    }

    static class MensajesViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        LinearLayout mensajeBG;
        TextView TvNombre;
        TextView TvMensaje;
        TextView TvHora;
        TextView TvTimeText;

        MensajesViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cvMensaje);
            mensajeBG = (LinearLayout) itemView.findViewById(R.id.mensajeBG);
            TvTimeText = (TextView) itemView.findViewById(R.id.timeText);
            TvNombre =  (TextView) itemView.findViewById(R.id.msName);
            TvMensaje = (TextView) itemView.findViewById(R.id.msTexto);
            TvHora = (TextView) itemView.findViewById(R.id.msHora);
        }
    }
}
