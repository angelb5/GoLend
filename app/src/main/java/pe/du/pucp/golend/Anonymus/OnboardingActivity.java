package pe.du.pucp.golend.Anonymus;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import pe.du.pucp.golend.R;

public class OnboardingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
    }

    public void ShowHidePass(View view){
        EditText editPassword = findViewById(R.id.etPasswordOnboardingTest);
        if(editPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_disabled);
            //Show Password
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_open);
            //Hide Password
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}