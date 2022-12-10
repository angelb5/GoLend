package pe.du.pucp.golend.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagingConfig;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.time.LocalTime;

import pe.du.pucp.golend.Adapters.EquiposPopularesAdapter;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class AdminHomeActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    User user;
    BottomNavigationView bottomNavigationView;
    PagingConfig config = new PagingConfig(8,4,true);
    FirestorePagingOptions<Device> options;
    TextView tvNombre;
    TextView tvSaludo;
    ImageView ivPfp;
    Query devicesQuery;
    TextView tvMarca;
    TextView tvPrestamos;
    ShapeableImageView ivDevice;
    ImageView ivCateg;
    TextView tvCateg;
    TextView tvModelo;
    TextView tvTotalPrestamo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        setBottomNavigationView();
        tvSaludo = findViewById(R.id.tvAdminHomeSaludo);
        tvNombre = findViewById(R.id.tvAdminHomeNombre);
        ivPfp = findViewById(R.id.ivAdminHomePfp);
        tvMarca = findViewById(R.id.tvAdminHomeMarca);
        tvModelo = findViewById(R.id.tvAdminHomeEquiposPopularNombre);
        tvPrestamos = findViewById(R.id.tvAdminHomeEquiposPopularPrestamos);
        ivCateg = findViewById(R.id.ivAdminHomeCategoria);
        tvCateg = findViewById(R.id.tvAdminHomeCategoria);
        ivDevice = findViewById(R.id.ivAdminHomeFotoDisp);
        tvTotalPrestamo = findViewById(R.id.tvAdminHomeCantPrestamos);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", ""), User.class);
        tvNombre.setText(user.getNombre());
        Glide.with(this).load(user.getAvatarUrl()).into(ivPfp);
        devicesQuery = FirebaseFirestore.getInstance().collection("devices").orderBy("enPrestamo", Query.Direction.DESCENDING).limit(1);
        devicesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Device device = queryDocumentSnapshots.iterator().next().toObject(Device.class);
                tvMarca.setText(device.getMarca());
                tvModelo.setText(device.getModelo());
                tvPrestamos.setText(device.getEnPrestamo() + "");
                tvCateg.setText(device.getCategoria());
                Glide.with(AdminHomeActivity.this).load(device.getFotosUrl().get(0)).into(ivDevice);
                switch (device.getSearchCategoria()) {
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
        FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("estado", "Solicitud aceptada").count().get(AggregateSource.SERVER).addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
            @Override
            public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                tvTotalPrestamo.setText(aggregateQuerySnapshot.getCount() + "");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarSaludo();
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

    public SnapshotParser<Device> deviceSnapshotParser = new SnapshotParser<Device>() {
        @NonNull
        @Override
        public Device parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            Device device = snapshot.toObject(Device.class);
            if (device !=null){
                device.setKey(snapshot.getId());
                return device;
            }
            return new Device();
        }
    };
}