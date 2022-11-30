package pe.du.pucp.golend.TI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class TIProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    EditText etUpdateNombre;
    EditText etUpdateCorreo;
    TextView tvTI;
    ImageView ivTI;
    String  userName;
    User userCliente;
    String userCorreo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_profile);
        setBottomNavigationView();
        etUpdateNombre = findViewById(R.id.etUpdateNombre);
        etUpdateCorreo = findViewById(R.id.etUpdateCorreo);
        tvTI = findViewById(R.id.tvcodigoTI);
        ivTI = findViewById(R.id.imageView);
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userCliente = gson.fromJson(sharedPreferences.getString("user",""), User.class);
        tvTI.setText(userCliente.getCodigo());
        userName= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        etUpdateNombre.setText(userName);
        etUpdateCorreo.setText(userCorreo);
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.avatar_placeholder).into(ivTI);
    }

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

    public void actualizarPerfilTI(View view){



    }

    public void cerrarSesionTI(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(TIProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(TIProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(TIProfileActivity.this);
        finish();
    }

}