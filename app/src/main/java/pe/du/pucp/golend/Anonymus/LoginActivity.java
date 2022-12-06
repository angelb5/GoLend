package pe.du.pucp.golend.Anonymus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.Objects;

import pe.du.pucp.golend.Admin.AdminHomeActivity;
import pe.du.pucp.golend.Cliente.ClienteHomeActivity;
import pe.du.pucp.golend.Cliente.ClienteReservasActivity;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.MainActivity;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.TI.TIHomeActivity;

public class LoginActivity extends AppCompatActivity {
    final String CHANNEL_ID = "CocoChannel";
    String APIGATEWWAY_IP;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    CollectionReference usersRef;
    EditText etCorreo;
    EditText etContrasena;
    ProgressBar progressBar;
    Button btnLogin;
    Button btnForgotPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        APIGATEWWAY_IP = getString(R.string.apigateway_ip);
        //Setea los EditText, ProgressBar y Button
        etCorreo = findViewById(R.id.etLoginCorreo);
        etContrasena = findViewById(R.id.etLoginContrasena);
        progressBar = findViewById(R.id.pbLogin);
        btnLogin = findViewById(R.id.btnLoginIngresar);
        btnForgotPassword = findViewById(R.id.btnLoginForgotPassword);
        btnRegister = findViewById(R.id.btnLoginGoToRegister);
        //Setea Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    public void ingresar(View view){
        Boolean isInvalid = false;
        String correo;
        String correoOCodigo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if(correoOCodigo.isEmpty()){
            etCorreo.setError("Debes ingresar tu correo o código");
            etCorreo.requestFocus();
            isInvalid = true;
        }

        if(contrasena.isEmpty()){
            etContrasena.setError("Debes ingresar tu contraseña");
            etContrasena.requestFocus();
            isInvalid = true;
        }

        if (isInvalid) return;

        if(Patterns.EMAIL_ADDRESS.matcher(correoOCodigo).matches() && (correoOCodigo.endsWith("pucp.edu.pe") || correoOCodigo.endsWith("pucp.pe"))){
            correo = correoOCodigo;
            mostrarCargando();
            firebaseSignIn(correo, contrasena);
        }else if (correoOCodigo.matches("\\d+") && correoOCodigo.length()==8) {
            mostrarCargando();
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = APIGATEWWAY_IP+"/api/goLend/user/"+correoOCodigo;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                try {
                    if (response.getBoolean("found")) {
                        String correo1 = response.getString("correo");
                        firebaseSignIn(correo1, contrasena);
                    } else {
                        ocultarCargando();
                        etCorreo.setError("No se ha podido iniciar sesión");
                        etCorreo.requestFocus();
                    }
                } catch (JSONException e) {
                    ocultarCargando();
                    e.printStackTrace();
                }
            }, error -> {
                ocultarCargando();
                Log.d("msg", "error", error);
                Toast.makeText(LoginActivity.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
            });

            queue.add(jsonObjectRequest);
        }
        else {
            etCorreo.setError("No es un correo o código válido");
            etCorreo.requestFocus();
        }
    }

    private void firebaseSignIn(String correo, String contrasena){
        firebaseAuth.signInWithEmailAndPassword(correo,contrasena).addOnSuccessListener(authResult -> {

            assert authResult.getUser()!=null;
            accesoEnBaseARol(authResult.getUser());

        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(LoginActivity.this, "No se ha podido iniciar sesión", Toast.LENGTH_SHORT).show();
        });
    }

    public void accesoEnBaseARol(FirebaseUser firebaseUser){
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        usersRef.document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ocultarCargando();
                if (!documentSnapshot.exists()) return;
                Intent intentPermisos;
                Gson gson = new Gson();
                User user = documentSnapshot.toObject(User.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", gson.toJson(user));
                editor.apply();
                switch (Objects.requireNonNull(documentSnapshot.getString("permisos"))){
                    case "Cliente":
                        if(firebaseUser.isEmailVerified()){
                            Toast.makeText(LoginActivity.this, "Hola Cliente", Toast.LENGTH_SHORT).show();
                            addListener();
                            intentPermisos  = new Intent(LoginActivity.this, ClienteHomeActivity.class);
                            startActivity(intentPermisos);
                            finish();
                        }else{
                            Intent intentNoVerificado = new Intent(LoginActivity.this, NonVerifiedActivity.class);
                            startActivity(intentNoVerificado);
                        }
                        break;
                    case "Admin":
                        Toast.makeText(LoginActivity.this, "Hola Admin", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(LoginActivity.this, AdminHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "TI":
                        Toast.makeText(LoginActivity.this, "Hola TI", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(LoginActivity.this, TIHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ocultarCargando();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(LoginActivity.this, "No se ha podido iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToRegisterActivity(View view){
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    public void goToForgotPasswordActivity(View view){
        Intent fpintent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
        startActivity(fpintent);
        overridePendingTransition(0,0);
    }

    public void showHidePass(View view){
        EditText editPassword = findViewById(R.id.etLoginContrasena);
        if(editPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_disabled);
            //Show Password
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_open);
            //Hide Password
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void mostrarCargando(){
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setClickable(false);
        btnRegister.setClickable(false);
        btnForgotPassword.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnLogin.setClickable(true);
        btnRegister.setClickable(true);
        btnForgotPassword.setClickable(true);
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
                        snapshots.getDocuments();
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
                                typeReservas = "enCurso";
                                notificarSolicitud(typeReservas, titulo, msg, i);
                            } else if (estado.equals("Solicitud aceptada")) {
                                titulo = "Tu solicitud ha sido aprobada";
                                msg = "Se aprobó tu solicitud de préstamo para " +modelo;
                                typeReservas = "rechazadas";
                                notificarSolicitud(typeReservas, titulo, msg, i);
                            }
                        }
                        i++;
                    }

                });
    }


    public void notificarSolicitud(String reservasType, String titulo, String mensajeStr, int id){
        Intent intent = new Intent(this, ClienteReservasActivity.class);
        intent.putExtra("reservasType", reservasType);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(LoginActivity.this, CHANNEL_ID)
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