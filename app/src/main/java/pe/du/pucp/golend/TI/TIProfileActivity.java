package pe.du.pucp.golend.TI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Anonymus.RegisterActivity;
import pe.du.pucp.golend.R;

public class TIProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiprofile);

        bottomNavigationView = findViewById(R.id.bottomNavMenuTiProfileAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuTiProfile);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //TODO: completar casos
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuTiHome:
                        startActivity(new Intent(getApplicationContext(),TIHomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bottomNavMenuTiSolicitud:
                        return true;
                    case R.id.bottomNavMenuTiDevices:
                        return true;
                    case R.id.bottomNavMenuTiProfile:
                        return true;
                }
                return false;
            }
        });
    }

    public void tiLogout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent logoutIntent = new Intent(TIProfileActivity.this, LoginActivity.class);
        startActivity(logoutIntent);
        ActivityCompat.finishAffinity(TIProfileActivity.this);
        finish();
    }
}