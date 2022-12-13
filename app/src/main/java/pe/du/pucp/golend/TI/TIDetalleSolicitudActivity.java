package pe.du.pucp.golend.TI;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class TIDetalleSolicitudActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    private static final String ICON_ID = "optionsMarker";
    TextView tvFechaReserva;
    TextView tvEstado;
    TextView tvMotivo;
    TextView tvCurso;
    TextView tvTiempoReserva;
    TextView tvOtros;
    ImageView ivDni;
    ImageView ivti;
    ChipGroup cgProgramas;
    LinearLayout llResponseInfo;
    LinearLayout llAcceptInfo;
    LinearLayout llRejectInfo;
    LinearLayout llButtons;
    LinearLayout llOtros;
    TextView tvMarca;
    TextView tvModelo;
    TextView tvCategoria;
    ImageView ivCategoriaDisp;
    ShapeableImageView ivFotoEquipo;
    TextView tvMotivoRechazo;
    TextView tvFechaResponse;
    TextView tvRolCliente;
    TextView tvNombreTI;
    TextView tvNombreCliente;
    ImageView ivCliente;
    Integer horaReservaNano;
    Long horaReservaSec;
    Integer horaRespNano;
    Long horaRespSec;
    Button btnAccept;
    Button btnReject;
    TextView nombreLugar;
    Integer horaFinNano;
    Long horaFinSec;
    private ArrayList<String> listProgramas = new ArrayList<>();
    ProgressBar pbLoading;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;
    private Double latitude;
    private  Double longitud;
    Button btnDevuelto;
    Reservas reservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_ti_detalle_solicitud);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        reservas = (Reservas) intent.getSerializableExtra("reservas");
        horaReservaNano = (Integer) intent.getSerializableExtra("horaReservaNano");
        horaReservaSec = (Long) intent.getSerializableExtra("horaReservaSec");
        latitude = (Double) intent.getSerializableExtra("lati");
        longitud = (Double) intent.getSerializableExtra("long");

        tvFechaReserva = findViewById(R.id.tvTIDetalleSolicitudFechaReservaCliente);
        tvEstado = findViewById(R.id.tvTIDetalleSolicitudEstado);
        tvMotivo = findViewById(R.id.tvTIDetalleSolicitudMotivo);
        tvCurso = findViewById(R.id.tvTIDetalleSolicitudCurso);
        tvTiempoReserva = findViewById(R.id.tvTIDetalleSolicitudTiempo);
        tvOtros = findViewById(R.id.tvTIDetalleSolicitudOtros);
        ivDni = findViewById(R.id.ivTIDetalleSolicitudDNI);
        cgProgramas = findViewById(R.id.cgTIDetalleSolicitudProgramas);
        llResponseInfo = findViewById(R.id.llTIDetalleSolicitudResponse);
        llAcceptInfo = findViewById(R.id.llTIDetalleSolicitudLugarRecojo);
        llRejectInfo = findViewById(R.id.llTIDetalleSolicitudMotivoRechazo);
        llOtros = findViewById(R.id.llTIDetalleSolicitudOtros);
        tvNombreTI = findViewById(R.id.tvTIDetalleSolicitudNombreTI);
        tvFechaResponse = findViewById(R.id.tvTIDetalleSolicitudFechaResponseTI);
        tvMotivoRechazo = findViewById(R.id.tvTIDetalleSolicitudMotivoRechazo);
        ivti = findViewById(R.id.ivTIDetalleSolicitudImageTI);
        tvModelo = findViewById(R.id.tvTIDetalleSolicitudCardModelo);
        tvMarca = findViewById(R.id.tvTIDetalleSolicitudCardMarca);
        tvCategoria = findViewById(R.id.tvTiDispCategoria);
        ivFotoEquipo = findViewById(R.id.ivTIDetalleSolicitudCardImage);
        ivCategoriaDisp = findViewById(R.id.ivTiDispCategoria);
        tvNombreCliente = findViewById(R.id.tvTIDetalleSolicitudNombreCliente);
        tvRolCliente = findViewById(R.id.tvTIDetalleSolicitudRolCliente);
        ivCliente = findViewById(R.id.ivTIDetalleSolicitudImageCliente);
        pbLoading = findViewById(R.id.pbTIDetalleSolicitudLoading);
        llButtons = findViewById(R.id.llTIDetalleSolicitudButtons);
        btnAccept = findViewById(R.id.btnTIDetalleSolicitudAceptarSoli);
        btnReject = findViewById(R.id.btnTIDetalleSolicitudRechazarSoli);
        nombreLugar = findViewById(R.id.tvTIDetalleSolicitudLugarRecojoNombre);
        btnDevuelto = findViewById(R.id.btnTIDetalleSoliDevolver);
        mapView = findViewById(R.id.mvTIDetalleSolicMap);
        mapView.onCreate(savedInstanceState);

        if(horaReservaNano!=null && horaReservaSec!=null){
            String fechaReserva = df.format(new Timestamp(horaReservaSec,horaReservaNano).toDate());
            tvFechaReserva.setText("Enviada "+fechaReserva);
        }

        tvEstado.setText(reservas.getEstado());
        tvMotivo.setText(reservas.getMotivoReserva());
        tvCurso.setText(reservas.getCurso());
        tvTiempoReserva.setText(reservas.getTiempoReserva() + " días");

        if(reservas.getOtros().isEmpty()){
            llOtros.setVisibility(View.GONE);
        }else{
            llOtros.setVisibility(View.VISIBLE);
            tvOtros.setText(reservas.getOtros());
        }

        ivDni.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(reservas.getDni()).placeholder(R.drawable.ic_image_placeholder_48).error(R.drawable.not_available).into(ivDni);
        listProgramas = reservas.getProgramas();

        for (String programa:listProgramas) {
            Chip chip = new Chip(TIDetalleSolicitudActivity.this);
            ChipDrawable drawable = ChipDrawable.createFromAttributes(TIDetalleSolicitudActivity.this,null,0, com.denzcoskun.imageslider.R.style.Widget_MaterialComponents_Chip_Entry);
            chip.setChipDrawable(drawable);
            chip.setCheckable(false);
            chip.setClickable(false);
            chip.setCloseIconVisible(false);
            chip.setPadding(60,10,60,10);
            chip.setText(programa);
            cgProgramas.addView(chip);
        }

        tvMarca.setText(reservas.getDevice().getMarca());
        tvModelo.setText(reservas.getDevice().getModelo());
        tvCategoria.setText(reservas.getDevice().getCategoria());

        switch (reservas.getDevice().getCategoria()){
            case "Laptop":
                ivCategoriaDisp.setImageResource(R.drawable.ic_laptop_green);
                break;
            case "Celular":
                ivCategoriaDisp.setImageResource(R.drawable.ic_celular_green);
                break;
            case "Monitor":
                ivCategoriaDisp.setImageResource(R.drawable.ic_monitor_green);
                break;
            case "Tablet":
                ivCategoriaDisp.setImageResource(R.drawable.ic_tablet_green);
                break;
            case "Otros":
                ivCategoriaDisp.setImageResource(R.drawable.ic_otros_green);
        }

        ivFotoEquipo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(reservas.getDevice().getFotoPrincipal()).placeholder(R.drawable.image_device_placeholder).into(ivFotoEquipo);

        tvNombreCliente.setText("De "+reservas.getClienteUser().getNombre());
        tvRolCliente.setText(reservas.getClienteUser().getRol());
        Glide.with(this).load(reservas.getClienteUser().getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivCliente);

        btnDevuelto.setOnClickListener(v -> {
            Map<String, Object> updates = new HashMap<>();
            Timestamp finReservaTimestamp = Timestamp.now();
            updates.put("horaFinReserva", finReservaTimestamp);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference reservaRef = db.collection("reservas").document(reservas.getKey());
            final DocumentReference deviceRef = db.collection("devices").document(reservas.getDevice().getUid());
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(deviceRef);
                    Device device = snapshot.toObject(Device.class);
                    Map<String, Object> deviceUpdates = new HashMap<>();
                    deviceUpdates.put("enPrestamo", device.getEnPrestamo()-1);
                    deviceUpdates.put("disponibles", device.getDisponibles()+1);
                    transaction.update(deviceRef, deviceUpdates);
                    transaction.update(reservaRef, updates);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(TIDetalleSolicitudActivity.this, "Se marcó el equipo como devuelto", Toast.LENGTH_SHORT).show();
                    btnDevuelto.setVisibility(View.GONE);
                    String fechafin= df.format(finReservaTimestamp.toDate());
                    tvTiempoReserva.setText(reservas.getTiempoReserva().toString() + " días - Finalizó " + fechafin);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("msg",e.getMessage());
                    Toast.makeText(TIDetalleSolicitudActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
                }
            });
        });

        if(!reservas.getEstado().equals("Pendiente de aprobación")){
            llResponseInfo.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.GONE);
            horaRespNano = (Integer) intent.getSerializableExtra("horaRespNano");
            horaRespSec = (Long) intent.getSerializableExtra("horaRespSec");
            reservas.setHoraRespuesta(new Timestamp(horaRespSec,horaRespNano));
            if(horaRespNano!=null && horaRespSec!=null){
                String fechaResp = df.format(reservas.getHoraRespuesta().toDate());
                tvFechaResponse.setText(fechaResp);
            }
            tvNombreTI.setText(reservas.getTiUser().getNombre());
            Glide.with(this).load(reservas.getTiUser().getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivti);
            switch (reservas.getEstado()){
                case "Solicitud rechazada":
                    llRejectInfo.setVisibility(View.VISIBLE);
                    llAcceptInfo.setVisibility(View.GONE);
                    tvEstado.setTextColor(getResources().getColor(R.color.red));
                    tvMotivoRechazo.setText(reservas.getMotivoRechazo());
                    break;
                case "Solicitud aceptada":
                    llAcceptInfo.setVisibility(View.VISIBLE);
                    llRejectInfo.setVisibility(View.GONE);
                    horaFinNano = (Integer) intent.getSerializableExtra("horaFinNano");
                    horaFinSec = (Long) intent.getSerializableExtra("horaFinSec");
                    if(horaFinNano==null && horaFinSec==null){
                        btnDevuelto.setVisibility(View.VISIBLE);
                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                        cal.setTime(reservas.getHoraRespuesta().toDate());
                        cal.add(Calendar.DATE, reservas.getTiempoReserva());
                        String fechaFin = df.format(cal.getTime());
                        tvTiempoReserva.setText(reservas.getTiempoReserva().toString() + " días - Finaliza " + fechaFin);
                    }else{
                        btnDevuelto.setVisibility(View.GONE);
                        String fechafin= df.format(new Timestamp(horaFinSec,horaFinNano).toDate());
                        tvTiempoReserva.setText(reservas.getTiempoReserva().toString() + " días - Finalizó " + fechafin);
                    }
                    tvEstado.setTextColor(getResources().getColor(R.color.green_main));
                    LatLng coord = new LatLng(latitude,longitud);
                    if(latitude != null && longitud != null){
                        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.OUTDOORS, style -> {
                            this.mapboxMap = mapboxMap;
                            mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitud)).setTitle(reservas.getNombreLugarRecojo()));
                            symbolManager = new SymbolManager(mapView, mapboxMap, style, null);
                            SymbolOptions symbolOptions = new SymbolOptions()
                                    .withLatLng(coord)
                                    .withIconImage(ICON_ID)
                                    .withIconSize(0.5f);
                            symbolManager.create(symbolOptions);
                            mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude,longitud)).zoom(15).build());
                        }));
                    }
                    nombreLugar.setText(reservas.getNombreLugarRecojo());
                    break;
            }

        }else{
            tvEstado.setTextColor(getResources().getColor(R.color.yellow));
            llResponseInfo.setVisibility(View.GONE);
            llAcceptInfo.setVisibility(View.GONE);
            llRejectInfo.setVisibility(View.GONE);
            llButtons.setVisibility(View.VISIBLE);
            btnAccept.setOnClickListener(view -> {
                Intent reservasIntent = new Intent(view.getContext(), TIAcceptSolicitudActivity.class);
                reservasIntent.putExtra("reservas",reservas);
                acceptlauncher.launch(reservasIntent);
            });
            btnReject.setOnClickListener(view -> {
                Intent reservasIntent = new Intent(view.getContext(), TIRejectSolicitudActivity.class);
                reservasIntent.putExtra("reservas",reservas);
                rejectlauncher.launch(reservasIntent);
            });
        }
    }

    ActivityResultLauncher<Intent> acceptlauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
            Intent data = result.getData();
            horaRespNano = data.getIntExtra("horaRespNano",0);
            horaRespSec = data.getLongExtra("horaRespSec",0);
            latitude = data.getDoubleExtra("lati", 0);
            longitud = data.getDoubleExtra("long",0);
            String lugarRecojo = data.getStringExtra("nombreLugarRecojo");
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            llResponseInfo.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.GONE);
            if(horaRespNano!=null && horaRespSec!=null){
                String fechaResp = df.format(new Timestamp(horaRespSec,horaRespNano).toDate());
                tvFechaResponse.setText(fechaResp);
            }
            tvNombreTI.setText(fUser.getDisplayName());
            Glide.with(this).load(fUser.getPhotoUrl()).placeholder(R.drawable.avatar_placeholder).into(ivti);

            llAcceptInfo.setVisibility(View.VISIBLE);
            llRejectInfo.setVisibility(View.GONE);
            btnDevuelto.setVisibility(View.VISIBLE);
            LocalDateTime localDate = LocalDateTime.now().plusDays(reservas.getTiempoReserva());
            Date date = new Date(localDate.atZone(ZoneId.of("America/New_York")).toEpochSecond() * 1000);
            String fechaFin = df.format(date);
            tvTiempoReserva.setText(reservas.getTiempoReserva().toString() + " días - Finaliza " + fechaFin);

            tvEstado.setText("Solicitud aceptada");
            tvEstado.setTextColor(getResources().getColor(R.color.green_main));

            LatLng coord = new LatLng(latitude,longitud);
            if(latitude != null && longitud != null){
                mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.OUTDOORS, style -> {
                    this.mapboxMap = mapboxMap;
                    mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitud)).setTitle(lugarRecojo));
                    symbolManager = new SymbolManager(mapView, mapboxMap, style, null);
                    SymbolOptions symbolOptions = new SymbolOptions()
                            .withLatLng(coord)
                            .withIconImage(ICON_ID)
                            .withIconSize(0.5f);
                    symbolManager.create(symbolOptions);
                    mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude,longitud)).zoom(15).build());
                }));
            }
            nombreLugar.setText(lugarRecojo);
        });

    ActivityResultLauncher<Intent> rejectlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                Intent data = result.getData();
                horaRespNano = data.getIntExtra("horaRespNano",0);
                horaRespSec = data.getLongExtra("horaRespSec",0);
                String motivoRechazo = data.getStringExtra("motivoRechazo");
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                llResponseInfo.setVisibility(View.VISIBLE);
                llButtons.setVisibility(View.GONE);
                if(horaRespNano!=null && horaRespSec!=null){
                    String fechaResp = df.format(new Timestamp(horaRespSec,horaRespNano).toDate());
                    tvFechaResponse.setText(fechaResp);
                }
                tvNombreTI.setText(fUser.getDisplayName());
                Glide.with(this).load(fUser.getPhotoUrl()).placeholder(R.drawable.avatar_placeholder).into(ivti);

                llRejectInfo.setVisibility(View.VISIBLE);
                llAcceptInfo.setVisibility(View.GONE);
                tvEstado.setText("Solicitud rechazada");
                tvEstado.setTextColor(getResources().getColor(R.color.red));
                tvMotivoRechazo.setText(motivoRechazo);
            });

    public void backButton(View view){
        onBackPressed();
    }

}