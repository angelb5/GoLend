package pe.du.pucp.golend.Cliente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import pe.du.pucp.golend.Admin.AdminProfileActivity;
import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.ChooseAvatarActivity;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class ClienteProfileActivity extends AppCompatActivity {
    CollectionReference userRef;
    BottomNavigationView bottomNavigationView;
    String  userName;
    String userCorreo;
    Uri userFoto;
    EditText etNombre;
    EditText etCorreo;
    User userCliente;
    TextView tvCodigo;
    TextView tvNombre;
    ImageView ivCliente;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_profile);
        setBottomNavigationView();
        etNombre =  findViewById(R.id.etUpdateNombre);
        etCorreo = findViewById(R.id.etUpdateCorreo);
        tvCodigo = findViewById(R.id.tvClienteProfileCodigo);
        tvNombre = findViewById(R.id.tvClienteProfileNombre);
        ivCliente = findViewById(R.id.ivClienteProfilePfp);
        userRef = FirebaseFirestore.getInstance().collection("users");
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userCliente = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvCodigo.setText(userCliente.getCodigo());
        tvNombre.setText(userCliente.getNombre());

        userName= userCliente.getNombre();
        userCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        etNombre.setText(userName);
        etCorreo.setText(userCorreo);
        Glide.with(this).load(userCliente.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivCliente);
    }

    public void actualizarPerfil(View view){

        String newNombre = etNombre.getText().toString().trim();
        String newCorreo = etCorreo.getText().toString().trim();

        boolean isInvalid = false;

        if(newNombre.isEmpty()){
            etNombre.setError("No puede estar vacío");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(newNombre.length()>30){
            etNombre.setError("No puede tener más de 30 caracteres");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(newCorreo).matches() || !(newCorreo.endsWith("pucp.edu.pe") || newCorreo.endsWith("pucp.pe"))){
            etCorreo.setError("Ingrese un correo válido");
            etCorreo.requestFocus();
            isInvalid = true;
        }
        Map<String, Object> updates = new HashMap<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (isInvalid || firebaseUser == null) return;

        if (!userCliente.getNombre().equals(newNombre)) {
            updates.put("nombre", newNombre);
            tvNombre.setText(newNombre);
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newNombre).build();
            firebaseUser.updateProfile(userProfileChangeRequest);
        }
        if(!userCliente.getCorreo().equals(newCorreo)) {
            updates.put("correo", newCorreo);
            firebaseUser.updateEmail(newCorreo);
        }

        userRef.document(firebaseUser.getUid()).update(updates).addOnSuccessListener(unused -> {
            Gson gson = new Gson();
            userCliente.setNombre(newNombre);
            userCliente.setCorreo(newCorreo);
            sharedPreferences.edit().putString("user", gson.toJson(userCliente)).apply();
        }).addOnFailureListener(e -> {
            Toast.makeText(ClienteProfileActivity.this, "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
        });
    }

    public void goToChooseAvatar(View view) {
        Intent caIntent = new Intent(ClienteProfileActivity.this, ChooseAvatarActivity.class);
        caIntent.putExtra("avatarUrl", userCliente.getAvatarUrl());
        launcherChooseAvatar.launch(caIntent);
    }

    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(ClienteProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit().remove("user").apply();
        startActivity(new Intent(ClienteProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(ClienteProfileActivity.this);
        finish();
    }

    ActivityResultLauncher<Intent> launcherChooseAvatar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    Gson gson = new Gson();
                    userCliente.setAvatarUrl(data.getStringExtra("avatarUrl"));
                    Glide.with(ClienteProfileActivity.this).load(userCliente.getAvatarUrl()).into(ivCliente);
                    sharedPreferences.edit().putString("user", gson.toJson(userCliente)).apply();
                }
            }
    );

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuClienteProfileAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuClienteProfile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuClienteHome:
                        startActivity(new Intent(getApplicationContext(), ClienteHomeActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuClienteSolicitud:
                        startActivity(new Intent(getApplicationContext(), ClienteSolicitudActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuClienteProfile:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ClienteHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

}