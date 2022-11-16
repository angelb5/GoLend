package pe.du.pucp.golend.Anonymus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import pe.du.pucp.golend.R;
import pe.du.pucp.golend.ScreenMessage;
import pe.du.pucp.golend.ScreenMessageActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText etCorreo;
    ProgressBar progressBar;
    Button btnSend;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        //Setea los EditText, ProgressBar y Button
        etCorreo = findViewById(R.id.etForgotPasswordCorreo);
        progressBar = findViewById(R.id.pbForgotPassword);
        btnSend = findViewById(R.id.btnForgotPasswordSend);
        btnRegister = findViewById(R.id.btnForgotPasswordGoToRegister);
        //Setea Firestore
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void sendForgotPasswordMail(View view){
        String correo = etCorreo.getText().toString().trim();

        if(correo.isEmpty()){
            etCorreo.setError("Debes ingresar tu correo");
            etCorreo.requestFocus();
            return;
        }

        if(Patterns.EMAIL_ADDRESS.matcher(correo).matches() && (correo.endsWith("pucp.edu.pe") || correo.endsWith("pucp.pe"))){
            mostrarCargando();
            firebaseSendMail(correo);
        }else{
            etCorreo.setError("No es un correo válido");
            etCorreo.requestFocus();
        }
    }

    public void firebaseSendMail(String correo){
        firebaseAuth.sendPasswordResetEmail(correo).addOnSuccessListener(unused -> {
            ocultarCargando();
            ScreenMessage screenMessage = new ScreenMessage(R.drawable.circle_tick, R.drawable.screenmessage_calm,
                    "Revisa tu bandeja de entrada",
                    "Enviamos un correo para que recuperes tu cuenta",
                    "Iniciar Sesión",
                    false,LoginActivity.class);
            Intent successIntent = new Intent(ForgotPasswordActivity.this, ScreenMessageActivity.class);
            successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            successIntent.putExtra("screenMessage", screenMessage);
            startActivity(successIntent);
            ActivityCompat.finishAffinity(ForgotPasswordActivity.this);
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            etCorreo.setError("Verifica que el correo sea correcto");
            etCorreo.requestFocus();
        });
    }

    public void goToRegisterActivity(View view){
        Intent registerIntent = new Intent(ForgotPasswordActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
        ActivityCompat.finishAffinity(ForgotPasswordActivity.this);
        finish();
    }

    public void backButton(View view){
        onBackPressed();
    }

    public void mostrarCargando(){
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setClickable(false);
        btnRegister.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnSend.setClickable(true);
        btnRegister.setClickable(true);
    }
}