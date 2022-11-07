package pe.du.pucp.golend.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pe.du.pucp.golend.Adapters.ImageSelectorAdapter;
import pe.du.pucp.golend.Adapters.UsersTIAdapter;
import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Anonymus.RegisterActivity;
import pe.du.pucp.golend.Decorations.ImageSelectorMargin;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.TI.TIHomeActivity;

public class AdminGestionTiActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    RecyclerView rvUsersTi;
    PagingConfig config = new PagingConfig(5,3,true);
    FirestorePagingOptions<User> options;
    UsersTIAdapter usersTIAdapter;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_gestion_ti);

        setBottomNavigationView();
        shimmerFrameLayout = findViewById(R.id.shimmerAdminGestionTi);
        rvUsersTi = findViewById(R.id.rvAdminGestionTi);

        shimmerFrameLayout.startShimmerAnimation();
        Query tiQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","TI");
        options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(tiQuery, config, User.class)
                .build();
        usersTIAdapter = new UsersTIAdapter(options);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvUsersTi.setLayoutManager(layoutManager);
        rvUsersTi.setAdapter(usersTIAdapter);

        usersTIAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                if (usersTIAdapter.getItemCount()>0){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.removeAllViews();
                    usersTIAdapter.removeLoadStateListener(this);
                }
                return null;
            }
        });
    }


    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdminGestonTiAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdminGestionTi);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdminHome:
                        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminGestionTi:
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

    @Override
    protected void onStart() {
        super.onStart();
        usersTIAdapter.startListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        usersTIAdapter.refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersTIAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    public void goToCreateUserActivity(View view){
        Intent loginIntent = new Intent(AdminGestionTiActivity.this, AdminCreateUserTiActivity.class);
        startActivity(loginIntent);
    }
}