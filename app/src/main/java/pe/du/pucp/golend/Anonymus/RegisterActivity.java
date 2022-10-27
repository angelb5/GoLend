package pe.du.pucp.golend.Anonymus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

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
    EditText etNombre;
    EditText etCorreo;
    EditText etCodigo;
    EditText etContrasena;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Setea el selector de roles
        roleSelector = findViewById(R.id.rvRegisterImageSelector);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ImageSelectorAdapter selectorAdapter = new ImageSelectorAdapter(this, ROLE_IMAGES, ROLE_TEXTS);
        selectorAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorAdapter.setSelectedItem(roleSelector.getChildAdapterPosition(view));
                selectorAdapter.notifyDataSetChanged();
            }
        });
        ImageSelectorMargin imageSelectorMargin = new ImageSelectorMargin(IMAGE_SELECTOR_COLUMNS,IMAGE_SELECTOR_MARGIN);

        roleSelector.setLayoutManager(layoutManager);
        roleSelector.setAdapter(selectorAdapter);
        roleSelector.addItemDecoration(imageSelectorMargin);
        //Setea los EditText y ProgressBar
        etNombre = findViewById(R.id.etRegisterNombre);
        etCorreo = findViewById(R.id.etRegisterCorreo);
        etCodigo = findViewById(R.id.etRegisterCodigo);
        etContrasena = findViewById(R.id.etRegisterContrasena);
        progressBar = findViewById(R.id.pbRegister);
        //Setea Firestore
        firebaseAuth = FirebaseAuth.getInstance();
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

        //TODO: Verificar que el correo o codigo no tengan cuentas asociadas

        if(isInvalid) return;

        Log.i("msg","El rol es "+rol);
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");
        firebaseAuth.createUserWithEmailAndPassword(correo,contrasena).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressBar.setVisibility(View.GONE);
                User user = new User(nombre,correo,codigo,rol,avatarUrl,"Cliente");
                usersRef.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "No se pudo completar el registro", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goToLoginActivity(View view){
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        ActivityCompat.finishAffinity(RegisterActivity.this);
        finish();
    }
}