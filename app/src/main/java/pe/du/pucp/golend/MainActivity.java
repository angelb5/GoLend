package pe.du.pucp.golend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pe.du.pucp.golend.Anonymus.OnboardingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser  == null || currentUser.isAnonymous()){
            Intent intentAnonymus = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(intentAnonymus);
            finish();
        }
    }
}