package pe.du.pucp.golend.Adapters;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import pe.du.pucp.golend.Dtos.PrestamosMarcasDto;
import pe.du.pucp.golend.R;

public class PrestamosMarcaAdapter extends  RecyclerView.Adapter<PrestamosMarcaAdapter.PrestamosMarcaViewHolder> {

    private List<PrestamosMarcasDto> marcasList;

    @Override
    public int getItemCount() {
        return marcasList.size();
    }

    public long getItemId(int position) {
        return position;
    }


    public PrestamosMarcaAdapter(List<PrestamosMarcasDto> marcasList) {
        this.marcasList = marcasList;
    }

    @NonNull
    @Override
    public PrestamosMarcaAdapter.PrestamosMarcaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_statistics_disp_marca, parent, false);
        return new PrestamosMarcaAdapter.PrestamosMarcaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrestamosMarcaAdapter.PrestamosMarcaViewHolder holder, int position) {
        PrestamosMarcasDto prestamosMarcaDto = marcasList.get(position);
        holder.tvMarca.setText(prestamosMarcaDto.getMarca());
        holder.tvPrestamos.setText(prestamosMarcaDto.getPrestamos().toString());
    }

    public class PrestamosMarcaViewHolder extends RecyclerView.ViewHolder {

        TextView tvMarca;
        TextView tvPrestamos;

        public PrestamosMarcaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMarca = itemView.findViewById(R.id.tvCardDevicesMarca);
            tvPrestamos = itemView.findViewById(R.id.tvCardDevicesModelo);
        }
    }

}
