package pe.du.pucp.golend.TI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import pe.du.pucp.golend.R;

public class TIHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tihome);

        bottomNavigationView = findViewById(R.id.bottomNavMenuTiHomeAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuTiHome);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //TODO: completar casos
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuTiHome:
                        return true;
                    case R.id.bottomNavMenuTiSolicitud:
                        return true;
                    case R.id.bottomNavMenuTiDevices:
                        return true;
                    case R.id.bottomNavMenuTiProfile:
                        startActivity(new Intent(getApplicationContext(),TIProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}