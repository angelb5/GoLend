package pe.du.pucp.golend.TI;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.time.LocalTime;

import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class TIHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    TextView tvSaludo;
    TextView tvNombre;
    ImageView ivPfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_home);

        setBottomNavigationView();

        tvSaludo = findViewById(R.id.tvTIHomeSaludo);
        tvNombre = findViewById(R.id.tvTIHomeNombre);
        ivPfp = findViewById(R.id.ivTIHomePfp);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(sharedPreferences.getString("user",""), User.class);

        tvNombre.setText(user.getNombre());
        Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivPfp);

    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuTIHomeAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuTiHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuTiHome:
                        return true;
                    case R.id.bottomNavMenuTiSolicitud:
                        startActivity(new Intent(getApplicationContext(),TISolicitudActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuTiDevices:
                        startActivity(new Intent(getApplicationContext(),TIDevicesActivity.class));
                        overridePendingTransition(0, 0);
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

    public void mostrarSaludo(){
        String saludo = "Buenas noches,";
        LocalTime localTime = LocalTime.now();
        if(localTime.getHour()>=6 && localTime.getHour()<12){
            saludo = "Buenos días,";
        } else if(localTime.getHour() >= 12 && localTime.getHour()<19){
            saludo = "Buenas tardes, ";
        }
        tvSaludo.setText(saludo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarSaludo();
    }
}