package pe.du.pucp.golend.TI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pe.du.pucp.golend.Adapters.ClienteReservasAdapter;
import pe.du.pucp.golend.Adapters.TISolicitudesAdapter;
import pe.du.pucp.golend.Cliente.ClienteDetalleReservaActivity;
import pe.du.pucp.golend.Cliente.ClienteReservasActivity;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class TIListSolicitudesActivity extends AppCompatActivity {

    TextView tvTitle;
    TextView tvTxt;
    TextView tvEmpty;
    PagingConfig config = new PagingConfig(8,4,true);
    TISolicitudesAdapter tiSolicitudesAdapter;
    FirestorePagingOptions<Reservas> options;
    Query reservasQuery;
    RecyclerView rvListReservas;
    LinearLayout emptyView;
    String typeReservas;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_list_solicitudes);

        tvTitle = findViewById(R.id.tvTISolicitudPendienteTittle);
        tvTxt = findViewById(R.id.tvTIListSolicitudesTxt);
        rvListReservas = findViewById(R.id.rvTIListSolicitudes);
        emptyView = findViewById(R.id.llTIListSolicitudesEmptyView);
        tvEmpty = findViewById(R.id.tvTIReservasTxtEmpty);
        shimmerFrameLayout = findViewById(R.id.shimmerTiListSolicitudes);

        typeReservas = getIntent().getStringExtra("reservasType");

        switch (typeReservas){
            case "enCurso":
                tvTitle.setText(R.string.reservas_curso);
                tvTxt.setText(R.string.reservas_curso_txt_TI);
                tvEmpty.setText(R.string.reservas_curso_empty_TI);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereGreaterThan("horaFinReserva", Timestamp.now()).whereEqualTo("estado","Solicitud aprobada");
                break;
            case "pendientes":
                tvTitle.setText(R.string.solicitudes_pendientes);
                tvTxt.setText(R.string.solicitudes_pendientes_txt_TI);
                tvEmpty.setText(R.string.solicitudes_pendientes_empty_TI);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("estado","Pendiente de aprobación");
                break;
            case "pasadas":
                tvTitle.setText(R.string.historial_reservas);
                tvTxt.setText(R.string.reservas_pasadas_txt_TI);
                tvEmpty.setText(R.string.reservas_pasadas_empty_TI);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereLessThan("horaFinReserva",Timestamp.now()).whereEqualTo("estado","Solicitud aprobada");
                break;
            case "rechazadas":
                tvTitle.setText(R.string.solicitudes_rechazadas);
                tvTxt.setText(R.string.solicitudes_rechazadas_txt_TI);
                tvEmpty.setText(R.string.solicitudes_rechazadas_empty_TI);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("estado","Solicitud rechazada");
                break;
        }

        options = new FirestorePagingOptions.Builder<Reservas>()
                .setLifecycleOwner(this)
                .setQuery(reservasQuery, config, reservasSnapshotParser)
                .build();
       tiSolicitudesAdapter = new TISolicitudesAdapter(options, TIDetalleSolicitudActivity.class);

        rvListReservas.setLayoutManager(new LinearLayoutManager(TIListSolicitudesActivity.this));
        rvListReservas.setAdapter(tiSolicitudesAdapter);
        tiSolicitudesAdapter.startListening();

        tiSolicitudesAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                Log.d("msg", tiSolicitudesAdapter.getItemCount()+"");
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    rvListReservas.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (tiSolicitudesAdapter.getItemCount()>0){
                        rvListReservas.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }else{
                        rvListReservas.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                return null;
            }
        });

    }

    public SnapshotParser<Reservas> reservasSnapshotParser = new SnapshotParser<Reservas>() {
        @NonNull
        @Override
        public Reservas parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            Reservas reservas = snapshot.toObject(Reservas.class);
            Log.d("msg", reservas.getEstado());
            if (reservas !=null){
                reservas.setKey(snapshot.getId());
                return reservas;
            }
            return new Reservas();
        }
    };

    public void backButton(View view){
        onBackPressed();
    }

}