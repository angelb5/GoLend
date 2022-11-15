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
import pe.du.pucp.golend.Helpers.BottomNavigationViewHelper;
import pe.du.pucp.golend.R;

public class TIProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiprofile);

        setBottomNavigationView();
    }

    public void tiLogout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent logoutIntent = new Intent(TIProfileActivity.this, LoginActivity.class);
        startActivity(logoutIntent);
        ActivityCompat.finishAffinity(TIProfileActivity.this);
        finish();
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuTiProfileAct);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
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