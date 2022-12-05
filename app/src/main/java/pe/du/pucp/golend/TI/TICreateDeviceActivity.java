package pe.du.pucp.golend.TI;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pe.du.pucp.golend.Adapters.ImageSelectorAdapter;
import pe.du.pucp.golend.Adapters.ImageUploadAdapter;
import pe.du.pucp.golend.Decorations.ImageSelectorMargin;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.R;

public class TICreateDeviceActivity extends AppCompatActivity {
    final int IMAGE_SELECTOR_MARGIN = 8;
    final List<Integer> CATEGORY_IMAGES =  Arrays.asList(R.drawable.category_laptop_circle, R.drawable.category_celular_circle, R.drawable.category_tablet_circle, R.drawable.category_monitor_circle, R.drawable.category_otros_circle);
    final List<Integer> CATEGORY_TEXTS = Arrays.asList(R.string.category_laptop, R.string.category_celular, R.string.category_tablet, R.string.category_monitor, R.string.category_otros);


    RecyclerView categorySelector;
    RecyclerView rvFotos;
    GridLayout glFotos;
    
    EditText etMarca;
    EditText etModelo;
    EditText etDescripcion;
    EditText etAccesorios;
    EditText etStock;
    EditText etOtros;
    
    ProgressBar pbPhoto;
    ProgressBar pbLoading;

    private ImageButton btnBack;
    private ImageButton btnPhotoAttach;
    private ImageButton btnPhotoCam;
    private Button btnAnadir;
    boolean isBusy = false;

    private Uri cameraUri;
    private List<String> listFotos = new ArrayList<>();
    ImageUploadAdapter fotosAdapter;

    ActivityResultLauncher<Intent> launcherPhotoDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    compressImageAndUpload(uri,50);
                } else {
                    Toast.makeText(TICreateDeviceActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPhotoCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    compressImageAndUpload(cameraUri,25);
                } else {
                    Toast.makeText(TICreateDeviceActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_create_device);

        categorySelector = findViewById(R.id.rvCreateDeviceCategorySelector);
        rvFotos = findViewById(R.id.rvCreateDeviceFotos);
        glFotos = findViewById(R.id.glCreateDevice);
        etMarca = findViewById(R.id.etCreateDeviceMarca);
        etModelo = findViewById(R.id.etCreateDeviceModelo);
        etDescripcion = findViewById(R.id.etCreateDeviceDescripcion);
        etAccesorios = findViewById(R.id.etCreateDeviceAccesorios);
        etStock = findViewById(R.id.etCreateDeviceStock);
        etOtros = findViewById(R.id.etCreateDeviceOtrosCategoria);

        pbPhoto = findViewById(R.id.pbCreateDevicePhoto);
        pbLoading = findViewById(R.id.pbCreateDeviceLoading);

        btnBack = findViewById(R.id.ibCreateDeviceBack);
        btnPhotoAttach = findViewById(R.id.ibCreateDevicePhotoAttach);
        btnPhotoCam = findViewById(R.id.ibCreateDevicePhotoCam);
        btnAnadir = findViewById(R.id.btnCreateDeviceAnadir);

        //Setea adapters
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        fotosAdapter = new ImageUploadAdapter(this, listFotos);

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

        //Implementa adapters
        rvFotos.setAdapter(fotosAdapter);
        rvFotos.setLayoutManager(gridLayoutManager);
        categorySelector.setLayoutManager(layoutManager);
        categorySelector.setAdapter(selectorAdapter);
        categorySelector.addItemDecoration(imageSelectorMargin);
        evaluarEmpty();
    }

    public void anadirDispositivo(View view){
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(TICreateDeviceActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInvalid = false;
        int selectedCategory = ((ImageSelectorAdapter) categorySelector.getAdapter()).getSelectedItem();
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String accesorios = etAccesorios.getText().toString().trim();
        String strStock = etStock.getText().toString().trim();
        String searchCategoria = getResources().getString(CATEGORY_TEXTS.get(selectedCategory)); //Search categoria será el campo por el que se hará la búsqueda mientras que categoría podrá ser cualquier string
        String categoria;
        int stock = 1;
        String otrosCategoria = etOtros.getText().toString().trim();

        if(marca.isEmpty()){
            etMarca.setError("La marca no puede estar vacía");
            etMarca.requestFocus();
            isInvalid = true;
        }

        if(!marca.matches("[a-zA-Z0-9]*")){
            etMarca.setError("La marca solo puede contener números y letras");
            etMarca.requestFocus();
            isInvalid = true;
        }

        if(marca.length()>20){
            etMarca.setError("La marca puede contener hasta 20 caracteres");
            etMarca.requestFocus();
            isInvalid = true;
        }

        if(modelo.isEmpty()){
            etModelo.setError("El modelo no puede estar vacío");
            etModelo.requestFocus();
            isInvalid = true;
        }

        if(modelo.length()>30){
            etModelo.setError("El modelo puede contener hasta 30 caracteres");
            etModelo.requestFocus();
            isInvalid = true;
        }

        if(descripcion.isEmpty()){
            etDescripcion.setError("La descripción no puede estar vacía");
            etDescripcion.requestFocus();
            isInvalid = true;
        }

        if(descripcion.length()>255){
            etDescripcion.setError("La descripción puede contener hasta 255 caracteres");
            etDescripcion.requestFocus();
            isInvalid = true;
        }

        if(accesorios.isEmpty()){
            etAccesorios.setError("Accesorios no puede estar vacío");
            etAccesorios.requestFocus();
            isInvalid = true;
        }

        if(accesorios.length()>50){
            etAccesorios.setError("Accesorios puede contener hasta 50 caracteres");
            etAccesorios.requestFocus();
            isInvalid = true;
        }

        if(!strStock.matches("[0-9]+")){
            etStock.setError("Ingrese el número de equipos");
            etStock.requestFocus();
            isInvalid = true;
        } else {
            stock = Integer.parseInt(strStock);

            if (stock<=0) {
                etStock.setError("El stock debe ser mayor a 0");
                etStock.requestFocus();
                isInvalid = true;
            }
        }

        if(listFotos.size()<3 || listFotos.size()>6){
            rvFotos.requestFocus();
            Toast.makeText(TICreateDeviceActivity.this, "Se deben subir entre 3 a 6 fotos", Toast.LENGTH_SHORT).show();
            isInvalid = true;
        }

        if (selectedCategory == CATEGORY_TEXTS.size()-1){
            if(otrosCategoria.isEmpty()){
                etOtros.setError("La categoría no puede estar vacía");
                etOtros.requestFocus();
                isInvalid = true;
            }

            if(otrosCategoria.length()>20){
                etOtros.setError("La categoría puede contener hasta 20 caracteres");
                etOtros.requestFocus();
                isInvalid = true;
            }

            categoria = otrosCategoria;
        }else{
            categoria = searchCategoria;
        }

        if(isInvalid) return;

        mostrarCargando();

        crearDispositivoFirestore(new Device(modelo, marca, categoria, descripcion, accesorios, listFotos, stock, generateKeywords(marca + " " +modelo), searchCategoria));
    }

    public void compressImageAndUpload(Uri uri, int quality){
        try{
            Bitmap originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            originalImage.compress(Bitmap.CompressFormat.JPEG,quality,stream);
            subirImagenAFirebase(stream.toByteArray());
        }catch (Exception e){
            Log.d("msg","error",e);
        }
    }

    public void crearDispositivoFirestore(Device device){
        FirebaseFirestore.getInstance().collection("devices").add(device).addOnSuccessListener(unused -> {
            ocultarCargando();
            Toast.makeText(TICreateDeviceActivity.this, "Se añadió el dispositivo "+device.getModelo(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }).addOnFailureListener(e->{
            ocultarCargando();
            Log.d("msg",e.getMessage());
            Toast.makeText(TICreateDeviceActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
        });
    }

    public void uploadPhotoFromDocument(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(TICreateDeviceActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            launcherPhotoDocument.launch(intent);
        }
    }

    public void uploadPhotoFromCamera(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(TICreateDeviceActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            launcherPhotoCamera.launch(cameraIntent);
        }
    }

    public void backButton(View view){
        onBackPressed();
    }

    public void subirImagenAFirebase(byte[] imageBytes) {
        StorageReference photoChild = FirebaseStorage.getInstance().getReference().child("devicephotos/" + "photo_" + Timestamp.now().getSeconds() + ".jpg");
        pbPhoto.setVisibility(View.VISIBLE);
        photoChild.putBytes(imageBytes).addOnSuccessListener(taskSnapshot -> {
            pbPhoto.setVisibility(View.GONE);
            photoChild.getDownloadUrl().addOnSuccessListener(uri -> {
                listFotos.add(uri.toString());
                fotosAdapter.notifyDataSetChanged();
                evaluarEmpty();
            }).addOnFailureListener(e ->{
                Log.d("msg-test", "error",e);
                Toast.makeText(TICreateDeviceActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.d("msg-test", "error",e);
            pbPhoto.setVisibility(View.GONE);
            Toast.makeText(TICreateDeviceActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                long bytesTransferred = snapshot.getBytesTransferred();
                long totalByteCount = snapshot.getTotalByteCount();
                double progreso = (100.0 * bytesTransferred) / totalByteCount;
                Long round = Math.round(progreso);
                pbPhoto.setProgress(round.intValue());
            }
        });
    }

    private List<String> generateKeywords(String inputString){
        inputString = inputString.toLowerCase();
        List<String> keywords = new ArrayList<>();

        List<String> words = Arrays.asList(inputString.split(" "));
        for (String word : words){
            String appendString = "";

            for (char c : inputString.toCharArray()){
                appendString+=c;
                keywords.add(appendString);
            }

            inputString = inputString.replace(word+" ","");
        }

        return keywords;
    }

    public void removerFoto(int position){
        listFotos.remove(position);
        fotosAdapter.notifyDataSetChanged();
        evaluarEmpty();
    }

    public void evaluarEmpty(){
        if (listFotos.size()>0){
            rvFotos.setVisibility(View.VISIBLE);
            glFotos.setVisibility(View.GONE);
        }else{
            rvFotos.setVisibility(View.GONE);
            glFotos.setVisibility(View.VISIBLE);
        }
    }

    public void mostrarCargando(){
        isBusy = true;
        pbLoading.setVisibility(View.VISIBLE);
        btnPhotoAttach.setClickable(false);
        btnPhotoCam.setClickable(false);
        btnBack.setClickable(false);
        btnAnadir.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        pbLoading.setVisibility(View.GONE);
        btnPhotoAttach.setClickable(true);
        btnPhotoCam.setClickable(true);
        btnBack.setClickable(true);
        btnAnadir.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if (!isBusy){
            super.onBackPressed();
        }
    }
}