package pe.du.pucp.golend.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class AdminHomeActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    User user;
    BottomNavigationView bottomNavigationView;
    TextView tvNombre;
    ImageView ivPfp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        setBottomNavigationView();
        tvNombre = findViewById(R.id.tvAdminHomeNombre);
        ivPfp = findViewById(R.id.ivAdminHomePfp);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvNombre.setText(user.getNombre());
        Glide.with(this).load(user.getAvatarUrl()).into(ivPfp);
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdminHomeAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdminHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdminHome:
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
                        startActivity(new Intent(getApplicationContext(), AdminProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }
}