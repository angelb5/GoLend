package pe.du.pucp.golend.TI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import pe.du.pucp.golend.Cliente.ClienteReservasActivity;
import pe.du.pucp.golend.R;

public class TISolicitudActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_solicitud);

        setBottomNavigationView();
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuTiSolicitudAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuTiSolicitud);
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
                        return true;
                    case R.id.bottomNavMenuTiDevices:
                        startActivity(new Intent(getApplicationContext(),TIDevicesActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuTiProfile:
                        startActivity(new Intent(getApplicationContext(),TIProfileActivity.class));
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
        startActivity(new Intent(getApplicationContext(),TIHomeActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    public void reservasEnCurso(View view){
        Intent reservasIntent = new Intent(view.getContext(), TIListSolicitudesActivity.class);
        reservasIntent.putExtra("reservasType", "enCurso");
        view.getContext().startActivity(reservasIntent);
    }

    public void solicitudesPendientes(View view){
        Intent reservasIntent = new Intent(view.getContext(),TIListSolicitudesActivity.class);
        reservasIntent.putExtra("reservasType", "pendientes");
        view.getContext().startActivity(reservasIntent);
    }

    public void reservasPasadas(View view){
        Intent reservasIntent = new Intent(view.getContext(),TIListSolicitudesActivity.class);
        reservasIntent.putExtra("reservasType", "pasadas");
        view.getContext().startActivity(reservasIntent);
    }

    public void solicitudesRechazadas(View view){
        Intent reservasIntent = new Intent(view.getContext(),TIListSolicitudesActivity.class);
        reservasIntent.putExtra("reservasType", "rechazadas");
        view.getContext().startActivity(reservasIntent);
    }

}