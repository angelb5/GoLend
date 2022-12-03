package pe.du.pucp.golend.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import pe.du.pucp.golend.R;

public class ClienteSolicitudActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_solicitud);

        setBottomNavigationView();
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuClienteSolicitudAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuClienteSolicitud);
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
                        return true;
                    case R.id.bottomNavMenuClienteProfile:
                        startActivity(new Intent(getApplicationContext(), ClienteProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
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


    public void reservasEnCurso(View view){
        Intent reservasIntent = new Intent(view.getContext(),ClienteReservasActivity.class);
        reservasIntent.putExtra("reservasType", "enCurso");
        view.getContext().startActivity(reservasIntent);
    }

    public void solicitudesPendientes(View view){
        Intent reservasIntent = new Intent(view.getContext(),ClienteReservasActivity.class);
        reservasIntent.putExtra("reservasType", "pendientes");
        view.getContext().startActivity(reservasIntent);
    }

    public void reservasPasadas(View view){
        Intent reservasIntent = new Intent(view.getContext(),ClienteReservasActivity.class);
        reservasIntent.putExtra("reservasType", "pasadas");
        view.getContext().startActivity(reservasIntent);
    }

    public void solicitudesRechazadas(View view){
        Intent reservasIntent = new Intent(view.getContext(),ClienteReservasActivity.class);
        reservasIntent.putExtra("reservasType", "rechazadas");
        view.getContext().startActivity(reservasIntent);
    }

}