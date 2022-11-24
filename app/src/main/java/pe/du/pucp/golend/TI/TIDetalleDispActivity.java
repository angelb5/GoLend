package pe.du.pucp.golend.TI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import pe.du.pucp.golend.R;

public class TIDetalleDispActivity extends AppCompatActivity {
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_detalle_disp);

        ImageSlider imgSlider = findViewById(R.id.isTIDisp);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));
        imgSlider.setImageList(slideModels);
    }


    public void showAlert(View view){
        MaterialAlertDialogBuilder alertEliminar = new MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme_Center);
        alertEliminar.setIcon(R.drawable.ic_trash);
        alertEliminar.setTitle("Borrar dispositivo");
        alertEliminar.setMessage("¿Está seguro que desea borrar este dispositivo? Esta acción es irreversible");
        alertEliminar.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    }

