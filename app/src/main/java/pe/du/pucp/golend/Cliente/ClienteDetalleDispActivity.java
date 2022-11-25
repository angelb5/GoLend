package pe.du.pucp.golend.Cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.R;

public class ClienteDetalleDispActivity extends AppCompatActivity {

    TextView tvMarca;
    TextView tvModelo;
    TextView tvDescripcion;
    TextView tvAccesorios;
    TextView tvDisponibles;
    TextView tvCategoria;
    ImageView ivCategoria;
    ImageSlider imgSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_detalle_disp);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }
        Device device = (Device) intent.getSerializableExtra("dispositivo");

        tvMarca = findViewById(R.id.tvClienteDispMarca);
        tvModelo = findViewById(R.id.tvClienteDispModelo);
        tvDescripcion = findViewById(R.id.tvClienteDispDescripcion);
        tvAccesorios = findViewById(R.id.tvClienteDispAccesorios);
        tvDisponibles = findViewById(R.id.tvClienteDispDisponibilidad);
        tvCategoria = findViewById(R.id.tvClienteDispCategoria);
        ivCategoria = findViewById(R.id.ivClienteDispCategoria);
        imgSlider = findViewById(R.id.isClienteDisp);

        tvMarca.setText(device.getMarca());
        tvModelo.setText(device.getModelo());
        tvDescripcion.setText(device.getDescripcion());
        tvAccesorios.setText(device.getAccesorios());
        tvDisponibles.setText(String.valueOf(device.getDisponibles()));
        tvCategoria.setText(device.getCategoria());

        ArrayList<SlideModel> slideModels = new ArrayList<>();
        for (String url : device.getFotosUrl()){
            slideModels.add(new SlideModel(url, ScaleTypes.CENTER_CROP));
        }

        imgSlider.setImageList(slideModels);

        switch (device.getSearchCategoria()){
            case "Laptop":
                ivCategoria.setImageResource(R.drawable.ic_laptop_green);
                break;
            case "Celular":
                ivCategoria.setImageResource(R.drawable.ic_celular_green);
                break;
            case "Monitor":
                ivCategoria.setImageResource(R.drawable.ic_monitor_green);
                break;
            case "Tablet":
                ivCategoria.setImageResource(R.drawable.ic_tablet_green);
                break;
            case "Otros":
                ivCategoria.setImageResource(R.drawable.ic_otros_green);
        }

    }

    public void backButton(View view){
        onBackPressed();
    }
}