package pe.du.pucp.golend.TI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
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
import java.util.ArrayList;
import java.util.Locale;

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
    ImageView ivFotoEquipo;
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
    private ArrayList<String> listProgramas = new ArrayList<>();
    ProgressBar pbLoading;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;
    private Double latitude;
    private  Double longitud;

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

        Reservas reservas = (Reservas) intent.getSerializableExtra("reservas");
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

        if(horaReservaNano!=null && horaReservaSec!=null){
            String fechaReserva = df.format(new Timestamp(horaReservaSec,horaReservaNano).toDate());
            tvFechaReserva.setText("Enviada "+fechaReserva);
        }

        tvEstado.setText(reservas.getEstado());
        tvMotivo.setText(reservas.getMotivoReserva());
        tvCurso.setText(reservas.getCurso());
        tvTiempoReserva.setText(reservas.getTiempoReserva().toString());

        if(reservas.getOtros().isEmpty()){
            llOtros.setVisibility(View.GONE);
        }else{
            llOtros.setVisibility(View.VISIBLE);
            tvOtros.setText(reservas.getOtros());
        }

        ivDni.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(reservas.getDni()).placeholder(com.denzcoskun.imageslider.R.drawable.placeholder).into(ivDni);
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
        Glide.with(this).load(reservas.getDevice().getFotoPrincipal()).placeholder(com.denzcoskun.imageslider.R.drawable.placeholder).into(ivFotoEquipo);

        tvNombreCliente.setText(reservas.getClienteUser().getNombre());
        tvRolCliente.setText(reservas.getClienteUser().getRol());
        Glide.with(this).load(reservas.getClienteUser().getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivCliente);

        if(!reservas.getEstado().equals("Pendiente de aprobación")){
            llResponseInfo.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.GONE);
            horaRespNano = (Integer) intent.getSerializableExtra("horaRespNano");
            horaRespSec = (Long) intent.getSerializableExtra("horaRespSec");
            if(horaRespNano!=null && horaRespSec!=null){
                String fechaResp = df.format(new Timestamp(horaRespSec,horaRespNano).toDate());
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
                    tvEstado.setTextColor(getResources().getColor(R.color.green_main));
                    mapView = findViewById(R.id.mvTIDetalleSolicMap);
                    mapView.onCreate(savedInstanceState);
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
                view.getContext().startActivity(reservasIntent);
            });
            btnReject.setOnClickListener(view -> {
                Intent reservasIntent = new Intent(view.getContext(), TIRejectSolicitudActivity.class);
                reservasIntent.putExtra("reservas",reservas);
                view.getContext().startActivity(reservasIntent);
            });
        }
    }

    public void backButton(View view){
        onBackPressed();
    }

}