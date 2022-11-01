package pe.du.pucp.golend.Anonymus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

import pe.du.pucp.golend.R;

public class LoginActivity extends AppCompatActivity {
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

        //Setea los EditText, ProgressBar y Button
        etCorreo = findViewById(R.id.etLoginCorreo);
        etContrasena = findViewById(R.id.etLoginContrasena);
        progressBar = findViewById(R.id.pbLogin);
        btnLogin = findViewById(R.id.btnLoginIngresar);
        btnForgotPassword = findViewById(R.id.btnLoginForgotPassword);
        btnRegister = findViewById(R.id.btnLoginGoToRegister);
        //Setea Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseFirestore.getInstance().collection("Users");
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
        }else if(Patterns.PHONE.matcher(correoOCodigo).matches() && correoOCodigo.length()==8){
            mostrarCargando();
            usersRef.whereEqualTo("codigo",correoOCodigo).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if(queryDocumentSnapshots.isEmpty()){
                    ocultarCargando();
                    etCorreo.setError("No existe una cuenta con este código");
                    etCorreo.requestFocus();
                    return;
                }
                String correoL = queryDocumentSnapshots.getDocuments().get(0).getString("correo");
                firebaseSignIn(correoL, contrasena);
            }).addOnFailureListener(e -> {
                ocultarCargando();
                Log.d("msg", "Error", e);
            });
        }else{
            etCorreo.setError("No es un correo o código válido");
            etCorreo.requestFocus();
        }
    }

    private void firebaseSignIn(String correo, String contrasena){
        firebaseAuth.signInWithEmailAndPassword(correo,contrasena).addOnSuccessListener(authResult -> {
            ocultarCargando();
            assert authResult.getUser()!=null;
            if(authResult.getUser().isEmailVerified()){
                //TODO: Ingresar a la respectiva pagina
                Toast.makeText(LoginActivity.this, "Hola "+authResult.getUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                Log.d("msg","Deben verificarse los permisos");
                firebaseAuth.signOut(); //Borrar luego
            }else{
                Intent intentNoVerificado = new Intent(LoginActivity.this, NonVerifiedActivity.class);
                startActivity(intentNoVerificado);
            }
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(LoginActivity.this, "Ocurrió un error", Toast.LENGTH_SHORT).show();
        });
    }

    public void goToRegisterActivity(View view){
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
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
}