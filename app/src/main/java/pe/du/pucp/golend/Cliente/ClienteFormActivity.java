package pe.du.pucp.golend.Cliente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pe.du.pucp.golend.Anonymus.ForgotPasswordActivity;
import pe.du.pucp.golend.Anonymus.LoginActivity;
import pe.du.pucp.golend.Entity.Device;
import pe.du.pucp.golend.Entity.Reservas;
import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.Interfaces.BlurApi;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.ScreenMessage;
import pe.du.pucp.golend.ScreenMessageActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

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
    TextView tvPhoto;
    ProgressBar pbLoading;
    FirebaseUser user;
    boolean isBusy = false;
    User userG;
    String fotoUrl = "";


    ActivityResultLauncher<Intent> launcherPhotoDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    compressImageAndUpload(uri);
                } else {
                    Toast.makeText(ClienteFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPhotoCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    compressImageAndUpload(cameraUri);
                } else {
                    Toast.makeText(ClienteFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

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
        tvPhoto = findViewById(R.id.tvClienteDNIPhoto);
        pbLoading = findViewById(R.id.pbClienteFormLoading);
        btnBack = findViewById(R.id.ibClienteFormBack);
        btnPhotoAttach = findViewById(R.id.ibClienteFormPhotoAttach);
        btnPhotoCam = findViewById(R.id.ibClienteFormPhotoCam);
        btnReservar = findViewById(R.id.btnClienteFormEnviarSoli);
        cgProgramas = findViewById(R.id.cgClienteFormProgramas);

        pbPhoto.setIndeterminate(true);
        etProgramas.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    Chip chip = new Chip(ClienteFormActivity.this);
                    String programa = etProgramas.getText().toString().trim();
                    if (programa.isEmpty()) return false;

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
                        etProgramas.setError("El programa ya se ha a??adido");
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

        String dni = fotoUrl;
        String otros = etOtros.getText().toString().trim();

        if(motivo.isEmpty()){
            etMotivo.setError("El motivo no puede estar vac??o");
            etMotivo.requestFocus();
            isInvalid = true;
        }

        if(motivo.length()>255){
            etMotivo.setError("El motivo puede contener hasta 255 caracteres");
            etMotivo.requestFocus();
            isInvalid = true;
        }

        if(curso.isEmpty()){
            etCurso.setError("El curso no puede estar vac??o");
            etCurso.requestFocus();
            isInvalid = true;
        }

        if(curso.length()>20){
            etCurso.setError("El curso puede contener hasta 20 caracteres");
            etCurso.requestFocus();
            isInvalid = true;
        }


        if(tiempoReservaStr.isEmpty()){
            etTiempoReserva.setError("El tiempo de reserva no puede estar vac??o");
            etTiempoReserva.requestFocus();
            isInvalid = true;
        }

        if(tiempoReserva>30){
            etTiempoReserva.setError("El tiempo de reserva no puede mayor a 30 d??as");
            etTiempoReserva.requestFocus();
            isInvalid = true;
        }

        if(listProgramas.isEmpty()){
            etProgramas.setError("El campo programas no puede estar vac??o");
            etProgramas.requestFocus();
            isInvalid = true;
        }

        if(listProgramas.size()>6){
            etTiempoReserva.setError("Los programas no pueden ser m??s de 6");
            etTiempoReserva.requestFocus();
            isInvalid = true;
        }

        if(fotoUrl.isEmpty()){
            isInvalid = true;
            ivDni.requestFocus();
            Toast.makeText(this, "No has subido foto de tu dni", Toast.LENGTH_SHORT).show();
        }


        if(isInvalid) return;

        mostrarCargando();

        crearSolicitudFirestore(new Reservas(new Reservas.ClienteUser(userG.getNombre(), user.getUid(), userG.getAvatarUrl(), userG.getRol()), new Reservas.TIUser(), new Reservas.Device(device.getModelo(),device.getMarca(), device.getFotosUrl().get(0),device.getCategoria(), device.getKey()), motivo,curso, tiempoReserva, listProgramas, dni, otros,null,"","","Pendiente de aprobaci??n",Timestamp.now(),null,null));
    }

    public void compressImageAndUpload(Uri uri){
        ivDni.setStrokeWidth(0);
        Glide.with(this).load(uri).into(ivDni);
        ivDni.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cargandoDNI();
        try{
            Bitmap originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            originalImage.compress(Bitmap.CompressFormat.JPEG,20,stream);
            subirImagenAFirebase(stream.toByteArray());
        }catch (Exception e){
            terminarCargandoDNI();
            Log.d("msg","error",e);
        }
    }

    public void crearSolicitudFirestore(Reservas reservas){
        FirebaseFirestore.getInstance().collection("reservas").add(reservas).addOnSuccessListener(unused -> {
            ocultarCargando();
            ScreenMessage screenMessage = new ScreenMessage(R.drawable.circle_tick, R.drawable.screenmessage_fingers_crossed,
                    "Tu solicitud ha sido enviada",
                    "Nuestro equipo la est?? revisando, pronto obtendr??s una respuesta",
                    "Ver mis solicitudes",
                    false, ClienteSolicitudActivity.class);
            Intent successIntent = new Intent(ClienteFormActivity.this, ScreenMessageActivity.class);
            successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            successIntent.putExtra("screenMessage", screenMessage);
            startActivity(successIntent);
            ActivityCompat.finishAffinity(ClienteFormActivity.this);;
            finish();
        }).addOnFailureListener(e->{
            ocultarCargando();
            Log.d("msg",e.getMessage());
            Toast.makeText(ClienteFormActivity.this, "Ocurri?? un error en el servidor", Toast.LENGTH_LONG).show();
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
        blurFaces(imageBytes);
    }

    public void blurFaces(byte[] bytes) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.faceblurapi.com/v1/")
                .build();
        // create api instance
        BlurApi api = retrofit.create(BlurApi.class);
        // create call object
        Call<ResponseBody> uploadFileCall = api.uploadFile("b67f6d249f4d49769cbb31974b33d858",
                MultipartBody.Part.createFormData("image", "DNI.jpg", reqFile));
        // async call
        uploadFileCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                terminarCargandoDNI();
                if (!response.isSuccessful() || response.body() == null) {
                    terminarCargandoDNI();
                    fotoUrl = "";
                    Toast.makeText(ClienteFormActivity.this, "Ocurri?? un error al subir la imagen", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("msg", response.raw().toString());
                try {
                    String body = response.body().string();
                    Log.d("msg", body);
                    JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                    String imageUrl = jsonObject.get("image").getAsString();
                    Log.d("msg", imageUrl);
                    if (imageUrl.isEmpty()) {
                        terminarCargandoDNI();
                        fotoUrl = "";
                        Toast.makeText(ClienteFormActivity.this, "Ocurri?? un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fotoUrl = imageUrl;
                    Glide.with(ClienteFormActivity.this).load(imageUrl).timeout(10000).error(R.drawable.not_available).into(ivDni);
                } catch (IOException e) {
                    terminarCargandoDNI();
                    fotoUrl = "";
                    Toast.makeText(ClienteFormActivity.this, "Ocurri?? un error al subir la imagen", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("msg", response.code()+"");
                Log.d("msg", String.valueOf(response.isSuccessful()));

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                terminarCargandoDNI();
                Log.d("msg", "error", t);
                Toast.makeText(ClienteFormActivity.this, "Ocurri?? un error al subir la imagen", Toast.LENGTH_SHORT).show();
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

    public void cargandoDNI() {
        pbPhoto.setVisibility(View.VISIBLE);
        tvPhoto.setVisibility(View.VISIBLE);
    }

    public void terminarCargandoDNI() {
        pbPhoto.setVisibility(View.GONE);
        tvPhoto.setVisibility(View.GONE);
    }

}