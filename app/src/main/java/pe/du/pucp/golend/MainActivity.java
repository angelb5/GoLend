package pe.du.pucp.golend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import java.util.Objects;

import pe.du.pucp.golend.Admin.AdminHomeActivity;
import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Anonymus.OnboardingActivity;
import pe.du.pucp.golend.Cliente.ClienteHomeActivity;
import pe.du.pucp.golend.Cliente.ClienteReservasActivity;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.TI.TIHomeActivity;

public class MainActivity extends AppCompatActivity {
    final String CHANNEL_ID = "CocoChannel";
    CollectionReference usersRef;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        usersRef = FirebaseFirestore.getInstance().collection("users");

        createNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser  == null || currentUser.isAnonymous()){
            Intent intentAnonymus = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(intentAnonymus);
            finish();
        }else{
            accesoEnBaseARol(currentUser);
        }
    }

    public void accesoEnBaseARol(FirebaseUser firebaseUser){
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        usersRef.document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().signOut();
                    Intent intentAnonymus = new Intent(MainActivity.this, OnboardingActivity.class);
                    startActivity(intentAnonymus);
                    finish();
                }
                Intent intentPermisos;
                Gson gson = new Gson();
                User user = documentSnapshot.toObject(User.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", gson.toJson(user));
                editor.apply();
                switch (Objects.requireNonNull(documentSnapshot.getString("permisos"))){
                    case "Cliente":
                        addListener();
                        intentPermisos  = new Intent(MainActivity.this, ClienteHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "Admin":
                        intentPermisos  = new Intent(MainActivity.this, AdminHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "TI":
                        intentPermisos  = new Intent(MainActivity.this, TIHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseAuth.getInstance().signOut();
                Intent intentAnonymus = new Intent(MainActivity.this, OnboardingActivity.class);
                startActivity(intentAnonymus);
                finish();
            }
        });;
    }

    public void addListener() {
        FirebaseFirestore.getInstance().collection("reservas")
            .whereEqualTo("clienteUser.uid", FirebaseAuth.getInstance().getUid())
            .whereGreaterThanOrEqualTo("horaRespuesta", Timestamp.now())
            .addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    if (e.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                        return;
                    }
                    Log.w("msg", "listen:error", e);
                    return;
                }
                int i = 1;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED){
                        Log.d("msg", dc.getDocument().toString());
                        String modelo = dc.getDocument().getString("device.modelo");
                        String estado = dc.getDocument().getString("estado");
                        if (estado == null || modelo == null) return;
                        String typeReservas;
                        String titulo;
                        String msg;
                        if (estado.equals("Solicitud rechazada")) {
                            titulo = "Tu solicitud ha sido rechazada";
                            msg = "No hemos podido aprobar tu solicitud de préstamo";
                            typeReservas = "rechazadas";
                            notificarSolicitud(typeReservas, titulo, msg, i);
                        } else if (estado.equals("Solicitud aceptada")) {
                            titulo = "Tu solicitud ha sido aprobada";
                            msg = "Se aprobó tu solicitud de préstamo para " +modelo;
                            typeReservas = "enCurso";
                            notificarSolicitud(typeReservas, titulo, msg, i);
                        }
                    }
                    i++;
                }

            });
    }

    public void createNotificationChannel(){
        NotificationChannel channelDefault = new NotificationChannel(CHANNEL_ID, "Actualizaciones de solicitud", NotificationManager.IMPORTANCE_HIGH);
        channelDefault.enableVibration(true);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channelDefault);
    }

    public void notificarSolicitud(String reservasType, String titulo, String mensajeStr, int id){
        Intent intent = new Intent(this, ClienteReservasActivity.class);
        intent.putExtra("reservasType", reservasType);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_solicitud_checked)
                .setOnlyAlertOnce(true)
                .setContentTitle(titulo)
                .setContentText(mensajeStr)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(id, builder.build());
    }
}