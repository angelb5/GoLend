package pe.du.pucp.golend.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.R;

public class ClienteProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    String  userName;
    String userCorreo;
    Uri userFoto;
    EditText nombre;
    EditText correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_profile);
        setBottomNavigationView();
        nombre =  findViewById(R.id.etUpdateNombre);
        correo = findViewById(R.id.etUpdateCorreo);
        userName= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userFoto = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        Toast.makeText(ClienteProfileActivity.this, userName, Toast.LENGTH_LONG).show();
        nombre.setText(userName);
        correo.setText(userCorreo);
    }

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

    public void actualizarPerfilCliente(View view){


    }

    public void cerrarSesionCliente(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(ClienteProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ClienteProfileActivity.this,LoginActivity.class));
    }

}