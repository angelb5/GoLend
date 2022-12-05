package pe.du.pucp.golend.TI;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.navigation.NavigationBarView;
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

public class TIDevicesActivity extends AppCompatActivity {
    PagingConfig config = new PagingConfig(8,4,true);
    DeviceCardAdapter deviceCardAdapter;
    FirestorePagingOptions<Device> options;
    Query devicesQuery = FirebaseFirestore.getInstance().collection("devices");

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout emptyView;
    EditText etSearch;
    TextView tvFilters;

    private String categoryFilter = "";
    private String marcasFilter = "";
    private String searchText = "";

    private String getMarcasUrl;

    private ModalBottomSheetFilter modalBottomSheet = new ModalBottomSheetFilter();
    //Text Typing
    private Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 900;

    private Runnable searchDevices = new Runnable() {
        @Override
        public void run() {
            String searchTextFromEt = etSearch.getText().toString().trim();
            if(!searchTextFromEt.isEmpty()){
                searchText = searchTextFromEt;
                ejecutarQuery();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_devices);

        getMarcasUrl = getString(R.string.apigateway_ip)+"/api/goLend/marcas";
        setBottomNavigationView();

        recyclerView = findViewById(R.id.rvTiListDevices);
        shimmerFrameLayout = findViewById(R.id.shimmerTiListDevices);
        emptyView = findViewById(R.id.llTiListDevicesEmptyView);
        etSearch = findViewById(R.id.etTiListDevicesSearch);
        tvFilters = findViewById(R.id.tvTiListDevicesFilters);

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

        options = new FirestorePagingOptions.Builder<Device>()
                .setLifecycleOwner(this)
                .setQuery(devicesQuery, config, deviceSnapshotParser)
                .build();
        deviceCardAdapter = new DeviceCardAdapter(options, TIDetalleDispActivity.class);
        deviceCardAdapter.setCrudResultLauncher(crudResultLauncher);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(deviceCardAdapter);

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

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuTiDevicesAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuTiDevices);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),TIHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    public void backButton(View view){
        onBackPressed();
    }

    public void goToCreateDevice(View view){
        Intent createIntent = new Intent(TIDevicesActivity.this,TICreateDeviceActivity.class);
        crudResultLauncher.launch(createIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
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

    ActivityResultLauncher<Intent> crudResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("msg", "resultcode "+result.getResultCode());
                    if(deviceCardAdapter!=null && result.getResultCode() == RESULT_OK) deviceCardAdapter.refresh(); //solo se refrescara el listado si viene de crear un dispositivo
                }
            });
}