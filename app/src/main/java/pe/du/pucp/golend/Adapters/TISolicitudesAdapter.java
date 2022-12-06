package pe.du.pucp.golend.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class TISolicitudesAdapter extends FirestorePagingAdapter<Reservas, TISolicitudesAdapter.ReservasViewHolder> {
    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());

    public TISolicitudesAdapter(@NonNull FirestorePagingOptions options, Class activity) {
        super(options);
        nextActivity = activity;
    }

    Class nextActivity;

    @NonNull
    @Override
    public TISolicitudesAdapter.ReservasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ti_solicitud, parent, false);
        return new TISolicitudesAdapter.ReservasViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull TISolicitudesAdapter.ReservasViewHolder holder, int position, @NonNull Reservas model) {
        holder.tvSolicitud.setText(("Solicitud "+model.getKey()).toUpperCase(Locale.ROOT));
        String fechaReserva = df.format(model.getHoraReserva().toDate());
        if(model.getHoraFinReserva()==null){
            holder.tvFechaRealizado.setText(fechaReserva);
        }else{
            String fechaFin = df.format(model.getHoraFinReserva().toDate());
            holder.tvFechaRealizado.setText(fechaFin);
        }
        holder.tvCliente.setText(model.getClienteUser().getNombre());
        holder.reservas = model;
    }

    public class ReservasViewHolder extends RecyclerView.ViewHolder {

        TextView tvSolicitud;
        TextView tvCliente;
        TextView tvFechaRealizado;
        Reservas reservas;
        Button btnDetalleTI;

        public ReservasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSolicitud = itemView.findViewById(R.id.tvTICardSolicitud);
            tvCliente = itemView.findViewById(R.id.tvTICardCliente);
            tvFechaRealizado= itemView.findViewById(R.id.tvTICardFechaReserva);
            btnDetalleTI = itemView.findViewById(R.id.btnDetallesSolicitudTI);
            itemView.setOnClickListener(view -> {
                Intent reservaIntent = new Intent(itemView.getContext(), nextActivity);
                reservaIntent.putExtra("reservas", reservas);
                if(reservas.getHoraFinReserva() != null){
                    reservaIntent.putExtra("horaFinNano", reservas.getHoraFinReserva().getNanoseconds());
                    reservaIntent.putExtra("horaFinSec", reservas.getHoraFinReserva().getSeconds());
                }
                reservaIntent.putExtra("horaReservaNano",reservas.getHoraReserva().getNanoseconds());
                reservaIntent.putExtra("horaReservaSec",reservas.getHoraReserva().getSeconds());
                if(reservas.getHoraRespuesta()!=null) {
                    reservaIntent.putExtra("horaRespNano", reservas.getHoraRespuesta().getNanoseconds());
                    reservaIntent.putExtra("horaRespSec", reservas.getHoraRespuesta().getSeconds());
                }
                if(reservas.getEstado().equals("Solicitud aceptada")){
                    reservaIntent.putExtra("lati", reservas.getLugarRecojo().getLatitude());
                    reservaIntent.putExtra("long", reservas.getLugarRecojo().getLongitude());
                }
                itemView.getContext().startActivity(reservaIntent);
            });
            btnDetalleTI.setOnClickListener(v -> {
                Intent reservaIntent = new Intent(itemView.getContext(), nextActivity);
                reservaIntent.putExtra("reservas", reservas);
                if(reservas.getHoraFinReserva() != null){
                    reservaIntent.putExtra("horaFinNano", reservas.getHoraFinReserva().getNanoseconds());
                    reservaIntent.putExtra("horaFinSec", reservas.getHoraFinReserva().getSeconds());
                }
                reservaIntent.putExtra("horaReservaNano",reservas.getHoraReserva().getNanoseconds());
                reservaIntent.putExtra("horaReservaSec",reservas.getHoraReserva().getSeconds());
                if(reservas.getHoraRespuesta()!=null) {
                    reservaIntent.putExtra("horaRespNano", reservas.getHoraRespuesta().getNanoseconds());
                    reservaIntent.putExtra("horaRespSec", reservas.getHoraRespuesta().getSeconds());
                }
                if(reservas.getEstado().equals("Solicitud aceptada")){
                    reservaIntent.putExtra("lati", reservas.getLugarRecojo().getLatitude());
                    reservaIntent.putExtra("long", reservas.getLugarRecojo().getLongitude());
                }
                itemView.getContext().startActivity(reservaIntent);
            });
        }
    }

}
