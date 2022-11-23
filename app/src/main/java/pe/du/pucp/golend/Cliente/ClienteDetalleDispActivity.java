package pe.du.pucp.golend.Cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

import pe.du.pucp.golend.R;

public class ClienteDetalleDispActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_detalle_disp);

        ImageSlider imgSlider = findViewById(R.id.imgSlider);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://sm.pcmag.com/t/pcmag_au/review/m/msi-katana/msi-katana-gf66_vcj6.1920.jpg", ScaleTypes.CENTER_CROP));

        imgSlider.setImageList(slideModels);

    }
}