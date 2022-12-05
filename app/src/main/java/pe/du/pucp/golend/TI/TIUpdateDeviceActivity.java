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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Locale;

import pe.du.pucp.golend.Adapters.ImageSelectorAdapter;
import pe.du.pucp.golend.Adapters.ImageUploadAdapter;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.R;

public class TIUpdateDeviceActivity extends AppCompatActivity {

    Device device;

    RecyclerView rvFotos;
    GridLayout glFotos;
    ImageView ivCategoria;
    TextView tvCategoria;
    TextView tvEnPrestamo;
    EditText etMarca;
    EditText etModelo;
    EditText etDescripcion;
    EditText etAccesorios;
    EditText etStock;

    ProgressBar pbPhoto;
    ProgressBar pbLoading;

    private ImageButton btnBack;
    private ImageButton btnPhotoAttach;
    private ImageButton btnPhotoCam;
    private Button btnAnadir;
    boolean isBusy = false;

    private Uri cameraUri;
    private List<String> listFotos;
    ImageUploadAdapter fotosAdapter;

    ActivityResultLauncher<Intent> launcherPhotoDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    compressImageAndUpload(uri,50);
                } else {
                    Toast.makeText(TIUpdateDeviceActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPhotoCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    compressImageAndUpload(cameraUri,25);
                } else {
                    Toast.makeText(TIUpdateDeviceActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ti_update_device);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }
        device = (Device) intent.getSerializableExtra("dispositivo");

        rvFotos = findViewById(R.id.rvUpdateDeviceFotos);
        glFotos = findViewById(R.id.glUpdateDevice);
        tvCategoria = findViewById(R.id.tvUpdateDeviceCategory);
        ivCategoria = findViewById(R.id.ivUpdateDeviceCategory);
        tvEnPrestamo = findViewById(R.id.tvUpdateDeviceEnPrestamo);
        etMarca = findViewById(R.id.etUpdateDeviceMarca);
        etModelo = findViewById(R.id.etUpdateDeviceModelo);
        etDescripcion = findViewById(R.id.etUpdateDeviceDescripcion);
        etAccesorios = findViewById(R.id.etUpdateDeviceAccesorios);
        etStock = findViewById(R.id.etUpdateDeviceStock);
        pbPhoto = findViewById(R.id.pbUpdateDevicePhoto);
        pbLoading = findViewById(R.id.pbUpdateDeviceLoading);

        btnBack = findViewById(R.id.ibUpdateDeviceBack);
        btnPhotoAttach = findViewById(R.id.ibUpdateDevicePhotoAttach);
        btnPhotoCam = findViewById(R.id.ibUpdateDevicePhotoCam);
        btnAnadir = findViewById(R.id.btnUpdateDeviceActualizar);

        listFotos = device.getFotosUrl();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        fotosAdapter = new ImageUploadAdapter(this, listFotos);

        //Implementa adapters
        rvFotos.setAdapter(fotosAdapter);
        rvFotos.setLayoutManager(gridLayoutManager);
        evaluarEmpty();

        tvCategoria.setText(device.getCategoria());
        switch (device.getSearchCategoria()){
            case "Laptop":
                ivCategoria.setImageResource(R.drawable.category_laptop_circle);
                break;
            case "Celular":
                ivCategoria.setImageResource(R.drawable.category_celular_circle);
                break;
            case "Monitor":
                ivCategoria.setImageResource(R.drawable.category_monitor_circle);
                break;
            case "Tablet":
                ivCategoria.setImageResource(R.drawable.category_tablet_circle);
                break;
            case "Otros":
                ivCategoria.setImageResource(R.drawable.category_otros_circle);
        }

        etMarca.setText(device.getMarca());
        etModelo.setText(device.getModelo());
        etDescripcion.setText(device.getDescripcion());
        etAccesorios.setText(device.getAccesorios());
        etStock.setText(String.valueOf(device.getStock()));
        tvEnPrestamo.setText(device.getEnPrestamo()+" en préstamo");
    }

    public void actualizarDispositivo(View view){
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(TIUpdateDeviceActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInvalid = false;
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String accesorios = etAccesorios.getText().toString().trim();
        String strStock = etStock.getText().toString().trim();
        int stock = device.getStock();

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
            if(stock<device.getEnPrestamo()) {
                etStock.setError("No se puede ingresar un número menor a los dispositivos en préstamo");
                etStock.requestFocus();
                isInvalid = true;
            }

            if (stock<=0) {
                etStock.setError("El stock debe ser mayor a 0");
                etStock.requestFocus();
                isInvalid = true;
            }
        }

        if(listFotos.size()<3 || listFotos.size()>6){
            rvFotos.requestFocus();
            Toast.makeText(TIUpdateDeviceActivity.this, "Se deben subir entre 3 a 6 fotos", Toast.LENGTH_SHORT).show();
            isInvalid = true;
        }

        if(isInvalid) return;

        mostrarCargando();

        device.setMarca(marca);
        device.setSearchMarca(marca.toLowerCase(Locale.ROOT));
        device.setModelo(modelo);
        device.setDescripcion(descripcion);
        device.setAccesorios(accesorios);
        device.setStock(stock);
        device.setDisponibles(stock-device.getEnPrestamo());
        device.setSearchKeywords(generateKeywords(marca + " " +modelo));
        FirebaseFirestore.getInstance().collection("devices").document(device.getKey()).set(device).addOnSuccessListener(unused -> {
            ocultarCargando();
            Intent intent = new Intent();
            intent.putExtra("dispositivo", device);
            setResult(RESULT_OK, intent);
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(TIUpdateDeviceActivity.this, "No se pudo actualizar el dispositivo", Toast.LENGTH_SHORT).show();
        });
    }

    public void uploadPhotoFromDocument(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(TIUpdateDeviceActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            launcherPhotoDocument.launch(intent);
        }
    }

    public void uploadPhotoFromCamera(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(TIUpdateDeviceActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TIUpdateDeviceActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.d("msg-test", "error",e);
            pbPhoto.setVisibility(View.GONE);
            Toast.makeText(TIUpdateDeviceActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
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
}