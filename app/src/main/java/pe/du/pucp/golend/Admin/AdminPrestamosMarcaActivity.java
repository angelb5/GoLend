package pe.du.pucp.golend.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pe.du.pucp.golend.Adapters.DeviceCardAdapter;
import pe.du.pucp.golend.Adapters.PrestamosMarcaAdapter;
import pe.du.pucp.golend.Dtos.PrestamosMarcasDto;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.TI.TIDetalleDispActivity;

public class AdminPrestamosMarcaActivity extends AppCompatActivity {

    RecyclerView rvReportesMarca;
    PrestamosMarcaAdapter prestamosMarcaAdapter;
    ShimmerFrameLayout shimmerFrameLayout;
    PrestamosMarcasDto prestamosMarcasDto;
    private List<PrestamosMarcasDto> prestamosList = new ArrayList<>();
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_prestamos_marca);

        rvReportesMarca = findViewById(R.id.rvReportesMarca);
        shimmerFrameLayout = findViewById(R.id.shimmerAdminPrestamosMarca);
        shimmerFrameLayout.startShimmerAnimation();
        gson = new Gson();
        getPrestamos();
        prestamosMarcaAdapter = new PrestamosMarcaAdapter(prestamosList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvReportesMarca.setLayoutManager(layoutManager);
        rvReportesMarca.setAdapter(prestamosMarcaAdapter);

    }


    public void getPrestamos() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://eureka-eli.brazilsouth.cloudapp.azure.com:8090/api/goLend/marcasPrestamo",
                response -> {
                    try {
                        JSONObject responseJson = new JSONObject(response);
                        prestamosList.clear();
                        JSONArray jsonArray = responseJson.getJSONArray("reporte");
                        if (jsonArray.length()==0){
                            Toast.makeText(AdminPrestamosMarcaActivity.this, "No se encontraron marcas para listar", Toast.LENGTH_SHORT).show();
                        }

                        for (int i=0; i<jsonArray.length(); i++) {
                            PrestamosMarcasDto prestamosMarcaDto = gson.fromJson(jsonArray.getString(i), PrestamosMarcasDto.class);
                            String marca = jsonArray.getJSONObject(i).getString("marca");
                            Integer prestamos = jsonArray.getJSONObject(i).getInt("prestamos");
                            prestamosMarcaDto.setMarca(marca);
                            prestamosMarcaDto.setPrestamos(prestamos);
                            prestamosList.add(prestamosMarcaDto);
                        }
                        prestamosMarcaAdapter.notifyDataSetChanged();
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.removeAllViews();
                        rvReportesMarca.smoothScrollToPosition(0);
                    } catch (JSONException e) {
                        Toast.makeText(AdminPrestamosMarcaActivity.this, "No se pudieron cargar los préstamos por marca", Toast.LENGTH_SHORT).show();
                        Log.d("msg", "error", e);
                    }
                },
                error -> {
                    Toast.makeText(AdminPrestamosMarcaActivity.this, "No se pudieron cargar los préstamos por marca", Toast.LENGTH_SHORT).show();
                    Log.e("msg","error", error);
                });
        requestQueue.add(stringRequest);
    }

    public void backButton(View view){
        onBackPressed();
    }

}