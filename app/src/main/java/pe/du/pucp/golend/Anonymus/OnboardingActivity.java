package pe.du.pucp.golend.Anonymus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pe.du.pucp.golend.Adapters.OnboardingSliderAdapter;
import pe.du.pucp.golend.R;

public class OnboardingActivity extends AppCompatActivity {

    final int SLIDES = 4;
    final int SLIDE_TIME = 2500;
    ViewPager vpSlider;
    LinearLayout llDots;
    OnboardingSliderAdapter sliderAdapter;
    TextView[] tvDots;

    final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = vpSlider.getCurrentItem();
            if (currentItem<SLIDES-1){
                vpSlider.setCurrentItem(currentItem+1, true);
            }else{
                vpSlider.setCurrentItem(0, true);
            }

        }
    };
    final Handler slideHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_onboarding);

        vpSlider = findViewById(R.id.vpOnboardingSlider);
        llDots = findViewById(R.id.llOnboardingDots);

        sliderAdapter = new OnboardingSliderAdapter(this);
        vpSlider.setAdapter(sliderAdapter);
        addDots();
        changeDotColor(0);
        slideHandler.postDelayed(slideRunnable,SLIDE_TIME);
        vpSlider.addOnPageChangeListener(pageChangeListener);
    }

    private void addDots(){
        tvDots = new TextView[SLIDES];

        for (int i=0; i<tvDots.length; i++){
            tvDots[i] = new TextView(OnboardingActivity.this);
            tvDots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY));
            tvDots[i].setTextSize(42);
            tvDots[i].setTextColor(getColor(R.color.grey_slide_unselected));

            llDots.addView(tvDots[i]);
        }
    }

    private void changeDotColor(int position){
        for (int i=0;i<tvDots.length;i++){
            if(i==position){
                tvDots[i].setTextColor(getColor(R.color.green_main));
            }else{
                tvDots[i].setTextColor(getColor(R.color.grey_slide_unselected));
            }
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            changeDotColor(position);
            slideHandler.removeCallbacks(slideRunnable);
            slideHandler.postDelayed(slideRunnable,SLIDE_TIME);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void goToRegisterActivity(View view){
        Intent registerIntent = new Intent(OnboardingActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slideRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(slideRunnable,SLIDE_TIME);
    }
}