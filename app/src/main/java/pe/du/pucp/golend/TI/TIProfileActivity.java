package pe.du.pucp.golend.TI;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class TIProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    CollectionReference userRef;
    EditText etUpdateNombre;
    EditText etUpdateCorreo;
    TextView tvCodigo;
    TextView tvNombre;
    ImageView ivPfp;
    User userTi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_profile);
        setBottomNavigationView();
        etUpdateNombre = findViewById(R.id.etUpdateNombre);
        etUpdateCorreo = findViewById(R.id.etUpdateCorreo);
        tvNombre = findViewById(R.id.tvTiProfileNombre);
        tvCodigo = findViewById(R.id.tvTiProfileCodigo);
        ivPfp = findViewById(R.id.ivTiProfilePfp);
        userRef = FirebaseFirestore.getInstance().collection("users");

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userTi = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvCodigo.setText(userTi.getCodigo());
        tvNombre.setText(userTi.getNombre());

        etUpdateNombre.setText(userTi.getNombre());
        etUpdateCorreo.setText(userTi.getCorreo());
        Glide.with(this).load(userTi.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivPfp);
    }

    public void actualizarPerfil(View view){

        String newNombre = etUpdateNombre.getText().toString().trim();
        String newCorreo = etUpdateCorreo.getText().toString().trim();

        boolean isInvalid = false;

        if(newNombre.isEmpty()){
            etUpdateNombre.setError("No puede estar vacío");
            etUpdateNombre.requestFocus();
            isInvalid = true;
        }

        if(newNombre.length()>30){
            etUpdateNombre.setError("No puede tener más de 30 caracteres");
            etUpdateNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(newCorreo).matches() || !(newCorreo.endsWith("pucp.edu.pe") || newCorreo.endsWith("pucp.pe"))){
            etUpdateCorreo.setError("Ingrese un correo válido");
            etUpdateCorreo.requestFocus();
            isInvalid = true;
        }
        Map<String, Object> updates = new HashMap<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (isInvalid || firebaseUser == null) return;

        if (!userTi.getNombre().equals(newNombre)) {
            updates.put("nombre", newNombre);
            tvNombre.setText(newNombre);
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newNombre).build();
            firebaseUser.updateProfile(userProfileChangeRequest);
        }
        if(!userTi.getCorreo().equals(newCorreo)) {
            updates.put("correo", newCorreo);
            firebaseUser.updateEmail(newCorreo);
        }

        userRef.document(firebaseUser.getUid()).update(updates).addOnSuccessListener(unused -> {
            Gson gson = new Gson();
            userTi.setNombre(newNombre);
            userTi.setCorreo(newCorreo);
            sharedPreferences.edit().putString("user", gson.toJson(userTi)).apply();
        }).addOnFailureListener(e -> {
            Toast.makeText(TIProfileActivity.this, "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
        });
    }

    public void goToChooseAvatar(View view) {
        Intent caIntent = new Intent(TIProfileActivity.this, ChooseAvatarActivity.class);
        caIntent.putExtra("avatarUrl", userTi.getAvatarUrl());
        launcherChooseAvatar.launch(caIntent);
    }

    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(TIProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit().remove("user").apply();
        startActivity(new Intent(TIProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(TIProfileActivity.this);
        finish();
    }

    ActivityResultLauncher<Intent> launcherChooseAvatar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    Gson gson = new Gson();
                    userTi.setAvatarUrl(data.getStringExtra("avatarUrl"));
                    Glide.with(TIProfileActivity.this).load(userTi.getAvatarUrl()).into(ivPfp);
                    sharedPreferences.edit().putString("user", gson.toJson(userTi)).apply();
                }
            }
    );

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuTiProfileAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuTiProfile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuTiHome:
                        startActivity(new Intent(getApplicationContext(),TIHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuTiSolicitud:
                        startActivity(new Intent(getApplicationContext(),TISolicitudActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuTiDevices:
                        startActivity(new Intent(getApplicationContext(),TIDevicesActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuTiProfile:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),TIHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }
}