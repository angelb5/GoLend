package pe.du.pucp.golend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Objects;

import pe.du.pucp.golend.Admin.AdminHomeActivity;
import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Anonymus.OnboardingActivity;
import pe.du.pucp.golend.Cliente.ClienteHomeActivity;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.TI.TIHomeActivity;

public class MainActivity extends AppCompatActivity {

    CollectionReference usersRef;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser  == null || currentUser.isAnonymous()){
            Intent intentAnonymus = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(intentAnonymus);
            finish();
        }else{
            accesoEnBaseARol(currentUser);
        }
    }

    public void accesoEnBaseARol(FirebaseUser firebaseUser){
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        usersRef.document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().signOut();
                    Intent intentAnonymus = new Intent(MainActivity.this, OnboardingActivity.class);
                    startActivity(intentAnonymus);
                    finish();
                }
                Intent intentPermisos;
                Gson gson = new Gson();
                User user = documentSnapshot.toObject(User.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", gson.toJson(user));
                editor.apply();
                switch (Objects.requireNonNull(documentSnapshot.getString("permisos"))){
                    case "Cliente":
                        Toast.makeText(MainActivity.this, "Hola Cliente", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(MainActivity.this, ClienteHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "Admin":
                        Toast.makeText(MainActivity.this, "Hola Admin", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(MainActivity.this, AdminHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "TI":
                        Toast.makeText(MainActivity.this, "Hola TI", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(MainActivity.this, TIHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseAuth.getInstance().signOut();
                Intent intentAnonymus = new Intent(MainActivity.this, OnboardingActivity.class);
                startActivity(intentAnonymus);
                finish();
            }
        });;
    }
}