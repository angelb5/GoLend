package pe.du.pucp.golend.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import pe.du.pucp.golend.Adapters.ImageSelectorCategoryAdapter;
import pe.du.pucp.golend.Decorations.ImageSelectorCategoryMargin;
import pe.du.pucp.golend.R;

public class ClienteHomeActivity extends AppCompatActivity {

    final List<Integer> CATEGORY_IMAGES = Arrays.asList(R.drawable.category_laptop, R.drawable.category_celular, R.drawable.category_tablet, R.drawable.category_monitor, R.drawable.category_otros);
    final List<Integer> CATEGORY_TEXTS = Arrays.asList(R.string.category_laptop, R.string.category_celular, R.string.category_tablet, R.string.category_monitor, R.string.category_otros);
    final int CATEGORY_SELECTOR_COLUMNS = CATEGORY_IMAGES.size();
    final int CATEGORY_SELECTOR_MARGIN = 16;
    final int CATEGORY_SELECTOR_MARGIN_HORIZONTAL = 40;
    BottomNavigationView bottomNavigationView;
    RecyclerView categorySelector;
    TextView tvSaludo;
    TextView tvNombre;
    ImageView ivPfp;
    EditText etSearch;

    //Text Typing
    private Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 900;

    private Runnable searchDevices = new Runnable() {
        @Override
        public void run() {
            String searchTextFromEt = etSearch.getText().toString().trim();
            if(!searchTextFromEt.isEmpty()){
                Intent searchIntent = new Intent(ClienteHomeActivity.this, ClienteListDevicesActivity.class);
                searchIntent.putExtra("searchText", searchTextFromEt);
                startActivity(searchIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_cliente_home);

        setBottomNavigationView();
        categorySelector = findViewById(R.id.rvClienteHomeCategories);
        tvSaludo = findViewById(R.id.tvClienteHomeSaludo);
        tvNombre = findViewById(R.id.tvClienteHomeNombre);
        etSearch = findViewById(R.id.etClienteHomeSearch);
        ivPfp = findViewById(R.id.ivCLienteHomePfp);

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


        tvNombre.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).placeholder(R.drawable.avatar_placeholder).into(ivPfp);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ImageSelectorCategoryAdapter categoryAdapter = new ImageSelectorCategoryAdapter(this, CATEGORY_IMAGES, CATEGORY_TEXTS);
        categoryAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = categorySelector.getChildAdapterPosition(view);
                if (position>=0 && position<CATEGORY_IMAGES.size()){
                    Intent categoryIntent = new Intent(ClienteHomeActivity.this, ClienteListDevicesActivity.class);
                    categoryIntent.putExtra("categoryFilter", getString(CATEGORY_TEXTS.get(position)));
                    startActivity(categoryIntent);
                }
            }
        });
        ImageSelectorCategoryMargin categorySelectorMargin = new ImageSelectorCategoryMargin(CATEGORY_SELECTOR_COLUMNS,CATEGORY_SELECTOR_MARGIN, CATEGORY_SELECTOR_MARGIN_HORIZONTAL);

        categorySelector.setLayoutManager(layoutManager);
        categorySelector.setAdapter(categoryAdapter);
        categorySelector.addItemDecoration(categorySelectorMargin);
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuClienteHomeAct);
        bottomNavigationView.clearAnimation();
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuClienteHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuClienteHome:
                        return true;
                    case R.id.bottomNavMenuClienteSolicitud:
                        startActivity(new Intent(getApplicationContext(), ClienteSolicitudActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuClienteProfile:
                        startActivity(new Intent(getApplicationContext(), ClienteProfileActivity.class));
                        overridePendingTransition(0, 0);
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