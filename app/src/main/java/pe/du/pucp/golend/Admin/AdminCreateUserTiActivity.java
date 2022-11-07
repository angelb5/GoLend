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

    final int IMAGE_SELECTOR_COLUMNS = 3;
    final int IMAGE_SELECTOR_MARGIN = 8;
    final List<Integer> ROLE_IMAGES =  Arrays.asList(R.drawable.role_student, R.drawable.role_administrator, R.drawable.role_teacher);
    final List<Integer> ROLE_TEXTS = Arrays.asList(R.string.role_student, R.string.role_administrator, R.string.role_teacher);
    final List<String> ROLE_AVATAR_URLS = Arrays.asList("https://firebasestorage.googleapis.com/v0/b/golend-e961f.appspot.com/o/avatars%2Frole_student.png?alt=media&token=fc83e656-00d2-4e16-aaaf-449778d7715d",
            "https://firebasestorage.googleapis.com/v0/b/golend-e961f.appspot.com/o/avatars%2Frole_administrator.png?alt=media&token=d3e01332-feec-4f3f-9253-e8b9e6f395ed",
            "https://firebasestorage.googleapis.com/v0/b/golend-e961f.appspot.com/o/avatars%2Frole_teacher.png?alt=media&token=77923516-fe28-4835-a0e0-676f73f1c0ef");
    RecyclerView roleSelector;
    FirebaseAuth firebaseAuth;
    FirebaseAuth firebaseAuth2;
    CollectionReference usersRef;
    EditText etNombre;
    EditText etCorreo;
    EditText etCodigo;
    ProgressBar progressBar;
    Button btnRegistrar;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_user_ti);
        //Setea el selector de roles
        roleSelector = findViewById(R.id.rvCreateUserTiImageSelector);
        //Setea los EditText, ProgressBar y Button
        etNombre = findViewById(R.id.etCreateUserTiNombre);
        etCorreo = findViewById(R.id.etCreateUserTiCorreo);
        etCodigo = findViewById(R.id.etCreateUserTiCodigo);
        progressBar = findViewById(R.id.pbCreateUserTi);
        btnRegistrar = findViewById(R.id.btnCreateUserTiRegistrar);
        btnBack = findViewById(R.id.ibCreateUserTiBack);

        //Selector de roles
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ImageSelectorAdapter selectorAdapter = new ImageSelectorAdapter(this, ROLE_IMAGES, ROLE_TEXTS);
        selectorAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = roleSelector.getChildAdapterPosition(view);
                if (position>=0 && position<ROLE_IMAGES.size()){
                    selectorAdapter.setSelectedItem(position);
                    selectorAdapter.notifyDataSetChanged();
                }
            }
        });
        ImageSelectorMargin imageSelectorMargin = new ImageSelectorMargin(IMAGE_SELECTOR_COLUMNS,IMAGE_SELECTOR_MARGIN);

        roleSelector.setLayoutManager(layoutManager);
        roleSelector.setAdapter(selectorAdapter);
        roleSelector.addItemDecoration(imageSelectorMargin);

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
    }

    public void registrarUsuario(View view){
        boolean isInvalid = false;
        int selectedRole = ((ImageSelectorAdapter) roleSelector.getAdapter()).getSelectedItem();
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();
        String rol = getResources().getString(ROLE_TEXTS.get(selectedRole));
        String avatarUrl = ROLE_AVATAR_URLS.get(selectedRole);
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        String contrasena = new String(array, Charset.forName("UTF-8"));

        if(nombre.isEmpty()){
            etNombre.setError("No puede estar vacío");
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
        progressBar.setVisibility(View.VISIBLE);
        btnRegistrar.setClickable(false);
        btnBack.setClickable(false);
        roleSelector.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnRegistrar.setClickable(true);
        btnBack.setClickable(true);
        roleSelector.setClickable(true);
    }

    public void backButton(View view){
        onBackPressed();
    }

}