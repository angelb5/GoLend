package pe.du.pucp.golend.Cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class ClienteDetalleReservaActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
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
    TextView tvNombreTI;
    TextView tvFechaResponse;
    TextView tvMotivoRechazo;
    TextView tvMarca;
    TextView tvModelo;
    TextView tvCategoria;
    ImageView ivCategoriaDisp;
    ImageView ivFotoEquipo;

    private ArrayList<String> listProgramas = new ArrayList<>();
    private ImageButton btnBack;
    ProgressBar pbLoading;
    boolean isBusy = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_detalle_reserva);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        Reservas reservas = (Reservas) intent.getSerializableExtra("reservas");

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
        tvNombreTI = findViewById(R.id.tvClienteDetalleReservaNombreTI);
        tvFechaResponse = findViewById(R.id.tvClienteDetalleReservaFechaResponseTI);
        tvMotivoRechazo = findViewById(R.id.tvClienteDetalleReservasMotivoRechazo);
        ivti = findViewById(R.id.ivClienteDetalleReservaImageTI);
        btnBack = findViewById(R.id.ibClienteDetalleReservasBack);
        pbLoading = findViewById(R.id.pbClienteDetalleReservaLoading);
        tvModelo = findViewById(R.id.tvClienteDetalleReservasCardModelo);
        tvMarca = findViewById(R.id.tvClienteDetalleReservasCardMarca);
        tvCategoria = findViewById(R.id.tvClienteDetalleReservasDispCategoria);
        ivFotoEquipo = findViewById(R.id.ivClienteDetalleReservasCardImage);
        ivCategoriaDisp = findViewById(R.id.ivClienteDetalleReservasDispCategoria);

        String fechaReserva = df.format(reservas.getHoraReserva().toDate());
        tvFechaReseva.setText(fechaReserva);
        tvEstado.setText(reservas.getEstado());
        tvMotivo.setText(reservas.getMotivoReserva());
        tvCurso.setText(reservas.getCurso());
        tvTiempoReserva.setText(reservas.getTiempoReserva());
        tvOtros.setText(reservas.getOtros());
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

        Glide.with(this).load(reservas.getDevice().getFotoPrincipal()).placeholder(com.denzcoskun.imageslider.R.drawable.placeholder).into(ivFotoEquipo);

        if(!reservas.getHoraRespuesta().toString().isEmpty()){
            llResponseInfo.setVisibility(View.VISIBLE);
            String fechaRespuesta = df.format(reservas.getHoraRespuesta().toDate());
            tvNombreTI.setText(reservas.getTiUser().getNombre());
            tvFechaResponse.setText(fechaRespuesta);
            Glide.with(this).load(reservas.getTiUser().getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivti);
            switch (reservas.getEstado()){
                case "Solicitud rechazada":
                    llRejectInfo.setVisibility(View.VISIBLE);
                    llAcceptInfo.setVisibility(View.GONE);
                    tvEstado.setTextColor(R.color.red);
                    tvMotivoRechazo.setText(reservas.getMotivoRechazo());
                    break;
                case "Solicitud aceptada":
                    llAcceptInfo.setVisibility(View.VISIBLE);
                    llRejectInfo.setVisibility(View.GONE);
                    tvEstado.setTextColor(R.color.green_main);
                    break;
            }

        }else{
            tvEstado.setTextColor(R.color.yellow);
            llResponseInfo.setVisibility(View.GONE);
            llAcceptInfo.setVisibility(View.GONE);
            llRejectInfo.setVisibility(View.GONE);
        }

    }

    public void backButton(View view){
        onBackPressed();
    }

}