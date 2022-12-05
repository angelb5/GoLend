package pe.du.pucp.golend.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pe.du.pucp.golend.Entity.User;
import pe.du.pucp.golend.R;
import pe.du.pucp.golend.TI.TIProfileActivity;

public class AdminUpdateUserTiActivity extends AppCompatActivity {

    boolean isBusy;
    User userTi;
    EditText etNombre;
    EditText etCorreo;
    EditText etCodigo;
    ImageView ivPfp;
    CollectionReference usersRef;
    Button btnActualizar;
    Button btnEliminar;
    ImageButton btnBack;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_user_ti);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("userTi")){
            finish();
            return;
        }
        userTi = (User) intent.getSerializableExtra("userTi");

        ivPfp = findViewById(R.id.ivUpdateTiPfp);
        etNombre = findViewById(R.id.etUpdateUserTiNombre);
        etCodigo = findViewById(R.id.etUpdateUserTiCodigo);
        etCorreo = findViewById(R.id.etUpdateUserTiCorreo);
        btnActualizar = findViewById(R.id.btnUpdateUserTiActualizar);
        btnEliminar = findViewById(R.id.btnUpdateUserTiEliminar);
        btnBack = findViewById(R.id.ibUpdateUserTiBack);
        progressBar = findViewById(R.id.pbUpdateUserTi);

        usersRef = FirebaseFirestore.getInstance().collection("users");
        Glide.with(this).load(userTi.getAvatarUrl()).into(ivPfp);
        etNombre.setText(userTi.getNombre());
        etCorreo.setText(userTi.getCorreo());
        etCodigo.setText(userTi.getCodigo());
    }

    public void actualizarTi(View view) {
        boolean isInvalid = false;
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String codigo = etCodigo.getText().toString().trim();
        if(nombre.isEmpty()){
            etNombre.setError("No puede estar vacío");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(nombre.length()>30){
            etNombre.setError("No puede tener más de 30 caracteres");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches() || !(correo.endsWith("pucp.edu.pe") || correo.endsWith("pucp.pe"))){
            etCorreo.setError("Ingrese un correo válido");
            etCorreo.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.PHONE.matcher(codigo).matches() || codigo.length()!=8){
            etCodigo.setError("Ingrese un código válido");
            etCodigo.requestFocus();
            isInvalid = true;
        }

        if (isInvalid) return;

        Map<String, Object> updates = new HashMap<>();
        if (!userTi.getNombre().equals(nombre)) {
            updates.put("nombre", nombre);
        }

        if (!userTi.getCodigo().equals(codigo)) {
            updates.put("codigo", codigo);
        }

        if (!userTi.getCorreo().equals(correo)) {
            updates.put("correo", correo);
        }

        if (updates.isEmpty()) return;
        Log.d("msg", "not empty");
        //Verifica que el codigo y correo sean únicos
        mostrarCargando();
        if (!userTi.getCodigo().equals(codigo)) {
            usersRef.whereEqualTo("codigo",codigo).count().get(AggregateSource.SERVER).addOnSuccessListener(aggregateQuerySnapshot -> {
                if (aggregateQuerySnapshot.getCount()>0){
                    ocultarCargando();
                    etCodigo.setError("Ya existe una cuenta con este código");
                    etCodigo.requestFocus();
                    return;
                }
                actualizarUserFirebase(updates);
            }).addOnFailureListener(e -> ocultarCargando());
        } else {
            actualizarUserFirebase(updates);
        }

    }

    public void actualizarUserFirebase(Map<String, Object> updates) {
        usersRef.document(userTi.getUid()).update(updates).addOnSuccessListener(unused -> {
            ocultarCargando();
            Toast.makeText(AdminUpdateUserTiActivity.this, "Se ha actualizado el usuario", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(AdminUpdateUserTiActivity.this, "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
        });
    }

    public void showAlert(View view){
        MaterialAlertDialogBuilder alertEliminar = new MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme_Center);
        alertEliminar.setIcon(R.drawable.ic_user_remove);
        alertEliminar.setTitle("Eliminar usuario");
        alertEliminar.setMessage("¿Está seguro que desea eliminar este usuario TI? Esta acción es irreversible");
        alertEliminar.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                usersRef.document(userTi.getUid()).delete().addOnSuccessListener( unused -> {
                    Toast.makeText(AdminUpdateUserTiActivity.this, "Se ha eliminado a "+userTi.getNombre() , Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Log.d("msg", "error", e);
                    Toast.makeText(AdminUpdateUserTiActivity.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                });
                Log.d("msgAlert","ELIMINAR");
            }

        });
        alertEliminar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("msgAlert","CANCELAR");
            }
        });
        alertEliminar.show();
    }

    public void backButton(View view) {
        onBackPressed();
    }

    public void mostrarCargando(){
        isBusy = true;
        progressBar.setVisibility(View.VISIBLE);
        btnActualizar.setClickable(false);
        btnEliminar.setClickable(false);
        btnBack.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        progressBar.setVisibility(View.GONE);
        btnActualizar.setClickable(true);
        btnEliminar.setClickable(true);
        btnBack.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if (!isBusy){
            super.onBackPressed();
        }
    }
}