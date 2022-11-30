package pe.du.pucp.golend.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class AdminProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    EditText etUpdateNombre;
    EditText etUpdateCorreo;
    TextView tvcodigo;
    String  userName;
    String userCorreo;
    CollectionReference user;
    ImageView ivPfp;
    User userAdmin;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        setBottomNavigationView();
        etUpdateNombre = findViewById(R.id.etUpdateNombre);
        etUpdateCorreo = findViewById(R.id.etUpdateCorreo);
        tvcodigo = findViewById(R.id.tvcodigo);
        ivPfp = findViewById(R.id.imageView);
        user = FirebaseFirestore.getInstance().collection("users");

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userAdmin = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvcodigo.setText(userAdmin.getCodigo());

        userName= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        etUpdateNombre.setText(userName);
        etUpdateCorreo.setText(userCorreo);
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.avatar_placeholder).into(ivPfp);
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdminProfileAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdminProfile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdminHome:
                        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminGestionTi:
                        startActivity(new Intent(getApplicationContext(), AdminGestionTiActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminReport:
                        startActivity(new Intent(getApplicationContext(), AdminReportActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminProfile:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    public void actualizarPerfilAdmin(AuthResult authResult, User user){
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(String.valueOf(etUpdateNombre.getText())).build();

        authResult.getUser().updateProfile(userProfileChangeRequest);
    }

    public void cerrarSesionAdmin(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(AdminProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AdminProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(AdminProfileActivity.this);
        finish();
    }
}