package pe.du.pucp.golend.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import pe.du.pucp.golend.Adapters.ImageSelectorCategoryAdapter;
import pe.du.pucp.golend.Decorations.ImageSelectorCategoryMargin;
import pe.du.pucp.golend.Helpers.BottomNavigationViewHelper;
import pe.du.pucp.golend.R;

public class ClienteHomeActivity extends AppCompatActivity {

    final List<Integer> CATEGORY_IMAGES = Arrays.asList(R.drawable.laptops, R.drawable.celulares, R.drawable.tablet, R.drawable.monitor, R.drawable.otros);
    final List<Integer> CATEGORY_TEXTS = Arrays.asList(R.string.category_laptop, R.string.category_celular, R.string.category_tablet, R.string.category_monitor, R.string.category_otros);
    final int CATEGORY_SELECTOR_COLUMNS = CATEGORY_IMAGES.size();
    final int CATEGORY_SELECTOR_MARGIN = 16;
    final int CATEGORY_SELECTOR_MARGIN_HORIZONTAL = 40;
    BottomNavigationView bottomNavigationView;
    RecyclerView categorySelector;
    TextView tvSaludo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_home);

        setBottomNavigationView();
        categorySelector = findViewById(R.id.rvClienteHomeCategories);
        tvSaludo = findViewById(R.id.tvClienteHomeSaludo);

        tvSaludo.setText("Hola , "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"!");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ImageSelectorCategoryAdapter categoryAdapter = new ImageSelectorCategoryAdapter(this, CATEGORY_IMAGES, CATEGORY_TEXTS);
        categoryAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = categorySelector.getChildAdapterPosition(view);
                if (position>=0 && position<CATEGORY_IMAGES.size()){
                    Log.d("msg", getResources().getString(CATEGORY_TEXTS.get(position)));
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
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
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
}