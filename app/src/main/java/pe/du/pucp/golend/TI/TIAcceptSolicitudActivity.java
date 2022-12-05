package pe.du.pucp.golend.TI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class TIAcceptSolicitudActivity extends AppCompatActivity {

    private static final String ICON_ID = "optionsMarker";
    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    double lugarRecojoLat;
    double lugarRecojoLong;
    Button btnAccept;
    private MapView mapView;
    private MapboxMap mapboxMapG;
    private SymbolManager symbolManager;
    private FillManager fillManager;
    FirebaseUser user;
    String lugarRecojo;
    ArrayList<LatLng> listMarkers = new ArrayList<LatLng>();
    LatLng cia = new LatLng(-12.071379567891883, -77.08025795958308);
    LatLng central = new LatLng(-12.06900927440643, -77.0802447586743);
    LatLng tinkuy = new LatLng(-12.067835217011334, -77.0793933177416);
    LatLng sociales = new LatLng(-12.070807302852508, -77.08042012349578);
    LatLng mcGregor = new LatLng(-12.068448804136043, -77.07877958782447);
    ArrayList<String> listNamesMarkers = new ArrayList<String>();

    protected OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {
            mapboxMapG = mapboxMap;
            for (int i=0;i<listMarkers.size();i++){
                mapboxMapG.addMarker(new MarkerOptions().position(listMarkers.get(i)).title(listNamesMarkers.get(i)));
                mapboxMapG.moveCamera(CameraUpdateFactory.newLatLng(listMarkers.get(i)));
            }
            mapboxMapG.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    String markerTitle = marker.getTitle();
                    lugarRecojo = markerTitle;
                    lugarRecojoLat = marker.getPosition().getLatitude();
                    lugarRecojoLong = marker.getPosition().getLongitude();
                    return false;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_ti_accept_solicitud);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        Geocoder geocoder = new Geocoder(TIAcceptSolicitudActivity.this, Locale.getDefault());
        try {
            List <Address> addresses = geocoder.getFromLocation(cia.getLatitude(), cia.getLongitude(), 1);
            String address = addresses.get(0).getSubLocality();
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
            Log.wtf("msg",address);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Reservas reservas = (Reservas) intent.getSerializableExtra("reservas");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> updates = new HashMap<>();
        btnAccept = findViewById(R.id.btnTIAcceptSolicitudAceptarSoli);
        btnAccept.setOnClickListener(v -> {
            if(lugarRecojo.isEmpty()){
                updates.put("tiUser.avatarUrl",user.getPhotoUrl().toString());
                updates.put("tiUser.nombre",user.getDisplayName());
                updates.put("tiUser.uid",user.getUid());
                updates.put("estado","Solicitud aceptada");
                LocalDateTime localDate = LocalDateTime.now().plusDays(reservas.getTiempoReserva());
                Date date = new Date(localDate.atZone(ZoneId.of("America/New_York")).toEpochSecond() * 1000);
                updates.put("horaFinReserva",new Timestamp(date));
                updates.put("horaRespuesta",Timestamp.now());
                updates.put("lugarRecojo", new GeoPoint(lugarRecojoLat,lugarRecojoLong));
                updates.put("nombreLugarRecojo",lugarRecojo);
                FirebaseFirestore.getInstance().collection("reservas").document(reservas.getKey()).update(updates).addOnSuccessListener(unused -> {
                    Toast.makeText(TIAcceptSolicitudActivity.this, "Se realizó la act con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e->{
                    Log.d("msg",e.getMessage());
                    Toast.makeText(TIAcceptSolicitudActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
                });
            }else{
                Toast.makeText(TIAcceptSolicitudActivity.this, "Debes seleccionar un lugar de recojo", Toast.LENGTH_LONG).show();
            }
        });

        List<LatLng> innerLatLngs = new ArrayList<>();
        innerLatLngs.add(new LatLng(-12.065190487100919, -77.08181911439887));
        innerLatLngs.add(new LatLng(-12.064321705613562, -77.07842649344136));
        innerLatLngs.add(new LatLng(  -12.065853590868658, -77.07817815154074));
        innerLatLngs.add(new LatLng(  -12.066848234422807, -77.07803073939958));
        innerLatLngs.add(new LatLng(  -12.067958197166385, -77.07801599293757));
        innerLatLngs.add(new LatLng(  -12.069990723431516, -77.07816339057023));
        innerLatLngs.add(new LatLng(  -12.071850270282527, -77.07853190563537));
        innerLatLngs.add(new LatLng(  -12.073003480028081, -77.07866456985295));
        innerLatLngs.add(new LatLng(  -12.073378274031759, -77.0787382738559));
        innerLatLngs.add(new LatLng(  -12.07401254233814, -77.0791805071979));
        innerLatLngs.add(new LatLng(-12.07378189965384, -77.07996179082565));
        innerLatLngs.add(new LatLng(-12.073666575534189, -77.0812147915738));
        innerLatLngs.add(new LatLng(-12.073335021618838, -77.08221718959761));
        innerLatLngs.add(new LatLng(-12.073032296722602, -77.08307217502248));
        innerLatLngs.add(new LatLng(-12.06970241616005, -77.08205499941027));
        innerLatLngs.add(new LatLng(-12.069716831112594, -77.08206974049183));
        innerLatLngs.add(new LatLng(-12.06869336341929, -77.08174543178646));
        innerLatLngs.add(new LatLng(-12.067251855665821, -77.08158327304088));
        innerLatLngs.add(new LatLng(-12.067194194875935, -77.08162749517663));
        innerLatLngs.add(new LatLng(-12.06720861069874, -77.08153905030703));
        innerLatLngs.add(new LatLng(-12.065190487100919, -77.08181911439887));
        List<List<LatLng>> latLngs = new ArrayList<>();
        latLngs.add(innerLatLngs);

        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.OUTDOORS, style -> {
            this.mapboxMapG = mapboxMap;
            fillManager = new FillManager(mapView, mapboxMap,style,null);
            // Use options to color it red.
            FillOptions fillOptions = new FillOptions()
                    .withLatLngs(latLngs)
                    .withFillOutlineColor(ColorUtils.colorToRgbaString(Color.GREEN))
                    .withFillOpacity(0.2f);

// Use the manager to draw the annotation.
            fillManager.create(fillOptions);
            symbolManager = new SymbolManager(mapView, mapboxMap, style, null);
        }));


        listMarkers.add(cia);
        listMarkers.add(central);
        listMarkers.add(tinkuy);
        listMarkers.add(sociales);
        listMarkers.add(mcGregor);
        listNamesMarkers.add("Complejo de Innovación Académica");
        listNamesMarkers.add("Biblioteca Central");
        listNamesMarkers.add("Tinkuy");
        listNamesMarkers.add("Complejo de Ciencias Sociales");
        listNamesMarkers.add("Complejo Mac Gregor");
        mapView.getMapAsync(onMapReadyCallback);
    }

    public void backButton(View view){
        onBackPressed();
    }

}