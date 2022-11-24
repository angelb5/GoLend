package pe.du.pucp.golend.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import java.util.Locale;

import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.R;

public class DeviceCardAdapter extends FirestorePagingAdapter<Device, DeviceCardAdapter.DeviceViewHolder>  {
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public DeviceCardAdapter(@NonNull FirestorePagingOptions options, Class activity) {
        super(options);
        nextActivity = activity;
    }

    Class nextActivity;


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_devices, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull DeviceViewHolder holder, int position, @NonNull Device model) {
        holder.tvMarca.setText(model.getMarca());
        holder.tvModelo.setText(model.getModelo());
        holder.tvCategoria.setText(model.getCategoria());
        switch (model.getSearchCategoria()){
            case "Laptop":
                holder.ivCategoria.setImageResource(R.drawable.ic_laptop_green);
                break;
            case "Celular":
                holder.ivCategoria.setImageResource(R.drawable.ic_celular_green);
                break;
            case "Monitor":
                holder.ivCategoria.setImageResource(R.drawable.ic_monitor_green);
                break;
            case "Tablet":
                holder.ivCategoria.setImageResource(R.drawable.ic_tablet_green);
                break;
            case "Otros":
                holder.ivCategoria.setImageResource(R.drawable.ic_otros_green);
        }

        holder.device = model;
        Glide.with(holder.itemView.getContext()).load(model.getFotosUrl().get(0))
                .placeholder(AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.image_device_placeholder)).dontAnimate()
                .into(holder.ivDeviceFoto);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDeviceFoto;
        ImageView ivCategoria;
        TextView tvMarca;
        TextView tvModelo;
        TextView tvCategoria;
        Device device;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDeviceFoto = itemView.findViewById(R.id.ivCardDevicesPhoto);
            ivCategoria = itemView.findViewById(R.id.ivCardDevicesCategoria);
            tvCategoria = itemView.findViewById(R.id.tvCardDevicesCategoria);
            tvMarca = itemView.findViewById(R.id.tvCardDevicesMarca);
            tvModelo = itemView.findViewById(R.id.tvCardDevicesModelo);
            itemView.setOnClickListener(view -> {
                Log.d("msg", "hola desde "+device.getModelo());
            });
        }
    }

}
