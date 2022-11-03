package chat.atc.tges.tgeschat.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import chat.atc.tges.tgeschat.R;

public class RvIncidenciasAdapter extends RecyclerView.Adapter<RvIncidenciasAdapter.PaletteViewHolder> {
    //private List<Incidencia> incidencias;
    private List<String> listaTextoIncidencia;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    /*public RvIncidenciasAdapter(List<Incidencia> incidencias, List<String> listaTextoIncidencia) {
        this.incidencias = incidencias;
        this.listaTextoIncidencia = listaTextoIncidencia;
        //this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }*/

    public RvIncidenciasAdapter(List<String> listaTextoIncidencia) {
        this.listaTextoIncidencia = listaTextoIncidencia;
        //this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public PaletteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_incidencia, parent, false);
        return new PaletteViewHolder(row);
    }

    @Override
    public void onBindViewHolder(PaletteViewHolder holder, int position) {
        String motivo="", cuerpo1="",cuerpo2="", cuerpo3="", ticketDoit="";

        String text="",textoAcumulado="";
        textoAcumulado= listaTextoIncidencia.get(position).toString();
        holder.getTxtCuerpo1().setText(Html.fromHtml(textoAcumulado));
    }

    @Override
    public int getItemCount() {
        return listaTextoIncidencia.size();
    }

    class PaletteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtCuerpo1, txtCuerpo2, txtMotivo, txtTicketDoit, txtCuerpo3;

        public PaletteViewHolder(View itemView) {
            super(itemView);
            txtCuerpo1 = (TextView) itemView.findViewById(R.id.txtCuerpo1);
            /*txtCuerpo2 = (TextView) itemView.findViewById(R.id.txtCuerpo2);
            txtMotivo = (TextView) itemView.findViewById(R.id.txtMotivo);
            txtTicketDoit = (TextView) itemView.findViewById(R.id.txtTicketDoit);
            txtCuerpo3 = (TextView) itemView.findViewById(R.id.txtCuerpo3);*/

            itemView.setOnClickListener(this);
        }

        public TextView getTxtCuerpo1() {
            return txtCuerpo1;
        }

        /*public TextView getTxtCuerpo2() {
            return txtCuerpo2;
        }

        public TextView getTxtMotivo() {
            return txtMotivo;
        }

        public TextView getTxtTicketDoit() {
            return txtTicketDoit;
        }

        public TextView getTxtCuerpo3() {
            return txtCuerpo3;
        }*/

        @Override
        public void onClick(View v) {
            //recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
        }
    }

}
