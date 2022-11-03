package chat.atc.tges.tgeschat.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.model.DTO_ticket_chat;
import java.util.List;

//public class RvChatTicket extends RecyclerView.Adapter<ViewHolder> {
public class RvChatTicket extends RecyclerView.Adapter<RvChatTicket.ViewHolder> {
    private List<DTO_ticket_chat> listaMensaje;

    public RvChatTicket(List<DTO_ticket_chat> list) {
        this.listaMensaje = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_ticket_uno, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String substring = this.listaMensaje.get(i).getUsuario().substring(0, 1);
        Log.d("VUELTAS", String.valueOf(this.listaMensaje.get(i).getInduser()));
        if (this.listaMensaje.get(i).getInduser() == 1) {
            viewHolder.layoutchat1.setVisibility(0);
            viewHolder.txt1.setText(substring);
            viewHolder.mensaje1.setText(this.listaMensaje.get(i).getMensaje());
            viewHolder.txtuserId1.setText(this.listaMensaje.get(i).getUsuario());
            viewHolder.txtfecha1.setText(this.listaMensaje.get(i).getFecha());
            return;
        }
        viewHolder.layoutchat2.setVisibility(0);
        viewHolder.txt2.setText(substring);
        viewHolder.mensaje2.setText(this.listaMensaje.get(i).getMensaje());
        viewHolder.txtuserId2.setText(this.listaMensaje.get(i).getUsuario());
        viewHolder.txtfecha2.setText(this.listaMensaje.get(i).getFecha());
    }

    public int getItemCount() {
        return this.listaMensaje.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public LinearLayout layoutchat1;
        /* access modifiers changed from: private */
        public LinearLayout layoutchat2;
        /* access modifiers changed from: private */
        public TextView mensaje1;
        /* access modifiers changed from: private */
        public TextView mensaje2;
        /* access modifiers changed from: private */
        public TextView txt1;
        /* access modifiers changed from: private */
        public TextView txt2;
        /* access modifiers changed from: private */
        public TextView txtfecha1;
        /* access modifiers changed from: private */
        public TextView txtfecha2;
        /* access modifiers changed from: private */
        public TextView txtuserId1;
        /* access modifiers changed from: private */
        public TextView txtuserId2;

        public ViewHolder(View view) {
            super(view);
            this.txt1 = (TextView) view.findViewById(R.id.txt1);
            this.txt2 = (TextView) view.findViewById(R.id.txt2);
            this.txtuserId1 = (TextView) view.findViewById(R.id.txtuserId1);
            this.txtuserId2 = (TextView) view.findViewById(R.id.txtuserId2);
            this.txtfecha1 = (TextView) view.findViewById(R.id.txtfecha1);
            this.txtfecha2 = (TextView) view.findViewById(R.id.txtfecha2);
            this.mensaje1 = (TextView) view.findViewById(R.id.mensaje1);
            this.mensaje2 = (TextView) view.findViewById(R.id.mensaje2);
            this.layoutchat1 = (LinearLayout) view.findViewById(R.id.layoutchat1);
            this.layoutchat2 = (LinearLayout) view.findViewById(R.id.layoutchat2);
        }
    }
}
