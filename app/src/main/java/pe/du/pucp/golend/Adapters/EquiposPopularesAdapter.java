package pe.du.pucp.golend.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import java.util.List;

import pe.du.pucp.golend.Dtos.PrestamosMarcasDto;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class EquiposPopularesAdapter extends FirestorePagingAdapter<Device,EquiposPopularesAdapter.EquiposPopularesViewHolder> {

    public EquiposPopularesAdapter(@NonNull FirestorePagingOptions options) {
        super(options);
    }

    @NonNull
    @Override
    public EquiposPopularesAdapter.EquiposPopularesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_equipos_populares, parent, false);
        return new EquiposPopularesAdapter.EquiposPopularesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquiposPopularesAdapter.EquiposPopularesViewHolder holder, int position, @NonNull Device model) {
        holder.tvMarca.setText(model.getMarca());
        holder.tvPrestamos.setText(model.getEnPrestamo()+"");
        holder.tvModelo.setText(model.getModelo());
        holder.tvCateg.setText(model.getCategoria());
        switch (model.getCategoria()){
            case "Laptop":
                holder.ivCateg.setImageResource(R.drawable.ic_laptop_green);
                break;
            case "Celular":
                holder.ivCateg.setImageResource(R.drawable.ic_celular_green);
                break;
            case "Monitor":
                holder.ivCateg.setImageResource(R.drawable.ic_monitor_green);
                break;
            case "Tablet":
                holder.ivCateg.setImageResource(R.drawable.ic_tablet_green);
                break;
            case "Otros":
                holder.ivCateg.setImageResource(R.drawable.ic_otros_green);
        }
        Glide.with(holder.itemView.getContext()).load(model.getFotosUrl().get(0))
                .placeholder(AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.image_device_placeholder)).dontAnimate()
                .into(holder.ivDevice);

    }

        public class EquiposPopularesViewHolder extends RecyclerView.ViewHolder {

        TextView tvMarca;
        TextView tvPrestamos;
        ImageView ivDevice;
        ImageView ivCateg;
        TextView tvCateg;
        TextView tvModelo;

        public EquiposPopularesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMarca = itemView.findViewById(R.id.tvEquiposPopularesMarca);
            tvModelo = itemView.findViewById(R.id.tvCardEquiposPopularesMarca);
            tvPrestamos = itemView.findViewById(R.id.tvCardEquiposPopularesCantDisp);
            ivCateg = itemView.findViewById(R.id.ivCardEquiposPopularesCategoria);
            tvCateg = itemView.findViewById(R.id.tvCardEquiposPopularesCategoria);
            ivDevice= itemView.findViewById(R.id.ivEquipoPopularDeviceFoto);
        }
    }

}
