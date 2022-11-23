package pe.du.pucp.golend.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pe.du.pucp.golend.Adapters.ImageSelectorAdapter;
import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Anonymus.RegisterActivity;
import pe.du.pucp.golend.Decorations.ImageSelectorMargin;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.ScreenMessage;
import pe.du.pucp.golend.ScreenMessageActivity;

public class AdminCreateUserTiActivity extends AppCompatActivity {

    String[] AVATAR_URLS;
    FirebaseAuth firebaseAuth;
    FirebaseAuth firebaseAuth2;
    CollectionReference usersRef;
    EditText etNombre;
    EditText etCorreo;
    EditText etCodigo;
    ProgressBar progressBar;
    Button btnRegistrar;
    ImageButton btnBack;
    boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_user_ti);
        //Setea los EditText, ProgressBar y Button
        etNombre = findViewById(R.id.etCreateUserTiNombre);
        etCorreo = findViewById(R.id.etCreateUserTiCorreo);
        etCodigo = findViewById(R.id.etCreateUserTiCodigo);
        progressBar = findViewById(R.id.pbCreateUserTi);
        btnRegistrar = findViewById(R.id.btnCreateUserTiRegistrar);
        btnBack = findViewById(R.id.ibCreateUserTiBack);

        //Setea Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseOptions options = firebaseAuth.getApp().getOptions();
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), options, "Golend");
            firebaseAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            firebaseAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("Golend"));
        }
        usersRef = FirebaseFirestore.getInstance().collection("users");

        AVATAR_URLS = getResources().getStringArray(R.array.avatars);
    }

    public void registrarUsuario(View view){
        boolean isInvalid = false;
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();
        String rol = "";
        if(nombre.isEmpty()){
            etNombre.setError("No puede estar vacío");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(nombre.length()>30){
            etNombre.setError("No puede tener más de 30 caracteres");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches() || !(correo.endsWith("pucp.edu.pe") || correo.endsWith("pucp.pe"))){
            etCorreo.setError("Ingrese un correo válido");
            etCorreo.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.PHONE.matcher(codigo).matches() || codigo.length()!=8){
            etCodigo.setError("Ingrese un código válido");
            etCodigo.requestFocus();
            isInvalid = true;
        }

        if(isInvalid) return;
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        String avatarUrl = AVATAR_URLS[new Random().nextInt(AVATAR_URLS.length)];
        String contrasena = new String(array, Charset.forName("UTF-8"));

        //Verifica que el codigo y correo sean únicos
        mostrarCargando();
        usersRef.whereEqualTo("codigo",codigo).count().get(AggregateSource.SERVER).addOnSuccessListener(aggregateQuerySnapshot -> {
            if (aggregateQuerySnapshot.getCount()>0){
                ocultarCargando();
                etCodigo.setError("Ya existe una cuenta con este código");
                etCodigo.requestFocus();
                return;
            }
            firebaseAuth2.fetchSignInMethodsForEmail(correo).addOnCompleteListener(signInMethodQueryResult -> {
                if(!Objects.requireNonNull(signInMethodQueryResult.getResult().getSignInMethods()).isEmpty()){
                    ocultarCargando();
                    etCorreo.setError("Ya existe una cuenta con este correo");
                    etCorreo.requestFocus();
                    return;
                };
                User user = new User(nombre,correo,codigo,rol,avatarUrl,"TI");
                crearUsuario(user, contrasena);
            });
        }).addOnFailureListener(e -> ocultarCargando());
    }

    public void crearUsuario(User user, String contrasena){
        firebaseAuth2.createUserWithEmailAndPassword(user.getCorreo(),contrasena)
                .addOnSuccessListener(authResult -> actualizarPerfilFireauth(authResult, user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Log.d("msg",e.getMessage());
                    Toast.makeText(AdminCreateUserTiActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
                });
    };

    public void actualizarPerfilFireauth(AuthResult authResult, User user){
        assert authResult.getUser()!=null;
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getNombre()).setPhotoUri(Uri.parse(user.getAvatarUrl())).build();

        authResult.getUser().updateProfile(userProfileChangeRequest)
                .addOnSuccessListener(unused -> anadirUsuarioFirestore(authResult.getUser().getUid(),user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Toast.makeText(AdminCreateUserTiActivity.this, "Ocurrió un error al crear el perfil", Toast.LENGTH_LONG).show();
                });
    }

    public void anadirUsuarioFirestore(String uid, User user){
        usersRef.document(uid).set(user)
                .addOnSuccessListener(documentReference -> enviarCorreo(user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Toast.makeText(AdminCreateUserTiActivity.this, "No se pudo completar el registro", Toast.LENGTH_LONG).show();
                });

    }

    public void enviarCorreo(User user){
        assert firebaseAuth2.getCurrentUser() !=null;
        firebaseAuth2.sendPasswordResetEmail(user.getCorreo()).addOnSuccessListener(unused -> {
            firebaseAuth2.signOut();
            ocultarCargando();
            Toast.makeText(AdminCreateUserTiActivity.this, "Se ha creado el usuario "+user.getNombre(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(AdminCreateUserTiActivity.this, "No pudimos enviar el correo de confirmación", Toast.LENGTH_LONG).show();
        });

    };

    public void mostrarCargando(){
        isBusy = true;
        progressBar.setVisibility(View.VISIBLE);
        btnRegistrar.setClickable(false);
        btnBack.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        progressBar.setVisibility(View.GONE);
        btnRegistrar.setClickable(true);
        btnBack.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if (!isBusy){
            super.onBackPressed();
        }
    }

    public void backButton(View view){
        onBackPressed();
    }

}