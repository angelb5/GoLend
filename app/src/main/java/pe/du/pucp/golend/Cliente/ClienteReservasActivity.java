package pe.du.pucp.golend.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pe.du.pucp.golend.Adapters.ClienteReservasAdapter;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.R;

public class ClienteReservasActivity extends AppCompatActivity {

    TextView tvTitle;
    TextView tvTxt;
    TextView tvEmpty;
    PagingConfig config = new PagingConfig(8,4,true);
    ClienteReservasAdapter reservasAdapter;
    FirestorePagingOptions<Reservas> options;
    Query reservasQuery;
    RecyclerView rvListReservas;
    LinearLayout emptyView;
    String typeReservas;
    ShimmerFrameLayout shimmerFrameLayout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_reservas);

        tvTitle = findViewById(R.id.tvClienteReservasTittle);
        tvTxt = findViewById(R.id.tvClienteReservasTxt);
        rvListReservas = findViewById(R.id.rvClienteListReservas);
        emptyView = findViewById(R.id.llTiListSolicitudesEmptyView);
        tvEmpty = findViewById(R.id.tvClienteReservasTxtEmpty);
        shimmerFrameLayout = findViewById(R.id.shimmerClienteListSolicitudes);

        typeReservas = getIntent().getStringExtra("reservasType");
        user = FirebaseAuth.getInstance().getCurrentUser();

        switch (typeReservas){
            case "enCurso":
                tvTitle.setText(R.string.reservas_curso);
                tvTxt.setText(R.string.reservas_curso_txt);
                tvEmpty.setText(R.string.reservas_curso_empty);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("clienteUser.uid",user.getUid()).whereEqualTo("estado","Solicitud aprobada").whereGreaterThan("horaFinReserva", Timestamp.now());
                break;
            case "pendientes":
                tvTitle.setText(R.string.solicitudes_pendientes);
                tvTxt.setText(R.string.solicitudes_pendientes_txt);
                tvEmpty.setText(R.string.solicitudes_pendientes_empty);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("clienteUser.uid",user.getUid()).whereEqualTo("estado","Pendiente de aprobación");
                break;
            case "pasadas":
                tvTitle.setText(R.string.reservas_pasadas);
                tvTxt.setText(R.string.reservas_pasadas_txt);
                tvEmpty.setText(R.string.reservas_pasadas_empty);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("clienteUser.uid",user.getUid()).whereEqualTo("estado","Solicitud aprobada").whereLessThan("horaFinReserva",Timestamp.now());
                break;
            case "rechazadas":
                tvTitle.setText(R.string.solicitudes_rechazadas);
                tvTxt.setText(R.string.solicitudes_rechazadas_txt);
                tvEmpty.setText(R.string.solicitudes_rechazadas_empty);
                reservasQuery = FirebaseFirestore.getInstance().collection("reservas").whereEqualTo("clienteUser.uid",user.getUid()).whereEqualTo("estado","Solicitud rechazada");
                break;
        }

        options = new FirestorePagingOptions.Builder<Reservas>()
                .setLifecycleOwner(this)
                .setQuery(reservasQuery, config, reservasSnapshotParser)
                .build();
        reservasAdapter = new ClienteReservasAdapter(options, ClienteDetalleReservaActivity.class);

        rvListReservas.setLayoutManager(new LinearLayoutManager(ClienteReservasActivity.this));
        rvListReservas.setAdapter(reservasAdapter);
        reservasAdapter.startListening();

        reservasAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                Log.d("msg", reservasAdapter.getItemCount()+"");
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    rvListReservas.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (reservasAdapter.getItemCount()>0){
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