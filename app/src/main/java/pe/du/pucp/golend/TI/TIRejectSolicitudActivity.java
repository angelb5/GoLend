package pe.du.pucp.golend.TI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class TIRejectSolicitudActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    TextView tvMotivoRechazo;
    EditText etMotivoRechazo;
    Button btnReject;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_reject_solicitud);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        Reservas reservas = (Reservas) intent.getSerializableExtra("reservas");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> updates = new HashMap<>();
        etMotivoRechazo = findViewById(R.id.etTIRejectSolicitudMotivoRechazo);
        btnReject = findViewById(R.id.btnTIRejectSolicitudRechazarSoli);
        btnReject.setOnClickListener(v -> {
            updates.put("tiUser.avatarUrl",user.getPhotoUrl().toString());
            updates.put("tiUser.nombre",user.getDisplayName());
            updates.put("tiUser.uid",user.getUid());
            updates.put("estado","Solicitud rechazada");
            LocalDateTime localDate = LocalDateTime.now().plusDays(reservas.getTiempoReserva());
            Date date = new Date(localDate.atZone(ZoneId.of("America/New_York")).toEpochSecond() * 1000);
            updates.put("horaFinReserva",new Timestamp(date));
            updates.put("horaRespuesta",Timestamp.now());
            updates.put("motivoRechazo",etMotivoRechazo.getText());
            FirebaseFirestore.getInstance().collection("reservas").document(reservas.getKey()).update(updates).addOnSuccessListener(unused -> {
                Toast.makeText(TIRejectSolicitudActivity.this, "Se realizó la act con éxito", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e->{
                Log.d("msg",e.getMessage());
                Toast.makeText(TIRejectSolicitudActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
            });
        });
    }

    public void backButton(View view){
        onBackPressed();
    }
}