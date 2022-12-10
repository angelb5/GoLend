package pe.du.pucp.golend.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pe.du.pucp.golend.Adapters.ClienteReservasAdapter;
import pe.du.pucp.golend.Adapters.DeviceCardAdapter;
import pe.du.pucp.golend.Adapters.EquiposPopularesAdapter;
import pe.du.pucp.golend.Adapters.PrestamosMarcaAdapter;
import pe.du.pucp.golend.Cliente.ClienteDetalleDispActivity;
import pe.du.pucp.golend.Cliente.ClienteReservasActivity;
import pe.du.pucp.golend.Dtos.PrestamosMarcasDto;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class AdminEquiposPrestadosActivity extends AppCompatActivity {

    RecyclerView rvEquiposPopulares;
    EquiposPopularesAdapter equiposPopularesAdapter;
    ShimmerFrameLayout shimmerFrameLayout;
    PagingConfig config = new PagingConfig(8,4,true);
    FirestorePagingOptions<Device> options;
    Query devicesQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_equipos_prestados);

        shimmerFrameLayout = findViewById(R.id.shimmerAdminEquiposPopulares);
        rvEquiposPopulares = findViewById(R.id.rvAAdminEquiposPopulares);

        devicesQuery = FirebaseFirestore.getInstance().collection("devices").orderBy("enPrestamo", Query.Direction.DESCENDING).limit(5);

        options = new FirestorePagingOptions.Builder<Device>()
                .setLifecycleOwner(this)
                .setQuery(devicesQuery, config, deviceSnapshotParser)
                .build();
        equiposPopularesAdapter = new EquiposPopularesAdapter(options);

        rvEquiposPopulares.setLayoutManager(new LinearLayoutManager(AdminEquiposPrestadosActivity.this));
        rvEquiposPopulares.setAdapter(equiposPopularesAdapter);

        equiposPopularesAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    rvEquiposPopulares.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (equiposPopularesAdapter.getItemCount()>0){
                        rvEquiposPopulares.setVisibility(View.VISIBLE);
                    }else{
                        rvEquiposPopulares.setVisibility(View.GONE);
                    }
                }
                return null;
            }
        });


    }

    public void backButton(View view){
        onBackPressed();
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