package pe.du.pucp.golend.Cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class ClienteDetalleReservaActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    private static final String ICON_ID = "optionsMarker";
    TextView tvFechaReseva;
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
    LinearLayout llOtros;
    TextView tvNombreTI;
    TextView tvFechaResponse;
    TextView tvMotivoRechazo;
    TextView tvMarca;
    TextView tvModelo;
    TextView tvCategoria;
    ImageView ivCategoriaDisp;
    ShapeableImageView ivFotoEquipo;
    Integer horaReservaNano;
    Long horaReservaSec;
    Integer horaFinNano;
    Long horaFinSec;
    Integer horaRespNano;
    Long horaRespSec;
    TextView nombreLugar;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;
    private Double latitude;
    private  Double longitud;

    private ArrayList<String> listProgramas = new ArrayList<>();
    ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_cliente_detalle_reserva);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        Reservas reservas = (Reservas) intent.getSerializableExtra("reservas");
        latitude = (Double) intent.getSerializableExtra("lati");
        longitud = (Double) intent.getSerializableExtra("long");

        tvFechaReseva = findViewById(R.id.tvClienteDetalleReservasFechaReserva);
        tvEstado = findViewById(R.id.tvClienteDetalleReservasEstado);
        tvMotivo = findViewById(R.id.tvClienteDetalleReservasMotivo);
        tvCurso = findViewById(R.id.tvClienteDetalleReservasCurso);
        tvTiempoReserva = findViewById(R.id.tvClienteDetalleReservasTiempo);
        tvOtros = findViewById(R.id.tvClienteDetalleReservasOtros);
        ivDni = findViewById(R.id.ivClienteDetalleReservasDNI);
        cgProgramas = findViewById(R.id.cgClienteDetalleReservasProgramas);
        llResponseInfo = findViewById(R.id.llClienteDetalleReservasResponse);
        llAcceptInfo = findViewById(R.id.llClienteDetalleReservasLugarRecojo);
        llRejectInfo = findViewById(R.id.llClienteDetalleReservasMotivoRechazo);
        llOtros = findViewById(R.id.llClienteDetalleReservasOtros);
        tvNombreTI = findViewById(R.id.tvClienteDetalleReservaNombreTI);
        tvFechaResponse = findViewById(R.id.tvClienteDetalleReservaFechaResponseTI);
        tvMotivoRechazo = findViewById(R.id.tvClienteDetalleReservasMotivoRechazo);
        ivti = findViewById(R.id.ivClienteDetalleReservaImageTI);
        pbLoading = findViewById(R.id.pbClienteDetalleReservaLoading);
        tvModelo = findViewById(R.id.tvClienteDetalleReservasCardModelo);
        tvMarca = findViewById(R.id.tvClienteDetalleReservasCardMarca);
        tvCategoria = findViewById(R.id.tvClienteDetalleReservasDispCategoria);
        ivFotoEquipo = findViewById(R.id.ivClienteDetalleReservasCardImage);
        ivCategoriaDisp = findViewById(R.id.ivClienteDetalleReservasDispCategoria);
        nombreLugar = findViewById(R.id.tvClienteDetalleReservasLugarRecojoNombre);

        horaReservaNano = (Integer) intent.getSerializableExtra("horaReservaNano");
        horaReservaSec = (Long) intent.getSerializableExtra("horaReservaSec");

        if(horaReservaNano!=null && horaReservaSec!=null){
            String fechaReserva = df.format(new Timestamp(horaReservaSec,horaReservaNano).toDate());
            tvFechaReseva.setText("Enviada "+fechaReserva);
        }

        tvEstado.setText(reservas.getEstado());
        tvMotivo.setText(reservas.getMotivoReserva());
        tvCurso.setText(reservas.getCurso());

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
            Chip chip = new Chip(ClienteDetalleReservaActivity.this);
            ChipDrawable drawable = ChipDrawable.createFromAttributes(ClienteDetalleReservaActivity.this,null,0, com.denzcoskun.imageslider.R.style.Widget_MaterialComponents_Chip_Entry);
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

        if(!reservas.getEstado().equals("Pendiente de aprobación")){
            llResponseInfo.setVisibility(View.VISIBLE);
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
                    horaFinNano = (Integer) intent.getSerializableExtra("horaFinNano");
                    horaFinSec = (Long) intent.getSerializableExtra("horaFinSec");
                    if(horaFinNano==null && horaFinSec==null){
                        LocalDateTime localDate = LocalDateTime.now().plusDays(reservas.getTiempoReserva());
                        Date date = new Date(localDate.atZone(ZoneId.of("America/New_York")).toEpochSecond() * 1000);
                        String fechaFin = df.format(date);
                        tvTiempoReserva.setText(reservas.getTiempoReserva().toString() + " días - Finaliza " + fechaFin);
                    }else{
                        String fechafin= df.format(new Timestamp(horaFinSec,horaFinNano).toDate());
                        tvTiempoReserva.setText(reservas.getTiempoReserva().toString() + " días - Finalizó " + fechafin);
                    }
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
            tvTiempoReserva.setText(reservas.getTiempoReserva().toString() + " días");
            tvEstado.setTextColor(getResources().getColor(R.color.yellow));
            llResponseInfo.setVisibility(View.GONE);
            llAcceptInfo.setVisibility(View.GONE);
            llRejectInfo.setVisibility(View.GONE);
        }

    }

    public void backButton(View view){
        onBackPressed();
    }

}