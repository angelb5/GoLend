package pe.du.pucp.golend.Anonymus;


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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pe.du.pucp.golend.Adapters.ImageSelectorAdapter;
import pe.du.pucp.golend.Decorations.ImageSelectorMargin;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.ScreenMessage;
import pe.du.pucp.golend.ScreenMessageActivity;

public class RegisterActivity extends AppCompatActivity {

    final int IMAGE_SELECTOR_COLUMNS = 3;
    final int IMAGE_SELECTOR_MARGIN = 8;
    final List<Integer> ROLE_IMAGES =  Arrays.asList(R.drawable.role_student, R.drawable.role_administrator, R.drawable.role_teacher);
    final List<Integer> ROLE_TEXTS = Arrays.asList(R.string.role_student, R.string.role_administrator, R.string.role_teacher);
    final List<String> ROLE_AVATAR_URLS = Arrays.asList("https://firebasestorage.googleapis.com/v0/b/golend-e961f.appspot.com/o/avatars%2Frole_student.png?alt=media&token=fc83e656-00d2-4e16-aaaf-449778d7715d",
            "https://firebasestorage.googleapis.com/v0/b/golend-e961f.appspot.com/o/avatars%2Frole_administrator.png?alt=media&token=d3e01332-feec-4f3f-9253-e8b9e6f395ed",
            "https://firebasestorage.googleapis.com/v0/b/golend-e961f.appspot.com/o/avatars%2Frole_teacher.png?alt=media&token=77923516-fe28-4835-a0e0-676f73f1c0ef");
    RecyclerView roleSelector;
    FirebaseAuth firebaseAuth;
    CollectionReference usersRef;
    EditText etNombre;
    EditText etCorreo;
    EditText etCodigo;
    EditText etContrasena;
    ProgressBar progressBar;
    Button btnLogin;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Setea el selector de roles
        roleSelector = findViewById(R.id.rvRegisterImageSelector);
        //Setea los EditText, ProgressBar y Button
        etNombre = findViewById(R.id.etRegisterNombre);
        etCorreo = findViewById(R.id.etRegisterCorreo);
        etCodigo = findViewById(R.id.etRegisterCodigo);
        etContrasena = findViewById(R.id.etRegisterContrasena);
        progressBar = findViewById(R.id.pbRegister);
        btnLogin = findViewById(R.id.btnRegisterGoToLogin);
        btnRegistrar = findViewById(R.id.btnRegisterRegistrar);


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
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    public void showHidePass(View view){
        if(etContrasena.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_disabled);
            //Show Password
            etContrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_open);
            //Hide Password
            etContrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void registrarUsuario(View view){
        boolean isInvalid = false;
        int selectedRole = ((ImageSelectorAdapter) roleSelector.getAdapter()).getSelectedItem();
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String rol = getResources().getString(ROLE_TEXTS.get(selectedRole));
        String avatarUrl = ROLE_AVATAR_URLS.get(selectedRole);

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

        if(contrasena.length()<6){
            etContrasena.setError("Debe contener al menos 6 caracteres");
            etContrasena.requestFocus();
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
            firebaseAuth.fetchSignInMethodsForEmail(correo).addOnCompleteListener(signInMethodQueryResult -> {
                if(!Objects.requireNonNull(signInMethodQueryResult.getResult().getSignInMethods()).isEmpty()){
                    ocultarCargando();
                    etCorreo.setError("Ya existe una cuenta con este correo");
                    etCorreo.requestFocus();
                    return;
                };
                User user = new User(nombre,correo,codigo,rol,avatarUrl,"Cliente");
                crearUsuario(user, contrasena);
            });
        }).addOnFailureListener(e -> ocultarCargando());


    }

    public void crearUsuario(User user, String contrasena){
        firebaseAuth.createUserWithEmailAndPassword(user.getCorreo(),contrasena)
                .addOnSuccessListener(authResult -> actualizarPerfilFireauth(authResult, user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Log.d("msg",e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(RegisterActivity.this, "Ocurrió un error al crear el perfil", Toast.LENGTH_LONG).show();
        });
    }

    public void anadirUsuarioFirestore(String uid, User user){
        usersRef.document(uid).set(user)
                .addOnSuccessListener(documentReference -> enviarCorreoVerificacion())
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Toast.makeText(RegisterActivity.this, "No se pudo completar el registro", Toast.LENGTH_LONG).show();
        });

    }

    public void enviarCorreoVerificacion(){
        assert firebaseAuth.getCurrentUser() !=null;
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(unused -> {
            firebaseAuth.signOut();
            ocultarCargando();
            ScreenMessage screenMessage = new ScreenMessage(R.drawable.circle_tick, R.drawable.screenmessage_successful,
                    "Te has registrado en GoLend",
                    "Verifica tu correo para poder usar la aplicación",
                    "Iniciar Sesión",
                    false,LoginActivity.class);
            Intent successIntent = new Intent(RegisterActivity.this, ScreenMessageActivity.class);
            successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            successIntent.putExtra("screenMessage", screenMessage);
            startActivity(successIntent);
            ActivityCompat.finishAffinity(RegisterActivity.this);
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(RegisterActivity.this, "No pudimos enviar el correo de confirmación", Toast.LENGTH_LONG).show();
        });

    };

    public void mostrarCargando(){
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setClickable(false);
        btnRegistrar.setClickable(false);
        roleSelector.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnLogin.setClickable(true);
        btnRegistrar.setClickable(true);
        roleSelector.setClickable(true);
    }

    public void goToLoginActivity(View view){
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        ActivityCompat.finishAffinity(RegisterActivity.this);
        finish();
    }
}