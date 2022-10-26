package pe.du.pucp.golend.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import pe.du.pucp.golend.R;

public class OnboardingSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public OnboardingSliderAdapter(Context context) {
        this.context = context;
    }

    int images[] = {
            R.drawable.onboarding_welcome,
            R.drawable.onboarding_devices,
            R.drawable.onboarding_hand,
            R.drawable.onboarding_ourusers
    };

    int texts[] = {
            R.string.onboarding_welcome,
            R.string.onboarding_devices,
            R.string.onboarding_hand,
            R.string.onboarding_ourusers
    };

    @Override
    public int getCount() {
        return texts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_onboarding, container,false);

        TextView textViewTitle = view.findViewById(R.id.tvOnboardingSlideTitle);
        TextView textViewText = view.findViewById(R.id.tvOnboardingSlideText);
        ImageView imageView = view.findViewById(R.id.ivOnboardingSlideImage);

        if (position == 0){
            textViewTitle.setVisibility(View.VISIBLE);
            textViewText.setTextSize(20);
        }
        textViewText.setText(texts[position]);
        imageView.setImageResource(images[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
