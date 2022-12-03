package pe.du.pucp.golend.Cliente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.RatingCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;

public class ClienteFormActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Device device;
    EditText etMotivo;
    EditText etCurso;
    EditText etTiempoReserva;
    EditText etProgramas;
    EditText etOtros;
    ShapeableImageView ivDni;
    ChipGroup cgProgramas;

    private Uri cameraUri;
    private ArrayList<String> listProgramas = new ArrayList<>();
    private ImageButton btnBack;
    private ImageButton btnPhotoAttach;
    private ImageButton btnPhotoCam;
    private Button btnReservar;
    ProgressBar pbPhoto;
    ProgressBar pbLoading;
    FirebaseUser user;
    boolean isBusy = false;
    User userG;
    String fotoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_form);

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userG = gson.fromJson(sharedPreferences.getString("user",""), User.class);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        device = (Device) intent.getSerializableExtra("dispositivo");
        user = FirebaseAuth.getInstance().getCurrentUser();

        ivDni = findViewById(R.id.ivClienteFormDNI);
        etMotivo = findViewById(R.id.etClienteFormMotivo);
        etCurso = findViewById(R.id.etClienteFormCurso);
        etTiempoReserva = findViewById(R.id.etClienteFormTiempo);
        etProgramas = findViewById(R.id.etClienteFormProgramas);
        etOtros = findViewById(R.id.etClienteFormOtros);
        pbPhoto = findViewById(R.id.pbClienteDNIPhoto);
        pbLoading = findViewById(R.id.pbClienteFormLoading);
        btnBack = findViewById(R.id.ibClienteFormBack);
        btnPhotoAttach = findViewById(R.id.ibClienteFormPhotoAttach);
        btnPhotoCam = findViewById(R.id.ibClienteFormPhotoCam);
        btnReservar = findViewById(R.id.btnClienteFormEnviarSoli);
        cgProgramas = findViewById(R.id.cgClienteFormProgramas);
        etProgramas.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    Chip chip = new Chip(ClienteFormActivity.this);
                    String programa = etProgramas.getText().toString().trim();
                    if(!listProgramas.contains(programa.toLowerCase(Locale.ROOT))){
                        ChipDrawable drawable = ChipDrawable.createFromAttributes(ClienteFormActivity.this,null,0, com.denzcoskun.imageslider.R.style.Widget_MaterialComponents_Chip_Entry);
                        chip.setChipDrawable(drawable);
                        chip.setCheckable(false);
                        chip.setClickable(false);
                        chip.setPadding(60,10,60,10);
                        chip.setText(programa);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cgProgramas.removeView(chip);
                                listProgramas.remove(chip.getText().toString());
                            }
                        });
                        cgProgramas.addView(chip);
                        listProgramas.add(programa);
                        etProgramas.setText("");
                        return true;
                    }else{
                        etProgramas.setError("El programa ya se ha añadido");
                        etProgramas.clearFocus();
                    }
                }
                return false;
            }
        });

    }

    public void realizarSolicitud(View view){

        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(ClienteFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInvalid = false;
        String motivo = etMotivo.getText().toString().trim();
        String curso = etCurso.getText().toString().trim();
        String tiempoReservaStr= etTiempoReserva.getText().toString().trim();
        Integer tiempoReserva;
        try
        {
           tiempoReserva = Integer.parseInt(tiempoReservaStr);
        }
        catch (NumberFormatException e)
        {
            tiempoReserva=0;
        }

        String dni = "";
        String otros = etOtros.getText().toString().trim();

        if(motivo.isEmpty()){
            etMotivo.setError("El motivo no puede estar vacío");
            etMotivo.requestFocus();
            isInvalid = true;
        }

        if(motivo.length()>255){
            etMotivo.setError("El motivo puede contener hasta 255 caracteres");
            etMotivo.requestFocus();
            isInvalid = true;
        }

        if(curso.isEmpty()){
            etCurso.setError("El curso no puede estar vacío");
            etCurso.requestFocus();
            isInvalid = true;
        }

        if(curso.length()>20){
            etCurso.setError("El curso puede contener hasta 20 caracteres");
            etCurso.requestFocus();
            isInvalid = true;
        }


        if(tiempoReservaStr.isEmpty()){
            etTiempoReserva.setError("El tiempo de reserva no puede estar vacío");
            etTiempoReserva.requestFocus();
            isInvalid = true;
        }

        if(tiempoReserva>30){
            etTiempoReserva.setError("El tiempo de reserva no puede mayor a 30 días");
            etTiempoReserva.requestFocus();
            isInvalid = true;
        }

        if(listProgramas.isEmpty()){
            etTiempoReserva.setError("El campo programas no puede estar vacío");
            etTiempoReserva.requestFocus();
            isInvalid = true;
        }

        if(listProgramas.size()>6){
            etTiempoReserva.setError("Los programas no pueden ser más de 6");
            etTiempoReserva.requestFocus();
            isInvalid = true;
        }


        if(isInvalid) return;

        mostrarCargando();

        crearSolicitudFirestore(new Reservas(new Reservas.ClienteUser(userG.getNombre(), user.getUid(), user.getPhotoUrl().toString(), userG.getRol()), new Reservas.TIUser(), new Reservas.Device(device.getModelo(),device.getMarca(), device.getFotosUrl().get(0),device.getCategoria()), motivo,curso, tiempoReserva, listProgramas, dni, otros,"","","Pendiente de aprobación",Timestamp.now(),null,null));
    }

    ActivityResultLauncher<Intent> launcherPhotoDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    compressImageAndUpload(uri,50);
                } else {
                    Toast.makeText(ClienteFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPhotoCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    compressImageAndUpload(cameraUri,25);
                } else {
                    Toast.makeText(ClienteFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

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

    public void crearSolicitudFirestore(Reservas reservas){
        FirebaseFirestore.getInstance().collection("reservas").add(reservas).addOnSuccessListener(unused -> {
            ocultarCargando();
            Toast.makeText(ClienteFormActivity.this, "Se realizó la solicitud con éxito", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e->{
            ocultarCargando();
            Log.d("msg",e.getMessage());
            Toast.makeText(ClienteFormActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
        });
    }


    public void uploadPhotoFromDocument(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(ClienteFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            launcherPhotoDocument.launch(intent);
        }
    }

    public void uploadPhotoFromCamera(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(ClienteFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
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
        StorageReference photoChild = FirebaseStorage.getInstance().getReference().child("dniphotos/" + "photo_" + Timestamp.now().getSeconds() + ".jpg");
        pbPhoto.setVisibility(View.VISIBLE);
        photoChild.putBytes(imageBytes).addOnSuccessListener(taskSnapshot -> {
            pbPhoto.setVisibility(View.GONE);
            photoChild.getDownloadUrl().addOnSuccessListener(uri -> {
                fotoUrl = uri.toString();
                Glide.with(ClienteFormActivity.this).load(fotoUrl).into(ivDni);
                ivDni.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }).addOnFailureListener(e ->{
                Log.d("msg-test", "error",e);
                Toast.makeText(ClienteFormActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.d("msg-test", "error",e);
            pbPhoto.setVisibility(View.GONE);
            Toast.makeText(ClienteFormActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
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

    public void mostrarCargando(){
        isBusy = true;
        pbLoading.setVisibility(View.VISIBLE);
        btnPhotoAttach.setClickable(false);
        btnPhotoCam.setClickable(false);
        btnBack.setClickable(false);
        btnReservar.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        pbLoading.setVisibility(View.GONE);
        btnPhotoAttach.setClickable(true);
        btnPhotoCam.setClickable(true);
        btnBack.setClickable(true);
        btnReservar.setClickable(true);
    }

}