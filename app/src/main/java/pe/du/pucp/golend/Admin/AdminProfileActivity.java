package pe.du.pucp.golend.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.R;

public class AdminProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    EditText etUpdateNombre;
    EditText etUpdateCorreo;
    TextView tvcodigo;
    String  userName;
    String userCorreo;
    String userCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        setBottomNavigationView();
        etUpdateNombre = findViewById(R.id.etUpdateNombre);
        etUpdateCorreo = findViewById(R.id.etUpdateCorreo);
        userName= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        etUpdateNombre.setText(userName);
        etUpdateCorreo.setText(userCorreo);
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

    public void actualizarPerfilAdmin(View view){

    }

    public void cerrarSesionAdmin(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(AdminProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AdminProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(AdminProfileActivity.this);
        finish();
    }
}