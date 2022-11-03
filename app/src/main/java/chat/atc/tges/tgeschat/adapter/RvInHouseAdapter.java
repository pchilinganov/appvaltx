package chat.atc.tges.tgeschat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.model.DTO_INHouse;
import chat.atc.tges.tgeschat.util.ExpandCollapse;

import static chat.atc.tges.tgeschat.util.GlobalFunctions.escapeNull;

public class RvInHouseAdapter extends RecyclerView.Adapter<RvInHouseAdapter.MyViewHolder>{
    private Context mContext;
    private List<DTO_INHouse> listaINHOUSE;

    public RvInHouseAdapter(Context mContext, List<DTO_INHouse> documentos) { //Constructor
        this.mContext = mContext;
        this.listaINHOUSE = documentos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // Relaciona la clase actual con el layout correspondiente
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ly_item_rv_in_house, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvProducto, tvCliente, tvEstado, tvBack, tvDepartamento, tvFolio, tvDireccion, tvTelefono, tvFechaContrato;
        public ImageView imgMas, imgMenos;
        LinearLayout hiddenDataHori, hiddenDataHori2;
        public MyViewHolder(View view)
        {
            super(view);
            hiddenDataHori = (LinearLayout) view.findViewById(R.id.hiddenDataHori);
            hiddenDataHori2 = (LinearLayout) view.findViewById(R.id.hiddenDataHori2);
            tvProducto = (TextView) view.findViewById(R.id.tvProducto);
            tvCliente = (TextView) view.findViewById(R.id.tvCliente);
            tvEstado = (TextView) view.findViewById(R.id.tvEstado);
            tvBack = (TextView) view.findViewById(R.id.tvBack);
            tvDepartamento = (TextView) view.findViewById(R.id.tvDepartamento);
            tvFolio = (TextView) view.findViewById(R.id.tvFolio);
            tvDireccion = (TextView) view.findViewById(R.id.tvDireccion);
            tvTelefono = (TextView) view.findViewById(R.id.tvTelefono);
            tvFechaContrato = (TextView) view.findViewById(R.id.tvFechaContrato);
            imgMas = (ImageView) view.findViewById(R.id.imgPlus);
            imgMenos = (ImageView) view.findViewById(R.id.imgMinus);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {  //opera con elementos parseados de layout Item
        DTO_INHouse oDocumento = listaINHOUSE.get(position);

        holder.tvProducto.setText(escapeNull(oDocumento.getProducto()));
        holder.tvCliente.setText(escapeNull(oDocumento.getCliente()));
        holder.tvEstado.setText(escapeNull(oDocumento.getEstado()));
        holder.tvBack.setText(escapeNull(oDocumento.getBack()));
        holder.tvDepartamento.setText(escapeNull(oDocumento.getDepartamento()));
        holder.tvFolio.setText(escapeNull(oDocumento.getFolio()));
        holder.tvDireccion.setText(escapeNull(oDocumento.getDireccion()));
        holder.tvTelefono.setText(escapeNull(oDocumento.getTelefono()));
        holder.tvFechaContrato.setText(escapeNull(oDocumento.getfContrato()));

        applyClickEvents(holder, position);
    }

    private void applyClickEvents(final MyViewHolder holder, final int position){
        holder.imgMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.hiddenDataHori.getVisibility()==View.VISIBLE && holder.hiddenDataHori2.getVisibility()==View.VISIBLE){
                    ExpandCollapse.Collapse(holder.hiddenDataHori);
                    ExpandCollapse.Collapse(holder.hiddenDataHori2);
                    holder.imgMas.setImageResource(R.drawable.ic_plus);
                }
                else
                {
                    ExpandCollapse.Expand(holder.hiddenDataHori);
                    ExpandCollapse.Expand(holder.hiddenDataHori2);
                    holder.imgMas.setImageResource(R.drawable.ic_minus);
                }



            }
        });
    }

    @Override
    public int getItemCount() {
        return listaINHOUSE.size();
    }

}
