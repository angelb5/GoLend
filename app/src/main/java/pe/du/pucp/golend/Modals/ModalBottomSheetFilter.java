package pe.du.pucp.golend.Modals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;
import java.util.List;

import pe.du.pucp.golend.R;
import pe.du.pucp.golend.TI.TIDevicesActivity;

public class ModalBottomSheetFilter extends BottomSheetDialogFragment {
    public String TAG = "ModalBottomSheet";
    public String categoryFilter = "";
    public List<String> marcasList = Arrays.asList("Todas las marcas");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.modal_bottom_sheet_filter, container, false);

        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, marcasList);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spMarcas = view.findViewById(R.id.spModalBottomSheetMarcas);
        ChipGroup chipGroup = view.findViewById(R.id.cgModalBottomSheet);
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                for (int i = 0; i<chipGroup.getChildCount(); i++){
                    Chip c = (Chip) group.getChildAt(i);
                    if (c.isChecked()){
                        c.setTextAppearanceResource(R.style.chipTextSelected);
                        c.setTextColor(view.getContext().getColor(R.color.green_main));
                        c.setChipStrokeColorResource(R.color.green_main);
                        c.setChipStrokeWidthResource(R.dimen.dp1);
                        categoryFilter = c.getText().toString().equals("Todas las categorías")?"":c.getText().toString();
                    }else{
                        c.setTextAppearanceResource(R.style.chipTextUnselected);
                        c.setTextColor(view.getContext().getColor(R.color.white));
                        c.setChipStrokeColorResource(R.color.grey_25);
                        c.setChipStrokeWidthResource(R.dimen.dp05);
                    }
                }
            }
        });
        view.findViewById(R.id.ibModalBottomSheetClose).setOnClickListener(view1 -> {
            dismiss();
        });
        view.findViewById(R.id.btnModalBottomSheetApply).setOnClickListener(view1 -> {
            String marcasFilter = spMarcas.getSelectedItem().toString().equals("Todas las marcas")?"":spMarcas.getSelectedItem().toString();

            if (getActivity() instanceof TIDevicesActivity){
                Log.d("msg", marcasFilter +" modal");
                ((TIDevicesActivity) getActivity()).setFilters(categoryFilter, marcasFilter);
            }
            dismiss();
        });
        spMarcas.setAdapter(spAdapter);
        return view;
    }

    public void setMarcasList(List<String> marcasList) {
        this.marcasList = marcasList;
    }
}
