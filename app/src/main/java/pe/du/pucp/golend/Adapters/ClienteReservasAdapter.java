package pe.du.pucp.golend.Adapters;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class ClienteReservasAdapter extends FirestorePagingAdapter<Reservas, ClienteReservasAdapter.ReservasViewHolder> {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());

    public ClienteReservasAdapter(@NonNull FirestorePagingOptions options, Class activity) {
        super(options);
        nextActivity = activity;
    }

    Class nextActivity;

    @NonNull
    @Override
    public ReservasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cliente_reservas, parent, false);
        return new ReservasViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReservasViewHolder holder, int position, @NonNull Reservas model) {
        holder.tvModelo.setText(model.getDevice().getModelo());
        String fecha = df.format(model.getHoraReserva().toDate());
        holder.tvFechaRealizado.setText(fecha);
        holder.reservas = model;
        Glide.with(holder.itemView.getContext()).load(model.getDevice().getFotoPrincipal())
                .placeholder(AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.image_device_placeholder)).dontAnimate()
                .into(holder.ivPhoto);
    }

    public class ReservasViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        TextView tvModelo;
        TextView tvFechaRealizado;
        Reservas reservas;
        Button btnDetalle;

        public ReservasViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivClienteReservasCardImage);
            tvModelo = itemView.findViewById(R.id.tvClienteReservasCardModelo);
            tvFechaRealizado= itemView.findViewById(R.id.tvClienteReservasCardFecha);
            btnDetalle = itemView.findViewById(R.id.btnClienteReservasCardDetalle);
            itemView.setOnClickListener(view -> {
                Intent reservaIntent = new Intent(itemView.getContext(), nextActivity);
                reservaIntent.putExtra("reservas", reservas);
                itemView.getContext().startActivity(reservaIntent);
            });
            btnDetalle.setOnClickListener(v -> {
                Intent reservaIntent = new Intent(itemView.getContext(), nextActivity);
                reservaIntent.putExtra("reservas", reservas);
                itemView.getContext().startActivity(reservaIntent);
            });

        }
    }
}
