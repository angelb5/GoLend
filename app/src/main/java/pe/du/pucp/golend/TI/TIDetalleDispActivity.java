package pe.du.pucp.golend.TI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.R;

public class TIDetalleDispActivity extends AppCompatActivity {

    Device device;
    TextView tvMarca;
    TextView tvModelo;
    TextView tvDescripcion;
    TextView tvAccesorios;
    TextView tvDisponibles;
    TextView tvStock;
    TextView tvEnPrestamo;
    TextView tvCategoria;
    ImageView ivCategoria;
    ImageSlider imgSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_detalle_disp);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }
        device = (Device) intent.getSerializableExtra("dispositivo");

        tvMarca = findViewById(R.id.tvTiDispMarca);
        tvModelo = findViewById(R.id.tvTiDispModelo);
        tvDescripcion = findViewById(R.id.tvTIDispDescripcion);
        tvAccesorios = findViewById(R.id.tvTIDispAccesorios);
        tvDisponibles = findViewById(R.id.tvTIDispDisponibilidad);
        tvStock = findViewById(R.id.tvTIDispStock);
        tvEnPrestamo = findViewById(R.id.tvTIDispEnPrestamo);
        tvCategoria = findViewById(R.id.tvTiDispCategoria);
        ivCategoria = findViewById(R.id.ivTiDispCategoria);
        imgSlider = findViewById(R.id.isTIDisp);

        tvMarca.setText(device.getMarca());
        tvModelo.setText(device.getModelo());
        tvDescripcion.setText(device.getDescripcion());
        tvAccesorios.setText(device.getAccesorios());
        tvDisponibles.setText(String.valueOf(device.getDisponibles()));
        tvStock.setText(String.valueOf(device.getStock()));
        tvEnPrestamo.setText(String.valueOf(device.getEnPrestamo()));
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


    public void showAlert(View view){
        if (device.getEnPrestamo()>0) {
            Toast.makeText(TIDetalleDispActivity.this, "Hay unidades de este dispositivo en préstamo", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialAlertDialogBuilder alertEliminar = new MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme_Center);
        alertEliminar.setIcon(R.drawable.ic_trash);
        alertEliminar.setTitle("Borrar dispositivo");
        alertEliminar.setMessage("¿Está seguro que desea borrar este dispositivo? Esta acción es irreversible");
        alertEliminar.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("devices").document(device.getKey()).delete().addOnSuccessListener(unused -> {
                    Toast.makeText(TIDetalleDispActivity.this, "Se ha eliminado el dispositivo", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(TIDetalleDispActivity.this, "No se ha podido eliminar el dispositivo", Toast.LENGTH_SHORT).show();
                });
                Log.d("msgAlert","ELIMINAR");
            }

        });
        alertEliminar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("msgAlert","CANCELAR");
            }
        });
        alertEliminar.show();
    }

    public void backButton(View view){
        onBackPressed();
    }

    public void goToUpdateDevice(View view) {
        Intent updateIntent = new Intent(TIDetalleDispActivity.this, TIUpdateDeviceActivity.class);
        updateIntent.putExtra("dispositivo", device);
        updateActivityLauncher.launch(updateIntent);
    }

    ActivityResultLauncher<Intent> updateActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if(data == null || !data.hasExtra("dispositivo")) return;
                        device = (Device) data.getSerializableExtra("dispositivo");
                        tvMarca.setText(device.getMarca());
                        tvModelo.setText(device.getModelo());
                        tvDescripcion.setText(device.getDescripcion());
                        tvAccesorios.setText(device.getAccesorios());
                        tvDisponibles.setText(String.valueOf(device.getDisponibles()));
                        tvStock.setText(String.valueOf(device.getStock()));
                        tvEnPrestamo.setText(String.valueOf(device.getEnPrestamo()));
                        tvCategoria.setText(device.getCategoria());

                        ArrayList<SlideModel> slideModels = new ArrayList<>();
                        for (String url : device.getFotosUrl()){
                            slideModels.add(new SlideModel(url, ScaleTypes.CENTER_CROP));
                        }

                        imgSlider.setImageList(slideModels);
                        setResult(RESULT_OK);
                    }
                }
            });
}

