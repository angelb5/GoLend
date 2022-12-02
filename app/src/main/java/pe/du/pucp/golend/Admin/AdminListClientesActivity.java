package pe.du.pucp.golend.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pe.du.pucp.golend.Adapters.UsersClienteAdapter;
import pe.du.pucp.golend.Adapters.UsersTIAdapter;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class AdminListClientesActivity extends AppCompatActivity {

    RecyclerView rvClientes;
    PagingConfig config = new PagingConfig(5,3,true);
    FirestorePagingOptions<User> options;
    UsersClienteAdapter usersClienteAdapter;
    ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_clientes);

        shimmerFrameLayout = findViewById(R.id.shimmerAdminListClientes);
        rvClientes = findViewById(R.id.rvAdminListClientes);

        shimmerFrameLayout.startShimmerAnimation();
        Query tiQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","Cliente");
        options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(tiQuery, config, new SnapshotParser<User>() {
                    @NonNull
                    @Override
                    public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        User user = snapshot.toObject(User.class);
                        if (user !=null){
                            user.setUid(snapshot.getId());
                            return user;
                        }
                        return new User();
                    }
                })
                .build();
        usersClienteAdapter = new UsersClienteAdapter(options);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvClientes.setLayoutManager(layoutManager);
        rvClientes.setAdapter(usersClienteAdapter);

        usersClienteAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                if (usersClienteAdapter.getItemCount()>0){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.removeAllViews();
                    usersClienteAdapter.removeLoadStateListener(this);
                }
                return null;
            }
        });
    }

    public void backButton(View view) { onBackPressed(); }
}