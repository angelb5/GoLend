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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.time.LocalTime;

import pe.du.pucp.golend.Admin.AdminHomeActivity;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class TIHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    TextView tvSaludo;
    TextView tvNombre;
    ImageView ivPfp;
    TextView tvMarca;
    TextView tvPrestamos;
    ShapeableImageView ivDevice;
    ImageView ivCateg;
    TextView tvCateg;
    TextView tvModelo;
    Query devicesQuery;
    ShapeableImageView ivFotitoTI;
    TextView tvTotalPrestamos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_home);

        setBottomNavigationView();

        tvSaludo = findViewById(R.id.tvTIHomeSaludo);
        tvNombre = findViewById(R.id.tvTIHomeNombre);
        ivPfp = findViewById(R.id.ivTIHomePfp);
        tvMarca = findViewById(R.id.tvTIHomeMarca);
        tvModelo = findViewById(R.id.tvTIHomeEquiposPopularNombre);
        tvPrestamos = findViewById(R.id.tvTIHomeEquiposPopularPrestamos);
        ivCateg = findViewById(R.id.ivTIHomeCategoria);
        tvCateg = findViewById(R.id.tvTIHomeCategoria);
        ivDevice= findViewById(R.id.ivTIHomeFotoDisp);
        ivFotitoTI = findViewById(R.id.ivTIHomeFoto);
        tvTotalPrestamos = findViewById(R.id.tvTIHomeCantPrestamos);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(sharedPreferences.getString("user",""), User.class);
        FirebaseUser userG = FirebaseAuth.getInstance().getCurrentUser();

        tvNombre.setText(user.getNombre());
        Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivPfp);
        Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivFotitoTI);
        devicesQuery = FirebaseFirestore.getInstance().collection("devices").orderBy("enPrestamo", Query.Direction.DESCENDING).limit(1);
        devicesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Device device = queryDocumentSnapshots.iterator().next().toObject(Device.class);
                tvMarca.setText(device.getMarca());
                tvModelo.setText(device.getModelo());
                tvPrestamos.setText(device.getEnPrestamo()+"");
                tvCateg.setText(device.getCategoria());
                Glide.with(TIHomeActivity.this).load(device.getFotosUrl().get(0)).into(ivDevice);
                switch (device.getSearchCategoria()){
                    case "Laptop":
                        ivCateg.setImageResource(R.drawable.ic_laptop_green);
                        break;
                    case "Celular":
                        ivCateg.setImageResource(R.drawable.ic_celular_green);
                        break;
                    case "Monitor":
                        ivCateg.setImageResource(R.drawable.ic_monitor_green);
                        break;
                    case "Tablet":
                        ivCateg.setImageResource(R.drawable.ic_tablet_green);
                        break;
                    case "Otros":
                        ivCateg.setImageResource(R.drawable.ic_otros_green);
                }
            }
        });
        FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("tiUser.uid", userG.getUid()).whereEqualTo("estado", "Solicitud aceptada").count().get(AggregateSource.SERVER).addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
            @Override
            public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
               tvTotalPrestamos.setText(aggregateQuerySnapshot.getCount() + "");
            }
        });

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