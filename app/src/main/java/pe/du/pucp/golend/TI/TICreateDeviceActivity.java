package pe.du.pucp.golend.TI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

import pe.du.pucp.golend.Adapters.ImageSelectorAdapter;
import pe.du.pucp.golend.Decorations.ImageSelectorMargin;
import pe.du.pucp.golend.R;

public class TICreateDeviceActivity extends AppCompatActivity {
    final int IMAGE_SELECTOR_MARGIN = 8;
    final List<Integer> CATEGORY_IMAGES =  Arrays.asList(R.drawable.category_laptop_circle, R.drawable.category_celular_circle, R.drawable.category_tablet_circle, R.drawable.category_monitor_circle, R.drawable.category_otros_circle);
    final List<Integer> CATEGORY_TEXTS = Arrays.asList(R.string.category_laptop, R.string.category_celular, R.string.category_tablet, R.string.category_monitor, R.string.category_otros);
    RecyclerView categorySelector;
    EditText etOtros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_create_device);

        categorySelector = findViewById(R.id.rvCreateDeviceCategorySelector);
        etOtros = findViewById(R.id.etCreateDeviceOtrosCategoria);

        ImageSelectorAdapter selectorAdapter = new ImageSelectorAdapter(this, CATEGORY_IMAGES, CATEGORY_TEXTS);
        selectorAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = categorySelector.getChildAdapterPosition(view);
                if (position>=0 && position<CATEGORY_IMAGES.size()){
                    selectorAdapter.setSelectedItem(position);
                    selectorAdapter.notifyDataSetChanged();
                    //Caso otros
                    if(position == CATEGORY_IMAGES.size()-1){
                        etOtros.setVisibility(View.VISIBLE);
                    }else{
                        etOtros.setVisibility(View.GONE);
                    }
                }
            }
        });
        ImageSelectorMargin imageSelectorMargin = new ImageSelectorMargin(3,IMAGE_SELECTOR_MARGIN);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 7 is the sum of items in one repeated section
                switch (position % 5) {
                    // first three items span 3 columns each
                    case 0:
                    case 1:
                    case 2:
                        return 2;
                    // next two items span 2 columns each
                    case 3:
                    case 4:
                        return 3;
                }
                throw new IllegalStateException("internal error");
            }
        });
        categorySelector.setLayoutManager(layoutManager);
        categorySelector.setAdapter(selectorAdapter);
        categorySelector.addItemDecoration(imageSelectorMargin);
    }

    public void backButton(View view){
        onBackPressed();
    }
}