package pe.du.pucp.golend.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pe.du.pucp.golend.Adapters.DeviceCardAdapter;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Modals.ModalBottomSheetFilter;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.TI.TIDetalleDispActivity;

public class ClienteListDevicesActivity extends AppCompatActivity {

    PagingConfig config = new PagingConfig(8,4,true);
    DeviceCardAdapter deviceCardAdapter;
    FirestorePagingOptions<Device> options;
    Query devicesQuery;

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout emptyView;
    EditText etSearch;
    TextView tvFilters;

    private String categoryFilter = "";
    private String marcasFilter = "";
    private String searchText = "";

    private String getMarcasUrl = "https://us-central1-golend-e961f.cloudfunctions.net/getMarcas"; //TODO: Reemplazar por microservicio!!

    private ModalBottomSheetFilter modalBottomSheet = new ModalBottomSheetFilter();
    //Text Typing
    private Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 900;

    private Runnable searchDevices = new Runnable() {
        @Override
        public void run() {
            String searchTextFromEt = etSearch.getText().toString().trim();
            searchText = searchTextFromEt;
            ejecutarQuery();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_cliente_list_devices);

        recyclerView = findViewById(R.id.rvClienteListDevices);
        shimmerFrameLayout = findViewById(R.id.shimmerClienteListDevices);
        emptyView = findViewById(R.id.llClienteListDevicesEmptyView);
        etSearch = findViewById(R.id.etClienteListDevicesSearch);
        tvFilters = findViewById(R.id.tvClienteListDevicesFilters);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(searchDevices);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(searchDevices, DELAY);
            }
        });

        devicesQuery = FirebaseFirestore.getInstance().collection("devices");

        Intent intent = getIntent();
        if (intent!=null){
            if (intent.hasExtra("categoryFilter")) {
                categoryFilter = intent.getStringExtra("categoryFilter");
                modalBottomSheet.setCategoryFilter(categoryFilter);
                devicesQuery = devicesQuery.whereEqualTo("searchCategoria",categoryFilter);
                tvFilters.setText(categoryFilter + ", Todas las marcas");
            }
            if (intent.hasExtra("searchText")){
                searchText = intent.getStringExtra("searchText");
                devicesQuery = devicesQuery.whereArrayContains("searchKeywords", searchText);
                etSearch.setText(searchText);
            }
        }

        options = new FirestorePagingOptions.Builder<Device>()
                .setLifecycleOwner(this)
                .setQuery(devicesQuery, config, deviceSnapshotParser)
                .build();
        deviceCardAdapter = new DeviceCardAdapter(options, ClienteDetalleDispActivity.class);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(deviceCardAdapter);
        deviceCardAdapter.startListening();

        deviceCardAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (deviceCardAdapter.getItemCount()>0){
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                return null;
            }
        });


        //Llenando el filtro de marcas
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                getMarcasUrl,
                response -> {
                    try {
                        JSONObject responseJson = new JSONObject(response);
                        JSONArray jsonArray = responseJson.getJSONArray("marcas");
                        List<String> listMarcasJson = new ArrayList<>();
                        listMarcasJson.add("Todas las marcas");
                        for (int i=0; i<jsonArray.length(); i++) {
                            listMarcasJson.add( jsonArray.getString(i) );
                        }
                        modalBottomSheet.setMarcasList(listMarcasJson);
                    } catch (JSONException e) {
                        Log.d("msg", "error", e);
                    }
                },
                error -> Log.e("msg","error", error));
        requestQueue.add(stringRequest);
    }

    public void ejecutarQuery(){
        String categoria = "Todas las categorías";
        String marca = "Todas las marcas";
        devicesQuery = FirebaseFirestore.getInstance().collection("devices");
        if(!categoryFilter.isEmpty()){
            devicesQuery = devicesQuery.whereEqualTo("searchCategoria",categoryFilter);
            categoria = categoryFilter;
        }
        if(!marcasFilter.isEmpty()){
            devicesQuery = devicesQuery.whereEqualTo("searchMarca",marcasFilter.toLowerCase(Locale.ROOT));
            marca = marcasFilter;
        }
        if(!searchText.isEmpty()){
            devicesQuery = devicesQuery.whereArrayContains("searchKeywords", searchText);
        }

        tvFilters.setText(categoria + ", " + marca);

        options = new FirestorePagingOptions.Builder<Device>()
                .setLifecycleOwner(this)
                .setQuery(devicesQuery, config, deviceSnapshotParser)
                .build();
        deviceCardAdapter.updateOptions(options);
    }

    public void showFilters(View view){
        modalBottomSheet.show(getSupportFragmentManager(), modalBottomSheet.getTag());
    }

    public void setFilters(String categoryFilter, String marcasFilter) {
        Log.d("msg", marcasFilter);
        if(!this.categoryFilter.equals(categoryFilter) || !this.marcasFilter.equals(marcasFilter)){
            this.categoryFilter = categoryFilter;
            this.marcasFilter = marcasFilter;
            ejecutarQuery();
        }
    }

    public void backButton(View view){
        onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(deviceCardAdapter!=null) deviceCardAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(deviceCardAdapter!=null)  deviceCardAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(deviceCardAdapter!=null) deviceCardAdapter.stopListening();
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